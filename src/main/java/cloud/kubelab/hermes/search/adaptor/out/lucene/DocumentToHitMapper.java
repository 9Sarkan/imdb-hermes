package cloud.kubelab.hermes.search.adaptor.out.lucene;


import cloud.kubelab.hermes.search.domain.SearchHit;
import cloud.kubelab.hermes.shared.document.IndexField;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DocumentToHitMapper {

    public SearchHit toSearchHit(Document doc, float score) {
        return new SearchHit(
            doc.get(IndexField.ID.fieldName()),
            doc.get(IndexField.TITLE_TYPE.fieldName()),
            doc.get(IndexField.PRIMARY_TITLE.fieldName()),
            startYear(doc),
            List.of(doc.getValues(IndexField.GENRE.fieldName())),
            score
        );
    }

    Optional<Integer> startYear(Document doc) {
       IndexableField field = doc.getField(IndexField.START_YEAR.fieldName());
        return field == null ? Optional.empty() : Optional.of(Integer.parseInt(field.stringValue()));
    }
}
