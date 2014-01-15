/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 14/06/2007 
 *
 */
package br.ufrn.sigaa.portal.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Informa��o do docente que s�o exibidas no portal
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "perfil_servidor", schema = "rh")
public class PerfilServidor implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_perfil", nullable = false)
	private int id;

	private String descricao;

	private String formacao;

	private String areas;

	private String sala;

	private String endereco;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;

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

	public ListaMensagens validate() {

		ListaMensagens erros = new ListaMensagens();

		// Validar sala
		if (sala != null) {
			sala = sala.trim();
			if (sala.length() > 10)
				erros.addErro("A sala deve conter no m�ximo 10 caracteres");
		}

		// Validar endere�o
		if (endereco != null) {
			endereco = endereco.trim();
			if (endereco.length() > 200)
				erros
						.addErro("O endere�o deve conter no m�ximo 200 caracteres");
		}

		// Validar descri��o do perfil p�blico
		if (descricao != null) {
			descricao = descricao.trim();
			if (descricao.length() > 500)
				erros
						.addErro("O perfil p�blico deve conter no m�ximo 500 caracteres");
		}

		// Validar forma��o
		if (formacao != null) {
			formacao = formacao.trim();
			if (formacao.length() > 500)
				erros
						.addErro("A forma��o deve conter no m�ximo 500 caracteres");
		}

		// Validar �reas
		if (areas != null) {
			areas = areas.trim();
			if (areas.length() > 300)
				erros
						.addErro("As �reas de interesse devem conter no m�ximo 300 caracteres");
		}

		return erros;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getAreas() {
		return areas;
	}

	public void setAreas(String areas) {
		this.areas = areas;
	}

	public String getFormacao() {
		return formacao;
	}

	public void setFormacao(String formacao) {
		this.formacao = formacao;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

}