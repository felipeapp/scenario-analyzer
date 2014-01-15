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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Entidade que armazena as informações referentes a orientações de estágio de discentes feitas por um servidor.
 *
 * @author eric
 */
@Entity
@Table(name = "estagio",schema="prodocente")
public class Estagio implements Validatable, ViewAtividadeBuilder {

    /**
     * Atributo que garante a unicidade.
     */
	@Id
    @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
						parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_estagio", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

	/**
     * Atributo que define a área de conhecimento CNPQ associada ao estágio.
     */
    @JoinColumn(name = "id_area", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne(fetch=FetchType.EAGER)
    private AreaConhecimentoCnpq area;

    /**
     * Atributo que define o nome do projeto.
     */
    @Column(name = "nome_projeto", nullable = false)
    private String nomeProjeto;

    /**
     * Atributo que define a sub-área de conhecimento CNPQ associada ao estágio.
     */
    @JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne(fetch=FetchType.EAGER)
    private AreaConhecimentoCnpq subArea;


    /**
     * Atributo que define a instituição. 
     */
    @Column(name = "instituicao")
    private String instituicao;

    /**
     * Atributo que define o nome do discente. 
     */
    @Column(name = "orientando")
    private String orientando;

    /**
     * Atributo que define o discente associado ao estágio.
     */
    @JoinColumn(name="id_orientando",referencedColumnName = "id_discente")
    @ManyToOne(fetch=FetchType.EAGER)
    private Discente aluno;

    /**
     * Atributo que o departamento associado ao estágio
     */
    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
	@ManyToOne(fetch=FetchType.EAGER)
    private Unidade departamento;

    /**
     * Atributo que define a data inicial do estágio. 
     */
    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    /**
     * Atributo que define a data final do estágio. 
     */
    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    /**
     * Atributo que define o orientador do estágio. 
     */
    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
	@ManyToOne(fetch=FetchType.EAGER)
    private Servidor servidor;

    /**
     * Atributo que define uma observação a respeito do estágio. 
     */
    @Column(name = "informacao")
    private String informacao;

    /**
     * Atributo que define a entidade que financiará o estágio. 
     */
    @JoinColumn(name = "id_entidade_financiadora", referencedColumnName = "id_entidade_financiadora")
    @ManyToOne(fetch=FetchType.EAGER)
    private EntidadeFinanciadora entidadeFinanciadora;

    /**
     * Atributo que define o orientandor se for discente externo. 
     */
    @Column(name="discente_externo")
    private Boolean discenteExterno;

    /**
     * Atributo que define a carga horária semanal que o orientando deverá cumprir. 
     */
    @Column(name="ch_docente_semanal")
    private Integer chSemanal;
    
    /**
     * Atributo que define a matrícula do discente no componente. 
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_matricula_componente")
    private MatriculaComponente matricula;

    /** Creates a new instance of Estagio */
    public Estagio() {
    }

    /**
     * Creates a new instance of Estagio with the specified values.
     * @param id the id of the Estagio
     */
    public Estagio(Integer id) {
        this.id = id;
    }


    /**
     * Gets the id of this Estagio.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this Estagio to the specified value.
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

	/**
	 * Ao remover as produções e atividades, as mesmas não serão emovidas da base de dados,
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
     * Gets the area of this Estagio.
     * @return the area
     */
    public AreaConhecimentoCnpq getArea() {
        return this.area;
    }

    /**
     * Sets the area of this Estagio to the specified value.
     * @param area the new area
     */
    public void setArea(AreaConhecimentoCnpq area) {
        this.area = area;
    }

    /**
     * Gets the nomeProjeto of this Estagio.
     * @return the nomeProjeto
     */
    public String getNomeProjeto() {
        return this.nomeProjeto;
    }

    /**
     * Sets the nomeProjeto of this Estagio to the specified value.
     * @param nomeProjeto the new nomeProjeto
     */
    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }


    /**
     * Gets the instituicao of this Estagio.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this Estagio to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    /**
     * Gets the orientando of this Estagio.
     * @return the orientando
     */
    public String getOrientando() {
        return this.orientando;
    }

    /**
     * Sets the orientando of this Estagio to the specified value.
     * @param orientando the new orientando
     */
    public void setOrientando(String orientando) {
        this.orientando = orientando;
    }

    /**
     * Gets the departamento of this Estagio.
     * @return the departamento
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the departamento of this Estagio to the specified value.
     * @param departamento the new departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /**
     * Gets the periodoInicio of this Estagio.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this Estagio to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the periodoFim of this Estagio.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this Estagio to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Gets the servidor of this Estagio.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this Estagio to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the informacao of this Estagio.
     * @return the informacao
     */
    public String getInformacao() {
        return this.informacao;
    }

    /**
     * Sets the informacao of this Estagio to the specified value.
     * @param informacao the new informacao
     */
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    /**
     * Gets the entidadeFinanciadora of this Estagio.
     * @return the entidadeFinanciadora
     */
    public EntidadeFinanciadora getEntidadeFinanciadora() {
        return this.entidadeFinanciadora;
    }

    /**
     * Sets the entidadeFinanciadora of this Estagio to the specified value.
     * @param entidadeFinanciadora the new entidadeFinanciadora
     */
    public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
        this.entidadeFinanciadora = entidadeFinanciadora;
    }



    /**
     * Determines whether another object is equal to this Estagio.  The result is
     * <code>true</code> if and only if the argument is not null and is a Estagio object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Estagio)) {
            return false;
        }
        Estagio other = (Estagio)object;
        if (this.id != other.id && this.id == 0) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Estagio[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		//Só valida se não for discente externo
		if( getDiscenteExterno() == null || !getDiscenteExterno() || (getDiscenteExterno() && getOrientando() ==null) )
			ValidatorUtil.validateRequired(getAluno(), "Orientando", lista);
		if (getDiscenteExterno() != null && getDiscenteExterno()) //se for discente externo só precisa da string do nome
			ValidatorUtil.validateRequired(getOrientando(), "Orientando", lista);

		ValidatorUtil.validateRequired(getNomeProjeto(), "Nome do Projeto", lista);
		if(getNomeProjeto() != null && getNomeProjeto().trim().length() > 200)
			lista.addErro("O nome do projeto deve conter no máximo 200 caracteres.");
		ValidatorUtil.validateRequired(getInstituicao(), "Instituição", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Periodo de Inicio", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getDepartamento(), "Departamento", lista);
		ValidatorUtil.validateRequired(getEntidadeFinanciadora(), "Entidade Financiadora", lista);
		

		return lista;
	}

	public AreaConhecimentoCnpq getSubArea() {
		return subArea;
	}

	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	public String getItemView() {
		return "  <td>"+nomeProjeto+ "</td>" +
			   "  <td>ES</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(periodoInicio) + " - " +Formatador.getInstance().formatarData(periodoFim)+"</td>";

	}

	public String getTituloView() {
		return "    <td>Nome do Projeto</td>" +
			   "    <td>Tipo</td>" +
			   "    <td style=\"text-align:center\">Período</td>";
	}

	/**
	 * Método que retorna uma mapa.
	 */
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("nomeProjeto", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		return itens;
	}

	/**
	 * Método que retorna o discente interno associado ao estágio.
	 * @return ies
	 */
	public Discente getAluno() {
		return aluno;
	}

	/**
	 * Método que popula o discente interno para o estágio.
	 * @param aluno the aluno to set
	 */
	public void setAluno(Discente aluno) {
		this.aluno = aluno;
	}

	public Boolean getDiscenteExterno(){ return this.discenteExterno; }
	public void setDiscenteExterno(Boolean discenteExterno) { this.discenteExterno = discenteExterno; }

	public float getQtdBase() {
		return 1;
	}

	public Integer getChSemanal() {
		return this.chSemanal;
	}

	public void setChSemanal(Integer chSemanal) {
		this.chSemanal = chSemanal;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	/**
	 * Anula os atributos
	 */
	public void anularAtributosTransient(){
		if (getDiscenteExterno() != null && getDiscenteExterno())
			setAluno(null);
	}
	
	/**
	 * Inicializa os atributos 
	 */
	public void iniciarAtributosTransient(){
		
		if (getAluno() == null) {
			setAluno(new Discente());
		}
		if ( getArea() == null ) {
			setArea(new AreaConhecimentoCnpq());
		}
		if ( getSubArea() == null ) {
			setSubArea(new AreaConhecimentoCnpq());
		}
		
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
