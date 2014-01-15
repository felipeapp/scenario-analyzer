/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/01/2010
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.NotaCirculacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.NotaCirculacao;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoModificarNotaCirculacao;
import br.ufrn.sigaa.biblioteca.jsf.PesquisaMateriaisInformacionaisMBean;
import br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 *     <p>MBean que gerencia o caso de uso no qual o usuário pode alterar as informações de vários 
 * materiais de uma única vez.<p>
 *	  <p><i>A criação desse caso de uso foi necessária porque algumas vezes a quantidade de materiais 
 * a ser alterada é muito grande o que inviabiliza alterar individualmente. Ocorre principalmente com fascículos.</i></p>.
 * 
 * @author jadson
 * @since 29/01/2010
 * @version 1.0 criacao da classe
 *
 */
@Component("modificarNotaCirculacaoVariosMateriaisMBean")
@Scope("request")
public class ModificarNotaCirculacaoVariosMateriaisMBean extends SigaaAbstractController<NotaCirculacao> implements PesquisarMateriaisInformacionais {

	/**  Página para onde o fluxo retorna ao final da operação */
	public static final String PAGINA_VOLTAR = "/biblioteca/index.jsp";
	
	/**
	 * Formulário para incluir nota de circulação
	 */
	public static final String PAGINA_FORM = "/biblioteca/processos_tecnicos/outras_operacoes/paginaModificarNotaCirculacaoVariosMateriais.jsp";

	/**
	 * Página default do módulo da biblioteca. Chamada no final das operações.
	 */
	public static final String PAGINA_INDEX = "/biblioteca/index.jsp";
	
	/**
	 * O motivo do bloqueio que o usuário vai digitar na página
	 */
	private String nota;
	
	/**
	 * Indica se a nota a ser incluída é bloqueante ou não.  Habilita e desabilita campos na página de inclusão de nota
	 */
	private boolean notaBloqueante = true;
	
	/** Guarda o valor que o usuário selecionou na tela, para a opção de mostrar a nota no próximio empréstimo */
	private boolean mostrarProximoEmprestimo = true;
	
	/** Guarda o valor que o usuário selecionou na tela, para a opção de mostrar a nota no próximia renovaçao */
	private boolean mostrarProximaRenovacao = true;
	
	/** Guarda o valor que o usuário selecionou na tela, para a opção de mostrar a nota no próximia devolução */
	private boolean mostrarProximaDevolucao = true;
	
	
	/**
	 * Materias selecionados para os quais a nota será criada
	 */
	private List<MaterialInformacional> materiaisParaModificacaoNota = new ArrayList<MaterialInformacional>();
	
	/**
	 * <p>Página para o onde o caso uso vai retornar depois de incluir a nota no material.</p>
	 * 
	 * <p>Usado quando o usuário chama o caso de uso de incluir notas a partir de outros casos de uso no sistema, caso contrário 
	 * retorna para a página de listagem das notas de circulação. </p>
	 */
	//private String paginaVoltarDepoisInclusao;
	
	/**
	 * Indica a operação que está sendo executada
	 */
	private int operacao;

	/** Operação incluir nota */
	private static final int OPERACAO_INCLUIR = 0;
	/** Operação editar nota */
	private static final int OPERACAO_EDITAR = 1;
	/** Operação remover nota */
	private static final int OPERACAO_REMOVER = 2;
	
	
	
	public ModificarNotaCirculacaoVariosMateriaisMBean (){
		
	}
	
	

	/**
	 * 
	 * Método chamado para iniciar a pesquisa do material para o qual se pretende incluir uma nota
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarBuscaMaterialIncluirNota(){
		operacao = OPERACAO_INCLUIR;
		
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Material para Incluir uma Nota de Circulação", 
				" <p>Caro usuário,</p> <p>Por favor, busque os materiais para os quais se deseja incluir uma nota de circulação. </p> "
				, "Incluir Nota");
	}
	
	

//	/**
//	 * 
//	 * Método chamado para iniciar a pesquisa do material para o qual se pretende editar uma nota
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    	<li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
//	 *   </ul>
//	 *
//	 * @return
//	 */
//	public String iniciarBuscaMaterialEditarNota(){
//		operacao = OPERACAO_EDITAR;
//		
//		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
//		return pBean.iniciarBusca(this, "Pesquisar Material para Editar uma Nota de Circulação", 
//				" <p>Caro usuário,</p> <p>Por favor, busque os materiais cujas notas de circulação se deseja editar. </p> "
//				, "Editar Nota");
//	}
//	
	

	/**
	 * 
	 * Método chamado para iniciar a pesquisa do material para o qual se pretende incluír uma nota
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarBuscaMaterialRemoverNota(){
		operacao = OPERACAO_REMOVER;
		
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Material para Remover uma Nota de Circulação", 
				" <p>Caro usuário,</p> <p>Por favor, busque os materiais cujas notas de circulação se deseja remover. </p> "
				, "Remover Nota");
	}
	
	
	
	
	
	
	//////////////////// Métodos da interface de pesquisa  ///////////////////////
	
	
	/**
	 * <p>Método não chamado de página JSP </p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais#setMaterial(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional)
	 */
	@Override
	public void setMateriaisPesquisaPadraoMateriais(List<MaterialInformacional> materiais) throws ArqException {
		this.materiaisParaModificacaoNota = materiais;
	}
	
	
	
	/**
	 * <p>Método não chamado de página JSP </p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais#selecionaMaterial()
	 */
	@Override
	public String selecionouMateriaisPesquisaPadraoMateriais() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
		
		MaterialInformacionalDao dao = null;
		EmprestimoDao daoEmp = null;
		NotaCirculacaoDao daoNota = null;
				
		try {
			
			dao = getDAO(MaterialInformacionalDao.class);
			daoEmp = getDAO(EmprestimoDao.class);
			daoNota = getDAO(NotaCirculacaoDao.class);
			
			for (MaterialInformacional m : materiaisParaModificacaoNota) {
				m.setInformacao( BibliotecaUtil.obtemDadosMaterialInformacional(m.getId()));
				Emprestimo emp = daoEmp.findEmAbertoByCodigoBarras(m.getCodigoBarras());
			
				if(emp != null){
					
					m.setPrazoEmprestimo( emp.getPrazo() );
					emp.setQuantidadeRenovacoes(BibliotecaUtil.getQuantidadeRenovacoesEmprestimo(emp));
					
					try {
						if(emp.podeRenovar() && ! emp.isAtrasado()){
							m.setPodeRenovar(true);
							
							SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
							if(formatador.format(emp.getPrazo()).equals( formatador.format(new Date()))){
								m.setUltimoDiaRenovacao(true);
							}else{
								m.setUltimoDiaRenovacao(false);
							}
						}else{
							m.setPodeRenovar(false);
						}
					} catch (NegocioException e) {
						m.setPodeRenovar(false);
					}
			
				}	
			}

			if (operacao == OPERACAO_INCLUIR) {
				// Prepara incluir nota ou desbloquear material //
				prepareMovimento(SigaaListaComando.INCLUIR_NOTA_MATERIAL_INFORMACIONAL);
				
				this.nota = null;
				this.notaBloqueante = true;
				this.mostrarProximoEmprestimo = true;
				this.mostrarProximaRenovacao = true;
				this.mostrarProximaDevolucao = true;
			} else if (operacao == OPERACAO_EDITAR) {
				// Prepara incluir nota ou desbloquear material //
				prepareMovimento(SigaaListaComando.EDITAR_NOTA_MATERIAL_INFORMACIONAL);
				
				this.nota = null;
				this.notaBloqueante = true;
				this.mostrarProximoEmprestimo = true;
				this.mostrarProximaRenovacao = true;
				this.mostrarProximaDevolucao = true;

				NotaCirculacao nota = null;
				
				for (MaterialInformacional m : materiaisParaModificacaoNota) {
					nota = daoNota.getNotaAtivaDoMaterial(m.getId());
					
					if (nota != null) {
						m.setInformacao2(nota.getNota());
					}
				}
			} else {
				// se debloquear algum material, prepara para desbloquear o próximo //
				prepareMovimento( SigaaListaComando.REMOVER_NOTA_MATERIAL_INFORMACIONAL );

				NotaCirculacao nota = null;
				
				for (MaterialInformacional m : materiaisParaModificacaoNota) {
					nota = daoNota.getNotaAtivaDoMaterial(m.getId());
					
					if (nota != null) {
						m.setInformacao2(nota.getNota());
					}
				}
			}
			
			return forward(PAGINA_FORM);
			
		} finally {
			if (dao != null)
				dao.close();
			if (daoEmp != null)
				daoEmp.close();
			if (daoNota != null)
				daoNota.close();
		}
		
	}


	


	/**
	 * Retorna para a página de notas, no botão voltar da página de pesquisa padrão de materiais
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa/biblioteca/pesquisaPadraoMaterialInformacional.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais#voltarBuscaPesquisaPadraoMateriais()
	 */
	@Override
	public String voltarBuscaPesquisaPadraoMateriais() throws ArqException {
		return forward(PAGINA_VOLTAR); 
	}
	

	
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * <p>Implementa a ação do botão voltar na tela de inclusão de uma nota. </p>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/formIncluirNotaDeCirculacaoMaterial.jsp</li>
	 *   </ul> 
	 */
	public String voltarTelaBusca() throws ArqException{
		PesquisaMateriaisInformacionaisMBean bean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		bean.setMbeanChamador(this);
		return bean.telaPesquisaPadraoMateriaisInformacionais();
	}
	
	
	/**
	 * Inclui uma nota dos materiais selecionados
	 * <br/><br/>
	 * Chamado a partir de : /sigaa.war/biblioteca/circulacao/formIncluirNotaDeCirculacaoMaterial.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String incluirNota () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
		
		try {
			
			List<NotaCirculacao> notas = new ArrayList<NotaCirculacao>();
			
			for (MaterialInformacional materialInclusaoNota : materiaisParaModificacaoNota) {						
				notas.add(new NotaCirculacao(materialInclusaoNota, 
						this.nota, 
						this.notaBloqueante, 
						this.mostrarProximoEmprestimo, 
						this.mostrarProximaRenovacao, 
						this.mostrarProximaDevolucao));
			}
			
			MovimentoModificarNotaCirculacao mov =  new MovimentoModificarNotaCirculacao(notas);
			mov.setCodMovimento(SigaaListaComando.INCLUIR_NOTA_MATERIAL_INFORMACIONAL);
			
			ListaMensagens erros = execute(mov);  

			if(erros.size() == 0)
				addMensagemInformation("Notas de circulação incluídas com sucesso!");
			else {
				// Caso algum material der erro, inclui as notas nos outros e só mostra a mensagem que não conseguiu incluir em alguns materiais
				addMensagens(erros);
				
				if (materiaisParaModificacaoNota.size() > erros.size()) {
					addMensagemInformation("Demais notas de circulação incluídas com sucesso!");
				}
			}
			return forward(PAGINA_INDEX);
	
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			ne.printStackTrace();
			return null;
		}
	}

	
//	/**
//	 * Edita as notas dos materiais informacionais da lista, a partir dos dados informados pelo usuário.
//	 * usado em /sigaa.war/biblioteca/circulacao/listaMateriaisBloqueados.jsp
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String editarNota() throws ArqException{
//		
//		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
//				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
//				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
//
//		NotaCirculacaoDao dao = null;
//		
//		try {
//			dao = getDAO(NotaCirculacaoDao.class);
//			
//			int qtdMateriaisParaModificacaoNota = materiaisParaModificacaoNota.size();
//
//			List<NotaCirculacao> notas = dao.findAtivaByMaterial(materiaisParaModificacaoNota);
//			
//			for (NotaCirculacao nota : notas) {
//				nota.setNota(this.nota);
//				nota.setBloquearMaterial(this.notaBloqueante);
//				nota.setMostrarEmprestimo(this.mostrarProximoEmprestimo);
//				nota.setMostrarRenovacao(this.mostrarProximaRenovacao);
//				nota.setMostrarDevolucao(this.mostrarProximaDevolucao);
//			}
//
//			MovimentoModificarNotaCirculacao mov =  new MovimentoModificarNotaCirculacao(notas, materiaisParaModificacaoNota);
//			mov.setCodMovimento(SigaaListaComando.EDITAR_NOTA_MATERIAL_INFORMACIONAL);
//			
//			ListaMensagens erros = execute(mov);  
//			
//			if(erros.size() == 0)
//				addMensagemInformation("Notas de circulação editadas com sucesso!");
//			else {
//				// Caso algum material der erro, inclui as notas nos outros e só mostra a mensagem que não conseguiu incluir em alguns materiais
//				addMensagens(erros);
//				
//				if (qtdMateriaisParaModificacaoNota > erros.size()) {
//					addMensagemInformation("Demais notas de circulação editadas com sucesso!");
//				}
//			}
//
//			return forward(PAGINA_INDEX);
//		} catch (NegocioException ne){
//			addMensagens(ne.getListaMensagens());
//			ne.printStackTrace();
//			return null;
//		} finally {
//			if (dao != null) dao.close();
//		}
//		
//	}
	
	
	
	/**
	 * Desbloqueia um material informacional, fazendo com que ele fique novamente disponível para empréstimos.
	 * usado em /sigaa.war/biblioteca/circulacao/listaMateriaisBloqueados.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String desbloquearRemoverNota() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);

		NotaCirculacaoDao dao = null;
		
		try {

			dao = getDAO(NotaCirculacaoDao.class);

			int qtdMateriaisParaModificacaoNota = materiaisParaModificacaoNota.size();

			List<NotaCirculacao> notas = dao.findAtivaByMaterial(materiaisParaModificacaoNota);
			
			MovimentoModificarNotaCirculacao mov = new MovimentoModificarNotaCirculacao(notas, materiaisParaModificacaoNota);
			mov.setCodMovimento(SigaaListaComando.REMOVER_NOTA_MATERIAL_INFORMACIONAL);
			
			ListaMensagens erros = execute(mov);  
			
			if(erros.size() == 0)
				addMensagemInformation("Notas de circulação removidas com sucesso!");
			else {
				// Caso algum material der erro, inclui as notas nos outros e só mostra a mensagem que não conseguiu incluir em alguns materiais
				addMensagens(erros);
				
				if (qtdMateriaisParaModificacaoNota > erros.size()) {
					addMensagemInformation("Demais notas de circulação removidas com sucesso!");
				}
			}
			
			return forward(PAGINA_INDEX);
			
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			ne.printStackTrace();
			return null;
		} finally {
			if (dao != null) dao.close();
		}
		
	}
	

	
	
	
	/**
	 *  <p> Retorna uma listagem contendo os materiais com notas.  </p>
	 * 
	 * <p>
	 * Chamado a partir de :
	 * <ul>
	 * 	<li> Usado em /sigaa.war/biblioteca/circulacao/listaMateriaisComNotasDeCirculacao.jsp </li>
	 * <ul>
	 */
	public List <NotaCirculacao> getAll () throws DAOException{
		return (List <NotaCirculacao>) all;
	}

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public List<MaterialInformacional> getMateriaisParaModificacaoNota() {
		return materiaisParaModificacaoNota;
	}

	public void setMateriaisParaModificacaoNota(List<MaterialInformacional> materiaisParaModificacaoNota) {
		this.materiaisParaModificacaoNota = materiaisParaModificacaoNota;
	}

	public boolean isNotaBloqueante() {
		return notaBloqueante;
	}

	public void setNotaBloqueante(boolean notaBloqueante) {
		this.notaBloqueante = notaBloqueante;
	}

	public boolean isMostrarProximoEmprestimo() {
		return mostrarProximoEmprestimo;
	}

	public void setMostrarProximoEmprestimo(boolean mostrarProximoEmprestimo) {
		this.mostrarProximoEmprestimo = mostrarProximoEmprestimo;
	}

	public boolean isMostrarProximaRenovacao() {
		return mostrarProximaRenovacao;
	}

	public void setMostrarProximaRenovacao(boolean mostrarProximaRenovacao) {
		this.mostrarProximaRenovacao = mostrarProximaRenovacao;
	}

	public boolean isMostrarProximaDevolucao() {
		return mostrarProximaDevolucao;
	}

	public void setMostrarProximaDevolucao(boolean mostrarProximaDevolucao) {
		this.mostrarProximaDevolucao = mostrarProximaDevolucao;
	}
	
	public boolean isOperacaoIncluir() {
		return operacao == OPERACAO_INCLUIR;
	}
	
	public boolean isOperacaoEditar() {
		return operacao == OPERACAO_EDITAR;
	}
	
}
