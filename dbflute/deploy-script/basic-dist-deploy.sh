cd ..
ant -f build.xml dist
ant -f build.xml reflect-to-basic

cd ../dbflute-basic-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-basic-example
ant