package pugb_gunLocation;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PUGBReducer extends
		Reducer<Text, Text, Text, Text> {

	Text textValue = new Text();
	FloatWritable floatWritable = new FloatWritable();
	
	private Point2D getKillerPoint(String[] data) {
        String killer_x = data[0];
        String killer_y = data[1];
        Point2D killer_p = new Point2D.Double(Double.valueOf(killer_x), Double.valueOf(killer_y));
        return killer_p;
    }
    
    private Point2D getVictimPoint(String[] data) {
        String victim_x = data[2];
        String victim_y = data[3];
        Point2D victim_p = new Point2D.Double(Double.valueOf(victim_x), Double.valueOf(victim_y));
        return victim_p;
    }
    

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		for (Text value : values) {

			String line = value.toString();
			String[] data = line.split(",");
			Point2D victim_p = getVictimPoint(data);
            Point2D killer_p = getKillerPoint(data);
            int x = (int) Math.round(victim_p.getX() *4096/800000);
            int y = (int) Math.round(victim_p.getY() *4096/800000);
            Point regVictimPoint = new Point(x, y);
            x = (int) Math.round(killer_p.getX() *4096/800000);
            y = (int) Math.round(killer_p.getY() *4096/800000);
            Point regKillerPoint = new Point(x, y);
            String gun = data[4];
            String color;
            if (gun.equals("AKM") || gun.equals("M16A4") || gun.equals("M416") || gun.equals("SCAR-L")
            		|| gun.equals("Vector") || gun.equals("UMP9")) {
            	color = "blue";
            }
            else if (gun.equals("S12K") || gun.equals("S1897") || gun.equals("S686")) {
            	color = "red";
            }
            else if (gun.equals("AWM") || gun.equals("Kar98k")  || gun.equals("SKS")
            		|| gun.equals("VSS") || gun.equals("Win94")){
            	color = "white";
            }
            else color = "none";
            String locInfo = String.valueOf(regVictimPoint.getX()) + "," 
            		+ String.valueOf(regVictimPoint.getY()) + "," 
            		+ String.valueOf(regKillerPoint.getX()) + "," 
            		+ String.valueOf(regKillerPoint.getY()) + ","
            		+ color;
            textValue.set(locInfo);
            key.set("");
			context.write(key, textValue);
		}
	}
}