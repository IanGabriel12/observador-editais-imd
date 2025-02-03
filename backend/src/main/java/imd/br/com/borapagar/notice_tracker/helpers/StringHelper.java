package imd.br.com.borapagar.notice_tracker.helpers;

public class StringHelper {

    public static String truncateEllipsis(String description, int max_length) {
        if (description != null && description.length() > max_length) {
            return description.substring(0, max_length - 3) + "...";
        } else {
            return description;
        }
    }

}
