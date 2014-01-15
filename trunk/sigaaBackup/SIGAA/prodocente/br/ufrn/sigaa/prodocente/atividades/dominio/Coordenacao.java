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
 * Esta entidade ainda é utilizada apenas para o registro de coordenações de cursos de especialização
 * anteriores à implantação do Módulo Lato Sensu do SIGAA, quando esta informação passou a ficar
 * na entidade CoordenacaoCurso do source folder ensino.
 *
 * @author eric
 * @author leonardo
 */
@Deprecated
@Entity
@Table(name = "coordenacao",schema="prodocente")
public class Coordenacao implements Validatable,ViewAtividadeBuilder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_coordenacao", nullable = false)
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

    @Column(name = "nome")
    private String nome;

    @Column(name = "pago")
    private Boolean pago;

    @Column(name = "periodo_inicio")
    @Temporal(TemporalType.DATE)
    private Date periodoInicio;

    @Column(name = "periodo_fim")
    @Temporal(TemporalType.DATE)
    private Date periodoFim;

    @Column(name = "tipo_curso_especializacao")
    private Character tipoCursoEspecializacao;

    /** Creates a new instance of Coordenacao */
    public Coordenacao() {
    }

    /**
     * Creates a new instance of Coordenacao with the specified values.
     * @param id the id of the Coordenacao
     */
    public Coordenacao(Integer id) {
        this.id = id;
    }


    /**
     * Gets the id of this Coordenacao.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of this Coordenacao to the specified value.
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
     * Gets the servidor of this Coordenacao.
     * @return the servidor
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Sets the servidor of this Coordenacao to the specified value.
     * @param servidor the new servidor
     */
    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }


    /**
     * Gets the pago of this Coordenacao.
     * @return the pago
     */
    public Boolean getPago() {
        return this.pago;
    }

    /**
     * Sets the pago of this Coordenacao to the specified value.
     * @param pago the new pago
     */
    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    /**
     * Gets the periodoInicio of this Coordenacao.
     * @return the periodoInicio
     */
    public Date getPeriodoInicio() {
        return this.periodoInicio;
    }

    /**
     * Sets the periodoInicio of this Coordenacao to the specified value.
     * @param periodoInicio the new periodoInicio
     */
    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    /**
     * Gets the periodoFim of this Coordenacao.
     * @return the periodoFim
     */
    public Date getPeriodoFim() {
        return this.periodoFim;
    }

    /**
     * Sets the periodoFim of this Coordenacao to the specified value.
     * @param periodoFim the new periodoFim
     */
    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    /**
     * Determines whether another object is equal to this Coordenacao.  The result is
     * <code>true</code> if and only if the argument is not null and is a Coordenacao object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Coordenacao)) {
            return false;
        }
        Coordenacao other = (Coordenacao)object;
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
        return "br.ufrn.sigaa.prodocente.dominio.CoordenacaoCurso[id=" + id + "]";
    }

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getNome(), "Nome do Curso", lista.getMensagens());
		ValidatorUtil.validateRequired(getServidor(), "Docente", lista.getMensagens());
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Período Início", lista.getMensagens());
		ValidatorUtil.validateRequired(getTipoCursoEspecializacao(), "Curso ou Especialização", lista.getMensagens());
		return lista;

	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Character getTipoCursoEspecializacao() {
		return tipoCursoEspecializacao;
	}

	public void setTipoCursoEspecializacao(Character tipoCursoEspecializacao) {
		this.tipoCursoEspecializacao = tipoCursoEspecializacao;
	}

	public String getItemView() {
		return "  <td>"+getNome()+ "</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(periodoInicio)+" - "+ Formatador.getInstance().formatarData(periodoFim)+"</td>" +
			   "  <td style=\"text-align:center\">"+(getPago()? "Sim" : "Não")+ "</td>";
	}

	public String getTituloView() {
		return  "    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Período</td>" +
				"    <td style=\"text-align:center\">Remunerado</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("nome", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		itens.put("pago", null);
		return itens;
	}

	public float getQtdBase() {
		return CalendarUtils.calculoMeses(periodoInicio, periodoFim);
	}

}
