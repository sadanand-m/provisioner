package com.demo.provisioner.factory;

import com.demo.provisioner.constants.ProvisionerConstants;
//import com.demo.provisioner.factory.impl.DatabricksProvisioner;
import com.demo.provisioner.factory.impl.SnowFlakeExecutor;
import com.demo.provisioner.factory.intraface.ProvisionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ExecutorFactory {

    @Autowired
    private SnowFlakeExecutor snowflakeExecutor;

    //TODO:
    //s3 s3Executor
    //GCS gcsExecutor

    private static final Map<String, ProvisionExecutor> factoryLookupMap = new HashMap<>();

    @PostConstruct
    private Map<String, ProvisionExecutor> getProvisionerMap() {
        factoryLookupMap.put(ProvisionerConstants.SNOWFLAKE, new SnowFlakeExecutor());
        return factoryLookupMap;
    }

    public static ProvisionExecutor getExecutor(String type) {
        return Optional.ofNullable(factoryLookupMap.get(type))
                           .orElseThrow(()->
                              new IllegalArgumentException("invalid provision type"));
    }
}