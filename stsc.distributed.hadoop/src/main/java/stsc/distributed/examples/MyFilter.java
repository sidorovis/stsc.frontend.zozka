package stsc.distributed.examples;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

class MyFilter extends Reducer<Text, LongWritable, Text, LongWritable> {

	@Override
	protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
		LongWritable lw = values.iterator().next();
		if (lw.get() > 2) {
			System.out.println(" ---- " + key);
			context.write(key, lw);
		}
	}
}
