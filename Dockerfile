FROM eclipse-temurin:17 AS builder
WORKDIR workspace
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} catalog-service.jar
# 계층 JAR 모드를 적용해 아카이브에서 계층 추출
RUN java -Djarmode=layertools -jar catalog-service.jar extract


FROM eclipse-temurin:17
# 'spring' 이라는 이름의 유저 생성
RUN useradd spring
# 'spring'을 현재 유저로 설정
USER spring
WORKDIR workspace
# 첫 단계에서 추출한 JAR 계층을 두 번째 단계로 복사
COPY --from=builder workspace/dependencies/ ./
COPY --from=builder workspace/spring-boot-loader/ ./
COPY --from=builder workspace/snapshot-dependencies/ ./
COPY --from=builder workspace/application/ ./
# 스프링 부트 런처를 사용해 우버 JAR 대신 계층으로 애플리케이션을 시작한다.
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
