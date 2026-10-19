# Hermes — Lucene search service
# One artifact, two profiles: writer (indexes) and reader (searches),
# sharing one index directory.

MVN        := ./mvnw
VERSION    := 0.0.1-SNAPSHOT
JAR        := target/hermes-$(VERSION).jar
JVM_OPTS   := --enable-native-access=ALL-UNNAMED
INDEX_DIR  := data/index
SEED_FULL  := data/title.basics.tsv
SEED_SAMPLE:= data/title.basics.sample.tsv
SAMPLE_ROWS:= 10000

.DEFAULT_GOAL := help
.PHONY: help generate build test clean run-reader run-writer bootstrap clean-index sample index-info

help: ## Show available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) \
		| awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}'

generate: ## Regenerate API interfaces and DTOs from api/*.yaml
	rm -rf target/generated-sources
	$(MVN) generate-sources

build: ## Compile and package (runs generate first)
	$(MVN) package -DskipTests

test: ## Run the test suite
	$(MVN) test

clean: ## Remove build output
	$(MVN) clean

run-reader: build ## Start the reader on :8081
	java $(JVM_OPTS) -jar $(JAR) --spring.profiles.active=reader

run-writer: build ## Start the writer on :8082
	java $(JVM_OPTS) -jar $(JAR) --spring.profiles.active=writer

bootstrap: build ## Run writer bootstrap once and exit (no web server)
	java $(JVM_OPTS) -jar $(JAR) \
		--spring.profiles.active=writer \
		--spring.main.web-application-type=none

index-info: ## Show what is currently in the index directory
	@ls -la $(INDEX_DIR) 2>/dev/null || echo "no index at $(INDEX_DIR)"

clean-index: ## Delete the index so the next writer run rebuilds it
	rm -rf $(INDEX_DIR)
	@echo "removed $(INDEX_DIR) — next writer run will bootstrap"

sample: ## Rebuild the seed sample from the full IMDb file
	head -n $$(($(SAMPLE_ROWS) + 1)) $(SEED_FULL) > $(SEED_SAMPLE)
	@wc -l < $(SEED_SAMPLE) | xargs echo "rows in sample (incl. header):"
