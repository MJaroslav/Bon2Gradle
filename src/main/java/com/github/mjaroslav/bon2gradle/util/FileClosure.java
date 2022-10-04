package com.github.mjaroslav.bon2gradle.util;

import groovy.lang.Closure;

import java.io.File;

public class FileClosure extends Closure<File> {
    private final File file;

    public FileClosure(Object owner, File file) {
        super(owner, null);
        this.file = file;
    }

    @Override
    public File call() {
        return file;
    }
}
