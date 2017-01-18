#!/bin/bash
#Uses verbmaps to get the future proche stem for the verb
curl verbmaps.com/en/verb/fr/$1 | perl -pe 's/\s//gs' | perl -pe 's/.*>([^><]+)<\/span><\/div><divclass="transform">.*>Addfuture.*/$1\n/gi' > output.txt
echo "================================================================================"
cat output.txt | perl -pe 's/.{20,}/Error 404\n/g'
