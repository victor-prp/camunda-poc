package victor.prp.cammunda.poc.sync;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import reactor.core.publisher.Mono;
import victor.prp.cammunda.poc.util.ZeebeReactiveOperations;

@SpringBootApplication(scanBasePackageClasses = {RedeemPointsSync.class,ZeebeReactiveOperations.class})
@EnableZeebeClient
public class RedeemPointsSync {
    private static final Logger log = LoggerFactory.getLogger(RedeemPointsSync.class);

    private final ZeebeClient zeebeClient;

    private final ZeebeReactiveOperations zeebe;

    public RedeemPointsSync(ZeebeClient zeebeClient, ZeebeReactiveOperations zeebe) {
        this.zeebeClient = zeebeClient;
        this.zeebe = zeebe;
    }


    public static void main(String[] args) {
        SpringApplication.run(RedeemPointsSync.class, args);
    }

    @ZeebeWorker(type = "start-redeem-points", autoComplete = false)
    public void redeem(final ActivatedJob job) {

        zeebe.doAsync(job,
            simulateCreateRedemptionStateInDB());//Asynchronous invocation using reactive programming
        log.info("Starting: redeem-points");
    }

    private Mono<String> simulateCreateRedemptionStateInDB() {
        return Mono.delay(Duration.ofSeconds(5))
            .map(number -> UUID.randomUUID().toString())
            .doOnSuccess(id -> log.info("Redemption state is created in DB, id: {}", id));
    }

    @ZeebeWorker(type = "subtract-points", autoComplete = false)
    public void subtractPoints(final ActivatedJob job) {

        zeebe.doAsync(job,
            simulateRestCallToSubtractPoints());//Asynchronous invocation using reactive programming
        log.info("Starting: subtract-points" + job.getVariables());
    }

    private Mono<String> simulateRestCallToSubtractPoints() {
        return Mono.delay(Duration.ofSeconds(5))
            .map(number -> UUID.randomUUID().toString())
            .doOnSuccess(id -> log.info("Points are subtracted, transactionId: {}", id));
    }

    @ZeebeWorker(type = "create-reward", autoComplete = false)
    public void createReward(final ActivatedJob job) {


        boolean fail = job.getVariablesAsMap().containsKey("fail-rewards");
        zeebe.doAsyncWithVars(job,
            simulateRestCallToCreateRewards(fail)); //Asynchronous invocation using reactive programming
        log.info("Starting: create-reward" + job.getVariables());

    }

    private Mono<Map<String, Object>> simulateRestCallToCreateRewards(boolean fail) {
        if (fail) {
            return Mono.error(new ZeebeBpmnError("1", "Reward cannot be created"));
        }
        return Mono.delay(Duration.ofSeconds(5))
            .map(number -> {
                String rewardId = UUID.randomUUID().toString();
                log.info("Completed: create-reward, id: {}", rewardId);
                return Collections.singletonMap("rewardId", rewardId);
            });
    }


    @ZeebeWorker(type = "add-points", autoComplete = true)
    public void addPoints(final ActivatedJob job) {

        // Do the business logic
        log.info("Completed: add-points");
    }


    @ZeebeWorker(type = "complete-redeem-points", autoComplete = true)
    public void completeRedeemPoints(final ActivatedJob job) {

        // Do the business logic
        log.info("Completed: redeem-points" + job.getVariables());
    }


    @ZeebeWorker(type = "fail-redeem-points", autoComplete = true)
    public void failRedeemPoints(final ActivatedJob job) {

        // Do the business logic
        log.info("Completed: fail-redeem-points");

    }

}
