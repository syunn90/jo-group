package com.jo.support.support.registry;

import com.jo.rpc.comm.annotation.RpcClient;
import com.jo.rpc.comm.annotation.RpcRoute;
import com.jo.rpc.reflect.RouterFactory;
import com.jo.support.support.annotation.EnableRpc;
import com.jo.support.support.factory.RpcClientFactoryBean;
import com.jo.support.support.processor.RpcPostProcessor;
import com.jo.support.support.scanner.RpcClientScanner;
import com.jo.support.support.scanner.RpcRouteScanner;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

import java.beans.Introspector;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jo
 * @date 2024/7/10
 */
public class RpcComponentRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    private static final String BASE_PACKAGES = "basePackages";

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Set<String> basePackages = new HashSet<>();

        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName());

        if (attributes != null && attributes.get(BASE_PACKAGES) != null) {

            String[] packages = (String[])attributes.get(BASE_PACKAGES);

            if (packages != null) {
                for (String pkg : packages) {
                    if (StringUtils.isNotBlank(pkg)){
                        basePackages.add(pkg);
                    }
                }
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        //扫描并注册Client
        scanAndRegisterClient(basePackages,registry);
        //扫描并注册Route
        scanRouteAndRegister(basePackages, registry);
        //注册rpc后置处理器
        registerRpcProcess(registry);
    }

    private void registerRpcProcess(BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(RpcPostProcessor.class);
        registry.registerBeanDefinition("rpcPostProcessor", definition.getBeanDefinition());
    }

    private void scanRouteAndRegister(Set<String> basePackages, BeanDefinitionRegistry registry) {
        RpcRouteScanner scanner = new RpcRouteScanner(false, environment);
        scanner.setResourceLoader(resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RpcRoute.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.getInterfaceNames().length > 0,
                            "the class annotated @RpcRoute must implement an interface");
                    Assert.isTrue(annotationMetadata.isConcrete(),
                            "@RpcRoute can not be specified on an interface or abstract");
                    //注册Route Class
                    RouterFactory.addRouterClazz(annotationMetadata.getClassName());
                    //注册Route bean
                    registerRoute(registry, annotationMetadata);
                }
            }
        }

    }

    private void registerRoute(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata) {
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(className);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), generateBeanName(className));
        // 注册到spring容器
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private void scanAndRegisterClient(Set<String> basePackages, BeanDefinitionRegistry registry) {

        RpcClientScanner scanner = new RpcClientScanner(false,environment);
        scanner.setResourceLoader(resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RpcClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                            "@RpcClient can only be specified on an interface");
                    //注册server address
                    RpcServerAddressRegistry.register(annotationMetadata, environment);
                    //注册Client bean
                    registerClient(registry, annotationMetadata);
                }
            }
        }


    }
    private void registerClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata) {
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcClientFactoryBean.class);
        builder.addPropertyValue("type", className);
        builder.setRole(RootBeanDefinition.ROLE_INFRASTRUCTURE);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), generateBeanName(className));
        // 注册到spring容器
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private String generateBeanName(String className) {
        String shortName = org.springframework.util.ClassUtils.getShortName(className);
        return Introspector.decapitalize(shortName);
    }
}
