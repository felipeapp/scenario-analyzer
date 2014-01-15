/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/08/2010
 *
 */

package br.ufrn.sigaa.ava.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ava.dao.PerfilUsuarioAvaDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.PerfilUsuarioAva;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed Bean para a ger�ncia do perfil do usu�rio para cada turma virtual.
 * 
 * @author Fred_Castro
 *
 */
@Component("perfilUsuarioAva") @Scope("request")
public class PerfilUsuarioAvaMBean extends ControllerTurmaVirtual {
	
	private PerfilUsuarioAva object;
	
	public PerfilUsuarioAvaMBean () {
		object = new PerfilUsuarioAva();
		object.setPessoa(new Pessoa());
		object.setTurma(new Turma());
	}

	/**
	 * Inicia o caso de uso de gerenciar o perfil do usu�rio em uma turma virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP:
	 * 
	 * <ul>
	 * 	<li>/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar () throws ArqException {
		
		Pessoa pessoa = getUsuarioLogado().getPessoa();
		Turma turma = ((TurmaVirtualMBean) getMBean("turmaVirtual")).getTurma();
		
		// TODO Atualmente s� � permitido aos discentes cadastrar o perfil.
		if (getDiscenteUsuario() == null){
			addMensagemErro("Voc� precisa estar com v�nculo de discente para realizar esta opera��o.");
			return null;
		}
		
		if (turma == null){
			addMensagemErro("Voc� deve acessar uma turma antes de realizar esta opera��o.");
			return null;
		}
		
		PerfilUsuarioAvaDao dao = null;
		
		try {
			dao = getDAO(PerfilUsuarioAvaDao.class);
			object = dao.findPerfilByPessoaTurma(pessoa, turma);
			
			if (object == null){
				prepareMovimento(ArqListaComando.CADASTRAR);
				
				object = new PerfilUsuarioAva();
				object.setPessoa(pessoa);
				object.setTurma(turma);
				
			} else
				prepareMovimento(ArqListaComando.ALTERAR);
			
			return forward("/ava/PerfilUsuarioAva/form.jsp");
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Salva as altera��es do perfil do usu�rio.<br/><br/>
	 * 
 	 * M�todo chamado pela seguinte JSP:
	 * 
	 * <ul>
	 * 	<li>/ava/PerfilUsuarioAva/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String salvar () throws ArqException {
		
		try {
			// Verifica se � um cadastro ou uma altera��o.
			boolean isCadastro = object.getId() == 0;
			
			if ( isCadastro )
				registrarAcao(null, EntidadeRegistroAva.PERFIL,AcaoAva.INICIAR_INSERCAO);
			else
				registrarAcao(null, EntidadeRegistroAva.PERFIL,AcaoAva.INICIAR_ALTERACAO,object.getId());

			MovimentoCadastro mov = new MovimentoCadastro(object);
			mov.setCodMovimento(object.getId() == 0 ? ArqListaComando.CADASTRAR : ArqListaComando.ALTERAR);
			
			ListaMensagens lm = object.validate();
			if (lm.isErrorPresent()){
				NegocioException e = new NegocioException();
				e.setListaMensagens(lm);
				throw e;
			}
			
			execute(mov);
			
			if ( isCadastro )
				registrarAcao(null, EntidadeRegistroAva.PERFIL,AcaoAva.INSERIR,object.getId());
			else
				registrarAcao(null, EntidadeRegistroAva.PERFIL,AcaoAva.ALTERAR,object.getId());
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			return tBean.entrar();
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	public PerfilUsuarioAva getObject() {
		return object;
	}

	public void setObject(PerfilUsuarioAva object) {
		this.object = object;
	}
}