package com.demo.provisioner;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    public static void main(String[] args) {

       // replaceTest();

        String input = "ignore everything except this {{ text1  }}";
        input = "create database if not exists A1SF_DB_{{env}}_{{tenanatid}};";
        System.out.println("input:: "+input);

        Map<String, String> inputParams = new HashMap<>();
        inputParams.put("env","104");
        inputParams.put("tenanatid","803");

        //String regex = "#\\{\\{(.*?)\\}\\}#";
        String regex2 = "\\{\\{(.*?)\\}\\}";

      /*  String output = input.replaceAll(regex2,input);
        System.out.println(" "+output);*/
      /*  preg_match('#\{\{(.*?)\}\}#', $text, $match);
        var_dump($match);*/
        /*String dataYouWant = StringUtils.substringBetween(input,regex2);
        System.out.println("dataYouWant:  "+dataYouWant);*/
        String mydata = input;

        Pattern pattern = Pattern.compile(regex2);
        Matcher matcher = pattern.matcher(mydata);
        Map<String, String> findReplace = new HashMap<>();
        while (matcher.find())
        {
            String token = matcher.group();
            String tokenWithoutBraces = token.substring(2,token.length()-2);
        if(inputParams.containsKey(tokenWithoutBraces))
                findReplace.put(tokenWithoutBraces,inputParams.get(tokenWithoutBraces));
            //findReplace.put(matcher.group(),null);
            System.out.println(matcher.group());
        }
        String finalInput = input;
        finalInput = input.replace("{{","");
        finalInput = finalInput.replace("}}","");
       for(Map.Entry<String, String> entry: findReplace.entrySet()){
           if(finalInput.contains(entry.getKey()))
               finalInput = finalInput.replaceAll(entry.getKey(),entry.getValue());
       }
     /*   findReplace.forEach((k, v)-> {
            System.out.println(k+ "==> "+v);
        if(finalInput.contains(k))
            finalInput.replaceAll(k,v);
        });*/
    }

    private static void replaceTest() {
        String  input = "create database if not exists A1SF_DB_{{env}}_{{tenanatid}};";


        System.out.println("input: "+input);
        String output = input.replace("{{","");
        String output2 = output.replace("}}","");
        System.out.println("input: "+input);
        System.out.println("output: "+output2);
    }


}
