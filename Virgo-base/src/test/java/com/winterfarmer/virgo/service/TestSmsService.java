package com.winterfarmer.virgo.service;

import com.winterfarmer.virgo.base.service.SmsService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by yangtianhang on 15-4-15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/virgo-test-base-context.xml")
public class TestSmsService {
    @Resource
    SmsService smsService;

    @Test
    public void testSms() {
        System.out.println(smsService.sendSms("13269937228", "test"));
    }
}
