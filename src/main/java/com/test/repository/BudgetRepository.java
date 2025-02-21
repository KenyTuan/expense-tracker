package com.test.repository;

import com.test.entity.Budget;

import java.io.IOException;
import java.util.List;

public interface BudgetRepository {
    List<Budget> loadBudget() throws IOException;

    void saveBudget(List<Budget> budget) throws IOException;
}
