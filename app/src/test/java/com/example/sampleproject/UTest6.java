package com.example.sampleproject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UTest6 {


    @Test
    public void zauzetostOrganizatora() {
        String organizator = "ne moze";
        if ("11:30".compareTo("11:29") < 0 || "10:30".compareTo("12:30") > 0) organizator="moze";

        assertEquals("ne moze"
                , organizator);
    }
}