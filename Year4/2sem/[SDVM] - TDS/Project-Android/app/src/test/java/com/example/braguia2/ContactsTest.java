package com.example.braguia2;

import static org.junit.Assert.assertEquals;

import com.example.braguia2.model.App.Contacts;

import org.junit.Before;
import org.junit.Test;

public class ContactsTest {
    private Contacts c;

    @Before
    public void setup() {
        c = new Contacts();
        c.setContactName("c1");
        c.setContactDesc("contacto 1");
        c.setContactPhone("00000");
        c.setContactMail("mail");
        c.setContactUrl("www.cont.com");
        c.setContactApp("c");

    }

    @Test
    public void contactNameTest(){
        assertEquals("c1", c.getContactName());
    }

    @Test
    public void contactDescTest(){
        assertEquals("contacto 1", c.getContactDesc());
    }

    @Test
    public void contactPhontTest(){
        assertEquals("00000",c.getContactPhone());
    }

    @Test
    public void contactMailTest(){
        assertEquals("mail", c.getContactMail());
    }

    @Test
    public void contactUrlTest(){
        assertEquals("www.cont.com", c.getContactUrl());
    }

    @Test
    public void contactAppTest(){
        assertEquals("c", c.getContactApp());
    }



}
