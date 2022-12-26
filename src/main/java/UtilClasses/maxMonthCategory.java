package UtilClasses;

import java.util.*;

import static ServerUtils.ClientRequestProcess.titleSearchMaxCategories;

public class maxMonthCategory {
    String category;
    Object sum;

    public maxMonthCategory(String category, Object sum) {
        this.category = category;
        this.sum = sum;
    }

    public static maxMonthCategory maxMonthCategorySearch(RequestCollection requestCollection,
                                                          Map<String, String> categoriesMap) {

        RequestClass maxMonthSearch =
                requestCollection.requestCollection
                        .stream()
                        .max(Comparator.comparing(RequestClass::getRequestMonth))
                        .orElseThrow(NoSuchElementException::new);

        String maxMonth = maxMonthSearch.getRequestMonth();

        RequestClass maxSumSearch =
                requestCollection.requestCollection
                        .stream()
                        .filter(y -> y.getRequestMonth().equals(maxMonth))
                        .max(Comparator.comparing(RequestClass::getRequestSum))
                        .orElseThrow(NoSuchElementException::new);
        String title = titleSearchMaxCategories(maxSumSearch, categoriesMap);

        return new maxMonthCategory(title, maxSumSearch.getRequestSum());
    }

    @Override
    public String toString() {
        return category + " " + sum;
    }
}
