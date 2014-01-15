/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;

/** Entidade que armazena os dados dos editais dos diversos tipos de projeto no sistema */
@Entity
@Table(name = "edital", schema = "projetos")
public class Edital implements Validatable {

	/** Atributo utilizado como constante para N�O INFORMADO */
	public static final char NAO_INFORMADO = 'Z'; 
	/** Atributo utilizado como constante para ASSOCIADO */
	public static final char ASSOCIADO 		= 'A';
	/** Atributo utilizado como constante para MONITORIA */
	public static final char MONITORIA 		= 'M';
	/** Atributo utilizado como constante para PESQUISA */
	public static final char PESQUISA 		= 'P';
	/** Atributo utilizado como constante para EXTENSAO */
	public static final char EXTENSAO 		= 'E';
	/** Atributo utilizado como constante para INOVACAO */
	public static final char INOVACAO 		= 'I'; //PAMQEG
	/** Atributo utilizado como constante para AMBOS */
	public static final char AMBOS_MONITORIA_E_INOVACAO 		= 'B';
	/** Atributo utilizado como constante quando � uma edital de monitoria e/ou inova��o */
	public static final char MONITORIA_EOU_INOVACAO 		= 'O';
	/** Atributo utilizado como constante para edital de projeto de monitoria externo. */
	public static final char MONITORIA_EXTERNO 		= 'X'; 
	
	/** Atributo utilizado para representar o n�mero id do Edital */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_edital")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	private int id;

	/** descri��o do edital */
	@Column(name = "descricao")
	private String descricao;

	/** 
	 * Tipo de edital
	 * 
	 * M - Monitoria (Ensino)
	 * I - Inova��o (Ensino)
	 * P - Pesquisa		
	 * E - Extens�o
	 * A - Associados
	 */
	@Column(name = "tipo")
	private char tipo;

	/** Refer�ncia ao arquivo do edital, normalmente em pdf */
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** In�cio do prazo de submiss�o de projetos */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_submissao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date inicioSubmissao;

	/** Fim do prazo de submiss�o de projetos */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_submissao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date fimSubmissao;
	
	/** In�cio do per�odo de realiza��o de projetos */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_realizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date inicioRealizacao;
	
	/** Fim do per�odo de realiza��o de projetos */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_realizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date fimRealizacao;

	/** Data de cadastro do edital */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataCadastro;

	/** Usu�rio que cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", unique = false, nullable = false, insertable = true, updatable = true)
	private Usuario usuario;

	/** In�cio do prazo para reconsidera��o de avalia��es*/
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_reconsideracao")
	private Date dataInicioReconsideracao;

	/** Fim do prazo para reconsidera��o de avalia��es*/
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_reconsideracao")
	private Date dataFimReconsideracao;

	/** Atributo utilizado para representar a restri��o da coordena��o */
	@Embedded
	private RestricaoCoordenacao restricaoCoordenacao = new RestricaoCoordenacao();

	
	/** Ano do edital */
	@Column(name = "ano")	
	private Integer ano = 0;
	
	/** Per�odo acad�mico do edital */
	@Column(name = "semestre")	
	private int semestre;

	/** informa se o edital est� ativo ou n�o */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo;

	/** Atributo utilizado para representar o registro de entrada */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
	/** Valor limite do financiamento para projetos que envolvem s� uma dimens�o acad�mica. */ 
	@Column(name = "primeiro_limite_orcamentario")
	private double primeiroLimiteOrcamentario = 0.0;

	/** Valor limite do financiamento para projetos que envolvem 2 dimens�es acad�micas. */ 
	@Column(name = "segundo_limite_orcamentario")
	private double segundoLimiteOrcamentario = 0.0;

	/** Valor limite do financiamento para projetos que envolvem 3 dimens�es acad�micas. */ 
	@Column(name = "terceiro_limite_orcamentario")
	private double terceiroLimiteOrcamentario = 0.0;
	
	/** � utilizado para indicar se o edital � de concess�o de bolsas ou n�o */
	@Transient
	private boolean editalConcessao = false;
	
	public Edital(Integer idArquivo, String descricao, char tipo) {
		this.idArquivo = idArquivo;
		this.descricao = descricao;
		this.tipo = tipo;
	}

	public Edital() {

	}

	/**
	 * Retorna true se a data final para recebimento de projetos ainda n�o
	 * expirou.
	 *
	 * @return
	 */
	public boolean isEmAberto() {
		if (getInicioSubmissao() != null && getFimSubmissao() != null) {
			return CalendarUtils.isDentroPeriodo(getInicioSubmissao(), getFimSubmissao());
		}
		return false;
	}
	
	public boolean isAssociado() {
		return tipo == ASSOCIADO;
	}
	
	public boolean isPesquisa() {
	    return tipo == PESQUISA;
	}

	public boolean isMonitoria() {
	    return tipo == MONITORIA;
	}

	public boolean isExtensao() {
	    return tipo == EXTENSAO;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	public Date getInicioSubmissao() {
		return inicioSubmissao;
	}

	public Date getFimSubmissao() {
		return fimSubmissao;
	}

	public void setFimSubmissao(Date fimSubmissao) {
		this.fimSubmissao = fimSubmissao;
	}

	public void setInicioSubmissao(Date inicioSubmissao) {
		this.inicioSubmissao = inicioSubmissao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/** 
	 * M�todo utilizado para informar o tipo do Edital em String
	 * @return
	 */
	@Transient
	public String getTipoString(){
		switch(this.tipo) {
			case ASSOCIADO: return "A��ES ACAD�MICAS INTEGRADAS";
			case MONITORIA: return "MONITORIA";
			case PESQUISA: return "PESQUISA";
			case EXTENSAO: return "EXTENS�O";
			case INOVACAO: return "PAMQEG";
			case AMBOS_MONITORIA_E_INOVACAO: return "MONITORIA E PAMQEG";
			case MONITORIA_EOU_INOVACAO: return "MONITORIA E/OU PAMQEG";
			default: return "EXTERNO";
		}
	}

	/**
	 * M�todo utilizado para validar os atributos do Edital.
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (getTipo() == NAO_INFORMADO){
			lista.addErro("Tipo de Edital: Campo obrigat�rio n�o informado.");
		}
		ValidatorUtil.validateRequired(getDescricao(), "Descri��o", lista);
		ValidatorUtil.validateRequired(getAno(), "Ano", lista);
		ValidatorUtil.validateRequired(getSemestre(), "Semestre", lista);
		ValidatorUtil.validateRequired(getInicioSubmissao(), "Iniciar Recebimento de Projetos em", lista);
		ValidatorUtil.validateRequired(getFimSubmissao(), "Receber Projetos at�", lista);
		ValidatorUtil.validaInicioFim(getInicioSubmissao(), getFimSubmissao(), "Per�odo de Recebimento de Projetos", lista);
		ValidatorUtil.validateRequired(getInicioRealizacao(), "In�cio da Realiza��o", lista);		
		ValidatorUtil.validateRequired(getFimRealizacao(), "Fim da Realiza��o", lista);
		ValidatorUtil.validaInicioFim(getInicioRealizacao(), getFimRealizacao(), "Per�odo de Realiza��o", lista);
		ValidatorUtil.validateRequired(getDataInicioReconsideracao(), "In�cio da Reconsidera��o da Avalia��o", lista);
		ValidatorUtil.validateRequired(getDataFimReconsideracao(), "Fim da Reconsidera��o da Avalia��o", lista);
		ValidatorUtil.validaInicioFim(getDataInicioReconsideracao(), getDataFimReconsideracao(), "Per�odo de Reconsidera��o da Avalia��o", lista);
		
		if (!ValidatorUtil.isEmpty(getRestricaoCoordenacao())) {
		    ValidatorUtil.validateRequired(getRestricaoCoordenacao().getMaxCoordenacoesAtivas(), "M�ximo de Coordena��es Ativas por Docente neste Edital", lista);
		}
		
		if (ano == 0 || semestre == 0) {
			lista.addErro("Ano-Semestre: Valor informado � inv�lido.");	
		}
		
		ValidatorUtil.validateRequired(getPrimeiroLimiteOrcamentario(), "Fim da Reconsidera��o da Avalia��o", lista);
		ValidatorUtil.validateRequired(getSegundoLimiteOrcamentario(), "Fim da Reconsidera��o da Avalia��o", lista);
		ValidatorUtil.validateRequired(getTerceiroLimiteOrcamentario(), "Fim da Reconsidera��o da Avalia��o", lista);
		
		return lista;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/**
	 * M�todo utilizado para informar a descri��o do tipo do edital em forma de String.
	 */
	@Override
	public String toString() {
		return descricao + " (" + getTipoString() + ")";
	}

	public Date getInicioRealizacao() {
		return inicioRealizacao;
	}

	public void setInicioRealizacao(Date inicioRealizacao) {
		this.inicioRealizacao = inicioRealizacao;
	}

	public Date getFimRealizacao() {
		return fimRealizacao;
	}

	public void setFimRealizacao(Date fimRealizacao) {
		this.fimRealizacao = fimRealizacao;
	}

	public Date getDataInicioReconsideracao() {
	    return dataInicioReconsideracao;
	}

	public void setDataInicioReconsideracao(Date dataInicioReconsideracao) {
	    this.dataInicioReconsideracao = dataInicioReconsideracao;
	}

	public Date getDataFimReconsideracao() {
	    return dataFimReconsideracao;
	}

	public void setDataFimReconsideracao(Date dataFimReconsideracao) {
	    this.dataFimReconsideracao = dataFimReconsideracao;
	}

	public RegistroEntrada getRegistroEntrada() {
	    return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
	    this.registroEntrada = registroEntrada;
	}

	public double getPrimeiroLimiteOrcamentario() {
	    return primeiroLimiteOrcamentario;
	}

	public void setPrimeiroLimiteOrcamentario(double primeiroLimiteOrcamentario) {
	    this.primeiroLimiteOrcamentario = primeiroLimiteOrcamentario;
	}

	public double getSegundoLimiteOrcamentario() {
	    return segundoLimiteOrcamentario;
	}

	public void setSegundoLimiteOrcamentario(double segundoLimiteOrcamentario) {
	    this.segundoLimiteOrcamentario = segundoLimiteOrcamentario;
	}

	public double getTerceiroLimiteOrcamentario() {
	    return terceiroLimiteOrcamentario;
	}

	public void setTerceiroLimiteOrcamentario(double terceiroLimiteOrcamentario) {
	    this.terceiroLimiteOrcamentario = terceiroLimiteOrcamentario;
	}

	public RestricaoCoordenacao getRestricaoCoordenacao() {
	    return restricaoCoordenacao;
	}

	public void setRestricaoCoordenacao(RestricaoCoordenacao restricaoCoordenacao) {
	    this.restricaoCoordenacao = restricaoCoordenacao;
	}

	public boolean isEditalConcessao() {
		return editalConcessao;
	}

	public void setEditalConcessao(boolean editalConcessao) {
		this.editalConcessao = editalConcessao;
	}

}