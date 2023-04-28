// Run on IntelliJ
val ideaActive by lazy { System.getProperty("idea.active") == "true" }
// Run on apple silicon
val isAppleSilicon by lazy { System.getProperty("os.arch") == "aarch64" }
