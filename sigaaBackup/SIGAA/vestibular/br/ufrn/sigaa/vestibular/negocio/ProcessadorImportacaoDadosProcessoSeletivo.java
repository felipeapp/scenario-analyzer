/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.vestibular.dominio.Inscrito;
import br.ufrn.sigaa.vestibular.dominio.Opcao;
import br.ufrn.sigaa.vestibular.dominio.Prova;
import br.ufrn.sigaa.vestibular.dominio.ResultadoArgumento;
import br.ufrn.sigaa.vestibular.dominio.ResultadoClassificacaoCandidato;
import br.ufrn.sigaa.vestibular.dominio.ResultadoOpcaoCurso;
import br.ufrn.sigaa.vestibular.dominio.SituacaoCandidato;

/**
 * Processador responsável pela efetivação da importação dos dados de candidatos do Vestibular 
 * originário da instituição mantenedora do processo seletivo.
 * @author Rafael Gomes
 *
 */
public class ProcessadorImportacaoDadosProcessoSeletivo extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		
		validate(mov);
		MovimentoImportacaoDadosProcessoSeletivo movimento = (MovimentoImportacaoDadosProcessoSeletivo) mov;
		Inscrito inscrito = movimento.getInscrito();
		GenericDAO dao = getGenericDAO(mov);
		
		ResultadoClassificacaoCandidato resultadoCandidato;
		ResultadoOpcaoCurso resultadoOpcao;
		ResultadoArgumento resultadoArgumento;
		
		try{
			if( inscrito.getNumeroInscricao() != null && !inscrito.isExisteResultadoClassificacao() ){
				resultadoCandidato = new ResultadoClassificacaoCandidato();
				resultadoCandidato.setNumeroInscricao(inscrito.getNumeroInscricao());
				resultadoCandidato.setOpcaoAprovacao(inscrito.getOpcaoAprovacao());
				resultadoCandidato.setSemestreAprovacao(inscrito.getSemestreAprovacao());
				resultadoCandidato.setSituacaoCandidato(SituacaoCandidato.getSituacao(inscrito.getSituacao()));
				resultadoCandidato.setInscricaoVestibular(inscrito.getInscricaoVestibular());
				resultadoCandidato.setExisteResultadoClassificacao(inscrito.isExisteResultadoClassificacao());
				resultadoCandidato.setAprovadoTurnoDistinto(inscrito.isAprovadoTurnoDistinto());
				resultadoCandidato.setAprovadoAma(inscrito.isAprovadoAma());
				resultadoCandidato.setClassificacaoAprovado(inscrito.getClassificacaoAprovado());
				dao.create(resultadoCandidato);
				
				for (Opcao opcao : inscrito.getOpcoes()) {
					resultadoOpcao = new ResultadoOpcaoCurso();
					resultadoOpcao.setResultadoClassificacaoCandidato(resultadoCandidato);
					resultadoOpcao.setMatrizCurricular(new MatrizCurricular(opcao.getMatrizCurricular()));
					resultadoOpcao.setOrdemOpcao(opcao.getOrdemOpcao());
					resultadoOpcao.setClassificacao(opcao.getClassificacao());
					resultadoOpcao.setArgumentoFinal(opcao.getArgumentoFinal());
					resultadoOpcao.setArgumentoFinalSemBeneficio(opcao.getArgumentoFinalSemBeneficio());
					dao.create(resultadoOpcao);
				}
				
				for (Prova prova : inscrito.getProvas()) {
					resultadoArgumento = new ResultadoArgumento();
					resultadoArgumento.setResultadoClassificacaoCandidato(resultadoCandidato);
					resultadoArgumento.setDescricaoProva(prova.getDescricaoProva());
					resultadoArgumento.setArgumento(prova.getArgumento());
					resultadoArgumento.setFase(prova.getFase());
					dao.create(resultadoArgumento);
				}
			}	
			
					
		} finally {
			dao.close();
		}
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	
}
