package com.github.ushiosan23.networkutils.http.response;

/**
 * Http action interface
 *
 * @param <T> Generic type class
 */
public interface HttpAction<T> {

	/**
	 * Invocation method
	 *
	 * @param item Item result
	 * @return {@link T} generic type
	 */
	T invoke(T item);

	/**
	 * Called when has an error
	 *
	 * @param throwable Error exception
	 * @param source    Source of error
	 * @return {@link T} generic type
	 */
	default T exceptionally(Throwable throwable, Object source) {
		return null;
	}

	/**
	 * Called when invocation finished
	 *
	 * @param accept {@link T} accept result
	 */
	default void thenAccept(T accept) {
	}

}
