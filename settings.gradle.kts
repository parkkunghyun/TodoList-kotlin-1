pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 외부라이브러리 등록시 꼭 등록해야함
        maven("https://jitpack.io")
    }
}

rootProject.name = "TodoList-1"
include(":app")
 