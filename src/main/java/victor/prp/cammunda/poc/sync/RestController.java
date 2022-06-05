package victor.prp.cammunda.poc.sync;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import victor.prp.cammunda.poc.util.CompletablePromise;
import victor.prp.cammunda.poc.util.ReactiveUtil;

@Controller
public class RestController {
    private static final Logger log = LoggerFactory.getLogger(RestController.class);

    private final ZeebeClient zeebeClient;

    public RestController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @PostMapping(
        value = "/redeem-points",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    Mono<ResponseEntity<ProcessInstanceEvent>> create(@RequestBody(required = false) String inputParams){
        Future<ProcessInstanceEvent> future = zeebeClient.newCreateInstanceCommand()
            .bpmnProcessId("Process_16f5e059-fa88-441c-968b-cd0b5834233e")
            .latestVersion()
            .variables(inputParams)
            .send();

        return ReactiveUtil.toMono(future)
            .doOnSuccess( result -> log.info("result: {}", result))
            .map( result -> ResponseEntity
                .accepted()
                .body(result));
    }

}
