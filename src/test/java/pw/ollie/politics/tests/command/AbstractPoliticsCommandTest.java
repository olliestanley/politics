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
package pw.ollie.politics.tests.command;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import pw.ollie.politics.AbstractPoliticsTest;
import pw.ollie.politics.command.PoliticsBaseCommand;
import pw.ollie.politics.command.PoliticsCommandManager;
import pw.ollie.politics.mock.AdminPlayerMock;

import org.junit.Assert;

import java.util.List;

public abstract class AbstractPoliticsCommandTest extends AbstractPoliticsTest {
    private PoliticsCommandManager commandManager;

    @Override
    public void setUp() {
        super.setUp();

        this.createDefaultUniverse();

        this.commandManager = plugin.getCommandManager();
    }

    @Override
    public void runTest() {
        PoliticsBaseCommand baseCommand = this.commandManager.getPoliticsCommand(getBaseCommand().toLowerCase());
        Assert.assertNotNull(baseCommand);

        PlayerMock admin = new AdminPlayerMock(server, "admin");
        server.addPlayer(admin);
        for (String args : getTestingArguments()) {
            baseCommand.execute(admin, getBaseCommand(), args.split(" "));
        }
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    public abstract String getBaseCommand();

    // get a List of the sets of arguments to run the command with for the test, in order
    public abstract List<String> getTestingArguments();
}
