/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/02/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.HistorioEmprestimosDao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * Bean respons�vel pelo caso de uso de emitir hist�ricos de empr�stimos dos 
 * usu�rios da biblioteca.
 *
 * @author jadson
 * @since 19/11/2008
 * @version 1.0 cria��o da classe
 * @version 2.0 20/04/2011 - Para o hist�rico de empr�stimos ser� preciso buscar os empr�stimos de 
 * todos as contas ( usu�rios bibliotecas ) ativos da pessoa ou biblioteca.
 */
@Component("emiteHistoricoEmprestimosMBean") 
@Scope("request")
public class EmiteHistoricoEmprestimosMBean extends SigaaAbstractController <Emprestimo> implements PesquisarUsuarioBiblioteca{

	/** P�gina que exibe o formul�rio para a gera��o do relat�rio. */
	public static final String PAGINA_EMITIR_HISTORIO_EMPRESTIMOS = "/biblioteca/circulacao/emiteHistoricoEmprestimos.jsp";
	
	/** P�gina que exibe o relat�rio gerado. */
	public static final String PAGINA_HISTORIO_EMPRESTIMOS = "/biblioteca/circulacao/historicoEmprestimos.jsp";
	
	/** Guarda o usu�rio que pode ser de um selecionado ou de quem est� logado. */
	private InformacoesUsuarioBiblioteca infoUsuarioBiblioteca;
	
	/**Todas as contas que o usu�rio teve durante sua vida acad�mcia   */
	private List<UsuarioBiblioteca> contasBiblioteca;
	
	/** O id  da pessoa passada pela busca padr�o do sistema */
	private Integer idPessoa;
	
	/** O id  da pessoa passada pela busca padr�o do sistema */
	private Integer idBiblioteca;
	
	/** Per�odo de in�cio do relat�rio . */  
	private Date  dataInicio;
	
	/** Per�odo de fim do relat�rio . */  
	private Date dataFinal;
	
	/** Guarda os Empr�stimos. */
	private List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
	
	/** Verifica se o usu�rio est� emitindo o pr�prio hist�rico ou � algum biblioteca */
	private boolean emitirProprioHistorio = true;
	
	

	/**
	 * Redireciona para a tela onde o bibliotec�rio vai emitir o hist�rico de empr�stimo do usu�rio selecionado.
	 * <br><br>
	 * M�todo chamado pela seguinte JSP: /biblioteca/resultadoBuscaUsuarioBiblioteca.jsp
	 *
	 * @param idUsuario
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciaEscolhendoUsuario() throws SegurancaException, DAOException{
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, true, true, "Visualizar o Hist�rico de Empr�stimos de um Usu�rio", null);
	}
	
	
	
	
	/**
	 *  Redireciona para a p�gina para emitir o hist�rico de empr�stimo APENAS do pr�prio usu�rio que 
	 * est� logado no momento..
	 * <br><br>
	 * M�todo chamado pelas seguintes JSP's: <ul><li>/portais/discente/menu_discente.jsp
	 * 									   <li> /portais/docente/menu_docente.jsp</ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciaUsuarioLogado() throws DAOException{
	
		// Por padr�o � consultado o hist�rio de �ltimo ano do usu�rio //
		dataInicio = CalendarUtils.diminuiDataEmUmAno(new Date());
		
		
		emitirProprioHistorio = true;
		
		UsuarioBibliotecaDao dao = getDAO(UsuarioBibliotecaDao.class);
		try {
			
			contasBiblioteca = dao.findUsuarioBibliotecaAtivoByPessoa(getUsuarioLogado().getPessoa().getId());
			UsuarioBiblioteca usuarioBiblioteca  = UsuarioBibliotecaUtil.recuperaUsuarioNaoQuitadosAtivos(contasBiblioteca);
			infoUsuarioBiblioteca = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario( usuarioBiblioteca, getUsuarioLogado().getPessoa().getId(), null );	
			
			return telaEmiteHistoricoEmprestimos();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally {
			if (dao != null) dao.close();
		}
	}
	
	
	
	///////////////////// M�todos da interface de busca no acervo //////////////////////////////
	

	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 *   <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		emitirProprioHistorio = false;
		
		// Por padr�o � consultado o hist�rio de �ltimo ano do usu�rio //
		dataInicio = CalendarUtils.diminuiDataEmUmAno(new Date());
		
		UsuarioBibliotecaDao dao = getDAO(UsuarioBibliotecaDao.class);
		try {
			
			if(idPessoa != null)
				contasBiblioteca = dao.findUsuarioBibliotecaAtivoByPessoa(idPessoa);
			if(idBiblioteca != null)
				contasBiblioteca = dao.findUsuarioBibliotecaAtivoByBiblioteca(idBiblioteca);
			
			UsuarioBiblioteca usuarioBiblioteca  = UsuarioBibliotecaUtil.recuperaUsuarioNaoQuitadosAtivos(contasBiblioteca);
			infoUsuarioBiblioteca = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario( usuarioBiblioteca, idPessoa, idBiblioteca );	
			
			return telaEmiteHistoricoEmprestimos();
			
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
		idBiblioteca = biblioteca.getId();
	}

	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
	}

	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		idPessoa = p.getId();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Volta para a tela de busca do usu�rio.
	 * <br><br>
	 * M�todo chamado pela seguinte JSP: /sigaa/biblioteca/circulacao/emiteHistoricoEmprestimos.jsp
	 * 
	 */
	public String voltarTelaBusca(){
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	
	/**
	 * Cont�m a l�gica de emiss�o de empr�stimos. Faz a busca e exibe o resultado.<br><br>
	 * M�todo chamado pela seguinte JSP: /biblioteca/circulacao/emiteHistoricoEmprestimos.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public String emiteHistorioEmprestimo() throws DAOException{
		
		if(contasBiblioteca == null || contasBiblioteca.size() == 0){
			addMensagemErro("Nenhum conta na biblioteca encontrada para o usu�rio. ");
			return cancelar();
		}
			
		HistorioEmprestimosDao dao = getDAO(HistorioEmprestimosDao.class);
		emprestimos  = dao.findEmprestimosAtivosByUsuarios(contasBiblioteca, dataInicio, dataFinal);
		
		// Formata para a visualiza��o html
		for (Emprestimo emprestimo : emprestimos) {
			emprestimo.getMaterial().setInformacao( emprestimo.getMaterial().getInformacao().replace("Localiza��o:", "<br/> <strong> Localiza��o: </strong>") );
			emprestimo.getMaterial().setInformacao( emprestimo.getMaterial().getInformacao().replace("Segunda Localiza��o:", " <strong> Segunda Localiza��o: </strong>") );
		}
		
		if (!emprestimos.isEmpty()){
			return telaHistoricoEmprestimos();
		} else {
			addMensagemWarning("Usu�rio n�o possui empr�stimos dentro dos crit�rios escolhidos");
			return telaEmiteHistoricoEmprestimos();
		}
	}
	
	
	
	/**
	 * Redireciona para a p�gina onde o usu�rio pode escolher os filtros da emiss�o de 
	 * hist�rico de empr�stimo.
	 *
	 * @return
	 */
	private String telaEmiteHistoricoEmprestimos(){
		return forward(PAGINA_EMITIR_HISTORIO_EMPRESTIMOS);
	}
	
	/**
	 * Redireciona para a p�gina onde os dados do hist�rico s�o mostrados.
	 *
	 * @return
	 */
	private String telaHistoricoEmprestimos(){
		return forward(PAGINA_HISTORIO_EMPRESTIMOS);
	}

	// Sets e gets.
	public Date getDataInicio() {
		return dataInicio;
	}

	public InformacoesUsuarioBiblioteca getInfoUsuarioBiblioteca() {
		return infoUsuarioBiblioteca;
	}

	public void setInfoUsuarioBiblioteca(InformacoesUsuarioBiblioteca infoUsuarioBiblioteca) {
		this.infoUsuarioBiblioteca = infoUsuarioBiblioteca;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<Emprestimo> getEmprestimos() {
		return emprestimos;
	}

	public void setEmprestimos(List<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
	}

	public boolean isEmitirProprioHistorio() {
		return emitirProprioHistorio;
	}

	public void setEmitirProprioHistorio(boolean emitirProprioHistorio) {
		this.emitirProprioHistorio = emitirProprioHistorio;
	}

	
	
	
}