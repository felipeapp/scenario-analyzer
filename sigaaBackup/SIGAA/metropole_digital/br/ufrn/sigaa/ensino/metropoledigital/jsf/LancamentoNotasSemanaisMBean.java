package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.components.barbecue.BarcodeProviders.NW7Provider;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.org.apache.bcel.internal.generic.NEW;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.metropoledigital.dao.AcompanhamentoSemanalDiscenteDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoParcialIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.LancamentoFreqEncontroDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RetornoNotaPVIntegracaoMoodle;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoCriacaoRegistrosAcompanhamento;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Entidade responsável por gerenciar os lançamentos das notas semanais dos discentes
 * 
 * @author Rafael Silva
 * @author Rafael Barros
 *
 */
@Scope("request")
@Component("lancamentoNotasSemanais")
public class LancamentoNotasSemanaisMBean extends SigaaAbstractController {	
	
	/** Entidade que irá armazenar a turma de entrada selecionada para se efetuar a frequência semanal do IMD **/
	TurmaEntradaTecnico turmaEntradaSelecionada;
	
	/** Entidade que irá armazenar a listagem dos discentes do curso técnico que compõem a turma de entrada selecionada para se efetuar a frequência **/
	Collection<DiscenteTecnico> listaDiscentesTurma;
	
	/** Entidade que irá armazenar a listagem dos períodos que compõem a turma de entrada selecionada para se efetuar a frequência **/
	Collection<PeriodoAvaliacao> listaPeriodosTurma;
	
	/** Tabela responsável por armazenar os registros de acompanhamentos dos discentes em função dos períodos de avaliação **/
	List<List<AcompanhamentoSemanalDiscente>> tabelaAcompanhamento;
	
	/** Entidade que possui a coleção geral de acompanhamentos para a turma**/
	List<AcompanhamentoSemanalDiscente> listaAcompanhamentosGeral;
	
	/** Variável responsável por armazenar a String de retorno das notas PV do MOODLE **/
	String retornoPVMooodle;
	
	/** Variável responsável por os códigos de integração dos periodos de avaliação **/
	String codigosIntegracaoPeriodos;
	
	/**Data Atual*/
	private Date dataAtual = new Date();   
	
	
	/**
	 * Construtor da Classe
	 */
	public LancamentoNotasSemanaisMBean() {
		this.turmaEntradaSelecionada = new TurmaEntradaTecnico();
		this.listaDiscentesTurma = new ArrayList<DiscenteTecnico>();
		this.listaPeriodosTurma = Collections.emptyList();
		this.listaAcompanhamentosGeral = new ArrayList<AcompanhamentoSemanalDiscente>();
		this.retornoPVMooodle = "";
		this.codigosIntegracaoPeriodos = "";
	}

	
	/** 
	 * Função que efetua o redirecionamento para a página de lançamento de notas
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws Exception 
	 */
	public String preLancarNotasSemanais() throws Exception{
		int id = getParameterInt("id");
		if (getDAO(ConsolidacaoParcialIMDDao.class).isConsolidadoParcialmente(id)) {
			addMessage("O lançamento de notas por período não pode ser realizado, pois a turma já foi consolidada.", TipoMensagemUFRN.ERROR);
			return redirect("/metropole_digital/principal.jsf");
		}
		
		turmaEntradaSelecionada.setId(id);
		turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(turmaEntradaSelecionada.getId(), TurmaEntradaTecnico.class);
		
		listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(
				turmaEntradaSelecionada.getId());
		listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(
				turmaEntradaSelecionada.getId());

		prepareMovimento(SigaaListaComando.CRIACAO_REGISTROS_ACOMPANHAMENTO_DISCENTE);
		
		MovimentoCriacaoRegistrosAcompanhamento movAcomp = new MovimentoCriacaoRegistrosAcompanhamento();
		movAcomp.setListaDiscentesTurma(listaDiscentesTurma);
		movAcomp.setListaGeralAcompanhamentos(listaAcompanhamentosGeral);
		movAcomp.setListaPeriodosTurma(listaPeriodosTurma);
		movAcomp.setTurmaSelecionada(turmaEntradaSelecionada);
		movAcomp.setCodMovimento(SigaaListaComando.CRIACAO_REGISTROS_ACOMPANHAMENTO_DISCENTE);

		executeWithoutClosingSession(movAcomp);
		
		//carregarIntegracaoPvMoodle();
		preencheTabelaAcompanhamento();
		return forward("/metropole_digital/lancar_notas_semanais/form.jsp");
	}
	
	/** 
	 * Função que efetua o redirecionamento para a página de visualização das notas
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws Exception 
	 */
	public String visualizarNotasSemanais(){
		return forward("/metropole_digital/lancar_notas_semanais/view.jsp");
	}
		
	
	/** 
	 * Função que efetua o salvamento dos dados
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws DAOException
	 */
	public String salvar() throws DAOException{	
		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {
			for(List<AcompanhamentoSemanalDiscente> linha: tabelaAcompanhamento){
				for(AcompanhamentoSemanalDiscente acomp: linha){
					acompDao.createOrUpdate(acomp);
				}
			}
			addMessage("Dados armazenados com sucesso.", TipoMensagemUFRN.INFORMATION);
			return null;
		} finally {
			acompDao.close();
		}
	}
	
	/** 
	 * Função que efetua o preenchimento dos registros da tabela de acompanhamentos
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws 
	 * 
	 */
	public String voltarTelaPortal(){
		return forward("/metropole_digital/principal.jsp");
	}
		
	
	/** 
	 * Função que efetua o preenchimento dos registros da tabela de acompanhamentos
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/lancar_notas_semanais/view.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws DAOException
	 * 
	 */
	public void preencheTabelaAcompanhamento() throws DAOException{
		AcompanhamentoSemanalDiscenteDao acompanhamentoDao = getDAO(AcompanhamentoSemanalDiscenteDao.class);	
		try {
			tabelaAcompanhamento = new ArrayList<List<AcompanhamentoSemanalDiscente>>();
			//listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(turmaEntradaSelecionada.getId());
			//listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(turmaEntradaSelecionada.getId());

			turmaEntradaSelecionada.getDadosTurmaIMD().getCronograma().getUnidadeTempo().getDescricao();
			//Carrega a lista de acompanhamento. Caso não existe ele cria a lista;
						
			/*List<DiscenteTecnico> discentesSemAcompanhamento = (ArrayList) acompanhamentoDao.findDiscentesSemAcompanhamento(turmaEntradaSelecionada.getId());
			if(!discentesSemAcompanhamento.isEmpty()){
				for(DiscenteTecnico discente: discentesSemAcompanhamento){
					for(PeriodoAvaliacao periodo: listaPeriodosTurma){
						AcompanhamentoSemanalDiscente acomp = new AcompanhamentoSemanalDiscente();
						acomp.setPeriodoAvaliacao(periodo);
						acomp.setDiscente(discente);
						acompanhamentoDao.createOrUpdate(acomp);						
					}
				}
			}*/
			this.listaAcompanhamentosGeral = (ArrayList) acompanhamentoDao.findAcompanhamentosByTurmaEntradaProjetado(turmaEntradaSelecionada.getId());
			int contadorLista = 0;
			List<AcompanhamentoSemanalDiscente> listaAuxiliarAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
			
			//Tabela de Acomponhamento
			for(AcompanhamentoSemanalDiscente acomp : this.listaAcompanhamentosGeral){
				if(contadorLista == listaPeriodosTurma.size()){
					tabelaAcompanhamento.add(listaAuxiliarAcompanhamentos);
					listaAuxiliarAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
					contadorLista = 0;
				}
				listaAuxiliarAcompanhamentos.add(acomp);
				contadorLista ++;
			}
			tabelaAcompanhamento.add(listaAuxiliarAcompanhamentos);
		} finally {
			acompanhamentoDao.close();
		}
	}
	
	
	
	/** Função que preenche a listagem dos discentes, períodos de avaliação e os registros das notas semanais da Turma de Entrada selecionada.
	 *  A função também verifica se os registros da frequencia da turma já foram criados, caso não tenham sido criados, a função chama uma nova
	 *  função que irá efetuar o procedimento de criar os registros da frequencia da turma
	 *  
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public void findDiscentesPeriodosTurma() throws Exception{	
		listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(turmaEntradaSelecionada.getId());
		listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(turmaEntradaSelecionada.getId());
		
		carregarIntegracaoPvMoodle();
		
		//preencheTabelaAcompanhamento(); 
	}
	
	//GETTERS AND SETTERS
	public TurmaEntradaTecnico getTurmaEntradaSelecionada() {
		return turmaEntradaSelecionada;
	}

	public void setTurmaEntradaSelecionada(
			TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}

	
	/** Função GET do atributo listaDiscentesTurma que também efetua o preenchimento dessa entidate caso esteja vazia
	 *  
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public Collection<DiscenteTecnico> getListaDiscentesTurma() throws Exception {
		if(this.listaDiscentesTurma.isEmpty()){
			findDiscentesPeriodosTurma();
		}
		
		return listaDiscentesTurma;
	}

	public void setListaDiscentesTurma(Collection<DiscenteTecnico> listaDiscentesTurma) {
		this.listaDiscentesTurma = listaDiscentesTurma;
	}
	
	
	/** Função GET do atributo listaPeriodosTurma que também efetua o preenchimento dessa entidate caso esteja vazia
	 *  
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public Collection<PeriodoAvaliacao> getListaPeriodosTurma() throws Exception {
		if(this.listaPeriodosTurma.isEmpty()){
			findDiscentesPeriodosTurma();
		}
		return listaPeriodosTurma;
	}
 
	public void setListaPeriodosTurma(
			Collection<PeriodoAvaliacao> listaPeriodosTurma) {
		this.listaPeriodosTurma = listaPeriodosTurma;
	}
	
	public List<List<AcompanhamentoSemanalDiscente>> getTabelaAcompanhamento() {
		return tabelaAcompanhamento;
	}

	public void setTabelaAcompanhamento(
			List<List<AcompanhamentoSemanalDiscente>> tabelaAcompanhamento) {
		this.tabelaAcompanhamento = tabelaAcompanhamento;
	}

	public String getRetornoPVMooodle(){
		return retornoPVMooodle;
	}
	
	public void setRetornoPVMooodle(String retornoPVMooodle) {
		this.retornoPVMooodle = retornoPVMooodle;
	}
	
	
	/** 
	 * Função que é acionada para carregar a integração das notas PV do moodle
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/principal.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public String preCarregarIntegracaoPvMoodle() throws Exception{
		//carregarIntegracaoPvMoodle();
		//preencheTabelaAcompanhamento();
		return forward("/metropole_digital/lancar_notas_semanais/form.jsp");
	}
	
	
	/** 
	 * Função que carrega a integração das notas PV do moodle
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public void carregarIntegracaoPvMoodle() throws Exception{
		AcompanhamentoSemanalDiscenteDao acompanhamentoDao = new AcompanhamentoSemanalDiscenteDao();
		
		try {
			if(this.listaAcompanhamentosGeral.isEmpty()){
				this.listaAcompanhamentosGeral = (ArrayList) acompanhamentoDao.findAcompanhamentosByTurmaEntradaProjetado(turmaEntradaSelecionada.getId());
			}
	
			for(PeriodoAvaliacao periodo: listaPeriodosTurma){
				codigosIntegracaoPeriodos += "@" + periodo.getCodigoIntegracao();
			}
			
			importarPVMoodle();
			
			if(retornoPVMooodle == null || retornoPVMooodle.equalsIgnoreCase("")){
				addMessage("As informações entre SIGAA/Moodle não estão sincronizadas. Favor verificar se os dados de integração estão sincronizados corretamente.", TipoMensagemUFRN.ERROR);
			} else {
			
				if(retornoPVMooodle.length() > 0) {
				
					// Adicionando ao String de retorno o caracter delimitador do fim do texto
					retornoPVMooodle += "^";
					
					int contadorNotasSincronizadas = 0;
					
					List<RetornoNotaPVIntegracaoMoodle> listaRetorno = new ArrayList<RetornoNotaPVIntegracaoMoodle>();
					listaRetorno = lerRegistrosRetornoNotaPVIntegracaoMoodle(retornoPVMooodle);
					
					if(listaRetorno.isEmpty()) {
						addMessage("Ocorreu um problema no retorno das informações por parte do Moodle. As notas não foram sincronizadas.", TipoMensagemUFRN.ERROR);
					} else {
						
						for(AcompanhamentoSemanalDiscente acomp : this.listaAcompanhamentosGeral) {
							//if(!acomp.isPvSincronizada()) {
								for(RetornoNotaPVIntegracaoMoodle registroRetorno: listaRetorno){
									if( (acomp.getDiscente().getMatricula() == Long.parseLong(registroRetorno.getMatriculaDiscente())) && (acomp.getPeriodoAvaliacao().getCodigoIntegracao().equalsIgnoreCase(registroRetorno.getCodigoIntegracaoPeriodo())) ){
										acomp.setParticipacaoVirtual(registroRetorno.getNotaPv());
										//acomp.setPvSincronizada(true);
										acompanhamentoDao.createOrUpdate(acomp);
										contadorNotasSincronizadas++;
										break;
									}
								}
							//}
						}
						if(contadorNotasSincronizadas > 0) {
							addMessage("Sincronização efetuada com sucesso. " + contadorNotasSincronizadas + " notas foram sincronizadas.", TipoMensagemUFRN.INFORMATION);
						} else {
							addMessage("Nenhuma nota foi sincronizada. ", TipoMensagemUFRN.INFORMATION);
						}
					}
				
					
				} else {
					addMessage("As informações entre SIGAA/Moodle não estão sincronizadas. Favor verificar se os dados de integração estão sincronizados corretamente.", TipoMensagemUFRN.ERROR);			
				}
			}
		}
		catch (Exception e) {
			addMessage("As informações entre SIGAA/Moodle não estão sincronizadas. Favor verificar se os dados de integração estão sincronizados corretamente.", TipoMensagemUFRN.ERROR);
		} finally {
			acompanhamentoDao.close();
		}
		
	}

	
	/** 
	 * Função reponsável por disparar a requisição de conexão HTTP a uma página externa.
	 * Nessa função serão criados e informados os parâmetros e a URL a ser requisitada
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_semanais/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public void importarPVMoodle () throws Exception{
		try {
			// a string da url  
			String urlString = "http://moodle.imd.ufrn.br/blocks/genome/notas_pv_sigaa.php";  
			  
			// os parametros a serem enviados  
			Properties parameters = new Properties();  
			parameters.setProperty("curso", turmaEntradaSelecionada.getDadosTurmaIMD().getCodigoIntegracao());  
			//parameters.setProperty("periodo", codigosIntegracaoPeriodos);  
			
			//parameters.setProperty("curso", "T99756537");
			 
			this.retornoPVMooodle += conexaoHttp(urlString, parameters);
			
		}
		catch(Exception e) {
			this.retornoPVMooodle = "";
		}
	}
	
	
	/** 
	 * Função responsável por realizar a requisição de conexão HTTP a uma página externa
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param urlString: URL da página externa a ser requisitada; parameters: Parâmetros passados na requisição via método GET
	 * @throws Exception 
	 * @return String de retorno da integração com Moodle
	 */
	public String conexaoHttp(String urlString, Properties parameters) throws IOException{
		try {
  
			// o iterador, para criar a URL  
			Iterator i = parameters.keySet().iterator();  
			// o contador  
			int counter = 0;  
			  
			// enquanto ainda existir parametros  
			while (i.hasNext()) {  
			  
			    // pega o nome  
			    String name = (String) i.next();  
			    // pega o valor  
			    String value = parameters.getProperty(name);  
			  
			    // adiciona com um conector (? ou &)  
			    // o primeiro é ?, depois são &  
			    urlString += (++counter == 1 ? "?" : "&")  
			        + name  
			        + "="  
			        + value;  
			  
			}  
			
			// cria o objeto url  
			URL url = new URL(urlString); 
			
			// cria o objeto httpurlconnection  
			HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
			
			// seta o metodo  
			connection.setRequestProperty("Request-Method", "GET"); 
			
			
			// seta a variavel para ler o resultado  
			connection.setDoInput(true);  
			connection.setDoOutput(false);  
			
			
			// conecta com a url destino  
			connection.connect();  
			
			// abre a conexão pra input  
			BufferedReader br =   new BufferedReader(new InputStreamReader(connection.getInputStream())); 
			
			
			// le ate o final  
			StringBuffer newData = new StringBuffer(10000);  
			String s = "";  
			while (null != ((s = br.readLine()))) {  
			    newData.append(s);  
			}  
			br.close();
			
			
			// imprime o codigo resultante  
			return new String(newData);
		}
		catch(Exception e) {
			return "";
		}
		
		
	}
	
	
	/** 
	 * Função responsável pelo tratamento da String de retorno da integração com o Moodle,
	 * Essa função irá separar os campos e seus determinados valores
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_notas_disciplina/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @throws Exception 
	 * @return Coleção de objetos do tipo RetornoNotaPVIntegracaoMoodle que representarão as informações da integração já organizadas
	 */
	public ArrayList<RetornoNotaPVIntegracaoMoodle> lerRegistrosRetornoNotaPVIntegracaoMoodle(String retorno){
		
		ArrayList<RetornoNotaPVIntegracaoMoodle> itens = new ArrayList<RetornoNotaPVIntegracaoMoodle>();
		
		char caracterLido = retorno.charAt(0);
		String valor = "";
		
		String matricula = "";
		String codigoIntegracaoPeriodo = "";
		Double notaPv = 0.0;
		
		
		int contadorCampos = 0;
		
		for (int i = 0; caracterLido != '^'; i++) {
			
			caracterLido = retorno.charAt(i);
			
			if(caracterLido != '#'){
				if(caracterLido != '@'){
					valor += (char) caracterLido;
				} else {
					
					if(contadorCampos > 0){
						 if(contadorCampos == 1){
	 						matricula = valor;
	 					} else if(contadorCampos == 2){
							codigoIntegracaoPeriodo = valor;
	 					} else if(contadorCampos == 3){
	 						if(valor == "") {
	 							notaPv = null;
	 						}
	 						else {
	 							notaPv = Double.parseDouble(valor);
	 						}
	 					} 
						
					}
					valor = "";
					contadorCampos++;
				}
				
			} else {
				if(contadorCampos == 3){
					if(valor == "") {
						notaPv = null;
					}
					else {
						notaPv = Double.parseDouble(valor);
					}
				}
				
				if (valor != "") {
	    			RetornoNotaPVIntegracaoMoodle objRetorno = new RetornoNotaPVIntegracaoMoodle(matricula, codigoIntegracaoPeriodo, notaPv);
	    			itens.add(objRetorno);
				}
				
				valor = "";
				contadorCampos = 0;
				
			}
			
		}
		if(contadorCampos == 3){
			if(valor == "") {
				notaPv = null;
			}
			else {
				valor = valor.substring(0, valor.length()-1);
				notaPv = Double.parseDouble(valor);
			}
		}
		
		if (valor != "") {
			RetornoNotaPVIntegracaoMoodle objRetorno = new RetornoNotaPVIntegracaoMoodle(matricula, codigoIntegracaoPeriodo, notaPv);
			itens.add(objRetorno);
		}
		
		valor = "";
		contadorCampos = 0;
		
		return itens;
	}


	public Date getDataAtual() {
		return dataAtual;
	}

	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}
}
