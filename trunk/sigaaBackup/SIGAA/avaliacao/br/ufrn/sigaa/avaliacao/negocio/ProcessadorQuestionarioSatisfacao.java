/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 09/06/2008 
 */
package br.ufrn.sigaa.avaliacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.avaliacao.dominio.QuestionarioSatisfacao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processador para cadastro do questionário de satisfação
 * 
 * @author David Pereira
 *
 */
public class ProcessadorQuestionarioSatisfacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		QuestionarioSatisfacao obj = cMov.getObjMovimentado();
		
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		if (usuario.getVinculoAtivo().isVinculoDiscente())
			obj.setDiscente(usuario.getDiscenteAtivo().getDiscente());
		else
			obj.setDocente(usuario.getServidorAtivo());
		
		getGenericDAO(mov).create(obj);
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		GenericDAO dao = getGenericDAO(mov);
		Collection<QuestionarioSatisfacao> questionarios = null;
		
		if (usuario.getVinculoAtivo().isVinculoDiscente()) {
			questionarios = dao.findByExactField(QuestionarioSatisfacao.class, "discente.id", usuario.getDiscenteAtivo().getId());
		} else if (usuario.getVinculoAtivo().isVinculoDocenteExterno() ){
			questionarios = dao.findByExactField(QuestionarioSatisfacao.class, "docenteExterno.id", usuario.getVinculoAtivo().getDocenteExterno().getId());
		} else {
			questionarios = dao.findByExactField(QuestionarioSatisfacao.class, "docente.id", usuario.getServidorAtivo().getId());
		}
		
		if (!isEmpty(questionarios))
			throw new NegocioException("O preenchimento do questionário de satisfação já foi realizado anteriormente. Não é possível preenchê-lo novamente.");
		
	}

}
