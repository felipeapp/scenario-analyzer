/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.mensagens.MensagensProdocente;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Managed Bean para o caso de uso de Bancas (Trabalhos de Conclus�o e Comiss�es Julgadoras) da Produ��o Intelectual
 * 
 * Gerado pelo CrudBuilder
 */
@Component("banca")
@Scope("session")
public class BancaMBean	extends	AbstractControllerProdocente<Banca> {
	
	/** Constante que define a view de listagem dos cursos  */
	private static final String LISTA_CURSO = "/prodocente/producao/Banca/lista_curso.jsp";
	/** Constante que define a view de listagem dos concursos  */
	private static final String LISTA_CONCURSO = "/prodocente/producao/Banca/lista_concurso.jsp";

	/** Define a cole��o do resultado da consulta das bancas. */
	Collection<Banca> bancas;
	
	public BancaMBean() {
		obj = new Banca();
		bancas = new ArrayList<Banca>();
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(Banca.class, "id", "descricao");
	}
	
	/**
	 * M�todo cancela a opera��o de envio de arquivo.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Banca/arquivo.jsp
	 * 
	 */
	@Override
	public String cancelArquivo() {
		try {
			return listar();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * M�todo respons�vel em redirecionar o docente para determinadas telas de acordo
	 * com as condi��es descritas internamente.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#cancelar()
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Banca/form.jsp
	 * 
	 */	
	@Override
	public String cancelar() {
		
		if (forwardCancelar != null){
			return forward(forwardCancelar);
		}

		if (validar != null && validar) {

			return forward("/portais/docente/docente.jsp");

		} else {

			if (getConfirmButton() != null) {
				if (getConfirmButton().equals("Alterar")
						|| getConfirmButton().equals("Remover")) {
					String url = "/sigaa/" + getListPage();
					url = url.substring(0, url.length() - 1);
					return redirectJSF(url + "f");

				} else if (getConfirmButton().equals("Cadastrar")) {
					return forward("/portais/docente/docente.jsp");
				}
			}
			return super.cancelar();
		}

	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@SuppressWarnings("cast")
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.setServidor(getServidorUsuario());

		if (obj.getTipoBanca().getId() == 1) {
			obj.setTipoProducao(TipoProducao.BANCA_CURSO_SELECOES);
			if (obj.getId() == 0 && obj instanceof Producao) {
				ProducaoDao dao = getDAO(ProducaoDao.class);
				dao.initialize(obj.getTipoProducao());
				Collection<Producao> bancas = dao.findProducaoByServidorAnoTituloTipo(obj);
				if (bancas.size() > 0) {
					for (Producao banca : bancas)
						if (((Banca) banca).getNaturezaExame().getId() == obj.getNaturezaExame().getId())
							addMensagem(MensagensProdocente.BANCA_JA_CADASTRADA_COM_MESMOS_DADOS,
									((Banca) banca).getNaturezaExame().getDescricao());
				}
			}
		}
		else if (obj.getTipoBanca().getId() == 2) {
			obj.setTipoProducao(TipoProducao.BANCA_CONCURSO);
			super.beforeCadastrarAndValidate();
		}
	}

//	@Override
//	public String cadastrar() throws SegurancaException, ArqException,NegocioException {
//		String retorno = super.cadastrar();
//		
//		if (forwardAlterar != null && !hasErrors())
//			redirectJSF(forwardAlterar);
//		
//		return retorno;
//	}
	
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,	SegurancaException, DAOException {
		
		if (!obj.getPais().isBrasil()) {
			obj.setMunicipio(null);
		}
		obj.setDataProducao(obj.getData());
		if(obj.getDepartamento() != null && obj.getDepartamento().getId() == 0)
			obj.setDepartamento(null);
		super.beforeCadastrarAfterValidate();
	}
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	protected void afterCadastrar() {
		try {
			initObj(obj.getTipoBanca().getId());
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		obj.iniciarNulos();
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public String getListPage() {
		if (obj.isBancaConcurso())
			return LISTA_CONCURSO;
		else
			return LISTA_CURSO;
	}

	@Override
	public String listar() throws ArqException {
		if (obj.isBancaConcurso())
			return listaConcurso();
		else
			return listaCurso();
	}
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public void popularObjeto(Producao p) {
		obj = (Banca) p;
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.BANCA_CURSO_SELECOES);
	}

	/**
	 * Inicializa o objeto associado ao MBean
	 * @param tipo
	 * @throws DAOException
	 */
	 private void initObj(int tipo) throws DAOException {
		 obj = new Banca();
		 obj.setTipoBanca(getGenericDAO().findByPrimaryKey(tipo, TipoBanca.class));
		 obj.getPais().setId(Pais.BRASIL);
		 obj.setDataCadastro(new Date());
		 obj.getInstituicao().setNome(InstituicoesEnsino.NOME_UFRN);
		 obj.getInstituicao().setId(InstituicoesEnsino.UFRN);
	 }

	/**
	 * Redireciona direto para p�gina de cadastro de bancas curso/concurso com combobox de concurso selecionado
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/nova_producao.jsp
	 * /prodocente/producao/Banca/lista_concurso.jsp
	 * 
	 * @throws DAOException
	 */
	 public String formCadastrarConcurso() throws SegurancaException, DAOException {
		 setReadOnly(false);
		 setConfirmButton("Cadastrar");
		 setValidar(false);
		 setVisualizar(false);		 
		 initObj(TipoBanca.CONCURSO);
		 return forward("/prodocente/producao/Banca/form.jsp");
	}

	/**
	 * Redireciona direto para p�gina de cadastro de bancas curso/concurso com combobox de curso selecionado
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/nova_producao.jsp
	 * /prodocente/producao/Banca/lista_curso.jsp
	 * 
	 * @throws DAOException
	 */
	public String formCadastrarCurso() throws SegurancaException, DAOException {
 		setReadOnly(false);
 		setConfirmButton("Cadastrar");
 		setValidar(false);
 		setVisualizar(false);
 		initObj(TipoBanca.CURSO);
 		return forward("/prodocente/producao/Banca/form.jsp");
	}

	/**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Banca/form.jsp
	 *  
	 */
	 public boolean isCurso() {
		 return obj.getTipoBanca() != null && obj.getTipoBanca().getId() == TipoBanca.CURSO;
	 }

	 /**
	  * Lista todas as bancas de curso
	  * 
	  * M�todo chamado pela(s) seguinte(s) JSP(s):
	  * /prodocente/producao/Banca/lista_curso.jsp
	  * 
	  * @return
	  * @throws SegurancaException
	 * @throws DAOException 
	  */
	 public Collection<Banca> getAllCurso() throws SegurancaException, DAOException {
		 	if ( bancas == null || mudouPagina() )
		 		bancas = new ArrayList<Banca>();
		 
			checkDocenteRole();

			if (bancas.isEmpty()) {
				ProducaoDao dao = getDAO(ProducaoDao.class);
				try {
					bancas = dao.findByBanca(getServidorUsuario(), TipoBanca.CURSO, getPaginacao()); 
					return bancas;
				} finally {
					dao.close();
				}
			} else {
				return bancas;
			}
	 }

	@Override
	public String preEnviarArquivo() throws DAOException {
		super.preEnviarArquivo();
		bancas = null;
		getCurrentSession().setAttribute("backPage", getListPage());
		return forward("/prodocente/producao/Banca/arquivo.jsp");
	}
	 
	 /**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/listar_producao.jsp
	 *  
	 */
	 public String listaCurso() {
		 bancas = new ArrayList<Banca>();
		 setPaginaInicial();
		 return forward(LISTA_CURSO);
	 }

	 /**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/listar_producao.jsp
	 * 
	 */
	 public String listaConcurso() {
		 bancas = new ArrayList<Banca>();
		 setPaginaInicial();
		 return forward(LISTA_CONCURSO);
	 }

	 /**
	  * Dependendo do tipo de banca direciona para a listagem correta.
	  * 
	  * M�todo chamado pela(s) seguinte(s) JSP(s):
	  * 
  	  * /prodocente/producao/Banca/form.jsp
  	  * 
	  */
	 public void listagem() {
		 boolean banca = getParameterBoolean("banca");
		 if (banca) {
			listaCurso();
		} else {
			listaConcurso();
		}
	 }

	 /**
	  * Lista todas as bancas de concurso
	  * 
	  * M�todo chamado pela(s) seguinte(s) JSP(s):
	  * /prodocente/producao/Banca/lista_concurso.jsp
	  * 
	  * @return
	  * @throws SegurancaException
	 * @throws DAOException 
	  */
	 public Collection<Banca> getAllConcurso() throws SegurancaException, DAOException {
		 	if ( bancas == null || mudouPagina() )
		 		bancas = new ArrayList<Banca>();
		 
			checkDocenteRole();

			if (bancas.isEmpty()) {
				ProducaoDao dao = getDAO(ProducaoDao.class);
				try {
					bancas = dao.findByBanca(getServidorUsuario(), TipoBanca.CONCURSO, getPaginacao()); 
					return bancas;
				} finally {
					dao.close();
				}
			} else {
				return bancas;
			}
	 }

	 /** Seta a p�gina inicial como sendo a primeira */
	 private void setPaginaInicial() {
		PagingInformation paginacao = (PagingInformation) getMBean("paginacao");
		paginacao.setPaginaAtual(0);
	}
	 
	/**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Banca/lista_concurso.jsp
	 * /prodocente/producao/Banca/lista_curso.jsp
	 * 
	 */	 
	@Override
	public String remover() {
			MovimentoCadastro mov = new MovimentoCadastro();
			PersistDB obj = this.obj;


			try {
				prepareMovimento(ArqListaComando.REMOVER);
				obj.setId(getParameterInt("id"));

				Banca banca = (Banca) getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
			
				if (banca == null || banca.getId() == 0) {
					addMensagemErro("O registro informado n�o encontra-se dispon�vel para remo��o");
					return null;
				} else {
					obj = banca;
				}
				
			} catch (Exception e) {
				return tratamentoErroPadrao(e);
			}

			
			
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(ArqListaComando.REMOVER);
			
			try {
				execute(mov,
						(HttpServletRequest) FacesContext.getCurrentInstance()
								.getExternalContext().getRequest());
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

			return	redirectJSF(getListPage().replace(".jsp", ".jsf"));
		}
	
	/**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /prodocente/producao/Banca/lista_concurso.jsp
	 * /prodocente/producao/Banca/lista_curso.jsp
	 * 
	 */	 
	@Override
	public String atualizar() throws ArqException {
		 
		setId();
		PersistDB obj = this.obj;
		Banca banca = (Banca) getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
		changeArea();
		 
		banca.setTitulo( StringUtils.unescapeHTML(banca.getTitulo()) );
		 
		if (banca == null || banca.getId() == 0) {
			addMensagemErro("O registro informado n�o encontra-se dispon�vel para atualiza��o");
			return null;
		}
		 
		return super.atualizar();
	}

}