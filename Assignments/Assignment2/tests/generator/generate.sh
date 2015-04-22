#! /bin/sh
# generate valid programs
for i in `seq 1 100`;
do
    python3 ./generator.py > "program$i.488"
done    
exit 0
