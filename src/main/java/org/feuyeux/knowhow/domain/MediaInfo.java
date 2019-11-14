package org.feuyeux.knowhow.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author feuyeux@gmail.com
 * @date 2019/09/01
 */
@Data
public class MediaInfo implements Serializable {
    private String originalTime;
    private String stage;
    private String dir;
    private String name;
    private String extension;
    private String exposureTime;
    private String fNumber;
    private String width;
    private String height;
    private String latitude;
    private String longitude;
    private int durationMills;
    private int vResolution;
    private int hResolution;
    private long size;
    private String compressionType;
    private boolean isPhoto;
    private String md5;

    public MediaInfo() {
        this.isPhoto = true;
    }

    public String output() {
        return originalTime + "|"
            + stage + "|"
            + dir + "|"
            + name + "|"
            + extension + "|"
            + exposureTime + "|"
            + fNumber + "|"
            + width + "|"
            + height + "|"
            + latitude + "|"
            + longitude + "|"
            + durationMills + "|"
            + vResolution + "|"
            + hResolution + "|"
            + size + "|"
            + compressionType + "|";
    }
}
