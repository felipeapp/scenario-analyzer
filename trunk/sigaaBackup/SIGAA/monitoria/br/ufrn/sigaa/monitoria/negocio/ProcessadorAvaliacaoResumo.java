/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 24/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ResumoSid;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;

/**
 * Processador para cadastro de avaliações de resumos SID.
 *
 * @author David Ricardo
 * @author Ilueny Santos
 *
 */
public class ProcessadorAvaliacaoResumo extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		GenericDAO dao = getGenericDAO(mov);
		AvaliacaoMonitoria ava = (AvaliacaoMonitoria) ((MovimentoCadastro) mov).getObjMovimentado();
		
		try {
			if (ava.getDataAvaliacao() == null) {
			    ava.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIACAO_EM_ANDAMENTO));
			} else {				
			    /** @negocio: O novo status do resumo SID depende do status da avaliação. */
			    int novoStatusResumo = ava.getResumoSid().getStatus().getId();
				
			    if (ava.getStatusAvaliacao().getId() == StatusAvaliacao.AVALIADO_COM_RESSALVAS){
				novoStatusResumo = StatusRelatorio.AVALIADO_COM_RESSALVAS;
			    }
			    if (ava.getStatusAvaliacao().getId() == StatusAvaliacao.AVALIADO_SEM_RESSALVAS){
				novoStatusResumo = StatusRelatorio.AVALIADO;
			    }
			    dao.updateField(ResumoSid.class, ava.getResumoSid().getId(), "status.id", novoStatusResumo);
			}
			dao.update(ava);					
			return ava;
		} finally {
			dao.close();
		}
	}
	
	
	public void validate(Movimento mov) throws NegocioException, ArqException {		
	    AvaliacaoMonitoria ava = (AvaliacaoMonitoria) ((MovimentoCadastro) mov).getObjMovimentado();
	    
	    /** @negocio: Se o resumo for avaliado com ressalvas uma justificativa deve ser informada. */
	    if ( (ava.getStatusAvaliacao().getId() == StatusAvaliacao.AVALIADO_COM_RESSALVAS) && ( (ava.getParecer() == null) || ("".equals(ava.getParecer().trim())))) {
		throw new NegocioException("É obrigatório acrescentar uma justificativa se o resumo for avaliado com ressalvas.");
	    }
	}

}