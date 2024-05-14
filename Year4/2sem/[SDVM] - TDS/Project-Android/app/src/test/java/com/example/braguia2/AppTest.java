package com.example.braguia2;

import static org.junit.Assert.assertEquals;

import com.example.braguia2.model.App.App;
import com.example.braguia2.model.App.Contacts;
import com.example.braguia2.model.App.Partners;
import com.example.braguia2.model.App.Socials;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AppTest {

    private App app;

    @Before
    public void setup(){

        List<Socials> socialsList = new ArrayList<>();
        Socials socials = new Socials();
        socials.setSocialName("Facebook");
        socials.setSocialUrl("https://facebook.com");
        socials.setSocialShareLink("https://example.com/share/facebook");
        socials.setSocialApp("BraGuia");
        socialsList.add(socials);

        List<Partners> partnersList = new ArrayList<>();
        Partners p = new Partners();
        p.setPartnerName("Um");
        p.setPartnerDesc("Melhor academia do mundo");
        p.setPartnerMail("uminho@spam.com");
        p.setPartnerPhone("0000000");
        p.setPartnerUrl("www.um.com");
        p.setPartnerApp("www.um.com");
        partnersList.add(p);

        List<Contacts> contactsList = new ArrayList<>();
        Contacts c = new Contacts();
        c.setContactName("c1");
        c.setContactDesc("contacto 1");
        c.setContactPhone("00000");
        c.setContactMail("mail");
        c.setContactUrl("www.cont.com");
        c.setContactApp("c");
        contactsList.add(c);

        app = new App(
                "BraGuia",
                "Isto é a descrição da app",
                "Landing text",
                socialsList,
                partnersList,
                contactsList);

    }

    @Test
    public void appNameTest(){
        String expected = "BraGuia";

        assertEquals(expected, app.getApp_name());
    }

    @Test
    public void appDescTest(){
        String expected = "Isto é a descrição da app";
        assertEquals(expected, app.getApp_desc());
    }

    @Test
    public void appLandingTextTest(){
        String expected = "Landing text";
        assertEquals(expected, app.getApp_landing_page_text());
    }

    @Test
    public void appSocialsTest(){
        List<Socials> aux = app.getSocials();
        Socials socialAux = null;
        if(aux!=null){
            socialAux = aux.get(0);
        }
        
        assertEquals(1, app.getSocials().size());
    }

    @Test
    public void appPartnersTest(){
        assertEquals(1, app.getPartners().size());
    }

    @Test
    public void appContactsTest(){
        assertEquals(1,app.getContacts().size());
    }




















}
