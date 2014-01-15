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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.UnidadeTempo;

/**
 * O curr�culo do ensino m�dio � composto de um elenco de disciplinas caracterizadas
 * por um c�digo, denomina��o, carga hor�ria, n�mero de cr�ditos, ementa e
 * bibliografia b�sica, agrupadas nas �reas de concentra��o e de dom�nio conexo,
 * de acordo com o respectivo conte�do program�tico.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "curriculo_medio", schema = "medio")
public class CurriculoMedio implements Validatable{

	/** Constante que define a situa��o ABERTA do curr�culo. */
	public static final int SITUACAO_ABERTO = 2;
	/** Constate que define a situa��o FECHADA do curr�culo. */
	public static final int SITUACAO_FECHADO = 1;
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })	
	@Column(name = "id_curriculo_medio", nullable = false)
	private int id;

	/** C�digo do curr�culo, normalmente num�rico, com dois d�gitos, precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...). */
	@Column(name = "codigo")
	private String codigo;
	
	/** S�rie ao qual este curr�culo pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_serie", nullable = false)
	private Serie serie;
	
	/** Curso ao qual este curr�culo pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_curso", nullable = true)
	private CursoMedio cursoMedio;
	
	/** Indica a situa��o do curr�culo (aberto ou fechado para edi��o). */
	@Column(name = "situacao")
	private Integer situacao;
	
	/** Indica se o curr�culo est� ativo, permitindo sua utiliza��o no SIGAA. */
	@Column(name = "ativo", nullable = false)
	private boolean ativo = true;
	
	/** Ano inicial de vigora��o do curr�culo. */
	@Column(name = "ano_entrada_vigor")
	private Integer anoEntradaVigor;
	
	/** Indica a Unidade de Tempo do curr�culo de M�dio. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_tempo", unique = false, nullable = true, insertable = true, updatable = true)
	private UnidadeTempo unidadeTempo = new UnidadeTempo();
	
	/** Indica a carga hor�ria total do Curr�culo. */
	@Column(name = "carga_horaria", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer cargaHoraria;
	
	/** Registro de Entrada do usu�rio de cadastrou o curr�culo. */	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data de cadastro do curr�culo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro de Entrada do usu�rio que modificou o curr�culo. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data de altera��o do curr�culo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;
	
	/** Indica todas as Disciplinas pertencentes ao Curr�culo. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "curriculoMedio")
	private Set<CurriculoMedioComponente> curriculoComponentes = new HashSet<CurriculoMedioComponente>(0);
		
	// Constructors

	/** default constructor */
	public CurriculoMedio() {
	}

	/** default minimal constructor */
	public CurriculoMedio(int id) {
		this.id = id;
	}	

	/** minimal constructor */
	public CurriculoMedio(int id, String codigo, Serie serie) {
		this.id = id;
		this.codigo = codigo;
		this.serie = serie;
	}

	
	// Getters and Setters
	/** Recuperar a Chave prim�ria. 
	/** @return the id */
	public int getId() {
		return id;
	}

	/** Seta a Chave prim�ria. 
	/** @param id the id to set */
	public void setId(int id) {
		this.id = id;
	}
	
	/** Recupera  o C�digo do curr�culo, normalmente num�rico, com dois d�gitos, 
	 * precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...).
	/** @return the codigo */
	public String getCodigo() {
		return codigo;
	}

	/** Seta  o C�digo do curr�culo, normalmente num�rico, com dois d�gitos, 
	 * precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...).
	/** @param codigo the codigo to set */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/** Recupera a S�rie ao qual este curr�culo pertence. 
	/** @return the serie */
	public Serie getSerie() {
		return serie;
	}
	
	/** Seta a S�rie ao qual este curr�culo pertence. 
	/** @param serie the serie to set */
	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	/**Recupera o Curso ao qual este curr�culo pertence.
	/** @return the curso */
	public CursoMedio getCursoMedio() {
		return cursoMedio;
	}

	/** Seta o Curso ao qual este curr�culo pertence.
	/** @param curso the curso to set */
	public void setCursoMedio(CursoMedio cursoMedio) {
		this.cursoMedio = cursoMedio;
	}

	/** Recupera a situa��o do curr�culo (aberto ou fechado para edi��o).
	/** @return the situacao */
	public Integer getSituacao() {
		return situacao;
	}

	/** Seta a situa��o do curr�culo (aberto ou fechado para edi��o).
	/** @param situacao the situacao to set */
	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}

	/** Indica se o curr�culo est� ativo, permitindo sua utiliza��o no SIGAA.
	/** @return the ativo */
	public boolean isAtivo() {
		return ativo;
	}

	/** Indica se o curr�culo est� ativo, permitindo sua utiliza��o no SIGAA.
	/** @param ativo the ativo to set */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Recupera o Ano inicial de vigora��o do curr�culo.
	/** @return the anoEntradaVigor */
	public Integer getAnoEntradaVigor() {
		return anoEntradaVigor;
	}

	/** Seta o Ano inicial de vigora��o do curr�culo.
	/** @param anoEntradaVigor the anoEntradaVigor to set */
	public void setAnoEntradaVigor(Integer anoEntradaVigor) {
		this.anoEntradaVigor = anoEntradaVigor;
	}

	/** Recuperar a Unidade de Tempo do curr�culo de M�dio.
	/** @return the unidadeTempo */
	public UnidadeTempo getUnidadeTempo() {
		return unidadeTempo;
	}

	/** Seta a Unidade de Tempo do curr�culo de M�dio.
	/** @param unidadeTempo the unidadeTempo to set */
	public void setUnidadeTempo(UnidadeTempo unidadeTempo) {
		this.unidadeTempo = unidadeTempo;
	}

	/** Recupera a carga hor�ria total do Curr�culo
	/** @return the cargaHoraria */
	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	/** Seta a carga hor�ria total do Curr�culo
	/** @param cargaHoraria the cargaHoraria to set */
	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	/** Recupera o Registro de Entrada do usu�rio de cadastrou o curr�culo.
	/** @return the registroEntrada */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o Registro de Entrada do usu�rio de cadastrou o curr�culo.
	/** @param registroEntrada the registroEntrada to set */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Recuperar a Data de cadastro do curr�culo.
	/** @return the dataCadastro */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a Data de cadastro do curr�culo.
	/** @param dataCadastro the dataCadastro to set */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Recupera o Registro de Entrada do usu�rio que modificou o curr�culo.
	/** @return the registroAtualizacao */
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o Registro de Entrada do usu�rio que modificou o curr�culo.
	/** @param registroAtualizacao the registroAtualizacao to set */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Recuperar a Data de altera��o do curr�culo.
	/** @return the dataAlteracao */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/** Seta a Data de altera��o do curr�culo.
	/** @param dataAlteracao the dataAlteracao to set */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/** Recupera todas as Disciplinas pertencentes ao Curr�culo.
	/** @return the curriculoComponentes */
	public Set<CurriculoMedioComponente> getCurriculoComponentes() {
		return curriculoComponentes;
	}

	/** Seta todas as Disciplinas pertencentes ao Curr�culo.
	/** @param curriculoComponentes the curriculoComponentes to set */
	public void setCurriculoComponentes(
			Set<CurriculoMedioComponente> curriculoComponentes) {
		this.curriculoComponentes = curriculoComponentes;
	}

	/** 
	 * Indica se o curr�culo est� aberto para edi��o.
	 * @return
	 */
	@Transient
	public boolean isAberto() {
		return situacao != null && situacao == SITUACAO_ABERTO;
	}
	
	/** Retornar as disciplinas que comp�em o curr�culo. */
	@Transient
	public Set<ComponenteCurricular> getDisciplinas() {
		Set<ComponenteCurricular> disciplinas = new HashSet<ComponenteCurricular>();
		for (CurriculoMedioComponente cComponente : getCurriculoComponentes()) {
			disciplinas.add(cComponente.getComponente());
		}
		return disciplinas;
	}
	
	/** Retorna a descri��o do Curr�culo, contemplando as descri��es de Curso e S�rie.*/
	@Transient
	public String getDescricao() {
		StringBuffer descricao = new StringBuffer();
		if (cursoMedio != null && cursoMedio.getNome() != null)
			descricao.append(cursoMedio.getNome() + " - ");
		if (serie != null && serie.getDescricaoCompleta() != null)
			descricao.append(serie.getDescricaoCompleta());
		return descricao.toString();
	}	

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(codigo, "C�digo do Curr�culo", lista);
		ValidatorUtil.validateRequired(cursoMedio, "Curso", lista);
		ValidatorUtil.validateRequired(serie, "S�rie", lista);
		ValidatorUtil.validateRequired(cargaHoraria, "Carga Hor�ria Total", lista);
		ValidatorUtil.validateRequired(anoEntradaVigor, "Ano de Entrada em Vigor", lista);
		ValidatorUtil.validateRequired(unidadeTempo, "Prazo de Conclus�o", lista);
		
		return lista;
	}
}
