/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/04/2008
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/*******************************************************************************
 * MBean utilizado na busca de ações acadêmicas associadas em diversas áreas
 * do sub-sistema de projetos.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope(value = "session")
@Component(value = "buscaAcaoAssociada")
public class BuscaAcaoAssociadaMBean extends SigaaAbstractController<Projeto> {

		
	//Atributos usados para a inserção de informações em telas de buscas.
	/** Atributo utilizado para inserção do Título em telas de buscas */
	private String buscaTitulo;
	/** Atributo utilizado para inserção do ano em telas de buscas */
	private Integer buscaAno = CalendarUtils.getAnoAtual();
	/** Atributo utilizado para inserção da Data de início em telas de buscas */
	private Date buscaInicio;	
	/** Atributo utilizado para inserção da Data de fim  em telas de buscas */
	private Date buscaFim;
	/** Atributo utilizado para inserção do Edital em telas de buscas */
	private int buscaEdital;
	/** Atributo utilizado para inserção da Área do CNPQ em telas de buscas */
	private int buscaAreaCNPq;	
	/** Atributo utilizado para inserção da Unidade em telas de buscas */
	private int buscaUnidade;	
	/** Atributo utilizado para inserção da Situação em telas de buscas */
	private int buscaSituacao;
	/** Atributo utilizado para inserção do Centro em telas de buscas */
	private int buscaCentro;	
	/** Atributo utilizado para inserção da Ação de solicitação de renovação em telas de buscas */
	private int buscaAcaoSolicitacaoRenovacao;	
	/** Atributo utilizado para inserção da busca de Financiamento Interno em telas de buscas */
	private boolean buscaFinanciamentoInterno;	
	/** Atributo utilizado para inserção da busca de Financiamento Externo em telas de buscas */
	private boolean buscaFinanciamentoExterno;	
	/** Atributo utilizado para inserção da busca de Auto Financiamento em telas de buscas */
	private boolean buscaAutoFinanciamento;
	/** Atributo utilizado para inserção da busca da Dimensão Acadêmica: Ensino, em telas de buscas */
	private boolean buscaEnsino;
	/** Atributo utilizado para inserção da busca da Dimensão Acadêmica: Extensão, em telas de buscas */
	private boolean buscaExtensao;
	/** Atributo utilizado para inserção da busca da Dimensão Acadêmica: Pesquisa, em telas de buscas */
	private boolean buscaPesquisa;
	
	/** Atributo utilizado para representar o membro do projeto */
	private MembroProjeto membroProjeto = new MembroProjeto();    
	
	//Atributos usados para a seleção de opções de busca em telas de busca.    
	/** Atributo utilizado para seleção de opções de Titulo em telas de busca */
	private boolean checkBuscaTitulo;	
	/** Atributo utilizado para seleção de opções de Ano em telas de busca */
	private boolean checkBuscaAno;	
	/** Atributo utilizado para seleção de opções de Período em telas de busca */
	private boolean checkBuscaPeriodo;	
	/** Atributo utilizado para seleção de opções do Edital em telas de busca */
	private boolean checkBuscaEdital;	
	/** Atributo utilizado para seleção de opções da AreaCNPq em telas de busca */
	private boolean checkBuscaAreaCNPq;	
	/** Atributo utilizado para seleção de opções da Unidade em telas de busca */
	private boolean checkBuscaUnidade;	
	/** Atributo utilizado para seleção de opções do Servidor em telas de busca */
	private boolean checkBuscaServidor;	
	/** Atributo utilizado para seleção de opções da Situacao em telas de busca */
	private boolean checkBuscaSituacao;	
	/** Atributo utilizado para seleção de opções da AcaoSolicitacaoRenovacao em telas de busca */
	private boolean checkBuscaAcaoSolicitacaoRenovacao;	
	/** Atributo utilizado para seleção de opções do FinanciamentoConvenio em telas de busca */
	private boolean checkBuscaFinanciamentoConvenio;
	/** Atributo utilizado para seleção de opções do Centro em telas de busca */
	private boolean checkBuscaCentro;
	/** Atributo utilziado para seleção de opções de Dimensão Acadêmica */
	private boolean checkBuscaDimensaoAcademica;
	
	
	/**
	 * Inicia procedimento de busca de ações acadêmicas associadas.
	 * 
	 * <br>
	 * Chamado por:
	 * <ul>
	 * 	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * 	<li>sigaa.war/projetos/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() {		
		return forward("/projetos/ProjetoBase/lista.jsf");
	}
	

	/**
	 * Filtra Ações Acadêmicas Associadas
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSPs.</li>
	 * </ul>
	 * 
	 * @throws SegurancaException
	 */	
	public void localizar() throws SegurancaException {
		try {

			Integer idEdital = null;
			Date inicio = null;
			Date fim = null;
			Integer ano = null;
			String titulo = null;
			Integer[] idsSituacoesProjeto = null;
			Integer idServidor = null;
			Integer idAreaCNPq = null;
			Integer idCentro = null;
			Integer idUnidadeProponente = null;
			Boolean financiamentoInterno = null;
			Boolean financiamentoExterno = null;
			Boolean autoFinanciamento = null;
			Boolean solicitcaoRenovacao = null;
			Boolean ensino = null;
			Boolean extensao = null;
			Boolean pesquisa = null;
			
			ListaMensagens lista = new ListaMensagens();			
			resultadosBusca = new ArrayList<Projeto>();


			if (checkBuscaEdital) {
			    idEdital = buscaEdital;
			    ValidatorUtil.validateRequiredId(buscaEdital, "Edital", lista);
			}
			
			if (checkBuscaPeriodo) {
			    inicio = buscaInicio;
			    fim = buscaFim;
			    ValidatorUtil.validateRequired(buscaInicio, "Data Início", lista);
			    ValidatorUtil.validateRequired(buscaFim, "Data Fim", lista);
			    ValidatorUtil.validaInicioFim(inicio, fim, "Período", lista);
			}
			
			if (checkBuscaAno) {
			    ano = buscaAno;
			    ValidatorUtil.validateRequired(ano, "Ano", lista);
			    ValidatorUtil.validaInt(ano, "Ano", lista);
			}
			
			if (checkBuscaTitulo) {
			    titulo = buscaTitulo;
			    ValidatorUtil.validateRequired(titulo, "Título", lista);
			}
			
			if (checkBuscaServidor) {
			    idServidor = membroProjeto.getServidor().getId();
			    ValidatorUtil.validateRequired(membroProjeto.getServidor(), "Servidor(A)", lista);
			}
			
			if (checkBuscaAreaCNPq) {
			    idAreaCNPq = buscaAreaCNPq;
			    ValidatorUtil.validateRequiredId(buscaAreaCNPq, "Área do CNPq", lista);
			}

			if (checkBuscaUnidade) {
			    idUnidadeProponente = buscaUnidade;
			    ValidatorUtil.validateRequiredId(buscaUnidade, "Unidade Proponente", lista);
			}
			
			if (checkBuscaAcaoSolicitacaoRenovacao) {
				ValidatorUtil.validateRequiredId(buscaAcaoSolicitacaoRenovacao, "Solicitação de Renovação", lista);
				solicitcaoRenovacao = (buscaAcaoSolicitacaoRenovacao == 1 ? true : false);
			}
			
			if (checkBuscaFinanciamentoConvenio) {
			    financiamentoInterno = buscaFinanciamentoInterno;
			    financiamentoExterno = buscaFinanciamentoExterno;
			    autoFinanciamento = buscaAutoFinanciamento;
			    if (!financiamentoInterno && !financiamentoExterno && !autoFinanciamento) {
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Financiamentos & Convênios");
			    }
			}

			if (checkBuscaSituacao) {
			    idsSituacoesProjeto = new Integer[1];
			    idsSituacoesProjeto[0] = buscaSituacao;
			    if(buscaSituacao == 0){
			    	lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Situação");
			    }
			}
			
			if (checkBuscaCentro) {
			    idCentro = buscaCentro;
			    ValidatorUtil.validateRequired(buscaCentro, "Centro", lista);
			}
			
			if (checkBuscaDimensaoAcademica){
				ensino = buscaEnsino;
				extensao = buscaExtensao;
				pesquisa = buscaPesquisa;
				if (!ensino && !extensao && !pesquisa){
					lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Dimensão Acadêmica");
				}
			}
			
			if (!checkBuscaEdital && !checkBuscaPeriodo && !checkBuscaAno &&
				!checkBuscaTitulo && !checkBuscaServidor && !checkBuscaAreaCNPq &&
				!checkBuscaUnidade && !checkBuscaAcaoSolicitacaoRenovacao &&
				!checkBuscaFinanciamentoConvenio && !checkBuscaSituacao &&
				!checkBuscaCentro && !checkBuscaDimensaoAcademica) {
			    lista.addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			}
			
			if (lista.isEmpty()) {
			    ProjetoDao dao = getDAO(ProjetoDao.class);			
			    resultadosBusca = dao.filter(
				    idEdital, inicio, fim, ano, titulo, idsSituacoesProjeto, 
				    idUnidadeProponente, idCentro, idAreaCNPq, idServidor, 
				    financiamentoInterno, financiamentoExterno, autoFinanciamento, 
				    solicitcaoRenovacao, ensino, extensao, pesquisa);
			    
			    if(resultadosBusca.isEmpty()){
			    	lista.addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			    	addMensagens(lista);
			    }
			}else {
			    addMensagens(lista);
			}

		} catch (LimiteResultadosException e) {
		    addMensagemWarning(e.getMessage());
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	}

	public String getBuscaTitulo() {
	    return buscaTitulo;
	}

	public void setBuscaTitulo(String buscaTitulo) {
	    this.buscaTitulo = buscaTitulo;
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

	public int getBuscaEdital() {
	    return buscaEdital;
	}

	public void setBuscaEdital(int buscaEdital) {
	    this.buscaEdital = buscaEdital;
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



	public MembroProjeto getMembroProjeto() {
	    return membroProjeto;
	}



	public void setMembroProjeto(MembroProjeto membroProjeto) {
	    this.membroProjeto = membroProjeto;
	}



	public int getBuscaSituacao() {
	    return buscaSituacao;
	}



	public void setBuscaSituacao(int buscaSituacao) {
	    this.buscaSituacao = buscaSituacao;
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



	public boolean isCheckBuscaTitulo() {
	    return checkBuscaTitulo;
	}



	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
	    this.checkBuscaTitulo = checkBuscaTitulo;
	}



	public boolean isCheckBuscaAno() {
	    return checkBuscaAno;
	}



	public void setCheckBuscaAno(boolean checkBuscaAno) {
	    this.checkBuscaAno = checkBuscaAno;
	}



	public boolean isCheckBuscaPeriodo() {
	    return checkBuscaPeriodo;
	}



	public void setCheckBuscaPeriodo(boolean checkBuscaPeriodo) {
	    this.checkBuscaPeriodo = checkBuscaPeriodo;
	}



	public boolean isCheckBuscaEdital() {
	    return checkBuscaEdital;
	}



	public void setCheckBuscaEdital(boolean checkBuscaEdital) {
	    this.checkBuscaEdital = checkBuscaEdital;
	}



	public boolean isCheckBuscaAreaCNPq() {
	    return checkBuscaAreaCNPq;
	}



	public void setCheckBuscaAreaCNPq(boolean checkBuscaAreaCNPq) {
	    this.checkBuscaAreaCNPq = checkBuscaAreaCNPq;
	}



	public boolean isCheckBuscaUnidade() {
	    return checkBuscaUnidade;
	}



	public void setCheckBuscaUnidade(boolean checkBuscaUnidade) {
	    this.checkBuscaUnidade = checkBuscaUnidade;
	}



	public boolean isCheckBuscaServidor() {
	    return checkBuscaServidor;
	}



	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
	    this.checkBuscaServidor = checkBuscaServidor;
	}



	public boolean isCheckBuscaSituacao() {
	    return checkBuscaSituacao;
	}



	public void setCheckBuscaSituacao(boolean checkBuscaSituacao) {
	    this.checkBuscaSituacao = checkBuscaSituacao;
	}



	public boolean isCheckBuscaAcaoSolicitacaoRenovacao() {
	    return checkBuscaAcaoSolicitacaoRenovacao;
	}



	public void setCheckBuscaAcaoSolicitacaoRenovacao(
		boolean checkBuscaAcaoSolicitacaoRenovacao) {
	    this.checkBuscaAcaoSolicitacaoRenovacao = checkBuscaAcaoSolicitacaoRenovacao;
	}



	public boolean isCheckBuscaFinanciamentoConvenio() {
	    return checkBuscaFinanciamentoConvenio;
	}



	public void setCheckBuscaFinanciamentoConvenio(
		boolean checkBuscaFinanciamentoConvenio) {
	    this.checkBuscaFinanciamentoConvenio = checkBuscaFinanciamentoConvenio;
	}



	public int getBuscaAcaoSolicitacaoRenovacao() {
	    return buscaAcaoSolicitacaoRenovacao;
	}



	public void setBuscaAcaoSolicitacaoRenovacao(
		int buscaAcaoSolicitacaoRenovacao) {
	    this.buscaAcaoSolicitacaoRenovacao = buscaAcaoSolicitacaoRenovacao;
	}



	public boolean isCheckBuscaCentro() {
	    return checkBuscaCentro;
	}



	public void setCheckBuscaCentro(boolean checkBuscaCentro) {
	    this.checkBuscaCentro = checkBuscaCentro;
	}


	public boolean isBuscaEnsino() {
		return buscaEnsino;
	}


	public void setBuscaEnsino(boolean buscaEnsino) {
		this.buscaEnsino = buscaEnsino;
	}


	public boolean isBuscaExtensao() {
		return buscaExtensao;
	}


	public void setBuscaExtensao(boolean buscaExtensao) {
		this.buscaExtensao = buscaExtensao;
	}


	public boolean isBuscaPesquisa() {
		return buscaPesquisa;
	}


	public void setBuscaPesquisa(boolean buscaPesquisa) {
		this.buscaPesquisa = buscaPesquisa;
	}


	public boolean isCheckBuscaDimensaoAcademica() {
		return checkBuscaDimensaoAcademica;
	}


	public void setCheckBuscaDimensaoAcademica(boolean checkBuscaDimensaoAcademica) {
		this.checkBuscaDimensaoAcademica = checkBuscaDimensaoAcademica;
	}

}
