plugins {
    kotlin("jvm")
    application
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(BuildConfig.JAVA_VERSION))

application.mainClass.set("dev.ebnbin.emojiwar.tools.ToolsLauncher")

dependencies {
    implementation(project(":core"))
    implementation(Dependencies.GDX_BACKEND_HEADLESS)
    implementation(Dependencies.GDX_PLATFORM_NATIVES_DESKTOP)
}
