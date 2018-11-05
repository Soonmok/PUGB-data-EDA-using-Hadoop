package pugb_gunDistance;

import java.io.IOException;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PUGBReducer extends
		Reducer<Text, Text, Text, Text> {

	Text avgDistance = new Text();

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
        double sum = 0;
        int count = 0;
        for (Text value : values) {
        	String valueStr = value.toString();
            sum += Double.parseDouble(valueStr);
            count++;
        }
        Double avg = sum / count;
        String info = avg.toString() + "," + String.valueOf(count);
        avgDistance.set(avg.toString());
        context.write(key, avgDistance);
	}
}
