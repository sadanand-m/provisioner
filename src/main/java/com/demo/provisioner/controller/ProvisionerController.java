package com.demo.provisioner.controller;

import com.demo.provisioner.processor.ProvisionProcessor;
import com.demo.provisioner.vo.PackageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
//@PropertySource({"classpath:config.properties", "classpath:config002.properties"})
@PropertySource({"classpath:config.properties","classpath:application.properties"})
public class ProvisionerController {

    @Autowired
    private Environment env;

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Autowired
    ProvisionProcessor provisionProcessor;

    Connection sfConnection = null;
    Statement statement = null;

    @RequestMapping("/provision")
    public String provision(@RequestBody Map<String, List<Object>> request) {
        System.out.println("input payload:: "+request);
         List<Object> tenantIdList = request.get("tenantid");
         PackageVO packageVO = null;
        List<PackageVO> packagesList = new ArrayList<>();
        //iterate over these list of maps & create packageVO objects
         for(Object obj: request.get("packages")) {
            Map<String, Object> temp = (Map<String, Object>) obj;
             //convert to vo
              packageVO = new PackageVO();
              packageVO.setPackageName(temp.get("packageName").toString());
              packageVO.setParams((Map<String, String>) temp.get("params"));
             packagesList.add(packageVO);
             System.out.println("provision package:: "+temp.get("packageName"));
             provisionProcessor.processRequest(tenantIdList, (String) temp.get("packageName"),packageVO);
         }
        //TODO: Validator validate input-request, else throw error
        //TODO: return meaningful exit codes
        System.out.println("provisioning done...");
        return "provisioning done...";
    }
}