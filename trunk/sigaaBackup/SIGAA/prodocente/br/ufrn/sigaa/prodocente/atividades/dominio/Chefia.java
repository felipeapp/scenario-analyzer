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
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;


/**
 * 
 * Entidade que registra as informações e tipo de chefia de um Docente, assim como a data final da chefia
 * designida ao Docente e se a chefia é remunerada ou não. 
 *
 * @author eric
 */
@Entity
@Table(name = "chefia",schema="prodocente")
public class Chefia implements Validatable, ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_chefia", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;


    @Column(name = "publicacao")
    private String publicacao;

    @Column(name = "data_documento")
    @Temporal(TemporalType.DATE)
    private Date dataDocumento;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "data_publicacao")
    @Temporal(TemporalType.DATE)
    private Date dataPublicacao;

    @Column(name = "data_final")
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    @Column(name = "remunerado")
    private Boolean remunerado;

    /** No de produção esta diferente de id_autoridade */
    @JoinColumn(name = "autoridade", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor autoridade;

    @JoinColumn(name = "id_tipo_chefia", referencedColumnName = "id_tipo_chefia")
    @ManyToOne
    private TipoChefia tipoChefia;

    /** Creates a new instance of Chefia */
    public Chefia() {
    }

    /**
     * Creates a new instance of Chefia with the specified values.
     * @param id the id of the Chefia
     */
    public Chefia(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this Chefia.
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this Chefia to the specified value.
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
	public Boolean getAtivo() {	return ativo; }

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)	{ this.ativo = ativo; }


    /**
     * Gets the publicacao of this Chefia.
     * @return the publicacao
     */
    public String getPublicacao() {
        return publicacao;
    }

    /**
     * Sets the publicacao of this Chefia to the specified value.
     * @param publicacao the new publicacao
     */
    public void setPublicacao(String publicacao) {
        this.publicacao = publicacao;
    }

    /**
     * Gets the dataDocumento of this Chefia.
     * @return the dataDocumento
     */
    public Date getDataDocumento() {
        return dataDocumento;
    }

    /**
     * Sets the dataDocumento of this Chefia to the specified value.
     * @param dataDocumento the new dataDocumento
     */
    public void setDataDocumento(Date dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    /**
     * Gets the servidor of this Chefia.
     * @return the servidor
     */
    public Servidor getServidor() {
        return servidor;
    }

    /**
     * Sets the servidor of this Chefia to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the dataPublicacao of this Chefia.
     * @return the dataPublicacao
     */
    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    /**
     * Sets the dataPublicacao of this Chefia to the specified value.
     * @param dataPublicacao the new dataPublicacao
     */
    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    /**
     * Gets the dataFinal of this Chefia.
     * @return the dataFinal
     */
    public Date getDataFinal() {
        return dataFinal;
    }

    /**
     * Sets the dataFinal of this Chefia to the specified value.
     * @param dataFinal the new dataFinal
     */
    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    /**
     * Gets the remunerado of this Chefia.
     * @return the remunerado
     */
    public Boolean getRemunerado() {
        return remunerado;
    }

    /**
     * Sets the remunerado of this Chefia to the specified value.
     * @param remunerado the new remunerado
     */
    public void setRemunerado(Boolean remunerado) {
        this.remunerado = remunerado;
    }

    /**
     * Gets the autoridade of this Chefia.
     * @return the autoridade
     */
    public Servidor getAutoridade() {
        return autoridade;
    }

    /**
     * Sets the autoridade of this Chefia to the specified value.
     * @param autoridade the new autoridade
     */
    public void setAutoridade(Servidor autoridade) {
        this.autoridade = autoridade;
    }

    /**
     * Gets the idTipoChefia of this Chefia.
     * @return the idTipoChefia
     */
    public TipoChefia getTipoChefia() {
        return tipoChefia;
    }

    /**
     * Sets the idTipoChefia of this Chefia to the specified value.
     * @param idTipoChefia the new idTipoChefia
     */
    public void setTipoChefia(TipoChefia idTipoChefia) {
        tipoChefia = idTipoChefia;
    }



    /**
     * Determines whether another object is equal to this Chefia.  The result is
     * <code>true</code> if and only if the argument is not null and is a Chefia object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Chefia)) {
            return false;
        }
        Chefia other = (Chefia)object;
        if (id != other.id && (id == 0 || id != other.id)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Chefia[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(getServidor().getId(), "Docente", lista);
		ValidatorUtil.validateRequired(publicacao, "Publicação", lista);
		ValidatorUtil.validaData(getDataDocumento(), "Data Documento", lista);
		ValidatorUtil.validaData(getDataPublicacao(), "Data Publicação", lista);
		ValidatorUtil.validateMinValue(getDataFinal(), getDataDocumento(), "Data Fim da Validade", lista);
		ValidatorUtil.validateRequiredId(getTipoChefia().getId(), "Tipo Chefia", lista);

		return lista;
	}

	public String getItemView() {
		return "  <td>"+getTipoChefia().getDescricao()+ "</td>";

	}

	public String getTituloView() {
		return "    <td>Atividade</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("dataPublicacao", null);
		itens.put("dataFinal", null);
		itens.put("tipoChefia.descricao", "descricaoChefia");
		return itens;
	}

	@Transient
	public void setDescricaoChefia(String descricao) {
		if (tipoChefia == null) {
			setTipoChefia(new TipoChefia());
		}
		getTipoChefia().setDescricao(descricao);
	}

	public float getQtdBase() {
		return CalendarUtils.calculoMeses(dataPublicacao, dataFinal);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}