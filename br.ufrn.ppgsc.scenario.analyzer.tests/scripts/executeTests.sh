#!/bin/bash

# -------------------------------------------------------- #
# ----- EXECUTAR ESTE ARQUIVO NO PROJETO ARGOUML-APP ----- #
# -------------------------------------------------------- #


# VARIABLES
ARGOUMLPREFIX="/Users/leosilva/Documents/Estudo/Mestrado/ArgoUMLSource/VERSION_0_34/src"
ARGOUMLTOOLSPREFIX="/../tools"
SCENARIOANALYZERPREFIX="/Users/leosilva/Documents/Estudo/Mestrado/projects/scenario-analyzer"
COMMAND="java -cp build-eclipse-tests:build-eclipse:lib/*:"$ARGOUMLPREFIX"/argouml-core-model/build-eclipse:"$ARGOUMLPREFIX"/argouml-core-model-mdr/build-eclipse:"$ARGOUMLPREFIX"/argouml-core-model-mdr/lib/*:"$ARGOUMLPREFIX$ARGOUMLTOOLSPREFIX"/junit-3.8.2/junit.jar:"$ARGOUMLPREFIX$ARGOUMLTOOLSPREFIX"/lib/easymock12.jar:"$ARGOUMLPREFIX$ARGOUMLTOOLSPREFIX"/apache-ant-1.7.0/lib/ant.jar:"$ARGOUMLPREFIX$ARGOUMLTOOLSPREFIX"/jdepend-2.9/lib/jdepend-2.9.jar:"$ARGOUMLPREFIX"/argouml-core-infra/lib/log4j-1.2.6.jar:"$SCENARIOANALYZERPREFIX"/br.ufrn.ppgsc.scenario.analyzer.common/bin:"$SCENARIOANALYZERPREFIX"/br.ufrn.ppgsc.scenario.analyzer.cdynamic/bin:"$SCENARIOANALYZERPREFIX"/br.ufrn.ppgsc.scenario.analyzer.libs/libs/javax.faces-2.2.6.jar:"$SCENARIOANALYZERPREFIX"/br.ufrn.ppgsc.scenario.analyzer.libs/libs/postgresql-9.3-1101.jdbc41.jar:"$SCENARIOANALYZERPREFIX"/br.ufrn.ppgsc.scenario.analyzer.libs/libs/hibernate_3.6.10/*:"$SCENARIOANALYZERPREFIX"/br.ufrn.ppgsc.scenario.analyzer.libs/libs_client/aspectjrt_1.7.3.jar junit.textui.TestRunner "
PREFIX="build/tests/classes"
COMPLETEPREFIX="build/tests/classes/"
DOT='.'
SLASH='/'
EMPTY=''
EMPTYSPACE=" "
NEWLINE='/n'
PATTERN='$'
DOTCLASS=".class"

CLASSNAMES=$(find $PREFIX -name '*.class')

# REPLACING...
CLASSNAMES=${CLASSNAMES//$COMPLETEPREFIX/$EMPTY}
CLASSNAMES=${CLASSNAMES//$SLASH/$DOT}
#CLASSNAMES=CLASSNAMES | tr ' ' '/n'



string='My string':

for i in $CLASSNAMES
do
	if [[ $i != */$* ]]
	then
		echo "Running test for " $i " class..."
	  	$COMMAND ${i//$DOTCLASS/$EMPTY}
	fi
done