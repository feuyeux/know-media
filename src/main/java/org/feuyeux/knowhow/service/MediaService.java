package org.feuyeux.knowhow.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.feuyeux.knowhow.domain.Action;
import org.feuyeux.knowhow.domain.MediaInfo;
import org.feuyeux.knowhow.utils.MediaCoon;
import org.feuyeux.knowhow.utils.MetaCoon;
import org.feuyeux.knowhow.utils.ThreadCoon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author feuyeux@gmail.com
 * @date 2019/09/01
 */
@Slf4j
public class MediaService {
    private static final int BATCH_SIZE = 10000;
    private static final List<String> processedFiles = new CopyOnWriteArrayList<>();

    /**
     * @param srcPath         source path of media
     * @param photoTargetPath target path of photo
     * @param videoTargetPath target path of video
     * @param action          how to process
     */
    public static void execute(String srcPath, String photoTargetPath, String videoTargetPath, Action action) {
        try {
            String metaDir = srcPath + File.separator + "meta";
            Path metaPath = Paths.get(metaDir);
            if (Files.exists(metaPath)) {
                FileUtils.forceDelete(new File(metaDir));
            }
            Files.createDirectories(metaPath);
            Path path = Paths.get(srcPath);
            List<String> lines = new ArrayList<>();
            List<String> ignores = new ArrayList<>();
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                int exeSeq, metaSeq, batchSeq;

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
                    if (!attr.isDirectory()) {
                        log.info(++exeSeq + "." + file);
                        MediaInfo mediaInfo = MetaCoon.parseFile(file, attr);
                        if (mediaInfo != null) {
                            String targetPath = mediaInfo.isPhoto() ? photoTargetPath : videoTargetPath;
                            /*去重条件*/
                            String key = mediaInfo.getName() + mediaInfo.getOriginalTime() + mediaInfo.getSize();
                            key += mediaInfo.getMd5();
                            if (!processedFiles.contains(key)) {
                                processedFiles.add(key);
                                if (action == Action.COPY) {
                                    ThreadCoon.submit(() -> {
                                        try {
                                            MediaCoon.needToCopy(mediaInfo, targetPath, targetPath != null);
                                        } catch (IOException e) {
                                            log.error("Fail to copy", e);
                                        }
                                    });

                                } else if (action == Action.MOVE) {
                                    ThreadCoon.submit(() -> {
                                        try {
                                            MediaCoon.needToMove(mediaInfo, targetPath, targetPath != null);
                                        } catch (IOException e) {
                                            log.error("Fail to move", e);
                                        }
                                    });
                                } else {
                                    log.info("Check Only: mediaInfo={},targetPath={}", mediaInfo, targetPath);
                                }
                                lines.add(mediaInfo.output());
                                if (++batchSeq >= BATCH_SIZE) {
                                    Path metaFilePath = Paths.get(metaPath + File.separator + "meta_" + ++metaSeq + ".txt");
                                    Files.write(metaFilePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                                    lines.clear();
                                    batchSeq = 0;
                                }
                            } else {
                                log.info("\nIgnore file:" + file);
                                ignores.add(file.toString());
                            }
                        }
                    }
                    return super.visitFile(file, attr);
                }
            });
            Files.write(Paths.get(metaPath + File.separator + "meta_latest.txt"), lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE);
            Files.write(Paths.get(metaPath + File.separator + "ignored" + ".txt"), ignores, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE);
            lines.clear();
            log.info("Collect knowledge path[" + path + "] Successfully.");
        } catch (IOException e) {
            log.error("", e);
        }
    }
}