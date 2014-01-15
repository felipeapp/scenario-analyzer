/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas
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
		
		/** vínculos que devem ser inseridos, seja para ser adicionado como para ser removido (inativado) */
		Set<ProgramaProjeto> vinculos = new HashSet<ProgramaProjeto>();
		
		
		//comentado, pois existia warning de "never read"	
//		boolean ehVinculado = false; /** variável de controle pra saber se o projeto está já vinculado  ao programa*/
		forExterno: for( ProjetoPesquisa pp : mov.getProjetos() ){
		//comentado, pois existia warning de "never read"	
//			ehVinculado = false; /** inicializando */
			if( pp.isSelecionado() ){
				/** se o projeto for selecionado tem que ver se ele já estava vinculado ao programa, 
				 * se já estiver vinculado não faz nada, senão tem que vincula-lo */
				
				for( ProgramaProjeto programaProjeto : mov.getProgramasProjeto() ){
					if( pp.equals( programaProjeto.getProjeto() ) && programaProjeto.isAtivo() ){
						continue forExterno; /** se o projeto estiver selecionado e já estiver vinculado ao programa não deve fazer nada */
					}
				}
				
				/** se chegou aqui é porque o projeto está selecionado e ainda não está vinculado ao programa, então o vinculo é criado*/
				ProgramaProjeto vinculo = new ProgramaProjeto();
				vinculo.setPrograma(programa);
				vinculo.setProjeto(pp);
				vinculo.setAtivo(true);
				vinculos.add(vinculo);
				
			}else{
				/** se o projeto não estiver selecionado tem que verificar se ele estava vinculado ao programa, 
				 * se tiver tem que remover (inativar) o vinculo, se não tiver não faz nada */
				
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
