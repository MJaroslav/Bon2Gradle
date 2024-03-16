package io.github.mjaroslav.bon2gradle.data

import com.github.parker8283.bon2.data.IErrorHandler
import com.github.parker8283.bon2.data.IProgressListener

/**
 * Just combining these two interfaces with copy method for parallel deobfuscating.
 */
interface Bon2Handlers : IErrorHandler, IProgressListener {
    /**
     * Create copy of these handlers with reset of progress and label.
     */
    fun copy(): Bon2Handlers
}
