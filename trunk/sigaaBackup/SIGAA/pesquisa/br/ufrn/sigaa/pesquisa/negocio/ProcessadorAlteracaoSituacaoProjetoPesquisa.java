/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador responsável por persistir alterações na situação e/ou tipo do projeto de pesquisa
 *
 * @author Leonardo
 * @author Ricardo Wendell
 *
 */
public class ProcessadorAlteracaoSituacaoProjetoPesquisa extends AbstractProcessador {


	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {


		validate(mov);

		ProjetoPesquisa projetoAlterado = ((MovimentoProjetoPesquisa) mov).getProjeto();
		GenericDAO dao = getGenericDAO(mov);
		try {
			// Buscar projeto persistido
			ProjetoPesquisa projetoPesquisa = dao.findByPrimaryKey(projetoAlterado.getId(), ProjetoPesquisa.class);
	
			// Alterar situação do projeto, se necessário
			if ( projetoPesquisa.getSituacaoProjeto().getId() != projetoAlterado.getSituacaoProjeto().getId() ) {
				projetoPesquisa.getProjeto().setUsuarioLogado( mov.getUsuarioLogado() );
				ProjetoPesquisaHelper.alterarSituacaoProjeto(dao, projetoAlterado.getSituacaoProjeto().getId() , projetoPesquisa);
			}
	
			// Alterar tipo do projeto, se necessário
			if (projetoPesquisa.isInterno() != projetoAlterado.isInterno()) {
	
				projetoPesquisa.setInterno( projetoAlterado.isInterno() );
	
				// Tratar projetos internos
				if ( projetoAlterado.isInterno() ) {
					EditalPesquisa edital = dao.findByPrimaryKey(projetoAlterado.getEdital().getId(), EditalPesquisa.class);
					projetoPesquisa.setEdital( edital );
					projetoPesquisa.setDataInicio( edital.getCota().getInicio() );
					projetoPesquisa.setDataFim( edital.getCota().getFim() );
				}
				// Tratar projetos externos
				else {
					projetoPesquisa.setEdital(null);
					projetoPesquisa.setDataInicio( projetoAlterado.getDataInicio() );
					projetoPesquisa.setDataFim( projetoAlterado.getDataFim() );
				}
			}
	
			dao.update( projetoPesquisa );
			return projetoPesquisa;
			
		}finally {
			dao.close();
		}
	}


	public void validate(Movimento mov) throws NegocioException, ArqException {
		ProjetoPesquisa projetoAlterado = ((MovimentoProjetoPesquisa) mov).getProjeto();
		GenericDAO dao = getGenericDAO(mov);
		try {
			ProjetoPesquisa projeto = dao.findByPrimaryKey(projetoAlterado.getId(), ProjetoPesquisa.class);
			if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SITUACAO_PROJETO_PESQUISA))
				checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);
			else if(mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_PROJETO_PESQUISA) && !projeto.isProjetoAssociado()){
				if(!projeto.isPassivelFinalizacaoCoordenador())
					throw new NegocioException("Apenas projetos EXTERNOS e EM ANDAMENTO podem ser finalizados pelo coordenador.");
				Usuario user = (Usuario) mov.getUsuarioLogado();
				if(user.getPessoa().getId() != projeto.getCoordenador().getPessoa().getId()){
					throw new NegocioException("Apenas o coordenador pode finalizar o projeto.");
				}
				if(projetoAlterado.getSituacaoProjeto().getId() != TipoSituacaoProjeto.FINALIZADO)
					throw new NegocioException("A nova situação do projeto deve ser FINALIZADO");
			}
		}finally {
			dao.close();
		}
	}

}
