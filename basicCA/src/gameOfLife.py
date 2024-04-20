from sys import argv
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import scipy.signal
from math import floor

plt.rcParams['image.cmap'] = 'binary'

def nextIter(state, size):
    mask = np.array([[1, 1, 1],
                      [1, 0, 1],
                      [1, 1, 1]])
    liveNeighboursCount = scipy.signal.convolve2d(state, mask, mode='same')

    newState = np.empty((size, size))
    for row in range(size):
        for col in range(size):
            n = liveNeighboursCount[row][col]
            if state[row][col] == 1:
                if n < 2 or n > 3:
                    newState[row][col] = 0
                else:
                    newState[row][col] = 1
            else:
                if n == 3:
                    newState[row][col] = 1
                else:
                    newState[row][col] = 0
    
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