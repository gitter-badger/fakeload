[![Build Status](https://travis-ci.org/msigwart/fakeload.svg?branch=master)](https://travis-ci.org/msigwart/fakeload)

# FakeLoad
FakeLoad is an open-source Java library which provides a simple way of producing “fake" system loads in applications or tests.

Producing system load using FakeLoad is as simple as this:
```java
// Creation
FakeLoad fakeload = FakeLoads.create()
    .lasting(10, TimeUnit.SECONDS)
    .withCpu(80)
    .withMemory(300, MemoryUnit.MB);
 
// Execution
FakeLoadExecutor executor = FakeLoadExecutors.newDefaultExecutor(); 
executor.execute(fakeload);
```
The above snippet would simulate a CPU load of 80% and a memory load of 300 MB for ten seconds.

## Motivation
FakeLoad was created with three different contexts in mind:

### 1. Testing of Non-Functional Requirements
Early verification of non-functional requirements like scalability, performance, availablity, etc. can be vital to a project's success. For example, an application or framework that does elastic data stream processing needs to make sure its auto-scaling behavior or monitoring is working correctly. This can be hard to test if the system is not fully developed yet as some "not yet" implemented parts of the system might have a huge impact on the those dynamic properties. FakeLoad could be used to simulate system behavior in missing parts. This way, instead of force-implementing missing parts or making risky assumptions about runtime behavior, developers can actually test dynamic properties of their systems.

### 2. Testing with Test Doubles (a.k.a. Mocks, Fakes, Dummies, etc.)
Replacing real objects with test doubles is a well-established practice in software testing, as they usually help to test components of a software system in isolation. However, replacing real objects with simpler 'fakes' can be dangerous when the simpler 'fake' object doesn't sufficiently mimick the more complex 'real' object or certain parts of it. FakeLoad could be used to mimick runtime characteristics like CPU usage, etc. of the real object within the test double. As FakeLoad is designed to be easy-to-use, test doubles would remain simple while at the same time mimicking 'real' runtime behavior.

### 3. Faking of "real" Data or Algorithms
FakeLoad can also be used when dealing with data or algorithms that stand under a non-disclosure agreement (NDA). The NDA might prohibit any publication of scientific evaluation involving the protected algorithm or data. FakeLoad could be used to "simulate" data or an algorithms behavior, bypassing the NDA and thus allowing publication.

## Getting Started
### Installation

### Usage
You can also combine FakeLoads to create more complex "load patterns":
```java

```
## Licence
This project is licensed under the MIT License - see the [LICENCE.md](LICENSE) file for details

## Acknowledgements

