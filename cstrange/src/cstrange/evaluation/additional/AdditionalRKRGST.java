package cstrange.evaluation.additional;

import java.util.ArrayList;

public class AdditionalRKRGST {
	
	public static double calcSimDegree(String s1, String s2) {
		ArrayList<AdditionalGSTMatchTuple> matches = AdditionalGreedyStringTiling.getMatchedTiles(s1.toCharArray(), s2.toCharArray(), 2);
		return AdditionalGreedyStringTiling.calcAverageSimilarity(matches);
	}
	
	public static void main(String[] args) {
		String s1 = "ha ha uhu hahaha this is oscar comment sample";
		String s2 = "ha ha this is oscar huga huga sample lol";
		
		System.out.println(calcSimDegree(s1, s2));
	}
}
