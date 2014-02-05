GradleBukkit
===========

GradleBukkit is a gradle plugin which allows you to download, manage and start a CraftBukkit server for development purposes.

How to use
----------
**Note**: *This Gradle plugin is still in beta which means that it might be unstable or not working at all - please file a bug report including the error/exception if you experience any issues. Furthermore, some features are incomplete or not well documented. A full documentation will be published once the plugin in stable.*

As for the GradleCurse plugin, I'm currently working on getting a stable release out and having it hosted on Maven Central.
For now, however, you will have to clone this repository and do a local `gradle publishToMavenLocal`

**gradle.build example**
```Groovy
buildscript {
    repositories {
            mavenCentral()
            mavenLocal()
    }
    dependencies {
        classpath group: 'net.monofraps', name: 'GradleBukkit', version: '1.0-SNAPSHOT'
    }
}

apply plugin: 'gradle-bukkit'

bukkit {
    plugin 'build/libs/MyBukkitPlugin-1.0.0-SNAPSHOT.jar'
}
```

If you run `gradle runBukkit` now the plugin will automatically download the latest CraftBukkit beta build, move the file `MyBukkitPlugin-1.0.0-SNAPSHOT.jar` into the servers plugins directory and start the server.
Stdin/out are redirected to/from the Gradle build command line. So you will be able to see Bukkit's log and send commands to the server.

If you wish to stop the server, simply send the regular Bukkit `stop` command. After the two stream grabbers have disconnected you have to use `gterm` to finish the build process.

Plugin Configuration
--------------------
* `bukkit`
 * `channel` - (optional, defaults to beta) The name of the release channel to use when searching for CraftBukkit artifacts. Leave null or empty to use all available channels.
 * `artifactSlug` - (optional, defaults to latest) The artifact slug to use when looking for CraftBukkit artifacts. (See below)
 * `additionalJvmArgs` - (optional) Allows you to pass additional arguments to the JVM.
 * `plugin` - (optional) Copies a file or directory to the server's `plugins` directory.
 * `remoteDebugging` - (optional) Allows you to enable automatic remote debugging configuration.
  * `jvmArguments` - (optional) The JVM argument template used for building the remote debugging JVM argument. $transport$ will be replaced with the transport identifier, $address$ will be replaced with the address (either memory or port)
  * `address` - (optional, defaults to "21451") The address to use for remote debugging. Can either be a text string when using shared memory transport or a numeric string specifying a network port when using socket transport.
  * `debuggingTransport` (optional, defaults to SOCKET (See below)


**Artifact Slug**:
* "latest" - either absolute latest artifact or latest of channel if channel is neither null nor empty
* "git-[commit_ref]" - use the artifact with the given commit reference
* "build-[build_number]" - use the artifact with the given build number


**Debugging Transport Options**:

Debugging transport options are defined in net.monofraps.gradlebukkit.DebuggingTransport

* SOCKET (network transport, specify port in address field)
* SHARED_MEM (shared memory, specify shared memory name in address field)

Default Tasks
-------------
The GradleBukkit plugin automatically creates some tasks.

* `listAvailableBukkitVersions` - Shows the latest 3 CraftBukkit builds.
* `downloadCraftBukkit` - Downloads a CraftBukkit server jar. The chosen artifact depends on your configuration.
* `copyBukkitPlugins` - Copies all `plugin`s defined in your configuration into the server's `plugins` directory.
* `runBukkit` - (depends on `downloadCraftBukkit` and `copyBukkitPlugins`) Starts the server.

Where are the server files?
-------------------------------
The plugin creates a `bukkit` directory in the project `buildDir` (usually `~/build`).
