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
import java.util.Collections;

import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class SingleSet implements Scorer {
	private final SingleMatch[] matches = new SingleMatch[10];
	public final Class<? extends CloudProvider> cpOne, cpTwo;
	private int sumSubScoreOne = -1, sumSubScoreTwo = -1;

	public SingleSet(Class<? extends CloudProvider> cpOne, Class<? extends CloudProvider> cpTwo) throws Exception {
		this.cpOne = cpOne;
		this.cpTwo = cpTwo;
		ArrayList<Class<? extends CloudProvider>> cps = new ArrayList<>();
		cps.add(cpOne);
		cps.add(cpTwo);
		for (int i = 0; i < matches.length; i++) {
			Collections.shuffle(cps);
			matches[i] = new SingleMatch(cps.get(0), cps.get(1));
		}
	}

	public void runSet() throws Exception {
		System.err.println("~~~~~~~ Starting set ~~~~~~~");
		System.err.println(this);
		if (sumSubScoreOne < 0) {
			// On the first run we initialize the scores
			sumSubScoreOne = 0;
			sumSubScoreTwo = 0;
		}
		for (SingleMatch m : matches) {
			if (!m.isMatchRan()) {
				// Matches ran only once
				m.runMatch();
				System.err.println(m);

				// Accumulate scores
				if (m.cpOne == cpOne) {
					sumSubScoreOne += m.getPointsForTeamOne();
					sumSubScoreTwo += m.getPointsForTeamTwo();
				} else {
					sumSubScoreOne += m.getPointsForTeamTwo();
					sumSubScoreTwo += m.getPointsForTeamOne();
				}
			}
		}
		System.err.println(this);
		System.err.println("~~~~~~~ End of set ~~~~~~~");
	}

	@Override
	public int getPointsForTeamOne() {
		return sumSubScoreOne > sumSubScoreTwo ? 3 : (sumSubScoreOne == sumSubScoreTwo ? 1 : 0);
	}

	@Override
	public int getPointsForTeamTwo() {
		final int t1pts = getPointsForTeamOne();
		return t1pts == 3 ? 0 : (t1pts == 0 ? 3 : 1);
	}

	@Override
	public String toString() {
		return "Set between " + cpOne.getName() + " and " + cpTwo.getName()
				+ (sumSubScoreOne < 0 ? ""
						: (" scores: " + sumSubScoreOne + ":" + sumSubScoreTwo + " points: " + getPointsForTeamOne()
								+ "/" + getPointsForTeamTwo()));
	}
}
