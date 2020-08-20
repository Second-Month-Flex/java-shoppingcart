package com.lambdaschool.shoppingcart.services;

import com.lambdaschool.shoppingcart.ShoppingcartApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShoppingcartApplication.class)
public class UserServiceImplTest {


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    @Autowired
    UserService userService;

    @Test
    public void findAll() {
        assertEquals(3, userService.findAll().size());
    }

    @Test
    public void findUserById() {
        assertEquals(1, userService.findUserById(1).getUserid());
    }
}