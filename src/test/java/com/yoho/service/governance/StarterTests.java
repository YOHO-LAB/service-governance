package com.yoho.service.governance;

import com.yoho.service.governance.influxDb.InfluxMapper;
import org.influxdb.dto.QueryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StarterTests {
    @Autowired
    InfluxMapper serviceCallMapper;


    @Test
    public void contextLoads() {
        QueryResult qCloud = serviceCallMapper.getByServiceName("qCloud", "bigdata.getHotSearchWord");
        System.out.println(qCloud);
    }


    @Test
    public void test() {
        QueryResult aaa = serviceCallMapper.getByServiceName("", "");
        System.out.println(aaa);

    }


}
