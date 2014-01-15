/**
 * 
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;

/**
 * Representa uma área de conhecimento na qual as crianças do ensino infantil 
 * serão observadas e avaliadas dentro de seus diversos conteúdos e objetivos.
 * Um conjunto de áreas compõem um formulário de evolução da criança.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="area", schema="infantil", uniqueConstraints={})
public class Area implements Validatable {

	/** chave primária da área */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 
	@Column(name = "id_area", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descrição da área */
	private String descricao;
	
	/** Ordem da área */
	private int ordem;
	
	/** Formulário para a qual a área irá fazer parte */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_formulario_evolucao_crianca")
	private FormularioEvolucaoCrianca formulario;
	
	/** Bloco presente na área */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_subarea")
	private Area bloco;
	
	/** Forma de avaliação da para área  */
	@Enumerated
	@Column(name="forma_avaliacao")
	private TipoFormaAvaliacao formaAvaliacao;
	
	/** Rótulo da área */
	private String rotulo;
	
	/** Conteúdos da área */
	@IndexColumn(name = "ordem", base = 1)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "area")
	private List<Conteudo> conteudos;
	
	/** Sub-Area da área */
	@IndexColumn(name = "ordem", base = 1)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "bloco")
    private List<Area> subareas;
	
	public Area() {
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

	public FormularioEvolucaoCrianca getFormulario() {
		return formulario;
	}

	public void setFormulario(FormularioEvolucaoCrianca formulario) {
		this.formulario = formulario;
	}

	public Area getBloco() {
		return bloco;
	}

	public void setBloco(Area bloco) {
		this.bloco = bloco;
	}

	public List<Conteudo> getConteudos() {
		return conteudos;
	}

	public void setConteudos(List<Conteudo> conteudos) {
		this.conteudos = conteudos;
	}

	public TipoFormaAvaliacao getFormaAvaliacao() {
		return formaAvaliacao;
	}

	public void setFormaAvaliacao(TipoFormaAvaliacao formaAvaliacao) {
		this.formaAvaliacao = formaAvaliacao;
	}

	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}
	
	public String getDescricaoOrdem(){
		return rotulo != null ? (rotulo +". "+ descricao) : (bloco == null ? Formatador.getInstance().converteParaRomano(ordem) + " - " : ordem +". ") + descricao;
	}

    public List<Area> getSubareas() {
        return subareas;
    }

    public void setSubareas(List<Area> subareas) {
        this.subareas = subareas;
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }
}
