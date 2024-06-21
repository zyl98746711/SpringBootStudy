package com.yan.configuration;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.annotation.Nonnull;

public class ClientRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        String backPackage;
        try {
            Class<?> aClass = Class.forName(importingClassMetadata.toString());
            backPackage = aClass.getPackage().getName();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ScanResult scan = new ClassGraph().enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(backPackage)
                .scan();

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ClientFactoryBean.class);
        
        scan.getClassesWithAnnotation(HttpClient.class.getName()).forEach(classInfo -> {
            classInfo.getAnnotationInfo()
                    .filter(info -> HttpClient.class.getName().equals(info.getName())).forEach(annotationInfo -> {
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
