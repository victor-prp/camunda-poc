package victor.prp.cammunda.poc.batch;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeClientConfig {

    @Bean
    public ZeebeClient client() {
        return ZeebeClient.newClientBuilder()
            .numJobWorkerExecutionThreads(100)
            .build();
    }
}
