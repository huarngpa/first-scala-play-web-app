#!/bin/bash

echo "Initializing database..."
psql -U postgres --command "CREATE USER scalauser WITH SUPERUSER PAS\
SWORD 'scalapass';"
createdb -U postgres -O scalauser scaladb
exit
