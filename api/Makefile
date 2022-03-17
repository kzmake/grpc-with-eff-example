SHELL = /bin/bash

.PHONY: all
all: pre update lint generate

.PHONY: pre
pre:
	rm -rf ./target/

.PHONY: update
update:
	buf mod update

.PHONY: lint
lint:
	buf lint

.PHONY: generate
generate:
	buf generate
