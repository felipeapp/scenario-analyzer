/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe com m�todos de valida��o para os passos do cadastro de um projeto de 
 * a��o acad�mica integrada.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class ProjetoBaseValidator {

    /**
     * Valida dimens�o acad�mica da proposta.
     * 
     * @param projeto
     * @param lista
     * @throws DAOException
     */
    public static void validaDimensaoAcademica(Projeto projeto, ListaMensagens lista) throws DAOException {

	if (!projeto.isEnsino() && !projeto.isPesquisa() && !projeto.isExtensao()) {
	    lista.addErro("Dimens�o acad�mica da proposta n�o especificada.");
	}
	
	if ( (!projeto.isEnsino() && !projeto.isPesquisa() && projeto.isExtensao()) 
		|| (!projeto.isEnsino() && projeto.isPesquisa() && !projeto.isExtensao())
		|| (projeto.isEnsino() && !projeto.isPesquisa() && !projeto.isExtensao()) ){
	    lista.addErro("Proposta deve abranger mais de uma dimens�o acad�mica.");
	}
	
    }
    
    /**
     * Valida os dados gerais da proposta de a��o integrada.
     * 
     * @param projeto
     * @param lista
     * @throws DAOException
     */
    public static void validaDadosGerais(Projeto projeto, ListaMensagens lista) throws DAOException {

	ValidatorUtil.validateMaxLength(projeto.getTitulo(), 400, "T�tulo", lista);
	validateRequired(projeto.getTitulo(), "T�tulo", lista);
	if (projeto.isInterno() && projeto.isFinanciamentoInterno()) {
	    validateRequired(projeto.getEdital(), "Edital", lista);
	    
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		try {
			if (!ValidatorUtil.isEmpty(projeto.getEdital())) {
			    if (!projeto.getEdital().isEmAberto() && 
			    		!dao.isProjetoPassouPorSituacao(projeto.getId(), new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO})) {
			    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			    	lista.addErro("Prazo para submiss�o de propostas para este edital �: " + sdf.format(projeto.getEdital().getInicioSubmissao()) + " at� " + sdf.format(projeto.getEdital().getFimSubmissao()));
			    }
			}
		}finally {
			dao.close();
		}
	}
	validateRequired(projeto.getTotalDiscentesEnvovidos(), "N�mero de discentes envolvidos", lista);
	validateRequired(projeto.getBolsasSolicitadas(), "N�mero de Bolsas Solicitadas", lista);
	ValidatorUtil.validateMinValue(projeto.getTotalDiscentesEnvovidos(), 0, "N�mero de discentes envolvidos", lista);
	ValidatorUtil.validateMinValue(projeto.getBolsasSolicitadas(), 0, "N�mero de Bolsas Solicitadas", lista);
	validateRequired(projeto.getRenovacao(), "Renova��o", lista);
	if (!projeto.isFinanciamentoInterno()) {
		validateRequired(projeto.getAno(), "Ano", lista);	
		validateRequired(projeto.getDataInicio(), "Data de In�cio", lista);
		validateRequired(projeto.getDataFim(), "Data de Fim", lista);
	}	
	
	validateRequired(projeto.getResumo(), "Resumo", lista);
	validateRequired(projeto.getJustificativa(), "Introdu��o/Justificativa", lista);
	validateRequired(projeto.getObjetivos(), "Objetivos", lista);
	validateRequired(projeto.getResultados(), "Resultados esperados", lista);
	validateRequired(projeto.getMetodologia(), "Metodologia", lista);
	validateRequired(projeto.getReferencias(), "Refer�ncias", lista);

	if ((projeto.getDataInicio() != null) && (projeto.getDataFim() != null)) {
	    if (projeto.getAno() < CalendarUtils.getAno(projeto.getDataInicio()) ||	projeto.getAno() > CalendarUtils.getAno(projeto.getDataFim())) {
	    	lista.addErro("Ano: Ano est� fora do per�odo de execu��o do projeto.");
	    }	    
	    
	    ValidatorUtil.validaOrdemTemporalDatas(projeto.getDataInicio(), projeto.getDataFim(), true, 
		    "Per�odo do projeto: Data do t�rmino dever ser maior que a data de in�cio.", lista);
	    
	    if ( (ValidatorUtil.isNotEmpty(projeto.getEdital())) && !(CalendarUtils.isDentroPeriodo(projeto.getEdital().getInicioRealizacao(), projeto.getEdital().getFimRealizacao(), projeto.getDataInicio()) 
	    		&& CalendarUtils.isDentroPeriodo(projeto.getEdital().getInicioRealizacao(), projeto.getEdital().getFimRealizacao(), projeto.getDataFim()) ) ) {
	    	lista.addErro("O per�odo definido n�o est� dentro do per�odo de execu��o do edital ( " + 
	    		Formatador.getInstance().formatarData(projeto.getEdital().getInicioRealizacao()) + " � " + 
	    			Formatador.getInstance().formatarData(projeto.getEdital().getFimRealizacao()) + " ).");
		}
	    
	}

	validateRequired(projeto.getUnidade(), "Unidade Proponente", lista);
	validateRequired(projeto.getAreaConhecimentoCnpq(), "�rea do CNPq", lista);
	validateRequired(projeto.getAbrangencia(), "Abrang�ncia", lista);

	if (projeto.isFinanciamentoExterno()) {
	    validateRequired(projeto.getClassificacaoFinanciadora(), "Financiador", lista);
	}
	
	if (!projeto.isFinanciamentoExterno() && !projeto.isFinanciamentoInterno() 
		&& !projeto.isAutoFinanciado() && !projeto.isSemFinanciamento()) {
	    lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Natureza do Financiamento");
	}
	
	//TODO: validar projetos com t�tulos repetidos...
	
	if(projeto.getResumo() != null && projeto.getResumo().trim().length() > 15000)
		lista.addErro("O resumo deve ter no m�ximo 15.000 caracteres.");
	if(projeto.getJustificativa() != null && projeto.getJustificativa().trim().length() > 15000)
		lista.addErro("A introdu��o/justificativa deve ter no m�ximo 15.000 caracteres.");
	if(projeto.getObjetivos() != null && projeto.getObjetivos().trim().length() > 15000)
		lista.addErro("Os objetivos devem ter no m�ximo 15.000 caracteres.");
	if(projeto.getResultados() != null && projeto.getResultados().trim().length() > 15000)
		lista.addErro("Os resultados devem ter no m�ximo 15.000 caracteres.");
	if(projeto.getMetodologia() != null && projeto.getMetodologia().trim().length() > 15000)
		lista.addErro("A metodologia deve ter no m�ximo 15.000 caracteres.");
	if(projeto.getReferencias() != null && projeto.getReferencias().trim().length() > 15000)
		lista.addErro("As refer�ncias devem ter no m�ximo 15.000 caracteres.");

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
		lista.addErro("A descricao da atividade <i>"	+ crono.getDescricao() + "<i> deve conter no m�ximo 1000 caracteres.");
	    }
	}
    }


    /**
     * Faz a valida��o do or�amento de uma a��o de extens�o.
     * 
     * @param orcamento
     * @param lista
     */
    public static void validaAdicionaOrcamento(OrcamentoDetalhado orcamento,  ListaMensagens lista) {
	validateRequired(orcamento.getDiscriminacao(), "Discrimina��o", lista);
	validateRequired(orcamento.getElementoDespesa(),"Selecione um elemento de despesa.", lista);
	validateRequired(orcamento.getValorUnitario(), "Valor Unit�rio", lista);
	ValidatorUtil.validateMinValue(orcamento.getValorUnitario(), 0.0, "Valor Unit�rio", lista);
	validateRequired(orcamento.getQuantidade(), "Quantidade", lista);
 	ValidatorUtil.validateMinValue(orcamento.getQuantidade(), 0.01d, "Quantidade", lista);
	ValidatorUtil.validateMinValue(orcamento.getValorUnitario(), 0.01d, "Valor Unit�rio", lista);
	if (orcamento.getValorTotal() >= new Double(1000000000)) {
	    lista.addErro("O valor da despesa n�o deve ultrapassar R$ 1.000.000.000.");
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
			"descri��o completa do projeto segundo o modelo disponibilizado pela PROPESQ no edital.", projeto.getArquivos(), lista);
	    }
	}
    }
    

    /**
     * Verifica se o projeto j� atingiu o limite m�ximo or�ament�rio definido no edital. 
     * 
     * @param atividade
     * @param lista
     */
    public static void validaLimiteOrcamentoEdital(final Projeto projeto, ListaMensagens lista) {
	if (projeto.isInterno() && !ValidatorUtil.isEmpty(projeto.getEdital())) {
	    if (projeto.getDimensaoProjeto() == 1) {
		ValidatorUtil.validateMaxValue(projeto.getTotalOrcamentoSolicitado(), projeto.getEdital().getPrimeiroLimiteOrcamentario() , "Valor Total do Or�amento", lista);
	    }
	    if (projeto.getDimensaoProjeto() == 2) {
		ValidatorUtil.validateMaxValue(projeto.getTotalOrcamentoSolicitado(), projeto.getEdital().getSegundoLimiteOrcamentario() , "Valor Total do Or�amento", lista);
	    }
	    if (projeto.getDimensaoProjeto() == 3) {
		ValidatorUtil.validateMaxValue(projeto.getTotalOrcamentoSolicitado(), projeto.getEdital().getTerceiroLimiteOrcamentario() , "Valor Total do Or�amento", lista);
	    }
	}
    }
    
    
    /**
     * Verifica se o projeto pode ser distribui para aprova��o dos departamentos.
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
	// Nova forma de a��o acad�mica associada n�o obriga submiss�o de projetos separadamente 
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
	//Atualizando pend�ncias para valida��o
	ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);	
	try {
	    ProjetoHelper.atualizarPendenciasProjetoBase(projeto, dao);
	    
	    if (projeto.isPesquisa()) {
		if (projeto.isApoioGrupoPesquisa() && !projeto.isApoioGrupoPesquisaSubmetido()) {
		    lista.addErro("Projeto de apoio a grupo de pesquisa: pendente de submiss�o.");
		}
		if (projeto.isApoioNovosPesquisadores() && !projeto.isApoioNovosPesquisadoresSubmetido()) {
		    lista.addErro("Projeto de apoio a novos pesquisadores: pendente de submiss�o.");
		}
		if (projeto.isIniciacaoCientifica() &&!projeto.isIniciacaoCientificaSubmetido()) {
		    lista.addErro("Projeto de bolsas de inicia��o a cient�fica: pendente de submiss�o.");
		}
	    }

	    if (projeto.isExtensao()) {
		if (projeto.isProgramaExtensao() && !projeto.isProgramaExtensaoSubmetido()) {
		    lista.addErro("Programa de extens�o: pendente de submiss�o.");
		}
		if (projeto.isProjetoExtensao() &&!projeto.isProjetoExtensaoSubmetido()) {
		    lista.addErro("Projeto de extens�o: pendente de submiss�o.");
		}
		if (projeto.isCursoExtensao() && !projeto.isCursoExtensaoSubmetido()) {
		    lista.addErro("Curso de extens�o: pendente de submiss�o.");
		}
		if (projeto.isEventoExtensao() && !projeto.isEventoExtensaoSubmetido()) {
		    lista.addErro("Evento de extens�o: pendente de submiss�o.");
		}
	    }

	    if (projeto.isEnsino()) {
		if (projeto.isProgramaMonitoria() && !projeto.isProgramaMonitoriaSubmetido()) {
		    lista.addErro("Projeto de monitoria: pendente de submiss�o.");
		}
		if (projeto.isMelhoriaQualidadeEnsino() && !projeto.isMelhoriaQualidadeEnsinoSubmetido()) {
		    lista.addErro("Projeto de melhoria da qualidade do ensino de gradua��o: pendente de submiss�o.");
		}
	    }
	}finally {
	    dao.close();
	}
    }
    
    
    /**
     *  Realiza valida��o quanto a vincula��o de um projeto a unidade or�ament�ria.
     *  
     * @param projeto
     * @param lista
     * @throws DAOException
     */
    public static void validaVincularProjetoUnidadeOrcamentaria(final Projeto projeto, ListaMensagens lista) throws DAOException {
    	if (ValidatorUtil.isEmpty(projeto.getUnidadeOrcamentaria())) {
    		lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Or�ament�ria");
    	}
    	if (ValidatorUtil.isEmpty(projeto.getCoordenador())) {
    		lista.addErro("Para que uma Unidade Orcament�ria seja vinculada a uma A��o de Extens�o esta a��o dever� ter obrigatoriamente um(a) Coordenador(a).");
    	}
    }

    
    /**
     * Realiza valida��es relativas a coordena��o do projeto.
     * 
     * @param projeto
     * @param servidor
     * @param lista
     * @throws DAOException
     */
    public static void validaRestricoesCoordenacaoEdital(Projeto projeto, Servidor servidor, ListaMensagens lista) throws DAOException {

	if (!ValidatorUtil.isEmpty(projeto.getEdital()) && !ValidatorUtil.isEmpty(projeto.getEdital().getRestricaoCoordenacao())) {
	    // s� servidores ativos
	    if (projeto.getEdital().getRestricaoCoordenacao().isApenasServidorAtivoCoordena()) {
		if ((servidor != null) && (!servidor.isPermanente())) {
		    lista.addErro("O edital vinculado a esta A��o Acad�mica possui restri��es para coordena��o: " +
		    		"apenas docentes do quadro permanente e em efetivo exerc�cio de suas fun��es podem ser coordendores de projetos.");
		}			    
	    }
	    
	    if (projeto.getEdital().getRestricaoCoordenacao().isApenasTecnicoSuperiorCoordena()) {		
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		try {
		    //evitar erro de lazy
		    servidor = dao.findByPrimaryKey(servidor.getId(), Servidor.class);
		    
		    if ((servidor != null) && (!isNivelSuperior(servidor))) {
			lista.addErro("O edital vinculado a esta A��o Acad�mica possui restri��es para coordena��o: " +
			"apenas t�cnicos administrativos com n�vel superior podem ser coordendores de projetos.");
		    }
		    if (servidor.getEscolaridade() == null) {			
			lista.addMensagem(MensagensExtensao.SOLICITACAO_ATUALIZAR_ESCOLARIDADE, servidor.getNome());		
		    }	
		}finally {
		    dao.close();
		}
	    }

	    // docentes n�o podem coordenar
	    if (!projeto.getEdital().getRestricaoCoordenacao().isPermitirCoordenadorDocente()) {
		if ((servidor != null) && (servidor.isDocente())) {
		    lista.addErro("O edital vinculado a esta A��o Acad�mica possui restri��es para coordena��o: " +
		    		"docentes n�o podem coordenar projetos.");
		}			    
	    }

	    // t�cnicos n�o podem coordenar
	    if (!projeto.getEdital().getRestricaoCoordenacao().isPermitirCoordenadorTecnico()) {
		if ((servidor != null) && (servidor.getCategoria().getId() == Categoria.TECNICO_ADMINISTRATIVO)) {
		    lista.addErro("O edital vinculado a esta A��o Acad�mica possui restri��es para coordena��o: " +
		    		"t�cnicos administrativos n�o podem coordenar projetos.");
		}			    
	    }
	}
    }
    
    /**
     * Testa se o servidor tem n�vel superior
     * 
     * @param s
     * @return false caso o servidor n�o tenha escolaridade
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
     * Realiza valida��es relativas a remo��o de projetos.
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
    			lista.addErro("Opera��o restrita � coordena��o atual do projeto.");
    		}
    	}finally {
    		dao.close();
    	}
    }

    /**
     * Realiza valida��es relativas a confirma��o ou nega��o da execu��o de projetos.
     *  
     * @param projeto
     * @param pessoa
     * @param lista
     * @return
     */
    public static void validaExecucaoProjeto(Projeto projeto, PessoaGeral pessoa,  ListaMensagens lista ) throws DAOException {
    	//@negocio: Somente o coordenador atual pode confirmar ou negar a execu��o o projeto.
    	if (!isCoordenadorProjeto(projeto, pessoa)) {
    		lista.addErro("Opera��o restrita � coordena��o atual do projeto.");
    	}
    }
    
    
	/**
	 * Verifica se existem Editais de A��es Associadas em aberto.
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
				lista.addErro("Atualmente n�o existem editais abertos para A��es Acad�micas Associadas.");
			}
		}finally {
			dao.close();
		}
	}

	/**
	 * Valida cadastro de projetos com t�tulos repetidos.
	 *  
	 * @param projeto
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaProjetosComMesmoTitulo(Projeto projeto, ListaMensagens lista) throws DAOException {
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		try {
			if (dao.existeProjetoComMesmoTitulo(projeto)) {
				lista.addErro("J� existe um Projeto cadastrado com o T�tulo informado para este mesmo Ano de refer�ncia.");
			}
		}finally {
			dao.close();
		}
	}


    
    
}
