package cloud.kubelab.hermes.indexing.adaptor.in.bootstrap;


import cloud.kubelab.hermes.indexing.application.IndexBootstrapService;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("writer")
public class IndexBootstrapRunner implements ApplicationRunner {
    private final IndexBootstrapService bootstrapService;

    public IndexBootstrapRunner(IndexBootstrapService bootstrapService) {
        this.bootstrapService = bootstrapService;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        bootstrapService.bootstrap();
    }
}
