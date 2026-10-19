package cloud.kubelab.hermes.indexing.adaptor.out.lucene;

import cloud.kubelab.hermes.indexing.domain.Title;
import cloud.kubelab.hermes.shared.document.IndexField;
import org.apache.lucene.document.*;
import org.springframework.stereotype.Component;


@Component
public class TitleDocumentMapper {

    public Document toDocument(Title title) {
        Document doc = new Document();

        doc.add(new StringField(IndexField.ID.fieldName(), title.getId(), Field.Store.YES));
        doc.add(new StringField(IndexField.TITLE_TYPE.fieldName(), title.getTitleType(), Field.Store.YES));
        doc.add(new TextField(IndexField.PRIMARY_TITLE.fieldName(), title.getPrimaryTitle(), Field.Store.YES));

        title.getStartYear().ifPresent(year -> {
            doc.add(new IntPoint(IndexField.START_YEAR.fieldName(), year));
            doc.add(new StoredField(IndexField.START_YEAR.fieldName(), year));
        });

        for (String genre : title.getGenre()) {
            doc.add(new StringField(IndexField.GENRE.fieldName(), genre, Field.Store.YES));
        }

        return doc;
    }
}
