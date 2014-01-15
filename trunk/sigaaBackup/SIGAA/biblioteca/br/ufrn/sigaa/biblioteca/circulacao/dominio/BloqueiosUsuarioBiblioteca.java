/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 14/06/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p> Classe que guarda o hist�rico de bloqueios e desbloqueios de um usu�rio da biblioteca </p>
 * <p> Um usu�rio vai est� bloqueado se a �ltima opera��o for de de bloqueio. </p>
 * <p> Serve para bloquear a conta do usu�rio indefinidamente, n�o importa o v�nculo que o usu�rio tenha na institui��o. </p>
 * <p> Outros bloqueio bloqueam por v�nculo, quando o usu�rio � quitado ou o v�nculo dele � desativado (por exemplo, concluiu o curso ).</p>
 * 
 * @author jadson
 * @version 2.0 12/04/2011 o bloqueio vai ser da pessoa ou biblioteca. J� que o usu�rio vai passar a poder possuir
 * v�rios usu�rios biblioteca durante a sua vida acad�mica, mas apenas um ativo e n�o quitado por vez.
 *
 */
@Entity
@Table(name = "bloqueios_usuario_biblioteca", schema = "biblioteca")
public class BloqueiosUsuarioBiblioteca implements Validatable {

	/**
	 * Se a opera��o foi um bloqueio 
	 */
	public static final int BLOQUEIO = 1;
	
	/**
	 * Se a opera��o foi um desbloqueio 
	 */
	public static final int DESBLOQUEIO = 2;
	
	/**
	 * O id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.historicos_alteracao_sequence") })
	@Column(name="id_bloqueios_usuario_biblioteca")
	private int id;
	
	
	
	/**
	 *  Se a opera��o realizada foi um bloqueio ou desbloqueio
	 */
	@Column(name = "tipo", nullable=false)
	private int tipo = BLOQUEIO;
	
	
	/**
	 * O motivo do bloqueio/desbloqueio
	 */
	@Column(name = "motivo", nullable=false)
	private String motivo;  
	
	/**
	 * A data em que o bloqueio/desbloqueio foi realizado
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable=false)
	private Date data;
	
	/**
	 * O usu�rio que realizou o bloqueio/desbloqueio.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_operacao", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouOperacao;
	
	
//	/** Usu�rio Biblioteca que foi bloqueio/desbloqueio. */
//	@ManyToOne(cascade  =  {})
//	@JoinColumn(name = "id_usuario_biblioteca", referencedColumnName ="id_usuario_biblioteca")
//	private UsuarioBiblioteca usuarioBiblioteca;

	
	/* **************************************************************************************
	 *                                                                                      *
	 * **************************************************************************************/
	
	/** Se o usu�rio do empr�stimo for uma pessoa . */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;

	
	/** Se o usu�rio do empr�stimo for uma biblioteca. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn (name="id_biblioteca", referencedColumnName="id_biblioteca")
	private Biblioteca biblioteca;	
	
	
	/* **************************************************************************************
	 *                                                                                      *
	 * **************************************************************************************/
	
	
	/**
	 * Construtor padr�o
	 */
	public BloqueiosUsuarioBiblioteca() {
		
	}

	/**
	 * Construtor usado para registrar um hist�rio de bloqueio/desbloqueio uma pessoa.
	 * 
	 * @param tipo
	 * @param motivo
	 * @param data
	 * @param usuarioRealizouOperacao
	 * @param usuarioBiblioteca
	 */
	public BloqueiosUsuarioBiblioteca(int tipo, String motivo, Date data, Usuario usuarioRealizouOperacao, Pessoa pessoa) {
		this.tipo = tipo;
		this.motivo = motivo;
		this.data = data;
		this.usuarioRealizouOperacao = usuarioRealizouOperacao;
		this.pessoa = pessoa;
	}

	/**
	 * Construtor usado para registrar um hist�rio de bloqueio/desbloqueio uma pessoa.
	 * 
	 * @param tipo
	 * @param motivo
	 * @param data
	 * @param usuarioRealizouOperacao
	 * @param usuarioBiblioteca
	 */
	public BloqueiosUsuarioBiblioteca(int tipo, String motivo, Date data, Usuario usuarioRealizouOperacao, Biblioteca biblioteca) {
		this.tipo = tipo;
		this.motivo = motivo;
		this.data = data;
		this.usuarioRealizouOperacao = usuarioRealizouOperacao;
		this.biblioteca = biblioteca;
	}
	
	

	/**
	 * Valida os dados para salvar um novo bloqueio ou desbloqueio no banco.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		if(StringUtils.isEmpty(motivo))
			erros.addErro("Informe o motivo do bloqueio ou desbloqueio do usu�rio");
		else
			if(motivo.length() > 200)
				erros.addErro("Tamanho m�ximo do campo motivo ultrapassado.");
		
		return erros;
	}
	
	
	///// sets e gets //////
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTipo() {
		return tipo;
	}


	public void setTipo(int tipo) {
		this.tipo = tipo;
	}


	public String getMotivo() {
		return motivo;
	}


	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}


	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public Usuario getUsuarioRealizouOperacao() {
		return usuarioRealizouOperacao;
	}


	public void setUsuarioRealizouOperacao(Usuario usuarioRealizouOperacao) {
		this.usuarioRealizouOperacao = usuarioRealizouOperacao;
	}


//	public UsuarioBiblioteca getUsuarioBiblioteca() {
//		return usuarioBiblioteca;
//	}
//
//
//	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {
//		this.usuarioBiblioteca = usuarioBiblioteca;
//	}

	
	
	public int getBloqueio() {
		return BLOQUEIO;
	}

	
	public int getDesbloqueio() {
		return DESBLOQUEIO;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

}
