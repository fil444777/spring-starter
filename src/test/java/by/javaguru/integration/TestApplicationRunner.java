package by.javaguru.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import spring.database.repository.pool.ConnectionPool;

@TestConfiguration
public class TestApplicationRunner {

    @MockitoSpyBean(name = "ConnectionPool2")
    private ConnectionPool pool1;
}
