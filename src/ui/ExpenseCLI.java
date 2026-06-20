package ui;

import domain.Category;
import domain.Expense;
import domain.ExpenseTracker;
import persistence.ExpenseFileRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExpenseCLI {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private final Scanner scanner = new Scanner(System.in);

    private final ExpenseTracker expenseTracker;
    private final ExpenseFileRepository repository;

    public ExpenseCLI(ExpenseTracker expenseTracker, ExpenseFileRepository repository) {
        this.expenseTracker = expenseTracker;
        this.repository = repository;
    }

    public void generateUI() {

        int userMenuSelection = -1;

        while (userMenuSelection != 0) {
            System.out.println("\n--- Expense Tracker CLI ---");
            System.out.println("1. Add expense");
            System.out.println("2. View all expenses");
            System.out.println("3. View expenses by category");
            System.out.println("4. View expenses by month");
            System.out.println("5. View total spending");
            System.out.println("6. View total spending by category");
            System.out.println("7. Delete expense");
            System.out.println("8. Save expenses");
            System.out.println("9. Load expenses");
            System.out.println("10. Set monthly budget");
            System.out.println("11. View largest expense");
            System.out.println("12. View category percentage report");
            System.out.println("0. Exit");
            System.out.print("Enter your choice here: ");

//            To prevent InputMismatchException
            if (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                scanner.next();
                continue;
            }

            userMenuSelection = scanner.nextInt();
//            Calling nextInt() leaves a hidden newline (\n) character in the buffer
//            Adding nextLine() right after safely clears it out
//            so your upcoming UI methods don't accidentally skip user prompts
            scanner.nextLine();

            switch (userMenuSelection) {
                case 1:
                    addExpenseUI();
                    break;
                case 2:
                    viewAllExpensesUI();
                    break;
                case 3:
                    viewExpensesByCategoryUI();
                    break;
                case 4:
                    viewExpensesByMonthUI();
                    break;
                case 5:
                    viewTotalSpendingUI();
                    break;
                case 6:
                    viewTotalSpendingByCategoryUI();
                    break;
                case 7:
                    deleteExpenseUI();
                    break;
                case 8:
                    saveExpensesUI();
                    break;
                case 9:
                    loadExpensesUI();
                    break;
                case 10:
                    setMonthlyBudgetUI();
                    break;
                case 11:
                    viewLargestExpenseUI();
                    break;
                case 12:
                    viewCategoryPercentagesUI();
                    break;
                case 0:
                    System.out.println("Exiting program.");
                    scanner.close();
                default:
                    System.out.println("Invalid selection. Please try again or quit.");
                    break;
            }
        }


    }

    private void addExpenseUI() {
        System.out.print("Enter the expense description: ");
        String description = scanner.nextLine();

        System.out.print("Enter amount of expense: $");
        BigDecimal amount = scanner.nextBigDecimal();

        Category category = getCategory();

        System.out.print("Enter date of expense (MM-dd-yyyy): ");
        String dateString = scanner.next();
        LocalDate date = parseDate(dateString);

        Expense expense = Expense.createNew(description, amount, category, date);

        expenseTracker.addExpense(expense);
        System.out.println("\n" + expense);
    }

    private void viewAllExpensesUI() {
        System.out.println("\n");
        List<Expense> expenses = expenseTracker.allExpenses();
        expenses.forEach(expense -> System.out.println(expense.toString()));
    }

    private void viewExpensesByMonthUI() {
        System.out.println("\n");
        System.out.print("Enter year and month of expenses to view (MM-yyyy): ");
        String stringDate = scanner.nextLine();

        YearMonth  yearMonth = parseYearMonth(stringDate);

        List<String> expenses = expenseTracker.findExpensesByMonth(yearMonth);

        for (String expense : expenses) {
            System.out.println(expense);
        }
    }

    private void viewTotalSpendingUI() {
        System.out.println("\n");
        System.out.println("Your total spending is: $" + expenseTracker.calculateTotalSpending());
    }

    private void viewTotalSpendingByCategoryUI() {
        System.out.println("\n");
        Map<Category, BigDecimal> categoryTotals = expenseTracker.calculateTotalByCategory();
        System.out.println("\n");
        categoryTotals.forEach((category, total) -> {
            System.out.println(category + ": $" + total);
        });
    }

    private void viewExpensesByCategoryUI() {
        System.out.println("\n");
        Category category = getCategory();
        List<Expense> expenses = expenseTracker.findExpensesByCategory(category);
        System.out.println("\n");
        for (Expense expense : expenses) {
            System.out.println(expense.toString());
        }
    }

    private void deleteExpenseUI() {
        System.out.print("\nEnter the ID of the expense you would like to delete: ");
        long expenseId = scanner.nextLong();
        expenseTracker.removeExpense(expenseId);
    }

    private void saveExpensesUI() {
        System.out.print("\nEnter a file name: ");
        String fileName = scanner.nextLine();
        repository.save(fileName);
    }

    private void loadExpensesUI() {
        System.out.print("\nEnter a file name: ");
        String fileName = scanner.nextLine();
        repository.load(fileName);
    }

    private void setMonthlyBudgetUI() {
        System.out.print("\nEnter month for budget (MM-yyyy): ");
        YearMonth yearMonth = parseYearMonth(scanner.nextLine());

        System.out.print("Enter your budget for the month: $");
        BigDecimal budget = scanner.nextBigDecimal();

        expenseTracker.setMonthlyBudget(budget, yearMonth);
        ExpenseTracker.MonthlyBudgetDetails budgetDetails = expenseTracker.calculateMonthlyBudget(yearMonth);

        Month month = yearMonth.getMonth();
        int year = yearMonth.getYear();

        String ui = """
                \n
                Month: %s %s
                Budget: $%s
                Spent: $%s
                Remaining: $%s
                """.formatted(
                        month,
                        year,
                        budgetDetails.monthBudget(),
                        budgetDetails.spent(),
                        budgetDetails.remaining()
                );

        System.out.println(ui);
    }

    private void viewLargestExpenseUI() {
        Expense expense = expenseTracker.findLargestExpense();
        System.out.println("\n Largest expense: " + expense.toString());
    }

    private void viewCategoryPercentagesUI() {
        System.out.println("\nCategory Percentage Report");
        Map<Category, BigDecimal> categories = expenseTracker.calculateCategoryPercentage();

        categories.forEach((category, percentage) -> {
            System.out.println(category + ": " + percentage + "%");
        });
    }

    private Category getCategory() {
        System.out.println("Select a category:");
        String categorySelectionMenu =
                """
                 1. Groceries
                 2. Rent
                 3. Utilities
                 4. Transportation
                 5. Food
                 6. Entertainment
                 7. Debt
                 8. Other""";

        System.out.println(categorySelectionMenu);
        System.out.print("Enter choice here: ");
        int userCategorySelection = scanner.nextInt();

        return switch (userCategorySelection) {
            case 1 -> Category.GROCERIES;
            case 2 -> Category.RENT;
            case 3 -> Category.UTILITIES;
            case 4 -> Category.TRANSPORTATION;
            case 5 -> Category.FOOD;
            case 6 -> Category.ENTERTAINMENT;
            case 7 -> Category.DEBT;
            case 8 -> Category.OTHER;
            default -> throw new IllegalArgumentException("Invalid category selection.");
        };
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(("Invalid date input"), e.getParsedString(), e.getErrorIndex());
        }
    }

    private YearMonth parseYearMonth(String date) {
        int year = Integer.parseInt(date.substring(3, 7));
        int month = Integer.parseInt(date.substring(0, 2));
        return YearMonth.of(year, month);
    }

}
