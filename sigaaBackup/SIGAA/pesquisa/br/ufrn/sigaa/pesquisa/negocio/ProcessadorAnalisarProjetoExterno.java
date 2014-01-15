/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Entidade responsável pelo processamento da análise do projeto externo
 * 
 * @author Victor Hugo
 */
public class ProcessadorAnalisarProjetoExterno extends AbstractProcessador {

	/** 
	 * Responsável 
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);

		MovimentoProjetoPesquisa movProjeto = (MovimentoProjetoPesquisa) mov;
		ProjetoPesquisa projetoPesquisa = movProjeto.getProjeto();
		GenericDAO dao = getDAO(mov);

		validate(mov);

		ProjetoPesquisa projetoBanco;

		try {
			projetoBanco = dao.findByPrimaryKey( projetoPesquisa.getId(), ProjetoPesquisa.class );
			projetoBanco.getProjeto().setUsuarioLogado( mov.getUsuarioLogado() );

			if( projetoPesquisa.getAprovado() )
				ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,TipoSituacaoProjeto.APROVADO, projetoBanco);
			else
				ProjetoPesquisaHelper.alterarSituacaoProjeto(dao,TipoSituacaoProjeto.REPROVADO, projetoBanco);

			dao.update(projetoBanco);
		} finally {
			dao.close();
		}

		return null;

	}

	/** 
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);

		ProjetoPesquisa projetoPesquisa = ((MovimentoProjetoPesquisa) mov).getProjeto();

		ListaMensagens erros = new ListaMensagens();

		AnalisarProjetoExternoValidator.validaProjetoExterno( projetoPesquisa, erros );

	}

}
