package domain;

import java.util.HashSet;
import java.util.Set;

public enum Category {
    GROCERIES,
    RENT,
    UTILITIES,
    TRANSPORTATION,
    FOOD,
    ENTERTAINMENT,
    DEBT,
    OTHER;

    private static final Set<String> LOOKUP_SET = new HashSet<>();

    static {
        for (Category category : Category.values()) {
            LOOKUP_SET.add(category.name());
        }
    }

    public static boolean isValid(String value) {
        return value != null && LOOKUP_SET.contains(value);
    }
}
