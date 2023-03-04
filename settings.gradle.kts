
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    includeBuild("H:/prg/KnewsPlatform2")
}
rootProject.name = "KNewsCommon"
//rootProject.buildFileName="build.gradle.kts"
//includeBuild("../knico/plugin-build")