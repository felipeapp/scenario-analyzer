/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe que representa um relatório de produtividade.
 * Cada novo relatório é uma instância dessa classe.
 *
 * @author Gleydson, Eric
 */
@Entity
@Table(name = "relatorio_produtividade", schema="prodocente")
public class RelatorioProdutividade implements Validatable {

	/** ID do relatório de distribuição de cotas */
	public static final int RELATORIO_DISTRIBUICAO_COTAS = 2;

	/**
	 * id do relatório
	 */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_relatorio_produtividade", nullable = false)
    private int id;

	/**
	 * Usuário que criou
	 */
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne
    private Usuario usuario;

    /**
     * data de criação
     */
    @Column(name = "data_criacao")
    @Temporal(TemporalType.DATE)
    @CriadoEm
    private Date dataCriacao;

    /**
     * Título do relatório
     */
    @Column(name = "titulo")
    private String titulo;

    /**
     * Grupos que estão associados a este relatório
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "relatorioProdutividade")
    @OrderBy(value = "numeroTopico asc")
    private List<GrupoRelatorioProdutividade> grupoRelatorioProdutividadeCollection = new ArrayList<GrupoRelatorioProdutividade>();

    /**
     * Diz se este relatório está ativo ou não
     */
    @Column(name = "ativo")
    private boolean ativo;

    /**
     * Calcular o total de pontos do relatório
     *
     * @return
     */
    public float getTotalPontos() {
    	float total = 0;
    	for ( GrupoRelatorioProdutividade grupo : grupoRelatorioProdutividadeCollection ) {
    		total += grupo.getTotalPontos();
    	}
    	return total;
    }
    
    /**
     * Calcular o total de pontos do relatório
     *
     * @return
     */
    public float getTotalPontosSemLimite() {
    	float total = 0;
    	for ( GrupoRelatorioProdutividade grupo : grupoRelatorioProdutividadeCollection ) {
    		total += grupo.getTotalPontosSemLimite();
    	}
    	return total;
    }

    /** Creates a new instance of RelatorioProdutividade */
    public RelatorioProdutividade() {
    }

    /**
     * Creates a new instance of RelatorioProdutividade with the specified values.
     * @param id the id of the RelatorioProdutividade
     */
    public RelatorioProdutividade(int idRelatorioProdutividade) {
        this.id = idRelatorioProdutividade;
    }

    /**
     * Gets the id of this RelatorioProdutividade.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this RelatorioProdutividade to the specified value.
     * @param id the new id
     */
    public void setId(int idRelatorioProdutividade) {
        this.id = idRelatorioProdutividade;
    }

    /**
     * Gets the usuario of this RelatorioProdutividade.
     * @return the usuario
     */
    public Usuario getUsuario() {
        return this.usuario;
    }

    /**
     * Sets the usuario of this RelatorioProdutividade to the specified value.
     * @param usuario the new usuario
     */
    public void setUsuario(Usuario idUsuario) {
        this.usuario = idUsuario;
    }

    /**
     * Gets the dataCriacao of this RelatorioProdutividade.
     * @return the dataCriacao
     */
    public Date getDataCriacao() {
        return this.dataCriacao;
    }

    /**
     * Sets the dataCriacao of this RelatorioProdutividade to the specified value.
     * @param dataCriacao the new dataCriacao
     */
    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    /**
     * Gets the titulo of this RelatorioProdutividade.
     * @return the titulo
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Sets the titulo of this RelatorioProdutividade to the specified value.
     * @param titulo the new titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Gets the grupoRelatorioProdutividadeCollection of this RelatorioProdutividade.
     * @return the grupoRelatorioProdutividadeCollection
     */
    public List<GrupoRelatorioProdutividade> getGrupoRelatorioProdutividadeCollection() {
        return this.grupoRelatorioProdutividadeCollection;
    }

    /**
     * Sets the grupoRelatorioProdutividadeCollection of this RelatorioProdutividade to the specified value.
     * @param grupoRelatorioProdutividadeCollection the new grupoRelatorioProdutividadeCollection
     */
    public void setGrupoRelatorioProdutividadeCollection(List<GrupoRelatorioProdutividade> grupoRelatorioProdutividadeCollection) {
        this.grupoRelatorioProdutividadeCollection = grupoRelatorioProdutividadeCollection;
    }


    /**
     * Determines whether another object is equal to this RelatorioProdutividade.  The result is
     * <code>true</code> if and only if the argument is not null and is a RelatorioProdutividade object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RelatorioProdutividade)) {
            return false;
        }
        RelatorioProdutividade other = (RelatorioProdutividade)object;
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
        return "br.ufrn.sigaa.prodocente.relatorio.dominio.RelatorioProdutividade[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getTitulo(), "Título do Relatório", lista);
		return lista;
	}

	/**
	 * @return the ativo
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Este método retorna um mapa com a validade de cada item do relatório de produtividade
	 * @return mapa<int id_do_item, int prazo_validade_em_anos>
	 */
	public Hashtable<Integer, Integer> getMapaValidadesItens(){
		Hashtable<Integer, Integer> mapaValidades = new Hashtable<Integer, Integer>();
		
		for( GrupoRelatorioProdutividade grupo : getGrupoRelatorioProdutividadeCollection() ){
			for (GrupoItem grupoItemRelatorio : grupo.getGrupoItemCollection()) {
					
					mapaValidades.put(grupoItemRelatorio.getItemRelatorioProdutividade().getId(), 
							grupoItemRelatorio.getValidade());
					
			}
		}
		
		return mapaValidades;
	}
}
