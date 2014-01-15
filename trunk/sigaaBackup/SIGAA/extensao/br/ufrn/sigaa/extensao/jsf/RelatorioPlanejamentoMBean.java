/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/03/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioPlanejamentoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe respons�vel pelos relat�rios quantitativos dos membros das equipes de extens�o.
 * 
 * 
 * @author Jean Guerethes
 * @author Ilueny Santos
 */

@Component
@Scope("request")
public class RelatorioPlanejamentoMBean extends SigaaAbstractController<Object> {

    //Constante que armazena endere�o de uma p�gina.
	public static final String JSP_RELATORIO_PLANEJAMENTO_EQUIPE_DETALHADO = "/extensao/Relatorios/relatorio_planejamento_equipe_detalhado.jsp";

	//Constante que armazena endere�o de uma p�gina.
    public static final String JSP_RELATORIO_PLANEJAMENTO_TRABALHO = "/extensao/Relatorios/relatorio_planejamento_discentes_plano_trabalho.jsp";
    
    //Constante que armazena endere�o de uma p�gina.
    public static final String JSP_RELATORIO_PLANEJAMENTO_DISCENTES_VINCULO = "/extensao/Relatorios/relatorio_planejamento_discentes_vinculos.jsp";
  
    //Constante que armazena endere�o de uma p�gina.
    public static final String JSP_RELATORIO_PLANEJAMENTO_GERAL = "/extensao/Relatorios/relatorio_planejamento_geral.jsp";
  
    //Constante que armazena endere�o de uma p�gina.
    public static final String JSP_RELATORIO_PLANEJAMENTO_PARTICIPANTES = "/extensao/Relatorios/relatorio_planejamento_participantes.jsp";
    
    //Constante que armazena endere�o de uma p�gina.
    public static final String JSP_RELATORIO_PLANEJAMENTO_PRODUTOS = "/extensao/Relatorios/relatorio_planejamento_produtos.jsp";

    //Constante que armazena endere�o de uma p�gina.
    public static final String JSP_BUSCA_RELATORIO = "/extensao/Relatorios/busca_relatorio_planejamento.jsp";
    
    //Utilizado para armazenar informa��o inserida em tela de busca. 
    private TipoAtividadeExtensao buscaTipoAcao = new TipoAtividadeExtensao();
    
    //Utilizado para armazenar informa��o inserida em tela de busca.
    private AreaTematica buscaAreaTematica = new AreaTematica();
    
    //Utilizado para armazenar informa��o inserida em tela de busca.
    private String nomeRelatorioAtual;
    
    //Utilizado para armazenar informa��o inserida em tela de busca.
    private String descricaoTotalRelatorioAtual;
    //Utilizado para armazenar informa��o inserida em tela de busca.
    private Date dataInicio = new Date();
    //Utilizado para armazenar informa��o inserida em tela de busca.
    private Date dataFim = new Date();
    //Utilizado para armazenar informa��o inserida em tela de busca.
    private String categoriaMembroEquipe;
    //Utilizado para armazenar informa��o inserida em tela de busca.
    private int escolhaRelatorio;
    //Utilizado para armazenar informa��o inserida em tela de busca.
    private TipoSituacaoProjeto situacaoAcao = new TipoSituacaoProjeto();

    //Dados para armazenar informa��es de relat�rios: gerarRelatorioDocentesPorNivel
    private List<HashMap<String, Object>> lista = new ArrayList<HashMap<String, Object>>();

    //Dados para armazenar informa��es de relat�rios
    private Map<String, Integer> result = new HashMap<String, Integer>();


    //Tipo de relat�rio
    public static final int RELATORIO_DETALHADO_EQUIPE = 1;
    //Tipo de relat�rio
    public static final int RELATORIO_DISCENTES_COM_PLANOS_TRABALHO = 2;
    //Tipo de relat�rio
    public static final int RELATORIO_NIVEL_DISCENTE = 3;
    //Tipo de relat�rio
    public static final int RELATORIO_NIVEL_E_VINCULO_DISCENTE = 4;
    //Tipo de relat�rio
    public static final int RELATORIO_TOTAL_PARTICIPANTES = 5;
    //Tipo de relat�rio
    public static final int RELATORIO_TOTAL_PRODUTOS = 6;
    
    

    /**
     * Construtor padr�o.
     */
    public RelatorioPlanejamentoMBean() {

    }
    
    
    /**
     * Inicia o relatorio que lista o total de docente envolvidos em 
     * a��es de extens�o com suas respectivas fun��es
     * por tipo de a��o (curso, evento, projeto, programa). 
     * 
     * @return
     */
    public String iniciarRelatorioDocentesDetalhado() {
	escolhaRelatorio = RELATORIO_DETALHADO_EQUIPE;
	categoriaMembroEquipe = "DOCENTE";
	nomeRelatorioAtual = "TOTAL DE DOCENTES POR TIPO DE A��O";
	descricaoTotalRelatorioAtual = "N�MERO DE DOCENTES ENCONTRADOS";
	return forward(JSP_BUSCA_RELATORIO);
    }
    
    /**
     * Inicia o relat�rio que lista o total de t�cnicos administrativos
     * cadastrados nas equipes das a��es de extens�o.
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioTecnicoAdmDetalhado() {
	escolhaRelatorio = RELATORIO_DETALHADO_EQUIPE;
        categoriaMembroEquipe = "SERVIDOR";
        nomeRelatorioAtual = "TOTAL DE T�CNICOS ADMINISTRATIVOS POR TIPO DE A��O";
        descricaoTotalRelatorioAtual = "N�MERO DE SERVIDORES ENCONTRADOS";
        return forward(JSP_BUSCA_RELATORIO);
    }

    /**
     * Inicia o relat�rio que lista o total de 
     * colaboradores da comunidade externa envolvidos
     * em equipes de a��es de extens�o.
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioExternoDetalhado() {
	escolhaRelatorio = RELATORIO_DETALHADO_EQUIPE;
	categoriaMembroEquipe = "EXTERNO";
	nomeRelatorioAtual = "TOTAL DE PARTICIPANTES EXTERNOS POR TIPO DE A��O";
	descricaoTotalRelatorioAtual = "N�MERO DE PARTICIPANTES EXTERNOS ENCONTRADOS";
        return forward(JSP_BUSCA_RELATORIO);
    }
    
    /**
     * Inicia Relat�rio que lista o total de planos de trabalhos
     * cadastrados para discentes de extens�o.
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioPlanosTrabalho() {
	escolhaRelatorio = RELATORIO_DISCENTES_COM_PLANOS_TRABALHO;
	nomeRelatorioAtual = "TOTAL DE DISCENTES COM PLANOS DE TRABALHO";
	descricaoTotalRelatorioAtual = "N�MERO DISCENTES ENCONTRADOS";
	return forward(JSP_BUSCA_RELATORIO);
    }
    
    
    /**
     * Inicia o relat�rio que lista o total de discentes envolvidos nas
     * equipes das a��es de extens�o.
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioDiscenteNaEquipe() {
	escolhaRelatorio = RELATORIO_DETALHADO_EQUIPE;
	categoriaMembroEquipe = "DISCENTE";
	nomeRelatorioAtual = "TOTAL DE DISCENTES NAS EQUIPES DE EXECU��O DA A��O";
	descricaoTotalRelatorioAtual = "N�MERO DE DISCENTES ENCONTRADOS";
	return forward(JSP_BUSCA_RELATORIO);
    }
    
    
    /**
     * Inicia o relat�rio que lista do total de discentes por n�vel
     * de forma��o (gradua��o, mestrado, doutorado, etc).
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioDiscentesPorNivel() {
	escolhaRelatorio = RELATORIO_NIVEL_DISCENTE;
	nomeRelatorioAtual = "TOTAL DE DISCENTES ATIVOS POR N�VEL DE ENSINO";
	descricaoTotalRelatorioAtual = "N�MERO DE DISCENTES ENCONTRADOS";
	return forward(JSP_BUSCA_RELATORIO);
    }

    /**
     * Inicia o relat�rio que lista os discentes por
     * n�vel de forma��o e tipo de v�nculo (bolsista, volunt�rio).
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioDiscentesPorVinculoENivel() {
	escolhaRelatorio = RELATORIO_NIVEL_E_VINCULO_DISCENTE;
	nomeRelatorioAtual = "TOTAL DE DISCENTES ATIVOS POR V�NCULO E N�VEL DE ENSINO";
	descricaoTotalRelatorioAtual = "N�MERO DE DISCENTES ENCONTRADOS";
	return forward(JSP_BUSCA_RELATORIO);
    }

    
    /**
     * Inicia o relat�rio que lista um resumo 
     * dos tipos de participa��o nas a��es 
     * em um determinado per�odo.
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioResumoParticipantes() {
	escolhaRelatorio = RELATORIO_TOTAL_PARTICIPANTES;
	nomeRelatorioAtual = "TOTAL DE A��ES E PARTICIPANTES ATIVOS POR �REA TEM�TICA";
	descricaoTotalRelatorioAtual = "TOTAL";
	return forward(JSP_BUSCA_RELATORIO);
    }

    /**
     * Inicia o relat�rio que lista os tipos de 
     * produtos de extens�o produzidos em um 
     * per�odo informado.
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioResumoProdutos() {
	escolhaRelatorio = RELATORIO_TOTAL_PRODUTOS;
	nomeRelatorioAtual = "TOTAL DE PRODUTOS ATIVOS POR TIPO E �REA TEM�TICA";
	descricaoTotalRelatorioAtual = "TOTAL";
	return forward(JSP_BUSCA_RELATORIO);
    }
    
    
    
    /**
     * Inicia o relat�rio que lista os tipos de 
     * produtos de extens�o produzidos em um 
     * per�odo informado.
     * 
     * Chamado por:
     * sigaa.war/extensao/menu.jsp
     * 
     * @return
     */
    public String iniciarRelatorioDocentesPorNivel() {	
    	return forward(ConstantesNavegacao.BUSCA_RELATORIO_DOCENTES_POR_NIVEL);
    }
    
    public String gerarRelatorioDocentesPorNivel() throws DAOException {
    	
    	erros = new ListaMensagens();		
		
    	ValidatorUtil.validateRequired(situacaoAcao, "Situa��o da A��o", erros);
		ValidatorUtil.validateRequired(dataInicio, "Data in�cio do Per�odo de Execu��o da A��o", erros);
		ValidatorUtil.validateRequired(dataFim, "Data Fim do Per�odo de Execu��o da A��o", erros);
		
		if(!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}		
		
		getGenericDAO().initialize(situacaoAcao);
		RelatorioPlanejamentoDao dao = getDAO(RelatorioPlanejamentoDao.class);
		lista = dao.relatorioDocentesPorNivel(situacaoAcao.getId(), dataInicio,dataFim);		
		
		if(lista.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}    	
    	
    	return forward(ConstantesNavegacao.RELATORIO_DOCENTES_POR_NIVEL);
    }

    
    

    /**
     * Define as configura��es iniciais do relat�rio e chama o m�todo que
     * popula com os dados.
     * 
     * Chamado por:
     * sigaa.war/extensao/Relatorios/busca_relatorio_planejamento.jsp
     * 
     * @return P�gina com os dados do relat�rio
     * @throws ArqException por erro na consulta dos dados.
     * @throws SQLException por erro na consulta dos dados.
     */
    public String gerarRelatorio() throws ArqException, SQLException {

	ListaMensagens mensagens = new ListaMensagens();
	erros.getMensagens().clear();
	if (escolhaRelatorio == 0) {
	    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo de Relat�rio");
	}
	if (situacaoAcao.getId() == 0) {
	    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Situa��o");
	}
	if (dataInicio == null || dataFim == null) {
	    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Per�odo de execu��o");
	} else {
	    ValidatorUtil.validaOrdemTemporalDatas(dataInicio, dataFim, true, "Data inicial deve ser menor que a data final", mensagens);
	    addMensagens(mensagens);
	}
	if (hasErrors()) {
	    return null;
	}

	switch (escolhaRelatorio) {
	case 1:
	    relatorioDetalhadoEquipeExtensao();
	    break;
	case 2:
	    discentesComPlanoTrabalho();
	    break;
	case 3: 
	    resumoNivelDiscentesExtensao();
	    break;
	case 4:
	    resumoVinculoDiscentesExtensao();
	    break;
	case 5:
	    if (buscaAreaTematica == null || buscaAreaTematica.getId() == 0) {
		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "�rea Tem�tica");
		return null;
	    }
	    resumoParticipantes();
	    break;
	case 6:
	    if (buscaAreaTematica == null || buscaAreaTematica.getId() == 0) {
		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "�rea Tem�tica");
		return null;
	    }
	    resumoProdutos();
	    break;
 
	}

	if (situacaoAcao != null)
	    getGenericDAO().initialize(situacaoAcao);
	if (buscaTipoAcao != null)
	    getGenericDAO().initialize(buscaTipoAcao);
	if (buscaAreaTematica != null)
	    getGenericDAO().initialize(buscaAreaTematica);

	return JSP_BUSCA_RELATORIO;
    }


    /**
     * Gera relat�rio com detalhes dos membros participantes das a��es de extens�o.
     * O relat�rio apresenta os totais de participantes agrupados por tipo de a��o de extens�o, fun��o e categoria.
     * 
     * N�o � chamado por JSPs.
     * 
     * @throws ArqException por erro na consulta dos dados.
     * @throws SQLException por erro na consulta dos dados.
     * @return p�gina com o relat�rio
     */
    private String relatorioDetalhadoEquipeExtensao() throws ArqException, SQLException {		
	RelatorioPlanejamentoDao dao = getDAO(RelatorioPlanejamentoDao.class);
	lista = dao.findDetalhesEquipeExtensaoByCategoriaPeriodoAcao(categoriaMembroEquipe, dataInicio, dataFim, situacaoAcao.getId(), buscaTipoAcao.getId());		
	if (ValidatorUtil.isEmpty(lista)) {
	    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
	    return forward(JSP_BUSCA_RELATORIO);
	} else {
	    return forward(JSP_RELATORIO_PLANEJAMENTO_EQUIPE_DETALHADO);
	}
    }

    /**
     * Tem com fun��o retornar todos os discentes com planos de trabalho cadastrados.
     * O agrupamento � feito por tipo de bolsa recebida pelo discente no plano.
     * 
     * N�o � chamado por JSPs.
     * 
     * @throws ArqException por erro na consulta dos dados.
     * @throws SQLException por erro na consulta dos dados.
     * @return Retorna a p�gina do relat�rio.
     */
    private String discentesComPlanoTrabalho() throws ArqException, SQLException {
	RelatorioPlanejamentoDao dao = getDAO(RelatorioPlanejamentoDao.class);
	lista = dao.findDiscentesComPlanoTrabalhoByPeriodoAcao(dataInicio, dataFim, situacaoAcao.getId(), buscaTipoAcao.getId());
	if (ValidatorUtil.isEmpty(lista)) {
	    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
	    return forward(JSP_BUSCA_RELATORIO);
	} else {
	    return forward(JSP_RELATORIO_PLANEJAMENTO_TRABALHO);
	}

    }

    /**
     * Retorna total de discentes de extens�o agrupados por n�vel de ensino.
     * gradua��o e p�s-gradua��o.
     * 
     * N�o � chamado por JSPs.
     * 
     * @throws ArqException por erro na consulta dos dados.
     * @return Retorna a p�gina do relat�rio.
     */
    private String resumoNivelDiscentesExtensao() throws ArqException {
	RelatorioPlanejamentoDao dao = getDAO(RelatorioPlanejamentoDao.class);
	result = dao.findDiscentesExtensaoByPeriodoAcaoSituacaoAcao(dataInicio, dataFim, situacaoAcao.getId(), buscaTipoAcao.getId());
	if (ValidatorUtil.isEmpty(result)) {
	    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);	    
	}
	return forward(JSP_RELATORIO_PLANEJAMENTO_GERAL);
    }

    /**
     * Agrupa os discentes por tipo de v�nculo e n�vel de ensino.
     * 
     * N�o � chamado por JSPs.
     * 
     * @return retorna p�gina com relat�rio
     * @throws ArqException por erro na consulta dos dados.
     * @throws SQLException por erro na consulta dos dados.
     */
    private String resumoVinculoDiscentesExtensao() throws ArqException, SQLException {
	RelatorioPlanejamentoDao dao = getDAO(RelatorioPlanejamentoDao.class);
	lista = dao.findDiscentesExtensaoByPeriodoAcaoSituacaoTipoAcao(dataInicio, dataFim, situacaoAcao.getId(), buscaTipoAcao.getId());
	
	//substituindo sigla por descri��o: 'G' = 'GRADUA��O', etc.
	for (HashMap<String, Object> l : lista) {
	    char nivel = ((Character) l.get("nivel"));
	    l.put("nivel", NivelEnsino.getDescricao(nivel));
	}
	return forward(JSP_RELATORIO_PLANEJAMENTO_DISCENTES_VINCULO);
    }

    /**
     * Relat�rio resumido com todos os participantes das a��es de extens�o
     * ativas.
     * 
     * N�o � chamado por JSPs.
     * 
     * @return
     * @throws ArqException
     * @throws SQLException
     */
    private String resumoParticipantes() throws ArqException, SQLException {
	RelatorioPlanejamentoDao dao = getDAO(RelatorioPlanejamentoDao.class);
	lista = dao.findResumoParticipantesByPeriodoAreaTematica(dataInicio, dataFim, situacaoAcao.getId(), buscaAreaTematica.getId());
	if (ValidatorUtil.isEmpty(lista)) {
	    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);	    
	}
	return forward(JSP_RELATORIO_PLANEJAMENTO_PARTICIPANTES);
    }
    
    /**
     * Relat�rio resumido com os produtos ativos.
     * 
     * N�o � chamado por JSPs.
     * 
     * @return
     * @throws ArqException
     * @throws SQLException
     */
    private String resumoProdutos() throws ArqException, SQLException {
	RelatorioPlanejamentoDao dao = getDAO(RelatorioPlanejamentoDao.class);
	lista = dao.findResumoProdutosByPeriodoAreaTematica(dataInicio, dataFim, situacaoAcao.getId(), buscaAreaTematica.getId());
	if (ValidatorUtil.isEmpty(lista)) {
	    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
	    return null;
	}
	return forward(JSP_RELATORIO_PLANEJAMENTO_PRODUTOS);
    }
    
    
    /**
     * Chamado por: 
     * 
     * sigaa.war/extensao/Relatorios/relatorio_planejamento_equipe_detalhado.jsp
     * sigaa.war/extensao/Relatorios/relatorio_planejamento_discentes_plano_trabalho.jsp
     * sigaa.war/extensao/Relatorios/relatorio_planejamento_discentes_vinculos.jsp
     * sigaa.war/extensao/Relatorios/relatorio_planejamento_geral.jsp 
     * 
     * @return
     */
    public List<HashMap<String, Object>> getLista() {
	return lista;
    }

    public void setLista(List<HashMap<String, Object>> lista) {
	this.lista = lista;
    }

    /**
     * Chamado por:
     * <ul>
     * 	<li>/sigaa.war/extensao/Relatorios/relatorio_planejamento_equipe_detalhado.jsp</li>
     *  <li>/sigaa.war/extensao/Relatorios/relatorio_planejamento_discentes_plano_trabalho.jsp</li>
     *  <li>/sigaa.war/extensao/Relatorios/relatorio_planejamento_discentes_vinculos.jsp</li>
     *  <li>/sigaa.war/extensao/Relatorios/relatorio_planejamento_geral.jsp</li>
     * </ul>
     * 
     * @return
     */
    public Map<String, Integer> getResult() {
	return result;
    }

    public void setResult(Map<String, Integer> result) {
	this.result = result;
    }

    public int getEscolhaRelatorio() {
	return escolhaRelatorio;
    }

    public void setEscolhaRelatorio(int escolhaRelatorio) {
	this.escolhaRelatorio = escolhaRelatorio;
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

    public String getCategoriaMembroEquipe() {
	return categoriaMembroEquipe;
    }

    public void setCategoriaMembroEquipe(String categoriaMembroEquipe) {
	this.categoriaMembroEquipe = categoriaMembroEquipe;
    }

    public String getNomeRelatorioAtual() {
	return nomeRelatorioAtual;
    }

    public void setNomeRelatorioAtual(String nomeRelatorioAtual) {
	this.nomeRelatorioAtual = nomeRelatorioAtual;
    }

    public String getDescricaoTotalRelatorioAtual() {
	return descricaoTotalRelatorioAtual;
    }

    public void setDescricaoTotalRelatorioAtual(String descricaoTotalRelatorioAtual) {
	this.descricaoTotalRelatorioAtual = descricaoTotalRelatorioAtual;
    }

    public TipoSituacaoProjeto getSituacaoAcao() {
        return situacaoAcao;
    }

    public void setSituacaoAcao(TipoSituacaoProjeto situacaoAcao) {
        this.situacaoAcao = situacaoAcao;
    }
    
    public TipoAtividadeExtensao getBuscaTipoAcao() {
        return buscaTipoAcao;
    }


    public void setBuscaTipoAcao(TipoAtividadeExtensao buscaTipoAcao) {
        this.buscaTipoAcao = buscaTipoAcao;
    }


    public AreaTematica getBuscaAreaTematica() {
        return buscaAreaTematica;
    }


    public void setBuscaAreaTematica(AreaTematica buscaAreaTematica) {
        this.buscaAreaTematica = buscaAreaTematica;
    }
    
}
