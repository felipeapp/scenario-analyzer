/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 28/08/2007
 * 
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean respons�vel por alterar a {@link FormaIngresso Forma de Ingresso} de um aluno.
 * @author Victor Hugo
 *
 */
@Component("alterarFormaIngresso")
@Scope("session")
public class AlterarFormaIngressoMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente {

	/** Select a ser populado com as formas de ingresso poss�veis para o aluno */
	private Collection<SelectItem> formasIngressoCombo = new ArrayList<SelectItem>(0);

	/** A nova forma de ingresso a ser alterada */
	private FormaIngresso formaIngresso = new FormaIngresso();
	
	/** O novo per�odo de ingresso a ser alterado */
	private Integer periodoIngresso;
	
	/** Construtor padr�o. */
	public AlterarFormaIngressoMBean() {
		obj = new Discente();
	}

	/**
	 * Inicia o caso de uso.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/aluno.jsp</li>
	 * <li>/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CDP);
		prepareMovimento(SigaaListaComando.ALTERAR_FORMA_INGRESSO_DISCENTE);

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_FORMA_INGRESSO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Chamado por {@link BuscaDiscenteMBean}.<br>
	 * M�todo n�o invocado por JSP�s.
	 * 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {

		int status = obj.getStatus();
		if( status != StatusDiscente.ATIVO && status != StatusDiscente.CADASTRADO && status != StatusDiscente.FORMANDO &&
				status != StatusDiscente.AFASTADO && status != StatusDiscente.GRADUANDO && status != StatusDiscente.TRANCADO ){
			addMensagemErro( "N�o � poss�vel alterar a forma de ingresso de discentes com a situa��o " + obj.getStatusString() );
		}

		if( obj.getFormaIngresso() != null  ) 
			formaIngresso.setId( obj.getFormaIngresso().getId() );
		periodoIngresso = obj.getPeriodoIngresso();

		char tipo = 'R';
		switch ( obj.getTipo() ) {
		case Discente.REGULAR:
			tipo = 'R';
			break;
		case Discente.ESPECIAL:
			tipo = 'E';
			break;
		}

		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		formasIngressoCombo = toSelectItems(dao.findByNivelTipo( obj.getNivel() , tipo), "id", "descricao");

		return forward( getFormPage() );
	}

	/**
	 * Chamado por {@link BuscaDiscenteMBean}.
	 * 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj = discente;
	}

	/**
	 * Altera a forma de ingresso de um discente.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/ensino/alterar_forma_ingresso/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String chamaModelo(){

		if( formaIngresso == null || formaIngresso.getId() == 0 )
			addMensagemErro("Informe a nova forma de ingresso.");
		
		if( periodoIngresso == null || periodoIngresso == 0 )
			addMensagemErro("Informe o novo per�odo de ingresso.");

		if( hasErrors() )
			return null;

		obj.setFormaIngresso(formaIngresso);
		obj.setPeriodoIngresso(periodoIngresso);
		DiscenteMov mov = new DiscenteMov( SigaaListaComando.ALTERAR_FORMA_INGRESSO_DISCENTE, obj ) ;

		try {
			execute(mov, getCurrentRequest());
			addMessage("Altera��o de dados de ingresso realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			addMensagemErro("N�o foi poss�vel alterar os dados de ingresso deste aluno. Contacte a administra��o do sistema.");
			notifyError(e);
			e.printStackTrace();
			return null;
		}

		return cancelar();
	}

	/** Retorna o diret�rio base dos formul�rios.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase()
	 */
	@Override
	public String getDirBase() {
		return "/ensino/alterar_forma_ingresso";
	}

	/** Retorna o select a ser populado com as formas de ingresso poss�veis para o aluno 
	 * @return
	 */
	public Collection<SelectItem> getFormasIngressoCombo() {
		return formasIngressoCombo;
	}

	/** Seta o select a ser populado com as formas de ingresso poss�veis para o aluno
	 * @param formasIngressoCombo
	 */
	public void setFormasIngressoCombo(Collection<SelectItem> formasIngressoCombo) {
		this.formasIngressoCombo = formasIngressoCombo;
	}

	/** Retorna a nova forma de ingresso a ser alterada
	 * @return
	 */
	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	/** Seta a nova forma de ingresso a ser alterada 
	 * @param formaIngresso
	 */
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	/** Retorna o novo per�odo de ingresso a ser alterado 
	 * @return
	 */
	public Integer getPeriodoIngresso() {
		return periodoIngresso;
	}

	/** Seta o novo per�odo de ingresso a ser alterado
	 * @param periodoIngresso
	 */
	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

}
