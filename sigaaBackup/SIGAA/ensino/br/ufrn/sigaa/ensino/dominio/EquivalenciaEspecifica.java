/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 09/04/2010
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.annotations.Required;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;

/**
 * Entidade que representa uma equivalência específica entre
 * componentes curriculares, conforme definido no parágrafo II
 * do art. 38 do regulamento dos cursos de graduação:
 * 
 * (A equivalência pode ser) específica, quando se aplica apenas 
 * a uma estrutura curricular de um curso, e que se destina 
 * principalmente a permitir migrações de alunos entre estruturas curriculares.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="equivalencia_especifica", schema="ensino")
public class EquivalenciaEspecifica implements PersistDB {

	public static final String EQUIVALENCIA_GLOBAL = "<GLOBAL>";
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.equivalencia_especifica_seq") })
	private int id;
	
	/** Currículo que possui a equivalência específica */
	@ManyToOne @JoinColumn(name="id_curriculo")
	@Required
	private Curriculo curriculo;
	
	/** Componente que possui a equivalência específica */
	@ManyToOne @JoinColumn(name="id_componente_curricular")
	@Required
	private ComponenteCurricular componente;
	
	/** Expressão de equivalência */
	@Required
	private String expressao;
	
	/** Data a partir da qual a equivalência vale */
	@Column(name="inicio_vigencia")
	@Required
	private Date inicioVigencia;
	
	/** Data a partir da qual a equivalência deixa de valer */
	@Column(name="fim_vigencia")
	private Date fimVigencia;

	@CampoAtivo(true)
	private boolean ativo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Curriculo getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	public String getExpressao() {
		return expressao;
	}

	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

	public Date getInicioVigencia() {
		return inicioVigencia;
	}

	public void setInicioVigencia(Date inicioVigencia) {
		this.inicioVigencia = inicioVigencia;
	}

	public Date getFimVigencia() {
		return fimVigencia;
	}

	public void setFimVigencia(Date fimVigencia) {
		this.fimVigencia = fimVigencia;
	}

	public boolean isEquivalenciaValendoNaData(Date data) {
		return fimVigencia == null || fimVigencia.after(data);
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
