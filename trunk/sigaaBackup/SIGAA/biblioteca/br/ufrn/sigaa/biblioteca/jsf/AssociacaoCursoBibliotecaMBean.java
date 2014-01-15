/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 10/02/2010
 *
 */

package br.ufrn.sigaa.biblioteca.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ServicosInformacaoReferenciaBiblioteca;
import br.ufrn.sigaa.dominio.Curso;

/**
 * MBean que controla a gerência dos cursos associados às bibliotecas
 * (cursos que têm acesso a serviços restritos).
 *
 * @author Bráulio
 * @since 26/02/2010
 */
@Component("associacaoCursoBibliotecaMBean")
@Scope("request")
public class AssociacaoCursoBibliotecaMBean
		extends SigaaAbstractController<ServicosInformacaoReferenciaBiblioteca> {
	
	/** Página que permite ao usuário escolher a biblioteca.  */
	private static final String ESCOLHER_BIBLIOTECA_JSP = "/biblioteca/associacao_curso_biblioteca/escolherBiblioteca.jsp";
	
	/** Página que permite ao usuário visualizar / editar as associações. */
	private static final String EDITAR_ASSOCIACOES_JSP = "/biblioteca/associacao_curso_biblioteca/editarAssociacoes.jsp";
	
	/** Biblioteca à qual os cursos listados por esse MBean pertencem. */
	private Biblioteca biblioteca = null;
	
	/** Os cursos listados. É um <tt>SortedSet</tt>, pois a lista é mostrada de forma ordenada. */
	private SortedSet<Curso> cursosAssociados;
	
	/** O curso a ser adicionado. */
	private Curso curso;
	
	/**
	 * Inicializa todos os campos do mBean.
	 */
	public AssociacaoCursoBibliotecaMBean() {
		obj = new ServicosInformacaoReferenciaBiblioteca();
		obj.setId( -1 );
		biblioteca = new Biblioteca();
		biblioteca.setId( -1 );
		
		curso = new Curso();
	}
	
	/**
	 * Escolhe uma biblioteca para visualizar/editar os cursos associados.
	 * <p>
	 * Chamado por: <ul><li>/sigaa.war/biblioteca/menus/cadastros.jsp</li></ul>
	 */
	public String escolherBiblioteca() {
		return forward(ESCOLHER_BIBLIOTECA_JSP);
	}
	
	/**
	 * <p>Lista os cursos associados a uma certa biblioteca.</p>
	 * 
	 * <p>
	 *   Chamado por:
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/associacao_curso_biblioteca/escolherBiblioteca.jsp</li>
	 *   </ul>
	 * </p>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		
		biblioteca = getGenericDAO().findByPrimaryKey( biblioteca.getId(), Biblioteca.class, new String[]{"id", "identificador", "descricao"} );
		
		if ( biblioteca == null ) {
			addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Biblioteca" );
			resetBean();
			return escolherBiblioteca();
		}
		
		obj =  getGenericDAO().findByExactField(ServicosInformacaoReferenciaBiblioteca.class, "biblioteca.id", biblioteca.getId(), true);
		
		// as associações são ordenadas pelos nomes dos cursos
		cursosAssociados = new TreeSet<Curso>(
				new Comparator<Curso>() {
					@Override
					public int compare(Curso a, Curso b) {
						return a.getDescricao().compareTo( b.getDescricao() );
					}
				});
		
		if (obj == null) {
			obj = new ServicosInformacaoReferenciaBiblioteca();
			
			obj.setCursosAssociados(new ArrayList<Curso>());
			
			obj.setBiblioteca(biblioteca);
			biblioteca.setServicos(obj);					
		}
		
		cursosAssociados.addAll( obj.getCursosAssociados() );
		
		prepareMovimento( ArqListaComando.CADASTRAR );
		
		return forward(EDITAR_ASSOCIACOES_JSP);
	}
	
	/**
	 * <p>
	 * Remove uma ligação entre um curso e uma biblioteca. Recebe o parâmetro
	 * <tt>idAssociacaoCursoBiblioteca</tt> na requisição HTTP.
	 * </p>
	 * <p>
	 * Chamado pelas JSPs:
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/associacao_curso_biblioteca/editarAssociacoes.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public String removerPorId() throws ArqException {
		int id = getParameterInt( "idAssociacaoCursoBiblioteca", -1 );
		
		if ( id == -1 ) {
			this.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
			return null;
		}
		
		Curso apagar = null;
		for ( Curso c : obj.getCursosAssociados() )
			if ( c.getId() == id ) {
				apagar = c;
				break;
			}
		
		obj.getCursosAssociados().remove(apagar);
		
		MovimentoCadastro mov = new MovimentoCadastro( this.obj, ArqListaComando.ALTERAR );
		
		prepareMovimento( ArqListaComando.ALTERAR );

		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return forward(getListPage());
		}
		
		resetBean();
		
		AssociacaoCursoBibliotecaMBean mbean = getMBean("associacaoCursoBibliotecaMBean");
		mbean.setBiblioteca( this.biblioteca );
		return mbean.listar();
	}
	
	/**
	 * <p>Cadastra uma nova ligação entre curso e biblioteca.</p>
	 * <p>Chamado pelas JSPs:
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/associacao_curso_biblioteca/editarAssociacoes.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		if ( curso.getId() <= 0 ) {
			this.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
			return null;
		}
		
		for ( Curso c : cursosAssociados ) {
			if ( c.getId() == curso.getId() ) {
				this.addMensagem( MensagensArquitetura.OBJETO_JA_CADASTRADO, "curso" );
				return null;
			}
		}
		
		checkChangeRole();
		
		obj.getCursosAssociados().add( curso );

		if (!hasErrors()) {
			try {
				beforeCadastrarAfterValidate();
			

				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
	
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
			
				execute(mov);
				
				addMensagem(OPERACAO_SUCESSO);
				
				if (isReprepare())
					prepareMovimento(ArqListaComando.CADASTRAR);
			} catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				return null;
			}
			
			resetBean();
			
			AssociacaoCursoBibliotecaMBean mbean = getMBean("associacaoCursoBibliotecaMBean");
			mbean.setBiblioteca( this.biblioteca );
			return mbean.listar();
		}
		
		return null;
	}
	
	/**
	 * <p>Volta para a página de edição da biblioteca.</p>
	 * <p> Chamado por:
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/associacao_curso_biblioteca/editarAssociacoes.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public String voltar() {
		return escolherBiblioteca();
	}
	
	/**
	 * Retorna todas as bibliotecas setoriais ativas do sistema.
	 * <p>
	 * Chamado por:  <ul><li>/sigaa.war/biblioteca/associacao_curso_biblioteca/escolherBiblioteca.jsp</li></ul>
	 */
	public List<SelectItem> getBibliotecasSetoriaisCombo() throws DAOException {
		BibliotecaDao dao = getDAO( BibliotecaDao.class );
		ArrayList<Biblioteca> bibliotecas = new ArrayList<Biblioteca>( dao.findAllBibliotecasInternasAtivas() );
		
		// retira a biblioteca central
		Iterator<Biblioteca> i = bibliotecas.iterator();
		while ( i.hasNext() ) {
			Biblioteca b = i.next();
			if ( b.isBibliotecaCentral() )
				i.remove();
		}
		
		return toSelectItems( bibliotecas, "id", "descricao" );
	}
	
	/**
	 * Checa se o usuário tem permissão para alterar os cursos associados da biblioteca.
	 * Administradores gerais podem alterar todas. Administradores locais podem alterar
	 * somente as bibliotecas nas quais eles têm papel de administrador.
	 * <p> Chamado por:
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/associacao_curso_biblioteca/editarAssociacoes.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public boolean getUsuarioTemPermissaoDeAlteracao() {
		if ( isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL) )
			return true;

		if ( isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL) ) {
			try {
				if( biblioteca != null ){
					checkRole( biblioteca.getUnidade(), SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL );
					return true;
				}
			} catch ( SegurancaException se ) {
				return false;
			}
		}

		return false;
	}
	
	//// Gets e sets ////

	public Biblioteca getBiblioteca() { return biblioteca; }
	public void setBiblioteca(Biblioteca biblioteca) { this.biblioteca = biblioteca; }

	public SortedSet<Curso> getCursosAssociados() { return cursosAssociados; }
	public void setCursosAssociados( SortedSet<Curso> cursosAssociados) { this.cursosAssociados = cursosAssociados; }

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

}
