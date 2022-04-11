package com.demo.provisioner.service;
import com.demo.provisioner.app.GithubClient;
import com.demo.provisioner.constants.ProvisionerConstants;
import com.demo.provisioner.util.SFProvisionerUtil;
import com.demo.provisioner.vo.PackageVO;
import com.demo.provisioner.vo.TrigramVO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SnowFlakeExecutor implements ProvisionExecutor {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Environment evn;

    @Autowired
    GithubClient githubClient;

    @Autowired
    TrigramVO trigramVO;

    /**
     * snowflake related provision goes here...
     * @param commands
     * @param tenantId
     * @param packageVO
     */
    @Override
    public void provision(List<String> commands, int tenantId, PackageVO packageVO) {
        String envid = evn.getProperty("env");
        System.out.println("provision snowflake objets for tenant:: "+tenantId+ " for env:: "+envid);
        for(String cmd: commands) {
            String actualCmd = SFProvisionerUtil.doParameterResolution(cmd,packageVO.getParams(),tenantId,envid);
            System.out.println("executing cmd:: "+actualCmd);
            jdbcTemplate.execute(actualCmd);
        }
        //make it a flag based check for now, in-order to avoid doing it for every client
        if(!trigramVO.isTrigramProvisioned()) {
            System.out.println("provisioning trigram table:: --------------start-------------");
            provisionTrigramTables(evn.getProperty("trigram.default.file.name"));
            provisionTrigramTables(evn.getProperty("trigram.turkish.file.name"));
            System.out.println("provisioning trigram table:: ----------------end---------------");
            trigramVO.setTrigramProvisioned(true);
        }
    }

    private void provisionTrigramTables(String trigramFile1Name) {
        //1. read file from githbub and get the data to be inserted
        //2. create table and insert the data
        //3. set global-trigram-flag to true
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("env",evn.getProperty("env"));
        paramMap.put("table_name",FilenameUtils.removeExtension(trigramFile1Name));
        List<Object[]> input = githubClient.getDataForTrigramTable(trigramFile1Name);
      //  MessageFormat.format("A1SF_DB_{0}_{1}", evn.getProperty("env"), 0);
        String tenant0DbaseName =   SFProvisionerUtil.getTenantZeroDBName(evn.getProperty("env"),0);
        Map<String, List<String>> packageFileAsJSON = githubClient.readPackage("trigram.pkg", ProvisionerConstants.PAYLOAD_KEY_PACKAGES);
        if(packageFileAsJSON.containsKey(ProvisionerConstants.SNOWFLAKE)) {
            List<String> trigramCmds = packageFileAsJSON.get(ProvisionerConstants.SNOWFLAKE);
            for (String cmd : trigramCmds) {
                String actualCmd = SFProvisionerUtil.doParameterResolution(cmd, paramMap, ProvisionerConstants.TENANT_ZERO_ID, evn.getProperty("env"));
                System.out.println("executing cmd:: " + actualCmd);
                jdbcTemplate.execute(actualCmd);
            }
            //INSERT VALUES IN TRIGRAM TABLES IN BATCH
            System.out.println("batch-insert into table:: "+trigramFile1Name);
            jdbcTemplate.batchUpdate(String.format("INSERT INTO %s.PUBLIC.%s(gram, cnt) values(?, ?)",tenant0DbaseName,
                    FilenameUtils.removeExtension(trigramFile1Name)),
                    input, 1000, this::setParameters);
        }else {
            try {
                throw new Exception("executor type mismatch... ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(MessageFormat.format("Trigram Table Provisioned:: {0}", FilenameUtils.removeExtension(trigramFile1Name)));
    }

    private void setParameters(PreparedStatement ps, Object[] argument) throws SQLException {
        if (argument.length > 2) {
            throw new SQLException("Found more than two columns in CSV" + Arrays.asList(argument));
        }
        ps.setString(1, removeBOM((String) argument[0]));
        ps.setLong(2, (long) argument[1]);
    }

    private static String removeBOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }
}