package tn.fst.tp;

public class ArrayOperations {
    
    int[] array;
    ArrayOperations() {
        this.array = new int[0];
    }

    ArrayOperations(int[] x) {
        // Constructor with array parameter
        this.array = x;
    }

    public int numZero(int[] x) {
        if (x == null) {
            throw new NullPointerException();
        }
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] == 0) {
                count++;
            }
        }
        return count;
    }
}
