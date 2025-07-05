package project.RnW.model;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.rmi.AccessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;

import java.sql.Statement;

import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.Database;
import project.RnW.service.serviceText;

public class User {
	
	private ObjectId id;
	private String name;
	private boolean admin;
	
	
	public User(ObjectId id, String name, boolean admin) {
		this.id = id;
		this.name = name;
		this.admin = admin;
	}

	//is used to change the name of the account
	public void changeName(String name) throws MongoException{
		this.name=name;
	}


	//returns the id of the account
	public ObjectId getId() {
		return id;
	}

	//returns the name of the account
	public String getName() {
		return name;
	}
	
	//returns true if the User is an administrator
	public boolean isAdmin() {
		return admin;
	}
	
	//returns true if this User is equal to u
	public boolean equals(User u) {
		//since the ids are unique, checking those is enough
		if(u.getId().equals(this.getId())) 
			return true;
		return false;
	}

	//returns true if u is the owner of this account
	public boolean isOwner(User u) {
		return this.equals(u);
		
	}
	
	
	
}

