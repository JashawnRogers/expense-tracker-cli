package domain;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

public class ExpenseTracker {
    private static final Map<Long, Expense> expenses = new LinkedHashMap<>();

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
            throw new IllegalArgumentException("Id not found.");
        }
    }

//    METHODS TO ADD
//   Find expenses by category
    public List<String> findExpensesByCategory(Category category) {
        List<String> expensesByCategory = new ArrayList<>();

        for (Map.Entry<Long, Expense> expense : expenses.entrySet()) {
            if (expense.getValue().getCategory().equals(category)) {
                expensesByCategory.add(expense.getValue().toString());
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

        for (Category category : Category.values()) {
            BigDecimal total = BigDecimal.ZERO;

            for (Map.Entry<Long, Expense> expense : expenses.entrySet()) {
                if (category.equals(expense.getValue().getCategory())) {
                    total = total.add(expense.getValue().getAmount());
                }
            }

            categoryTotals.put(category, total);
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
