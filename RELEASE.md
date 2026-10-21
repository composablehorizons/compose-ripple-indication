# Releasing to Maven Central

Publishing uses [KPM](https://github.com/composablehorizons/kpm) 0.1.0. Install the CLI:

```shell
bun install --global @composablehorizons/kpm@0.1.0
```

Run the following commands from the repository root, which contains the Gradle wrapper
and `package.yml`. The library module's `package.yml` is a symlink to the root file,
so the CLI and Gradle plugin share one version and set of Maven metadata. Preserve
that symlink when checking out the repository.

## Credentials

Set the standard Gradle publishing properties through environment variables or your
user-level `~/.gradle/gradle.properties` file:

- `mavenCentralUsername`: Central Portal user-token username.
- `mavenCentralPassword`: Central Portal user-token password.
- `signingInMemoryKey`: ASCII-armored private signing key.
- `signingInMemoryKeyId`: Signing key ID, if required by your key.
- `signingInMemoryKeyPassword`: Signing key password, if required by your key.

For environment variables, prefix each property with `ORG_GRADLE_PROJECT_`, for
example `ORG_GRADLE_PROJECT_mavenCentralUsername`. Keep credentials out of Git.
The previous `sonatype.*` and `signing.*` entries in `local.properties` are no
longer used for publishing.

## Verify and publish

Start from a clean working tree. Use macOS with an Android SDK and Xcode installed
to build all configured targets, including iOS.

```shell
./gradlew :ripple-indication:allTests
kpm version patch
kpm publish --dry-run
kpm publish
```

`kpm version patch` updates `package.yml`, commits the version, and creates an
annotated `v<version>` tag. Use an exact version instead of `patch` when needed.
`kpm publish --dry-run` inspects the task graph without building or uploading.
`kpm publish` builds, signs, uploads, and automatically releases the artifacts on
Maven Central. It does not run tests or push Git commits and tags.

After a successful release, push the release commit and tag:

```shell
git push --follow-tags
```

Published artifacts appear under
[com.composables:ripple-indication](https://repo1.maven.org/maven2/com/composables/ripple-indication/).

## Publish to Maven Local

```shell
./gradlew :ripple-indication:publishToMavenLocal
```

Add `mavenLocal()` to the consuming project's repositories to use the local artifacts.
