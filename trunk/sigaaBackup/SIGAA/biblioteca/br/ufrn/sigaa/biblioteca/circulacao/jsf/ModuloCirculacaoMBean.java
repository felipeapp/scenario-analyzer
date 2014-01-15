/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/07/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoDevolveEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoRealizaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoRenovaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.EmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.mensagens.MensagensBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * 
 *  <p>Classe que gerencia as opera��es de circula��o (Empr�stimo, Renova��o e Devolu��o)
 * de materiais na web.</p>
 *  <p><i>( O operador pode realizar todas as opera��es do m�dulo circula��o desktop e mais os empr�timo 
 *  <strong>institucionais</strong> e <strong>personalizados</strong> )</i></p>
 * 
 * @author Fred_Castro
 *
 */

@Component ("moduloCirculacaoMBean")
@Scope ("request")
public class ModuloCirculacaoMBean extends SigaaAbstractController <Emprestimo> implements PesquisarUsuarioBiblioteca{
	
	/** O formul�rio de realiza��o dos empr�stimos */
	public static final String PAGINA_EMPRESTIMO = "/biblioteca/circulacao/emprestimo.jsp";
	
	/** O formul�rio de renova��o dos empr�stimos */
	public static final String PAGINA_RENOVACAO = "/biblioteca/circulacao/renovacao.jsp";
	
	/** O formul�rio de devolu��o dos empr�stimos */
	public static final String PAGINA_DEVOLUCAO = "/biblioteca/circulacao/devolucao.jsp";
	
	/** A p�gina com o comprovante de renova��o para impress�o */
	public static final String PAGINA_COMPROVANTE_RENOVACAO = "/biblioteca/circulacao/comprovanteRenovacao.jsp";
	
	/** A p�gina de comprovante de renova��o */
	public static final String PAGINA_COMPROVANTE_DEVOLUCAO = "/biblioteca/circulacao/comprovanteDevolucao.jsp";
	
	/** A opera��o que vai ser realizado */
	public static final int OPERACAO_EMPRESTAR = 1;
	
	/** A opera��o que vai ser realizado */
	public static final int OPERACAO_RENOVAR = 2;
	
	/** A opera��o que vai ser realizado */
	public static final int OPERACAO_DEVOLVER = 3;
	
	/** O material que vai ser emprestado */
	private MaterialInformacional material;
	
	/** O c�digo de barra digitado pelo usu�rio */
	private String codigoBarras;
	
	/** O usu�rio biblioteca do usu�rio */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/** As informa��es do usu�rio */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/** Os empr�stimos ativos do usu�rio, s� para mostrar quais empr�stimos o usu�rio tem ativo no momento de relizar um novo. */
	private List <Emprestimo> emprestimosAtivos;
	
	/** Os dias que v�os ser dados ao empr�stimo. Utilizado apenas no caso do empr�stimo personalizado, caso contr�rio esse campo � ignorado */
	private Integer diasAEmprestar;
	
	/** A situa��o em que o usu�rio se encontra, pode emprestar, est� suspenso, etc ... */
	private List<SituacaoUsuarioBiblioteca> situacoesUsuario;
	
	/** A senha digitada pelo usu�rio a quem o material ser� emprestado */
	private String senhaBiblioteca = "";
	
	/** A data quando o usu�rio vai deixar de ser suspenso */
	private Date fimSuspensao = null;
	
	
	/** A opera��o que vai ser feita*/
	private int operacao;
	
	/** O tipo de empr�stimo escolhido */
	private Integer idTipoEmprestimo;
	
	/** Os tipos de empr�stimos que o usu�rio pode fazer */
	private List <TipoEmprestimo> tiposEmprestimos;
	
	/** Guarda o empr�timo do material selecionado. */
	private Emprestimo emprestimoDoMaterial;
	
	/** Guarda o empr�timo que foi devolvido para poder emitir o comprovante de devolu��o. Usa um DTO porque � compartinhado com a devolu��o desktop*/
	private EmprestimoDto emprestimoDevolvido;
	
	/** Mensagem mostradas no comprovante de devolu��o, dependem da estrat�gia de puni��o: usu�rio suspenso at�... usu�rio multado em ...*/
	private List<String> mensagensComprovanteDevolucao;
	
	/** Se foi feito alguma devolu��o, habilita a impress�o do comprovante */
	private boolean habilitaComprovanteRenovacao = false;
	
	/** Se foi feito alguma devolu��o, habilita a impress�o do comprovante */
	private boolean habilitaComprovante = false;
	
	/** Guardas as informa��es do usu�rio selecionado na busca padr�o de usu�rios da biblioteca */
	private int idUsuarioSelecionadoBuscaPadrao;
	
	/** Guarda a lista de empr�stimo renovados para poder emitir o comprovante de renova��o */
	private List<OperacaoBibliotecaDto> emprestimosRenovadosOp;
	
	/** Guarda a descri��o da pol�tica de empr�timo a ser utilizada para realiza o empr�stimos */
	private String descricaoPoliticaASerUtilizada;
	
	public ModuloCirculacaoMBean (){}
	
	
	
	/**
	 * Exibe a p�gina do m�dulo de circula��o.
	 * M�todo chamado pela seguinte JSP: /biblioteca/menus/circulacao.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar () throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
			
		
		usuarioBiblioteca = new UsuarioBiblioteca ();
		infoUsuario = null;
		emprestimosAtivos = new ArrayList <Emprestimo> ();
		codigoBarras = "";
		tiposEmprestimos = new ArrayList <TipoEmprestimo> ();
		
		zeradadosEmprestimo();
		
		operacao = getParameterInt("operacao", 0);
		
		switch (operacao){
			case OPERACAO_EMPRESTAR:
				// Sem empr�stimos para bibliotecas aqui.
				BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
				return pBean.iniciar(this, true, true, false, false, "Realizar Empr�stimos", null);
			
			case OPERACAO_RENOVAR:
				usuarioBiblioteca = null;
			break;
			case OPERACAO_DEVOLVER:
				usuarioBiblioteca = null;
			break;
			default:
				addMensagemErro("Selecione uma opera��o.");
				return null;
		}
		
		return telaCirculacao();
			
		
	}
	
	/** Zera os dados antes de come�ar um novo empr�stimo.
	 * 
	 *  <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public void zeradadosEmprestimo(){
	
		habilitaComprovante = false;
		habilitaComprovanteRenovacao = false;
		material = null;
		descricaoPoliticaASerUtilizada = "";
		idTipoEmprestimo = null;
		diasAEmprestar = 30;
		
	}
	
	
	/**
	 * Preenche o usu�rio de acordo com o id passado.
	 * Chamado no m�todo iniciar(); 
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 * @throws NegocioException 
	 */
	private void verificaSituacaoUsuario () throws DAOException, SegurancaException, NegocioException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if(usuarioBiblioteca == null){
			throw new NegocioException("Nenhum usu�rio selecionado");
		} 
			
		situacoesUsuario = new ArrayList<SituacaoUsuarioBiblioteca>();
		
		UsuarioBibliotecaDao daoUsuario = null;
		
		VerificaSituacaoUsuarioBibliotecaUtil.verificaVinculoUtilizadoAtivo(usuarioBiblioteca);
	
		try {	
			
			infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioBiblioteca, null, null);
			
			daoUsuario = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
			
			String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioBiblioteca);
			
			if(motivoBloqueio != null){ // se est� bloqueado n�o vai conseguir fazer empr�timos, ent�o n�o precisa verificar o resto
				SituacaoUsuarioBiblioteca situacaoBloqueio = SituacaoUsuarioBiblioteca.ESTA_BLOQUEADO;
				situacaoBloqueio.setDescricaoCompleta(situacaoBloqueio.getDescricaoResumida()+" Motivo: "+motivoBloqueio );
				situacoesUsuario.add(situacaoBloqueio);
			}else{
			
				situacoesUsuario.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(
						usuarioBiblioteca.getIdentificadorPessoa()
						, usuarioBiblioteca.getIdentificadorBiblioteca() ));
				situacoesUsuario.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosEmAbertoOUAtrasadosBiblioteca(usuarioBiblioteca.getId()) );
			
				if(situacoesUsuario.isEmpty()){
					situacoesUsuario.add(SituacaoUsuarioBiblioteca.SEM_PENDENCIA);
				}
			}
		} finally{
			if (daoUsuario != null)
				daoUsuario.close();
		}

	}
	
	
	/**
	 * 
	 *  Verifica se o usu�rio est� impedido de realizar emprestios
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/emprestimos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public boolean isUsuarioImpedidoFazerEmpretimos(){
		
		if( usuarioBiblioteca.getVinculo() != null && ! usuarioBiblioteca.getVinculo().isPodeRealizarEmprestimos())
			return true;
		
		if(situacoesUsuario != null){
			
			for (SituacaoUsuarioBiblioteca situacao : situacoesUsuario) {
				if(situacao.isSituacaoImpedeEmprestimos())
					return true;
			}
			
		}
		
		return false;
	}
	
	
	/**
	 * Busca por um material informacional de acordo com seu c�digo de barras. <br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /biblioteca/circulacao/moduloCirculacao.jsp
	 * @param e
	 * @throws ArqException 
	 */
	public String buscarMaterial () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		switch (operacao){
			case OPERACAO_EMPRESTAR:
				prepareMovimento(SigaaListaComando.REALIZA_EMPRESTIMO);
				break;
			case OPERACAO_RENOVAR:
				prepareMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
			break;
			case OPERACAO_DEVOLVER:
				prepareMovimento(SigaaListaComando.DEVOLVE_EMPRESTIMO);
			break;
		}
		
		zeradadosEmprestimo();
		
		if (StringUtils.isEmpty(codigoBarras)){
			addMensagemErro("Digite o c�digo de barras.");
			return null;
		}
				
		MaterialInformacionalDao dao = null;
		EmprestimoDao eDao = null;
		TipoEmprestimoDao teDao = null;
		
		
		try {
			dao = getDAO(MaterialInformacionalDao.class);
			eDao = getDAO(EmprestimoDao.class);
			teDao = getDAO(TipoEmprestimoDao.class);
			
			material = dao.findMaterialAtivoByCodigoBarras(codigoBarras);
			
			if (material != null){
				material.setSituacao(dao.refresh(material.getSituacao()));
				material.setInformacao( BibliotecaUtil.obtemDadosMaterialInformacional(material.getId()) );
				
				if (material.isEmprestado()){
					
					addMensagemInformation("Material encontrado e est� emprestado.");
					
					emprestimoDoMaterial = eDao.findEmAbertoByCodigoBarras(material.getCodigoBarras());
					
					if (emprestimoDoMaterial == null)
						throw new NegocioException ("Ocorreu uma inconsist�ncia no banco. Apesar deste material estar com situa��o emprestado, n�o h� um empr�stimo aberto para ele.");
					
					if(emprestimoDoMaterial.getPoliticaEmprestimo().getTipoEmprestimo().isInstitucional()){
						throw new NegocioException ("Empr�stimo "+emprestimoDoMaterial.getPoliticaEmprestimo().getTipoEmprestimo().getDescricao()+" n�o � suportado nesta opera��o.");
					}
					
					idTipoEmprestimo = emprestimoDoMaterial.getPoliticaEmprestimo().getTipoEmprestimo().getId();
					
					// NO MOMENTO DA RENOVA��O E DEVOLU��O CONFIGURAR COM O TIPO DE USU�RIO ATUAL DELE
					if(operacao == OPERACAO_DEVOLVER || operacao == OPERACAO_RENOVAR){
						usuarioBiblioteca = emprestimoDoMaterial.getUsuarioBiblioteca();
						infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioBiblioteca, null, null);
					}
					
				} else {
					emprestimoDoMaterial = null;
					
					if (operacao != OPERACAO_EMPRESTAR){
						addMensagemErro("O material de c�digo de barras "+material.getCodigoBarras()+" n�o est� emprestado.");
						material = null;
						return null;
					}
					
				}	
				
				// Na Renova��o ou Devolu��o, mostra para o operador as informa��es da politica em empr�stimo que foi usada //
				if(emprestimoDoMaterial != null ){ //usuarioBiblioteca != null && idTipoEmprestimo != null){
					descricaoPoliticaASerUtilizada = emprestimoDoMaterial.getPoliticaEmprestimo().getInformacoesPolitica(); 
				}
				
				// Recupera os tipos de empr�stimos dispon�veis para o usu�rio e material selecionados.
				if(infoUsuario.getVinculo().isPodeRealizarEmprestimos())
					tiposEmprestimos = (List<TipoEmprestimo>) teDao.findTiposEmprestimoByInformacoesPoliticas(material.getBiblioteca().getId(), infoUsuario.getVinculo()
							, material.getStatus().getId(), material.getTipoMaterial().getId(), true, false);
				
				if (tiposEmprestimos == null || tiposEmprestimos.isEmpty())
					addMensagemErro("N�o h� tipos de empr�stimos cadastrados para este tipo de usu�rio.");
			} else
				addMensagemErro ("Material n�o encontrado.");
				
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		} finally {
			if (dao != null)dao.close();
			if (eDao != null) eDao.close();
			if (teDao != null) teDao.close();
			
		}
		
		codigoBarras = "";
		
		return null;
	}
	
	/**
	 * 
	 * M�todo chamado quando o usu�rio escolher o tipo de empr�stimo para o usu�rio visualizar a pol�tica o usu�rio vai utilizar.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/emprestimo.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void mostraInformacaoPoliticaEmprestimo(ActionEvent evt) throws DAOException{
		if (material != null){
			
			List<String> descricoes = CirculacaoUtil.retornaDescricaoPoliticaEmpretimoASerUtilizada(
						material.getBiblioteca().getId(), 
						usuarioBiblioteca.getVinculo(), 
						material.getStatus().getId(),
						material.getTipoMaterial().getId(),
						idTipoEmprestimo);
			if(descricoes.size() == 0){
				descricaoPoliticaASerUtilizada  = "N�o h� uma Pol�tica de Empr�stimo ativa para o material de c�digo de barras: "+ material.getCodigoBarras();
			}else{
				descricaoPoliticaASerUtilizada = descricoes.get(0);
			}
		}
	}
	
	
	
	
	
	
	/**
	 * Encaminha para a JSP referente a opera��o corrente.
	 * @return
	 */
	private String telaCirculacao(){
		switch (operacao){
			case OPERACAO_EMPRESTAR: return forward(PAGINA_EMPRESTIMO);
			case OPERACAO_RENOVAR: return forward(PAGINA_RENOVACAO);
			case OPERACAO_DEVOLVER: return forward(PAGINA_DEVOLUCAO);
		}
		
		return null;
	}
	
	
	
	/**
	 * Empresta o material ao usu�rio ativo. <br/>
	 * M�todo chamado pela seguinte JSP: /biblioteca/circulacao/moduloCirculacao.jsp
	 * 
	 * @param e
	 * @throws ArqException 
	 */
	private String emprestar () throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		
		if (usuarioBiblioteca == null || usuarioBiblioteca.getId() <= 0)
			addMensagemErro("Escolha um usu�rio.");
		
		if (material == null)
			addMensagemErro("Escolha um material.");
		
		if (StringUtils.isEmpty(senhaBiblioteca))
			addMensagemErro("Informe a senha da biblioteca para realizar o empr�stimo");
		
		if (idTipoEmprestimo == null || idTipoEmprestimo <= 0)
			addMensagemErro ("Selecione um tipo de empr�stimo.");
		else {
			
			
			// Se � empr�stimo personaliz�vel, precisa confirmar com senha.
			if (idTipoEmprestimo != null && idTipoEmprestimo.equals(getIdTipoEmprestimoPersonalizado()) ){
				if (diasAEmprestar == null || diasAEmprestar <= 0)
					addMensagemErro("Informe a quantidade de dias a emprestar. Esta deve ser superior a zero.");
				
				confirmaSenha();
			}
		}
		
		if (!hasErrors()){
			
			Map <Integer, Integer> map = new HashMap <Integer, Integer> ();
			map.put(material.getId(), idTipoEmprestimo);
			
			// sempre passa a senha do usu�rio biblioteca, porque o m�dulo web n�o precisa pedir a senha do usu�rio //
			MovimentoRealizaEmprestimo mov = new MovimentoRealizaEmprestimo(map, usuarioBiblioteca, UFRNUtils.toMD5(senhaBiblioteca), diasAEmprestar, null); 
			
			
			try {
				RetornoOperacoesCirculacaoDTO retorno = execute(mov);
				
				material = null;
				
				for (String mensagem : retorno.mensagemAosUsuarios) {
					addMensagemWarning(mensagem);
				}
				
				for (OperacaoBibliotecaDto emprestimos : retorno.operacoesRealizadas) {
					addMensagemInformation(emprestimos.infoMaterial+"<br/><br/> Prazo para Devolu��o: "+emprestimos.getPrazoFormatado());
				}
				
				// atualiza os empr�stimos ativos do usu�rio //
				atualizaEmprestimosAtivosDoUsuario();
				
				verificaSituacaoUsuario();
				
				addMensagemInformation("Empr�stimo realizado com sucesso!");
				
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			}
		}
		
		return null;
	}


	/**
	 * Atualiza as informa��es do usu�rio a cada empr�stimo.
	 * @throws DAOException
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/</li>
	 * </ul>
	 *
	 */
	private void atualizaEmprestimosAtivosDoUsuario()throws DAOException {
		
		if(usuarioBiblioteca == null)
			return;
		
		EmprestimoDao emprestimoDao = null;
		
		try {
			emprestimoDao = getDAO(EmprestimoDao.class);
			emprestimosAtivos = emprestimoDao.findEmprestimosByUsuarioSituacaoPeriodo(usuarioBiblioteca, false, null, null);
			
			for (Emprestimo e : emprestimosAtivos)
				e.getMaterial().setInformacao(BibliotecaUtil.obtemDadosMaterialInformacional(e.getMaterial().getId()));
		}finally{
			if( emprestimoDao != null) emprestimoDao.close(); 
		}
	}
	
	
	
	/**
	 * Renova o empr�stimo do material.
	 * M�todo chamado pela seguinte JSP: /biblioteca/circulacao/moduloCirculacao.jsp
	 * 
	 * @param e
	 * @throws ArqException 
	 */
	private String renovar () throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (material == null)
			addMensagemErro("Escolha um material.");
		else if (!material.isEmprestado())
			addMensagemErro("O material selecionado n�o est� emprestado");
		else if (emprestimoDoMaterial == null)
			addMensagemErro("Ocorreu uma inconsist�ncia no banco. Apesar deste material estar com situa��o emprestado, n�o h� um empr�stimo aberto para ele.");
		
		if(StringUtils.isEmpty(senhaBiblioteca))
			addMensagemErro("Informe a senha da biblioteca para renovar o empr�stimo.");
		
		emprestimosRenovadosOp = null;
		
		if (!hasErrors()){
			
			try {
				
				List <Integer> ms = new ArrayList <Integer> ();
				ms.add(material.getId());
				
				MovimentoRenovaEmprestimo mov = new MovimentoRenovaEmprestimo(ms, usuarioBiblioteca, UFRNUtils.toMD5(senhaBiblioteca));
				
				RetornoOperacoesCirculacaoDTO retorno =  execute(mov);
				
				material = null;
				
				for (String mensagem : retorno.mensagemAosUsuarios) {
					addMensagemWarning(mensagem);
				}
				
				emprestimosRenovadosOp = retorno.operacoesRealizadas;
				
				for (OperacaoBibliotecaDto renovacoes : emprestimosRenovadosOp) {
					addMensagemInformation(renovacoes.infoMaterial+"<br/><br/> Prazo para Devolu��o: "+renovacoes.getPrazoFormatado());
				}
				
				addMensagem(MensagensBiblioteca.EMPRESTIMOS_RENOVADOS);
				
				habilitaComprovanteRenovacao = true;
				
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			}
			
		}
		
		return null;
	}
	
	
	
	
	/**
	 *  Exibe o c�digo de autentica��o na p�gina do comprovante.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>>/biblioteca/EmprestimoInstitucional/comprovanteRenovacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String getCodigoAutenticacaoRenovacao() throws DAOException{
		return CirculacaoUtil.getCodigoAutenticacaoRenovacao(emprestimosRenovadosOp);
	}
	
	
	
	
	/**
	 * Devolve o material selecionado.
	 * M�todo chamado pela seguinte JSP: /biblioteca/circulacao/moduloCirculacao.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private String devolver () throws ArqException{
		
			if (material == null)
				addMensagemErro("Informe um material.");
			else if (!material.isEmprestado())
				addMensagemErro("O material selecionado n�o est� emprestado");
			else if (emprestimoDoMaterial == null)
				addMensagemErro("Ocorreu uma inconsist�ncia no banco. Apesar deste material estar com situa��o emprestado, n�o h� um empr�stimo aberto para ele.");
	
			if (!hasErrors()){
				
				MovimentoDevolveEmprestimo mov = new MovimentoDevolveEmprestimo(material.getId(), getUsuarioLogado(), null);
				
				try {
					
					RetornoOperacoesCirculacaoDTO retorno = execute(mov);
					
					
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
					
					addMensagemInformation("Material devolvido com sucesso!");
				
						
					habilitaComprovante = true;
					
				    emprestimoDevolvido  = retorno.emprestimoRetornado;
				    mensagensComprovanteDevolucao = retorno.mensagensComprovanteDevolucao; 
				    
					material = null;
					
				} catch (NegocioException e){
					addMensagens(e.getListaMensagens());
				}
			}
		
		return null;
	}
	
	
	/**
	 * 
	 * Redireciona para a p�gina do comprovante de renova��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/renovacao.jsp</li>
	 *   </ul>
	 *
	 *  
	 *
	 * @return
	 */
	public String geraComprovanteRenovacao(){
		getCurrentRequest().setAttribute("liberaEmissaoComprovante", true); // para impedir que o usu�rio tente acessar diretamente a p�gina
		// v�rios MBean podem emitir o comprovante de renova��o, o nome na p�gina est� padronizado
		getCurrentRequest().setAttribute("_mBeanRealizouRenovacao", this); 
		return telaComprovanteRenovacao();
	}
	
	
	
	/**
	 * 
	 * Redireciona para a p�gina do comprovante de devolu��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/devolucao.jsp</li>
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
		return forward(PAGINA_COMPROVANTE_DEVOLUCAO);
	}


	/**
	 * Redireciona para a tela de impress�o do comprovante de renova��o.
	 * 
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaComprovanteRenovacao(){
		return forward(PAGINA_COMPROVANTE_RENOVACAO);
	}

	/**
	 * Realiza a opera��o selecionada.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/emprestmo.jsp</li>
	 *     <li>/sigaa.war/biblioteca/circulacao/renovacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/circulacao/devolucao.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String realizarOperacao () throws ArqException{
		switch (operacao){
			case OPERACAO_EMPRESTAR:
				emprestar();
			break;

			case OPERACAO_RENOVAR:
				renovar();
			break;
			
			case OPERACAO_DEVOLVER:
				devolver();
			break;
			default:
				addMensagemErro("A opera��o foi cancelada. Reinicie o processo.");
				return forward("/biblioteca/index.jsp");
		}
		
		return null;
	}
	
	

	/////////////////////////////  M�todos da Interface de Busca  ////////////////////////////////////
	
	
	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		if (idUsuarioSelecionadoBuscaPadrao == 0){
			addMensagemErro("Um usu�rio deve ser selecionado");
			return null;
		}
		
		habilitaComprovante = false; 
		habilitaComprovanteRenovacao = false;
		material = null;
		idTipoEmprestimo = null;
		
		infoUsuario = null;
		
		UsuarioBibliotecaDao dao = null;
		
		try {
			dao = getDAO(UsuarioBibliotecaDao.class);
			
			
			usuarioBiblioteca = dao.findInformacoesUsuarioBibliotecaNaoQuitado(idUsuarioSelecionadoBuscaPadrao);
			
			atualizaEmprestimosAtivosDoUsuario();
			verificaSituacaoUsuario();
			
			return telaCirculacao();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} finally {
			if (dao != null) dao.close();
		}
		
	}



	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}
	

	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
		if(parametroDePessoa)
			idUsuarioSelecionadoBuscaPadrao = Integer.valueOf(parametros[3]);
		else
			idUsuarioSelecionadoBuscaPadrao = Integer.valueOf(parametros[2]);
	}


	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 
	 * Volta para a busca do usu�rio
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/emprestimo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String voltaTelaBusca(){
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	
	
	//////  Sets  e Gets //////
	
	public MaterialInformacional getMaterial() {
		return material;
	}

	public void setMaterial(MaterialInformacional material) {
		this.material = material;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	/**
	 * Isso � um m�todo set
	 *
	 * @param codigoBarras
	 */
	public void setCodigoBarras(String codigoBarras) {
		if (!StringUtils.isEmpty(codigoBarras))
			this.codigoBarras = codigoBarras.trim().toUpperCase();
		else
			this.codigoBarras = codigoBarras;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {
		this.usuarioBiblioteca = usuarioBiblioteca;
	}

	public List<Emprestimo> getEmprestimosAtivos() {
		return emprestimosAtivos;
	}

	public void setEmprestimosAtivos(List<Emprestimo> emprestimosAtivos) {
		this.emprestimosAtivos = emprestimosAtivos;
	}

	public Integer getDiasAEmprestar() {
		return diasAEmprestar;
	}

	public void setDiasAEmprestar(Integer diasAEmprestar) {
		this.diasAEmprestar = diasAEmprestar;
	}

	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}

	public void setInfoUsuario(InformacoesUsuarioBiblioteca infoUsuario) {
		this.infoUsuario = infoUsuario;
	}

	public List<SituacaoUsuarioBiblioteca> getSituacoesUsuario() {
		return situacoesUsuario;
	}

	public void setSituacaoUsuario(List<SituacaoUsuarioBiblioteca> situacoesUsuario) {
		this.situacoesUsuario = situacoesUsuario;
	}

	public Date getFimSuspensao() {
		return fimSuspensao;
	}

	public void setFimSuspensao(Date fimSuspensao) {
		this.fimSuspensao = fimSuspensao;
	}

	

	public Integer getIdTipoEmprestimo() {
		return idTipoEmprestimo;
	}



	public void setIdTipoEmprestimo(Integer idTipoEmprestimo) {
		this.idTipoEmprestimo = idTipoEmprestimo;
	}



	public List <SelectItem> getTiposEmprestimos() {
		return toSelectItems(tiposEmprestimos, "id", "descricao");
	}
	
	/**
	 * Retorna o id do tipo de empr�stimo personalizado no banco.  Para verificar na p�gina se deve ativar o campo para o usu�rio informar 
	 * a quantidade de dias que o material vai ficar emprestado.
	 *
	 * @return
	 */
	public int getIdTipoEmprestimoPersonalizado() {
		
		if(tiposEmprestimos != null){
		
			for (TipoEmprestimo tipo : tiposEmprestimos) {
				
				if(tipo.isTipoEmprestimoPersonalizavel())
					return tipo.getId();
			}
		}
		return -1;
	}

	public Emprestimo getEmprestimoDoMaterial() {
		return emprestimoDoMaterial;
	}

	public void setEmprestimoDoMaterial(Emprestimo emprestimoDoMaterial) {
		this.emprestimoDoMaterial = emprestimoDoMaterial;
	}

	public void setTiposEmprestimos(List<TipoEmprestimo> tiposEmprestimos) {
		this.tiposEmprestimos = tiposEmprestimos;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public EmprestimoDto getEmprestimoDevolvido() {
		return emprestimoDevolvido;
	}

	public void setEmprestimoDevolvido(EmprestimoDto emprestimoDevolvido) {
		this.emprestimoDevolvido = emprestimoDevolvido;
	}

	public String getSenhaBiblioteca() {
		return senhaBiblioteca;
	}

	public void setSenhaBiblioteca(String senhaBiblioteca) {
		this.senhaBiblioteca = senhaBiblioteca;
	}

	public List<String> getMensagensComprovanteDevolucao() {
		return mensagensComprovanteDevolucao;
	}

	public void setMensagensComprovanteDevolucao(List<String> mensagensComprovanteDevolucao) {
		this.mensagensComprovanteDevolucao = mensagensComprovanteDevolucao;
	}


	public List<OperacaoBibliotecaDto> getEmprestimosRenovadosOp() {
		return emprestimosRenovadosOp;
	}

	public void setEmprestimosRenovadosOp(List<OperacaoBibliotecaDto> emprestimosRenovadosOp) {
		this.emprestimosRenovadosOp = emprestimosRenovadosOp;
	}

	public boolean isHabilitaComprovanteRenovacao() {
		return habilitaComprovanteRenovacao;
	}

	public void setHabilitaComprovanteRenovacao(boolean habilitaComprovanteRenovacao) {
		this.habilitaComprovanteRenovacao = habilitaComprovanteRenovacao;
	}

	public boolean isHabilitaComprovante() {
		return habilitaComprovante;
	}

	public void setHabilitaComprovante(boolean habilitaComprovante) {
		this.habilitaComprovante = habilitaComprovante;
	}

	public String getDescricaoPoliticaASerUtilizada() {
		return descricaoPoliticaASerUtilizada;
	}

	public void setDescricaoPoliticaASerUtilizada(String descricaoPoliticaASerUtilizada) {
		this.descricaoPoliticaASerUtilizada = descricaoPoliticaASerUtilizada;
	}
	
	
	
	
}