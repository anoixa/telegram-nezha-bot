FROM openjdk:21

ENV TZ=Asia/Shanghai
ADD telegram-nezha-bot-0.0.1-SNAPSHOT.jar bot.jar
#EXPOSE 8080
ENTRYPOINT ["java","-jar","bot.jar"]