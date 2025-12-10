package ca.weblite.jdeploy.prelaunch;

import ca.weblite.jdeploy.updateclient.UpdateClient;
import ca.weblite.jdeploy.updateclient.UpdateParameters;

import java.util.concurrent.CompletableFuture;
import java.util.Objects;

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
    private Main() {
        // no-op
    }

    public static void main(String[] args) {
            String packageName = firstNonEmpty(System.getProperty("packageName"), System.getenv("PACKAGE_NAME"), "brokk");
            String source = firstNonEmpty(System.getProperty("source"), System.getenv("SOURCE"), "https://github.com/BrokkAi/brokk");
            String appTitle = firstNonEmpty(System.getProperty("appTitle"), null, "Brokk");
            String currentVersion = firstNonEmpty(System.getProperty("currentVersion"), null, "");

            UpdateParameters params = new UpdateParameters.Builder(packageName)
                            .source(source)
                            .appTitle(appTitle)
                            .currentVersion(currentVersion)
                            .build();

            UpdateClient client = new UpdateClient();
            final String requiredVersion = "0.17.3";
            final boolean forceUpdate = true;

            try {
                    // Direct invocation (no reflection): require update or quit when forceUpdate=true
                    client.requireVersion(requiredVersion, params, forceUpdate);
            } catch (Throwable ex) {
                    System.err.println("Failed to invoke UpdateClient.requireVersion: " + ex);
                    ex.printStackTrace(System.err);
                    System.exit(2);
            }
    }

    private static String firstNonEmpty(String first, String second, String fallback) {
        if (isNonEmpty(first)) return first;
        if (isNonEmpty(second)) return second;
        return fallback;
    }

    private static boolean isNonEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
