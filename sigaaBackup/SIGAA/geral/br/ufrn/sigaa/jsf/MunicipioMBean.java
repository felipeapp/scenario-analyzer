/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * MBean responsável pela busca de {@link Municipio}.
 * 
 * @author Bernardo
 * @author Gleydson
 *
 */
public class MunicipioMBean extends SigaaAbstractController<br.ufrn.sigaa.pessoa.dominio.Municipio> {
	
	/** Filtros utilizados na busca de um município. */
	private boolean filtroNome, filtroCodigo, filtroUF;
	
	/** Lista de municípios encontrados. */
	private Collection<Municipio> lista;
	
	private boolean popular;

	/**
	 * Construtor padrão.
	 */
	public MunicipioMBean() {
		initObj();
	}

	/**
	 * Inicia o objeto trabalhado.
	 */
	private void initObj() {
		obj = new Municipio();
		obj.setUnidadeFederativa(new UnidadeFederativa());
		popular = true;
	}

	/**
	 * Retorna uma coleção de {@link SelectItem} referentes aos municipios ativos.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAllAtivosCombo() {
		return getAllAtivo(Municipio.class, "id", "nomeUF");
	}

	/**
	 * Define o atributo de ordenação dos municípios.
	 */
	@Override
	public String getAtributoOrdenacao() {
		return "nome";
	}

	/**
	 * Retorna uma coleção de {@link SelectItem} referentes aos municípios cuja unidade federativa seja o RN.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMunicipiosRN() throws DAOException {
		MunicipioDao dao = getDAO(MunicipioDao.class);
		Collection<Municipio> municipios = dao.findByUF(br.ufrn.comum.dominio.UnidadeFederativa.RN);
		List<SelectItem> itens = toSelectItems(municipios, "id", "nome");
		itens.add(0, new SelectItem(br.ufrn.comum.dominio.Municipio.NATAL, "Natal"));
		return itens;
	}
	
	/**
	 * Retorna uma coleção de {@link SelectItem} referentes aos municípios da unidade federativa definida.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMunicipiosByUF() throws DAOException {
		
		Collection<Municipio> municipios = null;
		
		MunicipioDao dao = getDAO(MunicipioDao.class);
		municipios = dao.findByUF(obj.getUnidadeFederativa().getId());
		
		return toSelectItems(municipios, "id", "nome");
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}

	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}

	public boolean isSubSistemaGraduacao() {
		return getSubSistema().equals(SigaaSubsistemas.GRADUACAO);
	}
	
	/**
	 * Busca os municípios de acordo com os dados definidos.
	 * <br /><br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/cadastro/Municipio/lista.jsp</li>
	 * <li>/sigaa.war/graduacao/menus/consultas.jsp</li>
	 * </ul>
	 */
	public String buscar() throws DAOException {
		setTamanhoPagina(20);
		getPaginacao().setPaginaAtual(0);
		
		validarDadosBusca();
		
		if(hasErrors()) {
			lista = null;
			popular = false;
			return null;
		}
		
		popular = true;
		
		popularLista();
		
		if(isEmpty(lista))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return forward(getListPage());
	}

	/**
	 * Popula a lista de municípios de acordo com os dados definidos.
	 * 
	 * @throws DAOException
	 */
	private void popularLista() throws DAOException {
		if(popular) {
			MunicipioDao dao = getDAO(MunicipioDao.class);
			
			if(!filtroCodigo && !filtroNome && !filtroUF)
				lista = dao.findAll(Municipio.class, getPaginacao(), getAtributoOrdenacao(), "asc");
			else
				lista = dao.findByCodigoNomeUF(obj.getCodigo(), obj.getNome(), obj.getUnidadeFederativa(), getPaginacao());
		}
	}

	/**
	 * Valida os dados definidos para a busca.
	 */
	private void validarDadosBusca() {
		if(filtroCodigo && isEmpty(obj.getCodigo()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Código");
		if(filtroNome && isEmpty(obj.getNome()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
		if(filtroUF && isEmpty(obj.getUnidadeFederativa()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Federativa");
		
		if(!filtroCodigo)
			obj.setCodigo(null);
		if(!filtroNome)
			obj.setNome(null);
		if(!filtroUF)
			obj.getUnidadeFederativa().setId(0);
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		obj.setAtivo(true);
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<Municipio> mesmoMunicipio = dao.findByExactField(Municipio.class, 
				"unidadeFederativa", obj.getUnidadeFederativa().getId());
		for (Municipio m : mesmoMunicipio) {
			if (m.getId() == obj.getId()) {
				return super.cadastrar();
			} if(m.getNome().equals(obj.getNome())&& (m.getCodigo().trim()).equals(obj.getCodigo())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Município");
				return null;
			}
		}
		return super.cadastrar();
	}

	@Override
	public String getFormPage() {
		return "/administracao/cadastro/Municipio/form.jsf";
	}
	@Override
	public String getListPage() {
		return "/administracao/cadastro/Municipio/lista.jsf";
	}
	
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, Municipio.class);
		if (obj == null) {
			addMensagemErro(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		return super.remover();
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public boolean isFiltroUF() {
		return filtroUF;
	}

	public void setFiltroUF(boolean filtroUF) {
		this.filtroUF = filtroUF;
	}

	/**
	 * Retorna a lista de municípios, levando em consideração a paginação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Municipio> getLista() throws DAOException {
		popularLista();
		return lista;
	}

	public void setLista(Collection<Municipio> lista) {
		this.lista = lista;
	}
	
}