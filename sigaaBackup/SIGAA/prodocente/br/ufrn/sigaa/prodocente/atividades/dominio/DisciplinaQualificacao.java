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
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Disciplinas cumpridas por um docente em uma qualificação
 *
 * @author eric
 */
@Entity
@Table(name = "disciplina_qualificacao",schema="prodocente")
public class DisciplinaQualificacao implements Validatable, ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_disciplina_qualificacao", nullable = false)
    private int id;
    
	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;


    /** Campo não pertencente ao domínio, utilizado para controle na view */
    @Transient
    private Boolean selecionado;

    @Column(name = "disciplina", nullable = false)
    private String disciplina;

    @Column(name = "conceito")
    private String conceito;

    @JoinColumn(name = "id_qualificacao", referencedColumnName = "id_qualificacao_docente", insertable=true, updatable=false)
    @ManyToOne
    private QualificacaoDocente qualificacaoDocente;

    /** Creates a new instance of DisciplinaQualificacao */
    public DisciplinaQualificacao() {
    }

    /**
     * Creates a new instance of DisciplinaQualificacao with the specified values.
     * @param id the id of the DisciplinaQualificacao
     */
    public DisciplinaQualificacao(Integer id) {
        this.id = id;
    }


    /**
     * Gets the id of this DisciplinaQualificacao.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this DisciplinaQualificacao to the specified value.
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
     * Gets the disciplina of this DisciplinaQualificacao.
     * @return the disciplina
     */
    public String getDisciplina() {
        return this.disciplina;
    }

    /**
     * Sets the disciplina of this DisciplinaQualificacao to the specified value.
     * @param disciplina the new disciplina
     */
    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    /**
     * Gets the conceito of this DisciplinaQualificacao.
     * @return the conceito
     */
    public String getConceito() {
        return this.conceito;
    }

    /**
     * Sets the conceito of this DisciplinaQualificacao to the specified value.
     * @param conceito the new conceito
     */
    public void setConceito(String conceito) {
        this.conceito = conceito;
    }

    /**
     * Gets the qualificacao of this DisciplinaQualificacao.
     * @return the qualificacao
     */
    public QualificacaoDocente getQualificacaoDocente() {
        return this.qualificacaoDocente;
    }

    /**
     * Sets the qualificacao of this DisciplinaQualificacao to the specified value.
     * @param qualificacao the new qualificacao
     */
    public void setQualificacaoDocente(QualificacaoDocente qualificacao) {
        this.qualificacaoDocente = qualificacao;
    }


    /**
     * Determines whether another object is equal to this DisciplinaQualificacao.  The result is
     * <code>true</code> if and only if the argument is not null and is a DisciplinaQualificacao object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DisciplinaQualificacao)) {
            return false;
        }
        DisciplinaQualificacao other = (DisciplinaQualificacao)object;
        if (this.id != other.id && this.id == 0 ) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.DisciplinaQualificacao[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDisciplina(), "Disciplina", lista.getMensagens());
		ValidatorUtil.validateRequired(getConceito(), "Conceito", lista.getMensagens());
		ValidatorUtil.validateRequired(getQualificacaoDocente().getId(), "Qualificação de Docente", lista.getMensagens());

		return lista;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getItemView() {
		return "  <td>"+getDisciplina()+ "</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(getQualificacaoDocente().getDataFinal())+"</td>";
	}

	public String getTituloView() {
		return  "    <td>Nível do Curso</td>" +
				"    <td style=\"text-align:center\">Período</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("disciplina", null);
		itens.put("qualificacaoDocente.dataFinal", "dataFinalQualificacao");
		return itens;
	}

	@Transient
	public void setDataFinalQualificacao(Date dataFinal) {
		if (this.getQualificacaoDocente() == null) {
			this.setQualificacaoDocente(new QualificacaoDocente());
		}
		this.getQualificacaoDocente().setDataFinal(dataFinal);
	}

	public float getQtdBase() {
		return 1;
	}
}
