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
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ValidacaoProducao;

/**
 * Processador respons�vel pelo registro da auto-valida��o
 * de produ��es intelectuais
 * 
 * @author Ricardo Wendell
 *
 */
public class ProcessadorAutoValidacaoProducao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		
		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;
		GenericDAO dao = getDAO(mov);
		
		// Percorrer as produ��es e cadastrar as valida��es
		Collection<Producao> producoes = (Collection<Producao>) movCadastro.getColObjMovimentado();
		
		try {
			for (Producao producao :  producoes ) {
				
				// Criar registro da valida��o
				ValidacaoProducao validacao = new ValidacaoProducao();
				validacao.setProducao(producao);
				validacao.setValidado(true);
				validacao.setUsuario(new Usuario(mov.getUsuarioLogado().getId()));
				validacao.setDataValidacao(new Date());
				dao.createNoFlush(validacao);
				
				// Atualizar produ��o
				dao.updateFields(Producao.class, producao.getId(), 
						new String[] {"validado", "dataValidacao"}, 
						new Object[] {true, validacao.getDataValidacao()});
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
			throw new NegocioException("N�o existem produ��es a serem validadas");
		}
	}

}
