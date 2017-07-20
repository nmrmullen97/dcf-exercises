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
 *  (C) Copyright 2015, Gabor Kecskemeti (kecskemeti@iit.uni-miskolc.hu)
 */

package hu.unimiskolc.iit.distsys.forwarders;

import java.util.HashMap;
import java.util.Map;

import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;

public class PMForwarder extends PhysicalMachine implements ForwardingRecorder {
	private boolean reqVMCalled = false;
	private boolean allocVMCalled = false;
	private boolean deployVMCalled = false;
	private final double reliMult;
	private final double maxConsumption;

	/**
	 * 
	 * @param cores
	 * @param perCorePocessing
	 * @param memory
	 * @param disk
	 * @param onD
	 * @param offD
	 * @param powerTransitions
	 * @param reliMult
	 *            The higher the value the less reliable the machine is
	 */
	public PMForwarder(double cores, double perCorePocessing, long memory, Repository disk, int onD, int offD,
			Map<String, PowerState> powerTransitions, double reliMult) {
		super(cores, perCorePocessing, memory, disk, onD, offD, powerTransitions);
		this.reliMult = reliMult;
		PowerState maxConsumingState = powerTransitions.get(PhysicalMachine.State.RUNNING.toString());
		maxConsumption = maxConsumingState.getConsumptionRange() + maxConsumingState.getMinConsumption();
	}

	public void resetForwardingData() {
		reqVMCalled = true;
		allocVMCalled = false;
		deployVMCalled = false;
	}

	public boolean isReqVMCalled() {
		return reqVMCalled;
	}

	public boolean isDeployVMCalled() {
		return deployVMCalled;
	}

	public boolean isAllocVMCalled() {
		return allocVMCalled;
	}

	public double getReliMult() {
		return reliMult;
	}

	public double getMaxConsumption() {
		return maxConsumption;
	}

	@Override
	public VirtualMachine[] requestVM(VirtualAppliance va, ResourceConstraints rc, Repository vaSource, int count)
			throws hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException, NetworkException {
		reqVMCalled = true;
		return super.requestVM(va, rc, vaSource, count);
	}

	@Override
	public VirtualMachine[] requestVM(VirtualAppliance va, ResourceConstraints rc, Repository vaSource, int count,
			HashMap<String, Object> schedulingConstraints)
			throws hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException, NetworkException {
		reqVMCalled = true;
		return super.requestVM(va, rc, vaSource, count, schedulingConstraints);
	}

	@Override
	public void deployVM(VirtualMachine vm, ResourceAllocation ra, Repository vaSource)
			throws hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException, NetworkException {
		deployVMCalled = true;
		super.deployVM(vm, ra, vaSource);
	}

	@Override
	public ResourceAllocation allocateResources(ResourceConstraints requested, boolean strict,
			int allocationValidityLength)
			throws hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException {
		allocVMCalled = true;
		return super.allocateResources(requested, strict, allocationValidityLength);
	}

	@Override
	public String toString() {
		return "(PMForwarder: reli=" + reliMult + " totPower=" + maxConsumption + " " + super.toString() + ")";
	}
}
