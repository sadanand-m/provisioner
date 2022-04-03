package com.demo.provisioner.factory.intraface;

import com.demo.provisioner.vo.PackageVO;

import java.util.List;

public interface ProvisionExecutor {

    void provision(List<String> request, int tenantId, PackageVO packageVO);
}
