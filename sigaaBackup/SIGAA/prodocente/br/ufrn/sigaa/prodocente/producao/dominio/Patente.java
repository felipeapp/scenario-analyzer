/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que representa os registros de patentes efetuados por docentes da instituição
 *
 * @author Gleydson
 */
@Entity
@Table(name = "patente", schema="prodocente")
@PrimaryKeyJoinColumn(name = "id_patente")
public class Patente extends ProducaoTecnologica implements ViewAtividadeBuilder {


	/** Representa o Status da patente */
	public final static int EM_REGISTRO = 1;
	/** Representa o Status da patente */
	public final static int CONCEDIDO = 2;


	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="prodocente.patente_instituicao_patrocinadora", joinColumns = @JoinColumn(name="id_patente"), inverseJoinColumns = @JoinColumn(name="id_instituicao_patrocinadora"))
	private List<InstituicoesEnsino> patrocinadora = new ArrayList<InstituicoesEnsino>();


	@Column(name = "numero_patente")
	private String numeroPatente;

	@Column(name = "registro_titulo")
	private String registroTitulo;

	@Column(name = "registro_local")
	private String registroLocal;

	@Column(name = "registro_data")
	@Temporal(TemporalType.DATE)
	private Date registroData;

	@Column(name = "registro_pagina")
	private Integer registroPagina;

	@Column(name = "registro_volume")
	private Integer registroVolume;

	@Deprecated
	@Column(name = "registro_numero")
	private Long registroNumero;

	@Column(name = "numero_registro")
	private String numeroRegistro;
	
	/** * Representa a fase que o processo de registro de patente está, se ainda está em registro (1) ou se ja foi concedida (2) */
	@Column(name = "status")
	private Integer status;


	/** Creates a new instance of Patente */
	public Patente() {
	}

	public String getNumeroPatente() {
		return numeroPatente;
	}

	public void setNumeroPatente(String numeroPatente) {
		this.numeroPatente = numeroPatente;
	}

	public List<InstituicoesEnsino> getPatrocinadora() {
		return patrocinadora;
	}

	public void setPatrocinadora(List<InstituicoesEnsino> patrocinadora) {
		this.patrocinadora = patrocinadora;
	}

	public Date getRegistroData() {
		return registroData;
	}

	public void setRegistroData(Date registroData) {
		setAnoReferencia(CalendarUtils.getAno(registroData));
		this.registroData = registroData;
	}

	public String getRegistroLocal() {
		return registroLocal;
	}

	public void setRegistroLocal(String registroLocal) {
		this.registroLocal = registroLocal;
	}

	public Long getRegistroNumero() {
		return registroNumero;
	}

	public void setRegistroNumero(Long registroNumero) {
		this.registroNumero = registroNumero;
	}

	public Integer getRegistroPagina() {
		return registroPagina;
	}

	public void setRegistroPagina(Integer registroPagina) {
		this.registroPagina = registroPagina;
	}

	public String getRegistroTitulo() {
		return registroTitulo;
	}

	public void setRegistroTitulo(String registroTitulo) {
		this.registroTitulo = registroTitulo;
	}

	public Integer getRegistroVolume() {
		return registroVolume;
	}

	public void setRegistroVolume(Integer registroVolume) {
		this.registroVolume = registroVolume;
	}

    /** *Representa a fase que o processo de registro de patente está, se ainda está em registro (1) ou se ja foi concedida (2)*/
	public Integer getStatus() { return status; }
    /** *Representa a fase que o processo de registro de patente está, se ainda está em registro (1) ou se ja foi concedida (2)*/
	public void setStatus(Integer status) { 
		this.status = status; 
	}


	/*
	 * Campos Obrigatorios: Titulo, Numero da Patente, Participacao, Titulo da Publicacao,
	 * 						Local, Data, Instituicao Patrocinadora
	 */

	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getTitulo(), "Título ", lista);
		ValidatorUtil.validateRequired(getRegistroVolume(), "Volume", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getPatrocinadora(), "Patrocinadora", lista);
		ValidatorUtil.validateRequired(getStatus(), "Status", lista);
		ValidatorUtil.validateRequired(getRegistroData(), "Data do Registro", lista);

		if ( ValidatorUtil.isNotEmpty(status) && status == 1 )
			ValidatorUtil.validateRequired(getNumeroRegistro(), "Número de Registro", lista);
		else
			ValidatorUtil.validateRequired(getNumeroPatente(),"Número da Patente", lista);
		
		lista.addAll(super.validate().getMensagens());

		return lista;
	}

	public String getItemView() {
		return "  <td>"+getTitulo()+ "</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(registroData)+"</td>";

	}

	public String getTituloView() {
		return "    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Data</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("registroData", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}

	public String getNumeroRegistro() {
		return numeroRegistro;
	}

	public void setNumeroRegistro(String numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}

}