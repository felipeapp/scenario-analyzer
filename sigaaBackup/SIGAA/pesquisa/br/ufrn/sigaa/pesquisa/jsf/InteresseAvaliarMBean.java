/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/08/2013
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
/**
 * MBean responsável pelo cadastro de AvaliadorCIC
 * @author Jeferson Queiroga 
 *
 */
@Component("interesseAvaliarBean") @Scope("session")
public class InteresseAvaliarMBean extends SigaaAbstractController<AvaliadorCIC> {

	/** JSP do formulário */
	private static final String FORM_CADASTRO = "/pesquisa/interesse_avaliar/form.jsp";
	
	/** Avaliador que será persistido*/
	private AvaliadorCIC avaliador;
	
	/** Congresso Iniciação cientifica atual que será utilizado na exibicação.*/
	private CongressoIniciacaoCientifica congresso;
			
	/** Inicializador dos atributos a serem usados */
	private void clear() {
		congresso = new CongressoIniciacaoCientifica();
		avaliador = new AvaliadorCIC();
		avaliador.setCongresso( new CongressoIniciacaoCientifica() );
	}
	
	public InteresseAvaliarMBean() {
		clear();
	}
	
	/**
	 * Método que inicia o casos de uso.
	 * <br/>
	 * Invocado pelas JSP:
	 *  /sigaa.war/portais/docente/menu_docente.jsp
	 *  /sigaa.war/portais/discente/menu_discente.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciar() throws SegurancaException, DAOException{
		verificaPermissao();
		clear();		
		
		if(hasAvaliadorCadastro()){
			addMensagemErro("Você já cadastrou o interesse como avaliador para o Congresso de Iniciação Científica Atual.");
			return null;
		}
		
		CongressoIniciacaoCientificaDao congressoDAO = getDAO(CongressoIniciacaoCientificaDao.class);
		congresso = congressoDAO.findAtivo();  	
			
		return forward(FORM_CADASTRO);
	}
	
	/**
	 * Método que realiza o cadastro do avaliador.
	 * <br/>
	 * JSP: /sigaa.war/pesquisa/interesse_avaliar/form.jsp
	 * @return
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	public String cadastrar() throws NegocioException, ArqException{
		
		verificaPermissao();
		validarFormulario();
		if(hasErrors()){
			return null;
		}		
		
		if( getSubSistema().equals( SigaaSubsistemas.PORTAL_DOCENTE )  && isUserInRole(SigaaPapeis.DOCENTE ) ){
			avaliador.setDocente( getUsuarioLogado().getServidor() );
			avaliador.setDiscente( null );
		}else if( getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE ) && getUsuarioLogado().getDiscenteAtivo()!= null ){
			avaliador.setDiscente(getDiscenteUsuario());
			avaliador.setDocente(null);
		}
		
		avaliador.setCongresso(congresso);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(avaliador);
		mov.setCodMovimento(ArqListaComando.CADASTRAR );
		prepareMovimento( ArqListaComando.CADASTRAR );
		execute(mov);
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Avaliador");
		
		return cancelar();
	}
	
	
	/**
	 * Método verifica se possui alguma avaliador já cadastrado para o congresso atual;
	 * <br/>
	 * Não invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	private boolean hasAvaliadorCadastro() throws DAOException{
			
		CongressoIniciacaoCientificaDao congressoDAO = getDAO(CongressoIniciacaoCientificaDao.class);
		CongressoIniciacaoCientifica congresso = congressoDAO.findAtivo();
		Collection<AvaliadorCIC> avaliadores = null;
		
		if( getSubSistema().equals( SigaaSubsistemas.PORTAL_DOCENTE )  && isUserInRole(SigaaPapeis.DOCENTE ) ){
			Servidor servidor = getUsuarioLogado().getServidor();
			avaliadores = congressoDAO.findByExactField(AvaliadorCIC.class, new String[]{"docente.id","congresso.id"}, new Object[]{servidor.getId(), congresso.getId() });
		}else if( getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE ) && getUsuarioLogado().getDiscenteAtivo()!= null ){
			DiscenteAdapter discente = getDiscenteUsuario();
			avaliadores = congressoDAO.findByExactField(AvaliadorCIC.class, new String[]{"discente.id","congresso.id"}, new Object[]{discente.getId(), congresso.getId() });
		}
		
		if( ValidatorUtil.isEmpty( avaliadores ) ){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Método para verificar se os campos foram preenchidos.
	 * Não invocado por JSP.
	 */
	private void validarFormulario() {
		if( ValidatorUtil.isEmpty( avaliador ) ){
			if(!avaliador.isAvaliadorApresentacao() && !avaliador.isAvaliadorResumo() ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Tipo Avalidor");
			} 
		}	
		
		if(congresso==null){
			addMensagem("Nenhum Congresso Ativo");
		}
	}

	/**
	 * Verifica as permissões para acessar o caso de uso.
	 * @throws SegurancaException
	 */
	private void verificaPermissao() throws SegurancaException{
		
		boolean flag = false;
		
		if( getSubSistema()== SigaaSubsistemas.PORTAL_DISCENTE ){
			if( getDiscenteUsuario()!= null	&& 
				getDiscenteUsuario().getStatus()!=StatusDiscente.ATIVO &&
				getDiscenteUsuario().getNivel()!= NivelEnsino.DOUTORADO){
						flag=true;
		
			}
		}
		
		if( getSubSistema()== SigaaSubsistemas.PORTAL_DOCENTE ){
			if( isUserInRole(SigaaPapeis.DOCENTE ) && getUsuarioLogado().getDocenteExternoAtivo() != null ){
				flag=true;
			}
		}
			
		if(flag){
			throw new SegurancaException();
		}
		
	}
	
	/**
	 * Verifica se o usuário logado tem permissão para ver o link do caso de uso.
	 * <br/>
	 * JSP: /sigaa.war/portais/docente/menu_docente.jsp
	 * 		/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp
	 * @return
	 */
	public boolean isPermissaoAcesso(){
		
		if( getSubSistema().equals(SigaaSubsistemas.PORTAL_DISCENTE )){
			if( getDiscenteUsuario()!= null	&& 
				getDiscenteUsuario().getStatus()==StatusDiscente.ATIVO &&
				getDiscenteUsuario().getNivel()== NivelEnsino.DOUTORADO){
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
	
	public AvaliadorCIC getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(AvaliadorCIC avaliador) {
		this.avaliador = avaliador;
	}
	
	public CongressoIniciacaoCientifica getCongresso() {
		return congresso;
	}

	public void setCongresso(CongressoIniciacaoCientifica congresso) {
		this.congresso = congresso;
	}

	
}
