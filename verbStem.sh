#!/bin/bash
#This is a sript that gets the verb stem for the below stated 4 languages for several tenses. It fetches the data from verbmaps.com
#Multi-lingual support (French, Spanish, Italian, Portugese)
#To run it:
#$ sh verbStem.sh verb tense language
#For example:
#$ sh verbStem.sh avoir future french
#ir-

#Replacing language names with their 2 characters
language=$(echo $3 | perl -pe "s/spanish/es/gi" | perl -pe "s/french/fr/gi" | perl -pe "s/portugese/pt/gi" | perl -pe "s/italian/it/gi")

#Fetching the output and doing replacement on it to get the stem
curl -s verbmaps.com/en/verb/$language/$1 | perl -pe 's/\s//gs' | perl -pe 's/.*>([^><]+)<\/span><\/div><divclass="transform">.*>Add'+$2+'.*/$1\n/gi' > output.txt

#If the output is longer than 20 characters, we remove it since it returned nothing. Also add a "-" at the end of output
cat output.txt | perl -pe 's/$/-/' | perl -pe 's/.{20,}/Error 404\n/g'
