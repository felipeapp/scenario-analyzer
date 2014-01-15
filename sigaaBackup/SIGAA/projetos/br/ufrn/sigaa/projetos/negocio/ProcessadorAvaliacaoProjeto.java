/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 11/10/2010
 * 
 */
package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.util.EnvioMensagemHelper;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.NotaItemAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliador;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador responsável por operações relativas a avaliação do projeto.
 *  
 * @author Ilueny Santos
 *
 */
public class ProcessadorAvaliacaoProjeto extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);	
		if (mov.getCodMovimento().equals(SigaaListaComando.DISTRIBUIR_PROJETO_MANUAL)) {
			distribuirManual(mov);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.DISTRIBUIR_PROJETO_AUTOMATICO)) {
			distribuirAutomatico(mov);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.AVALIAR_PROJETO)) {
			avaliar(mov);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_AVALIACAO_PROJETO)) {
			removerAvaliacao(mov);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_AVALIACOES_PROJETO)) {
			consolidarAvaliacoes(mov);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.CLASSIFICAR_PROJETOS)) {
			classificar(mov);
		}

		
		return mov;
	}

	/**
	 * Persiste a distribuição automática.
	 * @param mov
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private void distribuirAutomatico(Movimento mov) throws ArqException, NegocioException {
		GenericDAO dao = getGenericDAO(mov);
		MovimentoCadastro movC = (MovimentoCadastro) mov;
		EstrategiaDistribuicaoProjetos estrategia = (EstrategiaDistribuicaoProjetos) movC.getObjAuxiliar();
		try {
			for(Projeto projeto: estrategia.getProjetos()){
				projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO));
				dao.updateField(Projeto.class, projeto.getId(), "situacaoProjeto.id", projeto.getSituacaoProjeto().getId());
				ProjetoHelper.gravarHistoricoSituacaoProjeto(projeto.getSituacaoProjeto().getId(), projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
				for (Avaliacao ava : projeto.getAvaliacoes()) {
					if (ava.getId() == 0) { 
						/* @negocio: Enviando e-mail para o avaliador que recebeu o projeto para avaliar. */
						EnvioMensagemHelper.notificarAvaliadorProjeto(ava.getAvaliador().getServidor(), ava.getDistribuicao().getMsgNotificacaoAvaliadores());

						/* Reativa a distribuição para nova consolidação */
						dao.updateField(DistribuicaoAvaliacao.class, ava.getDistribuicao().getId(), "avaliacaoConsolidada", Boolean.FALSE);
					}
					ava.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
					dao.createOrUpdate(ava);
				}
			}
		} finally {
			dao.close();
		}	
	}

	/**
	 * Realiza a distribuição de projetos manualmente.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void distribuirManual(Movimento mov) throws DAOException, NegocioException, ArqException {
		GenericDAO dao = getGenericDAO(mov);
		MovimentoCadastro movC = (MovimentoCadastro) mov;
		EstrategiaDistribuicaoProjetos estrategia = (EstrategiaDistribuicaoProjetos) movC.getObjAuxiliar();
		estrategia.distribuir();
		Projeto projeto  = estrategia.getProjeto();
		try {

			projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO));
			dao.updateField(Projeto.class, projeto.getId(), "situacaoProjeto.id", projeto.getSituacaoProjeto().getId());
			ProjetoHelper.gravarHistoricoSituacaoProjeto(projeto.getSituacaoProjeto().getId(), projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
			for (Avaliacao ava : projeto.getAvaliacoes()) {
				if (ava.getId() == 0) {		    
					/* @negocio: Enviando e-mail para o avaliador que recebeu o projeto para avaliar. */
					 EnvioMensagemHelper.notificarAvaliadorProjeto(ava.getAvaliador().getServidor(), ava.getDistribuicao().getMsgNotificacaoAvaliadores());
					 
					 /* Reativa a distribuição para nova consolidação */
					dao.updateField(DistribuicaoAvaliacao.class, ava.getDistribuicao().getId(), "avaliacaoConsolidada", Boolean.FALSE);
				}
				ava.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				dao.createOrUpdate(ava);				
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Realiza a avaliação do projeto
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void avaliar(Movimento mov) throws DAOException,NegocioException, ArqException {
		validate(mov);
		Avaliacao avaliacao = (Avaliacao) ((MovimentoCadastro)mov).getObjMovimentado();
		AvaliacaoDao dao = getDAO(AvaliacaoDao.class, mov);

		try {
			/* @negocio: Definindo valor padrão da avaliação. */
			avaliacao.calcularMedia();
			avaliacao.setSituacao(getGenericDAO(mov).findByPrimaryKey(TipoSituacaoAvaliacao.REALIZADA, TipoSituacaoAvaliacao.class));
			avaliacao.setDataAvaliacao(new Date());
			avaliacao.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.createOrUpdate(avaliacao);

			if(ValidatorUtil.isNotEmpty(avaliacao.getNotas()))
				for(NotaItemAvaliacao nota : avaliacao.getNotas()) {
					dao.createOrUpdate(nota);
				}

			/* @negocio: A análise de qualquer membro do comitê finaliza o processo de avaliação do projeto. */
			if (avaliacao.getDistribuicao().getModeloAvaliacao().getTipoAvaliacao().getId() == TipoAvaliacao.PROJETOS) {
				avaliarProjeto(mov);
			}
			if (avaliacao.getDistribuicao().getModeloAvaliacao().getTipoAvaliacao().getId() == TipoAvaliacao.RELATORIOS) {
				avaliarRelatorio(mov);
			}

		}finally {	
			dao.close();
		}

	}

	/**
	 * Método que determina como será realizada a alteração nos dados do projeto
	 * dependendo do resultado da avaliação.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void avaliarProjeto(Movimento mov) throws DAOException,NegocioException, ArqException {
		EstrategiaAvaliacaoProjetos estrategiaAvaliacao = (EstrategiaAvaliacaoProjetos) ((MovimentoCadastro)mov).getObjAuxiliar();
		Avaliacao avaliacao = (Avaliacao) ((MovimentoCadastro)mov).getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			estrategiaAvaliacao.avaliar(avaliacao);
			dao.updateField(Projeto.class, avaliacao.getProjeto().getId(), "media", avaliacao.getProjeto().getMedia());
		}finally {	
			dao.close();
		}
	}


	/**
	 * Método que determina como será realizada a alteração nos dados do projeto
	 * dependendo do resultado da avaliação do relatório.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void avaliarRelatorio(Movimento mov) throws DAOException,NegocioException, ArqException {
		/*
		 * TODO atualizar dados do projeto:
		 * 1. verificar situação da avaliação
		 * 2. verificar data fim dos membros do projeto (equipe)
		 * 3. verificar dados do orçamento concedido
		 * 4. alterar situação do projeto
		 * 5. gravar histórico da situação do projeto
		 * 
		 */
	}


	/***
	 * Remove, logicamente, uma avaliação do projeto.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void removerAvaliacao(Movimento mov) throws DAOException,    NegocioException, ArqException {
		GenericDAO dao = getGenericDAO(mov);
		MovimentoCadastro movC = (MovimentoCadastro) mov;
		Avaliacao avaliacao = dao.findByPrimaryKey(movC.getObjMovimentado().getId(), Avaliacao.class);
		try {    		
			validate(movC);
			dao.updateFields(Avaliacao.class, avaliacao.getId(), 
					new String[] {"ativo","registroEntrada.id"}, 
					new Object[] {Boolean.FALSE, mov.getUsuarioLogado().getRegistroEntrada().getId()});
		} finally {
			dao.close();
		}
	}

	/**
	 * Finaliza todas as avaliações da lista de projetos informada.
	 * Este passo deverá ser realizado antes da classificação das propostas.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void consolidarAvaliacoes(Movimento mov) throws DAOException,NegocioException, ArqException {
		DistribuicaoAvaliacao distribuicao = (DistribuicaoAvaliacao) ((MovimentoCadastro)mov).getObjAuxiliar();
		@SuppressWarnings("unchecked")
		List<Projeto> projetos = (List<Projeto>) ((MovimentoCadastro)mov).getColObjMovimentado();
		AvaliacaoDao dao = getDAO(AvaliacaoDao.class, mov);
		try {
			//Finaliza a distribuição
			dao.updateField(DistribuicaoAvaliacao.class, distribuicao.getId(), "avaliacaoConsolidada", Boolean.TRUE);
			
			//Alterando a situação de todos os projetos para avaliado
			for (Projeto projeto : projetos) {
					projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AVALIADO));
					dao.updateField(Projeto.class, projeto.getId(), "situacaoProjeto.id", projeto.getSituacaoProjeto().getId());
					ProjetoHelper.gravarHistoricoSituacaoProjeto(projeto.getSituacaoProjeto().getId(), projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
			}
			
			//Cancelando as avaliações pendentes.
			List<Avaliacao> avaliacoesPendentes = dao.findByDistribuicaoSituacao(distribuicao.getId(), TipoSituacaoAvaliacao.PENDENTE);
			for (Avaliacao ava : avaliacoesPendentes) {
				dao.updateFields(Avaliacao.class, ava.getId(), 
						new String[] {"situacao.id","ativo","registroEntrada.id"}, 
						new Object[] {TipoSituacaoAvaliacao.CANCELADA, Boolean.FALSE, mov.getUsuarioLogado().getRegistroEntrada().getId()});
			}			
		}finally {	
			dao.close();
		}
	}
	
	/**
	 * Responsável por realizar a classificação dos projetos.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void classificar(Movimento mov) throws DAOException,NegocioException, ArqException {
		@SuppressWarnings("unchecked")
		List<Projeto> projetos = (List<Projeto>) ((MovimentoCadastro)mov).getColObjMovimentado();
		AvaliacaoDao dao = getDAO(AvaliacaoDao.class, mov);

		try {

			//Alterando a classificação de todos os projetos.
			for (Projeto projeto : projetos) {
				dao.updateField(Projeto.class, projeto.getId(), "classificacao", projeto.getClassificacao());
			}			

		}finally {	
			dao.close();
		}
	}
	
	
	/**
	 *  Realiza a validação das operações disponíveis no processador.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		if (mov.getCodMovimento().equals(SigaaListaComando.AVALIAR_PROJETO)) {
			checkRole(new int[] {SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.AVALIADOR_ACOES_ASSOCIADAS, SigaaPapeis.MEMBRO_COMITE_PESQUISA}, mov);
			Avaliacao avaliacao = (Avaliacao) ((MovimentoCadastro)mov).getObjMovimentado();
			checkValidation(avaliacao.validate());
		}
		
		if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_AVALIACOES_PROJETO)) {
			checkRole(new int[]{SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA}, mov);
			@SuppressWarnings("unchecked")
			List<Projeto> projetos = (List<Projeto>) ((MovimentoCadastro)mov).getColObjMovimentado();		
			for (Projeto projeto : projetos) {
				if (projeto.getMedia() == null) {
					throw new NegocioException("Consolidação não realizada. Ainda há projetos pendentes de avaliação.");
				}
			}
			
			for (Projeto projeto : projetos) {
				boolean erroAvaliacaoComiteInterno = true;
				List<Avaliacao> avaliacoes = projeto.getAvaliacoes();
				if (ValidatorUtil.isNotEmpty(avaliacoes)) {
					for (Avaliacao avaliacao : avaliacoes) {
						if (avaliacao.getDistribuicao().getTipoAvaliador().getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO) {
							erroAvaliacaoComiteInterno = false;
						}
					}
					if (erroAvaliacaoComiteInterno) {
						throw new NegocioException("Só é possível consolidar os projetos que tenham pelo menos uma avaliação do comitê interno.");
					}
				}
			}
			
		}
		
	}

}
