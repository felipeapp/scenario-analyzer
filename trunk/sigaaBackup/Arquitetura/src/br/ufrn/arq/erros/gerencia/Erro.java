/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/12/2008
 */
package br.ufrn.arq.erros.gerencia;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;

/**
 * Esta classe indica a ocorrência de um erro. Note que o mesmo erro pode
 * acontecer várias vezes, no entanto, só é registrado uma vez mudança de versão
 * do servidor de produção.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 * 
 */
@Entity @Table(name="erro", schema="infra")
public class Erro implements PersistDB {

	@Id
	@Column(name = "id_erro")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	@Column(name = "trace_completo")
	private String traceCompleto;

	/** trace principal que gerou */
	@Column(name = "trace_gerador")
	private String traceGerador;

	/** exceção geradora */
	private String excecao;

	private Date data;

	@ManyToOne
	@JoinColumn(name = "id_sistema")
	private Sistema sistema = new Sistema();

	@ManyToOne
	@JoinColumn(name = "id_sub_sistema")
	private SubSistema subSistema = new SubSistema();

	@Column(name = "id_deploy_producao")
	private Integer deployProducao;

	public Erro(int idErro) {
		setId(idErro);
	}

	public Erro() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTraceCompleto() {
		return traceCompleto;
	}

	public void setTraceCompleto(String traceCompleto) {
		this.traceCompleto = traceCompleto;
	}

	public String getTraceGerador() {
		return traceGerador;
	}

	public void setTraceGerador(String traceGerador) {
		this.traceGerador = traceGerador;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public SubSistema getSubSistema() {
		return subSistema;
	}

	public void setSubSistema(SubSistema subSistema) {
		this.subSistema = subSistema;
	}

	public Integer getDeployProducao() {
		return deployProducao;
	}

	public void setDeployProducao(Integer deployProducao) {
		this.deployProducao = deployProducao;
	}

	public String getExcecao() {
		return excecao;
	}

	public void setExcecao(String excecao) {
		this.excecao = excecao;
	}

}