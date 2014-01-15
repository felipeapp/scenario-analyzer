/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/10/2010
 *
 */
package br.ufrn.sigaa.diploma.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.diploma.dao.ResponsavelAssinaturaDiplomasDao;
import br.ufrn.sigaa.diploma.dominio.ResponsavelAssinaturaDiplomas;

/**
 * Processador responsável pelo cadastro de
 * {@link ResponsavelAssinaturaDiplomas dados dos responsáveis pelas assinaturas
 * nos diplomas}.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ProcessadorAssinaturaDiploma extends AbstractProcessador {

	/** Persiste os dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		ResponsavelAssinaturaDiplomas rad = ((MovimentoCadastro) mov).getObjMovimentado();
		ResponsavelAssinaturaDiplomasDao dao = getDAO(ResponsavelAssinaturaDiplomasDao.class, mov);
		if (rad.isAtivo())
			dao.inabilitaTodos(rad.getNivel());
		rad.setRegistroEntradaCadastro(mov.getUsuarioLogado().getRegistroEntrada());
		dao.createOrUpdate(rad);
		dao.close();
		return rad;
	}

	/** Valida os dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ResponsavelAssinaturaDiplomas rad = ((MovimentoCadastro) mov).getObjMovimentado();
		if (!rad.isAtivo()) {
			ResponsavelAssinaturaDiplomasDao dao = getDAO(ResponsavelAssinaturaDiplomasDao.class, mov);
			ResponsavelAssinaturaDiplomas ativo = dao.findAtivo(rad.getNivel());
			if (ativo != null && rad.getId() == ativo.getId()) {
				throw new NegocioException("Não é possível tornar o conjunto de nomes utilizado atualmente inativo. Defina outro como ativo primeiro.");
			}
		}
	}

}
