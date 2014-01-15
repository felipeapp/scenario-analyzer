/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/11/2010
 */
package br.ufrn.sigaa.estagio.dominio;

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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que contém as informações referente 
 * a Renovação de estágio.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "renovacao_estagio", schema = "estagio")
public class RenovacaoEstagio implements PersistDB, Validatable {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_renovacao_estagio")
	private int id;		
	
	/** Estágio que será renovado */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_estagiario")	
	private Estagiario estagio;
	
	/** Data final anterior do estágio */
	@Column(name="data_fim_anterior")
	private Date dataFimAnterior;	
	
	/** Data Renovada, novo prazo para o fim do estágio */
	@Column(name="data_renovacao")
	private Date dataRenovacao;	
	
	/** Observação referente a renovação */
	private String observacao;
	
	/** Status atual da Renovação */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status")	
	private StatusRenovacaoEstagio status;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Estagiario getEstagio() {
		return estagio;
	}

	public void setEstagio(Estagiario estagio) {
		this.estagio = estagio;
	}

	public Date getDataFimAnterior() {
		return dataFimAnterior;
	}

	public void setDataFimAnterior(Date dataFimAnterior) {
		this.dataFimAnterior = dataFimAnterior;
	}

	public Date getDataRenovacao() {
		return dataRenovacao;
	}

	public void setDataRenovacao(Date dataRenovacao) {
		this.dataRenovacao = dataRenovacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public StatusRenovacaoEstagio getStatus() {
		return status;
	}

	public void setStatus(StatusRenovacaoEstagio status) {
		this.status = status;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}	
	
	@Override
	public int hashCode() {		
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(dataRenovacao, "Data de Prorrogação", lista);
		
		if (dataFimAnterior != null && dataRenovacao != null 
				&& dataFimAnterior.getTime() >= dataRenovacao.getTime()) 
			lista.addErro("A Data de Prorrogação deve ser maior que a Data de Fim atual do Estágio");
		
		return lista;
	}	
}
