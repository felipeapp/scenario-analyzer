/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 05/08/2010
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Entidade que cont�m o hist�rico de modifica��o de status realizados no Plano de Doc�ncia Assistida.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="historico_plano_docencia_assistida", schema="stricto_sensu")
public class HistoricoPlanoDocenciaAssistida implements PersistDB {
	
	/**
	 * Chave prim�ria da indica��o.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_historico_plano_docencia_assistida")
	private int id;	
	
	/**
	 * Plano de Doc�ncia Assistida Vinculado.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_plano_docencia_assistida")
	private PlanoDocenciaAssistida planoDocenciaAssistida;	
	
	/**
	 * Status do hist�rico 
	 */
	@Column(name = "status")
	private int status;
	
	/**
	 * Observa��o do hist�rico
	 */
	@Column(name = "observacao")
	private String observacao;	
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data")
	@CriadoEm
	private Date data;

	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PlanoDocenciaAssistida getPlanoDocenciaAssistida() {
		return planoDocenciaAssistida;
	}

	public void setPlanoDocenciaAssistida(
			PlanoDocenciaAssistida planoDocenciaAssistida) {
		this.planoDocenciaAssistida = planoDocenciaAssistida;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	@Transient
	public String getStatusDescricao(){
		return PlanoDocenciaAssistida.getDescricaoStatus(status);
	}
}
