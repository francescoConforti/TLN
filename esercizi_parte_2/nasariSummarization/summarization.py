import argparse
import nltk.corpus
import string
from operator import itemgetter

class nasariObj:
  babel = ""
  wiki = ""
  wn = []

  def __init__(self, babelSynset, wikiPage, wordnetCorrelations):
    self.babel = babelSynset
    self.wiki = wikiPage.lower()
    self.wn = wordnetCorrelations

  def print(self):
    print("babel: " + self.babel)
    print("wiki: " + self.wiki)
    print("wordnet: " + str(self.wn))
    print("")

nasari = []

def readNasari():
  nasariData = []
  with open("dd-small-nasari-15.txt") as nasariFile:
    for line in nasariFile:
      if len(line) > 0:
        split = line.strip().split(";")
        wordnetCorrelations = []
        for s in split[2:]:
          wn = s.split("_")
          if len(wn) > 1:
            wordnetCorrelations.append((wn[0], wn[1]))
        nasariData.append(nasariObj(split[0], split[1], wordnetCorrelations))
  return nasariData

def readText(path):
  with open(path) as f:
    lines = f.readlines()
  return [l.strip() for l in lines if not l.strip() == "" and not l[0] == "#"]

def removeStopwords(words):
  stopwords = nltk.corpus.stopwords.words("english")
  return [w.translate(str.maketrans('','',string.punctuation)).lower() for w in words if not w.translate(str.maketrans('','',string.punctuation)) in stopwords]

# v1, v2 are lists of (synset, score)
def weightedOverlap(v1, v2):
  intersection = []
  for sense1, score1 in v1:
    for sense2, score2 in v2:
      if sense1 == sense2:
        intersection.append((sense1, score1, score2))
  res = 0
  if len(intersection) == 0:
    res = 0.01
  else:
    num = 0
    den = 0
    for i, (_sense, score1, score2) in enumerate(intersection):
      num += (float(score1) + float(score2))**(-1)
      den += (2*(i+1))**(-1)
    res = num / den
  return res

def similarity(word1, word2):
  synsets1 = [(obj.babel, obj.wn) for obj in nasari if obj.wiki == word1]
  synsets2 = [(obj.babel, obj.wn) for obj in nasari if obj.wiki == word2]
  maxOverlap = 0
  for _b1, s1 in synsets1:
    for _b2, s2 in synsets2:
      overlap = weightedOverlap(s1, s2)
      if overlap > maxOverlap:
        maxOverlap = overlap
  return maxOverlap

# returns a list of tuples (sentenceNumber, score)
def getScores(context, sentences):
  splittedSentences = [removeStopwords(sentence.strip().split()) for sentence in sentences]
  scores = []
  # compute the total score for each sentence, normalized by length
  for sentenceNumber, line in enumerate(splittedSentences):
    score = 0
    # for each word in the sentence compute the best similarity with the objects in the context
    for word in line:
      maxSimilarity = 0
      similarityScore = 0
      for c in context:
        similarityScore = similarity(c, word)
        if similarityScore > maxSimilarity:
          maxSimilarity = similarityScore
      score += maxSimilarity
    scores.append((sentenceNumber, score / len(line)))
  return scores

def summarize(sentences, scores, numDeletions):
  scores.sort(key=itemgetter(1))
  toDelete = [index for index, _score in scores[:numDeletions]]
  return [s for i, s in enumerate(sentences) if i not in toDelete]

def main():
  global nasari
  parser = argparse.ArgumentParser()
  parser.add_argument("compression", help="compression rate in [0-100]%", type=int)
  parser.add_argument("path", help="path to file to summarize")
  args = parser.parse_args()
  if args.compression < 0 or args.compression > 100:
    print("invalid compression value")
    exit(-1)
  nasari = readNasari()
  textLines = readText(args.path)
  titleWords = removeStopwords(textLines[0].strip().split())
  context = list(set([obj.wiki for obj in nasari if obj.wiki in titleWords or obj.wiki + "s" in titleWords]))
  numDeletions = int(len(textLines) * (args.compression / 100))
  scores = getScores(context, textLines)
  print("scores: ")
  print(scores)
  abridged = summarize(textLines, scores, numDeletions)
  print("\nresult:")
  print(abridged)
  f = open("abridged" + "_".join(textLines[0].split()) + ".txt", "w")
  for line in abridged:
    f.write(line + "\n\n")
  f.close()

if __name__ == "__main__":
   main()