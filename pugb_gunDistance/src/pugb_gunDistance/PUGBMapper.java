package pugb_gunDistance;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class PUGBMapper extends
		Mapper<LongWritable, Text, Text, Text> {

	private Text textKey = new Text();
	private Text Value = new Text();
	
	private Point2D getKillerPoint(String[] data) {
	    String killer_x = data[10];
	    String killer_y = data[11];
	    Point2D killer_p = new Point2D.Double(Double.valueOf(killer_x), Double.valueOf(killer_y));
	    return killer_p;
	}
	
	private Point2D getVictimPoint(String[] data) {
	    String victim_x = data[3];
	    String victim_y = data[4];
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
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		String line = value.toString();
		String[] field = line.split(",");
        
		if (field.length == 12 && field[3] != null && !field[3].isEmpty()
				&& !field[10].isEmpty() && !field[0].isEmpty()
				&& !field[3].equals("0") && !field[10].equals("0")) {
			String gun_id = field[0];
			String map_id = field[5];
			Point2D victim_p = getVictimPoint(field);
	        Point2D killer_p = getKillerPoint(field);
	        int x = (int) Math.round(victim_p.getX() *4096/800000);
	        int y = (int) Math.round(victim_p.getY() *4096/800000);
	        Point regVictimPoint = new Point(x, y);
	        x = (int) Math.round(killer_p.getX() *4096/800000);
	        y = (int) Math.round(killer_p.getY() *4096/800000);
	        Point regKillerPoint = new Point(x, y);
	        double distance = getDistance(regKillerPoint, regVictimPoint);

	        String distanceStr = String.valueOf(distance);
	        
			textKey.set(gun_id);
			Value.set(distanceStr);
			if (map_id.equals("MIRAMAR")) {
				context.write(textKey, Value);
			}
		}
	}
}