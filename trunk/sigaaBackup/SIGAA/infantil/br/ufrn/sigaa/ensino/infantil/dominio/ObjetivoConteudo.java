/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/12/2009
 *
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Entidade que representa os objetivos dentro de cada conteúdo das áreas
 * do formulário de evolução da criança no ensino infantil. 
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="objetivo", schema="infantil", uniqueConstraints={})
public class ObjetivoConteudo implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 
	@Column(name = "id_objetivo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	private String descricao;
	
	private int ordem;
	
	@ManyToOne
	@JoinColumn(name="id_conteudo")
	private Conteudo conteudo;
	
	@Transient
	private ObjetivoBimestre bimestreAtual;
	
	@Transient
	private ObjetivoBimestre[] bimestres = new ObjetivoBimestre[4];
	
	public ObjetivoConteudo() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Conteudo getConteudo() {
		return conteudo;
	}

	public void setConteudo(Conteudo conteudo) {
		this.conteudo = conteudo;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public ObjetivoBimestre getBimestreAtual() {
		return bimestreAtual;
	}

	public void setBimestreAtual(ObjetivoBimestre bimestreAtual) {
		this.bimestreAtual = bimestreAtual;
	}
	
	public String getDescricaoOrdem(){
		return UFRNUtils.inteiroToAlfabeto(ordem) + ") " + descricao;
	}

	public ObjetivoBimestre[] getBimestres() {
		return bimestres;
	}

	public void setBimestres(ObjetivoBimestre[] bimestres) {
		this.bimestres = bimestres;
	}
}
