package br.ufrn.sigaa.ensino_rede.academico.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino_rede.academico.dominio.EstornoMovimentoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.academico.dominio.MovimentacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.academico.dominio.TipoMovimentacao;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.negocio.GrupoDiscentesAlterados;
import br.ufrn.sigaa.ensino_rede.negocio.MovimentoAlterarStatusMatriculaRede;
import br.ufrn.sigaa.ensino_rede.negocio.ProcessadorAlterarStatusDiscente;
/**
 * Processador para estornar movimentação do módulo de ensino em rede.
 * @author Jeferson Queiroga
 *
 */
public class ProcessadorEstornarMovimentacao extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		GenericDAO dao = null;
		MovimentoCadastro movC = (MovimentoCadastro) mov;
		MovimentacaoDiscenteAssociado movDiscente = movC.getObjMovimentado();
		getGenericDAO(movC).updateField(MovimentacaoDiscenteAssociado.class, movDiscente.getId(), "ativo", Boolean.FALSE );
		
		//Caso a movimentação estornada já tenha sido retornada, séra necessário criar outra movimentação.
		int idStatus = movDiscente.getDiscente().getStatus().getId();
		if( movDiscente.getDataRetorno()!= null && ( idStatus == StatusDiscenteAssociado.ATIVO ) ){
			criarNovaMovimentacao(movC);
		}
				
		//Chama o método para realizar a operação de retorno do último status.
		alterarStatus(movC);
		
		try{
			dao = getGenericDAO(movC);
			EstornoMovimentoDiscenteAssociado estorno = new EstornoMovimentoDiscenteAssociado();
			estorno.setMovimentacao(movDiscente);
			dao.create(estorno);
		}finally{
			dao.close();
		}
		
		return null;
	}

	
	/**
	 * Método que retorna o discente para o último status.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void alterarStatus(MovimentoCadastro mov) throws NegocioException, ArqException {
				
		MovimentacaoDiscenteAssociado movDiscente = mov.getObjMovimentado();
		
		//Retornado o status anterior.
		Integer ultimoStatus = DiscenteRedeHelper.getUltimoStatus(movDiscente.getDiscente());
		
		GrupoDiscentesAlterados grupo = new GrupoDiscentesAlterados();
		grupo.addDiscente(movDiscente.getDiscente());
		grupo.setStatusNovo(new StatusDiscenteAssociado(ultimoStatus));
		
		MovimentoAlterarStatusMatriculaRede movA = new MovimentoAlterarStatusMatriculaRede();
		movA.addGrupo(grupo);
		movA.setCodMovimento(mov.getCodMovimento());
		movA.setSistema(mov.getSistema());
		movA.setUsuarioLogado(mov.getUsuarioLogado());
		
		ProcessadorAlterarStatusDiscente proc = new ProcessadorAlterarStatusDiscente();
		proc.execute(movA);
	}
	
	/**
	 * Método que cria um nova movimentação. Isso será utilizado nos estornos de movimentações já retonarda de trancamento.
	 * @param mov
	 */
	private void criarNovaMovimentacao( MovimentoCadastro mov){
		MovimentacaoDiscenteAssociado movimentacaoPai = mov.getObjMovimentado();
		GenericDAO dao = null;
				
		try{
			dao = getGenericDAO(mov);
			
			MovimentacaoDiscenteAssociado novaMovimentacao = new MovimentacaoDiscenteAssociado();
			novaMovimentacao = new MovimentacaoDiscenteAssociado();
			novaMovimentacao.setTipo(TipoMovimentacao.TRANCAMENTO);
			
			//Dados do periodo
			novaMovimentacao.setAnoOcorrencia( movimentacaoPai.getAnoOcorrencia() );
			novaMovimentacao.setPeriodoOcorrencia( movimentacaoPai.getPeriodoOcorrencia() );
			novaMovimentacao.setAnoReferencia( movimentacaoPai.getAnoReferencia() );
			novaMovimentacao.setPeriodoReferencia( movimentacaoPai.getPeriodoReferencia() );
			novaMovimentacao.setDiscente( movimentacaoPai.getDiscente() );
			//Guarda referencia para a movimentação que deu origem a nova.
			novaMovimentacao.setMovimentacaoOrigem( movimentacaoPai );
			dao.create(novaMovimentacao);
			
			
		} catch (DAOException e) {
			e.printStackTrace();
		}finally{
			if (dao != null)
				dao.close();
		}
	}

		
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movC = (MovimentoCadastro) mov;
		MovimentacaoDiscenteAssociado movDiscente = movC.getObjMovimentado();
		
		if( !movDiscente.isAtivo()){
			throw new NegocioException("A movimentação já foi estornada");	
		}
		
		if( ValidatorUtil.isEmpty( movDiscente.getDiscente() ) ){
			throw new NegocioException("Movimentação sem Discente");	
		}
	}

}
