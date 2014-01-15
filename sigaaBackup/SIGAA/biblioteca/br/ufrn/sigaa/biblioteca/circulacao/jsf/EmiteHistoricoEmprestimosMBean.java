/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Bean responsável pelo caso de uso de emitir históricos de empréstimos dos 
 * usuários da biblioteca.
 *
 * @author jadson
 * @since 19/11/2008
 * @version 1.0 criação da classe
 * @version 2.0 20/04/2011 - Para o histórico de empréstimos será preciso buscar os empréstimos de 
 * todos as contas ( usuários bibliotecas ) ativos da pessoa ou biblioteca.
 */
@Component("emiteHistoricoEmprestimosMBean") 
@Scope("request")
public class EmiteHistoricoEmprestimosMBean extends SigaaAbstractController <Emprestimo> implements PesquisarUsuarioBiblioteca{

	/** Página que exibe o formulário para a geração do relatório. */
	public static final String PAGINA_EMITIR_HISTORIO_EMPRESTIMOS = "/biblioteca/circulacao/emiteHistoricoEmprestimos.jsp";
	
	/** Página que exibe o relatório gerado. */
	public static final String PAGINA_HISTORIO_EMPRESTIMOS = "/biblioteca/circulacao/historicoEmprestimos.jsp";
	
	/** Guarda o usuário que pode ser de um selecionado ou de quem está logado. */
	private InformacoesUsuarioBiblioteca infoUsuarioBiblioteca;
	
	/**Todas as contas que o usuário teve durante sua vida acadêmcia   */
	private List<UsuarioBiblioteca> contasBiblioteca;
	
	/** O id  da pessoa passada pela busca padrão do sistema */
	private Integer idPessoa;
	
	/** O id  da pessoa passada pela busca padrão do sistema */
	private Integer idBiblioteca;
	
	/** Período de início do relatório . */  
	private Date  dataInicio;
	
	/** Período de fim do relatório . */  
	private Date dataFinal;
	
	/** Guarda os Empréstimos. */
	private List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
	
	/** Verifica se o usuário está emitindo o próprio histórico ou é algum biblioteca */
	private boolean emitirProprioHistorio = true;
	
	

	/**
	 * Redireciona para a tela onde o bibliotecário vai emitir o histórico de empréstimo do usuário selecionado.
	 * <br><br>
	 * Método chamado pela seguinte JSP: /biblioteca/resultadoBuscaUsuarioBiblioteca.jsp
	 *
	 * @param idUsuario
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciaEscolhendoUsuario() throws SegurancaException, DAOException{
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, true, true, "Visualizar o Histórico de Empréstimos de um Usuário", null);
	}
	
	
	
	
	/**
	 *  Redireciona para a página para emitir o histórico de empréstimo APENAS do próprio usuário que 
	 * está logado no momento..
	 * <br><br>
	 * Método chamado pelas seguintes JSP's: <ul><li>/portais/discente/menu_discente.jsp
	 * 									   <li> /portais/docente/menu_docente.jsp</ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciaUsuarioLogado() throws DAOException{
	
		// Por padrão é consultado o histório de último ano do usuário //
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
	
	
	
	///////////////////// Métodos da interface de busca no acervo //////////////////////////////
	

	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 *   <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		emitirProprioHistorio = false;
		
		// Por padrão é consultado o histório de último ano do usuário //
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
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		idBiblioteca = biblioteca.getId();
	}

	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
	}

	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		idPessoa = p.getId();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Volta para a tela de busca do usuário.
	 * <br><br>
	 * Método chamado pela seguinte JSP: /sigaa/biblioteca/circulacao/emiteHistoricoEmprestimos.jsp
	 * 
	 */
	public String voltarTelaBusca(){
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	
	/**
	 * Contém a lógica de emissão de empréstimos. Faz a busca e exibe o resultado.<br><br>
	 * Método chamado pela seguinte JSP: /biblioteca/circulacao/emiteHistoricoEmprestimos.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public String emiteHistorioEmprestimo() throws DAOException{
		
		if(contasBiblioteca == null || contasBiblioteca.size() == 0){
			addMensagemErro("Nenhum conta na biblioteca encontrada para o usuário. ");
			return cancelar();
		}
			
		HistorioEmprestimosDao dao = getDAO(HistorioEmprestimosDao.class);
		emprestimos  = dao.findEmprestimosAtivosByUsuarios(contasBiblioteca, dataInicio, dataFinal);
		
		// Formata para a visualização html
		for (Emprestimo emprestimo : emprestimos) {
			emprestimo.getMaterial().setInformacao( emprestimo.getMaterial().getInformacao().replace("Localização:", "<br/> <strong> Localização: </strong>") );
			emprestimo.getMaterial().setInformacao( emprestimo.getMaterial().getInformacao().replace("Segunda Localização:", " <strong> Segunda Localização: </strong>") );
		}
		
		if (!emprestimos.isEmpty()){
			return telaHistoricoEmprestimos();
		} else {
			addMensagemWarning("Usuário não possui empréstimos dentro dos critérios escolhidos");
			return telaEmiteHistoricoEmprestimos();
		}
	}
	
	
	
	/**
	 * Redireciona para a página onde o usuário pode escolher os filtros da emissão de 
	 * histórico de empréstimo.
	 *
	 * @return
	 */
	private String telaEmiteHistoricoEmprestimos(){
		return forward(PAGINA_EMITIR_HISTORIO_EMPRESTIMOS);
	}
	
	/**
	 * Redireciona para a página onde os dados do histórico são mostrados.
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