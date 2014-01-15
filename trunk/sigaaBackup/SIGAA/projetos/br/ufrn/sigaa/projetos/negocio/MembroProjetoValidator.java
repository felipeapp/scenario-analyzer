/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe com m�todos de valida��o altera��o de um membro do projeto de extens�o.
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
	 * Verifica se o membro � um docente ou t�cnicos-administrativo com n�vel superior do quadro
	 * permanente da UFRN podem ser Coordenadores de a��es de extens�o.
	 * @throws DAOException 
	 * 
	 */
	public static void validaCoordenacao(MembroProjeto m, ListaMensagens lista) throws DAOException {	    
		if (!m.getCategoriaMembro().isExterno() && (m.isCoordenador())) {			
			// A��o sem edital. Ser�o realizadas valida��es gen�ricas para um coordenador de projetos.
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

			// A��o com edital vinculado. Ser�o realizadas valida��es personalizadas de coordena��o.
			}else {
				ProjetoBaseValidator.validaRestricoesCoordenacaoEdital(m.getProjeto(), m.getServidor(), lista);
			}
			validaCoordenacaoComEmail(m.getServidor(), lista);
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

	/**
	 * Verifica se � um projeto de pesquisa ou de monitoria e limita quem pode ser um coordenador. 
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
		    	lista.addErro("Valida��o para Projetos de Ensino e Pesquisa:");
		    	lista.addMensagem(MensagensProjetos.REGRA_COORDENACAO);
		    }
		    if (membro.getProjeto().isPesquisa()) {
			if((membro.getServidor().getSituacaoServidor().getId() == Situacao.APOSENTADO) && !dao.isColaboradorVoluntario(membro.getServidor())) {
				lista.addErro("Valida��o para Projetos de Pesquisa:");
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
	 * Valida se o participante externo com fun��o de coordenador possui e-mail cadastrado.
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
	 * Verifica tentativa de adicionar dois Coordenadores a uma a��o.
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
					//Percorre toda lista em busca de coordenadores ativos no mesmo per�odo.
					for (MembroProjeto coordenadorAtual : membrosAtivos){
						if ( coordenadorAtual.isCoordenador()
							&& (coordenadorAtual.getPessoa().getId() != coordenadorCandidato.getPessoa().getId()) 
							&& (CalendarUtils.isIntervalosDeDatasConflitantes(
								coordenadorAtual.getDataInicio(), coordenadorAtual.getDataFim(), coordenadorCandidato.getDataInicio(), coordenadorCandidato.getDataFim()))) {
						    lista.addErro("J� existe um(a) Coordenador(a) ativo neste per�odo. (" + coordenadorAtual.getPessoa().getNome() + ")");
						}
					}
	
				}
		    }finally {
			dao.close();
		    }
		}
	}

	/**
	 * Valida possibilidade de um Docente ser coordenador de mais de uma a��o de extens�o.
	 * De acordo com a resolu��o atual, um docente pode ser coordenador de no m�ximo duas a��es 
	 * da mesma modalidade.
	 * 
	 * Caso o tipo da a��o de extens�o n�o seja informado a busca ser� realizada para todos os tipos. 
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
                	    	
                    	// Caso o tipo da a��o de extens�o n�o seja informado a busca ser� realizada para todos os tipos. 
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
	                		// N�o conta com a atividade atual
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
	                								+ " n�o pode coordenar simultaneamente mais de " + parametroMaxAcoesPossiveis 
	                								+ " a��es de extens�o da mesma modalidade ("
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
	 * Valida se o docente ultrapassou a quantidade m�xima de coordena��es ativas 
	 * para um mesmo tipo de a��o do mesmo edital.
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
					lista.addErro(membro.getServidor().getNome() + " n�o pode coordenar simultaneamente mais de " + maxCoordenacao 
							+ " " + atividadeExtensao.getTipoAtividadeExtensao().getDescricao().toLowerCase() + "(s) no edital selecionado para esta a��o.");
				}
			}finally {
				dao.close();
			}
		}	    
	}
	
	/**
	 * Valida altera��o de um membro de projeto de extens�o.
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
	 * Verifica se j� se passou 1/3 do tempo da a��o de extens�o. 
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
			    
			    	lista.addErro("O tempo de execu��o desta a��o �: " + totalDias + " dias (" 
			    		+ sdf.format(projeto.getDataInicio()) + " at� " + sdf.format(projeto.getDataFim()) +"). J� se passaram "
			    		+ diasPercorridos + " dias.");
			    	
				lista.addErro("O tempo m�ximo para substitui��o e/ou inclus�o de membros da equipe terminou." +
						" 1/3 (um ter�o) do tempo de execu��o da a��o.");
				lista.addErro("Verificar RESOLU��O No 053/2008 - CONSEPE de 15 de abril de 2008. Art. 21, Par�grafo �nico");
				
			}
		} else {
			lista.addErro("Data de in�cio e/ou fim desta a��o de extens�o n�o est�o definidas. Atualize as datas para alterar "
							+ "os membros da equipe desta a��o");
		}

	}

	
	/**
	 * Verifica possibilidade de alterar um membro de uma equipe de uma a��o.
	 * Isto s� poder� ocorrer se n�o tiver decorrido um tempo equivalente a 1/3 da a��o.
	 * 
	 * @param membro
	 * @param participanteExterno
	 * @param dao
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaAlteracaoMembroProjetoBase(MembroProjeto membro, GenericDAO dao, ListaMensagens lista) throws DAOException {
		Projeto projeto = dao.findByPrimaryKey(membro.getProjeto().getId(), Projeto.class);

		// atualiza membro na equipe da a��o para validar com o que est� no banco...
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
		ValidatorUtil.validateRequired(membro.getDataInicio(), "Data de In�cio", lista);
		ValidatorUtil.validateRequired(membro.getDataFim(), "Data de Fim", lista);
		if (membro.isValido()) {
		    ValidatorUtil.validaOrdemTemporalDatas(membro.getDataInicio(), membro.getDataFim(), true, "Per�odo", lista);
		    ValidatorUtil.validaDataAnteriorIgual(membro.getDataFim(), membro.getProjeto().getDataFim(), "Data Fim", lista);
		}
		if (membro.isCoordenador())
			validaCoordenacaoSimultanea(membro, projeto, lista);
	}


	
	/**
	 * Verifica se o servidor sendo inserido possui os requisitos m�nimos para a participa��o
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
		ValidatorUtil.validateRequired(membro.getFuncaoMembro(), "Fun��o", lista);
		ValidatorUtil.validateRequired(membro.getChDedicada(), "Carga hor�ria semanal dedicada", lista);

		if(membro.getServidor() != null && membro.getServidor().getFormacao() != null 
				&& membro.getServidor().getFormacao().getId() > Formacao.FORMACAO_PADRAO){
			lista.addErro("A titula��o do(a) servidor(a) n�o est� registrada no sistema. " +
			"Por favor, procure o DAP para regularizar a situa��o e em seguida tente novamente.");
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
	 * M�todo utilizado para validar a carga hor�rio total do membro do projeto
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
				lista.addErro("O Membro j� atingiu a carga hor�ria m�xima permitida.");
			} else {
			int chTotal = chAtual + membro.getChDedicada();
			if (chTotal > MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA) 
				lista.addErro("Carga hor�ria restante para o membro: " + (MembroProjeto.CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA - chAtual) + "h");
			}
		}
	}
	
	/**
	 * Valida a inclus�o de discente na equipe de projeto de integra��o.
	 * 
	 * @param equipe
	 * @param membro
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaDiscente(Collection<MembroProjeto> equipe, MembroProjeto membro, ListaMensagens lista) throws DAOException {
		validaDiscenteSemValidacaoBolsista(equipe, membro, lista);

		if (membro.getFuncaoMembro() != null) {

			// Testando se discente j� participa e � membro ativo de alguma a��o acad�mica
			// n�o verifica se tem bolsa no SIPAC.
			// as bolsas s� s�o realmente efetivadas quando s�o cadastrados planos de trabalho para os discentes
			if (membro.getFuncaoMembro().getId() == FuncaoMembro.BOLSISTA) {
				MembroProjetoDao dao = DAOFactory.getInstance().getDAO(MembroProjetoDao.class);					
				try {
					MembroProjeto mpBolsista = dao.getDiscenteBolsistaAtivo(membro.getDiscente());
					if (mpBolsista != null && !mpBolsista.equals(membro)) {
						lista.addErro(membro.getDiscente().getMatriculaNome() 
								+ " n�o poder� assumir bolsa nesta a��o porque j� possui bolsa ativa em outra A��o Acad�mica.");
					}
				}finally {
					dao.close();
				}
			}
		}
	 }

	/**
	 * Valida a inclus�o de discente na equipe de projeto
	 * @param equipe
	 * @param membro
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDiscenteSemValidacaoBolsista(
			Collection<MembroProjeto> equipe, MembroProjeto membro,
			ListaMensagens lista) throws DAOException {
		ValidatorUtil.validateRequired(membro.getDiscente(), "Discente", lista);
		ValidatorUtil.validateRequired(membro.getFuncaoMembro(), "Fun��o", lista);
		ValidatorUtil.validateRequired(membro.getChDedicada(), "Carga hor�ria semanal dedicada", lista);
		if (ValidatorUtil.isEmpty(membro.getCategoriaMembro())) {
			lista.addMensagem(MensagensProjetos.SELECIONE_CATEGORIA_MEMBRO_EQUIPE);
		}

		MembroProjetoValidator.validaPessoaDupla(equipe, membro, lista);
		
		if (membro.getFuncaoMembro() != null) {
			if (membro.isCoordenador()) {
				lista.addErro("Discentes n�o podem assumir fun��o de Coordena��o.");
			}
		}
	}
	
	
	/**
	 * Valida dados importantes que um participante externo deve ter.
	 * Participantes externo, a princ�pio, n�o podem ser coordenadores 
	 * de a��es de extens�o.
	 * 
	 * @param membro membro que est� sendo cadastrado
	 * @param participanteExterno participante externo que est� sendo cadastrado
	 * @param lista lista de erros
	 * @param permitirExternoCoordenarAcao permite, excepcionalmente que um participante externo seja coordenador de a��es de extens�o.
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
	    ValidatorUtil.validateRequired(membro.getParticipanteExterno().getFormacao(), "Forma��o", lista);
	    ValidatorUtil.validateRequired(membro.getParticipanteExterno().getInstituicao(), "Institui��o de Ensino", lista);
	    ValidatorUtil.validateRequired(membro.getFuncaoMembro(), "Fun��o", lista);
	    ValidatorUtil.validateRequired(membro.getChDedicada(), "Carga hor�ria semanal dedicada", lista);

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
	 * Verifica se o docente possui realat�rios pendentes de submiss�o para pr�-reitoria de extens�o.
	 * Docentes com relat�rios pendentes n�o podem submeter novas a��es de extens�o.
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
					    lista.addErro("A��o pendente de envio de Relat�rio Final: " + object.toString());				    
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
	 * Valida a remo��o de um membro do projeto. Verifica se o membro da equipe � coordenador ativo.
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
	 * Verifica tentativa de adicionar duas pessoas iguais a uma a��o.
	 * Pessoas duplicadas s� s�o permitidas com fun��es diferentes.  
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
				lista.addErro("Pessoa j� adicionada ao projeto nesta fun��o.");
				break;
			}
		}
	}


	/**
	 * Valida se o docente ultrapassou a quantidade m�xima de coordena��es ativas 
	 * para um mesmo tipo de a��o do mesmo edital.
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
			lista.addErro(membro.getServidor().getNome() + " n�o pode coordenar simultaneamente mais de " + maxCoordenacao 
				+ " Projeto(s) no edital selecionado para esta proposta.");
        	    }
        	}finally {
        	    dao.close();
        	}
	    }	    
	}

}