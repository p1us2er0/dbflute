cd ..
ant -f build.xml reflect-to-oracle

cd ../../dbflute-example-database/dbflute-oracle-example/dbflute_exampledb
rm ./log/*.log
. nextschema-renewal.sh
. manage.sh renewal
. diffworld-test.sh
