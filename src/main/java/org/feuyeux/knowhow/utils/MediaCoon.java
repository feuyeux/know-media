package org.feuyeux.knowhow.utils;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.knowhow.domain.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author feuyeux@gmail.com
 * @date 2019/09/01
 */
@Slf4j
public class MediaCoon {
    public static void needToCopy(MediaInfo photoInfo, String dir, boolean isNeed) throws IOException {
        if (isNeed) {
            MediaAction mediaAction = new MediaAction(photoInfo, dir).invoke();
            Path source = mediaAction.getSource();
            Path target = mediaAction.getTarget();
            log.info("Copy source={},target={}", source, target);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void needToMove(MediaInfo photoInfo, String dir, boolean isNeed) throws IOException {
        if (isNeed) {
            MediaAction mediaAction = new MediaAction(photoInfo, dir).invoke();
            Path source = mediaAction.getSource();
            Path target = mediaAction.getTarget();
            log.info("Move source={},target={}", source, target);
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            Path parent = source.getParent();
            long count = Files.list(parent).count();
            if (count == 0) {
                Files.delete(parent);
            }
        }
    }

    private static class MediaAction {
        private final MediaInfo photoInfo;
        private final String dir;
        private Path source;
        private Path target;

        public MediaAction(MediaInfo photoInfo, String dir) {
            this.photoInfo = photoInfo;
            this.dir = dir;
        }

        public MediaAction invoke() throws IOException {
            String name = photoInfo.getName();
            String newName = name.replaceAll(" ", "-");
            source = Paths.get(photoInfo.getDir(), File.separator, name);
            String folder = dir + File.separator + photoInfo.getStage();
            Path targetFolder = Paths.get(folder);
            target = Paths.get(folder + File.separator + newName);
            if (!Files.exists(targetFolder)) {
                Files.createDirectories(targetFolder);
            }
            return this;
        }

        public Path getSource() {
            return source;
        }

        public Path getTarget() {
            return target;
        }

    }
}
