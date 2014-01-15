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
 * Entidade que contém as informação de um minicurso ministrado por um docente da instituição
 *
 * @author eric
 */
@Entity
@Table(name = "mini_curso",schema="prodocente")
public class MiniCurso implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_mini_curso", nullable = false)
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

    @Column(name = "nome_congresso")
    private String nomeCongresso;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "data_inicio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "data_fim")
    @Temporal(TemporalType.DATE)
    private Date dataFim;

    @Column(name = "carga_horaria")
    private Integer cargaHoraria;

    @Column(name = "numero_docentes")
    private Integer numeroDocentes;

    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento;

    @Column(name = "tipo_ch_horaria")
    private String tipoChHoraria;

    /** Creates a new instance of MiniCurso */
    public MiniCurso() {
    }

    /**
     * Creates a new instance of MiniCurso with the specified values.
     * @param idMiniCurso the idMiniCurso of the MiniCurso
     */
    public MiniCurso(Integer idMiniCurso) {
        this.id = idMiniCurso;
    }


    /**
     * Gets the id of this MiniCurso.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this MiniCurso to the specified value.
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
     * Gets the servidor of this MiniCurso.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this MiniCurso to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the nomeCongresso of this MiniCurso.
     * @return the nomeCongresso
     */
    public String getNomeCongresso() {
        return this.nomeCongresso;
    }

    /**
     * Sets the nomeCongresso of this MiniCurso to the specified value.
     * @param nomeCongresso the new nomeCongresso
     */
    public void setNomeCongresso(String nomeCongresso) {
        this.nomeCongresso = nomeCongresso;
    }

    /**
     * Gets the titulo of this MiniCurso.
     * @return the titulo
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Sets the titulo of this MiniCurso to the specified value.
     * @param titulo the new titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }



    /**
	 * @return the periodoInicio
	 */
	public Date getPeriodoInicio() {
		return periodoInicio;
	}

	/**
	 * @param periodoInicio the periodoInicio to set
	 */
	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	/**
     * Gets the dataFim of this MiniCurso.
     * @return the dataFim
     */
    public Date getDataFim() {
        return this.dataFim;
    }

    /**
     * Sets the dataFim of this MiniCurso to the specified value.
     * @param dataFim the new dataFim
     */
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    /**
     * Gets the cargaHoraria of this MiniCurso.
     * @return the cargaHoraria
     */
    public Integer getCargaHoraria() {
        return this.cargaHoraria;
    }

    /**
     * Sets the cargaHoraria of this MiniCurso to the specified value.
     * @param cargaHoraria the new cargaHoraria
     */
    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    /**
     * Gets the numeroDocentes of this MiniCurso.
     * @return the numeroDocentes
     */
    public Integer getNumeroDocentes() {
        return this.numeroDocentes;
    }

    /**
     * Sets the numeroDocentes of this MiniCurso to the specified value.
     * @param numeroDocentes the new numeroDocentes
     */
    public void setNumeroDocentes(Integer numeroDocentes) {
        this.numeroDocentes = numeroDocentes;
    }

    /**
     * Gets the departamento of this MiniCurso.
     * @return the departamento
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the departamento of this MiniCurso to the specified value.
     * @param departamento the new departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /**
     * Gets the tipoChHoraria of this MiniCurso.
     * @return the tipoChHoraria
     */
    public String getTipoChHoraria() {
        return this.tipoChHoraria;
    }

    /**
     * Sets the tipoChHoraria of this MiniCurso to the specified value.
     * @param tipoChHoraria the new tipoChHoraria
     */
    public void setTipoChHoraria(String tipoChHoraria) {
        this.tipoChHoraria = tipoChHoraria;
    }


    /**
     * Determines whether another object is equal to this MiniCurso.  The result is
     * <code>true</code> if and only if the argument is not null and is a MiniCurso object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MiniCurso)) {
            return false;
        }
        MiniCurso other = (MiniCurso)object;
        if (this.id != other.id && (this.id == 0 || this.id !=other.id)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.MiniCurso[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getTitulo(), "Título", lista.getMensagens());
		ValidatorUtil.validateRequired(getNomeCongresso(), "Congresso/Evento", lista.getMensagens());
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Data Inicio", lista.getMensagens());
		ValidatorUtil.validateRequired(getCargaHoraria(), "Carga Horária", lista.getMensagens());
		ValidatorUtil.validateRequired(getTipoChHoraria(), "Tipo Carga Horária", lista.getMensagens());
		ValidatorUtil.validateRequired(getNumeroDocentes(), "Numero de Docentes", lista.getMensagens());
		//ValidatorUtil.validateRequired(getDepartamento(), "Departamento", lista.getMensagens());
		setDepartamento(null);
		return lista;
	}

	public String getItemView() {
		return "  <td>"+titulo+ "</td>" +
			   "  <td>"+Formatador.getInstance().formatarData(periodoInicio)+" - "+Formatador.getInstance().formatarData(dataFim)+ "</td>"+
			   "  <td>"+ cargaHoraria +"</td>";

	}

	public String getTituloView() {
		return  "    <td width=\"60%\">Atividade</td>" +
				"    <td align=center WIDTH=\"10%\">Período</td>" +
				"    <td align=center WIDTH=\"10%\">Carga Horária</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("periodoInicio", null);
		itens.put("dataFim", null);
		itens.put("cargaHoraria", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}

}
