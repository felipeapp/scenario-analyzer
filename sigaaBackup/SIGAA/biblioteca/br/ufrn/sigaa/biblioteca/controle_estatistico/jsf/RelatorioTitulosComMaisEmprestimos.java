/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 29/12/2009
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioTituloComMaisEmprestimoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * MBean que controla a gera��o do relat�rio de t�tulos com mais empr�stimos.
 * 
 * Observa��o: anteriormente este era o "Relat�rio de empr�stimos para fotoc�pia".
 *
 * @author Br�ulio
 */
@Component("relatorioTitulosComMaisEmprestimos")
@Scope("request")
public class RelatorioTitulosComMaisEmprestimos extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * A p�gina do relat�rio.
	 */
	private static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioTitulosComMaisEmprestimos.jsp";

	/** Lista dos t�tulos e a quantidade de empr�stimos feitos. */
	private SortedSet<Linha> resultados;
	
	/** Total de empr�stimos. */
	private int totalEmprestimos;
	
	/** Total de t�tulos emprestados. */
	private int totalTitulos;
	
	/** Total de materiais emprestados. */
	private int totalMateriais;
	
	/** Total de empr�stimos por material emprestado. */
	private double totalMediaEmprestimos;
	
	/** O n�mero m�ximo de materiais retornados. */
	private static final int LIMITE = 250;

	/**
	 * Representa uma linha do relat�rio.
	 */
	public static class Linha implements Comparable<Linha> {

		/**
		 * O T�tulo mostrado ao usu�rio
		 */
		private String titulo;
		
		/**
		 * A quantidade de empr�stimos desse t�tulo
		 */
		private int qtdEmprestimos;
		
		/**
		 * A quantidade de materiais desse t�tulo
		 */
		private int qtdMateriais;
		
		/**
		 * A m�dia de empr�stimos desse t�tulo
		 */
		private double mediaEmprestimos;
		
		/**
		 * O id do T�tulo no cache.
		 */
		private int id;

		public Linha(String titulo, Integer qtdEmprestimos, Integer qtdMateriais, Double mediaEmprestimos, int id) {
			this.titulo = titulo;
			this.qtdEmprestimos = qtdEmprestimos;
			this.qtdMateriais = qtdMateriais;
			this.mediaEmprestimos = mediaEmprestimos;
			this.id = id;
		}

		/**
		 * Ordena primeiro por m�dia de empr�stimos (decrescente), em seguida por n�mero de empr�stimos (decrescente) e depois pelo
		 * nome do t�tulo (crescente).
		 */
		@Override
		public int compareTo( Linha that ) {
			if ( (int) (this.getMediaEmprestimos() * 100) != (int) (that.getMediaEmprestimos() * 100) )
				return ((int) (that.getMediaEmprestimos() * 100)) - ((int) (this.getMediaEmprestimos() * 100));
			else if ( this.getQtdEmprestimos() != that.getQtdEmprestimos() )
				return that.getQtdEmprestimos() - this.getQtdEmprestimos();
			else if ( this.getTitulo().compareTo( that.getTitulo() ) != 0 )
				return this.getTitulo().compareTo( that.getTitulo() );
			else
				return this.id - that.id;
		}
		
		// GETs
		public String getTitulo() { return titulo; }
		public int getQtdEmprestimos() { return qtdEmprestimos; }
		public int getQtdMateriais() { return qtdMateriais; }
		public double getMediaEmprestimos() { return mediaEmprestimos; }
		public int getId() { return id; }
	}
	
	public RelatorioTitulosComMaisEmprestimos(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de T�tulos com mais Empr�stimos");
		setDescricao(
				"Neste relat�rio, o usu�rio pode visualizar que t�tulos foram mais emprestados " +
				"em um certo per�odo.");
		
		setFiltradoPorVariasBibliotecas(true);
		campoBibliotecaObrigatorio = false;
		setFiltradoPorPeriodo(true);
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);
		setFiltradoPorTipoDeEmprestimo(true);
	}

	/**
	 * Chamado por /biblioteca/controle_estatistico/formGeral.jsp
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {

		RelatorioTituloComMaisEmprestimoDao dao = getDAO( RelatorioTituloComMaisEmprestimoDao.class );
		configuraDaoRelatorio(dao);
		
		SortedMap<Integer, Number[]> resultadosUsandoIds = dao.findTitulosComMaisEmprestimos(
				UFRNUtils.toInteger(variasBibliotecas), this.getInicioPeriodo(), this.getFimPeriodo(), 
				this.getTipoDeEmprestimo(), LIMITE );

		resultados = new TreeSet<Linha>();

		totalEmprestimos 		= 0;
		totalTitulos     		= 0;
		totalMateriais     		= 0;
		totalMediaEmprestimos   = 0;

		for ( Map.Entry<Integer, Number[]> par : resultadosUsandoIds.entrySet() ) {
			CacheEntidadesMarc c = BibliotecaUtil.obtemDadosTituloCache(par.getKey());
			Number[] value = par.getValue();
			
			String tituloAtual = " T�tulo Removido ";
			
			if(c != null)
				tituloAtual = c.getTitulo() + ( c.getAutor() != null ? " - " + c.getAutor() : "");
			
			resultados.add( new Linha( tituloAtual, (Integer) value[0], (Integer) value[1], (Double) value[2], c.getId() ) );

			totalEmprestimos += (Integer) value[0];
			totalTitulos     += 1;
			totalMateriais += (Integer) value[1];
		}
		
		if ( totalTitulos == 0 ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		totalMediaEmprestimos = ((double) totalEmprestimos) / ((double) totalMateriais);

		return forward(PAGINA_DO_RELATORIO);
	}

	//// GETs e SETs ////

	public SortedSet<Linha> getResultados() { return resultados; }
	public int getTotalEmprestimos() { return totalEmprestimos; }
	public int getTotalTitulos() { return totalTitulos; }
	public int getLIMITE() {return LIMITE; }

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

	public int getTotalMateriais() {
		return totalMateriais;
	}

	public double getTotalMediaEmprestimos() {
		return totalMediaEmprestimos;
	}

}
