package com.test.cli;

import com.test.entity.Expense;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpenseAppPrinter {
    private static final String BORDER = "+-------+------------------------------------------+--------------+-------------------+------------------+";
    private static final String HEADER = "| ID    | Description                              | Amount       | Date              | Category         |";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String EXPENSE_FORMAT = "| %-5s | %-40s | %-12s | %-16s | %-16s |%n";
    private static final int DESCRIPTION_MAX_LENGTH = 40;

    public void printExpenses(List<Expense> exceptions) {
        System.out.println(BORDER);
        System.out.println(HEADER);
        System.out.println(BORDER);

        exceptions.forEach(e -> {
            System.out.printf(EXPENSE_FORMAT,
                    e.getId(),
                    truncate(e.getDescription()),
                    e.getAmount(),
                    e.getDate().format(DATE_FORMATTER),
                    e.getCategory());
        });

        System.out.println(BORDER);
    }

    private String truncate(String str){
        return (str.length() <= DESCRIPTION_MAX_LENGTH) ? str :
                str.substring(0, DESCRIPTION_MAX_LENGTH - 3) + "...";
    }
}
