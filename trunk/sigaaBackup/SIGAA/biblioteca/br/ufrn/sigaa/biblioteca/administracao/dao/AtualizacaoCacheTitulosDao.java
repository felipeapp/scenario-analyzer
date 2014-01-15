/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.administracao.dao;

import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 *
 * <p> Dao utilizado para atualizar o cache de dados MARC dos titulos</p>
 *
 * <p> <i> Sempre que as informações de novo campo precisa ser mostrado ao usuário ou foi adicionado um novo campo na pesquisa do acervo 
 * na bilioteca que usa a tabela cache, essa informação precisar ser atualizada em todos os objetos cache existentes. Esse caso de uso foi 
 * criado para isso. Para não precisar ficar criando sqls ou classes para atualizar o cache.</i> </p>
 * 
 * @author jadson
 *
 */
public class AtualizacaoCacheTitulosDao extends GenericSigaaDAO{

	/** Os dados que todo campo deve retornar para conseguir monta um título completo */
	public static final String SELECT_COMUM_ATUALIZA_CACHE = " e.tipo_campo, c.id_titulo_catalografico, e.tag, e.tipo, c.id_campo_dados, sc.dado, sc.codigo  ";
	
	/** Os dados deve ser retornados ordenados para a cache ser gerados corretamente */
	public static final String ORDER_BY_COMUM_ATUALIZA_CACHE  = " ORDER BY c.id_titulo_catalografico, c.posicao, sc.posicao "; 
	
	/** TITULO */
	public static final String SQL_BUSCA_TITULO = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '245' and e.tipo = 1 and sc.codigo = 'a' and t.ativo = trueValue()   "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** OUTRAS INFORMACOES TITULO ASCII */
	public static final String SQL_BUSCA_OUTRAS_INFORMACOES_TITULO =" select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados c "
	+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
	+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta  "
	+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados  "
	+" where (  "
	+"  ( ( e.tag = '245' or e.tag = '130' or e.tag = '240' or e.tag = '730' or e.tag = '210' or e.tag = '246' or e.tag = '243') and sc.codigo = 'a' ) "
	+" or "
	+" ( ( e.tag = '780' or e.tag = '785' ) and sc.codigo = 't' ) "
	+" or "
	+" ( ( e.tag = '246' ) and ( sc.codigo = 'b' or sc.codigo = 'n' or sc.codigo = 'p' ) ) "
	+" or "
	+" ( ( e.tag = '245' ) and ( sc.codigo = 'b' ) ) "
	+" or "
	+" ( ( e.tag = '740' ) and ( sc.codigo = 'a' or sc.codigo = 'n' or sc.codigo = 'p' ) ) "
	+" ) "
	+" and e.tipo = 1 "
	+" and t.ativo = trueValue()   "
	+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** MEIO DE PUBLICAÇÃO */
	public static final String SQL_BUSCA_MEIO_PUBLICACAO ="select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '245' and e.tipo = 1 and sc.codigo = 'h' and t.ativo = trueValue()   "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** SUB TITULO */
	public static final String SQL_BUSCA_SUBTITULO ="select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '245' and e.tipo = 1 and sc.codigo = 'b' and t.ativo = trueValue()   "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** FORMAS VARIANTES DO TITULO */
	public static final String SQL_BUSCA_FORMAS_VARIANTES_TITULO ="select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '246' and e.tipo = 1 and sc.codigo = 'a' and t.ativo = trueValue()   "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** TITULO UNIFORME */
	public static final String SQL_BUSCA_TITULO_UNIFORME ="select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '240' and e.tipo = 1 and sc.codigo = 'a' and t.ativo = trueValue()   "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** AUTOR */
	public static final String SQL_BUSCA_AUTOR ="select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where ( ( (e.tag = '100' or e.tag = '110' ) and ( sc.codigo = 'a' or sc.codigo = 'b'  ) ) "
		+" or ( e.tag = '111' and ( sc.codigo = 'a' or sc.codigo = 'e' or sc.codigo = 'n' or sc.codigo = 'd' or sc.codigo = 'c' )    ) "
		+" or (e.tag = '245' and sc.codigo = 'c' ) ) " 
		+"  and e.tipo = 1 and  t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** AUTOR Secundário */
	public static final String SQL_BUSCA_AUTOR_SECUNDARIO ="select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where ( (e.tag = '700' or e.tag = '710' or e.tag = '711' ) and e.tipo = 1 and sc.codigo = 'a' and t.ativo = trueValue()  "
		+" or (e.tag = '710' and sc.codigo = 'b' ) "
		+" or (e.tag = '711' and sc.codigo = 'e' )  ) "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** Assunto */
	public static final String SQL_BUSCA_ASSUNTO="select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where ( ( ( e.tag = '600' or e.tag = '610' or e.tag = '611' or e.tag = '630' or e.tag = '650' or e.tag = '651' or e.tag = '653') and sc.codigo = 'a' )"
		+" or  ( e.tag = '611' and sc.codigo = 'e' )"
		+" or ( ( (  e.tag = '650' or e.tag = '651') and ( sc.codigo = 'x' or sc.codigo = 'y' or sc.codigo = 'z') ) ) )"
		+" and e.tipo = 1 and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** ISBN */
	public static final String SQL_BUSCA_ISBN = "select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados c "
	+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
	+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
	+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
	+" where e.tag = '020' and e.tipo = 1 and sc.codigo = 'a' and  t.ativo = trueValue()  "
	+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** ISSN */
	public static final String SQL_BUSCA_ISSN = "select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados c "
	+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
	+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
	+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
	+" where e.tag = '022' and e.tipo = 1 and sc.codigo = 'a' and  t.ativo = trueValue()  "
	+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** classificação 1 */
	public static final String SQL_BUSCA_CLASSIFICACAO_1 = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = ':campo' and e.tipo = 1 and sc.codigo = ':subcampo' and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/**  classificação 2 */
	public static final String SQL_BUSCA_CLASSIFICACAO_2 = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = ':campo' and e.tipo = 1 and sc.codigo = ':subcampo' and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/**  classificação 3 */
	public static final String SQL_BUSCA_CLASSIFICACAO_3 = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = ':campo' and e.tipo = 1 and sc.codigo = ':subcampo' and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** DESCRIÇÃO FISICA */
	public static final String SQL_BUSCA_DESCRICAO_FISICA = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where ( e.tag = '300' ) and e.tipo = 1 and sc.codigo = 'a' and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** SERIE */
	public static final String SQL_BUSCA_SERIE = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where ( e.tag = '440' or e.tag = '490' ) and e.tipo = 1 and sc.codigo = 'a' and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** EDICAO */
	public static final String SQL_BUSCA_EDICAO = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '250'  and e.tipo = 1 and ( sc.codigo = 'a' or sc.codigo = 'b' )  and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** LOCAL PUBLICACAO */
	public static final String SQL_BUSCA_LOCAL_PUBLICACAO = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '260'  and e.tipo = 1 and  sc.codigo = 'a'   and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** EDITORA */
	public static final String SQL_BUSCA_EDITORA = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '260'  and e.tipo = 1 and  sc.codigo = 'b'   and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** ANO */
	public static final String SQL_BUSCA_ANO = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '260'  and e.tipo = 1 and ( sc.codigo = 'c' or sc.codigo = 'g' )  and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	/** NUMERO CHAMADA */
	public static final String SQL_BUSCA_NUMERO_CHAMADA = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '090'  and e.tipo = 1 and ( sc.codigo = 'a' or sc.codigo = 'b' or sc.codigo = 'c' or sc.codigo = 'd' )  and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	/** LOCALIZACAO ENDERECO ELETRONICO */
	public static final String SQL_BUSCA_ENDERECO_ELETRONICO = "select "+SELECT_COMUM_ATUALIZA_CACHE
		+" from biblioteca.campo_dados c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados "
		+" where e.tag = '856'  and e.tipo = 1 and ( sc.codigo = 'a' or sc.codigo = 'u' or sc.codigo = 'z' )  and t.ativo = trueValue()  "
		+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	                                                              
	/** ANO PUBLICACAO */
	public static final String SQL_BUSCA_ANO_PUBLICACAO = "select e.tipo_campo, c.id_titulo_catalografico, e.tag, e.tipo, c.id_campo_controle, c.dado "
		+" from biblioteca.campo_controle c "
		+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta "
		+" where e.tag = '008'  and e.tipo = 1  and t.ativo = trueValue()  "
		+" ORDER BY c.id_titulo_catalografico, c.posicao ";

	/** IDIOMA */
	public static final String SQL_BUSCA_IDIOMA = "SELECT e.tipo_campo, c.id_titulo_catalografico, e.tag, e.tipo, c.id_campo_controle, c.dado "
		+" FROM biblioteca.campo_controle c "
		+" INNER JOIN biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
		+" INNER JOIN biblioteca.etiqueta e on e.id_etiqueta = c.id_etiqueta "
		+" WHERE e.tag = '008' and e.tipo = '1' and t.ativo = trueValue()"
		+" ORDER BY c.id_titulo_catalografico, c.posicao ";
	
	
	/** Notas  */
	public static final String SQL_BUSCA_NOTAS =" select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados c "
	+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
	+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta  "
	+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados  "
	+" where (  "
	+"  (  e.tag = '500' and sc.codigo = 'a' ) "
	+"  or (  e.tag = '505'  and sc.codigo = 'a' ) "
	+"  or (  e.tag = '590'  and sc.codigo = 'a' ) "
	+"  or ( ( e.tag = '500' or e.tag = '501' or e.tag = '502' or e.tag = '505' or e.tag = '530' or e.tag = '590') and sc.codigo = 'a' ) "
	+"  or ( ( e.tag = '534' ) and ( sc.codigo = 'p' or sc.codigo = 'f' or sc.codigo = 'b' or sc.codigo = 'c' ) ) "
	+" ) "
	+" and e.tipo = 1 "
	+" and t.ativo = trueValue()   "
	+ORDER_BY_COMUM_ATUALIZA_CACHE;

	
	
	/** Resumo */
	public static final String SQL_BUSCA_RESUMO = " select "+SELECT_COMUM_ATUALIZA_CACHE
	+" from biblioteca.campo_dados c "
	+" join biblioteca.titulo_catalografico t on c.id_titulo_catalografico = t.id_titulo_catalografico "
	+" join biblioteca.etiqueta e on c.id_etiqueta = e.id_etiqueta  "
	+" join biblioteca.sub_campo sc on sc.id_campo_dados = c.id_campo_dados  "
	+" where ( "
	+"  e.tag = '520' and sc.codigo = 'a' "
	+" ) "
	+ORDER_BY_COMUM_ATUALIZA_CACHE;
	
	
	
}
