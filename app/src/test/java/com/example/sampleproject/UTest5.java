package com.example.sampleproject;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class UTest5 {


    @Test
    public void zauzetostOrganizatora() {
        String organizator = "ne moze";
        if ("11:30".compareTo("11:30") < 0 || "10:30".compareTo("12:30") > 0) organizator="moze";

        assertEquals("moze"
                , organizator);
    }
}