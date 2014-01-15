/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/08/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dao.CalendarioEnadeDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.CalendarioEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.CursoGrauAcademicoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ParticipacaoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoENADE;

/**
 * Controller responsável pelo cadastro do calendário de cursos a serem
 * avaliados pelo ENADE.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Component("calendarioEnadeMBean")
@Scope("request")
public class CalendarioEnadeMBean extends SigaaAbstractController<CalendarioEnade> {

	/** Curso a ser incluído no calendário do ENADE. */ 
	private Curso curso;
	/** Coleção de calendários cadastrados para um determinado tipo de ENADE (Ingressante / Concluínte), para importação de cursos ao calendário a ser cadastrado. */
	private Collection<CalendarioEnade> outrosCalendarios;
	/** Filtra a lista de calendários cadastrados por ano. */
	private int ano;
	/** Filtra a lista de calendários cadastrados por tipo de ingresso. */
	private String tipoEnade;
	/** Coleção de SelectItem com todos calendários cadastrados. */
	private Collection<SelectItem> allCombo;
	/** ID da Matriz Curricular onde o serão captados o curso e o grau acadêmico. */
	private int idMatriz;
	
	/** Construtor padrão. */
	public CalendarioEnadeMBean() {
		obj = new CalendarioEnade();
		this.curso = new Curso();
		this.all = null;
		this.outrosCalendarios = null;
	}
	
	/**
	 * Importa os cursos de outro calendário de ENADE para o calendário
	 * atualmente sendo cadastrado. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/copiar_ano.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String importarCursos() throws DAOException {
		int id = getParameterInt("id");
		CalendarioEnade anterior = getGenericDAO().findByPrimaryKey(id, CalendarioEnade.class);
		if (anterior != null) {
			obj.setCursosGrauAcademico(new TreeSet<CursoGrauAcademicoEnade>());
			for (CursoGrauAcademicoEnade cga : anterior.getCursosGrauAcademico()) {
				cga.setCalendarioEnade(obj);
				cga.setId(0);
				obj.getCursosGrauAcademico().add(cga);
			}
		}
		return forward( getFormPage() );
	}
	
	/** Retorna o link para a página ao qual o usuário será redirecionado após cadastrar o calendário.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	/**
	 * Inclui um curso no calendário de ENADE. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void adicionarCurso() throws DAOException {
		MatrizCurricular matriz = getGenericDAO().findByPrimaryKey(idMatriz, MatrizCurricular.class);
		if( isEmpty(idMatriz) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		else{
			CursoGrauAcademicoEnade cursoGrau = new CursoGrauAcademicoEnade();
			cursoGrau.setCurso(matriz.getCurso());
			cursoGrau.setGrauAcademico(matriz.getGrauAcademico());
			if (obj.addCursoGrauAcademico(cursoGrau))
				addMensagemInformation("Curso adicionado com Sucesso!");
			else
				addMensagemErro("Curso já adicionado à lista!");
		}
		curso = new Curso();
		idMatriz = 0;
	}
	
	/**
	 * Remove um curso no calendário de ENADE. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public String removerCurso() throws DAOException {
		boolean removido = false;
		int id = getParameterInt("id",0);
				
		Iterator<CursoGrauAcademicoEnade> iterator = obj.getCursosGrauAcademico().iterator();
		while (iterator.hasNext() && !removido) {
			if (iterator.next().getId() == id) {
				iterator.remove();
				removido = true;
			}
		}
		if (removido)
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "curso");
		else
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "curso","removido");
		
		return forward( getFormPage() );
	}
	
	/** Retorna o link para a página de listagem de calendários ENADE.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/graduacao/enade/calendario/lista.jsp";
	}
	
	/** Retorna o link para o formulário de cadastro de calendários ENADE.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/graduacao/enade/calendario/form.jsp";
	}
	
	/**
	 * Retorna o link para página de cópia do calendário do ENADE.
	 * @return
	 */
	public String getFormCopia() {
		return "/graduacao/enade/calendario/copiar_ano.jsp";
	}

	/** Rrtorna o curso a ser incluído no calendário do ENADE.
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso a ser incluído no calendário do ENADE.
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	
	/**
	 * Cadastra/altera um calendário ENADE.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String atualizar() throws ArqException {
		outrosCalendarios = null;
		int ano = getParameterInt("ano", 0);
		TipoENADE tipoEnade = TipoENADE.values()[getParameterInt("tipo")];
		String[] fields = {"ano","tipoEnade"};
		Object[] values = {ano,tipoEnade.ordinal()};
		
		Collection<CalendarioEnade> calendarioEnade = getGenericDAO().
			findByExactField(CalendarioEnade.class, fields , values);
		if(isEmpty(calendarioEnade)) {
			setConfirmButton("Cadastrar");
			obj = new CalendarioEnade();
			obj.setAno(ano);
			obj.setTipoEnade(tipoEnade);
			setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
			prepareMovimento(ArqListaComando.CADASTRAR);
			this.outrosCalendarios = null;
			if (!isEmpty(getOutrosCalendarios()))
				return forward( getFormCopia() );
			else
				return forward( getFormPage() );
		} else {
			obj = calendarioEnade.iterator().next();
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);
			setConfirmButton("Alterar");
			return forward( getFormPage() );
		}
	}
	
	/**
	 * Informa para onde o usário deverá ser redirecionado após
	 * atualizar/cadastrar o calendário ENADE. <br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterAtualizar()
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		all = null;
		listar();
	}
	
	/**
	 * Retorna uma coleção de calendários cadastrados para um determinado tipo
	 * de ENADE (Ingressante / Concluínte), para importação de cursos ao
	 * calendário a ser cadastrado.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<CalendarioEnade> getOutrosCalendarios() throws DAOException {
		if (outrosCalendarios == null) {
			String[] fields = {"tipoEnade"};
			if( tipoEnade == null )
				tipoEnade = obj.getTipoEnade().name();
			Object[] values = { TipoENADE.valueOf(tipoEnade).getOrdinal()};
			List<CalendarioEnade> anteriores = (List<CalendarioEnade>) 
				getGenericDAO().findByExactField(CalendarioEnade.class, fields , values);
			if (anteriores != null) {
				// ordena por ano-período, do maior para o menor
				Collections.sort(anteriores, new Comparator<CalendarioEnade>() {
					@Override
					public int compare(CalendarioEnade o1, CalendarioEnade o2) {
						return o2.getAno() - o1.getAno();
					}
				});
			}
			outrosCalendarios = anteriores;
		}
		return outrosCalendarios;
	}
	
	/**
	 * Atualiza a lista de outros questionário de acordo com {@link TipoENADE} selecionado.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/copiar_ano.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String atualizarOutrosCalendarios(ValueChangeEvent evt) throws DAOException{
		tipoEnade = (String) evt.getNewValue();
		
		if( isEmpty(tipoEnade) ){
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA,"Tipo");
			return null;
		}	
		outrosCalendarios = null;
		getOutrosCalendarios();
		if (!isEmpty(getOutrosCalendarios()))
			return forward( getFormCopia() );
		else
			return forward( getFormPage() );
	}
	
	/**
	 * Retorna para tela de copia de calendário.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String voltar() throws DAOException{
		if ( isEmpty(obj) )
			return forward( getFormCopia() );
		else
			return forward( getListPage() );
	}
	
	/**
	 * Retorna uma coleção de todos calendários cadastrados, incluindo anos que
	 * não foram cadastrados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		all = null;
		return forward(getListPage());
	}
	
	/**
	 * Retorna uma coleção de todos calendários cadastrados, incluindo anos que
	 * não foram cadastrados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	public Collection<CalendarioEnade> getAll() throws DAOException {
		if (all == null) {
			List<CalendarioEnade> all = new ArrayList<CalendarioEnade>();
			TipoENADE filtraTipo = null;
			if (!isEmpty(tipoEnade))
				filtraTipo = TipoENADE.valueOf(tipoEnade);
			Collection<CalendarioEnade> cadastrados = getDAO(CalendarioEnadeDao.class).findAll(getPaginacao(), ano, filtraTipo);
			if (!isEmpty(cadastrados))
				all.addAll(cadastrados);
			// inclui outros anos
			for (TipoENADE tipo : TipoENADE.values()) {
				int anoInicio = ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE > ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE ? 
						ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE : ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE;
				while (anoInicio <= getCalendarioVigente().getAno() + 1) {
					boolean  incluir = true;
					for (CalendarioEnade existente : all) {
						if (existente.getAno() == anoInicio && existente.getTipoEnade().equals(tipo) ) {
							incluir = false;
							break;
						}
					}
					if (incluir) {
						if (ano == 0 && filtraTipo == tipo || 
								filtraTipo == null && ano == anoInicio ||
								filtraTipo == tipo && ano == anoInicio ||
								filtraTipo == null && ano == 0) {
							CalendarioEnade pseudo = new CalendarioEnade();
							pseudo.setAno(anoInicio);
							pseudo.setTipoEnade(tipo);
							all.add(pseudo);
						}
					}
					anoInicio++;
				}
			}
			// ordena por ano-período, do maior para o menor
			Collections.sort(all, new Comparator<CalendarioEnade>() {
				@Override
				public int compare(CalendarioEnade o1, CalendarioEnade o2) {
					int cmp = o2.getAno() - o1.getAno();
					if (cmp == 0) cmp = o2.getTipoEnade().ordinal() - o1.getTipoEnade().ordinal();
					return cmp;
				}
			});
			this.all=all;
		}
		return all;
	}

	/** Cadastra/altera o calendário de aplicação do ENADE.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		erros.addAll(obj.validate());
		if (hasErrors())
			return forward(getFormPage());
		else
			return super.cadastrar();
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano= ano;
	}

	public String getTipoEnade() {
		return tipoEnade;
	}

	public void setTipoEnade(String tipoEnade) {
		this.tipoEnade = tipoEnade;
	}
	
	/**
	 * Retorna uma coleção de SelectItem com todos anos dos "/graduacao/enade/calendario/copiar_ano.jsp"calendários, incluindo anos que
	 * não foram cadastrados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAnoCombo() throws DAOException{
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		int anoInicio = ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE > ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE ? 
				ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE : ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE;
		int anoAtual = getCalendarioVigente().getAno() + 1;
		while (anoInicio <= anoAtual) {
			lista.add(new SelectItem(anoAtual, String.valueOf(anoAtual)));
			anoAtual--;
		}
		return lista;
	}
	
	/**
	 * Retorna uma coleção de SelectItem com todos calendários.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	public Collection<SelectItem> getAllCombo() throws DAOException {
		if (allCombo == null) {
			allCombo = new ArrayList<SelectItem>();
			Collection<CalendarioEnade> cadastrados = getDAO(CalendarioEnadeDao.class).findAll(CalendarioEnade.class, "ano", "desc");
			if (!isEmpty(cadastrados)) {
				for (CalendarioEnade cal : cadastrados) {
					allCombo.add(new SelectItem(cal.getId(), cal.getAno() + " - " + cal.getTipoEnade()));
				}
			}
		}
		return allCombo;
	}
	
	/**
	 * Retorna a coleção dos tipos de calendário ENADE.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/enade/calendario/form.jsp</li>
	 * </ul>
	 * 
	 */
	public Collection<SelectItem> getTipoCombo() throws DAOException {
		
		Collection<SelectItem> comboTipo = new ArrayList<SelectItem>();
		
		comboTipo.add( new SelectItem(TipoENADE.INGRESSANTE, TipoENADE.INGRESSANTE.toString()) );
		comboTipo.add( new SelectItem(TipoENADE.CONCLUINTE, TipoENADE.CONCLUINTE.toString()) );
		
		return comboTipo;
	}
	
	
	/**
	 * Método que possibilitar a criação de autocompletes de Curso. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /sigaa.war/graduacao/enade/calendario/form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> autocompleteNomeCurso(Object event) throws DAOException {
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		String nome = event.toString();
		Map<String, MatrizCurricular> cursoModalidade = new HashMap<String, MatrizCurricular>();
		for (MatrizCurricular matriz : dao.findByCurso(nome)) {
			String key = "C" + curso.getId()+"_G"+matriz.getGrauAcademico().getId();
			if (!cursoModalidade.containsKey(key))
				cursoModalidade.put(key, matriz);
		}
		return CollectionUtils.toList(cursoModalidade.values());  
	}

	public int getIdMatriz() {
		return idMatriz;
	}

	public void setIdMatriz(int idMatriz) {
		this.idMatriz = idMatriz;
	}
	
}