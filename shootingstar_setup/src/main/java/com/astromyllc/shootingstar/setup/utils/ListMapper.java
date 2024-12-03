package com.astromyllc.shootingstar.setup.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListMapper<T> {
    public List<T> mapLists(List<T> listA, List<T> listB) {
        // Convert listB to a HashSet for fast lookup
        Set<T> setB = new HashSet<>(listB);

        // Create a new list to store the mapped results
        List<T> resultList = new ArrayList<>();

        // Iterate over listA and check for existence in setB
        for (T element : listA) {
            if (!setB.contains(element)) {
                // If element from listA is not in listB, add it to result
                resultList.add(element);
            }
        }

        // Optionally, you can merge listB with resultList if needed
        return resultList;
    }
}
