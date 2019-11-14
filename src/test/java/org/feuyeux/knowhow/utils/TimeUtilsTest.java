package org.feuyeux.knowhow.utils;

import java.text.ParseException;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TimeUtilsTest {
    @Test
    public void test() throws ParseException {
        Date date = TimeUtils.f0.parse("Sat Jun 16 12:05:24 CST 2018");
        log.info("{}", date);
    }
}
