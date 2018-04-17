package com.fy.bigData.mapreduce.one;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FlowCount{
	static class FlowCountMapper extends Mapper<LongWritable, Text, Text, FlowBean>{
		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, FlowBean>.Context context)
				throws IOException, InterruptedException {
			String line=value.toString();
			String[] fields=line.split("\t");
			String phoneNub=fields[1];
			Long upFlow=Long.parseLong(fields[fields.length-3]);
			Long dFlow=Long.parseLong(fields[fields.length-2]);
			context.write(new Text(phoneNub), new FlowBean(upFlow,dFlow));
		}
	}
	
	static class FlowCountReducer extends Reducer<Text, FlowBean, Text, FlowBean>{
		@Override
		protected void reduce(Text key, Iterable<FlowBean> values, Reducer<Text, FlowBean, Text, FlowBean>.Context context)
				throws IOException, InterruptedException {
			long upFlow=0;
			long dFlow=0;
			for (FlowBean flowBean : values) {
				upFlow+=flowBean.getUpFlow();
				dFlow+=flowBean.getdFlow();
			}
			FlowBean resultBean=new FlowBean(upFlow, dFlow);
			context.write(key, resultBean);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		if(args==null||args.length==0) {
			args=new String[2];
			args[0]="/root/桌面/flow.log";
			args[1]="/root/桌面/out";
		}
		Configuration conf = new Configuration();
//		conf.set("mapreduce.job.jar","/root/桌面/word.jar");  
//        conf.set("fs.defaultFS", "hdfs://fy:9000");          
//        conf.set("mapreduce.framework.name", "yarn");               
//        conf.set("yarn.resourcemanager.hostname", "fy");
		Job job=Job.getInstance(conf);
		job.setJarByClass(FlowCount.class);
		
		job.setMapperClass(FlowCountMapper.class);
		job.setReducerClass(FlowCountReducer.class);
		
		job.setPartitionerClass(ProvincePartitioner.class);
		job.setNumReduceTasks(5);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FlowBean.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		boolean res=job.waitForCompletion(true);
		System.exit(res?0:1);
				
		
	}

}
