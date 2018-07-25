import sys
s = sys.stdin.readline().strip()
while s :
    try:
        x=None
        try:
            x = eval(s)
        except(Exception, ArithmeticError):
            exec(s)
            x=None
        if not (x is None): 
            sys.stdout.write(str(x)+"\n")
        else:
            sys.stdout.write("\n") 
    except Exception as e:
	    sys.stdout.write(str(e)+"\n")
    sys.stdout.flush()
    s = sys.stdin.readline().strip()