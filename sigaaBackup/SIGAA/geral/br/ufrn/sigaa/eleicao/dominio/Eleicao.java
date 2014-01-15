/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Apr 3, 2007
 *
 */
package br.ufrn.sigaa.eleicao.dominio;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Esta entidade representa uma eleição para diretor de centro
 * @author Victor Hugo
 */
@Entity
@Table(schema="comum", name = "eleicao", uniqueConstraints = {})
public class Eleicao implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_eleicao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** título da eleição, nome apresentado nas listagens e formulários */
	private String titulo;
	
	/** descrição mais detalhada da eleição */
	private String descricao;

	/** data de inicio da eleição */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** data de finalização da eleição */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim")
	private Date dataFim;
	
	@Transient
	private Date horaInicio;
	
	@Transient
	private Date horaFim;
	
	/** centro que está sendo realizada a eleição, se não for nulo apenas discentes deste centro poderão votar */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_centro", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade centro = new Unidade();
	
	/** data de cadastro da eleição */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataCadastro;
	
	/** registro entrada de quem cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;
	
	/** lista de candidatos da eleição */
	@OneToMany(mappedBy = "eleicao")
	private List<Candidato> candidatos;

	/**
	 * Verifica se esta eleição está aberta ou seja, se a data atual é inferior a data de fim
	 * @return true se esta aberta, false caso contrario.
	 */
	public boolean isAberta(){
		Date hoje = new Date();
		hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
		
		int result = hoje.compareTo( dataFim );
		if( result <= 0 )
			return true;
		else 
			return false;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Candidato> getCandidatos() {
		return candidatos;
	}

	public void setCandidatos(List<Candidato> candidatos) {
		this.candidatos = candidatos;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(titulo, "Título", lista);
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
//		Eleição do DCE abrange todos os centros
//		ValidatorUtil.validateRequired(centro, "Centro", lista);
		ValidatorUtil.validateRequired(dataInicio, "Data de Inicio", lista);
		ValidatorUtil.validateRequired(horaInicio, "Hora Inicio", lista);
		ValidatorUtil.validateRequired(dataFim, "Data de Fim", lista);
		ValidatorUtil.validateRequired(horaFim, "Hora Fim", lista);
		
		return lista;
	}

}
