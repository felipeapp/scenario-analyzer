/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/02/2013
 * 
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * <p>MBean que implementa a busca padrão de atividades de extensão.</p>
 *
 * <p>Para usar esse MBean o caso de uso deve implementar a interface PesquisarProjetosExtensao</p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @version 1.0 - criação da classe.
 * @since 15/02/2013
 *
 * @see PesquisarProjetosExtensao
 */
@Component("buscaPadraoProjetosDeExtensaoMBean")
@Scope("request")
public class BuscaPadraoProjetosDeExtensaoMBean  extends SigaaAbstractController<Projeto>{

	/** Págia padrão de busca de projetos de extenção dos eventos existentes.  */
	public static final String PAGINA_BUSCA_PADRAO_PROJETOS_EXTENSAO = "/extensao/buscaPadraoProjetosExtensao.jsp";

	
	
	/** O Mbean que chamou a busca padrão de participantes.  
	 * Tem que implementar essa interface para poder usar esse caso de uso.
	 */
	private PesquisarProjetosExtensao mBeanChamador;
	
	
	/** O título da operação que está chamando a busca de participantes */
	private String operacao;
	
	/** A descrição da operação que está chamando a busca de participantes */
	private String descricaoOperacao;
	
	
	 /** Armazena atividades de extensão encontradas na busca. Esse MBean manipula atividades, mas passa para quem chamou o projeto da atividade. **/
    private List<AtividadeExtensao> atividadesBuscadas;
	
	///////////////////////// Campos do formulário de busca ////////////////////////////////////
	
    /** Armazena as ações gravadas que são ações com cadastro em andamento que 
     * ainda não foram submetidas para avaliação dos departamentos. **/
    private Collection<AtividadeExtensao> atividadesGravadas;    
    /** Locais de Realização */
    private DataModel locaisRealizacao = new ListDataModel();
    /** Atributo utilizado para a inserção de informação de Nome da Atividade em telas de buscas. **/
    private String buscaNomeAtividade;
    /** Atributo utilizado para a inserção de informação do edital em telas de buscas. **/
    private int buscaEdital;    
    /** Atributo utilizado para a inserção de informação do Tipo de atividade em telas de buscas. **/
    private Integer[] buscaTipoAtividade;    
    /** Atributo utilizado para a inserção de informação do Título em telas de buscas. **/
    private String buscaTitulo;
    /** Atributo utilizado para a inserção de informação da Área do CNPQ em telas de buscas. **/
    private int buscaAreaCNPq;
    /** Atributo utilizado para a inserção de informação da Unidade de atividade em telas de buscas. **/
    private int buscaUnidade;
    /** Atributo utilizado para a inserção de informação do Centro de atividade em telas de buscas. **/
    private int buscaCentro;
    /** Atributo utilizado para a inserção de informação da Área Temática Principal em telas de buscas. **/
    private int buscaAreaTematicaPrincipal;
    /** Atributo utilizado para a inserção de informação da Situação da Atividade em telas de buscas. **/
    private Integer[] buscaSituacaoAtividade;
    /** Atributo utilizado para a inserção de informação do Financiamento Interno em telas de buscas. **/
    private boolean buscaFinanciamentoInterno;
    /** Atributo utilizado para a inserção de informação do Financiamento Externo em telas de buscas. **/
    private boolean buscaFinanciamentoExterno;
    /** Atributo utilizado para a inserção de informação do Auto Financiamento em telas de buscas. **/
    private boolean buscaAutoFinanciamento;
    /** Atributo utilizado para a inserção de informação do Convennio FUNPEC em telas de buscas. **/
    private boolean buscaConvenioFunpec;
    /** Atributo utilizado para a inserção de informação do Financiamento Interno após aprovação do comitê em telas de busca */
    private boolean buscaRecebeuFinanciamentoInterno;
    /** Atributo utilizado para a inserção de informação do Ano em telas de buscas. **/
    private Integer buscaAno = CalendarUtils.getAnoAtual();
    /** Atributo utilizado para a inserção de informação da Data de início em telas de buscas. **/
    private Date buscaInicio;
    /** Atributo utilizado para a inserção de informação da Data de fim em telas de buscas. **/
    private Date buscaFim;
    /** Atributo utilizado para a inserção de informação da Data de início da conclusão em telas de buscas. **/
    private Date buscaInicioConclusao;
    /** Atributo utilizado para a inserção de informação da Data de fim da conclusão em telas de buscas. **/
    private Date buscaFimConclusao;
    /** Atributo utilizado para a inserção de informação da Data de início da execução em telas de buscas. **/
    private Date buscaInicioExecucao;
    /** Atributo utilizado para a inserção de informação da Data de fim da execução em telas de buscas. **/
    private Date buscaFimExecucao;
    /** Atributo utilizado para a inserção de informação do Registro Simplificado em telas de buscas. **/
    private int buscaRegistroSimplificado = 0;
    /** Atributo utilizado para a inserção de informação da Ação de Solicitação de renovação em telas de buscas. **/
    private int buscaAcaoSolicitacaoRenovacao = 0;
    /** Atributo utilizado para a inserção de informação do Projeto Associado em telas de buscas. **/
    private int buscaProjetoAssociado = 0;
    /** Atributo utilizado para a inserção de informação do Código em telas de buscas. **/
    private String buscaCodigo = null;
    
    
    /** Armazena um membro do projeto. **/
    private MembroProjeto membroEquipe = new MembroProjeto();

    
	 /** Atributo utilizado para a seleção de opção de busca(filtro) do Título em telas de busca. **/
    private boolean checkBuscaTitulo;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Projeto Associado em telas de busca. **/
    private boolean checkBuscaProjetoAssociado;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Edital em telas de busca. **/
    private boolean checkBuscaEdital;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Código em telas de busca. **/
    private boolean checkBuscaCodigo;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Tipo de Atividade em telas de busca. **/
    private boolean checkBuscaTipoAtividade;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Situação da Atividade em telas de busca. **/
    private boolean checkBuscaSituacaoAtividade;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Unidade Proponente em telas de busca. **/
    private boolean checkBuscaUnidadeProponente;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Centro em telas de busca. **/
    private boolean checkBuscaCentro;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Área CNPQ em telas de busca. **/
    private boolean checkBuscaAreaCNPq;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Área Temática Principal em telas de busca. **/
    private boolean checkBuscaAreaTematicaPrincipal;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Servidor em telas de busca. **/
    private boolean checkBuscaServidor;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Ano em telas de busca. **/
    private boolean checkBuscaAno;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Financiamento do Convenio em telas de busca. **/
    private boolean checkBuscaFinanciamentoConvenio;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Registro Simplificado em telas de busca. **/
    private boolean checkBuscaRegistroSimplificado;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Período em telas de busca. **/
    private boolean checkBuscaPeriodo;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Período de conlcusão em telas de busca. **/
    private boolean checkBuscaPeriodoConclusao;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Período de início em telas de busca. **/
    private boolean checkBuscaPeriodoInicio;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Ação de solicitação de Renovação em telas de busca. **/
    private boolean checkBuscaAcaoSolicitacaoRenovacao;    
	
	
	/////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 *  Inicia o caso de uso de busca projetos de extensão, chamado de outros Mbeans de outros casos de uso que utilizam a busca padrão.
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String iniciaBuscaSelecaoProjeto(PesquisarProjetosExtensao mBeanChamador, String operacao, String descricaoOperacao){
		this.mBeanChamador = mBeanChamador;
		this.operacao = operacao;
		this.descricaoOperacao = descricaoOperacao;
		return telaBuscaPadraoProjetosExtensao();
	}

	
	/**
	 *  <p>Realiza a busca de projetos de extensão existentes no sistema.</p>
	 *  
	 *  <p>Observação: Esse método busca foi retirado do MBean:  AtividadeExtensaoMbean.java, por isso um método tão grande e complicado. </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/buscaPadraoProjetosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws DAOException 
	 */
	public String buscarProjeto() throws DAOException{
		
		
		atividadesBuscadas = new ArrayList<AtividadeExtensao>();

		/* Analisando filtros selecionados */
		Integer sequencia = null;
		String titulo = null;
		Integer[] idTipoAtividade = new Integer[1];
		Integer idEdital = null;
		Integer[] idSituacaoAtividade = new Integer[0];
		Integer idUnidadeProponente = null;
		Integer idCentro = null;
		Integer idAreaCNPq = null;
		Integer idAreaTematicaPrincipal = null;
		Integer idServidor = null;
		Integer ano = null;
		Integer anoCodigo = null;
		Boolean financiamentoInterno = null;
		Boolean financiamentoExterno = null;
		Boolean autoFinanciamento = null;
		Boolean convenioFunpec = null;
		Boolean recebeuFinanciamentoInterno = null;
		Boolean registroSimplificado = null;
		Date inicio = null;
		Date fim = null;
		Date inicioConclusao = null;
		Date fimConclusao = null;
		Date inicioExecucao = null;
		Date fimExecucao = null;
		Boolean solicitcaoRenovacao = null;
		Boolean associado = null;

		ListaMensagens listaMensagens = new ListaMensagens();
		
		AtividadeExtensaoMBean atividadeExtensaoMbean =  new AtividadeExtensaoMBean();
		
		if (checkBuscaCodigo) {
		    	ValidatorUtil.validateRequired(buscaCodigo, "Código", listaMensagens);
		    	if (!ValidatorUtil.isEmpty(buscaCodigo)) {
		    	    if (!atividadeExtensaoMbean.isCodigoAcaoValido(buscaCodigo)) {
		    		listaMensagens.addWarning("O código da ação digitado esta em formato inválido.");
		    	    } else {
		    		idTipoAtividade[0] = (atividadeExtensaoMbean.tipoAtividadeByCodigo(buscaCodigo)).getId();
		    		anoCodigo = atividadeExtensaoMbean.anoByCodigo(buscaCodigo);
		    		String meio = atividadeExtensaoMbean.sequenciaByCodigo(buscaCodigo);
	
		    		if (meio.equalsIgnoreCase("xxx")) {
		    		    sequencia = 0;
		    		}else {
		    		    sequencia = Integer.parseInt(meio);
		    		}
		    	    }
		    	}
		}

		// Definição dos filtros e validações
		if (checkBuscaEdital) {
			idEdital = buscaEdital;
			if (idEdital == 0) {
			    listaMensagens.addErro("Edital: Campo obrigatório não informado.");
			}
		}
		if (checkBuscaTitulo) {
			titulo = buscaNomeAtividade;
			ValidatorUtil.validateRequired(titulo, "Título da Ação", listaMensagens);
		}
		if (checkBuscaTipoAtividade) {
			idTipoAtividade = buscaTipoAtividade;
			if(idTipoAtividade.length == 0){
				listaMensagens.addErro("Tipo da Ação: Campo obrigatório não informado.");
			}
		}
		if (checkBuscaSituacaoAtividade) {
			idSituacaoAtividade = buscaSituacaoAtividade;
			if (buscaSituacaoAtividade.length == 0) {
			    listaMensagens.addErro("Situação da Ação: Campo obrigatório não informado.");
			}
		}
		if (checkBuscaUnidadeProponente) {
			idUnidadeProponente = buscaUnidade;
			ValidatorUtil.validateRequiredId(idUnidadeProponente, "Unidade Proponente", listaMensagens);
		}
		if (checkBuscaAreaCNPq) {
			idAreaCNPq = buscaAreaCNPq;
			ValidatorUtil.validateRequiredId(idAreaCNPq, "Área do CNPq", listaMensagens);
		}
		if (checkBuscaAreaTematicaPrincipal) {
			idAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
			ValidatorUtil.validateRequiredId(idAreaTematicaPrincipal, "Área Temática", listaMensagens);
		}
		if (checkBuscaServidor) {
			idServidor = membroEquipe.getServidor().getId();
			ValidatorUtil.validateRequired(membroEquipe.getServidor(), "Servidor", listaMensagens);
		}
		if (checkBuscaAno) {
			ano = buscaAno;
			ValidatorUtil.validaInt(ano, "Ano", listaMensagens);
		}
		if (checkBuscaPeriodo) {
			inicio = buscaInicio;
			fim = buscaFim;
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(inicio), "Período de execução: Data Início", listaMensagens);
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(fim), "Período de execução: Data Fim", listaMensagens);
			if ((inicio != null) && (fim != null)) {
				ValidatorUtil.validaOrdemTemporalDatas(inicio, fim, true, "Data de início deve ser menor que a data fim", listaMensagens);
			}
		}
	
		if (checkBuscaPeriodoConclusao) {
			inicioConclusao = buscaInicioConclusao;
			fimConclusao = buscaFimConclusao;
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(inicioConclusao), "Período de conclusão: Data Início", listaMensagens);
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(fimConclusao), "Período de conclusão: Data Fim", listaMensagens);
			if ((inicioConclusao != null) && (fimConclusao != null)) {
				ValidatorUtil.validaOrdemTemporalDatas(inicioConclusao, fimConclusao, true,
						"Data de início deve ser menor que a data fim", listaMensagens);
			}
		}
	
		if (checkBuscaPeriodoInicio) {
			inicioExecucao = buscaInicioExecucao;
			fimExecucao = buscaFimExecucao;
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(inicioExecucao), "Período de início: Data Início", listaMensagens);
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(fimExecucao), "Período de início: Data Fim", listaMensagens);
			if ((inicioExecucao != null) && (fimExecucao != null)) {
				ValidatorUtil.validaOrdemTemporalDatas(inicioExecucao, fimExecucao, true,
						"Data de início deve ser menor que a data fim", listaMensagens);
			}
		}
	
		
		if (checkBuscaFinanciamentoConvenio) {
			financiamentoInterno = buscaFinanciamentoInterno;
			financiamentoExterno = buscaFinanciamentoExterno;
			autoFinanciamento = buscaAutoFinanciamento;
			convenioFunpec = buscaConvenioFunpec;
			recebeuFinanciamentoInterno = buscaRecebeuFinanciamentoInterno;
			if (!buscaFinanciamentoInterno && !buscaFinanciamentoExterno
					&& !buscaAutoFinanciamento && !buscaConvenioFunpec && !buscaRecebeuFinanciamentoInterno) {
				addMensagemErro("Financiamentos & Convênios: Campo obrigatório não informado.");
			}
		}
		if (checkBuscaRegistroSimplificado) {
			if (buscaRegistroSimplificado == 0) {
			    addMensagemErro("Tipo de Registro: Campo obrigatório não informado");
			}else {
			    registroSimplificado = buscaRegistroSimplificado == 1;
			}
		}
		if (checkBuscaCentro) {
			idCentro = buscaCentro;
			ValidatorUtil.validateRequiredId(idCentro, "Centro", listaMensagens);
		}
		
		if (checkBuscaAcaoSolicitacaoRenovacao) {
			if (buscaAcaoSolicitacaoRenovacao == 0) {
			    listaMensagens.addErro("Solicitação de Renovação: campo obrigatório não informado.");
			} else {
			    solicitcaoRenovacao = buscaAcaoSolicitacaoRenovacao == 1;
			}
		}
		
		if (checkBuscaProjetoAssociado) {
			if (buscaProjetoAssociado == 0) {
			    listaMensagens.addErro("Dimensão Acadêmica: campo obrigatório não informado.");
			} else {
			    associado = buscaProjetoAssociado == 1;
			}
		}
		
		if (!checkBuscaEdital && !checkBuscaTitulo && !checkBuscaAreaCNPq
				&& !checkBuscaTipoAtividade && !checkBuscaSituacaoAtividade
				&& !checkBuscaUnidadeProponente
				&& !checkBuscaAreaTematicaPrincipal && !checkBuscaServidor
				&& !checkBuscaAno && !checkBuscaFinanciamentoConvenio
				&& !checkBuscaRegistroSimplificado && !checkBuscaCentro
				&& !checkBuscaPeriodo && !checkBuscaCodigo 
				&& !checkBuscaPeriodoConclusao && !checkBuscaPeriodoInicio
				&& !checkBuscaAcaoSolicitacaoRenovacao && !checkBuscaProjetoAssociado) {
	
		    listaMensagens.addErro("Selecione uma opção para efetuar a busca por ações de extensão.");
		    if(listaMensagens != null){
		    	addMensagens(listaMensagens);
		    }
	
		} else {
	
			try {
				if (listaMensagens.isEmpty()) {
					AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
					PagingInformation paginas = null;
					
					atividadesBuscadas = (List<AtividadeExtensao>) dao.filter(idEdital, inicio,
								fim, inicioConclusao, fimConclusao, inicioExecucao, fimExecucao, 
								titulo, idTipoAtividade,
								idSituacaoAtividade, idUnidadeProponente,
								idCentro, idAreaCNPq, idAreaTematicaPrincipal,
								null, idServidor, ano, financiamentoInterno,
								financiamentoExterno, autoFinanciamento,
								convenioFunpec, recebeuFinanciamentoInterno, paginas,
								false, registroSimplificado,
								sequencia, anoCodigo, solicitcaoRenovacao, associado, isExtensao());
	
						if (atividadesBuscadas == null || atividadesBuscadas.isEmpty())
							addMensagemWarning("Nenhuma atividade localizada!");
					
				} else {
					addMensagens(listaMensagens);
				}
			} catch (LimiteResultadosException lre) {
				addMensagemErro(lre.getMessage());
			}
		}
		
		return telaBuscaPadraoProjetosExtensao();
		
	}
	
	
	/**
	 * Chama o MBean do caso de uso para realizar o operação de cancelar da tela de busca.
	 *
	 * @return
	 */
	public String cancelarBuscaProjetoExtensao(){
		return mBeanChamador.cancelarBuscaProjetExtensao();
	}
	
	
	/**
	 * <p>Chamado quando o usuário seleciona um participante da busca padrão.</p>
	 * 
	 * <p>Continua o fluxo para o caso de uso que chamou a busca, passando só o participante com o id, o caso 
	 * de uso chamador deve popular os dados necessários.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws ArqException 
	 */
	public String selecionouProjeto() throws ArqException{
		mBeanChamador.setProjetoExtensao(new Projeto(getParameterInt("idProjetoSelecionado"))); 
		return mBeanChamador.selecionouProjetExtensao();
	}
	
	
	

	
	/**
	 * Redireciona para a tela de busca padrão de projeto
	 * @return
	 *  
	 *  <br />
	 * Método não chamado por JSP(s): 
	 * 
	 *
	 */
	private String telaBuscaPadraoProjetosExtensao() {
		return forward(PAGINA_BUSCA_PADRAO_PROJETOS_EXTENSAO);
	}
	
	

	/// sets e gets //
	
	public PesquisarProjetosExtensao getmBeanChamador() {return mBeanChamador;}
	public void setmBeanChamador(PesquisarProjetosExtensao mBeanChamador) {this.mBeanChamador = mBeanChamador;}
	public String getOperacao() {return operacao;}
	public void setOperacao(String operacao) {this.operacao = operacao;}
	public String getDescricaoOperacao() {return descricaoOperacao;}
	public void setDescricaoOperacao(String descricaoOperacao) {	this.descricaoOperacao = descricaoOperacao;}

	
	public int getQuantidadeAtividadesBuscadas(){
		if( atividadesBuscadas == null)
			return 0;
		else
			return atividadesBuscadas.size();
	}

	public List<AtividadeExtensao> getAtividadesBuscadas() {
		return atividadesBuscadas;
	}


	public void setAtividadesBuscadas(List<AtividadeExtensao> atividadesBuscadas) {
		this.atividadesBuscadas = atividadesBuscadas;
	}


	public Collection<AtividadeExtensao> getAtividadesGravadas() {
		return atividadesGravadas;
	}


	public void setAtividadesGravadas(
			Collection<AtividadeExtensao> atividadesGravadas) {
		this.atividadesGravadas = atividadesGravadas;
	}


	public DataModel getLocaisRealizacao() {
		return locaisRealizacao;
	}


	public void setLocaisRealizacao(DataModel locaisRealizacao) {
		this.locaisRealizacao = locaisRealizacao;
	}


	public String getBuscaNomeAtividade() {
		return buscaNomeAtividade;
	}


	public void setBuscaNomeAtividade(String buscaNomeAtividade) {
		this.buscaNomeAtividade = buscaNomeAtividade;
	}


	public int getBuscaEdital() {
		return buscaEdital;
	}


	public void setBuscaEdital(int buscaEdital) {
		this.buscaEdital = buscaEdital;
	}


	public Integer[] getBuscaTipoAtividade() {
		return buscaTipoAtividade;
	}


	public void setBuscaTipoAtividade(Integer[] buscaTipoAtividade) {
		this.buscaTipoAtividade = buscaTipoAtividade;
	}


	public String getBuscaTitulo() {
		return buscaTitulo;
	}


	public void setBuscaTitulo(String buscaTitulo) {
		this.buscaTitulo = buscaTitulo;
	}


	public int getBuscaAreaCNPq() {
		return buscaAreaCNPq;
	}


	public void setBuscaAreaCNPq(int buscaAreaCNPq) {
		this.buscaAreaCNPq = buscaAreaCNPq;
	}


	public int getBuscaUnidade() {
		return buscaUnidade;
	}


	public void setBuscaUnidade(int buscaUnidade) {
		this.buscaUnidade = buscaUnidade;
	}


	public int getBuscaCentro() {
		return buscaCentro;
	}


	public void setBuscaCentro(int buscaCentro) {
		this.buscaCentro = buscaCentro;
	}


	public int getBuscaAreaTematicaPrincipal() {
		return buscaAreaTematicaPrincipal;
	}


	public void setBuscaAreaTematicaPrincipal(int buscaAreaTematicaPrincipal) {
		this.buscaAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
	}


	public Integer[] getBuscaSituacaoAtividade() {
		return buscaSituacaoAtividade;
	}


	public void setBuscaSituacaoAtividade(Integer[] buscaSituacaoAtividade) {
		this.buscaSituacaoAtividade = buscaSituacaoAtividade;
	}


	public boolean isBuscaFinanciamentoInterno() {
		return buscaFinanciamentoInterno;
	}


	public void setBuscaFinanciamentoInterno(boolean buscaFinanciamentoInterno) {
		this.buscaFinanciamentoInterno = buscaFinanciamentoInterno;
	}


	public boolean isBuscaFinanciamentoExterno() {
		return buscaFinanciamentoExterno;
	}


	public void setBuscaFinanciamentoExterno(boolean buscaFinanciamentoExterno) {
		this.buscaFinanciamentoExterno = buscaFinanciamentoExterno;
	}


	public boolean isBuscaAutoFinanciamento() {
		return buscaAutoFinanciamento;
	}


	public void setBuscaAutoFinanciamento(boolean buscaAutoFinanciamento) {
		this.buscaAutoFinanciamento = buscaAutoFinanciamento;
	}


	public boolean isBuscaConvenioFunpec() {
		return buscaConvenioFunpec;
	}


	public void setBuscaConvenioFunpec(boolean buscaConvenioFunpec) {
		this.buscaConvenioFunpec = buscaConvenioFunpec;
	}


	public boolean isBuscaRecebeuFinanciamentoInterno() {
		return buscaRecebeuFinanciamentoInterno;
	}


	public void setBuscaRecebeuFinanciamentoInterno(
			boolean buscaRecebeuFinanciamentoInterno) {
		this.buscaRecebeuFinanciamentoInterno = buscaRecebeuFinanciamentoInterno;
	}


	public Integer getBuscaAno() {
		return buscaAno;
	}


	public void setBuscaAno(Integer buscaAno) {
		this.buscaAno = buscaAno;
	}


	public Date getBuscaInicio() {
		return buscaInicio;
	}


	public void setBuscaInicio(Date buscaInicio) {
		this.buscaInicio = buscaInicio;
	}


	public Date getBuscaFim() {
		return buscaFim;
	}


	public void setBuscaFim(Date buscaFim) {
		this.buscaFim = buscaFim;
	}


	public Date getBuscaInicioConclusao() {
		return buscaInicioConclusao;
	}


	public void setBuscaInicioConclusao(Date buscaInicioConclusao) {
		this.buscaInicioConclusao = buscaInicioConclusao;
	}


	public Date getBuscaFimConclusao() {
		return buscaFimConclusao;
	}


	public void setBuscaFimConclusao(Date buscaFimConclusao) {
		this.buscaFimConclusao = buscaFimConclusao;
	}


	public Date getBuscaInicioExecucao() {
		return buscaInicioExecucao;
	}


	public void setBuscaInicioExecucao(Date buscaInicioExecucao) {
		this.buscaInicioExecucao = buscaInicioExecucao;
	}


	public Date getBuscaFimExecucao() {
		return buscaFimExecucao;
	}


	public void setBuscaFimExecucao(Date buscaFimExecucao) {
		this.buscaFimExecucao = buscaFimExecucao;
	}


	public int getBuscaRegistroSimplificado() {
		return buscaRegistroSimplificado;
	}


	public void setBuscaRegistroSimplificado(int buscaRegistroSimplificado) {
		this.buscaRegistroSimplificado = buscaRegistroSimplificado;
	}


	public int getBuscaAcaoSolicitacaoRenovacao() {
		return buscaAcaoSolicitacaoRenovacao;
	}


	public void setBuscaAcaoSolicitacaoRenovacao(int buscaAcaoSolicitacaoRenovacao) {
		this.buscaAcaoSolicitacaoRenovacao = buscaAcaoSolicitacaoRenovacao;
	}


	public int getBuscaProjetoAssociado() {
		return buscaProjetoAssociado;
	}


	public void setBuscaProjetoAssociado(int buscaProjetoAssociado) {
		this.buscaProjetoAssociado = buscaProjetoAssociado;
	}


	public String getBuscaCodigo() {
		return buscaCodigo;
	}


	public void setBuscaCodigo(String buscaCodigo) {
		this.buscaCodigo = buscaCodigo;
	}


	public MembroProjeto getMembroEquipe() {
		return membroEquipe;
	}


	public void setMembroEquipe(MembroProjeto membroEquipe) {
		this.membroEquipe = membroEquipe;
	}


	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}


	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}


	public boolean isCheckBuscaProjetoAssociado() {
		return checkBuscaProjetoAssociado;
	}


	public void setCheckBuscaProjetoAssociado(boolean checkBuscaProjetoAssociado) {
		this.checkBuscaProjetoAssociado = checkBuscaProjetoAssociado;
	}


	public boolean isCheckBuscaEdital() {
		return checkBuscaEdital;
	}


	public void setCheckBuscaEdital(boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}


	public boolean isCheckBuscaCodigo() {
		return checkBuscaCodigo;
	}


	public void setCheckBuscaCodigo(boolean checkBuscaCodigo) {
		this.checkBuscaCodigo = checkBuscaCodigo;
	}


	public boolean isCheckBuscaTipoAtividade() {
		return checkBuscaTipoAtividade;
	}


	public void setCheckBuscaTipoAtividade(boolean checkBuscaTipoAtividade) {
		this.checkBuscaTipoAtividade = checkBuscaTipoAtividade;
	}


	public boolean isCheckBuscaSituacaoAtividade() {
		return checkBuscaSituacaoAtividade;
	}


	public void setCheckBuscaSituacaoAtividade(boolean checkBuscaSituacaoAtividade) {
		this.checkBuscaSituacaoAtividade = checkBuscaSituacaoAtividade;
	}


	public boolean isCheckBuscaUnidadeProponente() {
		return checkBuscaUnidadeProponente;
	}


	public void setCheckBuscaUnidadeProponente(boolean checkBuscaUnidadeProponente) {
		this.checkBuscaUnidadeProponente = checkBuscaUnidadeProponente;
	}


	public boolean isCheckBuscaCentro() {
		return checkBuscaCentro;
	}


	public void setCheckBuscaCentro(boolean checkBuscaCentro) {
		this.checkBuscaCentro = checkBuscaCentro;
	}


	public boolean isCheckBuscaAreaCNPq() {
		return checkBuscaAreaCNPq;
	}


	public void setCheckBuscaAreaCNPq(boolean checkBuscaAreaCNPq) {
		this.checkBuscaAreaCNPq = checkBuscaAreaCNPq;
	}


	public boolean isCheckBuscaAreaTematicaPrincipal() {
		return checkBuscaAreaTematicaPrincipal;
	}


	public void setCheckBuscaAreaTematicaPrincipal(
			boolean checkBuscaAreaTematicaPrincipal) {
		this.checkBuscaAreaTematicaPrincipal = checkBuscaAreaTematicaPrincipal;
	}


	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}


	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}


	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}


	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}


	public boolean isCheckBuscaFinanciamentoConvenio() {
		return checkBuscaFinanciamentoConvenio;
	}


	public void setCheckBuscaFinanciamentoConvenio(
			boolean checkBuscaFinanciamentoConvenio) {
		this.checkBuscaFinanciamentoConvenio = checkBuscaFinanciamentoConvenio;
	}


	public boolean isCheckBuscaRegistroSimplificado() {
		return checkBuscaRegistroSimplificado;
	}


	public void setCheckBuscaRegistroSimplificado(
			boolean checkBuscaRegistroSimplificado) {
		this.checkBuscaRegistroSimplificado = checkBuscaRegistroSimplificado;
	}


	public boolean isCheckBuscaPeriodo() {
		return checkBuscaPeriodo;
	}


	public void setCheckBuscaPeriodo(boolean checkBuscaPeriodo) {
		this.checkBuscaPeriodo = checkBuscaPeriodo;
	}


	public boolean isCheckBuscaPeriodoConclusao() {
		return checkBuscaPeriodoConclusao;
	}


	public void setCheckBuscaPeriodoConclusao(boolean checkBuscaPeriodoConclusao) {
		this.checkBuscaPeriodoConclusao = checkBuscaPeriodoConclusao;
	}


	public boolean isCheckBuscaPeriodoInicio() {
		return checkBuscaPeriodoInicio;
	}


	public void setCheckBuscaPeriodoInicio(boolean checkBuscaPeriodoInicio) {
		this.checkBuscaPeriodoInicio = checkBuscaPeriodoInicio;
	}


	public boolean isCheckBuscaAcaoSolicitacaoRenovacao() {
		return checkBuscaAcaoSolicitacaoRenovacao;
	}


	public void setCheckBuscaAcaoSolicitacaoRenovacao(
			boolean checkBuscaAcaoSolicitacaoRenovacao) {
		this.checkBuscaAcaoSolicitacaoRenovacao = checkBuscaAcaoSolicitacaoRenovacao;
	}

	
	
}
