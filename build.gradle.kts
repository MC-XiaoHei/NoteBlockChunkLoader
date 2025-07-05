@file:Suppress("SpellCheckingInspection")

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import xyz.jpenilla.resourcefactory.ConfigurateSingleFileResourceFactory
import xyz.jpenilla.resourcefactory.ResourceFactory
import xyz.jpenilla.resourcefactory.ResourceFactoryConventionPlugin
import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.util.*
import xyz.jpenilla.runtask.service.DownloadsAPIService
import xyz.jpenilla.runtask.service.DownloadsAPIService.Companion.registerIfAbsent

plugins {
    java
    alias(libs.plugins.leavesweightUserdev)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.resourceFactory)
}

group = "cn.xor7.xiaohei"
version = "1.0.0-SNAPSHOT"

val pluginJson = leavesPluginJson {
    main = "cn.xor7.xiaohei.note_block_chunk_loader.NoteBlockChunkLoader"
    authors.add("MC_XiaoHei")
    description = "添加了类似 Carpet AMS Addition 的音符盒加载器 "
    foliaSupported = false
    apiVersion = libs.versions.leavesApi.extractMCVersion()
    features.required.add("mixin")
    mixin.apply {
        packageName = "cn.xor7.xiaohei.note_block_chunk_loader.mixin"
        mixins.add("note-block-chunk-loader.mixins.json")
    }
}

val runServerPlugins = runPaper.downloadPluginsSpec {
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://modmaven.dev/") {
        name = "modmaven"
    }
    maven("https://repo.leavesmc.org/releases/") {
        name = "leavesmc-releases"
    }
    maven("https://repo.leavesmc.org/snapshots/") {
        name = "leavesmc-snapshots"
    }
    mavenLocal()
}

sourceSets {
    create("mixin") {
        java.srcDir("mixin/java")
        resources.srcDir("mixin/resources")
    }

    main {
        resourceFactory {
            factories(pluginJson.resourceFactory())
        }
    }
}
val mixinSourceSet: SourceSet = sourceSets["mixin"]

dependencies {
    apply `plugin dependencies`@{

    }

    apply `api and server source`@{
        compileOnly(libs.leavesApi)
        paperweight.devBundle(libs.leavesDevBundle)
    }

    apply `mixin dependencies`@{
        compileOnly(mixinSourceSet.output)
        mixinSourceSet.apply {
            val compileOnly = compileOnlyConfigurationName

            compileOnly(libs.spongeMixin)
            compileOnly(files(getMappedServerJar()))
        }
    }
}

tasks {
    runServer {
        downloadsApiService.set(leavesDownloadApiService())
        downloadPlugins.from(runServerPlugins)
        minecraftVersion(libs.versions.leavesApi.extractMCVersion())
        systemProperty("leavesclip.enable.mixin", true)
        systemProperty("file.encoding", Charsets.UTF_8.name())
    }

    withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.forkOptions.memoryMaximumSize = "6g"

        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    named<JavaCompile>("compileMixinJava") {
        dependsOn("paperweightUserdevSetup")
    }

    shadowJar {
        from(mixinSourceSet.output)
        archiveFileName = "${project.name}-${version}.jar"
    }

    build {
        dependsOn(shadowJar)
    }
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

fun getMappedServerJar(): String = File(rootDir, ".gradle")
    .resolve("caches/paperweight/taskCache/mappedServerJar.jar")
    .path

fun Provider<String>.extractMCVersion(): String {
    val versionString = this.get()
    val regex = Regex("""^(1\.\d+(?:\.\d+)?)""")
    return regex.find(versionString)?.groupValues?.get(1)
        ?: throw IllegalArgumentException("Cannot extract mcVersion from $versionString")
}

fun leavesDownloadApiService(): Provider<out DownloadsAPIService> = registerIfAbsent(project) {
    downloadsEndpoint = "https://api.leavesmc.org/v2/"
    downloadProjectName = "leaves"
    buildServiceName = "leaves-download-service"
}

// The codes below is under Apache License 2.0
// original repo is https://github.com/jpenilla/resource-factory
//
// Resource Factory Gradle Plugin
// Copyright (c) 2024 Jason Penilla
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

fun Project.leavesPluginJson(configure: Action<LeavesPluginJson> = nullAction()): LeavesPluginJson {
    val json = LeavesPluginJson(objects)
    json.setConventionsFromProjectMeta(this)
    configure.execute(json)
    return json
}

@Suppress("unused")
class LeavesPluginJson(
    @Transient
    private val objects: ObjectFactory
) : ConfigurateSingleFileResourceFactory.Simple.ValueProvider, ProjectMetaConventions, ResourceFactory.Provider {
    companion object {
        private const val PLUGIN_NAME_PATTERN: String = "^[A-Za-z0-9_\\.-]+$"
        private const val PLUGIN_CLASS_PATTERN: String =
            "^(?!io\\.papermc\\.)([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*$"
        private const val FILE_NAME: String = "leaves-plugin.json"
    }

    @get:Input
    val apiVersion: Property<String> = objects.property()

    @Pattern(PLUGIN_NAME_PATTERN, "Paper plugin name")
    @get:Input
    val name: Property<String> = objects.property()

    @get:Input
    val version: Property<String> = objects.property()

    @Pattern(PLUGIN_CLASS_PATTERN, "Leaves plugin main class name")
    @get:Input
    val main: Property<String> = objects.property()

    @Pattern(PLUGIN_CLASS_PATTERN, "Leaves plugin loader class name")
    @get:Input
    @get:Optional
    val loader: Property<String> = objects.property()

    @Pattern(PLUGIN_CLASS_PATTERN, "Leaves plugin bootstrapper class name")
    @get:Input
    @get:Optional
    val bootstrapper: Property<String> = objects.property()

    @get:Input
    @get:Optional
    val description: Property<String> = objects.property()

    @get:Input
    @get:Optional
    val author: Property<String> = objects.property()

    @get:Input
    @get:Optional
    val authors: ListProperty<String> = objects.listProperty()

    @get:Input
    @get:Optional
    val contributors: ListProperty<String> = objects.listProperty()

    @get:Input
    @get:Optional
    val website: Property<String> = objects.property()

    @get:Input
    @get:Optional
    val prefix: Property<String> = objects.property()

    @get:Input
    @get:Optional
    val defaultPermission: Property<Permission.Default> = objects.property()

    @get:Input
    @get:Optional
    val foliaSupported: Property<Boolean> = objects.property()

    @get:Nested
    var dependencies: Dependencies = objects.newInstance(Dependencies::class)

    @get:Nested
    val features: Features = objects.newInstance(Features::class)

    @get:Nested
    val mixin: Mixin = objects.newInstance(Mixin::class)

    @get:Input
    @get:Optional
    @Pattern(PLUGIN_NAME_PATTERN, "Leaves plugin name (of provides)")
    val provides: ListProperty<String> = objects.listProperty()

    @get:Nested
    val permissions: NamedDomainObjectContainer<Permission> =
        objects.domainObjectContainer(Permission::class) { Permission(objects, it) }

    fun dependencies(configure: Action<Dependencies>) {
        configure.execute(dependencies)
    }

    override fun setConventionsFromProjectMeta(project: Project) {
        name.convention(project.name)
        version.convention(project.version as String?)
        description.convention(project.description)
    }

    enum class Load {
        BEFORE,
        AFTER,
        OMIT
    }

    abstract class Dependencies @Inject constructor(objects: ObjectFactory) {
        @get:Nested
        val bootstrap: NamedDomainObjectContainer<Dependency> =
            objects.domainObjectContainer(Dependency::class) { Dependency(objects, it) }

        @get:Nested
        val server: NamedDomainObjectContainer<Dependency> =
            objects.domainObjectContainer(Dependency::class) { Dependency(objects, it) }

        fun bootstrap(
            name: String,
            load: Load = Load.OMIT,
            required: Boolean = true,
            joinClasspath: Boolean = true
        ): NamedDomainObjectProvider<Dependency> = bootstrap.register(name) {
            this.load.set(load)
            this.required.set(required)
            this.joinClasspath.set(joinClasspath)
        }

        fun server(
            name: String,
            load: Load = Load.OMIT,
            required: Boolean = true,
            joinClasspath: Boolean = true
        ): NamedDomainObjectProvider<Dependency> = server.register(name) {
            this.load.set(load)
            this.required.set(required)
            this.joinClasspath.set(joinClasspath)
        }
    }

    class Dependency(
        objects: ObjectFactory,
        @get:Input
        val name: String
    ) {
        @get:Input
        val load: Property<Load> = objects.property<Load>().convention(Load.OMIT)

        @get:Input
        val required: Property<Boolean> = objects.property<Boolean>().convention(true)

        @get:Input
        val joinClasspath: Property<Boolean> = objects.property<Boolean>().convention(true)
    }

    abstract class Features @Inject constructor(objects: ObjectFactory) {
        @get:Input
        @get:Optional
        val required: ListProperty<String> = objects.listProperty()

        @get:Input
        @get:Optional
        val optional: ListProperty<String> = objects.listProperty()
    }

    abstract class Mixin @Inject constructor(objects: ObjectFactory) {
        @get:Input
        @get:Optional
        val packageName: Property<String> = objects.property()

        @get:Input
        @get:Optional
        val mixins: ListProperty<String> = objects.listProperty()

        @get:Input
        @get:Optional
        val accessWidener: Property<String> = objects.property()
    }

    override fun resourceFactory(): ResourceFactory {
        val gen = objects.newInstance(ConfigurateSingleFileResourceFactory.Simple::class)
        gen.json {
            defaultOptions {
                it.serializers { s ->
                    s.registerExact(Permission.Default::class.java, Permission.Default.Serializer)
                }
            }
        }
        gen.path.set(FILE_NAME)
        gen.value.set(this)
        return gen
    }

    override fun asConfigSerializable(): Any {
        return Serializable(this)
    }

    @ConfigSerializable
    class Serializable(json: LeavesPluginJson) {
        val apiVersion = json.apiVersion.get()
        val name = json::name.getValidating()
        val version = json.version.get()
        val main = json::main.getValidating()
        val loader = json::loader.orNullValidating()
        val bootstrapper = json::bootstrapper.orNullValidating()
        val description = json.description.orNull
        val author = json.author.orNull
        val authors = json.authors.nullIfEmpty()
        val contributors = json.contributors.nullIfEmpty()
        val website = json.website.orNull
        val prefix = json.prefix.orNull
        val defaultPermission = json.defaultPermission.orNull
        val foliaSupported = json.foliaSupported.orNull
        val dependencies = SerializableDependencies.from(json.dependencies)
        val features = SerializableFeatures.from(json.features)
        val mixin = SerializableMixin.from(json.mixin)
        val provides = json::provides.nullIfEmptyValidating()
        val permissions = json.permissions.nullIfEmpty()?.mapValues { Permission.Serializable(it.value) }
    }

    @ConfigSerializable
    data class SerializableFeatures(
        val required: List<String>?,
        val optional: List<String>?
    ) {
        companion object {
            fun from(features: Features): SerializableFeatures? {
                val required = features.required.nullIfEmpty()
                val optional = features.optional.nullIfEmpty()
                if (required == null && optional == null) {
                    return null
                }
                return SerializableFeatures(required, optional)
            }
        }
    }

    @ConfigSerializable
    data class SerializableMixin(
        val packageName: String?,
        val mixins: List<String>?,
        val accessWidener: String?
    ) {
        companion object {
            fun from(mixin: Mixin): SerializableMixin {
                return SerializableMixin(
                    mixin.packageName.orNull,
                    mixin.mixins.nullIfEmpty(),
                    mixin.accessWidener.orNull
                )
            }
        }
    }

    @ConfigSerializable
    data class SerializableDependency(val load: Load, val required: Boolean, val joinClasspath: Boolean) {
        companion object {
            fun from(dep: Dependency) = SerializableDependency(
                dep.load.get(),
                dep.required.get(),
                dep.joinClasspath.get()
            )
        }
    }

    @ConfigSerializable
    data class SerializableDependencies(
        val bootstrap: Map<String, SerializableDependency>?,
        val server: Map<String, SerializableDependency>?
    ) {
        companion object {
            fun from(deps: Dependencies): SerializableDependencies? {
                val bs = deps.bootstrap.nullIfEmpty()?.mapValues { SerializableDependency.from(it.value) }
                    .also {
                        it?.keys?.validateAll(
                            PLUGIN_NAME_PATTERN,
                            "Leaves plugin name (of bootstrap dependency)"
                        )
                    }
                val server = deps.server.nullIfEmpty()?.mapValues { SerializableDependency.from(it.value) }
                    .also {
                        it?.keys?.validateAll(
                            PLUGIN_NAME_PATTERN,
                            "Leaves plugin name (of server dependency)"
                        )
                    }
                if (bs == null && server == null) {
                    return null
                }
                return SerializableDependencies(bs, server)
            }
        }
    }
}

@Suppress("unused")
abstract class LeavesConvention : ResourceFactoryConventionPlugin.Provider<LeavesPluginJson>(
    "leavesPluginJson",
    { project -> project.leavesPluginJson() }
)