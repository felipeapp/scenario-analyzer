/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 08/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaInicioFim;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;

/**
 * Define um calend�rio de Auto Avalia��o e Metas e Planejamento do Programa de
 * P�s Gradua��o a ser disponibilizado para preenchimento pelos coordenadores do
 * programas de Stricto Sensu.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(schema="stricto_sensu", name="calendario_aplicacao_auto_avaliacao")
public class CalendarioAplicacaoAutoAvaliacao implements Validatable {
	
	/** Chave primaria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_calendario_aplicacao_auto_avaliacao", nullable = false)
	private int id;

	/** Instru��es gerais que ser�o informadas ao usu�rio, no in�cio do formul�rio de Auto Avalia��o. */
	@Column(name = "instrucoes_gerais", nullable=false)
	private String instrucoesGerais;
	
	/** Question�rio a ser aplicado para a Auto Avalia��o. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_questionario",nullable=false)
	private Questionario questionario;
	
	/** Data de in�cio de respostas do question�rio. */
	@Column(name = "data_inicio", nullable=false)
	private Date dataInicio;
	
	/** Data de fim de respostas do question�rio. */
	@Column(name = "data_fim", nullable=false)
	private Date dataFim;
	
	/** Indica se o calend�rio est� ativo para utiliza��o do SIGAA. */
	@CampoAtivo
	private boolean ativo;
	
	/** Programas ao qual este calend�rio estar� dispon�vel. */
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="calendario_aplicacao_programa", schema="stricto_sensu",
			joinColumns=@JoinColumn(name="id_calendario_aplicacao_auto_avaliacao"),  
			inverseJoinColumns=@JoinColumn(name="id_unidade"))
	private Collection<Unidade> programas;
	
	/** Curso de Lato Sensu ao qual este calend�rio estar� dispon�vel. */
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="calendario_aplicacao_curso", schema="stricto_sensu",
			joinColumns=@JoinColumn(name="id_calendario_aplicacao_auto_avaliacao"),  
			inverseJoinColumns=@JoinColumn(name="id_curso"))
	private Collection<Curso> cursos;

	/** Indica que o question�rio ser� aplicado a todos os programas (caso stricto) ou cursos (caso lato). */
	@Column(name = "aplicavel_a_todos")
	private boolean aplicavelATodos;
	
	/** Quantidade de question�rios respondidos. */
	@Transient
	private int qtdRespostas;
	
	/** Construtor padr�o. */
	public CalendarioAplicacaoAutoAvaliacao() {
		this.questionario = new Questionario();
		this.ativo = true;
	}
	
	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}
	public String getInstrucoesGerais() {
		return instrucoesGerais;
	}
	public void setInstrucoesGerais(String instrucoesGerais) {
		this.instrucoesGerais = instrucoesGerais;
	}
	public Questionario getQuestionario() {
		return questionario;
	}
	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	/**
	 * Retorna uma representa��o textual deste objeto contendo o t�tulo do
	 * formul�rio e o per�odo de aplica��o.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return questionario.getTitulo() + ", de "
				+ Formatador.getInstance().formatarData(dataInicio) + " a "
				+ Formatador.getInstance().formatarData(dataFim);
	}

	/** Valida os dados obrigat�rios.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(instrucoesGerais, "Instru��es Gerais", lista);
		validateRequired(questionario, "Question�rio", lista);
		validateRequired(dataInicio, "In�cio da Avalia��o", lista);
		validateRequired(dataFim, "Fim da Avalia��o", lista);
		validaInicioFim(dataInicio, dataFim, "Per�odo de Avalia��o", lista);
		if (!aplicavelATodos) {
			if (questionario.getTipo().getId() == TipoQuestionario.AUTO_AVALIACAO_STRICTO_SENSU && isEmpty(programas)) 
				lista.addErro("Informe um ou mais programas ao qual o question�rio ser� aplicado.");
			else if (questionario.getTipo().getId() == TipoQuestionario.AUTO_AVALIACAO_LATO_SENSU && isEmpty(cursos)) 
				lista.addErro("Informe um ou mais cursos ao qual o question�rio ser� aplicado.");
		}
		return lista;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/** Indica se est� no per�odo de preenchimento do Auto Avalia��o
	 * @return
	 */
	public boolean isPeriodoPreenchimento() {
		return CalendarUtils.isDentroPeriodo(dataInicio, dataFim);
	}

	public int getQtdRespostas() {
		return qtdRespostas;
	}

	public void setQtdRespostas(int qtdRespostas) {
		this.qtdRespostas = qtdRespostas;
	}

	public Collection<Unidade> getProgramas() {
		return programas;
	}

	public void setProgramas(Collection<Unidade> programas) {
		this.programas = programas;
	}

	public Collection<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}

	public boolean isAplicavelATodos() {
		return aplicavelATodos;
	}

	public void setAplicavelATodos(boolean aplicavelATodos) {
		this.aplicavelATodos = aplicavelATodos;
	}

}
