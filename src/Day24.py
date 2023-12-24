import re
import z3

with open("inputs/Day24-input.txt") as file:
    h = [list(map(int, re.findall("-?\\d+", line))) for line in file]

xi, yi, zi, dxi, dyi, dzi = z3.Ints("xi yi zi dxi dyi dzi")
ts = [z3.Int("t" + str(i)) for i in range(len(h))]

s = z3.Solver()
for i, (x1, y1, z1, dx, dy, dz) in enumerate(h):
    s.add(x1 + dx * ts[i] == xi + dxi * ts[i])
    s.add(y1 + dy * ts[i] == yi + dyi * ts[i])
    s.add(z1 + dz * ts[i] == zi + dzi * ts[i])
s.check()
print(s.model().evaluate(xi + yi + zi))
