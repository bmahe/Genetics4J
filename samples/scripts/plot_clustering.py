#!/usr/bin/env python3

import argparse
import pandas


def plot(result_membership, result_clustering, destination):
    membership = pandas.read_csv(result_membership,)
    centroids = pandas.read_csv(result_clustering,)

    ax = membership.plot.scatter(x="x", y="y", c="cluster", cmap='viridis', 
        grid=True, figsize=(20, 10), xlim=(-150, 150), ylim=(-150, 150))

    centroids.plot.scatter(ax=ax, x="x", y="y", color="Red", s=50,
        grid=True, figsize=(20, 10), xlim=(-150, 150), ylim=(-150, 150))

    ax.figure.savefig(destination)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-e", "--result_membership", type=str, help="Result membership csv")
    parser.add_argument("-c", "--result_clustering", type=str, help="Result clustering csv")
    parser.add_argument("-d", "--destination", type=str, help="Destination filename")
    args = parser.parse_args()

    plot(args.result_membership, args.result_clustering, args.destination)


if __name__ == "__main__":
    main()
