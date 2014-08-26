import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

public class Plotter extends javax.swing.JFrame {
	
	private static final String month[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private static final String year[] = {"2012", "2013"};
	private static final String chartData[] = {"P-V", "Q-V", "P-Q"};
	
	private Map<String, String> buildingMap;
	
	private JPanel mainPanel;
	private ChartPanel chartPanel;
	private JPanel buildingPanel;
	private JPanel dataPanel;
	private JPanel timePanel;
	private JPanel datePanel;
	private JPanel startDatePanel;
	private JPanel endDatePanel;
	
	private JLabel buildingLabel;
	private JLabel chartDataLabel;
	private JLabel startTimeLabel;
	private JLabel endTimeLabel;
	private JLabel startDateLabel;
	private JLabel endDateLabel;
	
	private JTextField buildingTextField;
	
	private JSlider startTimeSlider;
	private JSlider endTimeSlider;
	
	private JComboBox chartDataComboBox;
	private JComboBox startMonthComboBox;
	private JComboBox endMonthComboBox;
	private JComboBox startYearComboBox;
	private JComboBox endYearComboBox;
	
	// initialize properly for valid data
	private String buildingName = "Skiles";
	private String chartDataType = "P-V";
	private String startTime = "10";
	private String endTime = "14";
	private String startMonth = "Sep";
	private String startYear = "2012";
	private String endMonth = "Sep";
	private String endYear = "2013";
	private String xAxis = "V";
	private String yAxis = "P";
	
	private JFreeChart chart;
	private XYDataset dataset;
	
	private MeterData meterData;
	private DBTunnel dbTunnel;
	
	public Plotter(String title) {
		super("Smart Grid Plotter");

		buildingMap = new HashMap();
		buildingMap.put("Skiles", "B002E_M1H1");
		buildingMap.put("Alumni House", "B003E_MH1");
		buildingMap.put("North Avenue", "B005E_M1H1");
		buildingMap.put("Smith Hall", "B006E_M1L1");
		buildingMap.put("Brown Hall", "B006E_M2L1");
		buildingMap.put("Harris Hall", "B006E_M3L1");
		
		dbTunnel = new DBTunnel();
		meterData = dbTunnel.getData(buildingMap.get(buildingName), startTime, endTime, startMonth, endMonth, startYear, endYear);
		
		///////////////////////////////////////////////////////////////////////////////////
		mainPanel = new JPanel();
		setContentPane(mainPanel);
		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
		mainPanel.setPreferredSize(new Dimension(540, 640));
		mainPanel.setBackground(Color.LIGHT_GRAY);
		
		createDataset();
		createChart(dataset);
		chart.setAntiAlias(true);
		chartPanel = new ChartPanel(chart); 
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 350));
		chartPanel.setBorder(new BevelBorder(0));
		mainPanel.add(chartPanel);

		buildingPanel = new JPanel();
		buildingPanel.setPreferredSize(new Dimension(245, 45));
		buildingLabel = new JLabel("Building Name");
		buildingPanel.add(buildingLabel);
		buildingTextField = new JTextField(buildingName);
		buildingTextField.setPreferredSize(new Dimension(100, 25));
		buildingPanel.add(buildingTextField);
		buildingPanel.setBorder(new BevelBorder(0));
		mainPanel.add(buildingPanel);
		
		dataPanel = new JPanel();
		dataPanel.setPreferredSize(new Dimension(245, 45));
		chartDataLabel = new JLabel("Chart Data");
		dataPanel.add(chartDataLabel);
		chartDataComboBox = new JComboBox(chartData);
		chartDataComboBox.setSelectedIndex(0);
		((JLabel)chartDataComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		chartDataComboBox.setPreferredSize(new Dimension(100, 25));
		dataPanel.add(chartDataComboBox);
		dataPanel.setBorder(new BevelBorder(0));
		mainPanel.add(dataPanel);
		
		/////////////////////////////////////////////////////////////////////////
		timePanel = new JPanel();
		timePanel.setPreferredSize(new Dimension(500, 75));
		startTimeLabel = new JLabel("                      Start Time                      ");
		timePanel.add(startTimeLabel);
		endTimeLabel = new JLabel("                      End Time                      ");
		timePanel.add(endTimeLabel);		
		startTimeSlider = new JSlider(JSlider.HORIZONTAL, 0, 24, 10);
		startTimeSlider.setMajorTickSpacing(6);
		startTimeSlider.setMinorTickSpacing(1);
		startTimeSlider.setPaintLabels(true);
		startTimeSlider.setPaintTicks(true);
		timePanel.add(startTimeSlider);
		endTimeSlider = new JSlider(JSlider.HORIZONTAL, 0, 24, 14);
		endTimeSlider.setMajorTickSpacing(6);
		endTimeSlider.setMinorTickSpacing(1);
		endTimeSlider.setPaintLabels(true);
		endTimeSlider.setPaintTicks(true);
		timePanel.add(endTimeSlider);
		timePanel.setBorder(new BevelBorder(0));
		mainPanel.add(timePanel);
		
		////////////////////////////////////////////////////////////////////////
		datePanel = new JPanel();
		startDatePanel = new JPanel();
		endDatePanel = new JPanel();
		datePanel.setPreferredSize(new Dimension(500, 65));
		startDatePanel.setPreferredSize(new Dimension(215, 55));
		endDatePanel.setPreferredSize(new Dimension(215, 55));
		
		startDateLabel = new JLabel("                      Start Date                      ");
		startDatePanel.add(startDateLabel);
		
		startMonthComboBox = new JComboBox(month);
		startMonthComboBox.setSelectedIndex(8);
		((JLabel)startMonthComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		startMonthComboBox.setPreferredSize(new Dimension(85, 25));
		startDatePanel.add(startMonthComboBox);
		
		startYearComboBox = new JComboBox(year);
		startYearComboBox.setSelectedIndex(0);
		((JLabel)startYearComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		startYearComboBox.setPreferredSize(new Dimension(85, 25));
		startDatePanel.add(startYearComboBox);
		
		endDateLabel = new JLabel("                      End Date                      ");
		endDatePanel.add(endDateLabel);
		
		endMonthComboBox = new JComboBox(month);
		endMonthComboBox.setSelectedIndex(8);
		((JLabel)endMonthComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		endMonthComboBox.setPreferredSize(new Dimension(85, 25));
		endDatePanel.add(endMonthComboBox);
		
		endYearComboBox = new JComboBox(year);
		endYearComboBox.setSelectedIndex(1);
		((JLabel)endYearComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		endYearComboBox.setPreferredSize(new Dimension(85, 25));
		endDatePanel.add(endYearComboBox);
		
		datePanel.add(startDatePanel);
		datePanel.add(endDatePanel);
		datePanel.setBorder(new BevelBorder(0));
		mainPanel.add(datePanel);
		
		//////////////////////////////////////////////////////////////////////////////////
		
//		long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK
//				+ AWTEvent.MOUSE_EVENT_MASK + AWTEvent.KEY_EVENT_MASK;
//
//		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
//			public void eventDispatched(AWTEvent e) {
//				getStatus();
//			}
//		}, eventMask);
			
			
		buildingTextField.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                buildingName = buildingTextField.getText();
                updateData();
            }
        });
		
	    chartDataComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                chartDataType = chartDataComboBox.getSelectedItem().toString();
                
                if (chartDataType.equals("P-V")) { xAxis = "V"; yAxis = "P"; }
                if (chartDataType.equals("Q-V")) { xAxis = "V"; yAxis = "Q"; }
                if (chartDataType.equals("P-Q")) { xAxis = "P"; yAxis = "Q"; }
                
//              System.out.println(xAxis+"-"+yAxis);
                updateData();
            }
        });
    	
        startTimeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	startTime = String.valueOf(startTimeSlider.getValue());
            	updateData();
            }
        });
        
        endTimeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	endTime = String.valueOf(endTimeSlider.getValue());
            	updateData();
            }
        });
        
        startMonthComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                startMonth = startMonthComboBox.getSelectedItem().toString();
                updateData();
            }
        });
        
        endMonthComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                endMonth = endMonthComboBox.getSelectedItem().toString();
                updateData();
            }
        });
        
        startYearComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                startYear = startYearComboBox.getSelectedItem().toString();
                updateData();
            }
        });
        
        endYearComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                endYear = endYearComboBox.getSelectedItem().toString();
                updateData();
            }
        });
        
//        chart.addChangeListener(new ChartChangeListener() {
//			@Override
//			public void chartChanged(ChartChangeEvent arg0) {
//				chartPanel.removeAll();
//				chartPanel = new ChartPanel(chart);
//				chartPanel.repaint();
//				chartPanel.revalidate();
//				mainPanel.repaint();
//				mainPanel.revalidate();
//				updateUI();
//				System.out.println("here");
//			}
//		});
        
//        dataset.addActionListener (new ActionListener () {
//            public void actionPerformed(ActionEvent e) {
//                endYear = endYearComboBox.getSelectedItem().toString();
//                updateData();
//            }
//        });
        
        //////////////////////////////////////////////////////////////////////////
	}

	
	private void createDataset() {
		XYSeries series = new XYSeries(chartDataType + " Plot");
		
//		System.out.println(meterData.P.size());
		
		for (int i = 0; i < meterData.P.size(); ++i) {
			if (meterData.P.get(i) < -9.0 || meterData.Q.get(i) < -9.0) continue;
			
			if (chartDataType.equals("P-V")) {
				series.add(meterData.V.get(i), meterData.P.get(i));
			}
			else if (chartDataType.equals("Q-V")) {
				series.add(meterData.V.get(i), meterData.Q.get(i));
			}
			else {
				series.add(meterData.P.get(i), meterData.Q.get(i));
			}
		}
		
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		seriesCollection.addSeries(series);
		
		dataset = seriesCollection;
	}

	private void createChart(XYDataset dataset) {
		// create the chart...
		chart = ChartFactory.createScatterPlot("Smart Grid Chart", // chart title
				xAxis, // x axis label
				yAxis, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, 
				true, // include legend
				true, // tooltips
				false // urls
				);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		if (chartDataType.equals("Q-V")) plot.getRenderer().setSeriesPaint(0, Color.GREEN);
		if (chartDataType.equals("P-Q")) plot.getRenderer().setSeriesPaint(0, Color.YELLOW);
		
		plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.setBackgroundPaint(Color.BLACK);
        
	}
	
	public void getStatus() {
		System.out.println("Building Name: "+buildingName);
		System.out.println("Chart Date: "+chartDataType);
		System.out.println("Starting Time: "+startTime);
		System.out.println("Ending Time: "+endTime);
		System.out.println("Starting Month: "+startMonth);
		System.out.println("Starting Year: "+startYear);
		System.out.println("Ending Month: "+endMonth);
		System.out.println("Ending Year: "+endYear);
	}
	
	private void updateData() {
		meterData = dbTunnel.getData(buildingMap.get(buildingName), startTime, endTime, startMonth, endMonth, startYear, endYear);
		createDataset();
		createChart(dataset);
		chartPanel.setChart(chart);
	}

	public static void main(String[] args)	{	
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		Plotter demo = new Plotter("Smart Grid Data");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		demo.getStatus();
	}
}