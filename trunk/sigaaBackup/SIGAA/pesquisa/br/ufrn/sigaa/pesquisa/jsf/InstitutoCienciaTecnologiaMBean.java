/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/08/2009
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.InstitutoCienciaTecnologiaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pesquisa.dominio.InstitutoCienciaTecnologia;
import br.ufrn.sigaa.pesquisa.dominio.MembroInstitutoCienciaTecnologia;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**
 * Mbean Institutos Nacionais de Ciência e Tecnologia
 * @author Geyson Karlos
 */

@Component("institutoCienciaTecnologia")
@Scope("request")
public class InstitutoCienciaTecnologiaMBean extends SigaaAbstractController<InstitutoCienciaTecnologia> {
	
	private MembroInstitutoCienciaTecnologia membroEquipe = new MembroInstitutoCienciaTecnologia();
	private  List<MembroInstitutoCienciaTecnologia> membroEquipeRemovido = new ArrayList<MembroInstitutoCienciaTecnologia>();
	private Servidor docente = new Servidor();
	private Servidor servidor = new Servidor();
	private Discente discente = new Discente();
	
	/**
	 * Construtor padrão
	 */
	public InstitutoCienciaTecnologiaMBean() {
		clear();
	}
	
	@Override
	public String getFormPage() {
		return "/pesquisa/InstitutoCienciaTecnologia/form.jsf";
	}

	@Override
	public String getListPage() {
		return "/pesquisa/InstitutoCienciaTecnologia/lista.jsf";
	}
	
	@Override
	public String getViewPage() {
		return "/pesquisa/InstitutoCienciaTecnologia/view.jsf";
	}
	
	/**
	 * Filtros de buscas.
	 */
	private boolean filtroUnidadeFederativa;
	private boolean filtroCoordenador;
	private boolean alterar = false;

	/**
	 * Realiza o cadastro de um Instituto de Ciência Tecnologia
	  * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/pesquisa/InstitutoCienciaTecnologia/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clear();
		prepareMovimento(SigaaListaComando.CADASTRAR_INSTITUTO_CIENCIA_TECNOLOGIA);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	/**
	 * Prepara o movimento de remoção.
	  * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Não invocado por JSP</li>
	 * </ul>
	 */
	@Override
	public void beforeRemover() throws DAOException {
		setId();
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inicia novo objeto do tipo InstitutoCienciaTecnologia
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li><Não invocado por JSP.</li>
	 * </ul>
	 */
	private void clear() {
		obj = new InstitutoCienciaTecnologia();	
		obj.setCoordenador(new Servidor());
		obj.setUnidadeFederativa(new UnidadeFederativa());
		alterar = false;
		setMembroEquipe(new MembroInstitutoCienciaTecnologia());
	}
	
	/**
	 * Popula o objeto e encaminha para a página de visualização
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>/sigaa.war/pesquisa/InstitutoCienciaTecnologia/view.jsp</li>
	 *     <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/InstitutoCienciaTecnologia/lista.jsp</li></ul>
	 * @return
	 * @throws ArqException
	 * 
	 * 
	 */
	public String view() throws ArqException {
		setId();
		PersistDB obj = this.obj;

		this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), InstitutoCienciaTecnologia.class);
		
		return forward(getViewPage());
	}
	
	/**
	 * retorna todas as unidades federativas com id e descrição para preenchimento do combobox
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllUnidadesFederativas() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(UnidadeFederativa.class), "id", "descricao");
	}
	
	/**
	 * Redireciona para página de listagem após cadastro.
	  * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> Não invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	/**
	 * Método responsável por Persistir Instituto de Ciência e Tecnologia e seus Membros.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/pesquisa/InstitutoCienciaTecnologia/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String persistir() throws ArqException {
		
		PersistDB ob = obj;
		
		Validatable validavel = null;
		if (ob instanceof Validatable) {
			validavel = obj;
		}
		
		if (ob instanceof Validatable && validavel != null) {
			erros = new ListaMensagens();
			ListaMensagens lista = validavel.validate();

			if (lista != null && !lista.isEmpty()) {
				erros.addAll(lista.getMensagens());
			}
		}
		
		if (!hasErrors()) {
		
		MovimentoCadastro mov = new MovimentoCadastro();
		if(obj.getId() > 0)
			mov.setCodMovimento(SigaaListaComando.ALTERAR_INSTITUTO_CIENCIA_TECNOLOGIA);
		else
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_INSTITUTO_CIENCIA_TECNOLOGIA);
		
		mov.setObjMovimentado(obj);
		mov.setColObjMovimentado(membroEquipeRemovido);
		try {
			execute(mov);
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		} 
		addMensagem(OPERACAO_SUCESSO);
		return forward(getListPage());
		}
		else
			return null;
	}

	/**
	 * Inativa Instituto de Ciência e Tecnologia.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/pesquisa/InstitutoCienciaTecnologia/list.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {

		beforeRemover();
		int id = getParameterInt("id");
		InstitutoCienciaTecnologia instituto = getGenericDAO().findByPrimaryKey( id, InstitutoCienciaTecnologia.class);
		PersistDB obj = instituto;
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);

		if (instituto == null || instituto.getId() == 0 || !instituto.isAtivo() ) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		} else {

			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return forward(getFormPage());
			} 

			setResultadosBusca(null);
			afterRemover();

			String forward = forwardRemover();
			if (forward == null) {
				return redirectJSF(getListPage());
			} else {
				return redirectJSF(forward);
			}

		}
	}
	
	/**
	 * Retorna uma lista de Institutos de Ciência e Tecnologia por unidade federativa.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP.</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String buscarUnidadeFederativa() throws DAOException, ArqException {
		
		InstitutoCienciaTecnologiaDao dao = getDAO(InstitutoCienciaTecnologiaDao.class);
		setResultadosBusca(null);
		UnidadeFederativa unidade = new UnidadeFederativa();
		ListaMensagens erros = new ListaMensagens();
		
		unidade = new UnidadeFederativa(obj.getUnidadeFederativa().getId());
		if(obj.getUnidadeFederativa().getId() == 0)
			erros.addErro("Informe Unidade Federativa.");
		if( erros.isEmpty() ) {
			Collection<InstitutoCienciaTecnologia> lista = dao.findByUnidadeFederativa(unidade);
			if (!lista.isEmpty()) {
				setResultadosBusca(lista);
			} else {
				addMessage("Nenhum Instituto de Ciência e Tecnologia foi encontrada de acordo com os critérios de busca utilizados.", TipoMensagemUFRN.WARNING);
			}
		} else
			addMensagens(erros);


		return forward(getListPage());
		
	}
	
	/**
	 * Retorna uma lista de Institutos de Ciência e Tecnologia por coordenador.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP.</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String buscarCoordenador() throws DAOException, ArqException {
		
		InstitutoCienciaTecnologiaDao dao = getDAO(InstitutoCienciaTecnologiaDao.class);
		setResultadosBusca(null);
		Servidor servidor = new Servidor();
		ListaMensagens erros = new ListaMensagens();
		
	    servidor = new Servidor(obj.getCoordenador().getId());

	    if(obj.getCoordenador().getId() == 0)
	    	erros.addErro("Informe o Coordenador.");
	    
		if( erros.isEmpty() ) {
			Collection<InstitutoCienciaTecnologia> lista = dao.findByCoordenador(servidor);
			if (!lista.isEmpty()) {
				setResultadosBusca(lista);
			} else {
				addMessage("Nenhum Instituto de Ciência e Tecnologia foi encontrada de acordo com os critérios de busca utilizados.", TipoMensagemUFRN.WARNING);
			}
		} else
			addMensagens(erros);


		return forward(getListPage());
		
	}
	/**
	 * Retorna uma lista de Institutos de Ciência e Tecnologia por unidade federativa e coordenador.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP.</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String buscarCoordenadorUnidadeFederativa() throws DAOException, ArqException {
		
		InstitutoCienciaTecnologiaDao dao = getDAO(InstitutoCienciaTecnologiaDao.class);
		setResultadosBusca(null);
		Servidor servidor = new Servidor();
		UnidadeFederativa unidade = new UnidadeFederativa();
		ListaMensagens erros = new ListaMensagens();
		
	    servidor = new Servidor(obj.getCoordenador().getId());
	    unidade = new UnidadeFederativa(obj.getUnidadeFederativa().getId());

		if( obj.getCoordenador().getId() > 0 &&  obj.getUnidadeFederativa().getId() > 0 ) {
			Collection<InstitutoCienciaTecnologia> lista = dao.findByCoordenadorUnidadeFederativa(servidor, unidade);
			if (!lista.isEmpty()) {
				setResultadosBusca(lista);
			} else {
				addMessage("Nenhum Instituto de Ciência e Tecnologia foi encontrada de acordo com os critérios de busca utilizados.", TipoMensagemUFRN.WARNING);
			}
		} else
			addMensagens(erros);


		return forward(getListPage());
		
	}
	
	/**
	 * Realiza busca de Institutos de Ciência e Tecnologia de acordo com filtros.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/pesquisa/InstitutoCienciaTecnologia/lista.jsp</li>
	 * </ul>
	 * @throws DAOException
	 * @throws ArqException
	 */
	public void busca() throws DAOException, ArqException {
		if(filtroUnidadeFederativa){
			buscarUnidadeFederativa();
		}
		if(filtroCoordenador){
			buscarCoordenador();
		}
		if(filtroUnidadeFederativa && filtroCoordenador)
			buscarCoordenadorUnidadeFederativa();
			
	}
	
	/**
	 * Retorna todos Institutos de Ciência e Tecnologia da base da dados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/pesquisa/InstitutoCienciaTecnologia/lista.jsp</li>
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public Collection<InstitutoCienciaTecnologia> getAllPaginado() throws ArqException {
		if(getResultadosBusca()!= null){
			return getResultadosBusca();
		}
		if (getAtributoOrdenacao() == null) {
			return (Collection<InstitutoCienciaTecnologia>) getGenericDAO().findAllAtivos(obj.getClass(), "nome");
		} else {
			return (Collection<InstitutoCienciaTecnologia>) getGenericDAO().findAllAtivos(obj.getClass(), getAtributoOrdenacao());
		}
	}
	
	@Override
	public String cancelar() {
		if ( obj.getId() > 0 )
			return getListPage();
		else
			return super.cancelar();
	}
	
	/**
	 * Prepara objeto para adicionar um membro.
	 */
	private void popularDadosMembroProjeto() {
		docente = new Servidor();
		discente = new Discente();
		servidor = new Servidor();
	
		membroEquipe = new MembroInstitutoCienciaTecnologia();
		membroEquipe.setInstitutoCienciaTecnologia(this.obj);
	}
	
	/**
	 * Popula o formulário com os dados do membro.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/pesquisa/InstitutoCienciaTecnologia/form.jsp</li>
	 * </ul>
	 * @param event
	 * @throws DAOException
	 */
	public void popularMembroEquipe(ActionEvent event) throws DAOException {
		int id = getParameterInt("id");
		
		
		for (MembroInstitutoCienciaTecnologia membro : obj.getEquipesInstitutoCienciaTecnologia()) {
			
			if (membro.getId() == id) {
				membroEquipe = membro;
				break;
			}
			
		}
		
		if (membroEquipe == null)
			return;
		
		if( getGenericDAO().findByPrimaryKey(obj.getId(), InstitutoCienciaTecnologia.class) != null){
			membroEquipe = getGenericDAO().findByPrimaryKey(membroEquipe.getId(), MembroInstitutoCienciaTecnologia.class);
		}

		if (membroEquipe.isDocenteUFRN()) {
			docente = membroEquipe.getServidor();
			getCurrentSession().setAttribute("aba", "membro-docente");
			getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.DOCENTE);
		} else if (membroEquipe.isServidorUFRN()) {
			servidor = membroEquipe.getServidor();
			getCurrentSession().setAttribute("aba", "membro-servidor");
			getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.SERVIDOR);
		} else if (membroEquipe.isDiscenteUFRN()) {
			discente = membroEquipe.getDiscente();
			getCurrentSession().setAttribute("aba", "membro-discente");
			getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.DISCENTE);
		} 

		alterar = true;
		
	}
	
	/**
	 * Remove Membro de um Instituto de Ciência e Tecnologia.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/pesquisa/InstitutoCienciaTecnologia/form.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void removeMembroEquipe(ActionEvent evt) throws DAOException {

		int id = getParameterInt("idMembro", 0);
		MembroInstitutoCienciaTecnologia membro = new MembroInstitutoCienciaTecnologia(id);

		// Verifica se o membro existe no banco
		if (id > 0) {
			membro = getGenericDAO().findByPrimaryKey(membro.getId(), MembroInstitutoCienciaTecnologia.class);
			membroEquipeRemovido.add(membro);		
		}else{
			for (MembroInstitutoCienciaTecnologia mem : obj.getEquipesInstitutoCienciaTecnologia()) {
				if(mem.getId() == id){
					obj.getEquipesInstitutoCienciaTecnologia().remove(mem);
				}
			}
		}
			
		
		obj.getEquipesInstitutoCienciaTecnologia().remove(membro);
		popularDadosMembroProjeto();
		addMensagem(OPERACAO_SUCESSO);
		
		return;
	}
	
	/**
	 * Adiciona um membro a equipe de um Instituto de Ciência e Tecnologia.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>JSP:/sigaa.war/pesquisa/InstitutoCienciaTecnologia/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String adicionarMembroEquipe() throws ArqException, RemoteException {

		GenericDAO dao = getGenericDAO();
		ListaMensagens erros = new ListaMensagens();

		// qual o tipo de membro será adicionado (default = docente)
		int categoriaMembro = getParameterInt("categoriaMembro",CategoriaMembro.DOCENTE);
		
		membroEquipe.setCategoriaMembro(dao.findByPrimaryKey(categoriaMembro, CategoriaMembro.class));

		// método para adicionar discente
		if (CategoriaMembro.DISCENTE == categoriaMembro) {
			return adicionaDiscente();
		}
		membroEquipe.setDiscente(null);
		// Validação
		if(categoriaMembro == CategoriaMembro.DOCENTE)
			ValidatorUtil.validateRequired(docente, "Docente", erros);
		else if(categoriaMembro == CategoriaMembro.SERVIDOR)
			ValidatorUtil.validateRequired(servidor, "Servidor", erros);
		
		ValidatorUtil.validateRequired(membroEquipe.getDataInicio(), "Data Início", erros);
		ValidatorUtil.validaInicioFim(membroEquipe.getDataInicio(), membroEquipe.getDataFim(), "Data Fim", erros);
		
		
		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}

		

		if (CategoriaMembro.SERVIDOR == categoriaMembro) {
			membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(
					servidor.getId(), Servidor.class));
			// evitando erro de lazy na validação
			membroEquipe.getServidor().getEscolaridade().getId();
			getCurrentSession().setAttribute("aba", "membro-servidor");
		}

		if (CategoriaMembro.DOCENTE == categoriaMembro) {
			membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(
					docente.getId(), Servidor.class));
			// evitando erro de lazy na validação
			membroEquipe.getServidor().getEscolaridade().getId();
			getCurrentSession().setAttribute("aba", "membro-docente");
		}

		if ((CategoriaMembro.SERVIDOR == categoriaMembro)
				|| (CategoriaMembro.DOCENTE == categoriaMembro)) {
			membroEquipe.setPessoa(membroEquipe.getServidor().getPessoa());
		}


		// informa qual o tipo de membro de equipe será cadastrado
		getCurrentSession().setAttribute("categoriaAtual", categoriaMembro);

		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}


		if (!obj.getEquipesInstitutoCienciaTecnologia().contains(membroEquipe)) {

			obj.addEquipeInstitutoCienciaTecnologia(membroEquipe);

		} else {
			if (alterar) {
				obj.getEquipesInstitutoCienciaTecnologia().remove(membroEquipe);
				obj.addEquipeInstitutoCienciaTecnologia(membroEquipe);			
			}else
				addMensagemErro(membroEquipe.getCategoriaMembro().getDescricao() +" já inserido");
		}

		// limpa os dados
		popularDadosMembroProjeto();
		addMensagemWarning("Após adicionar/alterar os membros do grupo e realizar todas as mudanças necessárias, caso deseje finalizar a operação, é necessário clicar no botão '" + getConfirmButton() + "' para confirmá-la. O botão está localizado no fim da página.");
		alterar = false;
		return null;
	}
	
	/**
	 * Cancela a operação de alteração de um membro equipe.
	  * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li> /sigaa.war/pesquisa/InstitutoCienciaTecnologia/form.jsp</li>
	 * </ul>
	 * @param event
	 */
	public void cancelarAlterarMembroEquipe(ActionEvent event) {
		popularDadosMembroProjeto();
		alterar = false;
	}
	
	/**
	 * Adiciona um membro do tipo Discente a equipe de um Instituto de Ciência e Tecnologia.
	  * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> Não invocado por JSP</li>
	 * </ul>
	 * @return
	 */
	public String adicionaDiscente() {

		ListaMensagens erros = new ListaMensagens();

		try {

			getCurrentSession().setAttribute("aba", "membro-discente");

			ValidatorUtil.validateRequired(discente, "Discente", erros);
			ValidatorUtil.validateRequired(membroEquipe.getDataInicio(), "Data Início", erros);			
			ValidatorUtil.validaInicioFim(membroEquipe.getDataInicio(), membroEquipe.getDataFim(), "Data Fim", erros);

			if (erros.isEmpty()) {
				if ( obj.getPeriodoInicio() != null && membroEquipe.getDataInicio() != null 
							&& obj.getPeriodoInicio().after(membroEquipe.getDataInicio()) )
					erros.addErro("Data Início: Deve ser posterior ao Período Inicial.");
				
				if ( membroEquipe.getDataFim() != null &&  obj.getPeriodoFim() != null 
							&& obj.getPeriodoFim().before(membroEquipe.getDataFim()) )
					erros.addErro("Data Fim: Deve ser posterior ao Período Final.");
			}

			if (!erros.isEmpty()) {
				addMensagens(erros);
				return null;
			}
			
			discente = getGenericDAO().findByPrimaryKey(discente.getId(),
					Discente.class);

			membroEquipe.setDiscente(discente);
			membroEquipe.setPessoa(discente.getPessoa());
			membroEquipe.setCategoriaMembro(getGenericDAO().findByPrimaryKey(CategoriaMembro.DISCENTE, CategoriaMembro.class));
			
			// validação
			if (!erros.isEmpty()) {
				membroEquipe = new MembroInstitutoCienciaTecnologia();
				addMensagens(erros);
				return null;
			}

			if (!obj.getEquipesInstitutoCienciaTecnologia().contains(membroEquipe)) {

				// limpando outros atributos para não dar
				// TransientObjectException
				membroEquipe.setServidor(null);

				obj.addEquipeInstitutoCienciaTecnologia(membroEquipe);

			} else {
				if (alterar) {
					obj.getEquipesInstitutoCienciaTecnologia().remove(membroEquipe);
					obj.addEquipeInstitutoCienciaTecnologia(membroEquipe);
				} else {
					addMensagemErro("Discente já inserido na atividade");
				}
			}

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		// limpa os dados
		popularDadosMembroProjeto();

		return null;
	}
	
	/**
	 * Tem como função a atualização de um Instituto Ciência Tecnologia.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/InstitutoCienciaTecnologia/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		clear();
		popularDadosMembroProjeto();
		
		int id = getParameterInt("id",0);
		InstitutoCienciaTecnologia instituto = getGenericDAO().findByPrimaryKey(id, InstitutoCienciaTecnologia.class);
		if(instituto == null){
			addMensagemErro("Registro não encontrado");
			return null;
		}
		this.obj = instituto; 
		prepareMovimento(SigaaListaComando.ALTERAR_INSTITUTO_CIENCIA_TECNOLOGIA);
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}

	public void setFiltroUnidadeFederativa(boolean filtroUnidadeFederativa) {
		this.filtroUnidadeFederativa = filtroUnidadeFederativa;
	}

	public boolean isFiltroUnidadeFederativa() {
		return filtroUnidadeFederativa;
	}

	public void setFiltroCoordenador(boolean filtroCoordenador) {
		this.filtroCoordenador = filtroCoordenador;
	}

	public boolean isFiltroCoordenador() {
		return filtroCoordenador;
	}

	public void setMembroEquipe(MembroInstitutoCienciaTecnologia membroEquipe) {
		this.membroEquipe = membroEquipe;
	}

	public MembroInstitutoCienciaTecnologia getMembroEquipe() {
		return membroEquipe;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public boolean isAlterar() {
		return alterar;
	}

}