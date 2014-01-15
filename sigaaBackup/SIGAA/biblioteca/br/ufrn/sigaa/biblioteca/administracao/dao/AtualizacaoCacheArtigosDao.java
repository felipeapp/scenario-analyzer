/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.administracao.dao;

/**
 *
 * <p> Dao utilizado para atualizar o cache de dados MARC dos artigos</p>
 *
 *  <p> <i> Sempre que as informações de novo campo precisa ser mostrado ao usuário ou foi adicionado um novo campo na pesquisa do acervo 
 * na bilioteca que usa a tabela cache, essa informação precisar ser atualizada em todos os objetos cache existentes. Esse caso de uso foi 
 * criado para isso. Para não precisar ficar criando sqls ou classes para atualizar o cache.</i> </p>
 * 
 * @author jadson
 *
 */
public class AtualizacaoCacheArtigosDao {

	/** Os dados que todo campo deve retornar para conseguir monta um título completo */
	public static final String SELECT_COMUM_ATUALIZA_CACHE = " cd.id_artigo_de_periodico, e.tag, e.tipo, cd.id_campo_dados, sc.dado, sc.codigo  ";
	
	/** Os dados deve ser retornados ordenados para a cache ser gerados corretamente */
	public static final String ORDER_BY_COMUM_ATUALIZA_CACHE  = " ORDER BY cd.id_artigo_de_periodico, cd.posicao, sc.posicao "; 
	
	/** BUSCA AS INFORMAÇÕES DO TITULO UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_TITULO = " select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados cd "
		+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
		+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
		+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
		+" where e.tag = '245' and sc.codigo = 'a'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;

	/** BUSCA AS INFORMAÇÕES DO AUTOR UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_AUTOR = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados cd "
	+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
	+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
	+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
	+" where e.tag = '100' and sc.codigo = 'a'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** BUSCA AS INFORMAÇÕES DO AUTORES SECUNDÁRIOS UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_AUTORES_SECUNDARIOS = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados cd "
	+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
	+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
	+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
	+" where e.tag = '700' and sc.codigo = 'a'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** BUSCA AS INFORMAÇÕES DO LOCAL PUBLICAÇÃO UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_LOCAL_PUBLICACAO = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados cd "
	+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
	+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
	+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
	+" where e.tag = '260' and sc.codigo = 'a'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** BUSCA AS INFORMAÇÕES DO EDITORA UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_EDITORA = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados cd "
	+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
	+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
	+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
	+" where e.tag = '260' and sc.codigo = 'b'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** BUSCA AS INFORMAÇÕES DO ANO PUBLICAÇÃO UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_ANO_PUBLICACAO = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados cd "
	+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
	+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
	+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
	+" where e.tag = '260' and sc.codigo = 'c'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** BUSCA AS INFORMAÇÕES DO PALAVRAS CHAVES UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_PALAVRAS_CHAVES = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados cd "
	+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
	+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
	+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
	+" where e.tag = '650' and sc.codigo = 'a'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** BUSCA AS INFORMAÇÕES DO INTERVADO DE PAGINAS  UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_INTERVALO_DE_PAGINAS = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados cd "
	+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
	+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
	+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
	+" where e.tag = '773' and sc.codigo = 'g'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** BUSCA AS INFORMAÇÕES DO RESUMO UTILIZADAS NO CACHE */
	public static final String SQL_BUSCA_RESUMO = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados cd "
	+" inner join biblioteca.artigo_de_periodico p on p.id_artigo_de_periodico = cd.id_artigo_de_periodico "
	+" inner join biblioteca.etiqueta e on e.id_etiqueta = cd.id_etiqueta  "
	+" inner join biblioteca.sub_campo sc on sc.id_campo_dados = cd.id_campo_dados   "
	+" where e.tag = '520' and sc.codigo = 'a'  "+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
}
