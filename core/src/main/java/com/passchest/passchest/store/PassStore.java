package com.passchest.passchest.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.*;
import com.google.gson.Gson;
import com.passchest.passchest.DriveHelper;
import com.passchest.passchest.crypto.AES;
import com.passchest.passchest.crypto.AES.InvalidAESStreamException;
import com.passchest.passchest.crypto.AES.InvalidKeyLengthException;
import com.passchest.passchest.crypto.AES.InvalidPasswordException;
import com.passchest.passchest.crypto.AES.StrongEncryptionNotAvailableException;

public class PassStore {
	
	public static java.io.File passStoreFile = new java.io.File(System.getProperty("user.home"), ".passchest/pass.store");
	public static PassStore instance;
	public static char[] masterPassword;
	
	public static Drive service;
	
	public List<PassGroup> passwords;
	
	public PassStore() {
		this.passwords = new ArrayList<>();
	}
	
	/**
	 * Decrypts and loads the pass store from Google Drive
	 * @throws IOException 
	 * @throws StrongEncryptionNotAvailableException 
	 * @throws InvalidAESStreamException 
	 * @throws InvalidPasswordException 
	 */
	public static boolean loadPassStore() throws IOException {
		if(service == null)
			service = DriveHelper.getDriveService();
		passStoreFile.getParentFile().mkdirs();
		java.io.File tempFile = new java.io.File(passStoreFile.getParent(), "temp");
		OutputStream outputStream = new FileOutputStream(tempFile);
		boolean failed = false;
		try {
			FileList files = service.files().list()
	        .setSpaces("appDataFolder")
	        .setFields("nextPageToken, files(id, name)")
	        .setPageSize(10)
	        .execute();
			String fileId = "";
			for(File f : files.getFiles()) {
				if(f.getName().equals("pass.store")) {
					fileId = f.getId();
					System.out.println("Reading: " + fileId);
				}
			}
			service.files().get(fileId)
			        .executeMediaAndDownloadTo(outputStream);
		} catch (IOException e) {
			failed = true;
		} finally {
			outputStream.close();
			if(!failed) {
				passStoreFile.delete();
				tempFile.renameTo(passStoreFile);
			} else {
				tempFile.delete();
				System.out.println("Fetch from Google Drive failed, proceeding to use local store");
			}
		}
		if(passStoreFile.exists() && passStoreFile.length() > 0) {
			return true;
		}
		return false;
//		FileList files = service.files().list().
//		        .setSpaces("appDataFolder")
//		        .setFields("nextPageToken, files(id, name)")
//		        .setPageSize(10)
//		        .execute();
//		return files.containsKey("pass.store");
	}
	
	public static void decryptPassStore(char[] password) throws FileNotFoundException, InvalidPasswordException, InvalidAESStreamException, IOException, StrongEncryptionNotAvailableException {
		masterPassword = password;
		Gson gson = new Gson();
		String json;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		AES.decrypt(password, new FileInputStream(PassStore.passStoreFile), os);
		json = new String(os.toByteArray(), StandardCharsets.UTF_8);
		instance = gson.fromJson(json, PassStore.class);
	}

	/**
	 * Creates a new PassStore object and sets it as the current instance
	 */
	public static void createEmptyPassStore() {
		instance = new PassStore();
	}

	/**
	 * Encrypts and saves the pass store to Google Drive
	 * @throws InvalidKeyLengthException
	 * @throws StrongEncryptionNotAvailableException
	 * @throws IOException
	 */
	public static void savePassStore() throws InvalidKeyLengthException, StrongEncryptionNotAvailableException, IOException {
		Gson gson = new Gson();
		String json = gson.toJson(PassStore.instance);
		InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		
		OutputStream fos = new FileOutputStream(passStoreFile);
		AES.encrypt(128, masterPassword, is, fos);
		
		if(service == null)
			service = DriveHelper.getDriveService();
		FileList files = service.files().list()
				.setSpaces("appDataFolder")
				.setFields("nextPageToken, files(id, name)")
				.setPageSize(10)
				.execute();
		String fileId = "";
		for(File f : files.getFiles()) {
			if(f.getName().equals("pass.store")) {
				fileId = f.getId();
			}
		}
		
		File fileMetadata = new File();
		fileMetadata.setName("pass.store");
		fileMetadata.setParents(Collections.singletonList("appDataFolder"));
		FileContent mediaContent = new FileContent("application/octet-stream", passStoreFile);
		File file;
		if(!fileId.equals("")){
			fileMetadata = new File();
			file = service.files().update(fileId, fileMetadata, mediaContent).execute();
		} else {
			file = service.files().create(fileMetadata, mediaContent)
			        .setFields("id")
			        .execute();
		}
		System.out.println("Saved to file id: " + file.getId());
	}

	/**
	 * Adds an username/password pair to the pass store
	 * @param passGroup The group to add the username/password pair to
	 * @param user Username or email
	 * @param password Password
	 * @return Created PassEntry
	 */
	public PassEntry putPassword(String passGroup, String user, String password) {
		boolean saved = false;
		PassEntry entry = new PassEntry(user, password);
		for(PassGroup group : passwords) {
			if(!group.groupName.equals(passGroup))
				continue;
			group.groupEntries.add(entry);
			saved = true;
		}
		if(!saved) {
			List<PassEntry> entryList = new ArrayList<>();
			entryList.add(entry);
			passwords.add(new PassGroup(passGroup, new ArrayList<String>(), entryList));
		}
		return entry;
	}
	
	/**
	 * Adds an username/password pair to the pass store
	 * @param passGroup The group to add the username/password pair to
	 * @param username Username
	 * @param email Email
	 * @param password Password
	 * @return Created PassEntry
	 */
	public PassEntry putPassword(String passGroup, String username, String email, String password) {
		boolean saved = false;
		PassEntry entry = new PassEntry(username, email, password);
		for(PassGroup group : passwords) {
			if(!group.groupName.equals(passGroup))
				continue;
			group.groupEntries.add(entry);
			saved = true;
		}
		if(!saved) {
			List<PassEntry> entryList = new ArrayList<>();
			entryList.add(entry);
			passwords.add(new PassGroup(passGroup, new ArrayList<String>(), entryList));
		}
		return entry;
	}
}
