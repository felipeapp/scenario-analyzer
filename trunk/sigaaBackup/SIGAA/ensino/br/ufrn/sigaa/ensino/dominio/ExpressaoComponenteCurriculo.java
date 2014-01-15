/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/05/2011
 */
package br.ufrn.sigaa.ensino.dominio;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.annotations.Required;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;

/**
 * Esta entidade funciona de forma semelhante a EquivalenciaEspecifica, porém sendo válido para co-requisitos e pre-requisitos. 
 * Ou seja, configura uma expressão de co-requisito ou pre-requisito de um componente curricular específica para um curriculo. 
 * 
 * @author Victor Hugo
 */
@Entity @Table(name="expressao_componente_curriculo", schema="ensino")
public class ExpressaoComponenteCurriculo implements PersistDB {

	/** identificador */
	@Id @GeneratedValue
	@Column(name="id_expressao_componente_curriculo")
	private int id;
	
	/** Currículo que possui a expressão específica de co-requisito ou pre-requisito */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_curriculo")
	@Required
	private Curriculo curriculo;
	
	/** Componente que possui a expressão específica de co-requisito ou pre-requisito */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_componente_curricular")
	@Required
	private ComponenteCurricular componente;
	
	/** Expressão de prerequisito */
	private String prerequisito;
	
	/** Expressão de corequisito */
	private String corequisito;
	
	/** Indica se a {@link ExpressaoComponenteCurriculo} está ativa */
	@CampoAtivo(true)
	private boolean ativo = true;

	/** Data de cadastro desta expressão */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Curriculo getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	public String getPrerequisito() {
		return prerequisito;
	}

	public void setPrerequisito(String prerequisito) {
		this.prerequisito = prerequisito;
	}

	public String getCorequisito() {
		return corequisito;
	}

	public void setCorequisito(String corequisito) {
		this.corequisito = corequisito;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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

}