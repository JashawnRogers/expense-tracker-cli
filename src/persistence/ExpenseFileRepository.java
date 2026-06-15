package persistence;

import domain.Category;
import domain.Expense;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public class ExpenseFileRepository {
    private final Map<Long, Expense> expensesMap;
    private static final String CLASSPATH = "/Users/jashawnrogers/IdeaProjects/ExpenseTrackerCLI/src/files/";

    public ExpenseFileRepository(Map<Long, Expense> expensesMap) {
        this.expensesMap = expensesMap;
    }

    public PrintWriter createFile(String fileName) throws IOException {
        FileWriter file = new FileWriter(CLASSPATH + fileName.toLowerCase().trim());

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

    private Map<Long, Expense> mapFileData(List<List<String>> fileData) {
        Map<Long, Expense> expenses = new LinkedHashMap<>();
        for (List<String> row : fileData) {
            Long id = Long.parseLong(row.getFirst());
            String description = row.get(1);
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(row.get(2)));
            Category category = Category.valueOf(row.get(3));
            LocalDate date = LocalDate.parse(row.getLast());

            expenses.put(id ,Expense.createFromExisting(id, description, amount,category,date));
        }

        return expenses;
    }

}
