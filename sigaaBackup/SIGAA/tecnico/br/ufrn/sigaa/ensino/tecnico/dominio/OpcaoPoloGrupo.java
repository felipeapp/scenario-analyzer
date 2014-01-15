/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ead.dominio.Polo;

/**
 * Entidade que modela a opção que o discente selecionou para pólo e grupo no processo seletivo técnico.
 * @author Fred
 *
 */

@Entity
@Table(name = "opcao_polo_grupo", schema = "tecnico")
public class OpcaoPoloGrupo implements PersistDB {
	
	public static final int GRUPO_ATE_21_ANOS = 1;
	public static final int GRUPO_MAIS_DE_21_ANOS = 2;
	public static final int SEM_GRUPO = 3;
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_opcao_polo_grupo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_polo")
	private Polo polo;
	
	private String descricao;
	
	private int grupo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	public String getDescricaoGrupo (){
		if (grupo == GRUPO_ATE_21_ANOS) return "Grupo I (15 a 20 anos)";
		if (grupo == GRUPO_MAIS_DE_21_ANOS) return "Grupo II (21 anos ou mais)";
		if (grupo == SEM_GRUPO) return "Sem Grupo";
		
		return "";
	}
	
	public static String getDescricaoGrupo (int grupo){
		if (grupo == GRUPO_ATE_21_ANOS) return "Grupo I (15 a 20 anos)";
		if (grupo == GRUPO_MAIS_DE_21_ANOS) return "Grupo II (21 anos ou mais)";
		if (grupo == SEM_GRUPO) return "Sem Grupo";
		
		return "";
	}
	
	public String toString () {
		String s = id + " - ";
		if (polo != null && polo.getCidade() != null)
			s += polo.getCidade().getNome();
		
		s += " - " + getDescricaoGrupo();
		
		return s;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isNatal(){
		return id == 101 || id == 102;
	}
		
	public boolean isMossoro(){
		return id == 201 || id == 202;
	}

	public boolean isAngicos(){
		return id == 301 || id == 302;
	}

	public boolean isCaico() {
		return id == 401 || id == 402;
	}

	public boolean isCenep(){
		return id == 501;
	}

}