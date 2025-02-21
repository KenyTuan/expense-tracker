package com.test.cli;

import com.test.entity.Expense;
import com.test.enums.Category;
import com.test.service.ExpenseService;
import com.test.utils.EnumUtils;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.test.enums.Category.*;

public class ExpenseAppCLI {
    private final Scanner sc;

    private final ExpenseService expenseService;

    private final ExpenseAppPrinter appPrinter;

    public ExpenseAppCLI(ExpenseService expenseService) {
        this.sc = new Scanner(System.in);
        this.expenseService = expenseService;
        this.appPrinter = new ExpenseAppPrinter();
    }

    public void runApp() {
        printUsage();
        boolean isRunning = true;

        while (isRunning) {
            System.out.print("expense-tracker ");
            List<String> input = splitInput(sc.nextLine());

            try {
                isRunning = processInput(input);
                expenseService.save();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private boolean processInput(List<String> input) throws IOException {
        if (input.isEmpty()) {
            System.out.println("Error: No action provided.");
            return true;
        }

        String action = input.get(0).toLowerCase();

        switch (action) {
            case "add" -> handleAdd(input);
            case "update" -> handleUpdate(input);
            case "delete" -> handleDelete(input);
            case "summary" -> handleSummary(input);
            case "list" -> handleList(input);
            case "set-budget" -> handleSetBudget(input);
            case "export" -> expenseService.exportToCSV();
            case "help" -> printUsage();
            case "exit" -> {
                System.out.println("Exiting...");
                return false;
            }
            default -> System.out.println("Error: Invalid action.");
        }

        return true;
    }

    private void handleAdd(List<String> input) {
        if (input.size() != 7) {
            System.out.println("Error: Missing expense description, amount, or category.");
            return;
        }

        String description = getValueFromInput(input, "--description");
        int amount = getIntValueFromInput(input, "--amount");
        Category category = getCategoryFromInput(input, "--category");

        if (description == null || amount < 0 || category == null) {
            System.out.println("Error: Invalid input for add action.");
            return;
        }

        Expense expense = expenseService.addExpense(description, amount, category);
        System.out.printf("Expense added successfully (ID: %s)%n", expense.getId());
    }

    private void handleUpdate(List<String> input) {
        if (input.size() <= 3) {
            System.out.println("Error: Missing expense ID, description, amount, or category.");
            return;
        }

        long id = getLongValueFromInput(input, "--id");
        String description = getValueFromInput(input, "--description");
        int amount = getIntValueFromInput(input, "--amount");
        Category category = getCategoryFromInput(input, "--category");

        if (id <= 0 || description == null || amount < 0 || category == null) {
            System.out.println("Error: Invalid input for update action.");
            return;
        }

        Expense expense = expenseService.updateExpense(id, description, amount, category);
        System.out.printf("Expense updated successfully (ID: %s)%n", expense.getId());
    }

    private void handleDelete(List<String> input) {
        if (input.size() != 3 || !input.get(1).equals("--id")) {
            System.out.println("Error: Missing expense ID.");
            return;
        }

        long id = getLongValueFromInput(input, "--id");
        if (id <= 0) {
            System.out.println("Error: Invalid expense ID.");
            return;
        }

        expenseService.deleteExpense(id);
        System.out.println("Expense deleted successfully!");
    }

    private void handleSummary(List<String> input) {
        if (input.size() == 1) {
            getTotalExpenses();
        } else if (input.size() == 3 && input.get(1).equals("--month")) {
            int month = getIntValueFromInput(input, "--month");
            if (month >= 1 && month <= 12) {
                getTotalExpensesByMonth(month);
            } else {
                System.out.println("Error: Invalid month.");
            }
        } else {
            System.out.println("Error: Invalid summary action.");
        }
    }

    private void handleList(List<String> input) {
        if (input.size() == 1) {
            getAll();
        } else if (input.size() == 3 && input.get(1).equals("--category")) {
            String category = input.get(2).toLowerCase();
            switch (category) {
                case "food" -> appPrinter.printExpenses(expenseService.getExpensesByCategory(FOOD));
                case "transport" -> appPrinter.printExpenses(expenseService.getExpensesByCategory(TRANSPORT));
                case "entertainment" -> appPrinter.printExpenses(expenseService.getExpensesByCategory(ENTERTAINMENT));
                case "other" -> appPrinter.printExpenses(expenseService.getExpensesByCategory(OTHER));
                default -> System.out.println("Error: Invalid category.");
            }
        } else {
            System.out.println("Error: Invalid list action.");
        }
    }

    private void handleSetBudget(List<String> input) throws IOException {
        if (input.size() != 5) {
            System.out.println("Error: Missing month or amount.");
            return;
        }

        int month = getIntValueFromInput(input, "--month");
        int amount = getIntValueFromInput(input, "--amount");

        if (month < 1 || month > 12 || amount < 0) {
            System.out.println("Error: Invalid month or amount.");
            return;
        }

        expenseService.setBudget(month, amount);
        System.out.println("Budget set successfully for " + Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    }

    private String getValueFromInput(List<String> input, String flag) {
        for (int i = 1; i < input.size(); i += 2) {
            if (input.get(i).equals(flag)) {
                return input.get(i + 1);
            }
        }
        return null;
    }

    private int getIntValueFromInput(List<String> input, String flag) {
        String value = getValueFromInput(input, flag);
        try {
            return value != null ? Integer.parseInt(value) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private long getLongValueFromInput(List<String> input, String flag) {
        String value = getValueFromInput(input, flag);
        try {
            return value != null ? Long.parseLong(value) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Category getCategoryFromInput(List<String> input, String flag) {
        String value = getValueFromInput(input, flag);
        return value != null && EnumUtils.isValidEnum(Category.class, value.toUpperCase())
                ? Category.valueOf(value.toUpperCase())
                : null;
    }

    private List<String> splitInput(String input) {
        List<String> result = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"[^\"]*\"|\\S+").matcher(input);

        while (matcher.find()) {
            String part = matcher.group();
            if (part.startsWith("\"") && part.endsWith("\"")) {
                part = part.substring(1, part.length() - 1);
            }
            result.add(part);
        }

        return result;
    }

    private void getTotalExpenses() {
        System.out.println("Total expenses: " + expenseService.getTotalExpenses());
    }

    private void getTotalExpensesByMonth(int month) {
        System.out.println("Total expenses for " +
                Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                ": " + expenseService.getTotalExpensesByMonth(month));
    }

    private void getAll() {
        appPrinter.printExpenses(expenseService.getAll());
    }

    private void printUsage() {
        System.out.println("Usage: expense-tracker <action> [arguments]");
        System.out.println("Actions:");
        System.out.println("  add --description \"<description>\" --amount <amount> --category \"<category>\"      - Add a new expense");
        System.out.println("  update --id <id> --description \"<description>\" --amount <amount> --category \"<category>\"   - Update an expense");
        System.out.println("  delete --id <id>            - Delete an expense");
        System.out.println("  summary               - Summary of all expenses");
        System.out.println("  summary --month <month>  - Summary of expenses for a specific month");
        System.out.println("  list          - List all expenses");
        System.out.println("  list --category \"<category>\"          - List expenses by category");
        System.out.println("  set-budget --month <month> --amount <amount>      - Set a budget for a specific month");
        System.out.println("  export                   - Export expenses to a CSV file");
        System.out.println("  help                   - Print this help message");
        System.out.println("  exit         - Exit the program");
    }
}
