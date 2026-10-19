package cloud.kubelab.hermes.search.adaptor.out.lucene;

import cloud.kubelab.hermes.search.domain.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;


@Component
@Profile("reader")
public class LuceneIndexSearch implements IndexSearchPort {
    private final IndexSearcher indexSearcher;
    private final DocumentToHitMapper documentToHitMapper;
    private final Analyzer analyzer;


    public LuceneIndexSearch(IndexSearcher indexSearcher, DocumentToHitMapper documentMapper, Analyzer analyzer) {
        this.indexSearcher = indexSearcher;
        this.documentToHitMapper = documentMapper;
        this.analyzer = analyzer;
    }

    public SearchResult search(SearchQuery searchQuery) {
        try{
            Query q = buildQuery(searchQuery);
            TopDocs docs = indexSearcher.search(q, searchQuery.limit());
            List<SearchHit> hits = new ArrayList<>();
            StoredFields storedFields = indexSearcher.storedFields();
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                hits.add(this.documentToHitMapper.toSearchHit(storedFields.document(scoreDoc.doc), scoreDoc.score));
            }
            return new SearchResult(hits, indexSearcher.count(q));
        } catch (IOException e){
            throw new UncheckedIOException("Query field: " + searchQuery.field().fieldName(), e);
        }
    }

    private Query buildQuery(SearchQuery query) throws IOException {
        String fieldName = query.field().fieldName();
        String value = query.value();
        return switch (query.field().kind()) {
            case KEYWORD -> new TermQuery(new Term(fieldName, value));
            case TEXT -> buildPhraseQuery(fieldName, value);
            case INT_POINT -> IntPoint.newExactQuery(fieldName, Integer.parseInt(value));
        };
    }

    private Query buildPhraseQuery(String fieldName, String value) throws IOException {
        List<String> tokens = analyse(fieldName, value);

        PhraseQuery.Builder phraseQueryBuilder = new PhraseQuery.Builder();

        for (String token : tokens) {
            phraseQueryBuilder.add(new Term(fieldName, token));
        }

        return phraseQueryBuilder.build();
    }

    private List<String> analyse(String field, String value) throws IOException {
        List<String> tokens = new ArrayList<>();
        try(TokenStream stream = this.analyzer.tokenStream(field, value)) {
            CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                tokens.add(term.toString());
            }
            stream.end();
        }

        if (tokens.isEmpty()) {
            throw new InvalidSearchQueryException("No tokens found for field: " + field);
        }

        return tokens;
    }
}
