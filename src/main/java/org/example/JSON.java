package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;

public class JSON {
    private final HashSet<String> searchCriterias = new HashSet<String>(
            Arrays.asList("lastName", "productName", "minTimes", "minExpenses", "maxExpenses", "badCustomers")
    );
    private final HashSet<String> numericCriterias = new HashSet<String>(
            Arrays.asList("minTimes", "minExpenses", "maxExpenses", "badCustomers")
    );
    public JSONObject readInputJson(String inputFilePath, String operationType) {
        File file = new File(inputFilePath);
        JSONObject resultJsonObject = null;
        JSONParser parser = new JSONParser();

        try(FileReader reader = new FileReader(file)) {
            JSONObject inputCriterias = (JSONObject) parser.parse(reader);
            JSONArray criterias = (JSONArray) inputCriterias.get("criterias");
            String formatCheckingMessage;

            switch (operationType) {
                case "search": formatCheckingMessage = checkCriteriasSearchFormat(criterias); break;
                case "stat": formatCheckingMessage = checkCriteriasStatFormat(inputCriterias); break;
                default: formatCheckingMessage = null;
            }


            if (formatCheckingMessage != null) {
                resultJsonObject = new JSONObject();
                resultJsonObject.put("type", "error");
                resultJsonObject.put("message", formatCheckingMessage);

            } else {
                resultJsonObject = inputCriterias;
            }

        } catch (IOException | ParseException e) {
            System.err.println(e);
            JSONObject errorObject = new JSONObject();
            errorObject.put("type", "error");
            errorObject.put("message", e.getMessage());
            return errorObject;
        }

        return resultJsonObject;
    }

    private String checkCriteriasStatFormat(JSONObject inputCriterias) {
        if (inputCriterias == null) {
            return "???? ?????????????? ?????????????? ????????????????";
        }

        if (inputCriterias.get("startDate") == null) {
            return "?????????????????????? ???????? startDate";
        }

        if (inputCriterias.get("endDate") == null) {
            return "?????????????????????? ???????? endDate";
        }

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(inputCriterias.get("startDate").toString());
            sdf.parse(inputCriterias.get("endDate").toString());
        } catch (java.text.ParseException e) {
            return "???????????????????????? ???????????? ????????";
        }

        return null;
    }

    private String checkCriteriasSearchFormat(JSONArray criterias) {
        if (criterias == null) {
            return "???????????????????? ?????????? ???????? criterias";
        }

        if (criterias.isEmpty()) {
            return "?????????????????????? ???????????????? ????????????";
        }

        for (Object criteria: criterias) {
            JSONObject jsonObject = (JSONObject) criteria;
            if (jsonObject.get("productName") != null && jsonObject.get("minTimes") == null) {
                return "???????????????? productName ????????????. ???????????????? minTimes ???? ????????????";
            }
            if (jsonObject.get("productName") == null && jsonObject.get("minTimes") != null) {
                return "???????????????? productName ???? ????????????. ???????????????? minTimes ????????????";
            }
            if (jsonObject.get("minExpenses") != null && jsonObject.get("maxExpenses") == null) {
                return "???????????????? minExpenses ????????????. ???????????????? maxExpenses ???? ????????????";
            }
            if (jsonObject.get("minExpenses") == null && jsonObject.get("maxExpenses") != null) {
                return "???????????????? minExpenses ???? ????????????. ???????????????? maxExpenses ????????????";
            }
            for (Object criteriaName: jsonObject.keySet()) {
                if (!searchCriterias.contains(criteriaName)) {
                    return "?????????????????????? ???????????????? ????????????: " + criteriaName;
                }
            }

            for (String field : numericCriterias) {
                try {
                    if (jsonObject.get(field) != null) {
                        Integer.parseInt(jsonObject.get(field).toString());
                    }
                } catch (NumberFormatException e) {
                    return "???????????????????????? ???????????? " + field;
                }
            }
        }
        return null;
    }

    public void writeOutputJson(String outputFilePath, JSONObject result) {
        File file = new File(outputFilePath);
        try(FileWriter writer = new FileWriter(file))
        {
            writer.write(formatJSONStr(result.toJSONString()));
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    private static String formatJSONStr(final String json_str) {
        final char[] chars = json_str.toCharArray();
        final String newline = System.lineSeparator();
        final int spacing = 4;

        String ret = "";
        boolean begin_quotes = false;

        for (int i = 0, indent = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == '\"') {
                ret += c;
                begin_quotes = !begin_quotes;
                continue;
            }

            if (!begin_quotes) {
                switch (c) {
                    case '{':
                    case '[':
                        ret += c + newline + String.format("%" + (indent += spacing) + "s", "");
                        continue;
                    case '}':
                    case ']':
                        ret += newline + ((indent -= spacing) > 0 ? String.format("%" + indent + "s", "") : "") + c;
                        continue;
                    case ':':
                        ret += c + " ";
                        continue;
                    case ',':
                        ret += c + newline + (indent > 0 ? String.format("%" + indent + "s", "") : "");
                        continue;
                    default:
                        if (Character.isWhitespace(c)) continue;
                }
            }

            ret += c + (c == '\\' ? "" + chars[++i] : "");
        }

        return ret;
    }
}
