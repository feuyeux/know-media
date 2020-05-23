package org.feuyeux.knowhow;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.knowhow.domain.Action;
import org.feuyeux.knowhow.service.MediaService;

/**
 * @author feuyeux@gmail.com
 * @date 2019/09/01
 */
@Slf4j
public class KnowMediaMain {

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            log.error("Please input: [source path], [photo path], [video path], and [0 for copy, 1 for move]");
            System.exit(0);
        }
        String srcPath = args[0];
        String photoTargetPath = args[1];
        String videoTargetPath = args[2];
        int action = Integer.parseInt(args[3]);
        if (srcPath.isEmpty() || photoTargetPath.isEmpty() || videoTargetPath.isEmpty()) {
            log.error("Shouldn't give empty input");
            System.exit(0);
        }
        MediaService.execute(srcPath, photoTargetPath, videoTargetPath, action == 0 ? Action.COPY : Action.MOVE);
        log.info("DONE");
    }
}
