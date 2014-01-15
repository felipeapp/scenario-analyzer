/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '31/03/2008'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * Processador respons�vel pelo registro da consolida��o
 * de produ��es intelectuais
 * 
 * @author Ricardo Wendell
 *
 */
public class ProcessadorConsolidacaoProducao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		
		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);
		
		// Percorrer as produ��es e cadastrar as consolida��es
		Collection<Producao> producoes = (Collection<Producao>) movCadastro.getColObjMovimentado();
		
		try {
			for (Producao producao :  producoes ) {
				// Atualizar produ��o
				dao.updateFields(Producao.class, producao.getId(), 
						new String[] {"consolidado", "dataConsolidacao", "usuarioConsolidador"}, 
						new Object[] {true, new Date(), new Usuario(mov.getUsuarioLogado().getId())});			
			}
		} finally {
			dao.close();
		}
		
		return producoes;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;
		
		// Validar produ��es selecionadas
		if (movCadastro.getColObjMovimentado() == null || movCadastro.getColObjMovimentado().isEmpty()) {
			throw new NegocioException("N�o existem produ��es a serem consolidadas");
		}
		
		// Verificar permiss�o de consolida��o
		ServidorDao dao = getDAO(ServidorDao.class, mov);
		Usuario usr = (Usuario) mov.getUsuarioLogado();
		Collection<Designacao> designacoes = dao.findDesignacoesAtivas(usr.getServidor());
		boolean possuiPermissao = false;

		if ( mov.getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO)) {
			possuiPermissao = true;
		}

		for (Designacao d : designacoes) {
			if (AtividadeServidor.CHEFE_DEPARTAMENTO.contains(d.getAtividade().getCodigoRH()) ||
					AtividadeServidor.DIRETOR_CENTRO.contains(d.getAtividade().getCodigoRH())) {
				possuiPermissao = true;
			}
		}
		if (!possuiPermissao) {
			NegocioException e = new NegocioException();
			e.addErro("Usu�rio n�o � autorizado a fazer consolida��o das valida��es de produ��es intelectuais");
			throw e;
		}
	}

}
