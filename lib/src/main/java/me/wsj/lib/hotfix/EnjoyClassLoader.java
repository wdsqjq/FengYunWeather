package me.wsj.lib.hotfix;


import dalvik.system.PathClassLoader;

public class EnjoyClassLoader extends PathClassLoader {
    public EnjoyClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super(dexPath, librarySearchPath, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
