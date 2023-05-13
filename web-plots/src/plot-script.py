import pandas as pd
import matplotlib.pyplot as plt




def read_data(filename):
    """Reads data from a CSV file and returns a pandas DataFrame"""
    return pd.read_csv(filename)


def get_classifier_names(df):
    """Returns a list of unique classifier names in the DataFrame"""
    return df['CLASSIFIER'].str.split('&').str[0].unique().tolist()


def get_feature_selection_types(df):
    """Returns a list of unique feature selection types in the DataFrame"""
    return df['CLASSIFIER'].str.split('&').str[1].unique().tolist()


def get_sampling_types(df):
    """Returns a list of unique sampling types in the DataFrame"""
    return df['CLASSIFIER'].str.split('&').str[2].unique().tolist()


def get_cost_sensitive_types(df):
    """Returns a list of unique cost sensitive types in the DataFrame"""
    return df['CLASSIFIER'].str.split('&').str[3].unique().tolist()


def filter_data(df, fs_type, s_type, cs_type):
    """Filters the DataFrame by feature selection, sampling, and cost sensitive types"""
    return df[df['CLASSIFIER'].str.contains(fs_type + '&' + s_type + '&' + cs_type)]


def plot_metrics(df, classifier_names, fs_type, s_type, cs_type):
    """Plots the metrics for each classifier in a filtered DataFrame"""
    metrics = ['PRECISION', 'RECALL', 'AUC', 'KAPPA']
    for metric in metrics:
        plt.figure()
        plt.title('{}, {}, {}, {}'.format(fs_type, s_type, cs_type, metric))
        for i, classifier_name in enumerate(classifier_names):
            classifier_df = df[df['CLASSIFIER'].str.startswith(classifier_name)]
            plt.plot(classifier_df['TRAINING_RELEASES'], classifier_df[metric], label=classifier_name, color='C{}'.format(i))
        plt.legend()
        plt.xlabel('TRAINING_RELEASES')
        plt.ylabel(metric)
        plt.savefig('plots/{}_{}_{}_{}.png'.format(fs_type, s_type, cs_type, metric))


def plot_boxplots(df, classifier_names, metric):
    """Plots a box plot for each classifier's performance on a given metric"""
    plt.figure()
    plt.title(metric)
    data = []
    for classifier_name in classifier_names:
        classifier_df = df[df['CLASSIFIER'].str.startswith(classifier_name)]
        data.append(classifier_df[metric])
    plt.boxplot(data, labels=classifier_names)
    plt.ylabel(metric)
    plt.savefig('plots/{}_boxplot.png'.format(metric))


def main():
    # read data from CSV file
    df = read_data('data.csv')

    # create plots directory if it doesn't exist
    import os
    if not os.path.exists('plots'):
        os.makedirs('plots')

    # get unique classifier names, feature selection types, sampling types, and cost sensitive types
    classifier_names = get_classifier_names(df)
    feature_selection_types = get_feature_selection_types(df)
    sampling_types = get_sampling_types(df)
    cost_sensitive_types = get_cost_sensitive_types(df)

    # plot metrics and box plots for each combination of feature selection, sampling, and cost sensitive types
    for fs_type in feature_selection_types:
        for s_type in sampling_types:
            for cs_type in cost_sensitive_types:
                filtered_df = filter_data(df, fs_type, s_type, cs_type)
                plot_metrics(filtered_df, classifier_names, fs_type, s_type, cs_type)
    for metric in ['PRECISION', 'RECALL', 'AUC', 'KAPPA']:
        plot_boxplots(df, classifier_names, metric)


if __name__ == '__main__':
    main()
