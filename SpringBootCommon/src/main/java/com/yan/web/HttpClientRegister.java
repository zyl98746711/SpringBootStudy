package com.yan.web;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.annotation.Nonnull;

public class HttpClientRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        String className = importingClassMetadata.getClassName();
        int lastDotIndex = className.lastIndexOf('.');
        String backPackage = (lastDotIndex != -1) ? className.substring(0, lastDotIndex) : "";
        ScanResult scan = new ClassGraph().enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(backPackage)
                .scan();


        scan.getClassesWithAnnotation(HttpClient.class.getName()).forEach(classInfo -> {

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(HttpClientFactoryBean.class);
            beanDefinitionBuilder.addPropertyValue("type", classInfo.loadClass());
            classInfo.getAnnotationInfo().filter(info -> HttpClient.class.getName().equals(info.getName()))
                    .forEach(annotationInfo -> {
                        AnnotationParameterValueList parameterValues = annotationInfo.getParameterValues();
                        parameterValues.forEach(p -> {
                            String name = p.getName();
                            Object value = p.getValue();
                            beanDefinitionBuilder.addPropertyValue(name, value);
                        });
                    });
            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            registry.registerBeanDefinition(classInfo.getName(), beanDefinition);
        });

    }
}
