package org.example;

import org.json.simple.JSONObject;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String checkArgumentsResult = checkArguments(args);
        if (checkArgumentsResult != null) {
            System.err.println(checkArgumentsResult);
            return;
        }

        final String operationType = args[0];
        final String inputFilePath = args[1];
        final String outputFilePath = args[2];



    }

    public static String checkArguments(String[] args) {
        if (args.length == 2) {
            return "Отсутсвует третий аргумент!";
        }
        if (args.length == 1) {
            return "Отсутсвуют второй и третий аргументы!";
        }
        if (args.length == 0) {
            return "Аргументы отсутсвуют!";
        }
        return null;
    }
}





