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


    private String checkCriteriasStatFormat(JSONObject inputCriterias) {
        if (inputCriterias == null) {
            return "Не найдены входные критерии";
        }

        if (inputCriterias.get("startDate") == null) {
            return "Отсутствует поле startDate";
        }

        if (inputCriterias.get("endDate") == null) {
            return "Отсутствует поле endDate";
        }

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(inputCriterias.get("startDate").toString());
            sdf.parse(inputCriterias.get("endDate").toString());
        } catch (java.text.ParseException e) {
            return "Неправильный формат даты";
        }

        return null;
    }

    private String checkCriteriasSearchFormat(JSONArray criterias) {
        if (criterias == null) {
            return "Невозможно найти поле criterias";
        }

        if (criterias.isEmpty()) {
            return "Отсутствуют критерии поиска";
        }

        for (Object criteria: criterias) {
            JSONObject jsonObject = (JSONObject) criteria;
            if (jsonObject.get("productName") != null && jsonObject.get("minTimes") == null) {
                return "Критерий productName найден. Критерий minTimes не найден";
            }
            if (jsonObject.get("productName") == null && jsonObject.get("minTimes") != null) {
                return "Критерий productName не найден. Критерий minTimes найден";
            }
            if (jsonObject.get("minExpenses") != null && jsonObject.get("maxExpenses") == null) {
                return "Критерий minExpenses найден. Критерий maxExpenses не найден";
            }
            if (jsonObject.get("minExpenses") == null && jsonObject.get("maxExpenses") != null) {
                return "Критерий minExpenses не найден. Критерий maxExpenses найден";
            }
            for (Object criteriaName: jsonObject.keySet()) {
                if (!searchCriterias.contains(criteriaName)) {
                    return "Неизвестный критерий поиска: " + criteriaName;
                }
            }

            for (String field : numericCriterias) {
                try {
                    if (jsonObject.get(field) != null) {
                        Integer.parseInt(jsonObject.get(field).toString());
                    }
                } catch (NumberFormatException e) {
                    return "Неправильный формат " + field;
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
