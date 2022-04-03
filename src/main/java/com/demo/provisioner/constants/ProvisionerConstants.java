package com.demo.provisioner.constants;

import java.util.HashMap;
import java.util.Map;

public class ProvisionerConstants {

    public static final String SNOWFLAKE = "snowflake";
    public static final String S3 = "s3";
    public static final String REGEX_FOR_PARAM_RESOLUTION="\\{\\{(.*?)\\}\\}";
    public static final String SF_USER = "";
    public static final String SF_PASSWD = "";
    public static final String SF_JDBC_URL = "";

   /* //TODO: not needed for now
    public static final Map<String, Integer> ENVIRONMENT_ID_MAP = new HashMap<String, Integer>() {{
        put("dev", 104);
        put("qa", 103);
        //TODO: add more env entries
    }};*/
}
