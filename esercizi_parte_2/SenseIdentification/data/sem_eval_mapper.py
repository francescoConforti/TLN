import sys
#
#
#
def letter_to_int(letter):
    alphabet = list('abcdefghijklmnopqrstuvwxyz \'0123456789')
    return alphabet.index(letter.lower()) + 1
#
# 
def map_2_hundred(accumulator):
    if accumulator < 1:
        print('annotazione coppie 1-100 del file it.test.data.txt')
    elif accumulator < 2:
        print('annotazione coppie 101-200 del file it.test.data.txt')
    elif accumulator < 3:
        print('annotazione coppie 201-300 del file it.test.data.txt')
    elif accumulator < 4:
        print('annotazione coppie 301-400 del file it.test.data.txt')
    else: # 4
        print('annotazione coppie 401-500 del file it.test.data.txt')
#
# 
# 
# ================================================================
#  Mappa un cognome su uno dei 5 insiemi di coppie da annotare
# ================================================================

if len(sys.argv) < 2:
    input_name = input("Inserimento cognome (senza spazi): ")
elif len(sys.argv) == 2:
    input_name = sys.argv[1]
else:
    print('\n\n\nUSAGE to run the program\n', \
      '\npython script_name your_surname\npython script_name\n\n')
    sys.exit(1)

accumulator = 0
for i, c in enumerate(input_name):
    accumulator += letter_to_int(c)

map_2_hundred(accumulator % 5)
