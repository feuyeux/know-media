package org.feuyeux.knowhow.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.mp4.Mp4MetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.knowhow.domain.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.List;

import static com.drew.imaging.FileType.*;

/**
 * @author feuyeux@gmail.com
 * @date 2019/09/01
 */
@Slf4j
public class MetaCoon {
    public static final String SPACE = " ";
    private static final List<String> photoTypes = Arrays.asList(Jpeg.getName(), "JPG", Png.getName(), Gif.getName());
    private static final List<String> videoTypes = Arrays.asList(Mp4.getName(), Mov.getName(), Avi.getName());

    /**
     * 获取元数据信息
     *
     * @param file media
     * @param attr media attr
     * @return media meta
     */
    public static MediaInfo parseFile(Path file, BasicFileAttributes attr) {
        String desc = file.getFileName().toString();
        int i = desc.lastIndexOf(".");
        String typeName = desc.substring(i + 1).toUpperCase();

        MediaInfo mediaInfo;
        if (photoTypes.contains(typeName)) {
            mediaInfo = parsePhoto(file.toFile(), file.getParent().toString(), attr.size());
            if (mediaInfo == null) {
                return null;
            }
        } else if (videoTypes.contains(typeName)) {
            mediaInfo = parseVideo(file.toFile(), file.getParent().toString(), attr.size());
            if (mediaInfo == null) {
                return null;
            }
            mediaInfo.setPhoto(false);
        } else {
            return null;
        }

        FileTime ct = attr.creationTime();
        FileTime mt = attr.lastModifiedTime();
        boolean isBefore = ct.compareTo(mt) < 0;
        FileTime fileTime = isBefore ? ct : mt;

        String originalTime = mediaInfo.getOriginalTime();
        if (originalTime == null) {
            originalTime = fileTime.toString();
            mediaInfo.setOriginalTime(originalTime);
        }
        String stage = TimeCoon.trans(originalTime);
        /*将时间信息转换为类别特征*/
        mediaInfo.setStage(stage);

        String fileName = desc.substring(0, i);
        mediaInfo.setName(fileName + "." + typeName);
        mediaInfo.setExtension(typeName);
        mediaInfo.setDir(file.getParent().toString());
        return mediaInfo;
    }

    private static MediaInfo parseVideo(File file, String dir, long size) {
        MediaInfo result = new MediaInfo();
        result.setDir(dir);
        try {
            Metadata metadata = Mp4MetadataReader.readMetadata(file);
            for (Directory directory : metadata.getDirectories()) {
                directory.getTags().parallelStream().forEach(tag -> parseVideo0(size, tag, result));
            }
            return result;
        } catch (ImageProcessingException | IOException e) {
            log.info(file + "<-" + e.getMessage());
            return null;
        }
    }

    private static void parseVideo0(long size, Tag tag, MediaInfo result) {
        //标签名
        String tagName = tag.getTagName();
        //标签信息
        String desc = tag.getDescription();
                    /*
                    com.drew.metadata.mp4.media.Mp4VideoDirectory
                    com.drew.metadata.mp4.media.Mp4MediaDirectory
                    com.drew.metadata.mp4.Mp4Directory
                    com.drew.metadata.Directory
                     */
        switch (tagName) {
            case "Creation Time":
                result.setOriginalTime(desc);
                break;
            case "File Name":
                int i = desc.lastIndexOf(".");
                String fileName = desc.substring(0, i);
                String typeName = desc.substring(i + 1).toLowerCase();
                result.setName(fileName + "." + typeName);
                result.setExtension(typeName);
                break;
            case "File Size":
                result.setSize(size);
                break;
            case "Duration":
                result.setDurationMills(Integer.parseInt(desc));
                break;
            case "Width":
                result.setWidth(getWh(desc));
                break;
            case "Height":
                result.setHeight(getWh(desc));
                break;
            case "Compression Type":
                result.setCompressionType(desc);
                break;
            case "Horizontal Resolution":
                result.setHResolution(Integer.parseInt(desc));
                break;
            case "Vertical Resolution":
                result.setVResolution(Integer.parseInt(desc));
                break;
            case "GPS Latitude":
                result.setLatitude(pointToLatLong(desc));
                break;
            case "GPS Longitude":
                result.setLongitude(pointToLatLong(desc));
                break;
            default:
        }
    }

    private static MediaInfo parsePhoto(File file, String dir, long size) {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setDir(dir);
        mediaInfo.setSize(size);
        try {
            HashCode md5 = com.google.common.io.Files.asByteSource(file).hash(Hashing.md5());
            String md5Hex = md5.toString();
            mediaInfo.setMd5(md5Hex);
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    //标签名
                    String tagName = tag.getTagName();
                    //标签信息
                    String desc = tag.getDescription();
                    switch (tagName) {
                        case "Date/Time Original":
                            mediaInfo.setOriginalTime(desc);
                            break;
                        case "File Name":
                            int i = desc.lastIndexOf(".");
                            String fileName = desc.substring(0, i);
                            String typeName = desc.substring(i + 1).toLowerCase();
                            mediaInfo.setName(fileName + "." + typeName);
                            mediaInfo.setExtension(typeName);
                            break;
                        case "Exposure Time":
                            mediaInfo.setExposureTime(desc);
                            break;
                        case "F-Number":
                            mediaInfo.setFNumber(desc);
                            break;
                        case "Image Height":
                            mediaInfo.setHeight(getWh(desc));
                            break;
                        case "Image Width":
                            mediaInfo.setWidth(getWh(desc));
                            break;
                        case "GPS Latitude":
                            mediaInfo.setLatitude(pointToLatLong(desc));
                            break;
                        case "GPS Longitude":
                            mediaInfo.setLongitude(pointToLatLong(desc));
                            break;
                        default:
                    }
                }
            }
            return mediaInfo;
        } catch (ImageProcessingException | IOException e) {
            log.info(file + "<-" + e.getMessage());
            return null;
        }
    }

    private static String getWh(String desc) {
        String w;
        if (desc.contains(SPACE)) {
            w = desc.substring(0, desc.indexOf(SPACE));
        } else {
            w = desc;
        }
        return w;
    }

    private static String pointToLatLong(String point) {
        double d = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());
        double f = Double.parseDouble(point.substring(point.indexOf("°") + 1, point.indexOf("'")).trim());
        double m = Double.parseDouble(point.substring(point.indexOf("'") + 1, point.indexOf("\"")).trim());
        double duStr = d + f / 60 + m / 60 / 60;
        return Double.toString(duStr);
    }
}