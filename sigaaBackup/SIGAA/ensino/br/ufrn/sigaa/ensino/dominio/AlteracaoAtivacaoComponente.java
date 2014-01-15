/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 04/05/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * Registra a alteração no status de ativo e o motivo de inativação de um componente
 * curricular.
 *
 * @author Andre M Dantas
 *
 */
@Entity
@Table(name = "alteracao_ativacao_componente", schema = "ensino")
public class AlteracaoAtivacaoComponente implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_alteracao_ativacao", nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	@ManyToOne
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricular componenteCurricular;

	private int statusInativoAntes;

	private int statusInativoDepois;

	private boolean ativoAntes;

	private boolean ativoDepois;

	private String observacao;

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public AlteracaoAtivacaoComponente() {
	}

	public AlteracaoAtivacaoComponente(ComponenteCurricular cc, RegistroEntrada re, Date d) {
		componenteCurricular = cc;
		registroEntrada = re;
		data = new Date();
	}

	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	public void setComponenteCurricular(ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAntesDepois(ComponenteCurricular antes, ComponenteCurricular depois) {
		statusInativoAntes = antes.getStatusInativo();
		ativoAntes = antes.isAtivo();
		statusInativoDepois = depois.getStatusInativo();
		ativoDepois = depois.isAtivo();
	}

	public boolean isAtivoAntes() {
		return ativoAntes;
	}

	public void setAtivoAntes(boolean ativoAntes) {
		this.ativoAntes = ativoAntes;
	}

	public boolean isAtivoDepois() {
		return ativoDepois;
	}

	public void setAtivoDepois(boolean ativoDepois) {
		this.ativoDepois = ativoDepois;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public int getStatusInativoAntes() {
		return statusInativoAntes;
	}

	public void setStatusInativoAntes(int statusInativoAntes) {
		this.statusInativoAntes = statusInativoAntes;
	}

	public int getStatusInativoDepois() {
		return statusInativoDepois;
	}

	public void setStatusInativoDepois(int statusInativoDepois) {
		this.statusInativoDepois = statusInativoDepois;
	}

}
