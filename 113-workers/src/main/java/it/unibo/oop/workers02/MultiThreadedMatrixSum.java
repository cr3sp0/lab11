package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedMatrixSum implements SumMatrix {
    private int tnumber;

    public MultiThreadedMatrixSum(int tnumber) {
        this.tnumber = tnumber;
    }

    @Override
    public double sum(double[][] matrix) {

        final List<Worker> workersList = new ArrayList<>(tnumber);
        for (int tID = 0; tID < tnumber; tID++) {
            workersList.add(new Worker(startingPointCalculator(matrix.length, tID),
                    startingPointCalculator(matrix.length, tID + 1) - 1, matrix));
        }

        for (final Worker w : workersList) {
            w.start();
        }

        long sum = 0;
        for (final Worker w : workersList) {
            try {
                w.join();
                sum += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }

    private int startingPointCalculator(int size, int tID) {
        if (tID == this.tnumber - 1 && tID != 0) {
            return size;
        }
        return tID * (size / this.tnumber);
    }

    private static class Worker extends Thread {
        private final int start;
        private final int end;
        private final double[][] matrix;
        private long sum;

        Worker(int start, int end, double[][] matrix) {
            super();
            this.start = start;
            this.end = end;
            this.matrix = matrix;
            this.sum = 0;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public synchronized void run() {
            System.out.println("Working from row " + start + " to row " + end);
            for (int i = start; i <= end; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    sum += matrix[i][j];
                }
            }
        }

        public synchronized long getResult() {
            return this.sum;
        }
    }
}
