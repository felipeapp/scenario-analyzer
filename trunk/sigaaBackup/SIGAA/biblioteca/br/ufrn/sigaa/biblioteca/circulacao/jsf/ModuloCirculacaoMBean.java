/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *  <p>Classe que gerencia as operações de circulação (Empréstimo, Renovação e Devolução)
 * de materiais na web.</p>
 *  <p><i>( O operador pode realizar todas as operações do módulo circulação desktop e mais os emprétimo 
 *  <strong>institucionais</strong> e <strong>personalizados</strong> )</i></p>
 * 
 * @author Fred_Castro
 *
 */

@Component ("moduloCirculacaoMBean")
@Scope ("request")
public class ModuloCirculacaoMBean extends SigaaAbstractController <Emprestimo> implements PesquisarUsuarioBiblioteca{
	
	/** O formulário de realização dos empréstimos */
	public static final String PAGINA_EMPRESTIMO = "/biblioteca/circulacao/emprestimo.jsp";
	
	/** O formulário de renovação dos empréstimos */
	public static final String PAGINA_RENOVACAO = "/biblioteca/circulacao/renovacao.jsp";
	
	/** O formulário de devolução dos empréstimos */
	public static final String PAGINA_DEVOLUCAO = "/biblioteca/circulacao/devolucao.jsp";
	
	/** A página com o comprovante de renovação para impressão */
	public static final String PAGINA_COMPROVANTE_RENOVACAO = "/biblioteca/circulacao/comprovanteRenovacao.jsp";
	
	/** A página de comprovante de renovação */
	public static final String PAGINA_COMPROVANTE_DEVOLUCAO = "/biblioteca/circulacao/comprovanteDevolucao.jsp";
	
	/** A operação que vai ser realizado */
	public static final int OPERACAO_EMPRESTAR = 1;
	
	/** A operação que vai ser realizado */
	public static final int OPERACAO_RENOVAR = 2;
	
	/** A operação que vai ser realizado */
	public static final int OPERACAO_DEVOLVER = 3;
	
	/** O material que vai ser emprestado */
	private MaterialInformacional material;
	
	/** O código de barra digitado pelo usuário */
	private String codigoBarras;
	
	/** O usuário biblioteca do usuário */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/** As informações do usuário */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/** Os empréstimos ativos do usuário, só para mostrar quais empréstimos o usuário tem ativo no momento de relizar um novo. */
	private List <Emprestimo> emprestimosAtivos;
	
	/** Os dias que vãos ser dados ao empréstimo. Utilizado apenas no caso do empréstimo personalizado, caso contrário esse campo é ignorado */
	private Integer diasAEmprestar;
	
	/** A situação em que o usuário se encontra, pode emprestar, está suspenso, etc ... */
	private List<SituacaoUsuarioBiblioteca> situacoesUsuario;
	
	/** A senha digitada pelo usuário a quem o material será emprestado */
	private String senhaBiblioteca = "";
	
	/** A data quando o usuário vai deixar de ser suspenso */
	private Date fimSuspensao = null;
	
	
	/** A operação que vai ser feita*/
	private int operacao;
	
	/** O tipo de empréstimo escolhido */
	private Integer idTipoEmprestimo;
	
	/** Os tipos de empréstimos que o usuário pode fazer */
	private List <TipoEmprestimo> tiposEmprestimos;
	
	/** Guarda o emprétimo do material selecionado. */
	private Emprestimo emprestimoDoMaterial;
	
	/** Guarda o emprétimo que foi devolvido para poder emitir o comprovante de devolução. Usa um DTO porque é compartinhado com a devolução desktop*/
	private EmprestimoDto emprestimoDevolvido;
	
	/** Mensagem mostradas no comprovante de devolução, dependem da estratégia de punição: usuário suspenso até... usuário multado em ...*/
	private List<String> mensagensComprovanteDevolucao;
	
	/** Se foi feito alguma devolução, habilita a impressão do comprovante */
	private boolean habilitaComprovanteRenovacao = false;
	
	/** Se foi feito alguma devolução, habilita a impressão do comprovante */
	private boolean habilitaComprovante = false;
	
	/** Guardas as informações do usuário selecionado na busca padrão de usuários da biblioteca */
	private int idUsuarioSelecionadoBuscaPadrao;
	
	/** Guarda a lista de empréstimo renovados para poder emitir o comprovante de renovação */
	private List<OperacaoBibliotecaDto> emprestimosRenovadosOp;
	
	/** Guarda a descrição da política de emprétimo a ser utilizada para realiza o empréstimos */
	private String descricaoPoliticaASerUtilizada;
	
	public ModuloCirculacaoMBean (){}
	
	
	
	/**
	 * Exibe a página do módulo de circulação.
	 * Método chamado pela seguinte JSP: /biblioteca/menus/circulacao.jsp
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
				// Sem empréstimos para bibliotecas aqui.
				BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
				return pBean.iniciar(this, true, true, false, false, "Realizar Empréstimos", null);
			
			case OPERACAO_RENOVAR:
				usuarioBiblioteca = null;
			break;
			case OPERACAO_DEVOLVER:
				usuarioBiblioteca = null;
			break;
			default:
				addMensagemErro("Selecione uma operação.");
				return null;
		}
		
		return telaCirculacao();
			
		
	}
	
	/** Zera os dados antes de começar um novo empréstimo.
	 * 
	 *  <p> Método não chamado por nenhuma página jsp.</p>
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
	 * Preenche o usuário de acordo com o id passado.
	 * Chamado no método iniciar(); 
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 * @throws NegocioException 
	 */
	private void verificaSituacaoUsuario () throws DAOException, SegurancaException, NegocioException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if(usuarioBiblioteca == null){
			throw new NegocioException("Nenhum usuário selecionado");
		} 
			
		situacoesUsuario = new ArrayList<SituacaoUsuarioBiblioteca>();
		
		UsuarioBibliotecaDao daoUsuario = null;
		
		VerificaSituacaoUsuarioBibliotecaUtil.verificaVinculoUtilizadoAtivo(usuarioBiblioteca);
	
		try {	
			
			infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioBiblioteca, null, null);
			
			daoUsuario = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
			
			String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioBiblioteca);
			
			if(motivoBloqueio != null){ // se está bloqueado não vai conseguir fazer emprétimos, então não precisa verificar o resto
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
	 *  Verifica se o usuário está impedido de realizar emprestios
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Busca por um material informacional de acordo com seu código de barras. <br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /biblioteca/circulacao/moduloCirculacao.jsp
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
			addMensagemErro("Digite o código de barras.");
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
					
					addMensagemInformation("Material encontrado e está emprestado.");
					
					emprestimoDoMaterial = eDao.findEmAbertoByCodigoBarras(material.getCodigoBarras());
					
					if (emprestimoDoMaterial == null)
						throw new NegocioException ("Ocorreu uma inconsistência no banco. Apesar deste material estar com situação emprestado, não há um empréstimo aberto para ele.");
					
					if(emprestimoDoMaterial.getPoliticaEmprestimo().getTipoEmprestimo().isInstitucional()){
						throw new NegocioException ("Empréstimo "+emprestimoDoMaterial.getPoliticaEmprestimo().getTipoEmprestimo().getDescricao()+" não é suportado nesta operação.");
					}
					
					idTipoEmprestimo = emprestimoDoMaterial.getPoliticaEmprestimo().getTipoEmprestimo().getId();
					
					// NO MOMENTO DA RENOVAÇÃO E DEVOLUÇÃO CONFIGURAR COM O TIPO DE USUÀRIO ATUAL DELE
					if(operacao == OPERACAO_DEVOLVER || operacao == OPERACAO_RENOVAR){
						usuarioBiblioteca = emprestimoDoMaterial.getUsuarioBiblioteca();
						infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioBiblioteca, null, null);
					}
					
				} else {
					emprestimoDoMaterial = null;
					
					if (operacao != OPERACAO_EMPRESTAR){
						addMensagemErro("O material de código de barras "+material.getCodigoBarras()+" não está emprestado.");
						material = null;
						return null;
					}
					
				}	
				
				// Na Renovação ou Devolução, mostra para o operador as informações da politica em empréstimo que foi usada //
				if(emprestimoDoMaterial != null ){ //usuarioBiblioteca != null && idTipoEmprestimo != null){
					descricaoPoliticaASerUtilizada = emprestimoDoMaterial.getPoliticaEmprestimo().getInformacoesPolitica(); 
				}
				
				// Recupera os tipos de empréstimos disponíveis para o usuário e material selecionados.
				if(infoUsuario.getVinculo().isPodeRealizarEmprestimos())
					tiposEmprestimos = (List<TipoEmprestimo>) teDao.findTiposEmprestimoByInformacoesPoliticas(material.getBiblioteca().getId(), infoUsuario.getVinculo()
							, material.getStatus().getId(), material.getTipoMaterial().getId(), true, false);
				
				if (tiposEmprestimos == null || tiposEmprestimos.isEmpty())
					addMensagemErro("Não há tipos de empréstimos cadastrados para este tipo de usuário.");
			} else
				addMensagemErro ("Material não encontrado.");
				
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
	 * Método chamado quando o usuário escolher o tipo de empréstimo para o usuário visualizar a política o usuário vai utilizar.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				descricaoPoliticaASerUtilizada  = "Não há uma Política de Empréstimo ativa para o material de código de barras: "+ material.getCodigoBarras();
			}else{
				descricaoPoliticaASerUtilizada = descricoes.get(0);
			}
		}
	}
	
	
	
	
	
	
	/**
	 * Encaminha para a JSP referente a operação corrente.
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
	 * Empresta o material ao usuário ativo. <br/>
	 * Método chamado pela seguinte JSP: /biblioteca/circulacao/moduloCirculacao.jsp
	 * 
	 * @param e
	 * @throws ArqException 
	 */
	private String emprestar () throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		
		if (usuarioBiblioteca == null || usuarioBiblioteca.getId() <= 0)
			addMensagemErro("Escolha um usuário.");
		
		if (material == null)
			addMensagemErro("Escolha um material.");
		
		if (StringUtils.isEmpty(senhaBiblioteca))
			addMensagemErro("Informe a senha da biblioteca para realizar o empréstimo");
		
		if (idTipoEmprestimo == null || idTipoEmprestimo <= 0)
			addMensagemErro ("Selecione um tipo de empréstimo.");
		else {
			
			
			// Se é empréstimo personalizável, precisa confirmar com senha.
			if (idTipoEmprestimo != null && idTipoEmprestimo.equals(getIdTipoEmprestimoPersonalizado()) ){
				if (diasAEmprestar == null || diasAEmprestar <= 0)
					addMensagemErro("Informe a quantidade de dias a emprestar. Esta deve ser superior a zero.");
				
				confirmaSenha();
			}
		}
		
		if (!hasErrors()){
			
			Map <Integer, Integer> map = new HashMap <Integer, Integer> ();
			map.put(material.getId(), idTipoEmprestimo);
			
			// sempre passa a senha do usuário biblioteca, porque o módulo web não precisa pedir a senha do usuário //
			MovimentoRealizaEmprestimo mov = new MovimentoRealizaEmprestimo(map, usuarioBiblioteca, UFRNUtils.toMD5(senhaBiblioteca), diasAEmprestar, null); 
			
			
			try {
				RetornoOperacoesCirculacaoDTO retorno = execute(mov);
				
				material = null;
				
				for (String mensagem : retorno.mensagemAosUsuarios) {
					addMensagemWarning(mensagem);
				}
				
				for (OperacaoBibliotecaDto emprestimos : retorno.operacoesRealizadas) {
					addMensagemInformation(emprestimos.infoMaterial+"<br/><br/> Prazo para Devolução: "+emprestimos.getPrazoFormatado());
				}
				
				// atualiza os empréstimos ativos do usuário //
				atualizaEmprestimosAtivosDoUsuario();
				
				verificaSituacaoUsuario();
				
				addMensagemInformation("Empréstimo realizado com sucesso!");
				
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			}
		}
		
		return null;
	}


	/**
	 * Atualiza as informações do usuário a cada empréstimo.
	 * @throws DAOException
	 *  
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
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
	 * Renova o empréstimo do material.
	 * Método chamado pela seguinte JSP: /biblioteca/circulacao/moduloCirculacao.jsp
	 * 
	 * @param e
	 * @throws ArqException 
	 */
	private String renovar () throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (material == null)
			addMensagemErro("Escolha um material.");
		else if (!material.isEmprestado())
			addMensagemErro("O material selecionado não está emprestado");
		else if (emprestimoDoMaterial == null)
			addMensagemErro("Ocorreu uma inconsistência no banco. Apesar deste material estar com situação emprestado, não há um empréstimo aberto para ele.");
		
		if(StringUtils.isEmpty(senhaBiblioteca))
			addMensagemErro("Informe a senha da biblioteca para renovar o empréstimo.");
		
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
					addMensagemInformation(renovacoes.infoMaterial+"<br/><br/> Prazo para Devolução: "+renovacoes.getPrazoFormatado());
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
	 *  Exibe o código de autenticação na página do comprovante.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método chamado pela seguinte JSP: /biblioteca/circulacao/moduloCirculacao.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private String devolver () throws ArqException{
		
			if (material == null)
				addMensagemErro("Informe um material.");
			else if (!material.isEmprestado())
				addMensagemErro("O material selecionado não está emprestado");
			else if (emprestimoDoMaterial == null)
				addMensagemErro("Ocorreu uma inconsistência no banco. Apesar deste material estar com situação emprestado, não há um empréstimo aberto para ele.");
	
			if (!hasErrors()){
				
				MovimentoDevolveEmprestimo mov = new MovimentoDevolveEmprestimo(material.getId(), getUsuarioLogado(), null);
				
				try {
					
					RetornoOperacoesCirculacaoDTO retorno = execute(mov);
					
					
					// Mostra mensagem cadastradas para serem mostradas no momento da devolução por meio de notas de circulação.  //  
					for (String mensagem : retorno.mensagemAosUsuarios) {
						addMensagemWarning(mensagem);
					}
					
					// Mostra detalhes do material devolvido //  
					if(retorno.getOperacoesRealizadas() != null){
						for (OperacaoBibliotecaDto devolucao : retorno.getOperacoesRealizadas()) {
							addMensagemInformation("<strong> Prazo: </strong>"+ devolucao.getPrazoFormatado()
									+ "<br/>"+" <strong> Data da Devolução: </strong>"+ devolucao.getDataRealizacaoFormatada()
									+ "<br/><br/>"+devolucao.getInfoMaterial()+ " ");
						}
					}
					
					// Mostra mensagem de multa e suspensão, se o usuário devolveu com atrazo //  
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
	 * Redireciona para a página do comprovante de renovação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/renovacao.jsp</li>
	 *   </ul>
	 *
	 *  
	 *
	 * @return
	 */
	public String geraComprovanteRenovacao(){
		getCurrentRequest().setAttribute("liberaEmissaoComprovante", true); // para impedir que o usuário tente acessar diretamente a página
		// vários MBean podem emitir o comprovante de renovação, o nome na página está padronizado
		getCurrentRequest().setAttribute("_mBeanRealizouRenovacao", this); 
		return telaComprovanteRenovacao();
	}
	
	
	
	/**
	 * 
	 * Redireciona para a página do comprovante de devolução.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/devolucao.jsp</li>
	 *   </ul>
	 *
	 *  
	 *
	 * @return
	 */
	public String geraComprovanteDevolucao(){
		getCurrentRequest().setAttribute("liberaEmissaoComprovante", true); // para impedir que o usuário tente acessar diretamente a página
		// vários MBean podem emitir o comprovante de devolução, o nome na página está padronizado
		getCurrentRequest().setAttribute("_mBeanRealizouDevolucao", this); 
		return telaComprovanteDevolucao();
	}
	
	
	
	/**
	 *   Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	public String telaComprovanteDevolucao() {
		return forward(PAGINA_COMPROVANTE_DEVOLUCAO);
	}


	/**
	 * Redireciona para a tela de impressão do comprovante de renovação.
	 * 
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaComprovanteRenovacao(){
		return forward(PAGINA_COMPROVANTE_RENOVACAO);
	}

	/**
	 * Realiza a operação selecionada.
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemErro("A operação foi cancelada. Reinicie o processo.");
				return forward("/biblioteca/index.jsp");
		}
		
		return null;
	}
	
	

	/////////////////////////////  Métodos da Interface de Busca  ////////////////////////////////////
	
	
	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		if (idUsuarioSelecionadoBuscaPadrao == 0){
			addMensagemErro("Um usuário deve ser selecionado");
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
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}
	

	/**
	 *  Ver comentários da classe pai.<br/>
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
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 
	 * Volta para a busca do usuário
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Isso é um método set
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
	 * Retorna o id do tipo de empréstimo personalizado no banco.  Para verificar na página se deve ativar o campo para o usuário informar 
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