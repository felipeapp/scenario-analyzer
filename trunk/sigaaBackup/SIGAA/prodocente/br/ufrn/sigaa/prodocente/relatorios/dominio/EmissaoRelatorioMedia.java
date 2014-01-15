/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/04/2007'
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
import br.ufrn.sigaa.dominio.Unidade;


/**
 * Entidade que armazena as médias de pontuação dos docentes de uma unidade (geralmente Centros)
 * para uma determinada classificação de pontuações de relatórios de produção intelectual.
 * 
 * @author eric
 */
@Entity
@Table(name = "emissao_relatorio_media",schema="prodocente")
public class EmissaoRelatorioMedia implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_emissao_relatorio_media", nullable = false)
    private int id;

    @Column(name = "ipi_medio")
    private Double ipiMedio;
    
    @Column(name = "ipi_desvpad")
    private Double ipiDesvpad;

    @JoinColumn(name = "id_unidade")
    @ManyToOne
    private Unidade unidade;

    @JoinColumn(name = "id_classificacao_relatorio")
    @ManyToOne
    private ClassificacaoRelatorio classificacaoRelatorio;

    /** Creates a new instance of EmissaoRelatorio */
    public EmissaoRelatorioMedia() {
    }

    /**
     * Creates a new instance of EmissaoRelatorio with the specified values.
     * @param id the id of the EmissaoRelatorio
     */
    public EmissaoRelatorioMedia(int idEmissaoRelatorio) {
        this.id = idEmissaoRelatorio;
    }

    /**
     * Gets the id of this EmissaoRelatorio.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this EmissaoRelatorio to the specified value.
     * @param id the new id
     */
    public void setId(int idEmissaoRelatorio) {
        this.id = idEmissaoRelatorio;
    }

    /**
     * Gets the ipi of this EmissaoRelatorio.
     * @return the ipi
     */
    public Double getIpiMedio() {
        return this.ipiMedio;
    }

    /**
     * Sets the ipi of this EmissaoRelatorio to the specified value.
     * @param ipi the new ipi
     */
    public void setIpiMedio(Double ipiMedio) {
        this.ipiMedio = ipiMedio;
    }

    /**
     * Determines whether another object is equal to this EmissaoRelatorio.  The result is
     * <code>true</code> if and only if the argument is not null and is a EmissaoRelatorio object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EmissaoRelatorioMedia)) {
            return false;
        }
        EmissaoRelatorioMedia other = (EmissaoRelatorioMedia)object;
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
        return "br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio[id=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

	/**
	 * @return the classificacaoRelatorio
	 */
	public ClassificacaoRelatorio getClassificacaoRelatorio() {
		return classificacaoRelatorio;
	}

	/**
	 * @param classificacaoRelatorio the classificacaoRelatorio to set
	 */
	public void setClassificacaoRelatorio(
			ClassificacaoRelatorio classificacaoRelatorio) {
		this.classificacaoRelatorio = classificacaoRelatorio;
	}

	/**
	 * @return the servidor
	 */
	public Unidade getUnidade() {
		return unidade;
	}

	/**
	 * @param servidor the servidor to set
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Double getIpiDesvpad() {
		return ipiDesvpad;
	}

	public void setIpiDesvpad(Double ipiDesvpad) {
		this.ipiDesvpad = ipiDesvpad;
	}
}
