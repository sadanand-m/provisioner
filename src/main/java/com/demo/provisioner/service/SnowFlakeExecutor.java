package com.demo.provisioner.service;
import com.demo.provisioner.app.GithubClient;
import com.demo.provisioner.util.SFProvisionerUtil;
import com.demo.provisioner.vo.PackageVO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@Service
public class SnowFlakeExecutor implements ProvisionExecutor {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Environment evn;

    @Autowired
    GithubClient githubClient;

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
        if(!false) {
            createTrigramTables(evn.getProperty("trigram.default.file.name"));
            createTrigramTables(evn.getProperty("trigram.turkish.file.name"));
        }

    }

    private void createTrigramTables(String trigramFile1Name) {
        //1. read file from githbub and get the data to be inserted
        //2. create table and insert the data
        //3. set global-trigram-flag to true
        List<Object[]> input = githubClient.getDataForTrigramTable(trigramFile1Name);
        MessageFormat.format("A1SF_DB_{0}_{1}", evn.getProperty("env"), 0);
        String tenant0DbaseName =   SFProvisionerUtil.getTenantZeroDBName(evn.getProperty("env"),0);
        jdbcTemplate.execute("ALTER USER A1SF_USER_104_PROVISIONER SET DEFAULT_ROLE = A1SF_ROLE_104_PROVISIONER");
        jdbcTemplate.execute(MessageFormat.format("USE ROLE A1SF_ROLE_{0}_PROVISIONER;",evn.getProperty("env")));
        jdbcTemplate.execute(MessageFormat.format("USE database {0}",tenant0DbaseName ));
        System.out.println("executing cmd: "+String.format("create or replace table %s( gram  string, cnt int);",
                                                                FilenameUtils.removeExtension(trigramFile1Name)));
        jdbcTemplate.execute(String.format("create or replace table A1SF_DB_104_0.PUBLIC.%s( gram  string, cnt int);",FilenameUtils.removeExtension(trigramFile1Name)));
        System.out.println(MessageFormat.format("Created Trigram tables in {0}.", tenant0DbaseName));
        System.out.println("executing cmd: "+String.format("insert into PUBLIC.%s(gram, cnt) values(?, ?)",FilenameUtils.removeExtension(trigramFile1Name)));
        jdbcTemplate.execute("alter user A1SF_USER_104_PROVISIONER set default_warehouse = A1SF_WH_104_temp");
        jdbcTemplate.batchUpdate(String.format("insert into A1SF_DB_104_0.PUBLIC.%s(gram, cnt) values(?, ?)",FilenameUtils.removeExtension(trigramFile1Name)),
                    input, 1000, this::setParameters);
        System.out.println(MessageFormat.format("Trigram Table Provisioned {0}.", trigramFile1Name));
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