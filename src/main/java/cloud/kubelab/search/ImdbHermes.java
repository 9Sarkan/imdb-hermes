package cloud.kubelab.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.nio.file.Paths;

public class ImdbHermes {
    private final IndexReader reader;
    private final IndexSearcher searcher;
    private final QueryParser parser;
    private final StoredFields storedFields;

    public ImdbHermes(String indexDirPath) throws IOException {
        Directory mmapDirectory = new MMapDirectory(Paths.get(indexDirPath));

        this.reader = DirectoryReader.open(mmapDirectory);
        this.searcher = new IndexSearcher(reader);
        this.storedFields = reader.storedFields();

        StandardAnalyzer analyzer = new StandardAnalyzer();
        this.parser = new QueryParser("title", analyzer);

        System.out.println("Index loaded in memory");
    }

    public void search(String queryStr) {
        try {
            Query query = this.parser.parse(queryStr);
            TopDocs hits = searcher.search(query, 10);
            System.out.println("Found "+ hits.totalHits.value() + "\nquery: " + queryStr);

            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = storedFields.document(scoreDoc.doc);
                System.out.printf("- [%s] %s (%s) | Score: %.4f%n",
                        doc.get("id"),
                        doc.get("title"),
                        doc.get("year"),
                        scoreDoc.score);
            }

        } catch (ParseException | IOException e) {
            System.out.println("failed to parse or execute query: " +  e.getMessage());
        }
    }

    public void close() throws IOException{
        if (reader != null){
            reader.close();
        }
    }
}
