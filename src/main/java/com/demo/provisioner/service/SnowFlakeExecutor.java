package com.demo.provisioner.service;

import com.demo.provisioner.util.SFProvisionerUtil;
import com.demo.provisioner.vo.PackageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnowFlakeExecutor implements ProvisionExecutor{

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Environment evn;

    /**
     * snowflake related provision goes here...
     * @param commands
     * @param tenantId
     * @param packageVO
     */
    @Override
    public void provision(List<String> commands, int tenantId, PackageVO packageVO) {
        //FIXME: autowiring is broken here, need to infer env value from it
        String envid = this.evn.getProperty("env");
        System.out.println("provision snowflake objets for tenant:: "+tenantId+ " for env:: "+envid);
        for(String cmd: commands) {
            String actualCmd = SFProvisionerUtil.doParameterResolution(cmd,packageVO.getParams(),tenantId,envid);
            System.out.println("executing cmd:: "+actualCmd);
            jdbcTemplate.execute(actualCmd);
        }
    }
}