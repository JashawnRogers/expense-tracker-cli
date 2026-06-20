package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

public class ExpenseTracker {
    public final Map<Long, Expense> expenses = new LinkedHashMap<>();
    private final Map<YearMonth, BigDecimal> budget = new HashMap<>();

    public void addExpense(Expense expense) {
        try {
            expenses.put(expense.getId(), expense);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to add expense.");
        }
    }

    public void removeExpense(long id) {
        try {
            expenses.remove(id);
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

    public void setMonthlyBudget(BigDecimal userSetBudget, YearMonth yearMonth) {
        if (userSetBudget.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Monthly Budget amount must be at least $0.");
        } else {
            budget.put(yearMonth, userSetBudget);
        }
    }

    public Expense findLargestExpense() {
        TreeMap<BigDecimal, Long> amounts = new TreeMap<>();
        expenses.forEach((id, expense) -> amounts.put(expense.getAmount(), id));

        Map.Entry<BigDecimal, Long> highestAmount = amounts.lastEntry();
        return expenses.get(highestAmount.getValue());
    }

    public Map<Category, BigDecimal> calculateCategoryPercentage() {
        Map<Category, BigDecimal> categoryTotals = calculateTotalByCategory();
        BigDecimal totalSpending = calculateTotalSpending();

        categoryTotals.replaceAll((category, total) ->
                total.divide(totalSpending, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP));

        return  categoryTotals;
    }

    public MonthlyBudgetDetails calculateMonthlyBudget(YearMonth yearMonth) {
        BigDecimal spent = BigDecimal.ZERO;
        BigDecimal monthBudget = budget.get(yearMonth);


        for (Map.Entry<Long, Expense> expense : expenses.entrySet()) {
            Month expenseMonth = expense.getValue().getDate().getMonth();
            int expenseYear = expense.getValue().getDate().getYear();

            if (expenseMonth.equals(yearMonth.getMonth()) &&
                    expenseYear == yearMonth.getYear()) {
                spent = spent.add(expense.getValue().getAmount());
            }
        }

        BigDecimal remaining = monthBudget.subtract(spent);
        return new MonthlyBudgetDetails(monthBudget, spent, remaining);
    }

    public record MonthlyBudgetDetails(BigDecimal monthBudget, BigDecimal spent, BigDecimal remaining) {}
}
