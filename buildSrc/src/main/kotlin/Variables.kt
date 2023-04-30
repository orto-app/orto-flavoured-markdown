import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

// Run on IntelliJ
val ideaActive by lazy { System.getProperty("idea.active") == "true" }
val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
// Run on apple silicon
val isAppleSilicon by lazy { System.getProperty("os.arch") == "aarch64" }
