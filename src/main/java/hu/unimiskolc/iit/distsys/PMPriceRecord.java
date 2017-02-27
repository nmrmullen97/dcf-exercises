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
 *  (C) Copyright 2015, Gabor Kecskemeti (kecskemeti@iit.uni-miskolc.hu)
 */
package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.forwarders.PMForwarder;

public class PMPriceRecord {
	public static final double hourlyAmortisaiton = ((double) Constants.anHour) / Constants.machineLifeTime;
	public static final double perMachineBaseCost = 3000; // GBP
	public static final double maxResourceCombination = ExercisesBase.maxCoreCount * ExercisesBase.maxProcessingCap
			* ExercisesBase.maxMem * ExercisesBase.maxDisk;

	public final PMForwarder pm;
	private double currentMachinePrice;
	private final double amortizationRate;

	public PMPriceRecord(PhysicalMachine forMe) {
		pm = (PMForwarder) forMe;
		ResourceConstraints pmComputePower = forMe.getCapacities();
		double combinedResources = pmComputePower.getTotalProcessingPower() * pmComputePower.getRequiredMemory()
				* forMe.localDisk.getMaxStorageCapacity();
		double performanceRatio = Math.pow(combinedResources / maxResourceCombination, 0.33);
		double inverseEnergyRatio = Math.pow(ExercisesBase.maxMaxPower / pm.getMaxConsumption(),0.8);

		currentMachinePrice = inverseEnergyRatio * performanceRatio * perMachineBaseCost / pm.getReliMult();
		amortizationRate = currentMachinePrice * hourlyAmortisaiton;
	}

	public void amortizefor(final int hours) {
		currentMachinePrice = Math.max(0, currentMachinePrice - hours * amortizationRate);
	}

	public double getCurrentMachinePrice() {
		return currentMachinePrice;
	}

	@Override
	public String toString() {
		return "(PMPrice: GBP" + currentMachinePrice + " " + pm.toString() + ")";
	}
}