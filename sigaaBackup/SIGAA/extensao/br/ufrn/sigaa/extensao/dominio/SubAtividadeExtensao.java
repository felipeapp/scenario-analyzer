/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/06/2012
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * <p>Representa uma Mini Atividade em um Evento ou Curso de extensão, apezar de ter sido criada com o nome "subatividade".</p>
 * 
 * @author Igor Linnik
 *
 */
@Entity
@Table(schema = "extensao", name = "sub_atividade_extensao")
public class SubAtividadeExtensao implements Validatable {
	
	/**
	 * Chave Primária
	 */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.sub_atividade_extensao_sequence") })
	@Column(name = "id_sub_atividade_extensao", unique = true, nullable = false)
    private int id;
	
	/** Título */
	@Column(name = "titulo")
    private String titulo;
	
	/** Descrição */
    @Column(name = "descricao")
    private String descricao;
    
    /** Local */
    @Column(name = "local")
    private String local;
	
	/** Início */
    @Temporal(TemporalType.DATE)
    @Column(name = "inicio")
    private Date inicio;
    
    /** Fim */
    @Temporal(TemporalType.DATE)
    @Column(name = "fim")
    private Date fim;
    
    /** Horário */
    @Column(name = "horario")
    private String horario;    
    
    /** Carga Horária da Sub atividade.
     *  Usada para gerar a carga horária nos certificados a partir da formula = ( CH * fequencia do participante )
     */
    @Column(name = "carga_horaria")
    private Integer cargaHoraria;
    
    /** Apenas a previsão de vagas, a quantidade real estará na inscrição aberta. */
    @Column(name = "numero_vagas")
    private Integer numeroVagas;	
	
	/** Atividade na qual a subAtividade pertence */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atividade")
    private AtividadeExtensao atividade = new AtividadeExtensao();
    
    /** Representa o tipo de Ação de Extensão. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_sub_atividade_extensao", nullable=false)
    private TipoSubAtividadeExtensao tipoSubAtividadeExtensao = new TipoSubAtividadeExtensao();
    
    
    /** Os participantes são, por exemplo, os alunos de um curso de extensão, ou os
    inscritos de um evento. Público alvo atendido.
	Estes participantes são originários de inscrições on-line e de cadastros realizados pelo próprio coordenador. */
    @OneToMany(mappedBy = "subAtividade")
	private Collection<ParticipanteAcaoExtensao> participantes = new ArrayList<ParticipanteAcaoExtensao>();
    
    
    /** 
     * Quando o usuário remove a sub atividade ela é desativada no sistema, não aparecendo mais para o usuário.
     */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo = true;
	
	
	/** 
	 *  Representa o total de inscritos e APROVADOS pela coordenação do curso/evento.
	 * 
	 *  Esse número é calculado "on line", não é salvo no banco. 
	 **/
	@Transient
	private int numeroInscritos;

	
	/** 
	 *  Representa o total vagaras que fora abertas. 
	 *  Ou seja o somatório do número de vagas de todas as inscrições abertas.
	 * 
	 *  Esse número é calculado "on line", não é salvo no banco. 
	 */
	@Transient
	private int numeroVagasAbertas;
	
	
	/** Representa o total de participantes para essa mini atividade.
	 * 
	 *  Esse número é calculado "on line", não é salvo no banco. 
	 */
	@Transient
	private int numeroParticipantes;
	
	
	
	public SubAtividadeExtensao(){
		
	}
	
	public SubAtividadeExtensao(int id){
		this();
		this.id = id;
	}
	
	public SubAtividadeExtensao(int id, String titulo){
		this(id);
		this.titulo = titulo;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public Integer getNumeroVagas() {
		return numeroVagas;
	}

	public void setNumeroVagas(Integer numeroVagas) {
		this.numeroVagas = numeroVagas;
	}

	public TipoSubAtividadeExtensao getTipoSubAtividadeExtensao() {
		return tipoSubAtividadeExtensao;
	}

	public void setTipoSubAtividadeExtensao(
			TipoSubAtividadeExtensao tipoSubAtividadeExtensao) {
		this.tipoSubAtividadeExtensao = tipoSubAtividadeExtensao;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(titulo, "Título", lista);
		ValidatorUtil.validateRequired(tipoSubAtividadeExtensao, "Tipo da Mini Atividade", lista);
		ValidatorUtil.validateRequired(local, "Local", lista);
		ValidatorUtil.validateRequired(inicio, "Data Início", lista);
		ValidatorUtil.validateRequired(fim, "Data Fim", lista);		
		ValidatorUtil.validateRequired(horario, "Horário", lista);
		ValidatorUtil.validateRequired(cargaHoraria, "Carga Horária", lista);
		ValidatorUtil.validateRequired(numeroVagas, "Número de Vagas", lista);	
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		
		
		if ((inicio != null)	&& (fim != null)) {
			ValidatorUtil.validaOrdemTemporalDatas(inicio, fim, true, "Período da Mini Atividade", lista);
		}
		
		
		return lista;
	}
	
	
	
	public boolean isFinalizada() {
	    return isAtivo() && getFim().before(CalendarUtils.descartarHoras(new Date()));
	}
	
	/**
     * Retorna lista de participantes 'ativos' da ação de extensão 'ordenados' por nome.
     * (Publico alvo atendido)
     * 
     * @return
     */
    public Collection<ParticipanteAcaoExtensao> getParticipantesOrdenados() {
    	Collections.sort(getParticipantesNaoOrdenados());
    	return participantes;
    }
    
    /**
     * Retorna lista de participantes 'ativos' da ação de extensão sem ordenação.
     * Melhora o desempenho na manipulação de listas de participantes. 
     * 
     * @return
     */
    public List<ParticipanteAcaoExtensao> getParticipantesNaoOrdenados() {
    	// Removendo os excluídos da lista..
    	if ((participantes != null) && (!participantes.isEmpty())) {
    		for (Iterator<ParticipanteAcaoExtensao> it = participantes.iterator(); it.hasNext();) {
    			if (!it.next().isAtivo())
    				it.remove();
    		}
    	}

    	return (List<ParticipanteAcaoExtensao>) participantes;
    }

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getNumeroInscritos() {
		return numeroInscritos;
	}

	public void setNumeroInscritos(int numeroInscritos) {
		this.numeroInscritos = numeroInscritos;
	}

	public Collection<ParticipanteAcaoExtensao> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(Collection<ParticipanteAcaoExtensao> participantes) {
		this.participantes = participantes;
	}

	public int getNumeroVagasAbertas() {
		return numeroVagasAbertas;
	}

	public void setNumeroVagasAbertas(int numeroVagasAbertas) {
		this.numeroVagasAbertas = numeroVagasAbertas;
	}

	public int getNumeroParticipantes() {
		return numeroParticipantes;
	}

	public void setNumeroParticipantes(int numeroParticipantes) {
		this.numeroParticipantes = numeroParticipantes;
	}

}
