/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '02/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.prodocente.AtividadesProdocenteDao;
import br.ufrn.sigaa.ensino.graduacao.jsf.RegistroAtividadeMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;

/**
 * Esta managed-bean é utilizado como base para as atividades que um docente pode realizar, tais como:
 * Afastamento, Avaliação Organizacional, informação sobre a Carga horária dedicada na residência Médica, 
 * Estágio, Formação Acadêmica, Financiamento de Visita Científica, dentre outros.  
 * 
 * @author Gleydson
 *
 * @param <T>
 */
public abstract class AbstractControllerAtividades<T> extends
		AbstractControllerProdocente<T> {

	/**
	 * Atributo que define a lista de atividades.
	 */
	protected Collection<T> atividades = new ArrayList<T>();

	/**
	 * Atributo que define o id do servidor selecionado na consulta
	 */
	protected int idServidor = -1;

	/**
	 * Atributo que define o id da unidade selecionada na consulta
	 */
	private int idUnidade = -1;

	/**
	 * Atributo que define id do tipo de orientação selecionado na consulta
	 */
	private int idTipoOrientacao = -1;
	
	/**
	 * Atributo que define se o filtro setado na consuta é servidor
	 */
	protected boolean buscaServidor;

	/**
	 * Atributo que define se o filtro setado na consuta é unidade
	 */
	private boolean buscaUnidade;

	/**
	 * Atributo que define se o filtro setado na consuta é tipo de orientação
	 */
	private boolean buscaTipoOrientacao;

	/**
	 * Atributo que define se a consulta deve ser orndenada.
	 */
	protected boolean order;

	/**
	 * Atributo que define se a consulta é proveniente de uma operação de cadastro de graduação
	 * TODO Analisar a possibilidade de alteração do nome do atributo pois também é tuilizado em outros níveis.
	 */
	protected boolean preCadastroParaGraduacao = false;
	
	/**
	 * Atributo que define se a consulta é proveniente de uma operação de atualização de graduação
	 */
	protected boolean atualizacaoParaGraduacao = false;

	/**
	 * A partir de um evento pode-se fazer uma busca pelo servidor ou pela
	 * unidade, ou pelos dois.
	 *
	 * @param e
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public void buscar(ActionEvent e) throws DAOException {
		atividades = new ArrayList<T>();
		if (!buscaServidor)
			idServidor = -1;
		if (!buscaUnidade)
			idUnidade = -1;
		if (!buscaTipoOrientacao)
			idTipoOrientacao = -1;

		AtividadesProdocenteDao dao = getDAO(AtividadesProdocenteDao.class);

		atividades = dao.findByServidorDepartamentoOrientacao(obj.getClass(), idServidor, idUnidade, idTipoOrientacao, null);
	}

	/**
	 * Busca Teses Filtrando os Tipos de Orientação de acordo com o filtro {Stricto, Lato, Residencia}
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@SuppressWarnings("unchecked")
	public void buscarTese(List<Integer> FiltroTipoOrientacao) throws DAOException
	{
		atividades = new ArrayList<T>();
		if (buscaServidor == false && idServidor <=0)
			idServidor = -1;
		if (!buscaUnidade)
			idUnidade = -1;
		if (!buscaTipoOrientacao)
			idTipoOrientacao = -1;

		AtividadesProdocenteDao dao = getDAO(AtividadesProdocenteDao.class);
		atividades = dao.findByServidorDepartamentoOrientacao(obj.getClass(), idServidor, idUnidade, idTipoOrientacao, FiltroTipoOrientacao);
	}

	/**
	 * Responsável por retornar todas as atividades do usuário logado.  
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> getAllAtividades() throws DAOException {

		if (atividades == null || atividades.isEmpty()){
			AtividadesProdocenteDao dao = getDAO(AtividadesProdocenteDao.class);
			if (getSubSistema().equals(SigaaSubsistemas.LATO_SENSU) && (isUserInRole( new int[] { SigaaPapeis.GESTOR_LATO })))
				atividades = dao.findByServidorDep(obj.getClass(), -1, -1, order);
			else atividades = dao.findByServidorDep(obj.getClass(), getUsuarioLogado().getServidor().getId(), -1, order);
		}
		return atividades;
	}

	/**
	 * Método que retorna todas atividades.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getAll() {

		setTamanhoPagina(20);
		GenericDAO genDAO = getGenericDAO();
		try {

			atividades = (Collection<T>) genDAO.findAll(obj.getClass(),getPaginacao());
			return atividades;
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		return atividades;
	}

	/**
	 * Action invocada pelo menu para listar todas as Atividades do docente.
	 *
	 * @return
	 */
	@Override
	public String listar() throws ArqException{
		checkListRole();
		resetBean("paginacao");
		setDirBase("/prodocente/atividades/");
		return forward(getListPage());

	}

	/**
	 * Action invocada na listagem das atividades, direcionando o usuário para a tela de cadastro de uma nova 
	 * atividade. 
	 * 
	 * @return
	 */
	@Override
	public String preCadastrar() {

		if (verificaBloqueio()
				&& !getUsuarioLogado().isUserInRole(SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG)) {
			return redirectBloqueio();
		} else {

			setDirBase("/prodocente/atividades/");

			try {
				carregaSubAreas();
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
			}
			return forward(getFormPage());
		}

	}

	/**
	 * Método que retorna as atividades. 
	 * @return the atividades
	 */
	public Collection<T> getAtividades() {
		return atividades;
	}

	/**
	 * Método que popula as atividades
	 * @param atividades
	 *            the atividades to set
	 */
	public void setAtividades(Collection<T> atividades) {
		this.atividades = atividades;
	}

	/**
	 * Método que retorna o id do servidor.
	 * @return the idServidor
	 */
	public int getIdServidor() {
		return idServidor;
	}

	/**
	 * Método que popula o id do servidor.
	 * @param idServidor
	 *            the idServidor to set
	 */
	public void setIdServidor(int idServidor) {
		this.idServidor = idServidor;
	}

	/**
	 * Método que retorna o id da unidade.
	 * @return the idUnidade
	 */
	public int getIdUnidade() {
		return idUnidade;
	}

	/**
	 * Método que popula o id da unidade.
	 * @param idUnidade
	 *            the idUnidade to set
	 */
	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	/**
	 * Método que verifica se o filtro da busca é servidor
	 * @return the buscaServidor
	 */
	public boolean isBuscaServidor() {
		return buscaServidor;
	}

	/**
	 * Método que popula se o filtro da busca é servidor
	 * @param buscaServidor
	 *            the buscaServidor to set
	 */
	public void setBuscaServidor(boolean buscaServidor) {
		this.buscaServidor = buscaServidor;
	}

	/**
	 * 	Método que verifica se o filtro da busca é unidade
	 * @return the buscaUnidade
	 */
	public boolean isBuscaUnidade() {
		return buscaUnidade;
	}

	/**
	 * Método que popula se o filtro da busca é unidade
	 * @param buscaUnidade
	 *            the buscaUnidade to set
	 */
	public void setBuscaUnidade(boolean buscaUnidade) {
		this.buscaUnidade = buscaUnidade;
	}

	/**
	 * 	Método que verifica se o filtro da busca é tipo de orientação
	 * @return
	 */
	public boolean getBuscaTipoOrientacao()
	{
		return this.buscaTipoOrientacao;
	}
	
	/**
	 * Método que popula se o filtro da busca é tipo de orientação
	 */
	public void setBuscaTipoOrientacao(boolean buscaTipoOrientacao)
	{
		this.buscaTipoOrientacao = buscaTipoOrientacao;
	}
	
	/**
	 * Método que verifica se o filtro da busca é tipo de orientação
	 * @return
	 */
	public boolean isBuscaTipoOrientacao()
	{
		return this.buscaTipoOrientacao;
	}
	
	/**
	 * Método que retorna o id do tipo de orientação
	 * @return
	 */
	public int getIdTipoOrientacao()
	{
		return this.idTipoOrientacao;
	}
	
	/**
	 * Método que popula o id do tipo de orientação.
	 * @param idTipoOrientacao
	 */
	public void setIdTipoOrientacao(int idTipoOrientacao)
	{
		this.idTipoOrientacao = idTipoOrientacao;
	}

	/**
	 * Método que verifica se a busca deve ser ordenada
	 * @return the order
	 */
	public boolean isOrder() {
		return order;
	}

	/**
	 * Método que popula a busca como ordenada.
	 * @param order
	 *            the order to set
	 */
	public void setOrder(boolean order) {
		this.order = order;
	}

	@Override
	public String cancelar() {

		if (getUsuarioLogado().isUserInRole(SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG,  SigaaPapeis.GESTOR_LATO)) {
			return	redirectJSF(getSubSistema().getLink());
		} else {
			return super.cancelar();
		}
	}

	/**
	 * Retorna os dados das atividades de graduação.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	
	@SuppressWarnings("unchecked")
	public String voltarDadosGraduacao() {
		RegistroAtividadeMBean raMbean = (RegistroAtividadeMBean) getMBean("registroAtividade");
		((AbstractControllerAtividades)raMbean.getAtividadesMBean()).setObj(obj);
		return raMbean.telaDadosRegistro();
	}

	/**
	 * Serve com um pre Cadastro para as atividades de um docente para o nível de Graduação.
	 * 
	 * @return
	 */
	public String preCadastrarParaGraduacao() {
		preCadastroParaGraduacao = true;
		setDirBase("/prodocente/atividades/");
		try {
			carregaSubAreas();
		} catch (DAOException e) {
			notifyError(e);
		}
		return forward(getFormPage());

	}
	
	/**
	 * Serve como uma atualição para as atividades de um docente para o nível de Graduação.
	 * 
	 * @return
	 */
	public String atualizarParaGraduacao() {
		atualizacaoParaGraduacao = true;
		setDirBase("/prodocente/atividades/");
		try {
			carregaSubAreas();
		} catch (DAOException e) {
			notifyError(e);
		}
		return forward(getFormPage());

	}

	/**
	 * Método que verifica se operação é de cadastro de graduação.
	 * @return the preCadastroParaGraduacao
	 */
	public boolean isPreCadastroParaGraduacao() {
		return preCadastroParaGraduacao;
	}

	/**
	 * Método que popula a operação como cadastro de graduação.
	 * @param preCadastroParaGraduacao 
	 */
	public void setPreCadastroParaGraduacao(boolean preCadastroParaGraduacao) {
		this.preCadastroParaGraduacao = preCadastroParaGraduacao;
	}
	
	
	/**
	 * Método que verifica se operação é de atualização de graduação.
	 * @return the atualizacaoParaGraduacao
	 */
	public boolean isAtualizacaoParaGraduacao() {
		return atualizacaoParaGraduacao;
	}
	
	/**
	 * Método que popula a operação como atualização de graduação.
	 * @param preCadastroParaGraduacao 
	 */
	public void setAtualizacaoParaGraduacao(boolean atualizacaoParaGraduacao) {
		this.atualizacaoParaGraduacao = atualizacaoParaGraduacao;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if ( !isAtualizacaoParaGraduacao() && ( !isPreCadastroParaGraduacao() 
				|| ((PersistDB )obj).getId() != 0 ) ) {
			return super.cadastrar();
		} else {
			erros = new ListaMensagens();
			Validatable v = (Validatable) obj;
			erros.addAll(v.validate());
			if (hasErrors())
				return null;
			RegistroAtividadeMBean raMbean = (RegistroAtividadeMBean) getMBean("registroAtividade");
			return raMbean.confirmar();
		}
	}


}
