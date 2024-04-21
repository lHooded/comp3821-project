from sys import argv
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import matplotlib.colors as mcolors
import scipy.signal
from math import floor, sqrt
import random as rand

colors = ['grey', 'white', 'blue']
positions = [0, 0.5, 1]
custom_cmap = mcolors.LinearSegmentedColormap.from_list("custom_colormap", list(zip(positions, colors)))
plt.register_cmap(cmap=custom_cmap)
plt.rcParams['image.cmap'] = 'custom_colormap'


def nextIter(state, obstacle, size):
    mask = np.array([[1, 1, 1],
                      [1, 0, 1],
                      [1, 1, 1]])
    neighbourSum = scipy.signal.convolve2d(state, mask, mode='same')
    obstacleSum = scipy.signal.convolve2d(obstacle, mask, mode='same')
    updateObstacleSumBoundaries(obstacleSum)

    newState = np.empty((size, size))
    for row in range(size):
        for col in range(size):
            nSum = neighbourSum[row, col]
            oSum = obstacleSum[row, col]

            if oSum < 8:
                newState[row, col] = (state[row, col] + sqrt(nSum / (8 - oSum))) / 2
            else:
                newState[row, col] = state[row, col]

            if obstacle[row, col] == 1:
                newState[row, col] = 0
            elif row == size - 1:
                newState[row, col] = 1

    return newState

def normaliseState(state):
    for row in range(size):
        for col in range(size):
            state[row, col] = (state[row, col] + 1) / 2
            if obstacle[row, col] == 1:
                state[row][col] = 0

def normaliseStateInverse(state):
    for row in range(size):
        for col in range(size):
            state[row, col] = (2 * state[row, col]) - 1
            if obstacle[row, col] == 1:
                state[row][col] = 0


def updateObstacleSumBoundaries(obstacleSum):
    obstacleSum[0, 0] += 5
    obstacleSum[0, size - 1] += 5
    obstacleSum[1:, 0] += 3
    obstacleSum[1:, size - 1] += 3
    obstacleSum[0, 1:size - 1] += 3

def initObstacle():
    obstacle = np.ones((size, size))
    for count in range(floor(size / 8)):
        obstacle[rand.randint(0, size - 4), :] = 0
        obstacle[:, rand.randint(0, size - 1)] = 0

    for count in range(floor(size / 30)):
        startingTopIndex = rand.randint(0, size - 1)
        for i in range(size):
            if startingTopIndex - i < 0:
                break
            obstacle[i, startingTopIndex - i] = 0
        startingBottomIndex = rand.randint(0, size - 1)
        for i in range(size):
            if startingBottomIndex + i > size - 1:
                break
            obstacle[size - 1 - i, startingBottomIndex + i] = 0

    for count in range(floor(size / 30)):
        startingTopIndex = rand.randint(0, size - 1)
        for i in range(size):
            if startingTopIndex + i > size - 1:
                break
            obstacle[i, startingTopIndex + i] = 0
        startingBottomIndex = rand.randint(0, size - 1)
        for i in range(size):
            if startingBottomIndex - i < 0:
                break
            obstacle[size - 1 - i, startingBottomIndex - i] = 0

    obstacle[size - 1, :] = 0
    return obstacle

def update(frame, img, state, size):
    img.set_data(state)

    normaliseStateInverse(state)
    newState = nextIter(state, obstacle, size)
    normaliseState(newState)
    state[:] = newState[:]

    return img,


size = int(argv[1])
fps = float(argv[2])

obstacle = initObstacle()
seed = np.zeros((size, size))
seed[size - 1, :] = 1
normaliseState(seed)


fig, ax = plt.subplots(figsize=(16, 9))
ax.axis(False)
img = ax.matshow(seed)

ani = animation.FuncAnimation(fig, update, fargs=(img, seed, size), frames=40, interval=floor(1000 / fps), blit=True)

plt.show()