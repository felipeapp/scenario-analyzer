/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.pesquisa.InvencaoDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CodigoNotificacaoInvencao;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoNotificacaoInvencao;
import br.ufrn.sigaa.pesquisa.dominio.Invencao;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;

/**
 * 
 * Classe com m�todos auxiliares para as notifica��es de inven��o
 * 
 * @author Leonardo Campos
 *
 */
public class InvencaoHelper {

	/**
	 * M�todo para gerar uma entrada no hist�rico de altera��es da notifica��o de inven��o
	 * 
	 * @param mov
	 * @param situacao
	 * @param invencao
	 * @throws DAOException
	 */
	public static void gerarEntradaHistorico(Movimento mov, int situacao, Invencao invencao) throws DAOException{
		HistoricoNotificacaoInvencao historico = new HistoricoNotificacaoInvencao();
		historico.setData(new Date());
		historico.setInvencao(invencao);
		historico.setStatus(situacao);
		historico.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
		getDAO(GenericDAOImpl.class, mov).create(historico);
	}
	
	public static void alterarSituacaoNotificacao(Movimento mov, int situacao, Invencao invencao) throws DAOException{
		invencao.setStatus(situacao);
		HistoricoNotificacaoInvencao historico = new HistoricoNotificacaoInvencao();
		historico.setData(new Date());
		historico.setInvencao(invencao);
		historico.setStatus(situacao);
		historico.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
		invencao.getHistorico().add(historico);
	}
	
	/**
	 * M�todo para gerar o c�digo de uma notifica��o de inven��o
	 *
	 * @param projeto
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static CodigoNotificacaoInvencao gerarCodigoNotificacao(Invencao invencao) throws DAOException, ArqException, NegocioException {
		CodigoNotificacaoInvencao codigo = new CodigoNotificacaoInvencao();
		StringBuilder prefixo = new StringBuilder("NI");
		int numero = 0;

		// Buscar a sigla da unidade do projeto e sequ�ncia num�rica
		Unidade gestora = invencao.getCentro();
		if (gestora == null) {
			throw new ArqException("O Centro ao qual a inven��o deve ser associada n�o foi definido.");
		}

		SiglaUnidadePesquisa siglaUnidade = null;
		InvencaoDao invencaoDao = DAOFactory.getInstance().getDAO(InvencaoDao.class, null, null);

		try {
			Collection<SiglaUnidadePesquisa> siglas = invencaoDao.findByExactField( SiglaUnidadePesquisa.class, "unidade.id", gestora.getId());
			if (siglas != null && !siglas.isEmpty()) {
				siglaUnidade = siglas.iterator().next();
			}
			numero = invencaoDao.findNextNumero();
		} catch (DAOException e) {
			throw new ArqException(e);
		} finally {
			invencaoDao.close();
		}

		// Concatenar a sigla da unidade
		if (siglaUnidade != null) {
			prefixo.append(siglaUnidade.getSigla());
		} else {
			throw new ArqException("Sigla de Projetos de Pesquisa n�o definida para a unidade "	+ gestora.getCodigoNome());
		}

		// Montar c�digo
		codigo.setPrefixo(prefixo.toString());
		codigo.setNumero(numero);
		codigo.setAno(CalendarUtils.getAnoAtual());

		return codigo;
	}
	
	@SuppressWarnings("unused")
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return getDAO(dao, null);
	}
	
	private static <T extends GenericDAO> T getDAO(Class<T> dao, Movimento mov) throws DAOException {
		UsuarioGeral usuario = null;
		if (mov != null) {
			usuario = mov.getUsuarioLogado();
		}
		return DAOFactory.getInstance().getDAO(dao, usuario, null);
	}
}
