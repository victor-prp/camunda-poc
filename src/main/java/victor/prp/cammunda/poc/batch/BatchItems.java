package victor.prp.cammunda.poc.batch;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class BatchItems {

    private static final Logger log = LoggerFactory.getLogger(BatchItems.class);


    public static void main(String[] args) {
        SpringApplication.run(BatchItems.class, args);
    }

    @ZeebeWorker(type = "create-items", autoComplete = true)
    public Map<String, Object> createItems(final @ZeebeVariable Integer count) {

        log.info("Starting create-items, count: {} ", count);

        // Probably add some process variables
        HashMap<String, Object> variables = new HashMap<>();
        List<String> items = IntStream.range(1, count)
            .boxed()
            .map(i -> "item"+i)
            .collect(Collectors.toList());
        return  Collections.singletonMap("items",items);
    }

    @ZeebeWorker(type = "item-processor", autoComplete = true)
    public void itemProcessor(final @ZeebeVariable String item) {

        log.info("Starting item-processor, item: " + item);

    }

    @ZeebeWorker(type = "notify-done", autoComplete = true)
    public void notifyDone(@ZeebeVariable Collection<String> items) {

        log.info("Completed, items count: " + items.size());

    }
}