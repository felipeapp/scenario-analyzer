/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import java.util.Date;
import java.util.HashMap;

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

import br.ufrn.academico.dominio.ProgramaEducacaoTutorial;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Dados de PET lançados anteriormente no Prodocente e que hoje é
 * em outro cadastro -> Graduação -> CDP
 *
 * @author eric
 */
@Entity
@Table(name = "tutoria_pet",schema="prodocente")
public class TutoriaPet implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tutoria_pet", nullable = false)
    private int id;
    
	@ManyToOne
	@JoinColumn(name="id_pet")
	private ProgramaEducacaoTutorial pet;
    
	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo = true;

    @Column(name = "instituicao")
    private String instituicao;

    @JoinColumn(name = "id_area", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq area;

    @JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq subArea;

    @JoinColumn(name = "id_classificacao_pet", referencedColumnName = "id_classificacao_pet")
    @ManyToOne
    private ClassificacaoPet classificacaoPet;

    @Column(name = "titulo")
    private String titulo;

    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @Column(name = "informacao")
    private String informacao;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    /** Creates a new instance of Pet */
    public TutoriaPet() {
    }

    /**
     * Creates a new instance of Pet with the specified values.
     * @param id the id of the Pet
     */
    public TutoriaPet(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this Pet.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this Pet to the specified value.
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }


	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo() {	return this.ativo; }

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)	{ this.ativo = ativo; }

    
    /**
     * Gets the instituicao of this Pet.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this Pet to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    /**
     * Gets the area of this Pet.
     * @return the area
     */
    public AreaConhecimentoCnpq getArea() {
        return this.area;
    }

    /**
     * Sets the area of this Pet to the specified value.
     * @param area the new area
     */
    public void setArea(AreaConhecimentoCnpq area) {
        this.area = area;
    }

    /**
     * Gets the subArea of this Pet.
     * @return the subArea
     */
    public AreaConhecimentoCnpq getSubArea() {
        return this.subArea;
    }

    /**
     * Sets the subArea of this Pet to the specified value.
     * @param subArea the new subArea
     */
    public void setSubArea(AreaConhecimentoCnpq subArea) {
        this.subArea = subArea;
    }

    /**
     * Gets the classificacao of this Pet.
     * @return the classificacao
     */
    public ClassificacaoPet getClassificacaoPet() {
        return this.classificacaoPet;
    }

    /**
     * Sets the classificacao of this Pet to the specified value.
     * @param classificacao the new classificacao
     */
    public void setClassificacaoPet(ClassificacaoPet classificacao) {
        this.classificacaoPet = classificacao;
    }

    /**
     * Gets the titulo of this Pet.
     * @return the titulo
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Sets the titulo of this Pet to the specified value.
     * @param titulo the new titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Gets the departamento of this Pet.
     * @return the departamento
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the departamento of this Pet to the specified value.
     * @param departamento the new departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /**
     * Gets the periodoInicio of this Pet.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this Pet to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the periodoFim of this Pet.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this Pet to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Gets the informacao of this Pet.
     * @return the informacao
     */
    public String getInformacao() {
        return this.informacao;
    }

    /**
     * Sets the informacao of this Pet to the specified value.
     * @param informacao the new informacao
     */
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    /**
     * Gets the servidor of this Pet.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this Pet to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }
    
    public ProgramaEducacaoTutorial getPet() {
		return pet;
	}

	public void setPet(ProgramaEducacaoTutorial pet) {
		this.pet = pet;
	}

	/**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Determines whether another object is equal to this Pet.  The result is
     * <code>true</code> if and only if the argument is not null and is a Pet object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TutoriaPet)) {
            return false;
        }
        TutoriaPet other = (TutoriaPet)object;
        if (this.id != other.id) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Pet[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(getPet().getId(), "Grupo PET", lista.getMensagens());
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Data de Início", lista.getMensagens());
		ValidatorUtil.validateRequired(getPeriodoFim(), "Data Final", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getServidor().getId(), "Docente", lista.getMensagens());
		return lista;
	}
	public String getItemView() {
		return "<td>" + getTitulo() + "</td><td style=\"text-align:center\">" + Formatador.getInstance().formatarData(periodoInicio) + "-" +
		Formatador.getInstance().formatarData(periodoFim) + "</td>";
	}

	public String getTituloView() {
		return "<td>Atividade</td><td style=\"text-align:center\">Período</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}
}
