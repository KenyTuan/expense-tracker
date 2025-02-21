package com.test.repository;

import com.test.entity.Expense;

import java.io.IOException;
import java.util.List;

public interface ExpenseRepository {

    List<Expense> loadExpense() throws IOException;

    void saveExpense(List<Expense> expenses) throws IOException;
}
