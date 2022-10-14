package io.github.mjaroslav.bon2gradle.task;

import io.github.mjaroslav.bon2gradle.deobf.Bon2ProgressListenerErrorHandler;
import io.github.mjaroslav.bon2gradle.deobf.MappingUtils;
import io.github.mjaroslav.bon2.BON2Impl;
import io.github.mjaroslav.bon2.data.MappingVersion;
import lombok.val;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;

public abstract class Bon2Task extends DefaultTask {
    @InputFile
    public abstract RegularFileProperty getFrom();

    @OutputFile
    public abstract RegularFileProperty getTo();

    @InputFile
    @Optional
    public abstract RegularFileProperty getMapping();

    @Input
    @Optional
    public abstract Property<String> getMappingsRelativeConfPath();

    @TaskAction
    public void doTask() throws IOException {
        val bonHandlers = new Bon2ProgressListenerErrorHandler(getProject());
        val parent = getTo().get().getAsFile().getParentFile();
        if (parent.isDirectory() || parent.mkdirs())
            BON2Impl.remap(getFrom().get().getAsFile(), getTo().get().getAsFile(), getMappingVersion(),
                bonHandlers, bonHandlers);
    }

    @UnknownNullability
    @Internal
    public MappingVersion getMappingVersion() {
        if (!getMapping().isPresent()) return MappingUtils.getCurrentMapping(getProject());
        else return MappingUtils.getMapping(getMapping().get().getAsFile(), getMappingsRelativeConfPath().get());
    }
}
