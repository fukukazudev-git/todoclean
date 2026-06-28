-- ポートフォリオ用サンプルデータ
-- 各 INSERT は「テーブルが空のときだけ」実行されるようにして、再起動で重複しないようにする。
-- 列: title, description, done(完了フラグ), version(楽観ロック用=0), created_at(NOT NULL), due_date

-- 未完了・期限が近い
INSERT INTO todo_entity (title, description, done, version, created_at, due_date)
SELECT 'ポートフォリオを公開する', 'GitHubにリポジトリを整備しREADMEを書く', false, 0, NOW(), '2026-07-01'
WHERE NOT EXISTS (SELECT 1 FROM todo_entity);

-- 未完了・期限切れ（赤表示などの確認用）
INSERT INTO todo_entity (title, description, done, version, created_at, due_date)
SELECT 'Dockerの学習', 'docker-composeでアプリとDBを起動できるようにする', false, 0, NOW(), '2026-06-20'
WHERE NOT EXISTS (SELECT 1 FROM todo_entity WHERE title = 'Dockerの学習');

-- 未完了・先の予定
INSERT INTO todo_entity (title, description, done, version, created_at, due_date)
SELECT '次のアプリの設計', '要件定義と画面遷移図を作成する', false, 0, NOW(), '2026-08-15'
WHERE NOT EXISTS (SELECT 1 FROM todo_entity WHERE title = '次のアプリの設計');

-- 完了済み
INSERT INTO todo_entity (title, description, done, version, created_at, due_date)
SELECT 'CRUD機能の実装', '作成・一覧・編集・削除を一通り実装する', true, 0, NOW(), '2026-06-10'
WHERE NOT EXISTS (SELECT 1 FROM todo_entity WHERE title = 'CRUD機能の実装');

-- 完了済み
INSERT INTO todo_entity (title, description, done, version, created_at, due_date)
SELECT '検索・絞り込み機能', 'キーワード検索と完了/未完了フィルタを追加する', true, 0, NOW(), '2026-06-18'
WHERE NOT EXISTS (SELECT 1 FROM todo_entity WHERE title = '検索・絞り込み機能');
