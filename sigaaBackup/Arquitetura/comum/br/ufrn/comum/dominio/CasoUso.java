/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.comum.dominio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.Parametro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.annotations.Required;

/**
 * Representa um caso de uso com os seus relacionamentos
 * nos sistemas.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="caso_uso", schema="comum")
public class CasoUso implements PersistDB {

	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="comum.caso_uso_seq")})
	private int id;
	
	/** Código do caso de uso */
	@Required
	private String codigo;
	
	/** Nome do caso de uso */
	@Required
	private String nome;
	
	/** Papéis utilizados para acessar o caso de uso */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="casos_uso_papeis", schema="comum",
			joinColumns=@JoinColumn(name="id_caso_uso"),
			inverseJoinColumns=@JoinColumn(name="id_papel"))
	private List<Papel> papeis;
	
	/** Papéis utilizados para acessar o caso de uso */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="casos_uso_parametros", schema="comum",
			joinColumns=@JoinColumn(name="id_caso_uso"),
			inverseJoinColumns=@JoinColumn(name="codigo_parametro"))
	private List<Parametro> parametros;
	
	/** Materiais de treinamento existentes para o caso de uso */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="casos_uso_materiais_treinamento", schema="comum",
			joinColumns=@JoinColumn(name="id_caso_uso"),
			inverseJoinColumns=@JoinColumn(name="id_material_treinamento"))
	private List<MaterialTreinamento> materiais;
	
	/** Sub-sistema do qual o caso de uso faz parte. */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_sub_sistema")
	@Required
	private SubSistema subSistema;
	
	/** Sistema do qual o caso de uso faz parte. */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_sistema")
	@Required
	private Sistema sistema;
	
	/** Relacionamentos com outros casos de uso que esse caso de uso possui */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="origem", cascade=CascadeType.ALL)
	private List<RelacionamentoCasoUso> relacionamentos;

	public CasoUso() {

	}

	public CasoUso(int id) {
		this.id = id;
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Papel> getPapeis() {
		return papeis;
	}

	public void setPapeis(List<Papel> papeis) {
		this.papeis = papeis;
	}

	public SubSistema getSubSistema() {
		return subSistema;
	}

	public void setSubSistema(SubSistema subSistema) {
		this.subSistema = subSistema;
	}

	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public List<RelacionamentoCasoUso> getRelacionamentos() {
		return relacionamentos;
	}

	public void setRelacionamentos(List<RelacionamentoCasoUso> relacionamentos) {
		this.relacionamentos = relacionamentos;
	}

	/**
	 * Adiciona o papel passado como parâmetro à lista de papéis
	 * associados ao caso de uso.
	 * @param p
	 */
	public void adicionarPapel(Papel p) {
		if (papeis == null)
			papeis = new ArrayList<Papel>();
		if (!papeis.contains(p))
			papeis.add(p);
	}
	
	/**
	 * Adiciona o parâmetro à lista de parâmetros associados ao caso de uso
	 * @param p
	 */
	public void adicionarParametro(Parametro p) {
		if (parametros == null)
			parametros = new ArrayList<Parametro>();
		if (!parametros.contains(p))
			parametros.add(p);
	}
	
	/**
	 * Adiciona o material de treinamento à lista de materiais associados ao caso de uso
	 * @param p
	 */
	public void adicionarMaterial(MaterialTreinamento m) {
		if (materiais == null)
			materiais = new ArrayList<MaterialTreinamento>();
		if (!materiais.contains(m))
			materiais.add(m);
	}

	/**
	 * Cria um relacionamento deste caso de uso com o caso de uso de destino
	 * passado como parâmetro. 
	 * @param destino
	 * @param tipo
	 */
	public void adicionarRelacionamento(CasoUso destino, TipoRelacionamentoCasoUso tipo) {
		if (relacionamentos == null)
			relacionamentos = new ArrayList<RelacionamentoCasoUso>();
		
		removerRelacionamento(destino);
		
		RelacionamentoCasoUso relacionamento = new RelacionamentoCasoUso();
		relacionamento.setOrigem(this);
		relacionamento.setDestino(destino);
		relacionamento.setTipo(tipo);
		relacionamentos.add(relacionamento);
	}

	/**
	 * Remove o papel passado como parâmetro da lista de papéis associados
	 * ao caso de uso.
	 * @param p
	 */
	public void removerPapel(Papel p) {
		if (papeis != null)
			papeis.remove(p);
	}
	
	/**
	 * Remove o parâmetro da lista de parâmetros associados
	 * ao caso de uso.
	 * @param p
	 */
	public void removerParametro(Parametro p) {
		if (parametros != null)
			parametros.remove(p);
	}
	
	/**
	 * Remove o material de treinamento da lista de materiais associados
	 * ao caso de uso.
	 * @param p
	 */
	public void removerMaterial(MaterialTreinamento p) {
		if (materiais != null)
			materiais.remove(p);
	}

	/**
	 * Remove o relacionamento cujo caso de uso de destino é o passado como parâmetro
	 * da lista de relacionamentos do caso de uso.
	 * @param destino
	 */
	public void removerRelacionamento(CasoUso destino) {
		for (Iterator<RelacionamentoCasoUso> it = relacionamentos.iterator(); it.hasNext(); ) {
			RelacionamentoCasoUso rel = it.next();
			if (rel.getDestino().getId() == destino.getId())
				it.remove();
		}
	}

	public List<Parametro> getParametros() {
		return parametros;
	}

	public void setParametros(List<Parametro> parametros) {
		this.parametros = parametros;
	}

	public List<MaterialTreinamento> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<MaterialTreinamento> materiais) {
		this.materiais = materiais;
	}
	
}
