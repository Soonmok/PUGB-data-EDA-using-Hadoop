package pugb_gunLocation;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * An example of how to pass multiple values from a mapper to a reducer in a
 * single string value via string concatenation. Input is a comma-separated
 * string, interpreted as Key:string Value:integer, float, string (i.e.
 * "A,1,2.0,This is a test"). Output is Key:string Value:string, and the value
 * contains the integer and float doubled (i.e. Key:"A" Value:
 * "2,4.0,This is a test").
 */

public class PUGBMapper extends
		Mapper<LongWritable, Text, Text, Text> {

	Text textKey = new Text();
	Text textValue = new Text();

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		String line = value.toString();
		String[] field = line.split(",");
		if (field.length == 12 && field[3] != null && !field[3].isEmpty()
				&& field[10] != null && !field[10].isEmpty()) {
			String map_id = field[5];
			String info = field[3] + "," +  field[4] + "," + field[10] + "," + field[11] + ","
					+ field[0];

			textKey.set(map_id);
			textValue.set(info);
			if (map_id.equals("ERANGEL")) {
				context.write(textKey, textValue);
			}
		}
	}
}