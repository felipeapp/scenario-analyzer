/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 *
 */	
package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.sae.CadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.cadunico.dominio.FormularioCadastroUnicoBolsa;

/**
 * Processador respons�vel pelas opera��es de configura��o do cadastro �nico
 * 
 * @author Henrique Andre
 *
 */
public class ProcessadorCadastroUnico extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoCadastro movParam = (MovimentoCadastro) mov;
		FormularioCadastroUnicoBolsa cub = movParam.getObjMovimentado();
		
		if (movParam.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PARAMETROS_CADASTRO_UNICO)) {
			
			//Coloca o status do que estava em vigor antes para fora do Prazo.
			CadastroUnicoBolsaDao dao = getDAO(CadastroUnicoBolsaDao.class, movParam);
			FormularioCadastroUnicoBolsa cubAtivo = dao.findUnicoAtivo();
			if (!ValidatorUtil.isEmpty(cubAtivo)) {
				cubAtivo.setStatus(FormularioCadastroUnicoBolsa.FORA_DO_PRAZO);
				dao.update(cubAtivo);
			}
			
			dao.create(cub);
			
		}
		else if(movParam.getCodMovimento().equals(SigaaListaComando.ALTERAR_PARAMETROS_CADASTRO_UNICO))
			getGenericDAO(movParam).update(cub);
		
		return cub;		
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
