/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/09/2013'
 *
 */
package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;

/**
 * Representa o vínculo de um fórum com um programa. 
 * 
 * @author Diego Jácome
 *
 */
public class ForumPrograma {

	/** Chave Primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_forum_programa")    
	private int id;
	
	/** Fórum do programa. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forum")
	private ForumGeral forum;

	/** Programa do fórum. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private ProgramaRede programa;

	/** Indica se um fórum válido. */
	@CampoAtivo
	private boolean ativo;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setForum(ForumGeral forum) {
		this.forum = forum;
	}

	public ForumGeral getForum() {
		return forum;
	}

	public void setPrograma(ProgramaRede programa) {
		this.programa = programa;
	}

	public ProgramaRede getPrograma() {
		return programa;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isAtivo() {
		return ativo;
	}
}
