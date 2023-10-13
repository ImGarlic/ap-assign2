package dylan.dahub.model;

import dylan.dahub.exception.InvalidDateException;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.service.PostManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public record Post(int ID, String author, String content, int likes, int shares, LocalDateTime dateTime) {

    public String getDateTimeString() {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }

    // Formats the number given to show the rounded representation. Used for likes and shares to be displayed
    // more cleanly. e.g. 14,765 likes would be shown as 14.8K likes.
    public static String formatNumber(int number) {
        try {
            if (number > 9999 && number < 999950) {
                return String.format("%.1fK", number / 1000.0);
            } else if (number >= 999950 && number < 999950000) {
                return String.format("%.1fM", number / 1000000.0);
            } else if (number >= 999950000) {
                return "1B+";
            }
            return String.valueOf(number);
        } catch (NumberFormatException e) {
            return "1B+";
        }
    }

    public String toString() {
        return String.format("%d,%s,%s,%d,%d,%s", this.ID(), this.author(), this.content(), this.likes(), this.shares(), this.getDateTimeString());
    }

    // Converts a single comma-separated string to a post. Expects all post values to be present
    // and of the correct type, otherwise throws an InvalidPostException.
    public static Post convertFromCSV(String importedPost) throws InvalidPostException {
        List<String> postValues = new ArrayList<>();
        int likes, shares;
        String content, author, dateTime;

        try (Scanner rowScanner = new Scanner(importedPost)) {
            String COMMA_DELIMITER = ",";
            rowScanner.useDelimiter(COMMA_DELIMITER);

            while(rowScanner.hasNext()) {
                postValues.add(rowScanner.next());
            }
            author = postValues.get(1);
            content = postValues.get(2);
            likes = Integer.parseInt(postValues.get(3));
            shares = Integer.parseInt(postValues.get(4));
            dateTime = postValues.get(5);

            return new Post(0, author, content, likes, shares, convertDateTime(dateTime));
        } catch (IndexOutOfBoundsException | NumberFormatException | InvalidDateException e) {
            throw new InvalidPostException(e.getMessage());
        }
    }

    // Converts the date-time string into a LocalDateTime to ensure it is of the correct format.
    public static LocalDateTime convertDateTime(String dateString) throws InvalidDateException {
        LocalDateTime dateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

        try {
            dateTime = LocalDateTime.parse(dateString, formatter);
            return dateTime;
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(e.getMessage());
        }
    }
}
