/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Feb 7, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.stricto.dominio.AproveitamentoCredito;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * MBeam responsável por lançar aproveitamento de credito para discente stricto
 * @author Victor Hugo
 *
 */
@Component("aproveitamentoCredito")
@Scope("session")
public class AproveitamentoCreditoMBean extends SigaaAbstractController<br.ufrn.sigaa.ensino.stricto.dominio.AproveitamentoCredito> implements OperadorDiscente {

	private ArrayList<AproveitamentoCredito> historicoAproveitamento = new ArrayList<AproveitamentoCredito>();

	public AproveitamentoCreditoMBean() {
		obj = new AproveitamentoCredito();
	}

	/**
	 * Inicia o caso de uso e redireciona para a busca de discente
	 * @return
	 * @throws ArqException
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/menu_coordenador.jsp
	 * /sigaa.war/stricto/menus/discente.jsp
	 */
	public String iniciar() throws ArqException{
		checkRole( SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS );
		prepareMovimento( ArqListaComando.CADASTRAR );
		return buscarDiscente();
	}

	/**
	 * Inicia o caso de uso de remoção e redireciona para a busca de discente
	 * @return
	 * @throws ArqException
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/menu_coordenador.jsp
	 * /sigaa.war/stricto/menus/discente.jsp
	 */
	public String iniciarRemover() throws ArqException {
		checkRole( SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS );
		prepareMovimento( ArqListaComando.ALTERAR );
		return buscarDiscente();
	}
	
	/**
	 * Popula os dados necessários para iniciar a busca de discentes.
	 * @return
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/aproveitamento_credito/form.jsp
	 */
	public String buscarDiscente() {		
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.APROVEITAMENTO_CREDITO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Não é chamado por nenhuma JSP.
	 */
	public String selecionaDiscente(){
		try {
			historicoAproveitamento = (ArrayList<AproveitamentoCredito>) getGenericDAO().findByExactField(AproveitamentoCredito.class, "discente.id", obj.getDiscente().getId(), "asc", "dataCadastro");
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}
		
		if (getUltimoComando() ==  ArqListaComando.CADASTRAR)
			return forward( getFormPage() );
		if (getUltimoComando() ==  ArqListaComando.ALTERAR)
			return forward( pageRemover() );
		
		return null;
	}

	/**
	 * Não é chamado por nenhuma JSP.
	 */
	public void setDiscente(DiscenteAdapter discente) {
		DiscenteDao dao = getDAO( DiscenteDao.class );
		try {
			obj.setDiscente( (DiscenteStricto) dao.findByPK( discente.getId() ) );
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return;
		}
	}

	/**
	 * Método que lança aproveitamento de crédito (cadastro no banco
	 * referente ao caso de uso).
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/aproveitamento_credito/form.jsp
	 * @throws ArqException 
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {

		if( obj.getCreditos() <= 0 ){
			addMensagemErro("Informe a quantidade de créditos para aproveitar!");
			return null;
		}

		if( !confirmaSenha() )
			return null;
		obj.setAtivo(true);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento( ArqListaComando.CADASTRAR );
		mov.setObjMovimentado( obj );

		try {
			execute(mov);
			addMessage("Aproveitamento de Crédito Cadastrado com Sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		return cancelar();
	}
	
	/**
	 * Método que remove créditos (remoção no banco
	 * referente ao caso de uso).
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/aproveitamento_credito/remover.jsp
	 */
	public String remover() throws DAOException {
		/*
		if( !confirmaSenha() )
			return null;
		 */
		Integer id = getParameterInt("idAprov");
		obj = getGenericDAO().findByPrimaryKey(id, AproveitamentoCredito.class);
		obj.setAtivo(false);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento( ArqListaComando.ALTERAR );
		mov.setObjMovimentado( obj );

		try {
			execute(mov);
			addMessage("Aproveitamento de Crédito Removido com Sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
		historicoAproveitamento = (ArrayList<AproveitamentoCredito>) getGenericDAO().findByExactField(AproveitamentoCredito.class, "discente.id", obj.getDiscente().getId());
		return cancelar();
	}

	@Override
	public String getDirBase() {
		return "/stricto/aproveitamento_credito";
	}

	/**
	 * Método que retorna o endereço da página de remoção.
	 * @return
	 */
	public String pageRemover() {
		return "/stricto/aproveitamento_credito/remover.jsf";
	}	
	
	public ArrayList<AproveitamentoCredito> getHistoricoAproveitamento() {
		return historicoAproveitamento;
	}

	public void setHistoricoAproveitamento(ArrayList<AproveitamentoCredito> historicoAproveitamento) {
		this.historicoAproveitamento = historicoAproveitamento;
	}

}
