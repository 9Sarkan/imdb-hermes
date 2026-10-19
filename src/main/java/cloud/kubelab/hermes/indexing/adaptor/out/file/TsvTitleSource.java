package cloud.kubelab.hermes.indexing.adaptor.out.file;

import cloud.kubelab.hermes.config.HermesIndexProperties;
import cloud.kubelab.hermes.indexing.domain.DocumentSourcePort;
import cloud.kubelab.hermes.indexing.domain.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Component
@Profile("writer")
public class TsvTitleSource implements DocumentSourcePort {
    private static final Logger log = LoggerFactory.getLogger(TsvTitleSource.class);

    private static final String NULL_MARKER = "\\N";
    private static final String EXPECTED_HEADER =
            "tconst\ttitleType\tprimaryTitle\toriginalTitle\tisAdult"
                    + "\tstartYear\tendYear\truntimeMinutes\tgenres";
    private static final int EXPECTED_COLUMNS = 9;

    private static final int COL_TCONST = 0;
    private static final int COL_TITLE_TYPE = 1;
    private static final int COL_PRIMARY_TITLE = 2;
    private static final int COL_START_YEAR = 5;
    private static final int COL_GENRES = 8;

    private final Resource seedFile;

    public TsvTitleSource(HermesIndexProperties properties) {
        this.seedFile = properties.getSeedFilePath();
    }

    @Override
    public Stream<Title> titles() {
        BufferedReader reader = openReader();
        try {
            validateHeader(reader);
        } catch (RuntimeException e) {
            close(reader);
            throw e;
        }

        AtomicLong skipped = new AtomicLong();
        return reader.lines()
                .map(line -> parse(line, skipped))
                .flatMap(Optional::stream)
                .onClose(() -> {
                    if (skipped.get() > 0){
                        log.warn("Skipped {} malformed rows in {}", skipped.get(), seedFile.getFilename());
                    }
                    close(reader);
                });
    }

    private BufferedReader openReader() {
        if (!this.seedFile.exists()) {
            throw new IllegalStateException("seed file does not exist!");
        }

        try {
            return new BufferedReader(
                    new InputStreamReader(seedFile.getInputStream(), StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            throw new UncheckedIOException("failed to open seed file", e);
        }
    }

    private void validateHeader(BufferedReader reader){
       String header;

       try {
           header = reader.readLine();
       } catch (IOException e) {
           throw new UncheckedIOException("failed to read header", e);
       }

       if (header == null) {
           throw new IllegalStateException("Seed file is empty!" + seedFile);
       }

       if (!EXPECTED_HEADER.equals(header)) {
           throw new IllegalStateException(String.format("Unexpected seed file header. Expected:%n  %s%nbut was:%n  %s"
                           ,EXPECTED_HEADER, header));
       }
    }

    private Optional<Title> parse(String line, AtomicLong skipped) {
        String[] columns = line.split("\t", -1);

        if (columns.length != EXPECTED_COLUMNS) {
            skipped.incrementAndGet();
            log.debug("Malformed row ({} columns): {}", columns.length, line);
            return Optional.empty();
        }

        return Optional.of(new Title(
                columns[COL_TCONST],
                nullable(columns[COL_TITLE_TYPE]),
                nullable(columns[COL_PRIMARY_TITLE]),
                parseStartYear(columns[COL_START_YEAR]),
                parseGenre(columns[COL_GENRES])
        ));
    }

    private static String[] parseGenre(String value) {
        return NULL_MARKER.equals(value) ? new String[0] : value.split(",");
    }

    private static Optional<Integer> parseStartYear(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException ignored){
            return Optional.empty();
        }
    }

    private static String nullable(String value) {
        return NULL_MARKER.equals(value) ? null : value;
    }

    private static void close (BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            throw new UncheckedIOException("failed to close reader", e);
        }
    }
}
