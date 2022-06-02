package victor.prp.cammunda.poc.async;

import java.util.*;
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
public class AsyncAll {
    private final ZeebeClient zeebeClient;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    public AsyncAll(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(AsyncAll.class, args);
    }

    @ZeebeWorker(type = "async-clone-config", autoComplete = true)
    public void asyncClone(final ActivatedJob job) {
        // Do the business logic
        System.out.println("Starting async-clone-config " + job.getVariables());
    }

    @ZeebeWorker(type = "async-clone-points", autoComplete = true)
    public Map<String, Object> clonePoints(final ActivatedJob job) {

        boolean failPoints = job.getVariablesAsMap().containsKey("fail-points");

        String correlationId = UUID.randomUUID().toString();
        System.out.println("Going to start async-clone-points, id: " + correlationId);
        executor.schedule(() -> clonePointsLongRunning(correlationId, failPoints) , 1, TimeUnit.SECONDS);

        Map<String,Object> vars = new HashMap<>();
        vars.put("ClonePointsCompletedCorrelationKey", correlationId);
        vars.put("ClonePointsFailedCorrelationKey", correlationId);
        return vars;
    }

    private void clonePointsLongRunning(String correlationId, boolean fail){
        if (fail){
            System.out.println("async-clone-points is going to fail, correlationId: "+correlationId);
            zeebeClient.newPublishMessageCommand()
                .messageName("PointsCloneFailed")
                .correlationKey(correlationId)
                .messageId(UUID.randomUUID().toString())
                .send()
                .join();

        }else {
            System.out.println("async-clone-points is going to complete, correlationId: " + correlationId);
            zeebeClient.newPublishMessageCommand()
                .messageName("PointsCloneCompleted")
                .correlationKey(correlationId)
                .messageId(UUID.randomUUID().toString())
                .send()
                .join();
        }
    }

    @ZeebeWorker(type = "async-revert-points", autoComplete = true)
    public Map<String, Object> revertPoints(final ActivatedJob job) {

        String correlationId = UUID.randomUUID().toString();
        System.out.println("Going to start async-revert-points, id: " + correlationId);
        executor.schedule(() -> revertPointsLongRunning(correlationId) , 1, TimeUnit.SECONDS);

        Map<String,Object> vars = new HashMap<>();
        vars.put("RevertPointsCompletedCorrelationKey", correlationId);
        return vars;
    }

    private void revertPointsLongRunning(String correlationId) {
        System.out.println("async-revert-points is going to complete, correlationId: " + correlationId);
        zeebeClient.newPublishMessageCommand()
            .messageName("PointsRevertCompleted")
            .correlationKey(correlationId)
            .messageId(UUID.randomUUID().toString())
            .send()
            .join();
    }


    @ZeebeWorker(type = "async-clone-rewards", autoComplete = true)
    public Map<String, Object> cloneRewards(final ActivatedJob job) {

        boolean failPoints = job.getVariablesAsMap().containsKey("fail-rewards");

        String correlationId = UUID.randomUUID().toString();
        System.out.println("Going to start async-clone-rewards, id: " + correlationId);
        executor.schedule(() -> cloneRewardsLongRunning(correlationId, failPoints) , 1, TimeUnit.SECONDS);

        Map<String,Object> vars = new HashMap<>();
        vars.put("CloneRewardsCompletedCorrelationKey", correlationId);
        vars.put("CloneRewardsFailedCorrelationKey", correlationId);
        return vars;
    }



    private void cloneRewardsLongRunning(String correlationId, boolean fail){
        if (fail){
            System.out.println("async-clone-rewards going to fail, correlationId: "+correlationId);
            zeebeClient.newPublishMessageCommand()
                .messageName("RewardsCloneFailed")
                .correlationKey(correlationId)
                .messageId(UUID.randomUUID().toString())
                .send()
                .join();

        }else {
            System.out.println("async-clone-rewards is going to complete, correlationId: " + correlationId);
            zeebeClient.newPublishMessageCommand()
                .messageName("RewardsCloneCompleted")
                .correlationKey(correlationId)
                .messageId(UUID.randomUUID().toString())
                .send()
                .join();
        }
    }

    @ZeebeWorker(type = "async-send-success-email", autoComplete = true)
    public void sendSuccessEmail(final ActivatedJob job) {
        System.out.println("Sending email: success " + job.getVariables());
    }

    @ZeebeWorker(type = "async-send-failure-email", autoComplete = true)
    public void sendFailureEmail(final ActivatedJob job) {
        System.out.println("Sending email: failure " + job.getVariables());
    }

}
