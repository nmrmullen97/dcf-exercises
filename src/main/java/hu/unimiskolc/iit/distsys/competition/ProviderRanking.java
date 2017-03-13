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

import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class ProviderRanking implements Comparable<ProviderRanking> {
	public final Class<? extends CloudProvider> provider;
	public final int score;

	public ProviderRanking(Class<? extends CloudProvider> cp, int score) {
		this.provider = cp;
		this.score = score;
	}

	@Override
	public int compareTo(ProviderRanking o) {
		return -((Integer) score).compareTo(o.score);
	}

	@Override
	public String toString() {
		return provider.getName() + " score: " + score;
	}
}
