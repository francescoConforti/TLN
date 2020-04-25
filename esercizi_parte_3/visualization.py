from wordcloud import WordCloud, STOPWORDS 
import matplotlib.pyplot as plt 
import pandas as pd
import argparse
import sentiment_analysis
  
# take a list of tweets and make a wordcloud of it
def plot_wordcloud(tweets, sentiment = "neutral"):
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

    wordcloud = WordCloud(width = 800, height = 800, 
                    background_color = bg, 
                    stopwords = stopwords,
                    colormap = cmap,
                    min_font_size = 10).generate(text) 
    
    # plot the WordCloud image                        
    plt.figure(figsize = (8, 8), facecolor = None) 
    plt.imshow(wordcloud, interpolation="bilinear") 
    plt.axis("off") 
    plt.tight_layout(pad = 0) 
    
    plt.show()

def main():
    parser = argparse.ArgumentParser(description='Choose what tweets you want visualized, by which sentiment, and how')
    parser.add_argument('-t', '--type', nargs=1, choices=['positive', 'negative', 'neutral', 'all'], default='all', help="What kind of tweets: positive, neutral, negative or all")
    parser.add_argument('-v', '--visualization', nargs=1, choices=['wordcloud'], default='wordcloud', help="Type of visualization: wordcloud")
    parser.add_argument('-q', '--query', nargs=1, help="Tweet query. Default is the first trending topic in New York")
    args = parser.parse_args()

    api = sentiment_analysis.TwitterClient()
    toSearch = ""
    if "query" in args and args.query != None:
        toSearch = args.query
    tweets = api.get_tweets(toSearch)
    if args.type == "all":
        tweets_text = [tweet["text"] for tweet in tweets]
    else:
        tweets_text = [tweet["text"] for tweet in tweets if tweet["sentiment"] == args.type[0]]
    plot_wordcloud(tweets_text, args.type[0])

if __name__ == "__main__":
    # calling main function
    main()