package pugb_visualizer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
public class visualizer {
	
    public visualizer() throws Exception {
        JFrame frame = new JFrame();
        frame.setTitle("Random Pixel Dots On Image with Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        initComponents(frame);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        //create frame and components on EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new visualizer();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    

    private boolean checkBoundary(Point point, ColorPanel cPanel) {
    	if(point.getX() < 0 && point.getY() < 0) {
    		point.x = 0;
            point.y = 0;
        }else if(point.getY() < 0) {
            point.y = 0;
        }else if(point.getX() < 0) {
        	point.x = 0;
        }
    	return true;
    }
    
    private void drawPlayersPoint(Point regKillerPoint, Point regVictimPoint, ColorPanel cPanel, String color) {
    	if(checkBoundary(regKillerPoint, cPanel)) {
        	cPanel.drawDot(regKillerPoint, color);
        }

    }

    private void initComponents(JFrame frame) throws Exception {
        final BufferedImage bi = ImageIO.read(new File("/home/hadoop/Downloads/erangel.jpg"));
        final ColorPanel cPanel = new ColorPanel(bi);
        frame.add(cPanel);
        //create timer to color random pixels
        Timer timer = new Timer(2, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String csvFile = "/home/hadoop/my_output_gun_eran_loc2/part-r-00000";
                String line = "";
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

                    while ((line = br.readLine()) != null) {
                        // use comma as separator
                        String[] data = line.split(",");
                        int killer_x = (int) Double.parseDouble(data[0]);
                        int killer_y = (int) Double.parseDouble(data[1]);
                        int victim_x = (int) Double.parseDouble(data[2]);
                        int victim_y = (int) Double.parseDouble(data[3]);
                        String color = data[4];
                        Point regKillerPoint = new Point(killer_x, killer_y);
                        Point regVictimPoint = new Point(victim_x, victim_y);
                        drawPlayersPoint(regKillerPoint, regVictimPoint, cPanel, color);
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        });
        timer.start();
    }
}
class ColorPanel extends JPanel {
    private BufferedImage bimg;
    private Dimension dims;
    public ColorPanel(BufferedImage image) {
        bimg = image;
        dims = new Dimension(bimg.getWidth(), bimg.getHeight());
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bimg, 0, 0, null);
        try {
			save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        g2d.dispose();

    }
    public void save() throws IOException{
        ImageIO.write(bimg, "JPG", new File("/home/hadoop/Downloads/eran_gunLoc.jpg"));
    }
    
    //this method will allow the changing of image
    public void setBufferedImage(BufferedImage newImg) {
        bimg = newImg;
    }
    
	
    //ths method will colour a pixel white
    public boolean drawDot(Point point, String color) {

        if (point.getX() > dims.getWidth() || point.getY() > dims.getHeight()) {
        	//System.out.println("Can't draw blue dot at: (" + point.getX() + "," + point.getY() + ")");
            return false;
        }
        Color white = new Color(255, 255, 255);
        Color red = new Color(255, 0, 0);
        Color blue = new Color(0, 0, 255);
        Color black = new Color(0, 0, 0);
     
    	if (color.equals("white")) {bimg.setRGB((int)point.getX(), (int)point.getY(), red.getRGB());}//white
    	else if (color.equals("red")) {bimg.setRGB((int)point.getX(), (int)point.getY(), white.getRGB());}
    	else if (color.equals("blue")) {bimg.setRGB((int)point.getX(), (int)point.getY(), blue.getRGB());}

        repaint();
        //System.out.println("Drew blue dot at: (" + point.getX() + "," + point.getY() + ")");
        return true;
    }
    

    @Override
    public Dimension getPreferredSize() {
        return dims;
    }
}
