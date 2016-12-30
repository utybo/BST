1:&
brm_load:

bdf_apply:bdf
call:2

set:a,0000
set:b,0000
set:name,bdf
bdf_apply:!name
call:2

bdf_apply:bdf,u_
assert:equ:u_a,10
assert:equ:u_b,Hello World!


2:&
assert:equ:a,10
assert:equ:b,Hello World!
