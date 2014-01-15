/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '23/06/2009'
 *
 */
package br.ufrn.sigaa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;

/**
 * Processador responsável pelo cadastro/alteração de formas de ingresso.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ProcessadorFormaIngresso extends AbstractProcessador {

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		FormaIngresso formaIngresso = ((MovimentoCadastro) mov).getObjMovimentado();
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class, mov);
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_FORMA_INGRESSO))
			validate(mov);
		dao.createOrUpdate(formaIngresso);
		return formaIngresso;
	}

	/**
	 * Valida se existe forma de ingresso cadastrada com a mesma descrição.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		FormaIngresso formaIngresso = ((MovimentoCadastro) mov).getObjMovimentado();
		ListaMensagens lista = formaIngresso.validate();
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class, mov);
		if (lista != null && lista.size() != 0) {
			throw new NegocioException(lista);
		}
		if (dao.hasFormaIngresso(formaIngresso))
			throw new NegocioException(
					"Já existe uma forma de ingresso com essa descrição. Por favor, informe uma descrição diferente para evitar problemas.");

	}

}
