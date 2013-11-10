package br.ufrn.dimap.rtquality.util;

import java.util.HashSet;
import java.util.Set;

public class MathUtil<T> {

	public static <T> Set<T> intersection(Set<T> A, Set<T> B) {
		Set<T> intersection = new HashSet<T>(A.size()+B.size());
		for(T  s : B) {
			if(A.contains(s) && !intersection.contains(s))
				intersection.add(s);
		}
		return intersection;
	}
	
	public static <T> Set<T> union(Set<T> A, Set<T> B) {
		Set<T> union = new HashSet<T>(A.size()+B.size());
		union.addAll(A);
		union.addAll(B);
		return union;
	}
	
}
