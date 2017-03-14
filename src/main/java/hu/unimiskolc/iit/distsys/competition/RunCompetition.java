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

public class RunCompetition {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		TeamCompetition[] competitions = new TeamCompetition[(args.length + 3) / 4];
		for (int i = 0; i < competitions.length; i++) {
			competitions[i] = new TeamCompetition();
		}
		for (int i = 0; i < args.length; i++) {
			// Round robin assignment to teams
			competitions[i % competitions.length]
					.addToCompetitors((Class<? extends CloudProvider>) Class.forName(args[i]));
		}
		ArrayList<ProviderRanking> rankings = new ArrayList<ProviderRanking>();
		System.err.println("Starting team competition phase!");
		for (TeamCompetition tc : competitions) {
			tc.arrangeSets();
			tc.runSets();
			rankings.addAll(tc.getRankedList());
		}
		System.err.println("Team competitions finished. Merged rankings:");
		Collections.sort(rankings);
		ArrayList<Class<? extends CloudProvider>> topProviders = new ArrayList<Class<? extends CloudProvider>>();
		for (ProviderRanking pr : rankings) {
			// Only the first 8 team goes to the knockout phase
			if (topProviders.size() < 8) {
				topProviders.add(pr.provider);
			}
			System.out.println(pr);
		}
		System.err.println("Sinlge elimination tournament starts.......");
		topProviders = SingleEliminationTournament.runCompetition(topProviders);
		System.err.println("Sinlge elimination tournament completed final league table:");
		for (int i = 0; i < topProviders.size(); i++) {
			System.err.println((i + 1) + ". " + topProviders.get(i).getName());
		}
	}

}
