/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/05/2010
 * 
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Parametro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroDao;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.Pergunta;
import br.ufrn.sigaa.avaliacao.dominio.TipoAvaliacaoInstitucional;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;

/** Controller responsável pela edição dos parâmetros utilizados na Avaliação Institucional.
 * @author Édipo Elder F. Melo
 *
 */
@Component("parametrosAvaliacaoInstitucionalBean")
@Scope("request")
public class ParametrosAvaliacaoInstitucionalMBean extends SigaaAbstractController<Parametro> {
	
	// valores utilizados nos parâmetros
	/** Indica se os docentes devem participar da avaliação institucional no período corrente.*/
	private boolean avaliacaoDocente;
	/** Indica se no relatório analítico do resultado da avaliação irá ser exibido os comentários ao docente da turma e de trancamentos.*/
	private boolean comentariosLiberados;
	/** ID do grupo de perguntas cuja a média será considerada com média geral do docente no Resultado da Avaliação Instucional.*/ 
	private int idGrupo;
	
	/** Lista de parâmetros a persistir. */
	private Collection<Parametro> listaParametros;
	/** Lista de perguntas selecionadas para o parâmetro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}. */
	private String[] perguntasSelecionadas;
	/** Coleção de perguntas que o usuário pode selecionar. */
	private Collection<SelectItem> perguntasCombo;

	/** Construtor padrão. */
	public ParametrosAvaliacaoInstitucionalMBean() {
		init();
	}
	
	/** Inicializa os atributos do controller. */
	private void init() {
		this.listaParametros = null;
	}
	
	/**
	 * Inicia a edição de parâmetros gerais da Avaliação Institucional.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarParametrosGerais(){
		init();
		this.avaliacaoDocente = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
		this.comentariosLiberados = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.INCLUIR_COMENTARIOS_RELATORIO_ANALITICO);
		this.idGrupo = ParametroHelper.getInstance().getParametroInt(ParametrosAvaliacaoInstitucional.ID_GRUPO_PERGUNTAS_MEDIA_GERAL_RESULTADO_AVALIACAO);
		return formParametrosGerais();
	}
	
	/**
	 * Inicia a edição do parâmetro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarPerguntasFixasConsultaDiscente(){
		init();
		perguntasSelecionadas = ParametroHelper.getInstance().getParametroStringArray(ParametrosAvaliacaoInstitucional.ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO);
		for (int i = 0; i < perguntasSelecionadas.length; i++)
			perguntasSelecionadas[i] = perguntasSelecionadas[i].trim();
		return formPerguntasFixasConsultaDiscente();
	}

	/**
	 * Atualiza os parâmetros da Avaliação Institucional. <br>
	 * Método não invocado por JSP(s):
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (ValidatorUtil.isEmpty(listaParametros)) {
			addMensagemErro("Parâmetros não informados.");
			return null;
		}
		for (Parametro parametro : listaParametros) {
			ParametroHelper.getInstance().atualizaParametro(getUsuarioLogado(), Sistema.SIGAA, parametro.getCodigo(), parametro.getValor());
		}
		addMensagem(OPERACAO_SUCESSO);
		return cancelar();
	}
	
	/**
	 * Atualiza os parâmetros gerais da Avaliação Institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/parametros/parametros_gerais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarParametrosGerais() throws SegurancaException, ArqException, NegocioException {
		ValidatorUtil.validateRequired(this.idGrupo, "Grupo de Perguntas (Dimensão)", erros);

		if (hasErrors()) {
			return null;
		}
		// parâmetros
		ParametroDao dao = getDAO(ParametroDao.class);
		Parametro avaliacaoDocente = dao.findByPrimaryKey(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
		Parametro comentariosLiberados = dao.findByPrimaryKey(ParametrosAvaliacaoInstitucional.INCLUIR_COMENTARIOS_RELATORIO_ANALITICO);
		Parametro grupo = dao.findByPrimaryKey(ParametrosAvaliacaoInstitucional.ID_GRUPO_PERGUNTAS_MEDIA_GERAL_RESULTADO_AVALIACAO);
		// valores
		avaliacaoDocente.setValor(String.valueOf(this.avaliacaoDocente));
		comentariosLiberados.setValor(String.valueOf(this.comentariosLiberados));
		grupo.setValor(String.valueOf(idGrupo));
		// lista de parâmetros a persistir
		listaParametros = new ArrayList<Parametro>();
		listaParametros.add(avaliacaoDocente);
		listaParametros.add(comentariosLiberados);
		listaParametros.add(grupo);
		return cadastrar();
	}
	
	/**
	 * Atualiza o parâmetro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/parametros/perguntas_fixas_consulta_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarPerguntasFixas() throws SegurancaException, ArqException, NegocioException {
		ParametroDao dao = getDAO(ParametroDao.class);
		Parametro perguntasFixas = dao.findByPrimaryKey(ParametrosAvaliacaoInstitucional.ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO);
		StringBuilder ids = new StringBuilder();
		if (!ValidatorUtil.isEmpty(perguntasSelecionadas)) {
			for (String id : perguntasSelecionadas) {
				if (ids.length() > 0) ids.append(",");
				ids.append(id);
			}
		}
		perguntasFixas.setValor(ids.toString());
		listaParametros = new ArrayList<Parametro>();
		listaParametros.add(perguntasFixas);

		return cadastrar();
	}
	
	/** Redireciona o usuário para o formulário de parâmetros gerais.<br>
	 * Método não invocado por JSP(s):
	 * @return
	 */
	public String formParametrosGerais() {
		return forward("/avaliacao/parametros/parametros_gerais.jsp");
	}
	
	/** Redireciona o usuário para o formulário de edição do parâmetro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}.<br>
	 * Método não invocado por JSP(s):
	 * @return
	 */
	public String formPerguntasFixasConsultaDiscente() {
		return forward("/avaliacao/parametros/perguntas_fixas_consulta_discente.jsp");
	}

	/** Indica se os docentes devem participar da avaliação institucional no período corrente.
	 * @return
	 */
	public boolean isAvaliacaoDocente() {
		return avaliacaoDocente;
	}

	/** Seta se os docentes devem participar da avaliação institucional no período corrente.
	 * @param avaliacaoDocente
	 */
	public void setAvaliacaoDocente(boolean avaliacaoDocente) {
		this.avaliacaoDocente = avaliacaoDocente;
	}

	/** Retorna a lista de perguntas selecionadas para o parâmetro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}.
	 * @return
	 */
	public String[] getPerguntasSelecionadas() {
		return perguntasSelecionadas;
	}

	/** Seta a lista de perguntas selecionadas para o parâmetro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}.
	 * @param perguntasSelecionadas
	 */
	public void setPerguntasSelecionadas(String[] perguntasSelecionadas) {
		this.perguntasSelecionadas = perguntasSelecionadas;
	}

	/** Indica se no relatório analítico do resultado da avaliação irá ser exibido os comentários ao docente da turma e de trancamentos.
	 * @return
	 */
	public boolean isComentariosLiberados() {
		return comentariosLiberados;
	}

	/** Seta se no relatório analítico do resultado da avaliação irá ser exibido os comentários ao docente da turma e de trancamentos.
	 * @param comentariosLiberados
	 */
	public void setComentariosLiberados(boolean comentariosLiberados) {
		this.comentariosLiberados = comentariosLiberados;
	}

	public int getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}

	/**
	 * Retorna uma coleção de SelectItem de Perguntas utilizadas na Avaliação
	 * Institucional.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPerguntasComboBox() throws HibernateException, DAOException {
		if (perguntasCombo == null) {
			perguntasCombo = new ArrayList<SelectItem>();
			int ordemGrupo = 1;
			GenericDAO dao = getGenericDAO();
			String fields[] = {"tipoAvaliacao", "ead"};
			Object values[] = {TipoAvaliacaoInstitucional.AVALIACAO_DISCENTE_GRADUACAO, false};
			Collection<FormularioAvaliacaoInstitucional> formularios = dao.findByExactField(FormularioAvaliacaoInstitucional.class, fields, values);
			if (formularios != null) {
				for (FormularioAvaliacaoInstitucional form : formularios)
					for (GrupoPerguntas grupo : form.getGrupoPerguntas()) {
						int ordemPergunta = 1;
						// retirando a tag <br/> que está cadastrada no banco.
						for (Pergunta pergunta : grupo.getPerguntas()) {
							if (pergunta.isNota()) {
								pergunta.setDescricao(pergunta.getDescricao().replaceAll("<br/>", "").replaceAll("<strong>", "").replaceAll("</strong>", ""));
								perguntasCombo.add(new SelectItem(new Integer(pergunta.getId()), 
										ordemGrupo+"."+ordemPergunta + " - " + pergunta.getDescricao()));
								ordemPergunta++;
							}
						}
						ordemGrupo++;
					}
			}
		}
		return perguntasCombo;
	}
}
