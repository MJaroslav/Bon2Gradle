package com.github.mjaroslav.bon2gradle.task;

import com.github.mjaroslav.bon2gradle.util.MappingUtils;
import com.github.parker8283.bon2.BON2Impl;
import com.github.parker8283.bon2.cli.CLIErrorHandler;
import com.github.parker8283.bon2.cli.CLIProgressListener;
import com.github.parker8283.bon2.data.MappingVersion;
import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.gradle.tasks.abstractutil.CachedTask;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;

@Setter
public class Bon2Task extends CachedTask {
    @InputFile
    @Cached
    private Closure<File> from;

    @OutputFile
    @Cached
    private Closure<File> to;

    @InputFile
    @Optional
    private Closure<File> mappings;

    @Input
    @Optional
    @Getter
    private String mappingsRelativeConfPath;

    @TaskAction
    public void doTask() throws IOException {
        BON2Impl.remap(getFrom(), getTo(), getMappingVersion(), new CLIErrorHandler(), new CLIProgressListener());
    }

    public File getFrom() {
        return from.call();
    }

    public File getTo() {
        return to.call();
    }

    public File getMappings() {
        return mappings.call();
    }

    private MappingVersion getMappingVersion() {
        if (mappings == null) return MappingUtils.getApiMapping(getProject());
        else return MappingUtils.getMapping(getMappings(), getMappingsRelativeConfPath());
    }
}
