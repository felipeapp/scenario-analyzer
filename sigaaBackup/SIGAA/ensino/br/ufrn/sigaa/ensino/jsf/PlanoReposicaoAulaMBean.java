/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 16/06/2009
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.AvisoFaltaDocenteHomologadaDao;
import br.ufrn.sigaa.arq.dao.ensino.PlanoReposicaoAulaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocenteHomologada;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAvisoFaltaHomologado;
import br.ufrn.sigaa.ensino.dominio.PlanoReposicaoAula;

/**
 * Mbean responsável por gerenciar os planos de aulas que os docentes
 * deverão apresentar quando uma falta for homologada.
 * 
 * @author Henrique André
 */
@Component("planoReposicaoAula") @Scope("request")
public class PlanoReposicaoAulaMBean extends SigaaAbstractController<PlanoReposicaoAula> {

	/** Aramzena a lista de {@link PlanoReposicaoAula} existentes para o docente logado. */
	private List<PlanoReposicaoAula> listaPlanoAula;
	
	/**
	 * Inicializa o objeto
	 */
	private void init() {
		obj = new PlanoReposicaoAula();
	}

	/**
	 * Inicia o procedimento para o preenchimento do plano de aula
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/falta_homologada/homologacao_pendentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarPlanoAulaFaltaHomologada() throws ArqException {
		init();

		setConfirmButton("Cadastrar Plano de Aula");
		prepareMovimento(ArqListaComando.ALTERAR);
		prepareMovimento(SigaaListaComando.CADASTRAR_PLANO_RESPOSICAO_AULA);
		
		Integer id = getParameterInt("id", 0);
		AvisoFaltaDocenteHomologada faltaHomologada = getGenericDAO().findByPrimaryKey(id, AvisoFaltaDocenteHomologada.class);
		
		if (isEmpty(faltaHomologada)) {
			addMensagemErro("Ocorreu um erro ao tentar localizar a homologação de falta.");
			return null;
		}
		
		obj.setFaltaHomologada(faltaHomologada);
		return forward("/ensino/aviso_falta/docente/plano_aula/form.jsp");
	}
	
	/**
	 * Popula a lista que armazena os {@link PlanoReposicaoAula} cadastrados para o docente logado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/aviso_falta/docente/plano_aula/view.jsp</li>
	 * <li>/sigaa.war/portais/docentemenu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarTodosPlanos() throws DAOException {
		
		PlanoReposicaoAulaDao dao = getDAO(PlanoReposicaoAulaDao.class);
		listaPlanoAula = dao.findAllPlanosByDocente(getServidorUsuario().getId());
		
		if (isEmpty(listaPlanoAula)) {
			addMensagemErro("Você não possui nenhum plano de aula cadastrado.");
			return null;
		}
			
		return forward("/ensino/aviso_falta/docente/plano_aula/lista_planos_elaborados.jsp");
	}
	
	/**
	 * Persiste o plano de aula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/falta_homologada/plano_aula/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String criarPlanoAula() throws ArqException {
		
		validarPlanoAula();
		
		if (hasErrors())
			return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		MovimentoCadastro movAux = new MovimentoCadastro();
		AvisoFaltaDocenteHomologadaDao avisoFaltaDao = getDAO(AvisoFaltaDocenteHomologadaDao.class);
		
		StringBuilder msg = new StringBuilder();
		if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_PLANO_RESPOSICAO_AULA)) {
			
			obj.getFaltaHomologada().setMovimentacao(avisoFaltaDao.findByPrimaryKey(MovimentacaoAvisoFaltaHomologado.PLANO_PENDENTE_APROVACAO_CHEFIA.getId(), MovimentacaoAvisoFaltaHomologado.class));
			mov.setObjMovimentado(obj);
			movAux.setObjMovimentado(obj.getFaltaHomologada());
			
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PLANO_RESPOSICAO_AULA);
			movAux.setCodMovimento(ArqListaComando.ALTERAR);
			msg.append("Plano de aula criado com sucesso. No ato da homologação por parte da chefia de unidade acadêmica os alunos serão notificados por e-mail.");
		} 
		else {
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			msg.append("Plano de aula alterado com sucesso. No ato da homologação por parte da chefia de unidade acadêmica os alunos serão notificados por e-mail.");
		}
		
		try {
			execute(mov);
			if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_PLANO_RESPOSICAO_AULA))
				execute(movAux);
			addMensagemInformation(msg.toString());
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e, "Não foi possível cadastrar seu plano de aula.");
		}
		
		if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_PLANO_RESPOSICAO_AULA)) {
			if (avisoFaltaDao.isPendentePlanoAula(getServidorUsuario().getId())) {
				AvisoFaltaDocenteHomologadaMBean faltaHomologadaMBean = getMBean("avisoFaltaHomologada");
				return faltaHomologadaMBean.listarHomologacoesPendentes();
			}
		} else {
			return listarTodosPlanos();
		}

		return cancelar();
	}

	/**
	 * Valida o formulário
	 */
	private void validarPlanoAula() {
		ValidatorUtil.validateRequired(obj.getDataAulaReposicao(), "Data da Aula", erros);
		ValidatorUtil.validateFuture(obj.getDataAulaReposicao(), "Data de Reposição da Aula", erros);
		ValidatorUtil.validateRequired(obj.getDidatica(), "Conteúdo", erros);
	}

	/**
	 * Vai para tela com a lista dos planos que não possuem parecer
	 * 
	 * JSP: /sigaa.war/ensino/falta_homologada/parecer/form.jsp
	 * @return
	 */
	public String listarPlanoAulaPendentesAprovacao() {
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_DOCENTE);
		return forward("/ensino/aviso_falta/docente/gerenciar_pendentes.jsp");
	}	
	
	/**
	 * Lista com os planos sem parecer
	 * 
	 * JSP: /sigaa.war/ensino/aviso_falta/docente/gerenciar_pendentes.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<PlanoReposicaoAula> getPlanoPendentesByDepartamento() throws DAOException {
		PlanoReposicaoAulaDao dao = getDAO(PlanoReposicaoAulaDao.class);
		
		List<PlanoReposicaoAula> planos = dao.findPlanosPendentesByDepartamento(getServidorUsuario().getUnidade().getId());
	
		if (isEmpty(planos))
			planos = new ArrayList<PlanoReposicaoAula>();
		
		
		return planos;
	}

	/**
	 * Visualiza o plano de aula e o parecer que foi dado a ele.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException {
		
		Integer id = getParameterInt("id", 0);
		
		PlanoReposicaoAula planoReposicaoAula = getGenericDAO().findByPrimaryKey(id, PlanoReposicaoAula.class);
		
		if (isEmpty(planoReposicaoAula)) {
			addMensagemErro("Plano de Aula não encontrado.");
			return null;
		}
		
		obj = planoReposicaoAula;
		
		return forward("/ensino/aviso_falta/docente/plano_aula/view.jsp");
	}
	
	/**
	 * Atualiza um plano de aula
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/falta_homologada/plano_aula/lista_planos_elaborados.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAlterar() throws ArqException {
		prepareMovimento(ArqListaComando.ALTERAR);
		
		setConfirmButton("Alterar");
		Integer id = getParameterInt("id", 0);
		
		PlanoReposicaoAula planoReposicaoAula = getGenericDAO().findByPrimaryKey(id, PlanoReposicaoAula.class);
		
		if (isEmpty(planoReposicaoAula)) {
			addMensagemErro("Plano de aula não encontrado.");
			return null;
		}
		
		if (!isEmpty(planoReposicaoAula.getParecer())) {
			addMensagemErro("Este plano não pode ser mais alterado porque o chefe de departamento já emitiu um parecer.");
			return null;
		}
		
		obj = planoReposicaoAula;
		
		return forward("/ensino/aviso_falta/docente/plano_aula/form.jsp");
	}	
	public List<PlanoReposicaoAula> getListaPlanoAula() {
		return listaPlanoAula;
	}

	public void setListaPlanoAula(List<PlanoReposicaoAula> listaPlanoAula) {
		this.listaPlanoAula = listaPlanoAula;
	}	

	/**
	 * Define o comportamento do botão voltar
	 * 
	 * JSP: /sigaa.war/ensino/falta_homologada/plano_aula/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String btnVoltar() throws ArqException {
		if (getUltimoComando().equals(ArqListaComando.CADASTRAR))
			return ((AvisoFaltaDocenteHomologadaMBean) getMBean("avisoFaltaHomologada")).listarHomologacoesPendentes();
		else
			return listarTodosPlanos();
	}
	
}
