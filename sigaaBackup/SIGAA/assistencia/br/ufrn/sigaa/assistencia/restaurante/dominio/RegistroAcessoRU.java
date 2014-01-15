/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/02/2009
 *
 */	
package br.ufrn.sigaa.assistencia.restaurante.dominio;

import java.util.Date;

import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que representa os acessos feitos ao RU através da catraca. Essa entidade é utilizada
 * através de JDBC puro. 
 * 
 * @author agostinho campos
 *
 */
public class RegistroAcessoRU {
	
	private int id;
	private Date dataHora;
	/** Justificativa feita pelo usuário do aplicativo Desktop para liberar determinado aluno **/
	private String outraJustificativa;
	private Discente discente;
	private Usuario usuario;
	/** Tipo de liberação que foi realizada pelo usuário **/
	private TipoLiberacaoAcessoRU tipoLiberacao;
	/** Tipo da Bolsa que foi liberada **/
	private TipoBolsaAuxilio tipoBolsa;
	private String refeicao;
	private Integer totalRefeicao;

	public Integer getTotalRefeicao() {
		return totalRefeicao;
	}
	public void setTotalRefeicao(Integer totalRefeicao) {
		this.totalRefeicao = totalRefeicao;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
	public String getOutraJustificativa() {
		return outraJustificativa;
	}
	public void setOutraJustificativa(String outraJustificativa) {
		this.outraJustificativa = outraJustificativa;
	}
	public Discente getDiscente() {
		return discente;
	}
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public TipoLiberacaoAcessoRU getTipoLiberacao() {
		return tipoLiberacao;
	}
	public void setTipoLiberacao(TipoLiberacaoAcessoRU tipoLiberacao) {
		this.tipoLiberacao = tipoLiberacao;
	}
	public TipoBolsaAuxilio getTipoBolsa() {
		return tipoBolsa;
	}
	public void setTipoBolsa(TipoBolsaAuxilio tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}
	public String getRefeicao() {
		return refeicao;
	}
	public void setRefeicao(String refeicao) {
		this.refeicao = refeicao;
	}	
}
