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

import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class RunCompetitionSingleTeam {
	public static void main(String[] args) throws Exception {
		TeamCompetition competition = new TeamCompetition(false);
		ArrayList<Class<? extends CloudProvider>> preList = RunCompetitionMultiPhase.parseCompetingClassNames(args, 3);
		// All competitors into a single team competition
		for (Class<? extends CloudProvider> cp : preList) {
			competition.addToCompetitors(cp);
		}
		ArrayList<ProviderRanking> rankings;
		System.out.println("Starting competition!");
		competition.arrangeSets();
		competition.runSets();
		rankings = competition.getRankedList();
		System.out.println("Competition finished. League table:");
		for (int i = 0; i < rankings.size(); i++) {
			System.out.println((i + 1) + ". " + rankings.get(i));
		}

	}
}
