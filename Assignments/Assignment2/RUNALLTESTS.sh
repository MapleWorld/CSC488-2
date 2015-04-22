# Author: Felix Lu (with a bit of modification by Anthony Vandikas)
#! /bin/sh
#  Location of directory containing  dist/compiler488.jar
WHERE=.

#Checks all of the passing and failing tests
echo --------------------------------------------
echo Checking passing files
echo --------------------------------------------
for file in $(find ./tests/passing -name '*.488');
do
    echo $file ::
    java -jar $WHERE/dist/compiler488.jar $file
done

echo
echo --------------------------------------------
echo Checking failing files
echo --------------------------------------------
for file in $(find ./tests/failing -name '*.488');
do
    echo $file ::
    java -jar $WHERE/dist/compiler488.jar $file
    echo
done
exit 0
