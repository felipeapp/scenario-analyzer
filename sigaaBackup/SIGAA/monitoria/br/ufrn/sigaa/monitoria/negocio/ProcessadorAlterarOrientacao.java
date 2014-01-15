/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/03/2007
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;

/**
 * Este processador é responsável por alterar os orientadores de um monitor
 * 
 * @author Victor Hugo
 * @author ilueny santos
 */
public class ProcessadorAlterarOrientacao extends AbstractProcessador {

	public Object execute(Movimento alterarOrientacaomov) throws NegocioException, ArqException,
			RemoteException {		
		
		AlterarOrientacaoMov mov = (AlterarOrientacaoMov) alterarOrientacaomov;		
		validate(mov);		
		GenericDAO dao = getGenericDAO(mov);
		try {
				DiscenteMonitoria monitor = mov.getObjMovimentado();
				
				// Permite a alteração de orientações de monitores convocados.
				// @negocio: Em monitores convocados, a data de entrada e saída no 
				// projeto é definida no momento da aceitação da bolsa pelo discente.
				if (monitor.isConvocado()) {
					monitor.setDataInicio(null);
					monitor.setDataFim(null);
				}
				
				//Atualizando data de início e fim do monitor.
		    	dao.update(monitor);		    	
		    	Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		    	
        		/**
        		 * Alterando orientações 
        		 */
        		for( Orientacao ori : mov.getOrientacoesAtualizar() ){
        		    ori.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
        		    ori.setAtivo(true);        		    
        		    if (ori.isFinalizar() && (ori.getDataFim() == null || hoje.before(ori.getDataFim()))){
        			ori.setDataFim(hoje);
        		    }
        		    dao.update(ori);
        		}
        		
        		/**
        		 * adicionando novas orientações.
        		 */
        		for( Orientacao ori : mov.getOrientacoesAdicionar() ){		
        		    ori.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
        		    ori.setAtivo(true);
        		    dao.create(ori);
        		}		
        		
        		return null;
		} finally {
		    dao.close();
		}
	}

	
	public void validate(Movimento mov) throws NegocioException, ArqException {
	    AlterarOrientacaoMov aMov = (AlterarOrientacaoMov) mov;
	    
	    ListaMensagens lista = new ListaMensagens();	    
	    //Validando o discente que está sendo alterado
	    DiscenteMonitoriaValidator.validaDadosPrincipais((DiscenteMonitoria) aMov.getObjMovimentado(), lista);
	    
	    //validando as orientações do discente.
	    for( Orientacao ori : aMov.getOrientacoesAdicionar() ){		
		lista.addAll(ori.validate());
		if (!lista.isEmpty()) {
		    break;
		}
	    }		

	    checkValidation(lista);
	}

}