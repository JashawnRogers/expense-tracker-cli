package persistence;

import domain.Expense;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpenseFileRepository {
    private final Map<Long, Expense> expensesMap;

    public ExpenseFileRepository(Map<Long, Expense> expensesMap) {
        this.expensesMap = expensesMap;
    }

    public PrintWriter createFile(String fileName) throws IOException {
        FileWriter file = new FileWriter(
                "/Users/jashawnrogers/IdeaProjects/ExpenseTrackerCLI/src/files/"
                        + fileName.toLowerCase().trim());

        return new PrintWriter(file);
    }

    public void writeToFile(PrintWriter writer) {
        List<String> csvLines = new ArrayList<>();

        for (Map.Entry<Long, Expense> expense : expensesMap.entrySet()) {
            String lineItem = "%s,%s,$%s,%s,%s".formatted(
                    expense.getKey(),
                    expense.getValue().getDescription(),
                    expense.getValue().getAmount(),
                    expense.getValue().getCategory().toString(),
                    expense.getValue().getDate()
            );

            int endingIndex = lineItem.indexOf(",");
            int startingIndex = 0;
            String id = lineItem.substring(startingIndex, endingIndex);

            if (expense.getKey().toString().equals(id)) {
                System.err.println("Line item with ID " + expense.getValue().getId() + " Already exists in file.");
                continue;
            }

            csvLines.add(lineItem);
        }

        if (csvLines.isEmpty()) {
            System.out.println("No data was added to ");
            return;
        }

        csvLines.forEach(writer::println);

        if (writer.checkError()) {
            System.err.println("An internal error occurred while writing CSV data.");
        }
    }

    public void save(String fileName) {
        try (PrintWriter writer = createFile(fileName)) {
            writeToFile(writer);
            System.out.println(" CSV file successfully generated.");
        } catch (IOException e) {
            System.err.println("Failed to initialize CSV file : " + e.getMessage());
        }
    }

}
