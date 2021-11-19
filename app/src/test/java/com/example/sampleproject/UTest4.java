package com.example.sampleproject;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class UTest4 {


    @Test
    public void zauzetostOrganizatora() {
        String organizator = "ne moze";
        if ("18:30".compareTo("19:30") < 0 || "16:30".compareTo("20:30") > 0) organizator="moze";

        assertEquals("moze"
                , organizator);
    }
}