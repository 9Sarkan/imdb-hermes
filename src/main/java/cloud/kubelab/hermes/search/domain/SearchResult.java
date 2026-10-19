package cloud.kubelab.hermes.search.domain;

import java.util.List;

public record SearchResult(List<SearchHit> result, long totalHits) {
}
