from nltk.corpus import wordnet
import string
import re

def simplifiedLesk(word, sentence):
  bestSense = wordnet.synsets(word)[0]
  maxOverlap = 0
  context = sentence.translate(sentence.maketrans('', '', string.punctuation)).split()
  print(bestSense)

# sentences.txt
myFile = open("./sentences.txt")
for sentence in myFile:
  if sentence[0] == "-":
    targetWord = re.search('\\*\\*(.+?)\\*\\*', sentence).group(1)
    targetWord = targetWord.translate(targetWord.maketrans('', '', '*'))
    simplifiedLesk(targetWord, sentence)
myFile.close()