package com.example.braguia2;

import static org.junit.Assert.assertEquals;

import com.example.braguia2.model.User.User;

import org.junit.Before;
import org.junit.Test;

public class UserTest {
    private User user;

    @Before
    public void setup(){
        user = new User();
        user.setUsername("user");
        user.setFirst_name("fname");
        user.setLast_name("lname");
        user.setUser_type("p");
        user.setEmail("mail");
        user.setIs_active(false);
        user.setIs_staff(false);
        user.setIs_superuser(false);
    }

    @Test
    public void userUsernameTest(){
        assertEquals("user",user.getUsername());
    }

    @Test
    public void userFirstNameTest(){
        assertEquals("fname",user.getFirst_name());
    }

    @Test
    public void userLastNameTest(){
        assertEquals("lname",user.getLast_name());
    }

    @Test
    public void userTypeTest(){
        assertEquals("p",user.getUser_type());
    }

    @Test
    public void userEmailTest(){
        assertEquals("mail",user.getEmail());
    }

    @Test
    public void userIsStaffTest(){
        assertEquals(false, user.getIs_staff());
    }

    @Test
    public void userIsActiveTest(){
        assertEquals(false, user.getIs_active());
    }

    @Test
    public void userIsSuperTest(){
        assertEquals(false, user.getIs_superuser());
    }


}
