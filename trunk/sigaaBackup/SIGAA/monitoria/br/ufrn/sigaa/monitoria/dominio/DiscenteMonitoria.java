/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 08/06/2009
 * 
 */
package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Banco;

/*******************************************************************************
 * <p>
 * Representa um discente no projeto de monitoria. O discente ingressa no projeto
 * atrav�s de um processo seletivo. Um discente de monitoria pode ser bolsista
 * (remunerado) ou volunt�rio (n�o remunerado)
 * </p>
 * 
 * @author Ilueny Santos
 * @author Victor Hugo
 * 
 ******************************************************************************/
@Entity
@Table(name = "discente_monitoria", schema = "monitoria")
public class DiscenteMonitoria implements Validatable, Comparable<DiscenteMonitoria> {


	/** Identificador do discente monitoria  */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_discente_monitoria", unique = true, nullable = false)
	private int id;

	/** Discente do n�vel de gradua��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente = new DiscenteGraduacao();

	/** Representar a classifica��o do discente monitoria dentro de um processo seletivo */
	@Column(name = "classificacao")
	private int classificacao;

	/** Representa nota do discente monitoria */
	@Column(name = "nota")
	private Double nota = 0.0;

	/** Representa nota da prova do discente monitoria */
	@Column(name = "nota_prova")
	private Double notaProva = 0.0;

	/** Indica se discente monitoria � ativo */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo;

	/**
	 * Representa o tipo de v�nculo do aluno com o projeto de monitoria. valores
	 * poss�veis: 1 - NAO_REMUNERADO, 2 - BOLSISTA, 3 - NAO_CLASSIFICADO
	 
	@Column(name = "tipo_monitoria")
	private Integer tipoMonitoria;
	*/	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscenteMonitoria tipoVinculo = new TipoVinculoDiscenteMonitoria();
	
    /** Observa��o do discente monitoria */
	@Column(name = "observacao")
	private String observacao;

	/** Representa a situa��o do discente monitoria 
	 *  Exemplos: AGUARDANDO_CONVOCACAO = 2, ASSUMIU_MONITORIA = 5
	 * */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_monitoria")
	private SituacaoDiscenteMonitoria situacaoDiscenteMonitoria = new SituacaoDiscenteMonitoria();

	/** Projeto de ensino ao qual o discente monitoria fica vinculado  */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto_monitoria")
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	/** Representa a data de cadastro do discente monitoria */
	@CriadoEm
	@Temporal(TemporalType.DATE)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/** Representa data de in�cio do discente monitoria  */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Representa data final do discente monitoria  */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Representa a data de valida��o da PROGRAD */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao_prograd")
	private Date dataValidacaoPrograd;

	/** Cole��o que representa as orienta��es que o discente possui */
	@OneToMany(mappedBy = "discenteMonitoria", cascade = { CascadeType.ALL })
	private Collection<Orientacao> orientacoes = new HashSet<Orientacao>();

	/** Conjunto de relat�rios que discente monitoria possui */
	@OneToMany(mappedBy = "discenteMonitoria")
	@OrderBy(value = "dataCadastro")
	private Set<RelatorioMonitor> relatoriosMonitor = new HashSet<RelatorioMonitor>();

	/** Conjunto de atividades que discente de monitoria poossui */
	@OneToMany(mappedBy = "discenteMonitoria")
	@OrderBy(value = "ano, mes")
	private Set<AtividadeMonitor> atividadesMonitor = new HashSet<AtividadeMonitor>();

	/** Representa a prova de sele��o ao qual o discente faz parte */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prova_selecao")
	private ProvaSelecao provaSelecao = new ProvaSelecao();

	/** Dados banc�rios dos alunos. S�o informados depois da valida��o da
	 * 	sele��o pela prograd; Banco */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_banco")
	private Banco banco = null;

	/** Representa n�mero da ag�ncia */
	@Column(name = "num_agencia")
	private String agencia;

	/** Representa n�mero da conta */
	@Column(name = "num_conta")
	private String conta;
	
	/** N�mero da opera��o da conta banc�ria do discente de monitoria. */
	@Column(name = "num_operacao")
	private String operacao;

	/** Representa observa��o da PROGRAD */
	@Column(name = "observacao_prograd")
	private String observacaoPrograd;
	
	/** Indica convoca��o do discente monitoria */
	@Column(name = "convocado")
	private boolean discenteConvocado;

	/** Representa hist�rico de situa��es */
	@OneToMany(mappedBy = "discenteMonitoria")
	private Collection<HistoricoSituacaoDiscenteMonitoria> historicoSituacaoDiscenteMonitoria = new HashSet<HistoricoSituacaoDiscenteMonitoria>();

	/**
	 * Campo utilizado no form para valida��o.
	 */
	@Transient
	private boolean classificado = false;

	/**
	 * Utilizado apenas na vis�o para o usu�rio definir se o discente �
	 * bolsista ou n�o
	 */
	@Transient
	private boolean bolsista = false;

	/**
	 * Utilizado apenas na vis�o para o usu�rio definir se o discente poder�
	 * assumir a monitoria ou n�o
	 */
	@Transient
	private boolean inativo = false;

	/**
	 * M�dia ponderada das m�dias finais das disciplinas cursadas pelo aluno que
	 * fazem parte de um determinado projeto utilizado para desempate no c�lculo
	 * da classifica��o do discente no projeto de monitoria em quest�o.
	 */
	@Transient
	private float mediaComponentesProjeto;

	/** cole��o de docentes */
	@Transient
	private Collection<EquipeDocente> docentes = new ArrayList<EquipeDocente>();

	/** indica se discente foi selecionado no processo seletivo. */
	@Transient
	private boolean selecionado = false;

	/** justificar reprova��o na sele��o.... */
	@Transient
	private String justificativa;

	/** Utilizado na view no cadastro do resultado da sele��o*/
	@Transient
	private Boolean prioritario = false;
	
	/** Utilizado na view ao adicionar novas orienta��es para o discente*/
	@Transient
	private Date dataInicioOrientacao;
	
	/** Utilizado na view ao adicionar novas orienta��es para o discente*/
	@Transient
	private Date dataFimOrientacao;

	
	// Constructors

	/** default constructor */
	public DiscenteMonitoria() {
	}

	public DiscenteMonitoria(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isInativo() {
		return inativo;
	}

	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}

	public boolean isBolsista() {
		return bolsista;	    
	}

	public void setBolsista(boolean bolsista) {
		this.bolsista = bolsista;
	}
	
	/** Formata a classifica��o do discente para exibi��o.*/
	public String getClassificacaoView() {
		return classificacao == 0 ? "-" : String.valueOf(classificacao) + "�"  ;
	}

	public int getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(int classificacao) {
		this.classificacao = classificacao;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public Double getNota() {
		return nota;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}

	public Double getNotaProva() {
		return notaProva;
	}

	public void setNotaProva(Double notaProva) {
		this.notaProva = notaProva;
	}

	/**
	 * Referente a altera��o da ordem na classifica��o ou descreve o motivo da
	 * n�o aceita��o da monitoria pelo discente
	 * 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	@Override
	public boolean equals(Object obj) {

		boolean result = false;

		if (obj == null)
			return false;

		if (obj instanceof DiscenteMonitoria) {
			DiscenteMonitoria o = (DiscenteMonitoria) obj;
			if (o.getId() > 0 && this.getId() > 0) {
			    result = this.getId() == o.getId();
			} else {
			    result = this.getDiscente().getId() == o.getDiscente().getId();
			}
		}

		return result;
	}

	@Override
	public int hashCode() {

		int result = 17;
		if (this.getId() > 0) {
		    result = 37 * result + new Integer(this.getId()).hashCode();
		} else {
		    result = 37 * result + new Integer(this.discente.getId()).hashCode();
		}
		return result;
	}

	
	/**
	 * Valida os campos obrigat�rios para um discente de monitoria existir.
	 * 
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(discente, "Discente", lista);
		ValidatorUtil.validateRequired(nota, "Nota do discente", lista);
		ValidatorUtil.validateRequired(notaProva, "Nota do discente", lista);
		ValidatorUtil.validateRequired(classificacao, "Classifica��o do discente", lista);
		ValidatorUtil.validateRequired(situacaoDiscenteMonitoria, "Situacao", lista);
		ValidatorUtil.validateRequired(tipoVinculo, "Tipo de V�nculo", lista);
		if ((nota != null) && ((nota > 10) || (nota < 0))) {
		    lista.addErro("Nota: O valor deve ser maior ou igual a 0 (zero) e menor ou igual a 10 (dez).");
		}
		if ((notaProva != null) && ((notaProva > 10) || (notaProva < 0))) {
			lista.addErro("Nota: O valor deve ser maior ou igual a 0 (zero) e menor ou igual a 10 (dez).");
		}
		if ((dataFim != null) && (dataInicio != null)) {
		    
			//In�cio da monitoria menor que o fim.
			ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Per�odo da monitoria",lista);
		    
			Date inicioProjeto = getProjetoEnsino().getProjeto().getDataInicio();
			Date fimProjeto = getProjetoEnsino().getProjeto().getDataFim();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (!CalendarUtils.isDentroPeriodo(inicioProjeto, fimProjeto, dataInicio)) {
			    lista.addErro("Data de in�cio da monitoria est� fora do per�odo do projeto (" + sdf.format(inicioProjeto) + " at� " + sdf.format(fimProjeto) + ").");
			}
			
			if (!CalendarUtils.isDentroPeriodo(inicioProjeto, fimProjeto, dataFim)) {
			    lista.addErro("Data de fim da monitoria est� fora do per�odo do projeto (" + sdf.format(inicioProjeto) + " at� " + sdf.format(fimProjeto) + ").");
			}
		}
		
		//Selecionou assumiu monitoria e um tipo de v�nculo incompat�vel
		if ( !ValidatorUtil.isEmpty(discente) && isAssumiuMonitoria() && !isVinculoBolsista() && !isVinculoNaoRemunerado() ){			
			lista.addErro("Para que " + discente.getNome() + " possa ASSUMIR MONITORIA selecione o v�nculos BOLSISTA ou N�O REMUNERADO.");
		}
		
		return lista;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Collection<EquipeDocente> getDocentes() {
		return docentes;
	}

	public void setDocentes(Collection<EquipeDocente> docentes) {
		this.docentes = docentes;
	}

	public Collection<Orientacao> getOrientacoes() {
		return orientacoes;
	}

	/**
	 * N�o exibe orienta��es marcadas como exclu�das no banco
	 * 
	 * @return
	 */
	@Transient
	public Collection<Orientacao> getOrientacoesValidas() {

		// removendo os exclu�dos da lista..
		if (orientacoes != null) {
			for (Iterator<Orientacao> it = orientacoes.iterator(); it.hasNext();) {
				if (!it.next().isAtivo()) {
					it.remove();
				}
			}
		}
		return orientacoes;
	}

	public void setOrientacoes(Collection<Orientacao> orientacoes) {
		this.orientacoes = orientacoes;
	}

	public SituacaoDiscenteMonitoria getSituacaoDiscenteMonitoria() {
		return situacaoDiscenteMonitoria;
	}

	public void setSituacaoDiscenteMonitoria(
			SituacaoDiscenteMonitoria situacaoDiscenteMonitoria) {
		this.situacaoDiscenteMonitoria = situacaoDiscenteMonitoria;
	}
	

	public TipoVinculoDiscenteMonitoria getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscenteMonitoria tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Na view de cadastro de resultado da sele��o este campo �
	 * exibido com 'situa��o'.
	 * 
	 * @return
	 */
	public boolean isClassificado() {
		return classificado;
	}

	public void setClassificado(boolean clas) {
		this.classificado = clas;
	}

	/**
	 * Retorna true caso o tipoVinculo do DiscenteMonitoria esteja definido
	 * entre os v�nculos que permitem ao discente assumir a 
	 * monitoria como volunt�rio ou bolsista.
	 * 
	 * @return
	 */
	public boolean isVinculoValido() {
		return (tipoVinculo.getId() == TipoVinculoDiscenteMonitoria.BOLSISTA) 
			|| (tipoVinculo.getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO)
			|| (tipoVinculo.getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA);
	}

	/**
	 * M�todo para clonar a cole��o de docentes
	 * 
	 * @param colEquipe
	 */
	public void clonaDocentes(Collection<EquipeDocente> colEquipe) {
		docentes = new ArrayList<EquipeDocente>();
		for (EquipeDocente equipe : colEquipe) {

			EquipeDocente novaEquipe = new EquipeDocente(equipe);
			docentes.add(novaEquipe);

		}
	}

	/**
	 * Retorna a lista de Atividades do monitor.
	 * 
	 * @return
	 */
	public Set<AtividadeMonitor> getAtividadesMonitor() {		
		// removendo os exclu�dos da lista..
		if ((atividadesMonitor != null) && (!atividadesMonitor.isEmpty())) {
			for (Iterator<AtividadeMonitor> it = atividadesMonitor.iterator(); it.hasNext();) {
			    	AtividadeMonitor r = it.next();
				if (!r.isAtivo())
					it.remove();
			}
		}

		return atividadesMonitor;
	}

	public void setAtividadesMonitor(Set<AtividadeMonitor> atividadesMonitor) {
		this.atividadesMonitor = atividadesMonitor;
	}

	/** Recupera relat�rios do discente monitoria */
	public Set<RelatorioMonitor> getRelatoriosMonitor() {
		// removendo os exclu�dos da lista..
		if ((relatoriosMonitor != null) && (!relatoriosMonitor.isEmpty())) {
			for (Iterator<RelatorioMonitor> it = relatoriosMonitor.iterator(); it.hasNext();) {
			    	RelatorioMonitor r = it.next();
				if (!r.isAtivo())
					it.remove();
			}
		}

		return relatoriosMonitor;
	}

	public void setRelatoriosMonitor(Set<RelatorioMonitor> relatoriosMonitor) {
		this.relatoriosMonitor = relatoriosMonitor;
	}

	public float getMediaComponentesProjeto() {
		return mediaComponentesProjeto;
	}

	public void setMediaComponentesProjeto(float mediaComponentesProjeto) {
		this.mediaComponentesProjeto = mediaComponentesProjeto;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/**
	 * Este m�todo ordena os discentes participantes do projeto de monitoria de
	 * acordo com os crit�rios estabelecidos sendo o primeiro crit�rio a nota
	 * final do processo de sele��o. Em caso de empate o desempate obedecer� os
	 * seguintes crit�rios na seguinte ordem: 1 - Maior nota na prova escrita; 2
	 * - Maior nota no(s) componente(s) curricular(es) de forma��o objeto da
	 * sele��o; (media ponderada dos componentes curriculares do projeto
	 * cursadas pelo aluno) 3 - Maior �ndice de rendimento acad�mico (IRA).
	 * 
	 * @return 1, 0 ou -1 dependendo do resultado da compara��o.
	 * @param other discente que ser� comparado com o discente atual (this).
	 * 
	 * 
	 */
	public int compareTo(DiscenteMonitoria other) {
		// Primeiro crit�rio: Nota final da avalia��o
		int result = other.getNota().compareTo(this.getNota());

		if (result == 0) {
			// Segundo crit�rio: Nota da prova escrita
			result = other.getNotaProva().compareTo(this.getNotaProva());
			if (result == 0) {
				// Terceiro crit�rio: M�dia ponderada dos componentes do
				// projetos cursadas pelo discente
				result = Float.valueOf(other.getMediaComponentesProjeto()).compareTo(this.getMediaComponentesProjeto());
				if (result == 0) {
					// Quarto crit�rio: IRA(�ndice 2) do discente
					if (other.getDiscente() != null && other.getDiscente().getDiscente() != null && other.getIndiceAcademicoSelecao() != null
							&& this.getDiscente() != null && this.getDiscente().getDiscente() != null && this.getIndiceAcademicoSelecao() != null) {
						
						Double v1 = other.getIndiceAcademicoSelecao().getValor();
						Double v2 = this.getIndiceAcademicoSelecao().getValor();
						result = v1.compareTo(v2);
					}
				}
			}
		}

		return result;
	}
	
	/** 
	 * Informa o �ndice acad�mico considerado na sele��o dos monitores
	 */
	public IndiceAcademicoDiscente getIndiceAcademicoSelecao() {
		if (this.getDiscente().getDiscente().getIndices() == null) {
			return null;
		}
		//TODO: Remover n�mero m�gico 2  = IRA(�ndice 2) do discente.
		return this.getDiscente().getDiscente().getIndice(2);
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getObservacaoPrograd() {
		return observacaoPrograd;
	}

	public void setObservacaoPrograd(String observacaoPrograd) {
		this.observacaoPrograd = observacaoPrograd;
	}

	public ProvaSelecao getProvaSelecao() {
		return provaSelecao;
	}

	public void setProvaSelecao(ProvaSelecao provaSelecao) {
		this.provaSelecao = provaSelecao;
	}

	public Collection<HistoricoSituacaoDiscenteMonitoria> getHistoricoSituacaoDiscenteMonitoria() {
		return historicoSituacaoDiscenteMonitoria;
	}

	public void setHistoricoSituacaoDiscenteMonitoria(
			Collection<HistoricoSituacaoDiscenteMonitoria> historicoSituacaoDiscenteMonitoria) {
		this.historicoSituacaoDiscenteMonitoria = historicoSituacaoDiscenteMonitoria;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	/**
	 * Utilizado em certificados (Certificado do SID, e participa��o em
	 * projetos)...
	 * 
	 * @return String de orientadores do discente separadas por v�rgula
	 */
	public String getListaOrientadores() {

		StringBuffer orientadores = new StringBuffer();
		orientadores.append("");

		for (Orientacao orientacao : getOrientacoes()) {
			EquipeDocente orientador = orientacao.getEquipeDocente();
			if ((!orientador.isExcluido())) {
				orientadores.append(", " + orientador.getServidor().getNome());
			}
		}

		if (orientadores.toString().trim().equals(""))
			return "";

		String result = orientadores.toString().replaceFirst("[, ]", "");
		int pos = result.lastIndexOf(',');

		if (pos > 0){
			return result.substring(0, pos) + " e" + result.substring(pos + 1);
		}else {
			return result == null ? "" : result;
		}
	}

	/**
	 * Informa se o discente j� foi validado
	 * 
	 * Utilizado para controlar exibi��o de bot�es na tela de valida��o da prova
	 * seletiva.
	 * 
	 * S� podem ser validados/n�o-validados os monitores que ainda n�o enviaram
	 * freq��ncias
	 * 
	 * @return
	 */
	public boolean isPassivelValidacao() {
		return (this.situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.AGUARDANDO_VALIDACAO_PROGRAD) 
			|| (this.situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.DESVALIDADO_PELA_PROGRAD)
			|| (this.situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA)
			|| (this.situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO);
	}

	/**
	 * Informa a data em que a prograd realizou a valida��o da prova seletiva do
	 * discente
	 * 
	 * @return
	 */
	public Date getDataValidacaoPrograd() {
		return dataValidacaoPrograd;
	}

	public void setDataValidacaoPrograd(Date dataValidacaoPrograd) {
		this.dataValidacaoPrograd = dataValidacaoPrograd;
	}

	/**
	 * Informa se a prograd j� realizou a valida��o deste discente
	 * 
	 * @return
	 */
	public boolean isValidadoPrograd() {
		return dataValidacaoPrograd == null;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/**
	 * Retorna todos os relat�rios parciais do monitor
	 * 
	 * @return
	 */
	public Set<RelatorioMonitor> getRelatoriosParciais() {
		Set<RelatorioMonitor> result = new HashSet<RelatorioMonitor>();

		for (RelatorioMonitor rm : getRelatoriosMonitor()) {
			if ((rm.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_PARCIAL) && rm.isAtivo())
				result.add(rm);
		}
		return result;
	}

	/** Verifica se foi enviado relat�rio parcial */
	public boolean isEnviouRelatoriosParciais() {
		if (getRelatoriosParciais().isEmpty())
			return false;
		else
			for (RelatorioMonitor rm : getRelatoriosParciais())
				if (rm.isEnviado())
					return true;

		return false;
	}

	/**
	 * Retorna todos os relat�rios finais do monitor
	 * 
	 * @return
	 */
	public Set<RelatorioMonitor> getRelatoriosFinais() {
		Set<RelatorioMonitor> result = new HashSet<RelatorioMonitor>();

		for (RelatorioMonitor rm : getRelatoriosMonitor()) {
			if (rm.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_FINAL)
				result.add(rm);
		}
		return result;
	}

	/** Verifica se foi enviado relat�rio final */
	public boolean isEnviouRelatoriosFinais() {
		if (getRelatoriosFinais().isEmpty())
			return false;
		else
			for (RelatorioMonitor rm : getRelatoriosFinais())
				if (rm.isEnviado())
					return true;

		return false;
	}

	/**
	 * Retorna todos os relat�rios de desligamento do monitor
	 * 
	 * @return
	 */
	public Set<RelatorioMonitor> getRelatoriosDesligamento() {
		Set<RelatorioMonitor> result = new HashSet<RelatorioMonitor>();

		for (RelatorioMonitor rm : getRelatoriosMonitor()) {
			if (rm.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR)
				result.add(rm);
		}
		return result;
	}

	/** Verifica se foi enviado relat�rio de desligamento */
	public boolean isEnviouRelatoriosDesligamento() {
		if (getRelatoriosDesligamento().isEmpty())
			return false;
		else
			for (RelatorioMonitor rm : getRelatoriosDesligamento())
				if (rm.isEnviado())
					return true;

		return false;
	}

	/**
	 * Informa se o discente est� finalizado no projeto e se enviou os
	 * relat�rios de desligamento ou final
	 * 
	 * Usado por: monitoria/ConsultarMonitor/lista.jsp
	 * Usado por: monitoria/DocumentosAutenticados/lista_discentes.jsp
	 * 
	 * @return
	 */
	public boolean isRecebeCertificado() {
		return ( isFinalizado()
				&& (situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA));
	}
	
	/**
	 * Informa se o discente est� finalizado no projeto e se enviou os
	 * relat�rios de desligamento ou final
	 * 
	 * @return
	 */
	public boolean isCadastrarRelatorios() {
		return (getDataInicio() != null)
				&& ((situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) 
						|| (situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA));
	}

	
	
	/**
	 * Verifica se o discente pode cadastrar relat�rio de atividades (frequ�ncia) segundo a regra de envio de frequ�ncia
	 * passada por par�metro.
	 * 
	 * @param atv {@link AtividadeMonitor} relat�rio de atividades do monitor.
	 * @return <code>true</code>
	 */
	public boolean isPermitidoCadastrarRelatorioAtividade(EnvioFrequencia regraEnvioFrequencia) {
		
		if(!isVigente() || !isAssumiuMonitoria()) {
			return false;
		}		
		
		return (
			(regraEnvioFrequencia.isEnvioLiberado()) 
			&& (regraEnvioFrequencia.getDataInicioEntradaMonitorPermitido().compareTo(getDataInicio()) <= 0)
			&& (regraEnvioFrequencia.getDataFimEntradaMonitorPermitido().compareTo(getDataInicio()) >= 0)
			); 
	}
	

	/**
	 * Informa se o Discente tem algum tipo de prioridade na sele��o de bolsas.
	 * Relativo ao Cadastro �nico de Bolsistas
	 * 
	 * @return
	 */
	public Boolean getPrioritario() {
		return prioritario;
	}

	public void setPrioritario(Boolean prioritario) {
		this.prioritario = prioritario;
	}

	public Date getDataInicioOrientacao() {
	    return dataInicioOrientacao;
	}

	public void setDataInicioOrientacao(Date dataInicioOrientacao) {
	    this.dataInicioOrientacao = dataInicioOrientacao;
	}

	public Date getDataFimOrientacao() {
	    return dataFimOrientacao;
	}

	public void setDataFimOrientacao(Date dataFimOrientacao) {
	    this.dataFimOrientacao = dataFimOrientacao;
	}
	
	public boolean isVinculoBolsista() {
	    return getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA;
	}

	public boolean isVinculoNaoRemunerado() {
	    return getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO;
	}
	
	public boolean isVinculoEmEspera() {
	    return getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA;
	}
	
	public boolean isVinculoNaoClassificado() {
	    return getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_CLASSIFICADO;
	}
	
	/**
	 * Verifica, com base na data in�cio e data fim, se a monitoria est� ocorrendo.
	 * @return
	 */
	public boolean isVigente() {
	    return isAtivo() && isValido() && CalendarUtils.isDentroPeriodo(getDataInicio(), getDataFim());
	}

	/**
	 * Verifica, com base na data fim, se a orienta��o j� foi finalizada.
	 * @return
	 */
	public boolean isFinalizado() {
	    return isAtivo() && isValido() && getDataFim().before(CalendarUtils.descartarHoras(new Date()));
	}
	
	/**
	 * Indica, com base na data in�cio e data fim, se o discente j� iniciou a monitoria.
	 * @return
	 */
	public boolean isValido() {
	    return (getDataInicio() != null) && (getDataFim() != null);
	}

	/**
	 * Retorna o status do discente com base nas datas de in�cio e fim.
	 * 
	 * @return
	 */
	public String getStatus() {
	    return isFinalizado() ? "Finalizado" : isVigente() ? "Ativo" : "Inativo" ;
	}

	
	public boolean isAssumiuMonitoria() {
	    return (situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);
	}
	
	public boolean isValidacaoCancelada() {
	    return situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.DESVALIDADO_PELA_PROGRAD;
	}
	
	public boolean isConvocado() {
	    return situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA;
	}
	
	public boolean isAguardandoConvocacao() {
	    return situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO;
	}

	public boolean isAguardandoValidacao() {
		return getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.AGUARDANDO_VALIDACAO_PROGRAD;
	}

	public boolean isDiscenteConvocado() {
		return discenteConvocado;
	}

	public void setDiscenteConvocado(boolean discenteConvocado) {
		this.discenteConvocado = discenteConvocado;
	}
	
	
}