/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/05/2013
 *
 */
package br.ufrn.sigaa.ava.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe de dom�nio que representa as configura��es de convers�o de um v�deo a ser exibido na turma virtual.
 * 
 */
@Entity @HumanName(value="Configura��es V�deo Turma", genero='M') 
@Table(name="configuracoes_video_turma", schema="ava")
public class ConfiguracoesVideoTurma implements DominioTurmaVirtual {

	/** Define as configura��es de convers�o de v�deo para todos os demais mime types */
	public static final int DEMAIS_MIME_TYPES = 1;
	/** Define as configura��es de convers�o de v�deo para os mime types FLV */
	public static final int FLV = 2;
	
	/** Chave Prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_configuracoes_video_turma", nullable = false)
	private int id;
	
	/** Nome do mime type da configura��o. */
	@Column(name="nome_mime_type")
	private String nomeMimeType;
	
	/** Comando de convers�o de do v�deo de acordo com o mime type. */
	private String comando;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String getMensagemAtividade() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Turma getTurma() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTurma(Turma turma) {
		// TODO Auto-generated method stub
		
	}

	public String getNomeMimeType() {
		return nomeMimeType;
	}

	public void setNomeMimeType(String nomeMimeType) {
		this.nomeMimeType = nomeMimeType;
	}

	public void setComando(String comando) {
		this.comando = comando;
	}

	public String getComando() {
		return comando;
	}	
	
}
