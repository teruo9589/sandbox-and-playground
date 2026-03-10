# 認証・認可ルール

---

## 基本情報

| 項目 | 内容 |
|------|------|
| ドキュメントID | DOC-TRANSITION-AUTH |
| 作成日 | YYYY-MM-DD |
| 最終更新日 | YYYY-MM-DD |
| バージョン | 1.0 |

---

## 関連ドキュメント

| ドキュメント | リンク |
|------------|--------|
| 画面一覧 | [screen-list.md](./screen-list.md) |
| 遷移設計書（一般ユーザー） | [screen-transition-general.md](./screen-transition-general.md) |
| 遷移設計書（管理者） | [screen-transition-admin.md](./screen-transition-admin.md) |
| 遷移設計書（ゲスト） | [screen-transition-guest.md](./screen-transition-guest.md) |
| 遷移設計書（その他） | [screen-transition-other.md](./screen-transition-other.md) |

---

## 1. 認証・認可ルール

| ルール | 内容 |
|--------|------|
| 未認証アクセス | 認証保護ページへのアクセス時、ログイン画面（SCR-001）へリダイレクト |
| ログイン済みのログイン画面アクセス | ダッシュボード（SCR-002）へリダイレクト |
| 権限不足 | エラー画面（SCR-099）へ遷移し、403 Forbidden を表示 |
| セッション切れ | 次のAPIリクエスト時にログイン画面（SCR-001）へリダイレクト |
| 存在しないページへのアクセス | エラー画面（SCR-099）へ遷移し、404 Not Found を表示 |
| サーバー内部エラー | エラー画面（SCR-099）へ遷移し、500 Internal Server Error を表示 |

---

## 2. ロール定義

| ロールID | ロール名 | 説明 |
|---------|---------|------|
| ROLE-001 | 一般ユーザー | ログイン済みの標準ユーザー |
| ROLE-002 | 管理者 | すべての機能にアクセス可能なユーザー |
| ROLE-003 | ゲスト | 未ログインのユーザー |
| ROLE-004 | その他 | 上記以外の特殊ロールを持つユーザー |

---

## 改訂履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0 | YYYY-MM-DD | 初版作成 |