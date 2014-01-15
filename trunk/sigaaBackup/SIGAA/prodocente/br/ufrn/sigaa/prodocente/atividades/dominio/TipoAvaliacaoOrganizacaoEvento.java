/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que representa as informações de Tipo Avaliacao Organizacao Evento
 *
 * @author eric
 */
@Entity
@Table(name = "tipo_avaliacao_organizacao_evento",schema="prodocente")
public class TipoAvaliacaoOrganizacaoEvento implements Validatable {

	public static final int CONSULTOR_AD_HOC_REVISTA_CIENTIFICA = 8;

	public static final int REPRESENTACAO_ORGAO_FOMENTO = 9;

	public static final int CONSULTOR_AD_HOC_ANAIS_EVENTO = 11;

	public static final int CONSULTOR_AD_HOC_ORGAO_FOMENTO = 12;

	public static final int CONSULTOR_AD_HOC_COMISSAO_NACIONAL_REFORMA = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_avaliacao_organizacao_evento", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

    
    @Column(name = "descricao")
    private String descricao;

    /** Creates a new instance of TipoAvaliacaoOrganizacaoEvento */
    public TipoAvaliacaoOrganizacaoEvento() {
    }

    /**
     * Creates a new instance of TipoAvaliacaoOrganizacaoEvento with the specified values.
     * @param id the id of the TipoAvaliacaoOrganizacaoEvento
     */
    public TipoAvaliacaoOrganizacaoEvento(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this TipoAvaliacaoOrganizacaoEvento.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this TipoAvaliacaoOrganizacaoEvento to the specified value.
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
	public void setAtivo(Boolean ativo)	{ 
		this.ativo = ativo; 
	}
    
    /**
     * Gets the descricao of this TipoAvaliacaoOrganizacaoEvento.
     * @return the descricao
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Sets the descricao of this TipoAvaliacaoOrganizacaoEvento to the specified value.
     * @param descricao the new descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Determines whether another object is equal to this TipoAvaliacaoOrganizacaoEvento.  The result is
     * <code>true</code> if and only if the argument is not null and is a TipoAvaliacaoOrganizacaoEvento object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoAvaliacaoOrganizacaoEvento)) {
            return false;
        }
        TipoAvaliacaoOrganizacaoEvento other = (TipoAvaliacaoOrganizacaoEvento)object;
        if (this.id != other.id || this.id == 0) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.TipoAvaliacaoOrganizacaoEvento[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descricao", lista);
		return lista;
	}

}
