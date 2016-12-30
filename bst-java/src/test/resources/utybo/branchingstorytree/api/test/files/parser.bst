# A file that contains every possible occurence for the parser
# Should never fail at parsing time.
# Undefined behavior at execution time.


aTag=aValue

1:Example

of

some
node
::color=incredible
:GOTO|2,3[equ:a,c]
:GONOPE|3|incr:a
:GOYUP|3|equ:a,d
:GOFORWARD FOREVER|4|{incr:b}[equ:a,1]

2:>Wheee!

3:&

incr:a
[equ:a,1]?{add:a,2}:{add:b,4}
:56,-1[equ:a,3]
