package cloud.kubelab.hermes.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.nio.file.Path;

@ConfigurationProperties("hermes.index")
public class HermesIndexProperties {
    private Resource seedFilePath;
    private Path path;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Resource getSeedFilePath() {
        return seedFilePath;
    }

    public void setSeedFilePath(Resource seedFilePath) {
        this.seedFilePath = seedFilePath;
    }
}
