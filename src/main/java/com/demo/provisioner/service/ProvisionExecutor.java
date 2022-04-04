package com.demo.provisioner.service;

import com.demo.provisioner.util.SFProvisionerUtil;
import com.demo.provisioner.vo.PackageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProvisionExecutor {

    void provision(List<String> request, int tenantId, PackageVO packageVO);
}
