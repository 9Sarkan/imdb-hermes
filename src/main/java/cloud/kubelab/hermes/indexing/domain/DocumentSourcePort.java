package cloud.kubelab.hermes.indexing.domain;

import java.util.stream.Stream;

public interface DocumentSourcePort {
    Stream<Title> titles();
}
