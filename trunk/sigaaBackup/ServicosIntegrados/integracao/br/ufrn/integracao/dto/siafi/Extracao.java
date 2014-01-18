/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 11/06/2013
 * Autor:     Eric Moura
 *
 */
package br.ufrn.integracao.dto.siafi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;

/** DTO para a extracao do dados para documento Habil para o siafi
 * @author Eric Moura
 *
 */
@XStreamAlias("ex:extracao")
public class Extracao {

	@XStreamAlias("ex:dataInicialExtracao")
	@XStreamConverter(value=DateConverter.class)
	private Date dataInicialExtracao;
	
	@XStreamAlias("ex:dataFinalExtracao")
	@XStreamConverter(value=DateConverter.class)
	private Date dataFinalExtracao;
	
	@XStreamAlias("ex:dataExtracao")
	@XStreamConverter(value=DateConverter.class)
	private Date dataExtracao;
	
	@XStreamAlias("ex:qtdRegistros")
	private Integer qtdRegistros;
	
	@XStreamAlias("ex:situacao")
	private String situacao;
	
	@XStreamImplicit(itemFieldName="ns2:CprDhConsultar")
	private List<CprDHCadastrar> documentosHabil = new ArrayList<CprDHCadastrar>();


	public Integer getQtdRegistros() {
		return qtdRegistros;
	}

	public void setQtdRegistros(Integer qtdRegistros) {
		this.qtdRegistros = qtdRegistros;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public List<CprDHCadastrar> getDocumentosHabil() {
		return documentosHabil;
	}

	public void setDocumentosHabil(List<CprDHCadastrar> documentosHabil) {
		this.documentosHabil = documentosHabil;
	}

	public Date getDataInicialExtracao() {
		return dataInicialExtracao;
	}

	public void setDataInicialExtracao(Date dataInicialExtracao) {
		this.dataInicialExtracao = dataInicialExtracao;
	}

	public Date getDataFinalExtracao() {
		return dataFinalExtracao;
	}

	public void setDataFinalExtracao(Date dataFinalExtracao) {
		this.dataFinalExtracao = dataFinalExtracao;
	}

	public Date getDataExtracao() {
		return dataExtracao;
	}

	public void setDataExtracao(Date dataExtracao) {
		this.dataExtracao = dataExtracao;
	}

	
}
