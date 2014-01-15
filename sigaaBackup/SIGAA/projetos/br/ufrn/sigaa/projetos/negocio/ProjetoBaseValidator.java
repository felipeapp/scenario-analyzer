/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.text.SimpleDateFormat;
import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.mensagens.MensagensExtensao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;



/*******************************************************************************
 * Classe com métodos de validação para os passos do cadastro de um projeto de 
 * ação acadêmica integrada.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class ProjetoBaseValidator {

    /**
     * Valida dimensão acadêmica da proposta.
     * 
     * @param projeto
     * @param lista
     * @throws DAOException
     */
    public static void validaDimensaoAcademica(Projeto projeto, ListaMensagens lista) throws DAOException {

	if (!projeto.isEnsino() && !projeto.isPesquisa() && !projeto.isExtensao()) {
	    lista.addErro("Dimensão acadêmica da proposta não especificada.");
	}
	
	if ( (!projeto.isEnsino() && !projeto.isPesquisa() && projeto.isExtensao()) 
		|| (!projeto.isEnsino() && projeto.isPesquisa() && !projeto.isExtensao())
		|| (projeto.isEnsino() && !projeto.isPesquisa() && !projeto.isExtensao()) ){
	    lista.addErro("Proposta deve abranger mais de uma dimensão acadêmica.");
	}
	
    }
    
    /**
     * Valida os dados gerais da proposta de ação integrada.
     * 
     * @param projeto
     * @param lista
     * @throws DAOException
     */
    public static void validaDadosGerais(Projeto projeto, ListaMensagens lista) throws DAOException {

	ValidatorUtil.validateMaxLength(projeto.getTitulo(), 400, "Título", lista);
	validateRequired(projeto.getTitulo(), "Título", lista);
	if (projeto.isInterno() && projeto.isFinanciamentoInterno()) {
	    validateRequired(projeto.getEdital(), "Edital", lista);
	    
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		try {
			if (!ValidatorUtil.isEmpty(projeto.getEdital())) {
			    if (!projeto.getEdital().isEmAberto() && 
			    		!dao.isProjetoPassouPorSituacao(projeto.getId(), new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO})) {
			    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			    	lista.addErro("Prazo para submissão de propostas para este edital é: " + sdf.format(projeto.getEdital().getInicioSubmissao()) + " até " + sdf.format(projeto.getEdital().getFimSubmissao()));
			    }
			}
		}finally {
			dao.close();
		}
	}
	validateRequired(projeto.getTotalDiscentesEnvovidos(), "Número de discentes envolvidos", lista);
	validateRequired(projeto.getBolsasSolicitadas(), "Número de Bolsas Solicitadas", lista);
	ValidatorUtil.validateMinValue(projeto.getTotalDiscentesEnvovidos(), 0, "Número de discentes envolvidos", lista);
	ValidatorUtil.validateMinValue(projeto.getBolsasSolicitadas(), 0, "Número de Bolsas Solicitadas", lista);
	validateRequired(projeto.getRenovacao(), "Renovação", lista);
	if (!projeto.isFinanciamentoInterno()) {
		validateRequired(projeto.getAno(), "Ano", lista);	
		validateRequired(projeto.getDataInicio(), "Data de Início", lista);
		validateRequired(projeto.getDataFim(), "Data de Fim", lista);
	}	
	
	validateRequired(projeto.getResumo(), "Resumo", lista);
	validateRequired(projeto.getJustificativa(), "Introdução/Justificativa", lista);
	validateRequired(projeto.getObjetivos(), "Objetivos", lista);
	validateRequired(projeto.getResultados(), "Resultados esperados", lista);
	validateRequired(projeto.getMetodologia(), "Metodologia", lista);
	validateRequired(projeto.getReferencias(), "Referências", lista);

	if ((projeto.getDataInicio() != null) && (projeto.getDataFim() != null)) {
	    if (projeto.getAno() < CalendarUtils.getAno(projeto.getDataInicio()) ||	projeto.getAno() > CalendarUtils.getAno(projeto.getDataFim())) {
	    	lista.addErro("Ano: Ano está fora do período de execução do projeto.");
	    }	    
	    
	    ValidatorUtil.validaOrdemTemporalDatas(projeto.getDataInicio(), projeto.getDataFim(), true, 
		    "Período do projeto: Data do término dever ser maior que a data de início.", lista);
	    
	    if ( (ValidatorUtil.isNotEmpty(projeto.getEdital())) && !(CalendarUtils.isDentroPeriodo(projeto.getEdital().getInicioRealizacao(), projeto.getEdital().getFimRealizacao(), projeto.getDataInicio()) 
	    		&& CalendarUtils.isDentroPeriodo(projeto.getEdital().getInicioRealizacao(), projeto.getEdital().getFimRealizacao(), projeto.getDataFim()) ) ) {
	    	lista.addErro("O período definido não está dentro do período de execução do edital ( " + 
	    		Formatador.getInstance().formatarData(projeto.getEdital().getInicioRealizacao()) + " à " + 
	    			Formatador.getInstance().formatarData(projeto.getEdital().getFimRealizacao()) + " ).");
		}
	    
	}

	validateRequired(projeto.getUnidade(), "Unidade Proponente", lista);
	validateRequired(projeto.getAreaConhecimentoCnpq(), "Área do CNPq", lista);
	validateRequired(projeto.getAbrangencia(), "Abrangência", lista);

	if (projeto.isFinanciamentoExterno()) {
	    validateRequired(projeto.getClassificacaoFinanciadora(), "Financiador", lista);
	}
	
	if (!projeto.isFinanciamentoExterno() && !projeto.isFinanciamentoInterno() 
		&& !projeto.isAutoFinanciado() && !projeto.isSemFinanciamento()) {
	    lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Natureza do Financiamento");
	}
	
	//TODO: validar projetos com títulos repetidos...
	
	if(projeto.getResumo() != null && projeto.getResumo().trim().length() > 15000)
		lista.addErro("O resumo deve ter no máximo 15.000 caracteres.");
	if(projeto.getJustificativa() != null && projeto.getJustificativa().trim().length() > 15000)
		lista.addErro("A introdução/justificativa deve ter no máximo 15.000 caracteres.");
	if(projeto.getObjetivos() != null && projeto.getObjetivos().trim().length() > 15000)
		lista.addErro("Os objetivos devem ter no máximo 15.000 caracteres.");
	if(projeto.getResultados() != null && projeto.getResultados().trim().length() > 15000)
		lista.addErro("Os resultados devem ter no máximo 15.000 caracteres.");
	if(projeto.getMetodologia() != null && projeto.getMetodologia().trim().length() > 15000)
		lista.addErro("A metodologia deve ter no máximo 15.000 caracteres.");
	if(projeto.getReferencias() != null && projeto.getReferencias().trim().length() > 15000)
		lista.addErro("As referências devem ter no máximo 15.000 caracteres.");

    }


    /**
     * Validar cronograma do projeto
     * 
     * @param projeto
     * @param lista
     */
    public static void validarCronograma(Projeto projeto, ListaMensagens lista) {
	ValidatorUtil.validateEmptyCollection("Informe ao menos uma atividade para o cronograma",projeto.getCronograma(), lista);
	for (CronogramaProjeto crono : projeto.getCronograma()) {
	    if (crono.getDescricao().trim().length() > 1000) {
		lista.addErro("A descricao da atividade <i>"	+ crono.getDescricao() + "<i> deve conter no máximo 1000 caracteres.");
	    }
	}
    }


    /**
     * Faz a validação do orçamento de uma ação de extensão.
     * 
     * @param orcamento
     * @param lista
     */
    public static void validaAdicionaOrcamento(OrcamentoDetalhado orcamento,  ListaMensagens lista) {
	validateRequired(orcamento.getDiscriminacao(), "Discriminação", lista);
	validateRequired(orcamento.getElementoDespesa(),"Selecione um elemento de despesa.", lista);
	validateRequired(orcamento.getValorUnitario(), "Valor Unitário", lista);
	ValidatorUtil.validateMinValue(orcamento.getValorUnitario(), 0.0, "Valor Unitário", lista);
	validateRequired(orcamento.getQuantidade(), "Quantidade", lista);
 	ValidatorUtil.validateMinValue(orcamento.getQuantidade(), 0.01d, "Quantidade", lista);
	ValidatorUtil.validateMinValue(orcamento.getValorUnitario(), 0.01d, "Valor Unitário", lista);
	if (orcamento.getValorTotal() >= new Double(1000000000)) {
	    lista.addErro("O valor da despesa não deve ultrapassar R$ 1.000.000.000.");
	}	
	validaLimiteOrcamentoEdital(orcamento.getProjeto(), lista);
    }

    /**
     * Verifica obrigatoriedade no envio de arquivos anexos ao projeto.
     * 
     * @param projeto
     * @param lista
     */
    public static void validaSubmissaoArquivos(final Projeto projeto, ListaMensagens lista) {
	if (projeto.isPesquisa()) {
	    if (projeto.isApoioGrupoPesquisa() || projeto.isApoioNovosPesquisadores()) {
		ValidatorUtil.validateEmptyCollection("Para projetos que envolvem as modalidades de Apoio a Grupos de Pesquisa e/ou Apoio a Novos Pesquisadores " +
			"deve-se anexar arquivo(s) contendo a " +
			"descrição completa do projeto segundo o modelo disponibilizado pela PROPESQ no edital.", projeto.getArquivos(), lista);
	    }
	}
    }
    

    /**
     * Verifica se o projeto já atingiu o limite máximo orçamentário definido no edital. 
     * 
     * @param atividade
     * @param lista
     */
    public static void validaLimiteOrcamentoEdital(final Projeto projeto, ListaMensagens lista) {
	if (projeto.isInterno() && !ValidatorUtil.isEmpty(projeto.getEdital())) {
	    if (projeto.getDimensaoProjeto() == 1) {
		ValidatorUtil.validateMaxValue(projeto.getTotalOrcamentoSolicitado(), projeto.getEdital().getPrimeiroLimiteOrcamentario() , "Valor Total do Orçamento", lista);
	    }
	    if (projeto.getDimensaoProjeto() == 2) {
		ValidatorUtil.validateMaxValue(projeto.getTotalOrcamentoSolicitado(), projeto.getEdital().getSegundoLimiteOrcamentario() , "Valor Total do Orçamento", lista);
	    }
	    if (projeto.getDimensaoProjeto() == 3) {
		ValidatorUtil.validateMaxValue(projeto.getTotalOrcamentoSolicitado(), projeto.getEdital().getTerceiroLimiteOrcamentario() , "Valor Total do Orçamento", lista);
	    }
	}
    }
    
    
    /**
     * Verifica se o projeto pode ser distribui para aprovação dos departamentos.
     * 
     * @param projeto
     * @param lista
     * @throws DAOException 
     */
    public static void validaPendenciasProjetoBase(final Projeto projeto, ListaMensagens lista) throws DAOException {	
	validaDimensaoAcademica(projeto, lista);	
	validaDadosGerais(projeto, lista);	
	validarCronograma(projeto, lista);	
	validaSubmissaoArquivos(projeto, lista);	
	MembroProjetoValidator.validaCoordenacaoAtiva(projeto.getEquipe(), lista);
	// Nova forma de ação acadêmica associada não obriga submissão de projetos separadamente 
	// validaEnvioProjetosAssociados(projeto, lista);
    }
    
    /**
     * Valida se todos os projetos associados ao projeto base foram enviados 
     * pelo coordenador.
     * 
     * @param projeto
     * @param lista
     * @throws DAOException
     */
    public static void validaEnvioProjetosAssociados(final Projeto projeto, ListaMensagens lista) throws DAOException {
	//Atualizando pendências para validação
	ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);	
	try {
	    ProjetoHelper.atualizarPendenciasProjetoBase(projeto, dao);
	    
	    if (projeto.isPesquisa()) {
		if (projeto.isApoioGrupoPesquisa() && !projeto.isApoioGrupoPesquisaSubmetido()) {
		    lista.addErro("Projeto de apoio a grupo de pesquisa: pendente de submissão.");
		}
		if (projeto.isApoioNovosPesquisadores() && !projeto.isApoioNovosPesquisadoresSubmetido()) {
		    lista.addErro("Projeto de apoio a novos pesquisadores: pendente de submissão.");
		}
		if (projeto.isIniciacaoCientifica() &&!projeto.isIniciacaoCientificaSubmetido()) {
		    lista.addErro("Projeto de bolsas de iniciação a científica: pendente de submissão.");
		}
	    }

	    if (projeto.isExtensao()) {
		if (projeto.isProgramaExtensao() && !projeto.isProgramaExtensaoSubmetido()) {
		    lista.addErro("Programa de extensão: pendente de submissão.");
		}
		if (projeto.isProjetoExtensao() &&!projeto.isProjetoExtensaoSubmetido()) {
		    lista.addErro("Projeto de extensão: pendente de submissão.");
		}
		if (projeto.isCursoExtensao() && !projeto.isCursoExtensaoSubmetido()) {
		    lista.addErro("Curso de extensão: pendente de submissão.");
		}
		if (projeto.isEventoExtensao() && !projeto.isEventoExtensaoSubmetido()) {
		    lista.addErro("Evento de extensão: pendente de submissão.");
		}
	    }

	    if (projeto.isEnsino()) {
		if (projeto.isProgramaMonitoria() && !projeto.isProgramaMonitoriaSubmetido()) {
		    lista.addErro("Projeto de monitoria: pendente de submissão.");
		}
		if (projeto.isMelhoriaQualidadeEnsino() && !projeto.isMelhoriaQualidadeEnsinoSubmetido()) {
		    lista.addErro("Projeto de melhoria da qualidade do ensino de graduação: pendente de submissão.");
		}
	    }
	}finally {
	    dao.close();
	}
    }
    
    
    /**
     *  Realiza validação quanto a vinculação de um projeto a unidade orçamentária.
     *  
     * @param projeto
     * @param lista
     * @throws DAOException
     */
    public static void validaVincularProjetoUnidadeOrcamentaria(final Projeto projeto, ListaMensagens lista) throws DAOException {
    	if (ValidatorUtil.isEmpty(projeto.getUnidadeOrcamentaria())) {
    		lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Orçamentária");
    	}
    	if (ValidatorUtil.isEmpty(projeto.getCoordenador())) {
    		lista.addErro("Para que uma Unidade Orcamentária seja vinculada a uma Ação de Extensão esta ação deverá ter obrigatoriamente um(a) Coordenador(a).");
    	}
    }

    
    /**
     * Realiza validações relativas a coordenação do projeto.
     * 
     * @param projeto
     * @param servidor
     * @param lista
     * @throws DAOException
     */
    public static void validaRestricoesCoordenacaoEdital(Projeto projeto, Servidor servidor, ListaMensagens lista) throws DAOException {

	if (!ValidatorUtil.isEmpty(projeto.getEdital()) && !ValidatorUtil.isEmpty(projeto.getEdital().getRestricaoCoordenacao())) {
	    // só servidores ativos
	    if (projeto.getEdital().getRestricaoCoordenacao().isApenasServidorAtivoCoordena()) {
		if ((servidor != null) && (!servidor.isPermanente())) {
		    lista.addErro("O edital vinculado a esta Ação Acadêmica possui restrições para coordenação: " +
		    		"apenas docentes do quadro permanente e em efetivo exercício de suas funções podem ser coordendores de projetos.");
		}			    
	    }
	    
	    if (projeto.getEdital().getRestricaoCoordenacao().isApenasTecnicoSuperiorCoordena()) {		
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		try {
		    //evitar erro de lazy
		    servidor = dao.findByPrimaryKey(servidor.getId(), Servidor.class);
		    
		    if ((servidor != null) && (!isNivelSuperior(servidor))) {
			lista.addErro("O edital vinculado a esta Ação Acadêmica possui restrições para coordenação: " +
			"apenas técnicos administrativos com nível superior podem ser coordendores de projetos.");
		    }
		    if (servidor.getEscolaridade() == null) {			
			lista.addMensagem(MensagensExtensao.SOLICITACAO_ATUALIZAR_ESCOLARIDADE, servidor.getNome());		
		    }	
		}finally {
		    dao.close();
		}
	    }

	    // docentes não podem coordenar
	    if (!projeto.getEdital().getRestricaoCoordenacao().isPermitirCoordenadorDocente()) {
		if ((servidor != null) && (servidor.isDocente())) {
		    lista.addErro("O edital vinculado a esta Ação Acadêmica possui restrições para coordenação: " +
		    		"docentes não podem coordenar projetos.");
		}			    
	    }

	    // técnicos não podem coordenar
	    if (!projeto.getEdital().getRestricaoCoordenacao().isPermitirCoordenadorTecnico()) {
		if ((servidor != null) && (servidor.getCategoria().getId() == Categoria.TECNICO_ADMINISTRATIVO)) {
		    lista.addErro("O edital vinculado a esta Ação Acadêmica possui restrições para coordenação: " +
		    		"técnicos administrativos não podem coordenar projetos.");
		}			    
	    }
	}
    }
    
    /**
     * Testa se o servidor tem nível superior
     * 
     * @param s
     * @return false caso o servidor não tenha escolaridade
     */
    private static boolean isNivelSuperior(Servidor s) {
	return (s.getEscolaridade() != null) && s.getEscolaridade().hasNivelSuperior();
    }
    
    /***
     * Verifica se a pessoa informada coordena o projeto informado.
     * 
     * @param projeto
     * @param pessoa
     * @return
     * @throws DAOException
     */
    public static boolean isCoordenadorProjeto(Projeto projeto, PessoaGeral pessoa) throws DAOException {
    	MembroProjetoDao dao = DAOFactory.getInstance().getDAO(MembroProjetoDao.class);
    	try {
	    	MembroProjeto coordenacao = dao.findCoordenadorAtualProjeto(projeto.getId());
	    	if (!ValidatorUtil.isEmpty(coordenacao)) {
	    		return coordenacao.getPessoa().getId() == pessoa.getId();
	    	} else {
	    		return false;
	    	}
    	}finally {
    		dao.close();
    	}
    }
    
    /**
     * Realiza validações relativas a remoção de projetos.
     *  
     * @param projeto
     * @param pessoa
     * @param lista
     * @return
     */
    public static void validaRemoverProjeto(Projeto projeto, PessoaGeral pessoa,  ListaMensagens lista) throws DAOException {
    	//@negocio: Somente o coordenador atual ou o autor pode remover o projeto.
    	MembroProjetoDao dao = DAOFactory.getInstance().getDAO(MembroProjetoDao.class);
    	try {
    		projeto = dao.findByPrimaryKey(projeto.getId(), Projeto.class, "id, registroEntrada.usuario.pessoa.id");
    		if (!isCoordenadorProjeto(projeto, pessoa) && 
    				(projeto.getRegistroEntrada().getUsuario().getPessoa().getId() != pessoa.getId())) {
    			lista.addErro("Operação restrita à coordenação atual do projeto.");
    		}
    	}finally {
    		dao.close();
    	}
    }

    /**
     * Realiza validações relativas a confirmação ou negação da execução de projetos.
     *  
     * @param projeto
     * @param pessoa
     * @param lista
     * @return
     */
    public static void validaExecucaoProjeto(Projeto projeto, PessoaGeral pessoa,  ListaMensagens lista ) throws DAOException {
    	//@negocio: Somente o coordenador atual pode confirmar ou negar a execução o projeto.
    	if (!isCoordenadorProjeto(projeto, pessoa)) {
    		lista.addErro("Operação restrita à coordenação atual do projeto.");
    	}
    }
    
    
	/**
	 * Verifica se existem Editais de Ações Associadas em aberto.
	 *  
	 * @param projeto
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaEditaisAbertos(Projeto projeto, ListaMensagens lista) throws DAOException {
		EditalDao dao = DAOFactory.getInstance().getDAO(EditalDao.class);
		try {
			Collection<Edital> editais = dao.findAbertos(1, Edital.ASSOCIADO);
			if (ValidatorUtil.isEmpty(editais)) {
				lista.addErro("Atualmente não existem editais abertos para Ações Acadêmicas Associadas.");
			}
		}finally {
			dao.close();
		}
	}

	/**
	 * Valida cadastro de projetos com títulos repetidos.
	 *  
	 * @param projeto
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaProjetosComMesmoTitulo(Projeto projeto, ListaMensagens lista) throws DAOException {
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		try {
			if (dao.existeProjetoComMesmoTitulo(projeto)) {
				lista.addErro("Já existe um Projeto cadastrado com o Título informado para este mesmo Ano de referência.");
			}
		}finally {
			dao.close();
		}
	}


    
    
}
