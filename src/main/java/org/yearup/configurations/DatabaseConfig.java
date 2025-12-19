package org.yearup.configurations;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig
{
    private final String serverUrl;
    private final String username;
    private final String password;

    @Autowired
    public DatabaseConfig(@Value("${spring.datasource.url}") String serverUrl,
                          @Value("${spring.datasource.username}") String username,
                          @Value("${spring.datasource.password}") String password)
    {
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;
    }

    @Bean
    public DataSource dataSource()
    {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(serverUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}