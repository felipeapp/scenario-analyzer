/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '16/11/2009'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoSituacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaConsultaCursoGeral;

/**
 * Controlador responsável pela operação de Aprovar Proposta de Cursos Lato.
 * 
 * @author daniel
 *
 */

@Scope("session")
@Component("aprovarPropostaLato")
public class AprovarPropostaCursoLatoMBean extends SigaaAbstractController<HistoricoSituacao> {
	
	/** Armazena os cursos onde suas propostas foram submetidas. */
	private Collection<LinhaConsultaCursoGeral> propostasSubmetidas;
	
	/** Curso lato sensu da proposta. */
	private CursoLato curso;
	
	public AprovarPropostaCursoLatoMBean(){
		initMBean();
	}
	
	/**
	 * Inicializa os atributos que serão utilizados na operação.
	 */
	private void initMBean() {
		obj = new HistoricoSituacao();
		obj.setProposta(new PropostaCursoLato());
	}
	
	/**
	 * Carrega todos os cursos Lato que possuem a proposta já submetida.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/curso.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String carregarPropostasSubmetidas() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_LATO);
		setPropostasSubmetidas(getDAO(CursoLatoDao.class).
				filter(null, null, null, new SituacaoProposta(SituacaoProposta.SUBMETIDA), null, true));
		return forward("/lato/aprovar_proposta_curso/lista.jsp");
	}
	
	/**
	 * Inicia o caso de uso de aprovação de uma Proposta do Curso Lato.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/aprovar_proposta_curso/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String preAprovar() throws ArqException {
		int id = getParameterInt("id");
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		obj.setProposta(dao.findPropostaByCurso(id));
		curso = dao.findById(id);
		GenericDAO comumDao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			if (curso.getIdConfiguracaoGRUInscricao() != null) {
				ConfiguracaoGRU config = comumDao.findAndFetch(curso.getIdConfiguracaoGRUInscricao(), ConfiguracaoGRU.class, "grupoEmissaoGRU");
				curso.setConfiguracaoGRUInscricao(config);
			}
			if (curso.getIdConfiguracaoGRUMensalidade() != null) {
				ConfiguracaoGRU config = comumDao.findByPrimaryKey(curso.getIdConfiguracaoGRUMensalidade(), ConfiguracaoGRU.class);
				curso.setConfiguracaoGRUMensalidade(config);
			}
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			comumDao.close();
		}
		prepareMovimento(SigaaListaComando.GRAVAR_HISTORICO_SITUACAO);
		return forward("/lato/aprovar_proposta_curso/form.jsp");
	}
	
	/**
	 * Responsável por aprovar uma Proposta do Curso Lato.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/aprovar_proposta_curso/form.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String aprovar() throws ArqException {
		ListaMensagens erros = new ListaMensagens();
		if (obj.getProposta().getAnoPortaria() == null || obj.getProposta().getAnoPortaria() == 0) 
			erros.addErro("Ano: campo obrigatório ou inválido");
		
		if (obj.getProposta().getNumeroPortaria() == null || obj.getProposta().getNumeroPortaria() == 0) 
			erros.addErro("Número: campo obrigatório ou inválido");
		
		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}

		obj.setObservacoes("Informação da portaria de aprovação do curso.");
		obj.setSituacao(new SituacaoProposta(SituacaoProposta.ACEITA));
		obj.getProposta().getSituacaoProposta().setId(SituacaoProposta.ACEITA);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.GRAVAR_HISTORICO_SITUACAO);

		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
			initMBean();
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		return carregarPropostasSubmetidas();
	}
	
	/**
	 * Responsável por cancelar a operação de aprovar Proposta do Curso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/aprovar_proposta_curso/form.jsp</li>
	 *	</ul>
	 */
	public String cancelar() {
		return redirect("/ensino/menus/menu_lato.jsf");
	}

	public Collection<LinhaConsultaCursoGeral> getPropostasSubmetidas() {
		return propostasSubmetidas;
	}

	public void setPropostasSubmetidas(
			Collection<LinhaConsultaCursoGeral> propostasSubmetidas) {
		this.propostasSubmetidas = propostasSubmetidas;
	}

	public CursoLato getCurso() {
		return curso;
	}

	public void setCurso(CursoLato curso) {
		this.curso = curso;
	}
	
}