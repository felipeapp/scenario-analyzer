/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Controlador abstrato que os controladores do prodocente devem herdar. Contém
 * funções comuns entre todos os controladores deste subsistema.
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
	
	/** Define se a ação é de validação da produção intelectual.  */
	protected Boolean validar = false;

	/** Define se a ação é de visualização dos dados da produção intelectual. */
	protected Boolean visualizar = false;

	/** Define a árvore de ques erá visualizada na view.  */
	protected TreeNode arvore = null;

	/** Define o mapa das produções intelectuais do docente. */
	protected Map<Servidor, Collection<Producao>> mapaProducoes;

	/** Define o ano de referência da produção intelectual do docente. */
	private int anoReferencia;

	/** Define o título na busca da produção do intelectual do docente */
	private String titulo = "";

	/** Define a se o cadastro é da categoria. */
	private boolean categoria = false;

	/** Define o docente que está acessando a produção intelectual. */
	protected Servidor docente = new Servidor();

	/** Define que a produção foi validada. */
	private int validados;

	/** Define se a operação é para carregar as produções do docente. */
	private boolean carregarMinhasProducoes = true;

	/** Define a  subárea. */
	protected Collection<SelectItem> subArea = new ArrayList<SelectItem>();

	/** Define as especialidades do docente. */
	private Collection<SelectItem> especialidades = new ArrayList<SelectItem>();
	
	/** Define as especialidades exclusivas do nível de ensino de lato sensu. */
	private Collection<SelectItem> especialidadeLatoSensu = new ArrayList<SelectItem>();

	/** Define a categoria funcional do docente. */
	private Collection<SelectItem> categoriaFuncional = new ArrayList<SelectItem>();

	/** Define o subtipo artístico **/
	private Collection<SelectItem> subTipoArtistico = new ArrayList<SelectItem>();

	/** Coleção com todos os anos cadastrados no sistema */
	private List<SelectItem> anos;

	/** Atributos usados para upload de arquivo */
	private UploadedFile arquivo;

	/** Define a produção selecionada. */
	private Producao dados;

	/** Define o redirecionamento após a remoção da produção. */
	private boolean forwardRemover = true;
	
	/** Variável utilizada para definir um redirecionamento específico (e diferente do padrão) após o usuário clicar em [Alterar]. */
	protected String forwardAlterar = null;
	
	/** Variável utilizada para definir um redirecionamento específico (e diferente do padrão) após o usuário clicar em [Cancelar]. */
	protected String forwardCancelar = null;

	/** Define todas as produções do docente. */
	@SuppressWarnings("unchecked")
	private Collection allProducao;

	/** Define o pagina atual na tela de listagem das produções. */
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
	 * Método que retorna o ano atual do calendário acadêmico.
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
	 * Retorna Trinta anos atrás a partir do ano atual, para preencher o comboBox
	 * de Ano de Referência
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
	 * Retorna todos os anos que tem publicados válidas
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
	 * Método que retorna o tipo de produção selecionado pelo docente. 
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
	 * Métod que retorna os tipos de produções para ser selecionado no formulário
	 * de cadastro da produção.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTipoProducaoCombo() throws DAOException {
		GenericDAO genericDAO = getGenericDAO();
		Collection<TipoProducao> all = genericDAO.findAll(TipoProducao.class, "descricao", "asc");
		
		return toSelectItems(all, "id", "descricao");
		
	}
	
	/**
	 * Para tratar mudança de área
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
	 * Método que retornar um coleção das áreas de conhecimentos para ser utilizado
	 * no formulário de cadastro das produções.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAreaConhecimentoCombo() throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		Collection<AreaConhecimentoCnpq> areas = dao.findAll(AreaConhecimentoCnpq.class, "nome", "asc");
		
		return toSelectItems(areas, "id", "nome");
	}
	
	/**
	 * Para tratar mudança da subárea
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
	 * Método que habilita a categoria no formulário.
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
	 * Para tratar mudança da subárea
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
	 * Retorna todos os docentes da unidade do usuário
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
	 * Retorna todos os docentes da unidade do usuÃ¡rio, exceto o servidor do
	 * usuário logado
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
	 * Método que retorna os chefes de departamento de acorod com o centro que está associado ao 
	 * usuári do docente.
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
	 * Método que retornar todas as produções do docente logado.
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

				// se entrar neste if é por que o método não foi reescrito no filho
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
	 * Método retorna a pagina atual na listagem das produções do docente.
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
	 * Método usado para ser reescrito no filho e fazer projeção para as consultas de listagem
	 *
	 * @return
	 */
	public Collection getDadosEspecificoDaProducao() throws DAOException {
		return null;
	}

	/**
	 * Método sobrescrito para mostrar as sub-areas das produções
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
	 * Operação para validar a produção de um professor pelo seu chefe do
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
			addMensagemErro("Escolha se a produção válida ou não");
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
			addMessage("Produção " + validado + " com sucesso",
					TipoMensagemUFRN.INFORMATION);
		} catch (Exception e) {
			notifyError(e);
		}
		return forward("/prodocente/producao/producoes.jsp");

	}

	/**
	 * Método que realiza o filtro das produções do docente.
	 * @return
	 * @throws DAOException
	 */
	public String filtrar() throws DAOException {
		mapaProducoes = null;
		return getCarregarArvoreProducoes();
	}

	/**
	 * Cria a árvore de produção de um servidor ou de todos servidores do
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
			
			// aqui são buscadas as produções da unidade de acordo com
			// preenchimento dos parâmetros de busca
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
	 * Métod que popula um mapa das produções do docente.
	 * @param producoes
	 */
	protected void popularMapaProducoes(Collection<? extends Producao> producoes) {
		// Criar mapa de servidores e suas produções
		mapaProducoes = new TreeMap<Servidor, Collection<Producao>>(new Comparator<Servidor>() {
			public int compare(Servidor o1, Servidor o2) {
				return o1.getPessoa().getNome().compareTo(o2.getPessoa().getNome());
			}
		});

		// Preencher mapa com as produções encontradas
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
	 * Verifica se os casos de uso de produção é iniciado a partir de um chefe de departamento
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
	 * Verifica se os casos de uso de produção é iniciado a partir de um diretor de centro
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
	 * Método que carrrega e redireciona para a tela dos dados
	 * de validação da produção.
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
				// sÃ³ o nome da classe
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
	 *  Método que indica a tela de visualização da produção do docente.
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
	 * Método que redireciona para tela de validação das produções.
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
				// sÃ³ o nome da classe
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
	 * Método que deve ser sobrescrito pelo classe filho
	 * setando o objeto.
	 * @param p
	 */
	public void popularObjeto(Producao p) {

	}

	/**
	 * Após do clique do enviar arquivo
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
			addMensagemErro("Produção não localizada.");
			return null;
		}
		
		if (dados.getServidor().getId() != getServidorUsuario().getId()) {
			addMensagemErro("Esta produção não está cadastrada para seu Usuário");
			return null;
		}
		return forward("/prodocente/producao/ArquivoProducao/arquivo.jsp");

	}

	/**
	 * Após do confirmar do enviar arquivo
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
	 * máximo definido no banco.
	 * @param arquivo
	 * @return
	 */
	public boolean isExcedeTamanhoMaximo(UploadedFile arquivo){ 
	
		Integer tamanhoMaximoMB = ParametroHelper.getInstance().
		getParametroInt( ConstantesParametro.TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO );
		//Converte a referência do parâmetro para o tamanho exato em bytes.
		Integer tamanhoMaximoBytes = tamanhoMaximoMB * 1024 * 1024;
	
		if ( arquivo.getSize() > tamanhoMaximoBytes ) {
			addMensagem( MensagensProdocente.TAMANHO_ARQUIVO_PRODUCAO_EXCEDE, tamanhoMaximoMB );
			return true;
		}
		
		return false;
	}
			
	/**
	 * Retorna o diretório base
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
	 * Método responsável em redirecionar o docente para determinadas telas de acordo
	 * com as condições descritas internamente.
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
	 * Mais informações {@link #anoReferencia} 
	 * @return
	 */
	public int getAnoReferencia() {
		return anoReferencia;
	}

	/**
	 * Mais informações {@link #anoReferencia} 
	 * @param anoReferencia
	 */
	public void setAnoReferencia(int anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	/**
	 * Mais informações {@link #docente} 
	 * @return
	 */
	public Servidor getDocente() {
		return docente;
	}

	/**
	 * Mais informações {@link #docente} 
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
	 * Mais informações {@link #titulo} 
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Mais informações {@link #titulo} 
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Mais informações {@link #subArea}
	 * @return
	 */
	public Collection<SelectItem> getSubArea() {
		return subArea;
	}

	/**
	 * Mais informações {@link #subArea}
	 * @param subArea
	 */
	public void setSubArea(Collection<SelectItem> subArea) {
		this.subArea = subArea;
	}

	/**
	 * Mais informações {@link #categoria}
	 * @return the categoria
	 */
	public boolean isCategoria() {
		return categoria;
	}

	/**
	 * Mais informações {@link #categoria}
	 * @param categoria         
	 */
	public void setCategoria(boolean categoria) {
		this.categoria = categoria;
	}

	/**
	 * Mais informações {@link #categoriaFuncional}
	 * @return the categoriaFuncional
	 */
	public Collection<SelectItem> getCategoriaFuncional() {
		return categoriaFuncional;
	}

	/**
	 * Mais informações {@link #categoriaFuncional}
	 * @param categoriaFuncional
	 */
	public void setCategoriaFuncional(Collection<SelectItem> categoriaFuncional) {
		this.categoriaFuncional = categoriaFuncional;
	}

	/**
	 * Ao remover as produções, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE.
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

			// Ao remover as produções, as mesmas não serão removidas da base de
			// dados, apenas o campo ativo será marcado como FALSE. Edson Anibal
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

		// Se for instância de Produção:
		if (obj instanceof Producao) {
			Producao producao = new Producao(obj.getId());
			mov.setObjMovimentado(producao); // como o método UpdateField usa
			// reflection , ele sempre
			// pegava a classe filha (mesmo
			// com o casting) por isso estou
			// passando a classe pai.
		} else {
			mov.setObjMovimentado(obj);
		}

		if (obj.getId() == 0) {
			addMensagemErro("Não há objeto informado para remoção");
			return null;
		} else {

			// Ao remover as produções, as mesmas não serão removidas da base de
			// dados, apenas o campo ativo será marcado como FALSE. Edson Anibal
			// (ambar@info.ufrn.br)
			mov.setCodMovimento(ArqListaComando.DESATIVAR);

			try {
				execute(mov, (HttpServletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

				addMessage("Operação realizada com sucesso!",
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
	 * ProducaoTecnologica, Precisam sobrescrever esse método para redirecionar
	 * para página correta.
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
	 * Método que redireciona o docente após perssionar o 
	 * botão cancelar na tela de envio de arquivos.
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
	 * Vai para tela para visualizar o relatório do prodocente
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
	 * Vai para tela para visualizar o relatório da GED
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
	 * Retorna os tipos de participações por tipo de produção
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
	 * Método que carrega as produções do docente para avaliação.
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
	 * Carrega as sub-areas da área cadastrada
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
	 * Método que carrega as subáreas após selecionar a {@link #atualizar()}
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#afterAtualizar() 
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		carregaSubAreas();
	}

	/**
	 * Método que reseta a listagem das produção após cadastrar uma produção.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#afterCadastrar() 
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();

		// Limpar lista de produções para que seja atualizada
		allProducao = null;
	}

	/**
	 * Método que reseta a listagem das produção após a remoção de uma produção.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#afterRemover() 
	 */
	@Override
	public void afterRemover()
	{
		super.afterRemover();
		allProducao = null; //<-- Garantindo que ao remover uma produção de um MBean em sessão, a página subsequente de listagem seja atualizada @author Edson Anibal (ambar@info.ufrn.br)
	}

	/**
	 * Método chamado antes do cadastrar, reimplementado pelo filho se ele
	 * desejar fazer algo antes de cadastrar
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {

		//Formações Acadêmicas Duplicadas (Verificar se já existe com o mesmo título, do mesmo Tipo e para o mesmo Servidor). @Author Edson Anibal (ambar@info.ufrn.br)
		PersistDB persistableObj = (PersistDB) obj;
		if ( persistableObj.getId() == 0 && obj instanceof Producao)
		{
			Producao producao = (Producao) obj;

			ProducaoDao dao = getDAO(ProducaoDao.class);
			dao.initialize(producao.getTipoProducao());
			if (dao.isProducaoMesmoAnoTituloTipo(producao))
				addMensagemErro("Já existe uma produção do tipo '"+producao.getTipoProducao().getDescricao()+"' cadastrada com o mesmo título, mesmo ano de referência e pertencente ao mesmo servidor.");
		}

		super.beforeCadastrarAndValidate();
	}
	//------//

	/**
	 * Reinicia o objeto após efetuar o cadastro de uma produção.
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
	 * Método que verifica se a produao está bloqueada.
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
	 * Método que prepara o formulário de atualização de acordo com o tipo, verificando também se 
	 * não existe bloqueio.
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
	 * Este método foi sobreescrito a fim de permitir um redirecionamento
	 * diferente do padrão (a ser definido pela variável forwardAlterar) caso
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
	 * Limpa a lista de produções utilizada na visualização.
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
	 * Retorna a string contendo o tamanho máximo permitido para envio de arquivo.
	 * Utilizado para visualização nos formulários.
	 * @return
	 */
	public String getTamanhoMaximoArquivo(){
		return ParametroHelper.getInstance().getParametro( ConstantesParametro.TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO );
	}

}