package io.github.mjaroslav.bon2gradle.deobf

import io.github.mjaroslav.bon2.data.IErrorHandler
import io.github.mjaroslav.bon2.data.IProgressListener
import org.gradle.api.logging.Logger

class Bon2ProgressListenerErrorHandler constructor(private val logger: Logger) : IProgressListener, IErrorHandler {
    override fun start(max: Int, label: String?) {
        logger.info(label)
    }

    override fun startWithoutProgress(label: String?) {
        logger.info(label)
    }

    override fun setProgress(value: Int) {
        // NO-OP
    }

    override fun setMax(max: Int) {
        // NO-OP
    }

    override fun setLabel(label: String?) {
        logger.info(label)
    }

    override fun handleError(message: String?, warning: Boolean): Boolean {
        if (warning) logger.warn(message) else logger.error(message)
        return true
    }
}
