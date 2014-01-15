/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/12/2009
 *
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa os conteúdos ministrados/observados dentro de cada área 
 * do formulário de evolução da criança no ensino infantil.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="conteudo", schema="infantil", uniqueConstraints={})
public class Conteudo implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 
	@Column(name = "id_conteudo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	private String descricao;
	
	private int ordem;
	
	private String rotulo;
	
	@ManyToOne
	@JoinColumn(name="id_area")
	private Area area;
	
	@IndexColumn(name = "ordem", base = 1)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "conteudo")
	private List<ObjetivoConteudo> objetivos;
	
	@Transient
	private ConteudoBimestre bimestreAtual;
	
	@Transient
	private ConteudoBimestre[] bimestres = new ConteudoBimestre[4];
	
	public Conteudo() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public List<ObjetivoConteudo> getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(List<ObjetivoConteudo> objetivos) {
		this.objetivos = objetivos;
	}

	public ConteudoBimestre getBimestreAtual() {
		return bimestreAtual;
	}

	public void setBimestreAtual(ConteudoBimestre bimestreAtual) {
		this.bimestreAtual = bimestreAtual;
	}
	
	public String getDescricaoOrdem(){
		return (rotulo != null) ? (rotulo + ". " + descricao) : (ordem + ". " + descricao);
	}

	public ConteudoBimestre[] getBimestres() {
		return bimestres;
	}

	public void setBimestres(ConteudoBimestre[] bimestres) {
		this.bimestres = bimestres;
	}

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }

}
