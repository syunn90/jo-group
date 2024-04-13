package com.jo.sort;

import java.util.Arrays;

/**
 * 插入排序
 * @author xtc
 * @date 2024/4/13
 * @description
 * 通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。
 */
public class InsertSort {

    public static int[] sort(int[] sourceArray) {
        // 对 arr 进行拷贝，不改变参数内容
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

        // 从下标为1的元素开始选择合适的位置插入，因为下标为0的只有一个元素，默认是有序的
        for (int i = 1; i < arr.length; i++) {

            // 记录要插入的数据
            int tmp = arr[i];

            // 从已经排序的序列最右边的开始比较，找到比其小的数
            int j = i;
            while (j > 0 && tmp < arr[j - 1]) {
                arr[j] = arr[j - 1];
                j--;
            }

            // 存在比其小的数，插入
            if (j != i) {
                arr[j] = tmp;
            }
            for (int i1 : arr) {
                System.out.print(i1);
            }
            System.out.println();
        }
        return arr;
    }

    public static void main(String[] args) {
        int[] sort = sort(new int[]{1, 3, 5, 2, 6, 4, 2, 8});
        System.out.print(1);
    }
}
