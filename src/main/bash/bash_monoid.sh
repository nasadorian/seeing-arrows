#!/bin/bash

#
# Simple equality checker for monoid laws
# args: $1 name of test, $2 name of law, $4 left term, $5 right term
#
function equivTest() {
  if [ -z $(diff <(echo $3) <(echo $4)) ]
  then
    echo $1 passes $2
  else
    echo $1 fails $2
  fi
}



#
# `cat` forms a monoid under string addition
#

echo -n LEFT > a
echo -n MIDDLE > b
echo -n RIGHT > c
echo -n "" > id

# Associativity
LEFT=$(cat a b <(cat c))
RIGHT=$(cat a <(cat b c))

equivTest "cat" "Associativity" $LEFT $RIGHT

# Identity
LEFT=$(cat id a)
RIGHT=$(cat a id)

equivTest "cat" "Identity" $LEFT $RIGHT


#
# `pipe` forms a monoid under endomorphic composition, 
# meaning it associativitely composes functions of the same type
#

# We define identity function in terms of stdin and stdout
function identity() {
  read IN
  echo $IN
}

# Some data to parse
echo -n BIGLONGSTRINGOF5TEXTTOGREP > text

# Associativity
LEFT=$(cat text | grep -o "\d" | wc -l)
RIGHT=$(cat <(cat text | grep -o '\d') | wc -l)
equivTest "pipe" "Associativity" $LEFT $RIGHT

# Identity
LEFT=$(cat text | identity | grep -o "\d")
RIGHT=$(cat text | grep -o "\d" | identity)
equivTest "pipe" "Identity" $LEFT $RIGHT 
