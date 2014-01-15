/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '14/01/2009'
 *
 */
package br.ufrn.sigaa.bolsas.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DTO para encapsulamento das informações de bolsas registradas no sistema
 * administrativo
 * 
 * @author wendell
 *
 */
public class Bolsista implements Validatable {

	public static final int BOLSA_CNPQ = 81;
	
	private int idBolsa;
	
	private int idBolsista;
	
	private int idTipoBolsa;
	private String tipoBolsa;
	
	private Discente discente;
	
	private Unidade unidade;

	/** Período da bolsa */
	private Date dataInicio, dataFim;
	
    /** Indica se a bolsa foi finalizada */
    private boolean finalizada;
    
    /** Data de finalização da bolsa */
    private Date dataFinalizacao;
    
    /** Usuário que finalizou a bolsa */
    private Usuario usuarioFinalizacao;
    
    /**
     * Indica se um aluno é carente ou não. adicionado para ser
     * usado no relatório do SAE.
     * @return
     */
    private boolean carente;

	public int getIdBolsa() {
		return idBolsa;
	}

	public void setIdBolsa(int idBolsa) {
		this.idBolsa = idBolsa;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public int getIdTipoBolsa() {
		return idTipoBolsa;
	}

	public void setIdTipoBolsa(int idTipoBolsa) {
		this.idTipoBolsa = idTipoBolsa;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public boolean isFinalizada() {
		return finalizada;
	}

	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
	}

	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	public Usuario getUsuarioFinalizacao() {
		return usuarioFinalizacao;
	}

	public void setUsuarioFinalizacao(Usuario usuarioFinalizacao) {
		this.usuarioFinalizacao = usuarioFinalizacao;
	}

	public int getIdBolsista() {
		return idBolsista;
	}

	public void setIdBolsista(int idBolsista) {
		this.idBolsista = idBolsista;
	}
	
	public String getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(String tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public boolean isCarente() {
		return carente;
	}

	public void setCarente(boolean carente) {
		this.carente = carente;
	}

	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		
		validateRequired(discente, "Discente",mensagens.getMensagens());
		validateRequired(dataInicio, "Data de início da bolsa", mensagens.getMensagens());
		validateMinValue(dataFim, dataInicio, "Data de fim da bolsa", mensagens.getMensagens());
		
		return mensagens;
	}

	public int getId() {
		return idBolsa;
	}

	public void setId(int id) {
		this.idBolsa = id;
	}
}
