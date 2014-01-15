/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '04/10/2006'
 *
 */
package br.ufrn.sigaa.struts;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.dominio.TipoEsferaAdministrativa;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.form.PessoaForm;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.PessoaJuridica;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TipoRedeEnsino;

/**
 * Ações responsáveis por carregar o formulário de cadastro de dados pessoais,
 * validar o form com os dados submetidos e redirecionar a navegação para o
 * próximo passo (que pode ser dados de discente, ou outras entidades que
 * precisem ter os dados pessoais)
 *
 * @author Andre M Dantas
 *
 */
public class PessoaAction extends AbstractWizardAction {

	public static final String OPERACAO = "operacao";

	public static final String DADOS_PESSOAIS = "dadosPessoais";

	/** Constantes */
	public static final String ATUALIZAR = "Atualizar";

	public static final String REMOVER = "Remover";

	public static final String CADASTRAR = "Cadastrar";

	public static final String NEXT_VIEW = "nextView";

	private boolean buscar = true;
	
	/**
	 * Verifica se é pessoa física
	 * @param req
	 * @return
	 */
	public boolean isPF(PessoaForm form) {
		return (form.getPessoa().isPF());
	}

	/**
	 * Primeira ação, em que se popula os selects do formulário, e no caso de
	 * alterar um objeto do banco, carrega esse objeto no formulário
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		clearSession(request);
		getForm(form).clear();

		PessoaForm pessoaForm = (PessoaForm) form;
		// se o tipo da pessoa (jurídica ou física) for passado por parâmetro
		pessoaForm.init(mapping, request);

		if (buscar) {
			pessoaForm.setDefaultProps();
			pessoaForm.setCpf_cnpj(null);
		}
		
		// Popular dados a partir de uma inscrição em processo seletivo
		Pessoa dadosPessoais = (Pessoa) request.getSession().getAttribute("dadosPessoaisInscricao");
		
		GenericDAO dao = getGenericDAO(request);
		
		if (pessoaForm.getCpf_cnpj() != null) {
			if (pessoaForm.getCpf_cnpj().contains(".") || pessoaForm.getCpf_cnpj().contains("-")) {
				pessoaForm.setCpf_cnpj(pessoaForm.getCpf_cnpj().replace( "." , ""));
				pessoaForm.setCpf_cnpj(pessoaForm.getCpf_cnpj().replace( "-" , ""));
			}
			PessoaDao dao1 = new PessoaDao();
			
			dadosPessoais = dao1.findByCpf(Long.parseLong(pessoaForm.getCpf_cnpj()));
			if (dadosPessoais != null) 
				dadosPessoais = dao.findByPrimaryKey(dadosPessoais.getId(), Pessoa.class);
			else
				addMensagemErro("Usuário não encontrado.", request);
				pessoaForm.setDefaultProps();
				
			buscar = true;
			dao1.close();
		}else
			pessoaForm.getPessoa().setSexo(("M").charAt(0));
		
		if ( dadosPessoais != null ) {
			pessoaForm.setPessoa(dadosPessoais);
			formartarCamposForm(pessoaForm);
			prepararDadosPessoais(pessoaForm, dadosPessoais);
			
			if(request.getSession().getAttribute("dadosPessoaisInscricao") != null){
				request.getSession().removeAttribute("dadosPessoaisInscricao");
				request.getSession().setAttribute("discenteProcessoSeletivo", Boolean.TRUE);
			}
		}
		
		if (request.getSession().getAttribute(NEXT_VIEW) == null && request.getParameter(NEXT_VIEW) != null) {
			request.getSession().setAttribute(NEXT_VIEW, request.getParameter(NEXT_VIEW));
		}
		popularSelects(request, pessoaForm);
		
		if(!buscar){
			if(request.getSession().getAttribute("discenteAntigo") == null && request.getParameter("discenteAntigo") != null){
				request.getSession().setAttribute("discenteAntigo", request.getParameter("discenteAntigo"));
			} else if(request.getSession().getAttribute("discenteAntigo") != null && request.getParameter("discenteAntigo") == null){
				request.getSession().removeAttribute("discenteAntigo");
			}
		}

		String operacao = null;
		Comando comando = null;
		if (request.getParameter("pessoaId") == null) {
			operacao = CADASTRAR;
			comando =SigaaListaComando.CADASTRAR_PESSOA;
			if (isPF(pessoaForm))
				pessoaForm.setDefaultProps();
		} else {
			operacao = ATUALIZAR;
			comando = SigaaListaComando.ALTERAR_PESSOA;
			
			
			try {
				addSession("proximoId", request.getParameter("proximoId"),
						request);
				addSession("cursoId", request.getParameter("cursoId"), request);
				int pessoaId = RequestUtils.getIntParameter(request, "pessoaId");
				if (isPF(pessoaForm)) {
					Pessoa p = dao.findByPrimaryKey(pessoaId, Pessoa.class);
					pessoaForm.setPessoa(p);
					prepararDadosPessoais(pessoaForm, p);
				} else {
					PessoaJuridica pj = dao.findByPrimaryKey(pessoaId, PessoaJuridica.class);
					pessoaForm.setPessoa(pj.getPessoa());
					pessoaForm.setPessoaJuridica(pj);
				}
				formartarCamposForm(pessoaForm);
			} finally {
				dao.close();
			}
		}
		addStep(request, "Dados Pessoais", "/pessoa/wizard", DADOS_PESSOAIS);
		addSession(OPERACAO, operacao, request);
		prepareMovimento(comando, request);
		
		
		if (request.getParameter("redirecionarDiscente") != null) 
			return submeterDados(mapping, pessoaForm, request, response, false);
		else
			return mapping.findForward(DADOS_PESSOAIS);
	}

	private void prepararDadosPessoais(PessoaForm pessoaForm, Pessoa p) {
		if ( p.getMunicipio() != null) {
			p.getMunicipio().getNome();
			p.setMunicipio(new Municipio(p.getMunicipio().getId()));
		}
		if (p.getEnderecoContato() != null && p.getEnderecoContato().getMunicipio() != null)  {
			p.getEnderecoContato().setMunicipio(new Municipio(p.getEnderecoContato().getMunicipio().getId()));
		}
		if (p.getEnderecoContato() != null && p.getEnderecoContato().getMunicipio() == null){
			p.getEnderecoContato().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
		}
		if (p.getEnderecoContato() != null && p.getEnderecoContato().getUnidadeFederativa() == null){
			p.getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		}
		if (p.getEnderecoContato() != null && p.getEnderecoContato().getTipoLogradouro() == null)
			p.getEnderecoContato().setTipoLogradouro(new TipoLogradouro(TipoLogradouro.RUA));
		pessoaForm.setDefaultProps();
	}

	/**
	 * Formata os campos especiais do formulário
	 *
	 * @param form
	 */
	public void formartarCamposForm(PessoaForm form) {
		Pessoa p = form.getPessoa();
		// campos comuns
		if (p.getCpf_cnpj() != null)
			form.setCpf_cnpj(Formatador.getInstance().formatarCPF_CNPJ(p.getCpf_cnpj()));
		// campos específicos
		if (isPF(form)) {
			if( p.getDataNascimento() != null)
			form.setDataNascimento(Formatador.getInstance().formatarData(
					p.getDataNascimento()));
			if( p.getIdentidade() != null ){
				if(p.getIdentidade().getDataExpedicao() != null)
					form.setDataExpedicaoIdentidade(Formatador.getInstance()
						.formatarData(p.getIdentidade().getDataExpedicao()));
			}
		} else {
		}
	}

	/**
	 * Carrega as listas para os SELECTS do formulário
	 *
	 * @param req
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public void popularSelects(HttpServletRequest req, PessoaForm form) throws ArqException {
		GenericDAO dao = getGenericDAO(req);
		try {
			// campos comuns
			addSession("ufs",
					dao.findAllProjection(UnidadeFederativa.class, "sigla",
							"asc", new String[0]), req);
			addSession("municipios", new ArrayList(), req);
			addSession("tiposLogradouro",
					dao.findAll(TipoLogradouro.class), req);

			addSession("paises", new ArrayList(), req);
			addSession("estadosCivil", new ArrayList(), req);
			addSession("racas", new ArrayList(), req);
			addSession("redesEnsino", new ArrayList(),
					req);
			// agora, campos específicos
			if (isPF(form)) {
				/** carregando selects de dadosPessoais */
				addSession("paises", dao.findAll(Pais.class), req);
				addSession("estadosCivil", dao.findAll(EstadoCivil.class), req);
				addSession("racas", dao.findAll(TipoRaca.class), req);
				addSession("redesEnsino", dao.findAll(TipoRedeEnsino.class),
						req);
			} else {
				addSession("esferasAdministrativas", dao.findAll(TipoEsferaAdministrativa.class),
						req);
			}

		} finally {
			dao.close();
		}

	}

	public ActionForward submeterDados(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		return submeterDados(mapping, form, req, response, true);
	}
	/**
	 * Valida os dados submetidos pelo formulário, e redireciona para os
	 * próximos passos.
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward submeterDados(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response, boolean validar)
			throws Exception {
		PessoaForm pForm = (PessoaForm) form;
		PessoaDao dao = getDAO(PessoaDao.class, req);
		DiscenteDao ddao = getDAO(DiscenteDao.class, req);
		GenericDAO daoGeneric = getGenericDAO(req);
		
		// manda validar o form submetido
		if (validar) {
			PessoaValidator.validarDadosPessoais(pForm.getPessoa(), pForm, PessoaValidator.GERAL, newListaMensagens(req));
			
			if (flushErros(req)) {
				return mapping.findForward(DADOS_PESSOAIS);
			}
			
			// verificando se a pessoa já possui um discente ativo no mesmo nível e unidade gestora, somente no cadastro
			if (getFromSession("proximoId", req) == null && req.getSession().getAttribute("discenteAntigo") == null) {
				Integer idPessoa = dao.findIdByCpf(pForm.getPessoa().getCpf_cnpj());
				if(idPessoa > 0){
					Discente d = ddao.findAtivoByPessoaUnidadeNivel(idPessoa, getUnidadeGestora(req), getNivelEnsino(req));
					if( d != null && d.getId() != 0 ) {
						addMensagemErro("Atenção! Não é possível continuar o processo de cadastro pois já existe um discente " 
								+ d.getStatusString() + "  associado a esta pessoa (mat. " + d.getMatricula() + ")", req);
						return mapping.findForward(DADOS_PESSOAIS);
					}
				}
			}
		}

		// Inicializando objs selecionados apenas pelo ID (objs dos SELECTS das JSPs)
		try {
			Pessoa pessoa = pForm.getPessoa();
			// campos comuns
			Endereco end = pessoa.getEnderecoContato();
			if (end.getTipoLogradouro() != null && end.getTipoLogradouro().getId() > 0)
				daoGeneric.findByPrimaryKey(end.getTipoLogradouro().getId(), TipoLogradouro.class);
//				dao.initialize(end.getTipoLogradouro());
			if (end.getUnidadeFederativa() != null && end.getUnidadeFederativa().getId() > 0)
				daoGeneric.findByPrimaryKey(end.getUnidadeFederativa().getId(), UnidadeFederativa.class);
//				dao.initialize(end.getUnidadeFederativa());
			if (end.getMunicipio() != null && end.getMunicipio().getId() > 0)
				end.setMunicipio( dao.findByPrimaryKey( end.getMunicipio().getId(), Municipio.class) );
			if (end.getMunicipio() != null)
				end.getMunicipio().setUnidadeFederativa(null);
			// campos específicos
			if (isPF(pForm))  {
				if (pessoa.getUnidadeFederativa() != null && pessoa.getUnidadeFederativa().getId() >0)
					daoGeneric.findByPrimaryKey(pessoa.getUnidadeFederativa().getId(), UnidadeFederativa.class);
//					dao.initialize(pessoa.getUnidadeFederativa());
				if (pessoa.getPais() != null && pessoa.getPais().getId() >0)
					pessoa.setPais( dao.findByPrimaryKey( pessoa.getPais().getId() , Pais.class));
				if (end.getMunicipio() != null && end.getMunicipio().getId() > 0)
					daoGeneric.findByPrimaryKey(pessoa.getMunicipio().getId(), Municipio.class);
//					dao.initialize(pessoa.getMunicipio());
				if (pessoa.getEstadoCivil() != null && pessoa.getEstadoCivil().getId() >0)
					daoGeneric.findByPrimaryKey(pessoa.getEstadoCivil().getId(), EstadoCivil.class);
//					dao.initialize(pessoa.getEstadoCivil());
				if (pessoa.getTipoRaca() != null && pessoa.getTipoRaca().getId() >0)
					daoGeneric.findByPrimaryKey(pessoa.getTipoRaca().getId(), TipoRaca.class);
//					dao.initialize(pessoa.getTipoRaca());
				if (pessoa.getTipoRedeEnsino() != null && pessoa.getTipoRedeEnsino().getId() >0)
					daoGeneric.findByPrimaryKey(pessoa.getTipoRedeEnsino().getId(), TipoRedeEnsino.class);
//					dao.initialize(pessoa.getTipoRedeEnsino());
				Identidade ident = pessoa.getIdentidade();
				if (ident != null && 
						ident.getUnidadeFederativa() != null && ident.getUnidadeFederativa().getId() >0)
					daoGeneric.findByPrimaryKey(pessoa.getUnidadeFederativa().getId(), UnidadeFederativa.class);
//					dao.initialize(ident.getUnidadeFederativa());
			} else {
				PessoaJuridica pj = pForm.getPessoaJuridica();
				daoGeneric.findByPrimaryKey(pj.getTipoEsferaAdministrativa().getId(), TipoEsferaAdministrativa.class);
//				dao.initialize(pj.getTipoEsferaAdministrativa());
				pj.setPessoa(pessoa);
			}
		} finally {
			dao.close();
		}

		setStep(req, 2);

		String nextView = (String) req.getSession().getAttribute(NEXT_VIEW);
		// e redireciona para o próximo form (de acordo com o parâmetro passado)

		if (nextView != null )
			return mapping.findForward(nextView);
		else
			persist(mapping, pForm, req, response);
		return mapping.findForward(DADOS_PESSOAIS);
	}

	public ActionForward confirmarDadosDiscente(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse response) throws Exception {
		addSession("resumirDados", true, req);
		return  submeterDados(mapping, form, req, response);
	}

	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		PessoaForm pForm = (PessoaForm) form;
		Pessoa pessoa = pForm.getPessoa();

		PessoaMov mov = new PessoaMov();
		mov.setCodMovimento(getUltimoComando(req));
		mov.setPessoa(pessoa);
		mov.setPessoaJuridica(pForm.getPessoaJuridica());

		try {
			execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward(DADOS_PESSOAIS);
		}

		ActionForward retorno = null;
		char tipo = pessoa.getTipo();
		String operacao = (String) getFromSession(OPERACAO, req);
		removeSession(mapping.getName(), req);
		clearSession(req);
		pForm.clear(tipo);
		addSession("tipoPessoa", tipo, req);
		if (CADASTRAR.equals(operacao)) {
			addInformation("Dados " + ((pessoa.isPF())?"pessoais":"de pessoa jurídica") + " cadastrados com sucesso", req);
			retorno = popular(mapping, form, req, res);
		} else if (ATUALIZAR.equals(operacao)) {
			addInformation("Dados " + ((pessoa.isPF())?"pessoais":"de pessoa jurídica") + " atualizados com sucesso", req);
			retorno = list(mapping, form, req, res);
		} else if (REMOVER.equals(operacao)) {
			addInformation("Dados " + ((pessoa.isPF())?"pessoais":"de pessoa jurídica") + " removidos com sucesso", req);
			retorno = list(mapping, form, req, res);
		}
		return retorno;
	}

	/**
	 * Além da implementação padrão se faz necessário identificar qual é a
	 * operação vigente, para saber qual jsp será exibida
	 */
	@Override
	public ActionForward cancelar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String nextView = (String) request.getSession().getAttribute(NEXT_VIEW);
		request.getSession().removeAttribute(NEXT_VIEW);
		// teste que determina o redirecionamento de um cancelamento
		String operacao = (String) getFromSession(OPERACAO, request);
		
		clearSession(request);
		removeSession(mapping.getName(), request);
		clearSteps(request);
		
		if (ATUALIZAR.equals(operacao)) {
			if ("dadosDiscente".equals(nextView)) {
				request.getSession().setAttribute("cancelandoDadosPessoais", Boolean.TRUE);
				return mapping.findForward("listaDiscente");
			} else
				return list(mapping, form, request, response);
		}
		
		return super.cancelar(mapping, form, request, response);
	}


	/**
	 * Listar pessoas jurídicas
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		getForm(form).checkRole(req);
		if (getPaging(req) == null) {
			req.setAttribute("pagingInformation", new PagingInformation(0));
		}
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, getForm(form).customSearch(req));
		return mapping.findForward(LISTAR);
	}

	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		int id =  RequestUtils.getIntParameter(req, "id");
		GenericDAO dao = getGenericDAO(req);
		try {
			PessoaJuridica pj = dao.findByPrimaryKey(id, PessoaJuridica.class);
			PessoaForm pForm = (PessoaForm) form;
			pForm.setPessoaJuridica(pj);
			req.setAttribute("pj", pj);
		} finally {
			dao.close();
		}
		prepareMovimento(SigaaListaComando.REMOVER_PESSOA, req);
		addSession(OPERACAO, REMOVER, req);
		return mapping.findForward("detalhes");
	}

	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		return popular(mapping, form, req, res);
	}
	
	public ActionForward carregarDados(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response) throws Exception {
		
		PessoaForm pForm = (PessoaForm) form;
		pForm.getCpf_cnpj();
		
		boolean deuCerto = ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(pForm.getCpf_cnpj());
		
		if (deuCerto){ 
			buscar = false;
			popular(mapping, pForm, req, response);	
		}else{
			addMensagemErro("CPF inválido digite um CPF Válido.", req);
			pForm.clear();
			pForm.setDefaultProps();
		}
		
		return mapping.findForward("dadosPessoais");
	}

	public boolean isBuscar() {
		return buscar;
	}

	public void setBuscar(boolean buscar) {
		this.buscar = buscar;
	}

}