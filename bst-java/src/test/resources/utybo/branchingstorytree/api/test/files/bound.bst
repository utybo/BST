1:&
set:a,4
set:b,11
bound:a,5,10
bound:b,5,10
assert:equ:a,5
assert:equ:b,10

set:a,4
set:b,11
set:min,5
set:max,10
bound:a,min,max
bound:b,min,max
assert:equ:a,5
assert:equ:b,10
