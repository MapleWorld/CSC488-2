#!/bin/sh

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

if [ -z "$2" ]; then
    type=good
else
    type=$2
fi

for i in `seq 1 $1`;
do
    swipl -q -t main -f $DIR/generator-$type.pro > "program$i.488"
done
exit 0
