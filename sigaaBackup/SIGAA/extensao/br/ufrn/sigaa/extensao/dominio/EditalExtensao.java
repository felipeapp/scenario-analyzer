/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/11/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.Edital;

/*******************************************************************************
 * <p>
 * Representa um edital de extens�o. Um edital contem informa��es importantes
 * como prazos e outros limites para envio de propostas de a��es de extens�o �
 * Pr�-Reitoria de extens�o.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "edital_extensao", schema = "extensao")
public class EditalExtensao implements Validatable {

	/** Atributo utilizado para representar o ID do Edital de Extens�o */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_edital_extensao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="extensao.edital_extensao_sequence") })
	private int id;

	/** Edital que possui os campos comuns a um edital. */ 
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_edital")
	private Edital edital = new Edital();

	/** N�mero de bolsas dispon�veis neste Edital. */
	@Column(name = "numero_bolsas")
	private short numeroBolsas;

	/** Valor total dispon�vel para financiamento. */
	@Column(name = "valor_financiamento")
	private double valorFinanciamento = 0;

	/** N�mero do Edital. */
	@Column(name = "numero_edital")
	private String numeroEdital;

	/** Data limite para os chefes de departamento autorizarem a a��o. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_autorizacao_chefe")
	private Date dataFimAutorizacaoChefe;

	/** Atributo utilizado para representar as regras do Edital de Extens�o */
	@OneToMany(mappedBy = "editalExtensao")
	private Collection<EditalExtensaoRegra> regras = new ArrayList<EditalExtensaoRegra>();

	// Constructors

	/** default constructor */
	public EditalExtensao() {
		setTipo('E'); // E - Extensao
	}

	/** full constructor */
	public EditalExtensao(int idEditalMonitoria, short numeroBolsas,
			double valorFinanciamento, String numeroEdital) {
		this.numeroBolsas = numeroBolsas;
		this.valorFinanciamento = valorFinanciamento;
		this.numeroEdital = numeroEdital;
	}

	public short getNumeroBolsas() {
		return this.numeroBolsas;
	}

	public void setNumeroBolsas(short numeroBolsas) {
		this.numeroBolsas = numeroBolsas;
	}

	public double getValorFinanciamento() {
		return this.valorFinanciamento;
	}

	public void setValorFinanciamento(double valorFinanciamento) {
		this.valorFinanciamento = valorFinanciamento;
	}

	public String getNumeroEdital() {
		return this.numeroEdital;
	}

	public void setNumeroEdital(String numeroEdital) {
		this.numeroEdital = numeroEdital;
	}

	/**
	 * 
	 * Realiza as valida��es das informa��es obrigat�rias para o cadastro de um Edital de Extens�o.
	 * 
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		lista.addAll(edital.validate().getMensagens());
		ValidatorUtil.validaInt(numeroBolsas, "N�mero de Bolsas", lista);
		ValidatorUtil.validaDouble(valorFinanciamento, "Valor do Financiamento", lista);
		ValidatorUtil.validateRequired(numeroEdital, "N�mero do Edital", lista);		
		ValidatorUtil.validateRequired(edital.getDataInicioReconsideracao(), "Data in�cio do limite para envio de reconsidera��o", lista);
		ValidatorUtil.validateRequired(edital.getDataFimReconsideracao(), "Data fim do limite para envio de reconsidera��o", lista);
		ValidatorUtil.validateRequired(regras, "Regras do Edital", lista);
		return lista;
	}

	@Transient
	public String getDescricaoCompleta() {
		return this.numeroEdital + " - " + getDescricao();
	}

	public Date getDataFimAutorizacaoChefe() {
		return dataFimAutorizacaoChefe;
	}

	public void setDataFimAutorizacaoChefe(Date dataFimAutorizacaoChefe) {
		this.dataFimAutorizacaoChefe = dataFimAutorizacaoChefe;
	}

	/**
	 * Retorna true se a submiss�o de projetos ainda esta em aberto e se a data
	 * final para autoriza��o dos projetos pelos chefes ainda n�o passou.
	 * 
	 * inicioSubmissao <= hoje e dataFimAutorizacaoChefe >= hoje
	 * 
	 * @return
	 */
	public boolean isPermitidoAutorizacaoChefe() {
		return (this.getInicioSubmissao().compareTo(
				DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) <= 0)
				&& (this.getDataFimAutorizacaoChefe().compareTo(
						DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) >= 0);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Edital getEdital() {
		return edital;
	}

	public void setEdital(Edital edital) {
		this.edital = edital;
	}

	public String getDescricao() {
		return edital.getDescricao();
	}

	/**
	 * M�todo utilizado para setar a descri��o do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		edital.setDescricao(descricao);
	}

	public Integer getIdArquivo() {
		return edital.getIdArquivo();
	}

	/**
	 * M�todo utilizado para setar o Id do Arquivo do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param idArquivo
	 */
	public void setIdArquivo(Integer idArquivo) {
		edital.setIdArquivo(idArquivo);
	}

	public char getTipo() {
		return edital.getTipo();
	}

	/**
	 * M�todo utilizado para setar o tipo do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param tipo
	 */
	public void setTipo(char tipo) {
		edital.setTipo(tipo);
	}

	public Date getInicioSubmissao() {
		return edital.getInicioSubmissao();
	}

	/**
	 * M�todo utilizado para setar a data de in�cio de submiss�o do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param inicioSubmissao
	 */
	public void setInicioSubmissao(Date inicioSubmissao) {
		edital.setInicioSubmissao(inicioSubmissao);
	}

	public Date getFimSubmissao() {
		return edital.getFimSubmissao();
	}

	/**
	 * M�todo utilizado para setar a data de fim de submiss�o do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param fimSubmissao
	 */
	public void setFimSubmissao(Date fimSubmissao) {
		edital.setFimSubmissao(fimSubmissao);
	}

	public Date getDataCadastro() {
		return edital.getDataCadastro();
	}

	/**
	 * M�todo utilizado para setar a data de cadastro do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		edital.setDataCadastro(dataCadastro);
	}

	public Usuario getUsuario() {
		return edital.getUsuario();
	}

	/**
	 * M�todo utilizado para setar ao usu�rio do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param usuario
	 */
	public void setUsuario(Usuario usuario) {
		edital.setUsuario(usuario);
	}

	public String getTipoString(){
		return edital.getTipoString();
	}

	public Integer getAno() {
		return edital.getAno();
	}

	/**
	 * M�todo utilizado para setar o ano do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param ano
	 */
	public void setAno(Integer ano) {
		edital.setAno(ano);
	}

	public int getSemestre() {
		return edital.getSemestre();
	}

	/**
	 * M�todo utilizado para setar o semestre do Edital de Extens�o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param semestre
	 */
	public void setSemestre(int semestre) {
		edital.setSemestre(semestre);
	}

	public boolean isAtivo() {
		return edital.isAtivo();
	}

	/**
	 * M�todo utilizado para setar se o Edital de Extens�o � ou n�o ativo.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		edital.setAtivo(ativo);
	}

	/**
	 * M�todo utilizado para mostrar a descri��o do Edital de Extens�o em forma de String.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 */
	public String toString() {
		return getDescricao() + " (" + getTipoString() + ")";
	}

	public boolean isAssociado() {
		return edital.getTipo() == Edital.ASSOCIADO;
	}

	/**
	 * Retorna somente as regras ativas do edital.
	 * @return
	 */
	public Collection<EditalExtensaoRegra> getRegrasAtivas() {
	    ArrayList<EditalExtensaoRegra> listaAtivos = new ArrayList<EditalExtensaoRegra>();
	    for(Iterator<EditalExtensaoRegra> it = regras.iterator(); it.hasNext();) {
		EditalExtensaoRegra eer = it.next();
		if (eer.isAtivo()) {
		    listaAtivos.add(eer);
		}		    
	    }
	    return listaAtivos;
	}
	
	public Collection<EditalExtensaoRegra> getRegras() {
	    return regras;
	}

	public void setRegras(Collection<EditalExtensaoRegra> regras) {
	    this.regras = regras;
	}
	
	/**
	 * M�todo utilizado para adicionar uma regra ao Edital de Extens�o � ou n�o ativo.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSP(s)</li>
	 * </ul>
	 * 
	 * @param novaRegra
	 * @return
	 */
	public boolean addRegra(EditalExtensaoRegra novaRegra) {
		novaRegra.setEditalExtensao(this);
		return regras.add(novaRegra);
	}
	
	/**
	 * Retorna a regra do edital para o tipo de a��o de extens�o informado.
	 * 
	 * @param tipo
	 * @return
	 */
	public EditalExtensaoRegra getRegraByTipo(TipoAtividadeExtensao tipo) {
	    for(EditalExtensaoRegra regra: getRegras()) {
		if (regra.getTipoAtividadeExtensao().getId() == tipo.getId() && regra.isAtivo())
		    return regra;
	    }
	    return null;
	}

}
