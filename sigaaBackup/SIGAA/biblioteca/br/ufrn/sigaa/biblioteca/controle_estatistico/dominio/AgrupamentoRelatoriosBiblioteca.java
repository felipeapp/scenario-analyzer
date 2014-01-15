/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;


/**
 *
 * <p>Representa um agrupamento que pode ser feito em consultas dos relat�rios da biblioteca.</p>
 * 
 * <p> OBSERVA��O: Para usar essas constantes de agrupamento, os nomes das vari�veis na consulta precisam ser padronizadas. </p>
 * 
 * @author jadson
 *
 */
public enum AgrupamentoRelatoriosBiblioteca {
	
		/** Agrupamento n�o foi escolhido pelo usu�rio */
		SEM_AGRUPAMENTO(-1, "Nenhum", "", "", "", ""),
	
		/** Agrupamento por classifica��o 1. */
		CLASSIFICACAO_1(1, ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao1() ? ClassificacoesBibliograficasUtil.getDescricaoClassificacao1() : "", "classificacao_1_agrupamento", "titulo.classe_principal_classificacao_1", "", "Sem classe"),
		
		/** Agrupamento por classifica��o 2. */
		CLASSIFICACAO_2(2, ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao2() ? ClassificacoesBibliograficasUtil.getDescricaoClassificacao2() : "", "classificacao_2_agrupamento", "titulo.classe_principal_classificacao_2", "", "Sem classe"),
		
		/** Agrupamento por classifica��o 3. */
		CLASSIFICACAO_3(3, ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao3() ? ClassificacoesBibliograficasUtil.getDescricaoClassificacao3() : "", "classificacao_3_agrupamento", "titulo.classe_principal_classificacao_3", "", "Sem classe"),
		
		/** Agrupamento por tipo de material. */
		TIPO_MATERIAL(4, "Tipo de Material", "tipoMaterial_agrupamento", "tipoMaterial.descricao", " INNER JOIN biblioteca.tipo_material AS tipoMaterial ON tipoMaterial.id_tipo_material = material.id_tipo_material ", "Sem Tipo de Material"),
		
		/** Agrupamento por cole��o. */
		COLECAO(5, "Cole��o", "colecao_agrupamento", "colecao.descricao", " INNER JOIN biblioteca.colecao AS colecao ON colecao.id_colecao = material.id_colecao ", "Sem Cole��o"),
		
		/** Agrupamento por situa��o de material. */
		SITUACAO_MATERIAL(6, "Situa��o do Material", "situacao_agrupamento", "situacao.descricao", " INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ", "Sem Situa��o de Material"),
		
		/** Agrupamento pela biblioteca de material. */
		BIBLIOTECA(7, "Biblioteca", "biblioteca_agrupamento", "biblioteca.descricao", " INNER JOIN biblioteca.biblioteca AS biblioteca ON biblioteca.id_biblioteca = material.id_biblioteca ", "Sem Biblioteca"),
		
		/** Agrupamento por M�s. */
		MES(8, "M�s", "mesAgrupamento", " EXTRACT(month from ? ) ", " ", "-1"),
		
		/** Agrupamento por Ano. */
		ANO(9, "Ano", "anoAgrupamento", " EXTRACT(year from ? ) ", " ", "-1");
		
		/**
		 * Nome da coluna de data usado no agrupamento por m�s e ano
		 */
		private String nomeColunaDataMesAno;
		
		private AgrupamentoRelatoriosBiblioteca(int valor, String alias, String nomeCampo, String campoAgrupamento,  String join, String substituiValoresNull) {
			this.valor = valor;
			this.alias = alias;
			this.nomeCampo = nomeCampo;
			this.campoAgrupamento = campoAgrupamento;
			this.join = join;
			this.substituiValoresNull = substituiValoresNull;
		}
		
		/** O valor do agrupamento*/
		public int valor;
		
		/** O nome do campo pelo qual a consulta ser� agrupada */
		public String campoAgrupamento;
		
		/** Alias padr�o para o campo. */
		public final String alias;
		
		/** Nome padr�o para o campo. */
		public final String nomeCampo;
		
		/** O join necess�rio para buscar o campo. */
		public final String join;
		
		/** Valor a ser retornado no lugar do null. */
		public final String substituiValoresNull;
	
		
		/**
		 * Retorna o campo de agrupamento utilizado, substituindo o nome da coluna de data da onde 
		 * vai se extrair o m�s o ano pelo nome da coluna utilizada na consulta.
		 *
		 * @return
		 */
		public String getCampoAgrupamento(){
			return campoAgrupamento.replace("?", nomeColunaDataMesAno);
		}

		/**
		 * 
		 * Configura o nome da coluna usado no agrupamento de M�s ou Ano, esse dado n�o � estatico 
		 * e deve ser informado no momento da utiliziza��o da vari�vel do enum. 
		 *
		 * @see findQtdMateriaisBaixadosSintetico() do Relatorio de Materiais Baixados Dao
		 *
		 * @param nomeColuna
		 */
		public void setNomeColunaDataMesAno(String nomeColuna){
			this.nomeColunaDataMesAno = nomeColuna; 
		}
		
		/** Verifica se o agrupamento � alguma agrupamento de classifica��o bibliogr�fica */
		public boolean isAgrupamentoClassificacaoBibliografica(){
			if( isAgrupamentoClassificacao1() || isAgrupamentoClassificacao2() || isAgrupamentoClassificacao3())
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � a primeira classifica��o bibliogr�fica */
		public boolean isAgrupamentoClassificacao1(){
			if(this == CLASSIFICACAO_1)
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � a segunda classifica��o bibliogr�fica */
		public boolean isAgrupamentoClassificacao2(){
			if(this == CLASSIFICACAO_2 )
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � a terceira classifica��o bibliogr�fica  */
		public boolean isAgrupamentoClassificacao3(){
			if( this == CLASSIFICACAO_3)
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � por biblioteca  */
		public boolean isAgrupamentoBiblioteca(){
			if(this == BIBLIOTECA)
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � por tipo de material  */
		public boolean isAgrupamentoTipoMaterial(){
			if(this == TIPO_MATERIAL)
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � por cole��o */
		public boolean isAgrupamentoColecao(){
			if(this == COLECAO )
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � por situa��o */
		public boolean isAgrupamentoSituacao(){
			if(this == SITUACAO_MATERIAL)
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � por M�s */
		public boolean isAgrupamentoMes(){
			if(this == MES)
				return true;
			else
				return false;
		}
		
		/** Verifica se o agrupamento � por Ano */
		public boolean isAgrupamentoAno(){
			if(this == ANO)
				return true;
			else
				return false;
		}
		
		
		/** Verifica se o agrupamento � por Ano */
		public boolean isSemAgrupamento(){
			if(this == SEM_AGRUPAMENTO)
				return true;
			else
				return false;
		}
		
		
		/**
		 * Retorna o agrupamento correspondente � constante.
		 * 
		 * @param constAgrup
		 * @return
		 */
		public static AgrupamentoRelatoriosBiblioteca getAgrupamento(int constAgrup) {
			
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_1.valor)
				return AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_1;
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_2.valor)
				return AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_2;
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_3.valor)
				return AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_3;
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.valor)
				return AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL;
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.COLECAO.valor)
				return AgrupamentoRelatoriosBiblioteca.COLECAO;
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL.valor)
				return AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL;
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.BIBLIOTECA.valor)
				return AgrupamentoRelatoriosBiblioteca.BIBLIOTECA;
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.ANO.valor)
				return AgrupamentoRelatoriosBiblioteca.ANO;
			if(constAgrup == AgrupamentoRelatoriosBiblioteca.MES.valor)
				return AgrupamentoRelatoriosBiblioteca.MES;
			
			return AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO;
		}
		
}
