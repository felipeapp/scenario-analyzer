/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on '29/01/2007'
 *
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.EnqueteDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.Enquete;
import br.ufrn.sigaa.ava.dominio.EnqueteResposta;
import br.ufrn.sigaa.ava.dominio.EnqueteVotos;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;

/**
 * MBean que gerencia as enquetes da turma virtual.
 * 
 * @author David
 */

@Component("enquete") @Scope("session")
public class EnqueteMBean extends CadastroTurmaVirtual<Enquete> {

	/** Enquete de visualização */
	private Enquete enqueteVisualizacao;
	/** Enquete mais atual */
	private Enquete enqueteMaiAtual;
	
	/** Hora final do prazo de votação */
	private Integer horaPrazo = 23;
	/** Minutos finais do prazo de votação */
	private Integer minutoPrazo = 59;

	/** Id do tópico de aula que a enquete está associada. */
	private int idTopicoAula;
	
	/** Enquete mais atual  */
	private Enquete enqueteMaisAtual;
	
	/**
	 * Retorna a lista contendo todas as enquetes da turma.<br/><br/>
	 * É public por causa da arquitetura.
	 * <br />
	 * Método não invocado por JSPs. 
	 */
	@Override
	public List<Enquete> lista() {
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		EnqueteDao dao = null;
		
		try {
			dao = getDAO(EnqueteDao.class);
			return dao.findEnquetesByTurma(turma(),tBean.isPermissaoDocente());
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
		
	}
	
	/**
	 * Indica a especificação dos campos necessários para o cadastro.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				Enquete ref = (Enquete) objeto;
				if (isEmpty(ref.getAula()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tópico de Aula");
				
				if (isEmpty(ref.getPergunta()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");
				
				if ( ref.getRespostas().size() < 2 )
					notification.addError("É obrigatório informar pelo menos 2 respostas");
				
				if (ref.getDataFim() == null)
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Prazo de Votação");
				else if (ref.getDataFim().getTime() < new Date().getTime())
					notification.addError("Insira um prazo de votação com data e hora maior ou igual a atual.");
				
				if (horaPrazo == null)
					notification.addError("Informe a hora para o final do prazo.");
				
				if (minutoPrazo == null)
					notification.addError("Informe os minutos para o final do prazo.");
				
				List<EnqueteResposta> listaRespostas = ref.getRespostas();
				List <String> auxRespostas = new ArrayList <String> ();
				
				for (EnqueteResposta enqueteResposta : listaRespostas) {
					
					enqueteResposta.setResposta(enqueteResposta.getResposta().trim());
					
					if (auxRespostas.contains(enqueteResposta.getResposta()))
						notification.addError("As respostas devem ser diferentes.");
					else
						auxRespostas.add(enqueteResposta.getResposta());
					
					if ( isEmpty(enqueteResposta.getResposta()) ){				
						notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Respostas");
					}
				}
								
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Indica a especificação dos campos necessários para a atualização.
	 */
	@Override
	public Specification getEspecificacaoAtualizacao() {	
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				Enquete ref = (Enquete) objeto;
				
				if (isEmpty(ref.getAula()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tópico de Aula");
				
				if (isEmpty(ref.getPergunta()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");
				
				if ( ref.getRespostas().size() < 2 )
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Prazo de Votação");
				
				if (ref.getDataFim() == null)
					notification.addError("Prazo de Votação: Campo obrigatório não informado!");
				else if (ref.getDataFim().getTime() < new Date().getTime())
					notification.addError("Insira um prazo de votação com data e hora maior ou igual a atual.");
				
				if (horaPrazo == null)
					notification.addError("Informe a hora para o final do prazo.");
				
				if (minutoPrazo == null)
					notification.addError("Informe os minutos para o final do prazo.");
				
				List<EnqueteResposta> listaRespostas = ref.getRespostas();
				List <String> auxRespostas = new ArrayList <String> ();
				
				for (EnqueteResposta enqueteResposta : listaRespostas) {
					
					enqueteResposta.setResposta(enqueteResposta.getResposta().trim());
					
					if (auxRespostas.contains(enqueteResposta.getResposta()))
						notification.addError("As respostas devem ser diferentes.");
					else
						auxRespostas.add(enqueteResposta.getResposta());
					
					if ( isEmpty(enqueteResposta.getResposta()) ){				
						notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Respostas");
					}
				}
								
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Instancia o objeto.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	public void instanciar() {
		object = new Enquete();
		object.setPublicada(true);
		object.adicionarResposta();
		object.adicionarResposta();
	}

	/**
	 * Prepara o objeto para remocao.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	protected void antesRemocao() throws DAOException {
		getGenericDAO().detach(object);
	}
	
	/**
	 * Adiciona um novo item à enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/ava/Enquete/_form.jsp</li>
	 * </ul>
	 */
	public String novoItem() {
		object.adicionarResposta();
		return null;
	}
	
	/**
	 * Remove um novo item da enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/Enquete/_form.jsp
	 */
	public String removerItem() {
		int index = getParameterInt("indice");
		object.removerResposta(index);
		return null;
	}
	

	/**
	 * Retorna a última enquete criada.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/barraDireita.jsp</li>
	 * 		<li>sigaa.war/portais/turma/cabecalho.jsp</li>
	 * </ul> 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException
	 */
	public Enquete getEnqueteMaisAtual() {
		
		EnqueteDao eDao = null;
		
		if (enqueteMaisAtual == null)
		
			try {
				eDao = getDAO(EnqueteDao.class);
				enqueteMaisAtual = eDao.findEnqueteMaisAtualByTurma(turma(), true);
			} finally {
				if (eDao != null)
					eDao.close();
			}
		
		return enqueteMaisAtual;
	}
	
	/**
	 * Zera a enquete mais atual.
	 * @return
	 */
	public String getZerarEnqueteMaisAtual (){
		enqueteMaisAtual = null;
		
		return null;
	}

	/**
	 * Verifica se o usuário já votou nesta enquete
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/barraDireita.jsp</li>
	 * </ul> 
	 * @author Edson Anibal (ambar@info.ufnr.br)
	 */
	public EnqueteResposta getRespostaUsuarioEnqueteMaisAtual() {
		return getDAO(EnqueteDao.class).getRespostaUsuarioEnquete(getUsuarioLogado(), getEnqueteMaisAtual());
	}
	
	/**
	 * Verifica se o usuário já votou nesta enquete.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/Enquete/votar.jsp</li>
	 * 		<li>sigaa.war/portais/turma/cabecalho.jsp</li>
	 * </ul> 
	 * @author Edson Anibal (ambar@info.ufnr.br)
	 */
	public EnqueteResposta getRespostaUsuarioEnquete() {
		return getDAO(EnqueteDao.class).getRespostaUsuarioEnquete(getUsuarioLogado(), object);
	}
	
	/**
	 * Cadastra um voto em uma enquete.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war/ava/Enquete/votar.jsp</li>
	 * 	<li>sigaa.war/ava/rodape.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String alo() throws NegocioException, ArqException, RemoteException {
		if(isEmpty(object)) {
			int id = getParameterInt("id");
			object = getGenericDAO().findByPrimaryKey(id, Enquete.class);
		}
		
		if (getRespostaUsuarioEnquete() != null) {
			addMensagemErro("Você já votou nesta enquete.");
			return null;
		}
		
		registrarAcao(object.getPergunta(), EntidadeRegistroAva.ENQUETE, AcaoAva.INICIAR_RESPOSTA, object.getId());
		
		if ( getParameterInt("idEnqueteResposta") != null ) {
			prepareMovimento(ArqListaComando.CADASTRAR);
			int idRespostaSelecionada = getParameterInt("idEnqueteResposta");
			
			EnqueteResposta respostaSelecionada = getDAO(EnqueteDao.class).findResposta(idRespostaSelecionada);		
			
			List<EnqueteResposta> listaEnqResposta = new ArrayList<EnqueteResposta>();		
								  listaEnqResposta.add(respostaSelecionada);																						
			
			EnqueteVotos enqueteVotos = new EnqueteVotos();
						 enqueteVotos.setEnqueteResposta( respostaSelecionada );
			  										
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(enqueteVotos);
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			executeWithoutClosingSession(mov, getCurrentRequest());
			registrarAcao(object.getPergunta(), EntidadeRegistroAva.ENQUETE, AcaoAva.RESPONDER, object.getId(), enqueteVotos.getId());
		}
		else {
			addMensagem(MensagensTurmaVirtual.ESCOLHER_RESPOSTA);
		}
		
		return null;

	}
	
	/**
	 * Método responsável pela remoção de um fórum.
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/aulas.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String removerDaTurma () throws ArqException{
		
		removerEnqueteComVotos();
		
		TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
		return tBean.retornarParaTurma();
	}
	
	/**
	 * Remove uma enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/Enquete/listar.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removerEnqueteComVotos() throws ArqException {
				
		prepareMovimento(SigaaListaComando.REMOVER_ENQUETE);
		
		int idEnqueteAtual = getParameterInt("id");
		
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			object = dao.findByPrimaryKey(idEnqueteAtual, Enquete.class);
			
			registrarAcao(object.getPergunta(), EntidadeRegistroAva.ENQUETE, AcaoAva.INICIAR_REMOCAO, idEnqueteAtual);
		
		MovimentoCadastroAva mov = new MovimentoCadastroAva();
		mov.setObjMovimentado(object);
		mov.setCodMovimento(SigaaListaComando.REMOVER_ENQUETE);
		
			execute(mov);
			registrarAcao(object.getPergunta(), EntidadeRegistroAva.ENQUETE, AcaoAva.REMOVER, object.getId());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
		
		listagem = null;
		return "";
	}

	/**
	 * Faz uma estatistica de quantos votos uma opção tem.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/barraDireita.jsp</li>
	 * </ul> 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException 
	 */
	public List<EnqueteResposta> getEstatisticaDeVotosMaisAtual() throws DAOException {
		return getDAO(EnqueteDao.class).findEstatisticaDeVotosbyEnquete(getEnqueteMaisAtual());
	}
	
	/**
	 * Faz uma estatistica de quantos votos uma opção tem.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/Enquete/votar.jsp</li>
	 * 		<li>sigaa.war/portais/turma/cabecalho.jsp</li>
	 * </ul> 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException 
	 */
	public List<EnqueteResposta> getEstatisticaDeVotos() throws DAOException {
		return getDAO(EnqueteDao.class).findEstatisticaDeVotosbyEnquete(object);
	}

	/**
	 * Retorna a quantidade de votos da enquete.
	 * 
 	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/Enquete/votar.jsp</li>
	 * </ul>
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @throws DAOException
	*/
	
	public String getTotalVotos() throws DAOException {
		EnqueteDao dao = getDAO(EnqueteDao.class);
		if (enqueteMaiAtual != null)
			return dao.findTotalVotosbyEnquete(this.enqueteMaiAtual);
		return null;
	}

	/**
	 * Retorna a enquete para visualização.
	 * 
	 * @return the enqueteVisualizacao
	 */
	public Enquete getEnqueteVisualizacao() {
		return enqueteVisualizacao;
	}

	/**
	 * Define a enquete para visualização.
	 * 
	 * @param enqueteVisualizacao the enqueteVisualizacao to set
	 */
	public void setEnqueteVisualizacao(Enquete enqueteVisualizacao) {
		this.enqueteVisualizacao = enqueteVisualizacao;
	}

	/**
	 * Exibe a tela para se votar em uma enquete.<br/><br/>
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/Enquete/listar.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String telaVotacao() throws DAOException {
		int id = getParameterInt("id");
		object = getGenericDAO().findByPrimaryKey(id, Enquete.class);
		if (getRespostaUsuarioEnquete() != null) {
			addMensagemInformation("Você já votou nesta enquete.");
		}
		
		return forward("/ava/Enquete/votar.jsp");
	}
	
	/**
	 * Atualiza o objeto após usa persistência.
	 */
	@Override
	protected void aposPersistir() {
		try {
			object = getGenericDAO().findByPrimaryKey(object.getId(), Enquete.class);
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Inicia o caso de uso de cadastrar um tópico de enquete e volta para a turma virtual.<br/><br/>
	 * 
	 * Chamado pela seguinte JSP: /sigaa.war/ava/aulas.jsp
	 * 
	 * 
	 * @param idTopicoSelecionado
	 * @return
	 */
	public String novoParaTurma(Integer idTopicoSelecionado) {
		this.idTopicoAula = idTopicoSelecionado;
		
		return novo();
	}
	
	/**
	 * Exibe o formulário para o cadastro de uma nova enquete.<br/><br>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/Enquete/listar.jsp
	 * 
	 */
	@Override
	public String novo (){
		horaPrazo = 23;
		minutoPrazo = 59;
		
		return super.novo();
	}
	
	/**
	 * Exibe o formulário para alteração de uma enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/Enquete/listar.jsp
	 */
	@Override
	public String editar (){
		String rs = super.editar();
		
		if (object != null){
			if (object.getDataFim() != null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(object.getDataFim());
				
				horaPrazo = cal.get(Calendar.HOUR_OF_DAY);
				minutoPrazo = cal.get(Calendar.MINUTE);
			} else {
				horaPrazo = 23;
				minutoPrazo = 59;
				object.setDataFim(new Date());
			}
		}
		
		return rs;
	}
	
	/**
	 * Cadastra uma nova enquete.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/Enquete/novo.jsp
	 */
	@Override
	public String cadastrar () throws ArqException{
		
		if (object != null && object.getDataFim() != null){
			
			if (idTopicoAula == 0)
				object.setAula(null);
			else
				object.setAula(new TopicoAula(idTopicoAula));
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(object.getDataFim());
			
			if (horaPrazo != null)
				cal.set(Calendar.HOUR_OF_DAY, horaPrazo);
			
			if (minutoPrazo != null)
				cal.set(Calendar.MINUTE, minutoPrazo);
			
			object.setDataFim(cal.getTime());
		}
		
		if (getCadastrarEm() == null){
			List <String> turmas = new ArrayList <String> ();
			turmas.add("" + turma().getId());
			setCadastrarEm(turmas);
		}
		
		return super.cadastrar();		
	}

	
	/**
	 * Atualiza uma enquete já existente.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/Enquete/editar.jsp
	 */
	@Override
	public String atualizar (){
		
		if (object != null && object.getDataFim() != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(object.getDataFim());
			
			if (horaPrazo != null)
				cal.set(Calendar.HOUR_OF_DAY, horaPrazo);
			
			if (minutoPrazo != null)
				cal.set(Calendar.MINUTE, minutoPrazo);
			
			object.setDataFim(cal.getTime());
		}
		object.setAula(new TopicoAula(idTopicoAula));
		setPaginaOrigem(getParameter("paginaOrigem"));
		
		return super.atualizar();
	}

	public Integer getHoraPrazo() {
		return horaPrazo;
	}

	public void setHoraPrazo(Integer horaPrazo) {
		this.horaPrazo = horaPrazo;
	}

	public Integer getMinutoPrazo() {
		return minutoPrazo;
	}

	public void setMinutoPrazo(Integer minutoPrazo) {
		this.minutoPrazo = minutoPrazo;
	}

	public int getIdTopicoAula() {
		return idTopicoAula;
	}

	public void setIdTopicoAula(int idTopicoAula) {
		this.idTopicoAula = idTopicoAula;
	}

}
