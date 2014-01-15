/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 10/10/2008
 *
 */	
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.jsf.EmiteHistoricoEmprestimosMBean;
import br.ufrn.sigaa.biblioteca.circulacao.jsf.ListaEmprestimosAtivosUsuarioMBean;
import br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.jsf.VerificaSituacaoUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BuscaUsuarioBibliotecaHelper;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>MBean que controla a p�gina de busca geral do usu�rio da biblioteca.</p>
 * <p> 
 * 	 <strong>Obseva��o 1 : </strong> A busca sempre � feita utilizando a pessoa ou a Biblioteca, n�o deve-se 
 *    verificar qual o v�nculo do usu�rio para os empr�stimos sen�o fica muito pessada a consulta. 
 *    O v�nculo s� deve ser mostrado ou escolher um usu�rio espec�fico  
 * </p>
 * <p> 
 *   <strong>Obseva��o 2:. </strong> Essa busca de usu�rios deve ser utilizada em todo o sistema para ficar centralizado e f�cil de manter. 
 * </p>
 *
 * <p><strong>Qualquer parte do sistema que quiser utilizar essa pesquisa p�blica no acervo deve implentar a interface {@link PesquisarUsuarioBiblioteca}<strong></p>
 *
 * @author jadson
 * @since 10/10/2008
 * @version 1.0 cria��o da classe
 *
 */
@Component("buscaUsuarioBibliotecaMBean")
@Scope("request")
public class BuscaUsuarioBibliotecaMBean extends SigaaAbstractController<UsuarioBiblioteca>{
	
	/** P�gia padr�o de busca de usu�rios   */
	public static final String PAGINA_BUSCA_USUARIO_BIBLIOTECA = "/biblioteca/circulacao/buscaUsuarioBiblioteca.jsp";
	
	/** P�gia para visualizar todos os v�nculo de um usu�rio  */
	public static final String PAGINA_BUSCA_TODOS_VINCULOS_USUARIO_BIBLIOTECA = "/biblioteca/circulacao/buscaTodosVinculosUsuarioBiblioteca.jsp";
	
	/** O limite de usu�rios buscados  */
	public static final int LIMITE_BUSCA_USUARIO = 100;
	
	/** Valores dos radios button na p�gina de busca para os 3 tipos poss�veis de usu�rios.. */
	public static final int RADIO_BUTTON_BUSCA_USUARIO_COMUM = 1;
	/** Valores dos radios button na p�gina de busca para os 3 tipos poss�veis de usu�rios.. */
	public static final int RADIO_BUTTON_BUSCA_USUARIO_EXTERNO = 2;
	/** Valores dos radios button na p�gina de busca para os 3 tipos poss�veis de usu�rios.. */
	public static final int RADIO_BUTTON_BUSCA_BIBLIOTECA = 3;

	
	/** O Mbean que chamou esse cado de uso e para onde o fluxo deve voltar depois que o usu�rio for selecionado.*/
	private PesquisarUsuarioBiblioteca mBeanChamador;

	
	
	/** Indica o Tipos Usu�rios que o usu�rio escolheu (Usu�rio Interno, Uusu�rio Externo, Biblioteca ), 
	 * habilita do radio button correto.
	 */
	private int valorRadioButtonTipoUsuario = RADIO_BUTTON_BUSCA_USUARIO_COMUM;
	
	
	
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarMatriculaUsuario;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarSiapeUsuario;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarNomeUsuario;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarNomeUsuarioExterno;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarVinculo;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarIdentificadorBiblioteca;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarDescricaoBiblioteca;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarCpfUsuario;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarCpfUsuarioExterno;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarPassaporte;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarPassaporteUsuarioExterno;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarBibliotecaInterna;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usu�rio. Guardam os valores dos checkbox da p�gina */
	private boolean buscarBibliotecaExterna;
	
	
	/** A matricula digitada pelo operador da p�gina de busca padr�o	 */
	private String matriculaUsuario;
	/** O SIAPE do usu�rio digitado pelo operador da p�gina de busca padr�o	 */
	private String siapeUsuario;
	/** O nome do usu�rio digitado pelo operador da p�gina de busca padr�o	 */
	private String nomeUsuario;
	/** O nome do usu�rio externo digitado pelo operador da p�gina de busca padr�o	 */
	private String nomeUsuarioExterno;
	/** O CPF do usu�rio externo digitado pelo operador da p�gina de busca padr�o	 */
	private String cpfUsuario;
	/** O CPF do usu�rio externo digitado pelo operador da p�gina de busca padr�o	 */
	private String cpfUsuarioExterno;
	/**  O passaporte do usu�rio digitado pelo  operador da p�gina de busca padr�o	 */
	private String passaporteUsuario;
	/** O passaporte do usu�rio externo digitado pelo operador da p�gina de busca padr�o	 */
	private String passaporteUsuarioExterno;
	/** A biblioteca escolhida pelo operador da p�gina de busca padr�o	 */
	private Biblioteca biblioteca = new Biblioteca ();
	/** A biblioteca externa escolhida pelo operador da p�gina de busca padr�o.	 */
	private Biblioteca bibliotecaExterna = new Biblioteca ();
	
	
	/** Guarda a lista de bibliotecas internas pelas quais o usu�rio pode buscar, j� que as bibliotecas tamb�m s�o usu�rios do sistema. */
	private List <Biblioteca> bibliotecasInternas;
	
	/** Guarda a lista de bibliotecas externas pelas quais o usu�rio pode buscar, j� que as bibliotecas tamb�m s�o usu�rios do sistema. */
	private List <Biblioteca> bibliotecasExternas;
	
	
	/** Guarda o resultado da busca, cada usu�rio encontrontra � um array de objetos contendo apenas os dados extritamente necess�rios */
	private List <Object []> infoPessoas;
	
	
	
	/** Indica que o sistema deve buscar todos as pessoas do sistema, mesmo as que n�o tenham cadastro na biblioteca*/
	private boolean buscarAsPessoasSemCadastroBiblioteca = false;
	
	/** Indica as buscas de quais tipos de usu�rios da biblioteca estar�o habilitadas */
	private boolean buscaUsuarioComumHabilitada = false;
	/** Indica as buscas de quais tipos de usu�rios da biblioteca estar�o habilitadas */
	private boolean buscaUsuarioExternoHabilitada = false;
	/** Indica as buscas de quais tipos de usu�rios da biblioteca estar�o habilitadas */
	private boolean buscaBibliotecaHabilitada = false;
	
	/** O nome da opera��o que ser� realizada a partir do caso de uso da busca*/
	private String nomeOperacao; 
	
	
	/**
	 *  <p>Utilizado caso o caso de uso que chama a pesquisa do usu�rio na biblioteca deseje realizar a��es 
	 *        extras que n�o est�o definidas na p�gina de busca padr�o. <p>
	 *  <p>O caso de uso que chamar pode passar a p�gina que vai ser inclu�da. Essa p�gina vai conter bot�es com a��es extras que o caso de uso chamador deseja 
	 *  utilizar.</p>
	 *  
	 *  <p><strong>Ver exemplo em UsuarioExternoBibliotecaMBean.java<strong></p>      
	 *        
	 */
	private String paginaAcoesExtras;
	
	/**
	 * <p>Inicia a busca dos usu�rios da biblioteca para v�rios casos de uso.</p>
	 * 
	 * <br/>
	 * M�todo n�o chamado por nenhum JSP.
	 * 
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciar(PesquisarUsuarioBiblioteca mBeanChamador
			,boolean buscaUsuarioComumHabilitada ,boolean buscaUsuarioExternoHabilitada, boolean buscaBibliotecaHabilitada, boolean buscarAsPessoasSemCadastroBiblioteca
			, String nomeOperacao, String paginaAcoesExtras) throws SegurancaException, DAOException{
		
		
		this.mBeanChamador = mBeanChamador;
		this.nomeOperacao = nomeOperacao;
		
		this.buscaUsuarioComumHabilitada = buscaUsuarioComumHabilitada;
		this.buscaUsuarioExternoHabilitada = buscaUsuarioExternoHabilitada;
		this.buscaBibliotecaHabilitada = buscaBibliotecaHabilitada;
		this.buscarAsPessoasSemCadastroBiblioteca = buscarAsPessoasSemCadastroBiblioteca;
		
		
		this.paginaAcoesExtras = paginaAcoesExtras;
		
		// Inicia o radio buttom com o primerio valor permitido //
		if(buscaUsuarioComumHabilitada)
			valorRadioButtonTipoUsuario = RADIO_BUTTON_BUSCA_USUARIO_COMUM;
		else{
			if(buscaUsuarioExternoHabilitada)
				valorRadioButtonTipoUsuario = RADIO_BUTTON_BUSCA_USUARIO_EXTERNO;
			else
				valorRadioButtonTipoUsuario = RADIO_BUTTON_BUSCA_BIBLIOTECA;
		}
		
		
		BibliotecaDao dao = null;
		
		try {
			dao = getDAO(BibliotecaDao.class);

			if (buscaBibliotecaHabilitada){
				bibliotecasInternas = dao.findAllBibliotecasInternasAtivas();
				bibliotecasExternas = dao.findAllBibliotecasExternasAtivas();
			}
				
		} finally {
			if (dao != null)
				dao.close();
		}

		return telaBuscaUsuarioBiblioteca();
	}
	
	
	
	/**
	 *    <p>Retorna o fluxo do caso de uso para o mBean que chamou a busca no acervo </p>
	 *  
	 *    <p> O Mbean que deseja realizar essa opera��o tem que implementar {@link PesquisarAcervoBiblioteca} </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaAcervo.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String selecionouUsuario() throws ArqException{
		
		
		Integer idPessoa  = getParameterInt("idPessoaSelecionada");
		Integer idBlioteca  = getParameterInt("idBibliotecaSelecionada");
		
		if(idPessoa != null){
			
			// Apenas esses 2 casos de uso n�o verificam se o usu�rio tem CPF v�lido, para suportar a visualisa��o os empr�stimos do usu�rio de migra��o
			if(! ( mBeanChamador instanceof ListaEmprestimosAtivosUsuarioMBean) 
					&& ! ( mBeanChamador instanceof EmiteHistoricoEmprestimosMBean) ){
			
				Pessoa pessoa= getGenericDAO().findByPrimaryKey(idPessoa, Pessoa.class, "cpf_cnpj", "tipo", "valido", "passaporte", "internacional");
				pessoa.setId(idPessoa);
				
				// A verifica��o dos dados corretos precisa ser realizada para n�o emitir quita��o duplicada para usu�rios que possuem 2 pessoas no sistema.
				// Porem deve verificar qualquer v�nculo infantil, diferente dos outros casos que verifica somente v�nculos infantis ativos, por exemplo,
				// na realiza��o dos empr�timos.
				
				boolean somenteAtivos = ! ( mBeanChamador instanceof VerificaSituacaoUsuarioBibliotecaMBean); 
				
				try {
					VerificaSituacaoUsuarioBibliotecaUtil.verificaDadosPessoaCorretosUtilizarBiblioteca(pessoa, somenteAtivos);
				} catch (NegocioException ne) {
					addMensagens(ne.getListaMensagens());
					return null;
				}
			}
			
			Pessoa p = new Pessoa(idPessoa);
			mBeanChamador.setPessoaBuscaPadrao(p);
		}
		
		if(idBlioteca != null){
			Biblioteca b = new Biblioteca(idBlioteca);
			mBeanChamador.setBibliotecaBuscaPadrao(b);
		}
		
		// Caso precise de novas informa��es adicionar novos par�metros no final da lista, mas n�o 
		// altere a ordem dos j� existente sen�o vai impactar em todos os caso de uso de circula��o
		String[] parametros;
		
		if(isMostrarDadosPessoa()){
			parametros = new String[]{getParameter("cpfPessoaSelecionada")
										 ,getParameter("passaportePessoaSelecionada") 
										 ,getParameter("nomePessoaSelecionada") 
										 ,getParameter("idUsuarioBibliotecaSelecionado")
										 ,getParameter("dataNascimentoUsuarioSelecionado") };
		}else{
			parametros = new String[]{getParameter("identificadorBibliotecaSelecionada")
					 ,getParameter("descricaoBibliotecaSelecionada") 
					 ,getParameter("idUsuarioBibliotecaSelecionado") };
		}
		
		mBeanChamador.setParametrosExtra(isMostrarDadosPessoa(), parametros);
		
		return mBeanChamador.selecionouUsuarioBuscaPadrao();
	}
	
	
	
	
	
	
	/**
	 * <p>Busca por um usu�rio da biblioteca, filtrando por tipo de usu�rio e campos selecionados para a busca.</p>
	 * <p><strong>Realiza a opera��o ao se clicar no bot�o ''buscar'' da p�gina.</strong></p>
	 * <br><br>
	 * M�todo chamado pelas seguintes JSPs:
	 * 	<ul><li>/sigaa.war/biblioteca/circulacao/emprestimo.jsp</li>
	 * 		<li>/sigaa.war/biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp</li>
	 * 		<li>/sigaa.war/biblioteca/buscaUsuarioBiblioteca.jsp</li></ul>
	 * 
	 * @throws ArqException 
	 */
	public String buscarUsuario() throws ArqException{
		
		String matriculaUsuarioBusca = null;
		String siapeUsuarioBusca= null;
		String nomeUsuarioBusca= null;
		String cpfUsuarioBusca= null;
		String passaporteUsuarioBusca= null;
		Biblioteca bibliotecaBusca = new Biblioteca(-1);
		
		if(buscarMatriculaUsuario) matriculaUsuarioBusca = matriculaUsuario;
		
		if(buscarSiapeUsuario) siapeUsuarioBusca = siapeUsuario;
		
		if(buscarNomeUsuario || buscarNomeUsuarioExterno){
			if(valorRadioButtonTipoUsuario == RADIO_BUTTON_BUSCA_USUARIO_EXTERNO) 
				nomeUsuarioBusca = nomeUsuarioExterno;
			else
				nomeUsuarioBusca = nomeUsuario;
		}
		
		if(buscarCpfUsuario || buscarCpfUsuarioExterno){
			if(valorRadioButtonTipoUsuario == RADIO_BUTTON_BUSCA_USUARIO_EXTERNO) 
				cpfUsuarioBusca = cpfUsuarioExterno;
			else
				cpfUsuarioBusca = cpfUsuario;
			
		} 
		
		if(buscarPassaporte || buscarPassaporteUsuarioExterno ){ 
			if(valorRadioButtonTipoUsuario == RADIO_BUTTON_BUSCA_USUARIO_EXTERNO) 
				passaporteUsuarioBusca = passaporteUsuarioExterno;
			else
				passaporteUsuarioBusca = passaporteUsuario;
			
		}
		
		if(buscarBibliotecaInterna) 
			bibliotecaBusca = biblioteca;
		else{
			if(buscarBibliotecaExterna) 
				bibliotecaBusca = bibliotecaExterna;
		}
		
		boolean buscarUsuarioComum = valorRadioButtonTipoUsuario == RADIO_BUTTON_BUSCA_USUARIO_COMUM;
		boolean buscarUsuarioExterno = valorRadioButtonTipoUsuario == RADIO_BUTTON_BUSCA_USUARIO_EXTERNO;
		boolean buscarBiblioteca = valorRadioButtonTipoUsuario == RADIO_BUTTON_BUSCA_BIBLIOTECA;
		
		try {
			infoPessoas = BuscaUsuarioBibliotecaHelper.buscaPadraoUsuarioBiblioteca (buscarUsuarioComum, buscarUsuarioExterno, buscarBiblioteca
					, cpfUsuarioBusca , passaporteUsuarioBusca, matriculaUsuarioBusca , siapeUsuarioBusca, nomeUsuarioBusca, bibliotecaBusca.getId() ,
					buscarAsPessoasSemCadastroBiblioteca, false, false);
			
			if (infoPessoas == null || infoPessoas.isEmpty())
				addMensagemErro(" Usu�rio n�o possui conta na biblioteca ou todas as suas contas est�o quitadas. ");
			
			if(infoPessoas != null && infoPessoas.size() >= LIMITE_BUSCA_USUARIO)
				addMensagemWarning(" Sua busca resultou em um n�mero muito grande de resultados, somente os "+LIMITE_BUSCA_USUARIO+" primeiros resultados est�o sendo mostrados.");
			
			return forward(PAGINA_BUSCA_USUARIO_BIBLIOTECA);
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	
	/**
	 *   M�todo chamado quando o usu�rio troca o tipo de usu�rio da busca, tem que apagar os 
	 *   resultados da busca sen�o da erro se o usu�rio tentar selecionar um usu�rio.
	 *   
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/buscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 */
	public void trocaTipoUsuarioBusca(ValueChangeEvent evt){
		
		valorRadioButtonTipoUsuario = (Integer) evt.getNewValue();
		
		infoPessoas = new ArrayList<Object[]>();
		telaBuscaUsuarioBiblioteca();
	}
	
	
	/**
	 * Verifica se a p�gina deve mostar os dados de uma pessoa no resultado da busca
	 *
	 * @return
	 */
	public boolean isMostrarDadosPessoa(){
		if(valorRadioButtonTipoUsuario == RADIO_BUTTON_BUSCA_USUARIO_COMUM 
				||valorRadioButtonTipoUsuario == RADIO_BUTTON_BUSCA_USUARIO_EXTERNO)
			return true;
		else
			return false;
	}
	
	/**
	 * Verifica se a p�gina deve mostar os dados de uma biblioteca no resultado da busca
	 *
	 * @return
	 */
	public boolean isMostarDadosBiblioteca(){
		return ! isMostrarDadosPessoa();
	}
	
	
	
	/**
	 * Retorna para a p�gina geral de busca do usu�rio da biblioteca.
	 * <br/>
	 * Usado em v�rias p�ginas em v�rios casos de uso que utilizando essa pesquisa, como por exemplo: /sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp
	 * Por isso, n�o altere esse m�todo. A n�o ser que voc� teste todos os casos de uso de circula��o novamente.
	 * 
	 * @return
	 */
	public String telaBuscaUsuarioBiblioteca(){
		return forward(PAGINA_BUSCA_USUARIO_BIBLIOTECA);
	}
	
	
	
	// sets e gets
	
	public String getMatriculaUsuario() {
		return matriculaUsuario;
	}

	public void setMatriculaUsuario(String matriculaUsuario) {
		this.matriculaUsuario = matriculaUsuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public int getQuantidadeUsuarios(){
		if(infoPessoas != null)
			return infoPessoas.size();
		else
			return 0;
	}
	
	
	public boolean isBuscaUsuarioComumHabilitada() {
		return buscaUsuarioComumHabilitada;
	}

	public boolean isBuscaUsuarioExternoHabilitada() {
		return buscaUsuarioExternoHabilitada;
	}

	public boolean isBuscaBibliotecaHabilitada() {
		return buscaBibliotecaHabilitada;
	}

	public boolean isBuscarMatriculaUsuario() {
		return buscarMatriculaUsuario;
	}

	public void setBuscarMatriculaUsuario(boolean buscarMatriculaUsuario) {
		this.buscarMatriculaUsuario = buscarMatriculaUsuario;
	}

	public boolean isBuscarNomeUsuario() {
		return buscarNomeUsuario;
	}

	public void setBuscarNomeUsuario(boolean buscarNomeUsuario) {
		this.buscarNomeUsuario = buscarNomeUsuario;
	}

	public boolean isBuscarVinculo() {
		return buscarVinculo;
	}

	public void setBuscarVinculo(boolean buscarVinculo) {
		this.buscarVinculo = buscarVinculo;
	}

	public boolean isBuscarIdentificadorBiblioteca() {
		return buscarIdentificadorBiblioteca;
	}

	public void setBuscarIdentificadorBiblioteca(boolean buscarIdentificadorBiblioteca) {
		this.buscarIdentificadorBiblioteca = buscarIdentificadorBiblioteca;
	}

	public boolean isBuscarDescricaoBiblioteca() {
		return buscarDescricaoBiblioteca;
	}
	
	public void setBuscarDescricaoBiblioteca(boolean buscarDescricaoBiblioteca) {
		this.buscarDescricaoBiblioteca = buscarDescricaoBiblioteca;
	}

	public String getNomeUsuarioExterno() {
		return nomeUsuarioExterno;
	}

	public void setNomeUsuarioExterno(String nomeUsuarioExterno) {
		this.nomeUsuarioExterno = nomeUsuarioExterno;
	}

	public String getCpfUsuario() {
		return cpfUsuario;
	}

	public void setCpfUsuario(String cpfUsuario) {
		this.cpfUsuario = cpfUsuario;
	}
	
	public String getPassaporteUsuario() {
		return passaporteUsuario;
	}

	public void setPassaporteUsuario(String passaporteUsuario) {
		this.passaporteUsuario = passaporteUsuario;
	}

	public String getPassaporteUsuarioExterno() {
		return passaporteUsuarioExterno;
	}

	public void setPassaporteUsuarioExterno(String passaporteUsuarioExterno) {
		this.passaporteUsuarioExterno = passaporteUsuarioExterno;
	}

	public String getCpfUsuarioExterno() {
		return cpfUsuarioExterno;
	}

	public void setCpfUsuarioExterno(String cpfUsuarioExterno) {
		this.cpfUsuarioExterno = cpfUsuarioExterno;
	}

	public Biblioteca getBibliotecaExterna() {
		return bibliotecaExterna;
	}

	public void setBibliotecaExterna(Biblioteca bibliotecaExterna) {
		this.bibliotecaExterna = bibliotecaExterna;
	}

	public List<SelectItem> getBibliotecasInternas() {
		return toSelectItems(bibliotecasInternas, "id", "descricao");
	}

	public void setBibliotecasInternas(List<Biblioteca> bibliotecasInternas) {
		this.bibliotecasInternas = bibliotecasInternas;
	}

	public List<SelectItem> getBibliotecasExternas() {
		return toSelectItems(bibliotecasExternas, "id", "descricao");
	}

	public void setBibliotecasExternas(List<Biblioteca> bibliotecasExternas) {
		this.bibliotecasExternas = bibliotecasExternas;
	}

	public boolean isBuscarBibliotecaInterna() {
		return buscarBibliotecaInterna;
	}

	public void setBuscarBibliotecaInterna(boolean buscarBibliotecaInterna) {
		this.buscarBibliotecaInterna = buscarBibliotecaInterna;
	}

	public boolean isBuscarBibliotecaExterna() {
		return buscarBibliotecaExterna;
	}

	public void setBuscarBibliotecaExterna(boolean buscarBibliotecaExterna) {
		this.buscarBibliotecaExterna = buscarBibliotecaExterna;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public boolean isBuscarNomeUsuarioExterno() {
		return buscarNomeUsuarioExterno;
	}

	public void setBuscarNomeUsuarioExterno(boolean buscarNomeUsuarioExterno) {
		this.buscarNomeUsuarioExterno = buscarNomeUsuarioExterno;
	}

	public boolean isBuscarCpfUsuario() {
		return buscarCpfUsuario;
	}

	public void setBuscarCpfUsuario(boolean buscarCpfUsuario) {
		this.buscarCpfUsuario = buscarCpfUsuario;
	}

	public boolean isBuscarCpfUsuarioExterno() {
		return buscarCpfUsuarioExterno;
	}

	public void setBuscarCpfUsuarioExterno(boolean buscarCpfUsuarioExterno) {
		this.buscarCpfUsuarioExterno = buscarCpfUsuarioExterno;
	}

	public String getSiapeUsuario() {
		return siapeUsuario;
	}

	public void setSiapeUsuario(String siapeUsuario) {
		this.siapeUsuario = siapeUsuario;
	}

	public List<Object[]> getInfoPessoas() {
		return infoPessoas;
	}

	public void setInfoPessoas(List<Object[]> infoPessoas) {
		this.infoPessoas = infoPessoas;
	}

	public int getRadioButtonBuscaUsuarioComum() {
		return RADIO_BUTTON_BUSCA_USUARIO_COMUM;
	}

	public int getRadioButtonBuscaUsuarioExterno() {
		return RADIO_BUTTON_BUSCA_USUARIO_EXTERNO;
	}

	public int getRadioButtonBuscaBiblioteca() {
		return RADIO_BUTTON_BUSCA_BIBLIOTECA;
	}

	public boolean isBuscarSiapeUsuario() {
		return buscarSiapeUsuario;
	}

	public void setBuscarSiapeUsuario(boolean buscarSiapeUsuario) {
		this.buscarSiapeUsuario = buscarSiapeUsuario;
	}

	public boolean isBuscarPassaporte() {
		return buscarPassaporte;
	}

	public void setBuscarPassaporte(boolean buscarPassaporte) {
		this.buscarPassaporte = buscarPassaporte;
	}

	public boolean isBuscarPassaporteUsuarioExterno() {
		return buscarPassaporteUsuarioExterno;
	}

	public void setBuscarPassaporteUsuarioExterno(boolean buscarPassaporteUsuarioExterno) {
		this.buscarPassaporteUsuarioExterno = buscarPassaporteUsuarioExterno;
	}


	public int getValorRadioButtonTipoUsuario() {
		return valorRadioButtonTipoUsuario;
	}

	public void setValorRadioButtonTipoUsuario(int valorRadioButtonTipoUsuario) {
		this.valorRadioButtonTipoUsuario = valorRadioButtonTipoUsuario;
	}

	public String getNomeOperacao() {
		return nomeOperacao;
	}

	public String getPaginaAcoesExtras() {
		return paginaAcoesExtras;
	}

	public void setPaginaAcoesExtras(String paginaAcoesExtras) {
		this.paginaAcoesExtras = paginaAcoesExtras;
	}
	
}
