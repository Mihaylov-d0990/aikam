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
