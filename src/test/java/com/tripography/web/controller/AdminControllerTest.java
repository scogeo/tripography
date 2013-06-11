package com.tripography.web.controller;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AdminControllerTest {

    // Treat as singleton for all tests to match Spring behavior.
    private static AdminController controller;

    @BeforeClass
    public static void initController() {
        controller = new AdminController();
    }

    @Test
    public void testMainPage() throws Exception {
        assertEquals("admin/index", controller.mainPage(null));
    }
}