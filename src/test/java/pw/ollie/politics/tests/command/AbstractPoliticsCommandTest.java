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

import pw.ollie.politics.AbstractPoliticsTest;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsCommandManager;

public abstract class AbstractPoliticsCommandTest extends AbstractPoliticsTest {
    protected PoliticsCommandManager commandManager;

    @Override
    public void setUp() {
        super.setUp();

        this.createDefaultUniverse();

        this.commandManager = plugin.getCommandManager();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @FunctionalInterface
    protected interface CommandTestRunner {
        void run() throws CommandException;
    }

    protected boolean throwsCommandException(CommandTestRunner runnable) {
        try {
            runnable.run();
            return false;
        } catch (CommandException e) {
            return true;
        }
    }
}
