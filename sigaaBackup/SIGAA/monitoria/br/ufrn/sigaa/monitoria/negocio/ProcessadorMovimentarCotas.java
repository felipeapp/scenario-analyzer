/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/06/2007
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.MovimentacaoCota;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;

/**
 * Processador para realizar movimenta��o de cotas entre projetos de monitoria
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 *
 */
public class ProcessadorMovimentarCotas extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);
		validate(mov);
		try {
        		if( mov.getCodMovimento().equals(SigaaListaComando.MOVIMENTAR_COTAS_MONITORIA)){
        			MovimentacaoCota movCotas = (MovimentacaoCota) cMov.getObjMovimentado();
        			movCotas.setData( new Date() );
        			movCotas.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
        			dao.create(movCotas);
        			
        			ProjetoEnsino projeto = dao.findByPrimaryKey( movCotas.getProjetoEnsino().getId() , ProjetoEnsino.class);			
        			int bolsasConcedidas = projeto.getBolsasConcedidas();
        			int bolsasSolicitadas = projeto.getBolsasSolicitadas();
        			
        			/** @regra: Dependendo do tipo de opera��o adiciona ou subtrai bolsas do projeto. */
        			if (movCotas.isCotaRemunerada()) {
        				if (movCotas.isIncluirBolsa()) {
	        			    bolsasConcedidas = bolsasConcedidas + movCotas.getQtdCotas();
	      					bolsasSolicitadas = bolsasSolicitadas + movCotas.getQtdCotas();
	      					dao.updateField(ProjetoEnsino.class, projeto.getId(), "bolsasSolicitadas", bolsasSolicitadas );
	        			}else if (movCotas.isRemoverBolsa()) {
	        			    bolsasConcedidas = bolsasConcedidas - movCotas.getQtdCotas();
	        			}			
	        			dao.updateField(ProjetoEnsino.class, projeto.getId(), "bolsasConcedidas", bolsasConcedidas );
        			}

        			if (movCotas.isCotaNaoRemunerada()) {
        				if (movCotas.isIncluirCotaNaoRemunerada()) {
        					bolsasSolicitadas = bolsasSolicitadas + movCotas.getQtdCotas();
	        			}else if (movCotas.isRemoverCotaNaoRemunerada()) {
	        				bolsasSolicitadas = bolsasSolicitadas - movCotas.getQtdCotas();
	        			}			
	        			dao.updateField(ProjetoEnsino.class, projeto.getId(), "bolsasSolicitadas", bolsasSolicitadas );
        			}
        			
        		}
        		return null;
		} finally {
        	    dao.close();
        	}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(new int[] {SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO}, mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		MovimentacaoCota movCotas = (MovimentacaoCota) cMov.getObjMovimentado();
		ListaMensagens lista = new ListaMensagens();		
		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, mov);
		try {

		    	lista = movCotas.validate();
		    
    			if(lista.isEmpty()){
    			
            			//Verifica o total de bolsistas ativos no projeto.
                		int totalBolsistasAtivosProjeto = dao.countMonitoresByProjeto(movCotas.getProjetoEnsino().getId(), TipoVinculoDiscenteMonitoria.BOLSISTA);
                		
                		//Verifica o total de volunt�rios ativos no projeto.
                		int totalVoluntariosAtivosProjeto = dao.countMonitoresByProjeto(movCotas.getProjetoEnsino().getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);
                
                		//Todas as bolsas do edital j� est�o sendo usadas?
                	    int bolsasOferecidasEdital = movCotas.getProjetoEnsino().getEditalMonitoria().getNumeroBolsas();
                	    	
                	    //Total de bolsas utilizadas para este edital
                		int totalBolsasConcedidas = dao.countBolsasConcedidasByEdital(movCotas.getProjetoEnsino().getEditalMonitoria().getId());
                		
        
                		/** @negocio: S� permite a movimenta��o de bolsas at� o limite disponibilizado no edital. */
                		if( movCotas.isIncluirBolsa() && (movCotas.getQtdCotas() > 0) && ((totalBolsasConcedidas + movCotas.getQtdCotas()) > bolsasOferecidasEdital)  ){
                		    lista.addErro("N�o h� bolsas dispon�veis neste edital. Para atribuir uma nova bolsa a este projeto � necess�rio remover uma bolsa de outro.");
                		}
                
                		//Validando Remo��o de Bolsas do projeto.
                		if( movCotas.isRemoverBolsa()) {		    
                			/** @negocio S� pode retirar as bolsas concedidas que N�O est�o sendo utilizadas pelo projeto */
                			int maxBolsasRemover = movCotas.getProjetoEnsino().getBolsasConcedidas() - totalBolsistasAtivosProjeto;
                			if (movCotas.getQtdCotas() > maxBolsasRemover || movCotas.getQtdCotas() > movCotas.getProjetoEnsino().getBolsasConcedidas()){
                				lista.addErro("Limite de Cotas Remuneradas (Bolsas) movimentadas excede o m�ximo permitido.");			
                			}
                		}
                		
                		//Validando Remo��o de Cotas N�O Remuneradas do projeto.
                		if ( movCotas.isRemoverCotaNaoRemunerada()) {			
                			/** @negocio S� pode retirar as vagas n�o remuneradas concedidas que N�O est�o sendo utilizadas pelo projeto */
                			int maxNaoRemuneradasRemover = movCotas.getProjetoEnsino().getBolsasNaoRemuneradas() - totalVoluntariosAtivosProjeto;	
                			if (movCotas.getQtdCotas() > maxNaoRemuneradasRemover || movCotas.getQtdCotas() > movCotas.getProjetoEnsino().getBolsasNaoRemuneradas()){
                			    lista.addErro("Limite de Cotas N�o Remuneradas movimentadas excede o m�ximo permitido.");			
                			}
                		}
    			}
        		
		} finally {
		    dao.close();   
        	}
		
		checkValidation(lista);
	}
}
