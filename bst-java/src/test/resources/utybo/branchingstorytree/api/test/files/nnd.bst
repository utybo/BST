# NND Testing

1:&
set:a,2
:a

2:&
set:a,4
:3,fail[equ:a,4]

3:&
:aliastestfirst

4:&
::alias=aliastestfirst
set:b,aliastestsecond
:b

5:&
::alias=aliastestsecond

1000:&
::alias=fail
fail:Manual fail