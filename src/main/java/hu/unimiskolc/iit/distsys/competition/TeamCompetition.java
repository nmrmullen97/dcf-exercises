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
import java.util.HashMap;

import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class TeamCompetition {
	private final boolean applyCompetitorLimit;
	private final ArrayList<Class<? extends CloudProvider>> competitors = new ArrayList<Class<? extends CloudProvider>>();
	private final ArrayList<SingleSet> sets = new ArrayList<SingleSet>();
	private final HashMap<Class<? extends CloudProvider>, Integer> resultsTable = new HashMap<Class<? extends CloudProvider>, Integer>();

	public TeamCompetition(boolean applyCompetitorLimit) {
		this.applyCompetitorLimit = applyCompetitorLimit;
	}

	public void addToCompetitors(Class<? extends CloudProvider> toAdd) {
		competitors.add(toAdd);
		if (applyCompetitorLimit && competitors.size() > 4) {
			throw new RuntimeException("Could not allow more than 4 members in a group!");
		}
	}

	public int getSize() {
		return competitors.size();
	}

	public void arrangeSets() throws Exception {
		if (competitors.size() < 3) {
			throw new RuntimeException("No group is allowed to have less than 3 members!");
		}
		for (int i = 0; i < competitors.size() - 1; i++) {
			for (int j = i + 1; j < competitors.size(); j++) {
				sets.add(new SingleSet(competitors.get(i), competitors.get(j)));
			}
		}
	}

	public void runSets() throws Exception {
		if (sets.size() > 0) {
			for (SingleSet currSet : sets) {
				// Run the current matches
				currSet.runSet();

				// Accumulate the scores:
				Integer currValue = resultsTable.get(currSet.cpOne);
				if (currValue == null) {
					currValue = new Integer(0);
				}
				currValue += currSet.getPointsForTeamOne();
				resultsTable.put(currSet.cpOne, currValue);

				currValue = resultsTable.get(currSet.cpTwo);
				if (currValue == null) {
					currValue = new Integer(0);
				}
				currValue += currSet.getPointsForTeamTwo();
				resultsTable.put(currSet.cpTwo, currValue);
			}
		} else {
			throw new RuntimeException("Should arrange the sets first!");
		}
	}

	public ArrayList<ProviderRanking> getRankedList() {
		if (resultsTable.isEmpty()) {
			throw new RuntimeException("Should run the sets first!");
		} else {
			ArrayList<ProviderRanking> ranking = new ArrayList<ProviderRanking>();
			for (Class<? extends CloudProvider> cp : competitors) {
				ranking.add(new ProviderRanking(cp, resultsTable.get(cp)));
			}
			Collections.sort(ranking);
			return ranking;
		}
	}

}
