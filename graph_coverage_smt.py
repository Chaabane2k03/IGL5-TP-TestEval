# pip install z3-solver
from z3 import Int, IntVal, Bool, And, Not, If, Solver, sat, is_true, simplify

MAX_LEN = 3

# ---- Symbolic inputs ----
is_null = Bool("is_null")
length  = Int("length")
x       = [Int(f"x{i}") for i in range(MAX_LEN)]

# ---- Symbolic result of the method (loop unrolled) ----
count = IntVal(0)
for i in range(MAX_LEN):
    count = If(And(i < length, x[i] == 0), count + 1, count)

# ---- Build the path condition from a list of CFG nodes ----
def path_condition(path):
    conds = []
    i = 0                          
    for a, b in zip(path, path[1:]):
        if   (a, b) == (1, 2): conds.append(is_null)
        elif (a, b) == (1, 3): conds.append(Not(is_null))
        elif (a, b) == (4, 5): conds.append(i < length)         # enter loop
        elif (a, b) == (4, 8): conds.append(Not(i < length))    # exit loop
        elif (a, b) == (5, 6): conds.append(x[i] == 0)
        elif (a, b) == (5, 7): conds.append(x[i] != 0)
        elif (a, b) == (7, 4): i += 1                           # i++
    return conds

# ---- Test requirements ----
requirements = {
    "TR1": [1, 2],
    "TR2": [1, 3, 4, 5, 6, 7, 4, 8],
}

# ---- Solve each requirement ----
for name, path in requirements.items():
    s = Solver()
    s.add(length >= 0, length <= MAX_LEN)   # well-formed input
    s.add(*path_condition(path))

    print(f"{name}: path = {' -> '.join(map(str, path))}")
    print("   path condition:", simplify(And(*path_condition(path))))

    if s.check() == sat:
        m = s.model()
        if is_true(m.eval(is_null, model_completion=True)):
            print("   input    = null")
            print("   expected = NullPointerException\n")
        else:
            n   = m.eval(length, model_completion=True).as_long()
            arr = [m.eval(x[k], model_completion=True).as_long() for k in range(n)]
            exp = m.eval(count, model_completion=True)
            print(f"   input    = {arr}")
            print(f"   expected = {exp}\n")
    else:
        print("   UNSAT -> infeasible path\n")