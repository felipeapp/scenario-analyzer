package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ouvidoria.dao.AssuntoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.HistoricoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.RelatoriosManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.AssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.OrigemManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;

/**
 * Controller responsável pela geração de relatórios da ouvidoria.
 * 
 * @author Bernardo
 *
 */
@Component(value="relatoriosOuvidoria") @Scope(value="request")
public class RelatoriosOuvidoriaMBean extends SigaaAbstractController<Manifestacao> {
    
	/** Link para a página do formulário de geração do quadro geral de anifestações. */
    public static final String JSP_FORM_QUADRO_GERAL = "/quadro_geral/form.jsp";
    
    /** Página do quadro geral de manifestações. */
    public static final String JSP_QUADRO_GERAL = "/quadro_geral/relatorio.jsp";
    
    /** Link para o formulário de geração do relatório geral de manifestações. */
    public static final String JSP_FORM_RELATORIO_GERAL_MANIFESTACOES = "/geral/form.jsp";
    
    /** Página do relatório geral de manifestações. */
    public static final String JSP_RELATORIO_GERAL_MANIFESTACOES = "/geral/relatorio.jsp";

    /** Históricos encontrados na geração do relatório geral. */
    private Collection<HistoricoManifestacao> historicos;
    
    /** Indica se o filtro de período de cadastro da manifestação foi selecionado. */
    private boolean checkPeriodo;
    
    /** Indica se o filtro de origem do manifestante foi selecionado. */
    private boolean checkOrigem;
    
    /** Indica se o filtro de assunto da manifestação foi selecionado. */
    private boolean checkAssunto;
    
    /** Indica se o filtro de categoria de assunto da manifestação foi selecionado. */
    private boolean checkCategoriaAssunto;
    
    /** Indica se o filtro de categoria do solicitante foi selecionado. */
    private boolean checkCategoriaSolicitante;
    
    /** Indica se o filtro de status da manifestação foi selecionado. */
    private boolean checkStatusManifestacao;
    
    /** Indica se o filtro de unidade responsável pela manifestação foi selecionado. */
    private boolean checkUnidadeResponsavel;
    
    /** Indica se o relatório deve trazer apenas manifestações que não foram respondidas. */
    private boolean somenteNaoRespondidas;
    
    /** Data de início do período de cadastro da manifestação. */
    private Date dataInicial;
    
    /** Data final do período de cadastro da manifestação. */
    private Date dataFinal;
    
    /** Categoria do solicitante escolhida no formulário. */
    private CategoriaSolicitante categoriaSolicitante;
    
    /** Origem da manifestação selecionada. */
    private OrigemManifestacao origemManifestacao;
    
    /** Status da manifestação selecionado. */
    private StatusManifestacao statusManifestacao;
    
    /** Categoria do assunto da manifestação escolhida. */
    private CategoriaAssuntoManifestacao categoriaAssuntoManifestacao;

    /** Assunto da manifestação escolhido. */
    private AssuntoManifestacao assuntoManifestacao;
    
    /** Unidade escolhida no formulário. */
    private Unidade unidade;

    public RelatoriosOuvidoriaMBean() {
    	init();
    }

    /**
     * Inicia os dados necessários ao MBean.
     */
    private void init() {
		obj = new Manifestacao();
		categoriaSolicitante = new CategoriaSolicitante();
		origemManifestacao = new OrigemManifestacao();
		statusManifestacao = new StatusManifestacao();
		categoriaAssuntoManifestacao = new CategoriaAssuntoManifestacao();
		assuntoManifestacao = new AssuntoManifestacao();
		unidade = new Unidade();
    }
    
    /**
     * Inicia a geração do quadro geral de manifestações.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/menu.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String iniciarQuadroGeral() {
    	return forward(getDirBase() + JSP_FORM_QUADRO_GERAL);
    }
    
    /**
     * Monta o quadro geral de acordo com os dados informados.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/relatorios/quadro_geral/form.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String montarQuadroGeral() throws DAOException {
	    validarDatas();
		
		if(hasErrors())
		    return null;
		
		RelatoriosManifestacaoDao dao = getDAO(RelatoriosManifestacaoDao.class);
		
		try {
		    historicos = dao.getManifestacoesQuadroGeral(dataInicial, dataFinal);
		    
		    if(isEmpty(historicos)) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
		    }
		    else {
		    	ordenarHistoricos();
		    }
		} finally {
		    dao.close();
		}
		
		return forward(getDirBase() + JSP_QUADRO_GERAL);
    }
    
    /**
     * Inicia a geração do relatório geral de manifestações.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/menu.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String iniciarRelatorioGeralManifestacoes() {
    	return forward(getDirBase() + JSP_FORM_RELATORIO_GERAL_MANIFESTACOES);
    }
    
    /**
     * Gera o relatório geral de manifestações de acordo com os filtros informados.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/relatorios/geral/form.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String gerarRelatorioGeralManifestacoes() throws DAOException {
    	validarDadosRelatorioGeral();
		
		if(hasErrors())
		    return null;
		
		RelatoriosManifestacaoDao dao = getDAO(RelatoriosManifestacaoDao.class);
		
		try {
		    historicos = dao.getManifestacoesRelatorioGeral(dataInicial, dataFinal, categoriaSolicitante, origemManifestacao, statusManifestacao, categoriaAssuntoManifestacao, assuntoManifestacao, unidade, somenteNaoRespondidas);
		    
		    if(isEmpty(historicos)) {
		    	addMensagemErro("Não foram encontradas manifestações cadastradas para os parâmetros informados.");
				return null;
		    }
		    else {
		    	popularParametrosBusca();
		    	ordenarHistoricos();
		    }
		} finally {
		    dao.close();
		}
		
		return forward(getDirBase() + JSP_RELATORIO_GERAL_MANIFESTACOES);
    }

    /**
     * Popula os dados utilizados na busca para apresentação no relatório.
     */
	private void popularParametrosBusca() {
		for (HistoricoManifestacao h : historicos) {
			if(checkAssunto) {
				assuntoManifestacao = h.getManifestacao().getAssuntoManifestacao();
			}
			
			if(checkCategoriaAssunto) {
				categoriaAssuntoManifestacao = h.getManifestacao().getAssuntoManifestacao().getCategoriaAssuntoManifestacao();
			}
			
			if(checkCategoriaSolicitante) {
				categoriaSolicitante = h.getManifestacao().getInteressadoManifestacao().getCategoriaSolicitante();
			}
			
			if(checkOrigem) {
				origemManifestacao = h.getManifestacao().getOrigemManifestacao();
			}
			
			if(checkStatusManifestacao) {
				statusManifestacao = h.getManifestacao().getStatusManifestacao();
			}
		}
	}

	/**
	 * Valida os dados informados para geração do relatório geral de manifestações.
	 */
	private void validarDadosRelatorioGeral() {
		if(!checkAssunto && !checkCategoriaAssunto && !checkCategoriaSolicitante && !checkOrigem && !checkPeriodo && !checkStatusManifestacao && !checkUnidadeResponsavel)
			addMensagemErro("Selecione uma das opções para gerar o relatório.");
		
		if(checkAssunto) {
			if(isEmpty(assuntoManifestacao))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Assunto da Manifestacao");
		}
		else
			assuntoManifestacao = new AssuntoManifestacao();
		
		if(checkCategoriaAssunto) {
			if(isEmpty(categoriaAssuntoManifestacao))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria do Assunto");
		}
		else
			categoriaAssuntoManifestacao = new CategoriaAssuntoManifestacao();
		
		if(checkCategoriaSolicitante) {
			if(isEmpty(categoriaSolicitante))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria do Solicitante");
		}
		else
			categoriaSolicitante = new CategoriaSolicitante();
		
		if(checkOrigem) {
			if(isEmpty(origemManifestacao))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Origem da Manifestacao");
		}
		else
			origemManifestacao = new OrigemManifestacao();
		
		if(checkPeriodo)
			validarDatas();
		else {
			dataInicial = null;
			dataFinal = null;
		}
		
		if(checkStatusManifestacao) {
			if(isEmpty(statusManifestacao))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Status da Manifestacao");
		}
		else
			statusManifestacao = new StatusManifestacao();
		
		if(checkUnidadeResponsavel) {
			if(isEmpty(unidade))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Responsável");
		}
		else
			unidade = new Unidade();
	}
	
	/**
	 * Ordena os históricos listados de acordo com sua data de cadastro.
	 */
	private void ordenarHistoricos() {
		List<HistoricoManifestacao> list = new ArrayList<HistoricoManifestacao>(historicos);
		
		Collections.sort(list, new Comparator<HistoricoManifestacao>() {
			@Override
			public int compare(HistoricoManifestacao h1, HistoricoManifestacao h2) {
				return h1.getManifestacao().getDataCadastro().compareTo(h2.getManifestacao().getDataCadastro());
			}
		});
		
		historicos = list;
	}
    
	/**
	 * Inicia o processo de geração do relatório de manifestações não respondidas.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/menu.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
    public String iniciarRelatorioManifestacoesNaoRespondidas() {
    	somenteNaoRespondidas = true;
    	return iniciarRelatorioGeralManifestacoes();
    }
    
    /**
	 * Inicia o processo de geração do relatório de manifestações por categoria do solicitante.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/menu.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
    public String iniciarRelatorioManifestacoesPorCategoriaSolicitante() {
    	checkCategoriaSolicitante = true;
    	return iniciarRelatorioGeralManifestacoes();
    }

    /**
	 * Inicia o processo de geração do relatório de manifestações por assunto.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/menu.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
    public String iniciarRelatorioManifestacoesPorAssunto() {
    	checkCategoriaAssunto = checkAssunto = true;
    	return iniciarRelatorioGeralManifestacoes();
    }
    
    /**
	 * Inicia o processo de geração do relatório de manifestações por status.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/menu.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
    public String iniciarRelatorioManifestacoesPorStatus() {
    	checkStatusManifestacao = true;
    	return iniciarRelatorioGeralManifestacoes();
    }
    
    /**
	 * Inicia o processo de geração do relatório de manifestações por unidade resopnsável.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/menu.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
    public String iniciarRelatorioManifestacoesPorUnidadeResponsavel() {
    	checkUnidadeResponsavel = true;
    	return iniciarRelatorioGeralManifestacoes();
    }
    
    /**
     * Valida as datas informadas para geração do relatório.
     */
    private void validarDatas() {
		if(isEmpty(dataInicial))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Inicial");
		if(isEmpty(dataFinal))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Final");
		
		if(isNotEmpty(dataInicial) && isNotEmpty(dataFinal) && dataInicial.getTime() > dataFinal.getTime())
		    addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Data Inicial");
    }
    
    /**
     * Retorna todos os assuntos associados à categoria escolhida.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<AssuntoManifestacao> getAllAssuntosByCategoria() throws DAOException {
		AssuntoManifestacaoDao dao = getDAO(AssuntoManifestacaoDao.class);
		Collection<AssuntoManifestacao> assuntos = new ArrayList<AssuntoManifestacao>();
		
		try {
		    if(isNotEmpty(categoriaAssuntoManifestacao))
		    	assuntos = dao.getAllAssuntosAtivosByCategoria(categoriaAssuntoManifestacao);
		} finally {
		    dao.close();
		}
		
		return assuntos;
    }
    
    /**
     * Retorna todos os assuntos associados à categoria escolhida em forma de combo.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getAllAssuntosByCategoriaCombo() throws DAOException {
    	return toSelectItems(getAllAssuntosByCategoria(), "id", "descricao");
    }
    
    /**
     * Retorna todas as unidades que já tiveram manifestações encaminhadas.
     * 
     * @return
     * @throws DAOException
     */
    private Collection<Unidade> getAllUnidadesComManifestacoes() throws DAOException {
    	HistoricoManifestacaoDao dao = getDAO(HistoricoManifestacaoDao.class);
		Collection<Unidade> unidades = new ArrayList<Unidade>();
		
		try {
	    	unidades = dao.getAllUnidadesComManifestacoes();
		} finally {
		    dao.close();
		}
		
		return unidades;
    }
    
    /**
     * Retorna todas as unidades que já tiveram manifestações encaminhadas em forma de combo.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getAllUnidadesComManifestacoesCombo() throws DAOException {
    	return toSelectItems(getAllUnidadesComManifestacoes(), "id", "nome");
    }

	@Override
    public String getDirBase() {
    	return super.getDirBase() + "/relatorios";
    }

    public Collection<HistoricoManifestacao> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(Collection<HistoricoManifestacao> historicos) {
        this.historicos = historicos;
    }

    public boolean isCheckPeriodo() {
        return checkPeriodo;
    }

    public void setCheckPeriodo(boolean checkPeriodo) {
        this.checkPeriodo = checkPeriodo;
    }

    public boolean isCheckOrigem() {
        return checkOrigem;
    }

    public void setCheckOrigem(boolean checkOrigem) {
        this.checkOrigem = checkOrigem;
    }

    public boolean isCheckAssunto() {
		return checkAssunto;
	}

	public void setCheckAssunto(boolean checkAssunto) {
		this.checkAssunto = checkAssunto;
	}

	public boolean isCheckCategoriaAssunto() {
		return checkCategoriaAssunto;
	}

	public void setCheckCategoriaAssunto(boolean checkCategoriaAssunto) {
		this.checkCategoriaAssunto = checkCategoriaAssunto;
	}

	public boolean isCheckCategoriaSolicitante() {
		return checkCategoriaSolicitante;
	}

	public void setCheckCategoriaSolicitante(boolean checkCategoriaSolicitante) {
		this.checkCategoriaSolicitante = checkCategoriaSolicitante;
	}

	public boolean isCheckStatusManifestacao() {
		return checkStatusManifestacao;
	}

	public void setCheckStatusManifestacao(boolean checkStatusManifestacao) {
		this.checkStatusManifestacao = checkStatusManifestacao;
	}

	public boolean isCheckUnidadeResponsavel() {
		return checkUnidadeResponsavel;
	}

	public void setCheckUnidadeResponsavel(boolean checkUnidadeResponsavel) {
		this.checkUnidadeResponsavel = checkUnidadeResponsavel;
	}

	public boolean isSomenteNaoRespondidas() {
		return somenteNaoRespondidas;
	}

	public void setSomenteNaoRespondidas(boolean somenteNaoRespondidas) {
		this.somenteNaoRespondidas = somenteNaoRespondidas;
	}

	public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public CategoriaSolicitante getCategoriaSolicitante() {
        return categoriaSolicitante;
    }

    public void setCategoriaSolicitante(CategoriaSolicitante categoriaSolicitante) {
        this.categoriaSolicitante = categoriaSolicitante;
    }

    public OrigemManifestacao getOrigemManifestacao() {
        return origemManifestacao;
    }

    public void setOrigemManifestacao(OrigemManifestacao origemManifestacao) {
        this.origemManifestacao = origemManifestacao;
    }

    public StatusManifestacao getStatusManifestacao() {
        return statusManifestacao;
    }

    public void setStatusManifestacao(StatusManifestacao statusManifestacao) {
        this.statusManifestacao = statusManifestacao;
    }

    public CategoriaAssuntoManifestacao getCategoriaAssuntoManifestacao() {
        return categoriaAssuntoManifestacao;
    }

    public void setCategoriaAssuntoManifestacao(CategoriaAssuntoManifestacao categoriaAssuntoManifestacao) {
        this.categoriaAssuntoManifestacao = categoriaAssuntoManifestacao;
    }

    public AssuntoManifestacao getAssuntoManifestacao() {
        return assuntoManifestacao;
    }

    public void setAssuntoManifestacao(AssuntoManifestacao assuntoManifestacao) {
        this.assuntoManifestacao = assuntoManifestacao;
    }

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

}
