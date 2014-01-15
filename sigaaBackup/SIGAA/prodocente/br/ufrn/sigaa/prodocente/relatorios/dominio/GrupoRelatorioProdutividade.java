/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Grupo de itens de um relatórios de produtividade, agrupando informações relacionadas
 * dentro de um contexto.
 *
 * @author eric
 */
@Entity
@Table(name = "grupo_relatorio_produtividade", schema="prodocente")
public class GrupoRelatorioProdutividade implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_grupo_relatorio_produtividade", nullable = false)
    private int id;

    @Column(name = "pontuacao_maxima")
    private int pontuacaoMaxima;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "numero_topico")
    private int numeroTopico;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "grupoRelatorioProdutividade")
    @OrderBy( value="indiceTopico asc"  )
    private List<GrupoItem> grupoItemCollection = new ArrayList<GrupoItem>();

    @JoinColumn(name = "id_relatorio_produtividade", referencedColumnName = "id_relatorio_produtividade")
    @ManyToOne
    private RelatorioProdutividade relatorioProdutividade;

    /** Creates a new instance of GrupoRelatorioProdutividade */
    public GrupoRelatorioProdutividade() {
    }

    /**
     * Creates a new instance of GrupoRelatorioProdutividade with the specified values.
     * @param id the id of the GrupoRelatorioProdutividade
     */
    public GrupoRelatorioProdutividade(int idGrupoRelatorioProdutividade) {
        this.id = idGrupoRelatorioProdutividade;
    }

    /**
     * Gets the id of this GrupoRelatorioProdutividade.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this GrupoRelatorioProdutividade to the specified value.
     * @param id the new id
     */
    public void setId(int idGrupoRelatorioProdutividade) {
        this.id = idGrupoRelatorioProdutividade;
    }

    /**
     * Gets the pontuacaoMaxima of this GrupoRelatorioProdutividade.
     * @return the pontuacaoMaxima
     */
    public int getPontuacaoMaxima() {
        return this.pontuacaoMaxima;
    }

    /**
     * Sets the pontuacaoMaxima of this GrupoRelatorioProdutividade to the specified value.
     * @param pontuacaoMaxima the new pontuacaoMaxima
     */
    public void setPontuacaoMaxima(int pontuacaoMaxima) {
        this.pontuacaoMaxima = pontuacaoMaxima;
    }

    /**
     * Gets the titulo of this GrupoRelatorioProdutividade.
     * @return the titulo
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Sets the titulo of this GrupoRelatorioProdutividade to the specified value.
     * @param titulo the new titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Gets the numeroTopico of this GrupoRelatorioProdutividade.
     * @return the numeroTopico
     */
    public int getNumeroTopico() {
        return this.numeroTopico;
    }

    /**
     * Sets the numeroTopico of this GrupoRelatorioProdutividade to the specified value.
     * @param numeroTopico the new numeroTopico
     */
    public void setNumeroTopico(int numeroTopico) {
        this.numeroTopico = numeroTopico;
    }

    /**
     * Gets the grupoItemCollection of this GrupoRelatorioProdutividade.
     * @return the grupoItemCollection
     */
    public List<GrupoItem> getGrupoItemCollection() {
        return this.grupoItemCollection;
    }

    /**
     * Sets the grupoItemCollection of this GrupoRelatorioProdutividade to the specified value.
     * @param grupoItemCollection the new grupoItemCollection
     */
    public void setGrupoItemCollection(List<GrupoItem> grupoItemCollection) {
        this.grupoItemCollection = grupoItemCollection;
    }

    /**
     * Gets the relatorioProdutividade of this GrupoRelatorioProdutividade.
     * @return the relatorioProdutividade
     */
    public RelatorioProdutividade getRelatorioProdutividade() {
        return this.relatorioProdutividade;
    }

    /**
     * Sets the relatorioProdutividade of this GrupoRelatorioProdutividade to the specified value.
     * @param relatorioProdutividade the new relatorioProdutividade
     */
    public void setRelatorioProdutividade(RelatorioProdutividade idRelatorioProdutividade) {
        this.relatorioProdutividade = idRelatorioProdutividade;
    }

    /**
     * Buscar o total de pontos do grupo, obedecendo o limite estabelecido
     *
     * @return
     */
    public float getTotalPontos() {
		float total = 0;
    	for ( GrupoItem grupo : grupoItemCollection ) {
    		total += grupo.getTotalPontos();
    	}
		if ( pontuacaoMaxima > 0 && total > pontuacaoMaxima ) {
			return pontuacaoMaxima;
		} else {
			return total;
		}
    }
    
    /**
     * Buscar o total de pontos do grupo, obedecendo o limite estabelecido
     *
     * @return
     */
    public float getTotalPontosSemLimite() {
		float total = 0;
    	for ( GrupoItem grupo : grupoItemCollection ) {
    		total += grupo.getTotalPontos();
    	}
		return total;
    }

    /**
     * Verificar se existe algum grupo de itens que não esteja vazio
     *
     * @return
     */
    public boolean isVazio() {
    	for ( GrupoItem grupo : grupoItemCollection ) {
    		if ( !grupo.isVazio() ) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * Determines whether another object is equal to this GrupoRelatorioProdutividade.  The result is
     * <code>true</code> if and only if the argument is not null and is a GrupoRelatorioProdutividade object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof GrupoRelatorioProdutividade)) {
            return false;
        }
        GrupoRelatorioProdutividade other = (GrupoRelatorioProdutividade)object;
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
        return "br.ufrn.sigaa.prodocente.relatorio.dominio.GrupoRelatorioProdutividade[id=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

}
