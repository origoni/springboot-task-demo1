package com.millky.demo.task.one;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AppRunner implements CommandLineRunner {

    private final GitHubLookupService gitHubLookupService;

    public AppRunner(GitHubLookupService gitHubLookupService) {
        this.gitHubLookupService = gitHubLookupService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Start the clock
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        List<CompletableFuture<User>> completableFutureList = new ArrayList<>();
        completableFutureList.add(gitHubLookupService.findUser("PivotalSoftware"));
        completableFutureList.add(gitHubLookupService.findUser("CloudFoundry"));
        completableFutureList.add(gitHubLookupService.findUser("Spring-Projects"));
        completableFutureList.add(gitHubLookupService.findUser("origoni"));
        completableFutureList.add(gitHubLookupService.findUser("onspring"));


        // Wait until they are all done
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();

//        CompletableFuture.anyOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();
//        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()]));


        // Print results, including elapsed time
        log.info("Elapsed time: " + (System.currentTimeMillis() - start));

        completableFutureList.forEach(future -> future.whenComplete((user, ex) -> {
            if (ex != null) {
                log.error("--> " + user + "; error=" + ex.getLocalizedMessage());
            } else {
                log.info("--> " + user);
            }
        }));
    }
}
