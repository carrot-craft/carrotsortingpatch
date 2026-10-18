package io.github.countyclaaw4.carrotcraftsortingpatch.modmenu;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.UpdateChannel;
import com.terraformersmc.modmenu.api.UpdateChecker;
import com.terraformersmc.modmenu.api.UpdateInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CarrotSortingPatchModMenuApi implements ModMenuApi {
    private static final String MOD_ID = "carrotsortingpatch";
    private static final String RELEASES_URL = "https://github.com/carrot-craft/carrotsortingpatch/releases";
    private static final URI LATEST_RELEASE_URI = URI.create(
            "https://api.github.com/repos/carrot-craft/carrotsortingpatch/releases/latest");

    @Override
    public UpdateChecker getUpdateChecker() {
        String currentVersion = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(mod -> mod.getMetadata().getVersion().getFriendlyString())
                .orElse("0.0.0");
        return new GitHubReleaseUpdateChecker(currentVersion);
    }

    private static final class GitHubReleaseUpdateChecker implements UpdateChecker {
        private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        private static final Pattern SEMVER = Pattern.compile(
                "^[vV]?(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)"
                        + "(?:-([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?"
                        + "(?:\\+[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*)?$");

        private final SemanticVersion currentVersion;

        private GitHubReleaseUpdateChecker(String currentVersion) {
            this.currentVersion = SemanticVersion.parse(currentVersion);
        }

        @Override
        public UpdateInfo checkForUpdates() {
            if (currentVersion == null) {
                return null;
            }

            HttpRequest request = HttpRequest.newBuilder(LATEST_RELEASE_URI)
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .header("User-Agent", "CarrotSortingPatch-UpdateChecker")
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    return null;
                }

                JsonObject release = JsonParser.parseString(response.body()).getAsJsonObject();
                if (release.get("draft").getAsBoolean() || release.get("prerelease").getAsBoolean()) {
                    return null;
                }

                String tagName = release.get("tag_name").getAsString();
                SemanticVersion latestVersion = SemanticVersion.parse(tagName);
                if (latestVersion == null || latestVersion.compareTo(currentVersion) <= 0) {
                    return null;
                }

                String downloadLink = release.has("html_url")
                        ? release.get("html_url").getAsString()
                        : RELEASES_URL;
                return new ReleaseUpdateInfo(tagName, downloadLink);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } catch (IOException | RuntimeException e) {
                return null;
            }
        }
    }

    private record ReleaseUpdateInfo(String version, String downloadLink) implements UpdateInfo {
        @Override
        public boolean isUpdateAvailable() {
            return true;
        }

        @Override
        public Component getUpdateMessage() {
            return Component.literal(version + " on GitHub");
        }

        @Override
        public String getDownloadLink() {
            return downloadLink;
        }

        @Override
        public UpdateChannel getUpdateChannel() {
            return UpdateChannel.RELEASE;
        }
    }

    private record SemanticVersion(BigInteger major, BigInteger minor, BigInteger patch, List<String> prerelease)
            implements Comparable<SemanticVersion> {
        private static SemanticVersion parse(String value) {
            Matcher matcher = GitHubReleaseUpdateChecker.SEMVER.matcher(value);
            if (!matcher.matches()) {
                return null;
            }

            List<String> prerelease = new ArrayList<>();
            if (matcher.group(4) != null) {
                prerelease = List.of(matcher.group(4).split("\\."));
            }
            return new SemanticVersion(
                    new BigInteger(matcher.group(1)),
                    new BigInteger(matcher.group(2)),
                    new BigInteger(matcher.group(3)),
                    prerelease);
        }

        @Override
        public int compareTo(SemanticVersion other) {
            int comparison = major.compareTo(other.major);
            if (comparison == 0) comparison = minor.compareTo(other.minor);
            if (comparison == 0) comparison = patch.compareTo(other.patch);
            if (comparison != 0) return comparison;
            if (prerelease.isEmpty() && other.prerelease.isEmpty()) return 0;
            if (prerelease.isEmpty()) return 1;
            if (other.prerelease.isEmpty()) return -1;

            int sharedIdentifiers = Math.min(prerelease.size(), other.prerelease.size());
            for (int i = 0; i < sharedIdentifiers; i++) {
                String left = prerelease.get(i);
                String right = other.prerelease.get(i);
                boolean leftNumeric = left.chars().allMatch(Character::isDigit);
                boolean rightNumeric = right.chars().allMatch(Character::isDigit);

                if (leftNumeric && rightNumeric) {
                    comparison = new BigInteger(left).compareTo(new BigInteger(right));
                } else if (leftNumeric != rightNumeric) {
                    comparison = leftNumeric ? -1 : 1;
                } else {
                    comparison = left.compareTo(right);
                }
                if (comparison != 0) return comparison;
            }
            return Integer.compare(prerelease.size(), other.prerelease.size());
        }
    }
}
