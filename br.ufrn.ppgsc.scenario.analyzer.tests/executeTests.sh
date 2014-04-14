#!/bin/bash

# -------------------------------------------------------- #
# ----- EXECUTAR ESTE ARQUIVO NO PROJETO ARGOUML-APP ----- #
# -------------------------------------------------------- #


# VARIABLES
PREFIX="build/tests/classes"
COMPLETEPREFIX="build/tests/classes/"
DOT='.'
SLASH='/'
EMPTY=''
EMPTYSPACE=" "
NEWLINE='\n'

CLASSNAMES=$(find $PREFIX -name '*.class')

# REPLACING...
CLASSNAMES=${CLASSNAMES//$COMPLETEPREFIX/$EMPTY}
CLASSNAMES=${CLASSNAMES//$SLASH/$DOT}
#CLASSNAMES=CLASSNAMES | tr ' ' '\n'



for i in $CLASSNAMES
do
	echo $i
done