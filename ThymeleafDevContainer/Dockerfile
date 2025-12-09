# 本番環境用Dockerfile（マルチステージビルド）

# ビルドステージ
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# ビルドファイルをコピー
COPY build.gradle settings.gradle ./
COPY gradle gradle

# ソースコードをコピー
COPY src src

# アプリケーションをビルド（テストはスキップ）
RUN gradle clean build -x test --no-daemon

# 実行ステージ
FROM eclipse-temurin:21-jre

WORKDIR /app

# ビルドステージからJARファイルをコピー
COPY --from=builder /app/build/libs/*.jar app.jar

# ポート8080を公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]
