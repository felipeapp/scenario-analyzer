/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * O currículo do ensino médio é composto de um elenco de disciplinas caracterizadas
 * por um código, denominação, carga horária, número de créditos, ementa e
 * bibliografia básica, agrupadas nas áreas de concentração e de domínio conexo,
 * de acordo com o respectivo conteúdo programático.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "curriculo_medio", schema = "medio")
public class CurriculoMedio implements Validatable{

	/** Constante que define a situação ABERTA do currículo. */
	public static final int SITUACAO_ABERTO = 2;
	/** Constate que define a situação FECHADA do currículo. */
	public static final int SITUACAO_FECHADO = 1;
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })	
	@Column(name = "id_curriculo_medio", nullable = false)
	private int id;

	/** Código do currículo, normalmente numérico, com dois dígitos, precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...). */
	@Column(name = "codigo")
	private String codigo;
	
	/** Série ao qual este currículo pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_serie", nullable = false)
	private Serie serie;
	
	/** Curso ao qual este currículo pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_curso", nullable = true)
	private CursoMedio cursoMedio;
	
	/** Indica a situação do currículo (aberto ou fechado para edição). */
	@Column(name = "situacao")
	private Integer situacao;
	
	/** Indica se o currículo está ativo, permitindo sua utilização no SIGAA. */
	@Column(name = "ativo", nullable = false)
	private boolean ativo = true;
	
	/** Ano inicial de vigoração do currículo. */
	@Column(name = "ano_entrada_vigor")
	private Integer anoEntradaVigor;
	
	/** Indica a Unidade de Tempo do currículo de Médio. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_tempo", unique = false, nullable = true, insertable = true, updatable = true)
	private UnidadeTempo unidadeTempo = new UnidadeTempo();
	
	/** Indica a carga horária total do Currículo. */
	@Column(name = "carga_horaria", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer cargaHoraria;
	
	/** Registro de Entrada do usuário de cadastrou o currículo. */	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data de cadastro do currículo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro de Entrada do usuário que modificou o currículo. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data de alteração do currículo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;
	
	/** Indica todas as Disciplinas pertencentes ao Currículo. */
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
	/** Recuperar a Chave primária. 
	/** @return the id */
	public int getId() {
		return id;
	}

	/** Seta a Chave primária. 
	/** @param id the id to set */
	public void setId(int id) {
		this.id = id;
	}
	
	/** Recupera  o Código do currículo, normalmente numérico, com dois dígitos, 
	 * precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...).
	/** @return the codigo */
	public String getCodigo() {
		return codigo;
	}

	/** Seta  o Código do currículo, normalmente numérico, com dois dígitos, 
	 * precedido de zero. Ex. (01, 02, 03, ..., 09, 10, ...).
	/** @param codigo the codigo to set */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/** Recupera a Série ao qual este currículo pertence. 
	/** @return the serie */
	public Serie getSerie() {
		return serie;
	}
	
	/** Seta a Série ao qual este currículo pertence. 
	/** @param serie the serie to set */
	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	/**Recupera o Curso ao qual este currículo pertence.
	/** @return the curso */
	public CursoMedio getCursoMedio() {
		return cursoMedio;
	}

	/** Seta o Curso ao qual este currículo pertence.
	/** @param curso the curso to set */
	public void setCursoMedio(CursoMedio cursoMedio) {
		this.cursoMedio = cursoMedio;
	}

	/** Recupera a situação do currículo (aberto ou fechado para edição).
	/** @return the situacao */
	public Integer getSituacao() {
		return situacao;
	}

	/** Seta a situação do currículo (aberto ou fechado para edição).
	/** @param situacao the situacao to set */
	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}

	/** Indica se o currículo está ativo, permitindo sua utilização no SIGAA.
	/** @return the ativo */
	public boolean isAtivo() {
		return ativo;
	}

	/** Indica se o currículo está ativo, permitindo sua utilização no SIGAA.
	/** @param ativo the ativo to set */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Recupera o Ano inicial de vigoração do currículo.
	/** @return the anoEntradaVigor */
	public Integer getAnoEntradaVigor() {
		return anoEntradaVigor;
	}

	/** Seta o Ano inicial de vigoração do currículo.
	/** @param anoEntradaVigor the anoEntradaVigor to set */
	public void setAnoEntradaVigor(Integer anoEntradaVigor) {
		this.anoEntradaVigor = anoEntradaVigor;
	}

	/** Recuperar a Unidade de Tempo do currículo de Médio.
	/** @return the unidadeTempo */
	public UnidadeTempo getUnidadeTempo() {
		return unidadeTempo;
	}

	/** Seta a Unidade de Tempo do currículo de Médio.
	/** @param unidadeTempo the unidadeTempo to set */
	public void setUnidadeTempo(UnidadeTempo unidadeTempo) {
		this.unidadeTempo = unidadeTempo;
	}

	/** Recupera a carga horária total do Currículo
	/** @return the cargaHoraria */
	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	/** Seta a carga horária total do Currículo
	/** @param cargaHoraria the cargaHoraria to set */
	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	/** Recupera o Registro de Entrada do usuário de cadastrou o currículo.
	/** @return the registroEntrada */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o Registro de Entrada do usuário de cadastrou o currículo.
	/** @param registroEntrada the registroEntrada to set */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Recuperar a Data de cadastro do currículo.
	/** @return the dataCadastro */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a Data de cadastro do currículo.
	/** @param dataCadastro the dataCadastro to set */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Recupera o Registro de Entrada do usuário que modificou o currículo.
	/** @return the registroAtualizacao */
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o Registro de Entrada do usuário que modificou o currículo.
	/** @param registroAtualizacao the registroAtualizacao to set */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/** Recuperar a Data de alteração do currículo.
	/** @return the dataAlteracao */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/** Seta a Data de alteração do currículo.
	/** @param dataAlteracao the dataAlteracao to set */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/** Recupera todas as Disciplinas pertencentes ao Currículo.
	/** @return the curriculoComponentes */
	public Set<CurriculoMedioComponente> getCurriculoComponentes() {
		return curriculoComponentes;
	}

	/** Seta todas as Disciplinas pertencentes ao Currículo.
	/** @param curriculoComponentes the curriculoComponentes to set */
	public void setCurriculoComponentes(
			Set<CurriculoMedioComponente> curriculoComponentes) {
		this.curriculoComponentes = curriculoComponentes;
	}

	/** 
	 * Indica se o currículo está aberto para edição.
	 * @return
	 */
	@Transient
	public boolean isAberto() {
		return situacao != null && situacao == SITUACAO_ABERTO;
	}
	
	/** Retornar as disciplinas que compõem o currículo. */
	@Transient
	public Set<ComponenteCurricular> getDisciplinas() {
		Set<ComponenteCurricular> disciplinas = new HashSet<ComponenteCurricular>();
		for (CurriculoMedioComponente cComponente : getCurriculoComponentes()) {
			disciplinas.add(cComponente.getComponente());
		}
		return disciplinas;
	}
	
	/** Retorna a descrição do Currículo, contemplando as descrições de Curso e Série.*/
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
		ValidatorUtil.validateRequired(codigo, "Código do Currículo", lista);
		ValidatorUtil.validateRequired(cursoMedio, "Curso", lista);
		ValidatorUtil.validateRequired(serie, "Série", lista);
		ValidatorUtil.validateRequired(cargaHoraria, "Carga Horária Total", lista);
		ValidatorUtil.validateRequired(anoEntradaVigor, "Ano de Entrada em Vigor", lista);
		ValidatorUtil.validateRequired(unidadeTempo, "Prazo de Conclusão", lista);
		
		return lista;
	}
}
