/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/01/2008
 */
package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;
import java.util.Set;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.DominioComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;
import br.ufrn.sigaa.cv.negocio.MovimentoCadastroCv;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Layer Supertype (http://martinfowler.com/eaaCatalog/layerSupertype.html) para
 * os managed beans da turma virtual.
 * 
 * @author David Pereira
 * 
 */
public abstract class ControllerComunidadeVirtual extends AbstractController {

	protected List<String> cadastrarEm;
	
	/**
	 * Exibe as mensagens passadas na notification.
	 * 
	 * @param notification
	 * @return
	 */
	protected String notifyView(Notification notification) {
		addMensagens(notification.getMessages());
		return null;
	}

	/**
	 * Retorna a comunidade virtual atual ou exibe a mensagem de erro caso nenhuma tenha sido escolhida.
	 * 
	 * @return
	 */
	public ComunidadeVirtual comunidade() {
		ComunidadeVirtualMBean comunidadeMBean = (ComunidadeVirtualMBean) getMBean("comunidadeVirtualMBean");
		if (comunidadeMBean.getComunidade() == null) {
			if (getParameterInt("cid") == null) {
				throw new RuntimeNegocioException("Nenhuma comunidade virtual foi selecionada.");
			} else {
				int id = getParameterInt("cid");
				try {
					return getDAO(ComunidadeVirtualDao.class).findByPrimaryKey(id, ComunidadeVirtual.class);
				} catch (DAOException e) {
					throw new TurmaVirtualException(e);
				}
			}
		}
		return comunidadeMBean.getComunidade();
	}

	/**
	 * Prepara a arquitetura para receber um movimento com o comando passado.
	 * @param comando
	 */
	protected void prepare(Comando comando) {
		try {
			prepareMovimento(comando);
		} catch (ArqException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Executa o comando passado utilizando o objeto e especificação passados.
	 * 
	 * @param comando
	 * @param object
	 * @param specification
	 * @return
	 */
	protected Notification execute(Comando comando, DominioComunidadeVirtual object,
			Specification specification) {
		try {
			MovimentoCadastroCv mov = new MovimentoCadastroCv();
			mov.setCodMovimento(comando);
			mov.setObjMovimentado(object);
			mov.setComunidade(comunidade());
			mov.setSpecification(specification);
			mov.setMensagem(object.getMensagemAtividade());
			return (Notification) executeWithoutClosingSession(mov,
					getCurrentRequest());
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Exibe a mensagem passada.
	 * 
	 * @param mensagem
	 */
	protected void flash(String mensagem) {
		addMessage(mensagem, TipoMensagemUFRN.INFORMATION);
	}

	/**
	 * Notifica a turma toda sobre um determinado assunto
	 * 
	 * @param assunto
	 * @param texto
	 * @throws DAOException 
	 */
	public void notificarComunidade(String assunto, String texto) throws DAOException {

		ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class);

		Set<MembroComunidade> listParticipantes = dao.findParticipantes( comunidade() );
		
		if (!isEmpty(listParticipantes)) {
			for (MembroComunidade mat : listParticipantes) {
	
					MailBody body = new MailBody();
	
					body.setAssunto(assunto);
					body.setMensagem(texto);
					body.setFromName("SIGAA - " + comunidade().getNome() );
					if (mat.getUsuario() != null) {
						body.setEmail(mat.getUsuario().getEmail());
						Mail.send(body);
					}
			}
		}
		
	}

	/**
	 * Retorna o usuário logado
	 */
	//Suppress adicionado por que o método herdado usa um unchecked cast
	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return (Usuario) getCurrentRequest().getSession().getAttribute("usuario");
	}

	/**
	 * Retorna o servidor do usuário logado
	 */
	public Servidor getServidorUsuario() {
		return getUsuarioLogado().getServidor();
	}

	public DiscenteAdapter getDiscenteUsuario() {
		return getUsuarioLogado().getDiscenteAtivo();
	}
	
	/**
	 * Usado para que após remover ou modificar algum tópico aula o sistema busque-os novamente
	 * juntamente com seus materiais.
	 */
	public void clearCacheTopicosAula() {
		
		TopicoComunidadeMBean topico = getMBean("topicoComunidadeMBean");
		topico.setTopicosComunidade(null);
		
	}

	public List<String> getCadastrarEm() {
		return cadastrarEm;
	}

	public void setCadastrarEm(List<String> cadastrarEm) {
		this.cadastrarEm = cadastrarEm;
	}

}
