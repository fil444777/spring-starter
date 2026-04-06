package spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.*;
import spring.database.repository.UserRepository;
import spring.database.repository.pool.ConnectionPool;


@Configuration
public class ApplicationConfiguration {

    @Bean("connectionPool1")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public ConnectionPool connectionPool(@Value("${db.username}") String username) {
        return new ConnectionPool(username, "aezakmi", 20, "url");
    }

    @Bean
    public ConnectionPool connectionPool2() {
        return new ConnectionPool("mysql", "123", 100, "----");
    }

//    @Bean
//    @Profile("prod&web")
//    public UserRepository userRepository() {
//        return new UserRepository(connectionPool2());
//    }
}
