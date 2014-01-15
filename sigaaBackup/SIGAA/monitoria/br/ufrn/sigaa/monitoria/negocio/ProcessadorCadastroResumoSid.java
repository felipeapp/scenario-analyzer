/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 16/11/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ParticipacaoSid;
import br.ufrn.sigaa.monitoria.dominio.ResumoSid;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;

/**
 * Processador para cadastro de resumos do SID.
 *
 * @author ilueny santos
 *
 */
public class ProcessadorCadastroResumoSid extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
			
		if( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.CADASTRAR_RESUMO_SID)){
                    MovimentoCadastro cMov = (MovimentoCadastro) mov;
                    ResumoSid rSid = (ResumoSid) cMov.getObjMovimentado();

                    AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class, mov);
                    try {
                        rSid.setDataEnvio(new Date());
                        rSid.setAtivo(true);					
                        rSid.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());                        
                        rSid.setStatus(new StatusRelatorio(StatusRelatorio.AGUARDANDO_DISTRIBUICAO));
                    
                        //Verificando se o resumo já foi avaliado.
                        if (rSid.getId() > 0) {
                        	Collection<AvaliacaoMonitoria> avaliacoes = dao.findByProjetosStatusAvaliacao(null, rSid.getId(), StatusAvaliacao.AVALIADO_COM_RESSALVAS);
                        	if ((avaliacoes != null) && (!avaliacoes.isEmpty())) {
                        		rSid.setStatus(new StatusRelatorio(StatusRelatorio.AVALIADO));
                        	}
                        }                    
                        dao.createOrUpdate(rSid);						
                        
                        /** @negocio: Um resumo SID é feito com a participação dos discentes envolvidos no projeto. */
                        /** @negocio: Cadastra todas as participações com relatórios no SID. */
                        for (ParticipacaoSid ps : rSid.getParticipacoesSid()) {
                            dao.createOrUpdate(ps);
                        }
                        
                    } finally {
                        dao.close();
                    }
		}
			
		/** @negocio: O todos os participantes do SID devem ter sua frequências registrada. */
		/** @negocio: O registro da frequência permite que ele receba certificado de participação. */
		if( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.REGISTRAR_FREQUENCIA_RESUMO_SID)){
			ParticipacaoSidMov pMov = (ParticipacaoSidMov) mov;						
			GenericDAO dao = getGenericDAO(pMov);
			try {
			    //atualiza todas as participações com relatórios no SID
			    for (ParticipacaoSid ps : pMov.getParticipacoes()) {
				dao.update(ps);
			    }							
			} finally {
			    dao.close();
			}
		}
		
		/** @negocio: Resumo do SID pode ser removido. */	
		if( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.REMOVER_RESUMO_SID)){
		    MovimentoCadastro cMov = (MovimentoCadastro) mov;
		    ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class, mov);			
		    ResumoSid rSid = (ResumoSid) cMov.getObjMovimentado();
				
		    try {
			rSid.setAtivo(false);
			rSid.setStatus(new StatusRelatorio(StatusRelatorio.REMOVIDO));
			rSid.setRegistroEntradaExclusao(mov.getUsuarioLogado().getRegistroEntrada());    					
			dao.createOrUpdate(rSid);								
					
			//cadastra todas as participações com relatórios no sid
			for (ParticipacaoSid ps : rSid.getParticipacoesSid()) {
			    ps.setAtivo(false);
			    dao.createOrUpdate(ps);
			}							
		    } finally {
			dao.close();
		    }
		}
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}