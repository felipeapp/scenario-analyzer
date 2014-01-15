/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/08/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InfoVinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>MBean criado para permitir aos bibliotecários e funcionários de circulação verificarem 
 * a situação dos vínculos dos usuários no sistema. E poderem constantar se o usuário pode ou não 
 * fazer empréstimos da biblioteca, e não ficar dependendo de consultas ao banco.
 * </p>
 *
 * 
 * @author jadson
 * @since 18/08/2011
 * @version 1.0 criação da classe
 */
@Component("verificaVinculosUsuarioBibliotecaMBean")
@Scope("request")
public class VerificaVinculosUsuarioBibliotecaMBean extends SigaaAbstractController<Object> implements PesquisarUsuarioBiblioteca{

	
	/**  Página onde o operador de circulação pode visualizar as inforamações sobre os vinculos do usuário selecionado no sistema. E verifica se ele 
	 * possui algum vínculo que permita fazer empréstimos na biblioteca. */
	public static final String PAGINA_MOSTRA_INFORMACOES_VINCULOS_USUARIO = "/biblioteca/circulacao/paginaMostraInforamacoesVinculosUsuario.jsp";
	
	
	
	
	/**
	 * Guarda o id da pessoa cujos vínculos serão verificados.
	 */
	private int idPessoaSelecionada;
	
	/**
	 * Guarda como o caso de uso está sendo acessado, pelo próprio usuário ou pelos funcionários de circulação
	 *  para saber para onde voltar depois de finalizado. Utilizado em PAGINA_MOSTRA_INFORMACOES_VINCULOS_USUARIO
	 */
	private boolean verificandoMeusVinculos = true;
	
	
	
	/** Informações sobre o usuário para ser mostrada da tela */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	
	/**
	 * A lista com as informações dos vinculos do usuário selecionado.  Cada arrya da lista é uma informação do usuário.
	 */
	private List<InfoVinculoUsuarioBiblioteca> infoVinculosDiscenteUsuario;
	
	/**
	 * A lista com as informações dos vinculos do usuário selecionado.  Cada arrya da lista é uma informação do usuário.
	 */
	private List<InfoVinculoUsuarioBiblioteca> infoVinculosServidorUsuario;
	
	/**
	 * A lista com as informações dos vinculos do usuário selecionado.  Cada arrya da lista é uma informação do usuário.
	 */
	private List<InfoVinculoUsuarioBiblioteca> infoVinculosDocenteExternoUsuario;
	
	/**
	 * A lista com as informações dos vinculos do usuário selecionado.  Cada arrya da lista é uma informação do usuário.
	 */
	private List<InfoVinculoUsuarioBiblioteca> infoVinculosUsuarioExternoUsuario;
	
	
	
	
	
	
	
	/**
	 *  <p>Inicia o caso de uso de verificar os vínculos do usuário utilizados pelos funcionários de circulação.. </p>
	 *  
	 *  <p>Só suporta a verificação de vínculos de pessoas, não bibliotecas. Até porque biblioteca não tem mais de um vínculo no sistema.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul>
	 *
	 *
	 *
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String iniciarVerificacao() throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
		
		verificandoMeusVinculos = false;
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, false, true, "Verificando os Vínculos do Usuário", null);
	}
	
	
	
	/**
	 *  <p>Inicia o caso de uso para o próprio usuário verificar seus vínculos com a UFRN e se eles permite fazer empréstimos ou não. </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 * 	   <li> /sigaa.war/portais/discente/menu_discente.jsp
	 *     <li> /sigaa.war/portais/docente/menu_docente.jsp
	 *     <li> /sigaa.war/biblioteca/menus/modulo_biblioteca.jsp      
	 *   </ul>
	 *
	 *
	 *
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String iniciarVerificacaoMeusVinculos() throws DAOException, SegurancaException{
		
		this.idPessoaSelecionada = getUsuarioLogado().getPessoa().getId();
		verificandoMeusVinculos = true;
		
		try{
			infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, idPessoaSelecionada, null);
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		verificaInformacoesVinculosUsuario();
		
		return telaMostraInformacoesVinculosUsuario();
	}
	
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * @throws DAOException 
	 * @throws  
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		idPessoaSelecionada = p.getId();
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		// Não usado
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
		// Não usado
	}

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		try{
			
			infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, idPessoaSelecionada, null);
			
			verificaInformacoesVinculosUsuario();
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}catch (DAOException e) {
			addMensagemErro("Erro ao consultas as informações dos vínculos do usuário");
			return null;
		}
		
		return telaMostraInformacoesVinculosUsuario();
	}
	
	
	
	
	
	/**
	 *  Verifica para todos os vínculos do usuário, se eles permitem fazer empréstimos na biblioteca ou não.
	 *  
	 *   
	 * @throws DAOException 
	 *
	 */
	private void verificaInformacoesVinculosUsuario() throws DAOException {
		
		infoVinculosDiscenteUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().montaInformacoesVinculoDiscenteBiblioteca(idPessoaSelecionada);
		infoVinculosServidorUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().montaInformacoesVinculoServidorBiblioteca(idPessoaSelecionada);
		infoVinculosDocenteExternoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().montaInformacoesVinculoDocenteExternoBiblioteca(idPessoaSelecionada);
		infoVinculosUsuarioExternoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().montaInformacoesVinculoUsuarioExternoBiblioteca(idPessoaSelecionada);			
		
		/* Verifica se os vínculo estão quintados */
		
		UsuarioBibliotecaDao dao = null;
		
		try {
			dao = getDAO(UsuarioBibliotecaDao.class);
			
			/** Todas as contas que o usuário já teve na biblioteca algum dia */
			List<UsuarioBiblioteca> contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByPessoa(idPessoaSelecionada );
		
			for (UsuarioBiblioteca usuarioBiblioteca : contasUsuarioBiblioteca) {
				
				InfoVinculoUsuarioBiblioteca infoDiscente = retornaVinculo(infoVinculosDiscenteUsuario, new InfoVinculoUsuarioBiblioteca(usuarioBiblioteca.getVinculo(), usuarioBiblioteca.getIdentificacaoVinculo()) );
			
				if(infoDiscente != null)
					infoDiscente.setQuitado(usuarioBiblioteca.isQuitado());
				
				InfoVinculoUsuarioBiblioteca infoServidor = retornaVinculo(infoVinculosServidorUsuario, new InfoVinculoUsuarioBiblioteca(usuarioBiblioteca.getVinculo(), usuarioBiblioteca.getIdentificacaoVinculo()) );
				
				if(infoServidor != null)
					infoServidor.setQuitado(usuarioBiblioteca.isQuitado());
				
				InfoVinculoUsuarioBiblioteca infoDocenteExterno = retornaVinculo(infoVinculosDocenteExternoUsuario, new InfoVinculoUsuarioBiblioteca(usuarioBiblioteca.getVinculo(), usuarioBiblioteca.getIdentificacaoVinculo()) );
				
				if(infoDocenteExterno != null)
					infoDocenteExterno.setQuitado(usuarioBiblioteca.isQuitado());
				
				InfoVinculoUsuarioBiblioteca infoUsuarioExterno = retornaVinculo(infoVinculosUsuarioExternoUsuario, new InfoVinculoUsuarioBiblioteca(usuarioBiblioteca.getVinculo(), usuarioBiblioteca.getIdentificacaoVinculo()) );
				
				if(infoUsuarioExterno != null)
					infoUsuarioExterno.setQuitado(usuarioBiblioteca.isQuitado());
			}
			
		} finally {
			if (dao != null)  dao.close();
		}
		
	}
	
	private InfoVinculoUsuarioBiblioteca retornaVinculo(List<InfoVinculoUsuarioBiblioteca> infoVinculos, InfoVinculoUsuarioBiblioteca infoVinculo){
		if(infoVinculos != null && infoVinculos.contains(infoVinculo))
			return infoVinculos.get(infoVinculos.indexOf(infoVinculo ));
		else 
			return null;
	}


	/**
	 * Retorna tela que exibe as informações sobre os vínculos do usuário. 
	 * 
	 *    <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 */
	private String telaMostraInformacoesVinculosUsuario(){
		return forward(PAGINA_MOSTRA_INFORMACOES_VINCULOS_USUARIO);
	}

	
	
	
	
	// sets e gets //

	

	public boolean isVerificandoMeusVinculos() {
		return verificandoMeusVinculos;
	}

	public List<InfoVinculoUsuarioBiblioteca> getInfoVinculosDiscenteUsuario() {
		return infoVinculosDiscenteUsuario;
	}
	
	public void setInfoVinculosDiscenteUsuario(List<InfoVinculoUsuarioBiblioteca> infoVinculosDiscenteUsuario) {
		this.infoVinculosDiscenteUsuario = infoVinculosDiscenteUsuario;
	}

	public List<InfoVinculoUsuarioBiblioteca> getInfoVinculosServidorUsuario() {
		return infoVinculosServidorUsuario;
	}

	public void setInfoVinculosServidorUsuario(List<InfoVinculoUsuarioBiblioteca> infoVinculosServidorUsuario) {
		this.infoVinculosServidorUsuario = infoVinculosServidorUsuario;
	}

	public List<InfoVinculoUsuarioBiblioteca> getInfoVinculosDocenteExternoUsuario() {
		return infoVinculosDocenteExternoUsuario;
	}

	public void setInfoVinculosDocenteExternoUsuario(List<InfoVinculoUsuarioBiblioteca> infoVinculosDocenteExternoUsuario) {
		this.infoVinculosDocenteExternoUsuario = infoVinculosDocenteExternoUsuario;
	}

	public List<InfoVinculoUsuarioBiblioteca> getInfoVinculosUsuarioExternoUsuario() {
		return infoVinculosUsuarioExternoUsuario;
	}
	
	public void setInfoVinculosUsuarioExternoUsuario(List<InfoVinculoUsuarioBiblioteca> infoVinculosUsuarioExternoUsuario) {
		this.infoVinculosUsuarioExternoUsuario = infoVinculosUsuarioExternoUsuario;
	}



	public void setVerificandoMeusVinculos(boolean verificandoMeusVinculos) {
		this.verificandoMeusVinculos = verificandoMeusVinculos;
	}

	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}

	public void setInfoUsuario(InformacoesUsuarioBiblioteca infoUsuario) {
		this.infoUsuario = infoUsuario;
	}
	
	
	
	
}
