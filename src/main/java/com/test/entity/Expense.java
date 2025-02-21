package com.test.entity;

import com.test.enums.Category;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Expense implements Serializable, Comparable<Expense> {
    private static final long serialVersionUID = 1L;

    private long id;

    private String description;

    private LocalDateTime date;

    private int amount;

    private Category category;

    public Expense(long id, String description, LocalDateTime date,
                   int amount, Category category) {
        checkId(id);
        checkDescription(description);
        checkAmount(amount);
        checkCategory(category);

        this.id = id;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.format("[ %d |  %s |  %s |  %s |  %s ]",
                this.id,
                this.description,
                "$" + this.amount,
                this.date,
                this.category.toString().toLowerCase()
        );
    }

    private void checkId(long id) {
        if (id <= 0)
            throw new IllegalArgumentException("Expense ID must be positive");
    }

    private void checkDescription(String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description cannot be null or empty");
        if (description.length() > 255)
            throw new IllegalArgumentException("Description is too long");
    }

    private void checkAmount(int amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
    }

    private void checkCategory(Category category) {
        if (category == null)
            throw new IllegalArgumentException("Category cannot be null");
    }

    @Override
    public int compareTo(Expense o) {
        return Long.compare(this.id, o.id);
    }
}
