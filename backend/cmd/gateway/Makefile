.PHONY: all
all: pre fmt lint

.PHONY: pre
pre:
	go mod tidy

.PHONY: fmt
fmt:
	goimports -l -local "github.com/kzmake/grpc-with-eff-example/backend/bankaccount" -w .

.PHONY: lint
lint:
	golangci-lint run
