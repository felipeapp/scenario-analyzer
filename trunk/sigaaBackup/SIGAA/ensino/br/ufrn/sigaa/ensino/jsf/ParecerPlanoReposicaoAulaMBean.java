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
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.negocio.TopicoAulaHelper;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.ensino.dominio.ParecerPlanoReposicaoAula;
import br.ufrn.sigaa.ensino.dominio.PlanoReposicaoAula;
import br.ufrn.sigaa.ensino.dominio.StatusParecerPlanoReposicaoAula;

/**
 * Mbean responsável por realizar as operações de julgamento de 
 * um plano de reposição de aula
 * 
 * @author Henrique André
 */
@Component("parecerPlanoReposicaoAula") @Scope("request")
public class ParecerPlanoReposicaoAulaMBean extends SigaaAbstractController<ParecerPlanoReposicaoAula> {

	/**
	 * Inicializa o objeto.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 */
	public void init() {
		obj = new ParecerPlanoReposicaoAula();
		obj.setStatus(new StatusParecerPlanoReposicaoAula());
		obj.getStatus().setId(StatusParecerPlanoReposicaoAula.APROVADO);
	}
	
	/**
	 * Vai para a tela de julgamento do plano de aula
	 * 
	 * JSP: /sigaa.war/ensino/aviso_falta/docente/gerenciar_pendentes.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarParecer() throws ArqException {
		
		init();
		
		prepareMovimento(SigaaListaComando.CADASTRAR_PARECER_PLANO_AULA);
		prepareMovimento(SigaaListaComando.NOTIFICAR_PROFESSOR_PARECER_PLANO_AULA);
		int id  = getParameterInt("id", 0);
		PlanoReposicaoAula planoAula = getGenericDAO().findByPrimaryKey(id, PlanoReposicaoAula.class);

		if (isEmpty(planoAula)) {
			addMensagemErro("Não foi possível localizar este plano de aula");
			return null;
		}		
		
		obj.adicionarPlanoAula(planoAula);
		
		return forward("/ensino/aviso_falta/docente/parecer/form.jsp");
	}		
	
	/**
	 * Registra o parecer de um plano de aula dado pelo chefe de departamento
	 * 
	 * JSP: /sigaa.war/ensino/falta_homologada/parecer/form.jsp
	 * @return
	 * @throws ArqException
	 */
	public String emitirParecer() throws ArqException {
		validarParecer();
		
		if(hasErrors()){
			return null;
		}
		
		try {
			criarParecer();
			addMensagemInformation("Parecer emitido com sucesso.");
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e, "Não foi possível emitir o parecer para este plano de aula.");
		}

		if (obj.getPlanoAula().isAprovado()) 
			notificarTurma();

		try {
			notificarProfessor();
			addMensagemInformation("O docente recebeu uma mensagem sobre o parecer.");
		} catch (NegocioException e) {
			addMensagemErro("Ocorreu um erro ao tentar notificar o professor sobre o parecer.");
		}
		
		PlanoReposicaoAulaMBean planoReposicaoAulaMBean = getMBean("planoReposicaoAula");
		return planoReposicaoAulaMBean.listarPlanoAulaPendentesAprovacao();
	}
	
	/**
	 * Valida os campos do parecer.
	 */
	private void validarParecer(){
		if(obj.getJustificativa().trim() == "" || obj.getJustificativa() == null){
			addMensagemErro("Justificativa: Campo Obrigatório Não Informado.");
		}
	}

	/**
	 * Envia email para os participantes da turma, falando sobre a
	 * reposição da aula
	 * 
	 * @throws DAOException 
	 */
	private void notificarTurma() throws DAOException {
		
		PlanoReposicaoAula planoAula = getGenericDAO().refresh(obj.getPlanoAula());
		
		String txt = construirTexto(planoAula);
		
		TurmaVirtualMBean mbBean = getMBean("turmaVirtual");
		
		mbBean.notificarTurma(planoAula.getFaltaHomologada().getDadosAvisoFalta().getTurma(), "Plano de Reposição de Aula da disciplina " + planoAula.getFaltaHomologada().getDadosAvisoFalta().getTurma().getDisciplina().getCodigoNome() , txt, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO);
		addMensagemInformation("A turma recebeu uma notificação com o conteúdo do plano de aula.");
		mbBean.resetBean();
	}

	/**
	 * Notifica o professor sobre o parecer
	 * 
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void notificarProfessor() throws ArqException, NegocioException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.NOTIFICAR_PROFESSOR_PARECER_PLANO_AULA);
		mov.setObjMovimentado(obj);
		
		execute(mov);
	}
	
	
	/**
	 * Monta o texto que será enviado a turma
	 * @param planoAula 
	 * 
	 * @return
	 */
	private String construirTexto(PlanoReposicaoAula planoAula) {
		String departamento = planoAula.getFaltaHomologada().getDadosAvisoFalta().getDocente().getUnidade().getNome();
		String codigoNome = planoAula.getFaltaHomologada().getDadosAvisoFalta().getTurma().getDisciplina().getCodigoNome();
		String codigoTurma = planoAula.getFaltaHomologada().getDadosAvisoFalta().getTurma().getCodigo();
		String dataReposicao = Formatador.getInstance().formatarData(planoAula.getDataAulaReposicao());
		
		StringBuilder txt = new StringBuilder();
		txt.append("Caros Participantes,\n");
		txt.append("Conforme Art. 55 do Regimento, a Chefia do " + departamento + "  apresenta o plano de reposição de aulas da Turma " + codigoNome + " - Turma " + codigoTurma + " para o dia " + dataReposicao + ".");
		txt.append("\n\n");
		txt.append("Docente: " + planoAula.getFaltaHomologada().getDadosAvisoFalta().getDocenteNome());
		txt.append("\n");
		txt.append("Conteúdo: " + planoAula.getDidatica());
		return txt.toString();
	}

	/**
	 * Invoca o processador para criar o parecer e atualizar o plano de aula
	 * 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void criarParecer() throws NegocioException, ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_PARECER_PLANO_AULA);
		mov.setObjMovimentado(obj);
		execute(mov);
		
		if (obj.isAprovado())
			criarTopicoAula();
	}

	/**
	 * Cria o tópico de aula com os dados informados pelo docente.
	 * @throws DAOException 
	 */
	private void criarTopicoAula() throws DAOException {
		TopicoAula topicoAula = TopicoAulaHelper.criar(obj);
		Notification notification = persistirTopicoAula(SigaaListaComando.CADASTRAR_AVA, topicoAula, new NullSpecification());

		if (notification.hasMessages()) 
			addMensagens(notification.getMessages());
	}
	
	/**
	 * Invoca o processador para persistir o tópico de aula previamente criado.
	 * 
	 * @param comando
	 * @param object
	 * @param specification
	 * @return
	 */
	private Notification persistirTopicoAula(Comando comando, DominioTurmaVirtual object, Specification specification){
		try {			
			prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
			
			MovimentoCadastroAva mov = new MovimentoCadastroAva();
			mov.setCodMovimento(comando);
			mov.setObjMovimentado(object);
			mov.setTurma(object.getTurma());
			mov.setSpecification(specification);
			mov.setMensagem(object.getMensagemAtividade());
			List<String> cadastrarEm = new ArrayList<String>();
			cadastrarEm.add(String.valueOf(mov.getTurma().getId()));
			mov.setCadastrarEm(cadastrarEm);
			mov.setCadastrarEmVariasTurmas(false);

			return execute(mov, getCurrentRequest());
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Monta combobox com os status possíveis para dar o parecer
	 * 
	 * JSP: /sigaa.war/ensino/falta_homologada/parecer/form.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPossiveisStatusParecerCombo() throws DAOException {
		Collection<StatusParecerPlanoReposicaoAula> all = getGenericDAO().findAll(StatusParecerPlanoReposicaoAula.class);
		return toSelectItems(all, "id", "descricao");
	}	
}
