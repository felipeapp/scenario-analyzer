/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gest�o de Atividades Acad�micas
 * Criado em: 11/12/2008
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.ProgramaProjeto;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/** 
 * Processador para realizar o controle do vinculo entre programas e projetos de pesquisa (ProgramaProjeto)
 * @author Victor Hugo
 */
public class ProcessadorProgramaProjeto extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoProgramaProjeto mov = (MovimentoProgramaProjeto) movimento;
		Unidade programa = mov.getPrograma();
		
		/** v�nculos que devem ser inseridos, seja para ser adicionado como para ser removido (inativado) */
		Set<ProgramaProjeto> vinculos = new HashSet<ProgramaProjeto>();
		
		
		//comentado, pois existia warning de "never read"	
//		boolean ehVinculado = false; /** vari�vel de controle pra saber se o projeto est� j� vinculado  ao programa*/
		forExterno: for( ProjetoPesquisa pp : mov.getProjetos() ){
		//comentado, pois existia warning de "never read"	
//			ehVinculado = false; /** inicializando */
			if( pp.isSelecionado() ){
				/** se o projeto for selecionado tem que ver se ele j� estava vinculado ao programa, 
				 * se j� estiver vinculado n�o faz nada, sen�o tem que vincula-lo */
				
				for( ProgramaProjeto programaProjeto : mov.getProgramasProjeto() ){
					if( pp.equals( programaProjeto.getProjeto() ) && programaProjeto.isAtivo() ){
						continue forExterno; /** se o projeto estiver selecionado e j� estiver vinculado ao programa n�o deve fazer nada */
					}
				}
				
				/** se chegou aqui � porque o projeto est� selecionado e ainda n�o est� vinculado ao programa, ent�o o vinculo � criado*/
				ProgramaProjeto vinculo = new ProgramaProjeto();
				vinculo.setPrograma(programa);
				vinculo.setProjeto(pp);
				vinculo.setAtivo(true);
				vinculos.add(vinculo);
				
			}else{
				/** se o projeto n�o estiver selecionado tem que verificar se ele estava vinculado ao programa, 
				 * se tiver tem que remover (inativar) o vinculo, se n�o tiver n�o faz nada */
				
				for( ProgramaProjeto programaProjeto : mov.getProgramasProjeto() ){
					if( pp.equals( programaProjeto.getProjeto() ) ){
						programaProjeto.setAtivo(false);
						vinculos.add( programaProjeto );
					}
				}
			}
			
		}
		
		GenericDAO dao = getDAO(mov);
		for( ProgramaProjeto vinculo : vinculos ){
			dao.createOrUpdate(vinculo);
		}
		
		return null;
	}

	public void validate(Movimento movimento) throws NegocioException, ArqException {
		checkRole( new int[] {SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS} , movimento);
	}

}
