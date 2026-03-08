package at.roessler;

import java.util.concurrent.locks.ReentrantLock;
import java.util.random.RandomGenerator;

public class DiningPhilosophers {
    // Params to provoke a deadlock
//    private final int MAX_THINKING_TIME = 0;
//    private final int MAX_EATING_TIME = 5000;
//    private final int NUMBER_OF_PHILOSOPHERS = 10;

    private final int MAX_THINKING_TIME = 500;
    private final int MAX_EATING_TIME = 500;
    private final int NUMBER_OF_PHILOSOPHERS = 10;

    private boolean isRunning = true;
    private ReentrantLock[] forks;

    public DiningPhilosophers() {
        this.forks = new ReentrantLock[this.NUMBER_OF_PHILOSOPHERS];
        for (int i = 0; i < this.NUMBER_OF_PHILOSOPHERS; i++) {
            this.forks[i] = new ReentrantLock();
        }
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

            t.start();
        }
    }

    private void operate(int idxPhil) throws InterruptedException {
        RandomGenerator rdm = RandomGenerator.getDefault();

        int t = rdm.nextInt(0, this.MAX_THINKING_TIME + 1);
        Thread.sleep(t);
        System.out.println("Phil<" + idxPhil + "> finished thinking (" + t + "ms)");

        int firstForkIdx = idxPhil;
        int secondForkIdx = (idxPhil + 1) % this.NUMBER_OF_PHILOSOPHERS;

        // Deadlock safe
        int firstLock = Math.min(firstForkIdx, secondForkIdx);
        int secondLock = Math.max(firstForkIdx, secondForkIdx);

//        int firstLock = firstForkIdx;
//        int secondLock = secondForkIdx;

        forks[firstLock].lock();
        try {
            System.out.println("Phil<" + idxPhil + "> took first fork<" + firstForkIdx + ">");

            forks[secondLock].lock();
            try {
                System.out.println("Phil<" + idxPhil + "> took second fork<" + secondForkIdx + ">");

                int t2 = rdm.nextInt(0, this.MAX_EATING_TIME + 1);
                System.out.println("Phil<" + idxPhil + "> finished eating (" + t2 + "ms)");
            } finally {
                forks[secondLock].unlock();
            }
        } finally {
            forks[firstLock].unlock();
        }
    }
}