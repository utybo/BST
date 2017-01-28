1:&
rand:a,1,10
assert:greaterequ:a,1
assert:lessequ:a,10

set:min,1
set:max,10
rand:a,min,max
assert:greaterequ:a,min
assert:lessequ:a,max

rand:a,15
assert:greaterequ:a,0
assert:lessequ:a,15

set:max,20
rand:a,max
assert:greaterequ:a,0
assert:lessequ:a,max
