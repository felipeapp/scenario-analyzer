package br.ufrn.rh.dominio;

import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;

/**
 * 
 * Informa uma atividade que um servidor está realizando, denominada de designação.
 * 
 * @author Gleydson Lima
 * 
 */
public class Designacao implements PersistDB  {

	public static final String TITULAR = "T";
	public static final String SUBSTITUTO = "S";
	
	/** Identificador da entidade*/
	private int id;

	/** Servidor associado à designação */
	private Servidor servidor;

	/** Identificador da atividade. A designação é referente a uma atividade do servidor.*/
	private AtividadeServidor atividade;

	/** Data inicial da designação */
	private Date inicio;

	/** Data de término da designação*/
	private Date fim;

	/** Código do Siape para a unidade */
	private String uorg;

	/** Nome da unidade vindo do SIAPE */
	private String unidade;
	
	/** Identificador da Unidade associada a designacao. Para a qual o servidor está designado à alguma atividade */
	private Integer idUnidade;
	
	/***************************************************************************
	 * Identificação da sigla do designado - FG - Função Gratificada FC -
	 * Função comissionada CD - Cargo Diretor com os dois últimos dígitos do
	 * Codigo Nivel da Função
	 **************************************************************************/
	private NivelDesignacao nivel;
	
	/** Tipo de gerência: T - Titular ou S - Substituto * */
	private String gerencia;
	
	/** Indica se a designação é remunerada ou não */
	private boolean remuneracao;
	
	/** 
	 * Em caso de ser uma designação de substituto, indicação do servidor titular para a designação. *
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
