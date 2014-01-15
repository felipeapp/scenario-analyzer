/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/01/2007
 *
 */
package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.cv.dominio.EnqueteComunidade;
import br.ufrn.sigaa.cv.dominio.EnqueteRespostaComunidade;
import br.ufrn.sigaa.cv.dominio.EnqueteVotosComunidade;

/**
 * Managed bean para gerenciar as enquetes de uma comunidade virtual
 * 
 * @author Agostinho
 */
@Component @Scope("session")
public class EnqueteComunidadeMBean extends CadastroComunidadeVirtual<EnqueteComunidade> {

	private String paginaOrigem;
	private EnqueteComunidade enqueteVisualizacao;
	//private EnqueteComunidade enqueteMaiAtual;

	/**
	 * Lista as enquetes de uma comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/listar.jsp
	 */
	@Override
	public List<EnqueteComunidade> lista() {
		return getDAO(ComunidadeVirtualDao.class).findEnquetesByComunidade(comunidade());
	}
	
	/**
	 * Edita uma enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/editar.jsp
	 */
	@Override
	public String editar() throws DAOException {
		try {
			
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
			instanciarAposEditar();
			prepare(SigaaListaComando.ATUALIZAR_CV);
			paginaOrigem = getParameter("paginaOrigem");
			return forward(getPaginaEdicao());
			
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 
	}
	
	/**
	 * Padrão Specification. Valida os campos obrigatórios antes de cadastrar uma enquete.<br/><br/>
	 * 
	 * Não invocado por JSP.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				EnqueteComunidade ref = (EnqueteComunidade) objeto;
				if (isEmpty(ref.getPergunta()))
					notification.addError("É obrigatório informar uma pergunta!");
				
				if ( ref.getRespostas().size() < 2 )
					notification.addError("É obrigatório informar pelo menos 2 respostas");
				
				List<EnqueteRespostaComunidade> listaRespostas = ref.getRespostas();
				for (EnqueteRespostaComunidade enqueteResposta : listaRespostas) {
					if ( isEmpty(enqueteResposta.getResposta()) ) {						
						notification.addError("Resposta não pode ser em branco!");
						break;
					}
				}
								
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Lista as enquetes de uma comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/listar.jsp
	 */
	@Override
	public String listar() {
		listagem = getDAO(ComunidadeVirtualDao.class).findEnquetesByComunidade(comunidade());
		return super.listar();
	}
	
	/**
	 * Padrão Specification. Valida os campos obrigatórios antes de atualizar uma enquete.
	 */
	@Override
	public Specification getEspecificacaoAtualizacao() {	
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				EnqueteComunidade ref = (EnqueteComunidade) objeto;
				if (isEmpty(ref.getPergunta()))
					notification.addError("É obrigatório informar uma pergunta!");
				
				if ( ref.getRespostas().size() < 2 )
					notification.addError("É obrigatório informar pelo menos 2 respostas");
				
				List<EnqueteRespostaComunidade> listaRespostas = ref.getRespostas();
				for (EnqueteRespostaComunidade enqueteResposta : listaRespostas) {
					if ( isEmpty(enqueteResposta.getResposta()) ) {						
						notification.addError("Resposta não pode ser em branco!");
					}
				}
								
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Instancia o domínio
	 */
	@Override
	public void instanciar() {
		object = new EnqueteComunidade();
		object.setPublicada(true);
		object.adicionarResposta();
		object.adicionarResposta();
	}

	/**
	 * Antes de remover, faz o detach do object
	 */
	@Override
	protected void antesRemocao() throws DAOException {
		getGenericDAO().detach(object);
	}
	
	/**
	 * Adiciona uma nova resposta à enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/novo.jsp
	 */
	public String novoItem() {
		object.adicionarResposta();
		return null;
	}

	/**
	 * Remove um item que tenha sido cadastrado como possível resposta para a enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/novo.jsp
	 * @return
	 */
	public String removerItem() {
		int index = getParameterInt("indice");
		object.removerResposta(index);
		return null;
	}

	/**
	 * Retorna a ultima enquete criada.
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException
	 */
	public EnqueteComunidade getEnqueteMaisAtual() {
		return getDAO(ComunidadeVirtualDao.class).findEnqueteMaisAtualByComunidade(comunidade());
	}

	/**
	 * Verifica se o usuário já votou nesta enquete.
	 * 
	 * @author Edson Anibal (ambar@info.ufnr.br)
	 */
	public EnqueteRespostaComunidade getRespostaUsuarioEnqueteMaisAtual() {
		return getDAO(ComunidadeVirtualDao.class).getRespostaUsuarioEnquete(getUsuarioLogado(), getEnqueteMaisAtual());
	}
	
	/**
	 * Verifica se o usuário já votou nesta enquete.
	 * 
	 * @author Edson Anibal (ambar@info.ufnr.br)
	 */
	public EnqueteRespostaComunidade getRespostaUsuarioEnquete() {
		return getDAO(ComunidadeVirtualDao.class).getRespostaUsuarioEnquete(getUsuarioLogado(), object);
	}
	
	/**
	 * Realizada o voto em uma determinada enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/votar.jsp
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String votar() throws NegocioException, ArqException, RemoteException {
		
		if ( getParameterInt("idEnqueteResposta") != null ) {
			prepareMovimento(ArqListaComando.CADASTRAR);
			int idRespostaSelecionada = getParameterInt("idEnqueteResposta");
			
			EnqueteRespostaComunidade respostaSelecionada = getGenericDAO().findByPrimaryKey(idRespostaSelecionada, EnqueteRespostaComunidade.class);		
			
			List<EnqueteRespostaComunidade> listaEnqResposta = new ArrayList<EnqueteRespostaComunidade>();		
								  listaEnqResposta.add(respostaSelecionada);																						
			
			EnqueteVotosComunidade enqueteVotos = new EnqueteVotosComunidade();
						 enqueteVotos.setEnqueteResposta( respostaSelecionada );
			  										
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(enqueteVotos);
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage("Voto cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
		}
		else {
			addMensagemWarning("É necessário escolher uma resposta!");
		}
		
		listagem = getDAO(ComunidadeVirtualDao.class).findEnquetesByComunidade(comunidade());
		return null;
	}
	
	/**
	 * Remove uma enquete e seus respectivos votos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/listar.jsp
	 * @return
	 */
	public String removerEnqueteComVotos() {
		
		try {
			
			prepareMovimento(SigaaListaComando.REMOVER_ENQUETE_COMUNIDADE);
			
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
			
			MovimentoCadastroAva mov = new MovimentoCadastroAva();
			mov.setObjMovimentado(object);
			mov.setCodMovimento(SigaaListaComando.REMOVER_ENQUETE_COMUNIDADE);
			execute(mov);
			
			listagem = getDAO(ComunidadeVirtualDao.class).findEnquetesByComunidade(comunidade());
			flash("Enquete removida com sucesso!");

		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return redirect("/cv/" + classe.getSimpleName() + "/listar.jsf");
		
	}

	/**
	 * Faz uma estatística de quantos votos uma opção tem.
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException 
	 */
	public List<EnqueteRespostaComunidade> getEstatisticaDeVotosMaisAtual() throws DAOException {
		return getDAO(ComunidadeVirtualDao.class).findEstatisticaDeVotosbyEnquete(getEnqueteMaisAtual());
	}
	
	/**
	 * Faz uma estatística de quantos votos uma opção tem.
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException 
	 */
	public List<EnqueteRespostaComunidade> getEstatisticaDeVotos() throws DAOException {
		return getDAO(ComunidadeVirtualDao.class).findEstatisticaDeVotosbyEnquete(object);
	}

	/**
	 * Direciona o usuário para a tela de votação.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/listar.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String telaVotacao() throws DAOException {
		popularEnquete();
		return forward("/cv/EnqueteComunidade/votar.jsp");
	}

	/**
	 * Seta em object a enquete do ID informado.
	 * 
	 * @throws DAOException
	 */
	private void popularEnquete() throws DAOException {
		int id = getParameterInt("id");
		object = getGenericDAO().findByPrimaryKey(id, EnqueteComunidade.class);
	}
	
	/**
	 * Redireciona para a tela de listagem após cadastrar uma enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/novo.jsp
	 */
	@Override
	public String forwardCadastrar() {
		return "/cv/" + classe.getSimpleName() + "/listar.jsf";
	}
	
	/**
	 * Após persistir, seta em object o que acabou de ser persistido.
	 * 
	 */
	@Override
	protected void aposPersistir() {
		try {
			object = getGenericDAO().findByPrimaryKey(object.getId(), EnqueteComunidade.class);
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Popula uma enquete (object).<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/EnqueteComunidade/votar.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getPopularEnquete() throws DAOException {
		popularEnquete();
		return "";
	}
	
	public String getPaginaOrigem() {
		return paginaOrigem;
	}

	public void setPaginaOrigem(String paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}
	
	public EnqueteComunidade getEnqueteVisualizacao() {
		return enqueteVisualizacao;
	}

	public void setEnqueteVisualizacao(EnqueteComunidade enqueteVisualizacao) {
		this.enqueteVisualizacao = enqueteVisualizacao;
	}
}
