from nltk.corpus import wordnet
import string
import re

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
  #print("overlap: " + str(maxOverlap))
  return bestSense

# sentences.txt
myFile = open("./sentences.txt")
for sentence in myFile:
  if sentence[0] == "-":
    targetWord = re.search('\\*\\*(.+?)\\*\\*', sentence).group(1)
    targetWord = targetWord.translate(targetWord.maketrans('', '', '*'))
    sense = simplifiedLesk(targetWord, sentence)
    sentenceHalves = sentence.split("**"+targetWord+"**")
    print(sentenceHalves[0], end= " ")
    for l in sense.lemmas():
      print(l.name(), end="/")
    print(" " + sentenceHalves[1] + "\t" + str(sense))

myFile.close()