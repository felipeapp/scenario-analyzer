/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/08/2009
 *
 */
package br.ufrn.sigaa.biblioteca.util;

import static br.ufrn.arq.util.StringUtils.toAsciiAndUpperCase;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.model.ListDataModel;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.LazyInitializationException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.biblioteca.InformacoesTombamentoMateriaisDTO;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EtiquetaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FormatoMaterialEtiquetaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TabelaCutterDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DadosTabelaCutter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorSubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterialEtiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.GrupoEtiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ObjetoPlanilhaCatalogacaoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ObjetoPlanilhaCatalogacaoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.PlanilhaCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorDescritorSubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorIndicador;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorPadraoCampoControle;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.MembroBancaPos;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 * Helper utilizado para ter métodos utilitários no caso de uso de catalogação da biblioteca.
 * 
 * @author Victor Hugo
 */
public class CatalogacaoUtil {
	
	/**
	 * Separador usado quando o campo é multivalorado, deixando os vários valores em uma só coluna
	 * no banco para otimizar as consultas. */
	public static final String SEPARADOR_VALORES_CACHE = "#$&";
	
	/**
	 * A expressão regular do Separador usado quando o campo é multivalorado,
	 */
	public static final String SEPARADOR_VALORES_CACHE_REGEX = "\\#\\$\\&";
	
	/**
	 * Separador usado para identificar o label do endereço eletrônico
	 */
	public static final String SEPARADOR_LABEL_ENDERECO_ELETRONICO = "@@@";

	/**
	 * Seprador utilizando no cache para colocar vários endereços eletrônicos em uma única coluna, separando o endereço do label dele.
	 */
	public static final String SEPARADOR_TEXTO_ENDERECO_ELETRONICO = "|||";
	
	/**
	 * Expressão Regular do Seprador utilizando no cache para colocar vários endereços eletrônicos em uma única coluna, separando o endereço do label dele.
	 */
	public static final String SEPARADOR_TEXTO_ENDERECO_ELETRONICO_REGEX  = "\\|\\|\\|";
	
	/** Artigos a serem desprezados na geração do Cutter. */
	public static final String[] ARTIGOS = {"O","A","Os","As","um","uma","uns","umas","do","da","dos","das","no","na","nos","nas",
		"ao", "à","aos","às","pelo","pela","pelos","pelas","co","coa","cos","coas","dum","duma","duns","dumas","num","numa","nuns","numas",
		"The","an", "some", "lo", "la", "del", "della", "dello", "de", "des", "la", "le", "un", "une", "El"};
	
	
	/**
	 * 
	 * <p>Recupera uma etiqueta persistida de um título, ou da lista em cache ou do banco, caso não tiver
	 * no cache da memória.</p>
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   Método não chamado por nenhuma página jsp.
	 *
	 * @return A etiqueta persistida caso ela seja encontrada, ou null caso não exista etiqueta procurada pelo usuário.
	 * @throws DAOException
	 */
	public static Etiqueta recuperaEtiquetaTitulo(Etiqueta etiquetaASerBuscada, List<Etiqueta> etiquetasBuscadas) throws DAOException{
		
		EtiquetaDao dao = null;
		
		try{
			if(etiquetasBuscadas.contains(etiquetaASerBuscada)){
				
				/* ***********************************************************************************
				 *                     SEMPRE RETORNAR UMA CÓPIA DA ETIQUETA NA LISTA EM CACHE
				 * ***********************************************************************************/
				
				// Caso contrário a os objetos do cache vão apontar para as etiqueta nos campos, se o usuário alterar no campo vai modificar no cache //
				
				Etiqueta etiquetaMemoria = etiquetasBuscadas.get(   etiquetasBuscadas.indexOf(etiquetaASerBuscada));
				
				// Retorna uma cópia da etiqueta do cache para o campo //
				return new Etiqueta(etiquetaMemoria.getId(), etiquetaMemoria.getTag(), etiquetaMemoria.getTipo(), etiquetaMemoria.getTipoCampo()
						, etiquetaMemoria.getDescricao(), etiquetaMemoria.isRepetivel(), etiquetaMemoria.isAtiva(), etiquetaMemoria.getInfo()
						, etiquetaMemoria.getDescricaoIndicador1(), etiquetaMemoria.getInfoIndicador1(), etiquetaMemoria.getDescricaoIndicador2()
						,etiquetaMemoria.getInfoIndicador2(), etiquetaMemoria.getFormatosMaterialEtiqueta(), etiquetaMemoria.getCategoriasMaterial(),
						etiquetaMemoria.getValoresIndicador(), etiquetaMemoria.getDescritorSubCampo());
			}else{
				
				dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
				
				Etiqueta etiquetaBanco = dao.findEtiquetaPorTagETipoAtivaInicializandoDados(etiquetaASerBuscada.getTag(), etiquetaASerBuscada.getTipo());
				
				if( etiquetaBanco != null){
				
					/* ***********************************************************************************
					 *                     SEMPRE CRIAR UMA NOVA ETIQUETA NA LISTA EM CACHE
					 * ***********************************************************************************/
					
					// Caso contrário a os objetos do cache vão apontar para as etiqueta nos campos, se o usuário alterar no campo vai modificar no cache //
					
					Etiqueta copia = new Etiqueta(etiquetaBanco.getId(), etiquetaBanco.getTag(), etiquetaBanco.getTipo(), etiquetaBanco.getTipoCampo()
							, etiquetaBanco.getDescricao(), etiquetaBanco.isRepetivel(), etiquetaBanco.isAtiva(), etiquetaBanco.getInfo()
							, etiquetaBanco.getDescricaoIndicador1(), etiquetaBanco.getInfoIndicador1(), etiquetaBanco.getDescricaoIndicador2()
							,etiquetaBanco.getInfoIndicador2(), etiquetaBanco.getFormatosMaterialEtiqueta(), etiquetaBanco.getCategoriasMaterial(),
							etiquetaBanco.getValoresIndicador(), etiquetaBanco.getDescritorSubCampo());
					
					// coloca etiquete busca no cache
					etiquetasBuscadas.add(etiquetaBanco); 
					
					
					// e retorna uma cópia da etiqueta para o campo
					return copia;
				}
				
			}
			
			return null;
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	/**
	 * <p>Método que retira a pontuação AACR2 </p>
	 * <p>Pontuação</p>
	 * <p>A pontuação serve para identificar e separar os elementos da descrição, os sinais de
     * pontuação da descrição são precedidos e seguidos de espaço, menos o ponto, a
     * vírgula e o hífen.
     * Os sinais usados na descrição são:</p>
     *
     * <ul>
     * <li>[ ] colchetes,</li>
     * <li>... reticências,</li>
     * <li>= igual,</li>
     * <li>/ barra inclinada,</li>
     * <li>: dois pontos,</li>
     * <li>; ponto e vírgula,</li>
     * <li>, vírgula,</li>
     * <li>. ponto,</li>
     * <li>? interrogação,</li>
     * <li>- hífen,</li>
     * <li>. - ponto espaço travessão espaço,</li>
     * <li>-- travessão;</li>
     * <li>+ sinal de adição,</li>
	 * <li>x sinal de multiplicação.</li>
	 * </ul>
	 * 
	 * http://cdij.pgr.mpf.gov.br/sistema-pergamum/ix-encontro-nacional/20_04_2007/Curso%20AACR2.pdf
	 * 
	 * @param dadoComPutuacaoAACR2
	 * @return
	 */
	public static String retiraPontuacaoAACR2(String dadoComPutuacaoAACR2, boolean retirarCaracteresEmPar){
		if(StringUtils.isEmpty(dadoComPutuacaoAACR2))
			return dadoComPutuacaoAACR2;
		
		if(retirarCaracteresEmPar){ // alguns () e [] não fazem parte da pontuação AACR2
			if(dadoComPutuacaoAACR2.contains("[") || dadoComPutuacaoAACR2.contains("]") ){
				dadoComPutuacaoAACR2 = dadoComPutuacaoAACR2.replace("[", "");
				dadoComPutuacaoAACR2 = dadoComPutuacaoAACR2.replace("]", "");
			}
			
			if(dadoComPutuacaoAACR2.contains("(") || dadoComPutuacaoAACR2.contains(")") ){
				dadoComPutuacaoAACR2 = dadoComPutuacaoAACR2.replace("(", "");
				dadoComPutuacaoAACR2 = dadoComPutuacaoAACR2.replace(")", "");
			}
		}
		for ( int posicao = dadoComPutuacaoAACR2.length()-1 ; posicao >= 0 ; posicao--) {
			
			if( ! Character.isWhitespace(dadoComPutuacaoAACR2.charAt(posicao)) ){ // achou a ultima letra
				
				/*
				 * Regra do :  ". -"  "ponto espaço travessão espaço"
				 * 
				 * Obs.: 150 código ascii do travessão
				 * 
				 */
				if(dadoComPutuacaoAACR2.charAt(posicao) == '-' || dadoComPutuacaoAACR2.charAt(posicao)  == 150 ){
					int posicaoTemp = posicao -1;
					
					if(  posicaoTemp >= 0 && Character.isWhitespace(dadoComPutuacaoAACR2.charAt(posicaoTemp))  ){
						posicaoTemp = posicaoTemp -1;
						
						if(posicaoTemp >= 0 && dadoComPutuacaoAACR2.charAt(posicaoTemp) == '.'){
							return dadoComPutuacaoAACR2.substring(0, posicaoTemp);
						}
					}
				}
				
				if(dadoComPutuacaoAACR2.charAt(posicao) == '='
					|| dadoComPutuacaoAACR2.charAt(posicao) == '/'
						|| dadoComPutuacaoAACR2.charAt(posicao) == ':'
							|| dadoComPutuacaoAACR2.charAt(posicao) == ';'
								|| dadoComPutuacaoAACR2.charAt(posicao) == ','
									|| dadoComPutuacaoAACR2.charAt(posicao) == '.'
										|| dadoComPutuacaoAACR2.charAt(posicao) == '?'
											|| dadoComPutuacaoAACR2.charAt(posicao) == '-'
											|| dadoComPutuacaoAACR2.charAt(posicao) == 150){
					return dadoComPutuacaoAACR2.substring(0, posicao); // se é uma pontuação, retorna a texto sem a pontuação.
				}else{
					return dadoComPutuacaoAACR2; // se não é retorna o texto normal.
				}
			}
		}
		
		return dadoComPutuacaoAACR2;
	}
	
	/**
	 *   Retira a pontuação existente nos campos MARC para não influenciar nas buscas.
	 */
	public static String retiraPontuacaoCamposBuscas(String campoBuscaAscii){
	
		if(StringUtils.notEmpty(campoBuscaAscii)){
			campoBuscaAscii = campoBuscaAscii.replaceAll("/", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll(":", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\.", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("-", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\+", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll(",", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll(";", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\"", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\'", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("!", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\?", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("#", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\$", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("@", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("%", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("¨", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("&", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\*", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\\\", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("_", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("=", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("§", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\|", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\[", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\]", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\{", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\}", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\(", " ");
			campoBuscaAscii = campoBuscaAscii.replaceAll("\\)", " ");
			
		}
		return campoBuscaAscii;
	}
	
	
	
	/**
	 *    <p>Método que cria ou atualiza as informações do campo de controle 005 </p>
	 *    <p> Deve ser chamado sempre que qualquer entidade que use o formato MARC, do sistema for
	 *    criada ou atualizar</p>
	 *
	 * @param objetoMarc
	 * @throws DAOException
	 */
	public static void configuraDataHoraUltimaIntervencao(Object objetoMarc) throws DAOException{
		
		boolean possuiCampo005 = false;
		
		EtiquetaDao dao = null;
		
		try{
		
			if(objetoMarc instanceof TituloCatalografico){
				
				TituloCatalografico titulo = (TituloCatalografico) objetoMarc;
				
				if(titulo.getCamposControle() != null){
					for (CampoControle c : titulo.getCamposControle()) {
						
						if(c.getEtiqueta() != null)
						if(c.getEtiqueta().equals(Etiqueta.CAMPO_005_BIBLIOGRAFICO)){ // tem campo 005
							c.setDado(BibliotecaUtil.geraDataHoraNoFormatoANSIX3());
							possuiCampo005 = true;
						}
						
					}
				}
				
				if( ! possuiCampo005){ // o título não tinha o campo 005
					dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
					
					Etiqueta e = dao.findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTag(), Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTipo());
					new CampoControle(BibliotecaUtil.geraDataHoraNoFormatoANSIX3(), e , -1, titulo);
				}
					
			}
			
			if(objetoMarc instanceof Autoridade){
				
				Autoridade autoridade = (Autoridade) objetoMarc;
				
				if(autoridade.getCamposControle() != null){
					for (CampoControle c : autoridade.getCamposControle()) {
						
						if(c.getEtiqueta() != null)
						if(c.getEtiqueta().equals(Etiqueta.CAMPO_005_AUTORIDADE)){ // tem campo 005
							c.setDado(BibliotecaUtil.geraDataHoraNoFormatoANSIX3());
							possuiCampo005 = true;
						}
						
					}
				}
				
				if( ! possuiCampo005){ // a autoridade não tinha o campo 005
					dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
					
					Etiqueta e = dao.findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_005_AUTORIDADE.getTag(), Etiqueta.CAMPO_005_AUTORIDADE.getTipo());
					new CampoControle(BibliotecaUtil.geraDataHoraNoFormatoANSIX3(), e , -1, autoridade);
				}
			
			}
			
			if(objetoMarc instanceof ArtigoDePeriodico){
				
				ArtigoDePeriodico artigo = (ArtigoDePeriodico) objetoMarc;
				
				if(artigo.getCamposControle() != null){
					for (CampoControle c : artigo.getCamposControle()) {
						
						if(c.getEtiqueta() != null)
						if(c.getEtiqueta().equals(Etiqueta.CAMPO_005_BIBLIOGRAFICO)){ // tem campo 005
							c.setDado(BibliotecaUtil.geraDataHoraNoFormatoANSIX3());
							possuiCampo005 = true;
						}
						
					}
				}
				
				if( ! possuiCampo005){ // o artigo não tinha o campo 005
					dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
					Etiqueta e = dao.findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTag(), Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTipo());
					new CampoControle(BibliotecaUtil.geraDataHoraNoFormatoANSIX3(), e , -1, artigo);
				}
			
			}
		}finally{
			if (dao != null) dao.close();
		}
		
	}
	
	/**
	 * 
	 * Método que deve ser chamado quando se deseja ordenar os campos de dados de um título,
	 * autoridade ou artigo.
	 *
	 * <br/>Método não chamado por nenhuma página JSP.
	 *
	 * @param titulo
	 */
	public static void ordenaCampoDados(Object objetoMarc){
		
		if(objetoMarc instanceof TituloCatalografico){
		
			TituloCatalografico titulo = (TituloCatalografico) objetoMarc;
			if(titulo.getCamposDados() != null){
				Collections.sort(titulo.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			}
		
		}
		
		if(objetoMarc instanceof Autoridade){
			
			Autoridade autoridade = (Autoridade) objetoMarc;
			if(autoridade.getCamposDados() != null){
				Collections.sort(autoridade.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			}
		
		}
		
		if(objetoMarc instanceof ArtigoDePeriodico){
			
			ArtigoDePeriodico artigo = (ArtigoDePeriodico) objetoMarc;
			if(artigo.getCamposDados() != null){
				Collections.sort(artigo.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			}
		
		}
	}
	
	/**
	 *     Método que atribui na variável <code>posicao</code> dos subcampos a posição que eles
	 *  ocupam na lista.
	 */
	public static void configuraPosicaoCamposMarc(Object objMarc){
		
		if(objMarc instanceof TituloCatalografico){
			
			TituloCatalografico t = (TituloCatalografico) objMarc;
			
			if(t.getCamposDados() != null && t.getCamposControle() != null){
				
				int posicaoControle = 0;
				
				
				for (CampoControle c  : t.getCamposControle() ) {

					if(c == null){
						 t.getCamposControle().remove(posicaoControle);
						 continue;
					}
					
					c.setPosicao(posicaoControle);
					
					posicaoControle++;
				}
				
				
				
				int posicao = 0;
				for (CampoDados c : t.getCamposDados() ) {
					
					if(c == null){
						 t.getCamposDados().remove(posicao);
						 continue;
					}
					
					c.setPosicao(posicao);
					
					if(c.getSubCampos() != null){
						for (int j = 0; j < c.getSubCampos().size(); j++) {
							
							if(c.getSubCampos().get(j) == null){
								c.getSubCampos().remove(j);
								 continue;
							}
							
							c.getSubCampos().get(j).setPosicao(j);
						}
					}
					posicao ++;
					
				}
			}
			
		}
			
		if(objMarc instanceof Autoridade){
			Autoridade a = (Autoridade) objMarc;
			
			if(a.getCamposDados() != null && a.getCamposControle() != null){
				
				int posicaoControle = 0;
				
				for (CampoControle c  : a.getCamposControle()  ) {
					
					if(c == null){
						 a.getCamposControle().remove(posicaoControle);
						 continue;
					}
					
					c.setPosicao(posicaoControle);
					
					posicaoControle++;
				}
				
				int posicao = 0;
				
				for (CampoDados c : a.getCamposDados() ) {
					
					if(c == null){
						 a.getCamposDados().remove(posicao);
						 continue;
					}
					
					c.setPosicao(posicao);
					
					if(c.getSubCampos() != null){
						for (int j = 0; j < c.getSubCampos().size(); j++) {
							
							if(c.getSubCampos().get(j) == null){
								c.getSubCampos().remove(j);
								continue;
							}
							
							c.getSubCampos().get(j).setPosicao(j);
						}
					}
					
					posicao++;
				}
			}
		}
				
				
		if(objMarc instanceof ArtigoDePeriodico){
			
			ArtigoDePeriodico a = (ArtigoDePeriodico) objMarc;
			
			if(a.getCamposDados() != null &&  a.getCamposControle() != null){
				
				int posicaoControle = 0;
				for ( CampoControle c  : a.getCamposControle()  ) {
					if(c == null){
						 a.getCamposControle().remove(posicaoControle);
						 continue;
					}
					
					c.setPosicao(posicaoControle);
					
					posicaoControle++;
				}
				
				int posicao = 0;
				for (CampoDados c : a.getCamposDados()  ) {
					
					if(c == null){
						 a.getCamposDados().remove(posicao);
						 continue;
					}

					c.setPosicao(posicao);
					
					if(c.getSubCampos() != null){
						for (int j = 0; j < c.getSubCampos().size(); j++) {
							
							if(c.getSubCampos().get(j) == null){
								c.getSubCampos().remove(j);
								 continue;
							}
							
							c.getSubCampos().get(j).setPosicao(j);
						}
					}
					
					posicao++;
				}
			}
		}
		
	}
	
	
	
	
	
	
	/**
	 * 
	 *    Método que monta os dados de um Artigo de Periódico completo no formato MARC com seus campos
	 *    e sub campos a partir dos dados simplificados que o usuário digita na tela de catalogação
	 *    de artigos de periódicos.
	 *
	 * @param fasciculoDoArtigo o fascículo do artigo, algumas informações do artigo vem dele
	 * @return o artigo no formato MARC
	 */
	public static ArtigoDePeriodico montaArtigoPeriodico(Fasciculo fasciculoDoArtigo, String titulo, String autor,
			List<String> autoresSecundarios, String intervaloPaginas, List<String> palavrasChaves,
			String localPublicacao, String editora, String ano, String resumo)
			throws DAOException {
		
		GenericDAO dao = null;
		EtiquetaDao etiquetaDao = null;
		
		ArtigoDePeriodico artigo = new ArtigoDePeriodico();
		
		try {
			dao = DAOFactory.getGeneric( Sistema.SIGAA );
			etiquetaDao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
			
			TituloCatalografico tituloDoFasciculo = fasciculoDoArtigo.getAssinatura().getTituloCatalografico();
			CacheEntidadesMarc cacheTitulo = BibliotecaUtil.obtemDadosTituloCache( tituloDoFasciculo.getId() );
			
			// igual ao do fascículo dele //
			artigo.setFormatoMaterial( new FormatoMaterial(tituloDoFasciculo.getFormatoMaterial().getId()));
			
			if(tituloDoFasciculo.getCamposControle() != null)
			for(CampoControle controle : tituloDoFasciculo.getCamposControle()){
				if(controle.getEtiqueta().equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO)){
					new CampoControle(CampoControle.DADOS_CAMPO_LIDER_ARTIGOS_PERIODICOS, dao.refresh(new Etiqueta(controle.getEtiqueta().getId())), -1, artigo);
				}else{
					new CampoControle(controle.getDado(), dao.refresh(new Etiqueta(controle.getEtiqueta().getId())), -1, artigo );
				}
			}
			 
			// 080
			
			if(tituloDoFasciculo.getCamposDados() != null)
			for(CampoDados dado : tituloDoFasciculo.getCamposDados()){
				if(dado.getEtiqueta().equals(Etiqueta.CDU)){
					Etiqueta e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.CDU.getTag(), Etiqueta.CDU.getTipo());
					
					for (SubCampo sub : dado.getSubCampos()) {
						
						if(sub.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
							new CampoDados(e, artigo, ArtigoDePeriodico.CDU_INDICADOR_1, ArtigoDePeriodico.CDU_INDICADOR_2, SubCampo.SUB_CAMPO_A, sub.getDado(), -1, -1);
						}
					}
				}
			}
			
			
			if(StringUtils.notEmpty( cacheTitulo.getNumeroChamada())){
			
				String[] numeroChamadaCampos = cacheTitulo.getNumeroChamada().split("\\s");
				
				CampoDados numeroChamda = null;
				
				if(numeroChamadaCampos.length > 0){
					Etiqueta e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.NUMERO_CHAMADA.getTag(), Etiqueta.NUMERO_CHAMADA.getTipo());
					numeroChamda = new CampoDados( e, ArtigoDePeriodico.NUMERO_CHAMADA_INDICADOR_1, ArtigoDePeriodico.NUMERO_CHAMADA_INDICADOR_2, artigo, -1);
				}
					
				for (int i = 0; i < numeroChamadaCampos.length; i++) {
					
					switch (i) {
					case 0:
						new SubCampo(SubCampo.SUB_CAMPO_A, numeroChamadaCampos[i], numeroChamda, -1);
						break;
					case 1:
						new SubCampo(SubCampo.SUB_CAMPO_B, numeroChamadaCampos[i], numeroChamda, -1);
						break;
					case 2:
						new SubCampo(SubCampo.SUB_CAMPO_C, numeroChamadaCampos[i], numeroChamda, -1);
						break;
					case 3:
						new SubCampo(SubCampo.SUB_CAMPO_D, numeroChamadaCampos[i], numeroChamda, -1);
						break;
					}
				}
			}
			
			// cria o campo 245$a
			
			Etiqueta e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.TITULO.getTag(), Etiqueta.TITULO.getTipo());
			new CampoDados(e, artigo,
					ArtigoDePeriodico.TITULO_INDICADOR_1, ArtigoDePeriodico.TITULO_INDICADOR_2, SubCampo.SUB_CAMPO_A, titulo, -1, -1);
			
			// cria o campo 100$a - autor
			
			e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.AUTOR.getTag(), Etiqueta.AUTOR.getTipo());
			new CampoDados(e, artigo,
					ArtigoDePeriodico.AUTOR_INDICADOR_1, ArtigoDePeriodico.AUTOR_INDICADOR_2, SubCampo.SUB_CAMPO_A, autor, -1, -1);
			
			// Autores secundários - 700$a
			
			e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.AUTOR_SECUNDARIO.getTag(), Etiqueta.AUTOR_SECUNDARIO.getTipo());
			for ( String autorSecundario : autoresSecundarios ) {
				new CampoDados(e, artigo, ArtigoDePeriodico.AUTOR_SECUNDARIO_INDICADOR_1,
						ArtigoDePeriodico.AUTOR_SECUNDARIO_INDICADOR_2, SubCampo.SUB_CAMPO_A,
						autorSecundario, -1, -1);
			}

			// cria o campo 773$g, 773$w, 773$t
			
			e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.LIGACAO.getTag(), Etiqueta.LIGACAO.getTipo());
			CampoDados campoLigacao =  new CampoDados(e, artigo,
					ArtigoDePeriodico.LIGACAO_INDICADOR_1, ArtigoDePeriodico.LIGACAO_INDICADOR_2, SubCampo.SUB_CAMPO_G, intervaloPaginas, -1, -1);
			
			new SubCampo(SubCampo.SUB_CAMPO_W, String.valueOf(cacheTitulo.getNumeroDoSistema()), campoLigacao, -1);
			new SubCampo(SubCampo.SUB_CAMPO_T, String.valueOf(cacheTitulo.getTitulo()), campoLigacao, -1);
			
			// cria o campo 650$a
			
			for (String palavraCache : palavrasChaves) {
				if(StringUtils.notEmpty(palavraCache)){
					
					e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.ASSUNTO.getTag(), Etiqueta.ASSUNTO.getTipo());
					
					new CampoDados(dao.refresh(e), artigo,
						ArtigoDePeriodico.PALAVRAS_CHAVE_INDICADOR_1, ArtigoDePeriodico.PALAVRAS_CHAVE_INDICADOR_2, SubCampo.SUB_CAMPO_A, palavraCache, -1, -1);
				}
			}
			
			CampoDados campoPublicacao = null;
			
			e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTipo());
			
			if(StringUtils.notEmpty(localPublicacao) || StringUtils.notEmpty(editora) || StringUtils.notEmpty(ano)){
				campoPublicacao = new CampoDados( e, ArtigoDePeriodico.PUBLICACAO_INDICADOR_1, ArtigoDePeriodico.PUBLICACAO_INDICADOR_2, artigo, -1);
			}
			
			if(StringUtils.notEmpty(localPublicacao)){
				
				if(campoPublicacao != null)
					new SubCampo(SubCampo.SUB_CAMPO_A, localPublicacao, campoPublicacao, -1);
			}
			if(StringUtils.notEmpty(editora)){
				if(campoPublicacao != null)
					new SubCampo(SubCampo.SUB_CAMPO_B, editora, campoPublicacao, -1);
			}
			if(StringUtils.notEmpty(ano)){
				if(campoPublicacao != null)
					new SubCampo(SubCampo.SUB_CAMPO_C, ano, campoPublicacao, -1);
			}
			
			if(StringUtils.notEmpty(resumo)){
				e = etiquetaDao.findEtiquetaPorTagETipoAtiva(Etiqueta.RESUMO.getTag(), Etiqueta.RESUMO.getTipo());
				
				new CampoDados(e, artigo,
					ArtigoDePeriodico.RESUMO_INDICADOR_1, ArtigoDePeriodico.RESUMO_INDICADOR_2, SubCampo.SUB_CAMPO_A, resumo, -1, -1);   // cria o campo 520$a
			}
		
		} finally {
			if(dao != null ) dao.close();
			if( etiquetaDao != null ) etiquetaDao.close();
		}
		
		return artigo;
	}
	
	
	
	
	
	
	
	
	
	/**
	 *    Atualiza todos os dados dos títulos que tinham sido criados com os dados da autoridade
	 * que acabou de ser alterada pelo usuário.
	 * 
	 *   <strong>Obs.: Esse método tem que ser chamado depois de atualizado o cache da autoridade.
	 *   Porque ele pega os dados do cache e ele já precisa estar com os dados atualizados.</strong>
	 * 
	 * @param dao
	 * @param autoridadeMemoria
	 * @throws DAOException
	 */
	public static void atualizaDadosTituloAutoridade(AutoridadeDao dao, Autoridade autoridadeMemoria, List<ClassificacaoBibliografica> classificacoesUtilizadas)
			throws DAOException {
		
		// guarda os títulos dos subcampos porque tem que gerar o cache novamente já que a informação ia mudar.
		Set<TituloCatalografico> titulos = new HashSet<TituloCatalografico>();
		
		/*
		 * Pega todos os subcampos de Títulos que foram gerados a partir do subcampo da autoridade.
		 */
		
		if(autoridadeMemoria.getCamposDados() != null)
		for (CampoDados campo : autoridadeMemoria.getCamposDados()) {
			
			for (SubCampo subcampoAutoridade : campo.getSubCampos()) {
				
				List<SubCampo> subCamposTitulo = (List<SubCampo>) dao.findByExactField(SubCampo.class,
						"subCampoAutoridade.id", subcampoAutoridade.getId());
			
				for (SubCampo subCampoTitulo : subCamposTitulo) {
					
					// se ainda não tem o título adiciona no SET para não ficar repetido
					titulos.add(subCampoTitulo.getCampoDados().getTituloCatalografico());
				
					subCampoTitulo.setDado(subcampoAutoridade.getDado());
					
					dao.update(subCampoTitulo);
					
				}
				
			}
			
		}
		
		// Atualiza as informações dos caches dos títulos cuja informações dos sub campos foram alteradas
		for (TituloCatalografico titulo : titulos) {
			if(titulo.isAtivo()){    // Só atualiza caso o título não tenha sido removido do acervo.
				sincronizaTituloCatalograficoCache(dao, titulo, true, classificacoesUtilizadas);
			}
		}
		
	}
	
	
	/**
	 *    Método que converte um Título em um Título num formato mais amigável para realizar as buscas
	 * do sistema. Vai ser chamado sempre no processador de catalogação e no processador que atualiza
	 * os títulos.
	 *
	 * @param título o título real com seus campo de controle, dados e subcampos
	 * @param se está atualizando um título ou está criando.
	 * @return um título sem campos de controle, dados e subcampos. Os dados necessário ficam no
	 *            próprio título para otimizar as buscas.
	 * @throws DAOException
	 */
	public static void sincronizaTituloCatalograficoCache(GenericDAO dao, TituloCatalografico tituloCat, boolean atualizando, List<ClassificacaoBibliografica> classificacoesUtilizadas) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		CacheEntidadesMarc tituloCache = null;
		
		if(atualizando){
			tituloCache = dao.findByExactField(CacheEntidadesMarc.class, "idTituloCatalografico", tituloCat.getId(), true);
		
			if(tituloCache == null){
				throw new DAOException(" Não existe um objeto cache para o título persistido");
			}
			
		}else
			tituloCache = new CacheEntidadesMarc();
		
		tituloCache = sincronizaTituloCatalograficoCache(tituloCat, tituloCache, classificacoesUtilizadas);
		
		System.out.println("$$$$$$$$$$$$$ mondar os dados do cache de título demorou : "+(System.currentTimeMillis()-tempo)+" ms");
		
		if(atualizando){
			dao.update(tituloCache);
		}else{
			dao.create(tituloCache);
		}
		
		System.out.println("$$$$$$$$$$$$$ salvar os dados do cache de título demorou : "+(System.currentTimeMillis()-tempo)+" ms");
	}
	
	
	
	/**
	 *    <p>Método quem contém a lógica para montar as informações de cada campo do cache.</p>
	 *
	 *    <p>Método também chamado da rotina que atualiza os campos do cache. </p>
	 *     
	 * 
	 * @param título o título real com seus campo de controle, dados e subcampos
	 * @param tituloCache o cache com os dados montados por esse método a partir das informações do objeto título
	 * @return o cache do título montado
	 * 
	 * @see br.ufrn.sigaa.biblioteca.timer.AtualizaCacheMARCTitulosThread
	 */
	public static CacheEntidadesMarc sincronizaTituloCatalograficoCache(TituloCatalografico tituloCat, CacheEntidadesMarc tituloCache
			, List<ClassificacaoBibliografica> classificacoesUtilizadas){
	
		
		
		/* *************************************************************************
		 * IMPORTANTE: Adicionar novos campos no cache, adicione-o nesse método para
		 * ser apagado quando for ser feita a sincronização do cache.
		 ***************************************************************************/
		tituloCache.zeraDadosMARCCache();
		
		
		tituloCache.setIdTituloCatalografico(tituloCat.getId());
		tituloCache.setIdObraDigitalizada (tituloCat.getIdObraDigitalizada() );
		tituloCache.setNumeroDoSistema(tituloCat.getNumeroDoSistema());
		
		tituloCache.setCatalogado(tituloCat.isCatalogado());
		
		
		
		if(tituloCat.getCamposControle() != null)
		for (CampoControle campoControle : tituloCat.getCamposControle()) {
			
			if(campoControle.getEtiqueta().equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO)){
				try{
					tituloCache.setAnoPublicacao( (  Integer.parseInt(  (String) campoControle.getDado().subSequence(7, 11) )  ) );
				}catch(NumberFormatException ex){
					// Se o ano do campo 008 não for numérico, não vai conseguir buscar pelos intervalos de datas na busca do acervo.  //
					// Esse campo é usado para isso.                                                                                   //
				}
				
				// Para a busca por idiomas utilizado na busca avançada -  Tarefa 71567 - Busca avançada - a partir de : 22/11/2011
				try{
					tituloCache.setIdiomaAscii(  ( (String) campoControle.getDado().subSequence(35, 38)).toUpperCase()  );
				}catch(IndexOutOfBoundsException ex){
				    // Se não tem valor nessa posição, não busca
				}
			}
		}
		
		// Primeira mente sincroniza os campos de dados de classificação onde a informação depende dos campos cadastrado no sistema //
		if(tituloCat.getCamposDados() != null)
		for (ClassificacaoBibliografica classificacaoUtilizada : classificacoesUtilizadas) {
			
			String informacaoCampo = ClassificacoesBibliograficasUtil.retornaInformacaoCampoDaClassificacao(tituloCat, classificacaoUtilizada);
			String informacaoCampoAscii =  StringUtils.toAsciiAndUpperCase(informacaoCampo);
			
			if(classificacaoUtilizada.isPrimeiraClassificacao()){
				if( tituloCache.getClassificacao1() != null ){
					tituloCache.setClassificacao1( tituloCache.getClassificacao1() + SEPARADOR_VALORES_CACHE +  informacaoCampo );
					tituloCache.setClassificacao1Ascii(tituloCache.getClassificacao1Ascii() + " " + informacaoCampoAscii );
				}else{
					tituloCache.setClassificacao1( informacaoCampo );
					tituloCache.setClassificacao1Ascii( informacaoCampoAscii );
				}
				
			}
			
			if(classificacaoUtilizada.isSegundaClassificacao()){
				if( tituloCache.getClassificacao2() != null ){
					tituloCache.setClassificacao2( tituloCache.getClassificacao2() + SEPARADOR_VALORES_CACHE +  informacaoCampo );
					tituloCache.setClassificacao2Ascii(tituloCache.getClassificacao2Ascii() + " " + informacaoCampoAscii );
				}else{
					tituloCache.setClassificacao2( informacaoCampo );
					tituloCache.setClassificacao2Ascii( informacaoCampoAscii );
				}
				
			}
			
			if(classificacaoUtilizada.isTerceiraClassificacao()){
				if( tituloCache.getClassificacao3() != null ){
					tituloCache.setClassificacao3( tituloCache.getClassificacao3() + SEPARADOR_VALORES_CACHE +  informacaoCampo );
					tituloCache.setClassificacao3Ascii(tituloCache.getClassificacao3Ascii() + " " + informacaoCampoAscii );
				}else{
					tituloCache.setClassificacao3( informacaoCampo );
					tituloCache.setClassificacao3Ascii( informacaoCampoAscii );
				}
				
			}
			
		}
		
		
		if(tituloCat.getCamposDados() != null)
		for (CampoDados campoDados : tituloCat.getCamposDados()) {
			
			if(campoDados.getEtiqueta().equals(Etiqueta.ISBN)){  // repete
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado() )){
						
						if( tituloCache.getIsbn() != null ){
							tituloCache.setIsbn( tituloCache.getIsbn() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
						}else{
							tituloCache.setIsbn( sc.getDado() );
						}
						
					}
				}
			}
			
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.ISSN)){  // repete
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado() )){
						if( tituloCache.getIssn() != null ){
							tituloCache.setIssn( tituloCache.getIssn() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
						}else{
							tituloCache.setIssn( sc.getDado() );
						}
					}
				}
			}
			
			
			
			////////////////////////////  Informações do Título que vão para o cache ////////////////////////////
			
			if(campoDados.getEtiqueta().equals(Etiqueta.TITULO)){
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado() )){
						tituloCache.setTitulo(  sc.getDado() );
						
						if( tituloCache.getOutrasInformacoesTituloAscii() != null ){
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii(tituloCache.getOutrasInformacoesTituloAscii() + " " + sc.getDadoAscii() );
						}else{
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii( sc.getDadoAscii());
						}
						
					}
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_H)){
						tituloCache.setMeioPublicacao( sc.getDado() );
					}
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_B)){
						tituloCache.setSubTitulo( sc.getDado() );
						
						if( tituloCache.getOutrasInformacoesTituloAscii() != null ){
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii(tituloCache.getOutrasInformacoesTituloAscii() + " " + sc.getDadoAscii() );
						}else{
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii( sc.getDadoAscii());
						}
						
					}
					
					// Autoria da Obra, é para ser busca no busca por autores (O campo 245$c é para ser busca na busca de autores)//
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_C) && StringUtils.notEmpty(sc.getDadoAscii())){
						
						if( tituloCache.getAutorAscii() != null ){
							tituloCache.setAutorAscii(tituloCache.getAutorAscii() + " " + sc.getDadoAscii() );
						}else{
							tituloCache.setAutorAscii( sc.getDadoAscii());
						}
					}
				}
			}
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.TITULO_VARIAVEL)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado() ) ){
						if( tituloCache.getFormasVarientesTitulo() != null ){
							tituloCache.setFormasVarientesTitulo( tituloCache.getFormasVarientesTitulo() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
						}else{
							tituloCache.setFormasVarientesTitulo( sc.getDado() );
						}
					}
				}
			}
			
			// Separa dos outros para utilizar na busca avançada
			if(campoDados.getEtiqueta().equals(Etiqueta.TITULO_UNIFORME)){ // só tem um
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty(sc.getDadoAscii())){
						tituloCache.setTituloUniformeAscii( sc.getDadoAscii() );
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.TITULO_UNIFORME_ENTRADA_PRINCIPAL)
					|| campoDados.getEtiqueta().equals(Etiqueta.TITULO_UNIFORME)
					|| campoDados.getEtiqueta().equals(Etiqueta.TITULO_ABREVIADO)
					|| campoDados.getEtiqueta().equals(Etiqueta.TITULO_COLETANEA)
					|| campoDados.getEtiqueta().equals(Etiqueta.TITULO_VARIAVEL)
					|| campoDados.getEtiqueta().equals(Etiqueta.TITULO_UNIFORME_ENTRADA_ADICIONAL)
					|| campoDados.getEtiqueta().equals(Etiqueta.TITULO_ANTERIOR)
					|| campoDados.getEtiqueta().equals(Etiqueta.TITULO_RELACIONADO_ANALITICO_NAO_CONTROLADO)
					){
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado() )){
						
						if( tituloCache.getOutrasInformacoesTituloAscii() != null ){
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii(tituloCache.getOutrasInformacoesTituloAscii() + " " + sc.getDadoAscii() );
						}else{
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii( sc.getDadoAscii());
						}
					}
				}
			}
			
			
			if( campoDados.getEtiqueta().equals(Etiqueta.TITULO_VARIAVEL)){
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && ( sc.getCodigo().equals(SubCampo.SUB_CAMPO_B) || sc.getCodigo().equals(SubCampo.SUB_CAMPO_N) || sc.getCodigo().equals(SubCampo.SUB_CAMPO_P) )   ){
						if( tituloCache.getOutrasInformacoesTituloAscii() != null ){
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii(tituloCache.getOutrasInformacoesTituloAscii() + " " + sc.getDadoAscii() );
						}else{
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii( sc.getDadoAscii());
						}
					}
				}
			}
			
			if( campoDados.getEtiqueta().equals(Etiqueta.TITULO_RELACIONADO_ANALITICO_NAO_CONTROLADO)){
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && ( sc.getCodigo().equals(SubCampo.SUB_CAMPO_N) || sc.getCodigo().equals(SubCampo.SUB_CAMPO_P ) ) ){
						if( tituloCache.getOutrasInformacoesTituloAscii() != null ){
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii(tituloCache.getOutrasInformacoesTituloAscii() + " " + sc.getDadoAscii() );
						}else{
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii( sc.getDadoAscii());
						}
					}
				}
			}
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.TITULO_ANTERIOR)
					|| campoDados.getEtiqueta().equals(Etiqueta.TITULO_POSTERIOR)){
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_T)){
						
						if( tituloCache.getOutrasInformacoesTituloAscii() != null ){
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii(tituloCache.getOutrasInformacoesTituloAscii() + " " + sc.getDadoAscii() );
						}else{
							if(StringUtils.notEmpty(sc.getDadoAscii()))
								tituloCache.setOutrasInformacoesTituloAscii( sc.getDadoAscii());
						}
					}
				}
				
			}
			
			
			
			///////////////////////////// Informações sobre o Autor que vão para o cache ///////////////////////////
			
			if(campoDados.getEtiqueta().equals(Etiqueta.AUTOR)
					|| campoDados.getEtiqueta().equals(Etiqueta.AUTOR_COOPORATIVO)){ // só tem um por vez
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty(sc.getDado()) ){
						
							tituloCache.setAutor( sc.getDado() );
							
							if( StringUtils.notEmpty( tituloCache.getAutorAscii()) ){
								tituloCache.setAutorAscii( tituloCache.getAutorAscii() + " " +  sc.getDadoAscii() );
							}else{
								tituloCache.setAutorAscii(sc.getDadoAscii() );
							}
					}
					
					// o sub campo "B" vai usar apenas para pesquisa, mas não vai mostrar para o usuário
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_B) && StringUtils.notEmpty( sc.getDadoAscii() ) ){
						
						if( StringUtils.notEmpty( tituloCache.getAutorAscii()) ){
							tituloCache.setAutorAscii( tituloCache.getAutorAscii() + " " +  sc.getDadoAscii() );
						}else{
							tituloCache.setAutorAscii(sc.getDadoAscii() );
						}
					}
					
				}
			}
			
			
			StringBuilder discrininadoresEvento = new StringBuilder();
			
			// no caso de evento tem que pegar os subcampos "n", "d" e "c" NESSA ORDEM porque eles diferenciam os eventos
			if( campoDados.getEtiqueta().equals(Etiqueta.AUTOR_EVENTO) ){ // só tem um por vez
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty(sc.getDado())){
						tituloCache.setAutor( sc.getDado() );
						tituloCache.setAutorAscii( toAsciiAndUpperCase(sc.getDadoAscii()) );
					}
					
					// o sub campo "E" vai usar apenas para pesquisa, mas não vai mostrar para o usuário
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_E)  && StringUtils.notEmpty( sc.getDadoAscii() ) ){
						
						if( StringUtils.notEmpty( tituloCache.getAutorAscii()) ){
							tituloCache.setAutorAscii( tituloCache.getAutorAscii() + " " +  sc.getDadoAscii() );
						}else{
							tituloCache.setAutorAscii(sc.getDadoAscii() );
						}
					}
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_N)){
						if(StringUtils.notEmpty(sc.getDado())){
							discrininadoresEvento.append(" "+sc.getDado());
						}
					}
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_D)){
						if(StringUtils.notEmpty(sc.getDado())){
							discrininadoresEvento.append(" "+sc.getDado());
						}
					}

					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_C)){
						if(StringUtils.notEmpty(sc.getDado())){
							discrininadoresEvento.append(" "+sc.getDado());
						}
					}
					
				}
				
				tituloCache.setAutor( tituloCache.getAutor() +" "+ discrininadoresEvento.toString());
				
			}
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.AUTOR_SECUNDARIO)
					|| campoDados.getEtiqueta().equals(Etiqueta.AUTOR_COOPORATIVO_SECUNDARIO)){ //pode se repetir
			
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty(sc.getDado()) ){
						if( tituloCache.getAutoresSecundarios() != null ){
							tituloCache.setAutoresSecundarios( tituloCache.getAutoresSecundarios() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
							tituloCache.setAutoresSecundariosAscii( tituloCache.getAutoresSecundariosAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setAutoresSecundarios( sc.getDado() );
							tituloCache.setAutoresSecundariosAscii(sc.getDadoAscii() );
						}
					}
					

					// o sub campo "B" vai usar apenas para pesquisa, mas não vai mostrar para o usuário
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_B)  && StringUtils.notEmpty( sc.getDadoAscii() ) ){
						
						if( tituloCache.getAutoresSecundarios() != null ){
							tituloCache.setAutoresSecundariosAscii( tituloCache.getAutoresSecundariosAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setAutoresSecundariosAscii(sc.getDadoAscii() );
						}
					}
					
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.AUTOR_EVENTO_SECUNDARIO)){ //pode se repetir
			
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty(sc.getDado())){
						if( tituloCache.getAutoresSecundarios() != null ){
							tituloCache.setAutoresSecundarios( tituloCache.getAutoresSecundarios() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
							tituloCache.setAutoresSecundariosAscii( tituloCache.getAutoresSecundariosAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setAutoresSecundarios( sc.getDado() );
							tituloCache.setAutoresSecundariosAscii(sc.getDadoAscii() );
						}
					}
					
					// o sub campo "E" vai usar apenas para pesquisa, mas não vai mostrar para o usuário
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_E)  && StringUtils.notEmpty( sc.getDadoAscii() ) ){
						
						if( tituloCache.getAutoresSecundarios() != null ){
							tituloCache.setAutoresSecundariosAscii( tituloCache.getAutoresSecundariosAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setAutoresSecundariosAscii(sc.getDadoAscii() );
						}
					}
				}
			}
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.EDICAO)){ // mais de um
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if( sc.getCodigo() != null && (sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) || sc.getCodigo().equals(SubCampo.SUB_CAMPO_B) ) && StringUtils.notEmpty(sc.getDado())){
						if( StringUtils.notEmpty(tituloCache.getEdicao()) ){
							tituloCache.setEdicao( tituloCache.getEdicao() + SEPARADOR_VALORES_CACHE +  sc.getDado()  );
						}else{
							tituloCache.setEdicao( sc.getDado() );
						}
					}
				}
			}
			
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO)){ // pode ter vários pois o campo pode ser repetido
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty(sc.getDado()) ){
						if( StringUtils.notEmpty(tituloCache.getLocalPublicacao() )  ){
							tituloCache.setLocalPublicacao(  tituloCache.getLocalPublicacao() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
							tituloCache.setLocalPublicacaoAscii(  tituloCache.getLocalPublicacaoAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setLocalPublicacao( sc.getDado() );
							tituloCache.setLocalPublicacaoAscii(sc.getDadoAscii() );
						}
					}
					
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_B) && StringUtils.notEmpty(sc.getDado())){
						if( StringUtils.notEmpty(tituloCache.getEditora()) ){
								tituloCache.setEditora( tituloCache.getEditora() + SEPARADOR_VALORES_CACHE +  sc.getDado()  );
								tituloCache.setEditoraAscii(tituloCache.getEditoraAscii() + " " +  sc.getDadoAscii() );
						}else{
							tituloCache.setEditora( sc.getDado() );
							tituloCache.setEditoraAscii(sc.getDadoAscii() );
						}
					}
					
					if( sc.getCodigo() != null && ( sc.getCodigo().equals(SubCampo.SUB_CAMPO_C) || sc.getCodigo().equals(SubCampo.SUB_CAMPO_G) )  && StringUtils.notEmpty(sc.getDado())){
						if( StringUtils.isEmpty(tituloCache.getAno())){
							tituloCache.setAno(sc.getDado());
						}else{
							tituloCache.setAno( tituloCache.getAno() +SEPARADOR_VALORES_CACHE+  sc.getDado() );
						}
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.NUMERO_CHAMADA)){ // só tem um
				
				String cdu = "";
				String cuter = "";
				String edicaolocal = "";
				String colecao = "";
				
				if(campoDados.getSubCampos() != null){
					forInterno:
					for (SubCampo sub : campoDados.getSubCampos()) {
						
						if(sub.getCodigo() != null){
							if(sub.getCodigo().equals(SubCampo.SUB_CAMPO_A) ){
								cdu = sub.getDado() != null ? sub.getDado() : "";
								continue forInterno;
							}
							
							if(sub.getCodigo().equals(SubCampo.SUB_CAMPO_B) ){
								cuter = sub.getDado() != null ? sub.getDado() : "";
								continue forInterno;
							}
							
							if(sub.getCodigo().equals( SubCampo.SUB_CAMPO_C) ){
								edicaolocal = sub.getDado() != null ? sub.getDado() : "";
								continue forInterno;
							}
							
							if(sub.getCodigo().equals( SubCampo.SUB_CAMPO_D) ){
								colecao = sub.getDado() != null ? sub.getDado() : "";
								continue forInterno;
							}
						}
					}
				}
				
				StringBuilder numeroChamada = new StringBuilder();
				
				if( StringUtils.notEmpty(cdu) ){
					numeroChamada.append(cdu);
				}
				
				if( StringUtils.notEmpty(cuter) ){
					if(StringUtils.notEmpty(numeroChamada.toString()) )
							numeroChamada.append(" "+cuter);
					else
						numeroChamada.append(cuter);
				}
				
				if( StringUtils.notEmpty(edicaolocal) ){
					if(StringUtils.notEmpty(numeroChamada.toString()) )
						numeroChamada.append(" "+edicaolocal);
					else
						numeroChamada.append(edicaolocal);
				}
				
				if( StringUtils.notEmpty(colecao) ){
					if(StringUtils.notEmpty(numeroChamada.toString()) )
						numeroChamada.append(" "+colecao);
					else
						numeroChamada.append(colecao);
				}
				
				tituloCache.setNumeroChamada( numeroChamada.toString() );
			}
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO_PESSOAL)
					|| campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO_ENTIDADE)
					|| campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO_EVENTOS)
					|| campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO_TITULO_UNIFORME)
					|| campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO)
					|| campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO_GEOGRAFICO)
					|| campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO_SEM_CONTROLE)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado()) ){
						if( tituloCache.getAssunto() != null ){
							tituloCache.setAssunto( tituloCache.getAssunto() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
							tituloCache.setAssuntoAscii(tituloCache.getAssuntoAscii()+ " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setAssunto( sc.getDado() );
							tituloCache.setAssuntoAscii(sc.getDadoAscii() );
						}
					}
				}
			}
			
			// Guarda tudo de assunto no cache
			if(campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO)
					|| campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO_GEOGRAFICO)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && ( sc.getCodigo().equals(SubCampo.SUB_CAMPO_X) || sc.getCodigo().equals(SubCampo.SUB_CAMPO_Y) || sc.getCodigo().equals(SubCampo.SUB_CAMPO_Z) )
						&& StringUtils.notEmpty( sc.getDado()) ){
						if( tituloCache.getAssunto() != null ){
							tituloCache.setAssunto( tituloCache.getAssunto() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
							tituloCache.setAssuntoAscii(tituloCache.getAssuntoAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setAssunto( sc.getDado() );
							tituloCache.setAssuntoAscii(sc.getDadoAscii() );
						}
					}
				}
			}
			
			// Guarda tudo de assunto no cache
			if(campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO_EVENTOS)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_E) && StringUtils.notEmpty( sc.getDado()) ){
						if( tituloCache.getAssunto() != null ){
							tituloCache.setAssunto( tituloCache.getAssunto() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
							tituloCache.setAssuntoAscii(tituloCache.getAssuntoAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setAssunto( sc.getDado() );
							tituloCache.setAssuntoAscii(sc.getDadoAscii() );
						}
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.DESCRICAO_FISICA) ){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						if( tituloCache.getDescricaoFisica() != null ){
							tituloCache.setDescricaoFisica( tituloCache.getDescricaoFisica() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
						}else{
							tituloCache.setDescricaoFisica( sc.getDado() );
						}
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.SERIE_OBSOLETA) || campoDados.getEtiqueta().equals(Etiqueta.SERIE)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						if( tituloCache.getSerie() != null ){
							tituloCache.setSerie( tituloCache.getSerie() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
							tituloCache.setSerieAscii(tituloCache.getSerieAscii() + " " + sc.getDadoAscii() );
						}else{
							tituloCache.setSerie( sc.getDado() );
							tituloCache.setSerieAscii( sc.getDadoAscii() );
						}
					}
				}
			}
			
			
			/*
			 *  Procura fechar o par:  @@@idCampoDados@@@Label #$& |||@idCampoDados|||Endereço #$& @@@idCampoDados@@@Label #$& |||@idCampoDados|||Endereço 
			 * 
			 * Nessa ordem.
			 */
			if(campoDados.getEtiqueta().equals(Etiqueta.LOCALIZACAO_E_ACESSO_ELETRONICO)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if (sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_Z)  && StringUtils.notEmpty(sc.getDado()) ){ // LABEL
						
						if( tituloCache.getLocalizacaoEnderecoEletronico() != null )
							tituloCache.setLocalizacaoEnderecoEletronico( tituloCache.getLocalizacaoEnderecoEletronico() + formataLabel(campoDados.getId(), sc.getDado()) );
						else
							tituloCache.setLocalizacaoEnderecoEletronico( formataLabel(campoDados.getId(), sc.getDado()));
						
					}
					
					if( sc.getCodigo() != null && ( sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) || sc.getCodigo().equals(SubCampo.SUB_CAMPO_U) ) && StringUtils.notEmpty(sc.getDado()) ){ // ENDEREÇO
						
						if( tituloCache.getLocalizacaoEnderecoEletronico() != null )
							tituloCache.setLocalizacaoEnderecoEletronico(  tituloCache.getLocalizacaoEnderecoEletronico() +  formataEndereco(campoDados.getId(), sc.getDado())  );
						else
							tituloCache.setLocalizacaoEnderecoEletronico( formataEndereco(campoDados.getId(), sc.getDado())  );
					}
					
					
				}
				
			}
			
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.NOTA_GERAL)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado()) ){
						if( tituloCache.getNotasGerais() != null ){
							tituloCache.setNotasGerais( tituloCache.getNotasGerais() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
						}else{
							tituloCache.setNotasGerais( sc.getDado() );
						}
						
						if( tituloCache.getNotasAscii() != null ){
							tituloCache.setNotasAscii( tituloCache.getNotasAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setNotasAscii(sc.getDadoAscii() );
						}
						
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.NOTA_DE_CONTEUDO)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado())){
						if( tituloCache.getNotaDeConteudo() != null ){
							tituloCache.setNotaDeConteudo( tituloCache.getNotaDeConteudo() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
						}else{
							tituloCache.setNotaDeConteudo( sc.getDado() );
						}
						
						if( tituloCache.getNotasAscii() != null ){
							tituloCache.setNotasAscii( tituloCache.getNotasAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setNotasAscii(sc.getDadoAscii() );
						}
						
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.NOTA_LOCAL)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDado())){
						if( tituloCache.getNotasLocais() != null ){
							tituloCache.setNotasLocais( tituloCache.getNotasLocais() + SEPARADOR_VALORES_CACHE +  sc.getDado() );
						}else{
							tituloCache.setNotasLocais( sc.getDado() );
						}
						
						if( tituloCache.getNotasAscii() != null ){
							tituloCache.setNotasAscii( tituloCache.getNotasAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setNotasAscii(sc.getDadoAscii() );
						}
						
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.NOTA_FAC_SIMILE) ){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null && (sc.getCodigo().equals(SubCampo.SUB_CAMPO_P)
							|| sc.getCodigo().equals(SubCampo.SUB_CAMPO_F)
							|| sc.getCodigo().equals(SubCampo.SUB_CAMPO_B)
							|| sc.getCodigo().equals(SubCampo.SUB_CAMPO_C) )&& StringUtils.notEmpty( sc.getDadoAscii())){
						if( tituloCache.getNotasAscii() != null ){
							tituloCache.setNotasAscii(tituloCache.getNotasAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setNotasAscii(sc.getDadoAscii() );
						}
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.NOTA_COM) || campoDados.getEtiqueta().equals(Etiqueta.NOTA_FORMA_FISICA)
					 || campoDados.getEtiqueta().equals(Etiqueta.NOTA_DISSETACAO_TESE)){ // pode ter vários pois o campo pode ser repetido
				
				if(campoDados.getSubCampos() != null)
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo() != null &&  sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && StringUtils.notEmpty( sc.getDadoAscii())){
						if( tituloCache.getNotasAscii() != null ){
							tituloCache.setNotasAscii(tituloCache.getNotasAscii() + " " + sc.getDadoAscii()  );
						}else{
							tituloCache.setNotasAscii(sc.getDadoAscii() );
						}
					}
				}
			}
			
			if ( campoDados.getEtiqueta().equals(Etiqueta.RESUMO) ) {
				
				if(campoDados.getSubCampos() != null)
				for ( SubCampo sc: campoDados.getSubCampos() ) {
					if ( sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) ) {
						if ( tituloCache.getResumo() != null )
							tituloCache.setResumo( tituloCache.getResumo() + SEPARADOR_VALORES_CACHE + sc.getDado() );
						else
							tituloCache.setResumo( sc.getDado() );
					}
				}
			}
			
		} // fim do for nos campos de dados
		
		
		tituloCache.formataDadosPesquisa(); // IMPORTANTE, tem que chamar senão a pesquisa não vai funcionar direito.
		
		return tituloCache;
	}
	
	
	/**
	 *  Formata o label para ser possível obter qual o respectivo endereço ele pertence
	 *
	 * @param idCampoDados
	 * @param endereco
	 * @return
	 */
	private static String formataLabel(int idCampoDados, String label){
		return SEPARADOR_LABEL_ENDERECO_ELETRONICO + idCampoDados + SEPARADOR_LABEL_ENDERECO_ELETRONICO+label+" "+SEPARADOR_VALORES_CACHE+" ";
	}
	
	/**
	 *  Formata o endereço para ser possível obter como o respectivo label a ser mostrado para o usuário
	 *
	 * @param idCampoDados
	 * @param endereco
	 * @return
	 */
	private static String formataEndereco(int idCampoDados, String endereco){
		return SEPARADOR_TEXTO_ENDERECO_ELETRONICO + idCampoDados + SEPARADOR_TEXTO_ENDERECO_ELETRONICO+endereco+" "+SEPARADOR_VALORES_CACHE+" ";
	}
	
	
	/**
	 *    Método que converte um Título em um Título num formato mais amigável para realizar as buscas
	 * do sistema. Vai ser chamado sempre no processador de catalogação e no processador que atualiza
	 * os títulos.
	 *
	 * @param título o título real com seus campo de controle, dados e subcampos
	 * @param se está atualizando um título ou está criando.
	 * @return um título sem campos de controle, dados e subcampos. Os dados necessários ficam no
	 *            próprio título para otimizar as buscas.
	 * @throws DAOException
	 */
	public static void sincronizaAutoridadeCache(GenericDAO dao, Autoridade autoridade, boolean atualizando) throws DAOException{
	
		CacheEntidadesMarc autoridadeCache = null;
		
		if(atualizando){
			autoridadeCache = dao.findByExactField(CacheEntidadesMarc.class, "idAutoridade",  autoridade.getId(), true);
		
			if(autoridadeCache == null){
				throw new DAOException(" Não existe um objeto cache para a autoridade persistida");
			}
			
		}else
			autoridadeCache = new CacheEntidadesMarc();
		
		/* *************************************************************************
		 * IMPORTANTE: Adicionar novos campos no cache, adicione-o nesse método para
		 * ser apagado quando for ser feita a sincronização do cache.
		 ***************************************************************************/
		autoridadeCache.zeraDadosMARCCache();
		
		autoridadeCache.setIdAutoridade(autoridade.getId());
		autoridadeCache.setNumeroDoSistema(autoridade.getNumeroDoSistema());
		
		autoridadeCache.setCatalogado(autoridade.isCatalogada());
		
		
		
		if(autoridade.getCamposDados() != null)
		for (CampoDados campoDados : autoridade.getCamposDados()) {
			
			// 100 ou 110 ou 111  (todos os sub campos)
			if(campoDados.getEtiqueta().equals(Etiqueta.NOME_PESSOAL)
					|| campoDados.getEtiqueta().equals(Etiqueta.NOME_CORPORATIVO)
					|| campoDados.getEtiqueta().equals(Etiqueta.NOME_EVENTO)){
				for (SubCampo sc : campoDados.getSubCampos()) { // só tem um por autoridade por vez
					
					if(StringUtils.notEmpty(sc.getDado()) ){
						
						if(autoridadeCache.getEntradaAutorizadaAutor() != null){
							autoridadeCache.setEntradaAutorizadaAutor(autoridadeCache.getEntradaAutorizadaAutor()+" "+sc.getDado()); // sem o separador, a entrada autorizada autor continua sendo apenas 1 campo, mas composto por todos os subcampos
							autoridadeCache.setEntradaAutorizadaAutorAscii(autoridadeCache.getEntradaAutorizadaAutorAscii()+" "+ StringUtils.toAsciiAndUpperCase(sc.getDado()));
						}else{
							autoridadeCache.setCampoEntradaAutorizada(campoDados.getEtiqueta().getTag());
							autoridadeCache.setEntradaAutorizadaAutor(sc.getDado());
							autoridadeCache.setEntradaAutorizadaAutorAscii(StringUtils.toAsciiAndUpperCase(sc.getDado()));
						}
					}
				}
			}
			
			// os vários 400 ou 410 ou 411 que podem existir (todos os sub campos)
			if(campoDados.getEtiqueta().equals(Etiqueta.NOME_PESSOAL_REMISSIVO)
					|| campoDados.getEtiqueta().equals(Etiqueta.NOME_CORPORATIVO_REMISSIVO)
					|| campoDados.getEtiqueta().equals(Etiqueta.NOME_EVENTO_REMISSIVO)){
				
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(StringUtils.notEmpty(sc.getDado())){
						
						if(autoridadeCache.getNomesRemissivosAutor() != null){
							autoridadeCache.setNomesRemissivosAutor( autoridadeCache.getNomesRemissivosAutor() +SEPARADOR_VALORES_CACHE+ sc.getDado() );
							autoridadeCache.setNomesRemissivosAutorAscii(autoridadeCache.getNomesRemissivosAutorAscii().toUpperCase() +" "+ sc.getDadoAscii());
						}else{
							autoridadeCache.setNomesRemissivosAutor( sc.getDado() );
							if(sc.getDadoAscii() != null)
								autoridadeCache.setNomesRemissivosAutorAscii(sc.getDadoAscii().toUpperCase());
						}
					}
				}
			}
			
			
			// 150 ou 151 (todos os sub campos)
			if(campoDados.getEtiqueta().equals(Etiqueta.CABECALHO_TOPICOS)
					|| campoDados.getEtiqueta().equals(Etiqueta.CABECALHO_NOME_GEOGRAFICO)){
				for (SubCampo sc : campoDados.getSubCampos()) {  // só tem um por autoridade por vez
					
					if(StringUtils.notEmpty(sc.getDado())){
						
						if(autoridadeCache.getEntradaAutorizadaAssunto() != null){
							autoridadeCache.setEntradaAutorizadaAssunto(autoridadeCache.getEntradaAutorizadaAssunto()+" "+sc.getDado()); // sem o separador, a entrada autorizada assunto continua sendo apenas um campo, mas composto por todos os subcampos
							autoridadeCache.setEntradaAutorizadaAssuntoAscii(autoridadeCache.getEntradaAutorizadaAssuntoAscii()+" "+ StringUtils.toAsciiAndUpperCase(sc.getDado()));
						}else{
							autoridadeCache.setCampoEntradaAutorizada(campoDados.getEtiqueta().getTag());
							autoridadeCache.setEntradaAutorizadaAssunto(sc.getDado());
							autoridadeCache.setEntradaAutorizadaAssuntoAscii(StringUtils.toAsciiAndUpperCase(sc.getDado()));
						}
						
					}
				}
			}
			
			// 180$x
			if( campoDados.getEtiqueta().equals(Etiqueta.CABECALHO_GERAL_SUBDIVISAO)){
				for (SubCampo sc : campoDados.getSubCampos()) { // só tem um por autoridade por vez
					
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_X) &&  StringUtils.notEmpty(sc.getDado()) ){
						autoridadeCache.setCampoEntradaAutorizada(campoDados.getEtiqueta().getTag());
						autoridadeCache.setEntradaAutorizadaAssunto( sc.getDado());
						if(sc.getDadoAscii() != null)
							autoridadeCache.setEntradaAutorizadaAssuntoAscii(sc.getDadoAscii().toUpperCase() );
					}
				}
			}
			
			// os vários 450 ou 451 (todos os sub campos)
			if(campoDados.getEtiqueta().equals(Etiqueta.CABECALHO_TOPICOS_REMISSIVO)
					|| campoDados.getEtiqueta().equals(Etiqueta.CABECALHO_NOME_GEOGRAFICO_REMISSIVO)){
				
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(StringUtils.notEmpty(sc.getDado()) ){
						
						if(autoridadeCache.getNomesRemissivosAssunto() != null){
							autoridadeCache.setNomesRemissivosAssunto( autoridadeCache.getNomesRemissivosAssunto() +SEPARADOR_VALORES_CACHE+ sc.getDado() );
							autoridadeCache.setNomesRemissivosAssuntoAscii( autoridadeCache.getNomesRemissivosAssuntoAscii().toUpperCase() +" "+ sc.getDadoAscii() );
						}else{
							autoridadeCache.setNomesRemissivosAssunto( sc.getDado() );
							if( sc.getDadoAscii() != null)
								autoridadeCache.setNomesRemissivosAssuntoAscii( sc.getDadoAscii().toUpperCase() );
						}
					}
				}
			}
			
			// ou 480$x
			if( campoDados.getEtiqueta().equals(Etiqueta.CABECALHO_GERAL_SUBDIVISAO_REMISSIVO)){
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_X) &&  StringUtils.notEmpty(sc.getDado()) ){
						
						if(autoridadeCache.getNomesRemissivosAssunto() != null){
							autoridadeCache.setNomesRemissivosAssunto( autoridadeCache.getNomesRemissivosAssunto() +SEPARADOR_VALORES_CACHE+  sc.getDado() );
							autoridadeCache.setNomesRemissivosAssuntoAscii(autoridadeCache.getNomesRemissivosAssuntoAscii().toUpperCase() +" "+  sc.getDadoAscii());
						}else{
							autoridadeCache.setNomesRemissivosAssunto( sc.getDado() );
							if( sc.getDadoAscii() != null)
								autoridadeCache.setNomesRemissivosAssuntoAscii( sc.getDadoAscii().toUpperCase());
						}
					}
				}
			}
			
			
		} // fim do for nos campos de dados
		
		// IMPORTANTE, tem que chamar senão a pesquisa não vai funcionar direito.
		autoridadeCache.formataDadosPesquisa();
		
		if(atualizando){
			dao.update(autoridadeCache);
		}else{
			dao.create(autoridadeCache);
		}
		
	}
	
	
	
	/**
	 *    Método que converte um Título em um Título num formato mais amigável para realizar as buscas
	 * do sistema. Vai ser chamado sempre no processador de catalogação e no processador que atualiza
	 * os títulos.
	 *
	 * @param título o título real com seus campo de controle, dados e subcampos
	 * @param se está atualizando um título ou está criando.
	 * @return um título sem campos de controle, dados e subcampos. Os dados necessário ficam no
	 *            próprio título para otimizar as buscas.
	 * @throws DAOException
	 */
	public static void sincronizaArtigoDePeriodicoCache(GenericDAO dao, ArtigoDePeriodico artigo, boolean atualizando) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		CacheEntidadesMarc artigoCache = null;
		
		if(atualizando){
			artigoCache = dao.findByExactField(CacheEntidadesMarc.class, "idArtigoDePeriodico",  artigo.getId(), true);
		
			if(artigoCache == null){
				throw new DAOException(" Não existe um objeto cache para o título persistido");
			}
			
		}else
			artigoCache = new CacheEntidadesMarc();
		
		artigoCache = sincronizaArtigoDePeriodicoCache(artigo, artigoCache);
		
		System.out.println("$$$$$$$$$$$$$ mondar os dados do cache de artigos demorou : "+(System.currentTimeMillis()-tempo)+" ms");
		
		if(atualizando){
			dao.update(artigoCache);
		}else{
			dao.create(artigoCache);
		}
		
		System.out.println("$$$$$$$$$$$$$ salvar os dados do cache de artigos demorou : "+(System.currentTimeMillis()-tempo)+" ms");
	}
	
	
	/**
	 *    Método que converte um Título em um Título num formato mais amigável para realizar as buscas
	 * do sistema. Vai ser chamado sempre no processador de catalogação e no processador que atualiza
	 * os títulos.
	 *
	 * @param título o título real com seus campo de controle, dados e subcampos
	 * @param se está atualizando um título ou está criando.
	 * @return um título sem campos de controle, dados e subcampos. Os dados necessário ficam no
	 *            próprio título para otimizar as buscas.
	 * @throws DAOException
	 */
	public static CacheEntidadesMarc sincronizaArtigoDePeriodicoCache(ArtigoDePeriodico artigo, CacheEntidadesMarc artigoCache) {
	
		
		
		/* *************************************************************************
		 * IMPORTANTE: Adicionar novos campos no cache, adicione-o nesse método para
		 * ser apagado quando for ser feita a sincronização do cache.
		 ***************************************************************************/
		artigoCache.zeraDadosMARCCache();
		
		artigoCache.setIdArtigoDePeriodico(artigo.getId());
		artigoCache.setNumeroDoSistema(artigo.getNumeroDoSistema());
		artigoCache.setCatalogado(true);  // para artigos não existem meia catalogação até agora. É sempre true.
		
		
		if(artigo.getCamposDados() != null)
		for (CampoDados campoDados : artigo.getCamposDados()) {
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.TITULO)){ // só tem um
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						artigoCache.setTitulo( sc.getDado() );
						if(sc.getDadoAscii() != null)
							artigoCache.setTituloAscii( sc.getDadoAscii().toUpperCase() );
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.AUTOR)){ // só tem um por vez
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						artigoCache.setAutor( sc.getDado() );
						if(sc.getDadoAscii() != null)
							artigoCache.setAutorAscii(sc.getDadoAscii().toUpperCase() );
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.AUTOR_SECUNDARIO)){
				for ( SubCampo sc : campoDados.getSubCampos() ) {
					
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						if ( artigoCache.getAutoresSecundarios() != null ) {
							artigoCache.setAutoresSecundarios(artigoCache.getAutoresSecundarios() +SEPARADOR_VALORES_CACHE + sc.getDado() );
						} else {
							artigoCache.setAutoresSecundarios( sc.getDado() );
						}
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO)){ // pode ter vários pois o campo pode ser repetido
				for (SubCampo sc : campoDados.getSubCampos()) {
					
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						
						if(artigoCache.getLocalPublicacao() != null)
							artigoCache.setLocalPublicacao( artigoCache.getLocalPublicacao() +SEPARADOR_VALORES_CACHE+ sc.getDado() );
						else
							artigoCache.setLocalPublicacao( sc.getDado() );
					}
					
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_B)){
						if(artigoCache.getEditora() != null)
							artigoCache.setEditora( artigoCache.getEditora() +SEPARADOR_VALORES_CACHE+ sc.getDado() );
						else
							artigoCache.setEditora(sc.getDado() );
					}
					
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_C)){
						
						if(artigoCache.getAno() != null)
							artigoCache.setAno( artigoCache.getAno() +SEPARADOR_VALORES_CACHE+ sc.getDado() );
						else
							artigoCache.setAno( sc.getDado() );
					}
				}
			}
			
			if(campoDados.getEtiqueta().equals(Etiqueta.ASSUNTO)){ // pode ter vários pois o campo pode ser repetido
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						
						if(artigoCache.getAssunto() != null){
							artigoCache.setAssunto( artigoCache.getAssunto() +SEPARADOR_VALORES_CACHE+ sc.getDado() );
							artigoCache.setAssuntoAscii( toAsciiAndUpperCase( artigoCache.getAssuntoAscii()) +" "+ toAsciiAndUpperCase(sc.getDadoAscii()));
						}else{
							artigoCache.setAssunto( sc.getDado() );
							artigoCache.setAssuntoAscii( toAsciiAndUpperCase(sc.getDadoAscii()));
						}
					}
				}
			}
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.LIGACAO)){ // pode ter vários pois o campo pode ser repetido
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_G)){
						artigoCache.setIntervaloPaginas( sc.getDado());
					}
				}
			}
			
			
			if(campoDados.getEtiqueta().equals(Etiqueta.RESUMO)){ // pode ter vários pois o campo pode ser repetido
				for (SubCampo sc : campoDados.getSubCampos()) {
					if(sc.getCodigo().equals(SubCampo.SUB_CAMPO_A)){
						artigoCache.setResumo( sc.getDado());
					}
				}
			}
			
			
		} // fim do for nos campos de dados
	
		artigoCache.formataDadosPesquisa(); // IMPORTANTE, tem que chamar senão a pesquisa não vai funcionar direito.
		
		return artigoCache;
	
	}
	
	
	
	/**
	 * 
	 *    Método que atribui o tamanho correto a um campo de controle.
	 *    Usado para consertar caso os dados do arquivo importado venham incorretos. Campos de controle
	 * com um tamanho menor que o possível.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/
	 * @param e
	 * @param dadosControle
	 * @return
	 */
	public static String formataDadosCampoControle(Etiqueta e, String dadosControle){
		
		char []dadosPreenchimento = new char[0];
		
		if(e.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO)){
			dadosPreenchimento = CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO.toCharArray();
		}
		
		if(e.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO)){
			dadosPreenchimento = CampoControle.DADOS_CAMPO_006_BIBLIOGRAFICO.toCharArray();
		}
		
		if(e.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO)){
			dadosPreenchimento = CampoControle.DADOS_CAMPO_007_BIBLIOGRAFICO.toCharArray();
		}
		
		if(e.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO)){
			dadosPreenchimento = CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO.toCharArray();
		}
		
		if(e.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE)){
			dadosPreenchimento = CampoControle.DADOS_CAMPO_LIDER_AUTORIDADE.toCharArray();
		}
		
		if(e.equals(Etiqueta.CAMPO_008_AUTORIDADE)){
			dadosPreenchimento = CampoControle.DADOS_CAMPO_008_AUTORIDADE.toCharArray();
		}
		
		for (int i = dadosControle.length() ; i < dadosPreenchimento.length; i++)
			dadosControle += dadosPreenchimento[i];

		return dadosControle;
		
	}
	
	
	/**
	 * 
	 * Método que duplica as informações que podem ser duplicadas de um título.
	 * 
	 * É usado principalmente no caso de catalogar duplicando as informações de outro título.
	 * Copia tudo para outro título menos o id, número sistema e os materiais.
	 *
	 * @param tituloModelo
	 */
	public static TituloCatalografico duplicarTitulo(TituloCatalografico tituloModelo){

		TituloCatalografico titulo = new TituloCatalografico();
		
		if(tituloModelo.getCamposControle() != null)
			for (CampoControle campo : tituloModelo.getCamposControle()) {
	
				// IMPORTANTE já adiciona o campo ao título
				new CampoControle(campo.getDado(), campo.getEtiqueta(), campo.getPosicao(), titulo);
	
			}

		if(tituloModelo.getCamposDados() != null)
			for (CampoDados campo : tituloModelo.getCamposDados()) {
	
				// IMPORTANTE já adiciona o campo ao título
				CampoDados campoTemp = new CampoDados(campo.getEtiqueta(), campo.getIndicador1(), campo.getIndicador2(),
						titulo, campo.getPosicao());
	
				for (SubCampo sub : campo.getSubCampos()) {
	
					// Já adiciona o sub campo ao campo
					new SubCampo(sub.getCodigo(), sub.getDado(), campoTemp, sub.getPosicao());
	
				}
	
			}

		titulo.setFormatoMaterial( tituloModelo.getFormatoMaterial() );

		
		return titulo;
	}
	
	
	
	/**
	 * 
	 * Método que duplica as informações que podem ser duplicadas de um título.
	 * 
	 * É usado principalmente no caso de catalogar duplicando as informações de outro título.
	 * Copia tudo para outro título menos o id, número sistema e os materiais.
	 *
	 * @param tituloModelo
	 */
	public static Autoridade duplicarAutoridade(Autoridade autoriadeModelo){

		Autoridade autoridade = new Autoridade();
		
		if(autoriadeModelo.getCamposControle() != null)
			for (CampoControle campo : autoriadeModelo.getCamposControle()) {
	
				// IMPORTANTE já adiciona o campo ao título
				new CampoControle(campo.getDado(), campo.getEtiqueta(), campo.getPosicao(), autoridade );
	
	
			}

		if(autoriadeModelo.getCamposDados() != null)
			for (CampoDados campo : autoriadeModelo.getCamposDados()) {
	
				// IMPORTANTE já adiciona o campo ao título
				CampoDados campoTemp = new CampoDados(campo.getEtiqueta(), campo.getIndicador1(), campo.getIndicador2(),
						autoridade, campo.getPosicao());
	
				for (SubCampo sub : campo.getSubCampos()) {
	
					// Já adiciona o sub campo ao campo
					new SubCampo(sub.getCodigo(), sub.getDado(), campoTemp, sub.getPosicao());
	
				}
	
			}

		
		return autoridade;
	}
	
	
	/***
	 *   Calculando dentre os números de patrimônio que vieram do SIPAC quais aqueles que já estão
	 * catalogados no acervo e quais aqueles que ainda estão livre para uso.
	 */
	public static void calculaInformacoesExemplaresNoAcerco(InformacoesTombamentoMateriaisDTO infomacoesTituloCompra)
			throws DAOException{
		
		// para todas as informações do título que vieram do SIPAC
		
		MaterialInformacionalDao materialDao = null;
		
		try{
			
			materialDao = DAOFactory.getInstance().getDAO(MaterialInformacionalDao.class);
			Set<Integer> idsBens =  infomacoesTituloCompra.numerosPatrimonio.keySet();
			
			for (Integer idBem : idsBens) {  // para cada bem
				
				Long numeroPatrimonio = infomacoesTituloCompra.numerosPatrimonio.get(idBem); // igual ao código de barras para os exemplares tombados;
				
				if(numeroPatrimonio == null)
					continue; // pula
				if(! materialDao.existeMateriaisByCodigosBarras(numeroPatrimonio.toString(), null) ){
					infomacoesTituloCompra.adicionaNumeroPatrimonioNaoUsado(idBem, infomacoesTituloCompra.numerosPatrimonio.get(idBem));
				}
			}
			
		
		}finally{
			if(materialDao != null) materialDao.close();
		}
		
	}
	

	/**
	 *    Retorna uma lista com os valores padrões do campo líder bibliográfico que são
	 * obtidos a partir do formato material. É o único valor padrão que é obtido a partir do material.
	 * 
	 * @param formato
	 * @return
	 * @throws DAOException
	 */
	public static List<ValorPadraoCampoControle> getValoresPadraoLiderBibliografico( FormatoMaterial formato ) throws DAOException {
		GenericDAO dao = null;
		
		try {
			dao = DAOFactory.getGeneric( Sistema.SIGAA );
			ArrayList<ValorPadraoCampoControle> lista = (ArrayList<ValorPadraoCampoControle>) dao.findByExactField(
					ValorPadraoCampoControle.class, "formatoMaterial.id", formato.getId(), "asc", "posicaoInicial");

			return lista;
		} finally {
			if (dao != null)
				dao.close();
		}
	}



	/**
	 *   Retorna uma lista com os valores padrões do campo 006 ou 008 bibliográfico.
	 *   Os valores padrão do campo 006 e 008 estão no FormatoMaterialEtiqueta porque dependem
	 * do FormatoMaterial que está sendo catalogado e da etiqueta.
	 * 
	 * @param formato
	 * @return
	 * @throws DAOException
	 */
	public static List<ValorPadraoCampoControle> getValoresPadrao006008Bibliografico( FormatoMaterial formato, Etiqueta etiqueta ) throws DAOException {
		FormatoMaterialEtiquetaDao dao = null;
		EtiquetaDao daoEdiqueta = null;
		
		try {
			dao = DAOFactory.getInstance().getDAO(FormatoMaterialEtiquetaDao.class);

			daoEdiqueta = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
			
			etiqueta = daoEdiqueta.findEtiquetaPorTagETipoAtiva(etiqueta.getTag(), etiqueta.getTipo());
			
			FormatoMaterialEtiqueta formatoMaterialEtiqueta =  dao.buscaFormatoMaterialEtiqueta(etiqueta, formato);
			
			ArrayList<ValorPadraoCampoControle> lista = (ArrayList<ValorPadraoCampoControle> ) dao.findByExactField(
					ValorPadraoCampoControle.class, "formatoMaterialEtiqueta.id", formatoMaterialEtiqueta.getId(), "asc", "posicaoInicial");

			return lista;
			
		} finally {

			if(daoEdiqueta != null) daoEdiqueta.close();
			
			if (dao != null) dao.close();
		}

	}
	

	/**
	 * Retorna uma lista com os valores padrões do campo 007 bibliográfico.
	 * É o único que os valores padrão estão em função da categoria do material
	 * @param formato
	 * @return
	 * @throws DAOException
	 */
	public static List<ValorPadraoCampoControle> getValoresPadrao007Bibliografico( String codigoCategoria ) throws DAOException {
		GenericDAO dao = null;
		try {
			dao = DAOFactory.getGeneric( Sistema.SIGAA );

			ArrayList<ValorPadraoCampoControle> lista =  (ArrayList<ValorPadraoCampoControle>)dao.findByExactField(
					ValorPadraoCampoControle.class, "categoriaMaterial.codigo", codigoCategoria, "asc", "posicaoInicial");
			
			return lista;
		} finally {
			if (dao != null)
				dao.close();
		}
	}


	/**
	 * Retorna uma lista com os valores padrões do campo líder e 008 para autoridades.
	 * Só essas duas etiquetas vão ter valor padrão em autoridades.
	 * @param formato
	 * @return
	 * @throws DAOException
	 */
	public static List<ValorPadraoCampoControle> getValoresPadraoAutoridades( Etiqueta e ) throws DAOException {
		FormatoMaterialEtiquetaDao dao = null;
		EtiquetaDao daoEdiqueta = null;
		
		try {
			dao = DAOFactory.getInstance().getDAO(FormatoMaterialEtiquetaDao.class);
			daoEdiqueta = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
			
			e = daoEdiqueta.findEtiquetaPorTagETipoAtiva(e.getTag(), e.getTipo());
			
			FormatoMaterialEtiqueta formatoMaterialEtiqueta =  dao.buscaFormatoMaterialEtiquetaQueNaoPossuemFormatoMaterial(e);
			
			if (formatoMaterialEtiqueta != null ){
			
				ArrayList<ValorPadraoCampoControle> lista = (ArrayList<ValorPadraoCampoControle> ) dao.findByExactField(
					ValorPadraoCampoControle.class, "formatoMaterialEtiqueta.id", formatoMaterialEtiqueta.getId(), "asc", "posicaoInicial");

				return lista;
			}else{
				
				throw new DAOException("Valor padrão para a etiqueta "+e.getDescricaoTipoEtiqueta()+" não cadastrados");
			}
			
		} finally {

			if(daoEdiqueta != null) daoEdiqueta.close();
			if (dao != null) dao.close();
		}
	}
	


	/**
	 * Método que monta a string de dados do campo de controle na posição correta de acordo com a coleção de valores
	 * @param valores
	 * @return string do campo líder formatado com os dados da coleção
	 * @throws NegocioException
	 * @throws NegocioException
	 */
	public static String montaDadosCampoControle(List<ValorPadraoCampoControle> valores, Etiqueta etiquetaDoCampo) throws NegocioException{

		String dadosCampo = null;
		
		if(etiquetaDoCampo.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO)){
			dadosCampo = CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO;
		}
		
		if(etiquetaDoCampo.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO)){
			dadosCampo = CampoControle.DADOS_CAMPO_006_BIBLIOGRAFICO;
		}
		
		if(etiquetaDoCampo.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO)){
			dadosCampo = CampoControle.DADOS_CAMPO_007_BIBLIOGRAFICO;
		}
		
		if(etiquetaDoCampo.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO)){
			dadosCampo = CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO;
		}
		
		if(etiquetaDoCampo.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE)){
			dadosCampo = CampoControle.DADOS_CAMPO_LIDER_AUTORIDADE;
		}
		
		if(etiquetaDoCampo.equals(Etiqueta.CAMPO_008_AUTORIDADE)){
			dadosCampo = CampoControle.DADOS_CAMPO_008_AUTORIDADE;
		}
		
		if(dadosCampo != null){
		
			if( valores == null )
				return dadosCampo;
	
			// para cada valor vindo da tela
			for (ValorPadraoCampoControle valor : valores) {
	
				/**
				 * Se o usuário digitou alguma coisa na tela, senão vai permanecer com espaço em branco
				 * e vai ter que existir um espaço em branco cadastrado entre os possíveis valores para
				 * os seus descritores, senão vai dá erro na validação
				 */
				if( StringUtils.notEmpty(valor.getValorPadrao()) ){
	
					int posicaoI = valor.getPosicaoInicial();
					int posicaoF = valor.getPosicaoFinal();
					int tamanhoDoCampo = (posicaoF - posicaoI) + 1;
	
					//				//erro
					//				if(tamanhoDoCampo < valor.getValorPadrao().length()){
					//					throw new NegocioException("O tamanho máximo para o campo "
					//							+ valor.getDescricao() + " é de: " + tamanhoDoCampo + " caracteres");
					//				}
	
					/**
					 * Em alguns casos o campo pode ter 4 posições. Significa que o campo pode ter
					 * até 4 valores possíveis. Mas o usuário pode digitar apenas 1. são os campos que
					 * podem possuir múltiplos valores.
					 */
					if(valor.getValorPadrao().length() < tamanhoDoCampo ){
						// completa o campo com espaços em branco
						for (int i = valor.getValorPadrao().length(); i < tamanhoDoCampo; i++) {
							valor.setValorPadrao(valor.getValorPadrao()+" ");
						}
					}
	
					// Então retira o caracter que estava em branco e substitui pelo valor que o
					// usuário digitou na posição correta
					try{
						dadosCampo = dadosCampo.substring(0, posicaoI) + valor.getValorPadrao() +
								dadosCampo.substring(posicaoF+1, dadosCampo.length());
					}catch(StringIndexOutOfBoundsException sobex){
						
						// Se o usuário voltar pelo navegador ele está com os dados do próximo campo carregado
						// e vai tentar montar o outro.
						sobex.printStackTrace();
						throw new NegocioException(" Você utilizou o botão voltar do navegador, por favor reinicie o processo.");
					}
				}
			}
		
		}
		
		return dadosCampo;
	}


	/**
	 * 
	 * Pega os dados do campo de controle e monta os valores padrão para o usuário pode editar na tela.
	 * 
	 * Faz exatamente o contrário do método <code>montaDadosCampo008</code>
	 *
	 * @param dadosCampoLider
	 * @return
	 */
	public static List<ValorPadraoCampoControle> montaValoresCampo(String dadosCampo, List<ValorPadraoCampoControle> valoresPadrao){

		List<ValorPadraoCampoControle> valorLocal = cloneValorPadrao(valoresPadrao);

		for (ValorPadraoCampoControle novoValorPadrao : valorLocal) {

			novoValorPadrao.setValorPadrao( dadosCampo.substring(novoValorPadrao.getPosicaoInicial(),
					novoValorPadrao.getPosicaoFinal()+1).trim() );
		}

		return valorLocal;
	}




	/**
	 * 
	 * Retorna uma lista que é cópia dos valores padrões passados para o usuário editar na tela.
	 *
	 * @param valorPadraoLider
	 * @param valorLider
	 */
	public static List<ValorPadraoCampoControle> cloneValorPadrao(List<ValorPadraoCampoControle> valorPadrao){

		List<ValorPadraoCampoControle> valor = new ArrayList<ValorPadraoCampoControle>();


		for (ValorPadraoCampoControle v : valorPadrao) {
			ValorPadraoCampoControle novoValor = new ValorPadraoCampoControle(v.getDescricao()
					, v.getPosicaoInicial(), v.getPosicaoFinal(), v.getValorPadrao(), v.getCategoriaMaterial());
			valor.add(novoValor);
		}

		return valor;

	}

	////////////////////// fim dos métodos para os campos de controle //////////////

	/**
	 * 
	 * Método que retorna o campo de controle que esteja atribuído ao título passado pela etiqueta.
	 * 
	 * OBS.: Só funciona para campos que não podem ser repetidos, se existir mais de um retorna
	 * apenas o primeiro.
	 *
	 * @param titulo
	 * @param tag
	 * @return
	 */
	public static CampoControle pegaCampoControle (TituloCatalografico titulo, String tag){

		if(titulo.getCamposControle() != null)
		for (CampoControle c : titulo.getCamposControle()) {
			if(c.getEtiqueta().getTag().equals(tag)){
				return c;
			}
		}

		return null; // nunca era para chegar aqui
	}


	/**
	 * Remove o campo de controle que tem a etiqueta informada do título catalográfico informado
	 * @param titulo que terá o campo de controle removido
	 * @param etiqueta etiqueta do campo de controle a ser removido
	 */
	public static void removeCampoControle(TituloCatalografico titulo, Etiqueta etiqueta){

		if(titulo.getCamposControle() == null)
			return;

		for (Iterator<CampoControle> i = titulo.getCamposControle().iterator(); i.hasNext();) {
			if(i.next().getEtiqueta().getTag().equals(etiqueta.getTag())){
				i.remove();
			}
		}
	}


	/**
	 * Retira campos que possuem a etiqueta nula e sub campos que não contém códigos.
	 * Se um campo tiver todos os seus subcampos vazios deve ser retirado também
	 * Esse método é necessário porque para se fazer a interface gráfica o encapsulamento
	 * foi quebrado e os objetos podem assumir estado inconsistente como um campo sem uma etiqueta.
	 */
	public static void retiraCamposDadosVazios(Object entidadeMarc){
		
		Iterator<CampoDados> i = null;
		
		
		
		if(entidadeMarc instanceof TituloCatalografico){
			
			TituloCatalografico t = (TituloCatalografico) entidadeMarc;
			
			if(t.getCamposDados() == null)
				return;
			
			i = t.getCamposDados().iterator();
		}else{
			
			Autoridade a = (Autoridade) entidadeMarc;
			
			if(a.getCamposDados() == null)
				return;
			
			i = a.getCamposDados().iterator();
		}
		
		while(i.hasNext()){

			CampoDados campo = i.next();

			if(campo.getEtiqueta() == null || StringUtils.isEmpty(campo.getEtiqueta().getDescricao()) ){
				i.remove();
			}else{
				Iterator<SubCampo> j = campo.getSubCampos().iterator();

				while(j.hasNext()){
					SubCampo sub = j.next();
					if( sub == null || sub.getCodigo() == null || ( !Character.isLetter(sub.getCodigo()) &&  !Character.isDigit(sub.getCodigo()) )
							|| StringUtils.isEmpty(sub.getDado()) ){
						j.remove();
					}
				}

				// se não sobrou nenhum sub campos, remove o campo
				if( campo.getSubCampos().size() == 0 ){
					i.remove();
				}

			}

		}
		
		
	}
	
	
	
	
	/**
	 * Método que cria os campos de controle do título com os dados da planilha.
	 * 
	 * @throws NegocioException
	 */
	public static void criaCamposDeControle(PlanilhaCatalogacao planilha, TituloCatalografico titulo, List<Etiqueta> etiquetasBuscadas)throws DAOException{

		// Para cada objeto de controle crio um campo de controle para o título

		if(planilha.getObjetosPlanilhaCatalogacaoControle() != null){

			int idCampoControle = 1;
			
			for (ObjetoPlanilhaCatalogacaoControle objetoControle : planilha.getObjetosPlanilhaCatalogacaoControle()) {

				// pega a etiqueta, era para existir um por tag;
				Etiqueta e =   recuperaEtiquetaTitulo( new Etiqueta(objetoControle.getTagEtiqueta(), planilha.getTipo()) , etiquetasBuscadas);

				CampoControle c = new CampoControle(objetoControle.getDado(), e  , -1, titulo );
				c.setIdentificadorTemp(idCampoControle++);
			}
		}
				
		

	}

	
	
	/**
	 * Cria os campos de dados de acordo com os dados da planilha.
	 */
	public static void criaCamposDeDados(PlanilhaCatalogacao planilha, TituloCatalografico titulo, List<Etiqueta> etiquetasBuscadas)throws DAOException{

		
		// Para cado objeto de dados crio um campos de dados
		if(planilha.getObjetosPlanilhaCatalogacaoDados() != null){
			
			int idCampoDados = 1;
			
			for (ObjetoPlanilhaCatalogacaoDados objetoDado : planilha.getObjetosPlanilhaCatalogacaoDados()) {

				
				Etiqueta e =  recuperaEtiquetaTitulo( new Etiqueta(objetoDado.getTagEtiqueta(), planilha.getTipo()) , etiquetasBuscadas);

				if(e != null) {
				
					// Cria o campo de dados  e já adiciona ao título
					CampoDados c = new CampoDados(e, titulo, -1);
	
					// Não esquecer dos indicadores
					c.setIndicador1(objetoDado.getIndicador1());
					c.setIndicador2(objetoDado.getIndicador2());
	
					c.setIdentificadorTemp(idCampoDados++);
					
					// Cria os sub campos desse campo
					criaSubCampos(c, objetoDado);

				}
				
				// Etiqueta foi removida por alguém e não pode ser usado mais na catalogação.
				
			}
			
		}

		
	}

	
	
	/**
	 * Cria os sub campos para os campos de dados.
	 * @throws DAOException 
	 */
	private static void criaSubCampos(CampoDados c, ObjetoPlanilhaCatalogacaoDados objetoDado) throws DAOException{

		// Quebra a String subCampos do objeto de dados que vem assim: $a$b$c$n$x
		StringTokenizer tokens = new StringTokenizer(objetoDado.getSubCampos(), "$");

		int idSubCampo = 1;
		
		// Cada token é o código de um novo sub campo: a - b - c - n - x
		while (tokens.hasMoreTokens()) {

			String codigoCampo =  tokens.nextToken();
			// Já adiciona ao campos de dados
			SubCampo s = new SubCampo(codigoCampo.charAt(0), c, -1);
			s.setIdentificadorTemp(idSubCampo++);
		}
	}

	
	
	
	
	
	/**
	 * 
	 *   Método que copia todas as informações de um título para uma autoridade.
	 *   Isso é necessário porque o Bean de catalogação trabalha sempre com um título. Só no final ele
	 * coloca essa informações em uma autoridade para salvar ou atualizar no banco.
	 *
	 * @param obj
	 * @return
	 */
	public static Autoridade criaAutoridaeAPartirTitulo(TituloCatalografico obj){
		
		Autoridade a = new Autoridade();
		a.setId(obj.getId());
		a.setNumeroDoSistema(obj.getNumeroDoSistema());
		a.setImportada(obj.isImportado());
		a.setBiblotecaImportacao(obj.getBibliotecaImportacao());
		
		a.setRegistroCriacao( obj.getRegistroCriacao() );
		a.setDataCriacao( obj.getDataCriacao());
		a.setRegistroUltimaAtualizacao( obj.getRegistroUltimaAtualizacao() );
		a.setDataUltimaAtualizacao( obj.getDataUltimaAtualizacao() );
		
		a.setCatalogada(obj.isCatalogado());
		
		if(obj.getCamposControle() != null){
			for (CampoControle controle : obj.getCamposControle()) {
				controle.setTituloCatalografico(null);
				controle.setAutoridade(a);
				a.addCampoControle( controle);
			}
		}
		
		if(obj.getCamposDados() != null){
			for (CampoDados dado : obj.getCamposDados()) {
				
				dado.setTituloCatalografico(null);
				dado.setAutoridade(a);
				a.addCampoDados(dado);
				
			}
		}
		
		return a;
		
	}
	
	/**
	 * 
	 *   Método que copia todas as informações de uma autoridade para um título.
	 *   Isso é necessário porque o Bean de catalogação trabalha sempre com um título. Só no final ele
	 * coloca essa informações em uma autoridade para salvar ou atualizar no banco.
	 *
	 * @param obj
	 * @return
	 */
	public static TituloCatalografico criaTituloAPartirAutoridade(Autoridade a){
		
		TituloCatalografico t = new TituloCatalografico();
		t.setId(a.getId());
		t.setNumeroDoSistema(a.getNumeroDoSistema());

		t.setImportado( a.isImportada() );
		t.setBibliotecaImportacao( a.getBiblotecaImportacao() );
		
		t.setRegistroCriacao( a.getRegistroCriacao() );
		t.setDataCriacao( a.getDataCriacao());
		t.setRegistroUltimaAtualizacao( a.getRegistroUltimaAtualizacao() );
		t.setDataUltimaAtualizacao( a.getDataUltimaAtualizacao() );
		
		t.setCatalogado(a.isCatalogada());
		
		if(a.getCamposControle() != null){
			for (CampoControle controle : a.getCamposControle()) {
				controle.setTituloCatalografico(t);
				controle.setAutoridade(null);
				t.addCampoControle( controle);
			}
		}
			
		if(a.getCamposDados() != null){
			for (CampoDados dado : a.getCamposDados()) {
	
				if(dado == null)
					continue;
				
				dado.setTituloCatalografico(t);
				dado.setAutoridade(null);
				t.addCampoDados(dado);
				
			}
		}
		
		return t;
		
	}
	
	
	
	
	
	
	
	/**
	 * Cria um título catalográfico populando os campos com os dados existentes
	 * em uma banca de pós.
	 */
	public static void criarTituloCatalograficoAPartirDeDefesa(BancaPos banca, TituloCatalografico titulo) throws DAOException {

		EtiquetaDao dao = null;

		try {
			dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
			
			titulo.setIdDadosDefesa( banca.getDadosDefesa().getId() );

			if (banca.getDadosDefesa() != null
					&& banca.getDadosDefesa().getDiscente() != null) {
				CampoDados cAutor = new CampoDados(
						dao.findEtiquetaInicializandoDados(Etiqueta.AUTOR),
						titulo, SubCampo.SUB_CAMPO_A, banca.getDadosDefesa()
								.getDiscente().getNome(), -1, -1);
				cAutor.setIndicador1('0');
			}

			if (banca.getTitulo() != null) {
				CampoDados cTitulo = new CampoDados(
						dao.findEtiquetaInicializandoDados(Etiqueta.TITULO),
						titulo, SubCampo.SUB_CAMPO_A,
						StringUtils.stripHtmlTags(StringUtils
								.unescapeHTML(banca.getTitulo())), -1, -1);
				cTitulo.setIndicador1('0');
				cTitulo.setIndicador2('0');
			}

			if (banca.getPaginas() != null) {
				new CampoDados(
						dao.findEtiquetaInicializandoDados(Etiqueta.DESCRICAO_FISICA),  // Na descrição física é guardado o número de páginas
						titulo, SubCampo.SUB_CAMPO_A, banca.getPaginas()
								.toString(), -1, -1);
			}

			if (banca.getDadosDefesa() != null
					&& banca.getDadosDefesa().getDiscente() != null
					&& banca.getDadosDefesa().getDiscente()
							.getGestoraAcademica() != null) {
				String nomeLocal = RepositorioDadosInstitucionais.getNomeInstituicao()
						+ ". "
						+ banca.getDadosDefesa().getDiscente()
								.getGestoraAcademica().getGestora().getNome()
						+ ". "
						+ banca.getDadosDefesa().getDiscente()
								.getGestoraAcademica().getNome() + ".";
				new CampoDados(
						dao.findEtiquetaInicializandoDados(Etiqueta.NOTA_DISSETACAO_TESE),
						titulo, SubCampo.SUB_CAMPO_A, nomeLocal, -1, -1);
			}

			if (banca.getResumo() != null) {
				new CampoDados(
						dao.findEtiquetaInicializandoDados(Etiqueta.RESUMO),
						titulo, SubCampo.SUB_CAMPO_A,
						StringUtils.stripHtmlTags(StringUtils
								.unescapeHTML(banca.getResumo())), -1, -1);
			}

			if (banca.getArea() != null && banca.getArea().getNome() != null) {
				new CampoDados(
						dao.findEtiquetaInicializandoDados(Etiqueta.AREA),
						titulo, SubCampo.SUB_CAMPO_A,
						banca.getArea().getNome(), -1, -1);
			}

			String titulacaoString = "Indefinido";
			if (NivelEnsino.MESTRADO == banca.getDadosDefesa().getDiscente()
					.getNivel()) {
				titulacaoString = "Mestrado";
			} else if (NivelEnsino.DOUTORADO == banca.getDadosDefesa()
					.getDiscente().getNivel()) {
				titulacaoString = "Doutorado";
			}
			new CampoDados(
					dao.findEtiquetaInicializandoDados(Etiqueta.TITULACAO),
					titulo, SubCampo.SUB_CAMPO_A, titulacaoString, -1, -1);

			if ( banca.getMembrosBanca() != null ) {
				for (MembroBancaPos m : banca.getMembrosBanca()) {
					new CampoDados(
							dao.findEtiquetaInicializandoDados(Etiqueta.MEMBRO_BANCA),
							titulo, SubCampo.SUB_CAMPO_A, m.getNome(), -1, -1);
				}
			}

			if (banca.getData() != null) {
				new CampoDados(
						dao.findEtiquetaInicializandoDados(Etiqueta.DATA_APRESENTACAO),
						titulo, SubCampo.SUB_CAMPO_A, CalendarUtils.format(
								banca.getData(), "dd/MM/yyyy"), -1, -1);
			}
		} finally {
			if (dao != null)
				dao.close();
		}

	}


	
	
	
	/**
	 * Faz um merge entre as informações da planilha e as informações que vêm previamente preenchidas do sipac.
	 *
	 * @param infoTituloCompra
	 * @param tituloTemp
	 * @throws DAOException
	 */
	public static void mergeInformacoesPlanilhaECompra(InformacoesTombamentoMateriaisDTO infoTituloCompra
			, TituloCatalografico tituloTemp) throws DAOException{

		// os campos que se precisa fazer merge
		//  autor;       ->  100$a
		//  autor2;      -> 100$b
		//  autor3;      -> 100$c
		//  titulo;      -> 245$a
		//  subtitulo;   -> 245$b
		//  local;       -> 260$a
		//  isbn;        -> 020$a
		//  edicao;      -> 250$a
		//  ano;         -> 260$c
		//  volume;      -> ???$?
		//  paginas;     -> ???$?
		//  serie;       -> 440$a

		boolean adicionouAutor = false;
		boolean adicionouAutor2 = false;
		boolean adicionouAutor3 = false;
		boolean adicionouTitulo = false;
		boolean adicionouSubTitulo = false;
		boolean adicionouLocal = false;
		boolean adicionouISBN = false;
		boolean adicionouEdicao = false;
		boolean adicionouAno = false;
		boolean adicionouSerie = false;

		if(tituloTemp.getCamposDados() != null)
		for (CampoDados dados: tituloTemp.getCamposDados()) {

			if(dados.getEtiqueta().equals(Etiqueta.ISBN)){
				for (SubCampo sub : dados.getSubCampos()) {
					if( sub.getCodigo() == SubCampo.SUB_CAMPO_A && StringUtils.notEmpty(infoTituloCompra.getIsbn()) ){
						sub.setDado(infoTituloCompra.getIsbn());
						adicionouISBN = true;
					}
				}

			}

			if(dados.getEtiqueta().equals(Etiqueta.AUTOR)){

				for (SubCampo sub : dados.getSubCampos()) {
					if( sub.getCodigo() == SubCampo.SUB_CAMPO_A && StringUtils.notEmpty(infoTituloCompra.getAutor())){
						sub.setDado(infoTituloCompra.getAutor());
						adicionouAutor = true;
					}

					if( sub.getCodigo() == SubCampo.SUB_CAMPO_B && StringUtils.notEmpty(infoTituloCompra.getAutor2())){
						sub.setDado(infoTituloCompra.getAutor2());
						adicionouAutor2 = true;
					}

					if( sub.getCodigo() == SubCampo.SUB_CAMPO_C && StringUtils.notEmpty(infoTituloCompra.getAutor3())){
						sub.setDado(infoTituloCompra.getAutor3());
						adicionouAutor3 = true;
					}
				}


			}

			if(dados.getEtiqueta().equals(Etiqueta.TITULO)){

				for (SubCampo sub : dados.getSubCampos()) {
					if( sub.getCodigo() == SubCampo.SUB_CAMPO_A && StringUtils.notEmpty(infoTituloCompra.getTitulo())){
						sub.setDado(infoTituloCompra.getTitulo());
						adicionouTitulo = true;
					}

					if( sub.getCodigo() == SubCampo.SUB_CAMPO_B && StringUtils.notEmpty(infoTituloCompra.getSubtitulo())){
						sub.setDado(infoTituloCompra.getSubtitulo());
						adicionouSubTitulo = true;
					}
				}

			}

			if(dados.getEtiqueta().equals(Etiqueta.EDICAO)){
				for (SubCampo sub : dados.getSubCampos()) {
					if( sub.getCodigo() == SubCampo.SUB_CAMPO_A && infoTituloCompra.getEdicao() > 0){
						sub.setDado( String.valueOf(infoTituloCompra.getEdicao()));
						adicionouEdicao = true;
					}
				}
			}

			if(dados.getEtiqueta().equals(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO)){

				for (SubCampo sub : dados.getSubCampos()) {
					if( sub.getCodigo() == SubCampo.SUB_CAMPO_A && StringUtils.notEmpty(infoTituloCompra.getLocal())){
						sub.setDado( infoTituloCompra.getLocal());
						adicionouLocal = true;
					}

					if( sub.getCodigo() == SubCampo.SUB_CAMPO_C && infoTituloCompra.getAno() > 0){

						sub.setDado( String.valueOf(infoTituloCompra.getAno()));
						adicionouAno = true;
					}
				}

			}

			if(dados.getEtiqueta().equals(Etiqueta.SERIE)){

				for (SubCampo sub : dados.getSubCampos()) {
					if( sub.getCodigo() == SubCampo.SUB_CAMPO_A & StringUtils.notEmpty(infoTituloCompra.getSerie())){
						sub.setDado( infoTituloCompra.getSerie());
						adicionouSerie = true;
					}
				}

			}

		} // fim do for


		/* ********* campos existiam na compra mas não na planilha, então precisa criar   ******** */

		EtiquetaDao dao = null;

		try {

			dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
			
			Etiqueta e = null;

			if(adicionouISBN == false && StringUtils.notEmpty(infoTituloCompra.getIsbn())){
				e = dao.findEtiquetaPorTagETipoAtiva("020", TipoCatalogacao.BIBLIOGRAFICA);
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_A, infoTituloCompra.getIsbn(), -1, -1);
			}

			e = dao.findEtiquetaPorTagETipoAtiva("100", TipoCatalogacao.BIBLIOGRAFICA);

			if(adicionouAutor == false && StringUtils.notEmpty(infoTituloCompra.getAutor())){
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_A, infoTituloCompra.getAutor(), -1, -1);
			}

			if(adicionouAutor2 == false && StringUtils.notEmpty(infoTituloCompra.getAutor2())){
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_B, infoTituloCompra.getAutor2(), -1, -1);
			}

			if(adicionouAutor3 == false && StringUtils.notEmpty(infoTituloCompra.getAutor3())){
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_C, infoTituloCompra.getAutor3(), -1, -1);
			}

			e = dao.findEtiquetaPorTagETipoAtiva("245", TipoCatalogacao.BIBLIOGRAFICA);

			if(adicionouTitulo == false && StringUtils.notEmpty(infoTituloCompra.getTitulo())){
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_A, infoTituloCompra.getTitulo(), -1, -1);
			}

			if(adicionouSubTitulo == false && StringUtils.notEmpty(infoTituloCompra.getSubtitulo())){
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_B, infoTituloCompra.getSubtitulo(),-1, -1);
			}

			e = dao.findEtiquetaPorTagETipoAtiva("260", TipoCatalogacao.BIBLIOGRAFICA);

			if(adicionouLocal == false && StringUtils.notEmpty(infoTituloCompra.getLocal())){
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_A, infoTituloCompra.getLocal(), -1, -1);
			}

			if(adicionouAno == false && infoTituloCompra.getAno() > 0){
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_C, String.valueOf(infoTituloCompra.getAno()), -1, -1);
			}

			if(adicionouSerie == false && StringUtils.notEmpty(infoTituloCompra.getSerie())){
				e = dao.findEtiquetaPorTagETipoAtiva("440", TipoCatalogacao.BIBLIOGRAFICA);
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_A, infoTituloCompra.getSerie(), -1, -1);
			}

			if(adicionouEdicao == false && infoTituloCompra.getEdicao() > 0){
				e = dao.findEtiquetaPorTagETipoAtiva("250", TipoCatalogacao.BIBLIOGRAFICA);
				new CampoDados(e, tituloTemp, SubCampo.SUB_CAMPO_A, String.valueOf(infoTituloCompra.getEdicao()), -1, -1);
			}

		} finally {
			if (dao != null) dao.close();
		}


	} // fim do método mergeInformacoesPlanilhaECompra

	
	
	
	/**
	 * <p>Retorna o código cutter referente ao título passado. <p>
	 * <p>Exemplo: <p>
	 * 
	 * <p>A111q
	 * 
     * <ul>
	 * <li>A: primeira letra do sobrenome do autor  (Ou do título se não existir autor) </li>
	 * <li>111: número da tabela correspondente ao sobrenome "mais próximo" do autor  (caso não exista autor, usa o nome no título) </li>
	 * <li>q: primeira letra do Título. (Se existir, caso não tenha autor e o título seja utilizado como autor, também não é mostrado aqui. ) </li>
	 * </ul>
     * <p>
     * 
	 * @param titulo
	 * @return
	 * @throws DAOException
	 */
	public static List<DadosTabelaCutter> gerarCodigoCutter( TituloCatalografico titulo ) throws DAOException {
		
		Character inicialSobreNomeAutor = null;
		Character inicialTituloObra = null;
		String sobreNomeAutor = "";
		String tituloObraCutter = "";
		
		// Os dados que vão ser retornados por esse método //
		List<DadosTabelaCutter> retorno = new ArrayList<DadosTabelaCutter>();
		
		
		/* **************************************************************************
		 *   Busca o caracter inicial do sobre nome, o caracter final do Título e o *
		 *   sobre nome do autor para poder gerar o código cuter.                   *
		 * **************************************************************************/
		
		if(titulo.getCamposDados() != null){
			
			boolean encontrouCampoAutor = false;
			boolean encontrouCampoTitulo = false;
			
			forExterno:
			for( CampoDados cd : titulo.getCamposDados() ){
				
				if(encontrouCampoAutor && encontrouCampoTitulo)  // não precisa mais procurar nos outros campos
					break forExterno;
				
				if( cd.getEtiqueta().getTag().equals(Etiqueta.AUTOR.getTag())
						|| cd.getEtiqueta().getTag().equals(Etiqueta.AUTOR_EVENTO.getTag())
						|| cd.getEtiqueta().getTag().equals(Etiqueta.AUTOR_COOPORATIVO.getTag()) && ! encontrouCampoAutor ){
					for( SubCampo sc : cd.getSubCampos() ){
						if( sc != null && sc.getCodigo() != null && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) && !isEmpty(sc.getDado())){
							inicialSobreNomeAutor = sc.getDado().toUpperCase().charAt(0);
							// verifica o que aparece primeiro, se é a vírgula ou o espaço em branco,
							// para pegar o último nome do autor (primeiro nome na String, exemplo: Assis, Machado de).
							// vai pegar do começo da string até a vírgula, espaço ou até o final se não tiver vírgula nem espaço.
							int indiceCorte = sc.getDado().indexOf(" ");
							
							if( indiceCorte == -1 ){ // não tem espaço
								indiceCorte = sc.getDado().indexOf(",");
								
								if( indiceCorte == -1 ){ // não tem vírgula
									
								}else{ // tem vírgula
									indiceCorte = sc.getDado().indexOf(",");
								}
							}else{ // tem espaço
								
								// se tem vírgula e a vírgula vem antes do espaço, pega até a vírgula
								if( sc.getDado().indexOf(",") != -1 && sc.getDado().indexOf(",") < sc.getDado().indexOf(" ") )
									indiceCorte = sc.getDado().indexOf(",");
								else{ // senão tem vírgula ou o espaço vem antes da vírgula
									indiceCorte = sc.getDado().indexOf(" ");
								}
							}
							
							if( indiceCorte == -1 ) // não tem espaço nem vírgula
								sobreNomeAutor = sc.getDado().substring(0, sc.getDado().length()); // pega até o final da string
							else
								sobreNomeAutor = sc.getDado().substring(0, indiceCorte);
							
							encontrouCampoAutor = true;
						}
					}
				}
				
				if( cd.getEtiqueta().equals(Etiqueta.TITULO) && ! encontrouCampoTitulo){
					for( SubCampo sc : cd.getSubCampos() ){
						if( sc.getCodigo() != null && !isEmpty(sc.getDado()) && sc.getCodigo().equals(SubCampo.SUB_CAMPO_A) ){
							
							String tituloObra = sc.getDado();
							
							StringBuilder buffer = new StringBuilder();
							
							int i = 0;
							boolean encontrouTitulo = false;
							boolean existeArtigo = false;
							while (i < tituloObra.length() && ! encontrouTitulo) {
	
								if(tituloObra.charAt(i) != ' ')
									buffer.append(tituloObra.charAt(i));
								else{ //encontrou o espaço
									for (int j = 0; j <= ARTIGOS.length -1; j++){
										// verifica se não tem algum dos artigos desprezados
										if (buffer.toString().equalsIgnoreCase(ARTIGOS[j])) {
											existeArtigo = true;
											break;
										}
									}
																																									
									if(buffer.toString().length() > 1 && !existeArtigo) // se tem só uma letra, esse não é o título.
										encontrouTitulo = true;
									else{
										buffer = new StringBuilder();
										existeArtigo = false;
									}
								}
								
								i++;
							}
							
							if(!isEmpty(buffer.toString())){
								inicialTituloObra = buffer.toString().toLowerCase().charAt(0);
								tituloObraCutter = buffer.toString();
								
								encontrouCampoTitulo = true;
							}
							
						}
					}
				}
				
			} // for campos dados
			
			
			/*
			 * Se a obra não tem autor principal, gera o código cutter com base no Título
			 * E a primeira lembra do Título vem em maiúscula no começo, ou seja,
			 * o Título vira o sobre nome do autor e a inicial do Título vira a inicial do autor
			 */
			if (sobreNomeAutor.isEmpty()){
				
				if(!isEmpty(tituloObraCutter)){
					sobreNomeAutor = tituloObraCutter;
					inicialSobreNomeAutor = tituloObraCutter.toUpperCase().charAt(0);
					inicialTituloObra = null;
				}
			}
			
			
			
		} // is campos dados != null
		
		
		final int  qtdResultadosDesejados = 30;
		int idPrimeiroCodCutter = 0;
		int idUltimoCodCutter = 0;
		
		int idCodCutterMaisProximo = 0;
		
		TabelaCutterDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(TabelaCutterDao.class);
			
			Object[] dadosCutter = dao.findCodigoCutterByNome(sobreNomeAutor);  // Procura se existe exatamento o código cutter do nome passado
			
			// Achou o resultado exato na tabela cutter
			if(dadosCutter != null){
				retorno.add(new DadosTabelaCutter((Integer)dadosCutter[0], inicialSobreNomeAutor,
													(Integer)dadosCutter[1], (String) dadosCutter[2], inicialTituloObra ));
				
				idPrimeiroCodCutter = idUltimoCodCutter = (Integer)dadosCutter[0] ;
				
			}else{ // Procura por intervalos de resultados
			
				int numeroTentativas = 0; // para impedir que fique em um laço infinito caso a lógica esteja errada
				
				boolean achouResultadosBetween = false;
				boolean achouResultadosLike = false;
				
				boolean achouCutter = false;
				String sobreNomeAutorTemp = sobreNomeAutor;
				
				
				while(! achouCutter && numeroTentativas < 100 && sobreNomeAutorTemp.length() > 0){
					
					sobreNomeAutorTemp = sobreNomeAutorTemp.substring(0, sobreNomeAutorTemp.length() - 1); // Busca pelo intervalo entre o nome-1 letra e o nome digitado
					
					List<Object[]> codigoCutterMaisProximos = dao.findCodigoCutterBetweenNome(sobreNomeAutorTemp, sobreNomeAutor, qtdResultadosDesejados);
					
					if(codigoCutterMaisProximos.size() > 0){
					
						for (int qtdCutters = 0 ;  qtdCutters < codigoCutterMaisProximos.size() ; qtdCutters++) {
							
							Object[] cutters = codigoCutterMaisProximos.get(qtdCutters);
							
							if(qtdCutters == 0){
								idUltimoCodCutter = (Integer)cutters[0];  // A lista é retornada ordenada em ordem descendente do id da tabela cutter
							}
						
							if(qtdCutters == codigoCutterMaisProximos.size()-1){
								idPrimeiroCodCutter = (Integer)cutters[0];  // A lista é retornada ordenada em ordem descendente do id da tabela cutter
							}
							
							retorno.add(new DadosTabelaCutter((Integer)cutters[0], inicialSobreNomeAutor, (Integer)cutters[1], (String) cutters[2], inicialTituloObra ));
						
						}
						
						achouResultadosBetween = true;
					}
					
					
					codigoCutterMaisProximos = dao.findCodigoCutterLikeNome(sobreNomeAutor, qtdResultadosDesejados);
					
					if(codigoCutterMaisProximos.size() > 0){
					
						for (int qtdCutters = 0 ;  qtdCutters < codigoCutterMaisProximos.size() ; qtdCutters++) {
							
							Object[] cutters = codigoCutterMaisProximos.get(qtdCutters);
							
							if(qtdCutters == 0){
								
								if(! achouResultadosBetween) // Se não achou resultado na busca com between o primeiro resultado vai ser o dessa lista, senão continua com o de lá.
									idPrimeiroCodCutter = (Integer)cutters[0];
							}
						
							if(qtdCutters == codigoCutterMaisProximos.size()-1){
								idUltimoCodCutter = (Integer)cutters[0];   // O último resultado é sempre dessa lista
							}
						
							retorno.add(new DadosTabelaCutter((Integer)cutters[0], inicialSobreNomeAutor, (Integer)cutters[1], (String) cutters[2], inicialTituloObra ));
							
						}
						
						achouResultadosLike = true;
					}
					
					if(achouResultadosLike || achouResultadosBetween)
						achouCutter = true;
					
					numeroTentativas ++;
				}
			}
			
			/*
			 * Configura dentre os resultados aquele que o sistema considera o mais correto.
			 */
			idCodCutterMaisProximo = calculaCodigoMaisProximo(retorno, sobreNomeAutor);
			
			for (DadosTabelaCutter dados : retorno) {
				
				if(dados.getIdTabelaCutter() == idCodCutterMaisProximo){
					dados.setCodigoCalculado();
					break;
				}
				
			}
			
		
			 /*
			  *  Para Garantir que o sistema retorna sempre a lista dos 20 elementos mais próximos.
			  * 
			  *  É a margem de erro, caso o calculo não tenha sido correto.
			  * 
			  */
			if(retorno.size() > 0 && retorno.size() < qtdResultadosDesejados){
				List<Object[]> codigosCutterPreencheIntervalo = dao.findCodigosProximosTabelaCutter(idPrimeiroCodCutter, idUltimoCodCutter,  retorno.size(), qtdResultadosDesejados);
			
				for (Object[] objects : codigosCutterPreencheIntervalo) {
					retorno.add(new DadosTabelaCutter((Integer)objects[0], inicialSobreNomeAutor, (Integer)objects[1], (String) objects[2], inicialTituloObra));
				}
			}
			
			Collections.sort(retorno);
			return retorno;
			
		}finally{
			if( dao != null ) dao.close();
		}
	}

	
	/**
	 * <p>Método que calcula dentre os palavras cutter pesquisados, qual é aquele que está mais próximo do nome do autor </p>
	 * 
	 * <p> Este método verifica dentre as palavra cutter achadas, as que a primeira letra inicia o nome do autor, depois as
	 * que a primeira e a segunda iniciam o nome no autor, e assim sucessivamente até sobre uma palavras cutter (Situação que achou exatamente a palavra cutter)
	 * . Se não sobrar, do conjunto anterior, a próxima letra se existir, tem que está mais próxima da próxima letra do nome do autor.
	 * </p>
	 *
	 * @param codigoCutterCalculados
	 * @param nomeAutorOriginal
	 * @return
	 */
	public static int calculaCodigoMaisProximo(List<DadosTabelaCutter> codigoCutterCalculados, String nomeAutorOriginal){
	
		String nomeAutorTemp = StringUtils.toAsciiAndUpperCase(nomeAutorOriginal);
		
		List<DadosTabelaCutter>  conjuntoValidoAnterior = new ArrayList<DadosTabelaCutter>();
		List<DadosTabelaCutter>  conjuntoValido = new ArrayList<DadosTabelaCutter>();
		
		int indexAutor = 0;
		
		conjuntoValidoAnterior = codigoCutterCalculados;
		
		do{   // while tiverem palavras que iniciam o nome do autor
		
			indexAutor ++;
			
			conjuntoValido = new ArrayList<DadosTabelaCutter>();
			
			boolean existeConjuntoValido = false;
			
			for (DadosTabelaCutter dadosTabelaRecuperados : conjuntoValidoAnterior) {  // Para cada palavra recuperada
				
				String sobreNomeAutorTemp = StringUtils.toAsciiAndUpperCase(dadosTabelaRecuperados.getSobreNomeAutor());
				
				if(dadosTabelaRecuperados.getSobreNomeAutor().length() > indexAutor
						&& nomeAutorTemp.startsWith(sobreNomeAutorTemp.substring(0, indexAutor) )  ){
					conjuntoValido.add(dadosTabelaRecuperados);
					existeConjuntoValido = true;
				}
				
			}
			
			if(existeConjuntoValido)
				conjuntoValidoAnterior = conjuntoValido;
			
		}while(conjuntoValido.size() > 1 && indexAutor < nomeAutorTemp.length());
			
		if(conjuntoValido.size() == 1){ // Achou exatamenta a palavra mais próxima
			return conjuntoValido.get(0).getIdTabelaCutter();
		}else{
			
			int diferenca = 1000;
			int idTabelaCutterMaisProximo = 0;
			
			indexAutor--; // precisa voltar uma posição;
			
			// Percorre o conjunto de palavaras que inicia o nome e verifica qual tem a próxima letras mais próxima
			for (DadosTabelaCutter dadosTabelaIniciamNomeAutor : conjuntoValidoAnterior) {
				
				String sobreNomeAutorTemp = StringUtils.toAsciiAndUpperCase(dadosTabelaIniciamNomeAutor.getSobreNomeAutor());
				
				if(dadosTabelaIniciamNomeAutor.getSobreNomeAutor().length() > indexAutor ){
					
					
					int diferencaAtual = Math.abs(nomeAutorTemp.charAt(indexAutor) - sobreNomeAutorTemp.charAt(indexAutor));
					
					if(diferencaAtual < diferenca){
						diferenca = diferencaAtual;
						idTabelaCutterMaisProximo  = dadosTabelaIniciamNomeAutor.getIdTabelaCutter();
					}
					
				}
			}
			
			return idTabelaCutterMaisProximo;
			
		}
		
	}
	
	
	
	/**
	 *  Inicializa com 5 campos de dados a tela, para não vim vazia
	 *
	 * @param obj
	 * @throws DAOException 
	 */
	public static void criaCamposDeDadosIniciais(TituloCatalografico obj, boolean catalogacaoBibliografica) throws DAOException {
		incluiCampoDadosLOC(obj, catalogacaoBibliografica);
	
		CampoDados c1 = new CampoDados(new Etiqueta("", (short) -1), obj, -1);
		new SubCampo(c1); // cria um sub campo vazio e adiciona a lista de subcampos
		c1.setDataModelSubCampos(new ListDataModel(c1.getSubCampos()));
		CampoDados c2 = new CampoDados(new Etiqueta("", (short) -1), obj, -1);
		new SubCampo(c2);// cria um sub campo vazio e adiciona a lista de subcampos
		c2.setDataModelSubCampos(new ListDataModel(c2.getSubCampos()));
		CampoDados c3 = new CampoDados(new Etiqueta("", (short) -1), obj, -1);
		new SubCampo(c3);// cria um sub campo vazio e adiciona a lista de subcampos
		c3.setDataModelSubCampos(new ListDataModel(c3.getSubCampos()));
		CampoDados c4 = new CampoDados(new Etiqueta("", (short) -1), obj, -1);
		new SubCampo(c4);// cria um sub campo vazio e adiciona a lista de subcampos
		c4.setDataModelSubCampos(new ListDataModel(c4.getSubCampos()));
		CampoDados c5 = new CampoDados(new Etiqueta("", (short) -1), obj, -1);
		new SubCampo(c5);// cria um sub campo vazio e adiciona a lista de subcampos
		c5.setDataModelSubCampos(new ListDataModel(c5.getSubCampos()));
	}
	
	
	/**
	 * <p>Método que verifica se o título possui o campo de dados passado, preenchido ou não.</p>
	 *  
	 * @param tituloCatalogafico
	 * @param tag
	 * @param subCampo
	 * @param preenchido  verifica se o título contém o campo de dados preenchido
	 * @return
	 */
	public static boolean tituloContemCampoDados(TituloCatalografico tituloCatalogafico, Etiqueta etiqueta, char subCampo, boolean preenchido){
	
		if(tituloCatalogafico.getCamposDados() != null){
			
			for (CampoDados dados : tituloCatalogafico.getCamposDados()) {
				if(dados.getEtiqueta().equals(etiqueta)){
					if(dados.getSubCampos() != null){
						for (SubCampo sub : dados.getSubCampos()) { // se tem o sub campo 'a' com dados ok
							if(sub.getCodigo() != null && sub.getCodigo().equals(subCampo)){
								
								if(preenchido){ // Se o campo deve está preenchido, verifica essa opção para pode dizer que o título o contém
									if(StringUtils.notEmpty( sub.getDado()))
										return true;
								}else{
									return true;  // se está procurando um campo não necessariamente preenchido, então se encontrou já retorna verdadeiro
								}	
							}
						}
					}
				}
			}
		}
		
		return false;
	}



	/**
	 * Adiciona o campo 040$a (Fonte de catalogação bibliográfica) ao título. Se o título já contiver este campo, seta seu valor 
	 * com o valor do parâmetro LOC (Código de identificação Library Of Congress), caso contrário cria e adiciona o campo 040$a com esse valor. 
	 * Caso o parâmetro LOC não tenha sido definido, não adiciona nem modifica o campo 040$a para deste título.
	 * 
	 * @param titulo
	 * @throws DAOException
	 */
	public static void incluiCampoDadosLOC(TituloCatalografico titulo, boolean catalogacaoBibliografica) throws DAOException {
		boolean encontrouCampoLOC = false;
		CampoDados campoDadosLOC = null;
		String valorLOC = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.CODIGO_IDENTIFICACAO_LIBRARY_OF_CONGRESS);
		
		if (StringUtils.isNotEmpty(valorLOC)) { // caso não tenha sido incluído nenhum código no sistema não faz nada.
			EtiquetaDao dao = null;
			
			try {
				dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
				
				Etiqueta etiquetaLOC = null;
				
				if(catalogacaoBibliografica)
					etiquetaLOC = dao.findEtiquetaPorTagETipoAtiva(Etiqueta.FONTE_CATALOGACAO_BIBLIOGRAFICA.getTag(), Etiqueta.FONTE_CATALOGACAO_BIBLIOGRAFICA.getTipo());
				else // de autoridades
					etiquetaLOC = dao.findEtiquetaPorTagETipoAtiva(Etiqueta.FONTE_CATALOGACAO_AUTORIDADES.getTag(), Etiqueta.FONTE_CATALOGACAO_AUTORIDADES.getTipo());
				
				if (titulo.getCamposDados() != null) {
					for (CampoDados campoDados : titulo.getCamposDados()) {
						if (etiquetaLOC.equals(campoDados.getEtiqueta())) {
							encontrouCampoLOC = true;
							campoDadosLOC = campoDados;
							
							break;
						}
					}
				}
				
				if (encontrouCampoLOC && campoDadosLOC != null && campoDadosLOC.getSubCampos() != null) {
					for (SubCampo subCampo : campoDadosLOC.getSubCampos()) {
						if (SubCampo.SUB_CAMPO_A == subCampo.getCodigo()) {
							subCampo.setDado(valorLOC);
							break;
						}
					}
				}
				else {
					campoDadosLOC = new CampoDados(etiquetaLOC, titulo, -1);
					
					new SubCampo(SubCampo.SUB_CAMPO_A, valorLOC, campoDadosLOC, -1);
				}
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	
	}
	
	
	/**
	 *   Método que monta a ajuda de um campo MARC  de dados, bibliográfico
	 * ou de autoridades com base na Etiqueta.
	 *     Essa ajuda montada é um HTML já no formato de ser exibido na página.
	 * 
	 *   <p><strong> Tem que passar a etiqueta com todos os valores e descritores inicializados.</strong></p>
	 */
	public static String montaAjudaCamposDados(Etiqueta e, boolean catalogacaoCompleta){

		if(e == null)
			return new StringBuilder( "<strong><h3 style=\"text-align:center\"> ETIQUETA NÃO CADASTRADA </h3></strong> <br/>").toString();

		StringBuilder retorno = new StringBuilder( "<strong><h3 style=\"text-align:center\">"+
				( catalogacaoCompleta ? e.getTag()+" - " : " " )
				+e.getDescricao()+"("+ (e.isRepetivel() ? "R" : "NR") +")"+" </h3></strong> <br/>");

		retorno.append( StringUtils.notEmpty(e.getInfo())? e.getInfo(): "");

		
		if(catalogacaoCompleta){
			
			retorno.append("<p style=\"margin-top: 10px; margin-bottom: 10px;\"><strong>Indicadores:</strong> </p>");
	
			retorno.append("Primeiro: "+ (StringUtils.notEmpty(e.getDescricaoIndicador1()) ? e.getDescricaoIndicador1() : "")+"<br/>");
			retorno.append("<p style=\"font-style: italic; margin-left: 40px;\">"+(StringUtils.notEmpty(e.getInfoIndicador1()) ? e.getInfoIndicador1() : "")+"</p>");
	
			List<ValorIndicador> valoresPrimerioIndicador  = new ArrayList<ValorIndicador>();
			
			for (ValorIndicador valor1 : e.getValoresIndicador()) {
				if(valor1.getNumeroIndicador() == ValorIndicador.PRIMEIRO)
					valoresPrimerioIndicador.add(valor1);
			}
			
			Collections.sort(valoresPrimerioIndicador, new ValorIndicadorByValorComparator());
			
			for (ValorIndicador valorIndicador : valoresPrimerioIndicador) {
				retorno.append("<p style=\" margin-left: 20px;\"> <strong>"+valorIndicador.getValor()+" - "+valorIndicador.getDescricao()+"  </strong> </p>");
				retorno.append("<p style=\"font-style: italic; margin-left: 40px;\">"+(StringUtils.notEmpty(valorIndicador.getInfo()) ? valorIndicador.getInfo() : "") +"</p>");
			}
	
			retorno.append("Segundo: "+(StringUtils.notEmpty(e.getDescricaoIndicador2()) ? e.getDescricaoIndicador2() : "")+"<br/>");
			retorno.append("<p style=\"font-style: italic; margin-left: 40px;\">"+(StringUtils.notEmpty(e.getInfoIndicador2()) ? e.getInfoIndicador2() : "")+"</p>");
	
			List<ValorIndicador> valoresSegundoIndicador  = new ArrayList<ValorIndicador>();
			
			for (ValorIndicador valor2 : e.getValoresIndicador()) {
				if(valor2.getNumeroIndicador() == ValorIndicador.SEGUNDO)
					valoresSegundoIndicador.add(valor2);
			}
			
			Collections.sort(valoresSegundoIndicador, new ValorIndicadorByValorComparator());
			
			for (ValorIndicador valorIndicador : valoresSegundoIndicador) {
				retorno.append("<p style=\"margin-left: 20px;\"> <strong> "+valorIndicador.getValor()+" - "+valorIndicador.getDescricao()+" </strong> </p>");
				retorno.append("<p style=\"font-style: italic; margin-left: 40px;\">"+(StringUtils.notEmpty(valorIndicador.getInfo()) ? valorIndicador.getInfo() : "") +"</p>");
			}

		} // if(catalogacaoCompleta)

		retorno.append("<p style=\"margin-top: 10px; margin-bottom: 10px;\"><strong>Sub Campos:</strong> </p>");
	
		List<DescritorSubCampo> descritores = new ArrayList<DescritorSubCampo>();
		
		if( e.getDescritorSubCampo() != null)
			descritores =  new ArrayList<DescritorSubCampo>( e.getDescritorSubCampo());
		
		Collections.sort(descritores, new DescritorSubCampoByCodigoComparator());

		for (DescritorSubCampo descritorSubCampo : descritores) {

			if(catalogacaoCompleta)
				retorno.append("<p> <strong> $"+ descritorSubCampo.getCodigo()+" - "+descritorSubCampo.getDescricao()+"("+ (descritorSubCampo.isRepetivel() ? "R" : "NR") +") </strong> </p>");
			else
				retorno.append("<p> <strong> "+descritorSubCampo.getDescricao()+"("+ (descritorSubCampo.isRepetivel() ? "R" : "NR") +") </strong> </p>");
				
			retorno.append("<p style=\"font-style: italic; margin-left: 20px; \">"+( StringUtils.notEmpty(descritorSubCampo.getInfo()) ? descritorSubCampo.getInfo() : "")+"</p>");

			List<ValorDescritorSubCampo> valores =  descritorSubCampo.getValoresDescritorSubCampoList();

			Collections.sort(valores, new ValorDescritorSubCampoByValorComparator());

			if(valores.size() > 0)
				retorno.append("<p style=\"margin-left: 50px; \"> Valores: </p>");

			for (ValorDescritorSubCampo valorDescritorSubCampo : valores) {
				if(catalogacaoCompleta)
					retorno.append("<p style=\"margin-left: 70px; \"> <strong> $"+valorDescritorSubCampo.getValor()+" - "+valorDescritorSubCampo.getDescricao()+" </strong> </p>");
				else
					retorno.append("<p style=\"margin-left: 70px; \"> <strong> "+valorDescritorSubCampo.getDescricao()+" </strong> </p>");
					
				retorno.append("<p style=\"font-style: italic; margin-left: 90px;\">"+  ( StringUtils.notEmpty(valorDescritorSubCampo.getInfo()) ? valorDescritorSubCampo.getInfo() : "") +"</p>");
			}

		}

		retorno.append("<br/>");

		return retorno.toString();
	}
	
	
	
	/**
	 * Carrega os dados de uma planilha de catalogação em um Título temporário para visualização por parte do usuário.
	 *
	 * @void
	 */
	public static TituloCatalografico carregaCamposPlanilhaCatalogacaoSimplificada(int idPlanilhaEscolhida, boolean planilhaBibliografica, List<Etiqueta> etiquetasBuscadas) throws DAOException{
		
		TituloCatalografico tituloTemp = null;
		
		GenericDAO dao = null;
		
		try{
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
		
			if(idPlanilhaEscolhida != -1){
				
				// a planilha escolhida //
				PlanilhaCatalogacao planilha = dao.findByPrimaryKey(idPlanilhaEscolhida, PlanilhaCatalogacao.class);
				
				if(planilhaBibliografica){
					// pego o formato do Título que vem da planilha //
					FormatoMaterial formatoMaterial =  dao.findByPrimaryKey(planilha.getIdFormato(), FormatoMaterial.class, "id", "sigla", "descricao");
					tituloTemp = new TituloCatalografico(formatoMaterial);
				}else{
					tituloTemp = new TituloCatalografico(); // Autoridades não tem formato do material
				}
				
					
				CatalogacaoUtil.criaCamposDeControle(planilha, tituloTemp, etiquetasBuscadas);
				CatalogacaoUtil.criaCamposDeDados(planilha, tituloTemp, etiquetasBuscadas);
				
				if(tituloTemp.getCamposControle() != null)
					Collections.sort(tituloTemp.getCamposControle(), new CampoControleByEtiquetaComparator());
				
				if(tituloTemp.getCamposDados() != null)
					Collections.sort(tituloTemp.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
					
			}
			
			return tituloTemp;
		
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	///////////////// Os métodos que crian os campos de dados para a catalogação simplificada ///////////// 
	
	
	/**
	 * <p>Monta os campos de dados que seram usados na catalogação simplificada.</p>
	 * 
	 * <p>Na catalogação simplificada só serão permitidos de serem adicionados novos campo se eles estiverem na planilha escolhida. 
	 * Para sabemos os valores de seus indicadores, códigos dos campos e sub campos.</p>
	 * 
	 * <p>Além disso os campos de controle e esses códigos dos campos de dados serão ocultados, mostrando apenas a descriação de cada um 
	 * e os dados da catalogação digitados pelo usuário.</p>
	 * 
	 * @throws DAOException 
	 *
	 * @List<CampoDados>
	 */
	public static List<CampoDados> montaCamposDadosCalalogacaoSimplificada(PlanilhaCatalogacao planilhaEscolhida, TituloCatalografico obj, List<Etiqueta> etiquetasBuscadas, boolean editando) throws DAOException{
		
		ArrayList<CampoDados> temp = new ArrayList<CampoDados>();
		
		EtiquetaDao dao = null;
		
		try {
			
			dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class);
			
			removeCamposDeOutraPlanilha(planilhaEscolhida, obj);
			
			adicionaInformacoesPresentesCatalogacao(planilhaEscolhida, obj, etiquetasBuscadas);
			
			adicionaCamposControlePresentesPanilha(planilhaEscolhida, obj, etiquetasBuscadas, dao);
			
			/* Adiciona os campos de dados presentes na panilha escolhida mas não na catalogação
			 * Apenas se se está criando uma nova catalogação, se está editando mostra apenas os campos que
			 * a catalogação já possui */
			if(! editando)
				adicionaCamposDadosPresentesPanilha(planilhaEscolhida, obj, etiquetasBuscadas, dao);
			
			configuraGruposEtiquetaVisiveis(obj);
				
		
		} finally {
			if (dao != null)dao.close();
		}
		
		return temp;
	}


	/**
	 * <p>Verifica quais dos grupos serão visualizados. Apenas o primeiro de cada tipo de ser atualizado.</p>
	 *
	 * <p>É uma forma de mostrar os campos de dados agrupados pelo tipo da etiqueta na tela para o usuário.</p>
	 *
	 * @void
	 */
	public static void configuraGruposEtiquetaVisiveis(TituloCatalografico obj) {
		
		CatalogacaoUtil.ordenaCampoDados(obj);
		
		GrupoEtiqueta grupoAnterior = null;
		
		for (CampoDados campoDados : obj.getCamposDadosNaoReservados()) {
			
			if(campoDados.getEtiqueta().getGrupo() == null)
				continue;
			
			// Lógica para mogtrar para o usuário apenas o grupo para o primeiro campo do grupo //
			if(grupoAnterior == null ||  ! campoDados.getEtiqueta().getGrupo().equals(grupoAnterior)   ){ 
				grupoAnterior = campoDados.getEtiqueta().getGrupo();
				campoDados.setGrupoEtiquetaVisivel(true);
			}else{
				campoDados.setGrupoEtiquetaVisivel(false);
			}
			
		}
	}


	/**
	 * Adiciona os campos de dados presentes na planilha mas não na catalogação.
	 *
	 * @void
	 */
	private static void adicionaCamposDadosPresentesPanilha(PlanilhaCatalogacao planilhaEscolhida, TituloCatalografico obj,
			List<Etiqueta> etiquetasBuscadas, EtiquetaDao dao)throws DAOException {
		
		for (ObjetoPlanilhaCatalogacaoDados objetoDado : planilhaEscolhida.getObjetosDadosPlanilhaOrdenadosByTag()) {
			Etiqueta e = new Etiqueta(objetoDado.getTagEtiqueta(), planilhaEscolhida.getTipo() );
			e = CatalogacaoUtil.recuperaEtiquetaTitulo(e, etiquetasBuscadas); 
			if(e == null)
				e =  dao.findEtiquetaPorTagETipoAtiva(objetoDado.getTagEtiqueta(), planilhaEscolhida.getTipo());
			
			
			boolean possuaCampoDados  = obj.contemCampoDados(e.getTag());

			// se não contém o campo de dados da planilha, cria e finaliza
			if ( ! possuaCampoDados ){ 
				
				// Cria o campo de dados  e já adiciona ao título
				CampoDados c = new CampoDados(e, obj, -1);
				// Não esquecer dos indicadores
				c.setIndicador1(objetoDado.getIndicador1());
				c.setIndicador2(objetoDado.getIndicador2());
				
				c.getEtiqueta().setGrupo(  retornaGrupoEtiqueta(e)  );
				
				c.setIdPlanilhaGerado(planilhaEscolhida.getId());
				
				// Cria os sub campos desse campo
				criaSubCampos(c, objetoDado);
				
				for (SubCampo sub : c.getSubCampos()) {
					sub.setDescricaoSubCampo(retornaDescritorSubCampo(e, sub.getCodigo()));
				}
				
			}else{ 
				// se já tem fica com os subcampos já existentes, não há como saber em qual campo adicionar 
				// porque tanto o Título quanto a planilha podem ter campos repetidos com a mesma tag
			}
		}
	}


	/**
	 * Adiciona os campos de controle existentes na planilha, mas não na catalogação
	 *
	 * @void
	 */
	private static void adicionaCamposControlePresentesPanilha(PlanilhaCatalogacao planilhaEscolhida, TituloCatalografico obj,
				List<Etiqueta> etiquetasBuscadas, EtiquetaDao dao) throws DAOException {
		
		if(planilhaEscolhida.getObjetosPlanilhaCatalogacaoControle() != null){
		
			for (ObjetoPlanilhaCatalogacaoControle objetoControle : planilhaEscolhida.getObjetosControlePlanilhaOrdenadosByTag()) {
				
				// pega a etiqueta, era para existir um por tag;
				Etiqueta e = new Etiqueta(objetoControle.getTagEtiqueta(), planilhaEscolhida.getTipo() );
				e = CatalogacaoUtil.recuperaEtiquetaTitulo(e, etiquetasBuscadas); 
				if(e == null)
					e =  dao.findEtiquetaPorTagETipoAtiva(objetoControle.getTagEtiqueta(), planilhaEscolhida.getTipo());
				
				if(e != null) {	
					boolean possuaCampoControle =  obj.contemCampoControle(e.getTag());
					
					// se não contém o campo de controle presente na planilha ainda, cria ! 
					if (! possuaCampoControle ){ 
						CampoControle c = new CampoControle(objetoControle.getDado(), e  , -1, obj );
						c.setIdPlanilhaGerado(planilhaEscolhida.getId());
					}
					
				}
			}
		}
	}


	/** Para cada compo de dados editável já existente, prepara ele para ser mostrado na tela da catalogação simplificada:
	 * 
	 * Verifica ao qual grupo de etiqueta ele pertence, porque na catalogação simplificada os campos são ordenados por grupo.
	 * 
	 * E atribuir a descrição do sub campo porque na catalogação simplificada é mostrado apenas a descriação para o usuário.   
	 */
	private static void adicionaInformacoesPresentesCatalogacao(PlanilhaCatalogacao planilhaEscolhida, TituloCatalografico obj, List<Etiqueta> etiquetasBuscadas) throws DAOException {
		
		Iterator<CampoDados> iterator = obj.getCamposDados().iterator();
		while ( iterator.hasNext()) {
			CampoDados campoDados = iterator.next();
			
			Etiqueta e = recuperaEtiquetaTitulo(campoDados.getEtiqueta(), etiquetasBuscadas);
			
			// remove etiqueta vazia, na catalogação simplificada todas os campos tem que ter etiqueta para poder agrupar.
			if(e == null)
				iterator.remove();
			else{
			
				campoDados.getEtiqueta().setGrupo(  retornaGrupoEtiqueta(e)  );
			
				for (SubCampo subcampo : campoDados.getSubCampos()) {
					subcampo.setDescricaoSubCampo( retornaDescritorSubCampo(e, subcampo.getCodigo()) );
				}
			}
		}
	}


	
	/**
	 * <p> Quando é usado a catalogação simplificada o sistema adiciona todos os campos presentes na 
	 * planilha escolhida pelo usuário, caso o usuário escolha outra planilha os campos adicionados pela planilha anterior devem ser removidos.</p>
	 */
	private static void removeCamposDeOutraPlanilha(PlanilhaCatalogacao planilhaEscolhida, TituloCatalografico obj) {
		
		Iterator<CampoControle> iteratorControle = obj.getCamposControle().iterator();
		while ( iteratorControle.hasNext()) {
			CampoControle campoControle = iteratorControle.next();
			
			if(campoControle.getIdPlanilhaGerado() > 0 && campoControle.getIdPlanilhaGerado() != planilhaEscolhida.getId())
				iteratorControle.remove();
			
		}
		
		Iterator<CampoDados> iteratorDados = obj.getCamposDados().iterator();
		while ( iteratorDados.hasNext()) {
			CampoDados campoDados = iteratorDados.next();
			
			if(campoDados.getIdPlanilhaGerado() > 0 && campoDados.getIdPlanilhaGerado() != planilhaEscolhida.getId())
				iteratorDados.remove();
			else{
				Iterator<SubCampo> iteratorSubCampos = campoDados.getSubCampos().iterator();
				
				SubCampo subCampo = iteratorSubCampos.next();
				
				if(subCampo.getIdPlanilhaGerado() > 0 && subCampo.getIdPlanilhaGerado() != planilhaEscolhida.getId())
					iteratorSubCampos.remove();
			}
		}
	}


	
	/** 
	 *  Retorna o grupo da etiqueta passada. retorna uma novo objeto, cada etiqueta tem
	 *  que ter o seu objeto porque a informação se o grupo é visível ou não para o usuário 
	 *  específica de cada etiqueta 
	 */
	public static GrupoEtiqueta retornaGrupoEtiqueta(Etiqueta e){
		try {
			
			if(e.isEtiquetaBibliografica()){
				for (GrupoEtiqueta grupo : GrupoEtiqueta.GRUPOS_ETIQUETAS_BIBLIOGRAFICOS) {
					if(grupo.isGrupoEtiqueta(e.getTag(), e.getTipo())){
						
						return (GrupoEtiqueta) grupo.clone();
					}
				}
			}
			
			if(e.isEtiquetaAutoridades()){
				for (GrupoEtiqueta grupo : GrupoEtiqueta.GRUPOS_ETIQUETAS_AUTORIDADES) {
					if(grupo.isGrupoEtiqueta(e.getTag(), e.getTipo())){
						
						return (GrupoEtiqueta) grupo.clone();
					}	
				}
			}
		
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		
		return null;
		
	}
		
		
	/** 
	 * retorna a descrição do subcampo a partir da etiqueta do codigo de subcampo passado
	 * @return  retorna o descritor referente ao sub campo passado
	 * @throws DAOException 
	 */
	public static String retornaDescritorSubCampo(Etiqueta e, Character codigoSubCampo) throws DAOException {
		
		if(e.getDescritorSubCampo() == null)
			return "";
		
		try{
			for (@SuppressWarnings("unused") DescritorSubCampo descritor : e.getDescritorSubCampo()) {
				break;
			}
		}catch(LazyInitializationException lie){
			e.setDescritorSubCampo(retornaDescritoresEtiqueta(e)); // se der erro de inicialização, busca do banco.
		}
		
		for (DescritorSubCampo descritor : e.getDescritorSubCampo()) {
			if(descritor.getCodigo().equals(codigoSubCampo))
				return descritor.getDescricao();
		}
		
		return "";
	}
		
	/**
	 * Recupera os descritores da etiqueta do banco.
	 *
	 * @Set<DescritorSubCampo>
	 */
	private static Set<DescritorSubCampo> retornaDescritoresEtiqueta(Etiqueta e) throws DAOException{
		GenericDAO dao = null;
		try{
			
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			return (Set<DescritorSubCampo>) dao.findByExactField(DescritorSubCampo.class, "etiqueta.id", e.getId());
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	/**
	 * <p>Valida o formato do arquivo que pode ser submetido junto com a catalogação</p>
	 * 
	 * @param arquivo
	 * @throws NegocioException
	 *  
	 * <p> Criado em:  16/05/2013  </p>
	 *
	 */
	public static void validaFormatoArquivoCatalogacao(UploadedFile arquivo)throws NegocioException{
		if( arquivo.getName().toLowerCase().endsWith(".pdf") && ( arquivo.getContentType().equals("application/pdf") || arquivo.getContentType().equals("application/x-download")  ) ){
			// OK
		}else{
			throw new NegocioException("O arquivo digitalizado precisa ser do formato PDF.");
		}
		
		if(arquivo != null && arquivo.getName().length() > 100){
			throw new NegocioException("O tamanho máximo permito para o nome de arquivo é de 100 caracteres.");
		}
	}
	
	
	
	
	
	
	
}