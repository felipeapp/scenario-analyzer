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
 * MBean responsável pelo gerenciamento do Calendário de Ações Associadas
 * 
 * @author julio
 */
@Scope("session")
@Component("calendarioAcoesAssociadas")
public class CalendarioAcoesAssociadasMBean extends SigaaAbstractController<CalendarioProjeto> {

	/** Atributo utilizado para renderizar o Ano de Refencia na tela do formulário */
	private boolean altera = false;
	
	/**
	 * Construtor padrão
	 */
	public CalendarioAcoesAssociadasMBean(){
		obj = new CalendarioProjeto();
	}
	
	
	/**
	 * Método utilizado para realizar ações antes de cadastrar o Calendário de Ações Associadas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método utilizado para realizar verificar se já existe um Calendário de Ação Associada cadastrado para o ano informado
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
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
					addMensagemErro("Já existe um Calendário de Ação Associada cadastrado para o ano de "+obj.getAnoReferencia());
				}
			}
		}
	}
	
	/**
	 * Método utilizado para setar o atributo altera para true
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
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
	 * Método utilizado para preparar setar a operação ativa e preparar o movimento DESATIVAR.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
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
	 * Método utilizado para buscar um Calendário de Ações Associadas no banco de dados.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamdo por JSPs</li>
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
	 * Método utilizado para definir o diretório base do Calendário de Ações Associadas
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamdo por JSPs</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/projetos/CalendarioAcoesAssociadas";
	}
	
	/**
	 * Método utilizado para redirecionar o usuário para a tela principal do subsistema selecionado
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamdo por JSPs</li>
	 * </ul>
	 */
	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
	}
	
	/**
	 * Método utilizado para buscar todos os Calendários ativos de Projetos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamdo por JSPs</li>
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
