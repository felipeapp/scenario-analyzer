/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/11/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;

/*******************************************************************************
 * <p>
 * Cont�m informa��es espec�ficas sobre uma a��o de extens�o do tipo curso ou
 * evento. <br/>
 * Os Cursos: <br/>
 * Caracterizam-se como atividade de ensino extracurricular que se prop�e a
 * transmitir os conhecimentos produzidos na Universidade, ou fora dela, de
 * forma presencial ou � dist�ncia, que venham a contribuir para uma melhor
 * articula��o entre o saber acad�mico e as praticas sociais. <br/>
 * Quando se tratar de cursos de extens�o semi-presencial ou a distancia, o
 * Projeto do Curso dever� ser submetido � aprecia��o da Secretaria de Educa��o
 * � Dist�ncia, que emitir� parecer. <br/>
 * Para a expedi��o de certificados, o coordenador do curso encaminhar� �
 * Pro-reitoria de Extens�o Universit�ria relat�rio circunstanciado das
 * atividades, juntamente com mapa de apura��o da assiduidade e dos resultados
 * da avalia��o, nas diversas disciplinas, quando for o caso, devidamente
 * apreciado pela plen�ria do seu departamento (Art. 14 da Res.
 * 074/2004-CONSEPE). <br/>
 * Os Cursos de Extens�o poder�o ser propostos e promovidos por Unidades
 * Acad�micas, Base ou N�cleo de Pesquisa, grupo de professores, ou org�o da
 * universidade, podendo ser realizados por mais de uma delas, ou em colabora��o
 * com entidades p�blicas ou privadas (Art 16 da Res. 074/2004 - COSEPE). <br/>
 * Os cursos de Extens�o poder�o cobrar taxas de inscri��o para cobrir, total ou
 * parcialmente, os seus custos, de acordo com as normas em vigor na UFRN (Art
 * 17 da Res. 074/2004 - CONSEPE). <br/>
 * Al�m das modalidades semi-presencial e � distancia, s�o modalidades dos
 * Cursos de Extens�o: Cursos de Divulga��o, Cursos de Atualiza��o e Cursos de
 * Capacita��o. <br/>
 * Os cursos de Divulga��o, Atualiza��o ou Capacita��o podem integralizar a
 * carga hor�ria docente, desde que sejam observados os crit�rios de dura��o e
 * controle acad�mico equivalentes aos das disciplinas regulares de gradua��o ou
 * p�s-gradua��o, nos termos da Resolu��o n� 023/92 - CONSEP de 04.02.92. <br/>
 * Os Eventos: <br/>
 * S�o eventos, para efeitos de registro de atividades de extens�o, a��es de
 * interesse t�cnico, social, cient�fico, art�stico, esportivo, que congreguem
 * pessoas em torno de objetivos espec�ficos (Art 18 da Res. 074/2004 -
 * CONSEPE).
 * </p>
 * 
 * 
 * @author Victor Hugo
 * @author Gleydson
 * @author Ricardo Wendell
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "curso_evento")
public class CursoEventoExtensao implements Validatable {
	
	/** Identifica tipo curso. */
	public static final char CURSO = 'C';
	/** Identifica tipo evento. */
	public static final char EVENTO = 'E';

	/** Identificador CursoEventoExtensao */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="extensao.curso_evento_extensao_sequence") })
	@Column(name = "id_curso_evento", nullable = false)
	private int id;

	/** Tipo curso ou evento. */
	@Column(name = "tipo")
	private char tipo;

	/** N�mero total de concluintes deste curso. */
	@Column(name = "numero_concluintes")
	private Integer numeroConcluintes;

	/** 
	 *  <p>Previs�o do N�mero total de vagas deste curso/evento.</p>
	 * 
	 *  <p>Esse n�mero � informado no momento do cadastro do curso ou evento e aparece de novo 
	 *  quando o coordenador vai abrir as inscri��es do curso e evento. </p>
	 *  
	 *  <p>Para saber o n�mero de inscri��es permitidas para o curso � usado outra vari�vel "quantidadeVagas" em InscricaoAtividade.</p>
	 *  
	 */
	@Column(name = "numero_vagas")
	private Integer numeroVagas;

	/** Carga hor�ria do curso. 
	 *  Usada para gerar a carga hor�ria nos certificados a partir da formula = ( CH * fequencia do participante )
	 */
	@Column(name = "carga_horaria")
	private Integer cargaHoraria;

	/** Resumo do curso. */
	@Column(name = "resumo")
	private String resumo;

	/** Programa��o do curso. */
	@Column(name = "programacao")
	private String programacao;

	/** Guarda as modalidades de educa��o da UFRN (presencial e a distancia) */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_modalidade_educacao")
	private ModalidadeEducacao modalidadeEducacao = new ModalidadeEducacao();

	/** Informa o tipo de curso. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_curso_evento")
	private TipoCursoEventoExtensao tipoCursoEvento = new TipoCursoEventoExtensao();

	// migrado para a inscri��o
//	/** Informa se o curso � gratuito ou n�o. */
//	@Column(name = "cobranca_taxa_matricula")
//	private boolean cobrancaTaxaMatricula;
//
//	/**  Taxa cobrada ao se matricular no curso. */
//	@Column(name = "taxa_matricula")
//	private Double taxaMatricula = 0.0;
//
//	/**  A data de vencimento para a emiss�o da GRU, s� usado caso seja cobrada uma taxa de matr�cula. 
//	 * Caso seja cobrado e esse valor n�o seja informado, a data de vencimento ser� por padr�o  data de in�cio do evento. */
//	@Column(name = "data_vencimento_gru", nullable=true)
//	private Date dataVencimentoGRU = null;
	
	
	
	
	
	public CursoEventoExtensao() {
	}

	public CursoEventoExtensao(int id) {
		this.id = id;
	}

	/**
	 * Informa se a Carga Hor�ria atual atende a as exig�ncias
	 * de Carga Hor�ria m�nima necess�ria para cria��o do curso
	 * ou evento de extens�o.
	 * 
	 * @return
	 */
	public boolean isCargaHorariaValida() {
		return getCargaHoraria() != null
		&& getTipoCursoEvento() != null
		&& (getTipoCursoEvento().getChMinima() == null || getCargaHoraria() >= getTipoCursoEvento()
				.getChMinima());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Informado durante o cadastro de 
	 * relat�rio parcial e final
	 * Atualizado no processamento do relat�rio
	 * 
	 * @return
	 */
	public Integer getNumeroConcluintes() {
		return this.numeroConcluintes;
	}

	public void setNumeroConcluintes(Integer numeroConcluintes) {
		this.numeroConcluintes = numeroConcluintes;
	}

	public Integer getNumeroVagas() {
		return this.numeroVagas;
	}

	public void setNumeroVagas(Integer numeroVagas) {
		this.numeroVagas = numeroVagas;
	}

	public Integer getCargaHoraria() {
		return this.cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public String getProgramacao() {
		return this.programacao;
	}

	public void setProgramacao(String programacao) {
		this.programacao = programacao;
	}

	public ModalidadeEducacao getModalidadeEducacao() {
		return modalidadeEducacao;
	}

	public void setModalidadeEducacao(ModalidadeEducacao modalidadeEducacao) {
		this.modalidadeEducacao = modalidadeEducacao;
	}

	public String getResumo() {
		return this.resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public char getTipo() {
		return this.tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	public TipoCursoEventoExtensao getTipoCursoEvento() {
		return this.tipoCursoEvento;
	}

	public void setTipoCursoEvento(TipoCursoEventoExtensao tipoCursoEvento) {
		this.tipoCursoEvento = tipoCursoEvento;
	}

//	public boolean isCobrancaTaxaMatricula() {
//		return this.cobrancaTaxaMatricula;
//	}
//
//	public void setCobrancaTaxaMatricula(boolean cobrancaTaxaMatricula) {
//		this.cobrancaTaxaMatricula = cobrancaTaxaMatricula;
//	}
//
//	public Double getTaxaMatricula() {
//		return this.taxaMatricula;
//	}
	
//	public void setTaxaMatricula(Double taxaMatricula) {
//		this.taxaMatricula = taxaMatricula;
//	}

	public ListaMensagens validate() {
		return null;
	}
	
	
}
