#!/bin/bash

LB_VERSION='1.9.5'

# Install liquibase sources.
mvn install:install-file -DgroupId=org.liquibase \
	-DartifactId=liquibase-core \
	-Dversion=$LB_VERSION \
	-Dpackaging=jar \
	-Dfile=lib/liquibase-$LB_VERSION.jar

mvn install:install-file -DgroupId=org.liquibase \
	-DartifactId=liquibase-core \
	-Dversion=$LB_VERSION \
	-Dpackaging=java-source \
	-Dfile=lib/liquibase-$LB_VERSION-source.jar

