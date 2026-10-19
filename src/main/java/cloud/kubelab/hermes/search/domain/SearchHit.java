package cloud.kubelab.hermes.search.domain;

import java.util.List;
import java.util.Optional;

public record SearchHit(
        String id,
        String titleType,
        String primaryTitle,
        Optional<Integer> startYear,
        List<String> genres,
        float score
) {
}
