package br.ufrn.sigaa.projetos.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.CalendarioProjeto;

/**
 * MBean respons�vel pelo gerenciamento do Calend�rio de A��es Associadas
 * 
 * @author julio
 */
@Scope("session")
@Component("calendarioAcoesAssociadas")
public class CalendarioAcoesAssociadasMBean extends SigaaAbstractController<CalendarioProjeto> {

	/** Atributo utilizado para renderizar o Ano de Refencia na tela do formul�rio */
	private boolean altera = false;
	
	/**
	 * Construtor padr�o
	 */
	public CalendarioAcoesAssociadasMBean(){
		obj = new CalendarioProjeto();
	}
	
	
	/**
	 * M�todo utilizado para realizar a��es antes de cadastrar o Calend�rio de A��es Associadas.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/projetos/menu.jsp</li>
	 * 		<li>/sigaa.war/projetos/CalendarioAcoesAssociadas/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		obj = new CalendarioProjeto(CalendarUtils.getAnoAtual());
		setAltera(false);
		return super.preCadastrar();
	}
	
	/**
	 * M�todo utilizado para realizar verificar se j� existe um Calend�rio de A��o Associada cadastrado para o ano informado
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @throws NegocioException
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		Integer ano = obj.getAnoReferencia(); 
		if( ano != null ){
			List<CalendarioProjeto> lista = buscarCalendario(obj.getAnoReferencia());
			for(CalendarioProjeto cal : lista){
				if(cal.getId() != obj.getId()){
					addMensagemErro("J� existe um Calend�rio de A��o Associada cadastrado para o ano de "+obj.getAnoReferencia());
				}
			}
		}
	}
	
	/**
	 * M�todo utilizado para setar o atributo altera para true
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	@Override
	protected void beforeAtualizar() throws ArqException {
		setAltera(true);
		super.beforeAtualizar();
	}
	
	/**
	 * M�todo utilizado para preparar setar a opera��o ativa e preparar o movimento DESATIVAR.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	@Override
	protected void beforeInativar() {
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			e.printStackTrace();
		}
		super.beforeInativar();
	}
	
	/**
	 * M�todo utilizado para buscar um Calend�rio de A��es Associadas no banco de dados.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSPs</li>
	 * </ul>
	 * 
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public List<CalendarioProjeto> buscarCalendario(int ano) throws DAOException{
		return (List<CalendarioProjeto>) getGenericDAO().findAtivosByExactField(CalendarioProjeto.class, "anoReferencia", ano);
	}
	
	/**
	 * M�todo utilizado para definir o diret�rio base do Calend�rio de A��es Associadas
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSPs</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/projetos/CalendarioAcoesAssociadas";
	}
	
	/**
	 * M�todo utilizado para redirecionar o usu�rio para a tela principal do subsistema selecionado
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSPs</li>
	 * </ul>
	 */
	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
	}
	
	/**
	 * M�todo utilizado para buscar todos os Calend�rios ativos de Projetos.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamdo por JSPs</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<CalendarioProjeto> getCalendarios() throws DAOException{
		return  (List<CalendarioProjeto>) getGenericDAO().findAllAtivos(CalendarioProjeto.class, "id"); 
	}
	
	public String listarCalendarios(){
		return redirect(getListPage());
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}
	
	
}
