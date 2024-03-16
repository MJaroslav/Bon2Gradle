package io.github.mjaroslav.bon2gradle

const val PLUGIN_NAME = "bon2gradle"
const val EXTENSION_NAME = "bon2"
const val RESOLVE_CONFIGURATION_PREFIX = "__$PLUGIN_NAME-resolvedArtifact-"
const val MAPPING_CACHE_DIRECTORY = "$PLUGIN_NAME/mappings"
const val GLOBAL_CACHE_DIRECTORY = PLUGIN_NAME
const val LOCAL_CACHE_DIRECTORY = "$PLUGIN_NAME/dirty"
const val DEFAULT_MAPPING_PROVIDER = "io.github.mjaroslav.bon2gradle.api.impl.FG12MappingProvider"
