/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '08/11/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.prodocente.CategoriaFuncionalDAO;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.mensagens.MensagensProdocente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.AudioVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.BloqueioCadastroProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.CategoriaFuncional;
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.NaturezaExame;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.PremioRecebido;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoTecnologica;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacaoOrganizacaoEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducaoTecnologica;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.prodocente.producao.dominio.ValidacaoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.VisitaCientifica;
import br.ufrn.sigaa.prodocente.relatorios.jsf.RelatorioProdutividadeMBean;

/**
 * Controlador abstrato que os controladores do prodocente devem herdar. Cont�m
 * fun��es comuns entre todos os controladores deste subsistema.
 *
 * @author Gleydson
 *
 * @param <T>
 */
@Component("producao")
@Scope("session")
public class AbstractControllerProdocente<T> extends SigaaAbstractController<T> {

	/** Define a tela de aviso de bloqueio. */
	public static final String BLOQUEIO = "/prodocente/aviso_bloqueio.jsp";
	
	/** Define se a a��o � de valida��o da produ��o intelectual.  */
	protected Boolean validar = false;

	/** Define se a a��o � de visualiza��o dos dados da produ��o intelectual. */
	protected Boolean visualizar = false;

	/** Define a �rvore de ques er� visualizada na view.  */
	protected TreeNode arvore = null;

	/** Define o mapa das produ��es intelectuais do docente. */
	protected Map<Servidor, Collection<Producao>> mapaProducoes;

	/** Define o ano de refer�ncia da produ��o intelectual do docente. */
	private int anoReferencia;

	/** Define o t�tulo na busca da produ��o do intelectual do docente */
	private String titulo = "";

	/** Define a se o cadastro � da categoria. */
	private boolean categoria = false;

	/** Define o docente que est� acessando a produ��o intelectual. */
	protected Servidor docente = new Servidor();

	/** Define que a produ��o foi validada. */
	private int validados;

	/** Define se a opera��o � para carregar as produ��es do docente. */
	private boolean carregarMinhasProducoes = true;

	/** Define a  sub�rea. */
	protected Collection<SelectItem> subArea = new ArrayList<SelectItem>();

	/** Define as especialidades do docente. */
	private Collection<SelectItem> especialidades = new ArrayList<SelectItem>();
	
	/** Define as especialidades exclusivas do n�vel de ensino de lato sensu. */
	private Collection<SelectItem> especialidadeLatoSensu = new ArrayList<SelectItem>();

	/** Define a categoria funcional do docente. */
	private Collection<SelectItem> categoriaFuncional = new ArrayList<SelectItem>();

	/** Define o subtipo art�stico **/
	private Collection<SelectItem> subTipoArtistico = new ArrayList<SelectItem>();

	/** Cole��o com todos os anos cadastrados no sistema */
	private List<SelectItem> anos;

	/** Atributos usados para upload de arquivo */
	private UploadedFile arquivo;

	/** Define a produ��o selecionada. */
	private Producao dados;

	/** Define o redirecionamento ap�s a remo��o da produ��o. */
	private boolean forwardRemover = true;
	
	/** Vari�vel utilizada para definir um redirecionamento espec�fico (e diferente do padr�o) ap�s o usu�rio clicar em [Alterar]. */
	protected String forwardAlterar = null;
	
	/** Vari�vel utilizada para definir um redirecionamento espec�fico (e diferente do padr�o) ap�s o usu�rio clicar em [Cancelar]. */
	protected String forwardCancelar = null;

	/** Define todas as produ��es do docente. */
	@SuppressWarnings("unchecked")
	private Collection allProducao;

	/** Define o pagina atual na tela de listagem das produ��es. */
	private int paginaAtual = 0;
	
	public void setForwardAlterar(String forwardAlterar) {
		this.forwardAlterar = forwardAlterar;
	}

	public void setForwardCancelar(String forwardCancelar) {
		this.forwardCancelar = forwardCancelar;
	}

	public AbstractControllerProdocente() {
		anoReferencia = CalendarUtils.getAnoAtual() - 1;
	}

	/**
	 * M�todo que retorna o ano atual do calend�rio acad�mico.
	 * @param data
	 * @return
	 */
	public int getAno(Date data) {
		Calendar c = Calendar.getInstance();
		if (data != null) {
			c.setTime(data);
		}
		return c.get(Calendar.YEAR);
	}

	/**
	 * Retorna Trinta anos atr�s a partir do ano atual, para preencher o comboBox
	 * de Ano de Refer�ncia
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public List<SelectItem> getAnosCadastrarAnoReferencia() {
		int anoAtual = CalendarUtils.getAnoAtual();
		int dezAnosAtras = anoAtual - 30;
		ArrayList<SelectItem> anos = new ArrayList<SelectItem>();
		for (Integer ano = anoAtual; ano >= dezAnosAtras; ano--) {
			SelectItem item = new SelectItem(ano.toString(), ano.toString());
			anos.add(item);
		}
		return anos;
	}

	/**
	 * Retorna todos os anos que tem publicados v�lidas
	 *
	 * @return
	 * @throws DAOException
	 */
	@Override
	public List<SelectItem> getAnos() throws DAOException {

		if (anos == null || anos.isEmpty()) {
			ProducaoDao dao = getDAO(ProducaoDao.class);
			Collection<Integer> anosBD = dao.getAnosCadastrados();
			anos = new ArrayList<SelectItem>();
			for (Integer ano : anosBD) {
				if (ano != null) {
					SelectItem item = new SelectItem(ano.toString(), ano
							.toString());
					anos.add(item);
				}
			}
		}

		return this.anos;
	}
	
	/**
	 * M�todo que retorna o tipo de produ��o selecionado pelo docente. 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTipo() throws DAOException {
		subTipoArtistico = new ArrayList<SelectItem>();
		ProducaoDao dao = getDAO(ProducaoDao.class);
		Producao producao = (Producao) obj;
		subTipoArtistico = toSelectItems(dao.findSubTipos(producao), "id",
				"descricao");
		return subTipoArtistico;
	}

	/**
	 * M�tod que retorna os tipos de produ��es para ser selecionado no formul�rio
	 * de cadastro da produ��o.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTipoProducaoCombo() throws DAOException {
		GenericDAO genericDAO = getGenericDAO();
		Collection<TipoProducao> all = genericDAO.findAll(TipoProducao.class, "descricao", "asc");
		
		return toSelectItems(all, "id", "descricao");
		
	}
	
	/**
	 * Para tratar mudan�a de �rea
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void changeArea(ValueChangeEvent evt) throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
			AreaConhecimentoCnpq grandeArea = new AreaConhecimentoCnpq( (Integer) evt.getNewValue()) ;
			subArea = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
		}

	}

	/**
	 * M�todo que retornar um cole��o das �reas de conhecimentos para ser utilizado
	 * no formul�rio de cadastro das produ��es.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAreaConhecimentoCombo() throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		Collection<AreaConhecimentoCnpq> areas = dao.findAll(AreaConhecimentoCnpq.class, "nome", "asc");
		
		return toSelectItems(areas, "id", "nome");
	}
	
	/**
	 * Para tratar mudan�a da sub�rea
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void changeSubArea(ValueChangeEvent evt) throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (((Integer) evt.getNewValue()) != null) {
			Integer codigo = (Integer) evt.getNewValue();
			especialidades = toSelectItems(dao.findSubAreas(codigo), "id", "nome");
		}

	}

	/**
	 * M�todo que habilita a categoria no formul�rio.
	 * @param evt
	 * @throws DAOException
	 */
	public void habilitarCategoria(ValueChangeEvent evt) throws DAOException {

		if (evt.getNewValue().equals(2)) {
			CategoriaFuncionalDAO dao = getDAO(CategoriaFuncionalDAO.class);
			categoriaFuncional = toSelectItems(dao.getAllCategorias(), "id",
					"descricao");
			categoria = true;
		} else {
			categoria = false;
		}

	}

	/**
	 * retorna a sub-area a partir da area antes do atualizar.
	 */
	public void changeArea() throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (obj instanceof Producao) {
			Producao p = (Producao) obj;
			if ( !ValidatorUtil.isEmpty( p.getArea()) ) {
				subArea = toSelectItems(dao.findAreas(p.getArea()), "id", "nome");
			}
		}

	}

	/**
	 * Para tratar mudan�a da sub�rea
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void changeEspecialidade(ValueChangeEvent evt) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);

		if (((Integer) evt.getNewValue()) != null) {
			Integer codigo = (Integer) evt.getNewValue();
			AreaConhecimentoCnpq areaConhecimento = getGenericDAO().findByPrimaryKey(codigo, AreaConhecimentoCnpq.class);
			especialidadeLatoSensu = toSelectItems(dao.findEspecialidade(areaConhecimento.getSubarea()), "id", "nome");
		}
	}
	
	/**
	 * Retorna todos os docentes da unidade do usu�rio
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getDocentesUnidade() throws DAOException {
		ServidorDao dao = null;
		try {
			dao = getDAO(ServidorDao.class);

			return toSelectItems(dao.findByDocente(getUsuarioLogado().getVinculoAtivo().getUnidade().getId()), "id", "pessoa.nome");

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			dao.close();
		}
	}

	/**
	 * Retorna todos os docentes da unidade do usuário, exceto o servidor do
	 * usu�rio logado
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getDocentesUnidadeParaValidacao()
			throws DAOException {
		try {
			ServidorDao dao = getDAO(ServidorDao.class);

			if (isChefe()) {
				Collection<Servidor> servidores = dao
						.findByDocente(getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
				servidores.remove(getServidorUsuario());
				return toSelectItems(servidores, "id", "pessoa.nome");
			} else if (isDiretorCentro()) {
				return toSelectItems(getChefes(dao), "id", "pessoa.nome");
			} else {
				return new ArrayList<SelectItem>();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		}
	}

	/**
	 * M�todo que retorna os chefes de departamento de acorod com o centro que est� associado ao 
	 * usu�ri do docente.
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private Collection<Servidor> getChefes(ServidorDao dao) throws DAOException {
		return dao.findChefesDepartamentoByCentro(getUsuarioLogado().getVinculoAtivo().getUnidade()
				.getGestora().getId());
	}

	/**
	 * Usada no menu do chefe de departamento
	 *
	 * @return
	 * @throws DAOException
	 */
	public Long getTotalValicoesPendentes() throws DAOException {
		ProducaoDao dao = getDAO(ProducaoDao.class);
		return dao.getTotalPendentes(getUsuarioLogado().getVinculoAtivo().getUnidade());
	}

	public void setAnos(List<SelectItem> anos) {
		this.anos = anos;
	}

	/**
	 * M�todo que retornar todas as produ��es do docente logado.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection getAll() throws SegurancaException {

		setTamanhoPagina(20);
		ProducaoDao dao = getDAO(ProducaoDao.class);

		checkDocenteRole();

		try {

			if ( allProducao == null || mudouPagina() ) {
				allProducao = getDadosEspecificoDaProducao();

				// se entrar neste if � por que o m�todo n�o foi reescrito no filho
				if ( allProducao == null ) {
					allProducao =   dao.findByProducaoServidor(getServidorUsuario(),
							obj.getClass(), getPaginacao());
				}
			}
			return allProducao;
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList();
		}

	}

	/**
	 * M�todo retorna a pagina atual na listagem das produ��es do docente.
	 * @param paginacao
	 * @return
	 */
	protected boolean mudouPagina() {
		boolean mudou = getPaginacao() != null && paginaAtual != getPaginacao().getPaginaAtual();
		if (mudou) {
			paginaAtual = getPaginacao().getPaginaAtual();
		}
		return mudou;
	}

	/**
	 * M�todo usado para ser reescrito no filho e fazer proje��o para as consultas de listagem
	 *
	 * @return
	 */
	public Collection getDadosEspecificoDaProducao() throws DAOException {
		return null;
	}

	/**
	 * M�todo sobrescrito para mostrar as sub-areas das produ��es
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> getAllClasse() {
		GenericDAO genDAO = getGenericDAO();

		try {

			return (Collection<T>) genDAO.findAll(obj.getClass());

		} catch (DAOException e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	public Boolean getVisualizar() {
		return visualizar;
	}

	public void setVisualizar(Boolean visualizar) {
		this.visualizar = visualizar;
	}

	/**
	 * Opera��o para validar a produ��o de um professor pelo seu chefe do
	 * departamento
	 *
	 * @return
	 * @throws ArqException
	 */
	public String validar() throws ArqException {

		ValidacaoProducao mov = new ValidacaoProducao();
		mov.setProducao((Producao) obj);
		mov.setUsuario(getUsuarioLogado());

		if (mov.getProducao().getValidado() == null) {
			addMensagemErro("Escolha se a produ��o v�lida ou n�o");
			GenericDAO dao = getGenericDAO();
			popularObjeto(dao.findByPrimaryKey(mov.getProducao().getId(), mov
					.getProducao().getClass()));
			return preValidar();
		}

		try {
			Producao retorno = (Producao) execute(mov, getCurrentRequest());
			popularObjeto(retorno);
			String validado = null;
			if (mov.getProducao().getValidado()) {
				validado = "validada";
			} else {
				validado = "invalidada";
			}
			addMessage("Produ��o " + validado + " com sucesso",
					TipoMensagemUFRN.INFORMATION);
		} catch (Exception e) {
			notifyError(e);
		}
		return forward("/prodocente/producao/producoes.jsp");

	}

	/**
	 * M�todo que realiza o filtro das produ��es do docente.
	 * @return
	 * @throws DAOException
	 */
	public String filtrar() throws DAOException {
		mapaProducoes = null;
		return getCarregarArvoreProducoes();
	}

	/**
	 * Cria a �rvore de produ��o de um servidor ou de todos servidores do
	 * departamento
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregarArvoreProducoes() throws DAOException {

		ProducaoDao producaoDao = getDAO(ProducaoDao.class);
		try {
			ArrayList<Producao> producoes = null;
			
			Boolean tempValidados = null;
			if (validados == 1) {
				tempValidados = true;
			} else if (validados == 2) {
				tempValidados = false;
			}
			
			// aqui s�o buscadas as produ��es da unidade de acordo com
			// preenchimento dos par�metros de busca
			if (!carregarMinhasProducoes) {
				if (isChefe()) {
					producoes = producaoDao.findToValidacaoChefe(
							getUsuarioLogado(), titulo, docente, anoReferencia,
							tempValidados);
				} else if (isDiretorCentro()) {
					ArrayList<Integer> chefes = new ArrayList<Integer>(0);
					if (docente == null || docente.getId() == 0) {
						ServidorDao servidorDao = getDAO(ServidorDao.class);
						try {
							Collection<Servidor> chefesCol = getChefes(servidorDao);
							for (Servidor c : chefesCol) {
								chefes.add(c.getId());
							}
						} finally {
							servidorDao.close();
						}
					}
					producoes = producaoDao.findToValidacaoDiretor(
							getUsuarioLogado(), titulo, docente, anoReferencia,
							tempValidados, chefes
							.toArray(new Integer[chefes.size()]));
				}
			} else {
				producoes = producaoDao.findByDocente(titulo, getUsuarioLogado()
						.getServidor(), anoReferencia, tempValidados);
			}
			
			if (!producoes.isEmpty()) {
				popularMapaProducoes(producoes);
			}
			
			return null;
			
		} finally {
			producaoDao.close();
		}
	}

	/**
	 * M�tod que popula um mapa das produ��es do docente.
	 * @param producoes
	 */
	protected void popularMapaProducoes(Collection<? extends Producao> producoes) {
		// Criar mapa de servidores e suas produ��es
		mapaProducoes = new TreeMap<Servidor, Collection<Producao>>(new Comparator<Servidor>() {
			public int compare(Servidor o1, Servidor o2) {
				return o1.getPessoa().getNome().compareTo(o2.getPessoa().getNome());
			}
		});

		// Preencher mapa com as produ��es encontradas
		for (Producao p : producoes) {
			Servidor servidor = p.getServidor();
			Collection<Producao> producoesServidor = mapaProducoes.get(servidor);
			if (producoesServidor == null) {
				producoesServidor = new TreeSet<Producao>(new Comparator<Producao>() {
					public int compare(Producao o1, Producao o2) {
						int comparacao = o1.getTipoProducao().getDescricao().compareTo(o2.getTipoProducao().getDescricao());
						if (comparacao == 0 && o1.getAnoReferencia() != null && o2.getAnoReferencia() != null) {
							comparacao = o1.getAnoReferencia().compareTo(o2.getAnoReferencia());
						}
						if (comparacao == 0 && o1.getTitulo() != null && o2.getTitulo() != null) {
							comparacao = o1.getTitulo().compareTo(o2.getTitulo());
						}
						return comparacao;
					}
				});
				mapaProducoes.put(servidor, producoesServidor);
			}
			producoesServidor.add(p);
		}
	}

	public TreeNode getArvore() {
		return arvore;
	}

	/**
	 * Verifica se os casos de uso de produ��o � iniciado a partir de um chefe de departamento
	 * @return
	 */
	protected boolean isChefe() {
		DadosAcesso acesso = getAcessoMenu();
		return acesso.isChefeDepartamento();
	}

	public boolean getChefe() {
		return isChefe();
	}

	public boolean getDiretorCentro() {
		return isDiretorCentro();
	}

	/**
	 * Verifica se os casos de uso de produ��o � iniciado a partir de um diretor de centro
	 * @return
	 */
	protected boolean isDiretorCentro() {
		DadosAcesso acesso = getAcessoMenu();
		return acesso.isDiretorCentro();
	}

	public Boolean getValidar() {
		return validar;
	}

	public void setValidar(Boolean validar) {
		this.validar = validar;
	}

	/**
	 * M�todo que carrrega e redireciona para a tela dos dados
	 * de valida��o da produ��o.
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public String getDadosValidacao() throws ArqException {

		// if(isChefe()){
		try {
			Producao p = null;
			GenericDAO dao = getGenericDAO();
			if (getParameter("id") != null) {
				p = dao.findByPrimaryKey(getParameterInt("id"), Producao.class);
				popularObjeto(p);

				String classFull = p.getClass().toString();
				// só o nome da classe
				String className = classFull.substring(classFull
						.lastIndexOf(".") + 1);

				//String packageName = "br.ufrn.sigaa.prodocente.producao.jsf.";
				// String mBeanClass = packageName + className + "MBean";
				String mBeanName = className.substring(0, 1).toLowerCase()
						+ className.substring(1);

				AbstractControllerProdocente mBeanObj = getMBean(mBeanName);
				mBeanObj.setObj(p);
				mBeanObj.setReadOnly(true);
				mBeanObj.setConfirmButton("Validar");
				if (isChefe()) {
					mBeanObj.setValidar(true);
				}
				mBeanObj.setVisualizar(true);

				// createMBean(mBeanName, mBeanObj);

				prepareMovimento(SigaaListaComando.VALIDAR_PRODUCAO);

				setDirBase("/prodocente/producao/");

				return "";
			}
			return null;

		} catch (Exception e) {
			throw new ArqException(e);
		}

	}

	/**
	 *  M�todo que indica a tela de visualiza��o da produ��o do docente.
	 * @param id
	 * @return
	 * @throws ArqException
	 */
	public String getViewProducao(int id) throws ArqException {

		Producao p = null;
		GenericDAO dao = getGenericDAO();

		p = dao.findByPrimaryKey(id, Producao.class);
		setDirBase("/prodocente/producao/");
		return getDirBaseProducao(p) + "/view.jsp";

	}

	/**
	 * M�todo que redireciona para tela de valida��o das produ��es.
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public String preValidar() throws ArqException {

		// if(isChefe()){
		try {
			Producao p = null;
			GenericDAO dao = getGenericDAO();
			if (getParameter("id") != null) {
				p = dao.findByPrimaryKey(getParameterInt("id"), Producao.class);
				// popularObjeto(p);

				String classFull = p.getClass().toString();
				// só o nome da classe
				String className = classFull.substring(classFull
						.lastIndexOf(".") + 1);

				String packageName = "br.ufrn.sigaa.prodocente.producao.jsf.";
				String mBeanClass = packageName + className + "MBean";
				String mBeanName = className.substring(0, 1).toLowerCase()
						+ className.substring(1);

				AbstractControllerProdocente mBeanObj = (AbstractControllerProdocente) Class
						.forName(mBeanClass).newInstance();
				mBeanObj.setObj(p);
				mBeanObj.setReadOnly(true);
				mBeanObj.setConfirmButton("Validar");
				if (isChefe()) {
					mBeanObj.setValidar(true);
				}
				mBeanObj.setVisualizar(true);

				createMBean(mBeanName, mBeanObj);

				prepareMovimento(SigaaListaComando.VALIDAR_PRODUCAO);

				setDirBase("/prodocente/producao/");

				return forward(getDirBaseProducao(p) + "/view.jsp");
			}
			return null;

		} catch (Exception e) {
			throw new ArqException(e);
		}
	}

	/**
	 * M�todo que deve ser sobrescrito pelo classe filho
	 * setando o objeto.
	 * @param p
	 */
	public void popularObjeto(Producao p) {

	}

	/**
	 * Ap�s do clique do enviar arquivo
	 *
	 * @return
	 * @throws DAOException
	 */
	public String preEnviarArquivo() throws DAOException {

		try {
			prepareMovimento(ArqListaComando.ALTERAR);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Integer idProducao = getParameterInt("idProducao");
		GenericDAO dao = getGenericDAO();
		dados = dao.findByPrimaryKey(idProducao, Producao.class);
		
		if (dados == null) {
			addMensagemErro("Produ��o n�o localizada.");
			return null;
		}
		
		if (dados.getServidor().getId() != getServidorUsuario().getId()) {
			addMensagemErro("Esta produ��o n�o est� cadastrada para seu Usu�rio");
			return null;
		}
		return forward("/prodocente/producao/ArquivoProducao/arquivo.jsp");

	}

	/**
	 * Ap�s do confirmar do enviar arquivo
	 *
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String enviarArquivo() throws SQLException, IOException,
			NegocioException, ArqException {

	
		if (arquivo != null && !isExcedeTamanhoMaximo(arquivo) ) {

				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();

				EnvioArquivoHelper.inserirArquivo(idArquivo,
						arquivo.getBytes(), arquivo.getContentType(), arquivo
								.getName());

				GenericDAO dao = getGenericDAO();
				
				boolean exibir = dados.isExibir();
				dados = dao.findByPrimaryKey(dados.getId(), Producao.class);
				dados.setIdArquivo(idArquivo);
				dados.setExibir(exibir);

				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(dados);
				mov.setCodMovimento(ArqListaComando.ALTERAR);

				try{
					execute(mov, getCurrentRequest());
					addMessage("Arquivo Adicionado com Sucesso", TipoMensagemUFRN.INFORMATION);
					allProducao = null;
				}catch (Exception e) {
					tratamentoErroPadrao(e);
					return null;
				}	
				
				return cancelArquivo();


		} else if ( !hasErrors() ) {
			addMensagemErro("Informe o arquivo para enviar");
		}

		return null;

	}
	
	/**
	 * Verifica se o arquivo enviado execede o tamanho
	 * m�ximo definido no banco.
	 * @param arquivo
	 * @return
	 */
	public boolean isExcedeTamanhoMaximo(UploadedFile arquivo){ 
	
		Integer tamanhoMaximoMB = ParametroHelper.getInstance().
		getParametroInt( ConstantesParametro.TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO );
		//Converte a refer�ncia do par�metro para o tamanho exato em bytes.
		Integer tamanhoMaximoBytes = tamanhoMaximoMB * 1024 * 1024;
	
		if ( arquivo.getSize() > tamanhoMaximoBytes ) {
			addMensagem( MensagensProdocente.TAMANHO_ARQUIVO_PRODUCAO_EXCEDE, tamanhoMaximoMB );
			return true;
		}
		
		return false;
	}
			
	/**
	 * Retorna o diret�rio base
	 *
	 * @return
	 */
	public String getDirBaseProducao(Producao p) {

		String dirBase = "";

		if (getCurrentSession().getAttribute("dirBase") != null) {
			dirBase = (String) getCurrentSession().getAttribute("dirBase");
		} else {
			dirBase = getSubSistema().getDirBase();
		}

		String mBeanClassName = p.getClass().toString();
		String mBean = mBeanClassName
				.substring(mBeanClassName.lastIndexOf(".") + 1);
		if (dirBase.equals("")) {
			return dirBase;
		} else {
			return dirBase + mBean;
		}
	}

	/**
	 * M�todo respons�vel em redirecionar o docente para determinadas telas de acordo
	 * com as condi��es descritas internamente.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#cancelar() 
	 */
	@Override
	public String cancelar() {
		
		if (forwardCancelar != null){
			return forward(forwardCancelar);
		}

		if (validar != null && validar) {

			return forward("/prodocente/producao/producoes.jsp");

		} else {

			if (getConfirmButton() != null) {
				if (getConfirmButton().equals("Alterar")
						|| getConfirmButton().equals("Remover")) {
					String url = "/sigaa/" + getListPage();
					url = url.substring(0, url.length() - 1);
					return redirectJSF(url + "f");

				} else if (getConfirmButton().equals("Cadastrar")) {
					return forward("/prodocente/nova_producao.jsp");
				}
			}
			return super.cancelar();
		}

	}

	/**
	 * Mais informa��es {@link #anoReferencia} 
	 * @return
	 */
	public int getAnoReferencia() {
		return anoReferencia;
	}

	/**
	 * Mais informa��es {@link #anoReferencia} 
	 * @param anoReferencia
	 */
	public void setAnoReferencia(int anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	/**
	 * Mais informa��es {@link #docente} 
	 * @return
	 */
	public Servidor getDocente() {
		return docente;
	}

	/**
	 * Mais informa��es {@link #docente} 
	 * @param docente
	 */
	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public int getValidados() {
		return validados;
	}

	public void setValidados(int validados) {
		this.validados = validados;
	}

	/**
	 * Mais informa��es {@link #titulo} 
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Mais informa��es {@link #titulo} 
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Mais informa��es {@link #subArea}
	 * @return
	 */
	public Collection<SelectItem> getSubArea() {
		return subArea;
	}

	/**
	 * Mais informa��es {@link #subArea}
	 * @param subArea
	 */
	public void setSubArea(Collection<SelectItem> subArea) {
		this.subArea = subArea;
	}

	/**
	 * Mais informa��es {@link #categoria}
	 * @return the categoria
	 */
	public boolean isCategoria() {
		return categoria;
	}

	/**
	 * Mais informa��es {@link #categoria}
	 * @param categoria         
	 */
	public void setCategoria(boolean categoria) {
		this.categoria = categoria;
	}

	/**
	 * Mais informa��es {@link #categoriaFuncional}
	 * @return the categoriaFuncional
	 */
	public Collection<SelectItem> getCategoriaFuncional() {
		return categoriaFuncional;
	}

	/**
	 * Mais informa��es {@link #categoriaFuncional}
	 * @param categoriaFuncional
	 */
	public void setCategoriaFuncional(Collection<SelectItem> categoriaFuncional) {
		this.categoriaFuncional = categoriaFuncional;
	}

	/**
	 * Ao remover as produ��es, as mesmas n�o ser�o removidas da base de dados,
	 * apenas o campo ativo ser� marcado como FALSE.
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@Override
	public String remover() {
		if (verificaBloqueio()) {
			return forward("/prodocente/aviso_bloqueio.jsp");
		}

		MovimentoCadastro mov = new MovimentoCadastro();
		PersistDB obj = (PersistDB) this.obj;

		try {

			// Ao remover as produ��es, as mesmas n�o ser�o removidas da base de
			// dados, apenas o campo ativo ser� marcado como FALSE. Edson Anibal
			// (ambar@info.ufrn.br)
			prepareMovimento(ArqListaComando.DESATIVAR);

			obj.setId(getParameterInt("id"));
			GenericDAO dao = new GenericDAOImpl(getSistema(),
					getSessionRequest());

			obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
		}

		// Se for inst�ncia de Produ��o:
		if (obj instanceof Producao) {
			Producao producao = new Producao(obj.getId());
			mov.setObjMovimentado(producao); // como o m�todo UpdateField usa
			// reflection , ele sempre
			// pegava a classe filha (mesmo
			// com o casting) por isso estou
			// passando a classe pai.
		} else {
			mov.setObjMovimentado(obj);
		}

		if (obj.getId() == 0) {
			addMensagemErro("N�o h� objeto informado para remo��o");
			return null;
		} else {

			// Ao remover as produ��es, as mesmas n�o ser�o removidas da base de
			// dados, apenas o campo ativo ser� marcado como FALSE. Edson Anibal
			// (ambar@info.ufrn.br)
			mov.setCodMovimento(ArqListaComando.DESATIVAR);

			try {
				execute(mov, (HttpServletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

				addMessage("Opera��o realizada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			} catch (Exception e) {
				addMensagemErro("Erro Inesperado: " + e.getMessage());
				e.printStackTrace();
				return forward(getListPage());
			}
			resetBean();
			setResultadosBusca(null);

			afterRemover();

			if (forwardRemover) {
				return redirectJSF(getUrlRedirecRemover());
			} else {
				return null;
			}

		}


	}

	/**
	 * Alguns MBeans como ,MaquetePrototipoOutros que usa o objeto
	 * ProducaoTecnologica, Precisam sobrescrever esse m�todo para redirecionar
	 * para p�gina correta.
	 *
	 * @author Edson Anibal ambar@info.ufrn.br
	 * @return
	 */
	public String getUrlRedirecRemover() {
		return "/sigaa/" + getDirBase() + "/lista.jsf";
	}

	/**
	 * Action invocada pelo menu para listar
	 *
	 * @return
	 */
	@Override
	public String listar() throws ArqException {

		resetBean("paginacao");
		setDirBase("/prodocente/producao/");
		getCurrentSession().setAttribute("backPage", getListPage());
		return forward(getListPage());

	}

	/**
	 * M�todo que redireciona o docente ap�s perssionar o 
	 * bot�o cancelar na tela de envio de arquivos.
	 * @return
	 */
	public String cancelArquivo() {
		String page = (String) getCurrentSession().getAttribute("backPage");
		if (page == null) {
			page = "prodocente/listar_producao.jsf";
		} else {
			page = page.substring(0, page.length() - 1) + "f";
			if ( !page.contains("prodocente/producao/") ) {
				page = "prodocente/producao/" + page;
			}
		}
		return redirectJSF("/sigaa/" + page);

	}

	/**
	 * Action invocada pelo jsp de listar para a tela de cadastro
	 *
	 * @return
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		resetBean();
		super.preCadastrar();
		setDirBase("/prodocente/producao/");
		if (verificaBloqueio()) {
			return forward("/prodocente/aviso_bloqueio.jsp");
		} else {
			return forward(getFormPage());
		}

	}

	/**
	 * Vai para tela para visualizar o relat�rio do prodocente
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String verRelatorioProdutividade() throws SegurancaException {
		checkDocenteRole();
		return forward("/prodocente/producao/relatorios/produtividade/emissao/seleciona_docente.jsf");
	}

	/**
	 * Vai para tela para visualizar o da GED
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String verRelatorioCotas() throws SegurancaException, DAOException {
		checkRole(new int[] { SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_PESQUISA,
				SigaaPapeis.PORTAL_PLANEJAMENTO });
		RelatorioProdutividadeMBean bean = getMBean("relatorioAtividades");
		bean.setIdRelatorio(ParametroHelper.getInstance().getParametroInt(ConstantesParametro.RELATORIO_PADRAO_PESQUISA));
		return forward("/prodocente/producao/relatorios/produtividade/emissao/seleciona_docente.jsf");
	}

	/**
	 * Vai para tela para visualizar o relat�rio da GED
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String verRelatorioProgressao() throws SegurancaException,
			DAOException {

		checkRole(new int[] { SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_PESQUISA,
				SigaaPapeis.PORTAL_PLANEJAMENTO });

		RelatorioProdutividadeMBean bean = getMBean("relatorioAtividades");
		bean.setIdRelatorio(ParametroHelper.getInstance().getParametroInt(
				ConstantesParametro.RELATORIO_PADRAO_PROGRESSAO));
		return forward("/prodocente/producao/relatorios/produtividade/emissao/seleciona_docente.jsf");
	}

	public Producao getDados() {
		return dados;
	}

	public void setDados(Producao dados) {
		this.dados = dados;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public Collection<SelectItem> getSubTipoArtistico() {
		return subTipoArtistico;
	}

	public void setSubTipoArtistico(Collection<SelectItem> subTipoArtistico) {
		this.subTipoArtistico = subTipoArtistico;
	}

	/**
	 * Retorna os tipos de participa��es por tipo de produ��o
	 *
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getTipoParticipacao(TipoProducao tipo)
			throws DAOException {

		GenericDAO dao = getGenericDAO();
		return toSelectItems(dao.findByExactField(TipoParticipacao.class,
				new String[]{"tipoProducao.id", "ativo"}, new Object[]{tipo.getId(), Boolean.TRUE}), "id", "descricao");

	}
	
	public boolean isCarregarMinhasProducoes() {
		return carregarMinhasProducoes;
	}

	public boolean getCarregarMinhasProducoes() {
		return isCarregarMinhasProducoes();
	}

	public void setCarregarMinhasProducoes(boolean carregarMinhasProducoes) {
		this.carregarMinhasProducoes = carregarMinhasProducoes;
	}

	/**
	 * M�todo que carrega as produ��es do docente para avalia��o.
	 * @return
	 */
	public String carregarProducoesParaAvaliacao() {
		carregarMinhasProducoes = false;
		return forward("/prodocente/producao/validacao.jsp");
	}

	public Collection<SelectItem> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Collection<SelectItem> especialidades) {
		this.especialidades = especialidades;
	}

	/**
	 * Carrega as sub-areas da �rea cadastrada
	 *
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public void carregaSubAreas() throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (obj instanceof Producao) {
			Producao p = (Producao) obj;

			if ( !ValidatorUtil.isEmpty( p.getArea()) ) {
				subArea = toSelectItems( dao.findAreas(p.getArea()), "id", "nome");
				AbstractControllerProdocente bean = getMBean("producao");
				bean.setSubArea(subArea);
			} else {
				p.setArea( new AreaConhecimentoCnpq() );
			}

			if (p.getSubArea() == null) {
				p.setSubArea(new AreaConhecimentoCnpq());
			}

		}

	}

	/**
	 * M�todo que carrega as sub�reas ap�s selecionar a {@link #atualizar()}
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#afterAtualizar() 
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		carregaSubAreas();
	}

	/**
	 * M�todo que reseta a listagem das produ��o ap�s cadastrar uma produ��o.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#afterCadastrar() 
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();

		// Limpar lista de produ��es para que seja atualizada
		allProducao = null;
	}

	/**
	 * M�todo que reseta a listagem das produ��o ap�s a remo��o de uma produ��o.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#afterRemover() 
	 */
	@Override
	public void afterRemover()
	{
		super.afterRemover();
		allProducao = null; //<-- Garantindo que ao remover uma produ��o de um MBean em sess�o, a p�gina subsequente de listagem seja atualizada @author Edson Anibal (ambar@info.ufrn.br)
	}

	/**
	 * M�todo chamado antes do cadastrar, reimplementado pelo filho se ele
	 * desejar fazer algo antes de cadastrar
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {

		//Forma��es Acad�micas Duplicadas (Verificar se j� existe com o mesmo t�tulo, do mesmo Tipo e para o mesmo Servidor). @Author Edson Anibal (ambar@info.ufrn.br)
		PersistDB persistableObj = (PersistDB) obj;
		if ( persistableObj.getId() == 0 && obj instanceof Producao)
		{
			Producao producao = (Producao) obj;

			ProducaoDao dao = getDAO(ProducaoDao.class);
			dao.initialize(producao.getTipoProducao());
			if (dao.isProducaoMesmoAnoTituloTipo(producao))
				addMensagemErro("J� existe uma produ��o do tipo '"+producao.getTipoProducao().getDescricao()+"' cadastrada com o mesmo t�tulo, mesmo ano de refer�ncia e pertencente ao mesmo servidor.");
		}

		super.beforeCadastrarAndValidate();
	}
	//------//

	/**
	 * Reinicia o objeto ap�s efetuar o cadastro de uma produ��o.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#beforeCadastrarAfterValidate() 
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
		super.beforeCadastrarAfterValidate();

		if (obj instanceof Producao) {
			Producao producao = (Producao) obj;
			producao.setValidado(null);
		}
	}

	/**
	 * M�todo que verifica se a produao est� bloqueada.
	 * @return
	 */
	public boolean verificaBloqueio() {

		try {
			ProducaoDao dao = getDAO(ProducaoDao.class);
			BloqueioCadastroProducao bloqueio = dao.findByBloqueio();
			getCurrentRequest().setAttribute("bloqueio", bloqueio);

			return bloqueio != null;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * M�todo que prepara o formul�rio de atualiza��o de acordo com o tipo, verificando tamb�m se 
	 * n�o existe bloqueio.
	 */
	@Override
	public String atualizar() throws ArqException {

		if (verificaBloqueio()) {
			return forward("/prodocente/aviso_bloqueio.jsp");
		} else {
			String retorno = super.atualizar();
			
			if (obj instanceof PremioRecebido) {
				PremioRecebido premio = (PremioRecebido) obj;
				if (premio.getInstituicao() == null)
					premio.setInstituicao(new InstituicoesEnsino());
			}else if(obj instanceof ParticipacaoComissaoOrgEventos){
				ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) obj;
				if(p.getTipoParticipacaoOrganizacao() == null)
					p.setTipoParticipacaoOrganizacao(new TipoParticipacaoOrganizacaoEventos());
			} else if (obj instanceof AudioVisual) {
				AudioVisual av = (AudioVisual) obj;
				if (av.getArea() == null || av.getArea().getId() == 0)
					av.setArea(new AreaConhecimentoCnpq(1));
				if (av.getSubArea() == null || av.getSubArea().getId() == 0)
					av.setSubArea(new AreaConhecimentoCnpq(1));
				
			} else if (obj instanceof Livro){
				Livro l = (Livro) obj;
				if(l.getTipoParticipacao() == null)
					l.setTipoParticipacao(new TipoParticipacao());
			} else if (obj instanceof Banca){
				Banca b = (Banca) obj;
				if (b.getArea() == null || b.getArea().getId() == 0)
					b.setArea(new AreaConhecimentoCnpq(1));
				if (b.getSubArea() == null || b.getSubArea().getId() == 0)
					b.setSubArea(new AreaConhecimentoCnpq(1));
				if(b.getTipoBanca() != null && b.getTipoBanca().getId() == TipoBanca.CURSO){
					if(b.getNaturezaExame() == null)
						b.setNaturezaExame(new NaturezaExame());
				} else {
					if(b.getCategoriaFuncional() == null)
						b.setCategoriaFuncional(new CategoriaFuncional());
				}
			} else if (obj instanceof VisitaCientifica){
				VisitaCientifica v = (VisitaCientifica) obj;
				if (v.getAmbito() == null || v.getAmbito().getId() == 0)
					v.setAmbito(new TipoRegiao());
				if (v.getArea() == null || v.getArea().getId() == 0)
					v.setArea(new AreaConhecimentoCnpq(1));
				if (v.getSubArea() == null || v.getSubArea().getId() == 0)
					v.setSubArea(new AreaConhecimentoCnpq(1));
				if (v.getIes() == null || v.getIes().getId() == 0)
					v.setIes(new InstituicoesEnsino());
			} else if (obj instanceof ProducaoArtisticaLiterariaVisual){
				ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) obj;
				if (p.getArea() == null || p.getArea().getId() == 0)
					p.setArea(new AreaConhecimentoCnpq(1));
				if (p.getSubArea() == null || p.getSubArea().getId() == 0)
					p.setSubArea(new AreaConhecimentoCnpq(1));
				if (p.getTipoArtistico() == null || p.getTipoArtistico().getId() == 0)
					p.setTipoArtistico(new TipoArtistico());
				if (p.getSubTipoArtistico() == null || p.getSubTipoArtistico().getId() == 0)
					p.setSubTipoArtistico(new SubTipoArtistico());
			} else if (obj instanceof ProducaoTecnologica) {
				ProducaoTecnologica p = (ProducaoTecnologica) obj;
				if (p.getTipoProducaoTecnologica() == null)
					p.setTipoProducaoTecnologica(new TipoProducaoTecnologica());
				if (p.getTipoRegiao() == null) 					
					p.setTipoRegiao(new TipoRegiao());
				if (p.getTipoParticipacao() == null)
					p.setTipoParticipacao(new TipoParticipacao());
				if (p.getArea() == null) 
					p.setArea(new AreaConhecimentoCnpq());
				if (p.getSubArea() == null) 
					p.setSubArea(new AreaConhecimentoCnpq());
			}		
			
			changeArea();
			return retorno;
		}

	}
	
	/**
	 * Este m�todo foi sobreescrito a fim de permitir um redirecionamento
	 * diferente do padr�o (a ser definido pela vari�vel forwardAlterar) caso
	 * este seja o comportamento desejado.
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException{
		String retorno = super.cadastrar();
		
		if (forwardAlterar != null && !hasErrors())
			redirectJSF(forwardAlterar);
		
		return retorno;
	}

	/**
	 * Redireciona para tela de aviso de bloqueio.
	 * @return
	 */
	public String redirectBloqueio() {
		return forward(BLOQUEIO);
	}

	public boolean isForwardRemover() {
		return this.forwardRemover;
	}

	public void setForwardRemover(boolean forwardRemover) {
		this.forwardRemover = forwardRemover;
	}

	public Map<Servidor, Collection<Producao>> getMapaProducoes() {
		return this.mapaProducoes;
	}

	public void setMapaProducoes(Map<Servidor, Collection<Producao>> mapaProducoes) {
		this.mapaProducoes = mapaProducoes;
	}

	/**
	 * Limpa a lista de produ��es utilizada na visualiza��o.
	 *
	 * @return
	 */
	public String getDropList() {
		allProducao = null;
		return "";
	}

	public Collection<SelectItem> getEspecialidadeLatoSensu() {
		return especialidadeLatoSensu;
	}

	public void setEspecialidadeLatoSensu(
			Collection<SelectItem> especialidadeLatoSensu) {
		this.especialidadeLatoSensu = especialidadeLatoSensu;
	}
	
	/**
	 * Retorna a string contendo o tamanho m�ximo permitido para envio de arquivo.
	 * Utilizado para visualiza��o nos formul�rios.
	 * @return
	 */
	public String getTamanhoMaximoArquivo(){
		return ParametroHelper.getInstance().getParametro( ConstantesParametro.TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO );
	}

}