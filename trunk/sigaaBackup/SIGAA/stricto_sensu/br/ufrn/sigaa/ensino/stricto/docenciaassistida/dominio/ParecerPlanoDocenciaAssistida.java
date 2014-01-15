/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/11/2011
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Entidade que contém o parecer realizados pelos docentes dos componentes curriculares
 * do Plano de Docência Assistida.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="parecer_plano_docencia_assistida", schema="stricto_sensu")
public class ParecerPlanoDocenciaAssistida implements PersistDB {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_parecer_plano_docencia_assistida")
	private int id;	
	
	/**
	 * Plano de Docência Assistida Vinculado.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_plano_docencia_assistida")
	private PlanoDocenciaAssistida planoDocenciaAssistida;	
	
	/** Observação do parecer */
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
	
}
