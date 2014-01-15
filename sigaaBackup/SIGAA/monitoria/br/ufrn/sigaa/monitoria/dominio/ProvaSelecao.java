/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 20/08/2008
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Informa��es sobre a sele��o de monitoria (ProvaSeletiva = Perfil da Vaga)
 * <br/>
 * 
 * O perfil da vaga � um conjunto de componentes curriculares que tem
 * caracter�sticas em comum e que pode ser inclu�dos como pr�-requisito cada
 * inscri��o do candidato em prova de sele��o.<br />
 * Um perfil pode ser formado por mais de um componente curricular e por mais de
 * uma bolsa do projeto.
 * <br/> 
 * 
 * O total de bolsas do projeto � resultado da soma de todas as bolsas de todos
 * os perfis do projeto. Um perfil pode conter componentes curriculares
 * obrigat�rios.<br />
 * 
 * Esse perfil, que ser� cadastrado pelo coordenador depois do cadastro do
 * projeto, poder� ser alterado de todas as formas por membros da PROGRAD.
 * </p>
 * 
 * @author Gleydson
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "prova_selecao", schema = "monitoria")
public class ProvaSelecao implements PersistDB, Validatable {

	/** Identificador da prova seletiva. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_prova_selecao", nullable = false)
	private int id; 

	/** T�tulo da prova seletiva. */
	@Column(name = "titulo")
	private String titulo;
	
	/** Vagas remuneradas do projeto reservadas para esta prova seletiva. */
	@Column(name = "vagas_remuneradas")
	private Integer vagasRemuneradas = 0;

	/** Vagas n�o remuneradas do projeto reservadas para esta prova seletiva. */
	@Column(name = "vagas_nao_remuneradas")
	private Integer vagasNaoRemuneradas = 0;

	/** Data limite para inscri��o dos discentes na prova. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_limite_inscricao")
	private Date dataLimiteIncricao;

	/** Data de realiza��o da prova. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_prova")
	private Date dataProva;

	/** Informa��es adicionais da prova seletiva. Ex. Local/Hor�rio da prova. */
	@Column(name = "informacao_selecao")
	private String informacaoSelecao;

	/** Data de cadastro da prova. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Informa se a prova seletiva est� ativa no sistema. Utilizado para exclus�o l�gica. */
	@Column(name = "ativo")
	private boolean ativo;

	/** Projeto ao qual a prova est� vinculada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto_monitoria")
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	/** Registro de entrada do cadatro da prova. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registro;

	/** Lista de discentes candidatos �s vagas dispon�veis na prova. */
	@OneToMany(mappedBy = "provaSelecao")
	private Collection<InscricaoSelecaoMonitoria> discentesInscritos = new ArrayList<InscricaoSelecaoMonitoria>();

	/** Lista de componentes curriculares que s�o pr�-requisitos para inscri��o dos candidatos �s vagas da prova. */
	@OneToMany(mappedBy = "provaSelecao", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	private Collection<ProvaSelecaoComponenteCurricular> componentesObrigatorios = new HashSet<ProvaSelecaoComponenteCurricular>();

	/** Situa��o da prova seletiva. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_situacao_prova_selecao")
	private TipoSituacaoProvaSelecao situacaoProva = new TipoSituacaoProvaSelecao();
	
	/** Armazena temporariamente o resultado da prova seletiva. Utilizado somente na visualiza��o, durante o cadastro das notas da prova. */
	@Transient
	private Collection<DiscenteMonitoria> resultadoSelecao = new ArrayList<DiscenteMonitoria>();
	
	/** Total de vagas remuneradas dispon�veis para reserva na prova. Utilizado somente na view, durante o cadastro da prova. */
	@Transient
	private int totalBolsasRemuneradasDisponiveisReserva;
	
	/** Total de vagas n�o remuneradas dispon�veis para reserva na prova. Utilizado somente na view, durante o cadastro da prova. */
	@Transient
	private int totalBolsasNaoRemuneradasDisponiveisReserva;

	/**
	 * Construtor padr�o.
	 */
	public ProvaSelecao() {
	}

	/**
	 * Construtor m�nimo.
	 * 
	 * @param idProva identificador da prova seletiva.
	 */
	public ProvaSelecao(int idProva) {
		this.id = idProva;
	}

	/**
	 * Identificador �nico para prova seletiva.
	 * 
	 * @return retorna o identificador.
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * T�tulo da prova que ser� utilizado para facilitar a identifica��o do processo seletivo.
	 * 
	 * @return T�tulo da prova.
	 */
	public String getTitulo() {
	    return titulo;
	}

	public void setTitulo(String titulo) {
	    this.titulo = titulo;
	}

	/**
	 * Data de realiza��o da prova.
	 * 
	 * @return Data de realiza��o da prova.
	 */
	public Date getDataProva() {
		return dataProva;
	}

	public void setDataProva(Date dataProva) {
		this.dataProva = dataProva;
	}

	/**
	 * Prazo final para inscri��o dos discente na prova.
	 * 
	 * @return Data limite para inscri��o na prova.
	 */
	public Date getDataLimiteIncricao() {
		return dataLimiteIncricao;
	}

	public void setDataLimiteIncricao(Date dataLimiteIncricao) {
		this.dataLimiteIncricao = dataLimiteIncricao;
	}

	/**
	 * Informa se a prova est� ativa (removida).
	 * 
	 * @return <code>true</code> se a prova estiver ativa.
	 */
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Data do cadastro da prova seletiva.
	 * 	
	 * @return retorna a data em que a prova foi cadastrada.
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * Projeto de ensino para o qual o monitor ser� selecionado.
	 * 
	 * @return retorna o projeto de monitoria.
	 */
	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	/**
	 * Registro de entrada do operador que cadastrou a prova.
	 * 
	 * @return retorna o registro de entrada.
	 */
	public RegistroEntrada getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

	/**
	 * Descreve detalhes da prova. Local, hora e conte�do que ser� abordado.
	 * 
	 * @return retorna um texto com detalhes da prova.
	 */
	public String getInformacaoSelecao() {
		return informacaoSelecao;
	}

	public void setInformacaoSelecao(String informacaoSelecao) {
		this.informacaoSelecao = informacaoSelecao;
	}

	/**
	 * Retorna a lista de discentes ativos inscritos para sele��o.
	 * Ordenados por prioridade e ordem alfab�tica.
	 * 
	 * @return {@link Collection} de {@link InscricaoSelecaoMonitoria} com todos os discentes com inscri��o ativa.
	 */
	public Collection<InscricaoSelecaoMonitoria> getDiscentesInscritos() {		
		if (discentesInscritos != null) {
			for (Iterator<InscricaoSelecaoMonitoria> i = discentesInscritos.iterator(); i.hasNext();) {
				if (!i.next().isAtivo()) {
					i.remove();
				}
			}		
		}
		return discentesInscritos;
	}

	public void setDiscentesInscritos(Collection<InscricaoSelecaoMonitoria> discentesInscritos) {
		this.discentesInscritos = discentesInscritos;
	}

	/**
	 * Retorna a lista de discentes de monitoria com notas cadastradas e classifica��o
	 * dos discentes na prova.
	 * 
	 * @return retorna {@link Collection} de {@link DiscenteMonitoria} com resultado da prova.
	 */
	public Collection<DiscenteMonitoria> getResultadoSelecao() {
		if (resultadoSelecao != null) {
			for (Iterator<DiscenteMonitoria> i = resultadoSelecao.iterator(); i.hasNext();) {
				DiscenteMonitoria dm = i.next();
				if (dm != null && !dm.isAtivo()) {
					i.remove();
				}
			}		
		}
		return resultadoSelecao;
	}

	public void setResultadoSelecao(Collection<DiscenteMonitoria> resultadoSelecao) {
		this.resultadoSelecao = resultadoSelecao;
	}

	/**
	 * Informa a quantidade de bolsas remuneradas do projeto
	 * que ser�o reservadas para esta prova seletiva.
	 * 
	 * @return total de bolsas reservada para prova.
	 */
	public Integer getVagasRemuneradas() {
		return vagasRemuneradas;
	}

	public void setVagasRemuneradas(Integer vagasRemuneradas) {
		this.vagasRemuneradas = vagasRemuneradas;
	}

	/**
	 * Informa o total de bolsas n�o remuneradas do projeto que
	 * devem ser reservadas para esta prova seletiva.
	 * 
	 * @return retorna o total de vagas n�o remuneradas reservadas para prova.
	 */
	public Integer getVagasNaoRemuneradas() {
		return vagasNaoRemuneradas;
	}

	public void setVagasNaoRemuneradas(Integer vagasNaoRemuneradas) {
		this.vagasNaoRemuneradas = vagasNaoRemuneradas;
	}

	/**
	 * Lista de componentes obrigat�rios adicionados pelo coordenador para prova seletiva.
	 * 
	 * @return retorna {@link Collection} de {@link ProvaSelecaoComponenteCurricular} com lista de componentes obrigat�rios.
	 */
	public Collection<ProvaSelecaoComponenteCurricular> getComponentesObrigatorios() {
		return componentesObrigatorios;
	}

	public void setComponentesObrigatorios(
			Collection<ProvaSelecaoComponenteCurricular> componentesObrigatorios) {
		this.componentesObrigatorios = componentesObrigatorios;
	}

	/**
	 * Retorna situa��o da prova seletiva.
	 * 
	 * @return retorna {@link TipoSituacaoProvaSelecao}
	 */
	public TipoSituacaoProvaSelecao getSituacaoProva() {
		return situacaoProva;
	}

	public void setSituacaoProva(TipoSituacaoProvaSelecao situacaoProva) {
		this.situacaoProva = situacaoProva;
	}
	
	/**
	 * Valida dados da prova seletiva.
	 * 
	 * @return retorna uma {@link ListaMensagens} com mensagens de valida��o da prova.
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(vagasNaoRemuneradas, "Vagas N�o Remuneradas", lista);
		ValidatorUtil.validateRequired(vagasRemuneradas, "Vagas Remuneradas", lista);
		ValidatorUtil.validateRequired(dataLimiteIncricao, "Data Limite da Inscri��o na Sele��o", lista);
		ValidatorUtil.validateRequired(dataProva, "Data da Prova", lista);
		ValidatorUtil.validateRequired(titulo, "T�tulo da Prova", lista);
		ValidatorUtil.validateRequired(situacaoProva, "Situa��o da Prova", lista);
		ValidatorUtil.validateEmptyCollection("Lista de componentes vinculados � prova n�o pode ser vazia.", componentesObrigatorios, lista);		

		if ((dataLimiteIncricao != null) && (dataProva != null)) {
			ValidatorUtil.validaOrdemTemporalDatas(dataLimiteIncricao, dataProva, true, "Data da Prova deve ser depois da Data Limite da Inscri��o.", lista);
		}
		// ativo = false = prova removida
		if ((!ativo) && (!ValidatorUtil.isEmpty(discentesInscritos))) {
			lista.addErro("Prova seletiva n�o pode ser removida porque j� tem alunos inscritos.");
		}
		return lista;
	}

	/**
	 * Permite altera��o da prova seletiva pelo coordenador somente se a Pr�-Reitoria
	 * de Gradua��o n�o iniciou o processo de valida��o.
	 * 
	 * @return <code>true</code> se o projeto 
	 */
	public boolean isPermitidoAlterar() {
	    return (situacaoProva != null) && (situacaoProva.getId() == TipoSituacaoProvaSelecao.AGUARDANDO_INSCRICAO);
	}
	
	/**
	 * Verifica se todos os discentes da prova foram validados pela pr�-reitoria de gradua��o.
	 * 
	 * @return
	 */
	public boolean isValidacoesFinalizadas() {
	    for (DiscenteMonitoria d : resultadoSelecao) {
		if (d.isPassivelValidacao()) {
		    return false;
		}		
	    }
	    return true;	    
	}

	/**
	 * Verifica se existe algum discente na espera
	 * para assumir uma vaga de bolsista ou volunt�rio.
	 * Para o processamento correto do m�todo o campo
	 * 'resultadoSelecao' deve est� preenchido.
	 * 
	 * @return
	 */
	public boolean isPossuiDiscenteEmEspera() {
	    for (DiscenteMonitoria d : resultadoSelecao) {
		if ((d.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA) &&
			((d.isAguardandoConvocacao() && d.isVinculoValido()) || 
			(d.isAguardandoValidacao() && d.isVinculoValido())) 
			) {
		    return true;
		}		
	    }
	    return false;	    
	}
	
	/**
	 * Verifica se existe algum discente N�o Remunerado na espera
	 * para assumir uma vaga de bolsista ou volunt�rio.
	 * Para o processamento correto do m�todo o campo
	 * 'resultadoSelecao' deve est� preenchido.
	 * 
	 * @return
	 */
	public boolean isPossuiDiscenteEmEsperaNaoRemunerado() {
	    for (DiscenteMonitoria d : resultadoSelecao) {
		if ((d.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA || d.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO ) &&
			((d.isVinculoValido()) || 
			(d.isAguardandoValidacao() && d.isVinculoValido())) 
			) {
		    return true;
		}		
	    }
	    return false;	    
	}
	
	/**
	 * Verifica se existe algum discente na espera que n�o foi convocado anteriormente
	 * para assumir uma vaga de bolsista ou volunt�rio.
	 * Para o processamento correto do m�todo o campo
	 * 'resultadoSelecao' deve est� preenchido.
	 * 
	 * @return
	 */
	public boolean isPossuiDiscenteEmEsperaConvocado() {
	    for (DiscenteMonitoria d : resultadoSelecao) {
		if ((d.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA) && !(d.isDiscenteConvocado() &&
			((d.isAguardandoConvocacao() && d.isVinculoValido()) || 
			(d.isAguardandoValidacao() && d.isVinculoValido())) )
			) {
		    return true;
		}		
	    }
	    return false;	    
	}
	
	/**
	 * Verifica se existe algum discente N�o Remunerado na espera
	 * para assumir uma vaga de bolsista ou volunt�rio.
	 * Para o processamento correto do m�todo o campo
	 * 'resultadoSelecao' deve est� preenchido.
	 * 
	 * @return
	 */
	public boolean isExisteDiscenteEmEsperaNaoRemunerado() {
	    for (DiscenteMonitoria d : resultadoSelecao) {
		if ((d.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA || d.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO ) &&
			d.isVinculoValido()) {
		    return true;
		}		
	    }
	    return false;	    
	}
	
	/**
	 * Retorna o discente mais bem classificado que possui bolsa n�o remunerada. 
	 * @return
	 */
	public DiscenteMonitoria getMelhorNaoRemunerado(){
		Collection<DiscenteMonitoria> melhores = new ArrayList<DiscenteMonitoria>();	
		for (DiscenteMonitoria dm : resultadoSelecao) {
		    if (dm.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO ) {
		    	melhores.add(dm);
		    }
		}
		if (!melhores.isEmpty()) {
		    Collections.sort((List<DiscenteMonitoria>) melhores);		
		    return melhores.iterator().next();
		}else {
		    return null;
		}
	}
	
	/**
	 * Retorna o discente mais bem classificado em espera. 
	 * @return
	 */
	public DiscenteMonitoria getMelhorEmEspera(){
		Collection<DiscenteMonitoria> melhores = new ArrayList<DiscenteMonitoria>();	
		for (DiscenteMonitoria dm : resultadoSelecao) {
		    if (dm.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA) {
		    	melhores.add(dm);
		    }
		}
		if (!melhores.isEmpty()) {
		    Collections.sort((List<DiscenteMonitoria>) melhores);		
		    return melhores.iterator().next();
		}else {
		    return null;
		}
	}

	
	
	
	public int getTotalBolsasRemuneradasDisponiveisReserva() {
	    return totalBolsasRemuneradasDisponiveisReserva;
	}

	public void setTotalBolsasRemuneradasDisponiveisReserva(
		int totalBolsasRemuneradasDisponiveisReserva) {
	    this.totalBolsasRemuneradasDisponiveisReserva = totalBolsasRemuneradasDisponiveisReserva;
	}

	public int getTotalBolsasNaoRemuneradasDisponiveisReserva() {
	    return totalBolsasNaoRemuneradasDisponiveisReserva;
	}

	public void setTotalBolsasNaoRemuneradasDisponiveisReserva(
		int totalBolsasNaoRemuneradasDisponiveisReserva) {
	    this.totalBolsasNaoRemuneradasDisponiveisReserva = totalBolsasNaoRemuneradasDisponiveisReserva;
	}
	
	public boolean isProvaConcluida() {
	    return (situacaoProva != null) && getSituacaoProva().getId() == TipoSituacaoProvaSelecao.CONCLUIDA;
	}
	
	/**
	 * Retorna a lista de componentes curriculares que o discente precisa ter pago
	 * para inscri��o na prova seletiva.
	 * O discente teve ter pago todos os obrigat�rios(caso existam) ou qualquer um dos opcionais.	 * 
	 *  
	 */
	public Collection<ProvaSelecaoComponenteCurricular> getPreRequisitosInscricaoDiscente() {
		Collection<ProvaSelecaoComponenteCurricular> obrigatorios = new ArrayList<ProvaSelecaoComponenteCurricular>();
		Collection<ProvaSelecaoComponenteCurricular> opcionais = new ArrayList<ProvaSelecaoComponenteCurricular>();
		
		for (ProvaSelecaoComponenteCurricular pscc : componentesObrigatorios) {
			if (pscc.isObrigatorio()) {
				obrigatorios.add(pscc);
			}else {			
				opcionais.add(pscc);
			}
		}
		
		return obrigatorios.isEmpty() ? opcionais : obrigatorios;
	}
	
	/**
	 * Retorna Express�o de ids de Componentes Curriculares que s�o Pr�-Requisitos 
	 * para inscri��o do discente na sele��o.
	 * O discente teve ter pago todos os obrigat�rios(caso existam) ou qualquer um dos 
	 * opcionais(caso n�o existam obrigat�rios).
	 * 
	 * @return
	 */
	public String getExpressaoPreRequisitosInscricaoDiscente() {
		if( isEmpty(getPreRequisitosInscricaoDiscente()) )
			return null;
		StringBuilder result = new StringBuilder("( ");
		for(ProvaSelecaoComponenteCurricular pscc : getPreRequisitosInscricaoDiscente()) {
			result.append(pscc.getComponenteCurricularMonitoria().getDisciplina().getId() + " ");
			if (pscc.isObrigatorio()) {
				result.append("E ");
			}else {
				result.append("OU ");
			}
		}
		int idx = (result.lastIndexOf("OU ") > -1) ? result.lastIndexOf("OU ") : result.lastIndexOf("E ");		
		result.delete(idx,result.length());
		result.append(") ");
		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
}
