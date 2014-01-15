/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Cria��o: 20/04/2009
 */
package br.ufrn.comum.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.notificacoes.GrupoDestinatarios;

/**
 * Tela de aviso com mensagem personalizada que aparece
 * ap�s o logon para determinados usu�rios.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="tela_aviso_logon", schema="comum")
public class TelaAvisoLogon implements Validatable {

	@Id @GeneratedValue
	private int id;
	
	/** Nome da tela de aviso */
	private String nome;
	
	/** Descri��o da tela de aviso */
	private String descricao;
	
	/** Mensagem que ser� mostrada na tela de aviso */
	private String conteudo;
	
	/** In�cio da publica��o do aviso */
	@Temporal(TemporalType.DATE)
	private Date inicio;
	
	/** Fim da publica��o do aviso */
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	/** Se a tela est� ativa ou n�o */
	private boolean ativo;
	
	/** Destinat�rios que ir�o visualizar a tela de aviso */
	@ManyToMany @JoinTable(name="tela_aviso_grupo_destinatarios", schema="comum",  
			joinColumns={@JoinColumn(name="id_tela_aviso_logon")}, 
			inverseJoinColumns={@JoinColumn(name="id_grupo_destinatario")})
	private List<GrupoDestinatarios> destinatarios;

	/** Sistemas nos quais a tela de aviso aparecer�. */
	@ManyToMany @JoinTable(name="tela_aviso_sistemas", schema="comum",
			joinColumns={@JoinColumn(name="id_tela_aviso_logon")},
			inverseJoinColumns={@JoinColumn(name="id_sistema")})
	private List<Sistema> sistemas;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public List<GrupoDestinatarios> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(List<GrupoDestinatarios> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public List<Sistema> getSistemas() {
		return sistemas;
	}

	public void setSistemas(List<Sistema> sistemas) {
		this.sistemas = sistemas;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateMaxLength(nome, 50, "Nome", lista);
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		ValidatorUtil.validateMaxLength(descricao, 150, "Descri��o", lista);
		ValidatorUtil.validateRequired(conteudo, "Conte�do", lista);
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateRequired(inicio, "In�cio", lista);
		if (fim != null)
			ValidatorUtil.validaInicioFim(inicio, fim, "In�cio", lista);
		ValidatorUtil.validateRequired(sistemas, "Sistemas", lista);
		ValidatorUtil.validateRequired(destinatarios, "Destinat�rios", lista);
		return lista;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TelaAvisoLogon other = (TelaAvisoLogon) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
