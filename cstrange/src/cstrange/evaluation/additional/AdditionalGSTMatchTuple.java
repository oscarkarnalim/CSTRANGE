package cstrange.evaluation.additional;

/*
 * tuple untuk menyimpan hasil kesamaan
 */
public class AdditionalGSTMatchTuple {
	public int patternPosition;
	public int textPosition;
	public int length;
	public AdditionalGSTMatchTuple(int p, int t, int l){
		this.patternPosition = p;
		this.textPosition = t;
		this.length = l;
	}
	
	public String toString(){
		return patternPosition + ":" + textPosition + ":" + length;
	}
}
