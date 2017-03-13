/*
 *  ========================================================================
 *  dcf-exercises
 *  ========================================================================
 *  
 *  This file is part of dcf-exercises.
 *  
 *  dcf-exercises is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  dcf-exercises is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with dcf-exercises.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2017, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
 */
package hu.unimiskolc.iit.distsys.competition;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;

import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class SingleEliminationTournament {

	public static ArrayList<Class<? extends CloudProvider>> runCompetition(
			ArrayList<Class<? extends CloudProvider>> currentCompetitors) throws Exception {
		if (currentCompetitors.size() % 2 != 0) {
			throw new RuntimeException("Cannot process non-even membered tournaments");
		}
		ArrayList<Class<? extends CloudProvider>> winners = new ArrayList<>();
		ArrayList<Class<? extends CloudProvider>> losers = new ArrayList<>();
		for (int i = 0; i < currentCompetitors.size() - 1; i += 2) {
			int miniRounds = 0;
			SingleSet ss;
			// Repeat each match until someone is a clear winner
			do {
				ss = new SingleSet(currentCompetitors.get(i), currentCompetitors.get(i + 1));
				ss.runSet();
			} while (ss.getPointsForTeamOne() == 1 && miniRounds++ < 10);
			// If after 10 sets we still don't have a clear winner we randomly
			// pick one
			winners.add(ss.getPointsForTeamOne() == 3 ? ss.cpOne
					: (ss.getPointsForTeamOne() == 1 ? (RandomUtils.nextInt(0, 2) == 0 ? ss.cpOne : ss.cpTwo)
							: ss.cpTwo));
			losers.add(ss.cpOne == winners.get(winners.size() - 1) ? ss.cpTwo : ss.cpOne);
		}
		if (winners.size() > 1) {
			winners = runCompetition(winners);
			winners.addAll(runCompetition(losers));
		} else {
			winners.add(losers.get(0));
		}
		return winners;
	}

}
