package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Classe responsável pela consulta das avaliações dos projeto de pesquisa. 
 * 
 * @author Jean Guerethes
 */
@Component @Scope("request")
public class BuscaAvaliacaoProjetoPesquisaMBean extends SigaaAbstractController<AvaliacaoProjeto> {
	
	/** View do formulário da consulta das avaliações */
	public static final String JSP_FORM_AVALIACAO_PROJETO 			 = "/pesquisa/avaliacaoProjeto/form.jsf";
	/** View do resultado da consulta das avaliações */
	public static final String JSP_VIEW_RELATORIO_AVALIACAO_PROJETO = "/pesquisa/avaliacaoProjeto/view.jsf";
	/** Armazena as avaliações dos projetos realizadas */
	private Collection<AvaliacaoProjeto> avaliacoes;
	/** Indica se o relatório deve ser exibido no formato de relatório ou não */
	private boolean formatoRelatorio;
	/** Indica a quantidade de avaliaões que o projeto deve apresentar */
	private int quantidadeAvaliacoes;
	/** Indica para qual edital está se analisando a quantidade de avaliações */
	private int editalPesquisa;
	
	public BuscaAvaliacaoProjetoPesquisaMBean() {
		clear();
	}
	
	/** Responsável pela inicialização dos atributos necessários para a geração da consulta */
	private void clear() {
		obj = new AvaliacaoProjeto();
		obj.setProjetoPesquisa(new ProjetoPesquisa());
		obj.getProjetoPesquisa().setEdital( new EditalPesquisa() );
		avaliacoes = new ArrayList<AvaliacaoProjeto>();
		formatoRelatorio = false;
		quantidadeAvaliacoes = 0;
		editalPesquisa = 0;
	}

	@Override
	public String getDirBase() {
		return super.getDirBase();
	}
	
	/**
	 * Inicializa a busca das avaliações dos projetos
	 * <br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/projetos.jsp</li>
	 * </ul>
	 */
	public String iniciarBuscaAvaliacoesProjeto() {
		clear();
		return forward( JSP_FORM_AVALIACAO_PROJETO );
	}

	/**
	 * Responsável pela realização da construção do relatório e/ou da listagem das 
	 * avaliações dos projetos. 
	 * 
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/avaliacaoProjeto/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String buscaAvaliacoesProjeto() throws DAOException {
		validate();
		if ( hasErrors() )
			return null;
		
		AvaliacaoProjetoDao dao = getDAO(AvaliacaoProjetoDao.class);
		try {
			EditalPesquisa edital = dao.findByPrimaryKey(editalPesquisa, EditalPesquisa.class);
			setAvaliacoes( dao.findAvaliacoesProjeto( edital.getEdital().getId(), quantidadeAvaliacoes ) );
			obj.getProjetoPesquisa().getEdital().setId( editalPesquisa );
			dao.initialize( obj.getProjetoPesquisa().getEdital() );
		} finally {
			dao.close();
		}
		
		if ( avaliacoes.isEmpty()  ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		if ( formatoRelatorio )
			return forward( JSP_VIEW_RELATORIO_AVALIACAO_PROJETO );
		else
			return forward( JSP_FORM_AVALIACAO_PROJETO );
	}

	private void validate() {
		if ( quantidadeAvaliacoes == -1 )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Quantidade de Avaliações");
		if ( isEmpty( editalPesquisa ) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital");
	}

	public Collection<AvaliacaoProjeto> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Collection<AvaliacaoProjeto> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public boolean isFormatoRelatorio() {
		return formatoRelatorio;
	}

	public void setFormatoRelatorio(boolean formatoRelatorio) {
		this.formatoRelatorio = formatoRelatorio;
	}

	public int getQuantidadeAvaliacoes() {
		return quantidadeAvaliacoes;
	}

	public void setQuantidadeAvaliacoes(int quantidadeAvaliacoes) {
		this.quantidadeAvaliacoes = quantidadeAvaliacoes;
	}

	public int getEditalPesquisa() {
		return editalPesquisa;
	}

	public void setEditalPesquisa(int editalPesquisa) {
		this.editalPesquisa = editalPesquisa;
	}
	
}