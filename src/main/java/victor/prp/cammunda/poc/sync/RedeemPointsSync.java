package victor.prp.cammunda.poc.sync;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@SpringBootApplication
@EnableZeebeClient
public class RedeemPointsSync {
    private static final Logger log = LoggerFactory.getLogger(RedeemPointsSync.class);


    public static void main(String[] args) {
    SpringApplication.run(RedeemPointsSync.class, args);
  }
  
  @ZeebeWorker(type = "start-redeem-points", autoComplete = true)
  public void redeem(final ActivatedJob job) {

      // Do the business logic
      log.info("Starting: redeem-points" + job.getVariables());
  }

    @ZeebeWorker(type = "subtract-points", autoComplete = true)
    public void subtractPoints(final ActivatedJob job) {

        // Do the business logic
        log.info("Completed: subtract-points" + job.getVariables());
    }

    @ZeebeWorker(type = "create-reward", autoComplete = true)
    public Map<String, Object> createReward(final ActivatedJob job) {

        if (job.getVariablesAsMap().containsKey("fail-rewards")){
            log.info("Failed: create-reward");
            throw new ZeebeBpmnError("1","Reward cannot be created");
        }

        String rewardId = UUID.randomUUID().toString();
        log.info("Completed: create-reward, id: {}", rewardId);
        return Collections.singletonMap("rewardId", rewardId);
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
