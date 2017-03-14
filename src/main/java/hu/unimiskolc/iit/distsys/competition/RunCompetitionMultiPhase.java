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

public class RunCompetitionMultiPhase {

	/**
	 * Validates the argument list, loads them as class objects, and randomizes
	 * their order for fairness
	 * 
	 * @param args
	 *            the unparsed command line argument list
	 * @return the class objects representing all cloud providers to take part
	 *         in the competition
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Class<? extends CloudProvider>> parseCompetingClassNames(String[] args) {
		if (args.length < 8) {
			System.err.println("There are not enough providers listed in the cli argument list.");
			System.err.println("Please use the following format: RunCompetition [FullyQualifiedClassName]*");
			System.exit(1);
		}
		ArrayList<Class<? extends CloudProvider>> preList = new ArrayList<Class<? extends CloudProvider>>();
		for (int i = 0; i < args.length; i++) {
			try {
				preList.add((Class<? extends CloudProvider>) Class.forName(args[i]));
			} catch (Exception e) {
				System.err.println("Argument " + i + " does not seem to be a fully qualified classname: " + args[i]);
				System.err.println("Make sure all arguments are referring to classes in your classpath!");
				System.exit(1);
			}
		}
		Collections.shuffle(preList);
		return preList;
	}

	public static void main(String[] args) throws Exception {
		// Preparing the teams
		ArrayList<Class<? extends CloudProvider>> preList = parseCompetingClassNames(args);
		TeamCompetition[] competitions = new TeamCompetition[(args.length + 3) / 4];
		for (int i = 0; i < competitions.length; i++) {
			competitions[i] = new TeamCompetition(true);
		}
		for (int i = 0; i < preList.size(); i++) {
			// Round robin assignment to teams
			competitions[i % competitions.length].addToCompetitors(preList.get(i));
		}
		// By now teams are set, we are good to go
		
		ArrayList<ProviderRanking> rankings = new ArrayList<ProviderRanking>();
		System.err.println("Starting team competition phase!");
		int groupIndex = 1;
		for (TeamCompetition tc : competitions) {
			System.err.println("Starting team " + groupIndex);
			tc.arrangeSets();
			tc.runSets();
			// merge the rankings across all groups
			rankings.addAll(tc.getRankedList());
			System.err.println("Completed team " + groupIndex);
			groupIndex++;
		}
		System.err.println("Team competitions finished. Merged rankings:");
		
		// We sort all providers to see who performed the best in each group
		Collections.sort(rankings);
		ArrayList<Class<? extends CloudProvider>> topProviders = new ArrayList<Class<? extends CloudProvider>>();
		for (ProviderRanking pr : rankings) {
			// Only the first 8 teams go to the knockout phase
			if (topProviders.size() < 8) {
				topProviders.add(pr.provider);
			}
			System.err.println(pr);
		}
		
		//Knockout phase
		System.err.println("Sinlge elimination tournament starts.......");
		topProviders = SingleEliminationTournament.runCompetition(topProviders);
		
		// Results
		System.err.println("Sinlge elimination tournament completed final league table:");
		for (int i = 0; i < topProviders.size(); i++) {
			System.err.println((i + 1) + ". " + topProviders.get(i).getName());
		}
	}

}
