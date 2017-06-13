package com.kimjinhwan.android.httpurlconnection.domain;

/**
 * Created by XPS on 2017-06-13.
 */

public class Data {

        private SearchPublicToiletPOIService SearchPublicToiletPOIService;

        public SearchPublicToiletPOIService getSearchPublicToiletPOIService () {
            return SearchPublicToiletPOIService;
        }

        public void setSearchPublicToiletPOIService (SearchPublicToiletPOIService SearchPublicToiletPOIService) {
            this.SearchPublicToiletPOIService = SearchPublicToiletPOIService;
        }

        @Override
        public String toString() {
            return "ClassPojo [SearchPublicToiletPOIService = "+SearchPublicToiletPOIService+"]";
        }

}
