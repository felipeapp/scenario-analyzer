/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/06/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;

/**
 * Managed bean para Relatório de Membros da Equipe de Ações de Extensão
 *
 * @author Daniel
 *
 */
@Scope("session")
@Component("relatorioEquipeExtensao")
public class RelatorioEquipeAcaoExtensaoMBean extends SigaaAbstractController<AtividadeExtensao> {

	//Constante contendo endereço de uma página
	private final String CONTEXTO = "/extensao/Relatorios/";
	//Constante contendo endereço de uma página
	private final String RELATORIO = "form_equipe_acao_extensao.jsp";
	//Constante contendo endereço de uma página
	private final String FORM_ESTATISTICAS = "form_dados_publico_area.jsp";
	//Constante contendo endereço de uma página
	private final String RELATORIO_ESTATISTICAS = "relatorio_dados_publico_area.jsp";
	
	private List<Map<String, Object>> listaEstatisticas;
	
	//Usado para armazenar informação inserida em tela de busca.
	private Integer buscaSituacaoAtividade;
	//Usado para armazenar informação inserida em tela de busca.
	private Integer buscaTipoAtividade;
	//Usado para armazenar informação inserida em tela de busca.
	private Integer buscaTipoServidor;
	//Usado para armazenar informação inserida em tela de busca.
	private Integer buscaFuncaoMembro;
	//Usado para armazenar informação inserida em tela de busca.
	private String labelSituacaoAtividade;
	//Usado para armazenar informação inserida em tela de busca.
	private String labelTipoAtividade;
	//Usado para armazenar informação inserida em tela de busca.
	private String labelTipoServidor;
	//Usado para armazenar informação inserida em tela de busca.
	private Integer ano;
	//Usado para armazenar informação inserida em tela de busca.
	private Date inicio;
	//Usado para armazenar informação inserida em tela de busca.
	private Date fim;

	//Utilizado para informar se o usuário deseja efetuar uma busca por determinado critério. 
	private boolean checkBuscaTipoAcao;
	//Utilizado para informar se o usuário deseja efetuar uma busca por determinado critério.
	private boolean checkBuscaSituacaoAcao;
	//Utilizado para informar se o usuário deseja efetuar uma busca por determinado critério.
	private boolean checkBuscaTipoServidor;
	//Utilizado para informar se o usuário deseja efetuar uma busca por determinado critério.
	private boolean checkBuscaFuncaoMembro;
	private String formato = "pdf";


	public RelatorioEquipeAcaoExtensaoMBean() {	}

	/**
	 * Leva para a tela de gerar relatório de membros da equipe de ação por Centro 
	 *  
	 * 
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * @return 
	 * @throws SegurancaException
	 * 
	 */
	public String carregarInformacoes() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		this.ano = CalendarUtils.getAnoAtual();
		this.buscaFuncaoMembro = null;
		this.buscaSituacaoAtividade = null;
		this.buscaTipoAtividade = null;
		this.buscaTipoServidor = null;
		this.checkBuscaFuncaoMembro = false;
		this.checkBuscaSituacaoAcao = false;
		this.checkBuscaTipoAcao = false;
		this.checkBuscaTipoServidor = false;
		return forward(CONTEXTO + RELATORIO);
	}
	
	/**
	 * Leva para a tela de gerar relatório com a totalização de público e membros da equipe em ações
	 * de extensão agrupados por Área Temática
	 * 
	 * sigaa.war/extensao/menu.jsp
	 * 
	 * @return 
	 * @throws SegurancaException
	 * 
	 */
	public String carregarInformacoesEstatisticas() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		this.ano = CalendarUtils.getAnoAtual();
		this.buscaTipoAtividade = null;
		this.buscaSituacaoAtividade = null;
		return forward(CONTEXTO + FORM_ESTATISTICAS);
	}
		
	/**
	 * Gera o relatório de acordo com os critérios selecionados
	 * 
	 * sigaa.war/extensao/Relatorios/form_equipe_acao_extensao.jsp
	 * 
	 * @return 
	 * @throws SegurancaException
	 * 
	 */
	public String gerarRelatorio() throws SegurancaException {

		this.validarAno();
		if (!isCheckBuscaFuncaoMembro()) {
			addMensagemErro("O campo Função do Membro é obrigatório");
		}
		if (!isCheckBuscaTipoServidor()) { 
			addMensagemErro("O campo Tipo do Servidor é obrigatório");
		}
		if (!isCheckBuscaSituacaoAcao()) { 
			addMensagemErro("O campo Situação da Ação é obrigatório");
		}
		if (!isCheckBuscaTipoAcao()) { 
			addMensagemErro("O campo Tipo da Ação é obrigatório");
		}
		if (hasErrors()) {
			return redirectMesmaPagina();
		}
		
		// Gerar relatório
		Connection con = null;
		try {

			// Popular parâmetros do relatório
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("SUBREPORT_DIR",	JasperReportsUtil.getReportSIGAA("trf16153_Relatorio_Equipe_Acao_Extensao.jasper"));
			parametros.put("ano", this.ano);
			parametros.put("situacaoAcao", this.buscaSituacaoAtividade);
			parametros.put("tipoAcao", this.buscaTipoAtividade);
			parametros.put("tipoServidor", this.buscaTipoServidor);
			parametros.put("funcaoMembro", this.buscaFuncaoMembro);
			parametros.put("titulo", "RELATÓRIO DE MEMBROS DA EQUIPE DE AÇÕES DE EXTENSÃO");

			parametros.put("tServidor", this.labelTipoServidor);
			parametros.put("textoAcao", this.labelTipoAtividade);
			parametros.put("textoSituacao", this.labelSituacaoAtividade);
			
			parametros.put("subSistema", getSubSistema().getNome());
			parametros.put("subSistemaLink", getSubSistema().getLink());

			// Preencher relatório
			con = Database.getInstance().getSigaaConnection();
			JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("trf16153_Relatorio_Equipe_Acao_Extensao.jasper"),
							parametros, con);

			// Exportar relatório de acordo com o formato escolhido
			String nomeArquivo = "Relatorio_equipe_acao_extensao." + this.formato;
			JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), this.formato);
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor, contacte o suporte através do \"Abrir Chamado\"");
			return null;
		} finally {
			Database.getInstance().close(con);
		}
		return null;
	}
	
	/**
	 * Gera o relatório de acordo com os critérios selecionados
	 * 
	 * sigaa.war/extensao/Relatorios/form_dados_publico_area.jsp
	 * 
	 * 
	 * @return 
	 * @throws SegurancaException
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String gerarRelatorioEstatisticas() throws SegurancaException {
		
		String tipoData = getParameter("data");
		if ( "ano".equals(tipoData )) {
			this.validarAno();
			if (hasErrors()) {
			    return redirectMesmaPagina();
			}
			inicio = null;
			fim = null;			
		} else {
			if ((inicio == null || fim == null) || inicio.getTime() > fim.getTime()) {
			    addMensagemErro("O campo Período é obrigatório ou data inválida");
			}
			if (hasErrors()) {
			    return redirectMesmaPagina();
			}
			ano = null;
		}	
		
		AtividadeExtensaoDao atividadeExtensaoDAO = getDAO(AtividadeExtensaoDao.class);
		try {
			listaEstatisticas = atividadeExtensaoDAO.quantidadeAcoesPublicoMembroPorArea(buscaTipoAtividade, buscaSituacaoAtividade, ano, inicio, fim);
		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor, contacte o suporte através do \"Abrir Chamado\"");
		}
		String[] colunas = { "acoes", "estimado", "atingido", "docentes", "bolsistas", "nao_bolsistas", "alunos_pos", "tecnicos", "externos" };
		Map<String, Object> reg;
		
		@SuppressWarnings("rawtypes")
		Iterator it = listaEstatisticas.iterator();
		//verifica se existem dados no lista.
		laco:{
			while (it.hasNext()) {
				reg = (Map<String, Object>) it.next();
				for (int i = 1; i < colunas.length; i++)
					if (reg.get(colunas[i]) != null) break laco;	
			}
			addMensagemErro("Nenhuma ação de extensão localizada");
			redirectMesmaPagina();
		}
		
		return forward(CONTEXTO + RELATORIO_ESTATISTICAS);
	}

	/**
	 * Redireciona para a mesma página carregada.
	 * 
	 * sigaa.war/extensao/Relatorios/form_equipe_acao_extensao.jsp
	 */
	public String redirectMesmaPagina() {
		return redirectJSF(getCurrentURL());
	}
	
	private void validarAno(){
		if (ano == null) {
			addMensagemErro("O campo Ano de Referência é obrigatório");
		}else if (ano <= 0 || ano > CalendarUtils.getAnoAtual()) { 
			addMensagemErro("O campo ano deve conter um valor válido");
		}
	}

	/**
	 * @param ano
	 *            the ano to set
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/**
	 * @return the ano
	 */
	public Integer getAno() {
		return ano;
	}

	/**
	 * @param buscaSituacaoAtividade
	 *            the buscaSituacaoAtividade to set
	 */
	public void setBuscaSituacaoAtividade(Integer buscaSituacaoAtividade) {
		this.buscaSituacaoAtividade = buscaSituacaoAtividade;
	}

	/**
	 * @return the buscaSituacaoAtividade
	 */
	public Integer getBuscaSituacaoAtividade() {
		return buscaSituacaoAtividade;
	}

	/**
	 * @param buscaTipoAtividade
	 *            the buscaTipoAtividade to set
	 */
	public void setBuscaTipoAtividade(Integer buscaTipoAtividade) {
		this.buscaTipoAtividade = buscaTipoAtividade;
	}

	/**
	 * @return the buscaTipoAtividade
	 */
	public Integer getBuscaTipoAtividade() {
		return buscaTipoAtividade;
	}

	/**
	 * @param buscaTipoServidor
	 *            the buscaTipoServidor to set
	 */
	public void setBuscaTipoServidor(Integer buscaTipoServidor) {
		this.buscaTipoServidor = buscaTipoServidor;
	}

	/**
	 * @return the buscaTipoServidor
	 */
	public Integer getBuscaTipoServidor() {
		return buscaTipoServidor;
	}

	/**
	 * @param buscaFuncaoMembro
	 *            the buscaFuncaoMembro to set
	 */
	public void setBuscaFuncaoMembro(Integer buscaFuncaoMembro) {
		this.buscaFuncaoMembro = buscaFuncaoMembro;
	}

	/**
	 * @return the buscaFuncaoMembro
	 */
	public Integer getBuscaFuncaoMembro() {
		return buscaFuncaoMembro;
	}

	public boolean isCheckBuscaTipoAcao() {
		return checkBuscaTipoAcao;
	}

	public void setCheckBuscaTipoAcao(boolean checkBuscaTipoAcao) {
		this.checkBuscaTipoAcao = checkBuscaTipoAcao;
	}

	public boolean isCheckBuscaSituacaoAcao() {
		return checkBuscaSituacaoAcao;
	}

	public void setCheckBuscaSituacaoAcao(boolean checkBuscaSituacaoAcao) {
		this.checkBuscaSituacaoAcao = checkBuscaSituacaoAcao;
	}

	public boolean isCheckBuscaTipoServidor() {
		return checkBuscaTipoServidor;
	}

	public void setCheckBuscaTipoServidor(boolean checkBuscaTipoServidor) {
		this.checkBuscaTipoServidor = checkBuscaTipoServidor;
	}

	public boolean isCheckBuscaFuncaoMembro() {
		return checkBuscaFuncaoMembro;
	}

	public void setCheckBuscaFuncaoMembro(boolean checkBuscaFuncaoMembro) {
		this.checkBuscaFuncaoMembro = checkBuscaFuncaoMembro;
	}

	/**
	 * @param labelSituacaoAtividade the labelSituacaoAtividade to set
	 */
	public void setLabelSituacaoAtividade(String labelSituacaoAtividade) {
		this.labelSituacaoAtividade = labelSituacaoAtividade;
	}

	/**
	 * @return the labelSituacaoAtividade
	 */
	public String getLabelSituacaoAtividade() {
		return labelSituacaoAtividade;
	}

	/**
	 * @param labelTipoServidor the labelTipoServidor to set
	 */
	public void setLabelTipoServidor(String labelTipoServidor) {
		this.labelTipoServidor = labelTipoServidor;
	}

	/**
	 * @return the labelTipoServidor
	 */
	public String getLabelTipoServidor() {
		return labelTipoServidor;
	}

	/**
	 * @param labelTipoAtividade the labelTipoAtividade to set
	 */
	public void setLabelTipoAtividade(String labelTipoAtividade) {
		this.labelTipoAtividade = labelTipoAtividade;
	}

	/**
	 * @return the labelTipoAtividade
	 */
	public String getLabelTipoAtividade() {
		return labelTipoAtividade;
	}

	/**
	 * @param listaEstatisticas the listaEstatisticas to set
	 */
	public void setListaEstatisticas(List<Map<String, Object>> listaEstatisticas) {
		this.listaEstatisticas = listaEstatisticas;
	}

	/**
	 * @return the listaEstatisticas
	 */
	public List<Map<String, Object>> getListaEstatisticas() {
		return listaEstatisticas;
	}

	/**
	 * @param inicio the inicio to set
	 */
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	/**
	 * @return the inicio
	 */
	public Date getInicio() {
		return inicio;
	}

	/**
	 * @param fim the fim to set
	 */
	public void setFim(Date fim) {
		this.fim = fim;
	}

	/**
	 * @return the fim
	 */
	public Date getFim() {
		return fim;
	}



}
