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
 * Classe que associa usuários a grupos de destinatários para controlar
 * permissões de envio.
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

	/** Usuário associado ao grupo de destinatários */
	@ManyToOne
	@JoinColumn(name="id_usuario")
	private UsuarioGeral usuario;
	
	/** 
	 * Papel associado ao grupo de destinatários. A permissão pode ser dada
	 * a um usuário ou a um papel.
	 */
	@ManyToOne
	@JoinColumn(name="id_papel")
	private Papel papel;

	/** Grupo de destinatário ao qual pertence o usuário */
	@ManyToOne
	@JoinColumn(name="id_grupo_destinatarios")
	private GrupoDestinatarios grupoDestinatarios;

	/** Nome que será utilizado como nome de remetente para as notificações enviadas por esse usuário. */
	@Column(name="nome_remetente")
	private String nomeRemetente;
	
	/** Endereço de e-mail que será utilizado como e-mail do remetente para as notificações enviadas por esse usuário. */
	@Column(name="endereco_remetente")
	private String enderecoRemetente;
	
	/** Se a associação está ativa */
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
		validateRequired(grupoDestinatarios, "Grupo de Destinatários", erros);
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
