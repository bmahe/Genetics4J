#!/usr/bin/env python3

import argparse
from multiprocessing import parent_process
import pandas

from pathlib import Path

def plot(execution_trace, destination, fitness_label='fitness'):
    data = pandas.read_csv(execution_trace,)

    data["max"] = data.groupby("generation")[fitness_label].max()
    data["min"] = data.groupby("generation")[fitness_label].min()
    data["75% percentile"] = data.groupby("generation")[fitness_label].quantile(0.75)
    data["50% percentile"] = data.groupby("generation")[fitness_label].quantile(0.50)
    data["25% percentile"] = data.groupby("generation")[fitness_label].quantile(0.25)

    ax = data["max"].plot(grid=True, figsize=(20, 10), xlabel="Generation", legend=True)

    data["75% percentile"].plot(ax=ax, grid=True, figsize=(20, 10), xlabel="Generation", legend=True)
    data["50% percentile"].plot(ax=ax, grid=True, figsize=(20, 10), xlabel="Generation", legend=True)
    data["25% percentile"].plot(ax=ax, grid=True, figsize=(20, 10), xlabel="Generation", legend=True)
    data["min"].plot(ax=ax, grid=True, figsize=(20, 10), xlabel="Generation", legend=True, title="Fitness")

    ax.figure.savefig(destination)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-e", "--execution_trace", type=str, help="Execution trace csv")
    parser.add_argument("-d", "--destination", type=str, help="Destination filename")
    args = parser.parse_args()

    dest_path = Path(args.destination)
    parent_dest_path = dest_path.parent
    parent_dest_path.mkdir(parents=True, exist_ok=True)

    plot(args.execution_trace, args.destination)


if __name__ == "__main__":
    main()
