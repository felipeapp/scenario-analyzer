/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Aug 26, 2008
 *
 */
package br.ufrn.sigaa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.sites.dominio.SecaoExtraSite;

/**
 * A classe é responsável em persistir os dados referente ao 
 * caso de uso notícia de um portal público
 * @author Mário Rizzi
 */
public class ProcessadorSecaoExtraSite extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

	
		if(movimento.getCodMovimento() == SigaaListaComando.ATUALIZAR_SECAO_EXTRA_SITE ){
			validate(movimento);
			atualizarSecaoExtraSite(movimento);
		}else if( movimento.getCodMovimento() == SigaaListaComando.REMOVER_SECAO_EXTRA_SITE ){
			removerSecaoExtraSite(movimento);
		}
		
		return movimento;
	}
	
	/**
	 * Atualiza os dados de uma notícia previamente cadastrada
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void atualizarSecaoExtraSite(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoSecaoExtraSite mov = (MovimentoSecaoExtraSite) movimento;
		SecaoExtraSite secao = mov.getSecaoExtraSite();
		GenericDAO dao = getDAO(mov);
		
		try {

			/* Criar ou atualizar o perfil do docente */
			if (secao.getId() == 0) {
				dao.create(secao);
			} else {
				dao.update(secao);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Erro na atualização da seção extra.");
		}
	}
	
	/**
	 * Remove a seção extra do site
	 * @param movimento
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String removerSecaoExtraSite(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		  
		MovimentoSecaoExtraSite mov = (MovimentoSecaoExtraSite) movimento;
		getGenericDAO(mov).remove(mov.getSecaoExtraSite());
        return null;
          
	 }
	
	
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		checkRole(new int[] {
				SigaaPapeis.SECRETARIA_POS,SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.SECRETARIA_COORDENACAO,SigaaPapeis.SECRETARIA_GRADUACAO,
				SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}, movimento);
		MovimentoSecaoExtraSite mov = (MovimentoSecaoExtraSite) movimento;
		SecaoExtraSite secao = mov.getSecaoExtraSite();
		ListaMensagens erros = secao.validate();
		
		if(!isEmpty(secao.getUnidade()) && secao.getUnidade().getId()>0)
			secao.setCurso(null);
		else
			secao.setUnidade(null);
		
		checkValidation(erros);
	}

}
