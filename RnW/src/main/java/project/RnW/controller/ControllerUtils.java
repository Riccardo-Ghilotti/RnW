package project.RnW.controller;

import java.util.ArrayList;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mongodb.MongoException;

import project.RnW.model.Comment;
import project.RnW.model.Report;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceReport;
import project.RnW.service.serviceText;
import project.RnW.service.serviceUser;

public class ControllerUtils {

	//This class is formed by useful methods for controllers.
	//These methods contain formatting logic, or are a part of the logic used by
	//multiple methods.
	
	
	public static ModelAndView setupUserPage(User u, ModelAndView mv, boolean isOwner) 
			throws AccountNotFoundException, MongoException {
		ObjectMapper mp = new ObjectMapper();
		mv.addObject("NAME", u.getName());
		mv.addObject("ID", u.getId());
		ArrayList<String[]> listOfTexts = getAllTextsFromAuthor(u, isOwner);
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



	public static ModelAndView setupHome(ModelAndView mv) throws MongoException {
		ArrayList<String[]> listOfTexts = getAllTexts();
		String json_id_title = setupTexts(listOfTexts);
		if(json_id_title == null)
			mv.addObject("TEXTS", "null");
		else
			mv.addObject("TEXTS", json_id_title);
		return mv;
		
	}
	
	//This method returns a json string containing id and title of the texts
	//in listOfTexts.
	//listOfTexts is a list of strings arrays "id_title" where:
	// - id_title[0] --> contains the id of a text.
	// - id_title[1] --> contains the title of the text referenced by id_title[0].
	public static String setupTexts(ArrayList<String[]> listOfTexts) {
		if(!listOfTexts.isEmpty()) {
			String json_id_title = "[";
			for(String[] id_title : listOfTexts) {
				json_id_title += "{\"id\": \"" + id_title[0] + 
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
	
	//The ArrayList that contains the formatted Comments is formed like this:
	//commentInfo[0] --> id of the comment.
	//commentInfo[1] --> name of the commenter.
	//commentInfo[2] --> content of the comment.
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
	
	public static ArrayList<String[]> getAllTextsFromAuthor(User u, boolean isOwner)
			throws AccountNotFoundException, MongoException {
		ArrayList<Text> textList = serviceText.getAllTextsFromAuthor(u.getId(), isOwner);
		ArrayList<String[]> infoTextList = new ArrayList<>();
		for (Text t : textList) {
			String[] temp = {t.getId().toString(),
					t.getTitle()};
			infoTextList.add(temp);
		}
		return infoTextList;
	}


	//This method returns a list of userIds and respective names.
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


	//this method returns all non-private texts that are present in the database
	//in the form of textId and respective title.
	public static ArrayList<String[]> getAllTexts() throws MongoException {
		ArrayList<Text> textList = serviceText.getAllTexts();
		ArrayList<String[]> infoTextList = new ArrayList<>();
		for (Text t : textList) {
			String[] temp = {t.getId().toString(),
					t.getTitle()};
			infoTextList.add(temp);
		}
		return infoTextList;
	}


	//The ArrayList that contains the Reports is formed like this:
	//r[0] --> id of the text.
	//r[1] --> title of the text.
	//r[2] --> id of the author of the text.
	//r[3] --> name of the author of the text.
	//r[4] --> id of the reporter.
	//r[5] --> name of the reporter.
	//r[6] --> content of the report.
	//r[7] --> id of the report.
	public static ArrayList<String[]> getReports() throws AccountNotFoundException, MongoException {
		ArrayList<Report> reportsList = serviceReport.getReports();
		ArrayList<String[]> infoReportsList = new ArrayList<String[]>();
		for(Report r : reportsList) {
			Text t = r.getReported();
			String[] temp = {t.getId().toString(),
					t.getTitle(),
					t.getAuthor().getId().toString(),
					t.getAuthor().getName(),
					r.getReporter().getId().toString(),
					r.getReporter().getName(),
					r.getContent(),
					r.getId().toString()};
			infoReportsList.add(temp);
		}
		return infoReportsList;
	}
}

