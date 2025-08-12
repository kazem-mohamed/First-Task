import java.util.LinkedList;
import java.util.Queue;

public class PrinterQueueExample {
    private static final Queue<String> messageQueue = new LinkedList<>();

    public static void main(String[] args) {
        Thread printer1 = new Thread(new Printer("Printer 1"));
        Thread printer2 = new Thread(new Printer("Printer 2"));
        Thread sender = new Thread(new Sender());

        printer1.start();
        printer2.start();
        sender.start();
    }

    static class Printer implements Runnable {
        private String name;

        public Printer(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message;
                    synchronized (messageQueue) {
                        while (messageQueue.isEmpty()) {
                            messageQueue.wait();
                        }
                        message = messageQueue.poll();
                    }
                    String formattedMessage = message.substring(0, 1).toUpperCase() + message.substring(1);
                    System.out.printf("%s %s (length %d)%n", name, formattedMessage, formattedMessage.length());
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Sender implements Runnable {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 4; i++) {
                    synchronized (messageQueue) {
                        messageQueue.add("hello everyone!");
                        messageQueue.notifyAll();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
