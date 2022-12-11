package UtilClasses;

import java.util.*;

import static ServerClient.Main.titleSearchMaxCategories;

public class maxYearCategory {
    String category;
    Object sum;

    public maxYearCategory(String category, Object sum) {
        this.category = category;
        this.sum = sum;
    }

    public static maxYearCategory maxYearCategorySearch(RequestCollection requestCollection,
                                                        Map<String, String> categoriesMap) {

        RequestClass maxYearSearch =
                requestCollection.requestCollection
                        .stream()
                        .max(Comparator.comparing(RequestClass::getRequestYear))
                        .orElseThrow(NoSuchElementException::new);

        String maxYear = maxYearSearch.getRequestYear();

        RequestClass maxSumSearch =
                requestCollection.requestCollection
                        .stream()
                        .filter(y -> y.getRequestYear().equals(maxYear))
                        .max(Comparator.comparing(RequestClass::getRequestSum))
                        .orElseThrow(NoSuchElementException::new);
        String title = titleSearchMaxCategories(maxSumSearch, categoriesMap);

        return new maxYearCategory(title, maxSumSearch.getRequestSum());
    }

    @Override
    public String toString() {
        return category + " " + sum;
    }
}
