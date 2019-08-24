package simulator;

import constants.Direction;
import models.Grid;
import models.Robot;

import javax.swing.*;

import static utils.GridDescriptor.loadGrid;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Simulator {
    private static JFrame frame = null;

    private static JPanel pMap = null;
    private static JPanel pButtons = null;

    private static Robot robot;

    private static Arena realMap = null;
    private static Arena exploredMap = null;
    
    //private static int timeLimit = 3600;
    //private static int coverageLimit = 300;

    private static final boolean simulatedRun = false;
    
    public static void main(String[] args) {
    	
        robot = new Robot(Grid.START_ROW, Grid.START_COL, Direction.North);
        realMap = new Arena(robot);
        exploredMap = new Arena(robot);

        displayMap();
        
    }

    private static void displayMap() {
        
    	frame = new JFrame();
    	frame.setTitle("MDP Group 5 Simulator");
    	frame.setSize(new Dimension(700, 720));
    	frame.setResizable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        pMap = new JPanel(new CardLayout());

        pButtons = new JPanel();

        Container contentPane = frame.getContentPane();
        contentPane.add(pMap, BorderLayout.CENTER);
        contentPane.add(pButtons, BorderLayout.PAGE_END);

        mapLayout();

        buttonsLayout();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
    }

    private static void mapLayout() {
        
        if (!simulatedRun) {
        	
            pMap.add(realMap, "REAL_MAP");
        
        }
        pMap.add(exploredMap, "EXPLORATION");

        CardLayout cl = ((CardLayout) pMap.getLayout());
        
        if (!simulatedRun) {
        
        	cl.show(pMap, "REAL_MAP");
        
        } else {
        
        	cl.show(pMap, "EXPLORATION");
        
        }
    }

    private static void buttonsLayout() {
    	
    	pButtons.setLayout(new GridLayout());
        addButtons();
        
    }

    private static void buttonStyle(JButton btn) {
    	
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        
    }

    private static void addButtons() {
    	
        if (!simulatedRun) {
            // Load Map Button
            JButton btn_LoadMap = new JButton("Load Map");
            buttonStyle(btn_LoadMap);
            btn_LoadMap.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    JDialog loadMapDialog = new JDialog(frame, "Load Map", true);
                    loadMapDialog.setSize(400, 60);
                    loadMapDialog.setLayout(new FlowLayout());

                    final JTextField loadTF = new JTextField(15);
                    JButton loadMapButton = new JButton("Load");

                    loadMapButton.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            loadMapDialog.setVisible(false);
                            loadGrid(realMap, loadTF.getText());
                            CardLayout cl = ((CardLayout) pMap.getLayout());
                            cl.show(pMap, "REAL_MAP");
                            realMap.repaint();
                        }
                    });

                    loadMapDialog.add(new JLabel("File Name: "));
                    loadMapDialog.add(loadTF);
                    loadMapDialog.add(loadMapButton);
                    loadMapDialog.setVisible(true);
                }
            });
            pButtons.add(btn_LoadMap);
        }
/*
        // FastestPath Class for Multithreading
        class FastestPath extends SwingWorker<Integer, String> {
            @Override
            protected Integer doInBackground() {
                robot.setRobotPosition(Grid.START_ROW, Grid.START_COL);
                exploredMap.repaint();
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
*/
        // Exploration Button
        JButton btn_Exploration = new JButton("Exploration");
        buttonStyle(btn_Exploration);
        btn_Exploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) pMap.getLayout());
                cl.show(pMap, "EXPLORATION");
                //new Exploration().execute();
            }
        });
        pButtons.add(btn_Exploration);

        // Fastest Path Button
        JButton btn_FastestPath = new JButton("Fastest Path");
        buttonStyle(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) pMap.getLayout());
                cl.show(pMap, "EXPLORATION");
                //new FastestPath().execute();
            }
        });
        pButtons.add(btn_FastestPath);


        // TimeExploration Class for Multithreading
        class TimeExploration extends SwingWorker<Integer, String> {
            @Override
            protected Integer doInBackground() throws Exception {
                robot.setRobotPosition(Grid.START_ROW, Grid.START_COL);
                exploredMap.repaint();

//                ExplorationAlgo timeExplo = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);
//                timeExplo.runExploration();
//
//                generateMapDescriptor(exploredMap);

                return 333;
            }
        }

        // Time-limited Exploration Button
        JButton btn_TimeExploration = new JButton("Time-Limited");
        buttonStyle(btn_TimeExploration);
        btn_TimeExploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog timeExploDialog = new JDialog(frame, "Time-Limited Exploration", true);
                timeExploDialog.setSize(400, 60);
                timeExploDialog.setLayout(new FlowLayout());
                final JTextField timeTF = new JTextField(5);
                JButton timeSaveButton = new JButton("Run");

                timeSaveButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        timeExploDialog.setVisible(false);
                        String time = timeTF.getText();
                        String[] timeArr = time.split(":");
                        //timeLimit = (Integer.parseInt(timeArr[0]) * 60) + Integer.parseInt(timeArr[1]);
                        CardLayout cl = ((CardLayout) pMap.getLayout());
                        cl.show(pMap, "EXPLORATION");
                        new TimeExploration().execute();
                    }
                });

                timeExploDialog.add(new JLabel("Time Limit (in MM:SS): "));
                timeExploDialog.add(timeTF);
                timeExploDialog.add(timeSaveButton);
                timeExploDialog.setVisible(true);
            }
        });
        pButtons.add(btn_TimeExploration);


        // CoverageExploration Class for Multithreading
        class CoverageExploration extends SwingWorker<Integer, String> {
            @Override
            protected Integer doInBackground() throws Exception {
                robot.setRobotPosition(Grid.START_ROW, Grid.START_COL);
                exploredMap.repaint();
//
//                ExplorationAlgo coverageExplo = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);
//                coverageExplo.runExploration();
//
//                generateMapDescriptor(exploredMap);

                return 444;
            }
        }

        // Coverage-limited Exploration Button
        JButton btn_CoverageExploration = new JButton("Coverage");
        buttonStyle(btn_CoverageExploration);
        btn_CoverageExploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog coverageExploDialog = new JDialog(frame, "Coverage-Limited Exploration", true);
                coverageExploDialog.setSize(400, 60);
                coverageExploDialog.setLayout(new FlowLayout());
                final JTextField coverageTF = new JTextField(5);
                JButton coverageSaveButton = new JButton("Run");

                coverageSaveButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        coverageExploDialog.setVisible(false);
                        //coverageLimit = (int) ((Integer.parseInt(coverageTF.getText())) * Grid.GRID_SIZE / 100.0);
                        new CoverageExploration().execute();
                        CardLayout cl = ((CardLayout) pMap.getLayout());
                        cl.show(pMap, "EXPLORATION");
                    }
                });

                coverageExploDialog.add(new JLabel("Coverage Limit (% of maze): "));
                coverageExploDialog.add(coverageTF);
                coverageExploDialog.add(coverageSaveButton);
                coverageExploDialog.setVisible(true);
            }
        });
        pButtons.add(btn_CoverageExploration);
    }
}
