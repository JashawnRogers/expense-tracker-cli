package domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {
    private static long nextId = 0;

    private final long id;
    private final String description;
    private final BigDecimal amount;
    private final Category category;
    private final LocalDate date;

    public Expense(String description, BigDecimal amount, Category category, LocalDate date) {
        this.id = nextId++;

        if (description != null && !description.isBlank()) {
            this.description = description;
        } else {
            throw new IllegalArgumentException("A description must be provided to create an expense.");
        }

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }

        if (Category.isValid(category.name())) {
            this.category = category;
        } else {
            throw new IllegalArgumentException("A valid category is needed to create an expense.");
        }

        if (date != null) {
            this.date = date;
        } else {
            throw new IllegalArgumentException("A date is required to create an expense.");
        }
    }

    public Expense(Long id, String description, BigDecimal amount, Category category, LocalDate date) {
        if (id != null) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("An ID is required to map an existing expense to an Expense object");
        }

        if (description != null && !description.isBlank()) {
            this.description = description;
        } else {
            throw new IllegalArgumentException("A description must be provided to create an expense.");
        }

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }

        if (Category.isValid(category.name())) {
            this.category = category;
        } else {
            throw new IllegalArgumentException("A valid category is needed to create an expense.");
        }

        if (date != null) {
            this.date = date;
        } else {
            throw new IllegalArgumentException("A date is required to create an expense.");
        }
    }

    public static Expense createFromExisting(Long id, String description, BigDecimal amount, Category category, LocalDate date) {
        return new Expense(id, description, amount, category, date);
    }

    public static Expense createNew(String description, BigDecimal amount, Category category, LocalDate date) {
        return new Expense(description, amount, category, date);
    }

    @Override
    public String toString() {
        return "ID: %s | Date: %s | Category: %s | Amount: $%s | Description: %s"
                .formatted(this.id, this.date, this.category, this.amount, this.description);
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }
}
