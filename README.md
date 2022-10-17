Genetics4j
==========

[![pipeline status](https://gitlab.com/bmahe/genetics4j/badges/master/pipeline.svg)](https://gitlab.com/bmahe/genetics4j/-/commits/master)

Genetics4j is an open source library for Evolutionary Algorithms. This includes Genetic Algorithm, Genetic Programming and more!

See the website https://bmahe.gitlab.io/genetics4j/ for more information, quickstart and examples.

# Features

* Genetic Algorithms
* Strongly Typed Genetic Programming
* Multi-Objective Optimization support with algorithms such as NSGA2 and SPEA2
* NeuroEvolution of Augmenting Topologies, also known as _NEAT_
* Supports multiple replacement strategies
* Configurable and user friendly
* Multi-GPU and Multithreaded based implementations for faster execution
* Clear separation between the problem definition and its execution model
* Enhanced test coverage with [mutation testing](https://en.wikipedia.org/wiki/Mutation_testing) :)

# Documentation and Quickstart

There are various ways to learn more about _Genetics4j_:

* There is a [Quickstart](https://bmahe.gitlab.io/genetics4j/docs/quickstart.html) guide to help you get up and running in minutes!
* Each module contains its own documentation
* Javadocs are built for the [whole project](https://bmahe.gitlab.io/genetics4j/apidocs/index.html) as well as for each individual modules. They can be found under _Project Reports_
* Various additional reports are also published under _Project Reports_, including [changelogs](https://bmahe.gitlab.io/genetics4j/gitlog.html) and tests results and coverage

# Examples

There are a few examples with this library. Namely:

* [Clustering](https://bmahe.gitlab.io/genetics4j/samples/docs/clustering.html) where we attempt to find the best way to cluster data and compare the different approaches
* [Mixture Models](https://bmahe.gitlab.io/genetics4j/samples/docs/mixture_models_on_gpu.html) where we attempt to find the best way to cluster data using mixture models with the help of GPUs
* [6 ways to handle your bloat issues](https://bmahe.gitlab.io/genetics4j/samples/docs/bloat_issues.html) where we explore different methods from the _Multi Objective Optimization_ field to find the best equation to represent a set of data points. This provides us a way to find a good trade-off between long and precise equations and shorter but less precise equations
* [Fitness Sharing](https://bmahe.gitlab.io/genetics4j/samples/docs/fitness_sharing.html) where we present one technique to reduce the likelihood to get stuck in a local optimum and explore a great set of diverse solutions


# License

Everything is under Apache License Version 2.0
