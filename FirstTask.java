import java.util.*;
import java.util.regex.*;

public class FirstTask {
    public static void main(String[] args) throws InterruptedException {
        String text = """
                Hi, I’m A.

                Contact me at a@short.com or support@example.org.

                You can also reach us via our assistant: info@company.net.

                Call us at 01012121212 or 01156789012 or 01234567890 or 01512345678.

                My friends: Al, Bo, Ann, Joe, Z, K, Moe.

                Random words: supercalifragilisticexpialidocious, ok, i, no.
                """;

        StringBuilder sharedText = new StringBuilder(text);

        List<String> shortNames = Collections.synchronizedList(new ArrayList<>());
        List<String> phoneNumbers = Collections.synchronizedList(new ArrayList<>());

        Thread nameFinder = new Thread(() -> {
            Pattern namePattern = Pattern.compile("\\b[A-Z][a-zA-Z]{0,1}\\b");
            Matcher matcher = namePattern.matcher(text);
            while (matcher.find()) {
                shortNames.add(matcher.group());
            }
        });

        Thread emailHider = new Thread(() -> {
            Pattern emailPattern = Pattern.compile("[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}");
            Matcher matcher = emailPattern.matcher(sharedText.toString());
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, "[EMAIL_HIDDEN]");
            }
            matcher.appendTail(sb);
            synchronized (sharedText) {
                sharedText.setLength(0);
                sharedText.append(sb.toString());
            }
        });

        Thread phoneExtractor = new Thread(() -> {
            Pattern phonePattern = Pattern.compile("\\b\\d{11}\\b|\\+?\\d{2,3}[- ]?\\d{3}[- ]?\\d{3}[- ]?\\d{3}\\b");
            Matcher matcher = phonePattern.matcher(text);
            while (matcher.find()) {
                phoneNumbers.add(matcher.group());
            }
        });

        // Start and wait for name and phone extractors
        nameFinder.start();
        phoneExtractor.start();
        nameFinder.join();
        phoneExtractor.join();

        // Hide emails after name and phone extraction
        emailHider.start();
        emailHider.join();

        // Print results
        System.out.println(shortNames);
        System.out.println(sharedText);
        System.out.println(phoneNumbers);
    }
}
