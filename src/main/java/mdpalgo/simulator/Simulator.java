package mdpalgo.simulator;

import mdpalgo.algorithm.Exploration;
import mdpalgo.algorithm.FastestPath;
import mdpalgo.constants.RobotConstant;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.constants.CommConstants;
import mdpalgo.utils.Connection;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

public class Simulator {
    // JFrame
    private static JFrame _appFrame = null; // application JFrame
    private static JPanel _mapCards = null; // JPanel for map views
    private static JPanel _buttons = null; // JPanel for buttons

    private Arena arena;
    private Grid realGrid;
    private Grid currentGrid;
    private Robot robot;
    private Connection connection;

    private int timeLimit;
    private int coverage;
    private int startRow;
    private int startCol;
    private boolean hasWayPoint;
    private int[] wayPoint;
    public static boolean testRobot = false;
    public static boolean testAndroid = false;
    public static boolean testImage = true;
    public static boolean sensorRight = false;
    public static boolean sensorLeft = false;
    public static boolean sensorLong = false;
    public static boolean obsLeft = false;
    public static int[] frontLeftPos = new int[2];
    public static int[] backLeftPos = new int[2];


    public Simulator() {
        robot = new Robot(Grid.START_ROW, Grid.START_COL, RobotConstant.START_DIR);
        currentGrid = new Grid();
        arena = new Arena(currentGrid, robot);
        timeLimit = RobotConstant.TIME_LIMIT;
        coverage = 100;
        startRow = 1;
        startCol = 1;
        hasWayPoint = false;
        wayPoint = new int[2];
    }

    public void simulate() {
        // initialize main frame
        _appFrame = new JFrame();
        _appFrame.setTitle("MDP Simulator");
        _appFrame.setSize(new Dimension(700, 720));
        _appFrame.setResizable(false);

        // Center the main frame in the middle of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        _appFrame.setLocation(dim.width / 2 - _appFrame.getSize().width / 2,
                dim.height / 2 - _appFrame.getSize().height / 2);

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
        initButtonsLayout();
        // Display the application
        _appFrame.setVisible(true);
        _appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        connection = Connection.getConnection();
        connection.openConnection();
        if (testAndroid) {
            while (true) {
                String receivedMsg = connection.receiveMessage();
                if (receivedMsg.equals(CommConstants.EX_START)) {
                    CardLayout cl = ((CardLayout) _mapCards.getLayout());
                    cl.show(_mapCards, "EXPLORATION");
                    System.out.println("exploration");
                    new ExplorationDisplay().execute();
                    break;

                } else if (receivedMsg.equals(CommConstants.FP_START)) {
                    System.out.println("fastestPath");
                    CardLayout cl = ((CardLayout) _mapCards.getLayout());
                    cl.show(_mapCards, "REAL_MAP");
                    new FastestPathDisplay().execute();
                    break;

                } else if (receivedMsg.equals("msg:init pc")) {
                    connection.sendMessage("msg", "pc up");

                } else if (receivedMsg.contains(CommConstants.START_POINT)) {
                    String[] values = receivedMsg.split(":");
                    values = values[1].split(",");
                    startRow = Integer.parseInt(values[1]);
                    startCol = Integer.parseInt(values[2]);

                    robot.setRobotPosition(startRow , startCol);
                    robot.setDirection(RobotConstant.START_DIR);
                    currentGrid = Grid.initCurrentGrid(robot);

                    arena.update(currentGrid, robot);
                    arena.repaint();

                } else if (receivedMsg.contains(CommConstants.WAY_POINT)) {
                    String[] values = receivedMsg.split(":");
                    if (values[0].equals("alg")) {
                        values = values[1].split(",");
                        if (values[0].equals(CommConstants.WAY_POINT)) {
                            hasWayPoint = true;
                            wayPoint[0] = Integer.parseInt(values[1]);
                            wayPoint[1] = Integer.parseInt(values[2]);
                        }
                    }
                }
            }
        }
    }

    private void initMainLayout() {
        CardLayout cl = ((CardLayout) _mapCards.getLayout());

        _mapCards.add(arena, "EXPLORATION");
        cl.show(_mapCards, "EXPLORATION");
    }

    private void initButtonsLayout() {
        _buttons.setLayout(new GridLayout());
        addButtons();
    }

    class ExplorationDisplay extends SwingWorker<Integer, String> {
        protected Integer doInBackground() throws Exception {
            // for android test

            robot.setRobotPosition(startRow , startCol);
            robot.setDirection(RobotConstant.START_DIR);
            currentGrid = Grid.initCurrentGrid(robot);

            arena.update(currentGrid, robot);
            Exploration exploration = new Exploration(currentGrid, realGrid, robot, timeLimit, coverage);

            startRow = 1;
            startCol = 1;
            if (testImage)
            	exploration.exploreImage(arena);
        	else
            	exploration.explore(arena);
            return 111;
        }
    }

    class FastestPathDisplay extends SwingWorker<Integer, String> {
        protected Integer doInBackground() throws Exception {
            robot.setRobotPosition(Grid.START_ROW, Grid.START_COL);
            robot.setDirection(RobotConstant.START_DIR);

            FastestPath fastestPath;
            if (hasWayPoint) {
                // move to way point
                fastestPath = new FastestPath(realGrid, robot, wayPoint[0], wayPoint[1]);
                arena.update(realGrid, robot);
                fastestPath.runFastestPath(arena);
            }

            // move to goal
            fastestPath = new FastestPath(realGrid, robot, Grid.GOAL_ROW, Grid.GOAL_COL);
            arena.update(realGrid, robot);
            fastestPath.runFastestPath(arena);

            hasWayPoint = false;
            return 111;
        }
    }

    private void addButtons() {
        // Load Map Button
        JButton btn_LoadMap = new JButton("Load Map");
        formatButton(btn_LoadMap);

        btn_LoadMap.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Center the main frame in the middle of the screen
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                JDialog loadMapDialog = new JDialog(_appFrame, "Load Map", true);
                loadMapDialog.setSize(400, 500);
                loadMapDialog.setResizable(false);
                loadMapDialog.setResizable(false);
                loadMapDialog.setLocation(dim.width / 2 - _appFrame.getSize().width / 2,
                        dim.height / 2 - _appFrame.getSize().height / 2);
                loadMapDialog.setLayout(new FlowLayout());

                JButton loadMapButton = new JButton("Load");
                java.util.List<String> files = null;

                try {
                    files = IOUtils.readLines(Simulator.class.getClassLoader().getResourceAsStream("maps/"), Charsets.UTF_8);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                final JList fileList = new JList(files.toArray());
                loadMapButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        loadMapDialog.setVisible(false);
                        realGrid = Grid.loadGridFromFile(fileList.getSelectedValue().toString());
                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "REAL_MAP");
                        arena.update(realGrid, robot);
                        arena.repaint();
                    }
                });

                loadMapDialog.add(new JLabel("File Name: "));
                loadMapDialog.add(fileList);
                loadMapDialog.add(loadMapButton);
                loadMapDialog.setVisible(true);
            }
        });
        _buttons.add(btn_LoadMap);

        JButton btn_ChangeSpeed = new JButton("Change Speed");
        this.formatButton(btn_ChangeSpeed);

        btn_ChangeSpeed.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                JDialog changeSpeedDialog = new JDialog(_appFrame, "Change Speed", true);
                changeSpeedDialog.setSize(400, 200);
                changeSpeedDialog.setLayout(new GridLayout(0, 1));
                changeSpeedDialog.setLocation(dim.width / 2 - _appFrame.getSize().width / 2,
                        dim.height / 2 - _appFrame.getSize().height / 2);

                int currentSpeed = (int) (1.0 / (robot.getSpeed() / 1000.0));
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentSpeed, 1, 30, 1));
                JButton changeSpeedButton = new JButton("Change Speed(X steps per second)");
                JLabel currentSpeedDisplay = new JLabel("Current Speed: " + currentSpeed + " steps per second\n");
                currentSpeedDisplay.setVerticalTextPosition(JLabel.BOTTOM);

                changeSpeedButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        changeSpeedDialog.setVisible(false);
                        robot.setSpeed((int)(1.0 / (int) spinner.getValue() * 1000) );
                        changeSpeedDialog.dispose();
                    }
                });

                changeSpeedDialog.add(new JLabel("Speed"));
                changeSpeedDialog.add(spinner);
                changeSpeedDialog.add(currentSpeedDisplay);
                changeSpeedDialog.add(changeSpeedButton);

                changeSpeedDialog.setVisible(true);
            }
        });

        _buttons.add(btn_ChangeSpeed);

        // Set time limit for exploration
        JButton btn_SetTimeLimit = new JButton("Time Limit");
        formatButton(btn_SetTimeLimit);

        btn_SetTimeLimit.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                JDialog changeTimeDialog = new JDialog(_appFrame, "Set Time Limit", true);
                changeTimeDialog.setSize(400, 200);
                changeTimeDialog.setLayout(new GridLayout(0, 1));
                changeTimeDialog.setLocation(dim.width / 2 - _appFrame.getSize().width / 2,
                        dim.height / 2 - _appFrame.getSize().height / 2);

                final JTextField timeTF = new JTextField(15);
                JButton changeTimeButton = new JButton("Set Time Limit(in min:sec)");
                String curTimeLimit = "" + timeLimit;
                JLabel currentTime = new JLabel(String.format("%d min:%d sec",
                        TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(curTimeLimit).longValue()),
                        TimeUnit.MILLISECONDS.toSeconds(Long.valueOf(curTimeLimit).longValue()) - TimeUnit.MINUTES
                                .toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(curTimeLimit).longValue()))));

                currentTime.setVerticalTextPosition(JLabel.BOTTOM);

                changeTimeButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        changeTimeDialog.setVisible(false);
                        String time = timeTF.getText();
                        String[] timeArr = time.split(":");
                        timeLimit = (Integer.parseInt(timeArr[0]) * 60000) + (Integer.parseInt(timeArr[1]) * 1000);
                        changeTimeDialog.dispose();
                    }
                });

                changeTimeDialog.add(new JLabel("Time Limit"));
                changeTimeDialog.add(timeTF);
                changeTimeDialog.add(currentTime);
                changeTimeDialog.add(changeTimeButton);

                changeTimeDialog.setVisible(true);
            }
        });

        _buttons.add(btn_SetTimeLimit);

        // Set coverage for exploration
        JButton btn_SetCoverage = new JButton("Coverage");
        formatButton(btn_SetCoverage);

        btn_SetCoverage.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                JDialog coverageDialog = new JDialog(_appFrame, "Set Coverage Limit", true);
                coverageDialog.setSize(400, 200);
                coverageDialog.setLayout(new GridLayout(0, 1));
                coverageDialog.setLocation(dim.width / 2 - _appFrame.getSize().width / 2,
                        dim.height / 2 - _appFrame.getSize().height / 2);

                final JSlider slider = new JSlider(0, 100, coverage);
                JLabel status = new JLabel("Slide the slider to set coverage limit", JLabel.CENTER);
                slider.setPaintTicks(true);
                Hashtable position = new Hashtable();
                position.put(0, new JLabel("0"));
                position.put(25, new JLabel("25"));
                position.put(50, new JLabel("50"));
                position.put(75, new JLabel("75"));
                position.put(100, new JLabel("100"));
                slider.setLabelTable(position);

                slider.addChangeListener(e1 -> status.setText("Set the coverage to: " + ((JSlider) e1.getSource()).getValue() + "%"));

                JButton changeSpeedButton = new JButton("Set Coverage Limit(%)");
                String curCoverage = String.valueOf(coverage);
                JLabel currentCoverage = new JLabel("Current Coverage Limit: " + curCoverage + "%\n");
                currentCoverage.setVerticalTextPosition(JLabel.BOTTOM);

                changeSpeedButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        coverageDialog.setVisible(false);
                        coverage = slider.getValue();
                        coverageDialog.dispose();
                    }
                });

                coverageDialog.add(new JLabel("Coverage"));
                coverageDialog.add(slider);
                coverageDialog.add(status);
                coverageDialog.add(currentCoverage);
                coverageDialog.add(changeSpeedButton);

                coverageDialog.setVisible(true);
            }
        });

        _buttons.add(btn_SetCoverage);


        // Fastest Path Button
        JButton btn_FastestPath = new JButton("Fastest Path");
        formatButton(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapCards.getLayout());
                cl.show(_mapCards, "REAL_MAP");
                new FastestPathDisplay().execute();
            }
        });
        _buttons.add(btn_FastestPath);


        JButton btn_Exploration = new JButton("Exploration");
        formatButton(btn_Exploration);
        btn_Exploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapCards.getLayout());
                cl.show(_mapCards, "EXPLORATION");
                System.out.println("exploration");
                new ExplorationDisplay().execute();
            }
        });
        _buttons.add(btn_Exploration);

    }

    // JButton Properties
    private void formatButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }
}
