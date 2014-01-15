/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Classe abstrata que representa as punições que o usuário pode obter por atrazar emprestimo na biblioteca </p>
 * 
 * <p> <i> Essa classe não é para ser salva no banco é apenas para criar uma abstração. </i> </p>
 * 
 * @author jadson
 *
 */
@MappedSuperclass
public abstract class PunicaoAtrasoEmprestimoBiblioteca implements PersistDB{

	
	/************ Informações do cadastro de uma multa ou suspensão manual para um usuário  ***************/
	
	/** Usuário que casdastrou a punição quando a punição é cadastrada manualmente */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro")
	protected Usuario usuarioCadastro;
	
	/** Data de cadastro. (também quando a punição é manual)*/
	@CriadoEm
	@Column(name="data_cadastro", nullable=false)
	protected Date dataCadastro;
	
	
	/** O motivo para o cadastro desta punição manualmente. */
	@Column (name="motivo_cadastro")
	protected String motivoCadastro;
	
	/*********************************************************************/
	
	
	
	
	/************ Informações do estrono ********************/
	
	/** Usuário que estornou esta punição. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_estorno")
	protected Usuario usuarioEstorno;
	
	/** Data em que o estorno foi realizado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_estorno")
	protected Date dataEstorno;
	
	/** O motivo para o estorno desta punição. */
	@Column (name="motivo_estorno")
	protected String motivoEstorno;
	
	/*********************************************************************/

	
	
	
	
	/************************************************************************/
	
	
	/**
	 * <p>Indica o usuário a quem pertence esta punição, para as punição cadastradas manualmente. </p> 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn (name="id_usuario_biblioteca", nullable=true)
	protected UsuarioBiblioteca usuarioBiblioteca;
	
	
	/** 
	 * <p>O Empréstimo que causou essa punição. (O usuário que foi punido está aqui.) </p>
	 * <p> <i>Para punição que são geradas automaticamente, por atraso no empréstimo.</i> </p>
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_emprestimo", referencedColumnName="id_emprestimo", nullable=true)
	protected Emprestimo emprestimo;
	
	/************************************************************************/
	
	
	
	
	
	/** Indica se esta suspensão foi criada manualmente. */
	protected boolean manual = false;
	
	
	/** Indica quando uma multa/suspensão foi extornada pelo operador de circulação */
	@Column(nullable=false)
	protected boolean ativo = true;
	
	
	/** Mensagens que deve ser impressa no comprovante de devolução, cada tipo de punição vai ter a sua mensagem personalizada, 
	 * porque as mensagem variam por tipo de punição, algumas punições podem ser em dias, outras em reais, etc... 
	 * 
	 *  Quem vai gerar esse mensagem é a estratégia de punição 
	 */
	@Transient
	protected String mensagemComprovante;
	
	
	/*** Multas selecionadas para executa alguma ação */
	@Transient
	protected boolean selecionado;

	
	/**
	 * A situação em que ficou o usuário pela punição gerada.
	 * Cada tipo de punição gera uma situação deferente. 
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
