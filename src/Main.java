import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * A class Position that represents a position in a 2D grid. The class has two fields, x and y, that represent the x and y coordinates of the position.
 * The f,g,h fields are used for the A* algorithm.
 * The agent field represents the agent that is in the position.
 * The parent field represents the parent of the position in the path. It is used for the A* algorithm.
 */
class Position {
    private int x, y;
    private int f, g, h;
    private int agent;

    private Position parent;

    /**
     * Utility function to parse a position from a string.
     *
     * @param arg The string to parse.
     * @return The position parsed from the string.
     */
    public static Position parsePosition(String arg) {
        String[] args = arg.split(",");
        int x = Integer.parseInt(args[0].substring(1));
        int y = Integer.parseInt(args[1].substring(0, args[0].length() - 1));
        return new Position(x, y);
    }

    /**
     * Constructor for the Position class.
     *
     * @param x The x coordinate of the position.
     * @param y The y coordinate of the position.
     */
    public Position(int x, int y) {
        setX(x);
        setY(y);
        setF(999999999);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x +
                "," + y + ")";

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getAgent() {
        return agent;
    }

    public void setAgent(int agent) {
        this.agent = agent;
    }

    public Position getParent() {
        return parent;
    }

    public void setParent(Position parent) {
        this.parent = parent;
    }
}

/**
 * A class that represents a grid. The class has a field that represents the grid as a 2D array of Position.
 * The class has a field <b>rock</b> that represents the position of the rock.
 * The class has a field <b>tortuga</b> that represents the positions of the tortuga.
 */
class Map {
    Position[][] map = new Position[9][9];
    Position rock;
    Position tortuga;

    /**
     * Constructor for the Map class.
     */
    public Map() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                map[j][i] = new Position(i, j);
            }
        }
    }

    /**
     * This function adds the agent to the map.
     *
     * @param i        - represents the agent type
     * @param position - represents the position of the agent on the map
     */
    public void addAgent(int i, Position position) {
        map[position.getY()][position.getX()].setAgent(i);
        // If the agent it dangerous (Davy Jones or Tortuga) then we also should consider their perception zones.
        if (i == 2) {
            ArrayList<Position> perceptionZone = getMooreNeighbors(position);
            for (Position perception : perceptionZone) {
                perception.setAgent(-1);
            }
        } else if (i == 3) {

            ArrayList<Position> perceptionZone = getVonNeumannNeighbors(position);

            for (Position perception : perceptionZone) {
                perception.setAgent(-1);
            }

        }
    }

    /**
     * This function adds the rock to the map.
     *
     * @param position - represents the position of the rock on the map
     */
    public void addRock(Position position) {
        if (map[position.getY()][position.getX()].getAgent() == 0) {
            map[position.getY()][position.getX()].setAgent(4);
        }
        rock = position;
    }

    /**
     * This function adds the tortuga to the map.
     *
     * @param position - represents the position of the tortuga on the map
     */
    public void addTortuga(Position position) {
        if (map[position.getY()][position.getX()].getAgent() == 0) {
            map[position.getY()][position.getX()].setAgent(6);
        }
        tortuga = position;
    }

    /**
     * Utility function to check if the given position is valid.
     *
     * @param x - represents the x coordinate of the position
     * @param y - represents the y coordinate of the position
     * @return - true if the position is valid, false otherwise
     */
    public boolean isPositionValid(int x, int y) {
        return x >= 0 && x <= 8 && y >= 0 && y <= 8;
    }

    /**
     * Utility function to get the Moore neighbors of the given position.
     *
     * @param position - represents the given position
     * @return - returns an ArrayList of Moore neighbors
     */
    public ArrayList<Position> getMooreNeighbors(Position position) {
        ArrayList<Position> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (isPositionValid(position.getX() + i, position.getY() + j)) {
                    neighbors.add(map[position.getY() + j][position.getX() + i]);
                }

            }
        }
        return neighbors;
    }

    /**
     * Utility function to get the Von Neumann neighbors of the given position.
     *
     * @param position - represents the given position
     * @return - returns an ArrayList of Von Neumann neighbors
     */
    public ArrayList<Position> getVonNeumannNeighbors(Position position) {
        ArrayList<Position> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (i != 0 && j != 0) continue;
                if (isPositionValid(position.getX() + i, position.getY() + j)) {
                    neighbors.add(map[position.getY() + j][position.getX() + i]);
                }

            }
        }
        return neighbors;
    }

    /**
     * A function that writes the solution and corresponding statistics to a file.
     *
     * @param writer      - represents the writer that writes to the file
     * @param solution    - represents the solution
     * @param elapsedTime - represents the time it took to find the solution
     * @throws IOException - throws an exception if the file is not found
     */
    public void representSolution(FileWriter writer, ArrayList<Position> solution, long elapsedTime) throws IOException {
        writer.write((solution.size() - 1) + "\n");
        for (Position position : solution) {
            writer.write("[" + position.getX() + "," + position.getY() + "] ");
        }
        writer.write("\n");
        writer.write("-------------------\n");
        writer.write("  0 1 2 3 4 5 6 7 8\n");
        for (int i = 0; i < 9; i++) {
            writer.write(i + "");
            for (int j = 0; j < 9; j++) {
                if (solution.contains(map[j][i])) {
                    writer.write(" *");
                } else {
                    writer.write(" -");
                }
            }
            writer.write("\n");
        }
        writer.write("-------------------\n");
        float time = (float) Math.round(elapsedTime * 100 / 1000000.0f) / 100;
        writer.write(time + " ms\n");


    }

    /**
     * A function that initializes AStar algorithm and finds the solution or combine them in case of killing the Kraken.
     */
    public void aStarInit() {
        // Try to reach the chest without visiting the tortuga and killing the kraken
        // If the chest is not reachable, try to reach the tortuga and then kill the kraken
        Position jack = null;
        Position chest = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (map[i][j].getAgent() == 1) {
                    jack = new Position(j, i);
                }
                if (map[i][j].getAgent() == 5) {
                    chest = new Position(j, i);
                }
            }
        }
        long startTime = System.nanoTime();
        ArrayList<Position> solution = aStar(jack, chest);
        long elapsedTime = System.nanoTime() - startTime;
        if (solution != null) {
            //Solution without killing the kraken exists
            solution.add(jack);
            Collections.reverse(solution);
            try {
                File outputFile = new File("outputAStar.txt");
                outputFile.createNewFile();
                FileWriter writer = new FileWriter("outputAStar.txt");
                writer.write("Win\n");
                representSolution(writer, solution, elapsedTime);

                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            startTime = System.nanoTime();
            // Now try to reach the tortuga
            ArrayList<Position> solution1 = aStar(jack, tortuga);
            // Now try to kill the kraken
            ArrayList<Position> solution2 = aStar(tortuga, chest);
            if (solution1 == null || solution2 == null) {
                // No solution
                try {
                    File outputFile = new File("outputAStar.txt");
                    outputFile.createNewFile();
                    FileWriter writer = new FileWriter("outputAStar.txt");
                    writer.write("Lose\n");
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                solution1.add(jack);
                Collections.reverse(solution1);
                elapsedTime = System.nanoTime() - startTime;
                Collections.reverse(solution2);
                solution1.addAll(solution2);
                try {
                    File outputFile = new File("outputAStar.txt");
                    outputFile.createNewFile();
                    FileWriter writer = new FileWriter("outputAStar.txt");
                    writer.write("Win\n");
                    representSolution(writer, solution1, elapsedTime);

                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        }
    }

    /**
     * A* Algorithm. A Position contains a cost to reach it, a cost to reach the goal and a parent Position.
     */
    public ArrayList<Position> aStar(Position start, Position goal) {
        PriorityQueue<Position> open = new PriorityQueue<>(81, Comparator.comparingInt(Position::getF));
        ArrayList<Position> closedList = new ArrayList<>();
        open.add(start);
        start.setF(0);
        while (!open.isEmpty()) {
            Position current = open.poll();
            for (Position neighbor : getMooreNeighbors(current)) {
                if (neighbor.equals(goal)) {
                    neighbor.setParent(current);
                    return reconstructPath(neighbor);
                }
                if (start.equals(tortuga) && neighbor.getAgent() == 3 && !neighbor.equals(rock)) {
                    // We killed the kraken
                    neighbor.setAgent(0);
                    for (Position danger : getMooreNeighbors(neighbor)) {
                        danger.setAgent(0);
                    }
                }
                if (neighbor.getAgent() != -1 && neighbor.getAgent() != 4 && neighbor.getAgent() != 2 && neighbor.getAgent() != 3) {
                    if (!open.contains(neighbor)) {
                        neighbor.setG(current.getG() + 1);
                        neighbor.setH(Math.abs(neighbor.getX() - goal.getX()) + Math.abs(neighbor.getY() - goal.getY()));
                        neighbor.setF(neighbor.getG() + neighbor.getH());
                        if (!closedList.contains(neighbor)) {
                            neighbor.setParent(current);
                            open.add(neighbor);
                        }
                    } else {
                        if (current.getF() + 1 < neighbor.getF()) {
                            neighbor.setG(current.getG() + 1);
                            neighbor.setH(Math.abs(neighbor.getX() - goal.getX()) + Math.abs(neighbor.getY() - goal.getY()));
                            neighbor.setF(neighbor.getG() + neighbor.getH());
                            neighbor.setParent(current);
                        }
                    }
                }

            }
            closedList.add(current);

        }

        return null;
    }

    /**
     * Reconstructs the path from the goal to the start.
     *
     * @param goal The goal Position
     * @return The path from the goal to the start
     */
    public ArrayList<Position> reconstructPath(Position goal) {
        ArrayList<Position> path = new ArrayList<>();
        Position current = goal;
        while (current.getParent() != null) {
            path.add(current);
            current = current.getParent();
        }
        return path;
    }

    /**
     * A function that initializes the backtracking algorithm and finds the solution or combine them in case of killing the Kraken.
     */
    public void backtrackInit() {
        Position jack = null;
        Position chest = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (map[i][j].getAgent() == 1) {
                    jack = new Position(j, i);
                }
                if (map[i][j].getAgent() == 5) {
                    chest = new Position(j, i);
                }
            }
        }
        long startTime = System.nanoTime();
        ArrayList<Position> solution = backtrackingSearch(jack, chest);
        long elapsedTime = System.nanoTime() - startTime;
        if (solution != null && solution.get(solution.size() - 1) != null && solution.get(solution.size() - 1).equals(chest)) {
            try {
                File outputFile = new File("outputBacktracking.txt");
                outputFile.createNewFile();
                FileWriter writer = new FileWriter("outputBacktracking.txt");
                writer.write("Win\n");
                representSolution(writer, solution, elapsedTime);

                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            try {
                File outputFile = new File("outputBacktracking.txt");
                outputFile.createNewFile();
                FileWriter writer = new FileWriter("outputBacktracking.txt");
                writer.write("Lose\n");
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Backtracking search.
     * Move towards the goal, if a position which is less successful is reached - stop and move back up the tree.
     *
     * @return The solution or null if there is no solution.
     */
    public ArrayList<Position> backtrackingSearch(Position start, Position goal) {
        ArrayList<Position> path = new ArrayList<>();
        path.add(start);
        ArrayList<Position> forks = new ArrayList<>();
        Position current = start;
        int bestResult = 99;
        while (current != null && !current.equals(goal)) {
            ArrayList<Position> neighbors = getMooreNeighbors(current);
            Position best = null;
            for (Position neighbor : neighbors) {
                if (path.contains(neighbor) || neighbor.getAgent() == -1 || neighbor.getAgent() == 2 || neighbor.getAgent() == 3 || neighbor.getAgent() == 4)
                    continue;
                if (best == null) {
                    best = neighbor;
                } else {
                    if (Math.abs(neighbor.getX() - goal.getX()) + Math.abs(neighbor.getY() - goal.getY()) < Math.abs(best.getX() - goal.getX()) + Math.abs(best.getY() - goal.getY())) {
                        best = neighbor;
                    } else if (Math.abs(neighbor.getX() - goal.getX()) + Math.abs(neighbor.getY() - goal.getY()) == Math.abs(best.getX() - goal.getX()) + Math.abs(best.getY() - goal.getY())) {
                        neighbor.setParent(current);
                        forks.add(neighbor); // The algorithm will return to the fork and try another path
                    }
                }
            }
            current.setParent(best);
            current = best;
            path.add(current);
            // If the size of the path is large enough, then there is no solution(at least without visiting tortuga).
            if (path.size() > 65) {
                break;
            }
        }
        // If we have forks, we will try to find the best path from them and then return to the main path
        ArrayList<ArrayList<Position>> forkedPaths = new ArrayList<>();
        if (current != null && !forks.isEmpty() && current.equals(goal)) {
            bestResult = path.size();
            for (Position fork : forks) {
                ArrayList<Position> newPathForFork = new ArrayList<>();
                for (Position position : path) {
                    if (position.equals(fork.getParent())) {
                        newPathForFork.add(position);
                        break;
                    }
                    newPathForFork.add(position);
                }
                newPathForFork.add(fork);

                backtrackingSearchForFork(newPathForFork, goal, bestResult, forkedPaths);
            }
        }
        for (ArrayList<Position> forkedPath : forkedPaths) {
            if (forkedPath.size() < bestResult) {
                bestResult = forkedPath.size();
                path = forkedPath;
            }
        }
        if (current == null) {
            //Restart the algorithm with the goal to reach the tortuga and then reach the chest by killing the kraken.
            path.clear();
            path.add(start);
            current = start;
            while (current != null && !current.equals(tortuga)) {
                ArrayList<Position> neighbors = getMooreNeighbors(current);
                Position best = null;
                for (Position neighbor : neighbors) {
                    if (path.contains(neighbor) || neighbor.getAgent() == -1 || neighbor.getAgent() == 2 || neighbor.getAgent() == 3 || neighbor.getAgent() == 4)
                        continue;
                    if (best == null) {
                        best = neighbor;
                    } else {
                        if (Math.abs(neighbor.getX() - tortuga.getX()) + Math.abs(neighbor.getY() - tortuga.getY()) < Math.abs(best.getX() - tortuga.getX()) + Math.abs(best.getY() - tortuga.getY())) {
                            best = neighbor;
                        } else if (Math.abs(neighbor.getX() - tortuga.getX()) + Math.abs(neighbor.getY() - tortuga.getY()) == Math.abs(best.getX() - tortuga.getX()) + Math.abs(best.getY() - tortuga.getY())) {
                            forks.add(neighbor); // The algorithm will return to the fork and try another path
                        }
                    }
                }
                current = best;
                path.add(current);
                if (path.size() > 65) {
                    break;
                }
            }
            // Now, we will try to kill the kraken and reach the chest.
            ArrayList<Position> pathToChest = new ArrayList<>();
            while (current != null && !current.equals(goal)) {
                ArrayList<Position> neighbors = getMooreNeighbors(current);
                Position best = null;
                for (Position neighbor : neighbors) {
                    if (neighbor.getAgent() == 3) {
                        // We killed the kraken
                        neighbor.setAgent(0);
                        for (Position danger : getMooreNeighbors(neighbor)) {
                            danger.setAgent(0);
                        }
                    }
                    if (pathToChest.contains(neighbor) || neighbor.getAgent() == -1 || neighbor.getAgent() == 2 || neighbor.getAgent() == 4)
                        continue;
                    if (best == null) {
                        best = neighbor;
                    } else {
                        if (Math.abs(neighbor.getX() - goal.getX()) + Math.abs(neighbor.getY() - goal.getY()) < Math.abs(best.getX() - goal.getX()) + Math.abs(best.getY() - goal.getY())) {
                            best = neighbor;
                        } else if (Math.abs(neighbor.getX() - goal.getX()) + Math.abs(neighbor.getY() - goal.getY()) == Math.abs(best.getX() - goal.getX()) + Math.abs(best.getY() - goal.getY())) {
                            neighbor.setParent(current);
                            forks.add(neighbor); // The algorithm will return to the fork and try another path
                        }
                    }
                }
                current.setParent(best);
                current = best;
                pathToChest.add(current);
                // If the size of the path is large enough, then there is no solution(at least without visiting tortuga).
                if (path.size() > 65) {
                    break;
                }
            }
            path.addAll(pathToChest);
        }

        return path;
    }

    /**
     * This method is used to find the best path from the fork.
     *
     * @param path        The path to the fork.
     * @param goal        The goal.
     * @param bestResult  The best result.
     * @param forkedPaths The list of forked paths.
     */
    public void backtrackingSearchForFork(ArrayList<Position> path, Position goal, int bestResult, ArrayList<ArrayList<Position>> forkedPaths) {
        ArrayList<Position> forks = new ArrayList<>();
        Position current = path.get(path.size() - 1);
        while (current != null && !current.equals(goal)) {
            ArrayList<Position> neighbors = getMooreNeighbors(current);
            Position best = null;
            for (Position neighbor : neighbors) {

                if (neighbor.getAgent() == -1 || neighbor.getAgent() == 2 || neighbor.getAgent() == 3 || neighbor.getAgent() == 4)
                    continue;
                if (best == null) {
                    best = neighbor;
                } else {
                    if (Math.abs(neighbor.getX() - goal.getX()) + Math.abs(neighbor.getY() - goal.getY()) < Math.abs(best.getX() - goal.getX()) + Math.abs(best.getY() - goal.getY())) {
                        best = neighbor;
                    } else if (Math.abs(neighbor.getX() - goal.getX()) + Math.abs(neighbor.getY() - goal.getY()) == Math.abs(best.getX() - goal.getX()) + Math.abs(best.getY() - goal.getY())) {
                        neighbor.setParent(current);
                        forks.add(neighbor); // The algorithm will return to the fork and try another path
                    }
                }
            }
            current.setParent(best);
            current = best;
            path.add(current);
            if (path.size() > bestResult) {
                return;
            }
            forkedPaths.add(path);
            if (path.size() > 65) {
                break;
            }
        }
    }

    /**
     * This method is used to check validity of the map.
     * @param agents The list of agents.
     * @return True if the map is valid, false otherwise.
     */
    public boolean checkValidity(String[] agents) {
        ArrayList<Position> agentsParsed = new ArrayList<>();
        for (int i=0;i<6;i++) {
            Position temp = Position.parsePosition(agents[i]);
            if (isPositionValid(temp.getX(), temp.getY())) {
                temp.setAgent(i+1);
                agentsParsed.add(temp);
            } else {
                return false;
            }
        }
        Position jack = agentsParsed.get(0);
        Position davy = agentsParsed.get(1);
        Position kraken = agentsParsed.get(2);
        Position rock = agentsParsed.get(3);
        Position chest = agentsParsed.get(4);
        Position tortuga = agentsParsed.get(5);

        if (jack.getX()!=0 && jack.getY()!=0) {
            // Check Jack
            return false;
        } else if (davy.getX() == 0 && davy.getY() == 0) {
            // Check Davy
            return false;
        } else if (getMooreNeighbors(kraken).contains(davy) || kraken.getX() == 0 && kraken.getY() == 0 || kraken.getX() == davy.getX() && kraken.getY() == davy.getY() ) {
            // Check Kraken
            return false;
        } else if (getMooreNeighbors(rock).contains(davy) || rock.getX() == 0 && rock.getY() == 0 || rock.getX() == davy.getX() && rock.getY() == davy.getY()) {
            // Check Rock
            return false;
        } else if (map[chest.getY()][chest.getX()].getAgent() == -1 || chest.getX() == 0 && chest.getY() == 0 || chest.getX() == davy.getX() && chest.getY() == davy.getY() || chest.getX() == kraken.getX() && chest.getY() == kraken.getY() || chest.getX() == rock.getX() && chest.getY() == rock.getY()) {
            // Check Chest
            return false;
        } else if (map[tortuga.getY()][tortuga.getX()].getAgent() == -1 || tortuga.getX() == davy.getX() && tortuga.getY() == davy.getY() || tortuga.getX() == kraken.getX() && tortuga.getY() == kraken.getY() || tortuga.getX() == rock.getX() && tortuga.getY() == rock.getY() || tortuga.getX() == chest.getX() && tortuga.getY() == chest.getY()) {
            // Check Tortuga
            return false;
        }
        return true;
    }
}

/**
 * The class is used to generate map and do tests.
 */
class Test {

    /**
     * Generate a new random map. The map is a 9x9 grid.
     * 0 - empty space
     * 1 - Jack - always at 0,0
     * 2 - Davy Jones - except inside the Tortuga and the chest, positons of the Kraken, the Rock and Jack.
     * 3 - Kraken - except inside the Tortuga and the chest, positons of the Davy Jones and Jack.
     * 4 - Rock - except inside the Tortuga and the chest, position of the Davy Jones and Jack.
     * 5 - Chest - except danger zones and Jack's position.
     * 6 - Tortuga - except danger zones and the chest.
     * -1 - Dangerous zone
     *
     * @param map The map.
     * @return agents The list of agents.
     */
    public static ArrayList<Position> generateMap(Map map) {
        Random random = new Random();


        Position jack = new Position(0, 0);
        map.addAgent(1, jack);
        Position davy;
        Position kraken;
        Position rock;
        Position chest;
        Position tortuga;
        ArrayList<Position> agents = new ArrayList<>();
        do {
            davy = new Position(random.nextInt(9), random.nextInt(9));
        } while (davy.getX() == 0 && davy.getY() == 0);
        map.addAgent(2, davy);
        do {
            kraken = new Position(random.nextInt(9), random.nextInt(9));
        } while (map.getMooreNeighbors(kraken).contains(davy) || kraken.getX() == 0 && kraken.getY() == 0 || kraken.getX() == davy.getX() && kraken.getY() == davy.getY());
        map.addAgent(3, kraken);
        do {
            rock = new Position(random.nextInt(9), random.nextInt(9));
        } while (map.getMooreNeighbors(rock).contains(davy) || rock.getX() == 0 && rock.getY() == 0 || rock.getX() == davy.getX() && rock.getY() == davy.getY());
        map.addRock(rock);
        do {
            chest = new Position(random.nextInt(9), random.nextInt(9));
        } while (map.map[chest.getY()][chest.getX()].getAgent() == -1 || chest.getX() == 0 && chest.getY() == 0 || chest.getX() == davy.getX() && chest.getY() == davy.getY() || chest.getX() == kraken.getX() && chest.getY() == kraken.getY() || chest.getX() == rock.getX() && chest.getY() == rock.getY());
        map.addAgent(5, chest);
        do {
            tortuga = new Position(random.nextInt(9), random.nextInt(9));
        } while (map.map[tortuga.getY()][tortuga.getX()].getAgent() == -1 || tortuga.getX() == davy.getX() && tortuga.getY() == davy.getY() || tortuga.getX() == kraken.getX() && tortuga.getY() == kraken.getY() || tortuga.getX() == rock.getX() && tortuga.getY() == rock.getY() || tortuga.getX() == chest.getX() && tortuga.getY() == chest.getY());
        map.addTortuga(tortuga);
        agents.add(jack);
        agents.add(davy);
        agents.add(kraken);
        agents.add(rock);
        agents.add(chest);
        agents.add(tortuga);
        return agents;
    }

    /**
     * This method is used to compare the algorithms using statistical analysis. The statistics are: the mean, mode, median and standard deviation for execution time, number of wins and number of loses.
     */
    public static void analysis() {
        int numberOfTests = 1000;
        int[] numberOfWins = new int[]{0, 0};
        int[] numberOfLoses = new int[]{0, 0};
        ArrayList<Double> resultsAStar = new ArrayList<>();
        ArrayList<Double> resultsBacktrack = new ArrayList<>();
        for (int k = 0; k < numberOfTests; k++) {
            Map map = new Map();
            ArrayList<Position> agents = generateMap(map);
            if (map.map[0][0].getAgent() == -1) {
                // Dangerous zone at the Jack's Position == lose
                numberOfLoses[0]++;
                numberOfLoses[1]++;
            } else {
                map.aStarInit();
                for (int i = 0; i < 6; i++) {
                    if (i == 3) {
                        map.addRock(agents.get(i));
                    } else if (i == 5) {
                        map.addTortuga(agents.get(i));
                    } else {
                        map.addAgent(i + 1, agents.get(i));
                    }
                }

                map.backtrackInit();
                ArrayList<String> resultsA = readResultsFromFile("outputAStar.txt");
                ArrayList<String> resultsB = readResultsFromFile("outputBacktracking.txt");
                if (resultsA.get(0).equals("Win")) {
                    numberOfWins[0]++;
                    resultsAStar.add(Double.parseDouble(resultsA.get(15).split(" ")[0]));
                } else {
                    numberOfLoses[0]++;
                }
                if (resultsB.get(0).equals("Win")) {
                    numberOfWins[1]++;
                    resultsBacktrack.add(Double.parseDouble(resultsB.get(15).split(" ")[0]));
                } else {
                    numberOfLoses[1]++;
                }
            }
        }
        double meanAStar = resultsAStar.stream().mapToDouble(a -> a).average().orElse(0.0);
        double meanBacktrack = resultsBacktrack.stream().mapToDouble(a -> a).average().orElse(0.0);
        Double modeAStar = mode(resultsAStar);
        Double modeBacktrack = mode(resultsBacktrack);
        Double medianAStar = median(resultsAStar);
        Double medianBacktrack = median(resultsBacktrack);
        Double standardDeviationAStar = standardDeviation(resultsAStar);
        Double standardDeviationBacktrack = standardDeviation(resultsBacktrack);
        System.out.println("AStar: ");
        System.out.println("Mean: " + meanAStar);
        System.out.println("Mode: " + modeAStar);
        System.out.println("Median: " + medianAStar);
        System.out.println("Standard Deviation: " + standardDeviationAStar);
        System.out.println("Number of wins: " + numberOfWins[0]);
        System.out.println("Number of loses: " + numberOfLoses[0]);
        System.out.println("Backtrack: ");
        System.out.println("Mean: " + meanBacktrack);
        System.out.println("Mode: " + modeBacktrack);
        System.out.println("Median: " + medianBacktrack);
        System.out.println("Standard Deviation: " + standardDeviationBacktrack);
        System.out.println("Number of wins: " + numberOfWins[1]);
        System.out.println("Number of loses: " + numberOfLoses[1]);

    }

    /**
     * This method is used to calculate the mode of a list of numbers.
     *
     * @param results is the list of numbers.
     * @return the mode of the list.
     */
    private static Double mode(ArrayList<Double> results) {
        HashMap<Double, Integer> map = new HashMap<>();
        for (Double i : results) {
            if (map.containsKey(i)) {
                map.put(i, map.get(i) + 1);
            } else {
                map.put(i, 1);
            }
        }
        int max = 0;
        Double mode = 0.0;
        for (java.util.Map.Entry<Double, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mode = entry.getKey();
            }
        }
        return mode;
    }

    /**
     * This method is used to calculate the median of the execution times of the algorithms.
     *
     * @param results The list of execution times.
     * @return The median of the execution times.
     */
    private static Double median(ArrayList<Double> results) {
        Collections.sort(results);
        if (results.size() % 2 == 0) {
            return (results.get(results.size() / 2) + results.get(results.size() / 2 - 1)) / 2;
        } else {
            return results.get(results.size() / 2);
        }
    }

    /**
     * This method is used to calculate the standard deviation of the execution time of the algorithms.
     *
     * @param results The list of execution times.
     * @return The standard deviation.
     */
    private static Double standardDeviation(ArrayList<Double> results) {
        double mean = results.stream().mapToDouble(a -> a).average().orElse(0.0);
        double sum = 0;
        for (Double i : results) {
            sum += Math.pow(i - mean, 2);
        }
        return Math.sqrt(sum / (results.size()-1));
    }

    /**
     * Utility function to read results from the file
     */
    public static ArrayList<String> readResultsFromFile(String fileName) {
        ArrayList<String> results = new ArrayList<>();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String temp = scanner.nextLine();
                results.add(temp);
                if (temp.equals("Lose")) {
                    scanner.close();
                    break;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return results;
    }

}

public class Main {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("How do you want to input the map? (1 - from file, 2 - generate a map, 3 - do 1000 tests and provide analysis)");
        int input = reader.nextInt();
        Map map = new Map();
        String[] agents = new String[6];
        int variant; // It was supposed to be used for the second variant of the task, but I didn't have time to implement it, and also I am not sure whether it will change the results.
        // I think that it could improve performance(Jack will not go to dead ends, but go to the goal by different way avoiding dead ends).
        ArrayList<Position> agentsParsed = new ArrayList<>();
        if (input == 1) {
            File inputFile = new File("input.txt");

            try {
                Scanner fileReader = new Scanner(inputFile);
                agents = fileReader.nextLine().split(" ", 0);

                variant = Integer.parseInt(fileReader.nextLine());
                fileReader.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            if(!map.checkValidity(agents)){
                throw new RuntimeException("The given input is not correct");
            }
            for (int i = 0; i < 6; i++) {
                Position temp = Position.parsePosition(agents[i]);
                if (i == 3) {
                    map.addRock(temp);
                } else if (i == 5) {
                    map.addTortuga(temp);
                } else {
                    map.addAgent(i + 1, temp);
                }
            }

        } else if (input == 2) {
            try {
                Scanner consoleReader = new Scanner(System.in);

                System.out.println("Input the variant of the spyglass (1 - Moore neighborhood, 2 - Manhattan neighborhood with radius equal to 2)");
                variant = Integer.parseInt(consoleReader.nextLine());
                consoleReader.close();
            } catch (Exception e) {
                throw new RuntimeException("Invalid input");
            }
            agentsParsed = Test.generateMap(map);
        } else if (input == 3) {
            Test.analysis();
            return;
        } else {
            throw new RuntimeException("Invalid input");
        }
        reader.close();

        map.aStarInit();
        // After A* is applied to the map, the map is changed, so we need to restore it to its original state.
        if (input == 1) {
            for (int i = 0; i < 6; i++) {
                Position temp = Position.parsePosition(agents[i]);
                if (i == 3) {
                    map.addRock(temp);
                } else if (i == 5) {
                    map.addTortuga(temp);
                } else {
                    map.addAgent(i + 1, temp);
                }
            }
            map.backtrackInit();
        } else {
            for (int i = 0; i < 6; i++) {
                {
                    if (i == 3) {
                        map.addRock(agentsParsed.get(i));
                    } else if (i == 5) {
                        map.addTortuga(agentsParsed.get(i));
                    } else {
                        map.addAgent(i + 1, agentsParsed.get(i));
                    }
                }
            }
            map.backtrackInit();

        }
    }
}