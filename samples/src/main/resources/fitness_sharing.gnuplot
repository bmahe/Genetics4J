# Requires the following variables:
# - filename : location of the csv file
# - plot_title: title of the plot
# - output_filename: Destination plot file
#
# Optionally, one can override the generation to plot with the variable 'generation'
#
# Ex:
#  gnuplot -e "filename='fitnessSharing.csv'" -e "plot_title='With Fitness Sharing'" fitness_sharing.gnuplot

if(!exists("output_filename")) output_filename = 'test.png'

set terminal png size 640,480 enhanced 
set output output_filename
set multiplot

set style fill transparent solid 0.35 noborder
set grid front

set title plot_title
set xlabel 'x'
set ylabel 'f(x)'

set xrange [0:100]
set yrange [0:35]

set datafile separator ","

if (!exists("generation")) generation = 5

plot \
    abs(30 * sin(x/10)) with lines title 'f(x) = abs(30 * sin(x/10))', \
	filename using (($1==generation)?$3:1/0):(abs(30*sin($3/10))) with circles lc rgb "blue" fs transparent solid 0.15 noborder title 'Solutions'
