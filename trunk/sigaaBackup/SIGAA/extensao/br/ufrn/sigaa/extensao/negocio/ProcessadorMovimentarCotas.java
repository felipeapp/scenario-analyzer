/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/12/2007
 *
 */
package br.ufrn.sigaa.extensao.negocio;

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
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.MovimentacaoCota;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/**
 * Processador para realizar movimenta��o de cotas entre atividades de extens�o.
 * 
 * @author Ilueny Santos
 *
 */
public class ProcessadorMovimentarCotas extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	    validate(mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(cMov);
		try {
        		if( cMov.getCodMovimento().equals(SigaaListaComando.MOVIMENTAR_COTAS_EXTENSAO)){
        			MovimentacaoCota movCota = (MovimentacaoCota) cMov.getObjMovimentado();
        			movCota.setData(new Date());
        			movCota.setRegistroEntrada( cMov.getUsuarioLogado().getRegistroEntrada() );
        			AtividadeExtensao atividade = dao.findByPrimaryKey( movCota.getAcaoExtensao().getId() , AtividadeExtensao.class);			
        			int bolsasConcedidas = atividade.getBolsasConcedidas();
        			
        			/** @regra: Dependendo do tipo de opera��o adiciona ou subtrai bolsas do projeto. */
        			if (movCota.isIncluirBolsa()) {
        			    bolsasConcedidas = bolsasConcedidas + movCota.getQtdCotas();
        			}else if (movCota.isRemoverBolsa()) {
        			    bolsasConcedidas = bolsasConcedidas - movCota.getQtdCotas();
        			}			
        			dao.updateField(AtividadeExtensao.class, atividade.getId(), "bolsasConcedidas", bolsasConcedidas);			
        			dao.create(movCota);
        		}
        		return null;
        		
		} finally {
        	    dao.close();
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	    checkRole(new int[] {SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO}, mov);
	    MovimentoCadastro cMov = (MovimentoCadastro) mov;
	    MovimentacaoCota movCota = (MovimentacaoCota) cMov.getObjMovimentado();
	    ListaMensagens lista  = movCota.validate();
	    
	    if(cMov.getCodMovimento().equals(SigaaListaComando.MOVIMENTAR_COTAS_EXTENSAO)){
	    	if(lista.isEmpty()){
		    	DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class, mov);
		    	try{
		    		// Atvidade de Extens�o
		    		AtividadeExtensao atividade = dao.findByPrimaryKey( movCota.getAcaoExtensao().getId() , AtividadeExtensao.class);
		    		
		    		if(atividade.getEditalExtensao() != null){
			    		// Total de bolsas j� concedidas do edital de extens�o no qual a Atividade faz parte
				    	int bolsasConcedidasEdital = dao.countBolsasConcedidasByEditalExtensao(atividade.getEditalExtensao().getId());
				    	
				    	// Total de bolsas que o edital de extens�o pode ter
				    	int totalBolsasDisponiveisEdital = atividade.getEditalExtensao().getNumeroBolsas();
				    	
				    	// Total de bolsistas internos(FAEX) da a��o de extens�o atual
				    	int totalBolsistasInternos = dao.countBolsistaByAtividadeOuVinculos(atividade.getId(), new Integer[] {TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO});
				    	
				    	// Total de bolsas concedidas para a atividade atual
				    	int totalBolsasConcedidasAtividade = atividade.getBolsasConcedidas();
				    	
				    	/**@regra S� � permitido incluir bolsas, se houverem bolsas dispon�veis no Edital de Extens�o */
				    	if(movCota.isIncluirBolsa()){
				    		if( (movCota.getQtdCotas() > 0) && ((bolsasConcedidasEdital + movCota.getQtdCotas()) > totalBolsasDisponiveisEdital) ){
				    			lista.addErro("N�o h� bolsas dispon�veis no Edital para o qual a A��o de Extens�o selecionada faz parte. " +
				    					"Para atribuir uma nova bolsa a essa A��o de Extens�o � necess�rio excluir uma bolsa de outra.");
				    		}
				    	}
				    	
				    	if(movCota.isRemoverBolsa()){
				    		/**@regra S� � permitido remover uma bolsa se a Atividade de extens�o tiver bolsas concedidas */
				    		if(totalBolsasConcedidasAtividade <= 0){
				    			lista.addErro("Esta A��o n�o tem bolsas concedidas. Para remover uma bolsa � necess�rio incluir uma.");
				    			
				    		/**@regra S� � permitido remover uma bolsa se a Atividade tiver uma bolsa livre(n�o utilizada por algum bolsista) */
				    		}else if(!(totalBolsasConcedidasAtividade > totalBolsistasInternos)){
				    			lista.addErro("Todas as bolsas dispon�veis est�o sendo usadas. Para remover uma bolsa, remova antes um bolsista vinculado a ela.");
				    		}else{
				    			int qtdMaximaBolsasRemoviveis = totalBolsasConcedidasAtividade - totalBolsistasInternos;
				    			if(movCota.getQtdCotas() > qtdMaximaBolsasRemoviveis){
				    				lista.addErro("O m�ximo de bolsas que podem ser removidas desta A��o � "+qtdMaximaBolsasRemoviveis+".");
				    			}
				    		}
				    	}
		    		}else{
		    			lista.addErro("Esta atividade n�o tem um Edital de Extens�o associado, logo n�o � poss�vel movimentar cotas.");
		    		}
			    }finally{
			    	dao.close();
			    }
	    	}
	    }
	    
	    checkValidation(lista);	    
	}

}