Genetics4j
==========

[![pipeline status](https://gitlab.com/bmahe/genetics4j/badges/master/pipeline.svg)](https://gitlab.com/bmahe/genetics4j/-/commits/master)
![Dynamic XML Badge](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fgitlab.com%2Fapi%2Fv4%2Fprojects%2F13863766%2Fjobs%2Fartifacts%2Fmaster%2Fraw%2Fsemgrep.xml%3Fjob%3Dsemgrep&query=%2Ftestsuites%2F%40failures&label=Semgrep%20Finding&color=blue&link=https%3A%2F%2Fsemgrep.dev%2F)

Genetics4j is an open source library for Evolutionary Algorithms. This includes Genetic Algorithm, Genetic Programming and more!

See the website https://genetics4j.org for more information, quickstart and examples.

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

* There is a [Quickstart](https://genetics4j.org/docs/quickstart.html) guide to help you get up and running in minutes!
* Each module contains its own documentation
* Javadocs are built for the [whole project](https://genetics4j.org/apidocs/index.html) as well as for each individual modules. They can be found under _Project Reports_
* Various additional reports are also published under _Project Reports_, including [changelogs](https://genetics4j.org/gitlog.html) and tests results and coverage

# Examples

There are a few examples with this library. Namely:

* [Clustering](https://genetics4j.org/samples/docs/clustering.html) where we attempt to find the best way to cluster data and compare the different approaches
* [Mixture Models](https://genetics4j.org/samples/docs/mixture_models_on_gpu.html) where we attempt to find the best way to cluster data using mixture models with the help of GPUs
* [6 ways to handle your bloat issues](https://genetics4j.org/samples/docs/bloat_issues.html) where we explore different methods from the _Multi Objective Optimization_ field to find the best equation to represent a set of data points. This provides us a way to find a good trade-off between long and precise equations and shorter but less precise equations
* [Fitness Sharing](https://genetics4j.org/samples/docs/fitness_sharing.html) where we present one technique to reduce the likelihood to get stuck in a local optimum and explore a great set of diverse solutions
* [Impelementing XOR with NEAT](https://genetics4j.org/samples/docs/neat_xor.html) where we implement a XOR gate with NeuroEvolution of Augmenting Topologies (_NEAT_)


# License

Everything is under Apache License Version 2.0
