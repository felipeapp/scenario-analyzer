/**
 * 
 */
package br.ufrn.comum.dominio.notificacoes;

import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.comum.dominio.Papel;

/**
 * Classe de domínio que define as permissões para envio de notificações 
 * a um grupo de destinatários ou a um determinado papel
 * 
 * @author Ricardo Wendell
 *
 */
public class PermissaoNotificacao implements PersistDB{

	private int id;
	
	// Cada permissao está relacionada a um grupo de destinatários ou a um papel
	private GrupoDestinatarios grupo;
	private Papel papel;
	
	// Dados do registro de cadastro da entidade
	private RegistroEntrada criadoPor;
	private Date criadoEm;
	
	public PermissaoNotificacao() {
		
	}
	
	public PermissaoNotificacao(GrupoDestinatarios grupo) {
		this.grupo = grupo;
	}
	
	public PermissaoNotificacao(Papel papel) {
		this.papel = papel;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public GrupoDestinatarios getGrupo() {
		return this.grupo;
	}

	public void setGrupo(GrupoDestinatarios grupo) {
		this.grupo = grupo;
	}

	public Papel getPapel() {
		return this.papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	public RegistroEntrada getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
}
