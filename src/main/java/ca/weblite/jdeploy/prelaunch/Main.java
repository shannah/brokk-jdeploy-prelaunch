package ca.weblite.jdeploy.prelaunch;

import ca.weblite.jdeploy.updateclient.UpdateClient;
import ca.weblite.jdeploy.updateclient.UpdateParameters;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Small launcher that constructs UpdateClient and UpdateParameters and invokes
 * requireVersion("0.17.3", params, forceUpdate=true).
 *
 * packageName and source may be provided via system properties or environment variables:
 *  - system property "packageName" or env "PACKAGE_NAME" (default: "brokk")
 *  - system property "source" or env "SOURCE" (default: "")
 *
 * Optional system properties:
 *  - "appTitle"
 *  - "currentVersion"
 */
public final class Main {
    private static final String PACKAGE_NAME = "brokk";
    private static final String SOURCE = "https://github.com/BrokkAi/brokk";
    private static final String APP_TITLE = "Brokk";
    private static final String CURRENT_LAUNCHER_VERSION = "0.17.1";
    private static final String REQUIRE_LAUNCHER_VERSION = "0.17.3";

    private Main() {
        // no-op
    }

    public static void main(String[] args) {
            // There was a bug in jdeploy 5.5.11 that placed the jdeploy.launcher.app.version after the jar file
            // which places it incorrectly. So we set it here if not already set.
            var launcherAppVersionArgs = Stream.of(args)
                    .filter(s -> s.startsWith("-Djdeploy.launcher.app.version="))
                    .findAny();

            if (System.getProperty("jdeploy.launcher.app.version") == null) {
                if (launcherAppVersionArgs.isPresent()) {
                    var launcherVersionParam = launcherAppVersionArgs.get();
                    var launcherVersion = launcherVersionParam.substring(launcherVersionParam.indexOf('=') + 1);
                    System.setProperty("jdeploy.launcher.app.version", launcherVersion);
                } else {
                    System.setProperty("jdeploy.launcher.app.version", CURRENT_LAUNCHER_VERSION);
                }
            }
            UpdateParameters params = new UpdateParameters.Builder(PACKAGE_NAME)
                            .source(SOURCE)
                            .appTitle(APP_TITLE)
                            .icon(Main.class.getResource("brokk-icon.png"))
                            .build();

            UpdateClient client = new UpdateClient();

            final boolean forceUpdate = true;

            try {
                    // Direct invocation (no reflection): require update or quit when forceUpdate=true
                    client.requireVersion(REQUIRE_LAUNCHER_VERSION, params, forceUpdate);
            } catch (Throwable ex) {
                    System.err.println("Failed to invoke UpdateClient.requireVersion: " + ex);
                    ex.printStackTrace(System.err);
                    System.exit(2);
            }
    }
}
