/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 22/06/2007
 * 
 */
package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/*******************************************************************************
 * <p>
 * Esta classe relaciona um discente de monitoria (DiscenteMonitoria) com seu(s)
 * orientador(es) (EquipeDocente). Cada discente que participa do projeto possui
 * um orientador que o auxilia nas tarefas que deve desenvolver. Este orientador
 * � o docente respons�vel, no projeto, pela disciplina que o discente est� 
 * vinculado.
 * Assim temos: Monitor - Orientacao - Docente(Orientador)
 * </p>
 * 
 * @author Victor Hugo
 ******************************************************************************/
@Entity
@Table(name = "orientacao", schema = "monitoria")
public class Orientacao implements Validatable, ViewAtividadeBuilder {

	// Fields

	/** Identificador da orienta��o.  */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_orientacao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Data de in�cio da orienta��o. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data de t�rmino da orienta��o.  */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Representa o docente respons�vel por orientar o discente no projeto.  */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_equipe_docente")
	private EquipeDocente equipeDocente = new EquipeDocente();

	/** Discente que recebe orienta��o.  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente_monitoria")
	private DiscenteMonitoria discenteMonitoria = new DiscenteMonitoria();

	/** Usu�rio que cadastrou a orienta��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/** Usu�rio que finalizou a orienta��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_finalizacao")
	private RegistroEntrada registroEntradaFinalizacao;

	/** Indica se a orienta��o est� ativa no sistema. Utilizado para exclus�o l�gica do registro no banco.  */
	@Column(name = "ativo")
	private boolean ativo;
	
	/** Utilizado somente como apoio na visualiza��o da orienta��o. */
	@Transient
	private boolean finalizar;

	// Constructors

	/** default constructor */
	public Orientacao() {
	}

	public Orientacao(int id) {
		this.id = id;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Orientador respons�vel pelo discente
	 * 
	 * @return
	 */
	public EquipeDocente getEquipeDocente() {
		return this.equipeDocente;
	}

	public void setEquipeDocente(EquipeDocente equipeDocente) {
		this.equipeDocente = equipeDocente;
	}

	public DiscenteMonitoria getDiscenteMonitoria() {
		return discenteMonitoria;
	}

	public void setDiscenteMonitoria(DiscenteMonitoria discenteMonitoria) {
		this.discenteMonitoria = discenteMonitoria;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (this.ativo) {
			ValidatorUtil.validateRequired(dataInicio, "Data In�cio", lista);
			ValidatorUtil.validateRequired(dataFim, "Data Fim", lista);
		}
		
		if ((dataFim != null) && (dataInicio != null)) {
			//In�cio da orienta��o menor que o fim.
			ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Per�odo da orienta��o",lista);
		    
			Date inicioMonitoria = discenteMonitoria.getProjetoEnsino().getProjeto().getDataInicio();
			Date fimMonitoria = discenteMonitoria.getProjetoEnsino().getProjeto().getDataFim();

			if ( inicioMonitoria != null && fimMonitoria != null ) {
				if (!CalendarUtils.isDentroPeriodo(inicioMonitoria, fimMonitoria, dataInicio) ) {
				    lista.addErro("Data de in�cio da orienta��o est� fora do per�odo de vig�ncia da monitoria (" + Formatador.getInstance().formatarData(inicioMonitoria) 
				    		+ " at� " + Formatador.getInstance().formatarData(fimMonitoria) + ").");
				}

				if (!CalendarUtils.isDentroPeriodo(inicioMonitoria, fimMonitoria, dataFim)) {
					lista.addErro("Data de fim da orienta��o est� fora do per�odo de vig�ncia da monitoria (" + Formatador.getInstance().formatarData(inicioMonitoria) 
							+ " at� " + Formatador.getInstance().formatarData(fimMonitoria) + ").");
				}

				//In�cio da monitoria menor que o fim.
				ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Per�odo da orienta��o",lista);
			}
		}
		
		return lista;
	}

	public String getItemView() {
		return "<td>"
				+ getDiscenteMonitoria().getDiscente().getPessoa().getNome()
				+ "</td>" + "<td style=\"text-align:center\">"
				+ Formatador.getInstance().formatarData(dataInicio)
				+ " - "
				+ Formatador.getInstance().formatarData(dataFim)
				+ "</td>";

	}

	public String getTituloView() {
		return "<td>Atividade</td><td style=\"text-align:center\">Per�odo</td>";
	}

	/** Retorna informa��es da orienta��o em forma de mapa. Utilizado na visualiza��o. */
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("discenteMonitoria.discente.discente.pessoa.nome", "discenteNome");
		itens.put("dataInicio", null);
		itens.put("dataFim", null);
		return itens;
	}

	/** Retorna o nome do discente orientado. Utilizado na visualiza��o. */
	public void setDiscenteNome(String nome) {
		if (this.getDiscenteMonitoria().getDiscente() == null) {
			this.getDiscenteMonitoria().setDiscente(new DiscenteGraduacao());
		}
		this.getDiscenteMonitoria().getDiscente().getPessoa().setNome(nome);
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public float getQtdBase() {
		return 1;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroEntradaFinalizacao() {
		return registroEntradaFinalizacao;
	}

	public void setRegistroEntradaFinalizacao(
			RegistroEntrada registroEntradaFinalizacao) {
		this.registroEntradaFinalizacao = registroEntradaFinalizacao;
	}

	public Date getDataInicio() {
	    return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
	    this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
	    return dataFim;
	}

	public void setDataFim(Date dataFim) {
	    this.dataFim = dataFim;
	}
	
	/**
	 * Verifica, com base na data in�cio e data fim, se a orienta��o ainda est�
	 * ocorrendo.
	 * @return
	 */
	public boolean isVigente() {
	    return getDiscenteMonitoria().isVigente() && isAtivo() && isValida() && CalendarUtils.isDentroPeriodo(getDataInicio(), getDataFim());
	}

	/**
	 * Verifica, com base na data fim, se a orienta��o j� foi finanlizada.
	 * @return
	 */
	public boolean isFinalizada() {
	    return isAtivo() && isValida() &&  ( getDataFim().before(CalendarUtils.descartarHoras(new Date())) || getDataFim().equals(CalendarUtils.descartarHoras(new Date())) ) ;
	}
	
	/**
	 * Indica, com base na data in�cio e data fim, se a orienta��o j� foi iniciada.
	 * @return
	 */
	public boolean isValida() {
	    return (getDataInicio() != null) && (getDataFim() != null);
	}

	/**
	 * Utilizado somente na view para informar se o usu�rio
	 * deseja finalizar a orienta��o.
	 * 
	 * @return
	 */
	public boolean isFinalizar() {
	    return finalizar;
	}

	public void setFinalizar(boolean finalizar) {
	    this.finalizar = finalizar;
	}

	/**
	 * Retorna o status da Orienta��o com base nas datas de in�cio e fim.
	 * 
	 * @return
	 */
	public String getStatus() {
	    return isFinalizada() ? "Finalizada" : isVigente() ? "Ativa" : "Inativa" ;
	}

}
