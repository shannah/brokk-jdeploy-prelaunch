# Brokk jDeploy Prelauncher

A specialized prelauncher for [Brokk](https://brokk.ai) jDeploy distributions that ensures launcher compatibility before running the main application.

## Overview

This project builds `jdeploy-prelaunch.jar`, which is embedded with Brokk jDeploy distributions and executed before launching the main Brokk application. It solves a critical compatibility issue with older Brokk launchers.

## Problem Statement

Starting in version 0.18, the Brokk launcher includes self-update capabilities. However, versions prior to 0.18 shipped with launchers that lacked this functionality. This creates a compatibility problem:

1. Older launchers (pre-0.18) could auto-update the Brokk application itself
2. If the updated app version became incompatible with the old launcher, the application would crash
3. Users had no way to update the launcher without manual intervention

## Solution

The prelauncher acts as a compatibility layer that:

1. **Detects** when an outdated launcher is being used
2. **Prompts** the user to update to the latest version
3. **Downloads** the appropriate installer for the user's system if they choose to update
4. **Opens** the installer so users can install the latest version with an updated launcher

## Technical Details

- Built as a single-use-case JAR file: `jdeploy-prelaunch.jar`
- Embedded with Brokk jDeploy distributions
- Executes before the main Brokk application launches
- Requires launcher version 0.18.0 or higher for full functionality
- Built with Java 21 and Gradle
- Uses Shadow plugin to create a standalone executable JAR with dependencies

## Usage

This prelauncher is automatically embedded in Brokk distributions and requires no manual configuration. End users will see an update prompt if they're running an outdated launcher version.

## Building

```bash
# Build the shadow JAR
./gradlew shadowJar

# The compiled JAR will be available at:
# build/libs/jdeploy-prelaunch.jar
```

### Development

```bash
# Run the prelauncher for testing (simulates version 0.17.1)
./gradlew runPrelaunch
```

## License

This project is licensed under GPL-3.0, matching the [Brokk](https://github.com/BrokkAi/brokk) license.