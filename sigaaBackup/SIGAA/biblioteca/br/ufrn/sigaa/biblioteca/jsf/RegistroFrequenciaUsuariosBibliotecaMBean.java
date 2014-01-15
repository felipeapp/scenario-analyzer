/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/11/2008
 *
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FrequenciaUsuariosBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroFrequenciaUsuariosBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

import static br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroFrequenciaUsuariosBiblioteca.TURNO_MATUTINO;
import static br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroFrequenciaUsuariosBiblioteca.TURNO_VESPERTINO;
import static br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroFrequenciaUsuariosBiblioteca.TURNO_NOTURNO;

/**
 * MBean responsável pelo cadastramento da frequência de usuários na biblioteca.
 * 
 * Obs: Esse MBean foi usado também no relatório que consultas as frequências dos usuário. Separar em dois MBean diferentes 
 * porque são dois casos de uso distintos. Porém, essa parte do código deixou de ser usada. Está aqui só no caso de ser necessária
 * de novo.
 * 
 * @author Agostinho
 */
@Component("registroFrequenciaUsuariosBibliotecaMBean")
@Scope("request")
public class RegistroFrequenciaUsuariosBibliotecaMBean extends SigaaAbstractController<RegistroFrequenciaUsuariosBiblioteca> {

	/* Constantes que devem ser usadas nos relatórios para se referenciar à bibliotecas especiais */
	public static final int TODAS_BIBLIOTECAS = -1;
	public static final int TODAS_BIBLIOTECAS_SETORIAIS = -2;

	
	/************************* Dados para montar o relatório **************************/
	private ArrayList<RegistroFrequenciaUsuariosBiblioteca> listaMesesNoturno = new ArrayList<RegistroFrequenciaUsuariosBiblioteca>();
	private ArrayList<RegistroFrequenciaUsuariosBiblioteca> listaMesesMatutino = new ArrayList<RegistroFrequenciaUsuariosBiblioteca>();
	private ArrayList<RegistroFrequenciaUsuariosBiblioteca> listaMesesVespertino = new ArrayList<RegistroFrequenciaUsuariosBiblioteca>();
	
	int somaJaneiroCol, somaFevereiroCol, somaMarcoCol, somaAbrilCol, somaMaioCol, 
	somaJunhoCol, somaJulhoCol, somaAgostoCol, somaSetembroCol, somaOutubroCol, 
	somaNovembroCol, somaDezembroCol;
	
	int somaJaneiroLin, somaFevereiroLin, somaMarcoLin, somaAbrilLin, somaMaioLin, 
	somaJunhoLin, somaJulhoLin, somaAgostoLin, somaSetembroLin, somaOutubroLin, 
	somaNovembroLin, somaDezembroLin;
	
	private HtmlDataTable dataTable;
	private int turnoSelecionado = TURNO_MATUTINO;
	private String mesInicio, ano;
	private int totalTodosMesesMatutino;
	private int totalTodosMesesVespertino;
	private int totalTodosMesesNoturno;
	private int totalLinhasEColunas;
	private int idBiblioteca;
	
	
	private Biblioteca biblioteca;
	/***************************************************************************/
	
	
	
	
	
	/** Guarda a lista de frequência retornada */
	private ArrayList<RegistroFrequenciaUsuariosBiblioteca> listaFrequenciaUsuariosBib = new ArrayList<RegistroFrequenciaUsuariosBiblioteca>();
	
	/** Guarda a biblioteca selecionada pelo usuário para fazer as pesquisas das frequências */
	private Biblioteca bibliotecaPesquisa = new Biblioteca(-1);
	
	
	/**
	 * Construtor padrão.
	 */
	public RegistroFrequenciaUsuariosBibliotecaMBean(){
		obj = new RegistroFrequenciaUsuariosBiblioteca();
		obj.setDataCadastro(new Date());
	}
	
	
	
	/**
	 *    Inicia o caso de uso de registrar a movimentação dos usuários das bibliotecas.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/menu/circulacao.jsp
	 */
	public String iniciarRegistroFrequenciaUsuario() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
		
		obj = new RegistroFrequenciaUsuariosBiblioteca();
		
		if(StringUtils.isEmpty(ano))
			ano = String.valueOf( CalendarUtils.getAnoAtual() );
		
		if(StringUtils.isEmpty(mesInicio))
		mesInicio = String.valueOf( CalendarUtils.getMesAtual()+1 );
		
		//clear();
		listaFrequenciaUsuariosBib.clear();
		
		return forward("/biblioteca/" + obj.getClass().getSimpleName() + "/form_abertura.jsp");
	}
	
	
	
	
	/**
	 * Consulta os registro de movimentação do usuário salvos. 
	 *
	 * <br/>
	 * Chamado a partir da página:  /biblioteca/RegistroFrequenciausuariosBiblioteca/form_abertura.jsp
	 */
	public String consultar() throws DAOException, ParseException {

		//clear();
		
		boolean valido = true;
		
		if (mesInicio == null || mesInicio.equals("") || mesInicio.length() < 1 || (Integer.parseInt(mesInicio) > 12 || Integer.parseInt(mesInicio) < 1) ) {
			valido = false;
			addMensagemErro("O campo Mês é inválido.");
		}
		if (ano == null || ano.equals("") || ano.length() < 4) {
			valido = false;
			addMensagemErro("É necessário informar o campo Ano com no mínimo 4 digitos, por exemplo: 2006.");
		}
		
		if (bibliotecaPesquisa == null || bibliotecaPesquisa.getId() <= 0 ) {
			valido = false;
			addMensagemErro("É necessário informar a biblioteca da movimentação do usuário.");
		}
		
		if (valido) {
			listarRegistrosFrequenciaUsuariosBiblioteca();
			
			obj.getBiblioteca().setId( bibliotecaPesquisa.getId()); // Para começar marcada a biblioteca que foi pesquisada.
			
			return forward("/biblioteca/" + obj.getClass().getSimpleName() + "/form_cadastro.jsp");
		}
		
		return forward("/biblioteca/" + obj.getClass().getSimpleName() + "/form_abertura.jsp");
	}
	
	
	
	/**
	 *   Busca todos os registro de frequência do usuário para o mês e ano digitados pelo usuário.
	 */
	private void listarRegistrosFrequenciaUsuariosBiblioteca() throws DAOException, NumberFormatException, ParseException {
		FrequenciaUsuariosBibliotecaDao dao = getDAO(FrequenciaUsuariosBibliotecaDao.class);
		
		listaFrequenciaUsuariosBib = (ArrayList<RegistroFrequenciaUsuariosBiblioteca>) 
					dao.findFrequenciasMovimentacaoUsuarioAtivasByAnoMesEBiblioteca(Integer.parseInt(mesInicio), Integer.parseInt(ano), bibliotecaPesquisa.getId());
	}
	
	/**
	 *  Adiciona um novo registro de frequência de usuário.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/RegistroFrequenciausuariosBiblioteca/form_abertura.jsp/</li>
	 *   </ul>
	 *
	 *   Método não chamado por nenhuma página jsp.
	 */
	public String adicionarItemCadastro() throws ArqException, HibernateException, NumberFormatException, ParseException {
		
		if ( validarEntradaPorAjax() ) {
			
			obj.setTurno(turnoSelecionado);

			SimpleDateFormat sp = new SimpleDateFormat();
			sp.applyPattern("dd-MM-yyyy");
			String data = sp.format(obj.getDataCadastro());
				
			String mes = data.substring(3,5);
			String ano = data.substring(6,10);
			
			obj.setAno(Integer.parseInt(ano));
			obj.setMes(Integer.parseInt(mes));
			
			listaFrequenciaUsuariosBib.add(obj);
			
			try{
			
				if ( listaFrequenciaUsuariosBib.size() > 0 ) {
	
					prepareMovimento(SigaaListaComando.CADASTRAR_MOVIMENTO_DIARIO_USUARIOS);
					
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_MOVIMENTO_DIARIO_USUARIOS);
					mov.setColObjMovimentado(listaFrequenciaUsuariosBib);
				
					execute(mov);
					listarRegistrosFrequenciaUsuariosBiblioteca();				
					//clear();
					addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				}else {
					addMensagemErro("É necessário adicionar ao menos um registro para realizar o cadastro!");
					return null;
				}
			
				int idBibliotecaRealizouRegistro = obj.getBiblioteca().getId();
				
				obj = new RegistroFrequenciaUsuariosBiblioteca();
				obj.setBiblioteca(new Biblioteca(idBibliotecaRealizouRegistro)); // permanece na mesma biblioteca que o usuário acabou de registar
			
				bibliotecaPesquisa = new Biblioteca(idBibliotecaRealizouRegistro); // muda os resultados da pesquisa para a biblioteca que acabou de registrar
				
				return consultar();
			
			}catch(NegocioException ne){
				addMensagens(ne.getListaMensagens());
				return null;
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * Remove um registro de movimentação.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/RegistroFrequenciaUsuarioBiblioteca/form_cadastro.jsp
	 */
	public String removerItem() throws ArqException, NegocioException, ParseException {
		
		int idRegistroRemocao = getParameterInt("idRegistroFrequenciaRemocao", 0);
		
		RegistroFrequenciaUsuariosBiblioteca reg = new RegistroFrequenciaUsuariosBiblioteca(idRegistroRemocao);
		
		reg = getGenericDAO().refresh(reg);
		
		reg.setAtivo(false);
		
		prepareMovimento(ArqListaComando.ALTERAR);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(ArqListaComando.ALTERAR);
		mov.setObjMovimentado(reg);
			
		execute(mov);

		listaFrequenciaUsuariosBib.remove(reg);
		addMensagemInformation("Registro removido com sucesso!");
		return consultar();
	}
	
	
	
	/**
	 * Se o usuário for o administrador geral das bibliotecas,
	 * retorna todas as bibliotecas ativas, ordenadas pela descrição. Senão retorna a biblioteca 
	 * onde tem papel de circulação.
	 * <br><br>
	 * Usado na página /biblioteca/RegistroFrequenciaUsuarioBiblioteca/form_cadastro.jsp
	 */
	public Collection<SelectItem> getBibliotecasAtivasRegistroFrenquencia() throws DAOException, SegurancaException{
		
		Collection<Biblioteca> bibliotecasRegistroFrequencia = new ArrayList<Biblioteca>();
		
		BibliotecaDao dao = null;
		
		try{
		
			
				dao = getDAO(BibliotecaDao.class);
				
				if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
					bibliotecasRegistroFrequencia = dao.findAllBibliotecasInternasAtivas(); 
			
				}else{
					
					if(isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO)){
						List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
								getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
					
						bibliotecasRegistroFrequencia = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
					}else{
						if(isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO)){
							List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
									getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
							bibliotecasRegistroFrequencia = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
						}
					}
					
				}
			
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return toSelectItems(bibliotecasRegistroFrequencia, "id", "descricaoCompleta");
	}
	
	
	
	
	
	/**
	 * Verifica se já existe lançamentos de frequência para o mesmo dia e turno.
	 */
	private boolean validarEntradaPorAjax() throws DAOException, ParseException {
		
			boolean valido = true;
			
			ListaMensagens listaErro = new ListaMensagens();

			if (obj.getQuantAcesso() == null || obj.getQuantAcesso() <= 0) {
				valido = false;
				listaErro.addErro("É necessário informar a quantidade de usuários.");
			}
			if (obj.getDataCadastro() == null ) {
				valido = false;
				listaErro.addErro("É necessário informar o campo data!");
			}
			
			if (obj.getBiblioteca() == null || obj.getBiblioteca().getId() <= 0 ) {
				valido = false;
				listaErro.addErro("É necessário informar a biblioteca da movimentação do usuário.");
			}
			
			//boolean diaRepetido = false;
			
			Collection<RegistroFrequenciaUsuariosBiblioteca> lista = getDAO(FrequenciaUsuariosBibliotecaDao.class)
					.findFrequenciasMovimentacaoUsuarioAtivasByTurnoDataEBiblioteca(turnoSelecionado, obj.getDataCadastro(), obj.getBiblioteca().getId());
			
			if(lista.size() > 0){
				listaErro.addErro("Você já adicionou um registro para esse dia/turno.");
				valido = false;
			}
			
			addMensagens(listaErro);
			
		return valido;
	}

	
	
	/**
	 * Inicia o relatório Anual de frequência dos usuários da biblioteca.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/biblioteca/menus/controle_estatistico.jsp
	 */
	public String iniciarRelatorioAnual(){
		return forward("/biblioteca/" + obj.getClass().getSimpleName() + "/form_relatorio_freq_usuario.jsp");
	}
	
	
	
	/**
	 * Responsável pela geração do relatório Anual de frequência os usuários que frequentaram a
	 * biblioteca.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/biblioteca/RegistroFrequenciaUsuariosBiblioteca/form_relatorio_freq_usuario.jsp
	 */
	public String gerarRelatorioAnual() throws DAOException {
		
		limparValoresLinhasTurnos();
		limparValoresColunasMeses();
		
		totalTodosMesesVespertino = 0;
		totalTodosMesesMatutino = 0;
		totalTodosMesesVespertino = 0;
		
		FrequenciaUsuariosBibliotecaDao fDao = getDAO(FrequenciaUsuariosBibliotecaDao.class);
		
		listaMesesMatutino = fDao.findAllFrequenciaAnual(Integer.parseInt(ano), TURNO_MATUTINO, idBiblioteca);
		listaMesesVespertino = fDao.findAllFrequenciaAnual(Integer.parseInt(ano), TURNO_VESPERTINO, idBiblioteca);
		listaMesesNoturno = fDao.findAllFrequenciaAnual(Integer.parseInt(ano), TURNO_NOTURNO, idBiblioteca);
		
		//calculando Total para as colunas somatórias (exibidas na parte de baixo do relatório) de Janeiro-Dezembro
		calcularTotalColunasMeses(listaMesesMatutino);
		calcularTotalColunasMeses(listaMesesVespertino);
		calcularTotalColunasMeses(listaMesesNoturno);

		//calculando Total para as linhas Matutino, Vespertino e Noturno
		calcularTotalLinhasTurnos(listaMesesMatutino, TURNO_MATUTINO);
		totalTodosMesesMatutino = calcularTotalTodosMesesTurno();
		limparValoresLinhasTurnos();
		
		calcularTotalLinhasTurnos(listaMesesVespertino, TURNO_VESPERTINO);
		totalTodosMesesVespertino = calcularTotalTodosMesesTurno();
		limparValoresLinhasTurnos();
		
		calcularTotalLinhasTurnos(listaMesesNoturno, TURNO_NOTURNO);
		totalTodosMesesNoturno = calcularTotalTodosMesesTurno();

		biblioteca = fDao.findByPrimaryKey(idBiblioteca, Biblioteca.class);
		
		calcularTotaLinhasColunas();
		
		return forward("/biblioteca/" + obj.getClass().getSimpleName() + "/relatorio_freq_usuario.jsp");
	}

	
	
	/**
	 * Soma as colunas de Janeiro-Dezembro e também 
	 * as 3 linhas dos turnos (manha, tarde, noite).
	 * 
	 * Exibe o total anual no canto inferior direito no relatório.
	 *  
	 */
	private void calcularTotaLinhasColunas() {
		
		totalLinhasEColunas = (somaJaneiroCol + somaFevereiroCol + somaMarcoCol + somaAbrilCol + 
							somaMaioCol + somaJunhoCol + somaJulhoCol + somaAgostoCol + 
							somaSetembroCol + somaOutubroCol + somaNovembroCol + somaDezembroCol 
							+ totalTodosMesesMatutino + totalTodosMesesVespertino + totalTodosMesesNoturno);
			
	}

	/**
	 * Apaga os valores dos meses
	 *
	 */
	private void limparValoresColunasMeses() {
		somaJaneiroCol = 0; 
		somaFevereiroCol = 0; 
		somaMarcoCol = 0; 
		somaAbrilCol = 0; 
		somaMaioCol = 0; 
		somaJunhoCol = 0; 
		somaJulhoCol = 0; 
		somaAgostoCol = 0; 
		somaSetembroCol = 0; 
		somaOutubroCol = 0; 
		somaNovembroCol = 0; 
		somaDezembroCol = 0;
	}
	
	/**
	 *  Apaga os valores dos turnos
	 */
	private void limparValoresLinhasTurnos() {
		somaJaneiroLin = 0; 
		somaFevereiroLin = 0; 
		somaMarcoLin = 0; 
		somaAbrilLin = 0; 
		somaMaioLin = 0; 
		somaJunhoLin = 0; 
		somaJulhoLin = 0; 
		somaAgostoLin = 0; 
		somaSetembroLin = 0; 
		somaOutubroLin = 0; 
		somaNovembroLin = 0; 
		somaDezembroLin = 0;
	}

	
	/**
	 * Calcula de Janeiro-Dezembro, somando o mês atual com o próximo
	 * Isso aqui é chamado 3 vezes, uma vez para cada turno.
	 * 
	 * Exibe os totais do lado direito do relatório.
	 * 
	 * @return
	 */
	private int calcularTotalTodosMesesTurno() {
		int total = (somaJaneiroLin + somaFevereiroLin + somaMarcoLin + somaAbrilLin + 
		somaMaioLin + somaJunhoLin + somaJulhoLin + somaAgostoLin + 
		somaSetembroLin + somaOutubroLin + somaNovembroLin + somaDezembroLin); 
		return total;
	}

	/**
	 * Calcula o somatório Total para cada mês, somando verticalmente
	 * cada coluna de cada mês (os valores que tem nos turnos)
	 * 
	 * É chamado 3 vezes, uma para cada turno.
	 * 
	 * Exibe os totais da parte de baixo do relatório.
	 */
	private void calcularTotalColunasMeses(ArrayList<RegistroFrequenciaUsuariosBiblioteca> lista) {
		
		for (RegistroFrequenciaUsuariosBiblioteca obj : lista) {
			
			if (obj.getMes() == 1)
				somaJaneiroCol += obj.getTotalAcessoJaneiro();
			
			if (obj.getMes() == 2)
				somaFevereiroCol += obj.getTotalAcessoFevereiro();
			
			if (obj.getMes() == 3)
				somaMarcoCol += obj.getTotalAcessoMarco();
			
			if (obj.getMes() == 4)
				somaAbrilCol += obj.getTotalAcessoAbril();
			
			if (obj.getMes() == 5)
				somaMaioCol += obj.getTotalAcessoMaio();
			
			if (obj.getMes() == 6)
				somaJunhoCol += obj.getTotalAcessoJunho();
			
			if (obj.getMes() == 7)
				somaJulhoCol += obj.getTotalAcessoJulho();
			
			if (obj.getMes() == 8)
				somaAgostoCol += obj.getTotalAcessoAgosto();
			
			if (obj.getMes() == 9)
				somaSetembroCol += obj.getTotalAcessoSetembro();

			if (obj.getMes() == 10)
				somaOutubroCol += obj.getTotalAcessoOutubro();
			
			if (obj.getMes() == 11)
				somaNovembroCol += obj.getTotalAcessoNovembro();
			
			if (obj.getMes() == 12)
				somaDezembroCol += obj.getTotalAcessoDezembro();
		}
		
	}
	
	/**
	 * Calcula o somatório Total para cada mês de acordo com o Turno 
	 * atual (somando horizontalmente) para cada mês/turno. 
	 *
	 * É chamado 3 vezes, uma para cada turno.
	 */
	private void calcularTotalLinhasTurnos(ArrayList<RegistroFrequenciaUsuariosBiblioteca> lista, int turno) {
		
		for (RegistroFrequenciaUsuariosBiblioteca obj : lista) {
			
			if (obj.getMes() == 1 && obj.getTurno() == turno) { 
				somaJaneiroLin += obj.getTotalAcessoJaneiro();
			}
			
			if (obj.getMes() == 2 && obj.getTurno() == turno) {
				somaFevereiroLin += obj.getTotalAcessoFevereiro();
			}
			
			if (obj.getMes() == 3 && obj.getTurno() == turno) {
				somaMarcoLin += obj.getTotalAcessoMarco();
			}
			
			if (obj.getMes() == 4 && obj.getTurno() == turno) {
				somaAbrilLin += obj.getTotalAcessoAbril();
			}
			
			if (obj.getMes() == 5 && obj.getTurno() == turno) {
				somaMaioLin += obj.getTotalAcessoMaio();
			}
			
			if (obj.getMes() == 6 && obj.getTurno() == turno) {
				somaJunhoLin += obj.getTotalAcessoJunho();
			}
			
			if (obj.getMes() == 7 && obj.getTurno() == turno) {
				somaJulhoLin += obj.getTotalAcessoJulho();
			}
			
			if (obj.getMes() == 8 && obj.getTurno() == turno) {
				somaAgostoLin += obj.getTotalAcessoAgosto();
			}
			
			if (obj.getMes() == 9 && obj.getTurno() == turno) {
				somaSetembroLin += obj.getTotalAcessoSetembro();
			}
			
			if (obj.getMes() == 10 && obj.getTurno() == turno) {
				somaOutubroLin += obj.getTotalAcessoOutubro();
			}
			
			if (obj.getMes() == 11 && obj.getTurno() == turno) {
				somaNovembroLin += obj.getTotalAcessoNovembro();
			}
			
			if (obj.getMes() == 12 && obj.getTurno() == turno) {
				somaDezembroLin += obj.getTotalAcessoDezembro();
			}
		}
		
	}

	
	/**
	 * Retorna o valor da constate que representa todas as biblioteca
	 */
	public int getConstanteTodasBibliotecas() {
		return TODAS_BIBLIOTECAS;
	}

	/**
	 * Retorna o valor da constate que representa todas as biblioteca setoriais
	 */
	public int getConstanteTodasBibliotecasSetoriais() {
		return TODAS_BIBLIOTECAS_SETORIAIS;
	}
	
	
	
	@Override
	public String cancelar() {
		return forward("/biblioteca/index.html");
	}

	public ArrayList<RegistroFrequenciaUsuariosBiblioteca> getListaFrequenciaUsuariosBib() {
		return listaFrequenciaUsuariosBib;
	}

	public void setListaFrequenciaUsuariosBib(
			ArrayList<RegistroFrequenciaUsuariosBiblioteca> listaFrequenciaUsuariosBib) {
		this.listaFrequenciaUsuariosBib = listaFrequenciaUsuariosBib;
	}

	public ArrayList<RegistroFrequenciaUsuariosBiblioteca> getListaMesesNoturno() {
		return listaMesesNoturno;
	}

	public void setListaMesesNoturno(
			ArrayList<RegistroFrequenciaUsuariosBiblioteca> listaMesesNoturno) {
		this.listaMesesNoturno = listaMesesNoturno;
	}

	public ArrayList<RegistroFrequenciaUsuariosBiblioteca> getListaMesesMatutino() {
		return listaMesesMatutino;
	}

	public void setListaMesesMatutino(
			ArrayList<RegistroFrequenciaUsuariosBiblioteca> listaMesesMatutino) {
		this.listaMesesMatutino = listaMesesMatutino;
	}

	public ArrayList<RegistroFrequenciaUsuariosBiblioteca> getListaMesesVespertino() {
		return listaMesesVespertino;
	}

	public void setListaMesesVespertino(
			ArrayList<RegistroFrequenciaUsuariosBiblioteca> listaMesesVespertino) {
		this.listaMesesVespertino = listaMesesVespertino;
	}

	public int getSomaJaneiroCol() {
		return somaJaneiroCol;
	}

	public void setSomaJaneiroCol(int somaJaneiroCol) {
		this.somaJaneiroCol = somaJaneiroCol;
	}

	public int getSomaFevereiroCol() {
		return somaFevereiroCol;
	}

	public void setSomaFevereiroCol(int somaFevereiroCol) {
		this.somaFevereiroCol = somaFevereiroCol;
	}

	public int getSomaMarcoCol() {
		return somaMarcoCol;
	}

	public void setSomaMarcoCol(int somaMarcoCol) {
		this.somaMarcoCol = somaMarcoCol;
	}

	public int getSomaAbrilCol() {
		return somaAbrilCol;
	}

	public void setSomaAbrilCol(int somaAbrilCol) {
		this.somaAbrilCol = somaAbrilCol;
	}

	public int getSomaMaioCol() {
		return somaMaioCol;
	}

	public void setSomaMaioCol(int somaMaioCol) {
		this.somaMaioCol = somaMaioCol;
	}

	public int getSomaJunhoCol() {
		return somaJunhoCol;
	}

	public void setSomaJunhoCol(int somaJunhoCol) {
		this.somaJunhoCol = somaJunhoCol;
	}

	public int getSomaJulhoCol() {
		return somaJulhoCol;
	}

	public void setSomaJulhoCol(int somaJulhoCol) {
		this.somaJulhoCol = somaJulhoCol;
	}

	public int getSomaAgostoCol() {
		return somaAgostoCol;
	}

	public void setSomaAgostoCol(int somaAgostoCol) {
		this.somaAgostoCol = somaAgostoCol;
	}

	public int getSomaSetembroCol() {
		return somaSetembroCol;
	}

	public void setSomaSetembroCol(int somaSetembroCol) {
		this.somaSetembroCol = somaSetembroCol;
	}

	public int getSomaOutubroCol() {
		return somaOutubroCol;
	}

	public void setSomaOutubroCol(int somaOutubroCol) {
		this.somaOutubroCol = somaOutubroCol;
	}

	public int getSomaNovembroCol() {
		return somaNovembroCol;
	}

	public void setSomaNovembroCol(int somaNovembroCol) {
		this.somaNovembroCol = somaNovembroCol;
	}

	public int getSomaDezembroCol() {
		return somaDezembroCol;
	}

	public void setSomaDezembroCol(int somaDezembroCol) {
		this.somaDezembroCol = somaDezembroCol;
	}

	public HtmlDataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(HtmlDataTable dataTable) {
		this.dataTable = dataTable;
	}

	public int getTurnoSelecionado() {
		return turnoSelecionado;
	}

	public void setTurnoSelecionado(int turnoSelecionado) {
		this.turnoSelecionado = turnoSelecionado;
	}

	public String getMesInicio() {
		return mesInicio;
	}

	public void setMesInicio(String mesInicio) {
		this.mesInicio = mesInicio;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public int getTotalTodosMesesMatutino() {
		return totalTodosMesesMatutino;
	}

	public int getTotalTodosMesesVespertino() {
		return totalTodosMesesVespertino;
	}

	public int getTotalTodosMesesNoturno() {
		return totalTodosMesesNoturno;
	}

	public int getTotalLinhasEColunas() {
		return totalLinhasEColunas;
	}

	public void setTotalLinhasEColunas(int totalLinhasEColunas) {
		this.totalLinhasEColunas = totalLinhasEColunas;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public int getIdBiblioteca() {
		return idBiblioteca;
	}

	public void setIdBiblioteca(int idBiblioteca) {
		this.idBiblioteca = idBiblioteca;
	}

	public Biblioteca getBibliotecaPesquisa() {
		return bibliotecaPesquisa;
	}

	public void setBibliotecaPesquisa(Biblioteca bibliotecaPesquisa) {
		this.bibliotecaPesquisa = bibliotecaPesquisa;
	}
	
	public int getTURNO_MATUTINO() { return TURNO_MATUTINO; }
	public int getTURNO_VESPERTINO() { return TURNO_VESPERTINO; }
	public int getTURNO_NOTURNO() { return TURNO_NOTURNO; }
	
	public int getTODAS_BIBLIOTECAS() { return TODAS_BIBLIOTECAS; }
	public int getTODAS_BIBLIOTECAS_SETORIAIS() { return TODAS_BIBLIOTECAS_SETORIAIS; }
	
}