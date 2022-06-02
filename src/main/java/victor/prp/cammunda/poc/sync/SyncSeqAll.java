package victor.prp.cammunda.poc.sync;

import java.util.HashMap;
import java.util.Map;

import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@SpringBootApplication
@EnableZeebeClient
public class SyncSeqAll {

  public static void main(String[] args) {
    SpringApplication.run(SyncSeqAll.class, args);
  }
  
  @ZeebeWorker(type = "clone-config", autoComplete = true)
  public Map<String, Object> orchestrateSomething(final ActivatedJob job) {

      // Do the business logic
      System.out.println("Starting clone config " + job.getVariables());

      // Probably add some process variables
      HashMap<String, Object> variables = new HashMap<>();
      variables.put("resultValue1", 42);
      return variables;
  }

    @ZeebeWorker(type = "clone-points", autoComplete = true)
    public Map<String, Object> clonePoints(final ActivatedJob job) {

        // Do the business logic
        System.out.println("Points config is cloned: " + job.getVariables());

        // Probably add some process variables
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("points", "cloned");
        return variables;
    }

    @ZeebeWorker(type = "clone-rewards", autoComplete = true)
    public Map<String, Object> cloneRewards(final ActivatedJob job) {
        // Probably add some process variables
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("reward", "cloned");

        if (job.getVariablesAsMap().containsKey("fail-rewards")){
            System.out.println("Clone rewards failed : " + job.getVariables());
            throw new ZeebeBpmnError("1","Rewards config cannot be cloned");
        }
        System.out.println("Rewards config is cloned: " + job.getVariables());
        return variables;
    }

    @ZeebeWorker(type = "revert-clone-rewards", autoComplete = true)
    public Map<String, Object> revertCloneRewards(final ActivatedJob job) {

        // Do the business logic
        System.out.println("Revert clone rewards config: " + job.getVariables());

        // Probably add some process variables
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("revert-rewards", "done");
        return variables;
    }

    @ZeebeWorker(type = "revert-clone-points", autoComplete = true)
    public Map<String, Object> revertClonePoints(final ActivatedJob job) {

        // Do the business logic
        System.out.println("Revert clone points config: " + job.getVariables());

        // Probably add some process variables
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("revert-points", "done");
        return variables;
    }


    @ZeebeWorker(type = "send-success-email", autoComplete = true)
    public Map<String, Object> sendSuccessEmail(final ActivatedJob job) {

        // Do the business logic
        System.out.println("Sending email: success " + job.getVariables());

        // Probably add some process variables
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("email", "sent");
        return variables;
    }


    @ZeebeWorker(type = "send-failure-email", autoComplete = true)
    public Map<String, Object> sendFailedEmail(final ActivatedJob job) {

        // Do the business logic
        System.out.println("Sending email: failed " + job.getVariables());

        // Probably add some process variables
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("email", "sent");
        return variables;
    }

}
