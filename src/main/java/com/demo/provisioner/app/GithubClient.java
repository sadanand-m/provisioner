package com.demo.provisioner.app;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GithubClient {

    @Autowired
    Environment environment;

    @Autowired
    RestTemplate restTemplate;

    private static Logger LOGGER = LoggerFactory.getLogger(GithubClient.class);


    private HttpHeaders createHttpHeaders() {
        String notEncoded = environment.getProperty("github.user") + ":" + environment.getProperty("github.password");
        String encodedAuth = Base64.getEncoder().encodeToString(notEncoded.getBytes());
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type","application/vnd.github.VERSION.raw");
        headers.set("Content-Type","application/json");
//        headers.set("accept", "application/vnd.github.inertia-preview+json");
        headers.set("Accept", "application/json");
        headers.add("Authorization", "Basic " + encodedAuth);
        return headers;
    }

    public Map<String, List<String>> readPackage(String packageFileName, String packageFolderName)
    {
        Map<String, List<String>> packageMap = null;
        String theUrl = environment.getProperty("github.url");
        String body = null;
        try {
            HttpHeaders headers = createHttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            //TODO: exception handling in case of exchange error e.g. missing or wrong pwd
            ResponseEntity<String>
                    response = restTemplate.exchange(theUrl, HttpMethod.GET, entity, String.class,
                    environment.getProperty("github.user"),
                    environment.getProperty("github.repo.name"),
                    environment.getProperty("github.branch"), packageFolderName ,packageFileName);
            System.out.println("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody());
            body = response.getBody();
            Gson gson = new Gson();
            packageMap = gson.fromJson(body, Map.class);
            LOGGER.debug("json-map {}",packageMap);
            System.out.println("json-map "+packageMap);
        }
        catch (Exception eek) {
            System.out.println("** Exception: "+ eek.getMessage());
            //TODO: throw custom exception here and exit
        }
        if(body==null)
        try {
            throw new Exception("No package file found in Github location:  "+theUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageMap;
    }

    public List<Object[]> getDataForTrigramTable(String trigramFileName) {
        List<Object[]> input = null;
        String theUrl = environment.getProperty("github.url");
        String body = null;
        try {
            HttpHeaders headers = createHttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            //TODO: exception handling in case of exchange error e.g. missing or wrong pwd
            ResponseEntity<String>
                    response = restTemplate.exchange(theUrl, HttpMethod.GET, entity, String.class,
                    environment.getProperty("github.user"), environment.getProperty("github.repo.name"),
                    environment.getProperty("github.branch"), environment.getProperty("github.trigram.folder"),trigramFileName);
            System.out.println("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody());
            body = response.getBody();
            String[] split = body.split("[\n\r]");
            input = Arrays.stream(split)
                    .filter(StringUtils::isNotBlank)
                    .map(lines -> lines.split("\u0001"))
                    .map(arr -> new Object[]{arr[0], Long.parseLong(arr[1])})
                    .collect(Collectors.toList());
        }
        catch (Exception eek) {
            System.out.println("** Exception: "+ eek.getMessage());
            //TODO: throw custom exception here and exit
        }
        if(body==null)
            try {
                throw new Exception("No trigram file found in Github location:  "+theUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return input;
    }
}