import com.alibaba.fastjson.JSONObject;
import com.jo.common.core.exception.HttpCollectException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author xtc
 * @date 2023/9/26
 */
@Slf4j
public class HttpClient {

    private static PoolingHttpClientConnectionManager connMgr; //连接池
    private static HttpRequestRetryHandler retryHandler; //重试机制
    private static final int MAX_TIMEOUT = 3000; //超时时间
    private static final int MAX_TOTAL=10; //最大连接数
    private static final int ROUTE_MAX_TOTAL=3; //每个路由基础的连接数
    private static final  int MAX_RETRY = 5; //重试次数
    static {
        cfgPoolMgr();
        cfgRetryHandler();
    }

    public static <T> T  sendHttp(RequestType reqType, String url, Map<String, String> headers,Class<T> clazz, Object param){
        //添加参数 参数是json字符串
        HttpRequestBase reqBase;

        reqBase=reqType.getHttpType(url);

        log.info("请求参数\n{}", param+"\n");
        log.info("请求地址\n:{}\n请求方式\n:{}",reqBase.getURI(),reqType+"\n");

        if(param instanceof String) {
            ((HttpEntityEnclosingRequest) reqBase).setEntity(
                    new StringEntity(String.valueOf(param), ContentType.create("application/json", "UTF-8")));
        }
        //参数是字节流数组
        else if(param instanceof byte[]) {
            reqBase=reqType.getHttpType(url);
            byte[] paramBytes = (byte[])param;
            ((HttpEntityEnclosingRequest) reqBase).setEntity(new ByteArrayEntity(paramBytes));
        }
        CloseableHttpClient httpClient = getHttpClient();
        //设置请求url
        config(reqBase);

        //设置请求头
        if(!CollectionUtils.isEmpty(headers)) {
            for(Map.Entry<String, String> entry :headers.entrySet()) {
                reqBase.setHeader(entry.getKey(), entry.getValue());
            }
        }

        //响应对象
        CloseableHttpResponse res = null;
        //响应内容
        String resCtx = null;
        try {
            //执行请求
            res = httpClient.execute(reqBase);

            //获取请求响应对象和响应entity
            HttpEntity httpEntity = res.getEntity();
            if(httpEntity != null) {
                resCtx = EntityUtils.toString(httpEntity,"utf-8");
            }

            log.info("响应\n{}", resCtx + "\n");
            return JSONObject.parseObject(resCtx,clazz);
        } catch (NoHttpResponseException e) {
            throw new HttpCollectException("服务器丢失");
        } catch (SSLHandshakeException e){
            throw new HttpCollectException("SSL握手异常");
        } catch (UnknownHostException e){
            throw new HttpCollectException("服务器找不到");
        } catch(ConnectTimeoutException e){
            throw new HttpCollectException("链接超时");
        } catch(SSLException e){
            throw new HttpCollectException("SSL异常");
        } catch (ClientProtocolException e) {
            throw new HttpCollectException("请求头异常");
        } catch (IOException e) {
            throw new HttpCollectException("网络请求失败");
        } finally {
            if(res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                    throw new HttpCollectException("关闭请求响应失败");
                }
            }
        }
    }
    private static void cfgPoolMgr() {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();

        //连接池管理器
        connMgr = new PoolingHttpClientConnectionManager(registry);
        //最大连接数
        connMgr.setMaxTotal(MAX_TOTAL);
        //每个路由基础的连接数
        connMgr.setDefaultMaxPerRoute(ROUTE_MAX_TOTAL);
    }
    private static CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(connMgr)
                .setRetryHandler(retryHandler)
                .build();
    }

    private static void cfgRetryHandler() {
        retryHandler = (e, excCount, ctx) -> {
            //超过最大重试次数，就放弃
            if(excCount > MAX_RETRY) {
                return false;
            }
            //服务器丢掉了链接，就重试
            if(e instanceof NoHttpResponseException) {
                return true;
            }
            //不重试SSL握手异常
            if(e instanceof SSLHandshakeException) {
                return false;
            }
            //中断
            if(e instanceof InterruptedIOException) {
                return false;
            }
            //目标服务器不可达
            if(e instanceof UnknownHostException) {
                return false;
            }
            //连接超时
            if(e instanceof ConnectTimeoutException) {
                return false;
            }
            //SSL异常
            if(e instanceof SSLException) {
                return false;
            }

            HttpClientContext clientCtx = HttpClientContext.adapt(ctx);
            HttpRequest req = clientCtx.getRequest();
            //如果是幂等请求，就再次尝试
            return !(req instanceof HttpEntityEnclosingRequest);
        };
    }
    private static void config(HttpRequestBase httpReqBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(MAX_TIMEOUT)
                .setConnectTimeout(MAX_TIMEOUT)
                .setSocketTimeout(MAX_TIMEOUT)
                .build();
        httpReqBase.setConfig(requestConfig);
    }

}
