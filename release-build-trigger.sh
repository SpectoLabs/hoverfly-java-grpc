#!/usr/bin/env bash

set -e

if [ -z $1 ]
then
  echo "Missing RELEASE_VERSION" && exit 1
fi

if [ -z $2 ]
then
  echo "Missing NEXT_DEV_VERSION" && exit 1
fi

trigger_build_url=https://circleci.com/api/v1.1/project/github/Spectolabs/hoverfly-java-grpc/tree/master?circle-token=${CIRCLE_TOKEN}

post_data=$(cat <<EOF
{
  "build_parameters": {
    "IS_RELEASE": true,
    "RELEASE_VERSION": "$1",
    "NEXT_DEV_VERSION": "$2",
    "CIRCLE_JOB": "deploy"
  }
}
EOF)

echo ${post_data}

curl \
--header "Accept: application/json" \
--header "Content-Type: application/json" \
--data "${post_data}" \
--request POST ${trigger_build_url} \
-v --fail
