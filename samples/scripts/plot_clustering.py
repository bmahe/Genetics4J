#!/usr/bin/env python3

import argparse
import pandas

from pathlib import Path

def plot(result_membership, result_clustering, destination, min_x, max_x, min_y, max_y, title):
    membership = pandas.read_csv(result_membership,)

    ax = membership.plot.scatter(x="x", y="y", c="cluster", cmap='viridis', 
        grid=True, figsize=(20, 10), xlim=(min_x, max_x), ylim=(min_y, max_y), title=title)

    if result_clustering is not None:
        centroids = pandas.read_csv(result_clustering,)
        centroids.plot.scatter(ax=ax, x="x", y="y", color="Red", s=50,
            grid=True, figsize=(20, 10), xlim=(min_x, max_x), ylim=(min_y, max_y))

    ax.figure.savefig(destination)


def main():
    parser = argparse.ArgumentParser(allow_abbrev=False)
    parser.add_argument("-e", "--result_membership", type=str, help="Result membership csv")
    parser.add_argument("-c", "--result_clustering", type=str, help="Result clustering csv")
    parser.add_argument("-d", "--destination", type=str, help="Destination filename")
    parser.add_argument("--min_x", default=None, type=int, help="Mininimum X axis for plotting")
    parser.add_argument("--max_x", default=None, type=int, help="Maximum X axis for plotting")
    parser.add_argument("--min_y", default=None, type=int, help="Mininimum Y axis for plotting")
    parser.add_argument("--max_y", default=None, type=int, help="Maximum Y axis for plotting")
    parser.add_argument("--title", default=None, type=str, help="Title for the plot")
    args = parser.parse_args()

    dest_path = Path(args.destination)
    parent_dest_path = dest_path.parent
    parent_dest_path.mkdir(parents=True, exist_ok=True)

    plot(args.result_membership, args.result_clustering, args.destination, args.min_x, args.max_x, args.min_y, args.max_y, args.title)


if __name__ == "__main__":
    main()
