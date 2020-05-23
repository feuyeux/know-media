# know-media
To 
Windows
```sh
SET ACTION_COPY=0
SET ACTION_MOVE=1
SET from_dir=D:/05
SET foto_dir=D:/0523/foto
SET video_dir=D:/0523/video
mvn clean package -DskipTests
java -jar target/know-media-0.0.1-SNAPSHOT.jar %from_dir% %foto_dir% %video_dir% %ACTION_MOVE%
```
Linux
```sh
export ACTION_COPY=0
export ACTION_MOVE=1
export from_dir=/tmp/media
export foto_dir=/tmp/0523/foto
export video_dir=/tmp/0523/video
mvn clean package -DskipTests
java -jar target/know-media-0.0.1-SNAPSHOT.jar ${from_dir} ${foto_dir} ${video_dir} ${ACTION_MOVE}
```
