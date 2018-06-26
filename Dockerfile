FROM ccr.ccs.tencentyun.com/yoho-base/spring-boot-1.8:1.0

MAINTAINER jimi <jimi.ji@yoho.cn>

ENV SPRING_PROFILES_ACTIVE prod
ENV XMX 1500M
ENV XMS 1500M

COPY target/service-governance-dashboard*.jar  /home/service-governance-dashboard.jar

VOLUME /Data/logs/governance

EXPOSE 8805

CMD exec java -Xmx$XMX -Xms$XMS  -jar /home/service-governance-dashboard.jar