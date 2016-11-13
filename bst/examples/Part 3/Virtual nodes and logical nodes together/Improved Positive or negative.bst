1:Press START to start
:START|10

10:The number ${a} is ${&2}

2:&
set:a,10
[less:a,0]?{set:b,1}:{set:b,2}
[equ:a,0]?{set:b,3}:
:3,-1[equ:b,1]
:4,5[equ:b,2]

3:>negative
4:>positive
5:>0
