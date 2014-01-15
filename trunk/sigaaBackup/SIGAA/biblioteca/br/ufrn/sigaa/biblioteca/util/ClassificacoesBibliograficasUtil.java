/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CamposMarcClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica.OrdemClassificacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaAreaCNPq;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p>Classe auxiliar com métodos utilitários para trabalhar com as classificações bibliográficas 
 * utilizadas no sistema. </p>
 *
 * <p>Essa classe também mantém em cache na memória quais classificações o sistema está utilizando 
 * e a descrição dessas classificações para exibição do usuário. É preciso manter em cache para não realizar
 *  novas buscas sempre que essa informações necessitar ser acessada.</p>
 *
 * <p> <i> Essa classe substitui a classe ConfiguraClassificacoesBibliograficasImpl que continha 
 * regras fixas para as classificações e classes desses classificações utilizadas na UFRN.
 * </i> </p>
 * 
 * @author jadson
 * @see ClassificacaoBibliografica
 */
public class ClassificacoesBibliograficasUtil {

	/**
	 * A tempo em que o cache das informações classificação vai ser atualizado.
	 */
	private static Date lastRefreshTime;
	
	/**
	 * Tempo EM SEGUNDOS que o sistema vai atualizar as informações das classificações no banco ( A cada 30 mim )
	 */
	private static final int REFRESH_TIME_CACHE_DIGITAIS = 1800; 
	
	/** Variável que informa que o sistema está utilizando a primeira classificação */
	private static boolean isSistemaUtilizandoClassificacao1;
	/** Variável que informa que o sistema está utilizando a segunda classificação */
	private static boolean isSistemaUtilizandoClassificacao2;
	/** Variável que informa que o sistema está utilizando a terceira classificação */
	private static boolean isSistemaUtilizandoClassificacao3;
	
	/** Retorna a descrição da primeira  classificação utilizada no sistema */
	private static String descricaoClassificacao1 = "";
	/** Retorna a descrição da segunda  classificação utilizada no sistema */
	private static String descricaoClassificacao2 = "";
	/** Retorna a descrição da terceira  classificação utilizada no sistema */
	private static String descricaoClassificacao3 = "";
	
	/** As classes principais da classificação 1 */
	private  static List<String> classesPincipaisClassificacao1 = new ArrayList<String>();
	
	/** As classes principais da classificação 2 */
	private  static List<String> classesPincipaisClassificacao2 = new ArrayList<String>();
	
	/** As classes principais da classificação 3 */
	private  static List<String> classesPincipaisClassificacao3 = new ArrayList<String>();
	
	/**
	 * <p>Método que verifica no banco se o sistema está trabalhando com a primeira classificação.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isSistemaUtilizandoClassificacao1(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna false se der erro ao buscar no banco
			}
		}
		return isSistemaUtilizandoClassificacao1;
	}
	
	
	/**
	 * <p>Método que verifica no banco se o sistema está trabalhando com a primeira classificação.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isSistemaUtilizandoClassificacao2(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna false se der erro ao buscar no banco
			}
		}
		return isSistemaUtilizandoClassificacao2;
	}
	
	
	/**
	 * <p>Método que verifica no banco se o sistema está trabalhando com a primeira classificação.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isSistemaUtilizandoClassificacao3(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna false se der erro ao buscar no banco
			}
		}
		return isSistemaUtilizandoClassificacao3;
	}
	
	
	
	/**
	 * <p>Método que retorna .</p>
	 * @return
	 * @throws DAOException 
	 */
	public static String getDescricaoClassificacao1(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna vazio se der erro ao buscar no banco
			}
		}
		return descricaoClassificacao1;
	}
	
	
	/**
	 * <p>Método que verifica no banco se o sistema está trabalhando com a segunda classificação.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static String getDescricaoClassificacao2(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna vazio se der erro ao buscar no banco
			}
		}
		return descricaoClassificacao2;
	}
	
	
	/**
	 * <p>Método que verifica no banco se o sistema está trabalhando com a terceira classificação.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static String getDescricaoClassificacao3(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna vazio se der erro ao buscar no banco
			}
		}
		return descricaoClassificacao3;
	}
	
	
	/**
	 * <p>Método que retorna .</p>
	 * @return
	 * @throws DAOException 
	 */
	public static List<String> getClassesPrincipaisClassificacao1(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
				Collections.sort(classesPincipaisClassificacao1);
			} catch (DAOException e) {
				// retorna coleção vazia caso der erro ao buscar no banco
			}
		}
		
		return classesPincipaisClassificacao1;
	}
	
	
	/**
	 * <p>Método que verifica no banco se o sistema está trabalhando com a segunda classificação.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static List<String> getClassesPrincipaisClassificacao2(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
				Collections.sort(classesPincipaisClassificacao2);
			} catch (DAOException e) {
				// retorna coleção vazia caso der erro ao buscar no banco
			}
		}
		return classesPincipaisClassificacao2;
	}
	
	
	/**
	 * <p>Método que verifica no banco se o sistema está trabalhando com a terceira classificação.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static List<String> getClassesPrincipaisClassificacao3(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
				Collections.sort(classesPincipaisClassificacao3);
			} catch (DAOException e) {
				// retorna coleção vazia caso der erro ao buscar no banco
			}
		}
		
		return classesPincipaisClassificacao3;
	}
	
	
	
	/**
	 * Verifia se alguma das tag passadas correspondes a campos de classificação bibliográficas.
	 *
	 * @param tag
	 * @return
	 */
	public static boolean isCampoUtilizadoClassificacao(String... tags){
		
		for (String tagCampos : tags) {
			if( Arrays.asList(CamposMarcClassificacaoBibliografica.getCamposClassificacao()).contains(tagCampos)
					|| Arrays.asList(CamposMarcClassificacaoBibliografica.getCamposClassificacao()).contains(tagCampos) ){
				return true;
			}
		}

		return false;
	}
	
	
	/**
	 * Retorna a classificação correspondente ao campo MARC passsado.
	 *
	 * @param classificacoes
	 * @param campoPassado
	 * @return
	 */
	public static ClassificacaoBibliografica encontraClassificacaoByCampo(List<ClassificacaoBibliografica> classificacoes, String... camposPassado){
		
		if(classificacoes != null)
		for (ClassificacaoBibliografica classificacaoBibliografica : classificacoes) {
			for (String campo : camposPassado) {
				if(classificacaoBibliografica.getCampoMARC().getCampo().equalsIgnoreCase(campo))
					return classificacaoBibliografica;
			}
			
		}
		
		return null;
	}
	
	/**
	 * Retorna a classificação correspondente a ordem passada
	 *
	 * @param classificacoes
	 * @param campoPassado
	 * @return
	 */
	public static ClassificacaoBibliografica encontraClassificacaoByCampo(List<ClassificacaoBibliografica> classificacoes, OrdemClassificacao ordem){
		
		if(classificacoes != null)
		for (ClassificacaoBibliografica classificacaoBibliografica : classificacoes) {
			if(classificacaoBibliografica.getOrdem() == ordem)
				return classificacaoBibliografica;
		}
		
		return null;
	}
	
	
	/**
	 * <p> Método que configura as informações das classes principais e áreas CNPQ das classificações passada para o Título passado. </p>
	 * 
	 * <p> Método é chamadado porque o sistema mantém em cache essas informações duplicadas para facilitar e melhar a performance das consultas dos relatórios. </p>
	 * 
	 * @throws DAOException 
	 */
	public static void configuraClassificacoesEAreasCNPQTitulo(TituloCatalografico titulo, ClassificacaoBibliografica... classificacoes) throws DAOException {
		
			if(classificacoes != null)
			for (ClassificacaoBibliografica classificacao : classificacoes) {
				
				if(classificacao == null) return;
				
				if(classificacao.isPrimeiraClassificacao()){
					// Apaga o que tinha antes ///
					titulo.zeraDadosClassificacao1();
				}
				
				if(classificacao.isSegundaClassificacao()){
					// Apaga o que tinha antes ///
					titulo.zeraDadosClassificacao2();
				}
				
				
				if(classificacao.isTerceiraClassificacao()){
					// Apaga o que tinha antes ///
					titulo.zeraDadosClassificacao3();
				}
				
				
				String classificacaoInformada = null;
				
				// percorre o Título até achar o campo e sub campo correspondente a classificação passada //
				if(titulo.getCamposDados() != null){
					forExterno:
					for (CampoDados dado : titulo.getCamposDados()) {
			
						if((dado != null && dado.getEtiqueta() != null) && classificacao.getCampoMARC() != null 
								&& dado.getEtiqueta().equals(new Etiqueta(classificacao.getCampoMARC().getCampo(), TipoCatalogacao.BIBLIOGRAFICA))){
			
							
							for (SubCampo sub : dado.getSubCampos()) {
								if(sub.getCodigo() != null && sub.getCodigo().equals( classificacao.getCampoMARC().getSubCampo()  ) ){
									classificacaoInformada = sub.getDado();
									break forExterno;
								}
							}
						}
					}
				}
				
				
				if ( StringUtils.notEmpty( classificacaoInformada ) ) {
					
					if(classificacao.isPrimeiraClassificacao()){
						titulo.setClassificacao1(classificacaoInformada); 
						titulo.setClassePrincipalClassificacao1(encontraClassePrincipal(classificacaoInformada, classificacao.getClassesPrincipaisClassificacaoBibliografica()));
						titulo.setAreaConhecimentoCNPQClassificacao1(encontraAreaConhecimentoCNPQClassificacao(classificacaoInformada, classificacao));
					
					}
					if(classificacao.isSegundaClassificacao()){
						titulo.setClassificacao2(classificacaoInformada); 
						titulo.setClassePrincipalClassificacao2(encontraClassePrincipal(classificacaoInformada, classificacao.getClassesPrincipaisClassificacaoBibliografica()));
						titulo.setAreaConhecimentoCNPQClassificacao2(encontraAreaConhecimentoCNPQClassificacao(classificacaoInformada, classificacao));
					}
					
					if(classificacao.isTerceiraClassificacao()){
						titulo.setClassificacao3(classificacaoInformada); 
						titulo.setClassePrincipalClassificacao3(encontraClassePrincipal(classificacaoInformada, classificacao.getClassesPrincipaisClassificacaoBibliografica()));
						titulo.setAreaConhecimentoCNPQClassificacao3(encontraAreaConhecimentoCNPQClassificacao(classificacaoInformada, classificacao) );
					}
					
				}
				
			} // for classificações
		
	}

	

	/**
	 * Enconta a classe principal da classificao bibliográfica passada.
	 *
	 * @param dadosClassificacaoDigitadaUsuario
	 * @param classesPrincipaisClassificacaoBibliografica
	 * @return
	 */
	public static String encontraClassePrincipal(String classificacaoBibliografica, List<String> classesPrincipaisClassificacaoBibliografica) {
		
		if(StringUtils.isEmpty(classificacaoBibliografica))
			return null;
		
		// Ordena as classes principais pelo tamnho e texto para poder usar corretamente o método startsWith
		Collections.sort(classesPrincipaisClassificacaoBibliografica, new Comparator<String>() {

			@Override
			public int compare(String s1, String s2) {
				
				if(s2.length() == s1.length()){
					return s1.compareTo(s2); // se for mesmo tamanho, compara pelo texto
				}else{
					return s2.length()- s1.length(); // se não, compara pelo tamanho da ordem inversa
				}
			}

		});
		
		
		// Algumas classificações podem conter parênteses (34) 5334.444,  então a classificação começa a contar depois do parênteses = 5334.444 
		
		final char abreParenteses = '(';
		final char fechaParenteses = ')';
		
		if(classificacaoBibliografica.startsWith(""+abreParenteses)){ // se contiver '(' e ')' a classe principal é o número depois do parênteses
			
			int ptr = classificacaoBibliografica.indexOf(""+abreParenteses);
			
			while(ptr < classificacaoBibliografica.length() && classificacaoBibliografica.charAt(ptr) != fechaParenteses ){
				ptr++;
			}
			
			if(ptr < classificacaoBibliografica.length()-1 ) // se não terminou porque chegou no final, anda mais 1 para retirar o ')'
				ptr++;
			
			String classificacaoDepoisParenteses = classificacaoBibliografica.substring(ptr, classificacaoBibliografica.length());
			
			return encontraClassePrincipal(classificacaoDepoisParenteses, classesPrincipaisClassificacaoBibliografica); // chama novamente o método para achar a classe principal com a informação depois do parêntestes
		}
		
		
		
		
		/*
		 * Para cada classe principal, verifica se o valor numérico dos X primeiros dígitos da 
		 * classificação está entre a classe principal atual e a próxima e se o primeiro dígito é igual a classe atual,
		 *  se positivo, então encontramos a classe principal.
		 * 
		 * X = número de dígitos da classe principal.
		 * 
		 * Importente para suportar as regras das classificações CDU e CDD. Haja visto as classes principais CDU precisamos apanes 
		 * verificar se o número começa com a classe principal, já na CDD , "500" é a classe principal para "513", apezar de "513" não começar com "500".
		 * 
		 * Para a classe BLACK, tentamos tratrar do mesmo jeito a classificação e classes principais, retirando os "D" iniciais nos números das classificações.
		 * 
		 * Esse algortimo serve para encontrar as classes principais das principais classificações utilizadas nas bibliotecas: CDU, CDD e BLACK.
		 * 
		 */
		StringBuilder classificacaoTemp = new StringBuilder(classificacaoBibliografica);
		
		while( classificacaoTemp.length() > 0  &&  ! Character.isDigit(classificacaoTemp.charAt(0))){
			classificacaoTemp = classificacaoTemp.deleteCharAt(0);     // Retira os "D" ou caracteres iniciais das classificações bibliográficas
		}
		
		
		for (int index = 0 ; index < classesPrincipaisClassificacaoBibliografica.size(); index++ ) {
			
			boolean possuiProximaClassePrincipal = index+1 < classesPrincipaisClassificacaoBibliografica.size() ? true : false ;
			
			String classePrincipal = classesPrincipaisClassificacaoBibliografica.get(index);
			String proximaClassePrincipal =  possuiProximaClassePrincipal ? classesPrincipaisClassificacaoBibliografica.get(index+1) : null;
			
			StringBuilder classePrincipalTemp = new StringBuilder(classePrincipal);
			StringBuilder proximaClassePrincipalTemp = possuiProximaClassePrincipal ? new StringBuilder(proximaClassePrincipal) : null;
			
			while( classePrincipalTemp.length() > 0 && ! Character.isDigit(classePrincipalTemp.charAt(0))){
				classePrincipalTemp = classePrincipalTemp.deleteCharAt(0); // Retira os "D" ou caracteres iniciais das classificações bibliográficas
			}
			
			if(possuiProximaClassePrincipal){
				while( proximaClassePrincipalTemp.length() > 0 &&  ! Character.isDigit(proximaClassePrincipalTemp.charAt(0))){
					proximaClassePrincipalTemp = proximaClassePrincipalTemp.deleteCharAt(0);     // Retira os "D" ou caracteres iniciais das classificações bibliográficas
				}
			}
			
			
			int qtdDigitos = classePrincipalTemp.length(); // quantidade de dígitos comparáveis
			
			if(qtdDigitos > classificacaoTemp.length())
				qtdDigitos =  classificacaoTemp.length();
			
			int valorClassificacacao = 0;
			int valorClassePrincipal = 0;
			int valorProximaClassePrincipal = 0; 
			
			try{
				
				valorClassificacacao = Integer.parseInt(classificacaoTemp.substring(0, qtdDigitos)); // O valor numérico dos X primeiros dígitos da classificação
				
				valorClassePrincipal = Integer.parseInt(classePrincipalTemp.toString());
				valorProximaClassePrincipal = possuiProximaClassePrincipal ? Integer.parseInt(proximaClassePrincipalTemp.toString()) : 0;
				
		
				if(possuiProximaClassePrincipal){
					
					// Se o valor está entre o interfava e o primeiro caracter e igual ao primeiro caracter da classe principal //
					if(valorClassificacacao >= valorClassePrincipal && valorClassificacacao < valorProximaClassePrincipal
							&& isPrimeiroCaracterIgual( classificacaoTemp.toString(), classePrincipalTemp.toString()) ){
						return classePrincipal;
					}
				}else{
					if(valorClassificacacao >= valorClassePrincipal
							&& isPrimeiroCaracterIgual( classificacaoTemp.toString(), classePrincipalTemp.toString()) ){
						return classePrincipal;
					}
				}
			
			}catch(NumberFormatException nfe){
				
				// Não é um número, então compara com startsWith
				if(classificacaoBibliografica.toUpperCase().trim().startsWith(classePrincipal) ){
					return classePrincipal;
				}
			}
			
		}
		
		
		return null;
		
	}
	
	
	/** Verifica se o primeiro caracter das string passadas são iguais */
	private static boolean isPrimeiroCaracterIgual(String classificao, String classePrincial){
		return classificao.startsWith(""+classePrincial.toString().charAt(0));
	}
	
	
	/**
	 * Encontra a área de conhecimento CNPQ a partir da classe da classificação passada e do relacionamento entre áreas CNPq e classificações bibliograficas existente no sistema.
	 *
	 * @see RelacionaClassificacaoBibliograficaAreaCNPq
	 *
	 * @param grandesAreasCNPq
	 * @param dadosClassificacaoDigitadaUsuario
	 * @param classificacao
	 * @return
	 * @throws DAOException 
	 */
	private static AreaConhecimentoCnpq encontraAreaConhecimentoCNPQClassificacao( String dadosClassificacaoDigitadaUsuario, ClassificacaoBibliografica classificacao) throws DAOException {
		
		ClassificacaoBibliograficaDao dao = null;
		
		try{
			
			dao = DAOFactory.getInstance().getDAO(ClassificacaoBibliograficaDao.class);
			
			List<RelacionaClassificacaoBibliograficaAreaCNPq> relacionamentos =  dao.findInformacoesRelacionamentosAreasCNPqByClassificacaoParaValidacao(classificacao.getId());
			
			if(dadosClassificacaoDigitadaUsuario == null)  return null;
			
			boolean encontrouClasse = false;
			
			
			
			for (RelacionaClassificacaoBibliograficaAreaCNPq relacionamento : relacionamentos) {
				
				encontrouClasse = false;
				
				String[] classesInclusao = new String[0]; // estão separadas por espaço.
				String[] classesExclusao = new String[0];
				
				if(StringUtils.notEmpty(relacionamento.getClassesInclusao()))
					classesInclusao = relacionamento.getClassesInclusao().split("\\s");
				
				if(StringUtils.notEmpty(relacionamento.getClassesExclusao()))
					classesExclusao = relacionamento.getClassesExclusao().split("\\s");
			
				ordenaClassesPorTamanho(classesInclusao);
				ordenaClassesPorTamanho(classesExclusao);
				
				forClasses:
				for (String prefixo : classesInclusao) {
					
					if (StringUtils.isEmpty(prefixo))
							continue;
					
					boolean comecaPrexicoCadastrado = false;
					
					if(prefixo.contains("-")){  // é um intervalo de classes CPF
						comecaPrexicoCadastrado = estaDentroIntervaloClassificacao(prefixo, dadosClassificacaoDigitadaUsuario);
					}else{
						comecaPrexicoCadastrado =  dadosClassificacaoDigitadaUsuario.startsWith(prefixo);
					}
					
					if (comecaPrexicoCadastrado ){  // se começa com o prefixo cadastrado
						
						encontrouClasse = true;
						
						for (String prefixoExclusao : classesExclusao) { // e não começa com os prefixo de exclusão
							
							if (StringUtils.isEmpty(prefixoExclusao))
								continue;
							
							boolean naoComecaPrexicoCadastrado = false;
							
							if(prefixoExclusao.contains("-")){  // é um intervalo de classes CPF
								naoComecaPrexicoCadastrado = estaDentroIntervaloClassificacao(prefixoExclusao, dadosClassificacaoDigitadaUsuario);
							}else{
								naoComecaPrexicoCadastrado =  dadosClassificacaoDigitadaUsuario.startsWith(prefixoExclusao);
							}
							
							if (  naoComecaPrexicoCadastrado ){
								encontrouClasse = false;
							}
						}
						
						if(encontrouClasse){
							break forClasses;
						}
					}
					
				}
				
				if(encontrouClasse){
					return relacionamento.getArea();
				}
				
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 *    Ordena as classes por tamanho. Os maiores devem vir primeiro, já que a comparação tem que ser com <tt>startsWith</tt>.
	 */
	private static void ordenaClassesPorTamanho(String[] classes){
	
		for (int i = 0; i < classes.length-1; i++) {
			for (int j = i+1; j < classes.length; j++) {
				
				if(classes[i].length() < classes[j].length()){
					String temp = classes[j];
					classes[j] = classes[i];
					classes[i] = temp;
				}
			}
		}
		
	}
	
	
	/**
	 *   <p>Método que verifica se a classificação cdu está dentro do intervalo passado.</p>
	 *   <p>Por exemplo, verifica se a classificação cdu 681.271 está dentro do intervalo 681.1-681.270 </p>
	 *
	 * @param intervaloClassificacao
	 * @param classificacaoCDUdoTitulo
	 * @return
	 */
	private static boolean estaDentroIntervaloClassificacao(String intervaloClassificacao, String classificacaoTitulo){
		
		if(StringUtils.isEmpty(classificacaoTitulo))
			return false;
		else{
			try{
			
				String[] temp =  intervaloClassificacao.split("-");
				
				
					
				if( StringUtils.notEmpty(temp[0])){
					temp[0] = retiraCaractaresInicialNaoDigitos(temp[0]);
					
				}
				
				if( StringUtils.notEmpty(temp[1])){
					temp[1] = retiraCaractaresInicialNaoDigitos(temp[1]);	
				}
				
				if( StringUtils.notEmpty(classificacaoTitulo)){
					classificacaoTitulo = retiraCaractaresInicialNaoDigitos(classificacaoTitulo);
					
				}
					
				float f1 = new Float(temp[0]);
				float f2 = new Float(temp[1]);
				
				int tamanhoIntervalo = temp[0].length();
				
				if(tamanhoIntervalo < temp[1].length()){
					tamanhoIntervalo = temp[1].length();
				}
				
				// tentar converter os dados da classificação do Título em um número para pode chegar o intervalo
				if(classificacaoTitulo == null)
					return false;
				
				// Para comprarar intervalos, tem que recuperar os primeiros caracteres numérico
				// por exemplo CDU = "1(44)",   recuperar somente o '1'
				String classificacaoTituloNumera = "";
				
				for (int posicao = 0; posicao < classificacaoTitulo.length(); posicao++) {
					
					if( Character.isDigit(classificacaoTitulo.charAt(posicao)))
						classificacaoTituloNumera += classificacaoTitulo.charAt(posicao);
					else{
						break;  // no primeiro caracter não dígito, sai do laco
					}
				}
				
				if(classificacaoTituloNumera.length() > tamanhoIntervalo)
					classificacaoTituloNumera = classificacaoTituloNumera.substring(0, tamanhoIntervalo);
				else{
					if(classificacaoTituloNumera.length() < tamanhoIntervalo){
						
						// Por exemplo:  Intervalo definido de 100 a 179 e classificação = 1,
						// tem que comparar 100 <= 100 <= 179  se comparar 100 <= 1 <= 179 vai ser false
						// quando na verdade está entre o intervalo
						
						for (int index = classificacaoTituloNumera.length(); index < tamanhoIntervalo; index++) {
							classificacaoTituloNumera = classificacaoTituloNumera+"0";
						}
					}else{
						// se for do mesmo tamanho não precisa fazer nada
					}
				}
				
				float f3 = new Float( classificacaoTituloNumera );
				
				if(f1 <= f3 && f3 <= f2){
					return true;
				}else{
					return false;
				}
			
			}catch(NumberFormatException nfe){
				/* se os dados do intervalo ou classificação não podem ser convertidos para
				 * números não dá para testar se a classificação está dentro do intervalo
				 */
				return false;
			}
		}
	}


	/**
	 * Retira os que não for dígito do intervalo informado pelo usuário. Algumas classificações 
	 * possuiem letras que não dá para calcular o intervalo.
	 * 
	 * Exemplo  A classificação black começa as classes com "D" 
	 *  
	 * @param temp
	 */
	private static String retiraCaractaresInicialNaoDigitos(String temp) {
			
		if( ! Character.isDigit(temp.charAt(0)) ){
			return temp.subSequence(1, temp.length()).toString(); 
			
		}
		return temp;
	}
	
	
	
	
	
	
	/**
	 * Remove as classificações bibliográficas e suas respectivas áreas de conhecimento CNPq de campos remotivos do Titulo pelo usuário.<br/>
	 * 
	 */
	public static void removeClassificacoesNaoInformadas(TituloCatalografico titulo, List<ClassificacaoBibliografica> classificacoesUtilizada){
		
		boolean classifcacao1Presente = false;
		boolean classifcacao2Presente = false;
		boolean classifcacao3Presente = false;

		// Percorre os campos de dados
		if(titulo != null && titulo.getCamposDados() != null){
			
			
			// Verifica para cada classificação configurada no sistema se os dados dela foram preenchidos //
			
			for (ClassificacaoBibliografica classificacaoUtilizada : classificacoesUtilizada) {
				if(classificacaoUtilizada.isPrimeiraClassificacao() ){
					classifcacao1Presente = verificaSeCampoCorrepondendeClassificacaoPreenchido(titulo, classificacaoUtilizada);
				}	
				else if(classificacaoUtilizada.isSegundaClassificacao() ){
					classifcacao2Presente = verificaSeCampoCorrepondendeClassificacaoPreenchido(titulo, classificacaoUtilizada);
				}	
				else if(classificacaoUtilizada.isTerceiraClassificacao() ){
					classifcacao3Presente = verificaSeCampoCorrepondendeClassificacaoPreenchido(titulo, classificacaoUtilizada);
				}
			}
			
			if(! classifcacao1Presente)
				titulo.zeraDadosClassificacao1(); 
			
			if(! classifcacao2Presente)
				titulo.zeraDadosClassificacao2(); 
			
			if(! classifcacao3Presente)
				titulo.zeraDadosClassificacao3(); 
				
			// Só chamar depois que atribuio o valor "null"  ///
			titulo.anularAreasCNPqNaoInformadas();
			
		}
		
	}


	
	
	
	/**
	 * Verifica se o Título passado possui o campo correpondende à classificação passada preenchido pelo usuário
	 */
	public static boolean verificaSeCampoCorrepondendeClassificacaoPreenchido(TituloCatalografico titulo, ClassificacaoBibliografica classificacaoUtilizada){
		
		// Percorre os campos de dados
		if(titulo != null && titulo.getCamposDados() != null){
			
			for (CampoDados dados : titulo.getCamposDados()) {
				
				if(dados.getEtiqueta().equals(new Etiqueta(classificacaoUtilizada.getCampoMARC().getCampo(), TipoCatalogacao.BIBLIOGRAFICA))){
					
					if(dados.getSubCampos() != null)
						for (SubCampo sub : dados.getSubCampos()) { // se tem o sub campo 'a' com dados ok
							if(StringUtils.notEmpty( sub.getDado()) && sub.getCodigo() != null && sub.getCodigo().equals(   classificacaoUtilizada.getCampoMARC().getSubCampo()   )   ){
								return true;
							} 
						}
				}
				
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * Retorna a informação contida no campo correspondente a informação passada.
	 */
	public static String retornaInformacaoCampoDaClassificacao(TituloCatalografico titulo, ClassificacaoBibliografica classificacaoUtilizada){
		
		// Percorre os campos de dados
		if(titulo != null && titulo.getCamposDados() != null){
			
			for (CampoDados dados : titulo.getCamposDados()) {
				
				if(dados.getEtiqueta().equals(new Etiqueta(classificacaoUtilizada.getCampoMARC().getCampo(), TipoCatalogacao.BIBLIOGRAFICA))){
					
					if(dados.getSubCampos() != null)
						for (SubCampo sub : dados.getSubCampos()) { // se tem o sub campo 'a' com dados ok
							if(StringUtils.notEmpty( sub.getDado()) && sub.getCodigo() != null && sub.getCodigo().equals(   classificacaoUtilizada.getCampoMARC().getSubCampo()   )   ){
								return sub.getDado();
							} 
						}
				}
				
			}
		}
		
		return null; // a informação do campo não está preenchida
	}
	
	
	
	/**
	 * Método que verifica se já está no tempo de atualizar o cache
	 *
	 * @return
	 */
	private static boolean expirouTempoAtualizaCache(){
		if(lastRefreshTime == null) 
			return true;
		else 
			return  ( System.currentTimeMillis() - lastRefreshTime.getTime() ) > ( REFRESH_TIME_CACHE_DIGITAIS * 1000 );
	}
	
	
	/**
	 * Atualiza as inforamções das classificações utilizadas pelo sistema quando o tempo de atualização expirou.
	 *  
	 * @throws DAOException
	 */
	private synchronized static void atualizaInformacoesClassificacaoUtilizadaSistema() throws DAOException {
		ClassificacaoBibliograficaDao dao = null;
		try{
			dao =  DAOFactory.getInstance().getDAO(ClassificacaoBibliograficaDao.class);
			
			isSistemaUtilizandoClassificacao1 =  dao.isSistemaTrabalhaClassificacao(OrdemClassificacao.PRIMERA_CLASSIFICACAO);
			isSistemaUtilizandoClassificacao2 =  dao.isSistemaTrabalhaClassificacao(OrdemClassificacao.SEGUNDA_CLASSIFICACAO);
			isSistemaUtilizandoClassificacao3 =  dao.isSistemaTrabalhaClassificacao(OrdemClassificacao.TERCEIRA_CLASSIFICACAO);
			
			if(isSistemaUtilizandoClassificacao1){
				descricaoClassificacao1  =  dao.getDescricaoClassificacao(OrdemClassificacao.PRIMERA_CLASSIFICACAO);
				classesPincipaisClassificacao1 = dao.getClassesPrincipaisClassificacao(OrdemClassificacao.PRIMERA_CLASSIFICACAO);
			}
			
			if(isSistemaUtilizandoClassificacao2){
				descricaoClassificacao2  =  dao.getDescricaoClassificacao(OrdemClassificacao.SEGUNDA_CLASSIFICACAO);
				classesPincipaisClassificacao2 = dao.getClassesPrincipaisClassificacao(OrdemClassificacao.SEGUNDA_CLASSIFICACAO);
			}
			
			if(isSistemaUtilizandoClassificacao3){
				descricaoClassificacao3  =  dao.getDescricaoClassificacao(OrdemClassificacao.TERCEIRA_CLASSIFICACAO);
				classesPincipaisClassificacao3 = dao.getClassesPrincipaisClassificacao(OrdemClassificacao.TERCEIRA_CLASSIFICACAO);
			}
			
			lastRefreshTime = new Date();
			
			ClassificacoesBibliograficasUtil.class.notifyAll(); // libera para outros thread entrarem na região crítica	
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
}
