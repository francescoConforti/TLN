from wordcloud import WordCloud, STOPWORDS
import matplotlib.pyplot as plt
import pandas as pd
from nltk.tokenize import word_tokenize, sent_tokenize
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer, PorterStemmer
from string import punctuation
import re
import argparse
import sentiment_analysis

porter = PorterStemmer()
wnl = WordNetLemmatizer()


def tokenize(text):

    tokens_ = [word_tokenize(sent) for sent in sent_tokenize(text)]

    tokens = []
    for token_by_sent in tokens_:
        tokens += token_by_sent

    tokens = list(filter(lambda t: t.lower()
                         not in stopwords.words('english'), tokens))
    tokens = list(filter(lambda t: t not in punctuation, tokens))

    filtered_tokens = []
    for token in tokens:
        token = wnl.lemmatize(token)
        if re.search('[a-zA-Z]', token):
            filtered_tokens.append(token)

    filtered_tokens = list(map(lambda token: token, filtered_tokens))

    return filtered_tokens

# take a list of tweets and make a wordcloud of it


def plot_wordcloud(tweets, sentiment="neutral"):
    stopwords = set(STOPWORDS)
    text = " ".join(tweets)

    # Choose colors
    if sentiment == "positive":
        bg = 'Lavender'
        cmap = 'spring'
    elif sentiment == "neutral":
        bg = 'black'
        cmap = 'Blues'
    elif sentiment == "negative":
        bg = 'black'
        cmap = 'hot'
    else:
        bg = 'LightGrey'
        cmap = 'viridis'

    wordcloud = WordCloud(width=800, height=800,
                          background_color=bg,
                          stopwords=stopwords,
                          colormap=cmap,
                          min_font_size=10).generate(text)

    # plot the WordCloud image
    plt.figure(figsize=(8, 8), facecolor=None)
    plt.imshow(wordcloud, interpolation="bilinear")
    plt.axis("off")
    plt.tight_layout(pad=0)

    plt.show()


def main():
    parser = argparse.ArgumentParser(
        description='Choose what tweets you want visualized, by which sentiment, and how')
    parser.add_argument('-t', '--type', nargs=1, choices=['positive', 'negative', 'neutral', 'all'],
                        default='all', help="What kind of tweets: positive, neutral, negative or all")
    parser.add_argument('-v', '--visualization', nargs=1, choices=[
                        'wordcloud', 'correlation_circle'], default='wordcloud', help="Type of visualization: wordcloud")
    parser.add_argument('-q', '--query', nargs=1,
                        help="Tweet query. Default is the first trending topic in New York")
    parser.add_argument('-n', '--number', nargs=1, type=int, default=10,
                        help="Number of total tweets retrieved. If a sentiment is specified, the number of tweets analyzed is less than the total number")
    args = parser.parse_args()

    api = sentiment_analysis.TwitterClient()
    toSearch = ""
    if "query" in args and args.query != None:
        toSearch = args.query
    tweets = api.get_tweets(toSearch, args.number)
    if args.type == "all":
        tweets_text = [tweet["text"] for tweet in tweets]
    else:
        tweets_text = [tweet["text"]
                       for tweet in tweets if tweet["sentiment"] == args.type[0]]
    tweets_text = [re.sub(r"https?:\/\/.*[\r\n]*", "", t,
                          flags=re.MULTILINE) for t in tweets_text]
    tweets_text = [re.sub(r"RT", "", t, flags=re.MULTILINE)
                   for t in tweets_text]
    
    if args.visualization[0] == "wordcloud":
        plot_wordcloud(tweets_text, args.type[0])
    elif args.visualization[0] == "correlation_circle":
        tweets_tokenized = [tokenize(tweet) for tweet in tweets_text]
        print(tweets_tokenized)

if __name__ == "__main__":
    # calling main function
    main()
