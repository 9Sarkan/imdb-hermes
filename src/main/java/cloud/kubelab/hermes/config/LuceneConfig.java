package cloud.kubelab.hermes.config;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class LuceneConfig {

    private static final Logger log = LoggerFactory.getLogger(LuceneConfig.class);

    @Bean
    public Directory indexDirectory(HermesIndexProperties properties) throws IOException {
        log.info("Lucene index directory: {}", properties.getPath().toAbsolutePath());

        return FSDirectory.open(properties.getPath());
    }

    @Bean
    public Analyzer analyzer() {
        return new StandardAnalyzer();
    }
}
