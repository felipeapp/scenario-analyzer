/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateEmptyCollection;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.RelatorioProjetoMonitorDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ComponenteCurricularMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocenteComponente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.negocio.MembroProjetoValidator;
import br.ufrn.sigaa.projetos.negocio.ProjetoBaseValidator;

/**
 * Classe com métodos de validação para os passos do cadastro de um projeto de
 * monitoria.
 * 
 * @author David Ricardo
 * @author ilueny santos
 * 
 * 
 */
public class ProjetoMonitoriaValidator {
	
	/** Valida os dados do projeto.
	 * @param projeto
	 * @param lista
	 */
	public static void validaDadosProjeto(ProjetoEnsino projeto, ListaMensagens lista) {

	    validateRequired(projeto.getTitulo(), "Título", lista);
	    validateRequired(projeto.getAno(), "Ano de Referência", lista);
	    validateRequired(projeto.getResumo(), "Resumo", lista);
	    validateRequired(projeto.getJustificativa(), "Justificativa e Diagnóstico", lista);
	    validateRequired(projeto.getObjetivos(), "Objetivos", lista);
	    validateRequired(projeto.getMetodologia(), "Metodologia", lista);
	    validateRequired(projeto.getAvaliacao(), "Avaliação", lista);
	    validateRequired(projeto.getResultados(), "Resultados", lista);
	    validateRequired(projeto.getReferencias(), "Referências", lista);
	    
	    if ( projeto.getValidaNovosCampos() != null &&  projeto.getValidaNovosCampos() ) {
		    validateRequired(projeto.getProduto(), "Produtos", lista);
		    validateRequired(projeto.getProcessoSeletivo(), "Processo Seletivo", lista);
	    }
	    
	    if ( projeto.getProjeto().isInterno() ) {
	    	validateRequired(projeto.getEditalMonitoria(), "Edital",	lista);
	    	
	    	if ((projeto.getTipoProjetoEnsino() != null) && (projeto.getTipoProjetoEnsino().getId() > 0)) {
	    		
	    		if (projeto.isProjetoMonitoria()) {
	    			validateRequired(projeto.getBolsasSolicitadas(), "Bolsas Solicitadas", lista);
	    			validateMinValue(projeto.getBolsasSolicitadas(), 1, "Bolsas Solicitadas", lista);
	    			
	    			if ((projeto.getBolsasSolicitadas() != null) && (projeto.getBolsasConcedidas() != null)) {
	    				if (projeto.getBolsasSolicitadas() < projeto.getBolsasConcedidas()) {
	    					lista.addErro("Bolsas solicitadas não pode ser menor que o total de bolsas concedidas ao projeto!");
	    				}
	    			}
	    		}
	    		
	    	} else {
	    		lista.addErro("Tipo de Projeto de Ensino deve ser informado!");
	    	}
	    	
	    } else {
	    	validateRequired(projeto.getProjeto().getDataInicio(), "Período Inicial", lista);
	    	validateRequired(projeto.getProjeto().getDataFim(), "Período Final", lista);
	    	ValidatorUtil.validaDataAnteriorIgual(projeto.getProjeto().getDataInicio(), projeto.getProjeto().getDataFim(), "Período Inicial", lista);
	    }
	    
	}

	/**
	 * Valida a inclusão de apenas um componente curricular no projeto.
	 * 
	 * 
	 * @param projeto
	 * @param lista
	 */
	public static void validaNovoComponenteCurricular(ComponenteCurricular componente, ProjetoEnsino projeto, ListaMensagens lista) {
	    
		if (ValidatorUtil.isEmpty(componente)) {
		    lista.addErro("Componente Curricular não encontrado.");
		} else {

		    if (ValidatorUtil.isEmpty(componente.getUnidade())) {
			lista.addErro("Este Componente Curricular não está corretamente configurado no sistema. "
				+ "Click no link 'Abrir Chamado', no canto superior direito da tela, e solicite sua configuração"
				+ "passando o Código e o Nome do Componente.");
		    }
		    for (ComponenteCurricularMonitoria ccm : projeto.getComponentesCurriculares()) {
			if (ccm.getDisciplina().equals(componente)) {
			    lista.addErro("Componente Curricular já adicionado ao projeto.");
			   break;
			}
		    }
		}
	}
	
	
	/** Valida os componentes curriculares do projeto.
	 * @param projeto
	 * @param componentes
	 * @param anoLetivoAtual
	 * @param periodoLetivoAtual
	 * @param lista
	 * @throws NegocioException
	 * @throws DAOException
	 */	
	public static void validaComponentesCurriculares(ProjetoEnsino projeto,
			Set<ComponenteCurricularMonitoria> componentes, int anoLetivoAtual,
			int periodoLetivoAtual, ListaMensagens lista)
			throws NegocioException, DAOException {

		validateEmptyCollection("É necessário incluir ao menos um Componente Curricular.", componentes, lista);
		for (ComponenteCurricularMonitoria comp : componentes) {
			if ((comp.getPlanoTrabalho() == null) || (comp.getPlanoTrabalho().trim().equals("")))
				lista.addErro("Atividades desenvolvidas pelo monitor da disciplina "+comp.getDisciplina().getCodigoNome()+": Campo obrigatório não informado.");
			
			if ( projeto.getValidaNovosCampos() != null && projeto.getValidaNovosCampos() ) {
				if (comp.getChDestinadaProjeto() == null || comp.getChDestinadaProjeto() == 0)
					lista.addErro("Carga horária semanal destinada ao projeto "+comp.getDisciplina().getCodigoNome()+": Campo obrigatório não informado.");
				if (comp.getChDestinadaProjeto() != null && comp.getChDestinadaProjeto() > 12)
					lista.addErro("Carga horária semanal destinada ao projeto "+comp.getDisciplina().getCodigoNome()+": deve ser menor ou igual à 12.");
				if ((comp.getAvaliacaoMonitor() == null) || (comp.getAvaliacaoMonitor().trim().equals("")))
					lista.addErro("Avaliação do Monitor "+comp.getDisciplina().getCodigoNome()+": Campo obrigatório não informado.");
			}
			
			if (lista.isErrorPresent())
				break;
		}
	}

	/** Valida a operação de adicionar um docente ao projeto.
	 * @param projeto
	 * @param compSelecionados
	 * @param equipeDocente
	 * @param dao
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaAdicionaDocente(ProjetoEnsino projeto,
			List<ComponenteCurricularMonitoria> compSelecionados,
			EquipeDocente equipeDocente, ListaMensagens lista) throws DAOException {
	
	    ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
	    try {

		Set<ComponenteCurricularMonitoria> componentes = projeto.getComponentesCurriculares();
		validateRequired(equipeDocente.getServidor().getId(), "Docente",lista);
		validateEmptyCollection("É necessário associar ao menos um Componente Curricular ao docente.",	compSelecionados, lista);
		if (equipeDocente != null && (!ValidatorUtil.isEmpty(equipeDocente.getServidor()))) {
			
		    	// orientador só incluído 1 vez com uma determinada disciplina
			for (ComponenteCurricularMonitoria cc : componentes) {
				for (EquipeDocenteComponente edc : cc.getDocentesComponentesValidos()) {
					if ((edc.getEquipeDocente().getServidor().getId() == equipeDocente.getServidor().getId())
							&& (compSelecionados.contains(cc) )) {
						lista.addErro("Orientador(a) já incluído(a) e relacionado(a) à uma Disciplina selecionada!");
						break;
					}
				}

				if ((!lista.isEmpty()) && (lista.size() > 0)) {
					break;
				}

			}

			// coordenadores..
			if (equipeDocente.isCoordenador()) {
			    // só pode um coordenador por projeto
			    for (ComponenteCurricularMonitoria cc : componentes) {
				for (EquipeDocenteComponente edc : cc.getDocentesComponentes()) {
				    if (edc.getEquipeDocente().isCoordenador()) {
					lista.addErro("Já existe um(a) coordenador(a) para o projeto.");
					break;
				    }
				}
				if ((!lista.isEmpty()) && (lista.size() > 0)) {
				    break;
				}
			    }
			    
			    //Restrições para coordenação do proejeto
			    ProjetoBaseValidator.validaRestricoesCoordenacaoEdital(projeto.getProjeto(), equipeDocente.getServidor(), lista);
			    
			}

			// docente deve ser do quadro permanente
			if ((equipeDocente.getServidor() != null) && (equipeDocente.getServidor().isSubstituto())) {
				lista.addErro("Somente docentes do quadro permanente da " + RepositorioDadosInstitucionais.getSiglaInstituicao() + " podem ser adicionados ao projeto.");
			}

			// em projetos de monitoria isolados o docente so pode estar em 2 projetos ativos ao mesmo tempo
			if (projeto.isProjetoIsolado()) {
			    
			    DiscenteMonitoriaValidator.validaMaximoProjetosDocente(equipeDocente, lista);
			}

			// Docente teve relatório de reprovado e não vai (Geladeira)
			// poder participar de novos projetos por 2 anos!
			if ( projeto.getProjeto().isInterno() )
				validaDocenteRelatorioProjetosAntigosNaoAprovados(equipeDocente, lista);

			// 'pendência de relatório!',.... docente não apresentou relatório do sid
			validaDocenteResumoSidProjetosAnteriores(equipeDocente, lista);

		}
	    }finally {
		dao.close();
	    }
	}

	/**
	 * Verifica se o docente informado possui relatórios do Seminário de Iniciação a Docência(SID) pendentes de
	 * entrega de projetos anteriores
	 * 
	 * @param docente
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDocenteResumoSidProjetosAnteriores(EquipeDocente docente, ListaMensagens lista) throws DAOException {

		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try {

			Collection<ProjetoEnsino> psm = dao.findValidosByServidor(docente.getServidor().getId(), null);
			for (ProjetoEnsino projetoEnsino : psm) {
				if (projetoEnsino.getAno() < (CalendarUtils.getAnoAtual() - 1)) {
					if (projetoEnsino.getResumosSid() == null) {
						lista.addErro("Docente não apresentou Resumo para o Seminário de Iniação a Docência (SID) do projeto: '"
												+ projetoEnsino.getAnoTitulo() + "'.");
					}
				}
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Verifica se o docente foi reprovado na avaliação dos relatórios final ou
	 * parcial dos projetos feitos anteriormente.
	 * 
	 * @param docente
	 * @param projeto
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDocenteRelatorioProjetosAntigosNaoAprovados(
			EquipeDocente docente, ListaMensagens lista)
			throws DAOException {

		RelatorioProjetoMonitorDao dao = DAOFactory.getInstance().getDAO(RelatorioProjetoMonitorDao.class);
		ProjetoMonitoriaDao projetoMonitoriaDao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		EditalMonitoria edital = null;
		if ( docente.getProjetoEnsino().getEditalMonitoria() != null )
			edital = dao.findByPrimaryKey(docente.getProjetoEnsino().getEditalMonitoria().getId(), EditalMonitoria.class);

		try {
			// ------------verificando relatórios reprovados----------------

			// verificando se o docente tem relatórios PARCIAIS reprovados no
			int anoRelatorioParcialInicio = edital == null ? CalendarUtils.getAno(docente.getProjetoEnsino().getProjeto().getDataInicio()) : edital.getAnoProjetoRelatorioParcialIncio();
			int anoRelatorioParcialFim = edital == null ? CalendarUtils.getAno(docente.getProjetoEnsino().getProjeto().getDataFim()) : edital.getAnoProjetoRelatorioParcialFim();
			Boolean coordenador = true; 

			Collection<RelatorioProjetoMonitoria> relatoriosReprovados = new ArrayList<RelatorioProjetoMonitoria>();

			relatoriosReprovados
					.addAll(dao.findByRelatorioProjetosDocente(
									docente.getServidor().getId(),
									new Integer[] { TipoSituacaoProjeto.MON_NAO_RENOVADO_PELA_COMISSAO_MONITORIA },
									anoRelatorioParcialInicio,
									anoRelatorioParcialFim,
									TipoRelatorioMonitoria.RELATORIO_PARCIAL, coordenador));

			// verificando relatórios FINAIS reprovados
			int anoRelatorioFinalInicio = edital == null ? CalendarUtils.getAno(docente.getProjetoEnsino().getProjeto().getDataFim()) : edital.getAnoProjetoRelatorioFinalIncio();
			int anoRelatorioFinalFim = edital == null ? CalendarUtils.getAno(docente.getProjetoEnsino().getProjeto().getDataFim()) : edital.getAnoProjetoRelatorioFinalFim();

			relatoriosReprovados
					.addAll(dao.findByRelatorioProjetosDocente(
									docente.getServidor().getId(),
									new Integer[] { TipoSituacaoProjeto.MON_NAO_RENOVADO_PELA_COMISSAO_MONITORIA },
									anoRelatorioFinalInicio,
									anoRelatorioFinalFim,
									TipoRelatorioMonitoria.RELATORIO_FINAL, coordenador));

			// tem relatórios reprovados?
			if (!relatoriosReprovados.isEmpty()) {
				lista.addErro("Este Docente teve pelo menos um Relatório de Projeto de Monitoria reprovado pela "
										+ "Pró-Reitoria de Graduação, portanto, não poderá participar de novos Projetos de Ensino por 2 anos!");
			}

			// -----------------Verificando relatórios enviados---------------

			// verificando se apresentou algum relatório PARCIAL
			// todos os projetos onde o docente está ativo
			Collection<ProjetoEnsino> projetosRelatorioParcialDocente = projetoMonitoriaDao.
					findMeusProjetosMonitoria(docente.getServidor().getId(), coordenador, anoRelatorioParcialInicio, anoRelatorioParcialFim);

			if ((projetosRelatorioParcialDocente != null)
					&& (!projetosRelatorioParcialDocente.isEmpty())) {

				for (ProjetoEnsino projetoEnsino : projetosRelatorioParcialDocente) {

					// enviou relatórios parciais?
					if ((projetoEnsino.getRelatoriosParciais() == null) || (projetoEnsino.getRelatoriosParciais().isEmpty())) {
						lista.addMensagem(MensagensMonitoria.DOCENTE_PENDENTE_RELATORIO_PARCIAL, anoRelatorioParcialInicio,anoRelatorioParcialFim);
						break;
					}

				}
			}

			// verificando se apresentou algum relatório FINAL
			// todos os projetos onde o docente está ativo
			Collection<ProjetoEnsino> projetosRelatorioFinalDocente = projetoMonitoriaDao
					.findMeusProjetosMonitoria(docente.getServidor().getId(), coordenador, anoRelatorioFinalInicio, anoRelatorioFinalFim);

			if ((projetosRelatorioFinalDocente != null)
					&& (!projetosRelatorioFinalDocente.isEmpty())) {

				for (ProjetoEnsino projetoEnsino : projetosRelatorioFinalDocente) {

					// enviou relatórios finais?
					if ((projetoEnsino.getRelatoriosFinais() == null) || (projetoEnsino.getRelatoriosFinais().isEmpty())) {
						lista.addMensagem(MensagensMonitoria.DOCENTE_PENDENTE_RELATORIO_PARCIAL, anoRelatorioFinalInicio,anoRelatorioFinalFim);
						break;
					}
				}
			}

		} finally {
		    	projetoMonitoriaDao.close();
			dao.close();
		}

	}

	/**
	 * Valida componentes curriculares sem docentes responsáveis.
	 * 
	 * @param projeto
	 * @param verificarCoordenador
	 * @param lista
	 */
	public static void validaComponentesCurricularesSemDocentes(ProjetoEnsino projeto, ListaMensagens lista) {

		Set<ComponenteCurricularMonitoria> componentes = projeto.getComponentesCurriculares();

		for (ComponenteCurricularMonitoria cc : componentes) {
			validateEmptyCollection("É necessário incluir ao menos um Docente para cada Componente Curricular.",
					cc.getDocentesComponentes(), lista);
			if ((lista != null) && (lista.size() > 0)) {
				break;
			}
		}		
	}
	
	/**
	 * Verifica se existe um coordenador no projeto de monitoria e se
	 * este servidor possui e-mail cadastrado no sistema.
	 * 
	 * @param projetoEnsino
	 * @param lista
	 */
	public static void validaCoordenador(ProjetoEnsino projetoEnsino, Servidor servidorNovoCoordenador, ListaMensagens lista){
		if (ValidatorUtil.isNotEmpty(projetoEnsino.getProjeto().getCoordenador())) {
			validateRequired(projetoEnsino.getProjeto().getCoordenador().getServidor(), "Coordenador(a)",lista);
			
			if (ValidatorUtil.isNotEmpty(projetoEnsino.getProjeto().getCoordenador().getServidor())) {
				MembroProjetoValidator.validaCoordenacaoComEmail(projetoEnsino.getProjeto().getCoordenador().getServidor(), lista);

				if (projetoEnsino.isProjetoAssociado() && 
						!projetoEnsino.getProjeto().coordenaEsteProjeto(servidorNovoCoordenador)) {
					lista.addErro("Coordenador(a) do Projeto de Ensino deve ser "  + projetoEnsino.getProjeto().getCoordenador().getPessoa().getNome() +  " (Coordenador(a) da Ação Acadêmica Associada)");
				}		
			}
		}
	}
	
	
	/**
	 * Faz a validação de um Edital aberto. 
	 * 
	 * 
	 * @param projeto
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaEditaisAbertos(ProjetoEnsino projeto, ListaMensagens lista) throws DAOException {
	    
	    ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
	    try {
        	    Collection<EditalMonitoria> editais = dao.findByExactField(EditalMonitoria.class, "edital.tipo", projeto.getTipoEdital());
        	    boolean aberto = false;
        	    for (EditalMonitoria edital : editais) {
        		aberto = edital.isEmAberto();
        		if (aberto) {		    
        		    break;
        		}
        	    }		
        	    if (!aberto) {
        	    	lista.addErro("Não existem editais em aberto para o tipo de proposta selecionada.");
        	    }
	    }finally {
	     dao.close();
           }
	}
	
	/**
	 * Verifica se o projeto de monitoria pode ser removido.
	 * 
	 * @param projeto
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaRemoverProjeto(ProjetoEnsino projeto, ListaMensagens lista) throws DAOException {
	    //Verificando se existem discentes ativos.
	    DiscenteMonitoriaDao dao = DAOFactory.getInstance().getDAO(DiscenteMonitoriaDao.class);
	    try {
		Collection<DiscenteMonitoria> discentes = dao.findAtivosByProjeto(projeto.getId());
		if (!ValidatorUtil.isEmpty(discentes)) {
		    lista.addMensagem(MensagensMonitoria.FINALIZAR_MONITORES_REMOVER_PROJETO);
		}
	    }finally {
		dao.close();
	    }
	}
	
	
	/**
	 * Verifica se ainda existem projetos de monitoria com avaliações discrepantes.
	 * 
	 * @param edital
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaAvaliacoesDiscrepantes(EditalMonitoria edital, ListaMensagens lista) throws DAOException {
		AvaliacaoMonitoriaDao dao = DAOFactory.getInstance().getDAO(AvaliacaoMonitoriaDao.class);
		try {
			Collection<ProjetoEnsino> projetos = dao.findProjetosDiscrepantes(TipoSituacaoProjeto.MON_AVALIADO, edital);		
			if(!ValidatorUtil.isEmpty(projetos)){
				lista.addWarning("Ainda existem projetos de monitoria pendentes de avaliação.");		
			}
		}finally {
			dao.close();
		}
	}

	
	/**
	 * Valida relação existente entre discentes e orientadores do projeto.
	 * 
	 * @param edital
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaRelacaoDiscenteOrientadores(ProjetoEnsino projetoEnsino, ListaMensagens lista) throws DAOException {
		if ( projetoEnsino.getProjeto().isInterno()  ) {
			int relacao = projetoEnsino.getEditalMonitoria().getRelacaoDiscenteOrientador();
			Integer bolsasSolicitadas = projetoEnsino.getBolsasSolicitadas();
			int minOrientadores = ((Double) Math.ceil(bolsasSolicitadas * 1.0 / relacao)).intValue();

			//Considera docentes transientes.
			Set<EquipeDocente> docentes = new HashSet<EquipeDocente>();
			for (ComponenteCurricularMonitoria ccm : projetoEnsino.getComponentesCurriculares()) {
				for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
					docentes.add(edc.getEquipeDocente());
				}
			}

			if(docentes.size() < minOrientadores) {
				lista.addErro("Quantidade de Orientadores cadastrados é insuficiente para manter a relação estabelecida no edital onde cada docente poderá orientar no máximo " + relacao + " discentes.");
				lista.addErro("Este projeto deve ter no mínimo " + minOrientadores + " docentes para manter a quantidade de bolsas solicitadas (" +bolsasSolicitadas+ ").");
				lista.addErro("Ajuste a quantidade de bolsas solicitadas ou cadastre mais docentes no projeto.");
			}
		}
	}
	
	/**
	 * Faz a validação do orçamento de um projeto de monitoria
	 * 
	 * 
	 * @param atividade
	 * @param orcamento
	 * @param lista
	 */
	public static void validaAdicionaOrcamento(ProjetoEnsino projetoEnsino,	OrcamentoDetalhado orcamento, ListaMensagens lista) {

		validateRequired(orcamento.getDiscriminacao(), "Discriminação", lista);
		validateRequired(orcamento.getElementoDespesa(), "Selecione um elemento de despesa.", lista);
		validateRequired(orcamento.getQuantidade(), "Quantidade", lista);
		validateRequired(orcamento.getValorUnitario(), "Valor Unitário", lista);

		if (orcamento.getElementoDespesa() != null) {
			validateRequiredId(orcamento.getElementoDespesa().getId(), "Selecione um elemento de despesa.", lista);
			
			if(! orcamento.getElementoDespesa().isPermiteValorFracionado()){
				if ( orcamento.getQuantidade() % 1 != 0 ){
					lista.addErro("A quantidade não pode ser um valor fracionado.");
				}
			}
		}
		
		if ((orcamento.getQuantidade() != null)	&& (orcamento.getQuantidade() == 0)) {
			lista.addErro("Quantidade deve ser maior que 0 (zero)");
		}
		if (orcamento.getValorUnitario() == 0) {
			lista.addErro("Valor Unitário deve ser maior que 0 (zero)");
		}
	}
	
	/**
	 * Faz a validação do orçamento de um projeto de monitoria
	 * 
	 * 
	 * @param atividade
	 * @param orcamento
	 * @param lista
	 */
	public static void validaOrcamentos(ProjetoEnsino projetoEnsino, ListaMensagens lista) {

		for (OrcamentoDetalhado orcamento : projetoEnsino.getOrcamentosDetalhados()){
		
			if (orcamento.getDiscriminacao() == null)
				lista.addErro("Necessário incluir a discriminação para todas as despesas.");
			else {
				
				if (orcamento.getElementoDespesa() == null || orcamento.getElementoDespesa().getId() <= 0)
					lista.addErro("Selecione um elemento de despesa para "+ orcamento.getDiscriminacao());
				validateRequired(orcamento.getQuantidade(), "Selecione a quantidade para "+ orcamento.getDiscriminacao(), lista);
				validateRequired(orcamento.getValorUnitario(),"Selecione o valor unitário para "+ orcamento.getDiscriminacao(), lista);
		
				if (orcamento.getElementoDespesa() != null) {
					
					if(! orcamento.getElementoDespesa().isPermiteValorFracionado()){
						if ( orcamento.getQuantidade() % 1 != 0 ){
							lista.addErro("A quantidade não pode ser um valor fracionado para "+ orcamento.getDiscriminacao());
						}
					}
				}
				
				if ((orcamento.getQuantidade() != null)	&& (orcamento.getQuantidade() == 0)) {
					lista.addErro("Quantidade deve ser maior que 0 (zero) para "+ orcamento.getDiscriminacao());
				}
				if (orcamento.getValorUnitario() == 0) {
					lista.addErro("Valor Unitário deve ser maior que 0 (zero) para "+ orcamento.getDiscriminacao());
				}
			}
		}
	}

}
