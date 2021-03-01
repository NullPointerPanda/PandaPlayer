package application;

import java.io.File;
import java.util.ArrayList;


public class Playlist {
	
	private String title;
	private ArrayList<File> list = new ArrayList<File>();
	
	public Playlist(String title, ArrayList<File> list) {
		this.title = title;
		this.list = list;
	}
	
	

	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public ArrayList<File> getList() {
		return list;
	}



	public void setList(ArrayList<File> list) {
		this.list = list;
	}



	@Override
	public String toString() {
		return title;
	}
	
	
}
