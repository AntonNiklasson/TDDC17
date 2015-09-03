package tddc17;


import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;
import aima.core.environment.liuvacuum.*;

import java.lang.System;
import java.util.*;

class MyAgentState {
    public int[][] world = new int[30][30];
    public int initialized = 0;
    final int UNKNOWN = 0;
    final int WALL = 1;
    final int CLEAR = 2;
    final int DIRT = 3;
    final int HOME = 4;
    final int ACTION_NONE = 0;
    final int ACTION_MOVE_FORWARD = 1;
    final int ACTION_TURN_RIGHT = 2;
    final int ACTION_TURN_LEFT = 3;
    final int ACTION_SUCK = 4;

    public int agent_x_position = 1;
    public int agent_y_position = 1;
    public int agent_previous_x_position = 1;
    public int agent_previous_y_position = 1;

    public int agent_last_action = ACTION_NONE;

    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    public int agent_direction = EAST;

    MyAgentState() {
        for (int i = 0; i < world.length; i++)
            for (int j = 0; j < world[i].length; j++)
                world[i][j] = UNKNOWN;
        world[1][1] = HOME;
        agent_last_action = ACTION_NONE;
    }

    // Based on the last action and the received percept updates the x & y agent position
    public void updatePosition(DynamicPercept p) {
        Boolean bump = (Boolean) p.getAttribute("bump");

        if (agent_last_action == ACTION_MOVE_FORWARD && !bump) {
            agent_previous_y_position = agent_y_position;
            agent_previous_x_position = agent_x_position;

            switch (agent_direction) {
                case MyAgentState.NORTH:
                    agent_y_position--;
                    break;
                case MyAgentState.EAST:
                    agent_x_position++;
                    break;
                case MyAgentState.SOUTH:
                    agent_y_position++;
                    break;
                case MyAgentState.WEST:
                    agent_x_position--;
                    break;
            }
        } else {
            System.out.println("-------------------------");
            System.out.println("Not updating the agents position!");
            System.out.println("-------------------------");
        }

    }

    public void updateWorld(int x_position, int y_position, int info) {
        world[x_position][y_position] = info;
    }

    public void printWorldDebug() {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if (world[j][i] == UNKNOWN)
                    System.out.print(" ? ");
                if (world[j][i] == WALL)
                    System.out.print(" # ");
                if (world[j][i] == CLEAR)
                    System.out.print(" . ");
                if (world[j][i] == DIRT)
                    System.out.print(" D ");
                if (world[j][i] == HOME)
                    System.out.print(" H ");
            }
            System.out.println("");
        }
    }
}

class MyAgentProgram implements AgentProgram {

    private int initialRandomActions = 10;
    private Random random_generator = new Random();

    // Here you can define your variables!
    public int iterationCounter = 1000;
    public MyAgentState state = new MyAgentState();

    private static final int PHASE_CORNERS = 0;
    private static final int PHASE_SNAKING = 1;
    private static final int PHASE_GOINGHOME = 2;
    private int phase = PHASE_CORNERS;

    // A queue of commands to perform over multiple steps.
    private Queue<Action> commandQueue = new LinkedList<Action>();

    // This is set to true as soon as the agent leaves the home cell.
    private boolean hasLeftHome = false;

    /*
    * Moves the Agent to a random start position.
    * Uses percepts to update the Agent position - only the position, other percepts are ignored
    * Returns: a random action
    */
    private Action moveToRandomStartPosition(DynamicPercept percept) {
        initialRandomActions--;
        state.updatePosition(percept);

        int action = random_generator.nextInt(6);

        if (action == 0) {
            state.agent_direction = ((state.agent_direction - 1) % 4);

            if (state.agent_direction < 0)
                state.agent_direction += 4;
            state.agent_last_action = state.ACTION_TURN_LEFT;

            return LIUVacuumEnvironment.ACTION_TURN_LEFT;
        } else if (action == 1) {
            state.agent_direction = ((state.agent_direction + 1) % 4);
            state.agent_last_action = state.ACTION_TURN_RIGHT;

            return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
        }
        state.agent_last_action = state.ACTION_MOVE_FORWARD;
        return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
    }


    @Override
    public Action execute(Percept percept) {

        Action action;
        boolean debug = true;

        if(iterationCounter == 0) {
            return NoOpAction.NO_OP;

        // Start out by moving to a random position.
        } else if(initialRandomActions > 0) {
            return moveToRandomStartPosition((DynamicPercept) percept);

        // Perform some action during the last random step.
        } else if(initialRandomActions == 0) {
            initialRandomActions--;
            state.updatePosition((DynamicPercept) percept);

            action = LIUVacuumEnvironment.ACTION_SUCK;

        // The random moves are performed. Start exploring the world.
        } else {
            iterationCounter--;

            DynamicPercept p = (DynamicPercept) percept;
            Boolean bump = (Boolean) p.getAttribute("bump");
            Boolean home = (Boolean) p.getAttribute("home");
            Boolean dirt = (Boolean) p.getAttribute("dirt");

            state.updatePosition((DynamicPercept) percept);

            // Print any relevant debug information.
            if(debug) {
                System.out.println();
                System.out.println("--------------------------------------------");
                System.out.println("x = " + state.agent_x_position);
                System.out.println("x_prev = " + state.agent_previous_x_position);
                System.out.println();
                System.out.println("y = " + state.agent_y_position);
                System.out.println("y_prev = " + state.agent_previous_y_position);
                System.out.println("dir=" + state.agent_direction);

                state.printWorldDebug();
            }

            // Memorize walls and dirt.
            this.memorizeWorld(bump, dirt);

            // Make sure we eat dirt if we are standing on top of it.
            if (dirt) {
                action = LIUVacuumEnvironment.ACTION_SUCK;

            // If no dirt was found, we keep on moving according the current phase.
            } else {

                System.out.println("Current command queue size: " + this.commandQueue.size());

                // Should we perform some queued command?
                if(this.commandQueue.size() > 0) {
                    action = this.commandQueue.remove();
                    System.out.println("Queued command: " + action);
                } else {
                    switch (this.phase) {

                        // Move right as far as possible, then down as far as possible, then left as far as possible.
                        case PHASE_CORNERS:
                            action = handleCornerPhaseStep(bump);
                            break;

                        // Move one row up, then as far right as possible. Move on row up, then as far left as possible. Repeat.
                        case PHASE_SNAKING:
                            action = handleSnakingPhaseStep(bump);
                            break;

                        // Go home as quick as possible.
                        case PHASE_GOINGHOME:
                            action = handleGoingHomePhaseStep(home);
                            break;

                        // This should never occur.
                        default:
                            action = NoOpAction.NO_OP;
                            break;
                    }
                }
            }
        }

        this.preprocessAction(action);

        return action;
    }

    /***
     * Memorize walls and dirt at the current position.
     * @param bump
     * @param dirt
     */
    private void memorizeWorld(boolean bump, boolean dirt) {
        // Store walls in the agent's memory.
        if (bump) {
            switch (state.agent_direction) {
                case MyAgentState.NORTH:
                    state.updateWorld(state.agent_x_position, state.agent_y_position - 1, state.WALL);
                    break;
                case MyAgentState.EAST:
                    state.updateWorld(state.agent_x_position + 1, state.agent_y_position, state.WALL);
                    break;
                case MyAgentState.SOUTH:
                    state.updateWorld(state.agent_x_position, state.agent_y_position + 1, state.WALL);
                    break;
                case MyAgentState.WEST:
                    state.updateWorld(state.agent_x_position - 1, state.agent_y_position, state.WALL);
                    break;
            }
        }

        // Store dist position in the agent's memory.
        if (dirt)
            state.updateWorld(state.agent_x_position, state.agent_y_position, state.DIRT);
        else
            state.updateWorld(state.agent_x_position, state.agent_y_position, state.CLEAR);
    }

    /***
     * Perform some action-dependant tasks before returning the action.
     * @param action
     */
    private void preprocessAction(Action action) {
        if(action == LIUVacuumEnvironment.ACTION_TURN_RIGHT) {
            state.agent_last_action = state.ACTION_TURN_RIGHT;

            state.agent_direction = state.agent_direction + 1;
            if(state.agent_direction == 4) state.agent_direction = 0;
            else if(state.agent_direction == -1) state.agent_direction = 3;

        } else if(action == LIUVacuumEnvironment.ACTION_TURN_LEFT) {
            state.agent_last_action = state.ACTION_TURN_LEFT;

            state.agent_direction = state.agent_direction - 1;
            if(state.agent_direction == 4) state.agent_direction = 0;
            else if(state.agent_direction == -1) state.agent_direction = 3;

        } else if(action == LIUVacuumEnvironment.ACTION_MOVE_FORWARD) {
            state.agent_last_action = state.ACTION_MOVE_FORWARD;

        } else if(action == LIUVacuumEnvironment.ACTION_SUCK) {
            state.agent_last_action = state.ACTION_SUCK;
        }
    }

    /***
     * Find the two bottom corners.
     * @param bump
     * @return
     */
    private Action handleCornerPhaseStep(boolean bump) {

        System.out.println("Corner Phase");

        // Facing east: move forwards then turn right at any wall.
        if(state.agent_direction == state.EAST) {
            this.hasLeftHome = true;

            if(!bump) {
                return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
            }

            this.commandQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
            return LIUVacuumEnvironment.ACTION_TURN_RIGHT;

        // Facing south: move forwards then turn right at any wall.
        } else if(state.agent_direction == state.SOUTH && this.hasLeftHome) {
            if(bump) {
                return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
            }

            return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;

        // Facing west: move forwards then turn right at any wall and start snaking.
        } else if(state.agent_direction == state.WEST && this.hasLeftHome) {
            if (bump) {
                this.phase = PHASE_SNAKING;
            }

            return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
        }

        // If we are still in the home cell, but not yet facing east. This can definitely be optimized.
        return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
    }

    /***
     * Move upwards and from side to side like a snake.
     * @param bump
     * @return
     */
    private Action handleSnakingPhaseStep(boolean bump) {

        System.out.println("Snake Phase");

        // If the agent failed to move the previous round we know we are done snaking.
        if(state.agent_x_position == state.agent_previous_x_position && state.agent_y_position == state.agent_previous_y_position) {
            this.phase = PHASE_GOINGHOME;
            return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;

        // Turn around 180 deg to the left.
        } else if(state.agent_direction == state.EAST && bump) {
            System.out.println("Left turn!");
            this.commandQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
            this.commandQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
            return LIUVacuumEnvironment.ACTION_TURN_LEFT;

        // Turn around 180 deg to the right.
        } else if(state.agent_direction == state.WEST && bump) {
            System.out.println("Right turn!");
            this.commandQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
            this.commandQueue.add(LIUVacuumEnvironment.ACTION_TURN_RIGHT);
            return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
        }

        // Move forward in any other case.
        System.out.println("Move forward!");
        return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
    }

    /***
     * Move the home cell as quick as possible.
     * @param home
     * @return
     */
    private Action handleGoingHomePhaseStep(boolean home) {

        System.out.println("Going Home Phase");

        return NoOpAction.NO_OP;
    }
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
        super(new MyAgentProgram());
    }
}
