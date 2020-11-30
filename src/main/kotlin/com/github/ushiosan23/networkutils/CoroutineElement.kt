package com.github.ushiosan23.networkutils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class CoroutineElement : CoroutineScope {

	/**
	 * Coroutine job executor
	 */
	protected val coroutineJob: Job = Job()

	/**
	 * Coroutine context property
	 */
	override val coroutineContext: CoroutineContext
		get() = coroutineJob + Dispatchers.Default

	fun coroutineLaunch(block: () -> Unit) = launch {
		block.invoke()
	}

}
