# Author: Felix Lu (with a bit of modification by Anthony Vandikas)
#! /bin/sh
#  Location of directory containing  dist/compiler488.jar
WHERE=.
STDERR_DIR=RUNALLTESTS_ERRS

if ! ant compiler488 >/dev/null ; then echo "compile fail" ; exit 1; fi
if ! ant dist >/dev/null        ; then echo "dist fail"    ; exit 1; fi

exec_test () {
    file=$1
    ERRFILE=$STDERR_DIR/$file.stderr
    mkdir -p $(dirname "${ERRFILE}")
    echo -n "$file" ::
    #  --semantic-only
    java -jar $WHERE/dist/compiler488.jar $file 2>$ERRFILE
    if [ -s $ERRFILE ]
    then
        if grep Exception $ERRFILE >/dev/null ; then
            printf " \e[1;34mException detected\e[0m\n"
            printf "\e[1;33m"
            cat $ERRFILE
            printf "\e[0m"
        elif grep Syntax $ERRFILE >/dev/null ; then
            printf ' \e[1;31mfail\e[1;34m(Syntax Error)\e[0m\n'
            printf "\e[1;33m"
            head -n 2 $ERRFILE
            echo  "... ($(($(wc -l <$ERRFILE)-2)) lines)"
            printf "\e[0m"
        else
            printf ' \e[1;31mfail\e[0m\n'
            head -n 1 $ERRFILE
            echo  "... ($(($(wc -l <$ERRFILE)-1)) lines)"
        fi
    else
        printf ' \e[1;32mpass\e[0m\n'
        rm $ERRFILE
    fi
}

PASSING_CASES="$(find ./tests/passing -name '*.488' | sort -V)"
FAILING_CASES="$(find ./tests/failing -name '*.488' | sort -V)"

PASSING_CASES="$(find ./tests/A4samples -name '*.488' | sort -V)"
FAILING_CASES="$(find ./tests/A4samples -name '*.488x' | sort -V)"

#Checks all of the passing and failing tests
echo --------------------------------------------
echo Checking passing files
echo --------------------------------------------
for file in $PASSING_CASES; do exec_test "$file"; done

echo
echo --------------------------------------------
echo Checking failing files
echo --------------------------------------------
for file in $FAILING_CASES; do exec_test "$file"; done

exit 0