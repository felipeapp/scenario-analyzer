/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 27/09/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaMedioDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.OrdemBuscaDisciplina;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;

/**
 * MBean de operações de buscas a qualquer nível de ensino de disciplinas do ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
@Component("buscaDisciplinaMedio")
@Scope("session")
public class BuscaDisciplinaMedioMBean extends BuscaTurmaMBean{
	
	/** Indica a JSP referente a tela de busca geral de disciplinas de ensino médio*/
	private static final String JSP_BUSCA_GERAL = "/medio/disciplina/busca_disciplina.jsf";
	
	/** Série utilizado para restringir o resultado da busca. */
	private Serie serie;
	
	/** Indica se filtra a busca por série. */
	private boolean filtroSerie;
	
	/**
	 * Coleção de selectItens com os tipos de turma que podem ser criados para
	 * popular o select no formulário quando a turma estiver sendo criada sem
	 * ser a partir de uma solicitação
	 */
	private Collection<SelectItem> tiposTurmaCombo = new ArrayList<SelectItem>();
	
	/** Lista das séries por curso de ensino médio. */
	private List<SelectItem> seriesByCurso = new ArrayList<SelectItem>(0);
	
	
	/** Construtor padrão. */
	public BuscaDisciplinaMedioMBean() {
		obj = new Turma();
		setCurso(new Curso());
		setUnidade(new Unidade());
		
		tiposTurmaCombo = new ArrayList<SelectItem>();
		
		SelectItem regular = new SelectItem();
		regular.setLabel("REGULAR");
		regular.setValue(Turma.REGULAR);
		tiposTurmaCombo.add(regular);
	
		SelectItem ferias = new SelectItem();
		ferias.setLabel("FÉRIAS");
		ferias.setValue(Turma.FERIAS);
		tiposTurmaCombo.add(ferias);
	
		SelectItem ensinoIndividual = new SelectItem();
		ensinoIndividual.setLabel("ENSINO INDIVIDUAL");
		ensinoIndividual.setValue(Turma.ENSINO_INDIVIDUAL);
		tiposTurmaCombo.add(ensinoIndividual);
	}
	
	/**
	 * Inicializa os filtros utilizados na consulta de turmas.
	 */
	private void initFiltros() {
		setFiltroAnoPeriodo(true); 
		setFiltroCodigo(false);
		setFiltroDisciplina(false);
		setFiltroDocente(false);
		setFiltroPolo(false);
	}
	
	/** 
	 * Popular os dados gerais para a realização da busca de turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/discente/medio/menu_discente_medio.jsp</li>
	 * </ul>
	 * 
	 * @return /ensino/turma/busca_turma.jsf
	 */
	public String popularBuscaGeral() {
		
		initFiltros();
		setSituacaoTurma(SituacaoTurma.ABERTA);
		CalendarioAcademico cal = getCalendarioVigente();
		setAnoTurma( cal != null ? cal.getAno() : CalendarUtils.getAnoAtual() );
		
		setNivelTurma(NivelEnsino.MEDIO);
		setCurso(new Curso());
		setSerie(new Serie());
		setCodigoDisciplina("");
		setNomeDisciplina("");
		setNomeDocente("");

		setResultadosBusca(new ArrayList<Turma>(0));
		return forward(JSP_BUSCA_GERAL);
	}

	@Override
	public String buscarGeral() throws DAOException {
		Character nivel = getNivelTurma();
		String codigo = null;
		String codigoTurma = null;
		String nome = null;
		String docente = null;
		Integer ano = null;
		Integer situacao = null;
		Polo poloEscolhido = null;
		Curso c = null;
		Serie s = null;
		Unidade u = null;
		String local = null;
		Integer tipo = null;
		String horario = null;
		Integer ordenarPor = null;
		ModalidadeEducacao modalidade = new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL);
		
		if (isUserInRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO))
			modalidade = null;
		
		if(isOrdenarBusca()) {
			ordenarPor = this.getOrdenarPor();
		}

		if(isFiltroUnidade()) {
			if (getUnidade().getId() == 0)
				addMensagemErro("Selecione uma unidade válida.");
			u = getUnidade();
		}
		if (isFiltroCodigoTurma())
			codigoTurma = this.getCodigoTurma().trim();
		if (isFiltroCodigo())
			codigo = getCodigoDisciplina().trim();
		if (isFiltroDisciplina())
			nome = getNomeDisciplina().trim();
		if (isFiltroDocente())
			docente = getNomeDocente().trim();
		if (isFiltroSituacao())
			situacao = getSituacaoTurma() > 0 ? getSituacaoTurma() : null;
		if (isFiltroAnoPeriodo()){
			ano = getAnoTurma();
		}
		if(isFiltroPolo()) {
			if (getPolo().getId() == 0)
				addMensagemErro("Selecione um pólo válido.");
			poloEscolhido = getPolo();
		}
		if(isFiltroCurso()) {
			if (getCurso().getId() == 0)
				addMensagemErro("Selecione um curso válido.");
			c = getGenericDAO().refresh(getCurso());
		}
		
		if(filtroSerie) {
			if (serie.getId() == 0)
				addMensagemErro("Selecione uma Série válida.");
			s = getGenericDAO().refresh(serie);
		}
		
		if(isFiltroLocal())
			local = this.getLocal();
		if(isFiltroTipo() && !isEmpty(this.getTipoTurma()) )
			tipo = this.getTipoTurma();
		if (isFiltroHorario()) {
			horario = this.getTurmaHorario();
		}
		
		if (isFiltroDiarioTurma()) {
			if (situacao == null || situacao != SituacaoTurma.CONSOLIDADA) {
				addMensagemErro("Situação: Só é possível emitir Diário de Classe das turmas Consolidadas. Por favor altere o valor do filtro 'Situação' para 'CONSOLIDADA' caso precise emitir o Diário de Classe.");
			}
			if (getUnidade().getId() != getUsuarioLogado().getVinculoAtivo().getUnidade().getId()) {
				addMensagemErro("Unidade: Só é possível emitir Diário de Classe do seu Departamento. Por favor altere o valor do filtro 'Unidade' para o seu Departamento caso precise emitir o Diário de Classe.");
			}
		}
		
		if (hasOnlyErrors())
			return null;
		
		if ((getTurmasEAD() || nivel == null) && u == null && codigo == null && codigoTurma == null
				&& nome == null && docente == null && situacao == null && ano == null && poloEscolhido == null 
				&& !isFiltroConvenio() && c == null && tipo == null && local == null && horario == null) {
			addMensagemErro("Por favor, escolha algum critério de busca.");
			return null;
		} else {
			TurmaMedioDao dao = getDAO(TurmaMedioDao.class);
			setUnidade(dao.refresh(getUnidade()));
			setCurso(dao.refresh(getCurso()));
			setSituacaoTurma(getSituacaoTurma());
			setPolo(dao.refresh(getPolo()));
			try {
				Collection<Curso> cursos = null;
				
				if(isFiltroCurso()) {
					if (c != null && c.isProbasica()) {
						cursos = new ArrayList<Curso>();
						cursos.add(c);
						c = null;
					}
				}
				
				setResultadosBusca(dao.findGeral(nivel, u, codigo, codigoTurma , nome, docente,
						(situacao != null ? new Integer[] { situacao } : null ) , ano, 0, poloEscolhido, cursos, 
						modalidade, c, s, null, local, tipo, horario,ordenarPor));
			} catch (LimiteResultadosException e) {
				addMensagemErro("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
				setResultadosBusca(new ArrayList<Turma>(0));
				return null;
			} catch (Exception e) {
				setResultadosBusca(new ArrayList<Turma>(0));
				return tratamentoErroPadrao(e);
			}
		}
		
		if( ValidatorUtil.isEmpty(getResultadosBusca()) )
			addMessage("Nenhuma turma encontrada de acordo com os critérios de busca informados.", TipoMensagemUFRN.WARNING);
		if(isFiltroRelatorio())
			return relatorio();

		return telaBuscaGeral();
	}
	
	/**
	 * Retorna uma lista de {@link SelectItem} com todas as possíveis opções de ordenação dos dados.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>sigaa.war/medio/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<SelectItem> getAllOpcoesOrdenacaoCombo() {
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(OrdemBuscaDisciplina.ORDENAR_POR_SERIE_TURMA.ordinal(), OrdemBuscaDisciplina.ORDENAR_POR_SERIE_TURMA.getLabel()));
		itens.add(new SelectItem(OrdemBuscaDisciplina.ORDENAR_POR_COMPONENTE_CURRICULAR.ordinal(), OrdemBuscaDisciplina.ORDENAR_POR_COMPONENTE_CURRICULAR.getLabel()));
		itens.add(new SelectItem(OrdemBuscaDisciplina.ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS.ordinal(), OrdemBuscaDisciplina.ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS.getLabel()));
		itens.add(new SelectItem(OrdemBuscaDisciplina.ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS.ordinal(), OrdemBuscaDisciplina.ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS.getLabel()));
		itens.add(new SelectItem(OrdemBuscaDisciplina.ORDENAR_POR_LOCAL.ordinal(), OrdemBuscaDisciplina.ORDENAR_POR_LOCAL.getLabel()));
		return itens;
	}
	
	/** 
	 * Carrega as séries pertencentes ao curso de ensino médio selecionado na jsp..
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso(ValueChangeEvent e) throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		
		CursoMedio cursoMedio = null;
		
		if( e != null && (Integer)e.getNewValue() > 0 )
			cursoMedio = dao.findByPrimaryKey((Integer)e.getNewValue(), CursoMedio.class);
		else {
			seriesByCurso = new ArrayList<SelectItem>(0);
			return;
		}	
		
		cursoMedio.setNivel(getNivelEnsino());
		if (cursoMedio != null){
			seriesByCurso = toSelectItems(dao.findByCurso(cursoMedio), "id", "descricaoCompleta");
			setSerie(new Serie(0));
		}	
	}
	
	/** Redireciona o usuário para o formulário de busca geral de turmas.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String telaBuscaGeral() {
		return forward(JSP_BUSCA_GERAL);
	}
	
	public Collection<SelectItem> getTiposTurmaCombo() {
		return tiposTurmaCombo;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public boolean isFiltroSerie() {
		return filtroSerie;
	}

	public void setFiltroSerie(boolean filtroSerie) {
		this.filtroSerie = filtroSerie;
	}

	public List<SelectItem> getSeriesByCurso() {
		return seriesByCurso;
	}

	public void setSeriesByCurso(List<SelectItem> seriesByCurso) {
		this.seriesByCurso = seriesByCurso;
	}
	
}
