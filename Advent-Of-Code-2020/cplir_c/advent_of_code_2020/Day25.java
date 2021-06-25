package cplir_c.advent_of_code_2020;

public final class Day25 {

    public static void main(String[] args) {
        findEncryptionKey(INPUT);
        findEncryptionKey(EXAMPLE);
    }
    private static void findEncryptionKey(int[] input) {
        var cardPublicKey = input[0];
        var doorPublicKey = input[1];
        var cardLoopSize  = findLoopSize(cardPublicKey);
        var doorLoopSize  = findLoopSize(doorPublicKey);
        System.out.println("card loop size " + cardLoopSize);
        System.out.println("door loop size " + doorLoopSize);
        var cardEncryptionKey = transformSubjectNumber(doorPublicKey, cardLoopSize);
        var doorEncryptionKey = transformSubjectNumber(cardPublicKey, doorLoopSize);
        if (doorEncryptionKey != cardEncryptionKey) {
            throw new AssertionError(doorEncryptionKey + " " + cardEncryptionKey);
        }
        System.out.println("encryption key: " + cardEncryptionKey);
    }
    private static int findLoopSize(int cardPublicKey) {
        var value = 1;
        int i;
        for (i = 0; value != cardPublicKey; ++i) {
            value *= 7;
            value %= 2020_12_27;
        }
        return i;
    }
    static long transformSubjectNumber(long subjectNumber, int loopSize) {
        var value = 1L;
        for (; loopSize > 0; --loopSize) {
            value *= subjectNumber;
            value %= 2020_12_27L;
        }
        return value;
    }

    static final int[] INPUT   = {1_614_360, 7_734_663};
    static final int[] EXAMPLE = {5_764_801, 17_807_724};
}
