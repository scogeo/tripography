package com.tripography.web.controller;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class HomeControllerTest {
    // Treat as singleton for all tests to match Spring behavior.
    private static HomeController controller;

    @BeforeClass
    public static void initController() {
        controller = new HomeController();
    }

    @Test
    public void testWelcomePage() {
        //assertEquals("welcome", controller.home(null));

        Model model = mock(Model.class);
        //assertEquals("welcome", controller.home(null, model));
    }

    @Test
    public void testHomePage() {
        Principal mock = mock(Principal.class);
        Model model = mock(Model.class);
        //assertEquals("home", controller.home(mock, model));
    }
}
