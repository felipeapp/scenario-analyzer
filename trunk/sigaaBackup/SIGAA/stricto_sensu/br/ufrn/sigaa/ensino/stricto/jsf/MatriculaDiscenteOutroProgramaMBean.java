/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Apr 15, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;


/**
 *
 * @author Victor Hugo
 */
@Component("matriculaDiscenteOutroProgramaBean") @Scope("request")
public class MatriculaDiscenteOutroProgramaMBean extends SigaaAbstractController<DiscenteStricto> {

	/** Resultado da busca pelos discentes */
	private Collection<DiscenteStricto> discentes;

	public boolean buscaMatricula = false;
	public boolean buscaNome = false;

	public MatriculaDiscenteOutroProgramaMBean() {
		obj = new DiscenteStricto();
	}

	/**
	 * Chamado por:
	 * /stricto/menu_coordenador.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		return telaBuscaDiscenteOutroPrograma();
	}

	/**
	 * Chamado por:
	 * /stricto/matricula/busca_discente_outro_programa.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionarDiscente() throws ArqException {
		//Integer id = (Integer) evt.getComponent().getAttributes().get("idDiscente");
		int id = getParameterInt("idDiscente");
		obj = getGenericDAO().findByPrimaryKey(id, DiscenteStricto.class);

		checkRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		if( obj.getGestoraAcademica().getId() == getProgramaStricto().getId() ){
			addMensagemErro("O discente selecionado deve ser de outro programa.");
			return null;
		}

		//return redirecionarDiscente(obj);
		prepareMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
		MatriculaGraduacaoMBean matriculaMBean = (MatriculaGraduacaoMBean) getMBean("matriculaGraduacao");
		matriculaMBean.clear();
		matriculaMBean.setOperacaoAtual("Matrícula On-Line");
		matriculaMBean.setDiscente(obj);
		matriculaMBean.setMatricularDiscenteOutroPrograma(true);
		matriculaMBean.setRestricoes( RestricoesMatricula.getRestricoesRegular() );
		return matriculaMBean.selecionaDiscente();
	}

	/**
	 * Não é chamado por JSPs
	 * @return
	 */
	public String telaBuscaDiscenteOutroPrograma(){
		return forward("/stricto/matricula/busca_discente_outro_programa.jsp");
	}

	public boolean isBuscaMatricula() {
		return buscaMatricula;
	}

	public void setBuscaMatricula(boolean buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}


	/**
	 * Efetua a consulta por discentes de outro programa
	 * 
	 * Chamado por:
	 * /stricto/matricula/busca_discente_outro_programa.jsp
	 */
	@Override
	public String buscar() throws ArqException{

		Long matricula = null;
		String nome = null;
		if (!buscaMatricula && !buscaNome) {
			addMensagemErro("É necessário selecionar um dos critérios de busca");
			return null;
		}

		// Verificar os critérios de busca utilizados
		if (buscaMatricula)
			matricula = obj.getMatricula();
		if (buscaNome)
			nome = obj.getPessoa().getNome().trim().toUpperCase();


		// Realizar a consulta
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class);
		try {
			// Definir os status de discentes que podem ser consultados
			Collection<Integer> status = new ArrayList<Integer>(1);
			status.add( StatusDiscente.ATIVO  );

			discentes = dao.findDiscenteOutroPrograma(matricula, nome, status, getProgramaStricto().getId());

			if (discentes == null || discentes.isEmpty()) {
				addMessage("Não foram encontrados discentes com status " + StatusDiscente.getDescricaoStatus( status ) +
						" de acordo com os critérios de busca informados.", TipoMensagemUFRN.WARNING);
				return null;
			}

		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		return null;
	}

	public Collection<DiscenteStricto> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteStricto> discentes) {
		this.discentes = discentes;
	}

}
