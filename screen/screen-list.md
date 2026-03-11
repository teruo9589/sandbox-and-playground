# 画面一覧

---

## 基本情報

| 項目 | 内容 |
|------|------|
| ドキュメントID | DOC-SCREEN-LIST |
| 作成日 | YYYY-MM-DD |
| 最終更新日 | YYYY-MM-DD |
| バージョン | 1.0.0 |

---

## 関連ドキュメント

| ドキュメント | リンク |
|------------|--------|
| 認証・認可ルール | [screen-transition-auth.md](./screen-transition-auth.md) |
| 遷移設計書（一般ユーザー） | [screen-transition-general.md](./screen-transition-general.md) |
| 遷移設計書（管理者） | [screen-transition-admin.md](./screen-transition-admin.md) |
| 遷移設計書（ゲスト） | [screen-transition-guest.md](./screen-transition-guest.md) |
| 遷移設計書（その他） | [screen-transition-other.md](./screen-transition-other.md) |

---

## 1. 画面一覧

> 画面IDの接頭語ルール:
> - `SCR-XXX` … メイン画面
> - `CMN-XXX` … 共通部品（ヘッダー・フッター・サイドメニューなど）
> - `MDL-XXX` … モーダル

### 1.1 メイン画面

| 画面ID | 画面名 | URL | 認証要否 | 設計書 | 備考 |
|--------|--------|-----|:-------:|--------|------|
| SCR-001 | ログイン画面 | `/login` | 不要 | [設計書](./screens/SCR-001_ログイン画面.md) | |
| SCR-002 | ダッシュボード | `/dashboard` | 必要 | [設計書](./screens/SCR-002_ダッシュボード.md) | ログイン後トップ |
| SCR-003 | 〇〇一覧画面 | `/items` | 必要 | [設計書](./screens/SCR-003_〇〇一覧画面.md) | |
| SCR-004 | 〇〇登録画面 | `/items/new` | 必要 | [設計書](./screens/SCR-004_〇〇登録画面.md) | |
| SCR-005 | 〇〇編集画面 | `/items/:id/edit` | 必要 | [設計書](./screens/SCR-005_〇〇編集画面.md) | |
| SCR-006 | 〇〇詳細画面 | `/items/:id` | 必要 | [設計書](./screens/SCR-006_〇〇詳細画面.md) | |
| SCR-099 | エラー画面 | `/error` | 不要 | [設計書](./screens/SCR-099_エラー画面.md) | 404 / 500 共通 |

### 1.2 共通部品

| 画面ID | 部品名 | 説明 | 設計書 | 備考 |
|--------|--------|------|--------|------|
| CMN-001 | ヘッダー | サイト共通ヘッダー | [設計書](./common/CMN-001_ヘッダー.md) | |
| CMN-002 | フッター | サイト共通フッター | [設計書](./common/CMN-002_フッター.md) | |
| CMN-003 | サイドメニュー | ナビゲーションメニュー | [設計書](./common/CMN-003_サイドメニュー.md) | |

### 1.3 モーダル

| 画面ID | モーダル名 | 説明 | 設計書 | 備考 |
|--------|-----------|------|--------|------|
| MDL-001 | 削除確認モーダル | 削除実行前の確認ダイアログ | [設計書](./modals/MDL-001_削除確認モーダル.md) | |
| MDL-002 | セッション切れモーダル | セッション切れ通知ダイアログ | [設計書](./modals/MDL-002_セッション切れモーダル.md) | |

---

## 改訂履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0 | YYYY-MM-DD | 初版作成 |