/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/07/2011
 * Autor: Arlindo Rodrigues
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade responsável pelo armazenamento da estrutura de notas 
 * referentes a configuração do curso (nível médio).
 * 
 * @author Arlindo
 *
 */
@Entity
@Table(name = "regra_nota", schema = "ensino")
public class RegraNota implements PersistDB {
	
	
	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_regra_nota", nullable = false)
	private int id;		
	
	/** Configuração do Curso. */
	@ManyToOne(fetch=FetchType.EAGER) 
	@JoinColumn(name = "id_configuracao_consolidacao")	
	private ConfiguracaoConsolidacao configuracao;	
	
	/** Título da nota */
	private String titulo;
	
	/** Número ordinal da nota */
	private Integer ordem;
	
	/** Tipo da nota*/
	@Enumerated(EnumType.ORDINAL)
	@Column(name="tipo", nullable=true)
	private TipoNota tipo;
	
	/** Peso da nota */
	private Integer peso;
	
	/** Indica quais notas a recuperação está vinculada (Usado apenas para o tipo de recuperação) */
	@Column(name = "ref_recuperacao")
	private String refRec;
	
	/** Classe que será implementada as regras de cálculo de recuperação */
	@Column(name = "classe_estrategia_recuperacao")
	private String classeEstrategiaRecuperacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ConfiguracaoConsolidacao getConfiguracao() {
		return configuracao;
	}

	public void setConfiguracao(ConfiguracaoConsolidacao configuracao) {
		this.configuracao = configuracao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Integer getPeso() {
		return peso;
	}

	public void setPeso(Integer peso) {
		this.peso = peso;
	}

	public String getRefRec() {
		return refRec;
	}

	public void setRefRec(String refRec) {
		this.refRec = refRec;
	}

	public String getClasseEstrategiaRecuperacao() {
		return classeEstrategiaRecuperacao;
	}

	public void setClasseEstrategiaRecuperacao(String classeEstrategiaRecuperacao) {
		this.classeEstrategiaRecuperacao = classeEstrategiaRecuperacao;
	}

	public TipoNota getTipo() {
		return tipo;
	}

	public void setTipo(TipoNota tipo) {
		this.tipo = tipo;
	}
	
	/** 
	 * Indica se o tipo da regra é de Nota
	 * @return
	 */
	public boolean isNota(){
		return tipo.equals(TipoNota.REGULAR);
	}
	
	/**
	 * Indica se o tipo da regra é de Recuperação
	 * @return
	 */
	public boolean isRecuperacao(){
		return tipo.equals(TipoNota.RECUPERACAO);
	}
	
	/**
	 * Indica se o tipo da regra é de Prova final
	 * @return
	 */
	public boolean isProvaFinal(){
		return tipo.equals(TipoNota.PROVA_FINAL);
	}	
}
