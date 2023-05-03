package it.uniroma2.writer;

import java.nio.file.Path;

public class PathBuilder {

    private PathBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static Path buildRootDirectoryPath(String projName) {
        return Path.of("./outputs", projName);
    }

    public static Path buildReportPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "report");
    }

    public static Path buildReportReleasesPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "report", "releases.txt");
    }

    public static Path buildReportIssuesPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "report", "issues.txt");
    }

    public static Path buildReportProportionPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "report", "proportion.txt");
    }

    public static Path buildReportGitPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "report", "git.txt");
    }

    public static Path buildTrainingSetPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "dataset", "training_set");
    }

    public static Path buildTestingSetPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "dataset", "testing_set");
    }

    public static Path buildWekaTrainDirectories(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "weka", "training_set");
    }

    public static Path buildWekaTestDirectories(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "weka", "testing_set");
    }

    public static Path buildTrainingSetFile(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "dataset", "training_set", projName);
    }

    public static Path buildTestingSetFile(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "dataset", "testing_set", projName);
    }

    public static Path buildWekaTrainFile(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "weka", "training_set", projName);
    }

    public static Path buildWekaTestFile(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "weka", "testing_set", projName);
    }

    public static Path buildWekaEvaluationPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "weka", "evaluation.csv");
    }

    public static Path buildWekaPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), "weka");
    }
}
