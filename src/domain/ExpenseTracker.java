package domain;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

public class ExpenseTracker {
    public final Map<Long, Expense> expenses = new LinkedHashMap<>();

    public void addExpense(Expense expense) {
        try {
            expenses.put(expense.getId(), expense);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to add expense.");
        }
    }

    public void removeExpense(long id) {
        try {
            expenses.get(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Expense not found.");
        }
    }

//    METHODS TO ADD
//   Find expenses by category
    public List<Expense> findExpensesByCategory(Category category) {
        List<Expense> expensesByCategory = new ArrayList<>();

        for (Expense expense : expenses.values()) {
            if (expense.getCategory().equals(category)) {
                expensesByCategory.add(expense);
            }
        }

        if (expensesByCategory.isEmpty()) {
            System.out.println("There are no expenses in category: " + category.name());
        }

        return expensesByCategory;
    }

//   Find expenses by month
    public List<String> findExpensesByMonth(YearMonth yearMonth) {
        List<String> expensesByMonth = new ArrayList<>();

        for (Map.Entry<Long, Expense> expense : expenses.entrySet()) {
            Month expenseMonth = expense.getValue().getDate().getMonth();
            int expenseYear = expense.getValue().getDate().getYear();

            if (expenseMonth.equals(yearMonth.getMonth()) &&
            expenseYear == yearMonth.getYear()) {
                expensesByMonth.add(expense.toString());
            }
        }

        if (expensesByMonth.isEmpty()) {
            System.out.println("There are no expenses in " + yearMonth.getMonth() + " " + yearMonth.getYear());
        }

        return expensesByMonth;
    }

//   Calculate total spending
    public BigDecimal calculateTotalSpending() {
       return expenses.values().stream()
               .map(Expense::getAmount)
               .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

//   Calculate total by category
    public Map<Category, BigDecimal> calculateTotalByCategory() {
        Map<Category, BigDecimal> categoryTotals = new HashMap<>();

        for (Expense expense : expenses.values()) {
            BigDecimal amount = expense.getAmount();
            Category category = expense.getCategory();

            // Get the current total for this category. If it's not in the map yet, start at 0.
            BigDecimal categoryTotal = categoryTotals.getOrDefault(category, BigDecimal.ZERO);

            categoryTotals.put(category, categoryTotal.add(amount));
        }

        return categoryTotals;
    }


//   Return all expenses
    public List<Expense> allExpenses() {
        List<Expense> allExpenses = new ArrayList<>();
        for (Map.Entry<Long, Expense> expense : expenses.entrySet()) {
            allExpenses.add(expense.getValue());
        }

        if (allExpenses.isEmpty()) {
            System.out.println("There are no expenses to display.");
        }

        return allExpenses;
    }
}
