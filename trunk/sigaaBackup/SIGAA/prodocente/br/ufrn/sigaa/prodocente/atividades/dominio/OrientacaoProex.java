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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que representa as informações de OrientacaoProex
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "orientacao_proex",schema="prodocente")
public class OrientacaoProex implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_orientacao_proex", nullable = false)
    private int id;
    
	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "alunos")
    private Integer alunos;

    @Column(name = "data_inicio")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @Column(name = "data_final")
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    @Column(name = "nome_aluno")
    private String nomeAluno;

    @Column(name = "instituicao")
    private String instituicao;
    
    @Column(name="orientacao")
    private String Orientacao;

    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento;

    @Column(name = "financiamento")
    private String financiamento;

    /** Creates a new instance of OrientacaoProex */
    public OrientacaoProex() {
    }

    /**
     * Creates a new instance of OrientacaoProex with the specified values.
     * @param id the id of the OrientacaoProex
     */
    public OrientacaoProex(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this OrientacaoProex.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this OrientacaoProex to the specified value.
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
     * Gets the servidor of this OrientacaoProex.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this OrientacaoProex to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the titulo of this OrientacaoProex.
     * @return the titulo
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Sets the titulo of this OrientacaoProex to the specified value.
     * @param titulo the new titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Gets the alunos of this OrientacaoProex.
     * @return the alunos
     */
    public Integer getAlunos() {
        return this.alunos;
    }

    /**
     * Sets the alunos of this OrientacaoProex to the specified value.
     * @param alunos the new alunos
     */
    public void setAlunos(Integer alunos) {
        this.alunos = alunos;
    }

    /**
     * Gets the dataInicio of this OrientacaoProex.
     * @return the dataInicio
     */
    public Date getDataInicio() {
        return this.dataInicio;
    }

    /**
     * Sets the dataInicio of this OrientacaoProex to the specified value.
     * @param dataInicio the new dataInicio
     */
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * Gets the dataFinal of this OrientacaoProex.
     * @return the dataFinal
     */
    public Date getDataFinal() {
        return this.dataFinal;
    }

    /**
     * Sets the dataFinal of this OrientacaoProex to the specified value.
     * @param dataFinal the new dataFinal
     */
    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    /**
     * Gets the nomeAluno of this OrientacaoProex.
     * @return the nomeAluno
     */
    public String getNomeAluno() {
        return this.nomeAluno;
    }

    /**
     * Sets the nomeAluno of this OrientacaoProex to the specified value.
     * @param nomeAluno the new nomeAluno
     */
    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }

    /**
     * Gets the instituicao of this OrientacaoProex.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this OrientacaoProex to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getOrientacao() { 
    	return this.Orientacao; 
    }
    
    public void setOrientacao(String Orientacao) { 
    	this.Orientacao = Orientacao; 
    }
    
    /**
     * Gets the departamento of this OrientacaoProex.
     * @return the departamento
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the departamento of this OrientacaoProex to the specified value.
     * @param departamento the new departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /**
     * Gets the financiamento of this OrientacaoProex.
     * @return the financiamento
     */
    public String getFinanciamento() {
        return this.financiamento;
    }

    /**
     * Sets the financiamento of this OrientacaoProex to the specified value.
     * @param financiamento the new financiamento
     */
    public void setFinanciamento(String financiamento) {
        this.financiamento = financiamento;
    }

    /**
     * Determines whether another object is equal to this OrientacaoProex.  The result is
     * <code>true</code> if and only if the argument is not null and is a OrientacaoProex object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof OrientacaoProex)) {
            return false;
        }
        OrientacaoProex other = (OrientacaoProex)object;
        if (this.id != other.id && (this.id == 0)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.OrientacaoProex[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getServidor(), "Docente", lista);
		ValidatorUtil.validateRequired(getTitulo(), "Título", lista);
		ValidatorUtil.validateRequired(getAlunos(), "Quantidade de Alunos", lista);
		ValidatorUtil.validateRequired(getDataInicio(), "Data de Início", lista);
		ValidatorUtil.validateRequired(getDataFinal(), "Data do Fim", lista);
		ValidatorUtil.validateRequired(getNomeAluno(), "Nome do Aluno", lista);
		ValidatorUtil.validateRequired(getInstituicao(), "Instituição", lista);
		ValidatorUtil.validateRequired(getDepartamento(), "Departamento", lista);
		ValidatorUtil.validateRequired(getFinanciamento(), "Financiamento", lista);
		return lista;
	}

	public String getItemView() {
		return "  <td>" + getNomeAluno() + "</td>" +
			   "  <td style=\"text-align:center\">" + Formatador.getInstance().formatarData(dataInicio) + " - "
			   + Formatador.getInstance().formatarData(dataFinal)+"</td>";
	}

	public String getTituloView() {
		return  "    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Período</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("nomeAluno", null);
		itens.put("dataInicio", null);
		itens.put("dataFinal", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}
}