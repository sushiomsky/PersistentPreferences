package com.suchomsky;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Dennis Suchomsky on 31.03.17.
 */
public class PersistentPreferencesTest {
    private PersistentPreferences preferences;

    @Before
    public void setUp() throws Exception {
        preferences = new PersistentPreferences("testtable");
    }

    @After
    public void tearDown() throws Exception {
       // preferences.deleteTable();
    }

    @Test
    public void setParam() throws Exception {
        preferences = new PersistentPreferences("testtable");
        Assert.assertTrue(preferences.setParam("testparam", "testvalue"));
    }

    @Test
    public void getParam() throws Exception {
       Assert.assertTrue(preferences.getParam("testparam").equals("testvalue"));
       Assert.assertTrue(preferences.setParam("testparam", "testvalueupdate"));
       Assert.assertTrue(preferences.getParam("testparam").equals("testvalueupdate"));
    }

}