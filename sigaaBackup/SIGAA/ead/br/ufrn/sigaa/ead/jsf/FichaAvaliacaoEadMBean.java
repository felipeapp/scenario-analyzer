/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/06/2008
 *
 */
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.arq.dao.ead.FichaAvaliacaoEadDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ead.dominio.AvaliacaoDiscenteEad;
import br.ufrn.sigaa.ead.dominio.FichaAvaliacaoEad;
import br.ufrn.sigaa.ead.dominio.ItemAvaliacaoEad;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.dominio.NotaItemAvaliacao;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ead.dominio.SemanaAvaliacao;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.negocio.AvaliacaoEadMov;
import br.ufrn.sigaa.ead.negocio.EADHelper;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.jsf.PortalTutorMBean;

/**
 * Managed bean para cadastro de ficha de avaliação de discentes de ensino a
 * distancia
 * 
 * @author David Pereira
 * 
 */
@Component("fichaAvaliacaoEad") @Scope("session")
public class FichaAvaliacaoEadMBean extends SigaaAbstractController<FichaAvaliacaoEad> {
	
	/** Armazena a página do formulário para escolha dos dados da avaliação. */
	private static final String JSP_FORMULARIO_AVALIACAO = "/ead/FichaAvaliacaoEad/form_avaliacao.jsp";

	/** Armazena a página do formulário para visualização da avaliação. */
	private static final String JSP_VISUALIZAR_AVALIACAO = "/ead/FichaAvaliacaoEad/visualizarAvaliacao.jsp";

	/** Metodologia de avaliação do curso. */
	private MetodologiaAvaliacao metodologia;

	/** Componente Curricular Selecionado para avaliação. */
	private Integer componenteEscolhido;
	
	/** Semana de Avaliação escolhida. */
	private Integer semanaEscolhida;

	/** Id do discente escolhido para avaliação. */
	private Integer discenteEscolhido;

	/** Armazena a {@link AvaliacaoDiscenteEad} do discente. */
	private AvaliacaoDiscenteEad avaliacao;

	/** Discente selecionado. */
	private DiscenteGraduacao discente;

	/** Itens de avaliação. */
	private List<ItemAvaliacaoEad> itens;

	/** Indica se a avaliação será cadastrada ou alterada. */
	private boolean novaAvaliacao;
	
	/** Id do pólo-curso escolhido. */
	private Integer idPoloCursoEscolhido;

	/** Curso escolhido. */
	private Integer cursoEscolhido;
	
	public FichaAvaliacaoEadMBean() {
		clean();
	}

	/**
	 * Verificar Metodologia de Avaliação e redirecionar para lista de discentes
	 * ou semanas de avaliação.<br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/alunos.jsp</li>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * <li>/sigaa.war/portais/tutor/alunos.jsp</li>
	 * <li>/sigaa.war/portais/tutor/menu_tutor.jsp</li>
	 * <li>/sigaa.war/portais/tutor/tutor.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciar() throws ArqException, NegocioException {
	    	clean();
	    
		setOperacaoAtiva(1, "Não existe avaliação ativa. Por favor, reinicie o processo.");
		prepareMovimento(SigaaListaComando.AVALIAR_DISCENTE_EAD);

		if (!isTutorEad())
			throw new SegurancaException("Apenas tutores presenciais podem realizar a avaliação de discentes.");

		idPoloCursoEscolhido = getParameterInt("poloCurso",0);
		discenteEscolhido = getParameterInt("discente");
		componenteEscolhido = getParameterInt("componente");
		semanaEscolhida = getParameterInt("semana");
			
		if (discenteEscolhido != null){
			EADDao dao = getDAO(EADDao.class);
			Discente discente = dao.findByPrimaryKey(discenteEscolhido, Discente.class);
			metodologia = MetodologiaAvaliacaoHelper.getMetodologia(discente.getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
	
			if (metodologia == null) {
				addMensagemErro("Não é possível realizar a avaliação porque ainda não foi definida uma metodologia de ensino para o curso "
						+ getTutorUsuario().getPoloCurso().getCurso().getNome());
				return null;
			}
			
			obj.setAno(getCalendarioVigente().getAno());
			obj.setPeriodo(getCalendarioVigente().getPeriodo());
	
			PortalTutorMBean pBean = getMBean ("portalTutor");
			
			if (getParameter("mostrarFicha") == null && (pBean.isAdministracaoEAD() ? metodologia.getSemanasAvaliacao().isEmpty() : metodologia.getSemanasAvaliacaoAtivas().isEmpty())) {
				addMensagemErro("Não existem avaliações abertas.");
				return null;
			}
		}
		return forward(JSP_FORMULARIO_AVALIACAO);
		
	}


	/**
	 * Seleciona a semana a ser avaliada.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/semanasMostrarFicha.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public String selecionaSemanaMostrarFicha() throws DAOException, NegocioException {
		
		if (metodologia.isUmaProva())
			componenteEscolhido = getParameterInt("semana");
		else
			semanaEscolhida = getParameterInt("semana");
		
		return mostrarFicha();
	}

	/**
	 * Carrega a semana a ser exibida.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/visualizarAvalicao.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public void carregaSemana(ValueChangeEvent e) throws DAOException, NegocioException {
		
		Integer semana = Integer.valueOf(((String) e.getNewValue()));
		
		if (metodologia.isUmaProva())
			componenteEscolhido = semana;
		else
			semanaEscolhida = semana;
	}
	
	/**
	 * Carrega o discente a ser exibido.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/visualizarAvalicao.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public void carregaDiscente(ValueChangeEvent e) throws DAOException, NegocioException {
		discenteEscolhido = (Integer) e.getNewValue();
		buscaDiscente();
	}
	
	/**
	 * Carrega os discentes a ser exibido.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/form_avaliacao.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public void carregarDiscentes(ValueChangeEvent e) throws DAOException, NegocioException {
		EADDao dao = getDAO(EADDao.class);
		cursoEscolhido = (Integer) e.getNewValue();
		if (cursoEscolhido!=0) {
			PoloCurso pcurso = dao.findByPrimaryKey(cursoEscolhido, PoloCurso.class);
			metodologia = MetodologiaAvaliacaoHelper.getMetodologia(pcurso.getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
			if (metodologia == null) {
				addMensagemErro("Não é possível realizar a avaliação porque ainda não foi definida uma metodologia de ensino para o curso "
						+ getTutorUsuario().getPoloCurso().getCurso().getNome());
			}
			
			obj.setAno(getCalendarioVigente().getAno());
			obj.setPeriodo(getCalendarioVigente().getPeriodo());
	
			PortalTutorMBean pBean = getMBean ("portalTutor");
			
			if (getParameter("mostrarFicha") == null && (pBean.isAdministracaoEAD() ? metodologia.getSemanasAvaliacao().isEmpty() : metodologia.getSemanasAvaliacaoAtivas().isEmpty())) {
				addMensagemErro("Não existem avaliações abertas.");
			}
		}
	}
	
	/**
	 * Cria uma nova avaliação para a ficha do discente para a semana
	 * digitada pelo tutor.<br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/nova.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	public String novaAvaliacaoSemanal() throws NegocioException, ArqException {
	    	if (isEmpty(discenteEscolhido)) {
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
		}
	    	if (isEmpty(componenteEscolhido) && metodologia.isUmaProva()) {
	    	addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Componente Curricular");
		}
	    	if (isEmpty(semanaEscolhida)) {
	    	addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Semana de Avaliação");
		}
	    	
	    	if(hasErrors())
	    	    return null;
		
		MatriculaComponenteDao mDao = getDAO(MatriculaComponenteDao.class);
		
		try {
		    discente = (DiscenteGraduacao) getDAO(DiscenteDao.class).findByPK(discenteEscolhido);
		    discente.getPolo().getDescricao();
		} finally {
		    mDao.close();
		}
		
		popularObj();

		if (obj.getId() == 0) {
			novaAvaliacao();
		} else {
			
			FichaAvaliacaoEadDao rDao = getDAO(FichaAvaliacaoEadDao.class);
			avaliacao = rDao.findAvaliacaoBySemana(obj, semanaEscolhida);

			// SETA O ANO/PERIODO DA FICHA AVALIACAO QUE O DISCENTE ESTÁ MATRICULADO
			if (obj.getComponente() != null) {
				List<?> lista = rDao.anoPeriodoComponenteCurricularByDiscente( obj );
				for (int i = 0; i < lista.size(); i++) {
					Map<?,?> m = (Map<?,?>) lista.get(i);
					
					obj.setAno((Integer)m.get("ano"));
					obj.setPeriodo((Integer)m.get("periodo"));
				}
			}
			
			if (avaliacao == null) {
				novaAvaliacao();
			} else {
				List<NotaItemAvaliacao> notas = avaliacao.getNotas();
				
				if (notas == null)
					throw new NegocioException("Não foi possível carregar as notas da avaliação. Por favor, reinicie a operação.");
				
				notas.iterator();

				itens = new ArrayList<ItemAvaliacaoEad>();
				for (NotaItemAvaliacao nota : notas) {
					if (!metodologia.isUmaProva() && rDao.isComponenteValido(obj, nota.getComponente())) {
						criaItens(nota);
					} else if (metodologia.isUmaProva()) {
						criaItens(nota);
					}
				}

				ordenarItensAvaliacao(itens);
				avaliacao.setItens(itens);
			}
		}

		if (isEmpty(getDisciplinas())) {
			addMensagemErro("O discente selecionado não está matriculado em nenhum componente curricular.");
			return null;
		}
		else {
			if(hasTurmasConsolidadas()) {
				addMensagemErro("Não é possível avaliar turmas que já foram consolidadas.");
				return null;
			}
		}

		return forward("/ead/FichaAvaliacaoEad/avaliacao.jsp");
	}

	/**
	 * Verifica se a(s) turma(s) que o tutor está tentando avaliar estão consolidadas.
	 * @throws DAOException 
	 */
	private boolean hasTurmasConsolidadas() throws DAOException {
		List<Integer> ids = new ArrayList<Integer>();
		for (ComponenteCurricular cc : getDisciplinas()) {
			ids.add(cc.getId());
		}
		
		String sql = "from ensino.componente_curricular cc " +
							"inner join ensino.turma t using (id_disciplina) " +
							 "inner join ensino.matricula_componente mc using (id_turma) " +
						"where cc.id_disciplina in " + UFRNUtils.gerarStringIn(ids) +" and t.id_situacao_turma = " + SituacaoTurma.CONSOLIDADA +
							" and mc.ano = " + obj.getAno() + " and mc.periodo = " + obj.getPeriodo() + " and mc.id_discente = " + discente.getId();
		
		return getGenericDAO().count(sql) > 0;
	}

	/**
	 * Cria os itens de acordo com a nota informada
	 * @param nota
	 */
	private void criaItens(NotaItemAvaliacao nota) {
		if (!nota.getItem().isAtivo()) return;
		if (!itens.contains(nota.getItem())) {
			ItemAvaliacaoEad i = nota.getItem();
			i.setNotas(new ArrayList<NotaItemAvaliacao>());
			i.getNotas().add(nota);
			Collections.sort(i.getNotas(), new Comparator<NotaItemAvaliacao>() {
				public int compare(NotaItemAvaliacao o1, NotaItemAvaliacao o2) {
					return o1.getComponente().getCodigo().compareTo(o2.getComponente().getCodigo());
				}
			});
			
			itens.add(i);
		} else {
			ItemAvaliacaoEad i = itens.get(itens.indexOf(nota
					.getItem()));
			i.getNotas().add(nota);
			Collections.sort(i.getNotas(), new Comparator<NotaItemAvaliacao>() {
				public int compare(NotaItemAvaliacao o1, NotaItemAvaliacao o2) {
					return o1.getComponente().getCodigo().compareTo(o2.getComponente().getCodigo());
				}
			});
		}
	}

	/**
	 * Cria uma nova avaliação e os seus objetos filhos
	 */
	private void novaAvaliacao() throws DAOException, NegocioException {
		avaliacao = new AvaliacaoDiscenteEad();
		avaliacao.setFicha(obj);
		avaliacao.setSemana(semanaEscolhida);
		avaliacao.setData(new Date());
		avaliacao.setUsuario(getUsuarioLogado());

		List<ItemAvaliacaoEad> itensEncontrados = getItens();
		
		ordenarItensAvaliacao(itensEncontrados);

		for (ItemAvaliacaoEad item : itensEncontrados) {
			item.setNotas(new ArrayList<NotaItemAvaliacao>());

			if (metodologia.isUmaProva()) {
				NotaItemAvaliacao nota = new NotaItemAvaliacao();
				if (componenteEscolhido == null) {
					addMensagemErro("Nenhum componente curricular foi escolhido.");
					return;
				}
				nota.setComponente(new ComponenteCurricular(componenteEscolhido));
				nota.setItem(item);
				nota.setAvaliacao(avaliacao);

				item.getNotas().add(nota);
				Collections.sort(item.getNotas(), new Comparator<NotaItemAvaliacao>() {
					public int compare(NotaItemAvaliacao o1, NotaItemAvaliacao o2) {
						return o1.getComponente().getCodigo().compareTo(o2.getComponente().getCodigo());
					}
				});
			} else {
				for (ComponenteCurricular disciplina : getDisciplinas()) {
					NotaItemAvaliacao nota = new NotaItemAvaliacao();
					nota.setComponente(disciplina);
					nota.setItem(item);
					nota.setAvaliacao(avaliacao);
	
					item.getNotas().add(nota);
					Collections.sort(item.getNotas(), new Comparator<NotaItemAvaliacao>() {
						public int compare(NotaItemAvaliacao o1, NotaItemAvaliacao o2) {
							return o1.getComponente().getCodigo().compareTo(o2.getComponente().getCodigo());
						}
					});
				}
			}
		}

		avaliacao.setItens(itensEncontrados);
	}

	/**
	 * Ordena os itens de avaliação.<br /><br />
	 * Método não invocado por JSP(s):
	 */
	private void ordenarItensAvaliacao(List<ItemAvaliacaoEad> itensOrdenacao) {
		Collections.sort(itensOrdenacao, new Comparator<ItemAvaliacaoEad>() {
			public int compare(ItemAvaliacaoEad i1, ItemAvaliacaoEad i2) {
				return String.valueOf(i1.getId()).compareTo(String.valueOf(i2.getId()));
			}
		});
	}
	
	/**
	 * Retorna o fluxo para a tela de escolha de dados da avaliação.
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/avaliacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String voltar() {
	    return forward(JSP_FORMULARIO_AVALIACAO);
	}

	/**
	 * Retorna o fluxo para a tela de escolha de dados da avaliação.
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/ficha.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public String voltarVisualizacao() throws DAOException, NegocioException {
		if (metodologia.isUmaProva())
			return verFichaDiscente();
		else
			return cancelar();
	}
	
	/**
	 * Finaliza a avaliação e grava as alterações realizadas.<br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/avaliacao.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String avaliar() throws ParseException, NegocioException, ArqException {
		if (!checkOperacaoAtiva(1)) return cancelar();
		
		popularNotas();

		try {
			AvaliacaoEadMov mov = new AvaliacaoEadMov();
			mov.setCodMovimento(SigaaListaComando.AVALIAR_DISCENTE_EAD);
			
			mov.setFicha(obj);
			mov.setAvaliacao(avaliacao);

			executeWithoutClosingSession(mov, getCurrentRequest());

			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Avaliação");
			
			clean();
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return null;
		}

		return iniciar();
	}

	/**
	 * Avaliar ou mostrar a ficha.
	 * <br /><br />
	 * Método não chamado por JSPs.
	 */
	public String avaliacaoDiscente() {

		try {
			buscaDiscente();
			prepareMovimento(SigaaListaComando.AVALIAR_DISCENTE_EAD);
			if (semanaEscolhida != null)
				novaAvaliacao = (getDAO(FichaAvaliacaoEadDao.class)
						.findAvaliacaoBySemana(obj, semanaEscolhida) == null);
			else
				throw new NegocioException(
						"Não há semanas de avaliação abertas.");

			if (getParameterBoolean("avaliar"))
				return novaAvaliacaoSemanal();
			else
				return mostrarFicha();
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
			return null;
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return null;
		}

	}

	/**
	 * Avaliar ou mostrar a ficha.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/discentes.jsp</li>
	 * <li>/sigaa.war/portais/cpolo/alunos.jsp</li>
	 * <li>/sigaa.war/portais/tutor/alunos.jsp</li>
	 * <li>/sigaa.war/portais/tutor/tutor.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String fichaDiscente() throws NegocioException, ArqException {
		
			discenteEscolhido = null;
			EADDao dao = getDAO(EADDao.class);

			metodologia = MetodologiaAvaliacaoHelper.getMetodologia(getTutorUsuario().getPoloCurso().getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());

			buscaDiscente();
			prepareMovimento(SigaaListaComando.AVALIAR_DISCENTE_EAD);

			if (metodologia.isUmaProva())
				return forward("/ead/FichaAvaliacaoEad/semanasMostrarFicha.jsp");
			else
				return mostrarFicha();

	}
	
	/**
	 * Avaliar ou mostrar a ficha.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>método não invocado por JSP(s):</li>
	 * </ul>
	 * FIXME código duplicado, EXTRAIR MÉTODO
	 */
	public String fichaDiscenteCoordenadorPolo(PoloCurso poloCurso) {
		
		try {
			discenteEscolhido = null;
			EADDao dao = getDAO(EADDao.class);
			metodologia = MetodologiaAvaliacaoHelper.getMetodologia(poloCurso.getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
			
			buscaDiscente();
			prepareMovimento(SigaaListaComando.AVALIAR_DISCENTE_EAD);
			
			if (metodologia.isUmaProva())
				return forward("/ead/FichaAvaliacaoEad/semanasMostrarFicha.jsp");
			else
				return mostrarFicha();
			
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return null;
		}
		
	}

	/**
	 * Popula todas as notas das avaliações vindas de request
	 */
	private void popularNotas() throws ParseException, DAOException, NegocioException {

		if (getItens() != null) {
			for (ItemAvaliacaoEad item : getItens()) {
				if (item.getNotas() != null) {
					for (NotaItemAvaliacao nota : item.getNotas()) {
						double notaDbl = getNota("nota_" + item.getId() + "_" + nota.getComponente().getId());
						nota.setNota(notaDbl);
					}
				}
			}
		}
	}

	/**
	 * Pega uma nota em request. Se a nota não existir, retorna zero. Usado pelo
	 * popularNotas()
	 */
	private double getNota(String paramName) throws ParseException {
		DecimalFormat format = new DecimalFormat("#0.0");
		String param = getParameter(paramName);

		if (param != null && !"".equals(param.trim())) {
			param = param.trim();

			return Double.parseDouble(format.parse(param).toString());
		}

		return 0.0;
	}

	/**
	 * Retorna os itens de avaliação. Se for null busca, senão retorna.
	 */
	private List<ItemAvaliacaoEad> getItens() throws DAOException, NegocioException {
		if (itens == null || itens.isEmpty()) {
			EADDao dao = getDAO(EADDao.class);
			
			Discente d = dao.findByPrimaryKey(discente.getId(), Discente.class);
			
			MetodologiaAvaliacao metodologia = MetodologiaAvaliacaoHelper.getMetodologia(d.getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
			
			if (metodologia == null)
				throw new NegocioException("Não é possível realizar a avaliação pois não foi definida uma metodologia de avaliação para o curso " + discente.getCurso().getNomeCompleto());
			
			itens = metodologia.getItens();
		}

		return itens;
	}

	/**
	 * Busca as disciplinas nas quais o discente selecionado está matriculado
	 */
	public List<ComponenteCurricular> getDisciplinas() throws DAOException {
		if (metodologia.isUmaProva()) {
			ComponenteCurricular componente = null;
			if (metodologia.isUmaProva())
				componente = getGenericDAO().findByPrimaryKey(componenteEscolhido, ComponenteCurricular.class);

			List<ComponenteCurricular> result = new ArrayList<ComponenteCurricular>();
			result.add(componente);
			return result;
		} else {
			MatriculaComponenteDao mDao = getDAO(MatriculaComponenteDao.class);
			return mDao.findComponentesMatriculadosTurmaAbertaByDiscente(discente, obj.getAno(), obj.getPeriodo());
		}
	}
	
	/**
	 * Retorna um combo contendo as disciplinas matriculadas pelo discente escolhido.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getDisciplinasCombo() throws DAOException {
	    MatriculaComponenteDao mDao = getDAO(MatriculaComponenteDao.class);
	    Collection<SelectItem> componentes = new ArrayList<SelectItem>();
	    
	    try {
		List<ComponenteCurricular> matriculadas = 
			mDao.findComponentesMatriculadosTurmaAbertaByDiscente(new DiscenteGraduacao(discenteEscolhido), null, null);
		
		/** Indica se o componente selecionado no portal do tutor é um componente matriculado pelo discente atualmente. */
		boolean hasComponenteSelecionado = false;
		if(!isEmpty(matriculadas)){
			for (ComponenteCurricular cc : matriculadas) {
			    if(!isEmpty(componenteEscolhido) && cc.getId() == componenteEscolhido) {
				hasComponenteSelecionado = true;
				break;
			    }
			}
		}
		
		//Zera o componente escolhido se o discente não tiver matrícula nele
		if(!hasComponenteSelecionado)
		    componenteEscolhido = null;
		
		if(!isEmpty(matriculadas)){
			componentes = toSelectItems(matriculadas, "id", "nome");
		}
	    } finally {
		mDao.close();
	    }
	    
	    return componentes;
	}

	/**
	 * Redireciona para lista de discentes com ficha de avaliação.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/tutor/menu_tutor.jsp</li>
	 * </ul>
	 * @return
	 */
	public String listaDiscentesMostrarFicha() {
		return forward("/ead/FichaAvaliacaoEad/discentesMostrarFicha.jsp");
	}

	/**
	 * Redireciona para o formulário de visualizar avaliações.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/tutor/menu_tutor.jsp</li>
	 * </ul>
	 * @return
	 */
	public String visualizarAvaliacoes() {
		clean();
		return forward(JSP_VISUALIZAR_AVALIACAO);
	}
	
	/**
	 * Mostra a ficha de avaliação de acordo com o ano/período.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/nova.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String mostrarFicha() throws DAOException, NegocioException {
		
		if (isEmpty(discente)){
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
		    return null;
		}
		
    	if (isEmpty(componenteEscolhido) && metodologia.isUmaProva()) {
	    	addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Componente Curricular");
	    	return null;
    	}
    	
		FichaAvaliacaoEadDao dao = getDAO(FichaAvaliacaoEadDao.class);
		CalendarioAcademico cal = getCalendarioVigente();

		int ano = getParameterInt("ano") == null ? cal.getAno() : getParameterInt("ano");
		int periodo = getParameterInt("periodo") == null ? cal.getPeriodo() : getParameterInt("periodo");

		ComponenteCurricular componente = null;
		if (metodologia.isUmaProva() && componenteEscolhido != null) {
			componente = dao.findByPrimaryKey(componenteEscolhido, ComponenteCurricular.class);
			
			List<Map<String, Object>>  lista = dao.anoPeriodoComponenteCurricularByDiscente( discenteEscolhido, componenteEscolhido );
			for (int i = 0; i < lista.size(); i++) {
				Map m = lista.get(i);
				
				ano = (Integer)m.get("ano");
				periodo = (Integer)m.get("periodo");
			}
		}

		obj = dao.findFichaAvaliacaoByDiscente(discente, periodo, ano, componente);
		if (obj == null) {
			addMensagemErro("Não foi realizada nenhuma avaliação para o discente selecionado.");
			obj = new FichaAvaliacaoEad();
			return null;
		} else {
			preencheFichaAvaliacao(obj);
		}

		return forward("/ead/FichaAvaliacaoEad/ficha.jsp");
	}

	/**
	 * Preenche a ficha de avaliação de acordo com a ficha informada
	 * @param ficha
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void preencheFichaAvaliacao(FichaAvaliacaoEad ficha)
			throws DAOException, NegocioException {

		ficha.setMetodologia(metodologia);
		
		List<ComponenteCurricular> disciplinas = getDisciplinas();
		ficha.setMedias(new double[disciplinas.size()]);
		ficha.setMediasUnidade2(new double[disciplinas.size()]);

		int semana = 1;

		for (AvaliacaoDiscenteEad a : ficha.getAvaliacoes()) {

			a.setMedias(new double[disciplinas.size()]);
			a.setMediasUnidade2(new double[disciplinas.size()]);

			Collections.sort(a.getNotas(), new Comparator<NotaItemAvaliacao>() {
				public int compare(NotaItemAvaliacao o1, NotaItemAvaliacao o2) {
					return o1.getComponente().getCodigo().compareTo(o2.getComponente().getCodigo());
				}
			});
			semana = a.getSemana();
			List<ItemAvaliacaoEad> itens = notas(disciplinas, a, semana);
			
			ordenarItensAvaliacao(itens);

			a.setItens(itens);
		}
		
		mediaFinal(disciplinas);
		
	}

	/**
	 * Retorna a lista de avaliações agrupadas por unidade.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/ficha.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public List<AvaliacoesAgrupadaPorUnidade> getResultado() {
		
		List<AvaliacoesAgrupadaPorUnidade> resultado = new ArrayList<FichaAvaliacaoEadMBean.AvaliacoesAgrupadaPorUnidade>();
			
		for (AvaliacaoDiscenteEad aval : obj.getAvaliacoes()) {
			AvaliacoesAgrupadaPorUnidade avalAgrupada = new AvaliacoesAgrupadaPorUnidade();
			avalAgrupada.setUnidade(aval.getUnidade());
			
			if (resultado.contains(avalAgrupada)) {
				int index = resultado.indexOf(avalAgrupada);
				avalAgrupada = resultado.get(index);
			} else {
				resultado.add(avalAgrupada);
				if (aval.getUnidade() == 1)
					avalAgrupada.setMedias(obj.getMedias());
				else
					avalAgrupada.setMedias(obj.getMediasUnidade2());
			}
			avalAgrupada.getAvaliacoes().add(aval);
		}
		
		return resultado;
	}
	
	/**
	 * Retorna as notas de acordo com as disciplinas, a avaliação e a semana
	 * @param disciplinas
	 * @param a
	 * @param semana
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private List<ItemAvaliacaoEad> notas(
			List<ComponenteCurricular> disciplinas, AvaliacaoDiscenteEad a,
			int semana) throws DAOException, NegocioException {

		List<NotaItemAvaliacao> notas = a.getNotas();
		notas.iterator();

		int num = 0, index = 0;
		List<ItemAvaliacaoEad> itens = new ArrayList<ItemAvaliacaoEad>();

		if (semana <= metodologia.getNumeroAulasPrimeiraUnidade() || metodologia.isUmaProva()) {

			for (NotaItemAvaliacao nota : notas) {
				if (disciplinas.contains(nota.getComponente())) {

					itens(itens, nota);

					a.getMedias()[num] += (nota.getNota() / getItens().size()); 

					index++;

					if (index == getItens().size()) {
						index = 0;
						num++;
					}
				}
			}
		} else {
			for (NotaItemAvaliacao nota : notas) {
				if (disciplinas.contains(nota.getComponente())) {

					itens(itens, nota);

					a.getMediasUnidade2()[num] += (nota.getNota() / getItens().size());

					index++;

					if (index == getItens().size()) {
						index = 0;
						num++;
					}
				}
			}
		}

		return itens;
	}

	/** Implementação para popular a lista de Itens de Avaliação da EAD, executando com disciplina e nota */
	private void itens(List<ItemAvaliacaoEad> itens, NotaItemAvaliacao nota) {

		if (!itens.contains(nota.getItem())) {
			ItemAvaliacaoEad i = new ItemAvaliacaoEad();
			i.setId(nota.getItem().getId());
			i.setNome(nota.getItem().getNome());

			i.setNotas(new ArrayList<NotaItemAvaliacao>());
			i.getNotas().add(nota);

			itens.add(i);
		} else {
			ItemAvaliacaoEad i = itens.get(itens.indexOf(nota.getItem()));
			i.getNotas().add(nota);
			Collections.sort(i.getNotas(), new Comparator<NotaItemAvaliacao>() {
				public int compare(NotaItemAvaliacao o1, NotaItemAvaliacao o2) {
					return o1.getComponente().getCodigo().compareTo(o2.getComponente().getCodigo());
				}
			});
		}
	}

	/**
	 * Calcula as médias do objeto.
	 * 
	 * @param disciplinas
	 * @throws NegocioException
	 */
	private void mediaFinal(List<ComponenteCurricular> disciplinas) throws NegocioException {
		for (AvaliacaoDiscenteEad a : obj.getAvaliacoes()) {
			for (int i = 0; i < disciplinas.size(); i++) {
				if (metodologia.isUmaProva()) {
					if (obj.getMedias() != null && a.getMedias() != null && obj.getComponente() != null) {
						if (!isEmpty(a.getMedias()[i]))
							obj.getMedias()[i] += (a.getMedias()[i]) / (obj.getComponente().getChTotalAula() > 100 ? 8 : 4);
						else
							obj.getMedias()[i] += (a.getMediasUnidade2()[i]) / (obj.getComponente().getChTotalAula() > 100 ? 8 : 4);
					}
				} else {
					if (obj.getMedias() != null && a.getMedias() != null) {
						if (a.getSemana() <= metodologia.getNumeroAulasPrimeiraUnidade())
							obj.getMedias()[i] += (a.getMedias()[i] / metodologia.getNumeroAulasPrimeiraUnidade());
						else
							obj.getMediasUnidade2()[i] += (a.getMediasUnidade2()[i] / metodologia.getNumeroAulasByUnidade(2));
					}
				}
			}
		}	
	} 

	/**
	 * Limpa o managed bean para executar uma nova avaliação.
	 * Método não chamado por JSPs.
	 */
	public void clean() {
		obj = new FichaAvaliacaoEad();
		avaliacao = new AvaliacaoDiscenteEad();
		discente = new DiscenteGraduacao();
		metodologia = new MetodologiaAvaliacao();
		itens = new ArrayList<ItemAvaliacaoEad>();
		semanaEscolhida = null;
		discenteEscolhido = null;
		idPoloCursoEscolhido = null;
		cursoEscolhido = null;
		novaAvaliacao = false;
		idPoloCursoEscolhido = 0;
	}
	
	/**
	 * Seta o discente em HistoricoMBean.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * <li>/sigaa.war/portais/tutor/tutor.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String historico() throws Exception {
		
		TutorOrientadorDao dao = getDAO(TutorOrientadorDao.class);
		
		Integer id = getParameterInt("idDiscente");
		DiscenteGraduacao discente = dao.findByPrimaryKey(id, DiscenteGraduacao.class);
		
		ArrayList<TutorOrientador> t = EADHelper.findTutoresByAluno(discente);
		if (t != null) {
			discente.setTutores(t);
		}
		
		HistoricoMBean historico = (HistoricoMBean) getMBean("historico");
		historico.setDiscente(discente);
		return historico.selecionaDiscente();
	}	
	
	public AvaliacaoDiscenteEad getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(AvaliacaoDiscenteEad avaliacao) {
		this.avaliacao = avaliacao;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public Integer getDiscenteEscolhido() {
	    return discenteEscolhido;
	}

	public void setDiscenteEscolhido(Integer discenteEscolhido) {
	    this.discenteEscolhido = discenteEscolhido;
	}

	public void setSemanaEscolhida(Integer semanaEscolhida) {
	    this.semanaEscolhida = semanaEscolhida;
	}

	@Override
	public String cancelar() {
		clean();
		return super.cancelar();
	}

	/**
	 * Retorna os discentes da tutoria 
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> getDiscentesTutoria() throws DAOException {
		TutorOrientadorDao dao = getDAO(TutorOrientadorDao.class);	
		Collection<DiscenteGraduacao> discentes = null;
		PoloCurso pcurso = dao.findByPrimaryKey(idPoloCursoEscolhido,PoloCurso.class);
		if (pcurso != null)
			discentes = EADHelper.findDiscentesByTutor(getUsuarioLogado().getTutor().getId(), getUsuarioLogado().getPessoa().getId(),pcurso.getPolo().getId(),pcurso.getCurso().getId());
		else
			discentes = EADHelper.findDiscentesByTutor(getUsuarioLogado().getTutor().getId(),getUsuarioLogado().getPessoa().getId(),null,null);
		return discentes;
	}
	
	/**
	 * Retorna os discentes da tutoria em formato de combo.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getDiscentesTutoriaCombo() throws DAOException {
	    return toSelectItems(getDiscentesTutoria(), "id", "nome");
	}
	
	/**
	 * Retorna os cursos do tutor em formato de combo.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCursosCombo() throws DAOException {		
	    return toSelectItems(getUsuarioLogado().getTutor().getPoloCursos(), "id", "descricao");
	}
	/**
	 * Verifica se usuário logado tem vínculo de tutor orientador
	 * 
	 * @return
	 */
	protected boolean isTutorEad() {
		if (getUsuarioLogado().getVinculoAtivo() != null)
			return getUsuarioLogado().getVinculoAtivo().isVinculoTutorOrientador();
		return false;
	}

	/**
	 * Redireciona para página com listagem de discentes.
	 * Método não chamado por JSPs.
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String listarDiscentes() throws SegurancaException, DAOException {
		return forward("/ead/FichaAvaliacaoEad/discentes.jsp");
	}

	/**
	 * Redireciona para página com a ficha do discente.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/discentesMostrarFicha.jsp</li>
	 * <li>/sigaa.war/portais/discente/include/ensino.jsp</li>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String verFichaDiscente() throws DAOException, NegocioException {
		discenteEscolhido = null;
		if (getDiscenteUsuario() != null) {
			discenteEscolhido = getDiscenteUsuario().getId();
			metodologia = MetodologiaAvaliacaoHelper.getMetodologia(getDiscenteUsuario().getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
		}
		
		try {
			buscaDiscente();
		} catch (NegocioException e) {
		}
		
		if (metodologia.isUmaProva())
			return forward("/ead/FichaAvaliacaoEad/semanasMostrarFicha.jsp");
		return mostrarFicha();
	}

	/**
	 * Retorna o combo com as semanas de avaliacao do discentes
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/FichaAvaliacaoEad/visualizarAvalicao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public Collection<SelectItem> getSemanasCombo() throws DAOException, NegocioException {
		
		List<SemanaAvaliacao> semanas = metodologia.getSemanasAvaliacaoAtivas();
		if (metodologia != null && !isEmpty(semanas)){
			return toSelectItems(semanas, "semana", "descricao");
		}
		return null;
	}
	
	/**
	 * Faz uma busca pelo discente
	 * 
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void buscaDiscente() throws DAOException, NegocioException {
		if (discenteEscolhido == null)
			discenteEscolhido = getParameterInt("discente");
		getUsuarioLogado().setNivelEnsino('G');

		FichaAvaliacaoEadDao dao = getDAO(FichaAvaliacaoEadDao.class);
		
		discente = (DiscenteGraduacao) getDAO(DiscenteDao.class).findByPK(discenteEscolhido);
		discente.getPolo().getDescricao();

		metodologia = MetodologiaAvaliacaoHelper.getMetodologia(discente.getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());

		if( metodologia == null ){
			throw new NegocioException("Não há Metodologia de Avaliação cadastrada para o Curso.");
		}

		popularObj();
	}

	/**
	 * Popula o objeto
	 * Método não chamado por JSPs.
	 * @throws DAOException
	 */
	private void popularObj() throws DAOException {
            FichaAvaliacaoEadDao dao = getDAO(FichaAvaliacaoEadDao.class);
            ComponenteCurricular componente = null;
            
            CalendarioAcademico cal = getCalendarioVigente();
            int ano = cal.getAno();
            int periodo = cal.getPeriodo();	
            
            if (metodologia.isUmaProva() && componenteEscolhido != null) {
                componente = dao.findByPrimaryKey(componenteEscolhido, ComponenteCurricular.class);		
                List<Map<String, Object>> lista = dao.anoPeriodoComponenteCurricularByDiscente( discenteEscolhido, componenteEscolhido );
                for (int i = 0; i < lista.size(); i++) {
                Map m = lista.get(i);
                
                ano = (Integer)m.get("ano");
                periodo = (Integer)m.get("periodo");
                }
            }
    	    
    	    if (semanaEscolhida != null || (metodologia.isUmaProva() && componenteEscolhido != null)) {
                this.obj = dao.findFichaAvaliacaoByDiscente(discente, periodo, ano, componente);
                
                if (this.obj == null) {
                    this.obj = new FichaAvaliacaoEad();
                    this.obj.setDiscente(discente.getDiscente());
                    this.obj.setAno(ano);
                    this.obj.setPeriodo(periodo);
                    this.obj.setData(new Date());
                    this.obj.setUsuario(getUsuarioLogado());
                    this.obj.setComponente(componente);
                }
            }
	}

	public boolean isNovaAvaliacao() {
		return novaAvaliacao;
	}

	public void setNovaAvaliacao(boolean novaAvaliacao) {
		this.novaAvaliacao = novaAvaliacao;
	}

	public MetodologiaAvaliacao getMetodologia() {
		return metodologia;
	}

	public Integer getSemanaEscolhida() {
		return semanaEscolhida;
	}

	public Integer getComponenteEscolhido() {
		return componenteEscolhido;
	}
	
	/**
	 * Retorna quantidade de semanas do componente escolhido.
	 * 
	 * @return
	 * @throws DAOException
	 */
    public Integer getSemanasComponenteEscolhido() throws DAOException {
    	if (metodologia.isUmaProva()) {
    	    if(isEmpty(componenteEscolhido))
    	    	return 0;
    	    
    	    ComponenteCurricular componente = getGenericDAO().findByPrimaryKey(componenteEscolhido, ComponenteCurricular.class);
    
    	    if (componente.getChTotalAula() < 100) {
    	    	return FichaAvaliacaoEad.QTD_SEMANAS_COMPONENTE_CURTO;
    	    } else {
    	    	return FichaAvaliacaoEad.QTD_SEMANAS_COMPONENTE_LONGO;
    	    }
    	}
    	else {
    	    int semanas = 0;
    	    
    	    for(Integer i : metodologia.getNumeroAulasInt())
    	    	semanas += i;
    	    
    	    return semanas;
    	}
    }
    
    /**
     * Retorna um combo contendo as semanas de avaliação do componente escolhido.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getSemanasComponenteCombo() throws DAOException {
        Collection<SelectItem> semanas = new ArrayList<SelectItem>();
        
        for(int i = 1; i <= getSemanasComponenteEscolhido(); i++) {
        	semanas.add(new SelectItem(i));
        }
        
        return semanas;
    }

	public void setComponenteEscolhido(Integer componenteEscolhido) {
		this.componenteEscolhido = componenteEscolhido;
	}

	public void setIdPoloCursoEscolhido(Integer idPoloCursoEscolhido) {
		this.idPoloCursoEscolhido = idPoloCursoEscolhido;
	}

	public Integer getIdPoloCursoEscolhido() {
		return idPoloCursoEscolhido;
	}

	public void setCursoEscolhido(Integer cursoEscolhido) {
		this.cursoEscolhido = cursoEscolhido;
	}

	public Integer getCursoEscolhido() {
		return cursoEscolhido;
	}

	/**
     * Classe auxiliar que representa todas as avaliações de um discente ead de uma determinada unidade.
     */
	public class AvaliacoesAgrupadaPorUnidade {
		
		public AvaliacoesAgrupadaPorUnidade() {
		}
		
		/** Avaliações do discente. */
		private List<AvaliacaoDiscenteEad> avaliacoes = new ArrayList<AvaliacaoDiscenteEad>();
		
		/** Unidade das avaliações. */
		private int unidade;
		
		/** Média dos itens das avaliações por componente. */
		private double[] medias;

		public List<AvaliacaoDiscenteEad> getAvaliacoes() {
			return avaliacoes;
		}

		public void setAvaliacoes(List<AvaliacaoDiscenteEad> avaliacoes) {
			this.avaliacoes = avaliacoes;
		}

		public int getUnidade() {
			return unidade;
		}

		public void setUnidade(int unidade) {
			this.unidade = unidade;
		}

		public double[] getMedias() {
			return medias;
		}

		public void setMedias(double[] medias) {
			this.medias = medias;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + unidade;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AvaliacoesAgrupadaPorUnidade other = (AvaliacoesAgrupadaPorUnidade) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (unidade != other.unidade)
				return false;
			return true;
		}

		private FichaAvaliacaoEadMBean getOuterType() {
			return FichaAvaliacaoEadMBean.this;
		}
		
	}
	
}
