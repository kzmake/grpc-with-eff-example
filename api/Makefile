SHELL = /bin/bash

.PHONY: all
all: update lint generate

.PHONY: update
update:
	buf mod update

.PHONY: lint
lint:
	buf lint

.PHONY: generate
generate:
	buf generate
