package com.test.utils.converter;

import com.test.entity.Budget;

public class BudgetConverter implements Converter<Budget> {
    @Override
    public String toJson(Budget budget) {
        return "\t{\n" +
                "\t\t\"month\":" + budget.getMonth() + ",\n" +
                "\t\t\"amount\": \"" + budget.getAmount() + "\"\n" +
                "\t}";
    }

    @Override
    public Budget fromJson(String json) {
        json = json.replaceAll("[{}\"]", "").trim();

        String[] keyValuePairs = json.split(",");

        int month = 0;
        int amount = 0;

        for (String keyValuePair : keyValuePairs) {
            String[] keyValue = keyValuePair.split(":", 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            switch (key) {
                case "month" -> month = Integer.parseInt(value);
                case "amount" -> amount  = Integer.parseInt(value);
            }
        }

        return new Budget(month, amount);
    }
}
