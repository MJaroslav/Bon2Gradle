package io.github.mjaroslav.bon2gradle.deobf;

import io.github.mjaroslav.bon2.data.IErrorHandler;
import io.github.mjaroslav.bon2.data.IProgressListener;
import lombok.RequiredArgsConstructor;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

// TODO: Make logs in BON2 fork more informative
@RequiredArgsConstructor
public class Bon2ProgressListenerErrorHandler implements IProgressListener, IErrorHandler {
    private final @NotNull Project project;

    @Override
    public void start(int max, String label) {
        project.getLogger().info(label);
    }

    @Override
    public void startWithoutProgress(String label) {
        project.getLogger().info(label);
    }

    @Override
    public void setProgress(int value) {
        // NO-OP
    }

    @Override
    public void setMax(int max) {
        // NO-OP
    }

    @Override
    public void setLabel(String label) {
        project.getLogger().info(label);
    }

    @Override
    public boolean handleError(String message, boolean warning) {
        if (warning) project.getLogger().warn(message);
        else project.getLogger().error(message);
        return true;
    }
}