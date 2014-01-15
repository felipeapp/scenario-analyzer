/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/11/2011
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.PlanoDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.ParecerPlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;


/**
 * Este MBean tem como finalidade de auxiliar nas operações relacionadas ao parecer do docente do 
 * plano de docência assistida.
 * 
 * @author Arlindo Rodrigues
 */
@Component("parecerDocenciaAssistida") @Scope("request")
public class ParecerPlanoDocenciaAssistidaMBean extends SigaaAbstractController<ParecerPlanoDocenciaAssistida> {
	
	/** Plano selecionado */
	private PlanoDocenciaAssistida plano = new PlanoDocenciaAssistida();
	
	/** Listagem de planos que permite parecer */
	private List<PlanoDocenciaAssistida> listagem = new ArrayList<PlanoDocenciaAssistida>();
	
	/**
	 * Construtor padrão
	 */
	public ParecerPlanoDocenciaAssistidaMBean() {
		obj = new ParecerPlanoDocenciaAssistida();
	}
	
	/**
	 * Inicia a listagem dos planos que podem realizar parecer para o docente logado
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String iniciar() throws HibernateException, DAOException{
		
		PlanoDocenciaAssistidaDao dao = getDAO(PlanoDocenciaAssistidaDao.class);
		try {
			
			int idDocente = 0;
			if (ValidatorUtil.isNotEmpty( getUsuarioLogado().getVinculoAtivo().getServidor() ))
				idDocente = getUsuarioLogado().getVinculoAtivo().getServidor().getId();
			else if (ValidatorUtil.isNotEmpty( getUsuarioLogado().getVinculoAtivo().getDocenteExterno() ))
				idDocente = getUsuarioLogado().getVinculoAtivo().getDocenteExterno().getId();
			
			listagem = (List<PlanoDocenciaAssistida>) dao.findByDocente(idDocente, null, null);
			
		} finally {
			if (dao != null)
				dao.close();
		}		
				
		
		return forward(getListPage());
	}
	
	/**
	 * Iniciar o registro do parecer do docente
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/plano_docencia_assistida/lista_parecer.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarParecer() throws ArqException {
		
		int id = getParameterInt("id", 0);
		plano = getGenericDAO().findByPrimaryKey(id, PlanoDocenciaAssistida.class);
		
		if (ValidatorUtil.isEmpty(plano)){
			addMensagemErro("Selecione o plano para registrar o parecer!");
			return null;
		}
		
		obj = new ParecerPlanoDocenciaAssistida();
		obj.setPlanoDocenciaAssistida(plano);
		
		OrientacaoAcademica orientacao = DiscenteHelper.getUltimoOrientador(plano.getDiscente().getDiscente());
		plano.getDiscente().setOrientacao(orientacao);		
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return forward("/stricto/plano_docencia_assistida/registro_parecer.jsf");
	}
	
	/**
	 * Cadastra o parecer
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/plano_docencia_assistida/registro_parecer.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		try {			
			
			if (ValidatorUtil.isEmpty(obj.getObservacao())){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observação");
				return null;
			}
			
			obj.setData(new Date());
			
			// Prepara o movimento, setando o objeto
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(getUltimoComando());
			execute(mov);
			
			String mensagem = "Acesse Portal do Docente > Planos de Docência Assistida > " +
			"Visualizar Plano de Docência Assistida, para verificar seu parecer.";
			addMensagemInformation(mensagem);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, TipoMensagemUFRN.INFORMATION);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}				
		return iniciar();
	}	
	
	/**
	 * JSP da lista de planos
	 */
	@Override
	public String getListPage() {
		return "/stricto/plano_docencia_assistida/lista_parecer.jsf";
	}
	
	public List<PlanoDocenciaAssistida> getListagem() {
		return listagem;
	}

	public void setListagem(List<PlanoDocenciaAssistida> listagem) {
		this.listagem = listagem;
	}

	public PlanoDocenciaAssistida getPlano() {
		return plano;
	}

	public void setPlano(PlanoDocenciaAssistida plano) {
		this.plano = plano;
	}

}
