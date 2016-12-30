1:&
set:a,10
set:b,10
add:c,a,b
assert:equ:c,20
sub:c,a,b
assert:equ:c,0
mul:c,a,b
assert:equ:c,100
div:c,a,b
assert:equ:c,1
:2

2:&
mod:a,9,3
assert:equ:a,0

add:x,zeze,zaza
assert:equ:x,0
:3

3:&
set:a,3
set:b,4
add:a,b
assert:equ:a,7
