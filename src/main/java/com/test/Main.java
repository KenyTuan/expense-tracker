package com.test;

import com.test.cli.ExpenseAppCLI;
import com.test.repository.impl.ExpenseRepositoryImpl;
import com.test.service.ExpenseServiceImpl;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new ExpenseAppCLI(new ExpenseServiceImpl( new ExpenseRepositoryImpl())).runApp();
    }
}