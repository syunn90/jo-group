package com.jo.sort;

import java.util.Arrays;

/**
 * @author xtc
 * @date 2024/4/13
 * @description
 * 选择排序是一种简单直观的排序算法，无论什么数据进去都是 O(n²) 的时间复杂度。
 * 所以用到它的时候，数据规模越小越好。唯一的好处可能就是不占用额外的内存空间。
 */
public class SelectionSort {
    public static int[] sort(int[] sourceArray) {
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

        // 总共要经过 N-1 轮比较
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;

            // 每轮需要比较的次数 N-i
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    // 记录目前能找到的最小值元素的下标
                    min = j;
                }
            }

            // 将找到的最小值和i位置所在的值进行交换
            if (i != min) {
                int tmp = arr[i];
                arr[i] = arr[min];
                arr[min] = tmp;
            }

        }
        return arr;
    }

    public static void main(String[] args) {
        int[] sort = sort(new int[]{1, 2, 3, 4, 9, 6, 7, 8, 5});
        System.out.println();
    }
}
