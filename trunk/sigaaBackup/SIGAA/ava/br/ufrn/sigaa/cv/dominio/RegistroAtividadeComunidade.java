/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Registra as novidades que acontecem na turma para aparecer no log
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "registro_atividade", schema = "cv")
public class RegistroAtividadeComunidade implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_registro", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	private String descricao;

	private Date data;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_comunidade")
	private ComunidadeVirtual comunidade;

	public RegistroAtividadeComunidade() {
		
	}
	
	public RegistroAtividadeComunidade(int id, String descricao, Date data) {
		this.id = id;
		this.descricao = descricao;
		this.data = data;
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

}
