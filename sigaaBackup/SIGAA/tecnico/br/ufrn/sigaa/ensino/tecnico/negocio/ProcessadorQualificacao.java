/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloQualificacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.QualificacaoTecnico;

/**
 * Processador responsável por alterar a Qualificação Técnica.
 * @author Andre M Dantas
 *
 */
public class ProcessadorQualificacao extends ProcessadorCadastro {


	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);

		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_QUALIFICACAO))
			alterar(mov);
		return null;
	}

	@Override
	protected Object alterar(MovimentoCadastro mov) throws DAOException, NegocioException {
		GenericDAO dao = getGenericDAO(mov);
		QualificacaoTecnico qualifNovo = (QualificacaoTecnico) mov.getObjMovimentado();
		QualificacaoTecnico qualifBD = dao.findByPrimaryKey(qualifNovo.getId(), QualificacaoTecnico.class);
		Collection<ModuloQualificacao> seraoRemovidos = new ArrayList<ModuloQualificacao>();

        if (!qualifNovo.getModulos().containsAll(qualifBD.getModulos())) {
        	// módulos removidos
        	for (ModuloQualificacao qMod : qualifBD.getModuloQualificacoes()) {
        		if (!qualifNovo.getModuloQualificacoes().contains(qMod)) {
        			seraoRemovidos.add(qMod);
        		}
        	}

        }
        dao.detach(qualifBD);
        dao.update(qualifNovo);

        for (ModuloQualificacao rems : seraoRemovidos) {
			dao.remove(rems);
		}

		dao.close();
		return mov.getObjMovimentado();
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
