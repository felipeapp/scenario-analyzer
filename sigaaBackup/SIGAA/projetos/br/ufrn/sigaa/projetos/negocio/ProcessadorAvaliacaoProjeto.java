/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Processador respons�vel por opera��es relativas a avalia��o do projeto.
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
	 * Persiste a distribui��o autom�tica.
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

						/* Reativa a distribui��o para nova consolida��o */
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
	 * Realiza a distribui��o de projetos manualmente.
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
					 
					 /* Reativa a distribui��o para nova consolida��o */
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
	 * Realiza a avalia��o do projeto
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
			/* @negocio: Definindo valor padr�o da avalia��o. */
			avaliacao.calcularMedia();
			avaliacao.setSituacao(getGenericDAO(mov).findByPrimaryKey(TipoSituacaoAvaliacao.REALIZADA, TipoSituacaoAvaliacao.class));
			avaliacao.setDataAvaliacao(new Date());
			avaliacao.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.createOrUpdate(avaliacao);

			if(ValidatorUtil.isNotEmpty(avaliacao.getNotas()))
				for(NotaItemAvaliacao nota : avaliacao.getNotas()) {
					dao.createOrUpdate(nota);
				}

			/* @negocio: A an�lise de qualquer membro do comit� finaliza o processo de avalia��o do projeto. */
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
	 * M�todo que determina como ser� realizada a altera��o nos dados do projeto
	 * dependendo do resultado da avalia��o.
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
	 * M�todo que determina como ser� realizada a altera��o nos dados do projeto
	 * dependendo do resultado da avalia��o do relat�rio.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void avaliarRelatorio(Movimento mov) throws DAOException,NegocioException, ArqException {
		/*
		 * TODO atualizar dados do projeto:
		 * 1. verificar situa��o da avalia��o
		 * 2. verificar data fim dos membros do projeto (equipe)
		 * 3. verificar dados do or�amento concedido
		 * 4. alterar situa��o do projeto
		 * 5. gravar hist�rico da situa��o do projeto
		 * 
		 */
	}


	/***
	 * Remove, logicamente, uma avalia��o do projeto.
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
	 * Finaliza todas as avalia��es da lista de projetos informada.
	 * Este passo dever� ser realizado antes da classifica��o das propostas.
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
			//Finaliza a distribui��o
			dao.updateField(DistribuicaoAvaliacao.class, distribuicao.getId(), "avaliacaoConsolidada", Boolean.TRUE);
			
			//Alterando a situa��o de todos os projetos para avaliado
			for (Projeto projeto : projetos) {
					projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_AVALIADO));
					dao.updateField(Projeto.class, projeto.getId(), "situacaoProjeto.id", projeto.getSituacaoProjeto().getId());
					ProjetoHelper.gravarHistoricoSituacaoProjeto(projeto.getSituacaoProjeto().getId(), projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
			}
			
			//Cancelando as avalia��es pendentes.
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
	 * Respons�vel por realizar a classifica��o dos projetos.
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

			//Alterando a classifica��o de todos os projetos.
			for (Projeto projeto : projetos) {
				dao.updateField(Projeto.class, projeto.getId(), "classificacao", projeto.getClassificacao());
			}			

		}finally {	
			dao.close();
		}
	}
	
	
	/**
	 *  Realiza a valida��o das opera��es dispon�veis no processador.
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
					throw new NegocioException("Consolida��o n�o realizada. Ainda h� projetos pendentes de avalia��o.");
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
						throw new NegocioException("S� � poss�vel consolidar os projetos que tenham pelo menos uma avalia��o do comit� interno.");
					}
				}
			}
			
		}
		
	}

}
