CSC488 group 1
==============

IRC: freenode #csc488_adfpv

| Account  | Name              | Email                                         |
|----------|-------------------|-----------------------------------------------|
| g3chowdh | Badrul Chowdhury  | badrul.chowdhury@mail.utoronto.ca             |
| g3av     | Anthony Vandikas  | anthony.vandikas@mail.utoronto.ca             |
| g3luding | Felix Lu          | dingyi.lu@mail.utoronto.ca                    |
| g3vishra | Vishrant Vasavada | v.vasavada@mail.utoronto.ca                   |
| g4hysw   | Peter Chen        | tianze.chen@mail.utoronto.ca / peter@hysw.org |

Usage
=====

    $ ant dist # Do this every time you modify the compiler
    $ ./RUNCOMPILER.sh <file_name>
    $ ./RUNALLTESTS.sh

Note
====

- (peter) I just finished the jcup file, not sure if there are any mistake.
- (anthony) I retrofitted the A2 assignment code into the repository
- (vishrant) I haven't added following tests in array fail tests because I guess we should make it general
        - Using statement before declaration
        - Assignment operation without anything to assign to (x <= )
    Let me know if there are any more array declaration tests you have in mind.
- (felix) I think negation should be able to associate like !!!, in the current parser it doess not support that
I also used Peter's program to generate some programs you guys should check out the errors to debug the parser.
