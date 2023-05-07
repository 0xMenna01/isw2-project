package it.uniroma2.writer;

import java.nio.file.Path;

public class PathBuilder {

    private static final String REPORT = "report";
    public static final String DATASET = "dataset";
    public static final String TRAINING_SET = "training_set";
    public static final String TESTING_SET = "testing_set";
    public static final String WEKA = "weka";

    private PathBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static Path buildRootDirectoryPath(String projName) {
        return Path.of("./outputs", projName);
    }

    public static Path buildReportPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), REPORT);
    }

    public static Path buildReportReleasesPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), REPORT, "releases.txt");
    }

    public static Path buildReportIssuesPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), REPORT, "issues.txt");
    }

    public static Path buildReportProportionPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), REPORT, "proportion.txt");
    }

    public static Path buildReportGitPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), REPORT, "git.txt");
    }

    public static Path buildTrainingSetPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), DATASET, TRAINING_SET);
    }

    public static Path buildTestingSetPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), DATASET, TESTING_SET);
    }

    public static Path buildWekaTrainDirectories(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), WEKA, TRAINING_SET);
    }

    public static Path buildWekaTestDirectories(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), WEKA, TESTING_SET);
    }

    public static Path buildTrainingSetFile(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), DATASET, TRAINING_SET, projName);
    }

    public static Path buildTestingSetFile(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), DATASET, TESTING_SET, projName);
    }

    public static Path buildWekaTrainFile(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), WEKA, TRAINING_SET, projName);
    }

    public static Path buildWekaTestFile(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), WEKA, TESTING_SET, projName);
    }

    public static Path buildWekaEvaluationPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), WEKA, "evaluation.csv");
    }

    public static Path buildWekaPath(String projName) {
        Path rootPath = buildRootDirectoryPath(projName);
        return Path.of(rootPath.toString(), WEKA);
    }
}
