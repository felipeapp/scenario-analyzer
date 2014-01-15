/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 09/11/2004
 */
package br.ufrn.arq.negocio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe pai de todos os processadores do Sistema. Implementa funções de
 * interesse de todos os processadores.
 *
 * @author Gleydson Lima
 *
 */
public abstract class AbstractProcessador implements ProcessadorComando {

	/**
	 * Verifica se o usuário logado possui o papel informado
	 *
	 * @param papel
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRole(int papel, Movimento mov) throws SegurancaException, ArqException {
		UFRNUtils.checkRole(mov.getUsuarioLogado(), papel);
	}

	/**
	 * Checa se o usuï¿½rio estï¿½ dentro de um dos papï¿½is permitidos
	 *
	 * @param papeis
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRole(int[] papeis, Movimento mov) throws SegurancaException, ArqException {
		UFRNUtils.checkRole(mov.getUsuarioLogado(), papeis);
	}

	
	
	/**
	 * Verifica se o usuário logado tem os papéis passados como parâmetro
	 * e se esses papéis são válidos para a unidade do parâmetro.
	 * @throws SegurancaException 
	 */
	public void checkRole(UnidadeGeral unidade, Movimento mov, int... papeis) throws SegurancaException {
		UFRNUtils.checkRole(mov.getUsuarioLogado(), unidade, papeis);
	}
	
	
	
	/**
	 *Método para verificar se um usuário possui o papel passados como parâmetro.
	 * Se o usuário tiver o papel, verifica ainda se o papel é válido para a unidade.
	 * Se não for, dispara uma exceção do tipo SegurancaException.
	 * @param papel
	 * @param unidade
	 * @param mov
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkHierarchyRole(int papel, UnidadeGeral unidade, Movimento mov) throws SegurancaException, ArqException {
		checkHierarchyRole(new int[]{papel}, unidade, mov);
	}

	/**
	 * Método para verificar se um usuário possui algum dos papéis passados como parâmetro.
	 * Se o usuário tiver o papel, verifica ainda se o papel é válido para a unidade do servidor.
	 * Se não for, dispara uma exceção do tipo SegurancaException.
	 * Caso usuário tenha algum dos papéis especificados e o mesmo não exija unidade, é permitido o acesso.
	 * @param papeis
	 * @param unidade
	 * @param mov
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkHierarchyRole(int[] papeis, UnidadeGeral unidade, Movimento mov) throws SegurancaException, ArqException {

		GenericDAO dao = getGenericDAO(mov);
		dao.initialize(unidade);

		UFRNUtils.checkHierarchyRole(mov.getUsuarioLogado(), unidade, papeis);

	}

	/**
	 * Verifica se o usuário logado possui algum papel no sipac.
	 *
	 * @param mov
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRoleSipac(Movimento mov) throws SegurancaException, ArqException {
		UsuarioGeral user = mov.getUsuarioLogado();
		if (!user.isUserInSIPAC()) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
		}
	}

	/**
	 * Verifica se o usuário logado possui algum papel no sigaa.
	 *
	 * @param mov
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRoleSigaa(Movimento mov) throws SegurancaException, ArqException {
		UsuarioGeral user = mov.getUsuarioLogado();
		if (!user.isUserInSIGAA()) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
		}
	}

	/**
	 * Retorna o ano atual
	 * @deprecated Utilizar CalendarUtils.getAnoAtual()
	 * @return
	 */
	@Deprecated
	public int getAnoAtual() {

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR);

	}

	/**
	 * Retorna o mês atual. Jan = 1, Fev = 2, Mar = 3, ... , Dec = 12
	 * JÁ SOMA 1 AO RETORNO
	 * @deprecated Utilizar CalendarUtils.getMesAtual1a12()
	 * @return
	 */
	@Deprecated
	public int getMesAtual() {

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.MONTH) + 1;

	}

	/**
	 * @deprecated Usar CollectionUtils.toSet()
	 * @param <T>
	 * @param col
	 * @return
	 */
	@Deprecated
	public <T> Set<T> toSet(Collection<T> col) {
		HashSet<T> set = new HashSet<T>();
		set.addAll(col);
		return set;
	}

	/**
	 * @deprecated Usar CollectionUtils.toList()
	 * @param <T>
	 * @param col
	 * @return
	 */
	@Deprecated
	public <T> ArrayList<T> toList(Collection<T> col) {
		ArrayList<T> list = new ArrayList<T>();
		list.addAll(col);
		return list;
	}

	/**
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	public int getUnidadeGestora(Movimento mov) throws ArqException {

		UnidadeGeral unidade = mov.getUsuarioLogado().getUnidade();

		int idUnidadeGestora = unidade.getId();
		if (unidade.getTipo() == UnidadeGeral.UNIDADE_FATO) {
			idUnidadeGestora = unidade.getRaiz();
		}
		return idUnidadeGestora;
	}

	/**
	 * 
	 * @param <T>
	 * @param dao
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public static <T extends GenericDAO> T getDAO(Class<T> dao, Movimento mov) throws DAOException {
		return DAOFactory.getInstance().getDAOMov(dao, mov);
	}
	/**
	 * Retorna um objeto DAO, passados classe, Movimento e sistema
	 * @param <T>
	 * @param dao
	 * @param mov
	 * @param sistema
	 * @return
	 * @throws DAOException
	 */
	public static  <T extends GenericDAO> T getDAO(Class<T> dao, Movimento mov, int sistema) throws DAOException {
		return DAOFactory.getInstance().getDAOMov(dao, sistema, mov);
	}

	/**
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public GenericDAO getGenericDAO(Movimento mov) throws DAOException {
		return DAOFactory.getInstance().getDAOMov(GenericDAOImpl.class, mov);
	}

	/**
	 * @deprecated Usar getGenericDao()
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	@Deprecated
	public GenericDAO getDAO(Movimento mov) throws DAOException {
		return getGenericDAO(mov);
	}

	/**
	 * Verifica se houve erros de validação e dispara um negócio exception
	 *
	 * @param mensagens
	 * @deprecated Utilizar checkValidation(ListaMensagens)
	 * @throws NegocioException
	 */
	@Deprecated
	public void checkValidation(Collection<MensagemAviso> mensagens) throws NegocioException {
		checkValidation(new ListaMensagens(mensagens));
	}

	/**
	 * Verifica se houve erros de validação e dispara um negócio exception
	 *
	 * @param mensagens
	 */
	public void checkValidation(ListaMensagens mensagens) throws NegocioException {
		if (mensagens != null && mensagens.isErrorPresent()) {
			NegocioException e = new NegocioException();
			e.addMensagens(mensagens.getErrorMessages());
			throw e;
		}
	}

	/**
	 * 
	 * @param <T>
	 * @param beanName
	 * @param mov
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean(String beanName, Movimento mov) {
		ApplicationContext ac = mov.getApplicationContext();
		if (ac != null) {
			return (T) ac.getBean(beanName);
		} else {
			throw new ApplicationContextException("Erro ao pegar o Application Context em um Movimento.");
		}
	}
	
	/**
	 * Identifica se um subsistema passado como parâmetro está ativo ou não.
	 * @param subsistema
	 * @return
	 * @throws DAOException
	 */
	public boolean isSubSistemaAtivo(int subsistema) throws DAOException {
		GenericDAO dao = new GenericSharedDBDao(Sistema.COMUM);
		try {
			SubSistema ss = dao.findByPrimaryKey(subsistema, SubSistema.class);
			return ss.isAtivo();
		} finally {
			dao.close();
		}
	}

	
}