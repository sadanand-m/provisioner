package com.demo.provisioner.util;

import com.demo.provisioner.constants.ProvisionerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class SFProvisionerUtil {

    @Autowired
    Environment env;

    public static String doParameterResolution(String cmd, Map<String, String> params, int tenantId) {
       // TODO remove this hardcoding
        params.put("env","104");
        params.put("tenantid",String.valueOf(tenantId));
        String regex = ProvisionerConstants.REGEX_FOR_PARAM_RESOLUTION;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cmd);
        Map<String, String> findReplace = new HashMap<>();
        while (matcher.find())
        {
            String token = matcher.group();
            String tokenWithoutBraces = token.substring(2,token.length()-2);
            if(params.containsKey(tokenWithoutBraces))
                findReplace.put(tokenWithoutBraces,params.get(tokenWithoutBraces));
            //System.out.println(matcher.group());
        }
        String finalInput = cmd;
        finalInput = cmd.replace("{{","");
        finalInput = finalInput.replace("}}","");
        for(Map.Entry<String, String> entry: findReplace.entrySet()){
            if(finalInput.contains(entry.getKey()))
                finalInput = finalInput.replaceAll(entry.getKey(),entry.getValue());
        }
        return finalInput;
    }
}
