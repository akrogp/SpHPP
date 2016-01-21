#!/bin/bash

JAR=EhuBio.jar
OPTS="-Xmx10g -Djava.util.logging.config.file=logging.properties -Djava.awt.headless=true"

if [ $# -eq 0 ]; then
	echo "Please, select one module:"
	for MOD in `jar tf "$JAR" org/sphpp/workflow/module | grep class | grep -v WorkflowModule | grep -v '\\$' | rev | cut -d'/' -f -1 | rev | sed 's/.class//g' | sort`; do
		echo -e "- $MOD: `$0 $MOD ? 2>&1`"
	done
else
	java -cp "$JAR" $OPTS org.sphpp.workflow.module.$@
fi
