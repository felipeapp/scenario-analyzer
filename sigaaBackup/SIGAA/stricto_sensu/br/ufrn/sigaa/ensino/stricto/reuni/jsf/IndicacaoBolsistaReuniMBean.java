/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/02/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.PlanoDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.IndicacaoBolsistaReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.IndicacaoBolsistaReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PeriodoIndicacaoReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PlanoTrabalhoReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.negocio.MovimentoIndicacaoBolsistaReuni;

/**
 * ManagedBean utilizado para o cadastro de indicação de bolsistas REUNI a um plano de trabalho.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("indicacaoBolsistaReuni") 
@Scope("session")
public class IndicacaoBolsistaReuniMBean extends SigaaAbstractController<IndicacaoBolsistaReuni> implements OperadorDiscente {

	/** Plano de trabalho selecionado para o Discente */
	private PlanoTrabalhoReuni planoTrabalhoReuni = new PlanoTrabalhoReuni();
	
	/** Período que auxilia na geração do Período de duração da bolsa REUNI */
	private int anoPeriodo;
	
	/** Discente que será indicado. */
	private DiscenteStricto discente;
	
	/** Comando que será enviado ao processador */
	private Comando comando = null;
	
	/** Construtor da Classe */
	public IndicacaoBolsistaReuniMBean() {
		
	}
	
	/**
	 * Inicia o cadastro de indicação de bolsista REUNI.<br/>
	 * Método não chamado por JSPs.
	 */
	private String iniciar(boolean cadastrar) throws ArqException{		
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG);
			
		clear();
			
		IndicacaoBolsistaReuniDao dao = getDAO(IndicacaoBolsistaReuniDao.class);
		try{
			obj = dao.findByPlanoTrabalho(planoTrabalhoReuni);
			if (cadastrar){
				if( !isEmpty( obj ) ){
					addMensagemErro( "O Plano de trabalho informado já possui uma indicação." );
					return null;
				}else {
					obj = new IndicacaoBolsistaReuni();
				}
				obj.setPlanoTrabalho(planoTrabalhoReuni);
			} else {
				if (obj == null){
					addMensagemErro( "O Plano de trabalho selecionado não possui uma indicação para ser alterada." );
					return null;
				}					
				discente = obj.getDiscente();				
			}
			if (cadastrar){
				setConfirmButton("Cadastrar");
				return informaDiscente();						
			}
			else {
				setConfirmButton("Alterar");
				return  forward(getFormPage());
			}
		} finally {
			if (dao != null)
				dao.close();
		}				
	}
	
    /**
     * Inicia o cadastro de indicação de bolsa REUNI.<br /><br />
     * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/solicitacao_bolsas_reuni/view_solicitacao.jsp</li>
	 * </ul>
	 * @return
     * @throws ArqException
     */
	public String iniciarCadastro() throws ArqException{
		comando = SigaaListaComando.CADASTRAR_INDICACAO_BOLSAS_REUNI;
		prepareMovimento(comando);
		return iniciar(true);
	}
	
    /**
     * Inicia a alteração de indicação de bolsa REUNI.<br /><br />
     * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/solicitacao_bolsas_reuni/view_solicitacao.jsp</li>
	 * </ul>
	 * @return
     * @throws ArqException
     */	
	public String alterar() throws ArqException{
		comando = SigaaListaComando.CADASTRAR_INDICACAO_BOLSAS_REUNI;
		prepareMovimento(comando);
		return iniciar(false);		
	}
	
	/**  Limpa campos do MBean para novos cadastros. */
	private void clear(){
		obj = new IndicacaoBolsistaReuni();
		discente = null;
	}
	
	/**
	 * Redireciona para selecionar um discente.<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/indicacao_bolsista_reuni/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String informaDiscente(){
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");				
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.INDICACAO_BOLSISTA_REUNI);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Cadastra a indicação.<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/indicacao_bolsista_reuni/form.jsp</li>
	 * </ul>
	 */
	public String cadastrar() throws ArqException{
		//se o objeto não for nulo salva.
		if (obj != null){				
			boolean existePlano = false;
			PlanoDocenciaAssistidaDao daoPlano = getDAO(PlanoDocenciaAssistidaDao.class);
			try{
				//Verifica se existe planos cadastrados nos período adicionados (normalmente não passa de 2 períodos) 
				for (PeriodoIndicacaoReuni periodo : obj.getPeriodosIndicacao()){
					List<PlanoDocenciaAssistida> planos = daoPlano.findByPeriodoSituacao(discente, null, periodo.getAno(), periodo.getPeriodo(), true);
					if (!isEmpty(planos)){
						existePlano = true;
						break;
					}				
				}
				if (existePlano){
					addMensagemErro( "Este discente já possui uma Plano de Docência Assistida cadastrado para o(s) Ano-Período informado(s)." );				
					return null;
				}				
			} finally {
				if (daoPlano != null)
					daoPlano.close();
			}
			
			try {					
				prepareMovimento(comando);
				// Prepara o movimento, setando o objeto
				MovimentoIndicacaoBolsistaReuni mov = new MovimentoIndicacaoBolsistaReuni();
				mov.setIndicacao(obj);
				mov.setDiscenteAtual(discente);
				mov.setCodMovimento(comando);
				execute(mov);			
				// Se chegou aqui, não houve erros. Exibe a mensagem de sucesso.
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);				
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			}							
		}
			
		return exibirPlanos();
	}

	/** 
	 * Chamado a partir do BuscaDiscenteMBean<br/><br/>
	 * Método não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		/* Comentado devido a solicitação da tarefa 36363.
		 * if (!planoTrabalhoReuni.getSolicitacao().getPrograma().equals(discente.getUnidade())){
			addMensagemErro("O curso do discente selecionado é diferente do Plano de Trabalho Reuni indicado.");
			return null;
		}*/
		
		return forward(getFormPage());
	}

	/** 
	 * Seta o discente selecionado na busca por discente.<br/><br/>
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 * Método não invocado por JSP.
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		try {
			this.discente = (DiscenteStricto) getDAO(DiscenteDao.class).findByPK(discente.getId());
			
			if (!isEmpty(obj) && (this.discente.getId() != obj.getDiscente().getId())){
				comando = SigaaListaComando.ALTERAR_ALUNO_INDICACAO_BOLSAS_REUNI;
			}
			prepareMovimento(comando);				
		} catch (Exception e) {
			discente = null;
			e.printStackTrace();
		}		
	}
	
	/**
	 * Volta para a tela da lista de planos de trabalho.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/indicacao_bolsista_reuni/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws ArqException
	 * @throws Exception 
	 */	
	public String exibirPlanos() throws ArqException{
		SolicitacaoBolsasReuniMBean solicitacao = getMBean("solicitacaoBolsasReuniBean");	
		solicitacao.setEdital(planoTrabalhoReuni.getEdital().getId());
		solicitacao.setObj(planoTrabalhoReuni.getSolicitacao());
		return solicitacao.view();
	}
	
	/**
	 * Adiciona um Período a lista da indicação de bolsa.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/indicacao_bolsista_reuni/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws ArqException
	 */
	public void adicionarPeriodo(ActionEvent e)  throws ArqException {			
		boolean continua = true;		
		List<PeriodoIndicacaoReuni> lista = obj.getPeriodosIndicacao();
		for (PeriodoIndicacaoReuni periodo : lista ){
			if (anoPeriodo <= periodo.getAnoPeriodo()){
				addMensagemErro("Informe sempre períodos crescentes.");
				continua = false;
				break;				
			}
		}
					
		if (continua) {
			PeriodoIndicacaoReuni periodoIndicacao = new PeriodoIndicacaoReuni();			
			periodoIndicacao.setAnoPeriodo(anoPeriodo);
			
			CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
			IndicacaoBolsistaReuniDao daoIndicacao = getDAO(IndicacaoBolsistaReuniDao.class);
			PlanoDocenciaAssistidaDao daoPlano = getDAO(PlanoDocenciaAssistidaDao.class);
			try{				
				
				
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);			
			
				if (periodoIndicacao.getAno() < cal.getAno() || (periodoIndicacao.getAno() == cal.getAno() && 
								periodoIndicacao.getPeriodo() < cal.getPeriodo()) ){
					addMensagemErro( "Ano-Período informado não pode ser inferior ao período vigente ("+cal.getAno()+"."+cal.getPeriodo()+")");
					return;
				}

				IndicacaoBolsistaReuni indicacao = daoIndicacao.findAtivoByDiscentePeriodo(discente,periodoIndicacao.getAno(), periodoIndicacao.getPeriodo());
				if( !isEmpty( indicacao ) ){
					addMensagemErro( "Este discente já possui uma indicação ativa cadastrada para o Ano-Período informado." );
					return;
				}		
				
				List<PlanoDocenciaAssistida> planos = daoPlano.findByPeriodoSituacao(discente, null, periodoIndicacao.getAno(), 
						periodoIndicacao.getPeriodo(), true);
				if (!isEmpty(planos)){
					addMensagemErro( "Este discente já possui uma Plano de Docência Assistida cadastrado para o Ano-Período informado." );
					return;					
				}
			} finally {
				if (dao != null)
					dao.close();
				
				if (daoIndicacao != null)
					daoIndicacao.close();
				
				if (daoPlano != null)
					daoPlano.close();
			}	
			 
			periodoIndicacao.setIndicacaoBolsistaReuni(obj);
			
			obj.getPeriodosIndicacao().add(periodoIndicacao);			
		}
		
	}
	
	/**
	 * Remove um Período da lista da indicação de bolsa.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/indicacao_bolsista_reuni/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String removerPeriodo()  throws ArqException {
		int linha = getParameterInt("indice");		
		PeriodoIndicacaoReuni periodo = obj.getPeriodosIndicacao().get(linha);
		
		PlanoDocenciaAssistidaDao dao = getDAO(PlanoDocenciaAssistidaDao.class);
		try {
			PlanoDocenciaAssistida planoDocencia = dao.findAtivoByPeriodoIndicacao(periodo);						
			if (!isEmpty(planoDocencia)){
				addMensagemErro("Não é possível remover o período selecionado, pois existe um plano de Docência Assistida vinculado.");
				return null;
			}	
		} finally {
			if (dao != null)
				dao.close();
		}	
		
		// remove a indicação
		getGenericDAO().remove(periodo);
		obj.getPeriodosIndicacao().remove(linha);		
		return forward(getFormPage());
	}
	
	/** 
	 * Combo de calendários para ser escolhido na indicação da bolsa.
	 * @throws DAOException */
	public Collection<SelectItem> getPeriodos() throws DAOException {
		ArrayList<SelectItem> combo = new ArrayList<SelectItem>(0);
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		try{				
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);	
			for (int i = cal.getAno(); i < cal.getAno() + 4; i++ ) {			
				combo.add( new SelectItem( i + "" + 1, i + "." + 1 ) );
				combo.add(new SelectItem( i + "" + 2, i + "." + 2));
			}			
		} finally {
			if (dao != null)
				dao.close();
		}
		return combo;
	}	

	/**
	 * Caminho do Formulário de cadastro.
	 */
	@Override
	public String getFormPage() {
		return "/stricto/indicacao_bolsista_reuni/form.jsp";
	}

	public DiscenteStricto getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	public PlanoTrabalhoReuni getPlanoTrabalhoReuni() {
		return planoTrabalhoReuni;
	}

	public void setPlanoTrabalhoReuni(PlanoTrabalhoReuni planoTrabalhoReuni) {
		this.planoTrabalhoReuni = planoTrabalhoReuni;
	}

	public int getAnoPeriodo() {
		return anoPeriodo;
	}

	public void setAnoPeriodo(int anoPeriodo) {
		this.anoPeriodo = anoPeriodo;
	}
	
	
}
