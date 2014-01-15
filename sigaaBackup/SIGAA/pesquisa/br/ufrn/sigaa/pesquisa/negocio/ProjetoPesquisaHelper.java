/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
/**
 * Classe respons�vel pela realiza��o das opera��es auxiliares dos projetos de pesquisa. 
 *  
 * @author Gleydson
 */
public class ProjetoPesquisaHelper {

	/**
	 * M�todo registra no hist�rico a mudan�a.  A situa��o s� � alterada efetivamente 
	 * quando n�o � um projeto associado a um  projeto-base integrado. Nesse caso, 
	 * � registrado apenas a entrada no hist�rico, pois as modifica��es das situa��es
	 * devem ser feitas apenas no projeto-base.
	 *
	 * @param dao
	 * @param situacao
	 * @param projeto
	 * @throws DAOException
	 */
	public static void alterarSituacaoProjeto(GenericDAO dao, int situacao,
			ProjetoPesquisa projeto) throws DAOException {

		TipoSituacaoProjeto situacaoProjeto = dao.findByPrimaryKey(situacao, TipoSituacaoProjeto.class);
		if(!projeto.isProjetoAssociado())
			projeto.setSituacaoProjeto(situacaoProjeto);

		// registrando a nova situa��o no hist�rico
		HistoricoSituacaoProjeto historico = new HistoricoSituacaoProjeto();
		historico.setSituacaoProjeto(situacaoProjeto);
		historico.setData(new Date());
		historico.setRegistroEntrada(projeto.getProjeto().getUsuarioLogado().getRegistroEntrada());
		projeto.getProjeto().addHistoricoSituacao(historico);
	}

	/**
	 * M�todo para alterar a situa��o do projeto. Ele altera a situa��o do projeto e registra no hist�rico a mudan�a.
	 * 
	 * @param dao
	 * @param situacao
	 * @param projeto
	 * @throws DAOException
	 */
	public static void gravarAlterarSituacaoProjeto(GenericDAO dao, int situacao, ProjetoPesquisa projeto) throws DAOException {
		alterarSituacaoProjeto(dao, situacao, projeto);
		dao.updateField(Projeto.class, projeto.getProjeto().getId(), "situacaoProjeto", situacao);
	}
	
	/**
	 * M�todo para gerar o c�digo de um projeto de pesquisa
	 *
	 * @param projeto
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static CodigoProjetoPesquisa gerarCodigoProjeto(
			ProjetoPesquisa projeto) throws DAOException, ArqException,
			NegocioException {
		CodigoProjetoPesquisa codigo = new CodigoProjetoPesquisa();
		StringBuilder prefixo = new StringBuilder("P");
		int numero = 0;

		// Verificar se � interno ou externo
		if (projeto.getLinhaPesquisa().getGrupoPesquisa() == null) {
			prefixo.append("I");
		} else {
			prefixo.append("V");
		}

		// Buscar a sigla da unidade do projeto e sequ�ncia num�rica
		Unidade gestora = projeto.getCentro();
		if (gestora == null) {
			throw new ArqException("O Centro ao qual o projeto deve ser associado n�o foi definido");
		}

		SiglaUnidadePesquisa siglaUnidade = null;
		ProjetoPesquisaDao projetoDao = DAOFactory.getInstance().getDAO(ProjetoPesquisaDao.class, null, null);

		try {
			Collection<SiglaUnidadePesquisa> siglas = projetoDao.findByExactField( SiglaUnidadePesquisa.class, "unidade.id", gestora.getId());
			if (siglas != null && !siglas.isEmpty()) {
				siglaUnidade = siglas.iterator().next();
			}
			numero = projetoDao.findNextNumero();
		} catch (DAOException e) {
			throw new ArqException(e);
		} finally {
			projetoDao.close();
		}

		// Concatenar a sigla da unidade
		if (siglaUnidade != null) {
			prefixo.append(siglaUnidade.getSigla());
		} else {
			throw new ArqException(
					"Sigla de Projetos de Pesquisa n�o definida para a unidade "
							+ gestora.getCodigoNome());
		}

		// Montar c�digo
		codigo.setPrefixo(prefixo.toString());
		codigo.setNumero(numero);
		if( projeto.getEdital() != null && projeto.getEdital().getId() > 0)
			codigo.setAno(projeto.getEdital().getAno());
		else if ( CalendarUtils.getAno(projeto.getDataInicio()) != CalendarUtils.getAnoAtual() || !projeto.isInterno()) 
			codigo.setAno( CalendarUtils.getAno(projeto.getDataInicio()) );
		return codigo;
	}

}
