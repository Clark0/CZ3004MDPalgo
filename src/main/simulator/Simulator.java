package simulator;

import constants.Direction;
import models.Grid;
import models.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Simulator {
    private static JFrame _appFrame = null;         // application JFrame

    private static JPanel _mapCards = null;         // JPanel for map views
    private static JPanel _buttons = null;          // JPanel for buttons

    private static Robot robot;

    private static Grid realMap = null;              // real map
    private static Grid exploredMap = null;          // exploration map
    private static Arena arena = null;

    private static int timeLimit = 3600;            // time limit
    private static int coverageLimit = 300;         // coverage limit

    private static final boolean realRun = false;

    public void simulate() {
        robot = new Robot(Grid.START_ROW, Grid.START_COL, Direction.Sorth);
        exploredMap = new Grid();
        arena = new Arena(exploredMap, robot);

        displayEverything();

    }

    private static void displayEverything() {
        // Initialise main frame for display
        _appFrame = new JFrame();
        _appFrame.setTitle("MDP Simulator");
        _appFrame.setSize(new Dimension(690, 700));
        _appFrame.setResizable(false);

        // Center the main frame in the middle of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        _appFrame.setLocation(dim.width / 2 - _appFrame.getSize().width / 2, dim.height / 2 - _appFrame.getSize().height / 2);

        // Create the CardLayout for storing the different maps
        _mapCards = new JPanel(new CardLayout());

        // Create the JPanel for the buttons
        _buttons = new JPanel();

        // Add _mapCards & _buttons to the main frame's content pane
        Container contentPane = _appFrame.getContentPane();
        contentPane.add(_mapCards, BorderLayout.CENTER);
        contentPane.add(_buttons, BorderLayout.PAGE_END);

        // Initialize the main map view
        initMainLayout();

        // Initialize the buttons
        initButtonsLayout();

        // Display the application
        _appFrame.setVisible(true);
        _appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static void initMainLayout() {
        // TODO
        if (!realRun) {
            _mapCards.add(arena, "REAL_MAP");
        }
        _mapCards.add(arena, "EXPLORATION");

        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        if (!realRun) {
            cl.show(_mapCards, "REAL_MAP");
        } else {
            cl.show(_mapCards, "EXPLORATION");
        }
    }

    private static void initButtonsLayout() {
        _buttons.setLayout(new GridLayout());
        addButtons();
    }

    private static void formatButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }

    private static void addButtons() {
        if (!realRun) {
            // Load Map Button
            JButton btn_LoadMap = new JButton("Load Map");
            formatButton(btn_LoadMap);
            btn_LoadMap.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    JDialog loadMapDialog = new JDialog(_appFrame, "Load Map", true);
                    loadMapDialog.setSize(400, 60);
                    loadMapDialog.setLayout(new FlowLayout());

                    final JTextField loadTF = new JTextField(15);
                    JButton loadMapButton = new JButton("Load");

                    loadMapButton.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            loadMapDialog.setVisible(false);
                            realMap = Grid.loadGridFromFile(loadTF.getText());
                            CardLayout cl = ((CardLayout) _mapCards.getLayout());
                            cl.show(_mapCards, "REAL_MAP");
                            arena.repaint();
                        }
                    });

                    loadMapDialog.add(new JLabel("File Name: "));
                    loadMapDialog.add(loadTF);
                    loadMapDialog.add(loadMapButton);
                    loadMapDialog.setVisible(true);
                }
            });
            _buttons.add(btn_LoadMap);
        }

        // FastestPath Class for Multithreading
        class FastestPath extends SwingWorker<Integer, String> {
            @Override
            protected Integer doInBackground() {
                robot.setRobotPosition(Grid.START_ROW, Grid.START_COL);
                arena.repaint();
//
//                if (realRun) {
//                    while (true) {
//                        System.out.println("Waiting for FP_START...");
//                        String msg = comm.recvMsg();
//                        if (msg.equals(CommMgr.FP_START)) break;
//                    }
//                }
//
//                FastestPathAlgo fastestPath;
//                fastestPath = new FastestPathAlgo(exploredMap, bot);
//
//                fastestPath.runFastestPath(RobotConstants.GOAL_ROW, RobotConstants.GOAL_COL);
//
                return 222;
            }
        }

        // Exploration Class for Multithreading
        class Exploration extends SwingWorker<Integer, String> {
            @Override
            protected Integer doInBackground() throws Exception {
//                int row, col;
//
//                row = RobotConstants.START_ROW;
//                col = RobotConstants.START_COL;
//
//                bot.setRobotPos(row, col);
//                exploredMap.repaint();
//
//                ExplorationAlgo exploration;
//                exploration = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);
//
//                if (realRun) {
//                    CommMgr.getCommMgr().sendMsg(null, CommMgr.BOT_START);
//                }
//
//                exploration.runExploration();
//                generateMapDescriptor(exploredMap);
//
//                if (realRun) {
//                    new FastestPath().execute();
//                }
//
                return 111;
            }
        }

        // Exploration Button
        JButton btn_Exploration = new JButton("Exploration");
        formatButton(btn_Exploration);
        btn_Exploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapCards.getLayout());
                cl.show(_mapCards, "EXPLORATION");
                new Exploration().execute();
            }
        });
        _buttons.add(btn_Exploration);

        // Fastest Path Button
        JButton btn_FastestPath = new JButton("Fastest Path");
        formatButton(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapCards.getLayout());
                cl.show(_mapCards, "EXPLORATION");
                new FastestPath().execute();
            }
        });
        _buttons.add(btn_FastestPath);


        // TimeExploration Class for Multithreading
        class TimeExploration extends SwingWorker<Integer, String> {
            @Override
            protected Integer doInBackground() throws Exception {
                robot.setRobotPosition(Grid.START_ROW, Grid.START_COL);
                arena.repaint();

//                ExplorationAlgo timeExplo = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);
//                timeExplo.runExploration();
//
//                generateMapDescriptor(exploredMap);

                return 333;
            }
        }

        // Time-limited Exploration Button
        JButton btn_TimeExploration = new JButton("Time-Limited");
        formatButton(btn_TimeExploration);
        btn_TimeExploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog timeExploDialog = new JDialog(_appFrame, "Time-Limited Exploration", true);
                timeExploDialog.setSize(400, 60);
                timeExploDialog.setLayout(new FlowLayout());
                final JTextField timeTF = new JTextField(5);
                JButton timeSaveButton = new JButton("Run");

                timeSaveButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        timeExploDialog.setVisible(false);
                        String time = timeTF.getText();
                        String[] timeArr = time.split(":");
                        timeLimit = (Integer.parseInt(timeArr[0]) * 60) + Integer.parseInt(timeArr[1]);
                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "EXPLORATION");
                        new TimeExploration().execute();
                    }
                });

                timeExploDialog.add(new JLabel("Time Limit (in MM:SS): "));
                timeExploDialog.add(timeTF);
                timeExploDialog.add(timeSaveButton);
                timeExploDialog.setVisible(true);
            }
        });
        _buttons.add(btn_TimeExploration);


        // CoverageExploration Class for Multithreading
        class CoverageExploration extends SwingWorker<Integer, String> {
            @Override
            protected Integer doInBackground() throws Exception {
                robot.setRobotPosition(Grid.START_ROW, Grid.START_COL);
                arena.repaint();
//
//                ExplorationAlgo coverageExplo = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);
//                coverageExplo.runExploration();
//
//                generateMapDescriptor(exploredMap);

                return 444;
            }
        }

        // Coverage-limited Exploration Button
        JButton btn_CoverageExploration = new JButton("Coverage-Limited");
        formatButton(btn_CoverageExploration);
        btn_CoverageExploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog coverageExploDialog = new JDialog(_appFrame, "Coverage-Limited Exploration", true);
                coverageExploDialog.setSize(400, 60);
                coverageExploDialog.setLayout(new FlowLayout());
                final JTextField coverageTF = new JTextField(5);
                JButton coverageSaveButton = new JButton("Run");

                coverageSaveButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        coverageExploDialog.setVisible(false);
                        coverageLimit = (int) ((Integer.parseInt(coverageTF.getText())) * Grid.GRID_SIZE / 100.0);
                        new CoverageExploration().execute();
                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "EXPLORATION");
                    }
                });

                coverageExploDialog.add(new JLabel("Coverage Limit (% of maze): "));
                coverageExploDialog.add(coverageTF);
                coverageExploDialog.add(coverageSaveButton);
                coverageExploDialog.setVisible(true);
            }
        });
        _buttons.add(btn_CoverageExploration);
    }
}