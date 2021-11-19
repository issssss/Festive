package com.example.sampleproject;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 *
 */




public class UTest3 {

    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy");

    @Test
    public void parsiranjeIsCorrect() {
        Calendar c = Calendar.getInstance();
        int provjera = 2;
        try {
            provjera = c.getTime().compareTo(formatter.parse("01/01/20"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertEquals(1
                , provjera);
    }
}
