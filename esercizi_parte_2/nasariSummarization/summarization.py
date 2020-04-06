class nasariObj:
  babel = ""
  wiki = ""
  wn = []

  def __init__(self, babelSynset, wikiPage, wordnetCorrelations):
    self.babel = babelSynset
    self.wiki = wikiPage
    self.wn = wordnetCorrelations

  def print(self):
    print("babel: " + self.babel)
    print("wiki: " + self.wiki)
    print("wordnet: " + str(self.wn))
    print("")

# read nasari contents
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