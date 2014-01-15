/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '23/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Entidade que registra a pontuação de um item de um relatório de produtividade
 * calculado para uma determinada emissão.
 *
 * @author eric
 */
@Entity
@Table(name = "emissao_relatorio_item", schema="prodocente")
public class EmissaoRelatorioItem implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_emissao_relatorio_item", nullable = false)
    private int id;

    @Column(name = "pontos")
    private Double pontos;

    @JoinColumn(name = "id_emissao_relatorio")
    @ManyToOne
    private EmissaoRelatorio emissaoRelatorio;

    @JoinColumn(name = "id_grupo_item")
    @ManyToOne
    private GrupoItem grupoItem;

    /** Creates a new instance of EmissaoRelatorioItem */
    public EmissaoRelatorioItem() {
    }

    /**
     * Creates a new instance of EmissaoRelatorioItem with the specified values.
     * @param idEmissaoRelatorioItem the idEmissaoRelatorioItem of the EmissaoRelatorioItem
     */
    public EmissaoRelatorioItem(int idEmissaoRelatorioItem) {
        this.id = idEmissaoRelatorioItem;
    }

    /**
     * Gets the idEmissaoRelatorioItem of this EmissaoRelatorioItem.
     * @return the idEmissaoRelatorioItem
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the idEmissaoRelatorioItem of this EmissaoRelatorioItem to the specified value.
     * @param idEmissaoRelatorioItem the new idEmissaoRelatorioItem
     */
    public void setId(int idEmissaoRelatorioItem) {
        this.id = idEmissaoRelatorioItem;
    }

    /**
     * Gets the pontos of this EmissaoRelatorioItem.
     * @return the pontos
     */
    public Double getPontos() {
        return this.pontos;
    }

    /**
     * Sets the pontos of this EmissaoRelatorioItem to the specified value.
     * @param pontos the new pontos
     */
    public void setPontos(Double pontos) {
        this.pontos = pontos;
    }

    /**
     * Gets the emissaoRelatorio of this EmissaoRelatorioItem.
     * @return the emissaoRelatorio
     */
    public EmissaoRelatorio getEmissaoRelatorio() {
        return this.emissaoRelatorio;
    }

    /**
     * Sets the emissaoRelatorio of this EmissaoRelatorioItem to the specified value.
     * @param emissaoRelatorio the new emissaoRelatorio
     */
    public void setEmissaoRelatorio(EmissaoRelatorio idEmissaoRelatorio) {
        this.emissaoRelatorio = idEmissaoRelatorio;
    }

    /**
     * Gets the grupoItem of this EmissaoRelatorioItem.
     * @return the grupoItem
     */
    public GrupoItem getGrupoItem() {
        return this.grupoItem;
    }

    /**
     * Sets the grupoItem of this EmissaoRelatorioItem to the specified value.
     * @param grupoItem the new grupoItem
     */
    public void setGrupoItem(GrupoItem idGrupoItem) {
        this.grupoItem = idGrupoItem;
    }

    /**
     * Determines whether another object is equal to this EmissaoRelatorioItem.  The result is
     * <code>true</code> if and only if the argument is not null and is a EmissaoRelatorioItem object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EmissaoRelatorioItem)) {
            return false;
        }
        EmissaoRelatorioItem other = (EmissaoRelatorioItem)object;
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
        return "br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorioItem[idEmissaoRelatorioItem=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

}
