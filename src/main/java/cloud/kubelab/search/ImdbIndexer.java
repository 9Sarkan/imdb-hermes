package cloud.kubelab.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class ImdbIndexer {
    private final IndexWriter writer;

    public ImdbIndexer(String indexDirPath) throws IOException {
        Directory indexDir = FSDirectory.open(Paths.get(indexDirPath));
        StandardAnalyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        this.writer = new IndexWriter(indexDir, config);
    }

    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }

    public void indexFile(String filePath) throws IOException {
        long startTime = System.currentTimeMillis();
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // tconst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres
                String[] columns = line.split("\t");

                if (columns.length < 9) continue;

                String id = columns[0];
                String titleType = columns[1];
                String primaryTitle = columns[2];
                String originalTitle = columns[3];
                boolean isAdult = Objects.equals(columns[4], "1");

                boolean hasStartYear = !Objects.equals(columns[5], "\\N");
                boolean hasEndYear = !Objects.equals(columns[6], "\\N");

                Document doc = new Document();

                doc.add(new StringField("id", id, Field.Store.YES));
                doc.add(new StringField("type", titleType, Field.Store.YES));
                if (hasStartYear) {
                    doc.add(new IntField("year", Integer.parseInt(columns[5]), Field.Store.YES));
                }

                doc.add(new TextField("title", primaryTitle, Field.Store.YES));

                writer.addDocument(doc);
                count ++;
                if (count % 10000 == 0) {
                    System.out.println("Indexed " + count + " records...");
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Successfully indexed " + count + " documents in " + (endTime - startTime) + " ms.");
        }
    }
}
