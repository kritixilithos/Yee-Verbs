#!/bin/bash
#This is a sript that gets the verb stem for the below stated 4 languages for several tenses. It fetches the data from verbmaps.com
#Multi-lingual support (French, Spanish, Italian, Portugese)
#To run it:
#$ sh verbStem.sh verb tense language
#For example:
#$ sh verbStem.sh avoir future french
#ir-

verb=$1
tense=$2
language=$3

if [ ! -f default.txt ]; then
  echo "1 arg=>none\n2 args=>none" > default.txt
fi

getDefaultOption() {
  cat default.txt | grep "$1" | perl -pe "s/$1 args?=>//g"
}

getVerb() {
  #Replacing language names with their 2 characters
  language=$(echo $3 | perl -pe "s/spanish/es/gi" | perl -pe "s/french/fr/gi" | perl -pe "s/portugese/pt/gi" | perl -pe "s/italian/it/gi")
  
  verb=$1
  tense=$2

  #Fetching the output and doing replacement on it to get the stem
  curl -s verbmaps.com/en/verb/$language/$verb | perl -pe 's/\s//gs' | perl -pe 's/.*>([^><]+)<\/span><\/div><divclass="transform">.*>Add'+$tense+'.*/$1\n/gi' > output.txt

  #If the output is longer than 20 characters, we remove it since it returned nothing. Also add a "-" at the end of output
  tput setaf 10
  cat output.txt | perl -pe 's/$/-/' | perl -pe 's/.{20,}/Error 404\n/g'
  tput setaf 15
}

usage() {
  cat << EOM

Usage:
    sh verbStem.sh <verb> <tense> <lanuage>     Outputs the stem of <verb>
                                                in the <tense> in <lanuage>
    
    sh verbStem.sh -h                           Outputs this help screen

    sh verbStem.sh -dv                          Edits the defaults using vim

    sh verbStem.sh -dn                          Edits the defaults using nano

    sh verbStem.sh <verb> <default1> [default2] Outputs the stem of <verb>
                                                in the <default1> tense in
						an optional [default2]
						language


EOM
}

if test "$#" -ne 3; then
  defaultOne=$(getDefaultOption 1)
  defaultTwo=$(getDefaultOption 2)
  if [ "$1" == "-h" ]; then
    usage
  elif [ "$1" == "-dv" ]; then
    vim default.txt
  elif [ "$1" == "-dn" ]; then
    nano default.txt
  elif [ "$defaultTwo" != "none" ]; then
    if [ "$#" == "2" ]; then
      getVerb $verb $tense $defaultTwo
    elif [ "$#" == "1" ] && [ "$defaultOne" != "none" ]; then
      getVerb $verb $defaultOne $defaultTwo
    ##else TODO
    fi
  else
    echo "Illegal number of parameters"
    usage
  fi
else
  getVerb $verb $tense $language
fi
