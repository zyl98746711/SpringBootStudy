package com.yan.configuration;

import com.yan.logging.ErrorLogger;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Objects;

import javax.sql.DataSource;

import lombok.Getter;

/**
 * ApplicationContext 工具类 及 检查连接
 */
@Getter
@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        try {
            DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
            Connection connection = dataSource.getConnection();
            if (Objects.isNull(connection)) {
                System.exit(-1);
            }
            this.applicationContext = applicationContext;
        } catch (Exception e) {
            ErrorLogger.log(e.getMessage());
            System.exit(-1);
        }
    }

}
