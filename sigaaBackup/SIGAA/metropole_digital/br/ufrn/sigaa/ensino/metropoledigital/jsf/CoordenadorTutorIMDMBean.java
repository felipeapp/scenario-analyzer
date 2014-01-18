package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CoordenadorTutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CoordenadorTutorIMD;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoCadastroCoordenadorTutorIMD;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * MBean responsável por realizar as operações que manipulam a classe Coordenador de Pólo do IMD
 * 
 * @author Rafael Barros
 *
 */

@Scope("request")
@Component("coordenadorTutorIMD")
public class CoordenadorTutorIMDMBean  extends SigaaAbstractController {

	/** Objeto referente a classe CoordenadorTutorIMD que se relaciona diretamente com o bean **/
	private CoordenadorTutorIMD coordenador;
	
	/** Atributo que irá armazenar qual o tipo de busca foi selecionada para vincular a pessoa ao coordenador,
	 *  nome ou CPF. **/
	private String tipoBuscaPessoa;
	
	/**Coleção de objetos da classe coordenador de pólo do IMD que armazenará o resultados das buscas.**/
	private Collection<CoordenadorTutorIMD> listaCoordenadores = new ArrayList<CoordenadorTutorIMD>();
	
	/**Atributo responsável por armazenar o nome informado para busca do coordenador**/
	private String nomePessoaBusca = null;
	
	/**Atributo responsável por armazenar o cpf informado para busca do coordenador**/
	private Long cpfPessoaBusca = null;
	
	
	/**Atributo de controle responsável por armazenar se o campo nome foi informado ou não para a busca do coordenador**/
	private boolean buscaNome = false;
	
	/**Atributo de controle responsável por armazenar se o campo cpf foi informado ou não para a busca do coordenador**/
	private boolean buscaCpf = false;
	
	/**Atributo de controle responsável por armazenar se o campo listar todos foi selecionado**/
	private boolean buscaListarTodos = false;
	
	/**Coleção de objetos da classe coordenador de pólo do IMD que armazenará a lista de vínculos de coordenador que uma determinada pessoa possui.**/
	private Collection<CoordenadorTutorIMD> listaVinculosCoordenador = new ArrayList<CoordenadorTutorIMD>();
	
	public CoordenadorTutorIMDMBean() {
		coordenador = new CoordenadorTutorIMD();
		coordenador.setPessoa(new Pessoa());
		nomePessoaBusca = null;
		cpfPessoaBusca = null;
		buscaNome = false;
		buscaCpf = false;
		buscaListarTodos = false;
	}
	

	public CoordenadorTutorIMD getCoordenador() {
		return coordenador;
	}



	public void setCoordenador(CoordenadorTutorIMD coordenador) {
		this.coordenador = coordenador;
	}



	public String getTipoBuscaPessoa() {
		return tipoBuscaPessoa;
	}



	public void setTipoBuscaPessoa(String tipoBuscaPessoa) {
		this.tipoBuscaPessoa = tipoBuscaPessoa;
	}



	public Collection<CoordenadorTutorIMD> getListaCoordenadores() {
		return listaCoordenadores;
	}



	public void setListaCoordenadores(
			Collection<CoordenadorTutorIMD> listaCoordenadores) {
		this.listaCoordenadores = listaCoordenadores;
	}



	public String getNomePessoaBusca() {
		return nomePessoaBusca;
	}



	public void setNomePessoaBusca(String nomePessoaBusca) {
		this.nomePessoaBusca = nomePessoaBusca;
	}



	public Long getCpfPessoaBusca() {
		return cpfPessoaBusca;
	}



	public void setCpfPessoaBusca(Long cpfPessoaBusca) {
		this.cpfPessoaBusca = cpfPessoaBusca;
	}


	public boolean isBuscaNome() {
		return buscaNome;
	}



	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}



	public boolean isBuscaCpf() {
		return buscaCpf;
	}



	public void setBuscaCpf(boolean buscaCpf) {
		this.buscaCpf = buscaCpf;
	}


	public boolean isBuscaListarTodos() {
		return buscaListarTodos;
	}



	public void setBuscaListarTodos(boolean buscaListarTodos) {
		this.buscaListarTodos = buscaListarTodos;
	}


	public Collection<CoordenadorTutorIMD> getListaVinculosCoordenador() {
		return listaVinculosCoordenador;
	}



	public void setListaVinculosCoordenador(
			Collection<CoordenadorTutorIMD> listaVinculosCoordenador) {
		this.listaVinculosCoordenador = listaVinculosCoordenador;
	}



	/**
	 * Redireciona a página para a tela inicial de seleção da pessoa que será cadastrada o coordenador do IMD
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/lista.jsp</li>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsf</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
	 */
	public String selecionarPessoaCadastro() throws SegurancaException {
		return forward("/metropole_digital/coordenador_tutor/busca_pessoa.jsf"); 
	}
	
	/**
	 * Redireciona a página para a listagem dos coordenadores
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/menus/menu_imd.jsf</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
	 */
	public String redicionarListagem() throws SegurancaException {
		return forward("/metropole_digital/coordenador_tutor/lista.jsf"); 
	}

	/**
	 * Busca pessoas de acordo com os critérios de busca informados.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa/geral/coordenador_tutor/busca_pessoa.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
	 * @throws SegurancaException
	 */
	public String buscarPessoa() throws SegurancaException {
		
		tipoBuscaPessoa = getParameter("tipoBuscaPessoa");
		
		// Realizar a consulta
		PessoaDao dao = getDAO(PessoaDao.class);
		try {
			if(tipoBuscaPessoa == null){
				addMessage("É necessário selecionar um dos critérios de busca.",  TipoMensagemUFRN.ERROR);
			} else {
			
				// Efetuar a consulta
				if ("nome".equalsIgnoreCase(tipoBuscaPessoa)){
					if(coordenador.getPessoa().getNome().length() > 0){
						setResultadosBusca(dao.findByNomeTipo(coordenador.getPessoa().getNome(), Pessoa.PESSOA_FISICA, null));
						if (getResultadosBusca() == null || getResultadosBusca().isEmpty()) {
							addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
						}
					}
					else {
						addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
					}
				} else { 
					if ("cpf".equalsIgnoreCase(tipoBuscaPessoa)){
	
						if (coordenador.getPessoa() == null || coordenador.getPessoa().getCpf_cnpj() == null || coordenador.getPessoa().getCpf_cnpj() == 0) {
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
							setResultadosBusca(null);
						}else{
							setResultadosBusca(dao.findByCpfCnpj(coordenador.getPessoa().getCpf_cnpj()));
							if (getResultadosBusca() == null || getResultadosBusca().isEmpty()) {
								addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
							}
						}
					}else
						setResultadosBusca(null);
				}

			}

		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um problema ocorreu durante a consulta. Contacte os administradores do sistema");
			return null;
		}
		return forward("/metropole_digital/coordenador_tutor/busca_pessoa.jsp");
	}

	/**
	 * Carrega do banco a pessoa selecionada na página.
	 * 
	 * <br/>
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/geral/coordenador_tutor/busca_pessoa.jsp</li>
	 * </ul>
	 * 
	 * @return A página que representa a operação a ser realizada sobre a pessoa.
	 */
	public String escolhePessoa() {
		CoordenadorTutorIMDDao coordenadorDao = new CoordenadorTutorIMDDao();
		coordenador.setPessoa(null);
		
		try {
			Collection<CoordenadorTutorIMD> lista = new ArrayList<CoordenadorTutorIMD>();
			
			lista = coordenadorDao.findCoordenadorByPessoa(getParameterInt("idPessoa"));
			
			if(lista.isEmpty()) {
				coordenador.setPessoa((Pessoa)getGenericDAO().findByPrimaryKey(getParameterInt("idPessoa"), Pessoa.class));
				
				if (coordenador.getPessoa() == null) {
					addMensagemErro("Pessoa não encontrada!");
					return null;
				} else {
					return forward("/metropole_digital/coordenador_tutor/form.jsp");
				}
			} else {
				addMensagemErro("A pessoa selecionada já possui o vínculo de coordenador de tutores.");
				return null;
			}
			
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações da pessoa.");
			e.printStackTrace();
			return null;
		} finally {
			coordenadorDao.close();
		}

		
	}
	
	/**
	 * Retorna a página que representa a operação a ser realizada sobre uma pessoa.
	 * 
	 * <p>Método não é chamado em nenhuma JSP.</p>
	 * 
	 * @param pessoa Pessoa sobre a qual a operação será realizada.
	 * @return Página referente à operação.
	 */
	private String redirecionarPessoa(Pessoa pessoa) {
		coordenador.setPessoa(pessoa);
		return forward("/metropole_digital/coordenador_tutor/form.jsp");
	}
	
	
	/**
	 * Método acionado para armazenar as informações de cadastro ou alteração de coordenador
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
	 * @throws ArqException, NegocioException 
	 */
	public String cadastrar() throws ArqException, NegocioException{
		//GERA OS ERROS DE VALICAÇÃO DO FORMULÁRIO
		erros.addAll(coordenador.validate());
		CoordenadorTutorIMDDao coordenadorTutorIMDDao = getDAO(CoordenadorTutorIMDDao.class);
		try {
			if (!hasErrors()) {
				
				listaVinculosCoordenador = coordenadorTutorIMDDao.findCoordenadorByPessoa(coordenador.getPessoa().getId());
				
				boolean cadastro = false;
				
				if(coordenador.getId() > 0){
					cadastro = false;
				} else {
					cadastro = true;
				}
				
				boolean existe = false;
				if(cadastro) {
					Collection<CoordenadorTutorIMD> lista = new ArrayList<CoordenadorTutorIMD>();
					lista = coordenadorTutorIMDDao.findCoordenadorByPessoa(coordenador.getPessoa().getId());
					if(lista.isEmpty()) {
						existe = false;
					} else {
						existe = true;
					}
				}
				
				if(!existe) {
				
					MovimentoCadastroCoordenadorTutorIMD mov = new MovimentoCadastroCoordenadorTutorIMD();
					mov.setCoordenadorTutorIMD(coordenador);
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_COORDENADOR_TUTOR_IMD);
					prepareMovimento(SigaaListaComando.CADASTRAR_COORDENADOR_TUTOR_IMD);
					execute(mov);
					//removeOperacaoAtiva();
					
					if(! cadastro){
						
						addMessage("Coordenador alterado com sucesso.", TipoMensagemUFRN.INFORMATION);
						coordenador = null;
						buscarCoordenadorSemRetorno();
						return forward("/metropole_digital/coordenador_tutor/lista.jsp");
						
					} else {
						
						addMessage("Coordenador cadastrado com sucesso.", TipoMensagemUFRN.INFORMATION);
						coordenador = null;
						return forward("/metropole_digital/menus/menu_imd.jsf");
						
					}
				} else {
					addMessage("A pessoa selecionada já possui o vínculo de coordenador de tutores.", TipoMensagemUFRN.INFORMATION);
					return forward("/metropole_digital/menus/menu_imd.jsf");
				}
				
				
			} else {
				return null;
			} 
		} finally {
			coordenadorTutorIMDDao.close();
		}
	}
	
	/**
	 * Método responsável por identificar o coordenador selecionado na listagem e redirecionar a página para o formulário de edição
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
	 */
	public String atualizar() throws DAOException{
		if(getParameter("id") != null &&  getParameter("id").length() > 0) {
			int id = getParameterInt("id");
			coordenador = getGenericDAO().findByPrimaryKey(id, CoordenadorTutorIMD.class);
			if(coordenador != null){
				return forward("/metropole_digital/coordenador_tutor/form.jsp");
			} else {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			}
		} else {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		}
		return forward("/metropole_digital/coordenador_tutor/lista.jsp");
	}
	
	
	/**
	 * Método responsável pela remoção de um registro
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
	 */
	public String remover() throws DAOException{
		CoordenadorTutorIMDDao coordenadorTutorIMDDao = getDAO(CoordenadorTutorIMDDao.class);
		try {
			coordenador = new CoordenadorTutorIMD();
			if(getParameter("id").length() > 0) {
				int id = getParameterInt("id");		
				coordenador = getGenericDAO().findByPrimaryKey(id, CoordenadorTutorIMD.class);
				
				if(coordenador != null){
					
					coordenador = getGenericDAO().findByPrimaryKey(id, CoordenadorTutorIMD.class);
					if(coordenador == null) {
						addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
					} else {
						coordenadorTutorIMDDao.remove(coordenador);
						addMessage("Coordenador removido com sucesso.", TipoMensagemUFRN.INFORMATION);
						
						if(nomePessoaBusca != null || cpfPessoaBusca != null){
							listaCoordenadores = (List<CoordenadorTutorIMD>) coordenadorTutorIMDDao.findCoordenador(nomePessoaBusca, cpfPessoaBusca);
						} else {
							listaCoordenadores = coordenadorTutorIMDDao.findAll(CoordenadorTutorIMD.class, "pessoa.nome", "ASC");
						}	
					}
					
				} else {
					listaCoordenadores = Collections.emptyList();
					addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
					return forward("/metropole_digital/coordenador_tutor/lista.jsp");
				}
				
				
			} else {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			}
			return null;
		} finally {
			coordenadorTutorIMDDao.close();
		}
	}
	
	
	/**
	 * Realiza pesquisa dos coordenadores cadastrados de acordo com as informações repassadas pelo usuário SEM RETORNO
	 *  
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
	 */
	public void buscarCoordenadorSemRetorno() throws DAOException{
		CoordenadorTutorIMDDao dao = getDAO(CoordenadorTutorIMDDao.class);
		try {
			if(!buscaListarTodos && !buscaNome && !buscaCpf) {
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			} else {
				if(verificarCriterios()){
					
					if(! buscaListarTodos) { 
						boolean nomeInformado = false;
						boolean cpfInformado = false;
						if(buscaNome && nomePessoaBusca != null && nomePessoaBusca.length() > 0){
							nomeInformado = true;
						}
						if(buscaCpf && cpfPessoaBusca != null && cpfPessoaBusca > 0){
							cpfInformado = true;
						}
						
						if(nomeInformado && cpfInformado) {
							listaCoordenadores = (List<CoordenadorTutorIMD>) dao.findCoordenador(nomePessoaBusca, cpfPessoaBusca);
						} else if(!nomeInformado && cpfInformado) {
							listaCoordenadores = (List<CoordenadorTutorIMD>) dao.findCoordenador(null, cpfPessoaBusca);
						} else if(nomeInformado && !cpfInformado) {
							listaCoordenadores = (List<CoordenadorTutorIMD>) dao.findCoordenador(nomePessoaBusca, null);
						} 
						
						if(listaCoordenadores.isEmpty()){
							addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
						}
					} else {
						listaCoordenadores = dao.findAll(CoordenadorTutorIMD.class, "pessoa.nome", "ASC");
					}
						
				}
				
				else {
					if(buscaNome){
						if((nomePessoaBusca == null || nomePessoaBusca.length() <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
						}
					}
					if(buscaCpf){
						if((cpfPessoaBusca == null || cpfPessoaBusca <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
						}
					}
					
				}
			}
			
		} finally{
			dao.close();
		}
		
	}
	
	
	/**
	 * Realiza pesquisa dos coordenadores cadastrados de acordo com as informações repassadas pelo usuário
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
	 */
	public String buscarCoordenador() throws DAOException{
		CoordenadorTutorIMDDao dao = getDAO(CoordenadorTutorIMDDao.class);
		try {
			if(!buscaListarTodos && !buscaNome && !buscaCpf) {
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			} else {
				
				if(verificarCriterios()){
					
						listaCoordenadores = dao.findAll(CoordenadorTutorIMD.class, "pessoa.nome", "ASC");
						
						if(! buscaListarTodos) { 
					
							boolean nomeInformado = false;
							boolean cpfInformado = false;
							if(buscaNome && nomePessoaBusca != null && nomePessoaBusca.length() > 0){
								nomeInformado = true;
							}
							if(buscaCpf && cpfPessoaBusca != null && cpfPessoaBusca > 0){
								cpfInformado = true;
							}
							
							if(nomeInformado && cpfInformado) {
								listaCoordenadores = (List<CoordenadorTutorIMD>) dao.findCoordenador(nomePessoaBusca, cpfPessoaBusca);
							} else if(!nomeInformado && cpfInformado) {
								listaCoordenadores = (List<CoordenadorTutorIMD>) dao.findCoordenador(null, cpfPessoaBusca);
							} else if(nomeInformado && !cpfInformado) {
								listaCoordenadores = (List<CoordenadorTutorIMD>) dao.findCoordenador(nomePessoaBusca, null);
							} 
		
							if(listaCoordenadores.isEmpty()){
								addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
							}
						}					
						
				}
				
				else {
					if(buscaNome){
						if((nomePessoaBusca == null || nomePessoaBusca.length() <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
						}
					}
					if(buscaCpf){
						if((cpfPessoaBusca == null || cpfPessoaBusca <= 0)){
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
						}
					}
					
				}
			}
			
			
			
			return null;
			
		} finally{
			dao.close();
		}
		
	}
	
	
	/**
	 * Realiza pesquisa dos coordenadores cadastrados de acordo com as informações repassadas pelo usuário
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/lista.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return TRUE: se todos os critérios de busca marcados foram informados. FALSE: se os critérios de busca marcados não foram informados.
	 */
	public boolean verificarCriterios(){
		if(buscaNome){
			if((nomePessoaBusca == null || nomePessoaBusca.length() <= 0)){
				return false;
			}
		}
		if(buscaCpf){
			if((cpfPessoaBusca == null || cpfPessoaBusca <= 0)){
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Método cancelar para retornar a página
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual será redirecionada
	 */
	public String cancelar() {
		nomePessoaBusca = null;
		cpfPessoaBusca = null;
		coordenador = new CoordenadorTutorIMD();
		return forward("/metropole_digital/menus/menu_imd.jsp");
	}
	
	/**
	 * Método cancelar para retornar a página
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual será redirecionada
	 */
	public String cancelarAlteracao() {
		return forward("/metropole_digital/coordenador_tutor/lista.jsp");
	}
	
	
	/**
	 * Método cancelar para retornar a página de busca de pessoa
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/coordenador_tutor/busca_pessoa.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual será redirecionada
	 */
	public String cancelarBuscaPessoa() {
		return forward("/metropole_digital/menus/menu_imd.jsp");
	}
	
	
}
