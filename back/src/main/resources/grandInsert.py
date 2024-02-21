f = open("vocabulary.csv", "r")
lines = f.readlines()

f2 = open("req.txt", "w")

lis = []

for line in lines:
    parts = line.strip().replace(',', '').split(' ')
    level = parts[0]
    i = len(parts) - 1
    pos = []
    while parts[i] == 'n.' or parts[i] == 'v.' or parts[i] == 'adj.' or parts[i] == 'adv.':
        if parts[i] == 'n.':
            pos.append("NOUN")
        if parts[i] == 'v.':
            pos.append("VERB")
        if parts[i] == 'adj.':
            pos.append("ADJECTIVE")
        if parts[i] == 'adv.':
            pos.append("ADVERB")
        i = i - 1
    word = ''
    for j in range(1, i+1):
        word = word + parts[j]
        if j < i:
            word = word + ' '
    
    for posc in pos:
       found = False 
       for l in lis:
           if l[0] == word and l[1] == posc:
               found = True
               break
       if found:
           continue
       l = [word, posc, level.capitalize()]
       lis.append(l)
       
for l in lis:
    f2.write("('" + l[0] + "', '" + l[1] + "', '" + l[2] + "'),\n")
    
f.close()
f2.close()
    
    
    
    
    
    
    
    
    
    
    
    
    
    