import org.apache.http.client.methods.*;

/**
 * @description 请求类型
 * @author  Jo
 * @date  2022/08/24
 * @version
 */
public enum RequestType {

	POST{
		@Override
		public HttpRequestBase getHttpType(String url) {
			return new HttpPost(url);
		}
	},
	GET{
		@Override
		public HttpRequestBase getHttpType(String url) {
			return new HttpGet(url);
		}
	},
	DELETE{
		@Override
		public HttpRequestBase getHttpType(String url) {
			return new HttpDelete(url);
		}
	},
	PUT{
		@Override
		public HttpRequestBase getHttpType(String url) {
			return new HttpPut(url);
		}
	},
	
	;
	
   public abstract HttpRequestBase getHttpType(String url);
}
