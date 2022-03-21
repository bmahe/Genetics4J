#!/usr/bin/env python3

import argparse
import logging
import pandas

import matplotlib.pyplot as plt

from pathlib import Path

pandas.options.mode.use_inf_as_na = True


def plot(execution_traces, names, destination, plot_min, plot_max, quantiles, logy, start_index, fitness_label='fitness'):
    if len(execution_traces) != len(names):
        raise Exception("Parameters don't match")

    data_all = {}

    for execution_trace, name in zip(execution_traces, names):
        data = pandas.read_csv(execution_trace,)

        if plot_max:
            data_all[name +
                     " - max"] = data.groupby("generation")[fitness_label].max()
        if plot_min:
            data_all[name +
                     " - min"] = data.groupby("generation")[fitness_label].min()

        for quantile in quantiles:
            quantile_name = "{} - {} quantile".format(name, quantile)
            data_all[quantile_name] = data.groupby(
                "generation")[fitness_label].quantile(quantile)

    fig, ax = plt.subplots(1, 1)
    for k, v in data_all.items():
        v[start_index:].rename(k).plot(ax=ax, grid=True, figsize=(20, 10), logy=logy,
                         xlabel="Generation", legend=True)

    ax.figure.savefig(destination)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-e", "--execution_trace",
                        action='append', type=str, help="Execution trace csv")
    parser.add_argument("-n", "--name", action='append',
                        type=str, help="Execution trace name")
    parser.add_argument("-d", "--destination", type=str,
                        help="Destination filename")
    parser.add_argument(
        "--min", action=argparse.BooleanOptionalAction, default=False)
    parser.add_argument(
        "--max", action=argparse.BooleanOptionalAction, default=False)
    parser.add_argument("--quantile", action='append', default=[],
                        type=float, help="Quantiles to plot")
    parser.add_argument(
        "--logy", action=argparse.BooleanOptionalAction, default=False)
    parser.add_argument("--start_index", type=int, default=0, help="Starting index")

    args = parser.parse_args()

    dest_path = Path(args.destination)
    parent_dest_path = dest_path.parent
    parent_dest_path.mkdir(parents=True, exist_ok=True)

    plot(args.execution_trace, args.name, args.destination,
         args.min, args.max, args.quantile, args.logy, args.start_index)


if __name__ == "__main__":
    main()
