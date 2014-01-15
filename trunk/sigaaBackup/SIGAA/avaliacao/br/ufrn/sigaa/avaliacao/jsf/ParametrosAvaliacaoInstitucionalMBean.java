/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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

/** Controller respons�vel pela edi��o dos par�metros utilizados na Avalia��o Institucional.
 * @author �dipo Elder F. Melo
 *
 */
@Component("parametrosAvaliacaoInstitucionalBean")
@Scope("request")
public class ParametrosAvaliacaoInstitucionalMBean extends SigaaAbstractController<Parametro> {
	
	// valores utilizados nos par�metros
	/** Indica se os docentes devem participar da avalia��o institucional no per�odo corrente.*/
	private boolean avaliacaoDocente;
	/** Indica se no relat�rio anal�tico do resultado da avalia��o ir� ser exibido os coment�rios ao docente da turma e de trancamentos.*/
	private boolean comentariosLiberados;
	/** ID do grupo de perguntas cuja a m�dia ser� considerada com m�dia geral do docente no Resultado da Avalia��o Instucional.*/ 
	private int idGrupo;
	
	/** Lista de par�metros a persistir. */
	private Collection<Parametro> listaParametros;
	/** Lista de perguntas selecionadas para o par�metro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}. */
	private String[] perguntasSelecionadas;
	/** Cole��o de perguntas que o usu�rio pode selecionar. */
	private Collection<SelectItem> perguntasCombo;

	/** Construtor padr�o. */
	public ParametrosAvaliacaoInstitucionalMBean() {
		init();
	}
	
	/** Inicializa os atributos do controller. */
	private void init() {
		this.listaParametros = null;
	}
	
	/**
	 * Inicia a edi��o de par�metros gerais da Avalia��o Institucional.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Inicia a edi��o do par�metro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Atualiza os par�metros da Avalia��o Institucional. <br>
	 * M�todo n�o invocado por JSP(s):
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (ValidatorUtil.isEmpty(listaParametros)) {
			addMensagemErro("Par�metros n�o informados.");
			return null;
		}
		for (Parametro parametro : listaParametros) {
			ParametroHelper.getInstance().atualizaParametro(getUsuarioLogado(), Sistema.SIGAA, parametro.getCodigo(), parametro.getValor());
		}
		addMensagem(OPERACAO_SUCESSO);
		return cancelar();
	}
	
	/**
	 * Atualiza os par�metros gerais da Avalia��o Institucional. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
		ValidatorUtil.validateRequired(this.idGrupo, "Grupo de Perguntas (Dimens�o)", erros);

		if (hasErrors()) {
			return null;
		}
		// par�metros
		ParametroDao dao = getDAO(ParametroDao.class);
		Parametro avaliacaoDocente = dao.findByPrimaryKey(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
		Parametro comentariosLiberados = dao.findByPrimaryKey(ParametrosAvaliacaoInstitucional.INCLUIR_COMENTARIOS_RELATORIO_ANALITICO);
		Parametro grupo = dao.findByPrimaryKey(ParametrosAvaliacaoInstitucional.ID_GRUPO_PERGUNTAS_MEDIA_GERAL_RESULTADO_AVALIACAO);
		// valores
		avaliacaoDocente.setValor(String.valueOf(this.avaliacaoDocente));
		comentariosLiberados.setValor(String.valueOf(this.comentariosLiberados));
		grupo.setValor(String.valueOf(idGrupo));
		// lista de par�metros a persistir
		listaParametros = new ArrayList<Parametro>();
		listaParametros.add(avaliacaoDocente);
		listaParametros.add(comentariosLiberados);
		listaParametros.add(grupo);
		return cadastrar();
	}
	
	/**
	 * Atualiza o par�metro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Redireciona o usu�rio para o formul�rio de par�metros gerais.<br>
	 * M�todo n�o invocado por JSP(s):
	 * @return
	 */
	public String formParametrosGerais() {
		return forward("/avaliacao/parametros/parametros_gerais.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de edi��o do par�metro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}.<br>
	 * M�todo n�o invocado por JSP(s):
	 * @return
	 */
	public String formPerguntasFixasConsultaDiscente() {
		return forward("/avaliacao/parametros/perguntas_fixas_consulta_discente.jsp");
	}

	/** Indica se os docentes devem participar da avalia��o institucional no per�odo corrente.
	 * @return
	 */
	public boolean isAvaliacaoDocente() {
		return avaliacaoDocente;
	}

	/** Seta se os docentes devem participar da avalia��o institucional no per�odo corrente.
	 * @param avaliacaoDocente
	 */
	public void setAvaliacaoDocente(boolean avaliacaoDocente) {
		this.avaliacaoDocente = avaliacaoDocente;
	}

	/** Retorna a lista de perguntas selecionadas para o par�metro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}.
	 * @return
	 */
	public String[] getPerguntasSelecionadas() {
		return perguntasSelecionadas;
	}

	/** Seta a lista de perguntas selecionadas para o par�metro {@link ParametrosAvaliacaoInstitucional#ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO}.
	 * @param perguntasSelecionadas
	 */
	public void setPerguntasSelecionadas(String[] perguntasSelecionadas) {
		this.perguntasSelecionadas = perguntasSelecionadas;
	}

	/** Indica se no relat�rio anal�tico do resultado da avalia��o ir� ser exibido os coment�rios ao docente da turma e de trancamentos.
	 * @return
	 */
	public boolean isComentariosLiberados() {
		return comentariosLiberados;
	}

	/** Seta se no relat�rio anal�tico do resultado da avalia��o ir� ser exibido os coment�rios ao docente da turma e de trancamentos.
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
	 * Retorna uma cole��o de SelectItem de Perguntas utilizadas na Avalia��o
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
						// retirando a tag <br/> que est� cadastrada no banco.
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
