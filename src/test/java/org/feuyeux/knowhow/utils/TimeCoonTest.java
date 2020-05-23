package org.feuyeux.knowhow.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

@Slf4j
public class TimeCoonTest {
    @Test
    public void test() throws ParseException {
        Date date = TimeCoon.f0.parse("Sat Jun 16 12:05:24 CST 2018");
        log.info("{}", date);
    }
}
