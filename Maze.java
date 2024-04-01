package edu.njit.cs114;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Class that solves maze problems with backtracking.
 *
 * @author Koffman and Wolfgang
 * Modified by Alejandro Serna
 **/
public class Maze {

    public static Color PATH = Color.green;
    public static Color BACKGROUND = Color.white;
    public static Color NON_BACKGROUND = Color.red;
    public static Color TEMPORARY = Color.black;

    /** The maze */
    private TwoDimGrid maze;

    private int cnt;

    private static class Cell {
        public final int col, row;
        public Cell(int col, int row) {
            this.col = col;
            this.row = row;
        }
        public boolean equals(Object obj) {
            if (!(obj instanceof Cell)) {
                return false;
            }
            Cell other = (Cell) obj;
            return this.row == other.row && this.col == other.col;
        }

        public String toString() {
            return "(" + col + "," + row + ")";
        }
    }

    private static boolean inStack(Stack<Cell> pathStack, int col, int row) {
        Cell target = new Cell(col, row);
        return pathStack.contains(target);
    }

    public Maze(TwoDimGrid m) {
        maze = m;
    }

    /** Wrapper method. */
    public boolean findMazePath(int startCol, int startRow, int destCol, int destRow) {
        cnt = 0; // keeps track of order of cell visits
        return findMazePathAux(startCol, startRow, destCol, destRow);
    }

    /**
     * Attempts to find a maze path recursively through point (x, y) to destination cell (destCol,destRow)
     *
     * @pre Possible path cells are initially in NON_BACKGROUND color
     * @post If a path is found, all cells on it are set to the PATH color; all
     *       cells that were visited but are not on the path are in the TEMPORARY
     *       color.
     * @param x
     *            The x-coordinate of current point
     * @param y
     *            The y-coordinate of current point
     * @param destCol
     *            The x-coordinate of destination cell
     * @param destRow
     *            The y-coordinate of destination cell
     * @return If a path through (x, y) is found, true; otherwise, false
     *         If a path is found, color the cells in the path with PATH color
     *         Also number cells in the order in which they are visited
     */
    public boolean findMazePathAux(int x, int y, int destCol, int destRow) {
        if (y>=maze.getNRows() || x >= maze.getNCols() || x <0 || y<0) {
            return false;
        }

        if(maze.getColor(x,y) != NON_BACKGROUND){
            return false;
        }

        if (x == destCol && y == destRow) {
            maze.recolor(x, y, edu.njit.cs114.Maze.PATH);
            cnt++;
            maze.setLabel(x,y,""+cnt);
            return true;
        }

        maze.recolor(x, y, edu.njit.cs114.Maze.TEMPORARY);
        cnt++;
        maze.setLabel(x,y,cnt+"");

        if ((findMazePathAux(x + 1, y, destCol, destRow) ||
                findMazePathAux(x, y + 1, destCol, destRow) ||
                findMazePathAux(x - 1, y, destCol, destRow) ||
                findMazePathAux(x, y - 1, destCol, destRow)))
        {
            maze.recolor(x, y, edu.njit.cs114.Maze.PATH);
            return true;
        } else {return false;}
    }

    /* Wrapper method for finding shortest path **/
    public boolean findMazeShortestPath(int startCol, int startRow, int destCol, int destRow) {
        Stack<Cell> pathStack = new Stack<>();
        pathStack.push(new Cell(startCol,startRow));
        ArrayList<Cell> shortestPath = findMazeShortestPathAux(startCol, startRow, destCol, destRow, pathStack);
        int cnt = 0;
        if (!shortestPath.isEmpty()) {
            /**
             * TO BE COMPLETED (color shortest path green with labels indicating
             *    the order of cells in this path)
             */
            for (Cell cell : shortestPath) {
                maze.recolor(cell.col, cell.row, PATH);
                maze.setLabel(cell.col, cell.row, String.valueOf(++cnt));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempts to find shortest path through point (x, y).
     *
     * @pre Possible path cells are initially in NON_BACKGROUND color
     * @post If a path is found, all cells on it are set to the PATH color; all
     *       cells that were visited but are not on the path are in the TEMPORARY
     *       color.
     * @param x
     *            The x-coordinate of current point
     * @param y
     *            The y-coordinate of current point
     * @param pathStack
     *            Stack that contains the current path found so far
     * @return If a path through (x, y) is found return shortest path; otherwise, return empty path
     */
    public ArrayList<Cell> findMazeShortestPathAux(int x, int y, int destCol, int destRow, Stack<Cell> pathStack) {
        if (x < 0 || y < 0 || x >= maze.getNCols() || y >= maze.getNRows() || maze.getColor(x, y) != NON_BACKGROUND) {
            return null;
        }
        if (x == destCol && y == destRow) {
            ArrayList<Cell> path = new ArrayList<>(pathStack);
            path.add(new Cell(x, y));
            return path;
        }
        maze.recolor(x, y, TEMPORARY);

        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        ArrayList<Cell> shortestPath = null;
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            pathStack.push(new Cell(x, y));
            ArrayList<Cell> newPath = findMazeShortestPathAux(newX, newY, destCol, destRow, pathStack);
            pathStack.pop();
            if (newPath != null && (shortestPath == null || newPath.size() < shortestPath.size())) {
                shortestPath = newPath;
            }
        }
        maze.recolor(x, y, NON_BACKGROUND);
        return shortestPath;
    }

    public void resetTemp() {
        maze.recolor(TEMPORARY, BACKGROUND);
    }

    public void resetMaze() {
        resetTemp();
        maze.recolor(PATH, BACKGROUND);
        maze.recolor(NON_BACKGROUND, BACKGROUND);
        maze.clearLabels();
    }

    public void restoreMaze() {
        maze.recolor(PATH, NON_BACKGROUND);
        maze.recolor(TEMPORARY, NON_BACKGROUND);
        maze.clearLabels();
    }
}
