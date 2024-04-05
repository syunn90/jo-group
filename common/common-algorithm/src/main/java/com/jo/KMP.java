package com.jo;

/**
 * @author xtc
 * @date 2024/4/5
 */
public class KMP {

    /**
     * = kmp algorithm
     * @param s
     * @param t
     * @return
     */
    public static int match(String s, String t) {

        char[] s_arr = s.toCharArray();
        char[] t_arr = t.toCharArray();

        int[] next = nextArray(t_arr);

        int i = 0, j = 0;

        while (i < s_arr.length && j < t_arr.length) {
            if (j == -1 || s_arr[i] == t_arr[j]) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        if (j == t_arr.length) {
            return i - j;
        } else {
            return -1;
        }
    }

    private static int[] nextArray(char[] t) {

        int[] next = new int[t.length];
        next[0] = -1;
        next[1] = 0;
        int k;
        for (int j = 2; j < t.length; j++) {
            k = next[j - 1];
            while (k != -1) {
                if (t[j - 1] == t[k]) {
                    next[j] = k + 1;
                    break;
                } else {
                    k = next[k];
                }
                next[j] = 0;
            }
        }
        return next;
    }
}
