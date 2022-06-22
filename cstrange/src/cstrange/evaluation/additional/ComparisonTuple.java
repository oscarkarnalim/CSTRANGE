package cstrange.evaluation.additional;

public class ComparisonTuple implements Comparable<ComparisonTuple> {
	public String path1, path2;
	public double simDegree;
	
	public ComparisonTuple(String path1, String path2, double simDegree) {
		super();
		this.path1 = path1;
		this.path2 = path2;
		this.simDegree = simDegree;
	}

	public int compareTo(ComparisonTuple o) {
		return (int)((o.simDegree - this.simDegree)*100);
	}
}
