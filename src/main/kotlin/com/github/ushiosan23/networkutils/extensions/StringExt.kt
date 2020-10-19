package com.github.ushiosan23.networkutils.extensions

import java.net.URI

/**
 * Crete uri from string
 *
 * @return [URI] object uri
 */
fun String.toURI(): URI = URI.create(this)
