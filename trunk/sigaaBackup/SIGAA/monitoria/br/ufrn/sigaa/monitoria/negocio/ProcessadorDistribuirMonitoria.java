/**
 * 
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoRelatorioProjetoDao;
import br.ufrn.sigaa.arq.util.EnvioMensagemHelper;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoRelatorioProjeto;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ResumoSid;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;


/**
 * Processador responsável por distribuir projetos de ensino (monitoria)
 * para comissão de monitoria.
 * 
 * @author ilueny santos
 *
 */
public class ProcessadorDistribuirMonitoria extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {		
		validate(mov);		
		switch (( (DistribuicaoMonitoriaMov)mov  ).getAcao()) {
			case DistribuicaoMonitoriaMov.DISTRIBUIR_PROJETOS:
				distribuirProjeto( (DistribuicaoMonitoriaMov)mov  );
			break;
			case DistribuicaoMonitoriaMov.DISTRIBUIR_RELATORIOS:
				distribuirRelatorios( (DistribuicaoMonitoriaMov)mov  );
			break;		
			case DistribuicaoMonitoriaMov.DISTRIBUIR_RESUMOS:
				distribuirResumos( (DistribuicaoMonitoriaMov)mov  );
			break;			
		}
		return null;		
	}

	
	
	
	/**
	 * Distribuir projetos de monitoria para avaliação dos membros
	 * da comissão de monitoria.
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	private void distribuirProjeto(DistribuicaoMonitoriaMov mov) throws ArqException{		
	    ProjetoEnsino pm = (ProjetoEnsino) mov.getObjMovimentado();			
	    AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class, mov);

	    try {
			//Situação padrão
			pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO));
			pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO));
			
			/** @negocio: Se retirou todas as avaliações do projeto, ele volta para o status de aguardando distribuição. */
			if ((pm.getAvaliacoes() == null) || (pm.getAvaliacoes().isEmpty())){
			    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_PROJETO));
			}						
			dao.update(pm);
			
			/** @negocio: Todo avaliador recebe um e-mail de notificação informando que recebeu um projeto para avaliar.*/
			/** @negocio: Atualizando o usuário do avaliador para facilitar o posterior envio do e-mail de notificação. */
			for(AvaliacaoMonitoria avaliacao : pm.getAvaliacoes()){
			    avaliacao.getAvaliador().getServidor().setPrimeiroUsuario(
					dao.findByExactField(Usuario.class,"pessoa.id", avaliacao.getAvaliador().getServidor().getPessoa().getId(), true));
			}
			
			//removendo avaliações
			if ((mov.getAvaliadoresRemovidos() != null) && (!mov.getAvaliadoresRemovidos().isEmpty())){
				for (MembroComissao mem : mov.getAvaliadoresRemovidos()) {					
				    AvaliacaoMonitoria ava = dao.findByProjetoResumoAvaliador(pm.getId(), 0, mem.getId(), TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO);						
				    	if (!ValidatorUtil.isEmpty(ava)){
				    	    ava.setDataRetiradaDistribuicao(new Date());
				    	    ava.setRegistroEntradaRetiradaDistribuicao(mov.getUsuarioLogado().getRegistroEntrada());
				    	    ava.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIACAO_CANCELADA));
				    	    dao.update(ava);
					}
				}
			}
			
			//criando avaliações.
			for (AvaliacaoMonitoria ava : pm.getAvaliacoes()) {				
				if ((ava.getResumoSid() != null) && (ava.getResumoSid().getId() == 0)) {
				    ava.setResumoSid(null);
				}					
				dao.createOrUpdate(ava);
				/** @negocio: Informando ao avaliador que ele recebeu o projeto para avaliar. */
				EnvioMensagemHelper.notificarSubmissaoProjeto(ava.getProjetoEnsino().getProjeto(), EnvioMensagemHelper.PROJETO_MONITORIA, ava.getAvaliador().getServidor());
			}
			
			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
	 		ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

	    }finally {
		dao.close();
	    }
		
	}
	
	
	/**
	 * Distribuir relatórios de monitoria
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	private void distribuirRelatorios(DistribuicaoMonitoriaMov mov) throws ArqException{		
		AvaliacaoRelatorioProjetoDao dao = getDAO(AvaliacaoRelatorioProjetoDao.class, mov);
	    try {
	    	if ( mov.getObjAuxiliar() != null ) {
	    		Collection<RelatorioProjetoMonitoria> relProjMonitoria = (Collection<RelatorioProjetoMonitoria>) mov.getObjAuxiliar();
	    		for (RelatorioProjetoMonitoria relatorioProjMonit : relProjMonitoria) {
	    			distribuir(mov, dao, relatorioProjMonit, relatorioProjMonit.getProjetoEnsino());	
	    		}
	    	} else {
	    		distribuir(mov, dao, (RelatorioProjetoMonitoria) mov.getObjMovimentado(), ((RelatorioProjetoMonitoria) mov.getObjMovimentado()).getProjetoEnsino());
	    	}
	    	
		} finally {
			dao.close();
		}
	}

	private void distribuir(DistribuicaoMonitoriaMov mov, AvaliacaoRelatorioProjetoDao dao, RelatorioProjetoMonitoria rp,
			ProjetoEnsino pm) throws DAOException {

		//Situação padrão
		pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO));

		/** @negocio: Se retirou todas as avaliações do relatório, ele volta pra o status de aguardando distribuição do relatório. */
		if ((rp.getAvaliacoes() == null) || (rp.getAvaliacoes().isEmpty())){
		    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO));
		}
		
		dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", pm.getSituacaoProjeto().getId());
		
		//removendo avaliações
		if ((mov.getAvaliadoresRemovidos() != null) && (mov.getAvaliadoresRemovidos().size() > 0)){
			for (MembroComissao mem : mov.getAvaliadoresRemovidos()) {
				AvaliacaoRelatorioProjeto avaR = dao.findByRelatorioAvaliador(rp.getId(), mem.getId());
				if (!ValidatorUtil.isEmpty(avaR)){
					avaR.setRegistroEntradaRetiradaDistribuicao(mov.getUsuarioLogado().getRegistroEntrada());
					avaR.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIACAO_CANCELADA));
					dao.update(avaR);
				}
			}
		}

		//criando avaliações
		for (AvaliacaoRelatorioProjeto avaR : rp.getAvaliacoes()) {
		    dao.createOrUpdate(avaR);				
		}
		
		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
 		ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

	}
	
	/**
	 * Distribuir projetos de monitoria.
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	private void distribuirResumos(DistribuicaoMonitoriaMov mov) throws ArqException{		
	    ResumoSid resumo = (ResumoSid) mov.getObjMovimentado();
	    AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class, mov);

	    try {
			/** @negocio: Se retirou todas as avaliações do projeto, ele volta pra o status de aguardando distribuição */
			if ((resumo.getAvaliacoes() == null) || (resumo.getAvaliacoes().isEmpty())){
				resumo.setStatus( new StatusRelatorio(StatusRelatorio.AGUARDANDO_DISTRIBUICAO));
			}else{
				//testa se ainda falta alguém avaliar o relatório
				for (AvaliacaoMonitoria ava : resumo.getAvaliacoes()) {				
					if( (ava.getStatusAvaliacao().getId() == StatusAvaliacao.AGUARDANDO_AVALIACAO) ||
						(ava.getStatusAvaliacao().getId() == StatusAvaliacao.AVALIACAO_EM_ANDAMENTO) ){					
							resumo.setStatus( new StatusRelatorio(StatusRelatorio.AGUARDANDO_AVALIACAO));
							break;
					}				
				}
			
			}			
			dao.update(resumo);		
			
			
			//removendo avaliações
			if ((mov.getAvaliadoresRemovidos() != null) && (mov.getAvaliadoresRemovidos().size() > 0)){				
				//removendo o avaliações
				for (MembroComissao mem : mov.getAvaliadoresRemovidos()) {
						AvaliacaoMonitoria ava = dao.findByProjetoResumoAvaliador(0, resumo.getId(), mem.getId(), TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID);
						if ((ava != null) && (ava.getId() > 0)){
							ava.setDataRetiradaDistribuicao(new Date());
							ava.setRegistroEntradaRetiradaDistribuicao(mov.getUsuarioLogado().getRegistroEntrada());
							ava.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIACAO_CANCELADA));
							dao.update(ava);
						}
				}
			}
			
			//criando novas avaliações
			for (AvaliacaoMonitoria ava : resumo.getAvaliacoes()) {				
			    dao.createOrUpdate(ava);
			}
			
	    }finally {
			dao.close();
	    }
	}

	
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		DistribuicaoMonitoriaMov dMov = (DistribuicaoMonitoriaMov) mov;
		
		if (dMov.getObjMovimentado() != null){
			
			switch( dMov.getAcao() ){
				case DistribuicaoMonitoriaMov.DISTRIBUIR_PROJETOS:					
					ProjetoEnsino pm = (ProjetoEnsino)dMov.getObjMovimentado();
					
					if ( pm.getId() == 0 ){
						throw new NegocioException("O Projeto deve ser selecionado!");
					}
					
					if ( pm.getAvaliacoes() != null){
						int totalAvaliacao = 0;
						for (AvaliacaoMonitoria ava : pm.getAvaliacoes() ) {
							if (ava.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO && ava.isAtivo()) {
								totalAvaliacao++;
							}
						}
						if (totalAvaliacao > 2) {
							throw new NegocioException("O projeto deve ser distribuído para até 2 avaliadores.");
						}
					}
				break;
				
				case DistribuicaoMonitoriaMov.DISTRIBUIR_RELATORIOS:					
					RelatorioProjetoMonitoria rp = (RelatorioProjetoMonitoria)dMov.getObjMovimentado();
					Collection<RelatorioProjetoMonitoria> rpm = (Collection<RelatorioProjetoMonitoria>) dMov.getObjAuxiliar();
					
					if ( rp.getAvaliacoes() == null){
						throw new NegocioException("O Relatório deve ser distribuído para 1 membro da comissão de monitoria.");
					}
					
					if ( rp.getId() == 0 && rpm == null ){
						throw new NegocioException("O Projeto deve ser selecionado!");
					}

					if (rp.getTotalAvaliadoresRelatorioAtivos() > 1){
						throw new NegocioException("O Relatório deve ser avaliado por somente 1(um) membro da comissão de monitoria!");
					}
				break;
				
				case DistribuicaoMonitoriaMov.DISTRIBUIR_RESUMOS:					
					ResumoSid res = (ResumoSid)dMov.getObjMovimentado();
					
					if ( res.getAvaliacoes() == null){
						throw new NegocioException("O resumo deve ser distribuído para até 2 avaliadores.");
					}
					
					if ( res.getId() == 0 ){
						throw new NegocioException("O Projeto deve ser selecionado!");
					}
				break;

				default:
					throw new NegocioException("Tipo de distribuição desconhecido!");
			}
		}
	}
}