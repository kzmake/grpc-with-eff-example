SHELL = /bin/bash

.PHONY: all
all: api

.PHONY: api
api:
	make -C api all

.PHONY: build
build:
	echo まだ用意してないよ
	# skaffold build

.PHONY: dev
dev:
	echo まだ用意してないよ
	# skaffold dev

.PHONY: http
http:
	curl -i -X POST http://localhost:8080/v1/accounts -H "principal: alice"
	:
	curl -i -X GET http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111 -H "principal: alice"
	:
	curl -i -X POST http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111/withdraw -H "principal: alice" -d '{"money": 1000}'
	:
	curl -i -X POST http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111/deposit -H "principal: alice" -d '{"money": 1000}'
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
