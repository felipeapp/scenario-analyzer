/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 *     <p>MBean que gerencia o caso de uso no qual o usu�rio pode alterar as informa��es de v�rios 
 * materiais de uma �nica vez.<p>
 *	  <p><i>A cria��o desse caso de uso foi necess�ria porque algumas vezes a quantidade de materiais 
 * a ser alterada � muito grande o que inviabiliza alterar individualmente. Ocorre principalmente com fasc�culos.</i></p>.
 * 
 * @author jadson
 * @since 29/01/2010
 * @version 1.0 criacao da classe
 *
 */
@Component("modificarNotaCirculacaoVariosMateriaisMBean")
@Scope("request")
public class ModificarNotaCirculacaoVariosMateriaisMBean extends SigaaAbstractController<NotaCirculacao> implements PesquisarMateriaisInformacionais {

	/**  P�gina para onde o fluxo retorna ao final da opera��o */
	public static final String PAGINA_VOLTAR = "/biblioteca/index.jsp";
	
	/**
	 * Formul�rio para incluir nota de circula��o
	 */
	public static final String PAGINA_FORM = "/biblioteca/processos_tecnicos/outras_operacoes/paginaModificarNotaCirculacaoVariosMateriais.jsp";

	/**
	 * P�gina default do m�dulo da biblioteca. Chamada no final das opera��es.
	 */
	public static final String PAGINA_INDEX = "/biblioteca/index.jsp";
	
	/**
	 * O motivo do bloqueio que o usu�rio vai digitar na p�gina
	 */
	private String nota;
	
	/**
	 * Indica se a nota a ser inclu�da � bloqueante ou n�o.  Habilita e desabilita campos na p�gina de inclus�o de nota
	 */
	private boolean notaBloqueante = true;
	
	/** Guarda o valor que o usu�rio selecionou na tela, para a op��o de mostrar a nota no pr�ximio empr�stimo */
	private boolean mostrarProximoEmprestimo = true;
	
	/** Guarda o valor que o usu�rio selecionou na tela, para a op��o de mostrar a nota no pr�ximia renova�ao */
	private boolean mostrarProximaRenovacao = true;
	
	/** Guarda o valor que o usu�rio selecionou na tela, para a op��o de mostrar a nota no pr�ximia devolu��o */
	private boolean mostrarProximaDevolucao = true;
	
	
	/**
	 * Materias selecionados para os quais a nota ser� criada
	 */
	private List<MaterialInformacional> materiaisParaModificacaoNota = new ArrayList<MaterialInformacional>();
	
	/**
	 * <p>P�gina para o onde o caso uso vai retornar depois de incluir a nota no material.</p>
	 * 
	 * <p>Usado quando o usu�rio chama o caso de uso de incluir notas a partir de outros casos de uso no sistema, caso contr�rio 
	 * retorna para a p�gina de listagem das notas de circula��o. </p>
	 */
	//private String paginaVoltarDepoisInclusao;
	
	/**
	 * Indica a opera��o que est� sendo executada
	 */
	private int operacao;

	/** Opera��o incluir nota */
	private static final int OPERACAO_INCLUIR = 0;
	/** Opera��o editar nota */
	private static final int OPERACAO_EDITAR = 1;
	/** Opera��o remover nota */
	private static final int OPERACAO_REMOVER = 2;
	
	
	
	public ModificarNotaCirculacaoVariosMateriaisMBean (){
		
	}
	
	

	/**
	 * 
	 * M�todo chamado para iniciar a pesquisa do material para o qual se pretende incluir uma nota
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarBuscaMaterialIncluirNota(){
		operacao = OPERACAO_INCLUIR;
		
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Material para Incluir uma Nota de Circula��o", 
				" <p>Caro usu�rio,</p> <p>Por favor, busque os materiais para os quais se deseja incluir uma nota de circula��o. </p> "
				, "Incluir Nota");
	}
	
	

//	/**
//	 * 
//	 * M�todo chamado para iniciar a pesquisa do material para o qual se pretende editar uma nota
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
//		return pBean.iniciarBusca(this, "Pesquisar Material para Editar uma Nota de Circula��o", 
//				" <p>Caro usu�rio,</p> <p>Por favor, busque os materiais cujas notas de circula��o se deseja editar. </p> "
//				, "Editar Nota");
//	}
//	
	

	/**
	 * 
	 * M�todo chamado para iniciar a pesquisa do material para o qual se pretende inclu�r uma nota
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarBuscaMaterialRemoverNota(){
		operacao = OPERACAO_REMOVER;
		
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Material para Remover uma Nota de Circula��o", 
				" <p>Caro usu�rio,</p> <p>Por favor, busque os materiais cujas notas de circula��o se deseja remover. </p> "
				, "Remover Nota");
	}
	
	
	
	
	
	
	//////////////////// M�todos da interface de pesquisa  ///////////////////////
	
	
	/**
	 * <p>M�todo n�o chamado de p�gina JSP </p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais#setMaterial(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional)
	 */
	@Override
	public void setMateriaisPesquisaPadraoMateriais(List<MaterialInformacional> materiais) throws ArqException {
		this.materiaisParaModificacaoNota = materiais;
	}
	
	
	
	/**
	 * <p>M�todo n�o chamado de p�gina JSP </p>
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
				// se debloquear algum material, prepara para desbloquear o pr�ximo //
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
	 * Retorna para a p�gina de notas, no bot�o voltar da p�gina de pesquisa padr�o de materiais
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Implementa a a��o do bot�o voltar na tela de inclus�o de uma nota. </p>
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemInformation("Notas de circula��o inclu�das com sucesso!");
			else {
				// Caso algum material der erro, inclui as notas nos outros e s� mostra a mensagem que n�o conseguiu incluir em alguns materiais
				addMensagens(erros);
				
				if (materiaisParaModificacaoNota.size() > erros.size()) {
					addMensagemInformation("Demais notas de circula��o inclu�das com sucesso!");
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
//	 * Edita as notas dos materiais informacionais da lista, a partir dos dados informados pelo usu�rio.
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
//				addMensagemInformation("Notas de circula��o editadas com sucesso!");
//			else {
//				// Caso algum material der erro, inclui as notas nos outros e s� mostra a mensagem que n�o conseguiu incluir em alguns materiais
//				addMensagens(erros);
//				
//				if (qtdMateriaisParaModificacaoNota > erros.size()) {
//					addMensagemInformation("Demais notas de circula��o editadas com sucesso!");
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
	 * Desbloqueia um material informacional, fazendo com que ele fique novamente dispon�vel para empr�stimos.
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
				addMensagemInformation("Notas de circula��o removidas com sucesso!");
			else {
				// Caso algum material der erro, inclui as notas nos outros e s� mostra a mensagem que n�o conseguiu incluir em alguns materiais
				addMensagens(erros);
				
				if (qtdMateriaisParaModificacaoNota > erros.size()) {
					addMensagemInformation("Demais notas de circula��o removidas com sucesso!");
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
