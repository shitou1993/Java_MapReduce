package com.fy.bigData.mapreduce.two;

import java.util.HashMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class ProvincePartitioner extends Partitioner<Text, FlowBean>{
	public static HashMap<String, Integer> provinceDict=new HashMap<String,Integer>();
	
	static {
		provinceDict.put("135", 0);
		provinceDict.put("136", 1);
		provinceDict.put("137", 2);
		provinceDict.put("139", 3);
		provinceDict.put("159", 4);
	}

	@Override
	public int getPartition(Text key, FlowBean values, int numPartitions) {
		String prefix=key.toString().substring(0, 3);
		Integer provinceId = provinceDict.get(prefix);
		return (provinceId==null?5:provinceId);
	}

}
