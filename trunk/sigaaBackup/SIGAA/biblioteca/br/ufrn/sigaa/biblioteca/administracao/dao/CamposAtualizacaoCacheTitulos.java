/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.administracao.dao;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;

/**
 *
 * <p>Enum que guarda os campos que o usu�rio pode escolher para atualizar no cache. Sempre que adicionar um novo campo adicionar aqui.</p>
 *
 * <p> <i> Sempre que as informa��es de novo campo precisa ser mostrado ao usu�rio ou foi adicionado um novo campo na pesquisa do acervo 
 * na bilioteca que usa a tabela cache, essa informa��o precisar ser atualizada em todos os objetos cache existentes. Esse caso de uso foi 
 * criado para isso. Para n�o precisar ficar criando sqls ou classes para atualizar o cache.</i> </p>
 * 
 * @author jadson
 *
 */
public enum CamposAtualizacaoCacheTitulos {
	
	/** O campo que guarda as inforam��es do T�tulo utilizados no cache de T�tulos  */
	TITULO(1, "T�TULO", AtualizacaoCacheTitulosDao.SQL_BUSCA_TITULO, "titulo", ""),
	
	/** O campo que guarda as inforam��es do T�tulo Ascii utilizados no cache de T�tulos  */
	TITULO_ASCII(2, "T�TULO ASCII", AtualizacaoCacheTitulosDao.SQL_BUSCA_OUTRAS_INFORMACOES_TITULO, "", "outras_informacoes_titulo_ascii"),
	
	/** O campo que guarda as inforam��es do T�tulo utilizados no cache de T�tulos  */
	MEIO_PUBLICACAO(3, "MEIO DE PUBLICA��O", AtualizacaoCacheTitulosDao.SQL_BUSCA_MEIO_PUBLICACAO, "meio_publicacao", ""),
	
	/** O campo que guarda as inforam��es do SubT�tulo utilizados no cache de T�tulos  */
	SUB_TITULO(4, "SUBT�TULO", AtualizacaoCacheTitulosDao.SQL_BUSCA_SUBTITULO, "sub_titulo", ""),
	
	/** O campo que guarda as inforam��es do formas variantes do T�tulo utilizados no cache de T�tulos  */
	FORMAS_VARIANTES_DO_TITULO(5, "FORMAS VARIANTES DO T�TULO", AtualizacaoCacheTitulosDao.SQL_BUSCA_FORMAS_VARIANTES_TITULO, "formas_varientes_titulo", ""),
	
	/** O campo que guarda as inforam��es do T�tulo iniforme utilizados no cache de T�tulos  */
	TITULO_UNIFORME(6, "T�TULO UNIFORME", AtualizacaoCacheTitulosDao.SQL_BUSCA_TITULO_UNIFORME, "", "titulo_uniforme_ascii"),
	
	/** O campo que guarda as inforam��es do autor utilizados no cache de T�tulos  */
	AUTOR(7, "AUTOR", AtualizacaoCacheTitulosDao.SQL_BUSCA_AUTOR, "autor", "autor_ascii"), // autor e autor_ascii
	
	/** O campo que guarda as inforam��es do Autor secund�rios utilizados no cache de T�tulos  */
	AUTOR_SECUNDARIO(8, "AUTOR SECUND�RIO", AtualizacaoCacheTitulosDao.SQL_BUSCA_AUTOR_SECUNDARIO, "autores_secundarios", "autores_secundarios_ascii"), // autor secund�rio e autor secund�rio ascii
	
	/** O campo que guarda as inforam��es do ASSUNTO utilizados no cache de T�tulos  */
	ASSUNTO(9, "ASSUNTO", AtualizacaoCacheTitulosDao.SQL_BUSCA_ASSUNTO, "assunto", "assunto_ascii"), //assunto e assunto ascii
	
	/** O campo que guarda as inforam��es do ISBN utilizados no cache de T�tulos  */
	ISBN(10, "ISBN", AtualizacaoCacheTitulosDao.SQL_BUSCA_ISBN, "isbn", ""),
	
	/** O campo que guarda as inforam��es do ISSN utilizados no cache de T�tulos  */
	ISSN(11, "ISSN", AtualizacaoCacheTitulosDao.SQL_BUSCA_ISSN, "issn", ""),
	
	/** O campo que guarda as inforam��es do CDU utilizados no cache de T�tulos  */
	CLASSIFICACAO1(12, "CLASSIFICA��O 1", AtualizacaoCacheTitulosDao.SQL_BUSCA_CLASSIFICACAO_1, "classificacao_1", "classificacao_1_ascii"),
	
	/** O campo que guarda as inforam��es do Black utilizados no cache de T�tulos  */
	CLASSIFICACAO2(13, "CLASSIFICA��O 2", AtualizacaoCacheTitulosDao.SQL_BUSCA_CLASSIFICACAO_2, "classificacao_2", "classificacao_2_ascii"),
	
	/** O campo que guarda as inforam��es do CDU utilizados no cache de T�tulos  */
	CLASSIFICACAO3(14, "CLASSIFICA��O 3", AtualizacaoCacheTitulosDao.SQL_BUSCA_CLASSIFICACAO_3, "classificacao_3", "classificacao_3_ascii"),
	
	/** O campo que guarda as inforam��es da s�rie utilizados no cache de T�tulos  */
	SERIE(15, "S�RIE", AtualizacaoCacheTitulosDao.SQL_BUSCA_SERIE, "serie", "serie_ascii"), //s�rie e s�rie ascii
	
	/** O campo que guarda as inforam��es da s�rie utilizados no cache de T�tulos  */
	DESCRICAO_FISICA(16, "DESCRI��O F�SICA", AtualizacaoCacheTitulosDao.SQL_BUSCA_DESCRICAO_FISICA, "descricao_fisica", ""),
	
	/** O campo que guarda as inforam��es do edi��o utilizados no cache de T�tulos  */
	EDICAO(17, "EDI��O", AtualizacaoCacheTitulosDao.SQL_BUSCA_EDICAO, "edicao", ""),
	
	/** O campo que guarda as inforam��es do local de publica��o utilizados no cache de T�tulos  */
	LOCAL_PUBLICACAO(18, "LOCAL PUBLICA��O",  AtualizacaoCacheTitulosDao.SQL_BUSCA_LOCAL_PUBLICACAO, "local_publicacao", "local_publicacao_ascii"),  // e o campo ascii
	
	/** O campo que guarda as inforam��es do Editora utilizados no cache de T�tulos  */
	EDITORA(19, "EDITORA", AtualizacaoCacheTitulosDao.SQL_BUSCA_EDITORA, "editora", "editora_ascii"),  // e o campo ascii
	
	/** O campo que guarda as inforam��es do Ano utilizados no cache de T�tulos  */
	ANO(20, "ANO", AtualizacaoCacheTitulosDao.SQL_BUSCA_ANO, "ano", ""),
	
	/** O campo que guarda as inforam��es do ano de publica��o utilizados no cache de T�tulos  */
	ANO_PUBLICACAO(21, "ANO PUBLICA��O ", AtualizacaoCacheTitulosDao.SQL_BUSCA_ANO_PUBLICACAO, "ano_publicacao", ""),
	
	/** O campo que guarda as inforam��es do idioma utilizados no cache de T�tulos  */
	IDIOMA(22, "IDIOMA", AtualizacaoCacheTitulosDao.SQL_BUSCA_IDIOMA, "", "idioma_ascii"),  // adicionado em 22/11/2011
	
	/** O campo que guarda as inforam��es das notas de conte�do utilizados no cache de T�tulos  */
	NOTAS_CONTEUDO(23, "NOTAS DE CONTE�DO ", AtualizacaoCacheTitulosDao.SQL_BUSCA_NOTAS, "nota_de_conteudo", ""),
	
	/** O campo que guarda as inforam��es das notas gerais utilizados no cache de T�tulos  */
	NOTAS_GERAIS(24, "NOTAS GERAIS ", AtualizacaoCacheTitulosDao.SQL_BUSCA_NOTAS, "notas_gerais", ""),
	
	/** O campo que guarda as inforam��es das notas locais utilizados no cache de T�tulos  */
	NOTAS_LOCAIS(25, "NOTAS LOCAIS ", AtualizacaoCacheTitulosDao.SQL_BUSCA_NOTAS, "notas_locais", ""),
	
	/** O campo que guarda as inforam��es das notas ascii utilizados no cache de T�tulos  */
	NOTAS_ASCII(26, "NOTAS ASCII ", AtualizacaoCacheTitulosDao.SQL_BUSCA_NOTAS, "", "notas_ascii"),  // notas gerais de conte�do, locais e notas_ascii
	
	/** O campo que guarda as inforam��es do endere�o eletr�nico utilizados no cache de T�tulos  */
	ENDERECO_ELETRONICO(27, "ENDERE�O ELETR�NICO ", AtualizacaoCacheTitulosDao.SQL_BUSCA_ENDERECO_ELETRONICO, "localizacao_endereco_eletronico", ""), 
	
	/** O campo que guarda as inforam��es do n�mero de chamada utilizados no cache de T�tulos  */
	NUMERO_CHAMADA(28, "N�MERO CHAMADA", AtualizacaoCacheTitulosDao.SQL_BUSCA_NUMERO_CHAMADA, "numero_chamada", ""),  
	
	/** O campo que guarda as inforam��es do resumo utilizados no cache de T�tulos  */
	RESUMO(29, "RESUMO", AtualizacaoCacheTitulosDao.SQL_BUSCA_RESUMO, "resumo", "");
	
	
	
	/** A descri��o do campo que vai ser atualiza��o, utilizado para o usu�rios escolher o campo.*/
	private String descricao;
	
	/**  A posi�o do campo. Utilizado para saber qual foi selecionado pelo usu�rio.*/
	private int posicao;
	
	/**  O SQL com as informa��es que precisam ser buscadas para atualiza as informa��es do campo no cache.*/
	private String sqlBuscaInformacoesMontarCampo;
	
	/** A coluna que ser� atualizada*/
	private String coluna;
	
	/** A coluna ascii que ser� atualizada*/
	private String colunaAscii;
	
	private CamposAtualizacaoCacheTitulos(int posicao, String descricao, String sqlBuscaInformacoesMontarCampo, String coluna, String colunaAscii){
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

	/** Utilizando nos campos de classifica��es bibliogr�ficas onde o sql deve ser configurado dinamicamente*/
	public void setSqlBuscaInformacoesMontarCampo(String sql) {
		this.sqlBuscaInformacoesMontarCampo = sql;
	}
	
	public String getColuna() {
		return coluna;
	}


	public String getColunaAscii() {
		return colunaAscii;
	}
	
	/** Retorna o sql utilizado para consultar as informa��es atuais do cache, para saber se precisa atualizar ou n�o.*/
	public String getSqlBuscaCampo(){
		String sqlSelect  = new String(" SELECT "+getColuna()+" FROM biblioteca.cache_entidades_marc WHERE id_titulo_catalografico = ? ");
		return sqlSelect;
	}
	
	/** Retorna o sql utilizado para atualizar as informa��es do cache .*/
	public String getSqlAtualizaCampo(){
		String sqlUpdate  = new String(" UPDATE biblioteca.cache_entidades_marc SET "+getColuna()+" = ?  WHERE id_titulo_catalografico = ? ");
		return sqlUpdate;
		
	}
	
	/** Retorna o sql utilizado para consultar as informa��es atuais do cache, para saber se precisa atualizar ou n�o.*/
	public String getSqlBuscaCampoAscii(){
		String sqlSelect  = new String(" SELECT "+getColunaAscii()+" FROM biblioteca.cache_entidades_marc WHERE id_titulo_catalografico = ? ");
		return sqlSelect;
	}
	
	/** Retorna o sql utilizado para atualizar as informa��es do cache .*/
	public String getSqlAtualizaCampoAscii(){
		String sqlUpdate  = new String(" UPDATE biblioteca.cache_entidades_marc SET "+getColunaAscii()+" = ? WHERE id_titulo_catalografico = ? ");
		return sqlUpdate;
		
	}
	
	/** Retorna a informa��o que vai ser atualizada de acordo com o campo passado */
	public String getInformacaoAtualizacaoCampo(CacheEntidadesMarc cacheTemp){
		
		if(this ==  CamposAtualizacaoCacheTitulos.TITULO ) return cacheTemp.getTitulo();
		if(this ==  CamposAtualizacaoCacheTitulos.SUB_TITULO ) return cacheTemp.getSubTitulo();
		if(this ==  CamposAtualizacaoCacheTitulos.MEIO_PUBLICACAO ) return cacheTemp.getMeioPublicacao();
		if(this ==  CamposAtualizacaoCacheTitulos.FORMAS_VARIANTES_DO_TITULO ) return cacheTemp.getFormasVarientesTitulo();
		if(this ==  CamposAtualizacaoCacheTitulos.TITULO_UNIFORME )  return cacheTemp.getTituloUniformeAscii();
		if(this ==  CamposAtualizacaoCacheTitulos.AUTOR ) return  cacheTemp.getAutor(); 
		if(this ==  CamposAtualizacaoCacheTitulos.AUTOR_SECUNDARIO ) return cacheTemp.getAutoresSecundarios();	
		if(this ==  CamposAtualizacaoCacheTitulos.ASSUNTO ) return cacheTemp.getAssunto();
		if(this ==  CamposAtualizacaoCacheTitulos.NUMERO_CHAMADA ) return cacheTemp.getNumeroChamada();
		if(this ==  CamposAtualizacaoCacheTitulos.ISBN ) return cacheTemp.getIsbn();
		if(this ==  CamposAtualizacaoCacheTitulos.ISSN ) return cacheTemp.getIssn();
		if(this ==  CamposAtualizacaoCacheTitulos.CLASSIFICACAO1 ) return cacheTemp.getClassificacao1();
		if(this ==  CamposAtualizacaoCacheTitulos.CLASSIFICACAO2 ) return cacheTemp.getClassificacao2();
		if(this ==  CamposAtualizacaoCacheTitulos.CLASSIFICACAO3 ) return cacheTemp.getClassificacao3();
		if(this ==  CamposAtualizacaoCacheTitulos.SERIE ) return cacheTemp.getSerie() ;
		if(this ==  CamposAtualizacaoCacheTitulos.DESCRICAO_FISICA ) return cacheTemp.getDescricaoFisica() ;
		if(this ==  CamposAtualizacaoCacheTitulos.EDICAO) return cacheTemp.getEdicao();
		if(this ==  CamposAtualizacaoCacheTitulos.LOCAL_PUBLICACAO ) return cacheTemp.getLocalPublicacao();
		
		if(this ==  CamposAtualizacaoCacheTitulos.EDITORA)return cacheTemp.getEditora();
		if(this ==  CamposAtualizacaoCacheTitulos.ANO) return cacheTemp.getAno();
		if(this ==  CamposAtualizacaoCacheTitulos.ANO_PUBLICACAO) return cacheTemp.getAnoPublicacao().toString();
		if(this ==  CamposAtualizacaoCacheTitulos.NOTAS_CONTEUDO ) return cacheTemp.getNotaDeConteudo();
		if(this ==  CamposAtualizacaoCacheTitulos.NOTAS_GERAIS ) return cacheTemp.getNotasGerais();
		if(this ==  CamposAtualizacaoCacheTitulos.NOTAS_LOCAIS ) return cacheTemp.getNotasLocais();
		if(this ==  CamposAtualizacaoCacheTitulos.ENDERECO_ELETRONICO) return cacheTemp.getLocalizacaoEnderecoEletronico();
		if(this ==  CamposAtualizacaoCacheTitulos.RESUMO) return cacheTemp.getResumo();
		
		return "";
	}
	
	
	/** Retorna a informa��o que vai ser atualizada de acordo com o campo passado */
	public String getInformacaoAtualizacaoCampoAscii(CacheEntidadesMarc cacheTemp){
		
		if(this ==  CamposAtualizacaoCacheTitulos.TITULO_ASCII ) return cacheTemp.getOutrasInformacoesTituloAscii();
		if(this ==  CamposAtualizacaoCacheTitulos.AUTOR )  return cacheTemp.getAutorAscii();
		if(this ==  CamposAtualizacaoCacheTitulos.AUTOR_SECUNDARIO ) return cacheTemp.getAutoresSecundariosAscii();
		if(this ==  CamposAtualizacaoCacheTitulos.ASSUNTO ) return cacheTemp.getAssuntoAscii();
		if(this ==  CamposAtualizacaoCacheTitulos.SERIE ) return cacheTemp.getSerieAscii() ;
		if(this ==  CamposAtualizacaoCacheTitulos.CLASSIFICACAO1 ) return cacheTemp.getClassificacao1Ascii();
		if(this ==  CamposAtualizacaoCacheTitulos.CLASSIFICACAO2 ) return cacheTemp.getClassificacao2Ascii();
		if(this ==  CamposAtualizacaoCacheTitulos.CLASSIFICACAO3 ) return cacheTemp.getClassificacao3Ascii();
		if(this ==  CamposAtualizacaoCacheTitulos.LOCAL_PUBLICACAO ) return cacheTemp.getLocalPublicacaoAscii();
		if(this ==  CamposAtualizacaoCacheTitulos.EDITORA)return cacheTemp.getEditoraAscii();
		if(this ==  CamposAtualizacaoCacheTitulos.NOTAS_ASCII ) return cacheTemp.getNotasAscii();
		if(this ==  CamposAtualizacaoCacheTitulos.IDIOMA) return cacheTemp.getIdiomaAscii();

		return "";
		
	}
	
	
}
