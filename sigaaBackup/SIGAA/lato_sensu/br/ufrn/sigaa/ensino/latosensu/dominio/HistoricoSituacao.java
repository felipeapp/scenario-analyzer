/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Entidade que armazena o histórico de modificação da situação de uma proposta
 * de curso Lato
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "historico_situacao_proposta", schema="lato_sensu")
public class HistoricoSituacao implements PersistDB{

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_historico_situacao")
	private int id;

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao")
	private SituacaoProposta situacao;

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_proposta")
	private PropostaCursoLato proposta;

	private String observacoes;

	@ManyToOne
	@JoinColumn(name = "id_usuario")
	@CriadoPor
	private Usuario usuario;

	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public SituacaoProposta getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoProposta situacao) {
		this.situacao = situacao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public PropostaCursoLato getProposta() {
		return proposta;
	}

	public void setProposta(PropostaCursoLato proposta) {
		this.proposta = proposta;
	}

}
