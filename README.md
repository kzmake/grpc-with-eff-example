# gRPCアプリケーション with Eff with 表明プログラミング

## これは何？

[ScalaMatsuri2022](https://scalamatsuri.org/ja)にてgRPCアプリケーションをライブコーディングするための課題プロジェクトです。

下記のみなさんの発表で取り上げられているエッセンスを、@kzmakeの解釈のもとgRPCアプリケーションとして実装したサンプルとなります。

- [Scalaで始める表明プログラミング](https://speakerdeck.com/dnskimo/scaladeshi-merubiao-ming-puroguramingu) by @dnskimo
- [Eff（atnos-eff）による実践的なコーディング集](https://speakerdeck.com/shiroichi315/eff-atnos-eff-niyorushi-jian-de-nakodeinguji) by @shiroichi315
- [AlpのEff独自Effect集](https://scalamatsuri.org/ja/proposals/J1647668100) by @ma2k8

> ⚠ 当コードは架空のサービスを想定したものであり、所属先とは全く関係がありません。

## エクササイズとしてコーディングしてみる

```bash
git clone -b exercise https://github.com/kzmake/grpc-with-eff-example
```
よりお試しいただけます！

1. お金の引き出し(WithdrawMoney)のAPIを実装 
   - WithdrawMoneyの実装
     - ユースケース層: WithdrawMoneyInteractorの実装
     - ドメイン層: Accountの実装
       - 表明プログラミング
2. IdGen(Id生成)エフェクトのインタープリター修正
   - インタープリターの修正
     - アダプター層: timestampなID採番 -> uuidなID採番
3. AuthZ(認可)エフェクトの実装
   - 独自のAuthZ(認可)エフェクト追加
     - ドメイン層: エフェクトの定義
     - アダプター層: インタープリターの実装
     - アダプター層: リポジトリの実装修正

> 🔔 3 の認可は簡易化のため `-H "principal: alice"` として認可対象を指定できるものとします。

## このアプリケーションについて

架空のサービスとして銀行口座サービスを想定します。

銀行口座(`Account`)はエンティティであるとともに集約(AggregateRoot)を想定し、

```scala
final case class Account(id: Id[Account], balance: Money) extends AggregateRoot[Account]
```

のように、

- id: 銀行口座の識別子
- balance: 残高

を持つものとします。

サービスが提供するAPIを利用し、ユーザーは、

- 銀行口座 を 作成する
- 銀行口座 を 取得する
- 銀行口座 に お金 を 預け入る
- 銀行口座 から お金 を 引き出す
- 銀行口座 を 削除する

ことができるものとします。

### ディレクトリ構成

```bash
$ tree
.
├── api
│   ├── banking/v1/banking.proto # 提供する api(grpc) の定義
│   └── gen
│       ├── go                   # `make api` による自動生成物: gateway用のgolangコード
│       └── openapiv2            # `make api` による自動生成物: gatewayで提供されるswagger
│
└── backend
    ├── banking                  # 銀行口座サービスコンテキスト
    │   ├── domain               # ドメイン層
    │   ├── usercase             # ユースケース層
    │   ├── adapter              # アダプター層
    │
    └── cmd                      # アプリケーションのエントリーポイント
        ├── grpc                 # 銀行口座サービス
        └── gateway              # json api を提供する grpc-gateway
```

### APIについて

buf.build / akka-grpc(scalapb) を用いて、

- [API仕様(openapiv2)](https://github.com/kzmake/grpc-with-eff-example/blob/main/api/gen/openapiv2/banking/v1/banking.swagger.json): buf generate実行時
- [gatewayのためのコード(golang + grpc-gateway)](https://github.com/kzmake/grpc-with-eff-example/tree/main/api/gen/go/banking/v1): buf generate実行時
- [bankingのためのコード(scala)](https://doc.akka.io/docs/akka-grpc/current/overview.html): sbt compile実行時
- [バリデーションコード(protoc-gen-validate with scalapb)](https://scalapb.github.io/docs/validation/): sbt compile実行時

を生成しています。

### 設計について

- クリーンアーキテクチャ
- ヘキサゴナルアーキテクチャ
- ドメイン駆動設計(DDD)

のエッセンスを取り入れ、 `backend/banking` 配下にレイヤーを、

1. ドメイン層: `banking-domain`
2. ユースケース層: `banking-usecase`
3. アダプター層: `banking-adapter`

とモジュール化しています。

### ローカルでアプリケーションを起動してみる

```bash
$ make dev
```

### リクエストしてみる

```bash
$ curl -i http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111 -H "principal: alice"
HTTP/1.1 200 OK
Content-Type: application/json
Grpc-Metadata-Content-Length: 48
Grpc-Metadata-Content-Type: application/grpc+proto
Grpc-Metadata-Date: Fri, 18 Mar 2022 14:17:47 GMT
Grpc-Metadata-Server: akka-http/10.2.9
Date: Fri, 18 Mar 2022 14:17:47 GMT
Content-Length: 74

{"account":{"id":"11111111-1111-1111-1111-111111111111","balance":"1200"}}
```

## 参考

- https://github.com/atnos-org/eff
- https://atnos-org.github.io/eff/
- https://www.exoego.net/eff/
- https://doc.akka.io/docs/akka-grpc/current/overview.html
- https://docs.buf.build/introduction
- https://scalapb.github.io/docs/validation/
- [Scalaで始める表明プログラミング](https://speakerdeck.com/dnskimo/scaladeshi-merubiao-ming-puroguramingu) by @dnskimo
- [Eff（atnos-eff）による実践的なコーディング集](https://speakerdeck.com/shiroichi315/eff-atnos-eff-niyorushi-jian-de-nakodeinguji) by @shiroichi315
- [AlpのEff独自Effect集](https://scalamatsuri.org/ja/proposals/J1647668100) by @ma2k8
