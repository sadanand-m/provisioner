package com.demo.provisioner.factory.impl;

import com.demo.provisioner.factory.intraface.ProvisionExecutor;
import com.demo.provisioner.util.SFProvisionerUtil;
import com.demo.provisioner.vo.PackageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnowFlakeExecutor implements ProvisionExecutor {

   /* Connection sfConnection = null;
    Statement statement = null;*/
//
//    @Autowired
//    JdbcTemplate jdbcTemplate;

    @Autowired
    Environment env;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    /**
     * snowflake related provision goes here...
     * @param commands
     * @param tenantId
     * @param packageVO
     */
    @Override
    public void provision(List<String> commands, int tenantId, PackageVO packageVO) {
        //FIXME: autowiring is broken here, need to infer env value from it
        int envid = 104;
        System.out.println("provision snowflake objets for tenant:: "+tenantId+ " for env:: "+envid);
        SFProvisionerUtil util = new SFProvisionerUtil();
        jdbcTemplate = util.getJdbcTemplate();
        for(String cmd: commands){
            String actualCmd = SFProvisionerUtil.doParameterResolution(cmd,packageVO.getParams(),tenantId);
            System.out.println("executing cmd:: "+actualCmd);
            jdbcTemplate.execute(actualCmd);
        }
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
