package com.hb.tool;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;

public class Commonutil {

	
		public static void writeData(String path, String msg, Context context) {
			try {
				OutputStream outputStream = context.openFileOutput(path, Context.BIND_WAIVE_PRIORITY);
				BufferedOutputStream bos = new BufferedOutputStream(outputStream);
				bos.write(msg.getBytes());
				bos.close();
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		
		public static String getDataFile(String path, Context context) {
			String json = "";
			try {
				InputStream is = context.openFileInput(path);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuffer result = new StringBuffer();
				while (reader.ready()) {
					result.append((char) reader.read());
				}
				json = result.toString();
				reader.close();
				is.close();
			} catch (Exception e) {
			}
			return json;
		}
}
