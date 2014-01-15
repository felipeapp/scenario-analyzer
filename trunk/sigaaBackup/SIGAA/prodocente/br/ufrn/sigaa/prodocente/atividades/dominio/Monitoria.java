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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Entidade que representa as informações de Monitoria
 *
 * @author eric
 */
@Deprecated
@Entity
@Table(name = "monitoria",schema="prodocente")
public class Monitoria implements Validatable, ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_monitoria", nullable = false)
    private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
    @CampoAtivo
	@Column(name = "ativo")
	private Boolean ativo;

    @JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
    @ManyToOne
    private Unidade departamento;

    @Column(name = "nome_disciplina")
    private String nomeDisciplina;

    @JoinColumn(name = "id_monitor", referencedColumnName ="id_discente")
    @ManyToOne
    private Discente monitor;

    @Column(name = "instituicao")
    private String instituicao;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
    @ManyToOne
    private Servidor servidor;

    @Column(name = "informacao")
    private String informacao;

    @Column(name = "nomemonitor")
    private String nomeMonitor;

    @JoinColumn(name="id_instituicao", referencedColumnName="id" )
    @ManyToOne
    private InstituicoesEnsino ies = new InstituicoesEnsino();

    @JoinColumn(name = "id_agencia_financiadora", referencedColumnName = "id_entidade_financiadora")
    @ManyToOne
    private EntidadeFinanciadora entidadeFinanciadora;

    /** Creates a new instance of Monitoria */
    public Monitoria() {
    }

    /**
     * Creates a new instance of Monitoria with the specified values.
     * @param id the id of the Monitoria
     */
    public Monitoria(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id of this Monitoria.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this Monitoria to the specified value.
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
	public Boolean getAtivo() {	
		return this.ativo; 
	}

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)	{ 
		this.ativo = ativo; 
	}

    /**
     * Gets the departamento of this Monitoria.
     * @return the departamento
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /**
     * Sets the departamento of this Monitoria to the specified value.
     * @param departamento the new departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /**
     * Gets the nomeDisciplina of this Monitoria.
     * @return the nomeDisciplina
     */
    public String getNomeDisciplina() {
        return this.nomeDisciplina;
    }

    /**
     * Sets the nomeDisciplina of this Monitoria to the specified value.
     * @param nomeDisciplina the new nomeDisciplina
     */
    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    /**
     * Gets the monitor of this Monitoria.
     * @return the monitor
     */
    public Discente getMonitor() {
        return this.monitor;
    }

    /**
     * Sets the monitor of this Monitoria to the specified value.
     * @param monitor the new monitor
     */
    public void setMonitor(Discente monitor) {
        this.monitor = monitor;
    }

    /**
     * Gets the instituicao of this Monitoria.
     * @return the instituicao
     */
    public String getInstituicao() {
        return this.instituicao;
    }

    /**
     * Sets the instituicao of this Monitoria to the specified value.
     * @param instituicao the new instituicao
     */
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    /**
     * Gets the periodoInicio of this Monitoria.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this Monitoria to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the periodoFim of this Monitoria.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this Monitoria to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Gets the servidor of this Monitoria.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this Monitoria to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    /**
     * Gets the informacao of this Monitoria.
     * @return the informacao
     */
    public String getInformacao() {
        return this.informacao;
    }

    /**
     * Sets the informacao of this Monitoria to the specified value.
     * @param informacao the new informacao
     */
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    /**
     * Gets the nomemonitor of this Monitoria.
     * @return the nomemonitor
     */
    public String getNomeMonitor() {
        return this.nomeMonitor;
    }

    /**
     * Sets the nomemonitor of this Monitoria to the specified value.
     * @param nomemonitor the new nomemonitor
     */
    public void setNomeMonitor(String nomemonitor) {
        this.nomeMonitor = nomemonitor;
    }

    /**
     * Gets the entidadeFinanciadora of this Monitoria.
     * @return the entidadeFinanciadora
     */
    public EntidadeFinanciadora getEntidadeFinanciadora() {
        return this.entidadeFinanciadora;
    }

    /**
     * Sets the entidadeFinanciadora of this Monitoria to the specified value.
     * @param entidadeFinanciadora the new entidadeFinanciadora
     */
    public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
        this.entidadeFinanciadora = entidadeFinanciadora;
    }

    /**
	 * @return the ies
	 */
	public InstituicoesEnsino getIes() {
		return ies;
	}

	/**
	 * @param ies the ies to set
	 */
	public void setIes(InstituicoesEnsino ies) {
		this.ies = ies;
	}

	/**
     * Determines whether another object is equal to this Monitoria.  The result is
     * <code>true</code> if and only if the argument is not null and is a Monitoria object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Monitoria)) {
            return false;
        }
        Monitoria other = (Monitoria)object;
        if (this.id != other.id || (this.id == 0)) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "br.ufrn.sigaa.prodocente.dominio.Monitoria[id=" + id + "]";
    }

	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Periodo Inicio", lista);
		ValidatorUtil.validateRequired(getPeriodoFim(), "Periodo Fim", lista);
		ValidatorUtil.validateRequiredId(getEntidadeFinanciadora().getId(), "Agência Financiadora", lista);
		ValidatorUtil.validateRequiredId(getDepartamento().getId(), "Departamento", lista);
		ValidatorUtil.validateRequiredId(getIes().getId(), "Instituição", lista);
		
		if (getMonitor().getId() == 1) {
			getMonitor().setId(0);
		}
		ValidatorUtil.validateRequiredId(getMonitor().getId() , "Nome do Monitor", lista);	
		ValidatorUtil.validateRequired(getNomeDisciplina(), "Disciplina", lista);
		ValidatorUtil.validateRequiredId(getServidor().getId() , "Docente", lista);
		return lista;
	}

	public String getItemView() {
		return "<td>" + getNomeMonitor() + "</td>" +
			   "<td>" + Formatador.getInstance().formatarData(periodoInicio) + " - " 
			   + Formatador.getInstance().formatarData(periodoFim) + "</td>";
	}

	public String getTituloView() {
		return "<td>Atividade</td><td>Período</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("nomeMonitor", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}

}