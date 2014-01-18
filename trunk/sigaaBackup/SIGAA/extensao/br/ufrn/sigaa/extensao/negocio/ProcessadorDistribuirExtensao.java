package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.extensao.AvaliadorAtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.dao.projetos.OrcamentoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.util.EnvioMensagemHelper;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoOrcamentoProposto;
import br.ufrn.sigaa.extensao.dominio.AvaliadorAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DistribuicaoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.TipoAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * 
 * Processador responsável por distribuir propostas  e relatórios de ações de extensão para análise por membros do 
 * comitê de extensão e do comitê ad hoc.
 * 
 * @author Ilueny Santos
 * 
 */
public class ProcessadorDistribuirExtensao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);

		if (mov.getCodMovimento().equals(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_MANUAL)) {
			distribuirManual((CadastroExtensaoMov) mov);

		} else if (mov.getCodMovimento().equals(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_AUTO)) {
			distribuirAutomatico((CadastroExtensaoMov) mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_AUTO_LINHA)) {
			distribuirAutomaticoLinha((CadastroExtensaoMov) mov);
		}
		
		return null;
	}

	/**
	 * Distribuição automática, para os projetos ligados as linhas de pesquisa em especpífico.
	 */
	private void distribuirAutomaticoLinha(CadastroExtensaoMov mov) throws DAOException {
		OrcamentoDao dao = getDAO(OrcamentoDao.class, mov);
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		MembroComissaoDao membroComissaoDao = getDAO(MembroComissaoDao.class, mov);
		
		DistribuicaoAtividadeExtensao dist = mov.getDistribuicaoExtensao();
		Integer idEdital = (Integer) mov.getObjAuxiliar();
		List<AtividadeExtensao> atividades = dist.getAtividades();

		try {
			/** Criando lista de avaliadores disponíveis */
			mov.getDistribuicaoExtensao().getMembrosComitePossiveis().addAll(membroComissaoDao.findMembrosAvaliadores(idEdital));

			/** Cria a avaliaçao para a ação de extensão. */
			for (AtividadeExtensao atv : atividades) {
				
				//Distribui até o máximo de avaliações
				for (int i=1; i <= dist.getNumAvaliacoesPorProjeto(); i++) {
					adicionarAvaliadorAutoLinha(atv, mov);
				}

				/**  @negocio: Ações pendentes de avaliação devem permanecer com a situação de 'aguardando avaliação' até que a avaliação do presidente seja realizada.	 */
				atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO));
				atv.setAtivo(true);
				dao.updateField(AtividadeExtensao.class, atv.getId(), "situacaoProjeto.id", atv.getSituacaoProjeto().getId());
				ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
				
				//Grava o histórico da situação do projeto
				HistoricoSituacaoProjeto historico = new HistoricoSituacaoProjeto();
				historico.setSituacaoProjeto(atv.getSituacaoProjeto());
				historico.setData(new Date());
				historico.setRegistroEntrada(mov.getRegistroEntrada());
				historico.setProjeto(atv.getProjeto());
				dao.create(historico);
			}
			
			List<AvaliacaoAtividade> avaliacoesCriadas = salvarAvaliacoes(dao, atividades);

			/** @negocio: O avaliador recebe uma notificação de que tem ações disponíveis aguardando o seu parecer. */
			for (AvaliacaoAtividade av1 : avaliacoesCriadas) {
				av1.getAvaliador().setPrimeiroUsuario(uDao.findPrimeiroUsuarioByPessoa(av1.getAvaliador().getPessoa().getId()));
				EnvioMensagemHelper.notificaAvaliadoresAssociados(av1.getAtividade().getProjeto(), EnvioMensagemHelper.PROJETO_EXTENSAO, av1.getAvaliador());
			}
			
		} catch (DAOException e) {
			throw new DAOException(e);
			
		} finally {
			dao.close();
			uDao.close();
			membroComissaoDao.close();
		}		
	}

	/**
	 * Distribuir atividade de extensão para avaliação dos membros do comitê.
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws NegocioException
	 */
	private void distribuirManual(CadastroExtensaoMov mov) throws ArqException, NegocioException, RemoteException {
		OrcamentoDao dao = getDAO(OrcamentoDao.class, mov);
		DistribuicaoAtividadeExtensao dist = mov.getDistribuicaoExtensao();
		AtividadeExtensao atv = dist.getAtividade();

		try {
			
			/** Removendo avaliações */
			if (ValidatorUtil.isNotEmpty(dist.getAvaliacoesRemovidas())) {
				for (AvaliacaoAtividade avaliacaoRemovida : dist.getAvaliacoesRemovidas()) {
					if (ValidatorUtil.isNotEmpty(avaliacaoRemovida)) {
						dao.updateFields(AvaliacaoAtividade.class, avaliacaoRemovida.getId(), 
								new String[] {"statusAvaliacao.id", "registroEntradaExclusao.id", "ativo"},
								new Object[] {StatusAvaliacao.AVALIACAO_CANCELADA, mov.getUsuarioLogado().getRegistroEntrada().getId(), false});
					}
				}
			}

			/** Cria a avaliação para a ação de extensão. */
			List<AtividadeExtensao> atividades = new ArrayList<AtividadeExtensao>();
			atividades.add(atv);
			List<AvaliacaoAtividade> avaliacoesCriadas = salvarAvaliacoes(dao, atividades);
			
			/** @negocio: 
			 * 	Ações pendentes de avaliação devem permanecer com a situação de 'aguardando 
			 * 	avaliação' até que a avaliação do presidente seja realizada.
			 */
			atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO));					
			
			/** @negocio: 
			 * 	Se retirou todas as avaliações do projeto, ele volta pra o status de 'submetido' 
			 * 	(aguardando distribuição para os membros do comitê). 
			 */
			if (ValidatorUtil.isEmpty(atv.getAvaliacoes())) {
			    atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_SUBMETIDO));
			}
			
			dao.updateField(AtividadeExtensao.class, atv.getId(), "situacaoProjeto.id", atv.getSituacaoProjeto().getId());
			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
			ProjetoHelper.gravarHistoricoSituacaoProjeto(atv.getSituacaoProjeto().getId(), atv.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

			/** @negocio: O avaliador recebe uma notificação de que tem ações disponíveis aguardando o seu parecer. */
			for (AvaliacaoAtividade av1 : avaliacoesCriadas) {
				EnvioMensagemHelper.notificarSubmissaoProjeto(av1.getAtividade().getProjeto(), EnvioMensagemHelper.PROJETO_EXTENSAO, av1.getAvaliador());
			}
			
		} finally {
			dao.close();
		}
	}

	/**
	 * Cria uma avaliação para cada ação de extensão informada.
	 * 
	 * @param dao
	 * @param atv
	 * @throws DAOException
	 */
	private List<AvaliacaoAtividade> salvarAvaliacoes(OrcamentoDao dao, List<AtividadeExtensao> atividades) throws DAOException {
		
		List<AvaliacaoAtividade> avaliacoesSalvas = new ArrayList<AvaliacaoAtividade>();
		
		for (AtividadeExtensao atv : atividades) {		
			
			//Criando novas avaliações
			for (AvaliacaoAtividade ava : atv.getAvaliacoes()) {
				if (ava.getId() == 0) {
	
					/** @negocio: 
					 * 	Avaliações de membros do comitê devem gerar propostas de orçamento para o projeto. 
					 * 	Estas propostas serão analisadas pelo presidente do comitê na avaliação final
					 * 	que definirá qual o orçamento concedido a ação.
					 */				    
					if (ava.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE) {
						// Lista de orçamentos consolidados da atividade para criar orçamentos propostos
						Collection<OrcamentoConsolidado> consolidados = dao.findOrcamentosConsolidadosByProjeto(ava.getAtividade().getProjeto().getId());
						
						for (OrcamentoConsolidado consolidado : consolidados) {
							AvaliacaoOrcamentoProposto orcamentoProposto = new AvaliacaoOrcamentoProposto();
							orcamentoProposto.setAvaliacaoAtividade(ava);
							orcamentoProposto.setElementoDespesa(consolidado.getElementoDespesa());
							ava.getOrcamentoProposto().add(orcamentoProposto);
						}
						
					/** @negocio: Avaliações para parecirista comuns (Ad-hoc) não geram propostas de orçamento. */	
					} else {
						ava.setOrcamentoProposto(null);
					}
					
					//Cria nova avaliação
					dao.create(ava);
					
					// criando orçamentos propostos
					if (ava.getOrcamentoProposto() != null) {
						for (AvaliacaoOrcamentoProposto op : ava.getOrcamentoProposto()) {
							if (op.getId() == 0) {
								dao.create(op);
							}
						}
					}
					
					//Inclui a avaliação para enviar email de notificação
					avaliacoesSalvas.add(ava);
				}
			}
			
		}
		
		return avaliacoesSalvas;
	}

	/**
	 * Distribui automaticamente as ações de extensão para os avaliadores correspondentes.
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	private void distribuirAutomatico(CadastroExtensaoMov mov) throws ArqException {
		CadastroExtensaoMov dMov = (CadastroExtensaoMov) mov;
		OrcamentoDao dao = getDAO(OrcamentoDao.class, mov);
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		
		DistribuicaoAtividadeExtensao dist = mov.getDistribuicaoExtensao();
		List<AtividadeExtensao> atividades = dist.getAtividades();

		try {
			
			/** Criando lista de avaliadores disponíveis */
			if (dist.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) {
				mov.getDistribuicaoExtensao().setAvaliadoresPossiveis(filtrarPareceristasDisponiveis(dMov, dist.getAreaTematica().getId()));
				
			} else if (dist.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE) {
				mov.getDistribuicaoExtensao().setMembrosComitePossiveis(filtrarMembrosComiteDisponiveis(mov));
				
			}

			
			/** Cria a avaliaçao para a ação de extensão. */
			for (AtividadeExtensao atv : atividades) {
				
				//Distribui até o máximo de avaliações
				for (int i=1; i <= dist.getNumAvaliacoesPorProjeto(); i++) {
					adicionarAvaliador(atv, mov);
				}

				/**  @negocio: Ações pendentes de avaliação devem permanecer com a situação de 'aguardando avaliação' até que a avaliação do presidente seja realizada.	 */
				atv.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO));
				atv.setAtivo(true);
				dao.updateField(AtividadeExtensao.class, atv.getId(), "situacaoProjeto.id", atv.getSituacaoProjeto().getId());
				ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atv);
				
				//Grava o histórico da situação do projeto
				HistoricoSituacaoProjeto historico = new HistoricoSituacaoProjeto();
				historico.setSituacaoProjeto(atv.getSituacaoProjeto());
				historico.setData(new Date());
				historico.setRegistroEntrada(mov.getRegistroEntrada());
				historico.setProjeto(atv.getProjeto());
				dao.create(historico);
				
			}
			
			List<AvaliacaoAtividade> avaliacoesCriadas = salvarAvaliacoes(dao, atividades);

			/** @negocio: O avaliador recebe uma notificação de que tem ações disponíveis aguardando o seu parecer. */
			for (AvaliacaoAtividade av1 : avaliacoesCriadas) {
				av1.getAvaliador().setPrimeiroUsuario(uDao.findPrimeiroUsuarioByPessoa(av1.getAvaliador().getPessoa().getId()));
				EnvioMensagemHelper.notificarSubmissaoProjeto(av1.getAtividade().getProjeto(), EnvioMensagemHelper.PROJETO_EXTENSAO, av1.getAvaliador());
			}
			
		} catch (DAOException e) {
			throw new DAOException(e);
			
		} finally {
			dao.close();
			uDao.close();
		}		
	}
	
	/**
	 * Cria uma avaliação, com as características passadas no Movimento, para a atividade informada.
	 *  
	 * @param atv
	 * @param mov
	 */
	private void adicionarAvaliador(AtividadeExtensao atv, CadastroExtensaoMov mov) {
		DistribuicaoAtividadeExtensao dist = mov.getDistribuicaoExtensao();
		
		MembroComissao avaliadorComissao = null;
		AvaliadorAtividadeExtensao avaliadorParecerista = null;
		
		if (dist.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA){
			avaliadorParecerista = getProximoAvaliadorCompativel(dist.getAvaliadoresPossiveis(), atv);
		} else {
			avaliadorComissao = getProximoMembroComissaoCompativel(dist.getMembrosComitePossiveis(), atv);
		}

		//Só adiciona avaliação no projeto se tiver avaliador disponível
		if (ValidatorUtil.isNotEmpty(avaliadorComissao) || ValidatorUtil.isNotEmpty(avaliadorParecerista)) {
			AvaliacaoAtividade aval = new AvaliacaoAtividade();
			aval.setAtividade(atv);
			aval.setDataDistribuicao(new Date());
			aval.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AGUARDANDO_AVALIACAO));
			aval.setTipoAvaliacao(new TipoAvaliacao(dist.getTipoAvaliacao().getId()));
			aval.setRegistroEntradaDistribuicao(mov.getUsuarioLogado().getRegistroEntrada());
			if (dist.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA){
				aval.setAvaliadorAtividadeExtensao(avaliadorParecerista);
			} else {
				aval.setMembroComissao(avaliadorComissao);
			}
	
			atv.getAvaliacoes().add(aval);
		}
	}

	/**
	 * Cria uma avaliação, com as características passadas no Movimento, para a atividade informada.
	 *  
	 * @param atv
	 * @param mov
	 */
	private void adicionarAvaliadorAutoLinha(AtividadeExtensao atv, CadastroExtensaoMov mov) {
		MembroComissao avaliadorComissao = null;
		DistribuicaoAtividadeExtensao dist = mov.getDistribuicaoExtensao();

		String[] comissoes = atv.getLinhaAtuacao().getExpressaoMembrosComissao().split(","); 
		int avaliacao = 0;
		for (int i = 0; i < comissoes.length; i++) {
			avaliacao = 0;
			do {
				avaliadorComissao = getMembroComissaoAvaliacoesAutoLinha(
						Integer.parseInt(comissoes[i]), dist, atv, avaliacao);
				avaliacao++;
			} while (!ValidatorUtil.isNotEmpty(avaliadorComissao));

			AvaliacaoAtividade aval = new AvaliacaoAtividade();
			aval.setAtividade(atv);
			aval.setDataDistribuicao(new Date());
			aval.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AGUARDANDO_AVALIACAO));
			aval.setTipoAvaliacao(new TipoAvaliacao(dist.getTipoAvaliacao().getId()));
			aval.setRegistroEntradaDistribuicao(mov.getUsuarioLogado().getRegistroEntrada());
			aval.setMembroComissao(avaliadorComissao);
			atv.getAvaliacoes().add(aval);
			
			avaliadorComissao.setQntAvaliacoes(avaliadorComissao.getQntAvaliacoes()+1);
		}

	}
	
	private MembroComissao getMembroComissaoAvaliacoesAutoLinha(int comissao, 
				DistribuicaoAtividadeExtensao dist, AtividadeExtensao atv, int avaliacao) {
		for (MembroComissao membroComissao : dist.getMembrosComitePossiveis()) {
			if ( avaliacao == membroComissao.getQntAvaliacoes() && comissao == membroComissao.getPapel() 
					&& membroPassivelSelecao(atv, membroComissao) ) {
				dist.getMembrosComitePossiveis().remove(membroComissao);
				dist.getMembrosComitePossiveis().add(membroComissao);
				return membroComissao;
			}
		}
		return null;
	}

	private boolean membroPassivelSelecao(AtividadeExtensao atv, MembroComissao membroComissao) {
		//Verificando se este membro já está avaliando esta ação.
		for (AvaliacaoAtividade ava : atv.getAvaliacoes()) {
			if (ValidatorUtil.isNotEmpty(ava.getAvaliador()) && 
					membroComissao.getServidor().getPessoa().getId() == ava.getAvaliador().getPessoa().getId()) {
				return false;
			}
		}
		
		// Verificando se este membro faz parte da equipe do projeto, se fizer, não pode avaliá-lo.
		if ( atv.getProjeto().isPertenceEquipe(membroComissao.getServidor().getPessoa().getId())) {
			return false;
		}
		
		return true;
	}

	/**
	 * Retorna o próximo membro da comissão de extensão compatível com a ação.
	 * 
	 * @param atv
	 * @return
	 */
	private MembroComissao getProximoMembroComissaoCompativel(Collection<MembroComissao> avaliadoresPossiveis, AtividadeExtensao atv) {
		Collections.shuffle((List<MembroComissao>) avaliadoresPossiveis);
		
		for (MembroComissao membroComissao : avaliadoresPossiveis) {
			boolean compativel = true;
			
			//Verificando se este membro já está avaliando esta ação.
			for (AvaliacaoAtividade ava : atv.getAvaliacoes()) {
				if (ValidatorUtil.isNotEmpty(ava.getAvaliador()) && membroComissao.getServidor().getPessoa().getId() == ava.getAvaliador().getPessoa().getId()) {
					compativel = false;
					break;
				}
			}
			
			// Verificando se este membro faz parte da equipe do projeto, se fizer, não pode avaliá-lo.
			if (!compativel || atv.getProjeto().isPertenceEquipe(membroComissao.getServidor().getPessoa().getId())) {
				compativel = false;
			}

			if (compativel) {
				return membroComissao;
			}
		}
		return null;
	}

	
	/**
	 * Retorna o próximo parecerista compatível para avaliar a ação de extensão.
	 * 
	 * @param atv
	 * @return
	 */
	private AvaliadorAtividadeExtensao getProximoAvaliadorCompativel(Collection<AvaliadorAtividadeExtensao> avaliadoresPossiveis, AtividadeExtensao atv) {
		Collections.shuffle((List<AvaliadorAtividadeExtensao>) avaliadoresPossiveis);
		for (AvaliadorAtividadeExtensao parecerista : avaliadoresPossiveis) {
			boolean compativel = true;
			
			//Verificando se este membro já está avaliando esta ação.
			for (AvaliacaoAtividade ava : atv.getAvaliacoes()) {
				if (ValidatorUtil.isNotEmpty(ava.getAvaliador()) && parecerista.getServidor().getPessoa().getId() == ava.getAvaliador().getPessoa().getId()) {
					compativel = false;
					break;
				}
			}
			
			// Verificando se este membro faz parte da equipe do projeto, se fizer, não pode avaliá-lo.
			if (!compativel || atv.getProjeto().isPertenceEquipe(parecerista.getServidor().getPessoa().getId())) {
				compativel = false;
			}

			if (compativel) {
				return parecerista;
			}
		}
		return null;

	}
	
	
	/**
	 * Lista todos os avaliadores que podem avaliar a atividade selecionada.
	 * @throws DAOException 
	 */
	private Collection<AvaliadorAtividadeExtensao> filtrarPareceristasDisponiveis(CadastroExtensaoMov mov, int idAreaTematicaPrincipal) throws DAOException {
		AvaliadorAtividadeExtensaoDao dao = getDAO(AvaliadorAtividadeExtensaoDao.class, mov);
		try {
			/** @negocio: Somente avaliadores da mesma área da ação de extensão podem avaliá-la. */
			return dao.findByAreaTematica(idAreaTematicaPrincipal);
		} catch (DAOException e) {
			throw new DAOException(e);
		}finally {
			dao.close();
		}
	}

	/**
	 * Lista todos os membros do comite que podem avaliar a atividade selecionada.
	 * @return 
	 * @throws DAOException 
	 */
	private Collection<MembroComissao> filtrarMembrosComiteDisponiveis(CadastroExtensaoMov mov) throws DAOException {
		MembroComissaoDao dao = getDAO(MembroComissaoDao.class, mov);
		try {
			/** @negocio: Qualquer membro do comitê de extensão pode avaliar qualquer ação independente da área de atuação do membro. */
			return dao.findByComissao(MembroComissao.MEMBRO_COMISSAO_EXTENSAO);
		} catch (DAOException e) {
			throw new DAOException(e);
		}finally {
			dao.close();
		}
	}

	/**
	 * Verifica se as distribuições podem ser realizadas.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO, mov);
		CadastroExtensaoMov dMov = (CadastroExtensaoMov) mov;

		if (dMov.getDistribuicaoExtensao() != null) {
			DistribuicaoAtividadeExtensao dist = dMov.getDistribuicaoExtensao();

			if (ValidatorUtil.isEmpty(dist.getTipoAvaliacao())){
				throw new NegocioException("O tipo de avaliação não foi definido.");
			}

			if (mov.getCodMovimento().equals(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_MANUAL)) {
				if (ValidatorUtil.isEmpty(dist.getAtividade())) {
					throw new NegocioException("A Ação deve ser selecionada.");
				}
				if (ValidatorUtil.isEmpty(dist.getAtividade().getAvaliacoes())	&& ValidatorUtil.isEmpty(dist.getAvaliacoesRemovidas())) {
					throw new NegocioException("A atividade deve ser distribuída para pelo menos 1 avaliador(a).");
				}
			}


			if (mov.getCodMovimento().equals(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_AUTO)) {

				if (ValidatorUtil.isEmpty(dist.getAtividades())) {
					throw new NegocioException("Nenhuma Ação de Extensão foi selecionada.");
				}

				if (dist.getNumAvaliacoesPorProjeto() <= 0) {
					throw new NegocioException("Número de Avaliadores por Projeto deve ser maior que 0 (zero).");
				}

				
				/** Verificando lista de avaliadores disponíveis */
				if (dist.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) {
					if (ValidatorUtil.isEmpty(filtrarPareceristasDisponiveis(dMov, dist.getAreaTematica().getId()))) {
						throw new NegocioException("Não existem avaliadores disponíveis para o tipo de Área Temática selecionada.");
					}

				} else if (dist.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE) {
					if (ValidatorUtil.isEmpty(filtrarMembrosComiteDisponiveis(dMov))) {
						throw new NegocioException("Não existem avaliadores disponíveis.");
					}
				}

			}
		}
	}

}