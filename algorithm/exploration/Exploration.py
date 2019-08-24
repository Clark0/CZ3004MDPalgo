import numpy as np
from algorithm.Constants import ROW, COL, EAST, WEST, NORTH, SOUTH, START_POSITION
from algorithm.models.Robot import Robot


class Exploration:
    def __init__(self):
        self.currentMap = np.zeros([ROW, COL])
        self.robot = Robot(EAST, START_POSITION, self.currentMap)