package cloud.kubelab;

import cloud.kubelab.search.ImdbIndexer;

import java.io.IOException;

public class Main {
    static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java ImdbIndexer <path_to_imdb_tsv_file> [path_to_index_directory]");
            System.exit(1);
        }

        String dataFilePath = args[0];
        // Allow optional override of the index directory, defaulting to "lucene-index"
        String indexDirPath = args.length > 1 ? args[1] : "lucene-index";

        try {
            ImdbIndexer indexer = new ImdbIndexer(indexDirPath);
            indexer.indexFile(dataFilePath);
            indexer.close();
        } catch (IOException e) {
            System.err.println("Fatal error during indexing: " + e.getMessage());
            e.printStackTrace();
        }


    }
}
