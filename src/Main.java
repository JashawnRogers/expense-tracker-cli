import domain.ExpenseTracker;
import persistence.ExpenseFileRepository;
import ui.ExpenseCLI;

public class Main {
    public static void main(String[] args) {
        ExpenseTracker expenseTracker = new ExpenseTracker();
        ExpenseFileRepository repository = new ExpenseFileRepository(expenseTracker);
        ExpenseCLI expenseCLI = new ExpenseCLI(expenseTracker, repository);
        expenseCLI.generateUI();
    }
}
