/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 11/10/2012 
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.negocio.EstrategiaAvaliacaoProjetos;

/**
 * @author Leonardo
 *
 */
public class AvaliacaoProjetoApoio implements EstrategiaAvaliacaoProjetos {

	@Override
	public void classificar(List<Projeto> projetos) throws NegocioException {
	}


	@Override
	public void avaliar(Avaliacao avaliacao) throws NegocioException {
		Projeto pj = avaliacao.getProjeto();
		try {
			DAOFactory.getGeneric(Sistema.SIGAA).updateField(Projeto.class, pj.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.PROJETO_BASE_AVALIADO);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
