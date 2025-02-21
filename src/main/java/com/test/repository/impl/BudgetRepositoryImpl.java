package com.test.repository.impl;

import com.test.entity.Budget;
import com.test.repository.BudgetRepository;
import com.test.utils.converter.BudgetConverter;
import com.test.utils.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BudgetRepositoryImpl implements BudgetRepository {
    private final static String File_Name = "budgets.json";

    private final Path path;

    private final JsonMapper<Budget> jsonMapper;

    public BudgetRepositoryImpl() {
        this.path = Paths.get(File_Name);
        this.jsonMapper = new JsonMapper<>(new BudgetConverter());
    }

    @Override
    public List<Budget> loadBudget() throws IOException {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        return jsonMapper.fromJson(Files.readString(path));
    }

    @Override
    public void saveBudget(List<Budget> budget) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, jsonMapper.toJson(budget));
    }
}
