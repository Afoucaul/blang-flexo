package org.blangflexo.core;


public class Pair<T, U> {
	public final T first;
	public final U second;
	
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public String toString() {
		return "(" + first.toString() + ", " + second.toString() + ")";
	}
	
	@Override
	public boolean equals(Object other) {
		Pair<T, U> otherPair;
		try {
			 otherPair = (Pair<T, U>) other;
		} catch (ClassCastException ex) {
			System.out.println("Could not cast other into Pair");
			return false;
		}
		return (otherPair.first.equals(first) && otherPair.second.equals(second));
	}
}