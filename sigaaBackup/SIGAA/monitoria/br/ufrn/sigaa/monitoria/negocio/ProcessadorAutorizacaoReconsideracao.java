/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 09/04/2006
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
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoReconsideracao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador usado nas opera��es de autoriza��o de pedidos de reconsidera��o
 * do projeto quanto a formaliza��o pela prograd.
 *
 * 
 * @author ilueny santos
 *
 */
public class ProcessadorAutorizacaoReconsideracao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);		
		switch( ( (MovimentoCadastro)mov ).getAcao() ){
			case MovimentoCadastro.ACAO_CRIAR:
			    criar(mov);
			    break;
			case MovimentoCadastro.ACAO_REMOVER:
			    remover(mov);
			    break;
			default:
			    throw new NegocioException("Tipo de a��o desconhecida.");
		}
		return null;		
	}
	
	/**
	 * Cria ou altera uma autoriza��o dependendo do id
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object criar(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		GenericDAO dao = getGenericDAO(mov);
       		MovimentoCadastro cMov = (MovimentoCadastro) mov;		
       		AutorizacaoReconsideracao recosideracao = (AutorizacaoReconsideracao) cMov.getObjMovimentado();		
       		ProjetoEnsino pm = recosideracao.getProjetoEnsino();
       		
       		try {
        		
        		if (recosideracao.getId() > 0){		
        			dao.update(recosideracao);
        			if(recosideracao.isAutorizado()){
        				pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO));
        				
        				/** @negocio: Remove todas as autoriza��es dos departamentos porque abriu o projeto para edi��o. */
        				for (AutorizacaoProjetoMonitoria autorizacaoDepto : pm.getAutorizacoesProjeto()) {
        				    autorizacaoDepto.setAtivo(false);
        				    dao.update(autorizacaoDepto);
        				}
        
        			}else{
        				pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_NAO_RECOMENDADO));
        			}
        			
        		}else{
        			dao.create(recosideracao);
        			pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_ANALISANDO_SOLICITACAO_DE_RECONSIDERACAO));
        
        			/** @negocio: Remove todas as autoriza��es dos departamentos porque a pr�-reitoria de gradua��o est� analisando. */
        			for (AutorizacaoProjetoMonitoria autorizacaoDepto : pm.getAutorizacoesProjeto()) {				
        			    autorizacaoDepto.setAtivo(false);
        			    dao.update(autorizacaoDepto);
        			}
        		}
        
        		dao.update(pm);
        		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
         		ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());		
        		return null;
		} finally {
		    dao.close();
		}
	}
	
 	
 	public Object remover(Movimento mov) throws NegocioException, ArqException,	RemoteException { 		
		GenericDAO dao = getGenericDAO(mov);
       		MovimentoCadastro cMov = (MovimentoCadastro) mov;
       		AutorizacaoReconsideracao auto = (AutorizacaoReconsideracao) cMov.getObjMovimentado();
       		
       		try {
       		    auto.setAtivo(false);
       		    dao.update(auto);		
       		    return null;
		} finally {
		    dao.close();
		}
 	}
 	

	public void validate(Movimento mov) throws NegocioException, ArqException {
		switch(((MovimentoCadastro)mov).getAcao()){
		case MovimentoCadastro.ACAO_CRIAR:
			break;
		case MovimentoCadastro.ACAO_REMOVER:
			break;
		}
	}

}