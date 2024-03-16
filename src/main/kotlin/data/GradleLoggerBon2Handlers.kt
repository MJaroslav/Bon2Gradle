package io.github.mjaroslav.bon2gradle.data

import org.gradle.api.logging.Logger

class GradleLoggerBon2Handlers(private val logger: Logger) : Bon2Handlers {
    private var percentStep = 1
    private var disableDoneSpam = false
    private var max = 0
    private var prevStep = -1
    private lateinit var currentLabel: String

    fun setPercentStep(percentStep: Int): GradleLoggerBon2Handlers {
        this.percentStep = percentStep
        if (this.percentStep < 1) this.percentStep = 1
        else if (this.percentStep > 100) this.percentStep = 100
        return this
    }

    fun setDisableDoneSpam(disableDoneSpam: Boolean): GradleLoggerBon2Handlers {
        this.disableDoneSpam = disableDoneSpam
        return this
    }

    override fun start(max: Int, label: String?) {
        if (disableDoneSpam && "Done!" == label) { // Are you sure?
            this.max = 0
            return
        }
        currentLabel = label ?: ""
        setMax(max)
        logger.info(currentLabel)
    }

    override fun startWithoutProgress(label: String?) {
        currentLabel = label ?: ""
        logger.info(currentLabel)
    }

    override fun setProgress(value: Int) {
        if (max == 0) return // Zero division protection
        val step = (value * 100 / max) / percentStep
        if (prevStep != step) {
            prevStep = step
            logger.info("Progress is ${step * percentStep}%...")
        }
        if (max == value) logger.info("Done!")
    }

    override fun setMax(max: Int) {
        this.max = max
        prevStep = -1
    }

    override fun setLabel(label: String?) {
        currentLabel = label ?: ""
        logger.info(currentLabel)
    }

    override fun handleError(message: String?, warning: Boolean): Boolean {
        if (warning) logger.warn(message) else logger.error(message)
        return true
    }

    override fun copy(): GradleLoggerBon2Handlers = GradleLoggerBon2Handlers(logger)
}
