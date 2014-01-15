package br.ufrn.integracao.siged.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data Transfer Object que armazena informações
 * sobre as versões de um determinado documento do SIGED.
 * 
 * @author Raphael Medeiros
 *
 */
public class DocumentoVersaoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8510173113222868890L;

	/** Chave-primária da versão do documento (identificador) */
	private int id;
	
	/** Data de cadastro da versão do documento */
	private Date data;
	
	/** Documento vinculado a esta versão */
	private int documento;
	
	/** Versão do documento */
	private long versao;
	
	/** Diff para próxima versão (aplicado apenas quando existir mais de uma versãoe nas versões anteriores, nunca na última) */
	private String diff;
	
	public DocumentoVersaoDTO() {
		System.out.println("Classloader de DocumentoVersaoDTO: " + getClass().getClassLoader());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getDocumento() {
		return documento;
	}

	public void setDocumento(int documento) {
		this.documento = documento;
	}

	public long getVersao() {
		return versao;
	}

	public void setVersao(long versao) {
		this.versao = versao;
	}

	public String getDiff() {
		return diff;
	}

	public void setDiff(String diff) {
		this.diff = diff;
	}
}