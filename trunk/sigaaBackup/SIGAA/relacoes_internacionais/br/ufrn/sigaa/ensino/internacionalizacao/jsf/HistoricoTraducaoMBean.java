/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 23/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.JoinColumn;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoInvalidaException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Historico;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ParticipacaoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.internacionalizacao.dao.TraducaoElementoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ConstanteTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.EntidadeTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.IdiomasEnum;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducaoElementos;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.TraducaoElemento;
import br.ufrn.sigaa.ensino.internacionalizacao.negocio.InternacionalizacaoHelper;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.negocio.MovimentoCalculoHistorico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * Controller responsável pela internacionalização dos elementos pertencentes ao histórico do aluno.
 * (Componente Curricular, Curso, Matriz Curricular, Estrutura Curricular)
 * 
 * @author Rafael Gomes
 *
 */
@Scope("session")
@Component
public class HistoricoTraducaoMBean extends AbstractTraducaoElementoMBean<Historico> implements OperadorDiscente{

	/** Mapa utilizado para listagem de componentes curriculares pertencentes ao histórico do discente.*/
	Map<ComponenteCurricular, List<ItemTraducaoElementos>> mapaComponente = new HashMap<ComponenteCurricular, List<ItemTraducaoElementos>>();
	/** Mapa utilizado para listagem de identificadores dos componentes curriculares pertencentes ao histórico do discente.*/
	Map<Integer, List<ItemTraducaoElementos>> mapaComponenteId = new HashMap<Integer, List<ItemTraducaoElementos>>();
	/** Mapa utilizado para listagem dos objetos pertencentes ao histórico do discente.*/
	Map<EntidadeTraducao, List<ItemTraducaoElementos>> mapaEntidade = new HashMap<EntidadeTraducao, List<ItemTraducaoElementos>>();
	/** Mapa utilizado para listagem dos elementos da matriz curricular pertencente ao histórico do discente.*/
	Map<EntidadeTraducao, List<ItemTraducaoElementos>> mapaMatriz = new HashMap<EntidadeTraducao, List<ItemTraducaoElementos>>();
	/** Lista de componente curricular pertencente ao histórico do aluno.*/
	List<ComponenteCurricular> listComponentesHistorico = new ArrayList<ComponenteCurricular>();
	
	/** Obtendo as entidades(Objetos) da Aba Curso*/
	Collection<EntidadeTraducao> listEntidadesAbaCurso = new ArrayList<EntidadeTraducao>();
	
	/** Obtendo as entidades(Objetos) da Aba Matriz Curricular*/
	Collection<EntidadeTraducao> listEntidadesAbaMatriz = new ArrayList<EntidadeTraducao>();
	
	/** Construtor padrão. */
	public HistoricoTraducaoMBean() {
		clear();
	}

	/**
	 * Limpa os dados do MBean para sua utilização <br>
	 * JSP: Não invocado por JSP
	 */
	public void clear() {
		obj = new Historico();
	}
	
	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/historico";
	}
	
	@Override
	public String getListPage() {
		return "/graduacao/busca_discente.jsp";
	}
	
	
	/**
	 * Invoca o Mbean responsável por realizar a busca de discente.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarDiscente() throws SegurancaException {
		checkChangeRole();

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.INTERNACIONALIZAR_HISTORICO);
		
		return buscaDiscenteMBean.popular();
	}	
	
	
	@Override
	public String selecionaDiscente() throws ArqException {
		
		setOperacaoAtiva(SigaaListaComando.TRADUZIR_ELEMENTO.getId());
		prepareMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		
		popularHistorico();
		
		carregarComponentesTraducao();
		carregarEntidadesAbaCurso();
		carregarEntidadesAbaMatriz();
		
		return forward(getDirBase() + "/historico_discente.jsp");
		
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj.setDiscente(discente);
	}

	/** 
	 * Responsável por popular o objeto {@link Historico} com os dados do discente.
	 * <br>Método não invocado por JSP´s.
	 * @param discente
	 * @return
	 */
	private String popularHistorico(){
		try {
			checkChangeRole();
			
			if (obj.getDiscente() == null)
				throw new NegocioException("Nenhum discente foi selecionado.");
			
			if (obj.getDiscente().getStatus() == StatusDiscente.EXCLUIDO) {
				addMensagem(MensagensGerais.DISCENTE_SEM_HISTORICO, StatusDiscente.getDescricao(StatusDiscente.EXCLUIDO));
				return null;
			}
			
			// Verificar bloqueio de emissão do documento
			if ( !AutenticacaoUtil.isDocumentoLiberado(TipoDocumentoAutenticado.HISTORICO, obj.getDiscente().getNivel())) {
				addMensagem(MensagensGerais.HISTORICO_INDISPONIVEL);
				return redirectMesmaPagina();
			}

			MovimentoCalculoHistorico mov = new MovimentoCalculoHistorico();
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setRecalculaCurriculo(false);
			mov.setDiscente(obj.getDiscente());
			mov.setCodMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
			prepareMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
			Historico historico = new Historico();
			try {
				historico = execute(mov);
				obj = historico;
			} catch(Exception e) {
				tratamentoErroPadrao(e);
				return null;
			}
			
			return null;
			
		} catch(ExpressaoInvalidaException e) {
			addMensagem(MensagensGerais.ERRO_EXPRESSOES_HISTORICO);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
			return null;
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
	public void carregarComponentesTraducao() throws DAOException{
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		listComponentesHistorico = new ArrayList<ComponenteCurricular>();
		for (MatriculaComponente mc : obj.getMatriculasDiscente()) {
			listComponentesHistorico.add(mc.getComponente());
		}
		listComponentesHistorico.addAll(obj.getDisciplinasPendentesDiscente());
		
		//TODO Remover componentes de ENADE.
		
		qtdeIdiomas = IdiomasEnum.values().length;
		listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
		List<TraducaoElemento> listElementoFinal = new ArrayList<TraducaoElemento>();
		
		Map<Integer, List<ItemTraducaoElementos>> mapComponenteAtributosTraducao = 
				new HashMap<Integer, List<ItemTraducaoElementos>>();
		
		Collection<ItemTraducao> atributosTraducao = dao.findItensByClasse(ComponenteCurricular.class.getName(), Order.desc("nome"));
		mapComponenteAtributosTraducao = dao.findComponentesTraducao(listComponentesHistorico);

		mapaComponente = new HashMap<ComponenteCurricular, List<ItemTraducaoElementos>>();
		
		for (ComponenteCurricular cc : listComponentesHistorico) {
			
			List<ItemTraducaoElementos> lista = mapComponenteAtributosTraducao.get(cc.getId());
			boolean inputDisabled = false;
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
						if (IdiomasEnum.PORTUGUES.getId().equalsIgnoreCase(str)) {
							elemento.setValor((String) ReflectionUtils.evalPropertyObj(cc, itemTraducao.getAtributo()));
							inputDisabled = ValidatorUtil.isEmpty(elemento.getValor());
						}
						elemento.setInputDisabled(inputDisabled);
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
			mapaComponente.put(cc, listaTraducaoElemento);
			mapaComponenteId.put(cc.getId(), listaTraducaoElemento);
			listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
		}
	}

	/**
	 * Método utilizado para preparar e carregar as propriedades das entidades pertencentes a área do curso no documento traduzido. 
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param <T> 
	 * @param classe
	 * @param idElemento
	 * @throws DAOException
	 */
	public void carregarEntidadesAbaCurso() throws DAOException{
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		try {
			/* Obtendo as entidades(Objetos) da Aba Curso*/
			listEntidadesAbaCurso = new ArrayList<EntidadeTraducao>();
			listEntidadesAbaCurso = dao.findEntidadeByAreaDocumento(1);
			
			/* Obtendo os atributos(Field) do Objeto discente que compõe o histórico.*/
			List<Field> fields = ReflectionUtils.findFieldsWithAnnotation(obj.getDiscente().getDiscente().getClass(), Column.class);
			fields.addAll(ReflectionUtils.findFieldsWithAnnotation(obj.getDiscente().getDiscente().getClass(), JoinColumn.class));
			fields.addAll(ReflectionUtils.findFieldsWithAnnotation(obj.getDiscente().getClass(), Column.class));
			fields.addAll(ReflectionUtils.findFieldsWithAnnotation(obj.getDiscente().getClass(), JoinColumn.class));
			
			mapaEntidade = getMapListaTraducaoElemento(listEntidadesAbaCurso, fields, false);
			
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		} 
	}
	
	/**
	 * Método utilizado para preparar e carregar as propriedades das entidades pertencentes a área da Matriz curricular no documento traduzido. 
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param <T> 
	 * @param classe
	 * @param idElemento
	 * @throws DAOException
	 */
	public void carregarEntidadesAbaMatriz() throws DAOException{
		
		if (!obj.getDiscente().isRegular() || !obj.getDiscente().isGraduacao()){
			return;
		}
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		try {
			/* Obtendo as entidades(Objetos) da Aba Matriz Curricular*/
			listEntidadesAbaMatriz = new ArrayList<EntidadeTraducao>();
			listEntidadesAbaMatriz = dao.findEntidadeByAreaDocumento(2);
			
			/* Obtendo os atributos(Field) do Objeto discente que compõe o histórico.*/
			DiscenteGraduacao discenteGraducao = (DiscenteGraduacao) obj.getDiscente();
			List<Field> fields = ReflectionUtils.findFieldsWithAnnotation(discenteGraducao.getMatrizCurricular().getClass(), Column.class);
			fields.addAll(ReflectionUtils.findFieldsWithAnnotation(discenteGraducao.getMatrizCurricular().getClass(), JoinColumn.class));
			
			mapaMatriz = getMapListaTraducaoElemento(listEntidadesAbaMatriz, fields, true);
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		} 
	}
	
	/**
	 * Método utilizado para preencher um mapa de entidade de tradução com a lista de seus atributos, 
	 * para serem utilizado na JSP.
	 * 
	 * @param listEntidadesAba
	 * @param fields
	 * @param isAbaMatrizCurricular
	 * @return
	 */
	private Map<EntidadeTraducao, List<ItemTraducaoElementos>> getMapListaTraducaoElemento(Collection<EntidadeTraducao> listEntidadesAba, 
																							List<Field> fields,
																							boolean isAbaMatrizCurricular){
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		Map<EntidadeTraducao, List<ItemTraducaoElementos>> mapaAba = new HashMap<EntidadeTraducao, List<ItemTraducaoElementos>>();
		
		try {
			/* Mapa utilizado para armazenar a classe da EntidadeTraducao e o seu objeto relacionado, sendo adquirido a partir dos atributos do discente. */
			Map<String, Object> mapaObjeto = new HashMap<String, Object>();
			/* Lista com os elementos de tradução, utilizados para buscar os objetos TraducaoElemento já persistidos no banco para o objeto em questão,
			 * neste caso será armazenado apenas a EntidadeTradução e o idElemento.*/
			List<TraducaoElemento> listaElementosBusca = new ArrayList<TraducaoElemento>();
			/* Lista contendo os identificados de todos os elementos a serem traduzidos pela aba curso.*/
			List<Integer> idsElementos = new ArrayList<Integer>();
			/* Mapa contendo a relação do identificado do elemento com a entidade de tradução do objeto.*/
			Map<Integer, EntidadeTraducao> mapIdElementoEntidade = new HashMap<Integer, EntidadeTraducao>();
			
			for (EntidadeTraducao ent : listEntidadesAba) {
				Field field = null;
				for (Field f : fields) {
					if (f.getType().getName().equals(ent.getClasse())){
						field = f;
						break;
					}	
				}
				if (field != null) {
					Object objAba = ReflectionUtils.newInstance(ent.getClasse());
					if (isAbaMatrizCurricular){
						DiscenteGraduacao discenteGraducao = (DiscenteGraduacao) obj.getDiscente();
						objAba = ReflectionUtils.getFieldValue(discenteGraducao.getMatrizCurricular(), field);
					} else {
						objAba = ReflectionUtils.getFieldValue(
									(ReflectionUtils.hasGetterSetter(obj.getDiscente().getDiscente(), field.getName()) ? obj.getDiscente().getDiscente() : obj.getDiscente())
									, field);
					}
					if (objAba != null) {	
						mapaObjeto.put(ent.getClasse(), objAba);
						
						TraducaoElemento te = new TraducaoElemento();
						te.setItemTraducao(new ItemTraducao());
						te.getItemTraducao().setEntidade(ent);
						te.setIdElemento((Integer) ReflectionUtils.evalPropertyObj(objAba, "id"));
						
						idsElementos.add(te.getIdElemento());
						listaElementosBusca.add(te);
						
						mapIdElementoEntidade.put(te.getIdElemento(), ent);
					}	
				}
			}
			
			qtdeIdiomas = IdiomasEnum.values().length;
			listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
			List<TraducaoElemento> listElementoFinal = new ArrayList<TraducaoElemento>();
			
			Map<Integer, List<ItemTraducaoElementos>> mapAtributosTraducao = new HashMap<Integer, List<ItemTraducaoElementos>>();
			mapAtributosTraducao = dao.findAtributosElemento(listaElementosBusca);
	
			mapaAba = new HashMap<EntidadeTraducao, List<ItemTraducaoElementos>>();
			
			for (Integer idElemento : idsElementos) {
				
				List<ItemTraducaoElementos> lista = mapAtributosTraducao.get(idElemento);
				EntidadeTraducao entidade = mapIdElementoEntidade.get(idElemento);
				boolean inputDisabled = false;
				for (ItemTraducao itemTraducao : entidade.getItensTraducao()) {
					
					ItemTraducaoElementos itElementos = new ItemTraducaoElementos(itemTraducao, new ArrayList<TraducaoElemento>());
					if (lista != null){
						for (ItemTraducaoElementos ite : lista) {
							if (ite.getItemTraducao().getId() == itemTraducao.getId())
								itElementos = ite;
						}
					}	
					
					if (itElementos.getElementos() != null && itElementos.getElementos().isEmpty()){
						for (String str : IdiomasEnum.getAll()) {
							TraducaoElemento elemento = new TraducaoElemento(itemTraducao, str, idElemento);
							elemento.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(str));
							if (IdiomasEnum.PORTUGUES.getId().equalsIgnoreCase(str)) {
								elemento.setValor((String) ReflectionUtils.evalPropertyObj(mapaObjeto.get(itemTraducao.getEntidade().getClasse()), itemTraducao.getAtributo()));
								inputDisabled = ValidatorUtil.isEmpty(elemento.getValor());
							}
							elemento.setInputDisabled(inputDisabled);
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
								listElementoFinal.add(new TraducaoElemento(itemTraducao, str, idElemento, IdiomasEnum.getDescricaoIdiomas().get(str)));
							}
						}
					}
					listaTraducaoElemento.add(new ItemTraducaoElementos(itemTraducao, listElementoFinal));
					listElementoFinal = new ArrayList<TraducaoElemento>();
					
				}
				mapaAba.put(entidade, listaTraducaoElemento);
				listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
			}
			return mapaAba;
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErro( e.getMessage() );
			return null;
		}
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
		for (ComponenteCurricular c : listComponentesHistorico) {
			listaTraducaoElemento.addAll(mapaComponente.get(c));
		}
		for (EntidadeTraducao ent : listEntidadesAbaCurso) {
			if(mapaEntidade.get(ent) != null)
				listaTraducaoElemento.addAll(mapaEntidade.get(ent));
		}
		for (EntidadeTraducao ent : listEntidadesAbaMatriz) {
			if(mapaMatriz.get(ent) != null)
				listaTraducaoElemento.addAll(mapaMatriz.get(ent));
		}
		return super.cadastrar();
	}
	
	/**
	 * Método responsável pela persistência dos elementos de tradução dos componentes relacionados ao curso.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/historico/curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarAbaCurso() throws SegurancaException, ArqException, NegocioException {
		
		listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
		for (EntidadeTraducao ent : listEntidadesAbaCurso) {
			if(mapaEntidade.get(ent) != null)
				listaTraducaoElemento.addAll(mapaEntidade.get(ent));
		}
		
		return super.cadastrar();
	}
	
	/**
	 * Método responsável pela persistência dos elementos de tradução dos componentes relacionados a matriz curricular de alunos de graduação.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/historico/matriz.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarAbaMatriz() throws SegurancaException, ArqException, NegocioException {
		
		listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
		for (EntidadeTraducao ent : listEntidadesAbaMatriz) {
			if(mapaMatriz.get(ent) != null)
				listaTraducaoElemento.addAll(mapaMatriz.get(ent));
		}
	
		return super.cadastrar();
	}
	
	/**
	 * Método responsável por tratar as traduções de um elemento percorrendo a listagem de traduções com histórico.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param atributo
	 * @param idElemento
	 * @param idioma
	 * @param mapAtributosTraducao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public TraducaoElemento buscaTraducaoPorIdioma(String atributo, Integer idElemento, String idioma, Map<Integer, List<ItemTraducaoElementos>> mapAtributosTraducao) throws HibernateException, DAOException{
		TraducaoElemento traducaoElemento = new TraducaoElemento();	
		List<ItemTraducaoElementos> lista = mapAtributosTraducao.get(idElemento);
		if (lista != null){
			for (ItemTraducaoElementos ite : lista) {
				if(ite.getItemTraducao().getAtributo().equals(atributo)){
					for (TraducaoElemento te : ite.getElementos()) {
						if (te.getIdioma().equals(idioma)){
							traducaoElemento = te;
						}
					}
				}
			}
		}
		return traducaoElemento;
	}
	
	/**
	 * Método responsável por tratar e retornar as traduções de uma entidade
	 * retornando um mapa com tradução dos elementos de uma determinada entidade.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param atributo
	 * @param idElemento
	 * @param idioma
	 * @param mapAtributosTraducao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<Integer, List<ItemTraducaoElementos>> buscaItensTraducaoPorEntidade(String classe) throws HibernateException, DAOException{
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		TraducaoElemento traducaoElemento = new TraducaoElemento();
		
		EntidadeTraducao entidadeElemento = dao.findEntidadeByClasse(classe);
		
		List<TraducaoElemento> listaElementosBusca = new ArrayList<TraducaoElemento>();
		
		traducaoElemento.setItemTraducao(new ItemTraducao());
		traducaoElemento.getItemTraducao().setEntidade(entidadeElemento);
		
		listaElementosBusca.add(traducaoElemento);
		
		Map<Integer, List<ItemTraducaoElementos>> mapAtributosTraducao = new HashMap<Integer, List<ItemTraducaoElementos>>();
		mapAtributosTraducao = dao.findAtributosElemento(listaElementosBusca);
		
		return mapAtributosTraducao;
	}
	
	/**
	 * Método responsável por tratar e retornar as traduções de uma entidade
	 * retornando um mapa com tradução dos elementos de uma determinada entidade 
	 * e identificadores de elementos para tradução.
 	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param classe
	 * @param idsElementos
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<Integer, List<ItemTraducaoElementos>> buscaItensTraducaoPorEntidadeElementos(String classe, List<Integer> idsElementos) throws HibernateException, DAOException{
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		Map<Integer, List<ItemTraducaoElementos>> mapAtributosTraducao = new HashMap<Integer, List<ItemTraducaoElementos>>();
		EntidadeTraducao entidadeElemento = dao.findEntidadeByClasse(classe);
		
		if (entidadeElemento != null){
		
			List<TraducaoElemento> listaElementosBusca = new ArrayList<TraducaoElemento>();
			
			for (Integer idElemento : idsElementos) {
				TraducaoElemento traducaoElemento = new TraducaoElemento();
				traducaoElemento.setItemTraducao(new ItemTraducao());
				traducaoElemento.getItemTraducao().setEntidade(entidadeElemento);
				traducaoElemento.setIdElemento(idElemento);
				
				listaElementosBusca.add(traducaoElemento);
			}
			
			mapAtributosTraducao = dao.findAtributosElemento(listaElementosBusca);
		} else {
			erros.addErro("Não foi possível localizar tradução para a entidade do objeto: " 
					+ classe.getClass().getSimpleName() + "." );
		}
		return mapAtributosTraducao;
	}
	
	/**
	 * Método responsável pela tradução dos elementos dinâmicos pertencentes ao histórico.  
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param historico
	 * @param historicoComEmenta 
	 * @throws DAOException 
	 */
	//TODO
	public void traduzirElementosHistorico(Historico historico, String idioma, boolean historicoComEmenta) throws DAOException{
		
		TraducaoElementoDao teDao = getDAO(TraducaoElementoDao.class, null);
		
		setObj(historico);
		carregarComponentesTraducao();
		carregarEntidadesAbaCurso();
		
		List<Integer> idsTurma = new ArrayList<Integer>();
		for (MatriculaComponente mc : historico.getMatriculasDiscente()) {
			if (mc.getTurma() != null) idsTurma.add(mc.getTurma().getId());
		}
		
		Map<Integer, List<ItemTraducaoElementos>> mapaComponente = getMapaComponenteId();
		Map<Integer, List<ItemTraducaoElementos>> mapaEntidadeAuxiliar = buscaItensTraducaoPorEntidade(SituacaoMatricula.class.getName());
		Map<Integer, List<ItemTraducaoElementos>> mapaEntidadeIndices = buscaItensTraducaoPorEntidade(IndiceAcademico.class.getName());
		Map<Integer, List<ItemTraducaoElementos>> mapaTurma = buscaItensTraducaoPorEntidadeElementos(Turma.class.getName(), idsTurma);
		Map<EntidadeTraducao, List<ItemTraducaoElementos>> mapaEntidade = getMapaEntidade();

		Set<MensagemAviso> errosTraducao = new HashSet<MensagemAviso>();
		MensagemAviso erro = new MensagemAviso();
		erros = new ListaMensagens();
		
		/*Verifica se todos os componentes e afins (Turma e Situação de Matrícula) possuem tradução para o idioma selecionado.*/
		for (MatriculaComponente mc : historico.getMatriculasDiscente()) {
			if (mc.getComponente().getCodigo() != null && mc.getComponente().getId() > 0 && mapaComponente.get(mc.getComponente().getId()) != null){
				List<ItemTraducaoElementos> traducaoElementos = mapaComponente.get(mc.getComponente().getId());

				ComponenteCurricular ccAux = new ComponenteCurricular();
				ccAux.setId(0);
				ccAux.setNome(mc.getTurma().getDisciplina().getNome());
				
				for (ItemTraducaoElementos ite : traducaoElementos) {
					for (TraducaoElemento te : ite.getElementos()) {
						if (te.getIdioma().equals(idioma)){
							TraducaoElemento traducao = new TraducaoElemento();
							
							/* Situação da matrícula */
							traducao = buscaTraducaoPorIdioma("descricao", mc.getSituacaoMatricula().getId(), te.getIdioma(), mapaEntidadeAuxiliar);
							if (traducao.getValor() != null)
								ReflectionUtils.setProperty(mc.getSituacaoMatricula(), "descricao", traducao.getValor());
							else {
								erro = new MensagemAviso("Não foi possível localizar tradução para Situação de Matrícula: " 
										+ mc.getSituacaoMatricula().getDescricao(), TipoMensagemUFRN.ERROR );
								errosTraducao.add(erro);
							}	
							/* Observação da Turma */
							if ( mc.getTurma() != null && ValidatorUtil.isNotEmpty(mc.getTurma().getObservacao())){
								traducao = buscaTraducaoPorIdioma("observacao", mc.getTurma().getId(), te.getIdioma(), mapaTurma);
								if (traducao.getValor() != null)
									ReflectionUtils.setProperty(mc.getTurma(), "observacao", traducao.getValor());
								else {
									mc.getTurma().getDisciplina().setNome(ccAux.getNome());
									erro = new MensagemAviso("Não foi possível localizar tradução para a observação da Turma: " 
											+ mc.getTurma().getDescricaoCodigo() + " ("+  mc.getAnoPeriodo() + ") ", TipoMensagemUFRN.ERROR );
									errosTraducao.add(erro);
								}	
							}	
							/* Componente Curricular */
							if (te.getValor() != null)
								ReflectionUtils.setProperty(mc.getComponente(), ite.getItemTraducao().getAtributo(), te.getValor());
							else if (ReflectionUtils.evalPropertyObj(mc.getComponente(), ite.getItemTraducao().getAtributo()) != null){
								erro = new MensagemAviso("Não foi possível localizar tradução para o Componente Curricular: " 
											+ mc.getComponente().getDescricao(), TipoMensagemUFRN.ERROR );
								errosTraducao.add(erro);
							}
						}
					}
				}
			}	
		}
		ConstanteTraducao msgNaoInformado = teDao.findConstanteByEntidadeIdiomaConstante(idioma, "NAO_INFORMADO", ComponenteCurricular.class.getName()); 
		
		/*Verifica se todos os componentes pendentes e afins (Turma e Situação de Matrícula) possuem tradução para o idioma selecionado.*/
		Collection<ConstanteTraducao> listEnade = teDao.findConstanteByEntidadeIdioma(idioma, ParticipacaoEnade.class.getName());
		for (ComponenteCurricular cc : historico.getDisciplinasPendentesDiscente()) {
			if (cc.getCodigo() != null && cc.getId() > 0 && mapaComponente.get(cc.getId()) != null){
				List<ItemTraducaoElementos> traducaoElementos = mapaComponente.get(cc.getId());
				for (ItemTraducaoElementos ite : traducaoElementos) {
					for (TraducaoElemento te : ite.getElementos()) {
						if (te.getIdioma().equals(idioma)){
							/* Componente Curricular */
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
			}
			else if (cc.getCodigo() != null && cc.getCodigo().equals("ENADE")){
				for (ConstanteTraducao ct : listEnade) {
					if ( cc.getNome().replace(" ", "_").equals(ct.getConstante()) ){
						cc.setNome(ct.getValor());
						cc.setEmenta(msgNaoInformado.getValor());
					}
				}
			}
		}
		
		//Tratamento para tradução de ENADE dos alunos de Graduação.
		if (historico.getDiscente().isGraduacao()){
			DiscenteGraduacao grad = (DiscenteGraduacao) historico.getDiscente();
			
			if (grad.getParticipacaoEnadeIngressante() != null){
				for (MatriculaComponente mc : historico.getMatriculasDiscente()) {
					if (mc.getComponente().getCodigo().equals("ENADE") && mc.getAno() == grad.getAnoIngresso().shortValue()){
						TraducaoElemento tradElemento = teDao.findByAtributoAndElementoIdioma(
															ParticipacaoEnade.class.getName(),
															"descricao", grad.getParticipacaoEnadeIngressante().getId(), 
															idioma);
						if (tradElemento != null && tradElemento.getValor() != null){
							mc.getComponente().setNome(tradElemento.getValor());
							mc.getComponente().setEmenta(msgNaoInformado.getValor());
						} else {
							erro = new MensagemAviso("Não foi possível localizar tradução para a Participação de ENADE: " 
										+ mc.getComponente().getDescricao(), TipoMensagemUFRN.ERROR );
							errosTraducao.add(erro);
						}
						
					}
						
				}
			}
			
			if (grad.getParticipacaoEnadeConcluinte() != null){
				int ano = grad.getDataProvaEnadeConcluinte() != null ? 
							CalendarUtils.getAno(grad.getDataProvaEnadeConcluinte()) : 
							( grad.getDataColacaoGrau() != null ? CalendarUtils.getAno(grad.getDataColacaoGrau()) : 0 );
				for (MatriculaComponente mc : historico.getMatriculasDiscente()) {
					if (mc.getComponente().getCodigo().equals("ENADE") && mc.getAno() == ano){
						TraducaoElemento tradElemento = teDao.findByAtributoAndElementoIdioma(
															ParticipacaoEnade.class.getName(),
															"descricao", grad.getParticipacaoEnadeConcluinte().getId(), 
															idioma);
						if (tradElemento != null && tradElemento.getValor() != null)
							mc.getComponente().setNome(tradElemento.getValor());
						else {
							erro = new MensagemAviso("Não foi possível localizar tradução para a Participação de ENADE: " 
										+ mc.getComponente().getDescricao(), TipoMensagemUFRN.ERROR );
							errosTraducao.add(erro);
						}
						
					}
						
				}
			}
			
		}
		
		List<Field> fields = ReflectionUtils.findFieldsWithAnnotation(historico.getDiscente().getDiscente().getClass(), Column.class);
		fields.addAll(ReflectionUtils.findFieldsWithAnnotation(historico.getDiscente().getDiscente().getClass(), JoinColumn.class));
		fields.addAll(ReflectionUtils.findFieldsWithAnnotation(historico.getDiscente().getClass(), Column.class));
		fields.addAll(ReflectionUtils.findFieldsWithAnnotation(historico.getDiscente().getClass(), JoinColumn.class));
		
		
		Iterator<Map.Entry<EntidadeTraducao, List<ItemTraducaoElementos>>> itMap = mapaEntidade.entrySet().iterator();
		while (itMap.hasNext()) {
		    Map.Entry<EntidadeTraducao, List<ItemTraducaoElementos>> entry = itMap.next();
		    
		    EntidadeTraducao ent = entry.getKey();
		    List<ItemTraducaoElementos> traducaoElementos = entry.getValue();
		    
		    Field field = null;
			for (Field f : fields) {
				if (f.getType().getName().equals(ent.getClasse())){
					field = f;
					break;
				}	
			}
			
			if (field != null) {
				Object objAbaCurso = ReflectionUtils.newInstance(ent.getClasse());
				if ( ReflectionUtils.hasGetterSetter(historico.getDiscente().getDiscente(), field.getName()) )
					objAbaCurso = ReflectionUtils.getFieldValue(historico.getDiscente().getDiscente(), field);
				else
					objAbaCurso = ReflectionUtils.getFieldValue(historico.getDiscente(), field);
				
				for (ItemTraducaoElementos ite : traducaoElementos) {
					boolean possuiValorPtBr = false;
					for (TraducaoElemento te : ite.getElementos()) {
						if (te.getIdioma().equals(IdiomasEnum.PORTUGUES.getId()) && ValidatorUtil.isNotEmpty(te.getValor())){
							possuiValorPtBr = true;
						}
						if (te.getIdioma().equals(idioma) && possuiValorPtBr){
							if (te.getValor() != null)
								ReflectionUtils.setProperty(objAbaCurso, ite.getItemTraducao().getAtributo(), te.getValor());
							else  if (ReflectionUtils.evalPropertyObj(objAbaCurso, ite.getItemTraducao().getAtributo()) != null){
								try {
									if (ReflectionUtils.getFieldValue(objAbaCurso, ite.getItemTraducao().getAtributo()) == null)
										objAbaCurso = HibernateUtils.getTarget(objAbaCurso);
									erro = new MensagemAviso("Não foi possível localizar tradução para " 
												+ ite.getItemTraducao().getNome() + " de " + ite.getItemTraducao().getEntidade().getNome()  
												+ ": " + ReflectionUtils.getFieldValue(objAbaCurso, ite.getItemTraducao().getAtributo()), TipoMensagemUFRN.ERROR );
									errosTraducao.add(erro);
								} catch (Exception e) {
									addMensagemErro( "Não foi possível pegar o valor do atributo '" + ite.getItemTraducao().getAtributo() + "' da Entidade " +ite.getItemTraducao().getEntidade().getNome() );
								}	
							}
						}
					}	
				}
				
				/* Inserindo o objeto traduzido para o objeto do discente no histórico. */
				if ( ReflectionUtils.hasGetterSetter(historico.getDiscente().getDiscente(), field.getName()) )
					ReflectionUtils.setProperty(historico.getDiscente().getDiscente(), field.getName(), objAbaCurso);
				else
					ReflectionUtils.setProperty(historico.getDiscente(), field.getName(), objAbaCurso);
			}
		}
		
		for (IndiceAcademicoDiscente iad : historico.getDiscente().getDiscente().getIndices()) {
			if (mapaEntidadeIndices.get(iad.getIndice().getId()) != null){
				List<ItemTraducaoElementos> traducaoElementos = mapaEntidadeIndices.get(iad.getIndice().getId());
				for (ItemTraducaoElementos ite : traducaoElementos) {
					for (TraducaoElemento te : ite.getElementos()) {
						if (te.getIdioma().equals(idioma)){
							if (te.getValor() != null)
								ReflectionUtils.setProperty(iad.getIndice(), ite.getItemTraducao().getAtributo(), te.getValor());
							else {
								erro = new MensagemAviso("Não foi possível localizar tradução para " + ite.getItemTraducao().getEntidade().getNome()  
										+": "+ ReflectionUtils.getFieldValue(iad, ite.getItemTraducao().getAtributo()), TipoMensagemUFRN.ERROR );
								errosTraducao.add(erro);
							}
						}
					}
				}
			}
		}
		
		erros.setMensagens(errosTraducao);
		errosTraducao = new HashSet<MensagemAviso>();
		
		List<TipoGenerico> equivalenciasDiscente = new ArrayList<TipoGenerico>();
		analisarEquivalenciaPorExtenso(historico.getDiscente(), 
				CollectionUtils.toList(historico.getMatriculasDiscente()), 
				equivalenciasDiscente, idioma, errosTraducao);
		historico.setEquivalenciasDiscente(equivalenciasDiscente);
		
		historico.setMobilidadeEstudantil(InternacionalizacaoHelper.mobilidadeEstudantil(historico.getDiscente(), idioma, errosTraducao));
		
		historico.setObservacoesDiscente(observacaoDiscente(historico.getDiscente(), idioma, errosTraducao));
		
		ConstanteTraducao constateStatusDiscente = teDao.findConstanteByEntidadeIdiomaConstante(idioma, String.valueOf(historico.getDiscente().getStatus()), StatusDiscente.class.getName());
		if (constateStatusDiscente != null)
			historico.setStatusDiscenteI18n(constateStatusDiscente.getValor());
		else{
			errosTraducao.add( new MensagemAviso("Não foram localizadas as traduções das Constantes de Status do discente para o idioma "
					+ IdiomasEnum.getDescricaoIdiomas().get(idioma)+"."
					, TipoMensagemUFRN.ERROR ));
		}

		if ( historico.getDiscente().getMovimentacaoSaida() != null ){
			TraducaoElemento traducaoMovSaida = teDao.findByAtributoAndElementoIdioma(TipoMovimentacaoAluno.class.getName(), "descricao", historico.getDiscente().getMovimentacaoSaida().getTipoMovimentacaoAluno().getId(), idioma);
			if (traducaoMovSaida != null)
				historico.getDiscente().getMovimentacaoSaida().getTipoMovimentacaoAluno().setDescricao(traducaoMovSaida.getValor());
			else{
				erro = new MensagemAviso("Não foi possível localizar tradução para a Movimentação de " + historico.getDiscente().getMovimentacaoSaida().getTipoMovimentacaoAluno().getDescricao()+ " do discente." 
						, TipoMensagemUFRN.ERROR );
				errosTraducao.add(erro);
			}
		}
		
		if (historico.getDiscente().isStricto() && historico.getDiscente().getCurso() == null){
			
			Collection<ConstanteTraducao> list = teDao.findConstanteByEntidadeIdioma(idioma, Discente.class.getName(), NivelEnsino.class.getName());
			String constEstudanteEspecial = "";
			String nivelDesc = "";
			for (ConstanteTraducao ct : list) {
				if (ct.getClasse().equals(Discente.class.getName()) && ct.getConstante().equals("ESTUDANTE_ESPECIAL")){
					constEstudanteEspecial = ct.getValor();
				}	
				if (ct.getClasse().equals(NivelEnsino.class.getName()) && ct.getConstante().charAt(0)==(historico.getDiscente().getNivel())){
					nivelDesc = " - " + ct.getValor();
				}	
			}
			historico.getDiscente().setCurso(new Curso());
			historico.getDiscente().getCurso().setNome(constEstudanteEspecial + nivelDesc);
		}
		
		if (historico.getDiscente().getTrancamentos() != null && historico.getDiscente().getTrancamentos().equals("Nenhum")){
			ConstanteTraducao constateTrancamentos = teDao.findConstanteByEntidadeIdiomaConstante(idioma, "NENHUM", Discente.class.getName());
			if (constateTrancamentos != null)
				historico.getDiscente().setTrancamentos(constateTrancamentos.getValor());
			else{
				errosTraducao.add( new MensagemAviso("Não foram localizadas as traduções das Constantes para Trancamentos do discente para o idioma "
						+ IdiomasEnum.getDescricaoIdiomas().get(idioma)+"."
						, TipoMensagemUFRN.ERROR ));
			}
		}
			
		
		if (historico.getDiscente().isGraduacao()){
			ReflectionUtils.setProperty(historico.getDiscente(), "rendimentoAcademino",  teDao.findConstanteByEntidadeIdiomaConstante(idioma, "RENDIMENTO_ACADEMICO", Discente.class.getName()).getValor());
		}
			
		// Remoção das matrículas com situações diferentes de pagas ou matriculadas
		if (historicoComEmenta){
			for (Iterator<MatriculaComponente> it = historico.getMatriculasDiscente().iterator(); it.hasNext();) {
				MatriculaComponente mComponente = it.next();
				if (ValidatorUtil.isEmpty(mComponente.getDetalhesComponente().getEmenta()))
					mComponente.getDetalhesComponente().setEmenta(msgNaoInformado.getValor());
				if (!SituacaoMatricula.getSituacoesPagasEMatriculadas().contains(mComponente.getSituacaoMatricula()))
					it.remove();
			}
			//Tratamento para componente pendentes
			for (Iterator<ComponenteCurricular> it = historico.getDisciplinasPendentesDiscente().iterator(); it.hasNext();) {
				ComponenteCurricular ccPendente = it.next();
				if (ValidatorUtil.isEmpty(ccPendente.getDetalhes().getEmenta()))
					ccPendente.getDetalhes().setEmenta(msgNaoInformado.getValor());
			}
		}
		erros.addAll(errosTraducao);
		hasErrors();
		
	}
	
	/**
	 * Retorna os componentes obrigatórios pendentes e faz análise textual das equivalências
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * 
	 * @param discenteDao
	 * @param discente
	 * @param disciplinas
	 * @param equivalenciasDiscente
	 * @throws DAOException
	 */
	private void analisarEquivalenciaPorExtenso(DiscenteAdapter discente, List<MatriculaComponente> disciplinas, List<TipoGenerico> equivalenciasDiscente, String idioma, Set<MensagemAviso> errosTraducao) throws DAOException {
		InternacionalizacaoHelper.analisarEquivalenciasPorExtenso(discente.getId(), disciplinas, equivalenciasDiscente, idioma, errosTraducao);
	}
	
	/**
	 * Retorna as traduções das observações do Discente.
	 * 
	 * @param discenteDao
	 * @param discente
	 * @param disciplinas
	 * @param equivalenciasDiscente
	 * @throws DAOException
	 */
	private List<ObservacaoDiscente> observacaoDiscente(DiscenteAdapter discente, String idioma, Set<MensagemAviso> errosTraducao) throws DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		TraducaoElementoDao teDao = getDAO(TraducaoElementoDao.class);
		
		List<ObservacaoDiscente> list = new ArrayList<ObservacaoDiscente>();
		list = dao.findObservacoesDiscente(discente);
		
		if (!list.isEmpty()) {
			List<Integer> idsObs = new ArrayList<Integer>();
			for (ObservacaoDiscente obs : list) {
				idsObs.add(obs.getId());
			}
	
			Collection<TraducaoElemento> colecaoTraducao = teDao.findByAtributoAndElementoIdioma(ObservacaoDiscente.class.getName(), "observacao", idsObs, idioma);
			if (!colecaoTraducao.isEmpty()) {
				for (ObservacaoDiscente obsDiscente : list) {
					for (TraducaoElemento te : colecaoTraducao) {
						if (obsDiscente.getId() == te.getIdElemento()){
							if (te != null && te.getValor() != null)
								ReflectionUtils.setProperty(obsDiscente, te.getItemTraducao().getAtributo(), te.getValor());
							else if (ReflectionUtils.evalPropertyObj(obsDiscente, te.getItemTraducao().getAtributo()) != null){
								MensagemAviso erro = new MensagemAviso("Não foi possível localizar tradução para as observações do discente: " 
											+ discente, TipoMensagemUFRN.ERROR );
								errosTraducao.add(erro);
							}
							break;
						}
					}
				}
			} else 	{
				MensagemAviso erro = new MensagemAviso("Não foi possível localizar tradução para as observações do discente: " 
						+ discente, TipoMensagemUFRN.ERROR );
				errosTraducao.add(erro);
			}
		}
		return list;
	}
	
	/**
	 * Método responsável por retornar uma seleção de itens formada por idiomas.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/relacoes_internacionais/historico/selecao_idioma.jsp</li>
	 * </ul>
	 * */
	public Collection<SelectItem> getAllIdiomasCombo() {
		return toSelectItems(IdiomasEnum.getDescricaoIdiomas());
	}
	
	
	// Getters and Setters
	public Map<ComponenteCurricular, List<ItemTraducaoElementos>> getMapaComponente() {
		return mapaComponente;
	}

	public void setMapaComponente(
			Map<ComponenteCurricular, List<ItemTraducaoElementos>> mapaComponente) {
		this.mapaComponente = mapaComponente;
	}

	public Map<EntidadeTraducao, List<ItemTraducaoElementos>> getMapaEntidade() {
		return mapaEntidade;
	}

	public void setMapaEntidade(
			Map<EntidadeTraducao, List<ItemTraducaoElementos>> mapaEntidade) {
		this.mapaEntidade = mapaEntidade;
	}

	public List<ComponenteCurricular> getListComponentesHistorico() {
		return listComponentesHistorico;
	}

	public void setListComponentesHistorico(
			List<ComponenteCurricular> listComponentesHistorico) {
		this.listComponentesHistorico = listComponentesHistorico;
	}

	public Map<EntidadeTraducao, List<ItemTraducaoElementos>> getMapaMatriz() {
		return mapaMatriz;
	}

	public void setMapaMatriz(
			Map<EntidadeTraducao, List<ItemTraducaoElementos>> mapaMatriz) {
		this.mapaMatriz = mapaMatriz;
	}

	public Map<Integer, List<ItemTraducaoElementos>> getMapaComponenteId() {
		return mapaComponenteId;
	}

	public void setMapaComponenteId(
			Map<Integer, List<ItemTraducaoElementos>> mapaComponenteId) {
		this.mapaComponenteId = mapaComponenteId;
	}
}