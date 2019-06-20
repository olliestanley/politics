/*
 * This file is part of Politics.
 *
 * Copyright (c) 2019 Oliver Stanley
 * Politics is licensed under the Affero General Public License Version 3.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pw.ollie.politics.tests;

import pw.ollie.politics.AbstractPoliticsTest;
import pw.ollie.politics.activity.ActivityManager;
import pw.ollie.politics.activity.activities.ConfirmationActivity;
import pw.ollie.politics.util.PoliticsEventCounter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.bukkit.entity.Player;

public final class ActivitiesTest extends AbstractPoliticsTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    private boolean callbackRun = false;

    @Override
    @Test
    public void runTest() {
        // setup
        PoliticsEventCounter eventCounter = this.registerEventCounter();

        Player subject = server.getPlayer(0);
        ActivityManager activityManager = plugin.getActivityManager();

        Assert.assertFalse(activityManager.isActive(subject));
        activityManager.beginActivity(new ConfirmationActivity(subject.getUniqueId(), () -> callbackRun = true));
        Assert.assertEquals(1, eventCounter.getActivityBegins());
        Assert.assertTrue(activityManager.isActive(subject));
        Assert.assertTrue(activityManager.getActivity(subject).get() instanceof ConfirmationActivity);
        Assert.assertTrue(activityManager.getActivity(subject).get().complete());
        Assert.assertTrue(callbackRun);
        activityManager.endActivity(subject);
        Assert.assertFalse(activityManager.isActive(subject));
        Assert.assertEquals(1, eventCounter.getActivityEnds());
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }
}
