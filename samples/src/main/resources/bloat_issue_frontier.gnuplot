# Requires the following variables:
# - filename : location of the csv file
# - plot_title: title of the plot
# - output_filename: Destination plot file
#
# Optionally, one can override the rank to plot with the variable 'rank'
#
# Ex:
#  gnuplot -e "filename='fitnessSharing.csv'" -e "plot_title='With Fitness Sharing'" blot_issue_frontier.gnuplot

if(!exists("output_filename")) output_filename = 'frontiers.png'

set terminal png size 1080,480 enhanced 
set output output_filename
set multiplot

set grid front

set title plot_title
set xlabel 'Size'
set ylabel 'Fitness Score'

set xrange [0:]
set yrange [0:]

#set logscale y

set datafile separator ","

if (!exists("rank")) rank = 3

set palette model RGB

plot \
	filename using (($4>=0 && $4<=rank && $2<=50000)?$3:1/0):2:($1) with circles lc palette fill solid notitle
