/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '30/01/2007'
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
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Entity class Relatorio
 *
 * @author eric
 */
@Entity
@Table(name = "relatorio", schema="prodocente")
@Deprecated
public class Relatorio implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_relatorio", nullable = false)
    private int id;

    @JoinColumn(name = "id_area", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq area;

    @JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
    @ManyToOne
    private AreaConhecimentoCnpq subArea;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "autores")
    private String autores;

    @Column(name = "numero_docentes")
    private int numeroDocentes;

    @Column(name = "numero_docentes_outros")
    private int numeroDocentesOutros;

    @Column(name = "instituicao")
    private String instituicao;

    @Column(name = "numero_estudantes")
    private int numeroEstudantes;

    @Column(name = "numero_tecnicos")
    private int numeroTecnicos;

    @Column(name = "numero_outros")
    private int numeroOutros;

    @Column(name = "data_aprovacao")
    @Temporal(TemporalType.DATE)
    private Date dataAprovacao;

    @Column(name = "local")
    private String local;

    @Column(name = "informacao")
    private String informacao;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "destaque")
    private Boolean destaque;

    @Column(name = "projeto")
    private Boolean projeto;

    @Column(name = "relatorio")
    private Boolean relatorio;

    @Column(name = "base")
    private Boolean base;

    @Column(name = "relatorio_final")
    private Boolean relatorioFinal;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "pago")
    private Boolean pago;

    @JoinColumn(name = "id_entidade_financiadora", referencedColumnName = "id_entidade_financiadora")
    @ManyToOne
    private EntidadeFinanciadora entidadeFinanciadora;

    @JoinColumn(name = "id_participacao", referencedColumnName = "id_tipo_participacao")
    @ManyToOne
    private TipoParticipacao tipoParticipacao;

    @JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
    @ManyToOne
    private TipoRegiao tipoRegiao;

    @JoinColumn(name = "id_tipo_relatorio", referencedColumnName = "id_tipo_relatorio")
    @ManyToOne
    private TipoRelatorio tipoRelatorio;

    /** Creates a new instance of Relatorio */
    public Relatorio() {
    }

    /**
     * Creates a new instance of Relatorio with the specified values.
     * @param id the id of the Relatorio
     */
    public Relatorio(int idRelatorio) {
        this.id = idRelatorio;
    }

    /**
     * Gets the id of this Relatorio.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this Relatorio to the specified value.
     * @param id the new id
     */
    public void setId(int idRelatorio) {
        this.id = idRelatorio;
    }

    /**
     * Gets the area of this Relatorio.
     * @return the area
     */
    public AreaConhecimentoCnpq getArea() {
        return this.area;
    }

    /**
     * Sets the area of this Relatorio to the specified value.
     * @param area the new area
     */
    public void setArea(AreaConhecimentoCnpq idArea) {
        this.area = idArea;
    }

    /**
     * Gets the subArea of this Relatorio.
     * @return the subArea
     */
    public AreaConhecimentoCnpq getSubArea() {
        return this.subArea;
    }

    /**
     * Sets the subArea of this Relatorio to the specified value.
     * @param subArea the new subArea
     */
    public void setSubArea(AreaConhecimentoCnpq idSubarea) {
        this.subArea = idSubarea;
    }

    /**
     * Gets the titulo of this Relatorio.
     * @return the titulo
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Sets the titulo of this Relatorio to the specified value.
     * @param titulo the new titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Gets the autores of this Relatorio.
     * @return the autores
     */
    public String getAutores() {
        return this.autores;
    }

    /**
     * Sets the autores of this Relatorio to the specified value.
     * @param autores the new autores
     */
    public void setAutores(String autores) {
        this.autores = autores;
    }

    /**
     * Gets the numeroDocentes of this Relatorio.
     * @return the numeroDocentes
     */
    public int getNumeroDocentes() {
        return this.numeroDocentes;
    }

    /**
     * Sets the numeroDocentes of this Relatorio to the specified value.
     * @param numeroDocentes the new numeroDocentes
     */
    public void setNumeroDocentes(int numeroDocentes) {
        this.numeroDocentes = numeroDocentes;
    }

    /**
     * Gets the numeroDocentesOutros of this Relatorio.
     * @return the numeroDocentesOutros
     */
    public int getNumeroDocentesOutros() {
        return this.numeroDocentesOutros;
    }

    /**
     * Sets the numeroDocentesOutros of this Relatorio to the specified value.
     * @param numeroDocentesOutros the new numeroDocentesOutros
     */
    public void setNumeroDocentesOutros(int numeroDocentesOutros) {
        this.numeroDocentesOutros = numeroDocentesOutros;
    }

    /**
     * Gets the instituicao of this Relatorio.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this Relatorio to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    /**
     * Gets the numeroEstudantes of this Relatorio.
     * @return the numeroEstudantes
     */
    public int getNumeroEstudantes() {
        return this.numeroEstudantes;
    }

    /**
     * Sets the numeroEstudantes of this Relatorio to the specified value.
     * @param numeroEstudantes the new numeroEstudantes
     */
    public void setNumeroEstudantes(int numeroEstudantes) {
        this.numeroEstudantes = numeroEstudantes;
    }

    /**
     * Gets the numeroTecnicos of this Relatorio.
     * @return the numeroTecnicos
     */
    public int getNumeroTecnicos() {
        return this.numeroTecnicos;
    }

    /**
     * Sets the numeroTecnicos of this Relatorio to the specified value.
     * @param numeroTecnicos the new numeroTecnicos
     */
    public void setNumeroTecnicos(int numeroTecnicos) {
        this.numeroTecnicos = numeroTecnicos;
    }

    /**
     * Gets the numeroOutros of this Relatorio.
     * @return the numeroOutros
     */
    public int getNumeroOutros() {
        return this.numeroOutros;
    }

    /**
     * Sets the numeroOutros of this Relatorio to the specified value.
     * @param numeroOutros the new numeroOutros
     */
    public void setNumeroOutros(int numeroOutros) {
        this.numeroOutros = numeroOutros;
    }

    /**
     * Gets the dataAprovacao of this Relatorio.
     * @return the dataAprovacao
     */
    public Date getDataAprovacao() {
        return this.dataAprovacao;
    }

    /**
     * Sets the dataAprovacao of this Relatorio to the specified value.
     * @param dataAprovacao the new dataAprovacao
     */
    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    /**
     * Gets the local of this Relatorio.
     * @return the local
     */
    public String getLocal() {
        return this.local;
    }

    /**
     * Sets the local of this Relatorio to the specified value.
     * @param local the new local
     */
    public void setLocal(String local) {
        this.local = local;
    }

    /**
     * Gets the informacao of this Relatorio.
     * @return the informacao
     */
    public String getInformacao() {
        return this.informacao;
    }

    /**
     * Sets the informacao of this Relatorio to the specified value.
     * @param informacao the new informacao
     */
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    /**
     * Gets the idServidor of this Relatorio.
     * @return the idServidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the idServidor of this Relatorio to the specified value.
     * @param idServidor the new idServidor
     */
    public void setServidor(Servidor idServidor) {
        this.servidor = idServidor;
    }

    /**
     * Gets the destaque of this Relatorio.
     * @return the destaque
     */
    public Boolean getDestaque() {
        return this.destaque;
    }

    /**
     * Sets the destaque of this Relatorio to the specified value.
     * @param destaque the new destaque
     */
    public void setDestaque(Boolean destaque) {
        this.destaque = destaque;
    }

    /**
     * Gets the projeto of this Relatorio.
     * @return the projeto
     */
    public Boolean getProjeto() {
        return this.projeto;
    }

    /**
     * Sets the projeto of this Relatorio to the specified value.
     * @param projeto the new projeto
     */
    public void setProjeto(Boolean projeto) {
        this.projeto = projeto;
    }

    /**
     * Gets the relatorio of this Relatorio.
     * @return the relatorio
     */
    public Boolean getRelatorio() {
        return this.relatorio;
    }

    /**
     * Sets the relatorio of this Relatorio to the specified value.
     * @param relatorio the new relatorio
     */
    public void setRelatorio(Boolean relatorio) {
        this.relatorio = relatorio;
    }

    /**
     * Gets the base of this Relatorio.
     * @return the base
     */
    public Boolean getBase() {
        return this.base;
    }

    /**
     * Sets the base of this Relatorio to the specified value.
     * @param base the new base
     */
    public void setBase(Boolean base) {
        this.base = base;
    }

    /**
     * Gets the relatorioFinal of this Relatorio.
     * @return the relatorioFinal
     */
    public Boolean getRelatorioFinal() {
        return this.relatorioFinal;
    }

    /**
     * Sets the relatorioFinal of this Relatorio to the specified value.
     * @param relatorioFinal the new relatorioFinal
     */
    public void setRelatorioFinal(Boolean relatorioFinal) {
        this.relatorioFinal = relatorioFinal;
    }

    /**
     * Gets the departamento of this Relatorio.
     * @return the departamento
     */
    public String getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the departamento of this Relatorio to the specified value.
     * @param departamento the new departamento
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * Gets the pago of this Relatorio.
     * @return the pago
     */
    public Boolean getPago() {
        return this.pago;
    }

    /**
     * Sets the pago of this Relatorio to the specified value.
     * @param pago the new pago
     */
    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    /**
     * Gets the entidadeFinanciadora of this Relatorio.
     * @return the entidadeFinanciadora
     */
    public EntidadeFinanciadora getEntidadeFinanciadora() {
        return this.entidadeFinanciadora;
    }

    /**
     * Sets the entidadeFinanciadora of this Relatorio to the specified value.
     * @param entidadeFinanciadora the new entidadeFinanciadora
     */
    public void setEntidadeFinanciadora(EntidadeFinanciadora idEntidadeFinanciadora) {
        this.entidadeFinanciadora = idEntidadeFinanciadora;
    }

    /**
     * Gets the tipoParticipacao of this Relatorio.
     * @return the tipoParticipacao
     */
    public TipoParticipacao getTipoParticipacao() {
        return this.tipoParticipacao;
    }

    /**
     * Sets the tipoParticipacao of this Relatorio to the specified value.
     * @param tipoParticipacao the new tipoParticipacao
     */
    public void setTipoParticipacao(TipoParticipacao idParticipacao) {
        this.tipoParticipacao = idParticipacao;
    }

    /**
     * Gets the tipoRegiao of this Relatorio.
     * @return the tipoRegiao
     */
    public TipoRegiao getTipoRegiao() {
        return this.tipoRegiao;
    }

    /**
     * Sets the tipoRegiao of this Relatorio to the specified value.
     * @param tipoRegiao the new tipoRegiao
     */
    public void setTipoRegiao(TipoRegiao idTipoRegiao) {
        this.tipoRegiao = idTipoRegiao;
    }

    /**
     * Gets the tipoRelatorio of this Relatorio.
     * @return the tipoRelatorio
     */
    public TipoRelatorio getTipoRelatorio() {
        return this.tipoRelatorio;
    }

    /**
     * Sets the tipoRelatorio of this Relatorio to the specified value.
     * @param tipoRelatorio the new tipoRelatorio
     */
    public void setTipoRelatorio(TipoRelatorio idTipoRelatorio) {
        this.tipoRelatorio = idTipoRelatorio;
    }



    /**
     * Determines whether another object is equal to this Relatorio.  The result is
     * <code>true</code> if and only if the argument is not null and is a Relatorio object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Relatorio)) {
            return false;
        }
        Relatorio other = (Relatorio)object;
        if (this.id != other.id  || this.id == 0) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Relatorio[id=" + id + "]";
    }

	public ListaMensagens validate() {
		return null;
	}

	public String getItemView() {
		return "<tr>" +
			   "  <td>"+titulo+ "</td>" +
			   "  <td>"+Formatador.getInstance().formatarData(dataAprovacao)+"</td>"+
			   "  <td>"+(pago ? "Sim":"Não" )+"</td>";

	}

	public String getTituloView() {
		return  "<tr>" +
				"    <td>Atividade</td>" +
				"    <td>Data Publicação</td>" +
				"    <td>Remunerado</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("dataAprovacao", null);
		itens.put("pago", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}


}
