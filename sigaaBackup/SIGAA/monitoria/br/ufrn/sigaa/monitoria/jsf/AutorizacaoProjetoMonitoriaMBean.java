/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 08/07/2008
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.monitoria.AutorizacaoProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.projetos.dominio.TipoAutorizacaoDepartamento;

/**
 * Managed-Bean para autoriza��o dos projetos de monitoria pelos chefes dos
 * departamentos envolvidos no projeto atrav�s dos componentes curriculares.
 * 
 * @author ilueny santos
 * 
 */
@Component("autorizacaoProjetoMonitoria")
@Scope("session")
public class AutorizacaoProjetoMonitoriaMBean extends
		SigaaAbstractController<AutorizacaoProjetoMonitoria> {

	/** lista autoriza��es dada pelo chefe de departamento que participa do projeto */
	private Collection<AutorizacaoProjetoMonitoria> autorizacoes;
	/** Atributo utilizado para representar a unidade do projeto */
	private Unidade unidade = new Unidade(0);
	
	/** auxilia no redirecionamento de p�ginas */
	private String pagina;
	/** Atributo utilizado informar se a solicita��o ja foi processada */
	private boolean solicitacaoProcessada = false;

	/**
	 * Construtor padr�o
	 */
	public AutorizacaoProjetoMonitoriaMBean() {
		obj = new AutorizacaoProjetoMonitoria();

	}
	/** Atributo utilizado para representar a autoriza��o dada pelo chefe do departamento que participa do projeto atrav�s dos componentes curriculares */
	private AutorizacaoProjetoMonitoria apm = new AutorizacaoProjetoMonitoria();

	public AutorizacaoProjetoMonitoria getApm() {
		return apm;
	}

	public void setApm(AutorizacaoProjetoMonitoria apm) {
		this.apm = apm;
	}

	/**
	 * Permite listar os tipos de autoriza��es poss�veis
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/monitoria/br/ufrn/sigaa/monitoria/jsf/AutorizacaoProjetoMonitoriaMBean.java</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/monitoria/AutorizacaoProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<SelectItem> getTiposAutorizacoesCombo() {
		try {
			GenericDAO dao = getGenericDAO();
			return toSelectItems(dao.findAll(TipoAutorizacaoDepartamento.class), "id","descricao");
		} catch (DAOException e) {
			notifyError(e);
			return null;
		}
	}

	/**
	 * Lista de projetos pendentes de autoriza��o dos chefes
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public Collection<ProjetoEnsino> getAllProjetosParaAutorizacao() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE);
		
		try {
			ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
			return dao.findByAutorizacaoPendente();
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<ProjetoEnsino>();
		}
	}
	
	/**
	 * Lista de autoriza��es dos departamentos
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/recibo_autorizacao.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarAutorizacoesDepartamentos() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		//prepareMovimento(SigaaListaComando.AUTORIZAR_PROJETO_MONITORIA);
		solicitacaoProcessada = false;

		listarAutorizacoes();
		// buscar todas as autoriza��es atrav�s da sele��o dos departamentos.
		return forward(ConstantesNavegacaoMonitoria.AUTORIZACAOATIVIDADE_MONITORIA_BUSCA);
	}
	
	
	/**
	 * Busca todas as autoriza��es de unidades
	 * 
	 * Este m�todo pode ser chamado na jsp	/SIGAA/app/sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/form_busca_autorizacoes.jsp
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public void listarAutorizacoes() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		try {
			int idUnidade = getParameterInt("idUnidade", 0);
			if (idUnidade > 0) {
				unidade = new Unidade(idUnidade);
			}
			AutorizacaoProjetoMonitoriaDao autorizacoeDao = getDAO(AutorizacaoProjetoMonitoriaDao.class);

			Collection<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();
			unidades.add(unidade);
			autorizacoes = autorizacoeDao.findByUnidades(unidades);

		} catch (DAOException e) {
			notifyError(e);
			autorizacoes = new ArrayList<AutorizacaoProjetoMonitoria>();
		}
	}

	/**
	 * Lista de autoriza��es da unidade do usu�rio logado.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/monitoria/AutorizacaoProjetoMonitoria/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public Collection<AutorizacaoProjetoMonitoria> getAutorizacoesNaoAnalisadas() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO,
				  SigaaPapeis.CHEFE_UNIDADE, SigaaPapeis.GESTOR_MONITORIA);

		UnidadeDAOImpl undDao = getDAO(UnidadeDAOImpl.class, Sistema.COMUM);
		try {

			//todas as unidades onde o usu�rio atual � chefe
			AutorizacaoProjetoMonitoriaDao dao = getDAO(AutorizacaoProjetoMonitoriaDao.class);
			Collection<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();			
			
			unidades.addAll( undDao.findUnidadesByResponsavel(getServidorUsuario().getId(), new Character[] {NivelResponsabilidade.CHEFE, NivelResponsabilidade.VICE, NivelResponsabilidade.SUPERVISOR_DIRETOR_ACADEMICO}) );

			if ((unidades == null) || (unidades.isEmpty()))
				addMensagemErro("O usu�rio atual n�o possui unidades configuradas sob sua responsabilidade no SIGAA.");

			autorizacoes = dao.findByUnidadeDoComponente(unidades);
			
			return autorizacoes;
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<AutorizacaoProjetoMonitoria>();
		}finally {
			undDao.close();
		}
	}
	
	/**
	 * Permite ao Chefe de departamento, diretor ou chefe de unidade re-imprimir
	 * recibo do projeto de ensino
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/monitoria/AutorizacaoProjetoMonitoria/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String reciboProjetoMonitoria() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE);
		GenericDAO dao = getGenericDAO();		
		
		int id = getParameterInt("idAutorizacao", 0);

		if (id > 0) {			
			try {
				obj = dao.findByPrimaryKey(getParameterInt("idAutorizacao"), AutorizacaoProjetoMonitoria.class);
			}
			catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
			}

		} else {
			addMensagemErro("Projeto de Ensino n�o Encontrado");
			return null;
		}		
		return forward(ConstantesNavegacaoMonitoria.AUTORIZACAOPROJETO_RECIBO);		
	}

	/**
	 * Escolhe uma autoriza��o que ainda n�o foi analisada
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/monitoria/AutorizacaoDepartamento/form_busca_autorizacoes.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/monitoria/AutorizacaoProjetoMonitoria/lista.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/monitoria/AutorizacaoReconsideracao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * 
	 */
	public String escolherAutorizacao() throws SegurancaException {

	    checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE, SigaaPapeis.GESTOR_MONITORIA);		
	    GenericDAO dao = getGenericDAO();
	    solicitacaoProcessada=false;

	    try {
		obj = dao.findByPrimaryKey(getParameterInt("idAutorizacao"), AutorizacaoProjetoMonitoria.class);
		this.pagina = getParameter("paginaParaVoltar");

		if (obj.getTipoAutorizacao() == null) {
		    obj.setTipoAutorizacao(new TipoAutorizacaoDepartamento());
		}			
		if (obj.getDataAutorizacao() == null) {
		    obj.setAutorizado(true); //Definindo padr�o como autorizado.
		}

		//Gestores de monitoria podem autorizar a proposta a qualquer hora
		if (( !obj.getProjetoEnsino().getProjeto().isInterno() || 
				( obj.getProjetoEnsino().getEditalMonitoria().isPermitidoDepartamentoAutorizar() ) ) 
			|| getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MONITORIA)) {
		    prepareMovimento(SigaaListaComando.AUTORIZAR_PROJETO_MONITORIA);
		    setConfirmButton("Autorizar");				
		    return forward(ConstantesNavegacaoMonitoria.AUTORIZACAOPROJETO_FORM);
		} else {
		    addMensagemErro("O prazo para autoriza��o de propostas de projetos de ensino expirou, ou ainda n�o foi definido.");
		    addMensagemErro("Entre em contato com a Pr�-Reitoria de gradua��o.");
		}

	    } catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
	    }

	    return null;

	}

	
	
	/**
	 * Chefe de departamento ou diretor de centro pode
	 * autorizar um projeto de monitoria
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String autorizar() throws SegurancaException {

		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE, SigaaPapeis.GESTOR_MONITORIA);
		
		try {
			if (solicitacaoProcessada) {
				addMensagemErro(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
				obj = new AutorizacaoProjetoMonitoria();
				return forward(ConstantesNavegacaoMonitoria.AUTORIZACAOPROJETO_FORM);
			}
			//prepareMovimento(SigaaListaComando.AUTORIZAR_PROJETO_MONITORIA);
			
			GenericDAO dao = getGenericDAO();
			
			// operador escolheu n�o autorizar
			// na view o select = 0 = n�o autorizar;
			if(!obj.isAutorizado())
				if (obj.getTipoAutorizacao().getId() == TipoAutorizacaoDepartamento.NAO_AUTORIZAR) {  
					obj.setTipoAutorizacao(null);
				}
			
			obj.setDataAutorizacao(new Date());
			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			
			ListaMensagens lista = obj.validate();
			if (lista != null && lista.getMensagens().size() > 0) {
				addMensagens(lista);
				return forward(ConstantesNavegacaoMonitoria.AUTORIZACAOPROJETO_FORM);
			}
	
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setAcao(MovimentoCadastro.ACAO_ALTERAR);
			mov.setCodMovimento(SigaaListaComando.AUTORIZAR_PROJETO_MONITORIA);
			
			
			obj = (AutorizacaoProjetoMonitoria)execute(mov, getCurrentRequest());			
			
			
			
			//carregando autoriza��es e projetos
			obj.setProjetoEnsino(dao.findByPrimaryKey(obj.getProjetoEnsino().getId(), ProjetoEnsino.class));
			solicitacaoProcessada=true;


		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			if(e.getMessage().equals("Solicita��o j� processada")) {
				solicitacaoProcessada = true;
			}
			obj = new AutorizacaoProjetoMonitoria();			
			return (getFormPage());			
		}

		return forward(ConstantesNavegacaoMonitoria.AUTORIZACAOPROJETO_RECIBO);
	}
	
	@Override
	public String cancelar() {

		resetBean();
		solicitacaoProcessada = false;
		if(getPagina().equals("chefe")) {
			return forward(ConstantesNavegacaoMonitoria.AUTORIZACAOPROJETO_LISTA);
		} else {
			return forward("/monitoria/AutorizacaoDepartamento/form_busca_autorizacoes.jsp");
		}
		
	}
	
	/**
	 * M�todo utilizado para remover uma avalia��o do projeto
	 * @return
	 */
	public String removerAvaliacao() {
		return null;
	}

	public Collection<AutorizacaoProjetoMonitoria> getAutorizacoes() {
		return autorizacoes;
	}

	public void setAutorizacoes(Collection<AutorizacaoProjetoMonitoria> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getPagina() {
		return pagina;
	}

	public void setPagina(String pagina) {
		this.pagina = pagina;
	}	
}
