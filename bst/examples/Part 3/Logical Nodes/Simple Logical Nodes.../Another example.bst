1:Press an option to start
:a = 1|10|{set:a,1}
:a = 2|10|{set:a,2}
:a = 3|10|{set:a,3}

2:a was 1

3:a was 2

4:a was 3

10:&
:2,-1[equ:a,1]
:3,-1[equ:a,2]
:4,1[equ:a,3]

# We return to 1 if for some reason a isn’t 1, 2 or 3 (this shouldn’t happen normally)
