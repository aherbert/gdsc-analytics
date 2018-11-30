#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" ]]; then
    mvn deploy -P snapshot-release --settings $DEPLOY_DIR/settings.xml -DskipTests=true
    # Ignore the exit code. The snapshot-release profile will fail if the version is not
    # a SNAPSHOT.
    #exit $?
fi
