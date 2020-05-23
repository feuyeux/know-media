#!/usr/bin/env bash
export ACTION_COPY=0
export ACTION_MOVE=1
export from_dir=/tmp/media
export foto_dir=/tmp/0523/foto
export video_dir=/tmp/0523/video
mvn clean package -DskipTests
java -jar target/know-media-0.0.1-SNAPSHOT.jar ${from_dir} ${foto_dir} ${video_dir} ${ACTION_COPY}