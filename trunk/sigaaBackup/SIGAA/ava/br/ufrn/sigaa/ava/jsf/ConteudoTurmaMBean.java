/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;

/**
 * Managed bean para cadastro de conte�dos para a turma virtual.
 * 
 * @author David Pereira
 * 
 */
@Component("conteudoTurma") @Scope("request")
public class ConteudoTurmaMBean extends CadastroTurmaVirtual<ConteudoTurma> {

	/** P�gina de origem do conte�do. */
	private String paginaOrigem;
	
	@Override
	public List<ConteudoTurma> lista() {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		return getDAO(TurmaVirtualDao.class).findConteudoTurma(turma(),tBean.isPermissaoDocente());
	}
	
	/**
	 * Edita um conte�do de turma.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSP:
	 * <ul>
	 * 	<li>/ava/ConteudoTurma/conteudo.jsp</li>
	 * 	<li>/ava/ConteudoTurma/listar.jsp</li>
	 * 	<li>/ava/aulas.jsp</li>
	 * </ul>
	 */
	public String editar() {
		try {
			Integer id = getParameterInt("id");
			if(isEmpty(id)) {
				addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
				return cancelar();
			}
			object = getGenericDAO().findByPrimaryKey(id, getClasse());
			instanciarAposEditar();
			prepare(SigaaListaComando.ATUALIZAR_AVA);
			
			paginaOrigem = getParameter("paginaOrigem");
			
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 
		return forward("/ava/" + getClasse().getSimpleName() + "/editar.jsp");
	}
	
	/**
	 * Atualiza as informa��es de um conte�do de uma turma.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /ava/ConteudoTurma/editar.jsp
	 */
	public String atualizar() {
		try {
			object.setTurma(turma());	
			object.setMaterial(getGenericDAO().findByExactField(MaterialTurma.class, "idMaterial", object.getId(), true));
			
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.CONTEUDO, AcaoAva.INICIAR_ALTERACAO, object.getId());
			antesPersistir();
			
			Specification specification = getEspecificacaoAtualizacao();
			if (specification == null || specification instanceof NullSpecification ) 
				specification = getEspecificacaoCadastro();

			Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, object, specification);

			if (notification.hasMessages()) {
				prepare(SigaaListaComando.ATUALIZAR_AVA);
				return notifyView(notification);
			}
	
			listagem = null;
			flash("Conte�do atualizado com sucesso!");
			aposPersistir();
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.CONTEUDO, AcaoAva.ALTERAR, object.getId());

			if ( object.isNotificarAlunos() && !notification.hasMessages()) {
				
				String assunto = "Material alterado: " +  object.getNome() + " - " + turma().getDescricaoSemDocente();
				String texto = "Um material foi alterado na turma virtual: " + turma().getDescricaoSemDocente() + "<p>Para visualiza-lo acesse a turma virtual no SIGAA</p>";
				notificarTurma(assunto, texto, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE , ControllerTurmaVirtual.AUTORIZADO , ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);	
			}
			
			if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
					TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
					return tBean.retornarParaTurma();
			} else
				return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
		} catch (DAOException e){
			tratamentoErroPadrao(e);
			return null;
		}
	}

	@Override
	protected void antesRemocao() throws DAOException {
		getGenericDAO().clearSession();
		// getGenericDAO().close();
	}

	/**
	 * Redefini��o da p�gina ap�s o cadastrar.
	 * 
	 * M�todo n�o invocado por JSPs:
	 *  
	 * @return
	 */
	@Override
	public String forwardCadastrar() {
		return "/ava/" + getClasse().getSimpleName() + "/listar.jsf";
	}
	
	/**
	 * Remove um conte�do de uma turma.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/ava/ConteudoTurma/listar.jsp</li>
	 * 	<li>/ava/aulas.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() {
		
		try {
			prepare(SigaaListaComando.REMOVER_AVA);
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"),
					getClasse());
			registrarAcao(object.getNome(), EntidadeRegistroAva.CONTEUDO, AcaoAva.INICIAR_REMOCAO, object.getId());
			antesRemocao();
			Notification notification = execute(SigaaListaComando.REMOVER_AVA,
					object, getEspecificacaoRemocao());

			if (notification.hasMessages())
				return notifyView(notification);
			
			registrarAcao(object.getNome(), EntidadeRegistroAva.CONTEUDO, AcaoAva.REMOVER, object.getId());
			listagem = null;
			flash("Removido com sucesso.");

			TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
			return tBean.retornarParaTurma();
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();

			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				ConteudoTurma ct = (ConteudoTurma) objeto;
				if (isEmpty(ct.getNome()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�tulo");
				if (ct.getAula() == null || ct.getAula().getId() == 0)
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�pico de Aula");
				if (isEmpty(ct.getConteudo()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conte�do");
				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Mostra informa��es sobre um conte�do de turma.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/ava/ConteudoTurma/editar.jsp</li>
	 * 	<li>/ava/ConteudoTurma/listar.jsp</li>
	 * 	<li>/ava/aulas.jsp</li>
	 * </ul>
	 */
	public String mostrar() throws DAOException {
		
		
		Integer id = getParameterInt("id");
		
		if(isEmpty(id)) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		
		object = getGenericDAO().findByPrimaryKey(id, getClasse());
		
		registrarAcao(object.getNome(), EntidadeRegistroAva.CONTEUDO, AcaoAva.ACESSAR, id);
		
		//Registro o acesso do discente somente quando usu�rio logado e turma virtual setada
		if( turma() != null )
			registrarLogAcessoDiscenteTurmaVirtual( getClasse().getName(), id, turma().getId());
            
        
        if (acessoPublico())
            return forward("/ava/" + getClasse().getSimpleName() + "/mostrarPublico.jsp");
        else
            return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
    }
	
	/**
	 * Mostra que redireciona o sistema para p�gina de cadastro de conte�do atrav�s do ComboBox do t�pico de aula.<br/><br/>
	 * 
	 * M�todo n�o invocados por JSPs:
	 */
	public void inserirConteudo(Integer idTopicoSelecionado) {
		object = new ConteudoTurma();
		object.setAula(new TopicoAula(idTopicoSelecionado));
		forward("/ava/ConteudoTurma/novo.jsp");
	}
	
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}

	public String getPaginaOrigem() {
		return paginaOrigem;
	}

	public void setPaginaOrigem(String paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}
	
}
