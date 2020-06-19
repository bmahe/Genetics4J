# Requires the following variables:
# - filename : location of the csv file
# - plot_title: title of the plot
# - output_filename: Destination plot file
#
# Optionally, one can override the rank to plot with the variable 'rank'
#
# Ex:
#  gnuplot -e "filename='fitnessSharing.csv'" -e "plot_title='Population Size '" bloat_issue_population_size.gnuplot.gnuplot

if(!exists("output_filename")) output_filename = 'population_size.png'

set terminal png size 1080,480 enhanced 
set output output_filename
set multiplot

set grid front

set title plot_title
set xlabel 'Generation'
set ylabel 'Size'

#set xrange [0:]
#set yrange [0:]
#set logscale y

set autoscale ymax

set datafile separator ","

set style boxplot nooutliers
set style data boxplot

plot \
	filename every ::1 using (1):3:(0):1 notitle
