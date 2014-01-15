/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Contém informações específicas sobre uma ação de extensão do tipo curso ou
 * evento. <br/>
 * Os Cursos: <br/>
 * Caracterizam-se como atividade de ensino extracurricular que se propõe a
 * transmitir os conhecimentos produzidos na Universidade, ou fora dela, de
 * forma presencial ou à distância, que venham a contribuir para uma melhor
 * articulação entre o saber acadêmico e as praticas sociais. <br/>
 * Quando se tratar de cursos de extensão semi-presencial ou a distancia, o
 * Projeto do Curso deverá ser submetido à apreciação da Secretaria de Educação
 * à Distância, que emitirá parecer. <br/>
 * Para a expedição de certificados, o coordenador do curso encaminhará à
 * Pro-reitoria de Extensão Universitária relatório circunstanciado das
 * atividades, juntamente com mapa de apuração da assiduidade e dos resultados
 * da avaliação, nas diversas disciplinas, quando for o caso, devidamente
 * apreciado pela plenária do seu departamento (Art. 14 da Res.
 * 074/2004-CONSEPE). <br/>
 * Os Cursos de Extensão poderão ser propostos e promovidos por Unidades
 * Acadêmicas, Base ou Núcleo de Pesquisa, grupo de professores, ou orgão da
 * universidade, podendo ser realizados por mais de uma delas, ou em colaboração
 * com entidades públicas ou privadas (Art 16 da Res. 074/2004 - COSEPE). <br/>
 * Os cursos de Extensão poderão cobrar taxas de inscrição para cobrir, total ou
 * parcialmente, os seus custos, de acordo com as normas em vigor na UFRN (Art
 * 17 da Res. 074/2004 - CONSEPE). <br/>
 * Além das modalidades semi-presencial e à distancia, são modalidades dos
 * Cursos de Extensão: Cursos de Divulgação, Cursos de Atualização e Cursos de
 * Capacitação. <br/>
 * Os cursos de Divulgação, Atualização ou Capacitação podem integralizar a
 * carga horária docente, desde que sejam observados os critérios de duração e
 * controle acadêmico equivalentes aos das disciplinas regulares de graduação ou
 * pós-graduação, nos termos da Resolução nº 023/92 - CONSEP de 04.02.92. <br/>
 * Os Eventos: <br/>
 * São eventos, para efeitos de registro de atividades de extensão, ações de
 * interesse técnico, social, científico, artístico, esportivo, que congreguem
 * pessoas em torno de objetivos específicos (Art 18 da Res. 074/2004 -
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

	/** Número total de concluintes deste curso. */
	@Column(name = "numero_concluintes")
	private Integer numeroConcluintes;

	/** 
	 *  <p>Previsão do Número total de vagas deste curso/evento.</p>
	 * 
	 *  <p>Esse número é informado no momento do cadastro do curso ou evento e aparece de novo 
	 *  quando o coordenador vai abrir as inscrições do curso e evento. </p>
	 *  
	 *  <p>Para saber o número de inscrições permitidas para o curso é usado outra variável "quantidadeVagas" em InscricaoAtividade.</p>
	 *  
	 */
	@Column(name = "numero_vagas")
	private Integer numeroVagas;

	/** Carga horária do curso. 
	 *  Usada para gerar a carga horária nos certificados a partir da formula = ( CH * fequencia do participante )
	 */
	@Column(name = "carga_horaria")
	private Integer cargaHoraria;

	/** Resumo do curso. */
	@Column(name = "resumo")
	private String resumo;

	/** Programação do curso. */
	@Column(name = "programacao")
	private String programacao;

	/** Guarda as modalidades de educação da UFRN (presencial e a distancia) */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_modalidade_educacao")
	private ModalidadeEducacao modalidadeEducacao = new ModalidadeEducacao();

	/** Informa o tipo de curso. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_curso_evento")
	private TipoCursoEventoExtensao tipoCursoEvento = new TipoCursoEventoExtensao();

	// migrado para a inscrição
//	/** Informa se o curso é gratuito ou não. */
//	@Column(name = "cobranca_taxa_matricula")
//	private boolean cobrancaTaxaMatricula;
//
//	/**  Taxa cobrada ao se matricular no curso. */
//	@Column(name = "taxa_matricula")
//	private Double taxaMatricula = 0.0;
//
//	/**  A data de vencimento para a emissão da GRU, só usado caso seja cobrada uma taxa de matrícula. 
//	 * Caso seja cobrado e esse valor não seja informado, a data de vencimento será por padrão  data de início do evento. */
//	@Column(name = "data_vencimento_gru", nullable=true)
//	private Date dataVencimentoGRU = null;
	
	
	
	
	
	public CursoEventoExtensao() {
	}

	public CursoEventoExtensao(int id) {
		this.id = id;
	}

	/**
	 * Informa se a Carga Horária atual atende a as exigências
	 * de Carga Horária mínima necessária para criação do curso
	 * ou evento de extensão.
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
	 * relatório parcial e final
	 * Atualizado no processamento do relatório
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
