#!/bin/bash

cd `dirname $0`
cd ..
BASE_DIR=`pwd`

TARGET_DIR=$BASE_DIR/target
ES_VERSION=`cat $BASE_DIR/pom.xml | xmllint --format - | sed -e "s/<project [^>]*>/<project>/" | xmllint --xpath "/project/properties/elasticsearch.version/text()" -`
ES_DIR=$TARGET_DIR/elasticsearch-$ES_VERSION
KUROMOJI_DIR=$BASE_DIR/src/main/java/org/codelibs/elasticsearch/extension/kuromoji

mkdir -p $TARGET_DIR
rm -rf $ES_DIR $KUROMOJI_DIR
mkdir -p $KUROMOJI_DIR
cd $TARGET_DIR

# Download source zip
if [ ! -f v${ES_VERSION}.zip ] ; then
  wget https://github.com/elastic/elasticsearch/archive/v${ES_VERSION}.zip
fi
if [ ! -f v${ES_VERSION}.zip ] ; then
  echo "Failed to download v${ES_VERSION}.zip."
  exit 1
fi
unzip -n v${ES_VERSION}.zip

cp -r $ES_DIR/plugins/analysis-kuromoji/src/main/java/org/elasticsearch/* $KUROMOJI_DIR

perl -pi -e "s/package org.elasticsearch.index.analysis;/package org.codelibs.elasticsearch.extension.kuromoji.index.analysis;/g" `find $KUROMOJI_DIR -type f`
perl -pi -e "s/package org.elasticsearch.plugin.analysis.kuromoji;/package org.codelibs.elasticsearch.extension.kuromoji.plugin.analysis.kuromoji;/g" `find $KUROMOJI_DIR -type f`
perl -pi -e "s/org.elasticsearch.index.analysis.Kuromoji/org.codelibs.elasticsearch.extension.kuromoji.index.analysis.Kuromoji/g" `find $KUROMOJI_DIR -type f`
perl -pi -e "s/org.elasticsearch.index.analysis.Japanese/org.codelibs.elasticsearch.extension.kuromoji.index.analysis.Japanese/g" `find $KUROMOJI_DIR -type f`

