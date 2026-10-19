package cloud.kubelab.hermes.indexing.application;

import cloud.kubelab.hermes.indexing.domain.DocumentSourcePort;
import cloud.kubelab.hermes.indexing.domain.IndexStatePort;
import cloud.kubelab.hermes.indexing.domain.IndexWritePort;
import cloud.kubelab.hermes.indexing.domain.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@Profile("writer")
public class IndexBootstrapService {
    private static final Logger log = LoggerFactory.getLogger(IndexBootstrapService.class);

    private final IndexStatePort indexState;
    private final DocumentSourcePort documentSource;
    private final IndexWritePort indexWrite;

    public IndexBootstrapService(IndexStatePort indexState, DocumentSourcePort documentSource, IndexWritePort indexWrite){
        this.indexState = indexState;
        this.documentSource = documentSource;
        this.indexWrite = indexWrite;
    }

    public void bootstrap() {
        if (indexState.indexExists()) {
            log.info("Usable index already present, skipping bootstrap!");
            return;
        }

        log.info("No usable index found, building index from seed file");
        long startedAt = System.currentTimeMillis();

        long written;
        try (Stream<Title> titles = documentSource.titles()){
            written = indexWrite.addAll(titles);
        }

        log.info("Bootstrap completed: {} titles indexed in {} ms", written, (System.currentTimeMillis() - startedAt));
    }
}
