package cstrange.evaluation;

public class JplagEntryTuple implements Comparable<JplagEntryTuple> {
	public String path1, path2;
	public double simDegreeJplag, simDegreeSyntactic, simDegreeSurface;

	public JplagEntryTuple(String path1, String path2, double simDegree) {
		super();
		this.path1 = path1;
		this.path2 = path2;
		this.simDegreeJplag = simDegree;
	}

	@Override
	public int compareTo(JplagEntryTuple o) {
		// TODO Auto-generated method stub
		int otot, ttot;

		otot = (int) (o.simDegreeJplag + o.simDegreeSyntactic + o.simDegreeSurface);
		ttot = (int) (this.simDegreeJplag + this.simDegreeSyntactic + this.simDegreeSurface);

		return otot - ttot;
	}

}
