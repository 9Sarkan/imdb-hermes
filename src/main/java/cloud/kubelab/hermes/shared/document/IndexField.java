package cloud.kubelab.hermes.shared.document;

import java.util.Arrays;
import java.util.Optional;

public enum IndexField {
    ID("id", Kind.KEYWORD),
    TITLE_TYPE("titleType", Kind.KEYWORD),
    PRIMARY_TITLE("primaryTitle", Kind.TEXT),
    START_YEAR("startYear", Kind.INT_POINT),
    GENRE("genre", Kind.KEYWORD);

    public enum Kind {
        KEYWORD,
        TEXT,
        INT_POINT
    }

    private final String fieldName;
    private final Kind kind;

    IndexField(String fieldName, Kind kind) {
        this.fieldName = fieldName;
        this.kind = kind;
    }

    public String fieldName() {
        return fieldName;
    }

    public Kind kind() {
        return kind;
    }

    public static Optional<IndexField> byName(String fieldName) {
        return Arrays.stream(values())
                .filter(field -> field.fieldName.equals(fieldName))
                .findFirst();
    }
}
