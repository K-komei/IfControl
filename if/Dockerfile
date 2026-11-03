FROM alpine:3.18
RUN apk add --no-cache bash postgresql-client tzdata && \
    cp /usr/share/zoneinfo/Asia/Tokyo /etc/localtime
WORKDIR /app
# 常駐用コマンド
CMD ["tail", "-f", "/dev/null"]