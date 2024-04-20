from sys import argv
import numpy as np
import matplotlib.pyplot as plt

plt.rcParams['image.cmap'] = 'binary'

def cellChangeState(rule, L, C, R):
    shift = (L << 2) | (C << 1) | R
    return (rule & (1 << shift)) >> shift

def nextIter(state, size, rule):
    newState = np.empty(size, dtype=int)
    newState[0] = cellChangeState(rule, 0, state[0], state[1])
    for i in range(1, size - 1):
        newState[i] = (cellChangeState(rule, state[i - 1], state[i], state[i + 1]))
    newState[size - 1] = (cellChangeState(rule, state[size - 2], state[size - 1], 0))
    return newState

n = int(argv[1])
size = 2 * n + 1
rule = int(argv[2])

seed = np.zeros(size, dtype=int)
seed[n] = 1

data = np.zeros((n + 1, size), dtype=int)
state = seed.copy()
for i in range(n + 1):
    data[i, :] = state.copy()
    state = nextIter(state, size, rule)

fig, ax = plt.subplots(figsize=(16, 9))
ax.matshow(data)
ax.axis(False)
plt.show()
