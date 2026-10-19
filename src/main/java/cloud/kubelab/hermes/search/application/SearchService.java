package cloud.kubelab.hermes.search.application;


import cloud.kubelab.hermes.search.domain.IndexSearchPort;
import cloud.kubelab.hermes.search.domain.SearchQuery;
import cloud.kubelab.hermes.search.domain.SearchResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("reader")
public class SearchService {
    private final IndexSearchPort indexSearcher;

    public SearchService(IndexSearchPort indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    public SearchResult search(SearchQuery query) {
        // validate search query
        return this.indexSearcher.search(query);
    }
}
