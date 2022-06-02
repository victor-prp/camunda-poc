package victor.prp.cammunda.poc.async;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class AsyncSimple {
    private final ZeebeClient zeebeClient;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    public AsyncSimple(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(AsyncSimple.class, args);
    }

    @ZeebeWorker(type = "async-clone-config", autoComplete = true)
    public Map<String, Object> asyncClone(final ActivatedJob job) {

        // Do the business logic
        System.out.println("Starting async-clone-config " + job.getVariables());

        String correlationId = UUID.randomUUID().toString();
        executor.schedule(() -> cloneLongRunning(correlationId) , 1, TimeUnit.MINUTES);
        System.out.println("Clone long running scheduled, correlationId: "+correlationId);

        return Collections.singletonMap("CloneCompletedCorrelationKey", correlationId);
    }

    private void cloneLongRunning(String correlationId){
        System.out.println("Clone long running completed: going to publish message, correlationId: "+correlationId);

        zeebeClient.newPublishMessageCommand()
            .messageName("CloneCompleted")
            .correlationKey(correlationId)
            .messageId(UUID.randomUUID().toString())
            .send()
            .join();
    }

    @ZeebeWorker(type = "async-send-success-email", autoComplete = true)
    public Map<String, Object> sendSuccessEmail(final ActivatedJob job) {

        // Do the business logic
        System.out.println("Sending email: success " + job.getVariables());

        // Probably add some process variables
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("email", "sent");
        return variables;
    }

}
