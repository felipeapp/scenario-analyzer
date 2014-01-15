/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe de domínio que representa as configurações de conversão de um vídeo a ser exibido na turma virtual.
 * 
 */
@Entity @HumanName(value="Configurações Vídeo Turma", genero='M') 
@Table(name="configuracoes_video_turma", schema="ava")
public class ConfiguracoesVideoTurma implements DominioTurmaVirtual {

	/** Define as configurações de conversão de vídeo para todos os demais mime types */
	public static final int DEMAIS_MIME_TYPES = 1;
	/** Define as configurações de conversão de vídeo para os mime types FLV */
	public static final int FLV = 2;
	
	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_configuracoes_video_turma", nullable = false)
	private int id;
	
	/** Nome do mime type da configuração. */
	@Column(name="nome_mime_type")
	private String nomeMimeType;
	
	/** Comando de conversão de do vídeo de acordo com o mime type. */
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
