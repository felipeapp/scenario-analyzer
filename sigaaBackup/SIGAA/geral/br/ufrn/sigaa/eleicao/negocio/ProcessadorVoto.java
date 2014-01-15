/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Apr 4, 2007
 *
 */
package br.ufrn.sigaa.eleicao.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.eleicao.dominio.Voto;

/**
 * 
 * @author Victor Hugo
 *
 */
public class ProcessadorVoto extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		Voto voto = (Voto) mov;
		
		validate( voto );
		
		//setando atributos default
		voto.setDataCadastro(new Date());
		voto.setRegistroEntrada( voto.getUsuarioLogado().getRegistroEntrada() );
		
		GenericDAO dao = getGenericDAO(mov);
		dao.create(voto);
		
		return null;
	}

	/**
	 * valida vota��o
	 * - um discente so pode votar uma vez por elei��o
	 */
	public void validate(Movimento vMov) throws NegocioException, ArqException {

		ListaMensagens mensagens = new ListaMensagens();
		
		Voto voto = (Voto) vMov;
		
		VotoValidator.validarVotacao(voto.getEleicao(), voto.getDiscente(), mensagens);
		
		if (!mensagens.isEmpty()) {
			NegocioException e = new NegocioException();
			e.setListaMensagens( mensagens );
			throw e;
		}

	}

}
