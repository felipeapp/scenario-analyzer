/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/09/2008'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;

/**
 * Controlador responsável pela prorrogação de prazos de cursos de especialização
 * 
 * @author Leonardo Campos
 *
 */
@Component("prorrogacaoLatoBean") @Scope("request")
public class ProrrogacaoPrazoCursoLatoMBean extends SigaaAbstractController {

	private CursoLato curso;
	
	private CursoLato cursoOld;
	
	private Date dataFim;
	
	private String justificativa;
	
	public ProrrogacaoPrazoCursoLatoMBean() {
		clear();
	}

	private void clear() {
		curso = new CursoLato();
		cursoOld = new CursoLato();
		dataFim = null;
		justificativa = "";
	}
	
	/**
	 * Inicia a operação de prorrogação de prazo de um curso lato sensu
	 * JSP: /WEB-INF/jsp/ensino/latosensu/menu/curso.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_LATO);
		prepareMovimento(SigaaListaComando.PRORROGAR_PRAZO_CURSO_LATO);
		return forward("/lato/prorrogacao_prazo/form.jsf");
	}
	
	/**
	 * Inicia uma solicitação de prorrogação de prazo para um curso lato sensu
	 * JSP: /lato/menu_coordenador.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarSolicitacao() throws ArqException{
		checkRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO);
		curso = getGenericDAO().findByPrimaryKey(getCursoAtualCoordenacao().getId(), CursoLato.class);
		if(curso.getJustificativa() != null){
			addMensagemErro("Você já solicitou prorrogação de prazo para este curso.");
			return null;
		}
		setOperacaoAtiva(SigaaListaComando.SOLICITAR_PRORROGACAO_PRAZO_CURSO_LATO.getId());
		prepareMovimento(SigaaListaComando.SOLICITAR_PRORROGACAO_PRAZO_CURSO_LATO);
		return forward("/lato/prorrogacao_prazo/solicitacao.jsf");
	}
	
	/**
	 * Chama o processador para prorrogar o prazo de um curso lato sensu
	 * JSP: /lato/prorrogacao_prazo/form.jsp
	 * @return
	 * @throws ArqException
	 */
	public String prorrogar() throws ArqException {
		
		erros = new ListaMensagens();
		ValidatorUtil.validateRequiredId(curso.getId(), "Curso", erros);
		ValidatorUtil.validateRequired(cursoOld.getDataFim(), "Novo Prazo Final", erros);
		
		if(hasErrors()){
			addMensagens(erros.getMensagens());
			return null;
		}
		
		//Obs. Tentei usar findbByPrimaryKey mas trazia o dado errado. Falei para David. Sendo assim criei método no DAO.
		//cursoBD = getGenericDAO().findByPrimaryKey(curso.getId(), CursoLato.class);
		if(CalendarUtils.calculoDias(curso.getDataFim(), cursoOld.getDataFim()) < 0 || curso.getDataFim().equals(cursoOld.getDataFim()))
			erros.addErro("O Novo Prazo Final deve ser posterior ao atual: "+ 
					Formatador.getInstance().formatarData(cursoOld.getDataFim()));
		
		// Não deve permitir que o período total do curso ultrapasse 18 meses (Cf. Resolução)
		Date prazoMaximo = CalendarUtils.somaMeses(curso.getDataInicio(), 18);
		if(CalendarUtils.calculoDias(prazoMaximo, cursoOld.getDataFim()) > 0)
			erros.addErro("O prazo máximo de duração de um curso de especialização é de 18 meses ("+ getPrazoMax() +").");
		
		if(hasErrors()){
			addMensagens(erros.getMensagens());
			return null;
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.PRORROGAR_PRAZO_CURSO_LATO);
		curso.setDataFim(cursoOld.getDataFim());
		mov.setObjMovimentado(curso);
		
		try {
			executeWithoutClosingSession(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		addMensagemInformation("Prazo final de curso alterado com sucesso!");
		clear();
		
		return "menuLato";
	}
	
	/**
	 * Chama o processador para registrar uma solicitação de prorrogação de prazo de um curso lato sensu
	 * JSP: /lato/prorrogacao_prazo/solicitacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String solicitar() throws ArqException {
		if(!checkOperacaoAtiva(SigaaListaComando.SOLICITAR_PRORROGACAO_PRAZO_CURSO_LATO.getId()))
			return cancelar();
		
		erros = new ListaMensagens();
		ValidatorUtil.validaData(getDataFim(), "Novo Prazo Final", erros);
		ValidatorUtil.validateRequired(getJustificativa(), "Justificativa", erros);
		CursoLato cursoBD = getGenericDAO().findByPrimaryKey(curso.getId(), CursoLato.class);
		
		if(hasErrors()){
			addMensagens(erros.getMensagens());
			curso = cursoBD;
			return null;
		}
		
		if(getDataFim().before(cursoBD.getDataFim()) || getDataFim().equals(cursoBD.getDataFim())){
			curso = cursoBD;
			erros.addErro("O Novo Prazo Final deve ser posterior ao atual: "+ 
					Formatador.getInstance().formatarData(cursoBD.getDataFim()));
		}
		
		// O período total do curso não pode ultrapassar 18 meses (Cf. Resolução)
		if(getDataFim().after(CalendarUtils.somaMeses(cursoBD.getDataInicio(), 18))){
			curso = cursoBD;
			erros.addErro("O prazo máximo de duração de um curso de especialização " +
					"é de 18 meses. <br/>("+ getPrazoMax() +")");
		}
		
		if(hasErrors()){
			addMensagens(erros.getMensagens());
			return null;
		}
		
		curso = cursoBD;
		curso.setDataFim(getDataFim());
		curso.setJustificativa(getJustificativa());
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.SOLICITAR_PRORROGACAO_PRAZO_CURSO_LATO);
		mov.setObjMovimentado(curso);
		
		try {
			executeWithoutClosingSession(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			curso = cursoBD;
			return null;
		}
		
		addMensagemInformation("Solicitação submetida com sucesso!");
		clear();
		
		return cancelar();
	}
	
	/**
	 * Carregar informações do curso selecionado na view
	 * JSP: /lato/prorrogacao_prazo/form.jsp
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarInfoCurso(ValueChangeEvent evt) throws DAOException {
		Integer id = (Integer) evt.getNewValue();
		
		if(id != null && id > 0){
			curso = getGenericDAO().findByPrimaryKey(id, CursoLato.class);
		}
	}
	
	/**
	 * Retorna a data limite que um curso de lato sensu pode ter
	 * para respeitar os 18 meses definidos na resolução.
	 * JSPs: /lato/prorrogacao_prazo/form.jsp
	 * 	/lato/prorrogacao_prazo/solicitacao.jsp
	 * @return
	 */
	public String getPrazoMax(){
		if(curso != null && curso.getDataInicio() != null){
			return "Prazo máx.: " + Formatador.getInstance().formatarData(CalendarUtils.somaMeses(curso.getDataInicio(), 18));
		}else
			return "";
	}
	
	public CursoLato getCurso() {
		return curso;
	}

	public void setCurso(CursoLato curso) {
		this.curso = curso;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public CursoLato getCursoOld() {
		return cursoOld;
	}

	public void setCursoOld(CursoLato cursoOld) {
		this.cursoOld = cursoOld;
	}

}
