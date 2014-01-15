package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;

/** Processador responsável por operações de discente projeto. */
public class ProcessadorDiscenteProjeto extends AbstractProcessador {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoCadastro mov = (MovimentoCadastro) movimento;	
		validate(mov);	
		
		/**
		 * Finaliza discente projeto do plano de trabalho.
		 */
		if( mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_DISCENTE_PROJETO_PLANO) ){
			finalizarDiscenteProjeto(mov);
		}
		/**
		 * Indica discente para plano de trabalho 
		 */
		if( mov.getCodMovimento().equals(SigaaListaComando.INDICAR_DISCENTE_PROJETO_PLANO)){
			indicarDiscenteProjeto(mov);
		}
		/**
		 * Finaliza discente projeto do plano de trabalho.
		 */
		if( mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_DISCENTE_PROJETO_PARA_AFASTADO) ){
			finalizarAllDiscenteProjetoByDiscente(mov);
		}

		return null;
	}
	
	/**
	 * Finalizando o discente projeto do plano de trabalho.
	 */
	private void finalizarDiscenteProjeto(MovimentoCadastro mov) throws DAOException{
		GenericDAO dao = getGenericDAO(mov);
		RegistroEntrada registroEntrada = mov.getUsuarioLogado().getRegistroEntrada();
		try{
			PlanoTrabalhoProjeto plano =(PlanoTrabalhoProjeto) mov.getObjMovimentado();
			if (plano.getDiscenteProjeto() != null){
				DiscenteProjeto discentePro = plano.getDiscenteProjeto();
				//TODO: regras para de validação do relatório final do discente.			
				discentePro.setSituacaoDiscenteProjeto(new TipoSituacaoDiscenteProjeto(TipoSituacaoDiscenteProjeto.FINALIZADO));
				discentePro.setDataFim(new Date());
				dao.update(discentePro);
				DiscenteProjetoHelper.gravarHistoricoSituacao(dao, discentePro, registroEntrada);
			}
			dao.update(plano);
		}
		finally{
			dao.close();
		}
	}
	
	/** Indica Discente para plano 
	 * @throws DAOException */
	private void indicarDiscenteProjeto(MovimentoCadastro mov) throws DAOException{
		GenericDAO dao = getGenericDAO(mov);
		RegistroEntrada registroEntrada = mov.getUsuarioLogado().getRegistroEntrada();
		try{
			PlanoTrabalhoProjeto planoPro =(PlanoTrabalhoProjeto) mov.getObjMovimentado();			

			if (planoPro.getDiscenteProjetoNovo() != null){				
				DiscenteProjeto discenteAntigo = planoPro.getDiscenteProjeto();

				if (discenteAntigo != null){
					discenteAntigo.setSituacaoDiscenteProjeto( new TipoSituacaoDiscenteProjeto(TipoSituacaoDiscenteProjeto.FINALIZADO));
					dao.update(discenteAntigo);

					DiscenteProjetoHelper.gravarHistoricoSituacao(dao, discenteAntigo, registroEntrada);		
				}

				DiscenteProjeto discenteNovo = planoPro.getDiscenteProjetoNovo();
				discenteNovo.setAtivo(true);
				discenteNovo.setSituacaoDiscenteProjeto(new TipoSituacaoDiscenteProjeto(TipoSituacaoDiscenteProjeto.SELECIONADO));
				discenteNovo.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				discenteNovo.setDiscenteProjetoAnterior(discenteAntigo);
				discenteNovo.setPlanoTrabalhoProjeto(planoPro);
				discenteNovo.setProjeto(planoPro.getProjeto());
				discenteNovo.setDataFim(planoPro.getDataFim()); //definindo data fim padrão para discente (data fim do plano de trabalho)
				dao.create(discenteNovo);

				DiscenteProjetoHelper.gravarHistoricoSituacao(dao, discenteNovo, registroEntrada);

				//Novo discente no plano
				planoPro.setDiscenteProjeto(discenteNovo);
				dao.update(planoPro);
			}
		}finally{
			dao.close();
		}
	}

	/**
	 * Finalizando o discente projeto do plano de trabalho.
	 */
	@SuppressWarnings("unchecked")
	private void finalizarAllDiscenteProjetoByDiscente(MovimentoCadastro mov) throws DAOException{
		GenericDAO dao = getGenericDAO(mov);
		RegistroEntrada registroEntrada = mov.getUsuarioLogado().getRegistroEntrada();
		try{
			Collection<DiscenteProjeto> listDiscenteProjeto = (Collection<DiscenteProjeto>) mov.getObjAuxiliar();
			for (DiscenteProjeto discentePro : listDiscenteProjeto) {
				//TODO: regras para de validação do relatório final do discente.			
				discentePro.setSituacaoDiscenteProjeto(new TipoSituacaoDiscenteProjeto(TipoSituacaoDiscenteProjeto.FINALIZADO));
				discentePro.setDataFim(new Date());
				dao.update(discentePro);
				DiscenteProjetoHelper.gravarHistoricoSituacao(dao, discentePro, registroEntrada);
			}
		}
		finally{
			dao.close();
		}
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
