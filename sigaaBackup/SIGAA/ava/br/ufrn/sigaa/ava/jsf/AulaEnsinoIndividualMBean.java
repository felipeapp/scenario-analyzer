/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 24/01/2013
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.ensino.AulaExtraDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.ensino.dominio.PlanoReposicaoAula;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;

/**
 * Managed Bean para a gerência de aulas de ensino individual para as turmas virtuais.
 * 
 * @author Rafael Gomes
 *
 */
@Component("aulaEnsinoIndividual") @Scope("request")
public class AulaEnsinoIndividualMBean extends AulaExtraMBean{

	/** Construtor padrão */
	public AulaEnsinoIndividualMBean (){
		object = new AulaExtra();
	}
	
	public String getDirBase(){
		return "/ava/AulaEnsinoIndividual";
	}

	@Override
	public String listar() {
		prepare(SigaaListaComando.REMOVER_AVA);
		return forward(getDirBase() + "/listar.jsp");
	}
	
	@Override
	public String novo() {
		try {
			instanciar();
			if (cadastrarEmVariasTurmas()) {
				cadastrarEm = new ArrayList<String>();
				cadastrarEm.add(String.valueOf(turma().getId()));
			}
			prepare(SigaaListaComando.CADASTRAR_AVA);
			return forward(getDirBase() + "/novo.jsp");
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Retorna a lista com as aulas de ensino individual da turma.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/AulaEnsinoIndividual/listar.jsp</li>
	 *  <li>/ava/AulaEnsinoIndividual/novo.jsp</li>
	 *  <li>/ava/menu.jsp</li>
	 * </ul>
	 */
	@Override
	public List<AulaExtra> lista() {
		if ( !isEmpty( turma().getSubturmas() )) {
			return getDAO(AulaExtraDao.class).buscarAulasEnsinoIndividualSubTurmas(turma());
		} else {
			return getDAO(AulaExtraDao.class).buscarAulasEnsinoIndividual(turma());
		}
	}
	
	/**
	 * Cadastra uma Aula de Ensino Individual e um tópico de aula associado a ela.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/AulaEnsinoIndividual/_form.jsp
	 * @throws ArqException 
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {

		registrarAcao(object.getDescricao(), EntidadeRegistroAva.AULA_ENSINO_INDIVIDUAL, AcaoAva.INICIAR_INSERCAO, object.getId());
		
		object.setTurma(turma());
		antesPersistir();
		object.setTipo(AulaExtra.ENSINO_INDIVIDUAL);
		prepare(SigaaListaComando.CADASTRAR_AVA);
		
		Notification notification = execute(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastro());
		
		if (notification.hasMessages()) {
			object.setId(0);
			return notifyView(notification);
		}
		
		// Registra inserção.
		registrarAcao(object.getDescricao(), EntidadeRegistroAva.AULA_EXTRA, AcaoAva.INSERIR, object.getId());
		
		if ( isNotificar() ){
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			String assunto = "Nova aula da turma: " + tBean.getTurma().getDescricaoSemDocente();
			String texto = "Uma nova aula foi cadastrada na turma virtual: " + tBean.getTurma().getDescricaoSemDocente() + "<p>Para visualizar mais informações acesse a turma virtual no SIGAA.</p>";
			notificarTurma(assunto, texto, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA );
		}

		cadastrarTopico();
		
		flash("Aula de Ensino Individual cadastrada com sucesso.");
	
		return redirect("/ava/index.jsf");
	}
	
	/**
	 * Remover uma aula de ensino individual se não estiver associada com um plano de aula.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/AulaEnsinoIndividual/listar.jsp
	 */
	public String remover() {
		
		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
			
			if ( object != null )
			{	
				ArrayList<PlanoReposicaoAula> planoAula = (ArrayList<PlanoReposicaoAula>) getGenericDAO().findByExactField(PlanoReposicaoAula.class, "aulaExtra.id", object.getId());
				
				if (planoAula != null && planoAula.size() > 0) {
					addMensagem(MensagensTurmaVirtual.AULA_EXTRA_ASSOCIADA_AO_PLANO_DE_AULA);
					return null;
				}
				
				registrarAcao(object.getDescricao(), EntidadeRegistroAva.AULA_ENSINO_INDIVIDUAL, AcaoAva.INICIAR_REMOCAO, object.getId());
				antesRemocao();
				
				Notification notification = null;
				if ( object != null ) {
					notification = execute(SigaaListaComando.REMOVER_AVA, object, getEspecificacaoRemocao());	
				}
	
				if (notification != null && notification.hasMessages())
					return notifyView(notification);
				
				registrarAcao(object.getDescricao(), EntidadeRegistroAva.AULA_ENSINO_INDIVIDUAL, AcaoAva.REMOVER, object.getId());
				listagem = null;		
				flash("Aula de ensino individual removida com sucesso.");
				
				if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
					TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
					return tBean.retornarParaTurma();
				}
			}else
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
		
		return listar();
		
	}
	
	/**
	 * Atualiza um objeto do tipo Aula de ensino individual da turma virtual.
	 * @return
	 */
	public String editar() {
		try {
			if (getParameterInt("id") == null) {
				addMensagemErro("Nenhum objeto para alterar.");
				return null;
			}
			
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
			
			if (object == null) {
				addMensagemErro("Nenhum objeto para alterar.");
				return null;
			}
			instanciarAposEditar();
			prepare(SigaaListaComando.ATUALIZAR_AVA);

		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 

		return forward(getDirBase() + "/editar.jsp");
	}
	
	/**
	 * Atualiza um objeto do tipo Aula de ensino individual e o exibe.
	 * @return
	 */
	public String atualizar() {

		try {
			
			registrarAcao(AcaoAva.INICIAR_ALTERACAO);

			object.setTurma(turma());
			antesPersistir();

			Specification specification = getEspecificacaoAtualizacao();
			if (specification == null || specification instanceof NullSpecification ) 
				specification = getEspecificacaoCadastro();

			Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, object, specification);

			if (notification.hasMessages()) {
				prepare(SigaaListaComando.ATUALIZAR_AVA);
				return notifyView(notification);
			}

			registrarAcao(AcaoAva.ALTERAR);
			listagem = null;
			flash("Aula de Ensino Individualizado atualizada com sucesso.");
			aposPersistir();
			if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
				TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
				return tBean.retornarParaTurma();
			}

		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}

		return forward(getDirBase() + "/mostrar.jsp");
	}
	
}
