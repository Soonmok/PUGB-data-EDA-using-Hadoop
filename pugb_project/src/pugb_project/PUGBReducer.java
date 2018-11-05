package pugb_project;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * An example of how to output multiple values from a reducer in a single string
 * value via string concatenation. Input is a comma-separated string,
 * interpreted as Key:string Value:integer, float, string (i.e. Key:"A"
 * Value:"2,4.0,This is a test"). Output is Key:string Value:string, and the
 * value contains the int and float doubled in tab separated format in order to
 * make future Hive/Pig import easier because keys and values are also separated
 * by tabs. (i.e. Key:"A" Value: "4\t8.0\tThis is a test").
 */

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
    
    private Double getDistance(Point regKillerPoint, Point regVictimPoint) {
        double delt_x = Math.abs(regKillerPoint.getX() - regVictimPoint.getX());
        double delt_y = Math.abs(regKillerPoint.getY() - regVictimPoint.getY());
        double distance = Math.sqrt(delt_x * delt_x + delt_y * delt_y);
        return distance;
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
            double distance = getDistance(regKillerPoint, regVictimPoint);
            String color;
            if (distance <= 10) {
            	color = "Red";
            }
            else if (10 <= distance && distance <= 20) {
            	color = "blue";
            }
            else {
            	color = "white";
            }
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