package com.demo.provisioner.processor;

import com.demo.provisioner.app.GithubClient;
import com.demo.provisioner.factory.ExecutorFactory;
import com.demo.provisioner.factory.intraface.ProvisionExecutor;
import com.demo.provisioner.vo.PackageVO;
import net.snowflake.client.jdbc.internal.amazonaws.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProvisionProcessor {

    @Autowired
    private Environment env;

    @Autowired
    GithubClient githubClient;

    /**
     * read the package definition from github and convert to JSONMap
     * @param tenantIdList
     * @param packageFileName
     * @param packageVO
     */
        public void processRequest(List<Object> tenantIdList, String packageFileName, PackageVO packageVO) {
        if(CollectionUtils.isNullOrEmpty(tenantIdList)) {
            System.out.println("tenant list is empty-nothing to process: exiting ");
            return;
        }
        Map<String, List<String>> packageFileAsJSON = githubClient.readPackage(packageFileName);
        processPackage(packageFileAsJSON,tenantIdList,packageVO);
    }

    /**
     * execute provisioning for each tenant
     * @param packageFileAsJson
     * @param tenantIdList
     * @param packageVO
     */
    private void processPackage(Map<String, List<String>> packageFileAsJson,
                                List<Object> tenantIdList, PackageVO packageVO) {
        // for each tenant
        // read, the input cmd frpm the json map
        // lauch appropriate executor and
       //
        String environmentId = env.getProperty("env");
        for(Object tenantId: tenantIdList) {
            System.out.println("provisioning for tenant: " + tenantId.toString()+ "  for environment:  "+env.getProperty("env"));
            String provisionType = (String) packageFileAsJson.keySet().toArray()[0];
            ProvisionExecutor executor =  ExecutorFactory.getExecutor(provisionType);
            if(executor!=null)
                executor.provision(packageFileAsJson.get(provisionType),(int)tenantId,packageVO); //env.getProperty("env"));
            else
                //TODO: throw custom exception here..
                System.out.println("invalid provision type: no executors found for this type "+provisionType);
        }
    }
}