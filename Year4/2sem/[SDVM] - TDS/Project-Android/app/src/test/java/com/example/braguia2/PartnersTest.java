package com.example.braguia2;

import static org.junit.Assert.assertEquals;

import com.example.braguia2.model.App.Partners;

import org.junit.Before;
import org.junit.Test;

public class PartnersTest {
    
    private Partners p;
    
    @Before
    public void setup(){
        p = new Partners();
        p.setPartnerName("Um");
        p.setPartnerDesc("Melhor academia do mundo");
        p.setPartnerMail("uminho@spam.com");
        p.setPartnerPhone("0000000");
        p.setPartnerUrl("www.um.com");
        p.setPartnerApp("www.um.com");
    }
    
    @Test
    public void partnerNameTest(){
        assertEquals("Um", p.getPartnerName());
    }

    @Test
    public void partnerDescTest(){
        assertEquals("Melhor academia do mundo", p.getPartnerDesc());
    }

    @Test
    public void partnerPhontTest(){
        assertEquals("0000000",p.getPartnerPhone());
    }

    @Test
    public void partnerMailTest(){
        assertEquals("uminho@spam.com", p.getPartnerMail());
    }

    @Test
    public void partnerUrlTest(){
        assertEquals("www.um.com", p.getPartnerUrl());
    }

    @Test
    public void partnerAppTest(){
        assertEquals("www.um.com", p.getPartnerApp());
    }
}
