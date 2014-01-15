/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/09/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MateriaisMarcadosParaGerarEtiquetas;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 *   Implementa as regras para dar baixa em um fasc�culo.
 *
 * @author Jadson
 * @since 11/09/2009
 * @version 1.0 cria��o da classe
 *
 */
public class ProcessadorDarBaixaFasciculo extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		MaterialInformacionalDao dao = null;
		
		try{
			
			dao = getDAO(MaterialInformacionalDao.class, mov);
			
			MovimentoDarBaixaFasciculo movimento = (MovimentoDarBaixaFasciculo) mov;
			
			Fasciculo fasciculo = movimento.getFasciculo();
			
			SituacaoMaterialInformacional situacaoDeBaixa = dao.findByExactField(
								SituacaoMaterialInformacional.class, "situacaoDeBaixa", true, true);
			
			fasciculo.setSituacao(situacaoDeBaixa);
			
			// Guarda as informa��es sobre o t�tulo do material baixado, para poder remover o t�tulo posteriormente.
			fasciculo.setInformacoesTituloMaterialBaixado(  BibliotecaUtil.obtemDadosResumidosTituloDoMaterial(fasciculo.getId()));
			
			ProcessadorAtualizaFasciculo processadorAtualizaFasciculo = new ProcessadorAtualizaFasciculo();
			
			MovimentoAtualizaFasciculo movAuxiliar = new MovimentoAtualizaFasciculo(fasciculo, "Fasc�culo baixado ");
			movAuxiliar.setCodMovimento(SigaaListaComando.ATUALIZA_FASCICULO);
			movAuxiliar.setUsuarioLogado(movimento.getUsuarioLogado());
			movAuxiliar.setSistema(movimento.getSistema());
			
			// Utilizado na baixa de materiais perdidos pelo usu�rio durante o empr�stimo //
			if(movimento.isPermiteBaixarMaterialEmprestado()){
				movAuxiliar.setPermiteAtualizacaoDeEmprestados(true);
			}
			
			processadorAtualizaFasciculo.execute(movAuxiliar);
			
			int idTitulo = dao.findIdTituloMaterial(fasciculo.getId());
			
			BibliotecaUtil.decrementaQuantidadesMateriaisTitulo(dao, idTitulo);
			

			// RETIRA DA LISTA DE MATERIAIS MARCADOS PARA  GERA��O ETIQUETA, SE TIVER //
			Collection<MateriaisMarcadosParaGerarEtiquetas> materiaisMarcados
				= dao.findByExactField(MateriaisMarcadosParaGerarEtiquetas.class, "idFasciculo", fasciculo.getId());
			
			for (MateriaisMarcadosParaGerarEtiquetas materialMarcado : materiaisMarcados) {
				dao.remove(materialMarcado);
			}
			
		
		}finally{
			if(dao != null ) dao.close();
		}
		
		return null;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoDarBaixaFasciculo movimento = (MovimentoDarBaixaFasciculo) mov;
		
		Fasciculo fasciculo = movimento.getFasciculo();
		
		MaterialInformacionalDao dao = null;
		FasciculoDao fasciculoDao = null;
		SituacaoMaterialInformacionalDao situacaoDao = null;
		
		try{
		
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			fasciculoDao = getDAO(FasciculoDao.class, mov);
			situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, movimento);
			
			if( StringUtils.isEmpty( fasciculo.getMotivoBaixa())){
				lista.addErro("� preciso informar o motivo da baixa.");
			}else{
				if(fasciculo.getMotivoBaixa().length() > 300)
					lista.addErro("O tamanho m�ximo para o motivo da baixa � de 300 caracteres");
			}
			
			/* *********************************************************************************************************************************
			* Se o material est� emprestado atualmente e o usu�rio est� tentando alterar para uma situa��o diferente de empretado //
			*  Pode ocorrer caso o usu�rio abra a tela de alterar o materail antes de realizar o empr�stimos, e clicar em atualizar depois de realizar o empr�stimo //
			* **********************************************************************************************************************************/
			
			
			if(! movimento.isPermiteBaixarMaterialEmprestado()){
				
				SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(
						SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
				
				
				SituacaoMaterialInformacional situacaoAtual =  situacaoDao.findSituacaoAtualMaterial(fasciculo.getId());
				
				if(situacaoAtual.isSituacaoEmprestado() && fasciculo.getSituacao().getId() != situacaoEmprestado.getId()){
					lista.addErro(" O Fasc�culo n�o pode ser baixado, pois ele est� "+situacaoEmprestado.getDescricao() );
				}
			
			}
			
			
			if( dao.materialEstaPendenteDeTransferenciaEntreBibliotecas(fasciculo.getId()))
				lista.addErro("O Fasc�culo n�o pode ser baixado, pois ele est� pendente de transfer�nica.");
		
			
			// VERIFICA��O DE MATERIAL POSSUI ANEXOS OU SUPLEMENTOS PARA PODER REMOVER //
			
			Long qtdSuplementos = fasciculoDao.countSuplementosAtivosDoFasciculo(fasciculo.getId());
			
			if(qtdSuplementos > 0)
				lista.addErro("A baixa no fasc�culo n�o p�de ser realizada, pois ele possui suplementos.");
			
		}finally{
			
			if(dao != null) dao.close();
			if(fasciculoDao != null) fasciculoDao.close();
			if(situacaoDao != null) situacaoDao.close();
			
			checkValidation(lista);
			checkValidation(lista);
			
		}
		
	}

}
