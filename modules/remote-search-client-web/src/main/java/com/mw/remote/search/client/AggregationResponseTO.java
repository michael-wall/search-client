package com.mw.remote.search.client;

public class AggregationResponseTO {

	public long getCardinality() {
		return cardinality;
	}
	public void setCardinality(long cardinality) {
		this.cardinality = cardinality;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	public long getMin() {
		return min;
	}
	public void setMin(long min) {
		this.min = min;
	}
	public long getMax() {
		return max;
	}
	public void setMax(long max) {
		this.max = max;
	}
	
	private long cardinality;	
	private double avg;
	private long min;
	private long max;	
}
