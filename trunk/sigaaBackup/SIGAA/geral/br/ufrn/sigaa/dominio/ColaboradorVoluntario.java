/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '08/12/2008'
 *
 */
package br.ufrn.sigaa.dominio;

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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que representa um colaborador voluntário de pesquisa
 * (servidor inativo que pode submeter e coordenar projetos de pesquisa)
 * 
 * @author leonardo
 *
 */
@Entity
@Table(name = "colaborador_voluntario", schema = "rh")
public class ColaboradorVoluntario implements Validatable {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_colaborador_voluntario", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;
	
	
	@ManyToOne
	@JoinColumn(name = "usuario_cadastro")
	@CriadoPor
	private Usuario usuarioCadastro;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	private boolean ativo = true;

	public ColaboradorVoluntario(){
	}
	
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequiredId(servidor.getId(), "Servidor", erros.getMensagens());
		return erros;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
