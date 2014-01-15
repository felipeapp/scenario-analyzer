package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaInicioFim;
import static br.ufrn.arq.util.ValidatorUtil.validaInt;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.sigaa.apedagogica.dao.GrupoAtividadesAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeArquivo;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.MensagensAP;
import br.ufrn.sigaa.apedagogica.negocio.MovimentoGrupoAtividadesAP;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controller que gerencia as operações de cadastro do grupo e atividades
 * de atualização pedagógica.
 * @author Mário Rizzi
 *
 */
@Component("grupoAtividadesAP") @Scope("request")
public class GrupoAtividadesAPMBean 
	extends SigaaAbstractController<GrupoAtividadesAtualizacaoPedagogica> {

	/** Atividade que está sendo associado a lista do grupo */
	private AtividadeArquivo atividadeArquivo;
	/** Pessoa que está sendo associado como instrutor da atividade do grupo */
	private Pessoa pessoa;
	/** Texto exibido no botão de adicionar/alterar atividade ao grupo. */
	private String confirmButtonAtividade;
	/** Possui as atividades do grupo selecionado */
	private Collection<SelectItem> atividadesCombo;
	/** Possui uma lista contendo as atividades e seus respectivos arquivos. */
	private List<AtividadeArquivo> listaAtividadesAnexo;
	/** Arquivo anexo a atividade se existir. */
	private UploadedFile arquivo;
	/** Docente Externo */
	private DocenteExterno docExterno;
	
	/** Habilita a visualização dos botões alterar e remover na lista de participações.	 */
	private boolean enableAlterarRemoverAtividade;
	
	public GrupoAtividadesAPMBean(){
 		obj = new GrupoAtividadesAtualizacaoPedagogica();
 		atividadeArquivo = new AtividadeArquivo();
 		pessoa = new Pessoa();
 		docExterno = new DocenteExterno();
 		docExterno.setPessoa(new Pessoa());
 		all = null;
 		setConfirmButtonAtividade("Adicionar Atividade");
 		setEnableAlterarRemoverAtividade(true);
 	}
 	
 	/**
 	 * Define o diretório base para as operações de CRUD.
 	 * Método não invocado por JSP's.
 	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getDirBase() 
 	 */
 	@Override
 	public String getDirBase() {
 		return "/apedagogica/GrupoAtividadesAP";
 	}
 	
 	/**
 	 * Indica a JSP do primeiro passo referente ao cadastro ou atualização 
 	 * do grupo de atividades.
 	 * Método não invocado por JSP's.
 	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getFormPage() 
 	 */
 	@Override
 	public String getFormPage() {
 		return getDirBase() + "/form_grupo_atividade.jsf";
 	}
 	 	
 	/**
 	 * Define os usuário que poderão realizar as ações de CRUD.
 	 * Método não invocado por JSP's.
 	 */
 	@Override
 	public void checkChangeRole() throws SegurancaException {
 		checkRole(SigaaPapeis.GESTOR_PAP);
 	}
 	
 	
 	/**
 	 * Método que cadastra o grupo e suas atividades associadas
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul> 
 	 */
 	@Override
 	public String cadastrar() throws SegurancaException, ArqException,
 			NegocioException {
 		
 		validateFormAtividadesGrupo();
 		
 		if(hasErrors())
 			return null;
 		
 		MovimentoGrupoAtividadesAP mov = new MovimentoGrupoAtividadesAP();
		mov.setObjMovimentado(obj);
		mov.setListaAtividadesAnexo(getListaAtividadesAnexo());
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA);
		
		try{
			execute(mov);
			if( !getConfirmButton().equals("Alterar") )
				addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Grupo de Atividade");
			else 
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Grupo de Atividade");
		}catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}	
		
		return redirect( getSubSistema().getLink());
		
 	}
 	
 	/**
 	 * Método que carrega os instrutores antes de exibir o formulário para alteração.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_grupo_atividade.jsp</li>
 	 * </ul>  
 	 */
 	@Override
 	public String preCadastrar() throws ArqException, NegocioException {
 		checkChangeRole();
		prepareMovimento(SigaaListaComando.CADASTRAR_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
 	}
 
 	/**
 	 * Método que prepara o formulário para alteração
 	 * do grupo e suas atividades.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/lista.jsp</li>
 	 * </ul>
 	 */
 	@Override
 	public String atualizar() throws ArqException {

			setOperacaoAtiva(SigaaListaComando.CADASTRAR_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA.getId());
			prepareMovimento(SigaaListaComando.CADASTRAR_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA);
			populateObj(true);
			setConfirmButton("Alterar");
			
			if( !isEmpty(obj) ){
	 			
				obj = getGenericDAO().findAndFetch(obj.getId(), GrupoAtividadesAtualizacaoPedagogica.class, "atividades");

	 			for (AtividadeAtualizacaoPedagogica a : obj.getAtividades())
					if( !isEmpty(a.getInstrutores()) )
						a.getInstrutores().iterator();
				for (AtividadeAtualizacaoPedagogica a : obj.getAtividades()) 
					addListaAtividadeAnexo(new AtividadeArquivo(a, null));

	 		}

		return forward(getFormPage());
 	}
 	
 	/**
 	 * Método que remove um grupo da listagem.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/lista.jsp</li>
 	 * </ul>
 	 */
 	@Override
 	public String remover() throws ArqException {
 		
 		removeOperacaoAtiva();
 		setOperacaoAtiva(SigaaListaComando.REMOVER_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA.getId());
 		prepareMovimento(SigaaListaComando.REMOVER_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA);
 		populateObj(true);

 		MovimentoGrupoAtividadesAP mov = new MovimentoGrupoAtividadesAP();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.REMOVER_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA);
		
 		try {
			execute(mov);
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Grupo de Atividade");
		} catch (Exception e) {
			addMensagem(MensagensAP.GRUPO_POSSUI_PARTICIPANTE_ATIVIDADES);
		} 
 		
		return redirect(getListPage());
 	}
 	
	/**
 	 * Caminho do formulário de cadastro e alteração
 	 * do grupo de atividades.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @return
 	 */
 	public String formGrupo(){
 		return forward(getFormPage());
 	}
 	
 	/**
	 * Caminho do formulário de cadastro e alteração
 	 * das atividades do grupo.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_grupo_atividade.jsp</li>
 	 * </ul>
 	 * @return
 	 * @throws DAOException 
 	 */
 	public String formAtividade() throws DAOException{
		
 		validateFormGrupo();
 		
 		if( hasErrors() )
 			return null;
 		
 		pessoa = new Pessoa();
 		atividadeArquivo = new AtividadeArquivo();
 		setConfirmButtonAtividade("Adicionar Atividade");
 		setEnableAlterarRemoverAtividade(true);
 		
		return forward(getDirBase() + "/form_atividade.jsf");
		
 	}
 	
 	/**
 	 * Carrega a pessoa no autocomplete.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @param e
 	 * @throws DAOException 
 	 */
 	public void carregaPessoa(ActionEvent e) throws DAOException{
 		Integer idServidor = (Integer) e.getComponent().getAttributes().get("idServidor");
 		if( isEmpty(idServidor) ){
			addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Servidor da instituição");
		}else{
			Servidor servidor = getGenericDAO().findByPrimaryKey(idServidor, Servidor.class, "id,pessoa.id,pessoa.nome");
			pessoa = servidor.getPessoa();
		}
 	}
 	
 	/**
 	 * Carrega a pessoa no autocomplete.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @param e
 	 * @throws DAOException 
 	 */
 	public void carregaDocenteExterno(ActionEvent e) throws DAOException{
 		Integer idDocenteExterno = (Integer) e.getComponent().getAttributes().get("idDocenteExt");
 		if( isEmpty(idDocenteExterno) ){
			addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Docente Externo");
		}else{
			docExterno = getGenericDAO().findByPrimaryKey(idDocenteExterno, DocenteExterno.class, "id,pessoa.id,pessoa.nome");
		}
 	}
 	
 	/**
 	 * Adiciona uma pessoa a lista de instrutores 
 	 * da atividade que está sendo cadastrada ou atualizada.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @param e
 	 * @throws DAOException
 	 */
	public void addInstrutor(ActionEvent e) throws DAOException {
		
		validateFormInstrutor();
		
		if ( !hasErrors() && !isEmpty(pessoa) ) {
			atividadeArquivo.getAtividade().addInstrutor(getGenericDAO().refresh(pessoa));
			pessoa = new Pessoa();
		}else
			addMensagemAjax(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Docente");
	}
	
 	/**
 	 * Adiciona um docente Externo a lista de instrutores 
 	 * da atividade que está sendo cadastrada ou atualizada.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @param e
 	 * @throws DAOException
 	 */
	public void addInstrutorExterno(ActionEvent e) throws DAOException {
		
		if (docExterno != null && docExterno.getPessoa() != null)
			pessoa = docExterno.getPessoa();
		
		validateFormInstrutor();
		
		if ( !hasErrors() && !isEmpty(pessoa) ) {
			atividadeArquivo.getAtividade().addInstrutor(getGenericDAO().refresh(pessoa));
			pessoa = new Pessoa();
			docExterno = new DocenteExterno();
			docExterno.setPessoa(new Pessoa());
		}else
			addMensagemAjax(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Docente Externo");
	}
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas físicas
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> autocompleteNomePessoa(Object event) throws DAOException {
		String nome = event.toString();
		return getDAO(PessoaDao.class).findByNomeTipo(nome, PessoaGeral.PESSOA_FISICA, new PagingInformation());
	}
	
	/**
 	 * Remove uma pessoa a lista de instrutores 
 	 * da atividade que está sendo cadastrada ou atualizada.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @param e
 	 * @throws DAOException
 	 */
	public void removeInstrutor(ActionEvent e) throws DAOException {
		pessoa.setId((Integer) e.getComponent().getAttributes().get("pessoa"));
		if ( !isEmpty(pessoa) ) {
			atividadeArquivo.getAtividade().removeInstrutor(getGenericDAO().refresh(pessoa));
			pessoa = new Pessoa();
		}
	}
	
	/**
 	 * Adiciona uma atividade a lista de atividades
 	 * do grupo que está sendo cadastrada ou atualizada.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @param e
 	 * @throws DAOException
 	 */
	public void addAtividade(ActionEvent e) throws DAOException {
		
		validateFormAtividade(atividadeArquivo.getAtividade());
		
		if ( !hasErrors() && atividadeArquivo != null ) {
			addListaAtividadeAnexo(atividadeArquivo);
			atividadeArquivo = new AtividadeArquivo();
			arquivo  = null;
			pessoa = new Pessoa();
			setConfirmButtonAtividade("Adicionar Atividade");
			setEnableAlterarRemoverAtividade(true);
		}
		
		
	}
	
	/**
 	 * Inativa uma atividade da lista. 
 	 * da atividade que está sendo cadastrada ou atualizada.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @param e
 	 * @throws DAOException
 	 */
	public void removeAtividade(ActionEvent e) throws DAOException {
		if ( getMapa(e) != null ) {
			
			if( !checkParticipanteAtividade( getMapa(e).getAtividade() ) ){
				
				getMapa(e).getAtividade().setAtivo(false);
				
				Integer pos = listaAtividadesAnexo.indexOf( getMapa(e) );
				if( pos >=0 )
					listaAtividadesAnexo.get(pos).getAtividade().setAtivo(false);
			}	
			
			atividadeArquivo = new AtividadeArquivo();
			setConfirmButtonAtividade("Adicionar Atividade");
			pessoa = new Pessoa();
	
		}
	}
	
	/**
	 * Verifica a existência de particpantes para atividade do parâmetro.
	 * Método não invocado por JSP's.
	 * @param atividade
	 * @return
	 * @throws DAOException
	 */
	private boolean checkParticipanteAtividade(AtividadeAtualizacaoPedagogica atividade) 
		throws DAOException{
		
		if(isEmpty(atividade))
			return false;
		
		boolean check = getDAO( GrupoAtividadesAtualizacaoPedagogicaDAO.class ).checkParticipanteAtividade(atividade);
		
		if( check )
			addMensagemAjax(MensagensAP.POSSUI_PARTICIPANTE_ATIVIDADES);
		
		return check;
	}

	/**
	 * Retorna uma mapa contendo a atividade e o arquivo.
	 * @param e
	 * @return
	 */
	private AtividadeArquivo getMapa(ActionEvent e) {
		return (AtividadeArquivo) e.getComponent().getAttributes().get("mapa");
	}
	
	/**
 	 * Carrega os dados da atividade para alteração 
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/form_atividade.jsp</li>
 	 * </ul>
 	 * @param e
 	 * @throws DAOException
 	 */
	public void atualizaAtividade(ActionEvent e) throws DAOException {
		atividadeArquivo = (AtividadeArquivo) e.getComponent().getAttributes().get("mapa");
		if( !isEmpty(atividadeArquivo) ){
			if( !isEmpty(atividadeArquivo.getAtividade().getInstrutores()) )
				atividadeArquivo.getAtividade().getInstrutores().iterator();
		}	
		setEnableAlterarRemoverAtividade(false);
		setConfirmButtonAtividade("Alterar Atividade");
	}
	
	/**
	 * Exibe a listagem de todos os registros do docente.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/lista.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 */
	public String view() throws DAOException, SegurancaException {
		populateObj(true);
		setReadOnly(true);
		return forward(getViewPage());
	}
	
	/**
	 * Exibe a listagem de todos os gruops cadastrados pelo gestor do pap.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/apedagogica/GrupoAtividadesAPedagogica/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		checkChangeRole();
		return super.listar();
	}
	
	public AtividadeArquivo getAtividadeArquivo() {
		return atividadeArquivo;
	}

	public void setAtividadeArquivo(AtividadeArquivo atividadeArquivo) {
		this.atividadeArquivo = atividadeArquivo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/**
	 * Método que valida o preenchimentodos campos do formulário
	 * do grupo de atividades referenet ao primeiro passo do caso de uso.
	 * Método não invocado por JSP's.
	 */
	private void validateFormGrupo(){
		validateRequired(obj.getDenominacao(), "Denominação", erros);
		validateRequired(obj.getInicio(), "Data Início do Período", erros);
		validateRequired(obj.getFim(), "Data Final do Período", erros);
		validaInicioFim(obj.getInicio(), obj.getFim(), "Período das Atividades", erros);
		validateRequired(obj.getInicioInscricao(), "Data Início de Inscricão", erros);
		validateRequired(obj.getFimInscricao(), "Data Final de Inscricão", erros);
		validaInicioFim(obj.getInicioInscricao(), obj.getFimInscricao(), "Período de Inscrição", erros);
	}
	
	/**
	 * Método que valida o preenchimento dos campos do formulário
	 * de atividade antes de associa-la ao grupo.
	 * 
	 */
	private void validateFormAtividade(AtividadeAtualizacaoPedagogica atividade){
		
		validateRequired(atividade.getNome(), "Nome", erros);
		if( atividade.getCh() != null)
			validaInt(atividade.getCh(), "Carga Horária", erros);
			
		validateRequired(atividade.getInicio(), "Data Inicial do Período", erros);
		validateRequired(atividade.getFim(), "Data Final do Período", erros);
		validaInicioFim(atividade.getInicio(), atividade.getFim(), "Período", erros);

		if( !isEmpty( atividade.getHorarioInicio() ) )
			ValidatorUtil.validaHora(atividade.getHorarioInicio(), "Horário Incial", erros);
		if( !isEmpty( atividade.getHorarioFim() ) )
			ValidatorUtil.validaHora(atividade.getHorarioFim(), "Horário Final", erros);
		if( !isEmpty( atividade.getHorarioInicio() ) && !isEmpty( atividade.getHorarioFim() )  ){
			int horarioInicio =  Integer.parseInt(atividade.getHorarioInicio().replace(":", ""));
			int horarioFim =  Integer.parseInt(atividade.getHorarioFim().replace(":", ""));
			if( horarioInicio > horarioFim )
				addMensagemErro("O horário final não deve ser inferior ao horário inicial.");
		}
		
		if(!isEmpty(atividade.getInicio()) && atividade.getInicio().getTime() < obj.getInicio().getTime())
			addMensagemErro("A data inicial do período da atividade não deve ser inferior a data inicial do período do grupo em que está associado.");
		if(!isEmpty(atividade.getFim()) && atividade.getFim().getTime() > obj.getFim().getTime())
			addMensagemErro("A data final do período da atividade não deve ser superior a data final do período do grupo em que está associado.");
		
		validateRequired(atividade.getInstrutores(), "Professores", erros);
		
		getCurrentRequest().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
		
	}
	
	/**
	 * Método que valida o preenchimento dos campos do formulário
	 * do instrutor antes de associa-lo a atividade do grupo.
	 * 
	 */
	private void validateFormInstrutor(){
		validateRequired(pessoa.getNome(), "Instrutores", erros);
		getCurrentRequest().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
	}
	
	/**
	 * Método que valida se existe ao menos uma 
	 * atividade associada ao grupo.
	 * 
	 */
	private void validateFormAtividadesGrupo(){
		if( isEmpty(getListaAtividadesAnexosAtivos()) )
			addMensagemErro("Para Salvar o Grupo deve-se adicionar pelo menos uma Atividade.");
		else{
			for (AtividadeArquivo a : getListaAtividadesAnexosAtivos()) {
				validateFormAtividade( a.getAtividade() );
				if(hasErrors())
					break;
			}
		}
	}
	
	/**
	 * Popula somente os grupos abertos cadastrados para o um componente 
	 * de visualização JSF na view.
	 * Método não invocado por JSP's.
	 */
	public Collection<SelectItem> getAllAbertosCombo() throws ArqException{
		return getAllCombo(true);
	}
	
	/**
	 * Popula todos os grupos cadastrados para o um componente 
	 * de visualização JSF na view.
	 * Método não invocado por JSP's.
	 */
	public Collection<SelectItem> getAllCombo() throws ArqException{
		return getAllCombo(false);
	}
		
	/**
	 * Retorna uma coleao de itens contendo grupos de atividade cadastrados.
	 * Método não invocado por JSP's.
	 */
	private Collection<SelectItem> getAllCombo(boolean somenteAbertos) throws ArqException{
		
		Collection<SelectItem> itens =	new ArrayList<SelectItem>();
				
		for (GrupoAtividadesAtualizacaoPedagogica g : getAllAbertos()) {
			SelectItem item = new  SelectItem(g.getId(), g.getDenominacao() + "-" + g.getInicio() + "/" + g.getFim());
			if( somenteAbertos ){
				if( CalendarUtils.isDentroPeriodo(g.getInicio(), g.getFim()) )
					itens.add( item );
			}else
				itens.add( item );
		}	
		return itens;
		
	}
	
	/**
	 * Retorna uma coleao de itens contendo grupos de atividade cadastrados.
	 * Método não invocado por JSP's.
	 */
	public Collection<SelectItem> getAtividadesAbertasCombo() throws ArqException{
		
		return null;
		
	}
	
	
	/**
	 * Retorna uma coleção de todos os grupos abertos.
	 * @return
	 * @throws ArqException 
	 */
	public Collection<GrupoAtividadesAtualizacaoPedagogica> getAllAbertos() throws ArqException{
		
		Collection<GrupoAtividadesAtualizacaoPedagogica> gruposAbertos = new ArrayList<GrupoAtividadesAtualizacaoPedagogica>();
		
		for (GrupoAtividadesAtualizacaoPedagogica g : getAll()) 
			if( !isEmpty(g.getFimInscricao()) 
					&& ( getAcessoMenu().isProgramaAtualizacaoPedagogica() 
							|| g.isAberto() ) )
				gruposAbertos.add( g );
		
		if( !isEmpty(gruposAbertos) ){
			Collections.sort( (List<GrupoAtividadesAtualizacaoPedagogica>) gruposAbertos, new Comparator<GrupoAtividadesAtualizacaoPedagogica>(){
				public int compare(GrupoAtividadesAtualizacaoPedagogica g1,	GrupoAtividadesAtualizacaoPedagogica g2) {						
					return g2.getInicio().compareTo(g1.getInicio()); 				
				}
			});
		}	
		
		return gruposAbertos;
	}
	
	/**
	 * Método que carrega as atividades do grupo selecionado.
	 * <br /> 
	 * Método não invocado por JSP's.
	 * @param e
	 * @throws DAOException
	 */
	public void carregarAtividades(ActionEvent e) throws DAOException{
		Integer idGrupoAtividade = (Integer) e.getComponent().getAttributes().get("idGrupoAtividade");
		
		if( !isEmpty(idGrupoAtividade) ){
			GrupoAtividadesAtualizacaoPedagogica grupo = getGenericDAO().
				findByPrimaryKey(idGrupoAtividade,GrupoAtividadesAtualizacaoPedagogica.class);
			atividadesCombo =	new ArrayList<SelectItem>();
			for (AtividadeAtualizacaoPedagogica a : grupo.getAtividades()) {
				SelectItem item = new  SelectItem(a.getId(), a.getNome() + "(" + a.getInicio() + "/" + a.getFim() + ") - CH " + a.getCh() + "h");
				atividadesCombo.add( item );
			}	
		}
		
	}
	
	public String getConfirmButtonAtividade() {
		return confirmButtonAtividade;
	}

	public void setConfirmButtonAtividade(String confirmButtonAtividade) {
		this.confirmButtonAtividade = confirmButtonAtividade;
	}

	public void setAtividadesCombo(Collection<SelectItem> atividadesCombo) {
		this.atividadesCombo = atividadesCombo;
	}
	
	/**
	 * Método que retorna os itens das atividades do grupo selecionado.
	 * @return
	 */
	public Collection<SelectItem> getAtividadesCombo(){
		return atividadesCombo;
	}

	public List<AtividadeArquivo> getListaAtividadesAnexo() {
		return listaAtividadesAnexo;
	}
	
	/**
	 * Método que retorna os itens das atividades ativos para serem exibidos no 
	 * formulário.
	 * @return
	 */
	public List<AtividadeArquivo> getListaAtividadesAnexosAtivos() {
		
		if( isEmpty(listaAtividadesAnexo) )
			return null;
		
		List<AtividadeArquivo> listaAtividadesAnexosAtivos = new ArrayList<AtividadeArquivo>(); 
				
		for (AtividadeArquivo mapa : listaAtividadesAnexo) {
			if( mapa.getAtividade().isAtivo() )
				listaAtividadesAnexosAtivos.add(mapa);
		}
		
		return listaAtividadesAnexosAtivos;
	}

	public void setListaAtividadesAnexo(
			List<AtividadeArquivo> listaAtividadesAnexo) {
		this.listaAtividadesAnexo = listaAtividadesAnexo;
	}
	
	/**
	 * Adiciona lista de mapas o arquivo e o anexo.
	 * Método não invocado pr JSP's.
	 * @param atividade
	 * @param arquivo
	 */
	private void addListaAtividadeAnexo(AtividadeArquivo atividadeArquivo){
		
		if( isEmpty(listaAtividadesAnexo) )
			listaAtividadesAnexo = new ArrayList<AtividadeArquivo>();
		
		boolean add = true;
		for (AtividadeArquivo m : listaAtividadesAnexo) {
			if( m.getAtividade().equals(atividadeArquivo.getAtividade()) ){
				listaAtividadesAnexo.get( listaAtividadesAnexo.indexOf(m) ).setAtividade( atividadeArquivo.getAtividade() );
				listaAtividadesAnexo.get( listaAtividadesAnexo.indexOf(m) ).setArquivo(atividadeArquivo.getArquivo());
				add = false;
			}	
		}
		if( add == true )
			listaAtividadesAnexo.add(atividadeArquivo);
			
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public boolean isEnableAlterarRemoverAtividade() {
		return enableAlterarRemoverAtividade;
	}

	public void setEnableAlterarRemoverAtividade(boolean enableAlterarRemoverAtividade) {
		this.enableAlterarRemoverAtividade = enableAlterarRemoverAtividade;
	}

	public void setDocExterno(DocenteExterno docExterno) {
		this.docExterno = docExterno;
	}

	public DocenteExterno getDocExterno() {
		return docExterno;
	}
 	
 }

