/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/09/2007
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;

/**
 * Processador responsável pela finalização de Planos de Trabalho.
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorFinalizacaoPlanosTrabalho extends AbstractProcessador {
	
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		checkRole(new int[]{ SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.DOCENTE}, mov);
		
		MovimentoCadastro movCad = (MovimentoCadastro)mov;
		
		@SuppressWarnings("unchecked")
		Collection<PlanoTrabalho> listaPlanos = (Collection<PlanoTrabalho>) movCad.getColObjMovimentado();
		PlanoTrabalho planoTrabalho = (PlanoTrabalho) movCad.getObjMovimentado();
		
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, mov);
		
		try {
			/** Finaliza Planos de Trabalho de uma determinada Cota **/
			if(mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_COM_COTA)){
				if(planoTrabalho != null && planoTrabalho.getId() == 0) {
					// Buscar cota
					CotaBolsas cota = planoDao.findByPrimaryKey(planoTrabalho.getCota().getId(), CotaBolsas.class);
					// Buscar planos ativos da cota selecionada
					Collection<PlanoTrabalho> planos = planoDao.findAtivosByCota(cota);
					
					if(planos != null && !planos.isEmpty()){
						for(PlanoTrabalho plano : planos){
							// Finaliza os planos em andamento da cota informada
							plano.setStatus(TipoStatusPlanoTrabalho.FINALIZADO);
							// Finaliza os bolsistas da cota informada
							if(plano.getMembroProjetoDiscente() != null){
								plano.getMembroProjetoDiscente().setDataFim(cota.getFim());
								plano.getMembroProjetoDiscente().setDataFinalizacao(cota.getFim());
							}
							// Gera o histórico
							HistoricoPlanoTrabalho historico = new HistoricoPlanoTrabalho();
							historico.setPlanoTrabalho(plano);
							historico.setStatus(TipoStatusPlanoTrabalho.FINALIZADO);
							historico.setData(new Date());
							historico.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
							if ( plano.getMembroProjetoDiscente() != null && plano.getMembroProjetoDiscente().getTipoBolsa() != null && plano.getMembroProjetoDiscente().getTipoBolsa().getId() == 0 )
								historico.setTipoBolsa( plano.getMembroProjetoDiscente().getTipoBolsa() );
							planoDao.updateField(PlanoTrabalho.class, plano.getId(), "status", plano.getStatus());
							if(plano.getMembroProjetoDiscente() != null)
								planoDao.updateFields(MembroProjetoDiscente.class, plano.getMembroProjetoDiscente().getId(),
										new String[]{"dataFim", "dataFinalizacao"}, 
										new Object[]{plano.getMembroProjetoDiscente().getDataFim(), plano.getMembroProjetoDiscente().getDataFinalizacao()});
							planoDao.createNoFlush(historico);
						}
						return planos.size();
					}
				}
			}
			/** Finaliza Planos de Trabalho SEM Cota **/
			else if(mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_SEM_COTA)){
				/** Finaliza Planos de Trabalho sem Cota pelo Gestor **/
				if(listaPlanos != null && !listaPlanos.isEmpty()){
					for(PlanoTrabalho plano : listaPlanos)
						finalizarPlano(plano, planoDao, mov);
					return listaPlanos.size();
				}
				/** Finaliza Planos de Trabalho sem Cota pelo Docente **/
				else if(planoTrabalho != null){
					planoTrabalho = planoDao.findByPrimaryKey(planoTrabalho.getId(), PlanoTrabalho.class);
					finalizarPlano(planoTrabalho, planoDao, mov);
				}
			}
		} finally {
			planoDao.close();
		}
		return null;
	}
	
	/**
	 * Responsável pela finalização de Planos de Trabalho.
	 * 
	 * @param plano
	 * @param planoDao
	 * @param mov
	 * @throws DAOException
	 */
	public void finalizarPlano(PlanoTrabalho plano, PlanoTrabalhoDao planoDao, Movimento mov) throws DAOException{
		// Finaliza o Plano em andamento.
		plano.setStatus(TipoStatusPlanoTrabalho.FINALIZADO);
		// Finaliza o bolsista.
		if(plano.getMembroProjetoDiscente() != null){
			plano.getMembroProjetoDiscente().setDataFim(new Date());
			plano.getMembroProjetoDiscente().setDataFinalizacao(new Date());
		}
		// Gera o histórico
		HistoricoPlanoTrabalho historico = new HistoricoPlanoTrabalho();
		historico.setPlanoTrabalho(plano);
		historico.setStatus(TipoStatusPlanoTrabalho.FINALIZADO);
		historico.setData(new Date());
		historico.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
		planoDao.updateField(PlanoTrabalho.class, plano.getId(), "status", plano.getStatus());
		if(plano.getMembroProjetoDiscente() != null)
			planoDao.updateFields(MembroProjetoDiscente.class, plano.getMembroProjetoDiscente().getId(),
					new String[]{"dataFim", "dataFinalizacao"}, 
					new Object[]{plano.getMembroProjetoDiscente().getDataFim(), plano.getMembroProjetoDiscente().getDataFinalizacao()});
		planoDao.createNoFlush(historico);
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
