/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/08/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.Order;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MobilidadeEstudantilDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MobilidadeEstudantil;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.ensino.internacionalizacao.dao.TraducaoElementoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ConstanteTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.IdiomasEnum;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducaoElementos;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.TraducaoElemento;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe auxiliar utilizada na internacionalização de documentos oficiais.
 * 
 * @author Rafael Gomes
 *
 */
public class InternacionalizacaoHelper {

	
	/**
	 * Realiza uma analise por extenso das equivalências e remove dos componentes analisados quando encontra uma equivalência
	 * 
	 * @param idDiscente
	 * @param disciplinas
	 * @param equivalenciasDiscente
	 * @param componentesParaAnalise
	 * @param idioma TODO
	 * @throws DAOException
	 */
	public static void analisarEquivalenciasPorExtenso(int idDiscente, List<MatriculaComponente> disciplinas, List<TipoGenerico> equivalenciasDiscente, String idioma, Set<MensagemAviso> errosTraducao) throws DAOException {
		
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, null);
		ComponenteCurricularDao ccdao = getDAO(ComponenteCurricularDao.class, null);
		TraducaoElementoDao teDao = getDAO(TraducaoElementoDao.class, null);
		
		try {
			// Coleção de componentes obrigatórios que o discente não pagou.
			Collection<ComponenteCurricular> componentesParaAnalise = discenteDao.findComponentesDaGradeQueDiscenteNaoPagou(idDiscente, disciplinas, true);
			// conjunto de componentes que serão usados na busca por equivalência
			List<Integer> idsDisciplinas = new ArrayList<Integer>();
			// conjunto de componentes que se encontrão com status de matriculado
			List<Integer> idsMatriculadas = new ArrayList<Integer>();
			
			Map<Integer, List<ItemTraducaoElementos>> mapaComponente = componentesTraducao(null, componentesParaAnalise);
			
			for (MatriculaComponente mc : disciplinas) {
				
				if (mc.getSituacaoMatricula().equals(SituacaoMatricula.MATRICULADO)) 
					idsMatriculadas.add(mc.getComponente().getId());
				
				if (mc.getTipoIntegralizacao() != null && mc.getTipoIntegralizacao().equals(TipoIntegralizacao.OBRIGATORIA))
					continue;
				
				if (mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVADO)
						|| mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_TRANSFERIDO)
						|| mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_CUMPRIU)
						|| mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_DISPENSADO)) {
					idsDisciplinas.add(mc.getComponente().getId());
				} 
				
			}
			
			MensagemAviso erro = new MensagemAviso();
			
			Map<Integer, List<Object[]>> mapaEquivalencias = ccdao.findEquivalenciasComponentesByDiscente(componentesParaAnalise, new Discente(idDiscente));
			Collection<ConstanteTraducao> listConstComponente = teDao.findConstanteByEntidadeIdioma(idioma, ComponenteCurricular.class.getName());
			String constCumpriu = null;
			String constAtraves = null;
			for (ConstanteTraducao constTraducao : listConstComponente) {
				constCumpriu = constTraducao.getConstante().equals("CUMPRIU") ? constTraducao.getValor() : constCumpriu;
				constAtraves = constTraducao.getConstante().equals("ATRAVES") ? constTraducao.getValor() : constAtraves;
			}
			for (Iterator<ComponenteCurricular> it = componentesParaAnalise.iterator(); it.hasNext(); ) {
				ComponenteCurricular cc = it.next();
				List<Object[]> equivalencias = mapaEquivalencias.get(cc.getId());
				
				if (!isEmpty(equivalencias)) {
					for (Object[] infoEquivalencia : equivalencias) {
						String equivalencia = (String) infoEquivalencia[0];
						
						ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(equivalencia);
						if (arvore != null && arvore.eval(idsDisciplinas)) {
							boolean encontrouEquivalencia = false;
							
							
							StringBuilder sb = new StringBuilder();
							String textoComponente = constCumpriu+ " " + cc + " " + "(" + cc.getChTotal() + "h) " + constAtraves + " ";
							if (idioma != null && !idioma.equals(IdiomasEnum.PORTUGUES.getId()))
	
								if (cc.getCodigo() != null && cc.getId() > 0 && mapaComponente.get(cc.getId()) != null){
								List<ItemTraducaoElementos> traducaoElementos = mapaComponente.get(cc.getId());
								for (ItemTraducaoElementos ite : traducaoElementos) {
									for (TraducaoElemento te : ite.getElementos()) {
										if (te.getIdioma().equals(idioma)){
											if (te.getValor() != null)
												ReflectionUtils.setProperty(cc, ite.getItemTraducao().getAtributo(), te.getValor());
											else if (ReflectionUtils.evalPropertyObj(cc, ite.getItemTraducao().getAtributo()) != null){
												erro = new MensagemAviso("Não foi possível localizar tradução para o Componente Curricular: " 
															+ cc.getDescricao(), TipoMensagemUFRN.ERROR );
												errosTraducao.add(erro);
											}
										}
									}	
								}
								textoComponente = constCumpriu+ " " + cc + " " + "(" + cc.getChTotal() + "h) " + constAtraves + " ";
							}	
								
							sb.append(textoComponente);
							
							Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, cc, idsDisciplinas);
							for (Iterator<ComponenteCurricular> itMatch = equivalentes.iterator(); itMatch.hasNext();) {
								ComponenteCurricular eq = discenteDao.findComponenteNomeChById(itMatch.next().getId());
								MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(disciplinas, eq, SituacaoMatricula.getSituacoesPagas());
								
								if (TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(mat.getTipoIntegralizacao()) 
										|| TipoIntegralizacao.EQUIVALENTE_OPTATIVA_CURSO.equals(mat.getTipoIntegralizacao())
										|| TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE.equals(mat.getTipoIntegralizacao())) {
	
									encontrouEquivalencia = true;
									
									TraducaoElemento te = teDao.findByAtributoAndElementoIdioma(eq.getClass().getName(), "nome", eq.getId(), idioma);
									if (te != null && te.getValor() != null)
										ReflectionUtils.setProperty(eq, "nome", te.getValor());
									else if (ReflectionUtils.evalPropertyObj(cc, "nome") != null){
										erro = new MensagemAviso("Não foi possível localizar tradução para o Componente Curricular: " 
													+ eq.getDescricao(), TipoMensagemUFRN.ERROR );
										errosTraducao.add(erro);
									}
									
									sb.append(eq + " (" + eq.getChTotal() + "h)");
									if (itMatch.hasNext()) {
										sb.append(", ");
									}
									
								}
							}
							
							if (encontrouEquivalencia) {
								equivalenciasDiscente.add(new TipoGenerico(sb.toString()));
								it.remove();
							}
							
							break;
						} else if (arvore != null && arvore.eval(idsMatriculadas)) {
							cc.setMatriculadoEmEquivalente(true);
							break;
						}
					}
				}
			}
		} finally {
			discenteDao.close();
			ccdao.close();
			teDao.close();
		}
	}
	
	/**
	 * Método utilizado para preparar e carregar as propriedades da entidade Componente Curricular 
	 * passada por parâmetros para serem traduzidos. 
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param listComponentes
	 * @throws DAOException
	 */
	public static Map<Integer, List<ItemTraducaoElementos>> componentesTraducao(Collection<MatriculaComponente> matriculas, Collection<ComponenteCurricular> disciplinas) throws DAOException{
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class, null);
		
		try {
			List<ComponenteCurricular> listComponentesHistorico = new ArrayList<ComponenteCurricular>();
			
			if (matriculas != null) {
				for (MatriculaComponente mc : matriculas) 
					listComponentesHistorico.add(mc.getComponente());
			}
			if (disciplinas != null) {
				listComponentesHistorico.addAll(disciplinas);
			}
				
			//TODO Remover componentes de ENADE.
			
			List<ItemTraducaoElementos> listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
			List<TraducaoElemento> listElementoFinal = new ArrayList<TraducaoElemento>();
			
			Map<Integer, List<ItemTraducaoElementos>> mapComponenteAtributosTraducao = 
					new HashMap<Integer, List<ItemTraducaoElementos>>();
			
			Collection<ItemTraducao> atributosTraducao = dao.findItensByClasse(ComponenteCurricular.class.getName(), Order.asc("nome"));
			mapComponenteAtributosTraducao = dao.findComponentesTraducao(listComponentesHistorico);
	
			Map<Integer, List<ItemTraducaoElementos>> mapaComponenteId = new HashMap<Integer, List<ItemTraducaoElementos>>();
			
			for (ComponenteCurricular cc : listComponentesHistorico) {
				
				List<ItemTraducaoElementos> lista = mapComponenteAtributosTraducao.get(cc.getId());
				
				for (ItemTraducao itemTraducao : atributosTraducao) {
					
					ItemTraducaoElementos itElementos = new ItemTraducaoElementos(itemTraducao, new ArrayList<TraducaoElemento>());
					if (lista != null){
						for (ItemTraducaoElementos ite : lista) {
							if (ite.getItemTraducao().getId() == itemTraducao.getId())
								itElementos = ite;
						}
					}	
					
					if (itElementos.getElementos() != null && itElementos.getElementos().isEmpty()){
						for (String str : IdiomasEnum.getAll()) {
							TraducaoElemento elemento = new TraducaoElemento(itemTraducao, str, cc.getId());
							elemento.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(str));
							if (IdiomasEnum.PORTUGUES.getId().equalsIgnoreCase(str))
								elemento.setValor((String) ReflectionUtils.evalPropertyObj(cc, itemTraducao.getAtributo()));
							listElementoFinal.add(elemento);
						}
					} else {
						boolean insereElemento = true;
						for (String str : IdiomasEnum.getAll()) {
							insereElemento = true;
							for (TraducaoElemento te : itElementos.getElementos()) {
								if (te.getIdioma().equals(str)){
									te.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(str));
									listElementoFinal.add(te);
									insereElemento = false;
									break;
								}
							}
							if (insereElemento){
								listElementoFinal.add(new TraducaoElemento(itemTraducao, str, cc.getId(), IdiomasEnum.getDescricaoIdiomas().get(str)));
							}
						}
					}
					listaTraducaoElemento.add(new ItemTraducaoElementos(itemTraducao, listElementoFinal));
					listElementoFinal = new ArrayList<TraducaoElemento>();
					
				}
				mapaComponenteId.put(cc.getId(), listaTraducaoElemento);
				listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
			}
			
			return mapaComponenteId;
		} finally {
			dao.close();
		}
	}
	
	/** 
	 * Insere a tradução da mobilidade estudantil no histórico do discente.
	 * @param discente
	 * @param mov
	 * @throws DAOException
	 */
	public static List<TipoGenerico> mobilidadeEstudantil(DiscenteAdapter discente, String idioma, Set<MensagemAviso> errosTraducao) throws DAOException {
		MobilidadeEstudantilDao mobilidadeDao = getDAO(MobilidadeEstudantilDao.class, null);
		TraducaoElementoDao teDao = getDAO(TraducaoElementoDao.class, null);
		
		
		try {
			Map<String, ConstanteTraducao> map = new HashMap<String, ConstanteTraducao>();
			popularTraducaoConstantes(MobilidadeEstudantil.class.getName(), idioma, map, errosTraducao);
			
			List<TipoGenerico> mobilidadeEstudantil = new ArrayList<TipoGenerico>();
			List<MobilidadeEstudantil> listaMobilidade = mobilidadeDao.findByDiscente(discente,true);
			
			
			if (idioma != null && !idioma.equals(IdiomasEnum.PORTUGUES.getId())){
				for (MobilidadeEstudantil mobilidade : listaMobilidade){
					TraducaoElemento tradEle = teDao.findByAtributoAndElementoIdioma(MobilidadeEstudantil.class.getName(), "paisExterna", mobilidade.getId(), idioma);
					 
					String paisExt = null;
					if (tradEle != null)
						paisExt = tradEle.getValor();
					
					if (ValidatorUtil.isNotEmpty(paisExt))
						ReflectionUtils.setProperty(mobilidade.getPaisExterna(), "nome", paisExt);
					else if (ReflectionUtils.evalPropertyObj(mobilidade, "observacao") != null){
						MensagemAviso erro = new MensagemAviso("Não foi possível localizar tradução para o País da Mobilidade Estudantil do discente: " 
								+ discente, TipoMensagemUFRN.ERROR );
						errosTraducao.add(erro);
					}
					StringBuilder sb = new StringBuilder();
					sb.append( map.get("TEXT_PART_01").getValor() + getDescricaoTipo(mobilidade.getTipo())+
							", "+getDescricaoSubTipo(mobilidade.getTipo(), mobilidade.getSubtipo())+ ", " +(mobilidade.isInterna() ? map.get("TEXT_PART_02").getValor() +
									mobilidade.getCampusDestino().getNome() : map.get("TEXT_PART_03").getValor() + mobilidade.getIesExterna()+
									(mobilidade.getSubtipo() == MobilidadeEstudantil.INTERNACIONAL?", " + map.get("TEXT_PART_04").getValor() +mobilidade.getPaisExterna().getNome()+ map.get("TEXT_PART_05").getValor() + mobilidade.getCidade():""))+
							", " + map.get("TEXT_PART_06").getValor() + mobilidade.getAno()+"."+mobilidade.getPeriodo()+" "); 
					
					if (mobilidade.getNumeroPeriodos() > 1){
						sb.append(map.get("TEXT_PART_07").getValor());
						StringBuilder periodoFinal = new StringBuilder();
						periodoFinal.append(DiscenteHelper.somaSemestres(mobilidade.getAno(), mobilidade.getPeriodo(), mobilidade.getNumeroPeriodos() - 1));
						periodoFinal.insert(4, ".");
						sb.append(periodoFinal.toString());
					}
					sb.append(".");
					
					TraducaoElemento te = teDao.findByAtributoAndElementoIdioma(MobilidadeEstudantil.class.getName(), "observacao", mobilidade.getId(), idioma);
					if (te != null && te.getValor() != null)
						ReflectionUtils.setProperty(mobilidade, "observacao", te.getValor());
					else if (ReflectionUtils.evalPropertyObj(mobilidade, "observacao") != null){
						MensagemAviso erro = new MensagemAviso("Não foi possível localizar tradução para a observação da Mobilidade Estudantil do discente: " 
									+ discente, TipoMensagemUFRN.ERROR );
						errosTraducao.add(erro);
					}
					
					sb.append("\r" + map.get("TEXT_PART_08").getValor() +mobilidade.getObservacao());
					mobilidadeEstudantil.add(new TipoGenerico(sb.toString()));
				}
			}	
			return mobilidadeEstudantil;
		} finally {
			mobilidadeDao.close();
			teDao.close();
		}
	}
	
	/**
	 * Método responsável por popular uma mapa com os valores de tradução de determinadas constantes por entidade.
	 * @param entidade
	 * @param idioma
	 * @param map
	 * @throws DAOException 
	 */
	public static void popularTraducaoConstantes( String classe, String idioma, Map<String, ConstanteTraducao> map, Set<MensagemAviso> errosTraducao ) throws DAOException, ConfiguracaoAmbienteException{
		TraducaoElementoDao teDao = getDAO(TraducaoElementoDao.class, null);
		Collection<ConstanteTraducao> list = teDao.findConstanteByEntidadeIdioma(idioma, classe);
		
		for (ConstanteTraducao ct : list) {
			map.put(ct.getConstante(), ct);
		}
		try {
			descricoesTipos = new HashMap<Integer, String>();
			descricoesTipos.put(MobilidadeEstudantil.INTERNA, map.get("INTERNA").getValor());
			descricoesTipos.put(MobilidadeEstudantil.EXTERNA, map.get("EXTERNA").getValor());
			
			descricoesSubTiposInterna = new HashMap<Integer, String>();
			descricoesSubTiposInterna.put(MobilidadeEstudantil.COMPULSORIA, map.get("COMPULSORIA").getValor());
			descricoesSubTiposInterna.put(MobilidadeEstudantil.VOLUNTARIA, map.get("VOLUNTARIA").getValor());
			
			descricoesSubTiposExterna = new HashMap<Integer, String>();
			descricoesSubTiposExterna.put(MobilidadeEstudantil.NACIONAL, map.get("NACIONAL").getValor());
			descricoesSubTiposExterna.put(MobilidadeEstudantil.INTERNACIONAL, map.get("INTERNACIONAL").getValor());
		} catch ( Exception e) {
			MensagemAviso erro = new MensagemAviso("Ocorreu um erro ao carregar as Constantes de Mobilidade Estudantil para o idioma "
					+ IdiomasEnum.getDescricaoIdiomas().get(idioma)+"."
					, TipoMensagemUFRN.ERROR );
			errosTraducao.add(erro);
		}	
	}
	
	/** Descrição dos Tipos  */
	private static Map<Integer, String> descricoesTipos;
	
	/** Descrição dos Sub-Tipos Interna  */
	private static Map<Integer, String> descricoesSubTiposInterna;
	
	/** Descrição dos Sub-Tipos Externa  */
	private static Map<Integer, String> descricoesSubTiposExterna;
	
	/**
	 * Método responsável por retornar a descrição dos tipos de mobilidade estudantil.
	 * @param tipoMobilidade
	 * @return
	 */
	public static String getDescricaoTipo(int tipoMobilidade) {
		return descricoesTipos.get(tipoMobilidade);
	}	
	
	/**
	 * Método responsável por retornar a descrição do subtipo de mobilidade estudantil.
	 * @param tipoMobilidade
	 * @param subtipoMobilidade
	 * @return
	 */
	public static String getDescricaoSubTipo(int tipoMobilidade, int subtipoMobilidade) {		
		if (tipoMobilidade == MobilidadeEstudantil.INTERNA)
			return descricoesSubTiposInterna.get(subtipoMobilidade);
		else if (tipoMobilidade == MobilidadeEstudantil.EXTERNA)			
			return descricoesSubTiposExterna.get(subtipoMobilidade);
		else
			return "NÃO INFORMADO";
	}
	
	/**
	 * Retorna o mês de uma data em seu formado de descrição abreviado
	 * 
	 * @param d
	 * @return
	 */
	public static String getMesAbreviadoLocale(int mes, String idioma){
		
		Calendar cal = Calendar.getInstance();
		mes--;
		cal.set(Calendar.MONTH, mes);
		cal.set(Calendar.DATE, 1);
		
		if (cal.getTime() != null) {
			// MMM é o formato para o nome do mês, abreviado.  
			DateFormat df = new SimpleDateFormat("MMM", new Locale(idioma));
			System.out.println(df.format(cal.getTime()).toUpperCase());
			return df.format(cal.getTime()).toUpperCase();
		}
	
		return null;
	}	
	
	/**
	 * Retorna uma instância do DAO cuja classe foi passada como parâmetro.
	 * @param dao
	 * @param mov
	 * @return 
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao, Movimento mov) throws DAOException {
		if (mov != null)
			return DAOFactory.getInstance().getDAOMov(dao, mov);
		else 
			return DAOFactory.getInstance().getDAO(dao);
	}
}
