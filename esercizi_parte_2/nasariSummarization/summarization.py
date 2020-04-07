import argparse

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

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument("compression", help="compression rate in [0-100]%", type=int)
  parser.add_argument("path", help="path to file to summarize")
  args = parser.parse_args()
  if args.compression < 0 or args.compression > 100:
    print("invalid compression value")
    exit(-1)
  nasari = readNasari()
  lines = readText(args.path)
  print(lines)
  


if __name__ == "__main__":
   main()