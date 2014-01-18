package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.ALTERADO_COM_SUCESSO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhlabs.image.OpacityFilter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.tecnico.dominio.TipoBolsaFormacaoComplementar;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.JustificativaAusenciaCIC;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/** Classe respons�vel pelas opera��es sobre as Justificativas de Aus�ncias no Congresso de Inicia��o Cientifica
 * 
 * 
 * @author Andreza Pollyana
 *
 */


@Scope("request")
@Component("justificativaCIC")
public class JustificativaAusenciaCICMBean  extends SigaaAbstractController<JustificativaAusenciaCIC>{
	
	/** Arquivo de justificativa da aus�ncia. */
	private UploadedFile arquivo;
	/** Constante para armazenar a representa��o do autor **/
	public static final int AUTOR = 1;
	/** Constante para armazenar a representa��o do avaliador **/
	public static final int AVALIADOR = 2;
	/** Cole��o que ir� armazenar a listagem das justificativas **/
	private Collection<JustificativaAusenciaCIC> ausentes = new ArrayList<JustificativaAusenciaCIC>();
	/** Recupera o arquivo enviado**/
	private OutputStream outFile = null;
	/** Armazena se o usu�rio logado � ou n�o discente **/	
	private boolean isdiscente;
	/** Armazena de que tipo o usu�rio logado � : Autor ou Avaliador**/
	private Integer tipo;
	
	public Integer getTipo() {
		return tipo;
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	public OutputStream getOutFile() {
		return outFile;
	}
	public void setOutFile(OutputStream outFile) {
		this.outFile = outFile;
	}

	public Collection<JustificativaAusenciaCIC> getAusentes() {
		return ausentes;
	}
	public void setAusentes(Collection<JustificativaAusenciaCIC> ausentes) {
		this.ausentes = ausentes;
	}
	public UploadedFile getArquivo() {
		return arquivo;
	}
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Verifica qual o congresso de inicia��o cientifica est� ativo*/
	public CongressoIniciacaoCientifica getCic() {
		try {
			CongressoIniciacaoCientifica congresso = getDAO(CongressoIniciacaoCientificaDao.class).findAtivo();
			return congresso;
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/** Construtor padr�o.  * */
	public JustificativaAusenciaCICMBean(){
			initObj();
	}
	
	/** Inicializa um novo objeto do caso de uso*/
	public void initObj(){
		obj = new JustificativaAusenciaCIC();
	}
	
	/** Link para a p�gina de listagem de justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/pesquisa/justificativa_ausencia_cic/lista.jsf";
	}
	
	/** Link para a p�gina de formul�rio de cadastro da justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/pesquisa/justificativa_ausencia_cic/form.jsf";
	}
	
	/** Link para a p�gina de visualiza��o de justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getViewPage()
	 */
	@Override
	public String getViewPage() {
		return "/pesquisa/justificativa_ausencia_cic/view.jsf";
	}
	
	/** M�todo utilizado para verificar se um usu�rio j� cadastrou uma justificativa de aus�ncia*/
	public boolean justificativaCadastrada() throws DAOException{
		Collection<JustificativaAusenciaCIC> justificativa = getDAO(CongressoIniciacaoCientificaDao.class).findAusenciaByCongressoAtivo(getUsuarioLogado());
		if(justificativa.isEmpty())
			return false;
		else
			return true;
	}
	
	/** M�todo utilizado para exibir os tipos de uma justificativa de aus�ncia*/
	public List<SelectItem> getAllCombos(){
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(AUTOR,"AUTOR"));
		itens.add(new SelectItem(AVALIADOR,"AVALIADOR "));
		itens.add(new SelectItem(0,"TODOS"));
		return itens;
	}

	/** M�todo utilizado para cadastrar uma justificativa de aus�ncia
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/pesquisa/justificativa_ausencia_cic/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException{		
	
		if( !(checkOperacaoAtiva((ArqListaComando.ALTERAR.getId()),ArqListaComando.CADASTRAR.getId()) )){
			return null; 
		}
		
		erros = obj.validate();
		if (hasErrors())
			return null;
		
		Discente discente = getUsuarioLogado().getDiscente();
		
		obj.setData_cadastro(new Date());
		obj.setCadastrado_por(getUsuarioLogado());
	
		if(discente != null && discente.isGraduacao())
			obj.setTipo(AUTOR);
		else
			obj.setTipo(AVALIADOR);
			
		super.cadastrar();

		return cancelar();
	}
	
	
	/** Salva o arquivo enviado antes de cadastrar e apos validar
	 * 
	 * Chamado por
	 * <ul>
	 * 	<li>/pesquisa/justificativa_ausencia_cic/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAfterValidate()
	 * **/
	public void beforeCadastrarAfterValidate() throws DAOException {
		if(arquivo != null){
			obj.setIdArquivo(EnvioArquivoHelper.getNextIdArquivo());
			try {
				EnvioArquivoHelper.inserirArquivo(obj.getIdArquivo(), arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** M�todo utilizado para verificar se esta no per�odo de justificativa de aus�ncia 
	 * 
	 *  Chamado por
	 * <ul>
	 * 	<li>/pesquisa/justificativa_ausencia_cic/form.jsp</li>
	 * </ul>
	 * **/
	public boolean isPeriodoJustificativa() throws DAOException{
		CongressoIniciacaoCientifica congresso = getDAO(CongressoIniciacaoCientificaDao.class).findAtivo();
		
		if(congresso !=null){
			Date fim = congresso.getFimJustificativa();
			Date inicio = congresso.getInicioJustificativa();
			Date hoje = new Date();
			if(fim == null || inicio == null){
				addMensagemErro("N�o estamos no prazo de justificativa de aus�ncia");
				return false;
			}
			else 
				if((inicio.before(hoje) && fim.after(hoje)) || DateUtils.isSameDay(hoje,inicio) || DateUtils.isSameDay(hoje,fim)){
					return true;
				}
				else {
					addMensagemErro("N�o estamos no prazo de justificativa de aus�ncia");
					return false;
				}
		}
		else {
			addMensagemErro("N�o h� congresso ativo para justificar aus�ncia");
			return false;
		}
	}
	
	/** M�todo utilizado para verificar se o usu�rio � um avaliador ou autor de algum resumo no congresso ativo
	 * 
	 *  Chamado por
	 * <ul>
	 * 	<li>/pesquisa/justificativa_ausencia_cic/form.jsp</li>
	 * </ul>
	 * */
	public boolean isAvaliadorOrAuthor() throws DAOException{

		CongressoIniciacaoCientificaDao dao = getDAO(CongressoIniciacaoCientificaDao.class);
		ResumoCongressoDao dao_resumo = getDAO(ResumoCongressoDao.class);
		CongressoIniciacaoCientifica congresso = dao.findAtivo();
		DiscenteAdapter discente = getDiscenteUsuario();
		if(discente != null){
			isdiscente = true;
			
			if(discente.isGraduacao()) {
				Collection<ResumoCongresso> resumo = dao_resumo.filter(congresso.getId(), null, null, getUsuarioLogado().getNome(), getUsuarioLogado().getCpf(), null, null,null,null,null);
				if(resumo.isEmpty())
					return false;
				else
					return true;
			}
			else if (discente.isDoutorado()){
				return dao.isAvaliador(congresso, null, discente, null);
				
			}
		}
		else{
			isdiscente = false;
			return dao.isAvaliador(congresso, getServidorUsuario() , null, null);
		}
		
		return false;
		
	}
	/** Prepara para cadastrar uma justificativa de fiscal.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/pesquisa/justificativa_ausencia_cic/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	public String preCadastrar() throws ArqException, NegocioException {
	
		if(isPeriodoJustificativa()){
			if(!isAvaliadorOrAuthor()){
				addMensagemErro("N�o h� registro seu como autor ou avalidor de resumos no congresso atual");
				return null;
			}
			else{
				initObj();			
				prepareMovimento(ArqListaComando.CADASTRAR);
				setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
				obj.setCIC(getDAO(CongressoIniciacaoCientificaDao.class).findAtivo());
				obj.setPessoa(getUsuarioLogado().getPessoa());
				if(justificativaCadastrada()) {
					addMensagemErro("Voc� J� Justificou Sua Aus�ncia No Congresso De Inicia��o Cient�fica Atual");
					return cancelar();
				}
				return forward(getFormPage());	
			}
		}
		else {			
			return cancelar();
		}
			
	}
	
	/** M�todo utilizado para  gerar a lista de ausentes ao entrar no caso de uso
	 * 
	 * Chamado por :
	 * <ul>
	 * 	<li>/pesquisa/justificativa_ausencia_cic/lista.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException */
	public String iniciarLista() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		tipo = 0;
		return listar();
	}
	
	/** M�todo utilizado para listar os ausentes 
	 * 
	 * Chamado por :
	 * <ul>
	 * 	<li>/pesquisa/justificativa_ausencia_cic/lista.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException */
	public String listar() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		buscar();
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);		
		return  forward(getListPage());
		
	}
	
	/** M�todo utilizado para buscar os ausentes no congresso ativo, caso n�o tenha nenhum
	 *  congresso ativo, busca pelo congresso mais recente.
	 *  
	 * Chamado por :
	 * <ul>
	 * <li>/pesquisa/justificativa_ausencia_cic/lista.jsp</li>
	 * </ul>
	 * 
	 * */
	public String buscar() throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		ausentes = getDAO(CongressoIniciacaoCientificaDao.class).findAusentesByCongressoAtivo(tipo);
		if(ausentes == null)
			ausentes = getDAO(CongressoIniciacaoCientificaDao.class).findAusenteByUltimo();
		if(ausentes.isEmpty())
			addMensagemErro("Nenhuma Justificativa foi cadastrada");
			
		return null;
		
	}
	
	
	/** M�todo utilizado para verificar se o usu�rio tem permiss�o para visualizar o menu de 
	 * justificativa de falta
	 * 
	 * Chamado por :
	 * <ul>
	 * <li>/pesquisa/menu/iniciacao.jsp</li>
	 * </ul>
	 * **/
	public boolean isPermissaoAcesso(){
		
		if( getSubSistema().equals(SigaaSubsistemas.PORTAL_DISCENTE )){
			if( getDiscenteUsuario()!= null	&& 
				getDiscenteUsuario().getStatus()==StatusDiscente.ATIVO &&
				(getDiscenteUsuario().getNivel()== NivelEnsino.DOUTORADO) || 
				(getDiscenteUsuario().getNivel()== NivelEnsino.GRADUACAO)){
				return true;		
			}
		}
		

		if( getSubSistema().equals(SigaaSubsistemas.PORTAL_DOCENTE )){
			if( isUserInRole(SigaaPapeis.DOCENTE ) && getUsuarioLogado().getDocenteExternoAtivo() == null ){
				return true;
			}
		}
		
		return false;
	}
	
	/** M�todo utilizado para visualizar o arquivo, caso exista, enviado ao cadastrar uma justificativa
	 * 
	 * Chamado por :
	 * <ul>
	 * <li>/pesquisa/justificativa_ausencia_cic/lista.jsp</li>
	 * </ul>*/
	public void visualizarArquivo(){		
		EnvioArquivoHelper.recuperaArquivo(outFile, obj.getIdArquivo());
	}
	
	/** M�todo utilizado para exibir os tipos de status de uma justificativa
	 * 
	 *Chamado por :
	 * <ul>
	 * <li>/pesquisa/justificativa_ausencia_cic/lista.jsp</li>
	 * </ul>*/
	public List<SelectItem> getTiposDeStatus(){
		List<SelectItem> status = new ArrayList<SelectItem>();
		status.add(new SelectItem(1, "REGISTRADO"));
		status.add(new SelectItem(2, "VISTO"));
		
		return  status;
	}
	
	/** M�todo utilizado para atualizar uma justificativa de aus�ncia
	 * 
	 *Chamado por :
	 * <ul>
	 * <li>/pesquisa/justificativa_ausencia_cic/lista.jsp</li>
	 * </ul>*/
	public String atualizar() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		setReadOnly(false);

		if( !checkOperacaoAtiva(ArqListaComando.ALTERAR.getId()) )
			return cancelar();

		for(JustificativaAusenciaCIC ausente : ausentes){
			setObj(getGenericDAO().findByPrimaryKey(ausente.getId(), JustificativaAusenciaCIC.class));
			if(ausente.isVisto() && !obj.isVisto()) {
				prepareMovimento(ArqListaComando.ALTERAR);		
				setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
				obj.setStatus(2);
				obj.setVisto(true);
				try {
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setObjMovimentado(obj);
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					execute(mov);
				} catch (NegocioException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		
		addMensagem(OPERACAO_SUCESSO);
		return cancelar();
	}
	
	
}
