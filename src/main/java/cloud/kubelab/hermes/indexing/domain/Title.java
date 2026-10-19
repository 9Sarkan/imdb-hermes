package cloud.kubelab.hermes.indexing.domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Title {
    String id;
    String titleType;
    String primaryTitle;
    Optional<Integer> startYear;
    String[] genre;

    public Title(String id, String titleType, String primaryTitle, Optional<Integer> startYear, String[] genre) {
        this.id = Objects.requireNonNull(id);
        this.titleType = Objects.requireNonNull(titleType);
        this.primaryTitle = Objects.requireNonNull(primaryTitle);
        this.startYear = Objects.requireNonNullElse(startYear, Optional.empty());
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public String getTitleType() {
        return titleType;
    }

    public String getPrimaryTitle() {
        return primaryTitle;
    }

    public Optional<Integer> getStartYear() {
        return startYear;
    }

    public String[] getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return String.format("Title[id=%s, titleType=%s, primaryTitle=%s, startYear=%s, genre=%s]"
                , id,
                titleType,
                primaryTitle,
                startYear.map(String::valueOf).orElse("-"),
                Arrays.toString(genre));
    }
}
