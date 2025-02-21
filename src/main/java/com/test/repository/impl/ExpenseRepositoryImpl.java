package com.test.repository.impl;

import com.test.entity.Expense;
import com.test.repository.ExpenseRepository;
import com.test.utils.converter.ExpenseConverter;
import com.test.utils.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepositoryImpl implements ExpenseRepository {
    private final static String File_Name = "expenses.json";

    private final Path path;

    private final JsonMapper<Expense> jsonMapper;

    public ExpenseRepositoryImpl() {
        this.path = Paths.get(File_Name);
        this.jsonMapper = new JsonMapper<>(new ExpenseConverter());
    }

    @Override
    public List<Expense> loadExpense() throws IOException {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        return jsonMapper.fromJson(Files.readString(path));
    }

    @Override
    public void saveExpense(List<Expense> expenses) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, jsonMapper.toJson(expenses));
    }
}
