/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador responsável pela persistência do resultado da análise das avaliações
 * dos projetos de pesquisa
 *
 * @author Victor Hugo
 * @author Ricardo Wendell
 *
 */
public class ProcessadorAnalisarAvaliacoes extends AbstractProcessador {

	/* 
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);

		MovimentoAnalisarAvaliacoes movAnalisar = (MovimentoAnalisarAvaliacoes) mov;
		GenericDAO dao = getDAO(mov);
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, mov);
		
		try {
		
			// Percorrer lista de projetos e atualizar os status e históricos
			for( ProjetoPesquisa projetoPesquisa : movAnalisar.getProjetos() ){
	
				Boolean aprovado = projetoPesquisa.getAprovado();
				Double media = projetoPesquisa.getProjeto().getMedia();
	
				projetoPesquisa = dao.refresh( projetoPesquisa );
				projetoPesquisa.getProjeto().setUsuarioLogado( mov.getUsuarioLogado() );
				projetoPesquisa.getProjeto().setMedia(media);
	
				if( aprovado == Boolean.TRUE ){
	
					// Gravar histórico de aprovação e marcá-lo como aprovado
					ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,TipoSituacaoProjeto.APROVADO, projetoPesquisa);
	
				} else if( aprovado == Boolean.FALSE ){
	
					// Marcar projeto como reprovado
					ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,TipoSituacaoProjeto.REPROVADO, projetoPesquisa);
	
					// Buscar planos de trabalho associados e marcá-los como não aprovados
					Collection<PlanoTrabalho> planos = planoDao.findByProjeto(projetoPesquisa);
	
					for (PlanoTrabalho plano : planos) {
						plano.setStatus(TipoStatusPlanoTrabalho.PROJETO_NAO_APROVADO);
						persist(plano, planoDao);
	
						HistoricoPlanoTrabalho historicoPlano = ProcessadorPlanoTrabalho.gerarEntradaHistorico(plano, mov);
						persist(historicoPlano, planoDao);
					}
				}
	
				dao.update( projetoPesquisa );
				dao.detach( projetoPesquisa );
			} 

		} finally {
			dao.close();
			planoDao.close();
		}

		return null;
	}

	/**
	 * Atualiza ou cria um novo objeto no banco dependendo se ele possui ou não id
	 * @param obj
	 * @throws DAOException
	 */
	private void persist(PersistDB obj, GenericDAO dao) throws DAOException {
		if( obj.getId() > 0 )
			dao.update(obj);
		else
			dao.create(obj);
	}
	
	/* 
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);
	}

}
