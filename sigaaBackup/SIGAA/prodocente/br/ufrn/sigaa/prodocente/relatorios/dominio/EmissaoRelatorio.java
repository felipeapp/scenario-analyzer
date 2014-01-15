/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
 * <p>
 * Entidade que armazena os itens e pontuações obtidas por um docente 
 * para uma determinada emissão de relatório de produção intelectual.
 * </p>
 * 
 * <p>
 * Esta emissão faz parte de um conjunto de outras emissões de docentes que serão
 * utilizadas em algum processo classificatório.
 * </p>
 * 
 * @author eric
 */
@Entity
@Table(name = "emissao_relatorio",schema="prodocente")
public class EmissaoRelatorio implements Validatable {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_emissao_relatorio", nullable = false)
    private int id;

    @Column(name = "ipi")
    private Double ipi;
    
    /** Agora o IPI pode ser alterado sem necessariamente fazer todo o cálculo novamente, o usuário com a permissão vai lá e simplesmente altera, isso gera uma inconsistência no banco, para evitar isso é importante guarda o IPI original. é o que essa coluna faz. @author Edson Anibal (ambar@info.ufrn.br) */
    @Column(name="ipi_original")
    private Double ipiOriginal;

    /** Guarda os motivos pelo qual o usuário alterou o IPI do outro, isso é importante pois uma alteração desse tipo requer uma explicação @author Edson Anibal (ambar@info.ufrn.br) */
    @Column(name="motivo_alteracao_ipi")
    private String motivoAlteracaoIPI;

    /** Fator de Produtividade em Pesquisa Individual (Normalização do IPI de um servidor em relação ao IPI médio de seu centro) */
    @Column(name = "fppi")
    private Double fppi;
    
    @JoinColumn(name = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @JoinColumn(name = "id_classificacao_relatorio")
    @ManyToOne
    private ClassificacaoRelatorio classificacaoRelatorio;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emissaoRelatorio")
    private Collection<EmissaoRelatorioItem> emissaoRelatorioItemCollection = new ArrayList<EmissaoRelatorioItem>();

    /** Creates a new instance of EmissaoRelatorio */
    public EmissaoRelatorio() {
    }

    /**
     * Creates a new instance of EmissaoRelatorio with the specified values.
     * @param id the id of the EmissaoRelatorio
     */
    public EmissaoRelatorio(int idEmissaoRelatorio) {
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
    public Double getIpi() {
        return this.ipi;
    }

    /**
     * Sets the ipi of this EmissaoRelatorio to the specified value.
     * @param ipi the new ipi
     */
    public void setIpi(Double ipi) {
        this.ipi = ipi;
    }

    /**
     * Gets the emissaoRelatorioItemCollection of this EmissaoRelatorio.
     * @return the emissaoRelatorioItemCollection
     */
    public Collection<EmissaoRelatorioItem> getEmissaoRelatorioItemCollection() {
        return this.emissaoRelatorioItemCollection;
    }

    /**
     * Sets the emissaoRelatorioItemCollection of this EmissaoRelatorio to the specified value.
     * @param emissaoRelatorioItemCollection the new emissaoRelatorioItemCollection
     */
    public void setEmissaoRelatorioItemCollection(Collection<EmissaoRelatorioItem> emissaoRelatorioItemCollection) {
        this.emissaoRelatorioItemCollection = emissaoRelatorioItemCollection;
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
        if (!(object instanceof EmissaoRelatorio)) {
            return false;
        }
        EmissaoRelatorio other = (EmissaoRelatorio)object;
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
	public Servidor getServidor() {
		return servidor;
	}

	/**
	 * @param servidor the servidor to set
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/**
	 * @return the fppi
	 */
	public Double getFppi() {
		return fppi;
	}

	/**
	 * @param fppi the fppi to set
	 */
	public void setFppi(Double fppi) {
		this.fppi = fppi;
	}

	public Double getIpiOriginal() {
		return ipiOriginal;
	}

	public void setIpiOriginal(Double ipiOriginal) {
		this.ipiOriginal = ipiOriginal;
	}

	public String getMotivoAlteracaoIPI() {
		return motivoAlteracaoIPI;
	}

	public void setMotivoAlteracaoIPI(String motivoAlteracaoIPI) {
		this.motivoAlteracaoIPI = motivoAlteracaoIPI;
	}

}
