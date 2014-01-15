/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * Define um Processo Seletivo / Vestibular
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "processo_seletivo", schema = "vestibular", uniqueConstraints = {})
public class ProcessoSeletivoVestibular implements PersistDB, Validatable {

	/** Indica se o processo seletivo est� ativo. */
	@CampoAtivo
	private boolean ativo = true;
	
	/** Data do fim do per�odo de inscri��o de candidatos. */
	@Column(name = "fim_inscricao_candidato")
	private Date fimInscricaoCandidato;
	
	/** Data do fim do per�odo de inscri��o dos fiscais. */
	@Column(name = "fim_inscricao_fiscal")
	private Date fimInscricaoFiscal;
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_processo_seletivo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Data do in�cio do per�odo de inscri��o de candidatos. */
	@Column(name = "inicio_inscricao_candidato")
	private Date inicioInscricaoCandidato;
	
	/** Data do in�cio do per�odo de inscri��o de fiscais. */
	@Column(name = "inicio_inscricao_fiscal")
	private Date inicioInscricaoFiscal;
	
	/** Nome do processo seletivo. */
	private String nome;
	
	/** Ano da aplica��o das provas. */
	private int ano;
	
	/** Per�odo da aplica��o das provas. */
	private int periodo;
	
	/** Ano de entrada dos alunos. */
	@Column(name = "ano_entrada")
	private int anoEntrada;
	
	/** Per�odo de entrada dos alunos. Caso seja zero, o processo seletivo � para entrada nos dois per�odos. */
	@Column(name = "periodo_entrada")
	private int periodoEntrada;
	
	/** Indica que o processo seletivo ter� entrada para os dois per�odos letivos do ano. */
	@Column(name = "entrada_dois_periodos")
	private boolean entradaDoisPeriodos;
	
	/** Sigla/nome abreviado do processo seletivo. */
	private String sigla;
	
	/** Forma de ingresso dos candidatos aprovados. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_forma_ingresso")
	private FormaIngresso formaIngresso;
	
	/** Indica se a sele��o de fiscais foi processada. */
	@Column(name = "selecao_fiscal_processada")
	private boolean selecaoFiscalProcessada = false;
	
	/** Indica se deve-se divulgar o resultado da sele��o de fiscais. */
	@Column(name = "informa_resultado_selecao")
	private boolean informaResultadoSelecao = false;
	
	/** Question�rio s�cio-econ�mico utilizado na inscri��o de candidatos. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_questionario")
	private Questionario questionario;
	
	/** ID do arquivo do Edital. */
	@Column(name="id_edital")
	private Integer idEdital;
	
	/** ID do arquivo do Manual do Candidato. */
	@Column(name="id_manual_candidato")
	private Integer idManualCandidato;
	
	/** Informa��es, avisos e not�cias referentes ao Processo Seletivo. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_seletivo")
	@OrderBy("inicioPublicacao")
	private Collection<AvisoProcessoSeletivoVestibular> avisos;
	
	/** Prazo m�ximo para a altera��o da foto */
	@Column(name = "inicio_data_limite_alteracao_foto")
	private Date inicioLimiteAlteracaoFotos;

	/** Prazo m�ximo para a altera��o da foto */
	@Column(name = "fim_data_limite_alteracao_foto")
	private Date fimLimiteAlteracaoFotos;

	/** In�cio do per�odo de realiza��o da prova */
	@Column(name = "inicio_realizacao_prova")
	private Date inicioRealizacaoProva;

	/** Fim do per�odo de realiza��o da prova */
	@Column(name = "fim_realizacao_prova")
	private Date fimRealizacaoProva;
	
	/** Configura��o de GRU a ser utilizada para recolhimento da taxa de inscri��o. */
	@Column(name = "id_configuracao_gru")
	private Integer idConfiguracaoGRU;
	
	/** Define se o candidato inscrito poder� optar pelo benef�cio concedido na pol�tica de inclus�o ou n�o. */
	@Column(name = "opcao_beneficio_inclusao")
	private boolean opcaoBeneficioInclusao;
	
	/** Configura��o da GRU utilizada. */
	@Transient
	private ConfiguracaoGRU configuracaoGRU;
	
	/** Data de vencimento do boleto banc�rio/GRU. */
	@Column(name = "data_vencimento_boleto")
	private Date dataVencimentoBoleto;
	
	/** Valor a ser cobrado pela GRU. */
	@Column(name = "valor_inscricao")
	private double valorInscricao;
	
	/** Indica que o processo seletivo � externo � instituicao. Por Exemplo: SiSU */
	@Column(name = "processo_externo")
	private boolean processoExterno;
	
	/** Item que referencia o argumento de inclus�o no Edital. */
	@Column(name="item_edital_argumento_inclusao")
	private String itemEditalArgumentoInclusao;
	
	/** Classe que implementa a interface {@link EstrategiaConvocacaoCandidatosVestibular} e ser� utilizada para a convoca��o de candidatos aprovados. */ 
	@Column(name = "classe_estrategia_convocacao")
	private String classeEstrategiaConvocacao;
	
	/** Implementa��o a interface {@link EstrategiaConvocacaoCandidatosVestibular} definido em {@link #classeEstrategiaConvocacao}. */
	@Transient
	private EstrategiaConvocacaoCandidatosVestibular estrategiaConvocacao;
		
	
	/** Construtor padr�o. */
	public ProcessoSeletivoVestibular() {
		formaIngresso = new FormaIngresso();
		questionario = new Questionario();
		opcaoBeneficioInclusao = false;
	}
	
	/** Construtor com ID. 
	 *  @param id
	 */
	public ProcessoSeletivoVestibular(int id) {
		this();
		this.id = id;
	}

	/** Retorna o ano da aplica��o das provas. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Indica se o processo seletivo est� ativo. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Retorna a data de vencimento do boleto banc�rio/GRU. 
	 * @return
	 */
	public Date getDataVencimentoBoleto() {
		return dataVencimentoBoleto;
	}

	/** Retorna a data do fim do per�odo de inscri��o de candidatos. 
	 * @return
	 */
	public Date getFimInscricaoCandidato() {
		return fimInscricaoCandidato;
	}

	/** Retorna a data do fim do per�odo de inscri��o dos fiscais.  
	 * @return
	 */
	public Date getFimInscricaoFiscal() {
		return fimInscricaoFiscal;
	}

	/** Retorna a forma de ingresso dos candidatos aprovados. 
	 * @return
	 */
	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	/**
	 * Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna a data do in�cio do per�odo de inscri��o de candidatos. 
	 * @return
	 */
	public Date getInicioInscricaoCandidato() {
		return inicioInscricaoCandidato;
	}

	/** Retorna a data do in�cio do per�odo de inscri��o de fiscais. 
	 * @return
	 */
	public Date getInicioInscricaoFiscal() {
		return inicioInscricaoFiscal;
	}

	/** Retorna o nome do processo seletivo. 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/** Retorna o per�odo da aplica��o das provas. 
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Retorna a sigla/nome abreviado do processo seletivo. 
	 * @return
	 */
	public String getSigla() {
		return sigla;
	}

	/** Retorna o valor padr�o da taxa de inscri��o. 
	 * @return
	 */
	public double getValorInscricao() {
		return valorInscricao;
	}

	/** Indica se a sele��o de fiscais foi processada. 
	 * @return
	 */
	public boolean isSelecaoFiscalProcessada() {
		return selecaoFiscalProcessada;
	}

	/** Seta o ano da aplica��o das provas. 
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Seta se o processo seletivo est� ativo.
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Seta a data do fim do per�odo de inscri��o de candidatos.
	 * @param fimInscricaoCandidato
	 */
	public void setFimInscricaoCandidato(Date fimInscricaoCandidato) {
		this.fimInscricaoCandidato = fimInscricaoCandidato;
	}

	/** Seta a data do fim do per�odo de inscri��o dos fiscais.
	 * @param fimInscricaoFiscal
	 */
	public void setFimInscricaoFiscal(Date fimInscricaoFiscal) {
		this.fimInscricaoFiscal = fimInscricaoFiscal;
	}

	/** Seta a forma de ingresso dos candidatos aprovados.
	 * @param formaIngresso
	 */
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	/**
	 * Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta a data do in�cio do per�odo de inscri��o de candidatos.
	 * @param inicioInscricaoCandidato
	 */
	public void setInicioInscricaoCandidato(Date inicioInscricaoCandidato) {
		this.inicioInscricaoCandidato = inicioInscricaoCandidato;
	}

	/** Seta a data do in�cio do per�odo de inscri��o de fiscais.
	 * @param inicioInscricaoFiscal
	 */
	public void setInicioInscricaoFiscal(Date inicioInscricaoFiscal) {
		this.inicioInscricaoFiscal = inicioInscricaoFiscal;
	}

	/** Seta o nome do processo seletivo.
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Seta o per�odo da aplica��o das provas.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Seta se a sele��o de fiscais foi processada. 
	 * @param selecaoFiscalProcessada
	 */
	public void setSelecaoFiscalProcessada(boolean selecaoFiscalProcessada) {
		this.selecaoFiscalProcessada = selecaoFiscalProcessada;
	}

	/** Seta a sigla/nome abreviado do processo seletivo. 
	 * @param sigla
	 */
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	/** Retorna o question�rio s�cio-econ�mico utilizado na inscri��o de candidatos. 
	 * @return
	 */
	public Questionario getQuestionario() {
		return questionario;
	}

	/** Seta  o question�rio s�cio-econ�mico utilizado na inscri��o de candidatos.
	 * @param questionario
	 */
	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	/** Valida os dados do processo seletivo (nome, sigla, forma de ingresso, ano/per�odo, etc.)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(getNome(), "Nome do Processo Seletivo", lista);
		validateRequired(getSigla(), "Sigla/Nome Abreviado", lista);
		validateRequired(formaIngresso, "Forma de Ingresso", lista);
		if (ano < 1900 || !(periodo == 1 || periodo == 2)) {
			lista.addErro("Ano/per�odo de aplica��o inv�lido");
		}
		validateMinValue(anoEntrada, 1900, "Ano de entrada inv�lido", lista);
		if (entradaDoisPeriodos) {
			periodoEntrada = 0;
		} else { 
			validateRange(periodoEntrada, 1, 2, "Per�odo de Entrada", lista);
		}
		if (ano * 10 + periodo > anoEntrada * 10 + periodoEntrada)
			lista.addErro("O Ano-Per�odo de entrada � anterior ao Ano-Per�odo de aplica��o");
		if (!processoExterno) {
			validateRequiredId(getTipoArrecadacao(), "Tipo de Arrecada��o", lista);
			
			// datas 
			if (inicioInscricaoCandidato == null && fimInscricaoCandidato != null
					|| inicioInscricaoCandidato != null && fimInscricaoCandidato == null) {
				lista.addErro("O per�odo de inscri��o de candidatos deve ter in�cio e fim.");
			} else if (inicioInscricaoCandidato == null || fimInscricaoCandidato == null) {
				lista.addErro("Informe as datas de in�cio e fim da inscri��o de candidatos.");
			} else if (fimInscricaoCandidato.before(inicioInscricaoCandidato)) {
				lista.addErro("O fim do per�odo de inscri��o de candidatos � anterior ao in�cio.");
			} else if (fimInscricaoCandidato != null && getDataVencimentoBoleto() != null
					&& getDataVencimentoBoleto().before(fimInscricaoCandidato)) {
				lista.addErro("O vencimento do boleto deve ser posterior ao fim do per�odo de inscri��o de candidato.");
			}
			if (inicioInscricaoFiscal == null && fimInscricaoFiscal != null
					|| inicioInscricaoFiscal != null && fimInscricaoFiscal == null) {
				lista
						.addErro("O per�odo de inscri��o de fiscais deve ter in�cio e fim.");
			}
			if (inicioInscricaoFiscal != null && fimInscricaoFiscal != null
					&& fimInscricaoFiscal.before(inicioInscricaoFiscal)) {
				lista
						.addErro("O fim do per�odo de inscri��o de fiscais � anterior ao in�cio.");
			}
			validateMinValue(getValorInscricao(), 1.0, "Valor da inscri��o", lista);
			validateRequired(getDataVencimentoBoleto(), "Data de vencimento da GRU", lista);
			validateRequired(anoEntrada,"Ano de Entrada", lista);
			if (idConfiguracaoGRU != null)
				validateRequiredId(idConfiguracaoGRU, "Configura��o da GRU", lista);
			validateRequired(inicioLimiteAlteracaoFotos, "Data Limite do Altera��o da Foto", lista);
			validateRequired(fimLimiteAlteracaoFotos, "Data Limite do Altera��o da Foto", lista);
			if (inicioLimiteAlteracaoFotos != null && fimLimiteAlteracaoFotos != null) {
				if (!(inicioLimiteAlteracaoFotos.after(inicioInscricaoCandidato) && inicioLimiteAlteracaoFotos.after(fimInscricaoCandidato))) 
					lista.addErro("Inicio da Data Limite do Altera��o da Foto: Deve ser posterior ao termino do per�odo de inscri��o de candidatos.");	
				
				if (!(inicioLimiteAlteracaoFotos.before(fimLimiteAlteracaoFotos))) 
					lista.addErro("Inicio da Data Limite do Altera��o da Foto: Deve ser posterior ao fim da Data Limite do Altera��o da Foto.");
				
				if (!(fimLimiteAlteracaoFotos.after(inicioInscricaoCandidato) && fimLimiteAlteracaoFotos.after(fimInscricaoCandidato))) 
					lista.addErro("Fim da Data Limite do Altera��o da Foto: Deve ser posterior ao termino do per�odo de inscri��o de candidatos.");	
				
				if (!(fimLimiteAlteracaoFotos.after(inicioLimiteAlteracaoFotos))) 
					lista.addErro("Fim da Data Limite do Altera��o da Foto: Deve ser posterior a Data Inicial Limite do Altera��o da Foto.");
			}
		}
		
		return lista;
	}

	/** Retorna uma representa��o textual do processo seletivo, no formato:
	 * ID, seguido de v�rgula, seguido do nome.
	 */
	@Override
	public String toString() {
		return getId() + ", " + getNome();
	}
	
	/** Indica se o per�odo de inscri��es para candidatos ao vestibular est� aberto. 
	 * @return
	 */
	@Transient
	public boolean isInscricoesCandidatoAbertas() {
		return CalendarUtils.isDentroPeriodo(getInicioInscricaoCandidato(), getFimInscricaoCandidato());
	}

	/** Retorna o ID do arquivo do Edital.
	 * @return 
	 */
	public Integer getIdEdital() {
		return idEdital;
	}

	/** Seta o ID do arquivo do Edital.
	 * @param idEdital 
	 */
	public void setIdEdital(Integer idEdital) {
		this.idEdital = idEdital;
	}

	/** Retorna o ID do arquivo do Manual do Candidato. 
	 * @return 
	 */
	public Integer getIdManualCandidato() {
		return idManualCandidato;
	}

	/** Seta o ID do arquivo do Manual do Candidato.
	 * @param idManualCandidato
	 */
	public void setIdManualCandidato(Integer idManualCandidato) {
		this.idManualCandidato = idManualCandidato;
	}

	/** Retorna as informa��es, avisos e not�cias referentes ao Processo Seletivo.
	 * @return
	 */
	public Collection<AvisoProcessoSeletivoVestibular> getAvisos() {
		return avisos;
	}
	
	/** Retorna as informa��es, avisos e not�cias ativos.
	 * @return
	 */
	public Collection<AvisoProcessoSeletivoVestibular> getAvisosAtivos() {
		Collection<AvisoProcessoSeletivoVestibular> avisosAtivos = new ArrayList<AvisoProcessoSeletivoVestibular>();
		if (avisos != null){
			for (AvisoProcessoSeletivoVestibular aviso : avisos){
				if (aviso.isAtivo())
					avisosAtivos.add(aviso);
			}
		}
		return avisosAtivos;
	}

	/** Retorna as informa��es, avisos e not�cias com data/hora igual ou anterior a data/hora atual.
	 * @return
	 */
	public Collection<AvisoProcessoSeletivoVestibular> getAvisosPublicaveis() {
		Collection<AvisoProcessoSeletivoVestibular> avisosPublicaveis = new ArrayList<AvisoProcessoSeletivoVestibular>();
		Date hoje = new Date();
		if (avisos != null) {
			for (AvisoProcessoSeletivoVestibular aviso : avisos){
				if (hoje.compareTo(aviso.getInicioPublicacao()) >= 0 && aviso.isAtivo())
					avisosPublicaveis.add(aviso);
			}
		}
		return avisosPublicaveis;
	}

	/** Seta as informa��es, avisos e not�cias referentes ao Processo Seletivo.
	 * @param avisos
	 */
	public void setAvisos(Collection<AvisoProcessoSeletivoVestibular> avisos) {
		this.avisos = avisos;
	}

	/** Indica se deve-se divulgar o resultado da sele��o de fiscais.
	 * @return
	 */
	public boolean isInformaResultadoSelecao() {
		return informaResultadoSelecao;
	}

	/** Seta se deve-se divulgar o resultado da sele��o de fiscais.
	 * @param informaResultadoSelecao
	 */
	public void setInformaResultadoSelecao(boolean informaResultadoSelecao) {
		this.informaResultadoSelecao = informaResultadoSelecao;
	}

	/** Retorna o ano de entrada dos alunos. 
	 * @return
	 */
	public int getAnoEntrada() {
		return anoEntrada;
	}

	/** Seta o ano de entrada dos alunos.
	 * @param anoEntrada
	 */
	public void setAnoEntrada(int anoEntrada) {
		this.anoEntrada = anoEntrada;
	}

	/** Retorna o per�odo de entrada dos alunos. Caso seja zero, o processo seletivo � para entrada nos dois per�odos. 
	 * @return
	 */
	public int getPeriodoEntrada() {
		return periodoEntrada;
	}

	/** Seta o per�odo de entrada dos alunos. Caso seja zero, o processo seletivo � para entrada nos dois per�odos.
	 * @param periodoEntrada
	 */
	public void setPeriodoEntrada(int periodoEntrada) {
		this.periodoEntrada = periodoEntrada;
	}

	/** Indica que o processo seletivo ter� entrada para os dois per�odos letivos do ano. 
	 * @return
	 */
	public boolean isEntradaDoisPeriodos() {
		return entradaDoisPeriodos;
	}

	/** Seta que o processo seletivo ter� entrada para os dois per�odos letivos do ano. 
	 * @param entradaDoisPeriodos
	 */
	public void setEntradaDoisPeriodos(boolean entradaDoisPeriodos) {
		this.entradaDoisPeriodos = entradaDoisPeriodos;
	}
	
	/** Retorna o in�cio da Data limite de altera��o das fotos */
	public Date getInicioLimiteAlteracaoFotos() {
		return inicioLimiteAlteracaoFotos;
	}

	/** Seta o Inicio da Data limite de altera��o das fotos */
	public void setInicioLimiteAlteracaoFotos(Date inicioLimiteAlteracaoFotos) {
		this.inicioLimiteAlteracaoFotos = inicioLimiteAlteracaoFotos;
	}

	/** Retorna o fim da Data limite de altera��o das fotos */
	public Date getFimLimiteAlteracaoFotos() {
		return fimLimiteAlteracaoFotos;
	}

	/** Seta o fim da Data limite de altera��o das fotos */
	public void setFimLimiteAlteracaoFotos(Date fimLimiteAlteracaoFotos) {
		this.fimLimiteAlteracaoFotos = fimLimiteAlteracaoFotos;
	}

	/** Retorna o in�cio da realiza��o da prova */
	public Date getInicioRealizacaoProva() {
		return inicioRealizacaoProva;
	}

	/** seta o in�cio da realiza��o da prova */
	public void setInicioRealizacaoProva(Date inicioRealizacaoProva) {
		this.inicioRealizacaoProva = inicioRealizacaoProva;
	}

	/** Retorna o fim da realiza��o da prova */
	public Date getFimRealizacaoProva() {
		return fimRealizacaoProva;
	}

	/** seta o fim da realiza��o da prova */
	public void setFimRealizacaoProva(Date fimRealizacaoProva) {
		this.fimRealizacaoProva = fimRealizacaoProva;
	}

	public int getTipoArrecadacao() {
		return configuracaoGRU != null && configuracaoGRU.getTipoArrecadacao() != null ?
				configuracaoGRU.getTipoArrecadacao().getId() : 0;
	}

	public String getCodigoRecolhimento() {
		return configuracaoGRU != null
				&& configuracaoGRU.getTipoArrecadacao().getCodigoRecolhimento() != null ? 
				configuracaoGRU.getTipoArrecadacao().getCodigoRecolhimento().getCodigo() : null;
	}

	public Integer getIdConfiguracaoGRU() {
		return idConfiguracaoGRU;
	}


	public void setIdConfiguracaoGRU(Integer idConfiguracaoGRU) {
		this.idConfiguracaoGRU = idConfiguracaoGRU;
	}


	public boolean isOpcaoBeneficioInclusao() {
		return opcaoBeneficioInclusao;
	}


	public void setOpcaoBeneficioInclusao(boolean opcaoBeneficioInclusao) {
		this.opcaoBeneficioInclusao = opcaoBeneficioInclusao;
	}

	public ConfiguracaoGRU getConfiguracaoGRU() {
		return configuracaoGRU;
	}

	public void setConfiguracaoGRU(ConfiguracaoGRU configuracaoGRU) {
		if (configuracaoGRU != null)
			this.idConfiguracaoGRU = configuracaoGRU.getId();
		this.configuracaoGRU = configuracaoGRU;
	}

	public void setDataVencimentoBoleto(Date dataVencimentoBoleto) {
		this.dataVencimentoBoleto = dataVencimentoBoleto;
	}

	public void setValorInscricao(double valorInscricao) {
		this.valorInscricao = valorInscricao;
	}

	public boolean isProcessoExterno() {
		return processoExterno;
	}

	public void setProcessoExterno(boolean processoExterno) {
		this.processoExterno = processoExterno;
	}

	public String getItemEditalArgumentoInclusao() {
		return itemEditalArgumentoInclusao;
	}

	public void setItemEditalArgumentoInclusao(String itemEditalArgumentoInclusao) {
		this.itemEditalArgumentoInclusao = itemEditalArgumentoInclusao;
	}

	public String getClasseEstrategiaConvocacao() {
		return classeEstrategiaConvocacao;
	}

	/** Seta a classe que implementa a interface {@link EstrategiaConvocacaoCandidatosVestibular} e ser� utilizada para a convoca��o de candidatos aprovados.
	 * @param classeEstrategiaConvocacao
	 */
	public void setClasseEstrategiaConvocacao(String classeEstrategiaConvocacao) {
		this.classeEstrategiaConvocacao = classeEstrategiaConvocacao;
		this.estrategiaConvocacao = null;
	}

	/** Retorna uma implementa��o da classe que implementa a interface {@link EstrategiaConvocacaoCandidatosVestibular} e ser� utilizada para a convoca��o de candidatos aprovados.
	 * @return
	 */
	public EstrategiaConvocacaoCandidatosVestibular getEstrategiaConvocacao() {
		if (estrategiaConvocacao == null && !isEmpty(classeEstrategiaConvocacao)) {
			estrategiaConvocacao = ReflectionUtils.newInstance(classeEstrategiaConvocacao);
		}
		return estrategiaConvocacao;
	}

}