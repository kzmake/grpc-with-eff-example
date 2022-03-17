SHELL = /bin/bash

.PHONY: all
all: api

.PHONY: api
api:
	make -C api all

.PHONY: build
build:
	skaffold build

.PHONY: dev
dev:
	skaffold dev

.PHONY: http
http:
	curl -i -X POST http://localhost:8080/v1/accounts -H "principal: alice"
	:
	curl -i -X GET http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111 -H "principal: alice"
	:
	curl -i -X GET http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111 -H "principal: bob"

.PHONY: grpc
grpc:
	grpcurl -H "principal: alice" \
		-plaintext localhost:50051 banking.v1.BankingService/CreateAccount
	:
	grpcurl -H "principal: alice" -d '{"id": "11111111-1111-1111-1111-111111111111"}' \
		-plaintext localhost:50051 banking.v1.BankingService/GetAccount
	:
	grpcurl -H "principal: bob" -d '{"id": "11111111-1111-1111-1111-111111111111"}' \
		-plaintext localhost:50051 banking.v1.BankingService/GetAccount
