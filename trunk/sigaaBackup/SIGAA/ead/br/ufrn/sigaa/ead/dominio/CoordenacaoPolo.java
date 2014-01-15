package br.ufrn.sigaa.ead.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que armazena os coordenadores de polo de ensino a dist�ncia
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "coordenacao_polo", schema = "ead")
public class CoordenacaoPolo implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_coordenacao_polo", nullable = false)
	private int id;

	@Temporal(TemporalType.DATE)
	/** In�cio da atividade de coordena��o */
	private Date inicio;

	@Temporal(TemporalType.DATE)
	/** Fim da atividade de coordena��o */
	private Date fim;

	@ManyToOne
	@JoinColumn(name="id_polo")
	/** P�lo do qual a pessoa � coordenadora */
	private Polo polo;

	@ManyToOne
	@JoinColumn(name="id_pessoa")
	/** Pessoa que � coordenadora de p�lo */
	private Pessoa pessoa;

	@Column(name="id_perfil")
	/** Perfil do coordenador de p�lo */
	private Integer idPerfil;
	
	@ManyToOne @JoinColumn(name="id_usuario")
	/** Usu�rio com o qual o coordenador de polo entra no sistema. */
	private Usuario usuario;

	/**
	 * @return the fim
	 */
	public Date getFim() {
		return fim;
	}

	/**
	 * @param fim the fim to set
	 */
	public void setFim(Date fim) {
		this.fim = fim;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the inicio
	 */
	public Date getInicio() {
		return inicio;
	}

	/**
	 * @param inicio the inicio to set
	 */
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	/**
	 * @return the pessoa
	 */
	public Pessoa getPessoa() {
		return pessoa;
	}

	/**
	 * @param pessoa the pessoa to set
	 */
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/**
	 * @return the polo
	 */
	public Polo getPolo() {
		return polo;
	}

	/**
	 * @param polo the polo to set
	 */
	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		if (inicio == null)
			lista.addErro("Data de in�cio inv�lida.");
		//if (fim == null)
		//	lista.addErro("Data de fim inv�lida.");
		if (inicio != null && fim != null && inicio.after(fim))
			lista.addErro("Data de in�cio n�o pode ser posterior � data de fim.");
		if (polo == null || polo.getId() == 0)
			lista.addErro("A informa��o do P�lo � obrigat�ria.");
		if (pessoa == null || pessoa.getId() == 0)
			lista.addErro("A informa��o da pessoa � obrigat�ria.");
		
		return lista;
	}

	public boolean isAtivo() {
		return (fim == null) || (new Date().before(fim));
	}

	public Integer getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
