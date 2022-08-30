package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class Postgre {
    private final String DB_USER = "postgres";
    private final String DB_PASSWORD = "postgres";
    private final String DB_URL = "jdbc:postgresql://localhost:5432/aikam";

    public JSONObject getOutputForSearch(JSONObject criterias) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            JSONObject outputObject = new JSONObject();
            outputObject.put("type", "search");
            JSONArray outputResultsArray = new JSONArray();
            JSONArray jsonArrayCriterias = (JSONArray) criterias.get("criterias");

            for (Object criteriaObject: jsonArrayCriterias) {
                JSONObject criteria = (JSONObject) criteriaObject;
                outputResultsArray.add(getCriteriaQueryResult(criteria, connection));
            }

            outputObject.put("results", outputResultsArray);

            return outputObject;

        } catch (SQLException e) {
            System.err.println(e);
            JSONObject errorObject = new JSONObject();
            errorObject.put("type", "error");
            errorObject.put("message", e.getMessage());
            return errorObject;
        }
    }

    public JSONObject getOutputForStat(JSONObject criterias) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Statement statement = connection.createStatement();
            JSONObject outputObject = new JSONObject();
            ResultSet resultSet = statement.executeQuery("SELECT customer.firstname, customer.lastname, product.name AS product, \n" +
                    "SUM(product.price) AS expenses, COUNT(*) AS totalDays FROM purchase \n" +
                    "INNER JOIN customer ON customer.customerid = purchase.customer\n" +
                    "INNER JOIN product ON product.productid = purchase.product\n" +
                    "WHERE purchase.date BETWEEN '" + criterias.get("startDate") + "' AND '" + criterias.get("endDate") + "'\n" +
                    "AND extract(dow from purchase.date) != 0 AND extract(dow from purchase.date) != 6\n" +
                    "GROUP BY (customer.firstname, customer.lastname, product.name)");

            Integer totalExpensesGlobal = 0;
            Integer totalDaysGlobal = 0;

            if (resultSet != null) {
                HashMap<JSONObject, JSONArray> customers = new HashMap<JSONObject, JSONArray>();
                JSONArray purchases = new JSONArray();
                while (resultSet.next()) {
                    JSONObject customer = new JSONObject();
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    customer.put("name", firstName + " " + lastName);

                    JSONObject productObject = new JSONObject();
                    String product = resultSet.getString("product");
                    Integer expenses = Integer.parseInt(resultSet.getString("expenses"));
                    productObject.put("name", product);
                    productObject.put("expenses", expenses);

                    if (customers.containsKey(customer)) {
                        purchases.add(productObject);
                        customers.put(customer, purchases);
                    } else {
                        purchases = new JSONArray();
                        purchases.add(productObject);
                        customers.put(customer, purchases);
                    }

                    Integer totalDays = Integer.parseInt(resultSet.getString("totalDays"));
                    totalDaysGlobal += totalDays;
                    totalExpensesGlobal += expenses;
                }

                JSONArray customersArray = new JSONArray();

                Integer countCustomers = 0;
                for (Map.Entry<JSONObject, JSONArray> entry : customers.entrySet()) {
                    JSONObject customer = new JSONObject();
                    customer.put("name", entry.getKey().get("name"));
                    JSONArray customerPurchases = new JSONArray();
                    Integer totalExpensesByCustomer = 0;

                    for (Object productObject : entry.getValue()) {
                        JSONObject product = (JSONObject) productObject;
                        customerPurchases.add(product);
                        totalExpensesByCustomer += Integer.parseInt(product.get("expenses").toString());
                    }

                    customer.put("purchases", customerPurchases);
                    customer.put("totalExpenses", totalExpensesByCustomer);
                    customersArray.add(customer);
                    countCustomers++;
                }

                outputObject.put("type", "stat");
                outputObject.put("totalDays", totalDaysGlobal);
                outputObject.put("customers", customersArray);
                outputObject.put("totalExpenses", totalExpensesGlobal);
                outputObject.put("avgExpenses", String.format("%.2f", (float) totalExpensesGlobal / countCustomers));
            }
            return outputObject;

        } catch (SQLException e) {
            System.err.println(e);
            JSONObject errorObject = new JSONObject();
            errorObject.put("type", "error");
            errorObject.put("message", e.getMessage());
            return errorObject;
        }
    }

    private JSONObject getCriteriaQueryResult(JSONObject criteria, Connection connection) {
        try (Statement statement = connection.createStatement()) {
            JSONObject criteriaResultObject = new JSONObject();
            ResultSet resultSet = null;
            JSONObject criteriaObject = new JSONObject();

            if (criteria.get("lastName") != null) {
                resultSet = statement.executeQuery("SELECT * FROM customer WHERE lastName = '" + criteria.get("lastName") + "'");
                criteriaObject.put("lastName", criteria.get("lastName"));
                criteriaResultObject.put("criteria", criteriaObject);
            }

            if (criteria.get("productName") != null) {
                resultSet = statement.executeQuery("SELECT customer.firstName, customer.lastName FROM purchase\n" +
                        "INNER JOIN customer ON customer.customerId = purchase.customer\n" +
                        "INNER JOIN product ON product.productId = purchase.product\n" +
                        "GROUP BY (customer.firstName, customer.lastName, product.name)\n" +
                        "HAVING COUNT(product.name) > '" + criteria.get("minTimes") + "' AND product.name = '" + criteria.get("productName") + "'");
                criteriaObject.put("productName", criteria.get("productName"));
                criteriaObject.put("minTimes", criteria.get("minTimes"));
                criteriaResultObject.put("criteria", criteriaObject);
            }

            if (criteria.get("minExpenses") != null) {
                resultSet = statement.executeQuery("SELECT customer.firstName, customer.lastName FROM purchase\n" +
                        "INNER JOIN customer ON customer.customerId = purchase.customer\n" +
                        "INNER JOIN product ON product.productId = purchase.product\n" +
                        "GROUP BY (customer.firstName, customer.lastName)\n" +
                        "HAVING SUM(product.price) > '" + criteria.get("minExpenses") + "' AND SUM(product.price) < '" + criteria.get("maxExpenses") + "'");
                criteriaObject.put("minExpenses", criteria.get("minExpenses"));
                criteriaObject.put("maxExpenses", criteria.get("maxExpenses"));
                criteriaResultObject.put("criteria", criteriaObject);
            }

            if (criteria.get("badCustomers") != null) {
                resultSet = statement.executeQuery("SELECT customer.firstName, customer.lastName FROM purchase \n" +
                        "INNER JOIN customer ON customer.customerId = purchase.customer\n" +
                        "GROUP BY (customer.firstName, customer.lastName) \n" +
                        "ORDER BY COUNT(purchase.customer) \n" +
                        "LIMIT '" + criteria.get("badCustomers") + "'");
                criteriaObject.put("badCustomers", criteria.get("badCustomers"));
                criteriaResultObject.put("criteria", criteriaObject);
            }

            JSONArray results = new JSONArray();
            if (resultSet != null) {
                while (resultSet.next()) {
                    JSONObject criteriaResults = new JSONObject();
                    criteriaResults.put("lastName", resultSet.getString("lastName"));
                    criteriaResults.put("firstName", resultSet.getString("firstName"));
                    results.add(criteriaResults);
                }
                criteriaResultObject.put("results", results);
                return criteriaResultObject;
            }

        } catch (SQLException e) {
            System.err.println(e);
            JSONObject errorObject = new JSONObject();
            errorObject.put("type", "error");
            errorObject.put("message", e.getMessage());
            return errorObject;
        }

        return null;
    }

}