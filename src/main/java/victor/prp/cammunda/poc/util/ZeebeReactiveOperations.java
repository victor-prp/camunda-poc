package victor.prp.cammunda.poc.util;

import java.util.Map;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ZeebeReactiveOperations {
    private final ZeebeClient zeebeClient;

    public ZeebeReactiveOperations(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public void doAsync(ActivatedJob job, Mono<?> execution){
        execution.subscribe(
            response -> {
                zeebeClient.newCompleteCommand(job.getKey()).send()
                    .exceptionally(t -> {throw new RuntimeException("Could not complete job: " + t.getMessage(), t);});
            },
            exception -> {
                zeebeClient.newFailCommand(job.getKey())
                    .retries(1)
                    .errorMessage("Could not invoke REST API: " + exception.getMessage()).send()
                    .exceptionally(t -> {throw new RuntimeException("Could not fail job: " + t.getMessage(), t);});
            }
        );
    }

    public void doAsyncWithVars(ActivatedJob job, Mono<Map<String,Object>> execution){
        execution.subscribe(
            response -> {
                zeebeClient.newCompleteCommand(job.getKey())
                    .variables(response)
                    .send()
                    .exceptionally(t -> {throw new RuntimeException("Could not complete job: " + t.getMessage(), t);});
            },
            exception -> {
                zeebeClient.newFailCommand(job.getKey())
                    .retries(1)
                    .errorMessage("Could not invoke REST API: " + exception.getMessage()).send()
                    .exceptionally(t -> {throw new RuntimeException("Could not fail job: " + t.getMessage(), t);});
            }
        );
    }
}
