/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 27/04/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Processador responsável gerenciar o perfil inicial do discente de graduação.
 * @author Rafael Gomes
 *
 */
public class ProcessadorPerfilInicial extends ProcessadorCadastro{
	@Override
	public Object execute(Movimento movimento) throws NegocioException,	ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		DiscenteGraduacao discente = (DiscenteGraduacao) mov.getObjMovimentado();
		
		getGenericDAO(movimento).updateField(DiscenteGraduacao.class, discente.getId(), "perfilInicialAlterado", discente.getPerfilInicialAlterado());
		
		return movimento;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		super.validate(mov);
	}
}
