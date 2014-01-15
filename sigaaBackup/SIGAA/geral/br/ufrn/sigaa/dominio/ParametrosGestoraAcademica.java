/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 03/11/2006
 *
 */
package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;

/**
 * Classe para guardar informa��es globais referentes a uma unidade gestora
 * acad�mica.
 *
 * @author David Ricardo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "parametros_gestora_academica", schema = "ensino")
public class ParametrosGestoraAcademica implements Validatable {


	/** Chave prim�ria dos {@link ParametrosGestoraAcademica} */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") }) 
	private int id;

	/** Unidade em que os par�metros est�o definidos. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_unidade")
	private Unidade unidade;

	/** {@link ModalidadeEducacao} para a qual os par�metros s�o v�lidos. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_modalidade")
	private ModalidadeEducacao modalidade;

	/** {@link ConvenioAcademico} para o qual os par�metros s�o v�lidos. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_convenio")
	private ConvenioAcademico convenio;

	/** {@link Curso} para o qual os par�metros s�o v�lidos. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	private Curso curso;

	/** M�todo de Avalia��o: 1 - Nota; 2 - Conceito; 3 - Compet�ncia */
	@Column(name = "metodo_avaliacao")
	private Integer metodoAvaliacao;

	/** Nome do arquivo modelo referente ao certificado de conclus�o. */
	private String certificado;

	/**
	 * N�mero m�ximo de trancamentos de PROGRAMA
	 */
	@Column(name = "max_trancamentos")
	private Integer maxTrancamentos;

	/**
	 * N�mero m�ximo de trancamentos de MATRICULA
	 */
	@Column(name = "max_trancamentos_matricula")
	private Integer maxTrancamentosMatricula;

	/** N�mero m�ximo de reprova��es que um discente pode ter em um mesmo componente curricular. */
	@Column(name = "max_reprovacoes")
	private Integer maxReprovacoes;

	/** N�mero m�ximo de cr�ditos obtidos atrav�s de matr�culas em componentes curriculares extra-curriculares que um discente pode ter. */
	@Column(name = "max_creditos_extra")
	private Integer maxCreditosExtra;
	
	/** N�mero m�nimo de cr�ditos obtidos atrav�s de matr�culas em componentes curriculares extra-curriculares que um discente pode ter. */
	@Column(name = "min_creditos_extra")
	private Integer minCreditosExtra;	

	/** N�vel de ensino ao qual os par�metros se referem. */
	private Character nivel;

	/** Define o in�cio da faixa de matr�culas. */
	@Column(name = "inicio_faixa_matricula")
	private Integer inicioFaixaMatricula;

	/**
	 * Caracteriza se o choque de hor�rio ser� impedido para docentes
	 */
	@Column(name="impede_choque_horario")
	private boolean impedeChoqueHorarios = false;

	/**
	 * Permite que uma turma de 100h, tenha dois professores com 100h alocados,
	 * por exemplo.
	 */
	@Column(name="permite_ch_compartilhada")
	private boolean permiteChCompartilhada = false;

	/**
	 * M�dia m�nima para aprova��o nas disciplinas
	 */
	@Column(name="media_minima_aprovacao")
	private Float mediaMinimaAprovacao;

	/**
	 * M�dia m�nima para aprova��o por m�dia (sem precisar de recupera��o) nas disciplinas
	 */
	@Column(name="media_minima_passar_por_media")
	private Float mediaMinimaPassarPorMedia;

	/**
	 * Frequ�ncia m�nima para aprova��o (em porcentagem)
	 */
	@Column(name="frequencia_minima")
	private Float frequenciaMinima;

	/**
	 * Quantidade de avalia��es m�ximas por componente curricular
	 */
	@Column(name="qtd_avaliacoes")
	private Short qtdAvaliacoes;

	/**
	 * Equival�ncia entre os cr�ditos e a hora de AULA
	 */
	@Column(name = "horas_creditos_aula")
	private Short horasCreditosAula;

	/**
	 * Equival�ncia entre os cr�ditos e a hora de LABORATORIO
	 */
	@Column(name = "horas_creditos_lab")
	private Short horasCreditosLaboratorio;

	/**
	 * Equival�ncia entre os cr�ditos e a hora de ESTAGIO
	 */
	@Column(name = "horas_creditos_estagio")
	private Short horasCreditosEstagio;

	/**
	 * Pesos das avalia��es para a unidade, separados por v�rgulas
	 */
	@Column(name = "pesos_avaliacoes")
	private String pesosAvaliacoes;
	
	/**
	 * Pesos das avalia��es para a unidade, separados por v�rgulas
	 */
	@Column(name = "pesos_avaliacoes_duas_unidades")
	private String pesosAvaliacoes2Unidades;
	
	
	/**
	 * Utilizado para poder dar pesos espec�ficos para a m�dia sem recupera��o(MediaSemRec) e 
	 * para a recupera��o(Rec) no calculo da m�dia final(MF). Por exemplo, em uma IFES ou Unidade a MediaSemRec e a Rec podem ter
	 * pesos iguais para ambas as notas, por exemplo ambas com peso 10, logo o c�lculo  seria
	 * feito assim: MF = ( ( 10*MediaSemRec + 10*Rec ) / ( 10 + 10 ) ).
	 * Mas em outra IFES ou Unidade o peso da recupera��o pode ser menor ou maior! 
	 * Por exemplo, suponha que os pesos da mediaSemRec e peso da Rec sejam respectivamente 70 e 30. 
	 * O calculo seria feito assim: MF = ( ( 70*MediaSemRec + 30*Rec ) / ( 70 + 30 ) ).
	 * O primeiro inteiro da String ser� o peso da MediaSemRec e o segundo inteiro ser� a o peso da Rec.
	 */
	@Column(name = "peso_media_recuperacao")
	private String pesoMediaRecuperacao;

	/**
	 * numero m�ximo de disciplinas que um aluno especial pode cursar por per�odo
	 */
	@Column(name = "max_disciplinas_aluno_especial")
	private Integer maxDisciplinasAlunoEspecial;

	/**
	 * numero m�ximo de per�odos consecutivos ou n�o que um aluno especial pode cursar
	 */
	@Column(name = "max_periodos_aluno_especial")
	private Integer maxPeriodosAlunoEspecial;

	/**
	 * numero m�ximo de dias que uma solicita��o de trancamento fica pendentes at� que seja efetivada!
	 */
	@Column(name = "tempo_solicitacao_trancamento")
	private Integer tempoSolicitacaoTrancamento;

	/** Define se deve solicitar aos discentes a cada per�odo de matr�cula online que atualizem seus dados pessoais de contato. */
	@Column(name = "solicitar_atualizacao_dados_matricula")
	private boolean solicitarAtualizacaoDadosMatricula = false; 
	
	/**
	 * Verifica se a gestora acad�mica atribuir� nota e frequ�ncia para aproveitamento de estudo.
	 */
	@Column(name = "exige_nota_aproveitamento")
	private boolean exigeNotaAproveitamento = false;
	
	/** Percentual cursado m�ximo para trancamento da matr�cula no componente curricular. */
	@Column(name = "perc_max_cumprido_trancamento")
	private Float percentualMaximoCumpridoTrancamento;
	
	/**
	 * Indica se a gestora acad�mica utiliza ou n�o reprova��o por bloco.
	 */
	@Column(name = "reprovacao_bloco")
	private boolean reprovacaoBloco = false;
	
	/** Define se permite recupera��o. */
	@Column(name = "permite_recuperacao")
	private boolean permiteRecuperacao = false;
	
	/** M�dia m�nima para as turmas. */
	@Column(name = "media_minima_possibilita_recuperacao")
	private Float mediaMinimaPossibilitaRecuperacao;
	
	/** Carga Hor�ria Total m�xima de um componente curricular para a cria��o de turmas de f�rias. */
	@Column(name = "ch_maxima_turma_ferias")
	private Integer chMaximaTurmaFerias;
	
	/** Indica se deve validar quantidade de letras */
	@Column(name="valida_qtd_letras_codigo")
	private boolean validaQtdLetrasCodigo = false;
	
	/** Indica se permite que o aluno possa trancar todas as disciplinas matriculadas */
	@Column(name="permite_trancar_todas_disciplinas")
	private boolean permiteTrancarTodasDisciplinas = false;
	
	/** Indica se a gestora acad�mica utiliza especializa��es para turmas de entrada. */
	@Column(name="especializacao_turma_entrada")
	private boolean especializacaoTurmaEntrada = false;
	
	/** Indica o total m�ximo de per�odos regulares de uma gestora.*/
	@Column(name="quantidade_periodos_regulares")
	private Integer quantidadePeriodosRegulares;
	
	/** Quantidade de minutos de uma aula regular, utilizado no c�lculo de n�mero de aulas de uma turma. Ex.: 60, 50, 45. */
	@Column(name="minutos_aula_regular")
	private int minutosAulaRegular;
	
	/** Telefone de contato da unidade */
	@Column(name = "telefone_contato")
	private String telefoneContato;

	/** Email de contato da unidade */
	@Column(name = "email_contato")
	private String emailContato;
	
	/** Indica se a unidade pode ofertar turmas nos hor�rios do dia de domingo. */
	@Column(name="habilitar_horarios_domingo")
	private boolean habilitarHorariosDomingo = false;
	
	public ParametrosGestoraAcademica(int i) {
		id = i;
	}

	public ParametrosGestoraAcademica() {
	}

	public Short getHorasCreditosEstagio() {
		return horasCreditosEstagio;
	}

	public void setHorasCreditosEstagio(Short horasCreditosEstagio) {
		this.horasCreditosEstagio = horasCreditosEstagio;
	}

	public Short getHorasCreditosLaboratorio() {
		return horasCreditosLaboratorio;
	}

	public void setHorasCreditosLaboratorio(Short horasCreditosLaboratorio) {
		this.horasCreditosLaboratorio = horasCreditosLaboratorio;
	}

	public Short getQtdAvaliacoes() {
		return qtdAvaliacoes;
	}

	public void setQtdAvaliacoes(Short qtdAvaliacoes) {
		this.qtdAvaliacoes = qtdAvaliacoes;
	}

	public Integer getMaxReprovacoes() {
		return maxReprovacoes;
	}

	public void setMaxReprovacoes(Integer maxReprovacoes) {
		this.maxReprovacoes = maxReprovacoes;
	}

	public Integer getMaxTrancamentos() {
		return maxTrancamentos;
	}

	public void setMaxTrancamentos(Integer maxTrancamentos) {
		this.maxTrancamentos = maxTrancamentos;
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getMetodoAvaliacao() {
		return metodoAvaliacao;
	}

	public void setMetodoAvaliacao(Integer metodoAvaliacao) {
		this.metodoAvaliacao = metodoAvaliacao;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getCertificado() {
		return certificado;
	}

	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}

	public boolean isImpedeChoqueHorarios() {
		return impedeChoqueHorarios;
	}

	public void setImpedeChoqueHorarios(boolean impedeChoqueHorarios) {
		this.impedeChoqueHorarios = impedeChoqueHorarios;
	}

	public Float getMediaMinimaAprovacao() {
		return mediaMinimaAprovacao;
	}

	public void setMediaMinimaAprovacao(Float mediaMinimaAprovacao) {
		this.mediaMinimaAprovacao = mediaMinimaAprovacao;
	}

	public boolean isPermiteChCompartilhada() {
		return permiteChCompartilhada;
	}

	public void setPermiteChCompartilhada(boolean permiteChCompartilhada) {
		this.permiteChCompartilhada = permiteChCompartilhada;
	}

	public Short getHorasCreditosAula() {
		return horasCreditosAula;
	}

	public void setHorasCreditosAula(Short horasCreditos) {
		this.horasCreditosAula = horasCreditos;
	}

	public Integer getInicioFaixaMatricula() {
		return inicioFaixaMatricula;
	}

	public void setInicioFaixaMatricula(Integer inicioFaixaMatricula) {
		this.inicioFaixaMatricula = inicioFaixaMatricula;
	}

	public String getPesosAvaliacoes() {
		return pesosAvaliacoes;
	}

	/**
	 * Os pesos das avalia��es est�o guardados como uma
	 * String separados por v�rgula. Este m�todo retorna todos
	 * os pesos em um array de Strings
	 * @return
	 */
	public String[] getArrayPesosAvaliacoes() {
		String[] arrayPesos = null;
		
		if (pesosAvaliacoes != null && !pesosAvaliacoes.trim().equals(""))
			arrayPesos = pesosAvaliacoes.split(",");

		return arrayPesos;
	}
	
	/**
	 * Os pesos das avalia��es est�o guardados como uma
	 * String separados por v�rgula. Este m�todo retorna todos
	 * os pesos em um array de Strings
	 * @return
	 */
	public String[] getArrayPesosAvaliacoes2Unidades() {
		String[] arrayPesos = null;
		
		if (pesosAvaliacoes2Unidades != null && !pesosAvaliacoes2Unidades.trim().equals(""))
			arrayPesos = pesosAvaliacoes2Unidades.split(",");

		return arrayPesos;
	}
	
	/**
	 * Os pesos das avalia��es est�o guardados como uma
	 * String separados por v�rgula. Este m�todo retorna todos
	 * os pesos em um array de Strings
	 * @return
	 */
	public String[] getArrayPesosMediaRec() {
		String[] arrayPesos = null;

		if (pesoMediaRecuperacao != null && !pesoMediaRecuperacao.trim().equals(""))
			arrayPesos = pesoMediaRecuperacao.split(",");

		return arrayPesos;
	}

	public void setPesosAvaliacoes(String pesosAvaliacoes) {
		this.pesosAvaliacoes = pesosAvaliacoes;
	}

	public Float getFrequenciaMinima() {
		return frequenciaMinima;
	}

	public void setFrequenciaMinima(Float frequenciaMinima) {
		this.frequenciaMinima = frequenciaMinima;
	}

	public Integer getMaxTrancamentosMatricula() {
		return maxTrancamentosMatricula;
	}

	public void setMaxTrancamentosMatricula(Integer maxTrancamentosMatricula) {
		this.maxTrancamentosMatricula = maxTrancamentosMatricula;
	}

	public Integer getMaxDisciplinasAlunoEspecial() {
		return maxDisciplinasAlunoEspecial;
	}

	public void setMaxDisciplinasAlunoEspecial(Integer maxDisciplinasAlunoEspecial) {
		this.maxDisciplinasAlunoEspecial = maxDisciplinasAlunoEspecial;
	}

	public Integer getMaxPeriodosAlunoEspecial() {
		return maxPeriodosAlunoEspecial;
	}

	public void setMaxPeriodosAlunoEspecial(Integer maxPeriodosAlunoEspecial) {
		this.maxPeriodosAlunoEspecial = maxPeriodosAlunoEspecial;
	}

	public Integer getMaxCreditosExtra() {
		return maxCreditosExtra;
	}

	public void setMaxCreditosExtra(Integer maxCreditosExtra) {
		this.maxCreditosExtra = maxCreditosExtra;
	}

	public Integer getTempoSolicitacaoTrancamento() {
		return tempoSolicitacaoTrancamento;
	}

	public void setTempoSolicitacaoTrancamento(Integer tempoSolicitacaoTrancamento) {
		this.tempoSolicitacaoTrancamento = tempoSolicitacaoTrancamento;
	}

	public Float getMediaMinimaPassarPorMedia() {
		return mediaMinimaPassarPorMedia;
	}

	public void setMediaMinimaPassarPorMedia(Float mediaMinimaPassarPorMedia) {
		this.mediaMinimaPassarPorMedia = mediaMinimaPassarPorMedia;
	}

	public ModalidadeEducacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ModalidadeEducacao modalidade) {
		this.modalidade = modalidade;
	}

	public ConvenioAcademico getConvenio() {
		return convenio;
	}

	public void setConvenio(ConvenioAcademico convenio) {
		this.convenio = convenio;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna uma descri��o textual do n�vel de ensino.
	 * @return
	 */
	@Transient
	public String getNivelDescr() {
		if (nivel == null)
			return null;
		return NivelEnsino.getDescricao(nivel);
	}

	public boolean getSolicitarAtualizacaoDadosMatricula() {
		return solicitarAtualizacaoDadosMatricula;
	}

	public void setSolicitarAtualizacaoDadosMatricula(
			boolean solicitarAtualizacaoDadosMatricula) {
		this.solicitarAtualizacaoDadosMatricula = solicitarAtualizacaoDadosMatricula;
	}

	public boolean isExigeNotaAproveitamento() {
		return exigeNotaAproveitamento;
	}

	public void setExigeNotaAproveitamento(boolean exigeNotaAproveitamento) {
		this.exigeNotaAproveitamento = exigeNotaAproveitamento;
	}

	public Integer getMinCreditosExtra() {
		return minCreditosExtra;
	}

	public void setMinCreditosExtra(Integer minCreditosExtra) {
		this.minCreditosExtra = minCreditosExtra;
	}

	public Float getPercentualMaximoCumpridoTrancamento() {
		return percentualMaximoCumpridoTrancamento;
	}

	public void setPercentualMaximoCumpridoTrancamento(Float percentualMaximoCumpridoTrancamento) {
		this.percentualMaximoCumpridoTrancamento = percentualMaximoCumpridoTrancamento;
	}

	public boolean isReprovacaoBloco() {
		return reprovacaoBloco;
	}

	public void setReprovacaoBloco(boolean reprovacaoBloco) {
		this.reprovacaoBloco = reprovacaoBloco;
	}

	public boolean isPermiteRecuperacao() {
		return permiteRecuperacao;
	}

	public void setPermiteRecuperacao(boolean permiteRecuperacao) {
		this.permiteRecuperacao = permiteRecuperacao;
	}

	public Float getMediaMinimaPossibilitaRecuperacao() {
		return mediaMinimaPossibilitaRecuperacao;
	}

	public void setMediaMinimaPossibilitaRecuperacao(Float mediaMinimaPossibilitaRecuperacao) {
		this.mediaMinimaPossibilitaRecuperacao = mediaMinimaPossibilitaRecuperacao;
	}

	public String getPesoMediaRecuperacao() {
		return pesoMediaRecuperacao;
	}

	public void setPesoMediaRecuperacao(String pesoMediaRecuperacao) {
		this.pesoMediaRecuperacao = pesoMediaRecuperacao;
	}
	
	/**
	 * Realiza a valida��o dos dados da classe e retorna uma lista com os erros encontrados.
	 */
	public ListaMensagens validate() {		
		ListaMensagens erros = new ListaMensagens();		
		
		//Aluno Especial
		if(!nivel.equals(NivelEnsino.TECNICO) && !nivel.equals(NivelEnsino.FORMACAO_COMPLEMENTAR) && !nivel.equals(NivelEnsino.MEDIO)){
			ValidatorUtil.validateRequired(maxDisciplinasAlunoEspecial, "N�mero M�ximo de Disciplinas por per�odo cursadas por Aluno Especial", erros);		
			ValidatorUtil.validateRequired(maxPeriodosAlunoEspecial, "N�mero M�ximo de Per�odos consecutivos ou n�o que um Aluno Especial pode cursar", erros);
		}
		
		//Matr�cula, Trancamentos e Reprova��o em Componentes Curriculares
		ValidatorUtil.validateRequired(percentualMaximoCumpridoTrancamento, "Percentual M�ximo Cumprido para Permitir Trancamento", erros);
		if(percentualMaximoCumpridoTrancamento != null)
			ValidatorUtil.validateRange(new Double(percentualMaximoCumpridoTrancamento), new Double(0.0), new Double(100.0), "Percentual M�ximo Cumprido para Permitir Trancamento", erros);
		ValidatorUtil.validateRequired(maxReprovacoes, "N�mero M�ximo de Reprova��es", erros);
		ValidatorUtil.validateRequired(quantidadePeriodosRegulares, "Quantidade de Per�odos Regulares", erros);
		
		
		//Par�metros Curriculares
		ValidatorUtil.validateRequired(minCreditosExtra, "N�mero M�nimo de Cr�ditos de Extra-Curricular", erros);
		ValidatorUtil.validateRequired(maxCreditosExtra, "N�mero M�ximo de Cr�ditos de Extra-Curricular", erros);
		ValidatorUtil.validateRequired(horasCreditosAula, "Equival�ncia de Cr�dito e Hora/Aula", erros);
		ValidatorUtil.validateRequired(horasCreditosLaboratorio, "Equival�ncia de Cr�dito e Hora/Laborat�rio", erros);
		ValidatorUtil.validateRequired(horasCreditosEstagio, "Equival�ncia de Cr�dito e Hora/Est�gio", erros);
		ValidatorUtil.validateRequired(minutosAulaRegular, "Dura��o de uma Aula Regular", erros);		
		if( !nivel.equals(NivelEnsino.MEDIO) ){
			ValidatorUtil.validateRequired(chMaximaTurmaFerias, "CH Total M�xima do Componente Curricular para Turmas de F�rias", erros);
			ValidatorUtil.validateRequired(maxTrancamentos, "N�mero M�ximo de Trancamentos de PROGRAMA", erros);
			ValidatorUtil.validateRequired(maxTrancamentosMatricula, "N�mero M�ximo de Trancamentos de MATR�CULA", erros);
		}
		//Avalia��o
		ValidatorUtil.validateRequiredId(metodoAvaliacao, "M�todo de Avalia��o", erros);
		ValidatorUtil.validateRequired(mediaMinimaAprovacao, "M�dia M�nima de Aprova��o", erros);
		ValidatorUtil.validateRequired(mediaMinimaPossibilitaRecuperacao, "M�dia M�nima que possibilita recupera��o", erros);
		ValidatorUtil.validateRequired(mediaMinimaPassarPorMedia, "M�dia M�nima de Aprova��o para passar por m�dia", erros);
		ValidatorUtil.validateRequired(frequenciaMinima, "Frequ�ncia M�nima para Aprova��o", erros);
		if(frequenciaMinima != null)
			ValidatorUtil.validateRange(new Double(frequenciaMinima), new Double(0.0), new Double(100.0), "Frequ�ncia M�nima para Aprova��o", erros);
		if( nivel.equals(NivelEnsino.MEDIO) )
			ValidatorUtil.validateRequired(qtdAvaliacoes, "N�mero M�ximo de Avalia��es por Disciplina", erros);
		else
			ValidatorUtil.validateRequired(qtdAvaliacoes, "N�mero M�ximo de Avalia��es por Turma", erros);
		
		ValidatorUtil.validateRequired(pesosAvaliacoes, "Pesos das Avalia��es", erros);
		ValidatorUtil.validateRequired(pesoMediaRecuperacao, "Peso da M�dia e Peso da Recupera��o", erros);
		
		
		//Verifica se o n�mero m�ximo de avalia��es por Turma deve ser igual a quantidade de Pesos das Avalia��es.
		String[] pesosAvaliacoes = getArrayPesosAvaliacoes();		
		if(pesosAvaliacoes != null && qtdAvaliacoes != null && pesosAvaliacoes.length != qtdAvaliacoes) {
			erros.addErro("O N�mero M�ximo de Avalia��es por Turma deve ser igual a quantidade de Pesos das Avalia��es.");
		}		
		
		//Verifica se algum peso foi informado zero ou com caracteres especiais
		String chars = "`~!@#$%^&*()=+[{]}|\\\'\".:;/?,<>�������������Ǿ������Ķ��Ϸ�������������";
		if(pesosAvaliacoes != null)
			for(String peso: pesosAvaliacoes){
				for (int i = 0; i < chars.length(); i++) {
					if (peso.indexOf(chars.charAt(i)) != -1) {
						peso = "";
					}
				}
				if(peso.length() == 0 || Integer.parseInt(peso) == 0)
					erros.addErro("Todos os pesos devem possuir valores maiores que zero.");
			}
		//Verifica se os valores informados para o peso da m�dia sem recupera��o e o peso da recupera��o esta dentro do padr�o.
		String[] pesoMediaPesoRec = getArrayPesosMediaRec();		
		if( pesoMediaPesoRec != null) {
			if(pesoMediaPesoRec.length == 2){
				Integer pesoMedia = new Integer(pesoMediaPesoRec[0].trim());
				Integer pesoRecuperacao = new Integer(pesoMediaPesoRec[1].trim());			
				Integer somaPesos = pesoMedia + pesoRecuperacao;
			
				if(somaPesos != 100) {
					erros.addErro("Por conven��o, a soma dos pesos referente a 'm�dia sem recupera��o' e a 'recupera��o' deve ser igual a 100. Por favor, altere o campo 'Peso da M�dia e Peso da Recupera��o' de forma que a soma dos pesos seja igual a 100.");
				}			
			}
			else {
				erros.addErro("Dois pesos devem ser digitados no campo 'Peso da M�dia e Peso da Recupera��o'");
			}
		}
		
		
		return erros;
		
	}

	public Integer getChMaximaTurmaFerias() {
		return chMaximaTurmaFerias;
	}

	public void setChMaximaTurmaFerias(Integer chMaximaTurmaFerias) {
		this.chMaximaTurmaFerias = chMaximaTurmaFerias;
	}

	/**
	 * Indica se deve validar quantidade de letras
	 * @return the validaQtdLetrasCodigo
	 */
	public boolean isValidaQtdLetrasCodigo() {
		return validaQtdLetrasCodigo;
	}

	/**
	 * Indica se deve validar quantidade de letras 
	 * @param validaQtdLetrasCodigo the validaQtdLetrasCodigo to set
	 */
	public void setValidaQtdLetrasCodigo(boolean validaQtdLetrasCodigo) {
		this.validaQtdLetrasCodigo = validaQtdLetrasCodigo;
	}

	public boolean isPermiteTrancarTodasDisciplinas() {
		return permiteTrancarTodasDisciplinas;
	}

	public void setPermiteTrancarTodasDisciplinas(
			boolean permiteTrancarTodasDisciplinas) {
		this.permiteTrancarTodasDisciplinas = permiteTrancarTodasDisciplinas;
	}

	public boolean isEspecializacaoTurmaEntrada() {
		return especializacaoTurmaEntrada;
	}

	public void setEspecializacaoTurmaEntrada(boolean especializacaoTurmaEntrada) {
		this.especializacaoTurmaEntrada = especializacaoTurmaEntrada;
	}

	public Integer getQuantidadePeriodosRegulares() {
		return quantidadePeriodosRegulares;
	}

	public void setQuantidadePeriodosRegulares(Integer quantidadePeriodosRegulares) {
		this.quantidadePeriodosRegulares = quantidadePeriodosRegulares;
	}

	public void setPesosAvaliacoes2Unidades(String pesosAvaliacoes2Unidades) {
		this.pesosAvaliacoes2Unidades = pesosAvaliacoes2Unidades;
	}

	public String getPesosAvaliacoes2Unidades() {
		return pesosAvaliacoes2Unidades;
	}

	public Integer getMinutosAulaRegular() {
		return minutosAulaRegular;
	}

	public void setMinutosAulaRegular(Integer minutosAulaRegular) {
		this.minutosAulaRegular = minutosAulaRegular;
	}

	public String getTelefoneContato() {
		return telefoneContato;
	}

	public void setTelefoneContato(String telefoneContato) {
		this.telefoneContato = telefoneContato;
	}

	public String getEmailContato() {
		return emailContato;
	}

	public void setEmailContato(String emailContato) {
		this.emailContato = emailContato;
	}
	
	public boolean isHabilitarHorariosDomingo() {
		return habilitarHorariosDomingo;
	}

	public void setHabilitarHorariosDomingo(boolean habilitarHorariosDomingo) {
		this.habilitarHorariosDomingo = habilitarHorariosDomingo;
	}
	
}