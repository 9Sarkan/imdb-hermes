package cloud.kubelab.hermes.search.config;


import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;


@Configuration
@Profile("reader")
public class SearchLuceneConfig {
    private static final Logger log = LoggerFactory.getLogger(SearchLuceneConfig.class);

    @Bean
    public DirectoryReader directoryReader(Directory directory) throws IOException {
        DirectoryReader reader = DirectoryReader.open(directory);
        log.info("Opened index with {} documents", reader.numDocs());
        return reader;
    }

    @Bean
    public IndexSearcher indexSearcher(DirectoryReader reader){
        return new IndexSearcher(reader);
    }
}
