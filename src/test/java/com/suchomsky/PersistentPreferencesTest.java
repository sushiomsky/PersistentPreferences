/*
 * *
 *  * ${PROJECT_NAME}
 *  * Copyright (c) ${YEAR} Dennis Suchomsky <dennis.suchomsky@gmail.com>
 *  *
 *  *  This program is free software: you can redistribute it and/or modify
 *  *  it under the terms of the GNU General Public License as published by
 *  *  the Free Software Foundation, either version 3 of the License, or
 *  *  (at your option) any later version.
 *  *
 *  *  This program is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License
 *  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setParam() throws Exception {
        preferences = new PersistentPreferences("testtable");
        Assert.assertTrue(preferences.setParam("testparam", "testvalue"));
        preferences = null;
    }

    @Test
    public void getParam() throws Exception {
        preferences = new PersistentPreferences("testtable");
        Assert.assertTrue(preferences.getParam("testparam").equals("testvalue"));
        preferences = null;
    }

    @Test
    public void updateParam() throws Exception {
        preferences = new PersistentPreferences("testtable");
        Assert.assertTrue(preferences.setParam("testparam", "testvalue"));
        Assert.assertTrue(preferences.getParam("testparam").equals("testvalue"));
        Assert.assertTrue(preferences.setParam("testparam", "testvalueupdate"));
        Assert.assertTrue(preferences.getParam("testparam").equals("testvalueupdate"));
        preferences = null;
    }

    @Test
    public void deleteTable() throws Exception {
        preferences = new PersistentPreferences("testtable");
        Assert.assertTrue(preferences.deleteTable());
        preferences = null;

        PersistentPreferences preferencesCustomDB = new PersistentPreferences("testtable", "test.db");
        Assert.assertTrue(preferencesCustomDB.deleteTable());

    }
}