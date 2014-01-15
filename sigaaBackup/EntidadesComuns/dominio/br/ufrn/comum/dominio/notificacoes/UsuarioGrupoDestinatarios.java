/**
 *
 */
package br.ufrn.comum.dominio.notificacoes;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe que associa usu�rios a grupos de destinat�rios para controlar
 * permiss�es de envio.
 * @author Leonardo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name="usuario_grupo_destinatarios", schema="comum")
public class UsuarioGrupoDestinatarios implements PersistDB, Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	/** Usu�rio associado ao grupo de destinat�rios */
	@ManyToOne
	@JoinColumn(name="id_usuario")
	private UsuarioGeral usuario;
	
	/** 
	 * Papel associado ao grupo de destinat�rios. A permiss�o pode ser dada
	 * a um usu�rio ou a um papel.
	 */
	@ManyToOne
	@JoinColumn(name="id_papel")
	private Papel papel;

	/** Grupo de destinat�rio ao qual pertence o usu�rio */
	@ManyToOne
	@JoinColumn(name="id_grupo_destinatarios")
	private GrupoDestinatarios grupoDestinatarios;

	/** Nome que ser� utilizado como nome de remetente para as notifica��es enviadas por esse usu�rio. */
	@Column(name="nome_remetente")
	private String nomeRemetente;
	
	/** Endere�o de e-mail que ser� utilizado como e-mail do remetente para as notifica��es enviadas por esse usu�rio. */
	@Column(name="endereco_remetente")
	private String enderecoRemetente;
	
	/** Se a associa��o est� ativa */
	private boolean ativo = true;

	public UsuarioGrupoDestinatarios() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public GrupoDestinatarios getGrupoDestinatarios() {
		return grupoDestinatarios;
	}

	public void setGrupoDestinatarios(GrupoDestinatarios grupoDestinatarios) {
		this.grupoDestinatarios = grupoDestinatarios;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		validateRequired(grupoDestinatarios, "Grupo de Destinat�rios", erros);
		return erros;
	}

	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	public String getNomeRemetente() {
		return nomeRemetente;
	}

	public void setNomeRemetente(String nomeRemetente) {
		this.nomeRemetente = nomeRemetente;
	}

	public String getEnderecoRemetente() {
		return enderecoRemetente;
	}

	public void setEnderecoRemetente(String enderecoRemetente) {
		this.enderecoRemetente = enderecoRemetente;
	}
	
}
