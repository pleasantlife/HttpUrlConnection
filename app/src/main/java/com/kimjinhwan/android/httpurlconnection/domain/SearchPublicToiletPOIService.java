package com.kimjinhwan.android.httpurlconnection.domain;

/**
 * Created by XPS on 2017-06-13.
 */

public class SearchPublicToiletPOIService {

        private RESULT RESULT;

        private int list_total_count;

        private Row[] row;

        public RESULT getRESULT ()
        {
            return RESULT;
        }

        public void setRESULT (RESULT RESULT)
        {
            this.RESULT = RESULT;
        }

        public int getList_total_count ()
        {
            return list_total_count;
        }

        public void setList_total_count (int list_total_count)
        {
            this.list_total_count = list_total_count;
        }

        public Row[] getRow ()
        {
            return row;
        }

        public void setRow (Row[] row)
        {
            this.row = row;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [RESULT = "+RESULT+", list_total_count = "+list_total_count+", row = "+row+"]";
        }

}
