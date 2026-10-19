package cloud.kubelab.hermes.search.domain;

import cloud.kubelab.hermes.shared.document.IndexField;

import java.util.Objects;

public record SearchQuery (IndexField field, String value, int limit) {
    public SearchQuery {
        Objects.requireNonNull(field);
        Objects.requireNonNull(value);
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be greater than 0");
        }
    }
}
