/*
 * Created on 01/06/2007
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Registra a execu��o da opera��o de cancelamento de bolsas do projeto.
 * </p>
 * <p>
 * Um cancelamento de bolsas de projeto pode ocorrer quando alguma condi��o
 * estabelecida na resolu��o foi violada. Exemplo: O relat�rio de atividades do
 * monitor n�o foi enviado por 3 meses consecutivos ou o projeto ficou, por 3
 * meses consecutivos sem monitor ativo.
 * </p>
 * <p>
 * Cancela bolsas de discentes que n�o enviaram relat�rio do mesInicio at� o
 * mesFim
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "cancelamento_bolsa", schema = "monitoria")
public class CancelamentoBolsa implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_cancelamento_bolsa", unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	@Column(name = "mes_inicio")
	private int mesInicio;

	@Column(name = "mes_fim")
	private int mesFim;

	@Column(name = "ano")
	private Integer ano;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/**
	 * Cancela bolsas de discentes que n�o enviaram relat�rio do mesInicio at� o
	 * mesFim
	 * 
	 * @return
	 */
	public int getMesFim() {
		return mesFim;
	}

	public void setMesFim(int mesFim) {
		this.mesFim = mesFim;
	}

	public int getMesInicio() {
		return mesInicio;
	}

	public void setMesInicio(int mesInicio) {
		this.mesInicio = mesInicio;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}
	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();				
		ValidatorUtil.validateRequired(ano, "Ano Refer�ncia", lista);
		ValidatorUtil.validateRequired(mesInicio, "M�s In�cio", lista);
		ValidatorUtil.validateRequired(mesFim, "M�s In�cio", lista);		
		if(ano!=null) {
			ValidatorUtil.validateMinValue(ano, 1900, "Ano Refer�ncia", lista);
		}
		return lista;
	}

}