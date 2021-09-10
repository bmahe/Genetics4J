package net.bmahe.genetics4j.samples.clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class LocationWrapper implements Clusterable {

	private double[] points;

	public LocationWrapper(double[] _points) {
		this.points = new double[2];
		this.points[0] = _points[0];
		this.points[1] = _points[1];
	}

	public double[] getPoint() {
		return points;
	}
}