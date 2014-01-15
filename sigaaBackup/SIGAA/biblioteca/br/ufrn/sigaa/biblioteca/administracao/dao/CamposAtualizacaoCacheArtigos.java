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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;

/**
 *
 *  <p>Enum que guarda os campos que o usuário pode escolher para atualizar no cache de artigos. Sempre que adicionar um novo campo adicionar aqui.</p>
 *
 * <p> <i> Sempre que as informações de novo campo precisa ser mostrado ao usuário ou foi adicionado um novo campo na pesquisa do acervo 
 * na bilioteca que usa a tabela cache, essa informação precisar ser atualizada em todos os objetos cache existentes. Esse caso de uso foi 
 * criado para isso. Para não precisar ficar criando sqls ou classes para atualizar o cache.</i> </p>
 * 
 * @author jadson
 *
 */
public enum CamposAtualizacaoCacheArtigos {

	/** O campo que guarda as inforamções do Título utilizados no cache de artigos */
	TITULO(1, "TÍTULO", AtualizacaoCacheArtigosDao.SQL_BUSCA_TITULO, "titulo", "titulo_ascii"),
	
	/** O campo que guarda as inforamções do autor utilizados no cache de artigos */
	AUTOR(2, "AUTOR", AtualizacaoCacheArtigosDao.SQL_BUSCA_AUTOR, "autor", "autor_ascii"),
	
	/** O campo que guarda as inforamções dos autores secundários utilizados no cache de artigos */
	AUTORES_SECUNDARIOS(3, "AUTORES SECUNDÁRIOS", AtualizacaoCacheArtigosDao.SQL_BUSCA_AUTORES_SECUNDARIOS, "autores_secundarios", ""),
	
	/** O campo que guarda as inforamções do local de publicação utilizados no cache de artigos */
	LOCAL_PUBLICACAO(4, "LOCAL DE PUBLICAÇÃO", AtualizacaoCacheArtigosDao.SQL_BUSCA_LOCAL_PUBLICACAO, "local_publicacao", ""),
	
	/** O campo que guarda as inforamções da editora utilizados no cache de artigos */
	EDITORA(5, "EDITORA", AtualizacaoCacheArtigosDao.SQL_BUSCA_EDITORA, "editora", ""),
	
	/** O campo que guarda as inforamções do ano utilizados no cache de artigos */
	ANO_PUBLICACAO(6, "ANO PUBLICAÇÃO", AtualizacaoCacheArtigosDao.SQL_BUSCA_ANO_PUBLICACAO, "ano", ""),
	
	/** O campo que guarda as inforamções das palavras-chaves utilizados no cache de artigos */
	PALAVRAS_CHAVES(7, "PALAVRAS-CHAVES", AtualizacaoCacheArtigosDao.SQL_BUSCA_PALAVRAS_CHAVES, "assunto", "assunto_ascii"),
	
	/** O campo que guarda as inforamções do intervalo de páginas utilizados no cache de artigos */
	INTERVALO_DE_PAGINAS(8, "INTERVALO DE PÁGINAS", AtualizacaoCacheArtigosDao.SQL_BUSCA_INTERVALO_DE_PAGINAS, "intervalo_paginas", ""),
	
	/** O campo que guarda as inforamções do resumo utilizados no cache de artigos */
	RESUMO(9, "RESUMO", AtualizacaoCacheArtigosDao.SQL_BUSCA_RESUMO, "resumo", "");
	
	
	/** A descrição do campo que vai ser atualização, utilizado para o usuários escolher o campo.*/
	private String descricao;
	
	/**  A posião do campo. Utilizado para saber qual foi selecionado pelo usuário.*/
	private int posicao;
	
	/**  O SQL com as informações que precisam ser buscadas para atualiza as informações do campo no cache.*/
	private String sqlBuscaInformacoesMontarCampo;
	
	/** A coluna que será atualizada*/
	private String coluna;
	
	/** A coluna ascii que será atualizada*/
	private String colunaAscii;
	
	
	private CamposAtualizacaoCacheArtigos(int posicao, String descricao, String sqlBuscaInformacoesMontarCampo, String coluna, String colunaAscii){
		this.posicao = posicao;
		this.descricao = descricao;
		this.sqlBuscaInformacoesMontarCampo = sqlBuscaInformacoesMontarCampo;
		this.coluna = coluna;
		this.colunaAscii = colunaAscii;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getPosicao() {
		return posicao;
	}

	public String getSqlBuscaInformacoesMontarCampo() {
		return sqlBuscaInformacoesMontarCampo;
	}

	public String getColuna() {
		return coluna;
	}


	public String getColunaAscii() {
		return colunaAscii;
	}
	
	/** Retorna o sql utilizado para consultar as informações atuais do cache, para saber se precisa atualizar ou não.*/
	public String getSqlBuscaCampo(){
		String sqlSelect  = new String(" SELECT "+getColuna()+" FROM biblioteca.cache_entidades_marc WHERE id_artigo_de_periodico = ? ");
		return sqlSelect;
	}
	
	/** Retorna o sql utilizado para atualizar as informações do cache .*/
	public String getSqlAtualizaCampo(){
		String sqlUpdate  = new String(" UPDATE biblioteca.cache_entidades_marc SET "+getColuna()+" = ?  WHERE id_artigo_de_periodico = ? ");
		return sqlUpdate;
		
	}
	
	/** Retorna o sql utilizado para consultar as informações atuais do cache, para saber se precisa atualizar ou não.*/
	public String getSqlBuscaCampoAscii(){
		String sqlSelect  = new String(" SELECT "+getColunaAscii()+" FROM biblioteca.cache_entidades_marc WHERE id_artigo_de_periodico = ? ");
		return sqlSelect;
	}
	
	/** Retorna o sql utilizado para atualizar as informações do cache .*/
	public String getSqlAtualizaCampoAscii(){
		String sqlUpdate  = new String(" UPDATE biblioteca.cache_entidades_marc SET "+getColunaAscii()+" = ? WHERE id_artigo_de_periodico = ? ");
		return sqlUpdate;
		
	}
	
	/** Retorna a informação que vai ser atualizada de acordo com o campo passado */
	public String getInformacaoAtualizacaoCampo(CacheEntidadesMarc cacheTemp){
		
		if(this ==  CamposAtualizacaoCacheArtigos.TITULO ) return cacheTemp.getTitulo();
		if(this ==  CamposAtualizacaoCacheArtigos.AUTOR ) return cacheTemp.getAutor();
		if(this ==  CamposAtualizacaoCacheArtigos.AUTORES_SECUNDARIOS ) return cacheTemp.getAutoresSecundarios();
		if(this ==  CamposAtualizacaoCacheArtigos.LOCAL_PUBLICACAO ) return cacheTemp.getLocalPublicacao();
		if(this ==  CamposAtualizacaoCacheArtigos.EDITORA ) return cacheTemp.getEditora();
		if(this ==  CamposAtualizacaoCacheArtigos.PALAVRAS_CHAVES ) return cacheTemp.getAssunto();
		if(this ==  CamposAtualizacaoCacheArtigos.INTERVALO_DE_PAGINAS ) return cacheTemp.getIntervaloPaginas();
		if(this ==  CamposAtualizacaoCacheArtigos.RESUMO ) return cacheTemp.getResumo();
		
		return "";
	}
	
	
	/** Retorna a informação que vai ser atualizada de acordo com o campo passado */
	public String getInformacaoAtualizacaoCampoAscii(CacheEntidadesMarc cacheTemp){
		
		if(this ==  CamposAtualizacaoCacheArtigos.TITULO ) return cacheTemp.getTituloAscii();
		if(this ==  CamposAtualizacaoCacheArtigos.AUTOR ) return cacheTemp.getAutorAscii();
		if(this ==  CamposAtualizacaoCacheArtigos.PALAVRAS_CHAVES ) return cacheTemp.getAssuntoAscii();
		
		return "";
		
	}
	
}
