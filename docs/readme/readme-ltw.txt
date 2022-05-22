https://www.eclipse.org/aspectj/doc/released/devguide/ltw.html

The AspectJ weaver takes class files as input and produces class files as output.
The weaving process itself can take place at one of three different times: compile-time, post-compile time, and load-time.
The class files produced by the weaving process (and hence the run-time behaviour of an application) are the same regardless of the approach chosen.

- Compile-time weaving is the simplest approach. When you have the source code for an application, ajc will compile from source and produce woven class files as output.
    The invocation of the weaver is integral to the ajc compilation process. The aspects themselves may be in source or binary form.
    If the aspects are required for the affected classes to compile, then you must weave at compile-time.
    Aspects are required, e.g., when they add members to a class and other classes being compiled reference the added members.

- Post-compile weaving (also sometimes called binary weaving) is used to weave existing class files and JAR files.
    As with compile-time weaving, the aspects used for weaving may be in source or binary form, and may themselves be woven by aspects.

- Load-time weaving (LTW) is simply binary weaving deferred until the point that a class loader loads a class file and defines the class to the JVM.
    To support this, one or more "weaving class loaders", either provided explicitly by the run-time environment or enabled through a "weaving agent" are required.

- You may also hear the term "run-time weaving". We define this as the weaving of classes that have already been defined to the JVM (without reloading those classes).
    AspectJ 5 does not provide explicit support for run-time weaving although simple coding patterns can support dynamically enabling and disabling advice in aspects.

Weaving class files more than once
As of AspectJ 5 aspects (code style or annotation style) and woven classes are reweavable by default.
If you are developing AspectJ applications that are to be used in a load-time weaving environment with an older version of the compiler
you need to specify the -Xreweavable compiler option when building them.
This causes AspectJ to save additional state in the class files that is used to support subsequent reweaving.