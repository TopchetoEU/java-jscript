package me.topchetoeu.jscript.filesystem;

import me.topchetoeu.jscript.engine.Extensions;
import me.topchetoeu.jscript.engine.values.Symbol;

public interface Filesystem {
    public static final Symbol ENV_KEY = Symbol.get("Environment.fs");

    String normalize(String... path);
    File open(String path, Mode mode) throws FilesystemException;
    void create(String path, EntryType type) throws FilesystemException;
    FileStat stat(String path) throws FilesystemException;

    public static Filesystem get(Extensions exts) {
        return exts.get(ENV_KEY);
    }
}