package cloud.kubelab.hermes.indexing.adaptor.out.lucene;

import cloud.kubelab.hermes.indexing.domain.IndexWritePort;
import cloud.kubelab.hermes.indexing.domain.Title;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.stream.Stream;

@Component
@Profile("writer")
public class LuceneIndexWriteAdapter implements IndexWritePort {
    private static final Logger log = LoggerFactory.getLogger(LuceneIndexWriteAdapter.class);
    private static final long PROGRESS_INTERVAL = 100_000;

    private final IndexWriter indexWriter;
    private final TitleDocumentMapper documentMapper;

    public LuceneIndexWriteAdapter(IndexWriter indexWriter, TitleDocumentMapper documentMapper){
        this.indexWriter = indexWriter;
        this.documentMapper = documentMapper;
    }

    @Override
    public void add(Title title) {
        try {
            indexWriter.addDocument(this.documentMapper.toDocument(title));
            indexWriter.commit();
        } catch (IOException e){
            throw new UncheckedIOException("faild to index title " + title.getId(), e);
        }
    }

    @Override
    public long addAll(Stream<Title> titles) {
        long written = 0;
        try {
            for (Iterator<Title> it = titles.iterator(); it.hasNext();) {
                indexWriter.addDocument(documentMapper.toDocument(it.next()));
                if (++written % PROGRESS_INTERVAL == 0){
                    log.info("indexed {} documents so far", written);
                }
            }
            indexWriter.commit();
            return written;
        } catch (IOException e) {
            rollback();
            throw new UncheckedIOException("Bulk indexing failed after " + written + " documents", e);
        } catch (RuntimeException e) {
            rollback();
            throw e;
        }
    }

    private void rollback(){
        try{
            indexWriter.rollback();
        } catch (IOException e) {
            log.error("Rollback failed; index directory maybe inconsistent", e);
        }
    }
}
