import nltk
from nltk.corpus import wordnet, semcor
from nltk import tree
import string
import re
import random

# signature should already be a set
def computeOverlap(signature, context):
  return len(set(context).intersection(signature))

# delete words which are not interesting for the context
def removeNoise(bagOfWords):
  return [word 
          for word in bagOfWords
          if (len(wordnet.synsets(word)) > 0) and (wordnet.synsets(word)[0].pos() in [wordnet.ADJ, wordnet.VERB, wordnet.NOUN, wordnet.ADV])]

def simplifiedLesk(word, sentence):
  bestSense = wordnet.synsets(word)[0]
  maxOverlap = 0
  context = sentence.translate(sentence.maketrans('', '', string.punctuation)).split()
  for sense in wordnet.synsets(word):
    gloss = sense.definition()
    examples = sense.examples()
    signature = re.findall(r'\w+', gloss)
    for example in examples:
      signature = signature + re.findall(r'\w+', example)
    signature = list(set(signature))
    signature = removeNoise(signature)
    overlap = computeOverlap(signature, context)
    if overlap > maxOverlap:
      maxOverlap = overlap
      bestSense = sense
  return bestSense

# sentences.txt
outputFile = open("results.txt", "w")
myFile = open("./sentences.txt")
outputFile.write("sentences.txt\n\n")
for sentence in myFile:
  if sentence[0] == "-":
    targetWord = re.search('\\*\\*(.+?)\\*\\*', sentence).group(1)
    targetWord = targetWord.translate(targetWord.maketrans('', '', '*'))
    sense = simplifiedLesk(targetWord, sentence)
    sentenceHalves = sentence.split("**"+targetWord+"**")
    print(sentenceHalves[0], end= " ")
    outputFile.write(sentenceHalves[0] + " ")
    for l in sense.lemmas():
      print(l.name(), end="/")
      outputFile.write(l.name() + "/")
    print(" " + sentenceHalves[1] + "\t" + str(sense))
    outputFile.write(" " + sentenceHalves[1] + "\t" + str(sense) + "\n")
myFile.close()
# semcor
outputFile.write("\nsemcor\n\n")
totalSentences = 50
indeces = random.sample(range(0, 600), totalSentences)
guessedSenses = 0
for i in indeces:
  sentence = semcor.sents()[i]
  sent_sem = semcor.tagged_sents(tag="sem")[i]
  lemmas = [word.label() for word in sent_sem if isinstance(word, tree.Tree)]  # annotated WordNet lemmas (= synset.hw)
  lemmas = [
      l for l in lemmas if isinstance(l, nltk.corpus.reader.wordnet.Lemma)
  ]  # skip entries where sense isn't a Lemma object
  indeces = list(range(len(sentence)-1))
  random.shuffle(indeces)
  for r in indeces:
    word = sentence[r].strip()
    if word.isalpha() and len(word) > 3 and len(wordnet.synsets(word)) > 0: # naive check to avoid punctuation and most function words (articles, auxiliaries, etc)
      break
  word = word.lower()
  sense = simplifiedLesk(word, " ".join(sentence))
  outputFile.write(" ".join(sentence) + "\nword: " + word + "\tsense: " + str(sense))
  for s in sense.lemmas():
    if s in lemmas:
      guessedSenses += 1
      outputFile.write("\tOK")
  outputFile.write("\n")
accuracy = guessedSenses / totalSentences
print("acc: " + str(accuracy) + " guessed: " + str(guessedSenses))
outputFile.write("\nacc: " + str(accuracy) + " guessed: " + str(guessedSenses))
outputFile.close()