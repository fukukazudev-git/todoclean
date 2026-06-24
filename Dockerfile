# =========================================================
# ステージ1: ビルド（Maven + JDK17 でjarを作る）
# =========================================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 先に依存定義(pom.xml)だけコピーして依存をダウンロードしておく。
# こうするとソースだけ変更したときに依存DLがキャッシュされ、再ビルドが速くなる。
COPY pom.xml .
RUN mvn -B dependency:go-offline

# ソースをコピーしてjarをビルド（テストはビルド時はスキップ）
COPY src ./src
RUN mvn -B clean package -DskipTests

# =========================================================
# ステージ2: 実行（JREのみの軽量イメージ）
# =========================================================
FROM eclipse-temurin:17-jre
WORKDIR /app

# ビルドステージで作られたjarだけをコピーする
COPY --from=build /app/target/*.jar app.jar

# Spring Bootの待ち受けポート
EXPOSE 8080

# アプリ起動
ENTRYPOINT ["java", "-jar", "app.jar"]
