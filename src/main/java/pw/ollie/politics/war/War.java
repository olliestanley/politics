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
package pw.ollie.politics.war;

import pw.ollie.politics.Politics;
import pw.ollie.politics.data.Storable;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.universe.Universe;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

public final class War implements Storable {
    private final int aggressor;
    private final int defender;

    private int aggressorScore = 0;
    private int defenderScore = 0;

    private boolean active;

    public War(int aggressor, int defender) {
        if (aggressor == defender) {
            throw new IllegalArgumentException("cannot create a war where the aggressor is also the defender");
        }

        this.aggressor = aggressor;
        this.defender = defender;
    }

    War(BasicBSONObject bObj) {
        aggressor = bObj.getInt("aggressor");
        defender = bObj.getInt("defender");
        aggressorScore = bObj.getInt("aggressor-score");
        defenderScore = bObj.getInt("defender-score");
    }

    public Universe getUniverse() {
        return getAggressor().getUniverse();
    }

    public Group getAggressor() {
        return Politics.getGroupById(aggressor);
    }

    public int getAggressorId() {
        return aggressor;
    }

    public Group getDefender() {
        return Politics.getGroupById(defender);
    }

    public int getDefenderId() {
        return defender;
    }

    public int getAggressorScore() {
        return aggressorScore;
    }

    public void setAggressorScore(int aggressorScore) {
        this.aggressorScore = aggressorScore;
    }

    public int getDefenderScore() {
        return defenderScore;
    }

    public void setDefenderScore(int defenderScore) {
        this.defenderScore = defenderScore;
    }

    public Group getWinningGroup() {
        return aggressorScore > defenderScore ? getAggressor() : getDefender();
    }

    public boolean involves(int groupId) {
        return defender == groupId || aggressor == groupId;
    }

    public boolean involves(Group group) {
        return involves(group.getUid());
    }

    public boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public BSONObject toBSONObject() {
        BasicBSONObject result = new BasicBSONObject();
        result.put("aggressor", aggressor);
        result.put("defender", defender);
        result.put("aggressor-score", aggressorScore);
        result.put("defender-score", defenderScore);
        return result;
    }

    @Override
    public boolean canStore() {
        return true;
    }
}
