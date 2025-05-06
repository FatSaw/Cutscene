package net.minecraft.server.v1_12_R1;

import java.util.AbstractList;

public class NonNullList<T> extends AbstractList<T> {

	@Override
	public int size() {
		return 0;
	}

	@Override
	public T get(int index) {
		return null;
	}
	
	public static <E> NonNullList<E> a() {
		return null;
	}

}
