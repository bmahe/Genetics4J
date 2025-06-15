# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Genetics4j is a comprehensive Java 21 library for Evolutionary Algorithms including Genetic Algorithms, Genetic Programming, Multi-Objective Optimization (NSGA-II, SPEA-II), NEAT neural networks, and GPU-accelerated computations using OpenCL.

## Build Commands

**Core Development Commands:**
- `mvn clean compile` - Compile all modules
- `mvn test` - Run unit tests across all modules
- `mvn verify` - Run tests + integration tests + mutation testing (PITest)
- `mvn clean install` - Full build and install to local repository
- `mvn site` - Generate project documentation and reports

**Testing Commands:**
- `mvn test -pl core` - Run tests for specific module (replace 'core' with target module)
- `mvn verify -DskipPITests=true` - Skip mutation testing for faster builds
- `mvn failsafe:integration-test` - Run integration tests only

**Quality Assurance:**
- `mvn jacoco:report` - Generate code coverage reports
- `mvn org.pitest:pitest-maven:mutationCoverage` - Run mutation testing explicitly

## Multi-Module Architecture

The project is structured as a Maven multi-module project with 7 modules:

- **core**: Foundation module with `EASystem`, chromosome types (`BitChromosome`, `IntChromosome`, `DoubleChromosome`, `FloatChromosome`, `TreeChromosome`), selection strategies, crossover/mutation operators, and replacement policies
- **gp**: Genetic Programming with strongly-typed GP support and tree-based operations  
- **moo**: Multi-Objective Optimization (NSGA-II, SPEA-II) with Pareto front utilities
- **neat**: NeuroEvolution of Augmenting Topologies for neural network evolution
- **gpu**: GPU-accelerated genetic algorithms using OpenCL/JOCL
- **extras**: Additional utilities like CSV logging and extended evolution listeners
- **samples**: Example implementations demonstrating library capabilities

## Key Design Patterns

**Strategy Pattern Implementation**: The library extensively uses strategy patterns for:
- Selection strategies (Tournament, Roulette Wheel, Proportional Tournament)  
- Crossover operations (SinglePoint, MultiPoint, Order, Edge Recombination)
- Mutation policies (RandomMutation, CreepMutation, SwapMutation)
- Replacement strategies (Elitism, Generational, DeleteNLast)

**Configuration-Driven Setup**: Use builders for EASystem configuration:
```java
EAConfiguration<BitChromosome> config = EAConfigurationBuilder.<BitChromosome>builder()
    .chromosomeSpecs(chromosomeSpec)
    .parentSelectionPolicy(parentSelection)
    .replacementStrategy(replacement)
    .build();
```

## Core Workflow Pattern

1. **Define Problem**: Create chromosome specification and fitness evaluator
2. **Configure EA**: Set up selection, crossover, mutation, and replacement strategies  
3. **Create System**: Build EASystem with configuration and execution context
4. **Execute**: Run evolution with population size and termination conditions
5. **Monitor**: Use evolution listeners for real-time monitoring and logging

## Development Practices

**Testing**: Uses JUnit 5 + Mockito with comprehensive mutation testing via PITest. The project maintains high test quality standards with mutation testing enabled by default.

**Immutability**: Extensively uses `@Value` from Immutables library for thread-safe, immutable data structures throughout the API.

**Type Safety**: Heavily parameterized with generic types. Pay attention to chromosome type constraints when implementing new components.

**Parallel Execution**: The library supports multi-threaded execution. Be aware of thread safety requirements when extending core components.

**GPU Support**: OpenCL integration requires careful memory management and kernel compilation. GPU module provides abstractions for device selection and kernel execution.
