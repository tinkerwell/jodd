// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

/**
 * Simple no-cache implementations of {@link Cache} for situation when cache
 * needs to be quickly turned-off.
 */
public class NoCache<K, V> implements Cache<K, V> {


	public int getCacheSize() {
		return 0;
	}

	public long getCacheTimeout() {
		return 0;
	}

	public void put(K key, V object) {
		// ignore
	}

	public void put(K key, V object, long timeout) {
		// ignore
	}

	public V get(K key) {
		return null;
	}

	public int prune() {
		return 0;
	}

	public boolean isFull() {
		return true;
	}

	public void remove(K key) {
		// ignore
	}

	public void clear() {
		// ignore
	}

	public int size() {
		return 0;
	}
}
