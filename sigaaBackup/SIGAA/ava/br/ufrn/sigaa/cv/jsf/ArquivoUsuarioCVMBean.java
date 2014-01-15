/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.jsf;

import java.util.Date;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.PortaArquivosDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.administracao.dominio.ConfiguracaoPortaArquivos;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.cv.dominio.ArquivoComunidade;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;
import br.ufrn.sigaa.cv.negocio.MovimentoArquivoCV;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Controlador para o porta arquivos do usuário da Comunidade Virtual
 *
 * @author Agostinho
 */
@Component
@Scope("request")
public class ArquivoUsuarioCVMBean extends ControllerComunidadeVirtual {

	/** Objeto movimentado */
	private ArquivoUsuario obj;
	/** Arquivo de Upload, o arquivo que foi enviado */
	private UploadedFile arquivo;
	/** Arquivo associado a uma comunidade */
	private ArquivoComunidade arquivoComunidade;
	/** Se é para notificar os membros da comunidade*/
	private boolean notificar;
	/**
	 * Instância o domínio relacionado ao MBean.
	 * 
	 * @throws Exception
	 */
	public ArquivoUsuarioCVMBean() throws Exception {
		obj = new ArquivoUsuario();

		arquivoComunidade = new ArquivoComunidade();
		arquivoComunidade.setTopico(new TopicoComunidade());
	}

	/**
	 * Retorna o dono do porta arquivos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	private Usuario getDonoPortaArquivos() throws DAOException {
		return getUsuarioLogado();
	}

	/**
	 * Cadastra um novo arquivo na Comunidade Virtual.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/arquivo.jsp
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		try {

			if (obj.getId() == 0) { // Cadastrar um novo arquivo. Se não entrar é porque está associando um arquivo existente

				if (arquivo == null || arquivo.getSize() == 0)
					throw new NegocioException("Escolha um arquivo para enviar ao porta-arquivos.");

				if ((getTamanhoTotalOcupado() + arquivo.getSize()) > getTamanhoPortaArquivos())
					throw new NegocioException("Não é possível inserir o arquivo porque o porta-arquivos está cheio.");

				String fileName = arquivo.getName();
				int pos = fileName.lastIndexOf("\\");
				fileName = fileName.substring(pos + 1);
				obj.setNome(fileName);
				obj.setTamanho(arquivo.getSize());
				obj.setData(new Date());
				obj.setUsuario(getDonoPortaArquivos());

				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
				obj.setIdArquivo(idArquivo);
			}

			TopicoComunidadeMBean topicoComunidadeMBean = (TopicoComunidadeMBean) getMBean("topicoComunidadeMBean");
			if (arquivoComunidade.getTopico().getId() == 0) {
				if (CollectionUtils.isEmpty(topicoComunidadeMBean.getComboIdentado())){
					throw new NegocioException("Não há nenhum tópico cadastrado nessa comunidade. Por favor, cadastre um tópico primeiro.");
				}else{
					throw new NegocioException("Selecione um tópico da comunidade.");
				}
			}
			
			cadastrarEm.add( String.valueOf( comunidade().getId()) );
			
			MovimentoArquivoCV mov = new MovimentoArquivoCV();
			mov.setArquivo(obj);
			mov.setArquivoComunidade(arquivoComunidade);
			mov.setCadastrarEm(cadastrarEm);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_ARQUIVO_CV);

			Notification n = (Notification) executeWithoutClosingSession(mov, getCurrentRequest());
			if (n != null && n.hasMessages())
				return notifyView(n);
			
			if (notificar){
				String assunto = "Novo arquivo cadastrado(a) na comunidade virtual: " + comunidade().getNome();
				String texto = "Um novo aquivo foi disponibilizado(a) na comunidade virtual: <b>"+comunidade().getNome()+"</b> <p>Para visualizar acesse a comunidade virtual no SIGAA.</p>";
				notificarComunidade(assunto,texto);
			}	
			addMensagemInformation("Arquivo " + (arquivo != null ? obj.getNome() : "")+ " inserido com sucesso.");
			
			reset();
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage() + "." + " Por favor, tente novamente usando o link 'Inserir Arquivo'.");
			return redirect("/cv/index.jsf");
		}
		
		return redirect("/cv/index.jsf");
	}

	/**
	 * Redireciona usuário para a página principal.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/arquivo.jsp
	 */
	@Override
	public String cancelar() {
		return redirect("/cv/index.jsf");
	}
	
	/**
	 * Total de espaço ocupado no Porta-Arquivos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/arquivo.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public long getTamanhoTotalOcupado() throws DAOException {
		PortaArquivosDao dao = getDAO(PortaArquivosDao.class);
		return dao.findTotalOcupadoByUsuario(getUsuarioLogado().getId());
	}

	/**
	 * Total de espaço ocupado no Porta-Arquivos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/arquivo.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public long getTamanhoPortaArquivos() throws ArqException, NegocioException {
		long tamanho = 0;

		try {
			ConfiguracaoPortaArquivos config = null;

			config = getGenericDAO().findByExactField(ConfiguracaoPortaArquivos.class, "usuario.id", getUsuarioLogado().getId(), true);		
			if ( config == null )
				tamanho = ParametroHelper.getInstance().getParametroLong(ConstantesParametro.TAMANHO_MAXIMO_PORTA_ARQUIVOS);
			else
				tamanho = config.getTamanhoMaximoPortaArquivos();
		} catch(NullPointerException e) {
			throw new NegocioException("O tamanho do porta-arquivos não foi configurado. Por favor, entre em contato com a administração do sistema.");
		}
		return tamanho;

	}

	/**
	 * Delega a inserção de arquivos na Comunidade Virtual para o método overloaded inserirArquivoComunidade(Integer idTopico).<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * @return
	 */
	public String inserirArquivoComunidade() {
		return inserirArquivoComunidade(null);
	}
	
	/**
	 * Prepara o movimento e insere o arquivo.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/include/_menu_comunidade.jsp
	 * 
	 * @param idTopico
	 * @return
	 */
	public String inserirArquivoComunidade(Integer idTopico) {
		
		reset();
		
		if (idTopico != null && idTopico > 0) {
			arquivoComunidade.setTopico(new TopicoComunidade());
			arquivoComunidade.getTopico().setId(idTopico);
		}

		try {

			prepareMovimento(SigaaListaComando.CADASTRAR_ARQUIVO_CV);
			MovimentoArquivoCV mov = new MovimentoArquivoCV();
			mov.setUsuario(getDonoPortaArquivos());
			mov.setComunidade(comunidade());
			
		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
		
		return forward("/cv/arquivo.jsp");
	}

	/**
	 * Reseta os objetos relacionados ao domínio.
	 */
	private void reset() {
		obj = new ArquivoUsuario();
		arquivoComunidade = new ArquivoComunidade();
		arquivoComunidade.setTopico(new TopicoComunidade());
	}

	/**
	 * Localiza um ArquivoUsuario pelo ID.<br/><br/>
	 * 
	 * Não invocado por JSP.
	 */
	public String getAssociarATopico() throws DAOException {
		int id = getParameterInt("id");
		GenericDAO dao = getDAO(GenericSigaaDAO.class);
		ArquivoUsuario arquivo = dao.findByPrimaryKey(id, ArquivoUsuario.class);
		this.obj = arquivo;

		return "";
	}

	/**
	 * Remove a associação do ArquivoUsuario da comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/principal.jsp
	 * @return
	 */
	public String removerAssociacaoArquivo() {
		
		try {
			int id = getParameterInt("id");
			this.arquivoComunidade = new ArquivoComunidade();
			this.arquivoComunidade.setId(id);

			prepareMovimento(ArqListaComando.REMOVER);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(this.arquivoComunidade);
			mov.setCodMovimento(ArqListaComando.REMOVER);

			executeWithoutClosingSession(mov, getCurrentRequest());
			
			addMessage("Arquivo removido com sucesso!", TipoMensagemUFRN.INFORMATION);
			
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch(Exception e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		return redirect("/cv/index.jsf");
	}

	public ArquivoUsuario getObj() {
		return obj;
	}

	public void setObj(ArquivoUsuario obj) {
		this.obj = obj;
	}

	public String getSelecionarTurmaPadrao() {
		return null;
	}

	public ArquivoComunidade getArquivoComunidade() {
		return arquivoComunidade;
	}

	public void setArquivoComunidade(ArquivoComunidade arquivoComunidade) {
		this.arquivoComunidade = arquivoComunidade;
	}
	
	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}

	public boolean isNotificar() {
		return notificar;
	}
}