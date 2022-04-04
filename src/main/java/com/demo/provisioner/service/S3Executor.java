package com.demo.provisioner.service;

import com.demo.provisioner.vo.PackageVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class S3Executor implements ProvisionExecutor{

    @Override
    public void provision(List<String> request, int tenantId, PackageVO packageVO) {
        System.out.println("s3 executor todo");
    }
}
