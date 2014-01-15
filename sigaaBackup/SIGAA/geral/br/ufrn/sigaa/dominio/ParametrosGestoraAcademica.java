/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
 * Classe para guardar informações globais referentes a uma unidade gestora
 * acadêmica.
 *
 * @author David Ricardo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "parametros_gestora_academica", schema = "ensino")
public class ParametrosGestoraAcademica implements Validatable {


	/** Chave primária dos {@link ParametrosGestoraAcademica} */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") }) 
	private int id;

	/** Unidade em que os parâmetros estão definidos. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_unidade")
	private Unidade unidade;

	/** {@link ModalidadeEducacao} para a qual os parâmetros são válidos. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_modalidade")
	private ModalidadeEducacao modalidade;

	/** {@link ConvenioAcademico} para o qual os parâmetros são válidos. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_convenio")
	private ConvenioAcademico convenio;

	/** {@link Curso} para o qual os parâmetros são válidos. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	private Curso curso;

	/** Método de Avaliação: 1 - Nota; 2 - Conceito; 3 - Competência */
	@Column(name = "metodo_avaliacao")
	private Integer metodoAvaliacao;

	/** Nome do arquivo modelo referente ao certificado de conclusão. */
	private String certificado;

	/**
	 * Número máximo de trancamentos de PROGRAMA
	 */
	@Column(name = "max_trancamentos")
	private Integer maxTrancamentos;

	/**
	 * Número máximo de trancamentos de MATRICULA
	 */
	@Column(name = "max_trancamentos_matricula")
	private Integer maxTrancamentosMatricula;

	/** Número máximo de reprovações que um discente pode ter em um mesmo componente curricular. */
	@Column(name = "max_reprovacoes")
	private Integer maxReprovacoes;

	/** Número máximo de créditos obtidos através de matrículas em componentes curriculares extra-curriculares que um discente pode ter. */
	@Column(name = "max_creditos_extra")
	private Integer maxCreditosExtra;
	
	/** Número mínimo de créditos obtidos através de matrículas em componentes curriculares extra-curriculares que um discente pode ter. */
	@Column(name = "min_creditos_extra")
	private Integer minCreditosExtra;	

	/** Nível de ensino ao qual os parâmetros se referem. */
	private Character nivel;

	/** Define o início da faixa de matrículas. */
	@Column(name = "inicio_faixa_matricula")
	private Integer inicioFaixaMatricula;

	/**
	 * Caracteriza se o choque de horário será impedido para docentes
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
	 * Média mínima para aprovação nas disciplinas
	 */
	@Column(name="media_minima_aprovacao")
	private Float mediaMinimaAprovacao;

	/**
	 * Média mínima para aprovação por média (sem precisar de recuperação) nas disciplinas
	 */
	@Column(name="media_minima_passar_por_media")
	private Float mediaMinimaPassarPorMedia;

	/**
	 * Frequência mínima para aprovação (em porcentagem)
	 */
	@Column(name="frequencia_minima")
	private Float frequenciaMinima;

	/**
	 * Quantidade de avaliações máximas por componente curricular
	 */
	@Column(name="qtd_avaliacoes")
	private Short qtdAvaliacoes;

	/**
	 * Equivalência entre os créditos e a hora de AULA
	 */
	@Column(name = "horas_creditos_aula")
	private Short horasCreditosAula;

	/**
	 * Equivalência entre os créditos e a hora de LABORATORIO
	 */
	@Column(name = "horas_creditos_lab")
	private Short horasCreditosLaboratorio;

	/**
	 * Equivalência entre os créditos e a hora de ESTAGIO
	 */
	@Column(name = "horas_creditos_estagio")
	private Short horasCreditosEstagio;

	/**
	 * Pesos das avaliações para a unidade, separados por vírgulas
	 */
	@Column(name = "pesos_avaliacoes")
	private String pesosAvaliacoes;
	
	/**
	 * Pesos das avaliações para a unidade, separados por vírgulas
	 */
	@Column(name = "pesos_avaliacoes_duas_unidades")
	private String pesosAvaliacoes2Unidades;
	
	
	/**
	 * Utilizado para poder dar pesos específicos para a média sem recuperação(MediaSemRec) e 
	 * para a recuperação(Rec) no calculo da média final(MF). Por exemplo, em uma IFES ou Unidade a MediaSemRec e a Rec podem ter
	 * pesos iguais para ambas as notas, por exemplo ambas com peso 10, logo o cálculo  seria
	 * feito assim: MF = ( ( 10*MediaSemRec + 10*Rec ) / ( 10 + 10 ) ).
	 * Mas em outra IFES ou Unidade o peso da recuperação pode ser menor ou maior! 
	 * Por exemplo, suponha que os pesos da mediaSemRec e peso da Rec sejam respectivamente 70 e 30. 
	 * O calculo seria feito assim: MF = ( ( 70*MediaSemRec + 30*Rec ) / ( 70 + 30 ) ).
	 * O primeiro inteiro da String será o peso da MediaSemRec e o segundo inteiro será a o peso da Rec.
	 */
	@Column(name = "peso_media_recuperacao")
	private String pesoMediaRecuperacao;

	/**
	 * numero máximo de disciplinas que um aluno especial pode cursar por período
	 */
	@Column(name = "max_disciplinas_aluno_especial")
	private Integer maxDisciplinasAlunoEspecial;

	/**
	 * numero máximo de períodos consecutivos ou não que um aluno especial pode cursar
	 */
	@Column(name = "max_periodos_aluno_especial")
	private Integer maxPeriodosAlunoEspecial;

	/**
	 * numero máximo de dias que uma solicitação de trancamento fica pendentes até que seja efetivada!
	 */
	@Column(name = "tempo_solicitacao_trancamento")
	private Integer tempoSolicitacaoTrancamento;

	/** Define se deve solicitar aos discentes a cada período de matrícula online que atualizem seus dados pessoais de contato. */
	@Column(name = "solicitar_atualizacao_dados_matricula")
	private boolean solicitarAtualizacaoDadosMatricula = false; 
	
	/**
	 * Verifica se a gestora acadêmica atribuirá nota e frequência para aproveitamento de estudo.
	 */
	@Column(name = "exige_nota_aproveitamento")
	private boolean exigeNotaAproveitamento = false;
	
	/** Percentual cursado máximo para trancamento da matrícula no componente curricular. */
	@Column(name = "perc_max_cumprido_trancamento")
	private Float percentualMaximoCumpridoTrancamento;
	
	/**
	 * Indica se a gestora acadêmica utiliza ou não reprovação por bloco.
	 */
	@Column(name = "reprovacao_bloco")
	private boolean reprovacaoBloco = false;
	
	/** Define se permite recuperação. */
	@Column(name = "permite_recuperacao")
	private boolean permiteRecuperacao = false;
	
	/** Média mínima para as turmas. */
	@Column(name = "media_minima_possibilita_recuperacao")
	private Float mediaMinimaPossibilitaRecuperacao;
	
	/** Carga Horária Total máxima de um componente curricular para a criação de turmas de férias. */
	@Column(name = "ch_maxima_turma_ferias")
	private Integer chMaximaTurmaFerias;
	
	/** Indica se deve validar quantidade de letras */
	@Column(name="valida_qtd_letras_codigo")
	private boolean validaQtdLetrasCodigo = false;
	
	/** Indica se permite que o aluno possa trancar todas as disciplinas matriculadas */
	@Column(name="permite_trancar_todas_disciplinas")
	private boolean permiteTrancarTodasDisciplinas = false;
	
	/** Indica se a gestora acadêmica utiliza especializações para turmas de entrada. */
	@Column(name="especializacao_turma_entrada")
	private boolean especializacaoTurmaEntrada = false;
	
	/** Indica o total máximo de períodos regulares de uma gestora.*/
	@Column(name="quantidade_periodos_regulares")
	private Integer quantidadePeriodosRegulares;
	
	/** Quantidade de minutos de uma aula regular, utilizado no cálculo de número de aulas de uma turma. Ex.: 60, 50, 45. */
	@Column(name="minutos_aula_regular")
	private int minutosAulaRegular;
	
	/** Telefone de contato da unidade */
	@Column(name = "telefone_contato")
	private String telefoneContato;

	/** Email de contato da unidade */
	@Column(name = "email_contato")
	private String emailContato;
	
	/** Indica se a unidade pode ofertar turmas nos horários do dia de domingo. */
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
	 * Os pesos das avaliações estão guardados como uma
	 * String separados por vírgula. Este método retorna todos
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
	 * Os pesos das avaliações estão guardados como uma
	 * String separados por vírgula. Este método retorna todos
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
	 * Os pesos das avaliações estão guardados como uma
	 * String separados por vírgula. Este método retorna todos
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

	/** Retorna uma descrição textual do nível de ensino.
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
	 * Realiza a validação dos dados da classe e retorna uma lista com os erros encontrados.
	 */
	public ListaMensagens validate() {		
		ListaMensagens erros = new ListaMensagens();		
		
		//Aluno Especial
		if(!nivel.equals(NivelEnsino.TECNICO) && !nivel.equals(NivelEnsino.FORMACAO_COMPLEMENTAR) && !nivel.equals(NivelEnsino.MEDIO)){
			ValidatorUtil.validateRequired(maxDisciplinasAlunoEspecial, "Número Máximo de Disciplinas por período cursadas por Aluno Especial", erros);		
			ValidatorUtil.validateRequired(maxPeriodosAlunoEspecial, "Número Máximo de Períodos consecutivos ou não que um Aluno Especial pode cursar", erros);
		}
		
		//Matrícula, Trancamentos e Reprovação em Componentes Curriculares
		ValidatorUtil.validateRequired(percentualMaximoCumpridoTrancamento, "Percentual Máximo Cumprido para Permitir Trancamento", erros);
		if(percentualMaximoCumpridoTrancamento != null)
			ValidatorUtil.validateRange(new Double(percentualMaximoCumpridoTrancamento), new Double(0.0), new Double(100.0), "Percentual Máximo Cumprido para Permitir Trancamento", erros);
		ValidatorUtil.validateRequired(maxReprovacoes, "Número Máximo de Reprovações", erros);
		ValidatorUtil.validateRequired(quantidadePeriodosRegulares, "Quantidade de Períodos Regulares", erros);
		
		
		//Parâmetros Curriculares
		ValidatorUtil.validateRequired(minCreditosExtra, "Número Mínimo de Créditos de Extra-Curricular", erros);
		ValidatorUtil.validateRequired(maxCreditosExtra, "Número Máximo de Créditos de Extra-Curricular", erros);
		ValidatorUtil.validateRequired(horasCreditosAula, "Equivalência de Crédito e Hora/Aula", erros);
		ValidatorUtil.validateRequired(horasCreditosLaboratorio, "Equivalência de Crédito e Hora/Laboratório", erros);
		ValidatorUtil.validateRequired(horasCreditosEstagio, "Equivalência de Crédito e Hora/Estágio", erros);
		ValidatorUtil.validateRequired(minutosAulaRegular, "Duração de uma Aula Regular", erros);		
		if( !nivel.equals(NivelEnsino.MEDIO) ){
			ValidatorUtil.validateRequired(chMaximaTurmaFerias, "CH Total Máxima do Componente Curricular para Turmas de Férias", erros);
			ValidatorUtil.validateRequired(maxTrancamentos, "Número Máximo de Trancamentos de PROGRAMA", erros);
			ValidatorUtil.validateRequired(maxTrancamentosMatricula, "Número Máximo de Trancamentos de MATRÍCULA", erros);
		}
		//Avaliação
		ValidatorUtil.validateRequiredId(metodoAvaliacao, "Método de Avaliação", erros);
		ValidatorUtil.validateRequired(mediaMinimaAprovacao, "Média Mínima de Aprovação", erros);
		ValidatorUtil.validateRequired(mediaMinimaPossibilitaRecuperacao, "Média Mínima que possibilita recuperação", erros);
		ValidatorUtil.validateRequired(mediaMinimaPassarPorMedia, "Média Mínima de Aprovação para passar por média", erros);
		ValidatorUtil.validateRequired(frequenciaMinima, "Frequência Mínima para Aprovação", erros);
		if(frequenciaMinima != null)
			ValidatorUtil.validateRange(new Double(frequenciaMinima), new Double(0.0), new Double(100.0), "Frequência Mínima para Aprovação", erros);
		if( nivel.equals(NivelEnsino.MEDIO) )
			ValidatorUtil.validateRequired(qtdAvaliacoes, "Número Máximo de Avaliações por Disciplina", erros);
		else
			ValidatorUtil.validateRequired(qtdAvaliacoes, "Número Máximo de Avaliações por Turma", erros);
		
		ValidatorUtil.validateRequired(pesosAvaliacoes, "Pesos das Avaliações", erros);
		ValidatorUtil.validateRequired(pesoMediaRecuperacao, "Peso da Média e Peso da Recuperação", erros);
		
		
		//Verifica se o número máximo de avaliações por Turma deve ser igual a quantidade de Pesos das Avaliações.
		String[] pesosAvaliacoes = getArrayPesosAvaliacoes();		
		if(pesosAvaliacoes != null && qtdAvaliacoes != null && pesosAvaliacoes.length != qtdAvaliacoes) {
			erros.addErro("O Número Máximo de Avaliações por Turma deve ser igual a quantidade de Pesos das Avaliações.");
		}		
		
		//Verifica se algum peso foi informado zero ou com caracteres especiais
		String chars = "`~!@#$%^&*()=+[{]}|\\\'\".:;/?,<>Áª£¢°¤¦¥»¼­ÒÔÇ¾ÉÂûÆú©Ä¶§ŒÏ·«¨ ´¬ö¿¹³²µ÷º";
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
		//Verifica se os valores informados para o peso da média sem recuperação e o peso da recuperação esta dentro do padrão.
		String[] pesoMediaPesoRec = getArrayPesosMediaRec();		
		if( pesoMediaPesoRec != null) {
			if(pesoMediaPesoRec.length == 2){
				Integer pesoMedia = new Integer(pesoMediaPesoRec[0].trim());
				Integer pesoRecuperacao = new Integer(pesoMediaPesoRec[1].trim());			
				Integer somaPesos = pesoMedia + pesoRecuperacao;
			
				if(somaPesos != 100) {
					erros.addErro("Por convenção, a soma dos pesos referente a 'média sem recuperação' e a 'recuperação' deve ser igual a 100. Por favor, altere o campo 'Peso da Média e Peso da Recuperação' de forma que a soma dos pesos seja igual a 100.");
				}			
			}
			else {
				erros.addErro("Dois pesos devem ser digitados no campo 'Peso da Média e Peso da Recuperação'");
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