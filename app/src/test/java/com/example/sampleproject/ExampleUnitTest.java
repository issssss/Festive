package com.example.sampleproject;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 *
 */




public class ExampleUnitTest {

    SimpleDateFormat formatter= new SimpleDateFormat("MM/dd/yy");

    @Test
    public void parsiranjeIsCorrect() {
        Calendar c = Calendar.getInstance();
        c.set(2020, 0, 1);

        assertEquals("01/01/20"
                , formatter.format(c.getTime()));
    }
}