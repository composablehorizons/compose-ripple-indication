# Releasing to Maven Central

Releases follow the same KPM and GitHub Actions flow as Compose System UI and
Compose Google Fonts. Pushing a version tag publishes signed artifacts to Maven
Central, then creates a **draft** GitHub release from `CHANGELOG.md`. Review and publish that draft separately.

## Prepare a release

Install [KPM](https://github.com/composablehorizons/kpm) 0.1.0:

```shell
bun install --global @composablehorizons/kpm@0.1.0
```

Run commands from the repository root, which contains the Gradle wrapper and
`package.yml`. The library module's `package.yml` is a symlink to the root file,
so the CLI and Gradle plugin share one version and set of Maven metadata. Preserve
that symlink when checking out the repository.

1. Move the release notes from `Unreleased` into a dated heading matching the next
   version, for example `## [1.2.1] - YYYY-MM-DD`. Keep an empty `Unreleased` section.
2. Run the same checks as CI on macOS with JDK 17, Android SDK platform 36, and Xcode:

   ```shell
   ./gradlew :ripple-indication:jvmTest :ripple-indication:publishToMavenLocal --no-configuration-cache --console=plain
   ```

3. Update the package version without creating a release tag yet:

   ```shell
   kpm version patch --no-git-tag-version
   ```

   KPM updates `package.yml`. Use an exact version instead of `patch` when needed.
   Commit the package version and changelog together and merge them through a pull request.
4. Check out the merged release commit on `main`, then create and push its tag:

   ```shell
   git switch main
   git pull --ff-only origin main
   git tag -a v1.2.1 -m "Release 1.2.1"
   git push origin v1.2.1
   ```

   Replace `v1.2.1` with the prepared release tag. The `Release` workflow validates
   the tag, package version, and changelog; runs JVM tests and builds every platform
   publication; then publishes with `kpm publish`. Once publishing succeeds, it
   creates a draft GitHub release with that version's changelog notes.

The workflow also supports **Actions → Release → Run workflow** with an existing
release tag. Dispatching does not bump the version or create a tag. Check the failed
run and Maven Central before retrying a publish; released versions cannot be overwritten.

## GitHub publishing credentials

The publish job uses the `maven-central` environment and the same organization
secret names as System UI and Google Fonts:

- `OSSRH_USERNAME` and `OSSRH_PASSWORD`: Central Portal user token.
- `SIGNING_KEY`: ASCII-armored private signing key.
- `SIGNING_KEY_ID` and `SIGNING_PASSWORD`: Signing key ID and password.

These secrets must be accessible to this repository. The workflow maps them to
KPM's standard Gradle properties. The previous `sonatype.*` and `signing.*` entries
in `local.properties` are no longer used for publishing.

Published artifacts appear under
[com.composables:ripple-indication](https://repo1.maven.org/maven2/com/composables/ripple-indication/).

## Publish to Maven Local

```shell
./gradlew :ripple-indication:publishToMavenLocal
```

Add `mavenLocal()` to the consuming project's repositories to use the local artifacts.
