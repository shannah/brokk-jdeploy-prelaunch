package ca.weblite.jdeploy.prelaunch;

import ca.weblite.jdeploy.updateclient.UpdateClient;
import ca.weblite.jdeploy.updateclient.UpdateParameters;

import java.util.concurrent.CompletableFuture;
import java.util.Objects;

/**
 * Small launcher that constructs UpdateClient and UpdateParameters and invokes
 * requireVersion("0.17.3", params, /*forceUpdate=*/true).
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
        String source = firstNonEmpty(System.getProperty("source"), System.getenv("SOURCE"), "");
        String appTitle = firstNonEmpty(System.getProperty("appTitle"), null, "");
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
            // Try requireVersion(String, UpdateParameters, boolean)
            try {
                java.lang.reflect.Method m = client.getClass()
                        .getMethod("requireVersion", String.class, UpdateParameters.class, boolean.class);
                m.invoke(client, requiredVersion, params, forceUpdate);
                return;
            } catch (NoSuchMethodException ignored) {
                // try next
            }

            // Try requireVersion(String, UpdateParameters)
            try {
                java.lang.reflect.Method m = client.getClass()
                        .getMethod("requireVersion", String.class, UpdateParameters.class);
                m.invoke(client, requiredVersion, params);
                return;
            } catch (NoSuchMethodException ignored) {
                // try next
            }

            // Try requireVersionAsync(String, UpdateParameters) and wait for completion
            try {
                java.lang.reflect.Method m = client.getClass()
                        .getMethod("requireVersionAsync", String.class, UpdateParameters.class);
                Object result = m.invoke(client, requiredVersion, params);
                if (result instanceof CompletableFuture) {
                    ((CompletableFuture<?>) result).join();
                }
                return;
            } catch (NoSuchMethodException ignored) {
                // no suitable method found
            }

            System.err.println("No suitable requireVersion method found on UpdateClient.");
            System.exit(1);
        } catch (ReflectiveOperationException ex) {
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
