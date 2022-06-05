package victor.prp.cammunda.poc.util;

import java.util.concurrent.Future;

import reactor.core.publisher.Mono;

public class ReactiveUtil {
    public static <T> Mono<T> toMono(Future<T> future){
        return Mono.fromFuture(new CompletablePromise<>(future));
    }
}
