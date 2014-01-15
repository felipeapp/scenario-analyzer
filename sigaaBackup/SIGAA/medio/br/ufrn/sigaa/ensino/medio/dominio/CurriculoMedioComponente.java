/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 24/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Entidade que representa o relacionamento NxN entre Componente Curricular e Curr�culos do ensino m�dio.
 * Um curr�culo possui v�rios componentes, enquanto cada componente curricular 
 * pode pertencer a v�rios curr�culos diferentes.
 *
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "curriculo_medio_componente", schema = "medio")
public class CurriculoMedioComponente implements Validatable{

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_curriculo_medio_componente", nullable = false)
	private int id;
	
	/** Curr�culo de ensino m�dio ao qual este objeto pertence, referente ao relacionamento NxN de componente com curr�culo. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_curriculo_medio", nullable = false)
	private CurriculoMedio curriculoMedio;
	
	/** Componente Curricular referente ao relacionamento NxN de componente com curr�culo. */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular", nullable = false)
	private ComponenteCurricular componente = new ComponenteCurricular();
	
	/** Carga hor�ria anual do componente curricular. */
	@Column(name = "ch_ano")
	private Integer chAno;
	
	/** Programa curricular do componente a ser cadastrado no curr�culo do ensino m�dio.*/
	@Column(name = "programa")
	private String programa;
	
	/** Registro de Entrada do usu�rio de cadastrou o componente no curr�culo. */	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data de cadastro do componente no curr�culo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/**
	 * Auxilia no cadastro para Indicar se o CurriculoMedioComponente est� selecionado.
	 */
	@Transient
	private boolean selecionado;
	
	/**
	 * Auxilia no cadastro para Indicar se o CurriculoMedioComponente possui aluno matriculado.
	 */
	@Transient
	private boolean possuiAlunoMatriculado = false;
	// Constructors

	public boolean isPossuiAlunoMatriculado() {
		return possuiAlunoMatriculado;
	}

	public void setPossuiAlunoMatriculado(boolean possuiAlunoMatriculado) {
		this.possuiAlunoMatriculado = possuiAlunoMatriculado;
	}

	/** default constructor */
	public CurriculoMedioComponente() {
	}

	/** default minimal constructor */
	public CurriculoMedioComponente(int id) {
		this.id = id;
	}	

	/** minimal constructor */
	public CurriculoMedioComponente(int id, CurriculoMedio curriculoMedio,
			ComponenteCurricular componente) {
		this.id = id;
		this.curriculoMedio = curriculoMedio;
		this.componente = componente;
	}
	

	//Getters and Setters
	
	/** Recupera a Chave Prim�ria.
	/** @return the id */
	public int getId() {
		return id;
	}

	/** Seta a Chave Prim�ria.
	/** @param id the id to set */
	public void setId(int id) {
		this.id = id;
	}

	/** Recupera o Curr�culo de ensino m�dio ao qual este objeto pertence, referente ao relacionamento NxN de componente com curr�culo. 
	/** @return the curriculoMedio */
	public CurriculoMedio getCurriculoMedio() {
		return curriculoMedio;
	}
	
	/** Seta o Curr�culo de ensino m�dio ao qual este objeto pertence, referente ao relacionamento NxN de componente com curr�culo. 
	/** @param curriculoMedio the curriculoMedio to set */
	public void setCurriculoMedio(CurriculoMedio curriculoMedio) {
		this.curriculoMedio = curriculoMedio;
	}

	/** Recupera o Componente Curricular referente ao relacionamento NxN de componente com curr�culo
	/** @return the componente */
	public ComponenteCurricular getComponente() {
		return componente;
	}

	/** Seta o Componente Curricular referente ao relacionamento NxN de componente com curr�culo
	/** @param componente the componente to set */
	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	/** Recupera a Carga hor�ria anual do componente curricular.
	/** @return the chAno */
	public Integer getChAno() {
		return chAno;
	}

	/** Seta a Carga hor�ria anual do componente curricular.
	/** @param chAno the chAno to set */
	public void setChAno(Integer chAno) {
		this.chAno = chAno;
	}

	/** Programa curricular do componente a ser cadastrado no curr�culo do ensino m�dio
	/** @return the programa */
	public String getPrograma() {
		return programa;
	}

	/** Programa curricular do componente a ser cadastrado no curr�culo do ensino m�dio
	/** @param programa the programa to set */
	public void setPrograma(String programa) {
		this.programa = programa;
	}

	/** Recuperar o Registro de Entrada do usu�rio de cadastrou o componente no curr�culo
	/** @return the registroEntrada */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o Registro de Entrada do usu�rio de cadastrou o componente no curr�culo
	/** @param registroEntrada the registroEntrada to set */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Recupera a Data de cadastro do componente no curr�culo. 
	/** @return the dataCadastro */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a Data de cadastro do componente no curr�culo. 
	/** @param dataCadastro the dataCadastro to set */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna uma descri��o textual deste componente curricular.
	 * @return
	 */
	@Transient
	public String getDescricaoComponente() {
		StringBuilder sb = new StringBuilder();
		
		if (componente.getCodigo() != null) {
			sb.append(componente.getCodigo());
		}

		sb.append(" - ");
		
		if (componente.getDetalhes() != null && componente.getDetalhes().getNome() != null) {
			sb.append(componente.getDetalhes().getNome());
		}
		
		sb.append(" - ");
		
		sb.append(getChAno() != null ? getChAno() : 0  + "h");
		
		return sb.toString();
	}
	
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	public boolean isSelecionado() {
		return selecionado;
	}
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(componente, "Componente Curricular", erros);
		ValidatorUtil.validateRequired(programa, "Programa", erros);
		return erros;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "componente");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(componente);
	}
	
}
