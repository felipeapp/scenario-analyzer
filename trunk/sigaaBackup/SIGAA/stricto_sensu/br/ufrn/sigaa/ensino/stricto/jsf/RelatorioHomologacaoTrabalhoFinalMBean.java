/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 05/03/2008
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;

/**
 * Managed bean para gera��o do comprovante de homologa��o de um discente
 * stricto.
 * 
 * @author David Pereira
 * 
 */
@Component("relatorioHomologacaoStricto")
@Scope("request")
public class RelatorioHomologacaoTrabalhoFinalMBean extends SigaaAbstractController<HomologacaoTrabalhoFinal> implements OperadorDiscente {

	private DiscenteAdapter discente;
	
	/**
	 * M�todo que popula a p�gina inicial do caso de uso.
	 * 
	 * Chamado por: 
	 * /stricto/homologacao_trabalho_final/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG);

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.RELATORIO_HOMOLOGACAO_STRICTO);
		return buscaDiscenteMBean.popular();
	}

	
	public String selecionaDiscente() throws ArqException {
		
		try {
			
			obj = getDAO(BancaPosDao.class).findHomologacaoByDiscente(discente);
			
			if (obj == null) {
				addMensagemErro("N�o foi encontrada nenhuma solicita��o de homologa��o para esse discente.");
				return null;
			}
			
			obj.getBanca().getMembrosBanca().iterator();
			carregarDadosDiscente(obj.getBanca().getDadosDefesa().getDiscente());
			
			
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}

		return forward("/stricto/relatorios/comprovante_homologacao.jsp");
	}

	/**
	 * Exibe o relat�rio iniciando a partir da p�gina de listagem da solicita��es de homologa��es 
	 * dispon�veis para os programas e n�o da busca geral de discentes
	 * 
	 * Chamado por: 
	 * /stricto/homologacao_trabalho_final/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionaHomologacao() throws ArqException {
		obj = new HomologacaoTrabalhoFinal();
		setId();
		BancaPosDao dao = getDAO(BancaPosDao.class);
		dao.initialize(obj);
		discente = obj.getBanca().getDadosDefesa().getDiscente();
		return selecionaDiscente();
	}
	
	private void carregarDadosDiscente(DiscenteStricto ds) throws DAOException {
		Curso curso = ds.getCurso();
		if (curso != null) {
			if (curso.getUnidade() != null)
				curso.getUnidade().getNome();
			if (curso.getTipoCursoStricto() != null)
				curso.getTipoCursoStricto().getDescricao();

			ds.getCurso().getNomeCursoStricto();
		}
		if (ds.getCurriculo() != null)
			ds.getCurriculo().getCodigo();
		if (ds.getArea() != null)
			ds.getArea().getDenominacao();
		if (ds.getLinha() != null)
			ds.getLinha().getDenominacao();
		
		ds.setOrientacao(DiscenteHelper.getUltimoOrientador(ds.getDiscente()));
		ds.setCoOrientacao(DiscenteHelper.getUltimoCoOrientador(ds));
	}


	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = discente;
	}

}
