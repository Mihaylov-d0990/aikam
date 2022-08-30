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

        File testFile = new File(inputFilePath);
        if (!testFile.exists() || testFile.isDirectory()) {
            System.err.println("Файл с входными данными не существует или это директория");
            return;
        }

        testFile = new File(outputFilePath);
        if (!testFile.exists() || testFile.isDirectory()) {
            System.err.println("Файл с выходными данными не существует или это директория");
            return;
        }

        JSON json = new JSON();
        JSONObject readResult = json.readInputJson(inputFilePath, operationType);

        if (readResult.get("type") == "error") {
            json.writeOutputJson(outputFilePath, readResult);
        } else {
            Postgre postgre = new Postgre();
            JSONObject outputResult = null;
            switch (operationType) {
                case "search": outputResult = postgre.getOutputForSearch(readResult); break;
                case "stat": outputResult = postgre.getOutputForStat(readResult); break;
                default: {
                    outputResult.put("type", "error");
                    outputResult.put("message", "Неизвестная ошибка");
                }
            }
            json.writeOutputJson(outputFilePath, outputResult);
        }

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





