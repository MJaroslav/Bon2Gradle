package com.github.mjaroslav.bon2gradle.task;

import com.github.mjaroslav.bon2gradle.util.MappingUtils;
import com.github.parker8283.bon2.BON2Impl;
import com.github.parker8283.bon2.cli.CLIErrorHandler;
import com.github.parker8283.bon2.cli.CLIProgressListener;
import com.github.parker8283.bon2.data.MappingVersion;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.io.IOException;

@Setter
@Getter
public class Bon2Task extends DefaultTask {
    @InputFile
    private File from;

    @OutputFile
    private File to;

    @InputFile
    @Optional
    private File mappings;

    @Input
    private String mappingsRelativeConfPath;

    @TaskAction
    public void doTask() throws IOException {
        BON2Impl.remap(getFrom(), getTo(), getMappingVersion(), new CLIErrorHandler(), new CLIProgressListener());
    }

    @UnknownNullability
    private MappingVersion getMappingVersion() {
        if (mappings == null) return MappingUtils.getCurrentMapping(getProject());
        else return MappingUtils.getMapping(getMappings(), getMappingsRelativeConfPath());
    }
}
