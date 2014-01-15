/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/07/2011
 * Autor: Arlindo Rodrigues
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Curso;

/**
 * Entidade responsável pelo armazenamento das configurações referentes a
 * consolidação de notas (nível médio).
 * 
 * @author Arlindo
 *
 */
@Entity
@Table(name = "configuracao_consolidacao", schema = "ensino")
public class ConfiguracaoConsolidacao implements PersistDB {
	
	
	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_configuracao_consolidacao", nullable = false)
	private int id;	
	
	/** Curso que está definida as configurações da consolidação de notas. */
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name = "id_curso", nullable = false)	
	private Curso curso;
	
	/** Classe que será implementada as regras de cálculo de média */
	@Column(name = "classe_estrategia_media")
	private String classeEstrategiaMedia;
	
	/** Notas da configuração */
	@OneToMany(mappedBy = "configuracao")
	private List<RegraNota> notas = new ArrayList<RegraNota>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getClasseEstrategiaMedia() {
		return classeEstrategiaMedia;
	}

	public void setClasseEstrategiaMedia(String classeEstrategiaMedia) {
		this.classeEstrategiaMedia = classeEstrategiaMedia;
	}

	public List<RegraNota> getNotas() {
		return notas;
	}

	public void setNotas(List<RegraNota> notas) {
		this.notas = notas;
	}
}
