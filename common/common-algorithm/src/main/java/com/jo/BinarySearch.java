package com.jo;

/**
 * @author xtc
 * @date 2024/4/12
 */
public class BinarySearch {


    public static int binarySearch(int[] arr ,int target){
        int low = 0, high = arr.length - 1;
        if(target < arr[low] || target > arr[high] || low > high){
            return -1;
        }
        while (low <= high) {
            int mid = low + (high - low) >>> 2;
            int midVal = arr[mid];

            if (midVal == target) {
                return mid;  // 找到目标元素
            } else if (midVal < target) {
                low = mid + 1;  // 目标元素在 mid 的右边，所以下一次查找从 mid + 1 开始
            } else {
                high = mid - 1;  // 目标元素在 mid 的左边，所以下一次查找从 mid - 1 开始
            }
        }

        return -1;  // 未找到目标元素
    }

    public static int recursionBinarySearch(int[] arr,int key,int low,int high){

        if(key < arr[low] || key > arr[high] || low > high){
            return -1;
        }

        int middle = (low + high) >>> 2;			//初始中间位置
        if(arr[middle] > key){
            //比关键字大则关键字在左区域
            return recursionBinarySearch(arr, key, low, middle - 1);
        }else if(arr[middle] < key){
            //比关键字小则关键字在右区域
            return recursionBinarySearch(arr, key, middle + 1, high);
        }else {
            return middle;
        }

    }

    public static void main(String[] args) {
        int[] arr = {1, 3, 5, 7, 9, 11, 13, 15, 17};
        int target = 0;
        int i = binarySearch(arr, target);
        int result = recursionBinarySearch(arr, target,0,arr.length-1);
        System.out.println("Element found at index: " + result);


    }
}
