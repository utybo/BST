1:&
jse_eval:a,1+Math.sqrt(9)*4+Math.abs(-16)
jse_eval:b,a+14
assert:equ:a,29
assert:equ:b,43

set:a,1
set:b,0
set:c,1

assert:jse_eval:a & c
assert:jse_eval:a | b
assert:jse_eval:!Boolean(b)
assert:jse_eval:Boolean(a) && !Boolean(b)

set:a,Stringstring
jse_eval:a,a + "string"
assert:equ:a,Stringstringstring
