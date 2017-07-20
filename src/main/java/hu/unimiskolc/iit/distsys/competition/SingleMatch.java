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
package hu.unimiskolc.iit.distsys.competition;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService.IaaSHandlingException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.Constants;
import hu.unimiskolc.iit.distsys.CostAnalyserandPricer;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.FaultInjector;
import hu.unimiskolc.iit.distsys.MultiCloudUser;
import hu.unimiskolc.iit.distsys.PMDeregPreparator;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class SingleMatch implements MultiCloudUser.CompletionCallback, Scorer {
	private ArrayList<DeferredEvent> obsolitionEvents = new ArrayList<DeferredEvent>();
	private int completeUserCount = 0;
	public static final int totalUserCount = 10;
	public static final int initialMachineCount = 30;
	private CostAnalyserandPricer ourAnalyser, competitionAnalyser;
	public final Class<? extends CloudProvider> cpOne, cpTwo;
	private boolean matchRan = false;

	public SingleMatch(Class<? extends CloudProvider> cpOne, Class<? extends CloudProvider> cpTwo) throws Exception {
		this.cpOne = cpOne;
		this.cpTwo = cpTwo;
	}

	@Override
	public void alljobsComplete() {
		completeUserCount++;
		if (completeUserCount == totalUserCount) {
			ourAnalyser.completeCostAnalysis();
			competitionAnalyser.completeCostAnalysis();
			FaultInjector.simulationisComplete = true;
			for (DeferredEvent oe : obsolitionEvents) {
				oe.cancel();
			}
		}
	}

	private CostAnalyserandPricer prepareIaaS(final IaaSService service, Class<? extends CloudProvider> provider)
			throws Exception {
		// Register for events on newly added PMs to model their obsolition
		service.subscribeToCapacityChanges(new VMManager.CapacityChangeEvent<PhysicalMachine>() {
			@Override
			public void capacityChanged(ResourceConstraints newCapacity, List<PhysicalMachine> affectedCapacity) {
				final boolean newRegistration = service.isRegisteredHost(affectedCapacity.get(0));
				if (newRegistration && !FaultInjector.simulationisComplete) {
					for (final PhysicalMachine pm : affectedCapacity) {
						obsolitionEvents.add(new DeferredEvent(Constants.machineLifeTime) {
							// To be done when the machine should become
							// obsolete
							@Override
							protected void eventAction() {
								obsolitionEvents.remove(this);
								// The life of the pm is over
								if (service.isRegisteredHost(pm)) {
									// The pm is not broken so far
									try {
										// Then let's wait for all its VMs and
										// then throw away the PM
										new PMDeregPreparator(pm, new PMDeregPreparator.DeregPreparedCallback() {
											@Override
											public void deregistrationPrepared(PMDeregPreparator forPM) {
												try {
													// The PM has became
													// obsolete
													service.deregisterHost(pm);
												} catch (IaaSHandlingException e) {
													throw new RuntimeException(e);
												}
											}
										}, true);
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								}
							}
						});
					}
				}
			}
		});

		// Every hour we set the PMs a small likely-hood to fail
		new FaultInjector(Constants.anHour, Constants.machineHourlyFailureRate, service, false);

		// Here we create the initial cloud infrastructures
		final int maxPMCount = 10000;
		Repository centralStorage = ExercisesBase.getNewRepository(maxPMCount);
		service.registerRepository(centralStorage);

		ArrayList<PhysicalMachine> pmlist = new ArrayList<PhysicalMachine>();
		long minSize = centralStorage.getMaxStorageCapacity();
		for (int i = 0; i < initialMachineCount; i++) {
			PhysicalMachine curr = ExercisesBase.getNewPhysicalMachine();
			pmlist.add(curr);
			minSize = Math.min(minSize, curr.localDisk.getMaxStorageCapacity());
		}
		service.bulkHostRegistration(pmlist);

		// We make sure the users will find the VM image required for their VMs
		// to start
		VirtualAppliance va = new VirtualAppliance("mainVA", 30, 0, false, minSize / 50);
		centralStorage.registerObject(va);

		// We configure the cost analyser and pricing provider to base their
		// services on the just created IaaS
		CostAnalyserandPricer theCostAnalyser = new CostAnalyserandPricer(service);
		CloudProvider myProvider = provider.newInstance();
		myProvider.setIaaSService(service);
		((IaaSForwarder) service).setQuoteProvider(myProvider);
		return theCostAnalyser;
	}

	public void runMatch() throws Exception {
		if (matchRan) {
			throw new RuntimeException("Attemted to run a match two times");
		} else {
			Timed.resetTimed();
			ExercisesBase.reset();
			long startTime = System.currentTimeMillis();
			PrintStream errOut = System.err;
			System.setErr(new PrintStream(new OutputStream() {
				@Override
				public void write(int arg0) throws IOException {
					// Ignore the standard output of
					// multiclouduser
				}
			}));

			// Prepares two providers with the same VM/PM schedulers
			IaaSService ourService, theCompetition;
			ourService = ExercisesBase.getNewIaaSService();
			do {
				theCompetition = ExercisesBase.getNewIaaSService();
			} while (theCompetition.pmcontroller.getClass() != ourService.pmcontroller.getClass()
					&& theCompetition.sched.getClass() != ourService.sched.getClass());

			// Ensures both IaaSServices are having their pricing providers
			ourAnalyser = prepareIaaS(ourService, cpOne);
			competitionAnalyser = prepareIaaS(theCompetition, cpTwo);

			int baseDelay = 0;
			final ArrayList<IaaSService> iaasList = new ArrayList<IaaSService>();
			iaasList.add(ourService);
			iaasList.add(theCompetition);
			// Ensures cloud users are started with a random delay in between
			// them
			for (int i = 0; i < totalUserCount; i++) {
				new DeferredEvent(baseDelay) {
					@Override
					protected void eventAction() {
						try {
							Collections.shuffle(iaasList);
							new MultiCloudUser(iaasList.toArray(new IaaSService[2]), SingleMatch.this);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				};
				baseDelay += RandomUtils.nextLong(Constants.anHour, Constants.machineLifeTime / 10);
			}

			// Does the actual simulation, makes sure no timed related issues
			// cause trouble
			while (!FaultInjector.simulationisComplete) {
				Timed.simulateUntil(Timed.getFireCount() + 24 * 60 * 60 * 1000);
			}
			System.setErr(errOut);
			matchRan = true;
			System.out.println("Duration of match was: " + (System.currentTimeMillis() - startTime) + "ms");
		}
	}

	private static int getScoring(CostAnalyserandPricer teamOne, CostAnalyserandPricer teamTwo, boolean ran) {
		if (ran) {
			final double balanceTeamOne = teamOne.getCurrentBalance();
			final double balanceTeamTwo = teamTwo.getCurrentBalance();
			if (balanceTeamOne > 0) {
				if (balanceTeamTwo < 0) {
					return 3;
				} else if (balanceTeamOne > balanceTeamTwo) {
					return 2;
				} else {
					return 1;
				}
			} else {
				return 0;
			}
		} else {
			throw new RuntimeException("Scoring attempt before any match");
		}
	}

	@Override
	public int getPointsForTeamOne() {
		return getScoring(ourAnalyser, competitionAnalyser, matchRan);
	}

	@Override
	public int getPointsForTeamTwo() {
		return getScoring(competitionAnalyser, ourAnalyser, matchRan);
	}

	public boolean isMatchRan() {
		return matchRan;
	}

	@Override
	public String toString() {
		return "Match between " + cpOne.getName() + " and " + cpTwo.getName() + " score: "
				+ (matchRan ? ("" + getPointsForTeamOne() + "/" + getPointsForTeamTwo()) : "-");
	}
}
