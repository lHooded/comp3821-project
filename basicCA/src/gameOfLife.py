from sys import argv
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import scipy.signal
from math import floor

plt.rcParams['image.cmap'] = 'binary'

def numLiveNeighbours(state, row, col):
    return (state[row - 1][col - 1] + state[row - 1][col] + state[row - 1][col + 1] + 
            state[row][col - 1] + state[row][col + 1] +
            state[row + 1][col - 1] + state[row + 1][col] + state[row + 1][col + 1])

def cellChangeState(state, row, col):
    n = numLiveNeighbours(state, row, col)
    if state[row][col] == 1:
        if n < 2 or n > 3:
            return 0
        else:
            return 1
    else:
        if n == 3:
            return 1
        else:
            return 0

def nextIter(state, size):
    newState = np.zeros((size, size))
    for row in range(1, size - 1):
        for col in range(1, size - 1):
            newState[row][col] = cellChangeState(state, row, col)
    return newState

def update(frame, img, state, size):
    img.set_data(state)
    newState = nextIter(state, size)
    state[:] = newState[:]
    return img,


size = int(argv[1])
fps = int(argv[2])

seed = np.random.randint(0, 2, size=(size, size))

fig, ax = plt.subplots(figsize=(16, 9))
ax.axis(False)
img = ax.matshow(seed)

ani = animation.FuncAnimation(fig, update, fargs=(img, seed, size), frames=40, interval=floor(1000 / fps), blit=True)

plt.show()