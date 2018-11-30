#!/bin/bash

# Simple check for a SNAPSHOT version
grep '<version>.*SNAPSHOT</version>' $DEPLOY_DIR/../pom.xml -q

# Run if a SNAPSHOT and not a pull request or tagged
if [[ $? == 0 && $TRAVIS_PULL_REQUEST == "false" && $TRAVIS_TAG == "" ]]; then
    mvn deploy -P snapshot-release --settings $DEPLOY_DIR/settings.xml -DskipTests=true
    # The snapshot-release profile will fail if the version is not a SNAPSHOT.
    # It will also fail if the release failed.
    exit $?
fi
