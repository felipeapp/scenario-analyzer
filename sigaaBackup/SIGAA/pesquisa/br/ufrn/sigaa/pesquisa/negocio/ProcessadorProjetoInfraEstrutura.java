/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/08/2009
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoInfraEstruturaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SubProjetoInfraEstruturaPesquisa;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Processador responsável pelo registro e submissão de projetos de Infra-Estrutura de pesquisa.
 * 
 * @author Daniel Augusto
 *
 */
public class ProcessadorProjetoInfraEstrutura extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		validate(movimento);
		ProjetoInfraEstruturaPesquisa projetoIE = movimento.getObjMovimentado();
		Projeto projeto = projetoIE.getProjeto();
		projeto.setAreaConhecimentoCnpq(null);
		projeto.setClassificacaoFinanciadora(null);
		projeto.setNumeroInstitucional(getDAO(ProjetoDao.class, mov).findNextNumeroInstitucional(projeto.getAno()));
		projeto.setRenovacao(false);
		projeto.setUnidadeOrcamentaria(projetoIE.getExecutora());
		
		GenericDAO dao = getDAO(movimento);
        dao.createOrUpdate(projeto);

		dao.createOrUpdate(projetoIE);
		
		dao.close();
		return movimento;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ProjetoInfraEstruturaPesquisa projeto = ((ProjetoInfraEstruturaPesquisa) ((MovimentoCadastro) mov)
				.getObjMovimentado());
		ListaMensagens lista = projeto.validate();
		for (SubProjetoInfraEstruturaPesquisa subprojeto : projeto.getSubProjetos())
			lista.addAll(subprojeto.validate());
		checkValidation(lista);
	}

}
