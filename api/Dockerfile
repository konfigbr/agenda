# ## A PARTIR DA IMAGEM openjdk:8-jdk-alpine (DANDO O NOME DE build)
# FROM openjdk:8-jdk-alpine as build

# ## Setando a pasta de trabalho no container como /app
# WORKDIR /app

# ## copiando os arquivos do maven para o container
# COPY mvnw .
# COPY .mvn .mvn

# # enviar o pom.xml para o container
# COPY pom.xml .

# # CACHE DAS DEPENDENCIAS 
# RUN ./mvnw dependency:go-offline -B

# # COPIA O ARQUIVO SRC 
# COPY src src

# RUN ./mvnw package -DskipTests
# RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# FROM openjdk:8-jre-alpine

# ARG DEPENDENCY=/app/target/dependency

# COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
# COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
# COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# ENTRYPOINT ["java","-cp","app:app/lib/*","br.com.optdoc.Application"]
FROM maven:3.5-jdk-8