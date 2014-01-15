package br.ufrn.rh.dominio;

import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;

/**
 * 
 * Informa uma atividade que um servidor est� realizando, denominada de designa��o.
 * 
 * @author Gleydson Lima
 * 
 */
public class Designacao implements PersistDB  {

	public static final String TITULAR = "T";
	public static final String SUBSTITUTO = "S";
	
	/** Identificador da entidade*/
	private int id;

	/** Servidor associado � designa��o */
	private Servidor servidor;

	/** Identificador da atividade. A designa��o � referente a uma atividade do servidor.*/
	private AtividadeServidor atividade;

	/** Data inicial da designa��o */
	private Date inicio;

	/** Data de t�rmino da designa��o*/
	private Date fim;

	/** C�digo do Siape para a unidade */
	private String uorg;

	/** Nome da unidade vindo do SIAPE */
	private String unidade;
	
	/** Identificador da Unidade associada a designacao. Para a qual o servidor est� designado � alguma atividade */
	private Integer idUnidade;
	
	/***************************************************************************
	 * Identifica��o da sigla do designado - FG - Fun��o Gratificada FC -
	 * Fun��o comissionada CD - Cargo Diretor com os dois �ltimos d�gitos do
	 * Codigo Nivel da Fun��o
	 **************************************************************************/
	private NivelDesignacao nivel;
	
	/** Tipo de ger�ncia: T - Titular ou S - Substituto * */
	private String gerencia;
	
	/** Indica se a designa��o � remunerada ou n�o */
	private boolean remuneracao;
	
	/** 
	 * Em caso de ser uma designa��o de substituto, indica��o do servidor titular para a designa��o. *
	 */
	private Servidor servidorTitular;

	public AtividadeServidor getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeServidor atividade) {
		this.atividade = atividade;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getUorg() {
		return uorg;
	}

	public void setUorg(String uorg) {
		this.uorg = uorg;
	}

	public NivelDesignacao getNivel() {
		return nivel;
	}

	public void setNivel(NivelDesignacao nivel) {
		this.nivel = nivel;
	}

	public Integer getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(Integer idUnidade) {
		this.idUnidade = idUnidade;
	}

	public String getGerencia() {
		return gerencia;
	}

	public void setGerencia(String gerencia) {
		this.gerencia = gerencia;
	}

	public Servidor getServidorTitular() {
		return servidorTitular;
	}

	public void setServidorTitular(Servidor servidorTitular) {
		this.servidorTitular = servidorTitular;
	}

	public boolean isRemuneracao() {
		return remuneracao;
	}

	public void setRemuneracao(boolean remuneracao) {
		this.remuneracao = remuneracao;
	}
}
