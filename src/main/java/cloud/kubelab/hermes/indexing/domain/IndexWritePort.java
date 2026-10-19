package cloud.kubelab.hermes.indexing.domain;

import java.util.stream.Stream;

public interface IndexWritePort {
    long addAll(Stream<Title> titles);
    void add(Title title);
}
