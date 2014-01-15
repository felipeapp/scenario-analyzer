/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe responsável pela realização das operações auxiliares dos projetos de pesquisa. 
 *  
 * @author Gleydson
 */
public class ProjetoPesquisaHelper {

	/**
	 * Método registra no histórico a mudança.  A situação só é alterada efetivamente 
	 * quando não é um projeto associado a um  projeto-base integrado. Nesse caso, 
	 * é registrado apenas a entrada no histórico, pois as modificações das situações
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

		// registrando a nova situação no histórico
		HistoricoSituacaoProjeto historico = new HistoricoSituacaoProjeto();
		historico.setSituacaoProjeto(situacaoProjeto);
		historico.setData(new Date());
		historico.setRegistroEntrada(projeto.getProjeto().getUsuarioLogado().getRegistroEntrada());
		projeto.getProjeto().addHistoricoSituacao(historico);
	}

	/**
	 * Método para alterar a situação do projeto. Ele altera a situação do projeto e registra no histórico a mudança.
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
	 * Método para gerar o código de um projeto de pesquisa
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

		// Verificar se é interno ou externo
		if (projeto.getLinhaPesquisa().getGrupoPesquisa() == null) {
			prefixo.append("I");
		} else {
			prefixo.append("V");
		}

		// Buscar a sigla da unidade do projeto e sequência numérica
		Unidade gestora = projeto.getCentro();
		if (gestora == null) {
			throw new ArqException("O Centro ao qual o projeto deve ser associado não foi definido");
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
					"Sigla de Projetos de Pesquisa não definida para a unidade "
							+ gestora.getCodigoNome());
		}

		// Montar código
		codigo.setPrefixo(prefixo.toString());
		codigo.setNumero(numero);
		if( projeto.getEdital() != null && projeto.getEdital().getId() > 0)
			codigo.setAno(projeto.getEdital().getAno());
		else if ( CalendarUtils.getAno(projeto.getDataInicio()) != CalendarUtils.getAnoAtual() || !projeto.isInterno()) 
			codigo.setAno( CalendarUtils.getAno(projeto.getDataInicio()) );
		return codigo;
	}

}
