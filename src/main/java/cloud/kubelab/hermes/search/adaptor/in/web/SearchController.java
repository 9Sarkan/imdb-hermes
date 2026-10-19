package cloud.kubelab.hermes.search.adaptor.in.web;

import cloud.kubelab.hermes.search.adaptor.in.web.api.SearchApi;
import cloud.kubelab.hermes.search.adaptor.in.web.dto.SearchField;
import cloud.kubelab.hermes.search.adaptor.in.web.dto.SearchResponse;
import cloud.kubelab.hermes.search.adaptor.in.web.dto.TitleHit;
import cloud.kubelab.hermes.search.application.SearchService;
import cloud.kubelab.hermes.search.domain.InvalidSearchQueryException;
import cloud.kubelab.hermes.search.domain.SearchHit;
import cloud.kubelab.hermes.search.domain.SearchQuery;
import cloud.kubelab.hermes.search.domain.SearchResult;
import cloud.kubelab.hermes.shared.document.IndexField;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("reader")
@RestController
public class SearchController implements SearchApi {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public ResponseEntity<SearchResponse> search(SearchField field, String value, Integer limit) {
        SearchQuery q = new SearchQuery(toIndexField(field), value, limit);
        SearchResult result = this.searchService.search(q);
        return ResponseEntity.ok(hitsToResponse(result));
    }

    private static IndexField toIndexField(SearchField fieldName) {
        return IndexField.byName(fieldName.getValue()).orElseThrow(
                () -> new InvalidSearchQueryException("Field " + fieldName.getValue() + " not found.")
        );
    }

    private SearchResponse hitsToResponse(SearchResult searchResult) {
        List<TitleHit> titleHits = searchResult.result().stream().map(SearchController::toTitleHit).toList();

        return new SearchResponse(titleHits, searchResult.totalHits());
    }

    private static TitleHit toTitleHit(SearchHit hit) {
        return new TitleHit(hit.id(), hit.titleType(), hit.primaryTitle(), hit.genres(), hit.score()).
                startYear(hit.startYear().orElse(null));
    }
}
