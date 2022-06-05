package victor.prp.cammunda.poc.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CompletablePromise<V> extends CompletableFuture<V> {
    private Future<V> future;

    public CompletablePromise(Future<V> future) {
        this.future = future;
        CompletablePromiseContext.schedule(this::tryToComplete);
    }

    private void tryToComplete() {
        if (future.isDone()) {
            try {
                complete(future.get());
            } catch (InterruptedException e) {
                completeExceptionally(e);
            } catch (ExecutionException e) {
                completeExceptionally(e.getCause());
            }
            return;
        }

        if (future.isCancelled()) {
            cancel(true);
            return;
        }

        CompletablePromiseContext.schedule(this::tryToComplete);
    }
}
