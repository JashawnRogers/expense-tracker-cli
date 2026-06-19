package persistence;

import domain.Category;
import domain.Expense;
import domain.ExpenseTracker;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public class ExpenseFileRepository {
    private final ExpenseTracker expenseTracker;
    private static final String CLASSPATH = "/Users/jashawnrogers/IdeaProjects/ExpenseTrackerCLI/src/files/";

    public ExpenseFileRepository(ExpenseTracker expenseTracker) {
        this.expenseTracker = expenseTracker;
    }

    private PrintWriter createFile(String fileName) throws IOException {
        FileWriter file = new FileWriter(CLASSPATH + fileName.toLowerCase().trim());
        return new PrintWriter(file);

    }

    public void writeToFile(PrintWriter writer) {
        List<String> csvLines = new ArrayList<>();

        for (Map.Entry<Long, Expense> expense : expenseTracker.expenses.entrySet()) {
            String lineItem = "%s,%s,%s,%s,%s".formatted(
                    expense.getKey(),
                    expense.getValue().getDescription(),
                    expense.getValue().getAmount(),
                    expense.getValue().getCategory().toString(),
                    expense.getValue().getDate()
            );

            csvLines.add(lineItem);
        }

        if (csvLines.isEmpty()) {
            System.out.println("No data was added to the file.");
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

            System.out.println("CSV file successfully generated.");
        } catch (IOException e) {
            System.err.println("Failed to initialize CSV file : " + e.getMessage());
        }
    }

    public void load(String fileName) {
        String validatedFileName = fileName.toLowerCase().strip();

        try (Stream<String> lines = Files.lines(Path.of(CLASSPATH + validatedFileName))){
            List<List<String>> fileData = lines
                    .map(line -> Arrays.asList(line.split(",")))
                    .toList();

            mapFileData(fileData);

            System.out.println("File successfully loaded.");
        } catch (IOException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    private void mapFileData(List<List<String>> fileData) {
        for (List<String> row : fileData) {
            Long id = Long.parseLong(row.getFirst());
            String description = row.get(1);
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(row.get(2)));
            Category category = Category.valueOf(row.get(3));
            LocalDate date = LocalDate.parse(row.getLast());

            expenseTracker.addExpense(Expense.createFromExisting(id, description, amount,category,date));
        }
    }

}
