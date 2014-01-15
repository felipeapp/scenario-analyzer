/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/11/2006
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.rh.dominio.Situacao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ColaboradorVoluntarioDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteExterno;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.mensagens.MensagensExtensao;
import br.ufrn.sigaa.mensagens.MensagensProjetos;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;



/*******************************************************************************
 * Classe com métodos de validação alteração de um membro do projeto de extensão.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class MembroProjetoValidator {

	/**
	 * Verifica se o projeto tem ao menos um Coordenador.
	 * 
	 * @param membros
	 * @param lista
	 */
	public static void validaCoordenacaoAtiva(Collection<MembroProjeto> membros, ListaMensagens lista) {
		boolean coordenadorOK = false;
		for (MembroProjeto membro : membros) {
			membro.getProjeto().getId();
			if (membro.isCoordenadorAtivo()) {
				coordenadorOK = true;
				break;
			}
		}
		if (!coordenadorOK) {
			lista.addMensagem(MensagensExtensao.COORDENADOR_ACAO);
		}
	}

	/**
	 * Verifica se o membro é um docente ou técnicos-administrativo com nível superior do quadro
	 * permanente da UFRN podem ser Coordenadores de ações de extensão.
	 * @throws DAOException 
	 * 
	 */
	public static void validaCoordenacao(MembroProjeto m, ListaMensagens lista) throws DAOException {	    
		if (!m.getCategoriaMembro().isExterno() && (m.isCoordenador())) {			
			// Ação sem edital. Serão realizadas validações genéricas para um coordenador de projetos.
			if (ValidatorUtil.isEmpty(m.getProjeto().getEdital())) {
				if(!m.getServidor().isPermanente()) {
					lista.addMensagem(MensagensExtensao.REQUISITOS_COORDENADOR_ACAO);
				}

				if (!(( m.isCategoriaDocente() || ( m.isCategoriaServidor() && isNivelSuperior(m.getServidor()))))) {
					lista.addMensagem(MensagensExtensao.REQUISITOS_COORDENADOR_ACAO);
				}

				if (m.getServidor().getEscolaridade() == null) {			
					lista.addMensagem(MensagensExtensao.SOLICITACAO_ATUALIZAR_ESCOLARIDADE,m.getServidor().getNome());		
				}

			// Ação com edital vinculado. Serão realizadas validações personalizadas de coordenação.
			}else {
				ProjetoBaseValidator.validaRestricoesCoordenacaoEdital(m.getProjeto(), m.getServidor(), lista);
			}
			validaCoordenacaoComEmail(m.getServidor(), lista);
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

	/**
	 * Verifica se é um projeto de pesquisa ou de monitoria e limita quem pode ser um coordenador. 
	 * 
	 * @param membros
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaCoordenacaoEnsinoPesquisa(MembroProjeto membro, ListaMensagens lista) throws DAOException{
	    ColaboradorVoluntarioDao dao = DAOFactory.getInstance().getDAO(ColaboradorVoluntarioDao.class);
	    try {
		if (membro.isCoordenador()) {
		    if (!membro.isCategoriaDocente() &&
			    (membro.getProjeto().isEnsino() || membro.getProjeto().isPesquisa())) {
		    	lista.addErro("Validação para Projetos de Ensino e Pesquisa:");
		    	lista.addMensagem(MensagensProjetos.REGRA_COORDENACAO);
		    }
		    if (membro.getProjeto().isPesquisa()) {
			if((membro.getServidor().getSituacaoServidor().getId() == Situacao.APOSENTADO) && !dao.isColaboradorVoluntario(membro.getServidor())) {
				lista.addErro("Validação para Projetos de Pesquisa:");
			    lista.addMensagem(MensagensExtensao.REGRA_SERVIDOR_APOSENTADO_COORDENAR_PROJETO);
			}
		    }
		    validaCoordenacaoComEmail(membro.getServidor(), lista);        		    
		}
	    }finally {
		dao.close();
	    }          
	}

	/**
	 * Valida se o coordenador informado possui e-mail cadastrado no SIGPRH.
	 * 
	 * @param coordenador
	 * @param lista
	 */
	public static void validaCoordenacaoComEmail(Servidor coordenador, ListaMensagens lista) {
	    if (coordenador != null) {
			JdbcTemplate jd = new JdbcTemplate(Database.getInstance().getSigaaDs());
			String email;
			email = (String) jd.queryForObject("SELECT email FROM comum.pessoa WHERE id_pessoa = ?", new Object[] {coordenador.getPessoa().getId()}, String.class);
			if(email == null || email.isEmpty()) {		    
			    lista.addMensagem(MensagensExtensao.REGRA_EMAIL_COORDENAR_PROJETO, coordenador.getPessoa().getNome());
			}
	    }	    
	}
	
	/**
	 * Valida se o participante externo com função de coordenador possui e-mail cadastrado.
	 * 
	 * @param coord
	 * @param lista
	 */
	public static void validaCoordenacaoComEmailParticipanteExterno(ParticipanteExterno coord, ListaMensagens lista){
		if (coord != null &&coord.getPessoa() != null && coord.getPessoa().getId() > 0) {
			JdbcTemplate jd = new JdbcTemplate(Database.getInstance().getSigaaDs());
			String email;
			email = (String) jd.queryForObject("SELECT email FROM comum.pessoa WHERE id_pessoa = ?", new Object[] {coord.getPessoa().getId()}, String.class);
			if(email == null) {		    
			    lista.addMensagem(MensagensExtensao.REGRA_EMAIL_COORDENAR_PROJETO, coord.getPessoa().getNome());
			}
	    }	
	}
	
	
	/**
	 * Verifica tentativa de adicionar dois Coordenadores a uma ação.
	 * 
	 * @param membros
	 * @param coordenadorCandidato
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaCooordenacaoDupla(Projeto pj, MembroProjeto coordenadorCandidato, ListaMensagens lista) throws DAOException {
		if (coordenadorCandidato.getFuncaoMembro() != null) {
		    MembroProjetoDao dao = DAOFactory.getInstance().getDAO(MembroProjetoDao.class);
		    try {
		    	//Lista de todos os membros do projeto
		    	List<MembroProjeto> membrosAtivos = (List<MembroProjeto>) dao.findByProjeto(pj.getId(), true);

				// apenas um coordenador
				if (coordenadorCandidato.isCoordenador()) {
					//Percorre toda lista em busca de coordenadores ativos no mesmo período.
					for (MembroProjeto coordenadorAtual : membrosAtivos){
						if ( coordenadorAtual.isCoordenador()
							&& (coordenadorAtual.getPessoa().getId() != coordenadorCandidato.getPessoa().getId()) 
							&& (CalendarUtils.isIntervalosDeDatasConflitantes(
								coordenadorAtual.getDataInicio(), coordenadorAtual.getDataFim(), coordenadorCandidato.getDataInicio(), coordenadorCandidato.getDataFim()))) {
						    lista.addErro("Já existe um(a) Coordenador(a) ativo neste período. (" + coordenadorAtual.getPessoa().getNome() + ")");
						}
					}
	
				}
		    }finally {
			dao.close();
		    }
		}
	}

	/**
	 * Valida possibilidade de um Docente ser coordenador de mais de uma ação de extensão.
	 * De acordo com a resolução atual, um docente pode ser coordenador de no máximo duas ações 
	 * da mesma modalidade.
	 * 
	 * Caso o tipo da ação de extensão não seja informado a busca será realizada para todos os tipos. 
	 * 
	 * @param tipoAtividade
	 * @param membro
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaCoordenacaoSimultaneaExtensao(TipoAtividadeExtensao tipoAtividade, MembroProjeto membro, ListaMensagens lista) throws DAOException {
	    if (membro.getServidor() != null) {		    
        	AtividadeExtensaoDao dao = DAOFactory.getInstance().getDAO(AtividadeExtensaoDao.class);
        	try {
                    	ArrayList<TipoAtividadeExtensao> listaTipos = new ArrayList<TipoAtividadeExtensao>();
                	    	
                    	// Caso o tipo da ação de extensão não seja informado a busca será realizada para todos os tipos. 
                    	if (tipoAtividade == null) {
                    	   listaTipos = (ArrayList<TipoAtividadeExtensao>) dao.findAllAtivos(TipoAtividadeExtensao.class, "id"); 
                    	}else {
                    	   listaTipos.add(tipoAtividade);
                    	}
                    	
                	Collection<AtividadeExtensao> listaAcoesExtensao = dao.findAtivasByServidor(membro.getServidor(), membro.getProjeto().getAno(), FuncaoMembro.COORDENADOR);
                	int totalMesmaModalidadeCoordenadas = 0;
                	int parametroMaxAcoesPossiveis = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.TOTAL_ACOES_ATIVAS_MESMA_MODALIDADE_COORDENADAS);
                	TipoAtividadeExtensao tipoMesmaMododalidadeCoordenada = null;
                	if (listaAcoesExtensao.size() >= parametroMaxAcoesPossiveis) {
                	    for(TipoAtividadeExtensao tipo : listaTipos) {
	                		// Não conta com a atividade atual
	                		for (AtividadeExtensao a : listaAcoesExtensao) { 
	                			tipoMesmaMododalidadeCoordenada = new TipoAtividadeExtensao();
	                			if ((a.getTipoAtividadeExtensao().getId() == tipo.getId())
	                					&& (a.getProjeto().getId() != membro.getProjeto().getId()) 
	                					&& (a.getSituacaoProjeto().getId() != TipoSituacaoProjeto.EXTENSAO_CONCLUIDO)) {
	                				totalMesmaModalidadeCoordenadas++;
	                				tipoMesmaMododalidadeCoordenada = a.getTipoAtividadeExtensao();
	                			}
	                			if (totalMesmaModalidadeCoordenadas >= parametroMaxAcoesPossiveis) {
	                				lista.addErro(membro.getServidor().getNome()
	                								+ " não pode coordenar simultaneamente mais de " + parametroMaxAcoesPossiveis 
	                								+ " ações de extensão da mesma modalidade ("
	                								+ tipoMesmaMododalidadeCoordenada.getDescricao()
	                								+ ") no mesmo ano.");
	                				break;
	                			}
	                		}
                	    }
                	}
        	}finally {
        	    dao.close();
        	}
	    }
	}

	/**
	 * Valida se o docente ultrapassou a quantidade máxima de coordenações ativas 
	 * para um mesmo tipo de ação do mesmo edital.
	 * 
	 * @param membro
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaCoordenacaoSimultaneaExtensao(MembroProjeto membro, AtividadeExtensao atividadeExtensao,  ListaMensagens lista) throws DAOException {
		ValidatorUtil.validateRequired(membro.getPessoa(), "Coordenador", lista);	    	
		if (membro.getPessoa() != null) {		    
			AtividadeExtensaoDao dao = DAOFactory.getInstance().getDAO(AtividadeExtensaoDao.class);
			try {
				long totalCoordAtivas = dao.countAtividadesCoordenador(membro.getPessoa(), atividadeExtensao);
				int maxCoordenacao = atividadeExtensao.getRegrasEdital().getMaxCoordenacoesAtivas();
				if (maxCoordenacao > 0 && totalCoordAtivas >= maxCoordenacao) {
					lista.addErro(membro.getServidor().getNome() + " não pode coordenar simultaneamente mais de " + maxCoordenacao 
							+ " " + atividadeExtensao.getTipoAtividadeExtensao().getDescricao().toLowerCase() + "(s) no edital selecionado para esta ação.");
				}
			}finally {
				dao.close();
			}
		}	    
	}
	
	/**
	 * Valida alteração de um membro de projeto de extensão.
	 * 
	 * @param membro
	 * @param dao
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaAlteracaoMembroExtensao(MembroProjeto membro, GenericDAO dao, ListaMensagens lista) throws DAOException {
	    validaAlteracaoMembroProjetoBase(membro, dao, lista);
	    
	    if (membro.isCoordenador() && !membro.getCategoriaMembro().isDiscente()) {
		validaCoordenacaoSimultaneaExtensao(null, membro, lista);
		validaCoordenacao(membro, lista);
	    }
	    
	    // validaPrazoAlterarMembroEquipe(membro.getProjeto(), lista);	    
	}

	
	/**
	 * Verifica se já se passou 1/3 do tempo da ação de extensão. 
	 * 
	 * @param projeto
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaPrazoAlterarMembroExtensao(Projeto projeto, ListaMensagens lista)
			throws DAOException {
	    
		if ((projeto.getDataInicio() != null)	&& (projeto.getDataFim() != null)) {
			long totalDias = CalendarUtils.diferencaDias(projeto.getDataInicio(), projeto.getDataFim());
			long diasPercorridos = CalendarUtils.diferencaDias(projeto.getDataInicio(), new Date());
			if ((totalDias / 3) <= diasPercorridos) {
			    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			    
			    	lista.addErro("O tempo de execução desta ação é: " + totalDias + " dias (" 
			    		+ sdf.format(projeto.getDataInicio()) + " até " + sdf.format(projeto.getDataFim()) +"). Já se passaram "
			    		+ diasPercorridos + " dias.");
			    	
				lista.addErro("O tempo máximo para substituição e/ou inclusão de membros da equipe terminou." +
						" 1/3 (um terço) do tempo de execução da ação.");
				lista.addErro("Verificar RESOLUÇÃO No 053/2008 - CONSEPE de 15 de abril de 2008. Art. 21, Parágrafo Único");
				
			}
		} else {
			lista.addErro("Data de início e/ou fim desta ação de extensão não estão definidas. Atualize as datas para alterar "
							+ "os membros da equipe desta ação");
		}

	}

	
	/**
	 * Verifica possibilidade de alterar um membro de uma equipe de uma ação.
	 * Isto só poderá ocorrer se não tiver decorrido um tempo equivalente a 1/3 da ação.
	 * 
	 * @param membro
	 * @param participanteExterno
	 * @param dao
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaAlteracaoMembroProjetoBase(MembroProjeto membro, GenericDAO dao, ListaMensagens lista) throws DAOException {
		Projeto projeto = dao.findByPrimaryKey(membro.getProjeto().getId(), Projeto.class);

		// atualiza membro na equipe da ação para validar com o que está no banco...
		projeto.getEquipe().remove(membro); // retira o antigo (do banco).
		projeto.getEquipe().add(membro); // adiciona o novo que veio da view.

		if (membro.getCategoriaMembro().isExterno()) {
			validaParticipanteExterno(projeto.getEquipe(), membro, lista, false);
		} if (membro.getCategoriaMembro().isServidor()) {
			validaServidor(projeto.getEquipe(), membro, lista);
		} if (membro.getCategoriaMembro().isDiscente()) {
			validaDiscenteSemValidacaoBolsista(projeto.getEquipe(), membro, lista);
		}
		
		validaCoordenacaoAtiva(projeto.getEquipe(), lista);
		validaCooordenacaoDupla(projeto, membro, lista);
		ValidatorUtil.validateRequired(membro.getDataInicio(), "Data de Início", lista);
		ValidatorUtil.validateRequired(membro.getDataFim(), "Data de Fim", lista);
		if (membro.isValido()) {
		    ValidatorUtil.validaOrdemTemporalDatas(membro.getDataInicio(), membro.getDataFim(), true, "Período", lista);
		    ValidatorUtil.validaDataAnteriorIgual(membro.getDataFim(), membro.getProjeto().getDataFim(), "Data Fim", lista);
		}
		if (membro.isCoordenador())
			validaCoordenacaoSimultanea(membro, projeto, lista);
	}


	
	/**
	 * Verifica se o servidor sendo inserido possui os requisitos mínimos para a participação
	 * como membro de uma equipe organizadora de projetos.
	 * 
	 * 
	 * @param membro
	 * @param docente
	 * @param tecnico
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaServidor(Collection<MembroProjeto> equipe, MembroProjeto membro, ListaMensagens lista) throws DAOException {
		ValidatorUtil.validateRequired(membro.getServidor(), "Servidor", lista);
		ValidatorUtil.validateRequired(membro.getFuncaoMembro(), "Função", lista);
		ValidatorUtil.validateRequired(membro.getChDedicada(), "Carga horária semanal dedicada", lista);

		if(membro.getServidor() != null && membro.getServidor().getFormacao() != null 
				&& membro.getServidor().getFormacao().getId() > Formacao.FORMACAO_PADRAO){
			lista.addErro("A titulação do(a) servidor(a) não está registrada no sistema. " +
			"Por favor, procure o DAP para regularizar a situação e em seguida tente novamente.");
		}

		if (ValidatorUtil.isEmpty(membro.getCategoriaMembro())) {
			lista.addMensagem(MensagensProjetos.SELECIONE_CATEGORIA_MEMBRO_EQUIPE);
		}

		MembroProjetoValidator.validaPessoaDupla(equipe, membro, lista);

		//FIXME: parametrizar
		if(membro != null && membro.getChDedicada() != null && membro.getChDedicada() > MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA ) {
			lista.addMensagem(MensagensExtensao.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA,MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA);
		}
	}

	/**
	 * Método utilizado para validar a carga horário total do membro do projeto
	 * 
	 * @param equipe
	 * @param membro
	 * @param lista
	 */
	public static void validaChTotalMembroProjeto(Collection<MembroProjeto> equipe, MembroProjeto membro, ListaMensagens lista) {
		if (membro != null && membro.getChDedicada() != null) {
			int chAtual = 0;
			for (MembroProjeto mp: equipe) {
				if (mp.getPessoa().getId() == membro.getPessoa().getId()) 
					chAtual += mp.getChDedicada();
			}
			
			ValidatorUtil.validaInt(membro.getChDedicada(), "CH Semanal", lista);
			if (chAtual == MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA){ 
				lista.addErro("O Membro já atingiu a carga horária máxima permitida.");
			} else {
			int chTotal = chAtual + membro.getChDedicada();
			if (chTotal > MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA) 
				lista.addErro("Carga horária restante para o membro: " + (MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA - chAtual) + "h");
			}
		}
	}
	
	/**
	 * Valida a inclusão de discente na equipe de projeto de integração.
	 * 
	 * @param equipe
	 * @param membro
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaDiscente(Collection<MembroProjeto> equipe, MembroProjeto membro, ListaMensagens lista) throws DAOException {
		validaDiscenteSemValidacaoBolsista(equipe, membro, lista);

		if (membro.getFuncaoMembro() != null) {

			// Testando se discente já participa e é membro ativo de alguma ação acadêmica
			// não verifica se tem bolsa no SIPAC.
			// as bolsas só são realmente efetivadas quando são cadastrados planos de trabalho para os discentes
			if (membro.getFuncaoMembro().getId() == FuncaoMembro.BOLSISTA) {
				MembroProjetoDao dao = DAOFactory.getInstance().getDAO(MembroProjetoDao.class);					
				try {
					MembroProjeto mpBolsista = dao.getDiscenteBolsistaAtivo(membro.getDiscente());
					if (mpBolsista != null && !mpBolsista.equals(membro)) {
						lista.addErro(membro.getDiscente().getMatriculaNome() 
								+ " não poderá assumir bolsa nesta ação porque já possui bolsa ativa em outra Ação Acadêmica.");
					}
				}finally {
					dao.close();
				}
			}
		}
	 }

	/**
	 * Valida a inclusão de discente na equipe de projeto
	 * @param equipe
	 * @param membro
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDiscenteSemValidacaoBolsista(
			Collection<MembroProjeto> equipe, MembroProjeto membro,
			ListaMensagens lista) throws DAOException {
		ValidatorUtil.validateRequired(membro.getDiscente(), "Discente", lista);
		ValidatorUtil.validateRequired(membro.getFuncaoMembro(), "Função", lista);
		ValidatorUtil.validateRequired(membro.getChDedicada(), "Carga horária semanal dedicada", lista);
		if (ValidatorUtil.isEmpty(membro.getCategoriaMembro())) {
			lista.addMensagem(MensagensProjetos.SELECIONE_CATEGORIA_MEMBRO_EQUIPE);
		}

		MembroProjetoValidator.validaPessoaDupla(equipe, membro, lista);
		
		if (membro.getFuncaoMembro() != null) {
			if (membro.isCoordenador()) {
				lista.addErro("Discentes não podem assumir função de Coordenação.");
			}
		}
	}
	
	
	/**
	 * Valida dados importantes que um participante externo deve ter.
	 * Participantes externo, a princípio, não podem ser coordenadores 
	 * de ações de extensão.
	 * 
	 * @param membro membro que está sendo cadastrado
	 * @param participanteExterno participante externo que está sendo cadastrado
	 * @param lista lista de erros
	 * @param permitirExternoCoordenarAcao permite, excepcionalmente que um participante externo seja coordenador de ações de extensão.
	 * @throws DAOException 
	 */
	public static void validaParticipanteExterno(Collection<MembroProjeto> equipe, MembroProjeto membro, ListaMensagens lista, boolean permitirExternoCoordenarAcao) throws DAOException {

	    if (!membro.getParticipanteExterno().getPessoa().isInternacional()){
		boolean cpfOK = ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(membro.getParticipanteExterno().getPessoa().getCpf_cnpj());
		if (!cpfOK) {
		    lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "CPF");
		}
	    }

	    MembroProjetoValidator.validaPessoaDupla(equipe, membro, lista);
	    
	    ValidatorUtil.validateRequired(membro.getParticipanteExterno().getPessoa().getNome(), "Nome", lista);
	    ValidatorUtil.validateRequired(membro.getParticipanteExterno().getPessoa().getSexo(), "Sexo", lista);
	    ValidatorUtil.validateRequired(membro.getParticipanteExterno().getFormacao(), "Formação", lista);
	    ValidatorUtil.validateRequired(membro.getParticipanteExterno().getInstituicao(), "Instituição de Ensino", lista);
	    ValidatorUtil.validateRequired(membro.getFuncaoMembro(), "Função", lista);
	    ValidatorUtil.validateRequired(membro.getChDedicada(), "Carga horária semanal dedicada", lista);

	    //permite que participantes externos sejam coordenadores.
	    if ( (!permitirExternoCoordenarAcao) && (membro.getFuncaoMembro() != null) && (membro.getCategoriaMembro() != null)) {
			if ((membro.isCoordenador())) {
			    lista.addMensagem(MensagensExtensao.REQUISITOS_COORDENADOR_ACAO);
			}
	    }else{
	    	validaCoordenacaoComEmailParticipanteExterno(membro.getParticipanteExterno(), lista);	    
	    }
	}

	
	
	/**
	 * Verifica se o docente possui realatórios pendentes de submissão para pró-reitoria de extensão.
	 * Docentes com relatórios pendentes não podem submeter novas ações de extensão.
	 * 
	 * @param membro
	 * @param lista
	 * @throws DAOException 
	 */
	public static void verificaRelatorioPendenteDocente(MembroProjeto membro, ListaMensagens lista) throws DAOException {
	    	AtividadeExtensaoDao dao = DAOFactory.getInstance().getDAO(AtividadeExtensaoDao.class);
		try {			
			Integer diasPermitidosAtrasoRelatorio = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.TOTAL_MAXIMO_DIAS_PERMITIDO_ATRASO_RELATORIO);
			
			if ( !isEmpty( membro.getServidor() ) && membro.getId() == 0 ) {
				Collection<Object> atividades =  dao.projetosCoordenadorNaoEnviouRelatorio(
						membro.getServidor().getId(),diasPermitidosAtrasoRelatorio,
						TipoAtividadeExtensao.CURSO, TipoAtividadeExtensao.EVENTO, TipoAtividadeExtensao.PROJETO);
			
				if( !atividades.isEmpty() ) {
					lista.addMensagem(MensagensExtensao.REGRA_INSERIR_MEMBRO_PENDENTE_QUANTO_ENVIO_DE_RELATORIO,membro.getServidor().getPessoa().getNome(),diasPermitidosAtrasoRelatorio);
					for (Object object : atividades) {
					    lista.addErro("Ação pendente de envio de Relatório Final: " + object.toString());				    
					}
				}			
			}
		} catch (DAOException e) {
			lista.addErro(e.getMessage());
		}finally {
		    dao.close();
		}
	}
	
	/***
	 * Valida a remoção de um membro do projeto. Verifica se o membro da equipe é coordenador ativo.
	 * 
	 * @param membro
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaRemoverMembroProjeto(MembroProjeto membro, ListaMensagens lista) throws DAOException {
	    if (ValidatorUtil.isEmpty(membro)) {
	    	lista.addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
	    }
	}

	

	/**
	 * Verifica tentativa de adicionar duas pessoas iguais a uma ação.
	 * Pessoas duplicadas só são permitidas com funções diferentes.  
	 * 
	 * @param equipe
	 * @param novoMembro
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaPessoaDupla(Collection<MembroProjeto> equipe, MembroProjeto novoMembro, ListaMensagens lista) throws DAOException {
		for (MembroProjeto membroNoBanco : equipe) {
			if ((membroNoBanco.getPessoa() != null) && (novoMembro.getPessoa() != null) 
				&& (membroNoBanco.getPessoa().equals(novoMembro.getPessoa()))
				&& (!membroNoBanco.isFinalizado()) 
				&& (membroNoBanco.getFuncaoMembro() != null && novoMembro.getFuncaoMembro() != null &&
						membroNoBanco.getFuncaoMembro().equals(novoMembro.getFuncaoMembro()) && membroNoBanco.isAtivo()) 
				&& novoMembro.getId() == 0) {
				lista.addErro("Pessoa já adicionada ao projeto nesta função.");
				break;
			}
		}
	}


	/**
	 * Valida se o docente ultrapassou a quantidade máxima de coordenações ativas 
	 * para um mesmo tipo de ação do mesmo edital.
	 * 
	 * @param membro
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaCoordenacaoSimultanea(MembroProjeto membro, Projeto projeto,  ListaMensagens lista) throws DAOException {
	    ValidatorUtil.validateRequired(membro.getPessoa(), "Coordenador", lista);	    	
	    if (membro.getPessoa() != null && membro.isCoordenador() && projeto.isInterno() && !ValidatorUtil.isEmpty(projeto.getEdital())) {		    
        	ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
        	try {
        	    long total = dao.countProjetosCoordenador(membro.getPessoa(), projeto);
        	    int maxCoordenacao = projeto.getEdital().getRestricaoCoordenacao().getMaxCoordenacoesAtivas();
        	    if (total >= maxCoordenacao) {
			lista.addErro(membro.getServidor().getNome() + " não pode coordenar simultaneamente mais de " + maxCoordenacao 
				+ " Projeto(s) no edital selecionado para esta proposta.");
        	    }
        	}finally {
        	    dao.close();
        	}
	    }	    
	}

}