/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Criação: 16/12/2009
 */
package br.ufrn.integracao.siged.dto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.struts.upload.FormFile;

import eu.medsea.mimeutil.MimeUtil;

/**
 * Interface que define os dados necessários para inserir arquivos
 * referentes aos documentos na base de arquivos. 
 * 
 * @author David Pereira
 *
 */
public class ArquivoDocumento implements Serializable {

	private byte[] bytes;
	private String contentType;
	private String name;
	private long size;
	
	public ArquivoDocumento() {
		
	}
	
	public ArquivoDocumento(InputStream is, String name, String contentType) throws IOException {
		this.name = name;
		this.contentType = contentType;
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int _byte;
		
		try {
			while((_byte = is.read()) != -1) {
				os.write(_byte);
				size++;
			}
		} finally {
			is.close();
		}
		
		bytes = os.toByteArray();
	}
	
	public ArquivoDocumento(FormFile file) throws IOException{
		this.bytes = file.getFileData();
		this.contentType = file.getContentType();
		this.name = file.getFileName();
		this.size = file.getFileSize();

	}
	
	public ArquivoDocumento(UploadedFile file) throws IOException {
		this.bytes = file.getBytes();
		this.contentType = file.getContentType();
		this.name = file.getName();
		this.size = file.getSize();
	}
	
	public ArquivoDocumento(File file, String fileName) throws IOException {
		this.bytes = FileUtils.readFileToByteArray(file);
		this.contentType = MimeUtil.getMimeTypes(file).iterator().next().toString();
		this.name = fileName;
		this.size = file.length();
	}
	
	public ArquivoDocumento(File file) throws IOException {
		this.bytes = FileUtils.readFileToByteArray(file);
		this.contentType = MimeUtil.getMimeTypes(file).iterator().next().toString();
		this.name = file.getName();
		this.size = file.length();
	}

	/** Retorna o conteúdo do arquivo como um array de bytes. */
	public byte[] getBytes() throws IOException {
		return bytes;
	}
	  
	/** String que identifica o tipo do conteúdo do arquivo. */
	public String getContentType() {
		return contentType;
	}
	  
	/** Retorna o nome do arquivo. */
	public String getName() {
		return name;
	}
	  
	/** Retorna o tamanho do arquivo em bytes. */
	public long getSize() {
		return size;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
}
