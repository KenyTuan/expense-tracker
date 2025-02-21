package com.test.service;

import com.opencsv.CSVWriter;
import com.test.entity.Budget;
import com.test.entity.Expense;
import com.test.enums.Category;
import com.test.repository.BudgetRepository;
import com.test.repository.impl.BudgetRepositoryImpl;
import com.test.repository.ExpenseRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ExpenseServiceImpl implements ExpenseService{
    private final static String FILE_NAME = "expenses.csv";

    private final ExpenseRepository expenseRepository;

    private final BudgetRepository budgetRepository;

    private final List<Expense> expenses;

    private final List<Budget> budgets;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository)
            throws IOException {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = new BudgetRepositoryImpl();
        this.expenses = expenseRepository.loadExpense();
        this.budgets = budgetRepository.loadBudget();
    }

    @Override
    public void save() throws IOException {
        expenseRepository.saveExpense(expenses);
    }

    @Override
    public Expense addExpense(String description, int amount, Category category) {
        final LocalDateTime now = LocalDateTime.now();
        final Expense expense = new Expense(getNewId(),description,now,amount,category);

        expenses.add(expense);
        checkBudget();

        return expense;
    }

    @Override
    public Expense updateExpense(long id, String description, int amount, Category category) {
        final Expense expense = findExpense(id);
        final Expense updatedExpense = new Expense(
                expense.getId(),
                description.isBlank() ? expense.getDescription() : description,
                expense.getDate(),
                amount != 0? amount : expense.getAmount(),
                category == null ? expense.getCategory() : category
        );

        expenses.remove(expense);
        expenses.add(updatedExpense);
        checkBudget();

        return updatedExpense;
    }

    @Override
    public void deleteExpense(long id) {
        expenses.remove(findExpense(id));
        checkBudget();
    }

    @Override
    public List<Expense> getAll() {
        return expenses
                .stream()
                .sorted()
                .toList();
    }

    @Override
    public List<Expense> getExpensesByCategory(Category category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equals(category))
                .toList();
    }

    @Override
    public String getTotalExpenses() {
        final int totalExpenses = expenses.stream().mapToInt(Expense::getAmount).sum();
        return "$" + totalExpenses;
    }

    @Override
    public String getTotalExpensesByMonth(int month) {
        return "$" + getTotalExpensesForMonth(month);
    }

    @Override
    public void exportToCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME))) {
            String[] header = {"ID", "Description", "Amount", "Date", "Category"};
            writer.writeNext(header);

            for (Expense expense : expenses) {
                String[] data = {
                        String.valueOf(expense.getId()),
                        expense.getDescription(),
                        String.valueOf(expense.getAmount()),
                        expense.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        expense.getCategory().toString()
                };
                writer.writeNext(data);
            }
            System.out.println("Expenses exported to " + FILE_NAME + " successfully!");
        }catch (IOException e) {
            System.out.println("Error writing expenses to file!");
        }
    }

    private int getTotalExpensesForMonth(int month) {
        final int currentYear = LocalDate.now().getYear();

        return expenses.stream()
                .filter(e -> e.getDate().getYear() == currentYear)
                .filter(e -> e.getDate()
                        .getMonth()
                        .equals(Month.of(month)))
                .mapToInt(Expense::getAmount).sum();
    }

    @Override
    public void setBudget(int month, int amount) throws IOException {
        budgets.removeIf(budget -> budget.getMonth() == month);
        budgets.add(new Budget(month, amount));

        budgetRepository.saveBudget(budgets);
        System.out.println("Budget set for month " + month + ": " + amount);
        checkBudget();
    }

    public void checkBudget() {
        final int currentYear = LocalDate.now().getYear();

        for (Budget budget : budgets) {
            int total = expenses.stream()
                    .filter(e -> e.getDate().getYear() == currentYear)
                    .filter(expense -> expense.getDate().getMonth()
                            .equals(Month.of(budget.getMonth())))
                    .mapToInt(Expense::getAmount)
                    .sum();
            if (total > budget.getAmount()) {
                System.out.println("Warning: You have exceeded your budget for " +
                        Month.of(budget.getMonth()).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "!");
            }
        }
    }

    private long getNewId() {
        return expenses.stream()
                .mapToLong(Expense::getId)
                .max()
                .orElse(0) + 1;
    }

    private Expense findExpense(long id) {
        return expenses.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Expense with ID %d not found!", id)));
    }
}
