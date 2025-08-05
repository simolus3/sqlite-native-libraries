tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}
