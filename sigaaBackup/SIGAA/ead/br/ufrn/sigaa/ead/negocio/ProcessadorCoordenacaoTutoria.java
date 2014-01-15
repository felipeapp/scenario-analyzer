/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/06/25 - 17:37:00
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.CoordenacaoTutoria;

/**
 * Processador para cadastro de coordenadores de tutoria
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCoordenacaoTutoria extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		CoordenacaoTutoria c = (CoordenacaoTutoria) cMov.getObjMovimentado();
		
		EADDao dao = getDAO(EADDao.class, mov);
		
		try {
			// Inativa o coordenador anterior
			CoordenacaoTutoria u = dao.findUltimaCoordenacao();
			if (u != null) {
				u.setAtivo(false);
				u.setDataFim(new Date());
				u.setUsuarioInativacao((Usuario) mov.getUsuarioLogado());
				dao.update(u);
			}
			
			// Cadastra o novo coordenador
			c.setUsuarioCadastro((Usuario) mov.getUsuarioLogado());
			c.setAtivo(true);
			dao.create(c);
		} finally {
			dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
