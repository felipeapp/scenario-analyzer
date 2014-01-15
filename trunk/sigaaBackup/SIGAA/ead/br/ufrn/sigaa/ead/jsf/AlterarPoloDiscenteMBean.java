/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Oct 15, 2007
 *
 */
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;

/**
 *
 * @author Victor Hugo
 *
 */
@Component("alterarPoloDiscente") @Scope("session")
public class AlterarPoloDiscenteMBean extends SigaaAbstractController<DiscenteGraduacao> implements OperadorDiscente {

	public static final int ALTERAR_POLO_DISCENTE = 1;

	/** select a ser populando com os p�los poss�veis para o aluno */
	private Collection<SelectItem> polosCombo = new ArrayList<SelectItem>(0);

	/** o novo polo a ser alterado, caso seja aluno a distancia */
	private Polo polo = new Polo();

	/** o novo curso a ser alterado, caso seja aluno pro-b�sica */
	private Curso curso = new Curso();

	public AlterarPoloDiscenteMBean() {
		this.obj = new DiscenteGraduacao();
		distancia = true;
	}

	/** indica se � altera��o de polo de aluno a distancia ou de aluno de curso pro-b�sica
	 * TRUE caso seja aluno a distancia
	 * FALSE caso seja aluno de curso pro-b�sica*/
	private boolean distancia = true;

	/**
	 * inicia o caso de uso para alunos EAD
	 * @throws ArqException
	 */
	public String iniciarEad() throws ArqException{
		distancia = true;
		return iniciar();
	}

	/**
	 * inicia o caso de uso para alunos de cursos PROBASICA
	 * @throws ArqException
	 */
	public String iniciarProbasica() throws ArqException{
		distancia = false;
		return iniciar();
	}

	/**
	 * Inicia a altera��o de p�lo do discente
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.DAE);
		prepareMovimento(SigaaListaComando.ALTERAR_POLO_DISCENTE);
		polo = new Polo();
		curso = new Curso();

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_POLO_DISCENTE);
		if( distancia ){
			buscaDiscenteMBean.setEad(true);
			buscaDiscenteMBean.setConvenio(0);
		}
		else{
			buscaDiscenteMBean.setConvenio(ConvenioAcademico.PROBASICA);
			buscaDiscenteMBean.setEad(false);
		}

		return buscaDiscenteMBean.popular();
	}

	/**
	 * Seleciona uma discente
	 */
	public String selecionaDiscente() throws ArqException {
		if( distancia && !obj.isDiscenteEad() ){
			addMensagemErro("S� � poss�vel alterar o p�lo de discentes de ensino a dist�ncia.");
			return null;
		} else if( !distancia && !obj.getCurso().isProbasica() ){
			addMensagemErro("S� � poss�vel alterar o p�lo de discentes de cursos prob�sica.");
			return null;
		}

		setOperacaoAtiva(ALTERAR_POLO_DISCENTE);
		PoloDao dao = getDAO(PoloDao.class);
		if( obj.isDiscenteEad() ){
			/* se for discente a distancia carrega os outros p�los do curso ao qual o aluno pertence */
			distancia = true;
			polosCombo =  toSelectItems( dao.findByCurso( obj.getCurso().getId() ), "id",  "cidade");
		}
		else if( obj.getCurso().isProbasica() ){
			/* se for aluno de curso a distancia carrega os outros cursos pro-b�sica que possuem o mesmo nome*/
			distancia = false;
			polosCombo =  toSelectItems( dao.findByExactField( Curso.class, "nome", obj.getCurso().getNome()  ), "id",  "descricao");
		}

		return forward( getFormPage() );
	}

	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj = (DiscenteGraduacao) discente;
	}

	/**
	 * Retorna o path do diret�rio
	 */
	@Override
	public String getDirBase() {
		return "/ead/AlterarPoloDiscente";
	}

	public Collection<SelectItem> getPolosCombo() {
		return polosCombo;
	}

	public void setPolosCombo(Collection<SelectItem> polosCombo) {
		this.polosCombo = polosCombo;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	/**
	 * chama o modelo para persistir a altera��o do polo do discente
	 * @return
	 */
	public String chamaModelo(){

		if (!checkOperacaoAtiva(ALTERAR_POLO_DISCENTE)) return cancelar();

		if( isEmpty(polo) && isEmpty(curso) )
			addMensagemErro("Informe o novo p�lo do discente.");

		if( distancia ){

			if( obj.getPolo() != null && polo.getId() == obj.getPolo().getId() )
				addMensagemErro("� necess�rio que voc� selecione um p�lo diferente da atual.");

		}else{

			if( curso.getId() == obj.getCurso().getId() )
				addMensagemErro("� necess�rio que voc� selecione um p�lo diferente da atual.");

		}

		if( hasErrors() )
			return null;

		if( distancia )
			obj.setPolo(polo);
		else
			obj.setCurso(curso);
		DiscenteMov mov = new DiscenteMov( SigaaListaComando.ALTERAR_POLO_DISCENTE, obj ) ;

		try {
			execute(mov, getCurrentRequest());
			addMessage("Altera��o de p�lo do discente realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			addMensagemErro("N�o foi poss�vel alterar o p�lo deste aluno. Contacte a administra��o do sistema.");
			notifyError(e);
			e.printStackTrace();
			return null;
		}

		return cancelar();

	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isDistancia() {
		return distancia;
	}

	public void setDistancia(boolean distancia) {
		this.distancia = distancia;
	}


}
