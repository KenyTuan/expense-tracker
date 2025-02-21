package com.test.utils.converter;

import com.test.entity.Expense;
import com.test.enums.Category;

import java.time.LocalDateTime;

public class ExpenseConverter implements Converter<Expense> {
    @Override
    public String toJson(Expense expense) {

        return "\t{\n" +
                "\t\t\"id\":" + expense.getId() + ",\n" +
                "\t\t\"description\": \"" + expense.getDescription() + "\",\n" +
                "\t\t\"amount\": \"" + expense.getAmount() + "\",\n" +
                "\t\t\"date\": \"" + expense.getDate() + "\",\n" +
                "\t\t\"category\": \"" + expense.getCategory() + "\"\n" +
                "\t}";
    }

    @Override
    public Expense fromJson(String json) {
        json = json.replaceAll("[{}\"]", "").trim();

        String[] keyValuePairs = json.split(",");

        long id = 0;
        String description = null;
        int amount = 0;
        LocalDateTime date = null;
        Category category = null;

        for (String keyValuePair : keyValuePairs) {
            String[] keyValue = keyValuePair.split(":", 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            switch (key) {
                case "id":
                    id = Long.parseLong(value);
                    break;
                case "description":
                    description = value;
                    break;
                case "amount":
                    amount  = Integer.parseInt(value);
                    break;
                case "date":
                    date = LocalDateTime.parse(value);
                    break;
                case "category":
                    category = Category.valueOf(value);
                    break;
                default:
                    break;
            }
        }

        return new Expense(id, description, date, amount,category);
    }
}
