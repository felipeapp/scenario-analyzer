/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 * <p> Classe abstrata que representa as puni��es que o usu�rio pode obter por atrazar emprestimo na biblioteca </p>
 * 
 * <p> <i> Essa classe n�o � para ser salva no banco � apenas para criar uma abstra��o. </i> </p>
 * 
 * @author jadson
 *
 */
@MappedSuperclass
public abstract class PunicaoAtrasoEmprestimoBiblioteca implements PersistDB{

	
	/************ Informa��es do cadastro de uma multa ou suspens�o manual para um usu�rio  ***************/
	
	/** Usu�rio que casdastrou a puni��o quando a puni��o � cadastrada manualmente */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro")
	protected Usuario usuarioCadastro;
	
	/** Data de cadastro. (tamb�m quando a puni��o � manual)*/
	@CriadoEm
	@Column(name="data_cadastro", nullable=false)
	protected Date dataCadastro;
	
	
	/** O motivo para o cadastro desta puni��o manualmente. */
	@Column (name="motivo_cadastro")
	protected String motivoCadastro;
	
	/*********************************************************************/
	
	
	
	
	/************ Informa��es do estrono ********************/
	
	/** Usu�rio que estornou esta puni��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_estorno")
	protected Usuario usuarioEstorno;
	
	/** Data em que o estorno foi realizado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_estorno")
	protected Date dataEstorno;
	
	/** O motivo para o estorno desta puni��o. */
	@Column (name="motivo_estorno")
	protected String motivoEstorno;
	
	/*********************************************************************/

	
	
	
	
	/************************************************************************/
	
	
	/**
	 * <p>Indica o usu�rio a quem pertence esta puni��o, para as puni��o cadastradas manualmente. </p> 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn (name="id_usuario_biblioteca", nullable=true)
	protected UsuarioBiblioteca usuarioBiblioteca;
	
	
	/** 
	 * <p>O Empr�stimo que causou essa puni��o. (O usu�rio que foi punido est� aqui.) </p>
	 * <p> <i>Para puni��o que s�o geradas automaticamente, por atraso no empr�stimo.</i> </p>
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_emprestimo", referencedColumnName="id_emprestimo", nullable=true)
	protected Emprestimo emprestimo;
	
	/************************************************************************/
	
	
	
	
	
	/** Indica se esta suspens�o foi criada manualmente. */
	protected boolean manual = false;
	
	
	/** Indica quando uma multa/suspens�o foi extornada pelo operador de circula��o */
	@Column(nullable=false)
	protected boolean ativo = true;
	
	
	/** Mensagens que deve ser impressa no comprovante de devolu��o, cada tipo de puni��o vai ter a sua mensagem personalizada, 
	 * porque as mensagem variam por tipo de puni��o, algumas puni��es podem ser em dias, outras em reais, etc... 
	 * 
	 *  Quem vai gerar esse mensagem � a estrat�gia de puni��o 
	 */
	@Transient
	protected String mensagemComprovante;
	
	
	/*** Multas selecionadas para executa alguma a��o */
	@Transient
	protected boolean selecionado;

	
	/**
	 * A situa��o em que ficou o usu�rio pela puni��o gerada.
	 * Cada tipo de puni��o gera uma situa��o deferente. 
	 */
	@Transient
	protected SituacaoUsuarioBiblioteca situacaoGeradaPelaPunicao;
	
	
	////// sets e gets ////

	public String getMensagemComprovante() {
		return mensagemComprovante;
	}

	public void setMensagemComprovante(String mensagemComprovante) {
		this.mensagemComprovante = mensagemComprovante;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getMotivoCadastro() {
		return motivoCadastro;
	}

	public void setMotivoCadastro(String motivoCadastro) {
		this.motivoCadastro = motivoCadastro;
	}

	public Usuario getUsuarioEstorno() {
		return usuarioEstorno;
	}

	public void setUsuarioEstorno(Usuario usuarioEstorno) {
		this.usuarioEstorno = usuarioEstorno;
	}

	public Date getDataEstorno() {
		return dataEstorno;
	}

	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	public String getMotivoEstorno() {
		return motivoEstorno;
	}

	public void setMotivoEstorno(String motivoEstorno) {
		this.motivoEstorno = motivoEstorno;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {
		this.usuarioBiblioteca = usuarioBiblioteca;
	}

	public Emprestimo getEmprestimo() {
		return emprestimo;
	}

	public void setEmprestimo(Emprestimo emprestimo) {
		this.emprestimo = emprestimo;
	}

	public boolean isManual() {
		return manual;
	}

	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public SituacaoUsuarioBiblioteca getSituacaoGeradaPelaPunicao() {
		return situacaoGeradaPelaPunicao;
	}

	public void setSituacaoGeradaPelaPunicao(SituacaoUsuarioBiblioteca situacaoGeradaPelaPunicao) {
		this.situacaoGeradaPelaPunicao = situacaoGeradaPelaPunicao;
	}
	
	
	
}
