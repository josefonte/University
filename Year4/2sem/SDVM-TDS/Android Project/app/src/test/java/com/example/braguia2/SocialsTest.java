package com.example.braguia2;

import static org.junit.Assert.assertEquals;

import com.example.braguia2.model.App.Socials;

import org.junit.Before;
import org.junit.Test;

public class SocialsTest {

    private Socials socials;

    @Before
    public void setup() {
        socials = new Socials();
        socials.setSocialName("Facebook");
        socials.setSocialUrl("https://facebook.com");
        socials.setSocialShareLink("https://example.com/share/facebook");
        socials.setSocialApp("BraGuia");

    }

    @Test
    public void socialNameTest(){
        assertEquals("Facebook", socials.getSocialName());
    }

    @Test
    public void socialUtlTest(){
        assertEquals("https://facebook.com", socials.getSocialUrl());
    }

    @Test
    public void socialShareLinkTest(){
        assertEquals("https://example.com/share/facebook", socials.getSocialShareLink());
    }

    @Test
    public void socialSocialAppTest(){
        assertEquals("BraGuia", socials.getSocialApp());
    }
}
