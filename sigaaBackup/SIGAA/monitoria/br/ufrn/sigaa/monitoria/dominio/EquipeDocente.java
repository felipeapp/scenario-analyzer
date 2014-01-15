/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 11/06/2007
 * 
 */
package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/*******************************************************************************
 * <p>
 * Classe que representa o orientador (Docente Efetivo) do projeto de monitoria.
 * 
 * //ATENÇÃO => Representa apenas um membro da equipe, apesar do nome da classe
 * sugerir uma coleção. 
 * 
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "equipe_docente", schema = "monitoria")
public class EquipeDocente implements Validatable {

	// Fields

	private int id;

	private boolean coordenador;

	private Date dataInicioCoordenador;

	private Date dataFimCoordenador;

	private Date dataEntradaProjeto;

	private Date dataSaidaProjeto;

	private boolean ativo;

	private Servidor servidor = new Servidor();

	private Set<EquipeDocenteComponente> docentesComponentes = new HashSet<EquipeDocenteComponente>();

	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	private Set<Orientacao> orientacoes = new HashSet<Orientacao>();

	private boolean excluido;

	private RegistroEntrada registroEntradaExclusao;

	
	//campos usados somente para views
	private boolean selecionado;

	private Date dataInicioOrientacao;
	
	private Date dataFimOrientacao;

	
	
	/** default constructor */
	public EquipeDocente() {
	}

	/** default constructor */
	public EquipeDocente(EquipeDocente equipe) {
		this.id = equipe.getId();
		this.coordenador = equipe.isCoordenador();
		this.dataInicioCoordenador = equipe.getDataInicioCoordenador();
		this.dataFimCoordenador = equipe.getDataFimCoordenador();
		this.dataEntradaProjeto = equipe.getDataEntradaProjeto();
		this.dataSaidaProjeto = equipe.getDataSaidaProjeto();
		this.ativo = equipe.isAtivo();
		this.servidor = equipe.getServidor();
		this.docentesComponentes = equipe.getDocentesComponentes();
		this.projetoEnsino = equipe.getProjetoEnsino();
		this.orientacoes = equipe.getOrientacoes();
	}

	/** minimal constructor */
	public EquipeDocente(int idOrientador, boolean coordenador,
			Date dataEntradaProjeto, boolean ativo, Servidor servidor) {
		this.id = idOrientador;
		this.coordenador = coordenador;
		this.dataEntradaProjeto = dataEntradaProjeto;
		this.ativo = ativo;
		this.servidor = servidor;
	}

	/** full constructor */
	public EquipeDocente(int idOrientador, boolean coordenador,
			Date dataInicioCoordenador, Date dataFimCoordenador,
			Date dataEntradaProjeto, Date dataSaidaProjeto, boolean ativo,
			Servidor servidor) {
		this.id = idOrientador;
		this.coordenador = coordenador;
		this.dataInicioCoordenador = dataInicioCoordenador;
		this.dataFimCoordenador = dataFimCoordenador;
		this.dataEntradaProjeto = dataEntradaProjeto;
		this.dataSaidaProjeto = dataSaidaProjeto;
		this.ativo = ativo;
		this.servidor = servidor;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_equipe_docente", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idOrientador) {
		this.id = idOrientador;
	}

	@Column(name = "coordenador")
	public boolean isCoordenador() {
		return this.coordenador;
	}

	public void setCoordenador(boolean coordenador) {
		this.coordenador = coordenador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inicio_coordenador")
	public Date getDataInicioCoordenador() {
		return this.dataInicioCoordenador;
	}

	public void setDataInicioCoordenador(Date dataInicioCoordenador) {
		this.dataInicioCoordenador = dataInicioCoordenador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim_coordenador")
	public Date getDataFimCoordenador() {
		return this.dataFimCoordenador;
	}

	public void setDataFimCoordenador(Date dataFimCoordenador) {
		this.dataFimCoordenador = dataFimCoordenador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_entrada_projeto")
	public Date getDataEntradaProjeto() {
		return this.dataEntradaProjeto;
	}

	public void setDataEntradaProjeto(Date dataEntradaProjeto) {
		this.dataEntradaProjeto = dataEntradaProjeto;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_saida_projeto")
	public Date getDataSaidaProjeto() {
		return this.dataSaidaProjeto;
	}

	public void setDataSaidaProjeto(Date dataSaidaProjeto) {
		this.dataSaidaProjeto = dataSaidaProjeto;
	}

	@Column(name = "ativo")
	public boolean isAtivo() {
		return this.ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_monitoria")
	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	@OneToMany(mappedBy = "equipeDocente")
	public Set<Orientacao> getOrientacoes() {
		return orientacoes;
	}

	/**
	 * Não exibe orientações marcadas como excluídas no banco
	 * 
	 * @return
	 */
	@Transient	
	public Set<Orientacao> getOrientacoesValidas() {

		// removendo os excluídos da lista..
		if (orientacoes != null) {
			for (Iterator<Orientacao> it = orientacoes.iterator(); it.hasNext();) {
			    	Orientacao ori = it.next(); 
				if (!ori.isAtivo() || !ori.isValida()) {
					it.remove();
				}
			}
		}

		return orientacoes;
	}
		
	

	public void setOrientacoes(Set<Orientacao> orientacoes) {
		this.orientacoes = orientacoes;
	}

	@OneToMany(cascade = {}, mappedBy = "equipeDocente")
	public Set<EquipeDocenteComponente> getDocentesComponentes() {
		
		return docentesComponentes;
	}
	
	@Transient
	public Set<EquipeDocenteComponente> getDocentesComponentesValidos() {
		// removendo os inativos da lista..
		if (docentesComponentes != null) {
			for (Iterator<EquipeDocenteComponente> comp = docentesComponentes.iterator(); comp.hasNext();) {
				EquipeDocenteComponente compDocente = comp.next(); 
				if (!compDocente.isAtivo()) {
					comp.remove();
				}
			}
		}
		return docentesComponentes;
	}
	
	


	public void setDocentesComponentes(
			Set<EquipeDocenteComponente> docentesComponentes) {
		this.docentesComponentes = docentesComponentes;
	}

	public boolean isExcluido() {
		return excluido;
	}

	public void setExcluido(boolean excluido) {
		this.excluido = excluido;
	}

	// usuário que excluiu o equipe docente
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_exclusao", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntradaExclusao() {
		return registroEntradaExclusao;
	}

	public void setRegistroEntradaExclusao(
			RegistroEntrada registroEntradaExclusao) {
		this.registroEntradaExclusao = registroEntradaExclusao;
	}


	/**
	 * Inclui um componente curricular para o Docente.
	 * 
	 * @param componenteCurricularMonitoria
	 * @return
	 */
	public boolean addComponenteCurricularMonitoria(
			ComponenteCurricularMonitoria componenteCurricularMonitoria) {
		EquipeDocenteComponente novoDocenteComponente = new EquipeDocenteComponente();
		novoDocenteComponente
				.setComponenteCurricularMonitoria(componenteCurricularMonitoria);
		novoDocenteComponente.setEquipeDocente(this);
		return docentesComponentes.add(novoDocenteComponente);
	}

	/**
	 * Inclui uma Orientação (ligação de docente com monitor) para o Docente.
	 * 
	 * @param o
	 *            Orientacao
	 * @return true se adicionado com sucesso
	 */
	public boolean addOrientacao(Orientacao o) {
		o.setEquipeDocente(this);
		return orientacoes.add(o);
	}

	@Override
	public boolean equals(Object obj) {
		EquipeDocente outro = (EquipeDocente) obj;
		if (outro.getServidor() != null && getServidor() != null) {
			return outro.getServidor().getId() == getServidor().getId();
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.servidor.getPessoa().getNome() + "  ("
				+ this.projetoEnsino.getTitulo() + ")";
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(servidor, "Servidor", lista);
		ValidatorUtil.validateRequired(projetoEnsino, "Projeto Monitoria",lista);
		ValidatorUtil.validateRequired(dataEntradaProjeto, "Data Entrada",lista);
		ValidatorUtil.validateRequired(dataSaidaProjeto, "Data Saída",lista);
		if (dataSaidaProjeto != null) {
			if(dataEntradaProjeto.compareTo(dataSaidaProjeto) > 0) {
				lista.addMensagem(MensagensMonitoria.REGRA_DATA_SAIDA_PROJETO);
			}
		}		
		return lista;
	}

	
	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/** 
	 * Utilizado somente nas views.
	 * Permite determinar uma data inicial na criação de orientações deste docente.
	 * Usado na View de criação de novos monitores pela prograd. 
	 *  
	 * @return
	 */
	@Transient
	public Date getDataInicioOrientacao() {
		return dataInicioOrientacao;
	}

	public void setDataInicioOrientacao(Date dataInicioOrientacao) {
		this.dataInicioOrientacao = dataInicioOrientacao;
	}
	
	/** 
	 * Utilizado somente nas views.
	 * Permite determinar uma data final na criação de orientações deste docente.
	 * Usado na View de criação de novos monitores pela prograd. 
	 *  
	 * @return
	 */
	@Transient
	public Date getDataFimOrientacao() {
	    return dataFimOrientacao;
	}

	public void setDataFimOrientacao(Date dataFimOrientacao) {
	    this.dataFimOrientacao = dataFimOrientacao;
	}

	
	/**
	 * Coordenador antigo ou atual. 
	 */
	@Transient
	public Boolean getFoiCoordenador()	{
		return (dataInicioCoordenador != null);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}


}
