package io.github.mjaroslav.bon2gradle.data

class SysErrBon2Handlers : Bon2Handlers {
    override fun handleError(message: String?, warning: Boolean): Boolean {
        System.err.println(message)
        return true
    }

    override fun start(max: Int, progress: String?) {} // NO-OP

    override fun startWithoutProgress(label: String?) {} // NO-OP

    override fun setProgress(progress: Int) {} // NO-OP

    override fun setMax(max: Int) {} // NO-OP

    override fun setLabel(label: String?) {} // NO-OP

    override fun copy(): Bon2Handlers = this
}
