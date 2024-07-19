package com.jo.support.support.scanner;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

/**
 * @author Jo
 * @date 2024/7/10
 */
public class RpcClientScanner extends ClassPathScanningCandidateComponentProvider {

    public RpcClientScanner(boolean useDefaultFilters, Environment environment) {
        super(useDefaultFilters, environment);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent() &&
                beanDefinition.getMetadata().isInterface();
    }
}
