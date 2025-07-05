package project.RnW.controller;

import java.util.ArrayList;

import org.bson.Document;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mongodb.client.FindIterable;

import project.RnW.model.Comment;
import project.RnW.model.Report;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceReport;
import project.RnW.service.serviceText;
import project.RnW.service.serviceUser;

public class ControllerUtils {

	
	
	public static ModelAndView setupUserPage(User u, ModelAndView mv) {
		ObjectMapper mp = new ObjectMapper();
		mv.addObject("NAME", u.getName());
		mv.addObject("ID", u.getId());
		ArrayList<String[]> listOfTexts = getAllTextsFromAuthor(u);
		String json_id_title = setupTexts(listOfTexts);
		try {
				if(json_id_title == null)
					return mv.addObject("TEXTS", mp.writeValueAsString(null));
				else
					
						mv.addObject("TEXTS", json_id_title);
					
				return mv;
				}
		catch (JsonProcessingException e) {
			e.printStackTrace();
			}
		
		return mv;
	}



	public static ModelAndView home() {
		ArrayList<String[]> listOfTexts = getAllTexts();
		String json_id_title = setupTexts(listOfTexts);
		ModelAndView mv = new ModelAndView("home");
		if(json_id_title == null)
			mv.addObject("TEXTS", "Nessun testo è ancora stato scritto");
		else
			mv.addObject("TEXTS", json_id_title);
		mv.addObject("ERROR", 0);
		return mv;
		
	}
	
	public static String setupTexts(ArrayList<String[]> listOfTexts) {
		if(!listOfTexts.isEmpty()) {
			String json_id_title = "[";
			for(String[] id_title : listOfTexts) {
				json_id_title += "{\"id\" : \"" + id_title[0] + 
						"\",\"title\": \""
						+ id_title[1]
						+ "\"},";
			}
			json_id_title = 
					json_id_title.substring(0,json_id_title.length() - 1) 
						+ "]";
			return json_id_title;
		}
		else
			return null;
		
	}
	
	public static String[] getMacroSectionsAsString(Text t) {
		ObjectMapper mp = new ObjectMapper();
		mp = JsonMapper.builder()
			    .disable(JsonWriteFeature.ESCAPE_NON_ASCII)
			    .build();
		try {
			String[] macroSections = {
			mp.writeValueAsString(t.getIntro()),
			mp.writeValueAsString(t.getCorpus()),
			mp.writeValueAsString(t.getConclusion())};
			return macroSections;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static ArrayList<ArrayList<String>> formatComments(
			ArrayList<Comment> comments,
			String userId) {
		ArrayList<ArrayList<String>> commentsInfo = 
				new ArrayList<ArrayList<String>>();
		for(Comment comm : comments) {
			ArrayList<String> commentInfo = new ArrayList<String>();
			commentInfo.add(comm.getId().toString());
			commentInfo.add(comm.getUser().getName());
			commentInfo.add(comm.getContent());
			commentsInfo.add(commentInfo);
		}
		return commentsInfo;
	}
	
	public static ArrayList<String[]> getAllTextsFromAuthor(User u) {
		ArrayList<Text> textList = serviceText.getAllTextsFromAuthor(u.getId());
		ArrayList<String[]> infoTextList = new ArrayList<>();
		for (Text t : textList) {
			String[] temp = {t.getId().toString(),
					t.getTitle()};
			infoTextList.add(temp);
		}
		return infoTextList;
	}



	public static ArrayList<String[]> getUsersIds(){
		ArrayList<User> userList = 
				serviceUser.getAllUsers();
		ArrayList<String[]> userIds = new ArrayList<>();
		for (User u : userList) {
			String[] temp = {u.getId().toString(),
					u.getName()};
			userIds.add(temp);
		}
		return userIds;
	}



	public static ArrayList<String[]> getAllTexts() {
		ArrayList<Text> textList = serviceText.getAllTexts();
		ArrayList<String[]> infoTextList = new ArrayList<>();
		for (Text t : textList) {
			String[] temp = {t.getId().toString(),
					t.getTitle()};
			infoTextList.add(temp);
		}
		return infoTextList;
	}



	public static ArrayList<String[]> getReports() {
		ArrayList<Report> reportsList = serviceReport.getReports();
		ArrayList<String[]> infoReportsList = new ArrayList<String[]>();
		for(Report r : reportsList) {
			Text t = r.getReported();
			String[] temp = {t.getId().toString(),
					t.getTitle(),
					t.getAuthor().getId().toString(),
					t.getAuthor().getName(),
					r.getReporter().toString(),
					r.getReporter().getName(),
					r.getContent(),
					r.getId().toString()};
			infoReportsList.add(temp);
		}
		return infoReportsList;
	}
}

