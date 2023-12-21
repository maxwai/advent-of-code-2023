from collections import deque
import numpy as np
import sys

# polynomial extrapolation
a0 = int(sys.argv[1])
a1 = int(sys.argv[2])
a2 = int(sys.argv[3])
n = int(sys.argv[4])

vandermonde = np.matrix([[0, 0, 1], [1, 1, 1], [4, 2, 1]])
b = np.array([a0, a1, a2])
x = np.linalg.solve(vandermonde, b).astype(np.int64)

print(x[0] * n * n + x[1] * n + x[2])