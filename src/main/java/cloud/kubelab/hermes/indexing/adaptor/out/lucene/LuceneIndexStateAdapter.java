package cloud.kubelab.hermes.indexing.adaptor.out.lucene;

import cloud.kubelab.hermes.indexing.domain.IndexStatePort;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
@Profile("writer")
public class LuceneIndexStateAdapter implements IndexStatePort {
    private final Directory directory;

    public LuceneIndexStateAdapter(Directory directory){
        this.directory = directory;
    }

    @Override
    public boolean indexExists() {
        try {
            if (!DirectoryReader.indexExists(directory)){
                return false;
            }

            try (DirectoryReader reader = DirectoryReader.open(directory)) {
                return reader.numDocs() > 0;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to inspect index directory", e);
        }
    }
}
