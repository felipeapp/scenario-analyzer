/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/06/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ProrrogacaoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PunicaoAtrasoEmprestimoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoComunicarMaterialPerdido;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoDevolveEmprestimoMaterialPerdido;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.EmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean respons�vel por gerenciar materiais emprestados, que foram perdidos pelos usu�rios.
 * 
 * @author Fred_Castro
 */

@Component("comunicarMaterialPerdidoMBean")
@Scope("request")
public class ComunicarMaterialPerdidoMBean extends SigaaAbstractController <UsuarioBiblioteca> implements PesquisarUsuarioBiblioteca{
	
	/**
	 * A p�gina que lista os empr�stimos ativos do usu�rio para comunicar o material que foi perdido.
	 */
	public static final String PAGINA_LISTA_COMUNICAR_MATERIAL_PERDIDO = "/biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp";
	
	/**
	 * A p�gina para comunicar um material perdido.
	 */
	public static final String PAGINA_FORM_COMUNICAR_MATERIAL_PERDIDO = "/biblioteca/circulacao/formComunicarMaterialPerdido.jsp";
	
	/**
	 * O comprovante que o usu�rio comunicou o material perdido
	 */
	public static final String PAGINA_COMPROVANTE_COMUNICACAO_MATERIAL_PERDIDO = "/biblioteca/circulacao/comprovanteComunicacaoMaterialPerdido.jsp";
	
	/**
	 * A p�gina que lista as comunica��es de materiais perdidos ativas no sistema.
	 */
	public static final String PAGINA_LISTA_MATERIAIS_PERDIDOS = "/biblioteca/circulacao/listaComunicacoesMateriaisPerdidosAtivasNoSistema.jsp";

	/**
	 * A p�gina pela qual o usu�rio deve devolver os materiais que est�o perdidos do sistema.
	 */
	public static final String PAGINA_DEVOLVE_EMPRESTIMO_MATERIL_PERDIDO = "/biblioteca/circulacao/paginaDevolveEmprestimoMaterialPerdido.jsp";
	
	/** Guarda se o usu�rio devolveu ou n�o o material */
	private short valorTipoDevolucao = DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_SIMILAR.getValor();

	/** S� informado quando o usu�rio n�o entregar uma material para substituir o anterior */
	private String motivoNaoEntregaMaterial;
	
	
	/** Os empr�stimos ativos do usu�rio selecionado. */
	private List <Emprestimo> emprestimosAtivos = new ArrayList <Emprestimo> ();
	
	/** O usu�rio selecionado da busca padr�o do sistema */
	private int idUsuarioSelecionadoBuscaPadrao;
	
	/** Informa��es sobre o v�nculo ativo do usu�rio para empr�stimos da biblioteca. */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	/** O Empr�stimo do material perdido selecionado pelo usu�rio */
	private Emprestimo emprestimo = new Emprestimo();
	
	
	/** O motivo para justificar a perda do material. */
	private String motivo;
	
	
	/** O novo prazo que vai ser dado para o aluno adquirir um novo livro, prazo sugerido pelo sistema de 30 dias*/
	private Date novoPrazo;
	
	
	/** Guarda a lista de materiais perdido que foram comunicados */
	private List <Object []> listaMateriaisPerdidos;
	
	
	/** Habilita o link para o usu�rio imprimir o comprovante que ele fez a comunica��o do material perdido*/
	private boolean habilitaComprovante = false;
	
	/** Habilita o link para o usu�rio imprimir o comprovante da devolu��o do material*/
	private boolean habilitaComprovanteDevolucao = false;
	
	/** Guarda o empr�timo que foi devolvido para poder emitir o comprovante de devolu��o. Usa um DTO porque � compartinhado com a devolu��o desktop*/
	private EmprestimoDto emprestimoDevolvido;
	
	/** Mensagem mostradas no comprovante de devolu��o, dependem da estrat�gia de puni��o: usu�rio suspenso at�... usu�rio multado em ...*/
	private List<String> mensagensComprovanteDevolucao;
	
	/**
	 * Liga��o com o caso de uso de reservas. 
	 * Caso particular que indica que todos os materiais emprestados do T�tulo foram perdidos.
	 * Se o sistema trabalhar com reservas, o sistema mostrar� uma mensagem para o operador, sugerindo que 
	 * ele cancela as reservas existentes para o T�tulo em quest�o. */
	private boolean todosMateriasEmprestadosEstaoPeridos = false;
	
	/** O t�tulo do cujo material foi comunicada a perda, � importante guardar para ser usado do caso de uso de reserva.
	 * Caso seja chamado por esse. 
	 */
	private int idTituloDoMaterialPerdido;
	
	/** Guarda as informa��es do material que ser�o mostradas no comprovante de comunica��o */
	private CacheEntidadesMarc infoMaterial ;
	
	
	
	public ComunicarMaterialPerdidoMBean (){
		obj = new UsuarioBiblioteca();
	}
	
	//////////////////////////// M�todos da Interface de busca ////////////////////////////
	

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		// Passou o parametro do usu�rio, sen�o passar utiliza o atual
		if(idUsuarioSelecionadoBuscaPadrao > 0){
			obj = new UsuarioBiblioteca(idUsuarioSelecionadoBuscaPadrao);
			populateObj();
		}else{
			if(obj == null || obj.getId() <=0 ){
				addMensagemErro("Usu�rio  n�o informado.");
				return null;
			}
		}
		
		try {
		
			informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(obj, null, null);
		
			atualizaEmprestimosUsuario();
			
			if(emprestimosAtivos == null || emprestimosAtivos.size() == 0){
				addMensagemErro("Usu�rio n�o possui empr�stimos ativos.");
				return null;
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} 
		
		return telaListaEmprestimosAtivoUsuarioParaComunicarMaterialPerdido();
	}

	
	/** Atualiza os empr�stimos ativos do usu�rio 
	 * @throws DAOException 
	 */
	private void atualizaEmprestimosUsuario() throws DAOException {
		
		EmprestimoDao dao = null;
		ProrrogacaoEmprestimoDao daoProrrogacao = null;
		try {
		
		
		dao = getDAO(EmprestimoDao.class);
		emprestimosAtivos = dao.findEmprestimosAtivosByUsuarioMaterialBiblioteca(obj, null, null, false, null, null, null, null, null);
		
		if(emprestimosAtivos != null && emprestimosAtivos.size() > 0){
		
		
			List<Integer> idsEmprestimos = new ArrayList<Integer>();
			
			for (Emprestimo e : emprestimosAtivos){
				e.getMaterial().setInformacao( BibliotecaUtil.obtemDadosResumidosTituloDoMaterial(  e.getMaterial().getId())  );
				e.getMaterial().setTituloCatalografico( new TituloCatalografico(e.getMaterial().getTituloCatalografico().getId())); // vai precisar para imprimir as informa��o no comprovante.
				idsEmprestimos.add(e.getId());
			}
				
			
			daoProrrogacao = getDAO(ProrrogacaoEmprestimoDao.class);
			
			List<ProrrogacaoEmprestimo> prorrogacoes = daoProrrogacao.findProrrogacoesPorPerdaDeMaterialDosEmprestimos(idsEmprestimos);
			
			
			// Atribui as prorroga��es aos seus empr�stimos //
			for (Emprestimo e : emprestimosAtivos){
				for (ProrrogacaoEmprestimo prorrogacaoEmprestimo : prorrogacoes) {
					if(prorrogacaoEmprestimo.getEmprestimo().getId() == e.getId())
						e.getProrrogacoes().add(prorrogacaoEmprestimo);
				}
			}
		
		}
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa,String... parametros) {
		if(parametroDePessoa)
			idUsuarioSelecionadoBuscaPadrao = Integer.valueOf(parametros[3]);
		else
			idUsuarioSelecionadoBuscaPadrao = Integer.valueOf(parametros[2]);
		
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {

		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  <p>Inicia caso de uso de comunicar materias perdidos</p>
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul> 
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String iniciarComunicacao () throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
	
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, false, false, "Comunicar Materiais perdidos pelo Usu�rio", null);
	}

	/**
	 *  <p>Inicia caso de uso de comunicar materias perdidos de bibliotecas. Utilizado pela sess�o de informa��o e refer�ncia.</p>
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/informacao_referencia.jsp</li>
	 *   </ul> 
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String iniciarComunicacaoBiblioteca () throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
	
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, false, true, false, "Comunicar Materiais perdidos pelo Usu�rio", null);
	}



	/**
	 * Recupera os dados do empr�stimo selecionado e exibe a p�gina para o operador informar o motivo da prorroga��o.
	 * M�todo chamado pela seguinte JSP: /biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preComunicar() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		habilitaComprovante = false;
		todosMateriasEmprestadosEstaoPeridos = false;
		
		int idEmprestimoSelecionado = getParameterInt("idEmprestimo");
		
		for (Emprestimo  emp : emprestimosAtivos) {
			if( emp.getId() == idEmprestimoSelecionado ){
				emprestimo = emp;
				break;
			}
		} 
		
		iniciaDadosFormularioComunicacao();
		
		/// O sistema sugere um prazo de 30 dias que � o correto, mas dependendo da justificativo do usu�rio o bibliotec�rio pode aumentar ////
		Calendar cal = Calendar.getInstance();
		cal.setTime(emprestimo.getPrazo());
		cal.add(Calendar.DAY_OF_MONTH, 30);
		novoPrazo = cal.getTime();
		
		
		prepareMovimento(SigaaListaComando.COMUNICAR_MATERIAL_PERDIDO);
		
		return forward(PAGINA_FORM_COMUNICAR_MATERIAL_PERDIDO);
	}
	
	
	/** Inicia os dados do formul�rio no qualo usu�rio vai comunicar a perda de um material */
	private void iniciaDadosFormularioComunicacao(){
		
		motivo = "";
	}
	
	
	
	/**
	 * Comunica a perda de um material, prorrogando seu prazo. <br/><br/>
	 * M�todo chamado pela seguinte JSP: /biblioteca/circulacao/formComunicarMaterialPerdido.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String comunicar () throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (StringUtils.isEmpty(motivo))
			addMensagemErro ("O motivo deve ser informado.");
		
		if (novoPrazo == null || CalendarUtils.estorouPrazo(novoPrazo, new Date()))
			addMensagemErro ("O novo prazo deve ser informado. O mesmo deve ser posterior � data atual.");
		
		ProrrogacaoEmprestimoDao dao = null;
		ReservaMaterialBibliotecaDao daoReserva = null;
		
		
		if (!hasErrors()){
			MovimentoComunicarMaterialPerdido mov = new MovimentoComunicarMaterialPerdido(emprestimo, motivo, novoPrazo);
			
			try {
				
				dao = getDAO(ProrrogacaoEmprestimoDao.class);
				
				Object[] retorno= execute (mov); 
				
				@SuppressWarnings("unchecked")
				List<PunicaoAtrasoEmprestimoBiblioteca> punicoesSofridas = (List<PunicaoAtrasoEmprestimoBiblioteca>) retorno[0];
				emprestimo = (Emprestimo) retorno[1];
				
				
				emprestimo = dao.refresh(emprestimo);
				
				
				List<Integer> idsEmprestimos = new ArrayList<Integer>();
				idsEmprestimos.add(emprestimo.getId());
				
				emprestimo.getMaterial().setInformacao( BibliotecaUtil.obtemDadosResumidosTituloDoMaterial(  emprestimo.getMaterial().getId())  );
				
				emprestimo.setProrrogacoes(dao.findProrrogacoesPorPerdaDeMaterialDosEmprestimos(idsEmprestimos));
				
				addMensagemInformation("Comunica��o de perda cadastrada com sucesso.");
				
				for (PunicaoAtrasoEmprestimoBiblioteca punicaoSofrida : punicoesSofridas) {
					addMensagemInformation(punicaoSofrida.getMensagemComprovante()+", pois comunicou a perda com atraso. <br/>");
				}
				
				habilitaComprovante = true;
				
				idTituloDoMaterialPerdido = emprestimo.getMaterial().getTituloCatalografico().getId();
				
				infoMaterial =  BibliotecaUtil.obtemDadosTituloCache(idTituloDoMaterialPerdido);
				
				iniciaDadosFormularioComunicacao();
				
				if(ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
					
					try {
						
						daoReserva = getDAO(ReservaMaterialBibliotecaDao.class);
						
						int quantidadeReservaAtivasTitulo = daoReserva.countReservasAtivasDoTitulo(idTituloDoMaterialPerdido);
						
						if(quantidadeReservaAtivasTitulo > 0){
							todosMateriasEmprestadosEstaoPeridos = ReservaMaterialBibliotecaUtil.verificaTodosMateriaisEmprestadosTituloEstaoPerdidos(idTituloDoMaterialPerdido);
						}
						
					}finally{
						if(daoReserva != null) daoReserva.close();
					}
					
					if(todosMateriasEmprestadosEstaoPeridos){
						addMensagemWarning("Todos os materiais emprestados do T�tulo est�o perdidos, � recomendado que se cancele as reservas existentes para esse T�tulo.");
					}
					
				}
				
				return forward(PAGINA_FORM_COMUNICAR_MATERIAL_PERDIDO);
				
			} catch (NegocioException e){
				addMensagens (e.getListaMensagens());
			}finally{
				if( dao != null) dao.close();
			}
		}
		
		return null;
	}
	
	/**
	 * <p>Realiza a devolu��o de materiais perdidos, aplica as puni��es da parte de circula��o, por�m 
	 * o material no lugar de voltar para a situa��o "DISPONIVEL" no acervo, voltar para a situa��o "BAIXADO".</p>
	 * 
	 * <p>O pr�xima usu�rio da reserva n�o � comunicado, pois n�o existem materiais para ele emprestar</p>
	 * 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp</li>
	 *   </ul> 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String preDevolverEmprestimo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		int idEmprestimoSelecionado = getParameterInt("idEmprestimo");
		
		for (Emprestimo  emp : emprestimosAtivos) {
			if( emp.getId() == idEmprestimoSelecionado ){
				emprestimo = emp;
				break;
			}
		} 
		
		// incializa os dados da tela //
		valorTipoDevolucao = DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_SIMILAR.getValor();
		motivoNaoEntregaMaterial = "";
		
		prepareMovimento(SigaaListaComando.DEVOLVE_EMPRESTIMO_MATERIAL_PERDIDO);
		
		return telaDevolveEmprestimoMaterialPerdido();
	}
	
	
	
	/**
	 * <p>Realiza a devolu��o de materiais perdidos, aplica as puni��es da parte de circula��o, por�m 
	 * o material no lugar de voltar para a situa��o "DISPONIVEL" no acervo, voltar para a situa��o "BAIXADO".</p>
	 * 
	 * <p>O pr�xima usu�rio da reserva n�o � comunicado, pois n�o existem materiais para ele emprestar</p>
	 * 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp</li>
	 *   </ul> 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String devolverEmprestimoBaixandoMaterial() throws ArqException{
		return devolverEmprestimo(true);
	}
	
	/**
	 * <p>Realiza a devolu��o de materiais perdidos, aplica as puni��es da parte de circula��o.
	 * Sem realizar a baixa do material, utilizando caso a baixa der erro e a devolu��o precise ser feita para o usu�rio n�o ser punido.
	 * A baixa dever se feita depois pelo op��es existentes no sistema.</p>
	 * 
	 * <p>O pr�xima usu�rio da reserva n�o � comunicado, pois n�o existem materiais para ele emprestar</p>
	 * 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp</li>
	 *   </ul> 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String devolverEmprestimoSemBaixarMaterial() throws ArqException{
		return devolverEmprestimo(false);
	}
	
	
	/**
	 * <p>Realiza a devolu��o de materiais perdidos, aplica as puni��es da parte de circula��o, por�m 
	 * o material no lugar de voltar para a situa��o "DISPONIVEL" no acervo, voltar para a situa��o "BAIXADO".</p>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	private String devolverEmprestimo(boolean realizarBaixaMaterial) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		DevolucaoMaterialPerdido devolucaoMaterialPerdido = new DevolucaoMaterialPerdido();
		
		devolucaoMaterialPerdido.setEmprestimo(emprestimo);
		devolucaoMaterialPerdido.setTipo(TipoDevolucaoMaterialPerdido.getTipoDevolucao(valorTipoDevolucao));
		devolucaoMaterialPerdido.setMotivoNaoEntregaMaterial(motivoNaoEntregaMaterial);
		
		MovimentoDevolveEmprestimoMaterialPerdido movimento = new MovimentoDevolveEmprestimoMaterialPerdido(emprestimo.getMaterial().getId(), devolucaoMaterialPerdido, realizarBaixaMaterial);
		
		try {
			RetornoOperacoesCirculacaoDTO retorno = execute(movimento);
			
			// Mostra mensagem cadastradas para serem mostradas no momento da devolu��o por meio de notas de circula��o.  //  
			for (String mensagem : retorno.mensagemAosUsuarios) {
				addMensagemWarning(mensagem);
			}
			
			// Mostra detalhes do material devolvido //  
			if(retorno.getOperacoesRealizadas() != null){
				for (OperacaoBibliotecaDto devolucao : retorno.getOperacoesRealizadas()) {
					addMensagemInformation("<strong> Prazo: </strong>"+ devolucao.getPrazoFormatado()
							+ "<br/>"+" <strong> Data da Devolu��o: </strong>"+ devolucao.getDataRealizacaoFormatada()
							+ "<br/><br/>"+devolucao.getInfoMaterial()+ " ");
				}
			}
			
			// Mostra mensagem de multa e suspens�o, se o usu�rio devolveu com atrazo //  
			for (String mensagem : retorno.mensagensComprovanteDevolucao) {
				addMensagemInformation(mensagem);
			}
			
			StringBuilder mensagemDevolucao = new  StringBuilder("Comunica��o de perda finalizada com sucesso. O empr�stimo foi devolvido ");
			
			if(realizarBaixaMaterial)
				mensagemDevolucao.append("e o material foi baixado no acervo.");
			else{
				if(! devolucaoMaterialPerdido.isUsuarioEntregouMaterialSimilar())
					mensagemDevolucao.append("por�m o material n�o foi baixado do acervo, lembre-se de realizar a baixa do material.");
				else
					mensagemDevolucao.append(".");
			}
			
			addMensagemInformation(mensagemDevolucao.toString());
			
			habilitaComprovanteDevolucao = true;
			
		    emprestimoDevolvido  = retorno.emprestimoRetornado;
		    mensagensComprovanteDevolucao = retorno.mensagensComprovanteDevolucao; 
			
		    atualizaEmprestimosUsuario();
		    
		    return telaListaEmprestimosAtivoUsuarioParaComunicarMaterialPerdido();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		
	}
	
	
	/** M�todo chamado para atualizar a p�gina quando o usu�rio altera o tipo de devolu��o. Mostrando ou n�o a motivo da n�o substitu���o do material*/
	public void verificaAlteracaoTipoDevolucao(ActionEvent evt){
		// N�o precisa fazer nada 
	}
	
	/**
	 * 
	 * Redireciona para a p�gina do comprovante de devolu��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/formComunicaMaterialPerdido.jsp</li>
	 *   </ul>
	 *
	 *  
	 *
	 * @return
	 */
	public String geraComprovanteDevolucao(){
		getCurrentRequest().setAttribute("liberaEmissaoComprovante", true); // para impedir que o usu�rio tente acessar diretamente a p�gina
		// v�rios MBean podem emitir o comprovante de devolu��o, o nome na p�gina est� padronizado
		getCurrentRequest().setAttribute("_mBeanRealizouDevolucao", this); 
		return telaComprovanteDevolucao();
	}
	
	/**
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @return
	 */
	public String telaComprovanteDevolucao() {
		return forward(ModuloCirculacaoMBean.PAGINA_COMPROVANTE_DEVOLUCAO);
	}
	
	
	/**
	 * <p>
	 * O caso de uso de comunicar material perdido pode chamar o caso de uso de reserva para cancelar as reservas do T�tulo se
	 * todos os materiais do T�tulo forem perdidos.
	 * </p>
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String cancelarReservasDoTitulo() throws ArqException{
		if(todosMateriasEmprestadosEstaoPeridos){
			VisualizarReservasMaterialBibliotecaMBean mBean = getMBean("visualizarReservasMaterialBibliotecaMBean");
			return mBean.iniciaVisualizacaoReservasDeUmTituloEspecifico(idTituloDoMaterialPerdido, PAGINA_FORM_COMUNICAR_MATERIAL_PERDIDO);
		}else
			return null;
	}
	
	
	
	/**
	 * 
	 *   Re imprime o comprovante de comunica��o para o empr�stimo selecionado, caso o empr�stimo tenha 
	 * sido prorrogado por perda.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp/</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String reImprimirComprovante () throws ArqException{
		
		int idEmprestimoSelecionado = getParameterInt("idEmprestimo");
		
		ProrrogacaoEmprestimoDao dao = null;
		
		try{
			
			dao = getDAO(ProrrogacaoEmprestimoDao.class);
		
			for (Emprestimo  emp : emprestimosAtivos) {
				if( emp.getId() == idEmprestimoSelecionado )
					emprestimo = emp;
			} 
		
			List<Integer> idsEmprestimos = new ArrayList<Integer>();
			idsEmprestimos.add(emprestimo.getId());
			
			emprestimo.setProrrogacoes( dao.findProrrogacoesPorPerdaDeMaterialDosEmprestimos(idsEmprestimos));
		
			if(emprestimo.getProrrogacoes() == null || emprestimo.getProrrogacoes() .size() == 0){
				addMensagemErro("N�o foi comunicada nenhuma perda de material para o empr�stimo escolhido.");
				return telaListaEmprestimosAtivoUsuarioParaComunicarMaterialPerdido();
			}
			
			habilitaComprovante = true;
			infoMaterial =  BibliotecaUtil.obtemDadosTituloCache(emprestimo.getMaterial().getTituloCatalografico().getId());
			
			return telaComprovanteComunicacaoMaterialPerdido();
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 *  M�todo que gera um c�digo de verifica��o com base no id da prorroga��o por perda de material gerada.<br/>
	 *  Assim se a prorroga��o por perda de material existir no banco de dados para o empr�stimo do usu�rio, o comprovante vai ser verdadeiro<br/>
	 *  Caso o usu�rio tente falsificar o comprovante o id da prorroga��o n�o vai bater.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>>/biblioteca/circulacao/comprovanteComunicacao.jsp</li>
	 *   </ul>
	 *
	 *   
	 *
	 * @return
	 */
	public String getCodigoAutenticacaoProrrogacao(){
		
		StringBuilder codigoComprovante = new StringBuilder();
		
		// C�digo de autentica��o com o id da prorroga��o por perda e a data do cadastro da prorroga��o por perda
		for(ProrrogacaoEmprestimo prorrogacao: emprestimo.getProrrogacoes()){
			codigoComprovante.append( BibliotecaUtil.geraNumeroAutenticacaoComprovantes(prorrogacao.getId(), prorrogacao.getDataCadastro())+" ");
		}
		
		
		
		return codigoComprovante.toString();
	}
	
	
	
	/**
	 * Exibe uma listagem contendo todos os usu�rios com materiais perdidos da biblioteca do operador.
	 * Caso o operador seja um administrador geral, retorna todos os usu�rios com materiais perdidos.
	 * 
	 * M�todo chamado pela seguinte JSP: /biblioteca/menus/circulacao.jsp
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String listarUsuariosComMateriaisPerdidos () throws SegurancaException, DAOException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		MaterialInformacionalDao dao = null;
		
		try {
			dao = getDAO(MaterialInformacionalDao.class);
			
			boolean administradorGeral = false;
			
			List<Integer> idUnidades = new ArrayList<Integer>();
			
			if (isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL))
				administradorGeral = true;
			else {
				
				administradorGeral = false;
				
				idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO));
			
				idUnidades.addAll( BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO));
				
			}
			
			
			listaMateriaisPerdidos = new ArrayList<Object[]>();
			List <Object []> listaMateriaisPerdidosTemp = dao.findListagemMateriaisPerdidosPessoas(idUnidades, administradorGeral);
			
			for (Object[] objects : listaMateriaisPerdidosTemp) {
				if(! contemEmprestimo(listaMateriaisPerdidos, objects[0]))
					listaMateriaisPerdidos.add(objects);
			}
			
			
			
			return forward(PAGINA_LISTA_MATERIAIS_PERDIDOS);
			
		} finally {
			if (dao != null)
			dao.close();
		}
		
		
	}
	
	
	/**
	 * Exibe uma listagem contendo todos os usu�rios com materiais perdidos da biblioteca do operador.
	 * Caso o operador seja um administrador geral, retorna todos os usu�rios com materiais perdidos.
	 * 
	 * M�todo chamado pela seguinte JSP: /biblioteca/menus/informacao_referencia.jsp
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String listarBibliotecasComMateriaisPerdidos() throws SegurancaException, DAOException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		MaterialInformacionalDao dao = null;
		
		try {
			dao = getDAO(MaterialInformacionalDao.class);
			
			boolean administradorGeral = false;
			
			List<Integer> idUnidades = new ArrayList<Integer>();
			
			if (isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL))
				administradorGeral = true;
			else {
				
				administradorGeral = false;
				
				idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO));
			
				idUnidades.addAll( BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO));
				
			}
			
			
			listaMateriaisPerdidos = new ArrayList<Object[]>();
			List <Object []> listaMateriaisPerdidosTemp = dao.findListagemMateriaisPerdidosBibliotecas(idUnidades, administradorGeral);
			
			for (Object[] objects : listaMateriaisPerdidosTemp) {
				if(! contemEmprestimo(listaMateriaisPerdidos, objects[0]))
					listaMateriaisPerdidos.add(objects);
			}
			
			
			
			return forward(PAGINA_LISTA_MATERIAIS_PERDIDOS);
			
		} finally {
			if (dao != null)
			dao.close();
		}
		
		
	}
	
	
	/**
	 * Verifica se o empr�stimio j� n�o foi adicionado a lista
	 *
	 * @param listaMateriaisPerdidos2
	 * @param object
	 * @return
	 */
	private boolean contemEmprestimo(List<Object[]> listaMateriaisPerdidosTemp, Object object) {
		
		for (Object[] objects : listaMateriaisPerdidosTemp) {
			if( new Integer( (Integer) objects[0]).equals(object))
				return true;
		}
		
		return false;
	}




	/**
	 *   Carrega os dados da comunica��o de perda de material selecionada
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/detalhesComunicaacoPerda.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public String getCarregaDadosComunicacao() throws DAOException{
		
		int idEmprestimo = getParameterInt("idEmprestimo", 0);
		
		int idUsuarioBiblioteca = getParameterInt("idUsuarioBiblioteca", 0);
		
		ProrrogacaoEmprestimoDao daoProrrogacao = null;
		
		
		
		try {
			daoProrrogacao = getDAO(ProrrogacaoEmprestimoDao.class);
			
			UsuarioBiblioteca ub = daoProrrogacao.refresh(new UsuarioBiblioteca(idUsuarioBiblioteca));
			
			informacaoUsuario =  new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(ub, null, null);
			
			List<Integer> idsEmprestimos = new ArrayList<Integer>();
			idsEmprestimos.add(idEmprestimo);
			
			emprestimo = daoProrrogacao.refresh(new Emprestimo(idEmprestimo));
			
			List<ProrrogacaoEmprestimo> prorrogacoes = daoProrrogacao.findProrrogacoesPorPerdaDeMaterialDosEmprestimos(idsEmprestimos);
			
			// Ordena as prorroga��es pelo prazo dado em ordem decescente.
			Collections.sort(prorrogacoes, new Comparator<ProrrogacaoEmprestimo>(){
				@Override
				public int compare(ProrrogacaoEmprestimo o1, ProrrogacaoEmprestimo o2) {
					return o1.getDataAtual().compareTo(o2.getDataAtual());
				}
				
			});
			
			emprestimo.getProrrogacoes().addAll(prorrogacoes);
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
		}finally{
			if(daoProrrogacao != null) daoProrrogacao.close();
		}
		
		return "";
	}
	
	
	
	/**
	 * 
	 * Retorna todas as bibliotecas internas cuja unidade o usu�rio tem papel de Circula��o, ou todas 
	 * as biblioteca se ele � administrador geral.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternasComPermissaoUsuario() throws DAOException{
		
		Collection <Biblioteca> b = new ArrayList<Biblioteca>();
		
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
			
			idUnidades.addAll( BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO));
			
			b  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
			
		}else{
			b  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		}
		
		return toSelectItems(b, "id", "descricaoCompleta");
	}


	
	
	/** 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/formComunicarMaterialPerdido.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String telaListaEmprestimosAtivoUsuarioParaComunicarMaterialPerdido() {
		return forward(PAGINA_LISTA_COMUNICAR_MATERIAL_PERDIDO);
	}
	
	
	/** 
	 *  Redireciona para a p�gina na qual o usu�rio vai poder devolver o empr�stimo de um material perdido no sistema.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String telaDevolveEmprestimoMaterialPerdido() {
		return forward(PAGINA_DEVOLVE_EMPRESTIMO_MATERIL_PERDIDO);
	}
	
	
	
	
	/**
	 * 
	 * Volta para a busca do usu�rio
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/listarEmprestimosComunicarPerdido.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String voltaTelaBusca(){
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	/**
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/formComunicarMaterialPerdido.jsp</li>
	 *    
	 *   </ul>
	 *
	 * @return
	 */
	public String telaComprovanteComunicacaoMaterialPerdido() {
		return forward(PAGINA_COMPROVANTE_COMUNICACAO_MATERIAL_PERDIDO);
	}
	
	
	
	///  Sets e Gets ///
	
	
	public short getValorTipoDevolucaoUsuarioEntregouSimilar(){
		return DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_SIMILAR.getValor();
	}
	
	public short getValorTipoDevolucaoUsuarioEntregouEquivalente(){
		return DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE.getValor();
	}
	
	public short getValorTipoDevolucaoUsuarioNaoEntregou(){
		return DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido.USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO.getValor();
	}
	
	public short getValorTipoDevolucao() {
		return valorTipoDevolucao;
	}

	public void setValorTipoDevolucao(short valorTipoDevolucao) {
		this.valorTipoDevolucao = valorTipoDevolucao;
	}

	public List<Emprestimo> getEmprestimosAtivos() {
		return emprestimosAtivos;
	}

	public void setEmprestimosAtivos(List<Emprestimo> emprestimosAtivos) {
		this.emprestimosAtivos = emprestimosAtivos;
	}

	public InformacoesUsuarioBiblioteca getInformacaoUsuario() {
		return informacaoUsuario;
	}

	/** Tem que usar esse nome para a p�gina padr�o do comprovante de devolu��o. */
	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return informacaoUsuario;
	}
	
	public void setInformacaoUsuario(InformacoesUsuarioBiblioteca informacaoUsuario) {
		this.informacaoUsuario = informacaoUsuario;
	}

	public Emprestimo getEmprestimo() {
		return emprestimo;
	}

	public void setEmprestimo(Emprestimo emprestimo) {
		this.emprestimo = emprestimo;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Date getNovoPrazo() {
		return novoPrazo;
	}

	public void setNovoPrazo(Date novoPrazo) {
		this.novoPrazo = novoPrazo;
	}

	public List<Object[]> getListaMateriaisPerdidos() {
		return listaMateriaisPerdidos;
	}

	public void setListaMateriaisPerdidos(List<Object[]> listaMateriaisPerdidos) {
		this.listaMateriaisPerdidos = listaMateriaisPerdidos;
	}


	public boolean isHabilitaComprovante() {
		return habilitaComprovante;
	}

	public void setHabilitaComprovante(boolean habilitaComprovante) {
		this.habilitaComprovante = habilitaComprovante;
	}

	public CacheEntidadesMarc getInfoMaterial() {
		return infoMaterial;
	}

	public void setInfoMaterial(CacheEntidadesMarc infoMaterial) {
		this.infoMaterial = infoMaterial;
	}

	public boolean isTodosMateriasEmprestadosEstaoPeridos() {
		return todosMateriasEmprestadosEstaoPeridos;
	}

	public void setTodosMateriasEmprestadosEstaoPeridos(boolean todosMateriasEmprestadosEstaoPeridos) {
		this.todosMateriasEmprestadosEstaoPeridos = todosMateriasEmprestadosEstaoPeridos;
	}

	public String getMotivoNaoEntregaMaterial() {
		return motivoNaoEntregaMaterial;
	}

	public void setMotivoNaoEntregaMaterial(String motivoNaoEntregaMaterial) {
		this.motivoNaoEntregaMaterial = motivoNaoEntregaMaterial;
	}

	public boolean isHabilitaComprovanteDevolucao() {
		return habilitaComprovanteDevolucao;
	}

	public void setHabilitaComprovanteDevolucao(boolean habilitaComprovanteDevolucao) {
		this.habilitaComprovanteDevolucao = habilitaComprovanteDevolucao;
	}

	public EmprestimoDto getEmprestimoDevolvido() {
		return emprestimoDevolvido;
	}

	public void setEmprestimoDevolvido(EmprestimoDto emprestimoDevolvido) {
		this.emprestimoDevolvido = emprestimoDevolvido;
	}

	public List<String> getMensagensComprovanteDevolucao() {
		return mensagensComprovanteDevolucao;
	}

	public void setMensagensComprovanteDevolucao(	List<String> mensagensComprovanteDevolucao) {
		this.mensagensComprovanteDevolucao = mensagensComprovanteDevolucao;
	}
	
	
	
}                                                               