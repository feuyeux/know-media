package org.feuyeux.knowhow;

import org.feuyeux.knowhow.domain.Action;
import org.feuyeux.knowhow.service.MediaService;
import org.junit.Test;

public class MediaServiceTest {
    @Test
    public void test() {
        Action action = Action.MOVE;
        String srcPath = "D:/2020-05";
        String photoTargetPath = "D:/0523/foto";
        String videoTargetPath = "D:/0523/video";
        MediaService.execute(srcPath, photoTargetPath, videoTargetPath, action);
    }
}
