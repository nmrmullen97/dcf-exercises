# dcf-exercises
A few exercises to get familiar with some distributed system concepts and their simulator


another attempt 


## Covered exercises
This project provides the following exercises and even offers some simplistic solutions for students of distributed systems:
* Investigate several ways one can create a virtual machine on a physical machine
  * Task: Fulfill the test cases in [TestVMCreation](https://github.com/kecskemeti/dcf-exercises/blob/master/src/test/java/hu/unimiskolc/iit/distsys/TestVMCreation.java)
  * An example solution: [SolutionVMC](https://github.com/kecskemeti/dcf-exercises/blob/master/src/main/java/hu/unimiskolc/iit/distsys/solution/SolutionVMC.java)
* Analyze techniques to fill in an inhomogeneous computing infrastructure with virtual machines, then apply round robin job scheduling on top of the created VMs
  * Task: Fulfill the test case in [TestRoundRobinJobSched](https://github.com/kecskemeti/dcf-exercises/blob/master/src/test/java/hu/unimiskolc/iit/distsys/TestRoundRobinJobSched.java)
  * An example solution: 
    * For the infrastructure filling: [SolutionFiller](https://github.com/kecskemeti/dcf-exercises/blob/master/src/main/java/hu/unimiskolc/iit/distsys/solution/SolutionFiller.java)
    * For the round robin scheduling: [SolutionJobtoStaticVMsetRR](https://github.com/kecskemeti/dcf-exercises/blob/master/src/main/java/hu/unimiskolc/iit/distsys/solution/SolutionJobtoStaticVMsetRR.java)
* Get a grasp on the principles of auto scaling:
  * Task: fulfill the test case of [TestSimpleScaler](https://github.com/kecskemeti/dcf-exercises/blob/master/src/test/java/hu/unimiskolc/iit/distsys/TestSimpleScaler.java)
  * Example solution: [SolutionBasicScaler](https://github.com/kecskemeti/dcf-exercises/blob/master/src/main/java/hu/unimiskolc/iit/distsys/solution/SolutionBasicScaler.java)
* Check out some basic high availability techniques:
  * Task: fulfill the test case of [TestHighAvailability](https://github.com/kecskemeti/dcf-exercises/blob/master/src/test/java/hu/unimiskolc/iit/distsys/TestHighAvailability.java)
  * Example solution: [SolutionHA](https://github.com/kecskemeti/dcf-exercises/blob/master/src/main/java/hu/unimiskolc/iit/distsys/solution/SolutionHA.java) 
* Introduce new pricing models for infrastructure clouds:
  * Task: fulfill the test case of [TestPricing](https://github.com/kecskemeti/dcf-exercises/blob/master/src/test/java/hu/unimiskolc/iit/distsys/TestPricing.java)
  * Example solution: no example provided as the test case itself uses a simplistic pricing technique in [BuiltInCloudProvider](https://github.com/kecskemeti/dcf-exercises/blob/master/src/main/java/hu/unimiskolc/iit/distsys/BuiltInCloudProvider.java)

### Remark
The solutions above are kept to be as simplistic as possible in order to allow easy understanding of the mechanics in place. As a result, some of the solutions do not offer 100% success rate in the test runs.
