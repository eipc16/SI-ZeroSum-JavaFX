package SI.utils;

import java.util.List;

public class CustomUtils {

    public static <T> void replace(List<T> sourceList, int left, int right) {
        try {
            T tempLeft = sourceList.get(left);
            T tempRight = sourceList.get(right);

            sourceList.set(left, tempRight);
            sourceList.set(right, tempLeft);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public static <T, D extends Comparable<D>> void sortArray(List<T> sourceList, List<D> coeffs) {
        for(int i = 0; i < sourceList.size() - 1; i++) {
            for(int j = i + 1; j < sourceList.size(); j++) {
                if(coeffs.get(i).compareTo(coeffs.get(j)) < 0) {
                    replace(sourceList, i, j);
                    replace(coeffs, i, j);
                }
            }
        }
    }
}
