/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean que controla a página de busca geral do usuário da biblioteca.</p>
 * <p> 
 * 	 <strong>Obsevação 1 : </strong> A busca sempre é feita utilizando a pessoa ou a Biblioteca, não deve-se 
 *    verificar qual o vínculo do usuário para os empréstimos senão fica muito pessada a consulta. 
 *    O vínculo só deve ser mostrado ou escolher um usuário específico  
 * </p>
 * <p> 
 *   <strong>Obsevação 2:. </strong> Essa busca de usuários deve ser utilizada em todo o sistema para ficar centralizado e fácil de manter. 
 * </p>
 *
 * <p><strong>Qualquer parte do sistema que quiser utilizar essa pesquisa pública no acervo deve implentar a interface {@link PesquisarUsuarioBiblioteca}<strong></p>
 *
 * @author jadson
 * @since 10/10/2008
 * @version 1.0 criação da classe
 *
 */
@Component("buscaUsuarioBibliotecaMBean")
@Scope("request")
public class BuscaUsuarioBibliotecaMBean extends SigaaAbstractController<UsuarioBiblioteca>{
	
	/** Págia padrão de busca de usuários   */
	public static final String PAGINA_BUSCA_USUARIO_BIBLIOTECA = "/biblioteca/circulacao/buscaUsuarioBiblioteca.jsp";
	
	/** Págia para visualizar todos os vínculo de um usuário  */
	public static final String PAGINA_BUSCA_TODOS_VINCULOS_USUARIO_BIBLIOTECA = "/biblioteca/circulacao/buscaTodosVinculosUsuarioBiblioteca.jsp";
	
	/** O limite de usuários buscados  */
	public static final int LIMITE_BUSCA_USUARIO = 100;
	
	/** Valores dos radios button na página de busca para os 3 tipos possíveis de usuários.. */
	public static final int RADIO_BUTTON_BUSCA_USUARIO_COMUM = 1;
	/** Valores dos radios button na página de busca para os 3 tipos possíveis de usuários.. */
	public static final int RADIO_BUTTON_BUSCA_USUARIO_EXTERNO = 2;
	/** Valores dos radios button na página de busca para os 3 tipos possíveis de usuários.. */
	public static final int RADIO_BUTTON_BUSCA_BIBLIOTECA = 3;

	
	/** O Mbean que chamou esse cado de uso e para onde o fluxo deve voltar depois que o usuário for selecionado.*/
	private PesquisarUsuarioBiblioteca mBeanChamador;

	
	
	/** Indica o Tipos Usuários que o usuário escolheu (Usuário Interno, Uusuário Externo, Biblioteca ), 
	 * habilita do radio button correto.
	 */
	private int valorRadioButtonTipoUsuario = RADIO_BUTTON_BUSCA_USUARIO_COMUM;
	
	
	
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarMatriculaUsuario;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarSiapeUsuario;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarNomeUsuario;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarNomeUsuarioExterno;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarVinculo;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarIdentificadorBiblioteca;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarDescricaoBiblioteca;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarCpfUsuario;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarCpfUsuarioExterno;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarPassaporte;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarPassaporteUsuarioExterno;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarBibliotecaInterna;
	/** Indicam quais dos campos de busca o operador vai utilizar para realizar a busca do usuário. Guardam os valores dos checkbox da página */
	private boolean buscarBibliotecaExterna;
	
	
	/** A matricula digitada pelo operador da página de busca padrão	 */
	private String matriculaUsuario;
	/** O SIAPE do usuário digitado pelo operador da página de busca padrão	 */
	private String siapeUsuario;
	/** O nome do usuário digitado pelo operador da página de busca padrão	 */
	private String nomeUsuario;
	/** O nome do usuário externo digitado pelo operador da página de busca padrão	 */
	private String nomeUsuarioExterno;
	/** O CPF do usuário externo digitado pelo operador da página de busca padrão	 */
	private String cpfUsuario;
	/** O CPF do usuário externo digitado pelo operador da página de busca padrão	 */
	private String cpfUsuarioExterno;
	/**  O passaporte do usuário digitado pelo  operador da página de busca padrão	 */
	private String passaporteUsuario;
	/** O passaporte do usuário externo digitado pelo operador da página de busca padrão	 */
	private String passaporteUsuarioExterno;
	/** A biblioteca escolhida pelo operador da página de busca padrão	 */
	private Biblioteca biblioteca = new Biblioteca ();
	/** A biblioteca externa escolhida pelo operador da página de busca padrão.	 */
	private Biblioteca bibliotecaExterna = new Biblioteca ();
	
	
	/** Guarda a lista de bibliotecas internas pelas quais o usuário pode buscar, já que as bibliotecas também são usuários do sistema. */
	private List <Biblioteca> bibliotecasInternas;
	
	/** Guarda a lista de bibliotecas externas pelas quais o usuário pode buscar, já que as bibliotecas também são usuários do sistema. */
	private List <Biblioteca> bibliotecasExternas;
	
	
	/** Guarda o resultado da busca, cada usuário encontrontra é um array de objetos contendo apenas os dados extritamente necessários */
	private List <Object []> infoPessoas;
	
	
	
	/** Indica que o sistema deve buscar todos as pessoas do sistema, mesmo as que não tenham cadastro na biblioteca*/
	private boolean buscarAsPessoasSemCadastroBiblioteca = false;
	
	/** Indica as buscas de quais tipos de usuários da biblioteca estarão habilitadas */
	private boolean buscaUsuarioComumHabilitada = false;
	/** Indica as buscas de quais tipos de usuários da biblioteca estarão habilitadas */
	private boolean buscaUsuarioExternoHabilitada = false;
	/** Indica as buscas de quais tipos de usuários da biblioteca estarão habilitadas */
	private boolean buscaBibliotecaHabilitada = false;
	
	/** O nome da operação que será realizada a partir do caso de uso da busca*/
	private String nomeOperacao; 
	
	
	/**
	 *  <p>Utilizado caso o caso de uso que chama a pesquisa do usuário na biblioteca deseje realizar ações 
	 *        extras que não estão definidas na página de busca padrão. <p>
	 *  <p>O caso de uso que chamar pode passar a página que vai ser incluída. Essa página vai conter botões com ações extras que o caso de uso chamador deseja 
	 *  utilizar.</p>
	 *  
	 *  <p><strong>Ver exemplo em UsuarioExternoBibliotecaMBean.java<strong></p>      
	 *        
	 */
	private String paginaAcoesExtras;
	
	/**
	 * <p>Inicia a busca dos usuários da biblioteca para vários casos de uso.</p>
	 * 
	 * <br/>
	 * Método não chamado por nenhum JSP.
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
	 *    <p> O Mbean que deseja realizar essa operação tem que implementar {@link PesquisarAcervoBiblioteca} </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			
			// Apenas esses 2 casos de uso não verificam se o usuário tem CPF válido, para suportar a visualisação os empréstimos do usuário de migração
			if(! ( mBeanChamador instanceof ListaEmprestimosAtivosUsuarioMBean) 
					&& ! ( mBeanChamador instanceof EmiteHistoricoEmprestimosMBean) ){
			
				Pessoa pessoa= getGenericDAO().findByPrimaryKey(idPessoa, Pessoa.class, "cpf_cnpj", "tipo", "valido", "passaporte", "internacional");
				pessoa.setId(idPessoa);
				
				// A verificação dos dados corretos precisa ser realizada para não emitir quitação duplicada para usuários que possuem 2 pessoas no sistema.
				// Porem deve verificar qualquer vínculo infantil, diferente dos outros casos que verifica somente vínculos infantis ativos, por exemplo,
				// na realização dos emprétimos.
				
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
		
		// Caso precise de novas informações adicionar novos parâmetros no final da lista, mas não 
		// altere a ordem dos já existente senão vai impactar em todos os caso de uso de circulação
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
	 * <p>Busca por um usuário da biblioteca, filtrando por tipo de usuário e campos selecionados para a busca.</p>
	 * <p><strong>Realiza a operação ao se clicar no botão ''buscar'' da página.</strong></p>
	 * <br><br>
	 * Método chamado pelas seguintes JSPs:
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
				addMensagemErro(" Usuário não possui conta na biblioteca ou todas as suas contas estão quitadas. ");
			
			if(infoPessoas != null && infoPessoas.size() >= LIMITE_BUSCA_USUARIO)
				addMensagemWarning(" Sua busca resultou em um número muito grande de resultados, somente os "+LIMITE_BUSCA_USUARIO+" primeiros resultados estão sendo mostrados.");
			
			return forward(PAGINA_BUSCA_USUARIO_BIBLIOTECA);
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	
	/**
	 *   Método chamado quando o usuário troca o tipo de usuário da busca, tem que apagar os 
	 *   resultados da busca senão da erro se o usuário tentar selecionar um usuário.
	 *   
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Verifica se a página deve mostar os dados de uma pessoa no resultado da busca
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
	 * Verifica se a página deve mostar os dados de uma biblioteca no resultado da busca
	 *
	 * @return
	 */
	public boolean isMostarDadosBiblioteca(){
		return ! isMostrarDadosPessoa();
	}
	
	
	
	/**
	 * Retorna para a página geral de busca do usuário da biblioteca.
	 * <br/>
	 * Usado em várias páginas em vários casos de uso que utilizando essa pesquisa, como por exemplo: /sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp
	 * Por isso, não altere esse método. A não ser que você teste todos os casos de uso de circulação novamente.
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
