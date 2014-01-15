/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 03/07/2008
 *
 */	
package br.ufrn.sigaa.assistencia.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade responsável pelos períodos que uma Bolsa
 * pode possuir
 * 
 * @author agostinho
 *
 */

@Entity 
@Table(name="bolsa_auxilio_periodo", schema="sae")
public class BolsaAuxilioPeriodo implements PersistDB {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_bolsa_auxilio_periodo")
	private int id;
	
	/** Ano da solicitação */
	private Integer ano;
	
	/** Período da Solicitação */
	private Integer periodo;
	
	/** Data de Cadastro da solicitação */
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/** Inicio da bolsa, para os bolsistas promisaes */
	@Column(name="inicio_bolsa")
	private Date inicioBolsa;
	
	/** Fim da bolsa, para os bolsistas promisaes */
	@Column(name="fim_bolsa")
	private Date fimBolsa;
	
	/** Referente a solicitação da bolsa auxilio */
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="id_bolsa_auxilio")
	private BolsaAuxilio bolsaAuxilio;

	/** Data da finalização da bolsa */
	@Column(name="data_finalizacao")
	private Date dataFinalizacao;

	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_finalizacao")
	private RegistroEntrada registroFinalizacao;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public BolsaAuxilio getBolsaAuxilio() {
		return bolsaAuxilio;
	}
	public void setBolsaAuxilio(BolsaAuxilio bolsaAuxilio) {
		this.bolsaAuxilio = bolsaAuxilio;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public Integer getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	public Date getInicioBolsa() {
		return inicioBolsa;
	}
	public void setInicioBolsa(Date inicioBolsa) {
		this.inicioBolsa = inicioBolsa;
	}
	public Date getFimBolsa() {
		return fimBolsa;
	}
	public void setFimBolsa(Date fimBolsa) {
		this.fimBolsa = fimBolsa;
	}

	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}
	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}
	public RegistroEntrada getRegistroFinalizacao() {
		return registroFinalizacao;
	}
	public void setRegistroFinalizacao(RegistroEntrada registroFinalizacao) {
		this.registroFinalizacao = registroFinalizacao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
}