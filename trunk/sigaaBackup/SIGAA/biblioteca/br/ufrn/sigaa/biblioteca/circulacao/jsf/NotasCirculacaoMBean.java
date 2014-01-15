package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.EditaMaterialInformacionalMBean;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * Gerencia o envio de notas ao setor de circula��o sobre os materiais do acervo.
 * 
 * @author Fred_Castro
 *
 */

@Component("notasCirculacaoMBean")
@Scope("request")
public class NotasCirculacaoMBean extends SigaaAbstractController <NotaCirculacao> implements PesquisarMateriaisInformacionais{
	
	/**
	 * Formul�rio para incluir nota de circula��o
	 */
	public static final String PAGINA_FORM = "/biblioteca/circulacao/formIncluirNotaDeCirculacaoMaterial.jsp";
	
	/**
	 * Formul�rio para incluir nota de circula��o
	 */
	public static final String PAGINA_EDIT_FORM = "/biblioteca/circulacao/formEditarNotaDeCirculacaoMaterial.jsp";
	
	/**
	 * P�gina que lista os materiais que est�o com nota
	 */
	public static final String PAGINA_LISTA = "/biblioteca/circulacao/listaMateriaisComNotasDeCirculacao.jsp";
	
	
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
	private List<MaterialInformacional> materiaisParaInclusaoNota = new ArrayList<MaterialInformacional>();
	
	/**
	 * <p>P�gina para o onde o caso uso vai retornar depois de incluir a nota no material.</p>
	 * 
	 * <p>Usado quando o usu�rio chama o caso de uso de incluir notas a partir de outros casos de uso no sistema, caso contr�rio 
	 * retorna para a p�gina de listagem das notas de circula��o. </p>
	 */
	private String paginaVoltarDepoisInclusao;
	
	
	/**
	 * A nota de circula��o a ser editada
	 */
	private NotaCirculacao notaCirculacaoEdicao;
	
	/** Guarda as informa��es dos materiais da notas de circula��o, para n�o precisar ficar buscando 
	 * sempre que retornar para a p�gina de lista. Diminuindo a quantidade de consultas ao banco. 
	 * J� que para cada materail com nota � realizada uma busca no banco. */
	private Map<Integer, String> cacheInformacoesMateriais = new HashMap<Integer, String>();
	
	
	public NotasCirculacaoMBean (){
		
	}
	
	

	/**
	 * 
	 * M�todo chamado para iniciar a pesquisa do material para o qual se pretende inclu�r uma nota
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa.war/biblioteca/circulacao/listaMateriaisComNotasDeCirculacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarBuscaMaterialIncluirNota(){
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Material para Incluir uma Nota de Circula��o", 
				" <p>Caro usu�rio,</p> <p>Por favor, busque o material para o qual se deseja incluir uma nota de circula��o. </p> "
				, "Incluir Nota");
	}
	
	
	
	/**
	 * <p>M�todo chamado de outros casos de uso, quando j� se est� trabalhando com o material e se deseja 
	 * incluir uma nota de circula��o, pulando a etapa de pesquisar esse material. <p>
	 * 
	 * <p>Qualquer caso de uso que queira chamar o caso de uso para incluir uma nova de circula��o em algum 
	 * material espec�fico deve chamar esse m�todo. Exemplo: {@link EditaMaterialInformacionalMBean#incluirNotaCirculacaoExemplar()} 
	 * 
	 * </p> 
	 *  
	 *  <br/>
	 *  M�todo n�o chamado por nenhuma JSP.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarConfigurandoDiretamenteMaterialIncluirNota(List<MaterialInformacional> materiais, String paginaVoltarDepoisInclusao) throws ArqException{
		
		this.materiaisParaInclusaoNota = new ArrayList<MaterialInformacional>();
		this.materiaisParaInclusaoNota.addAll( materiais );
		this.paginaVoltarDepoisInclusao = paginaVoltarDepoisInclusao;
		return selecionouMateriaisPesquisaPadraoMateriais();
		
	}
	
	
	/**
	 * <p> Come�a o caso de uso de criar notas de circula��o.  Listando os materiais que possuem alguma nota. </p>
	 * <p> Lista apenas os materias da biblioteca onde o usu�rio tem permiss�o de circula��o ou cataloga��o, ou todos os materiais,
	 * caso seja um administrador geral do sistema. </p>
	 * 
	 * <ul>
	 * 		<li> Usado em /sigaa.war/biblioteca/menus/circulacao.jsp </li>
	 *      <li> Usado em /sigaa.war/biblioteca/menus/processos_tecnicos.jsp </li>
	 * </ul>
	 * @throws ArqException 
	 */
	@Override
	public String listar () throws ArqException{

		prepareMovimento(SigaaListaComando.REMOVER_NOTA_MATERIAL_INFORMACIONAL); // prepara o desbloqueio
	
		long tempo = System.currentTimeMillis();
		
		NotaCirculacaoDao dao = null;
	
		try {
			dao = getDAO(NotaCirculacaoDao.class);
			
			List<Integer> idUnidades = new ArrayList<Integer>();
			
			if (! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				if (! isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO)
					&& ! isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO)
					&& ! isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO)
					&& ! isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS)
					&& ! isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO)){
				
					idUnidades.add(-1);  // para n�o trazer nenhum material
				}else{
					//  retorna os ids da unidades onde o usu�rio tem permiss�o para os papeis que podem criar notas //
					idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO));
					idUnidades.addAll( BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO));
					idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO));
					idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS));
					idUnidades.addAll( BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO));
				}
			}
			
			all = dao.findAllMateriaisComNotasByBibliotecas(idUnidades);
			
			for (NotaCirculacao nota : all){
				
				String informacoesMaterialEmCache = cacheInformacoesMateriais.get(nota.getMaterial().getId());
				
				if(StringUtils.isEmpty(informacoesMaterialEmCache)){ // busca no banco e coloca no cache
					informacoesMaterialEmCache = BibliotecaUtil.obtemDadosMaterialInformacional(nota.getMaterial().getId());
					cacheInformacoesMateriais.put(nota.getMaterial().getId(), informacoesMaterialEmCache);
				}
				
				nota.getMaterial().setInformacao(informacoesMaterialEmCache);
			}
			
			// Ordena pela biblioteca e c�digo de barras para mostrar ao usu�rio //
			Collections.sort((List<NotaCirculacao>) all, new Comparator<NotaCirculacao>(){
				@Override
				public int compare(NotaCirculacao o1, NotaCirculacao o2) {
					int resultado = new Integer (o1.getMaterial().getBiblioteca().getId()).compareTo(   new Integer (o2.getMaterial().getBiblioteca().getId() )  );
					
					if(resultado != 0 )
						return resultado;
					else
						return  o1.getMaterial().getCodigoBarras().compareTo(o2.getMaterial().getCodigoBarras());
				}
				
			});
			
			System.out.println(" Busca Todos materais com Nota >>>> "+ ((System.currentTimeMillis()-tempo)) +" ms  <<<<<<<<<<<");
			
		} finally {
			if (dao != null) dao.close();
		}
		
		return forward(PAGINA_LISTA);
	}
	
	
	
	
	
	
	//////////////////// M�todos da interface de pesquisa  ///////////////////////
	
	
	/**
	 * <p>M�todo n�o chamado de p�gina JSP </p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais#setMaterial(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional)
	 */
	@Override
	public void setMateriaisPesquisaPadraoMateriais(List<MaterialInformacional> materiais) throws ArqException {
		this.materiaisParaInclusaoNota = materiais;
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
		
		try {
			
			dao = getDAO(MaterialInformacionalDao.class);
			daoEmp = getDAO(EmprestimoDao.class);
			
			for (MaterialInformacional m : materiaisParaInclusaoNota) {
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
			
			// Prepara incluir nota ou desbloquear material //
			prepareMovimento(SigaaListaComando.INCLUIR_NOTA_MATERIAL_INFORMACIONAL);
			
			this.nota = null;
			this.notaBloqueante = true;
			this.mostrarProximoEmprestimo = true;
			this.mostrarProximaRenovacao = true;
			this.mostrarProximaDevolucao = true;
			
			return forward(PAGINA_FORM);
		} finally {
			if (dao != null)
				dao.close();
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
		return forward(PAGINA_LISTA); 
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
		if(StringUtils.isNotEmpty(paginaVoltarDepoisInclusao)){ // Se veio de outro caso de uso
			return forward(paginaVoltarDepoisInclusao);
		}else{
			return listar();
		}
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
			
			for (MaterialInformacional materialInclusaoNota : materiaisParaInclusaoNota) {
				
				notas.add(new NotaCirculacao(materialInclusaoNota, this.nota, this.notaBloqueante, this.mostrarProximoEmprestimo, this.mostrarProximaRenovacao, this.mostrarProximaDevolucao));
				
			}
			
			MovimentoModificarNotaCirculacao mov =  new MovimentoModificarNotaCirculacao(notas);
			mov.setCodMovimento(SigaaListaComando.INCLUIR_NOTA_MATERIAL_INFORMACIONAL);
			
			ListaMensagens erros = execute(mov);  
			
			if(erros.getErrorMessages().size() == 0)
				addMensagemInformation("Notas de circula��o inclu�das com sucesso!");
			else {
				// Caso algum material der erro, inclui as notas nos outros e s� mostra a mensagem que n�o conseguiu incluir em alguns materiais
				addMensagens(erros);
				
				if (materiaisParaInclusaoNota.size() > erros.getErrorMessages().size()) {
					addMensagemInformation("Demais notas de circula��o inclu�das com sucesso!");
				}
			}
			
			if(StringUtils.isNotEmpty(paginaVoltarDepoisInclusao)){ // Se veio de outro caso de uso
				return forward(paginaVoltarDepoisInclusao);
			}else
				return listar();
	
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	/**
	 * Prepara os dados para edi��o da nota de circula��o de um material informacional.
	 * 
	 * Chamado a partir de : /sigaa.war/biblioteca/circulacao/listaMateriaisComNotasDeCirculacao.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String prepararEditarNota() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
		
		EmprestimoDao daoEmp = null;
		
		try {
			
			int idMaterial = getParameterInt("idMaterial");
			
			for (NotaCirculacao nc : all) {  // Percorre os materiais da lista mostrada ao usu�rio at� achar o que foi selecionado
				
				if(nc.getMaterial().getId() ==  idMaterial){
					notaCirculacaoEdicao = nc;
				}
				
			}
			
			if(notaCirculacaoEdicao != null){
				
				daoEmp = getDAO(EmprestimoDao.class);
				
				MaterialInformacional m = notaCirculacaoEdicao.getMaterial();

				m.setInformacao(BibliotecaUtil.obtemDadosMaterialInformacional(m.getId()));
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
				

				
				// Prepara editar nota //
				prepareMovimento(SigaaListaComando.EDITAR_NOTA_MATERIAL_INFORMACIONAL);
				
				return forward(PAGINA_EDIT_FORM);
				
			}else{
				addMensagemWarning("Selecione um Material para editar.");
			}
		} finally {
			if (daoEmp != null)
				daoEmp.close();
		}
		
		return null;
				
	}
	
	/**
	 * Chamado a partir de : /sigaa.war/biblioteca/circulacao/formEditarNotaDeCirculacaoMaterial.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String editarNota() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
		
		try {
			
			MovimentoModificarNotaCirculacao mov =  new MovimentoModificarNotaCirculacao(new ArrayList<NotaCirculacao>(Arrays.asList(new NotaCirculacao[] { notaCirculacaoEdicao })), new ArrayList<MaterialInformacional>(Arrays.asList(new MaterialInformacional[] { notaCirculacaoEdicao.getMaterial() })));
			mov.setCodMovimento(SigaaListaComando.EDITAR_NOTA_MATERIAL_INFORMACIONAL);
			
			ListaMensagens erros = execute(mov);  

			if(erros.getErrorMessages().size() == 0)
				addMensagemInformation("Nota de Circula��o do material "+notaCirculacaoEdicao.getMaterial().getCodigoBarras()+" editada com sucesso !");
			else {
				addMensagens(erros);
			}

			return listar();
			
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	

	
	/**
	 * Desbloqueia um material informacional, fazendo com que ele fique novamente dispon�vel para empr�stimos.
	 * usado em /sigaa.war/biblioteca/circulacao/listaMateriaisBloqueados.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String desbloquearRemoverNota () throws ArqException{
		desbloquearRemoverNotaInterno(null);

		return listar();
	}

	/**
	 * Desbloqueia um material informacional, fazendo com que ele fique novamente dispon�vel para empr�stimos.
	 * N�o � utilizado por nenhum JSP, apenas para managed beans que necessitem deste servi�o
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void desbloquearRemoverNota (NotaCirculacao nota) throws ArqException{
		prepareMovimento(SigaaListaComando.REMOVER_NOTA_MATERIAL_INFORMACIONAL);

		desbloquearRemoverNotaInterno(nota);
	}
	
	/**
	 * Desbloqueia um material informacional, fazendo com que ele fique novamente dispon�vel para empr�stimos.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	private void desbloquearRemoverNotaInterno (NotaCirculacao nota) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
		
		try {
			
			NotaCirculacao notaCirculacaoBloqueante = null;
			
			if (nota != null) {
				notaCirculacaoBloqueante = nota;
			} else {
				int idMaterial = getParameterInt("idMaterial");
				
				for (NotaCirculacao notaCirculacao : all) {  // Percorre os materiais da lista mostrada ao usu�rio at� achar o que foi selecionado
					
					if(notaCirculacao.getMaterial().getId() ==  idMaterial){
						notaCirculacaoBloqueante = notaCirculacao;
					}
					
				}
			}
			
			if(notaCirculacaoBloqueante != null){
				boolean isBloqueante = notaCirculacaoBloqueante.isBloquearMaterial();
				
				MovimentoModificarNotaCirculacao mov =  new MovimentoModificarNotaCirculacao(new ArrayList<NotaCirculacao>(Arrays.asList(new NotaCirculacao[] { notaCirculacaoBloqueante })), new ArrayList<MaterialInformacional>(Arrays.asList(new MaterialInformacional[] { notaCirculacaoBloqueante.getMaterial() })));
				mov.setCodMovimento(SigaaListaComando.REMOVER_NOTA_MATERIAL_INFORMACIONAL);
				
				execute(mov);
				
				if (isBloqueante) {
					addMensagemInformation("Material " + notaCirculacaoBloqueante.getMaterial().getCodigoBarras() + " desbloqueado com sucesso !");
				} else {
					addMensagemInformation("Nota de Circula��o do material "+notaCirculacaoBloqueante.getMaterial().getCodigoBarras()+" removida com sucesso !");
				}
				
				// se debloquear algum material, prepara para desbloquear o pr�ximo //
				prepareMovimento( SigaaListaComando.REMOVER_NOTA_MATERIAL_INFORMACIONAL ); 
				
			}else{
				addMensagemWarning("Selecione um Material para desbloquear.");
			}
			
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			ne.printStackTrace();
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

	public List<MaterialInformacional> getMateriaisParaInclusaoNota() {
		return materiaisParaInclusaoNota;
	}

	public void setMateriaisParaInclusaoNota(List<MaterialInformacional> materiaisParaInclusaoNota) {
		this.materiaisParaInclusaoNota = materiaisParaInclusaoNota;
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

	public NotaCirculacao getNotaCirculacaoEdicao() {
		return notaCirculacaoEdicao;
	}
	
}