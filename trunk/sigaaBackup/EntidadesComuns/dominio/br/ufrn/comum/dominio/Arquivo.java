package br.ufrn.comum.dominio;

import java.io.IOException;
import java.io.InputStream;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.struts.upload.FormFile;

public class Arquivo {
	
	private byte[] bytes;
	private InputStream inputStream;
	private String contentType;
	private String name;
	private long size;
	
	public Arquivo() {
		
	}
	
	public Arquivo(byte[] bytes, InputStream inputStream, String contentType,
			String name, long size) {
		super();
		this.bytes = bytes;
		this.inputStream = inputStream;
		this.contentType = contentType;
		this.name = name;
		this.size = size;
	}
	
	public Arquivo(FormFile file) throws IOException{
		this.bytes = file.getFileData();
		this.inputStream = file.getInputStream();
		this.contentType = file.getContentType();
		this.name = file.getFileName();
		this.size = file.getFileSize();

	}
	
	public Arquivo(UploadedFile file) throws IOException {
		this.bytes = file.getBytes();
		this.inputStream = file.getInputStream();
		this.contentType = file.getContentType();
		this.name = file.getName();
		this.size = file.getSize();
	}
	
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}

}
