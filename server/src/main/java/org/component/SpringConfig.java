package org.component;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("org.domain")
@ComponentScan
@EnableAutoConfiguration
public class SpringConfig {

    @Bean
    public Void getServer(Service service) {
        Server server = new Server(1234, 10, service);
        server.startServer(30 * 1000);
        return null;
    }
}


