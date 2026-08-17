# Learning Bank

金融アプリケーションの基本的な設計、認証、トランザクション制御を学ぶためのインターネットバンキング・デモです。実在する金融機関やサービスとは関係なく、実際の資金・個人情報には使用できません。

このプロジェクトは、一般的な銀行機能の要件から独立して新規実装されています。

## 機能

- フォームログインとBCryptパスワード
- 所有口座の一覧・残高照会
- 入金・出金・口座間振込
- 取引履歴
- 管理者専用ダッシュボード
- 顧客検索・登録・連絡先・利用状態管理
- 普通預金・貯蓄預金の口座開設
- 口座の利用中・凍結・解約状態管理
- 所有権チェック、残高チェック、CSRF対策
- 悲観ロックとDBトランザクションによる残高更新
- Flywayによるスキーマ管理

## 起動

必要環境はJava 21以上です。Maven Wrapperを同梱しています。

```bash
./mvnw spring-boot:run
```

`http://localhost:8080` を開き、顧客画面は `alice` / `demo-pass`、管理画面は `admin` / `admin-pass` でログインします。Bobの振込先デモ口座は `1200013` です。データは `./data` に保存されます。

管理画面は `http://localhost:8080/admin` です。管理者と顧客はSpring Securityのロールで分離されています。

デモ銀行は「はと銀行」（銀行コード `0200`）です。支店コードは3桁です。口座番号は支店・科目別に自動採番し、科目番号帯1桁、店別・科目別連番5桁、Luhn（Mod 10）方式のチェックデジット1桁を合わせた7桁で表現します。普通預金は1番帯、貯蓄預金は2番帯で、採番済み番号は再使用しません。

## アーキテクチャ

```mermaid
flowchart LR
    Browser --> Security[Spring Security]
    Security --> Customer[顧客向けMVC]
    Security --> Admin[管理者向けMVC]
    Customer --> Service[BankingService]
    Admin --> AdminService[AdminService]
    Service --> Repository[Spring Data JPA]
    AdminService --> Repository
    Repository --> DB[(H2)]
    Flyway --> DB
```

## 公開前の注意

これは学習用MVPです。本番の金融サービスに必要な本人確認、監査ログ、二要素認証、不正検知、振込承認、レート制限、秘密情報管理、障害復旧などは実装していません。

## License

MIT License。第三者依存ライブラリには、それぞれのライセンスが適用されます。
