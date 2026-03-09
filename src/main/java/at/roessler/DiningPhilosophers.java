package at.roessler;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.random.RandomGenerator;

public class DiningPhilosophers {
    // Params to provoke a deadlock
//    private final int MAX_THINKING_TIME = 0;
//    private final int MAX_EATING_TIME = 5000;
//    private final int NUMBER_OF_PHILOSOPHERS = 10;

    private final int MAX_THINKING_TIME = 50;
    private final int MAX_EATING_TIME = 500;
    private final int NUMBER_OF_PHILOSOPHERS = 10;

    private volatile boolean isRunning = true;
    private final ReentrantLock[] forks;

    private final AtomicLong totalEatingTimeNanos = new AtomicLong(0L);
    private final long startTime;

    public DiningPhilosophers() {
        this.forks = new ReentrantLock[this.NUMBER_OF_PHILOSOPHERS];
        for (int i = 0; i < this.NUMBER_OF_PHILOSOPHERS; i++) {
            this.forks[i] = new ReentrantLock();
        }

        this.startTime = System.nanoTime();
    }

    public static void main(String[] args) {
        DiningPhilosophers dp = new DiningPhilosophers();

        System.out.println("Press ENTER to stop...");


        // InteruptionThread generated with ChatGPT
        new Thread(() -> {
            try {
                System.in.read();
                dp.isRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        Thread[] philosopherThreads = new Thread[dp.NUMBER_OF_PHILOSOPHERS];

        for (int i = 0; i < dp.NUMBER_OF_PHILOSOPHERS; i++) {
            int id = i;
            Thread t = new Thread(() -> {
                try {
                    while (dp.isRunning) {
                        dp.operate(id);
                    }
                } catch (InterruptedException e) {
                    System.out.println("ERROR: " + e.getCause());
                    Thread.currentThread().interrupt();
                }
            });

            philosopherThreads[i] = t;
            t.start();
        }

        for (Thread t : philosopherThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        dp.printReport();

    }

    private void operate(int idxPhil) throws InterruptedException {
        RandomGenerator rdm = RandomGenerator.getDefault();

        int t = rdm.nextInt(0, this.MAX_THINKING_TIME + 1);
        Thread.sleep(t);
        System.out.println("Phil<" + idxPhil + "> finished thinking (" + t + "ms)");

        // Deadlock prevention (own) ... macht genau das gleiche wie im Task lol
//        int firstForkIdx = idxPhil;
//        int secondForkIdx = (idxPhil + 1) % this.NUMBER_OF_PHILOSOPHERS;

        // int firstLock = Math.min(firstForkIdx, secondForkIdx);
        // int secondLock = Math.max(firstForkIdx, secondForkIdx);

        // Deadlock prevention (task)
        int firstLock = idxPhil % 2 == 1 ? idxPhil : idxPhil + 1;
        int secondLock = idxPhil % 2 == 1 ? idxPhil + 1 : idxPhil;

        firstLock = firstLock % this.NUMBER_OF_PHILOSOPHERS;
        secondLock = secondLock % this.NUMBER_OF_PHILOSOPHERS;


        forks[firstLock].lock();
        try {
            System.out.println("Phil<" + idxPhil + "> took first fork<" + firstLock + ">");

            forks[secondLock].lock();
            try {
                System.out.println("Phil<" + idxPhil + "> took second fork<" + secondLock + ">");

                int t2 = rdm.nextInt(0, this.MAX_EATING_TIME + 1);

                // Messe tatsächliche Essensdauer (sleep + akkumulieren)
                long eatStart = System.nanoTime();
                Thread.sleep(t2);
                long eatEnd = System.nanoTime();

                long eatenNanos = eatEnd - eatStart;
                this.totalEatingTimeNanos.addAndGet(eatenNanos);

                System.out.println("Phil<" + idxPhil + "> finished eating (" + t2 + "ms)");
            } finally {
                forks[secondLock].unlock();
            }
        } finally {
            forks[firstLock].unlock();
        }
    }

    private void printReport() {
        long endTime = System.nanoTime();
        long totalNanos = endTime - startTime;
        long totalEatNanos = this.totalEatingTimeNanos.get();

        double totalWallMs = totalNanos / 1_000_000.0;
        double totalEatMs = totalEatNanos / 1_000_000.0;

        System.out.println("\n=== Report ===");
        System.out.printf("Gesamte Laufzeit: %.3f ms\n", totalWallMs);
        System.out.printf("Essenszeit (alle Philosophen): %.3f ms\n", totalEatMs);
        if (totalNanos > 0) {
            double percent = (double) totalEatNanos / (double) totalNanos * 100.0;
            System.out.printf("Essenszeit / Laufzeit = %.2f%%\n", percent);
        }
    }
}