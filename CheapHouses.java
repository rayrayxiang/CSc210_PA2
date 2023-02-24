import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.*;

/**
 * This class creates a graphical user interface. The bulk of the window is 
 * whitespace where points will be plotted. The user can enter the name of a 
 * csv file and a maximum price. Once they press the button, the relative 
 * positions of houses below the specified price point will appear. In 
 * addition, the class creates a file containing specific prices of the plotted 
 * houses.
 * 
 * @author rayra
 */
public class CheapHouses {
	
	static HashMap<String, String[]> houses_info = new HashMap<String, String[]>();
	static ArrayList<Double[]> points = new ArrayList<Double[]>();

	public static void main(String[] args) {
		make_GUI();
	}
	
	/**
	 * This method reads the contents of a csv file containing house info. The 
	 * file should be formatted the same as houses.csv. After filtering the 
	 * houses below a certain price point. First, it maps addresses to arrays 
	 * containing price, longitude, and latitude. Then, it stores longitude and 
	 * latitude values as doubles in an ArrayList. Finally, addresses and their 
	 * prices are printed into a file called "cheaphouses.txt".
	 * 
	 * @param file_name names of the csv file with house info
	 * @param max_price max price
	 * @throws FileNotFoundException
	 */
	public static void read_file(String file_name, double max_price) 
			throws FileNotFoundException {
		houses_info.clear();
		points.clear();
		PrintWriter cheap_houses = new PrintWriter("cheaphouses.txt");
		
		Scanner file = new Scanner(new File(file_name));
		String[] cur_line = file.nextLine().split(",");
		while (file.hasNext()) {
			cur_line = file.nextLine().split(",");
			double cur_price = Double.valueOf(cur_line[9]);
			if (cur_price < max_price) {
				// hashmap
				String address = cur_line[0];
				String[] info = Arrays.copyOfRange(cur_line, 9, 12);
				houses_info.put(address, info);
				
				// arraylist
				Double lat = Double.valueOf(info[1]);
				Double lon = Double.valueOf(info[2]);
				Double[] point = {-lat, lon};
				points.add(point);
				
				// output file
				cheap_houses.println(address + " -> $" + info[0]);
			}
		}
		
		cheap_houses.close();
		file.close();
	}
	
	/**
	 * Draws the user interface.
	 */
	public static void make_GUI() {
		JFrame main_frame = new JFrame("Home Price Distribution");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.setSize(600, 600);
		
			JPanel main_panel = new JPanel(null);
			
				JPanel plot_panel = new GPanel();
				plot_panel.setLocation(0, 0);
				plot_panel.setSize(600, 500);
				plot_panel.setBorder(BorderFactory.createLineBorder(Color.black));
				main_panel.add(plot_panel);
				
				JPanel widgets_panel = new JPanel();
				widgets_panel.setLocation(0, 500);
				widgets_panel.setSize(600, 100);
				widgets_panel.setBorder(BorderFactory.createLineBorder(Color.black));
				
					JLabel file_label = new JLabel("File:", JLabel.RIGHT);
					widgets_panel.add(file_label);
					
					JTextField file_field = new JTextField("houses.csv");
					file_field.setColumns(10);
					widgets_panel.add(file_field);
					
					JLabel price_label = new JLabel("Price:", JLabel.RIGHT);
					widgets_panel.add(price_label);
					
					JTextField price_field = new JTextField();
					price_field.setColumns(10);
					widgets_panel.add(price_field);
					
					JButton plot_button = new JButton("plot houses");
					plot_button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String file_name = file_field.getText();
							String max_price = price_field.getText();
							// make sure fields are not empty
							if (!file_name.equals("") && !max_price.equals("")) {
								try {
									read_file(file_name, Double.valueOf(max_price));
									plot_panel.repaint();
								}
								catch(FileNotFoundException e1) {
									System.out.println("The file does not exist!");
								}
								catch(NumberFormatException e2) {
									System.out.println("Invalid price! Numbers only please.");
								}
							}
						}
					});
					widgets_panel.add(plot_button);
					
			main_panel.add(widgets_panel);
		
		main_frame.add(main_panel);
		main_frame.setVisible(true);
	}
	
	/**
	 * This class allows us to draw on the plot panel.
	 * 
	 * @author rayra
	 */
	private static class GPanel extends JPanel {
		
		/**
		 * Draws the background and then the points that represent houses.
		 */
		public void paintComponent(Graphics g) {
			int width = getSize().width; int height = getSize().height;
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			
			// group latitude values and longitude values
			ArrayList<Double> lats = new ArrayList<Double>(); ArrayList<Double> lons = new ArrayList<Double>();
			for (int i = 0; i < points.size(); i++) {
				lats.add(points.get(i)[0]);
				lons.add(points.get(i)[1]);
			}
			
			// make sure we have points so that we can call max and min
			if (!lats.isEmpty()) {
				double max_lat = Collections.max(lats); double min_lat = Collections.min(lats);
				double max_lon = Collections.max(lons); double min_lon = Collections.min(lons);
				// loop to plot every point
				for (Double[] point: points) {
					double scaled_lat = (point[0] - min_lat)/(max_lat - min_lat) * 500;
					double scaled_lon = (point[1] - min_lon)/(max_lon - min_lon) * 600;
					g.setColor(Color.GREEN);
					g.fillOval((int)scaled_lon, (int)scaled_lat, 5, 5);
				}
			}
		}
		
	}

}