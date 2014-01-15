/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Unidade;

@Entity
@Table(name = "espaco_fisico", schema = "espaco_fisico")
public class EspacoFisico implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_espaco_fisico", unique = true)
	private int id;

	private String descricao;
	
	@Column(unique = true)
	private String codigo;
	
	private Integer capacidade;
	
	private Double area; // Em metros quadrados

	@ManyToOne
	@JoinColumn(name = "id_tipo_espaco_fisico")
	private TipoEspacoFisico tipo;
	
	@OneToMany(mappedBy = "espacoFisico", cascade = CascadeType.ALL)
	private List<RecursoEspacoFisico> recursos;
	
	@ManyToOne
	@JoinColumn(name = "id_unidade_responsavel")
	private Unidade unidadeResponsavel;

	@ManyToOne
	@JoinColumn(name = "id_unidade_preferencia_reserva")
	private Unidade unidadePreferenciaReserva;

	private boolean ativo = true;
	
	@CriadoEm 
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;
	
	@CriadoPor 
	@ManyToOne
	@JoinColumn(name="id_registro_cadastro")
	private RegistroEntrada registroCadastro;
	
	public EspacoFisico() {

	}
	
	/**
	 * Adiciona um recurso ao espaço
	 * 
	 * @param recurso
	 */
	public void adicionarRecurso(RecursoEspacoFisico recurso) {
		if (recursos == null)
			recursos = new ArrayList<RecursoEspacoFisico>();

		recurso.setEspacoFisico(this);
		recursos.add(recurso);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Integer capacidade) {
		this.capacidade = capacidade;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Unidade getUnidadeResponsavel() {
		return unidadeResponsavel;
	}

	public void setUnidadeResponsavel(Unidade unidadeResponsavel) {
		this.unidadeResponsavel = unidadeResponsavel;
	}

	public Unidade getUnidadePreferenciaReserva() {
		return unidadePreferenciaReserva;
	}

	public void setUnidadePreferenciaReserva(Unidade unidadePreferenciaReserva) {
		this.unidadePreferenciaReserva = unidadePreferenciaReserva;
	}

	public List<RecursoEspacoFisico> getRecursos() {
		return recursos;
	}

	public void setRecursos(List<RecursoEspacoFisico> recursos) {
		this.recursos = recursos;
	}

	public TipoEspacoFisico getTipo() {
		return tipo;
	}

	public void setTipo(TipoEspacoFisico tipo) {
		this.tipo = tipo;
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
		return HashCodeUtil.hashAll(id, descricao);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "codigo", "unidadeResponsavel");
	}

}
