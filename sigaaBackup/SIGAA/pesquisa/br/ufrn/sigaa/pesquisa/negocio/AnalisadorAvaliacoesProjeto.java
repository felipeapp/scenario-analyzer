/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/07/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoProjetoDao;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Esta entidade tem como finalidade a Análise das avaliações de projetos
 * 
 * @author Jean Guerethes
 */
public class AnalisadorAvaliacoesProjeto {

	public static final int APROVADO = 1;
	public static final int NAO_APROVADO = 2;
	public static final int INDEFINIDO = 3;

	/**
	 * Esse método tem como finalidade sugerir um avaliação para o projeto. 
	 * 
	 * JSP: Não invocado por JSP.
	 * Invocado pela classe 
	 * 			/SIGAA/pesquisa/br/ufrn/sigaa/pesquisa/struts/AnalisarAvaliacoesAction.java
	 * @param projetos
	 * @return
	 * @throws DAOException
	 */
	public static Map< ProjetoPesquisa, Integer > sugerirAvaliacao(Collection<ProjetoPesquisa> projetos ) throws DAOException {
		Map< ProjetoPesquisa, Integer > analise = new TreeMap<ProjetoPesquisa, Integer>();

		AvaliacaoProjetoDao avaliacaoDao = new AvaliacaoProjetoDao();
		try {

			// Percorrer os projetos e verificar as analises das avaliações
			for ( ProjetoPesquisa projeto : projetos ){

				Integer resultado = INDEFINIDO;

				// Percorrer e verificar o total de avaliações validas
				int totalValidas = 0;
				boolean consultoriaEspecial = false;
				for (AvaliacaoProjeto avaliacao : avaliacaoDao.findByProjeto(projeto) ) {
					if (avaliacao.getSituacao() == AvaliacaoProjeto.REALIZADA) {
						totalValidas++;
					}
					if (!consultoriaEspecial && avaliacao.getTipoDistribuicao() == AvaliacaoProjeto.CONSULTORIA_ESPECIAL) {
						consultoriaEspecial = true;
					}
				}

				boolean valido = totalValidas >= 2 || consultoriaEspecial;

				if ( valido ) {
					if (projeto.getProjeto().getMedia() >= 5.0) {
						resultado = APROVADO;
					} else {
						resultado = NAO_APROVADO;
					}
				}

				analise.put(projeto, resultado);
			}
		} finally {
			avaliacaoDao.close();
		}

		return analise;
	}

}