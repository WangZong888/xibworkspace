package com.jinke.tianyan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Date Author Version Description
 * -----------------------------------------------------------------------------
 * ---- 2019年6月10日 下午1:42:22 wangyue 1.0 To create
 */
@Controller
public class LogController {

    @Autowired
    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate primaryJdbcTemplate;
    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate secondaryJdbcTemplate;

    @RequestMapping("/log")
    public String helloHtml(HashMap<String, Object> map) {

        String sql = "select * from calc_log order by create_time desc";
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list = secondaryJdbcTemplate.queryForList(sql);

        map.put("list", list);
        return "log";
    }
}
