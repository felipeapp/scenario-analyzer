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
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 *  Entidade que representa as informações de Cargo Administrativo
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "cargo_administrativo",schema="prodocente")
public class CargoAdministrativo implements Validatable, ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_cargo_administrativo", nullable = false)
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

    @Column(name = "data_inicio")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @Column(name = "data_fim")
    @Temporal(TemporalType.DATE)
    private Date dataFim;

    @Column(name = "portaria")
    private String portaria;

    @Column(name = "data_portaria")
    @Temporal(TemporalType.DATE)
    private Date dataPortaria;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "portaria_final")
    private String portariaFinal;

    @Column(name = "data_portaria_fim")
    @Temporal(TemporalType.DATE)
    private Date dataPortariaFim;

    @JoinColumn(name = "id_designacao_cargo", referencedColumnName = "id_designacao_cargo")
    @ManyToOne
    private DesignacaoCargo designacaoCargo;

    /** Creates a new instance of CargoAdministrativo */
    public CargoAdministrativo() {
    }

    /**
     * Creates a new instance of CargoAdministrativo with the specified values.
     * @param id the id of the CargoAdministrativo
     */
    public CargoAdministrativo(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this CargoAdministrativo.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this CargoAdministrativo to the specified value.
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
     * Gets the servidor of this CargoAdministrativo.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this CargoAdministrativo to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the dataInicio of this CargoAdministrativo.
     * @return the dataInicio
     */
    public Date getDataInicio() {
        return this.dataInicio;
    }

    /**
     * Sets the dataInicio of this CargoAdministrativo to the specified value.
     * @param dataInicio the new dataInicio
     */
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * Gets the dataFim of this CargoAdministrativo.
     * @return the dataFim
     */
    public Date getDataFim() {
        return this.dataFim;
    }

    /**
     * Sets the dataFim of this CargoAdministrativo to the specified value.
     * @param dataFim the new dataFim
     */
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    /**
     * Gets the portaria of this CargoAdministrativo.
     * @return the portaria
     */
    public String getPortaria() {
        return this.portaria;
    }

    /**
     * Sets the portaria of this CargoAdministrativo to the specified value.
     * @param portaria the new portaria
     */
    public void setPortaria(String portaria) {
        this.portaria = portaria;
    }

    /**
     * Gets the dataPortaria of this CargoAdministrativo.
     * @return the dataPortaria
     */
    public Date getDataPortaria() {
        return this.dataPortaria;
    }

    /**
     * Sets the dataPortaria of this CargoAdministrativo to the specified value.
     * @param dataPortaria the new dataPortaria
     */
    public void setDataPortaria(Date dataPortaria) {
        this.dataPortaria = dataPortaria;
    }

    /**
     * Gets the observacao of this CargoAdministrativo.
     * @return the observacao
     */
    public String getObservacao() {
        return this.observacao;
    }

    /**
     * Sets the observacao of this CargoAdministrativo to the specified value.
     * @param observacao the new observacao
     */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

   /**
     * Gets the portariaFinal of this CargoAdministrativo.
     * @return the portariaFinal
     */
    public String getPortariaFinal() {
        return this.portariaFinal;
    }

    /**
     * Sets the portariaFinal of this CargoAdministrativo to the specified value.
     * @param portariaFinal the new portariaFinal
     */
    public void setPortariaFinal(String portariaFinal) {
        this.portariaFinal = portariaFinal;
    }

    /**
     * Gets the dataPortariaFim of this CargoAdministrativo.
     * @return the dataPortariaFim
     */
    public Date getDataPortariaFim() {
        return this.dataPortariaFim;
    }

    /**
     * Sets the dataPortariaFim of this CargoAdministrativo to the specified value.
     * @param dataPortariaFim the new dataPortariaFim
     */
    public void setDataPortariaFim(Date dataPortariaFim) {
        this.dataPortariaFim = dataPortariaFim;
    }

    /**
     * Gets the designacaoCargo of this CargoAdministrativo.
     * @return the designacaoCargo
     */
    public DesignacaoCargo getDesignacaoCargo() {
        return this.designacaoCargo;
    }

    /**
     * Sets the designacaoCargo of this CargoAdministrativo to the specified value.
     * @param designacaoCargo the new designacaoCargo
     */
    public void setDesignacaoCargo(DesignacaoCargo designacaoCargo) {
        this.designacaoCargo = designacaoCargo;
    }



    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ativo == null) ? 0 : ativo.hashCode());
		result = prime * result + ((dataFim == null) ? 0 : dataFim.hashCode());
		result = prime * result
				+ ((dataInicio == null) ? 0 : dataInicio.hashCode());
		result = prime * result
				+ ((dataPortaria == null) ? 0 : dataPortaria.hashCode());
		result = prime * result
				+ ((dataPortariaFim == null) ? 0 : dataPortariaFim.hashCode());
		result = prime * result
				+ ((designacaoCargo == null) ? 0 : designacaoCargo.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((observacao == null) ? 0 : observacao.hashCode());
		result = prime * result
				+ ((portaria == null) ? 0 : portaria.hashCode());
		result = prime * result
				+ ((portariaFinal == null) ? 0 : portariaFinal.hashCode());
		result = prime * result
				+ ((servidor == null) ? 0 : servidor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CargoAdministrativo other = (CargoAdministrativo) obj;
		if (ativo == null) {
			if (other.ativo != null)
				return false;
		} else if (!ativo.equals(other.ativo))
			return false;
		if (dataFim == null) {
			if (other.dataFim != null)
				return false;
		} else if (!dataFim.equals(other.dataFim))
			return false;
		if (dataInicio == null) {
			if (other.dataInicio != null)
				return false;
		} else if (!dataInicio.equals(other.dataInicio))
			return false;
		if (dataPortaria == null) {
			if (other.dataPortaria != null)
				return false;
		} else if (!dataPortaria.equals(other.dataPortaria))
			return false;
		if (dataPortariaFim == null) {
			if (other.dataPortariaFim != null)
				return false;
		} else if (!dataPortariaFim.equals(other.dataPortariaFim))
			return false;
		if (designacaoCargo == null) {
			if (other.designacaoCargo != null)
				return false;
		} else if (!designacaoCargo.equals(other.designacaoCargo))
			return false;
		if (id != other.id)
			return false;
		if (observacao == null) {
			if (other.observacao != null)
				return false;
		} else if (!observacao.equals(other.observacao))
			return false;
		if (portaria == null) {
			if (other.portaria != null)
				return false;
		} else if (!portaria.equals(other.portaria))
			return false;
		if (portariaFinal == null) {
			if (other.portariaFinal != null)
				return false;
		} else if (!portariaFinal.equals(other.portariaFinal))
			return false;
		if (servidor == null) {
			if (other.servidor != null)
				return false;
		} else if (!servidor.equals(other.servidor))
			return false;
		return true;
	}

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.CargoAdministrativo[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(getDesignacaoCargo().getId(), "Cargo", lista);
		ValidatorUtil.validateRequired(getPortaria(), "Portaria", lista);
		ValidatorUtil.validateRequiredId(getServidor().getId(), "Docente", lista);
		ValidatorUtil.validateRequired(getDataInicio(), "Data Inicio", lista);
		ValidatorUtil.validateRequired(getDataPortaria(), "Data Portaria", lista);
		ValidatorUtil.validateRequired(getPortariaFinal(), "Portaria Final", lista);

		return lista;
	}
	
	public String getItemView() {
		return "  <td>"+getDesignacaoCargo().getDescricao()+ "</td>" +
			   "  <td>"+Formatador.getInstance().formatarData(dataInicio)+" - "+ Formatador.getInstance().formatarData(dataFim)+"</td>" +
			   "  <td>"+CalendarUtils.calculoMeses(dataInicio, dataFim)+"</td>";
	}

	public String getTituloView() {
		return 	"    <td>Atividade</td>" +
				"    <td>Período</td>" +
				"	 <td>Meses</td>";
	}


	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("designacaoCargo.descricao", "descricaoDesignacao");
		itens.put("dataInicio", null);
		itens.put("dataFim", null);
		return itens;
	}


	public void setDescricaoDesignacao( String nome ) {
		if ( this.getDesignacaoCargo() == null ) {
			this.setDesignacaoCargo( new DesignacaoCargo() );
		}
		this.getDesignacaoCargo().setDescricao(nome);
	}

	public float getQtdBase() {
		return CalendarUtils.calculoMeses(dataInicio, dataFim);
	}
}
