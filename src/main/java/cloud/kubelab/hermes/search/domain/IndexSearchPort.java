package cloud.kubelab.hermes.search.domain;

public interface IndexSearchPort {
    SearchResult search(SearchQuery searchQuery);
}
