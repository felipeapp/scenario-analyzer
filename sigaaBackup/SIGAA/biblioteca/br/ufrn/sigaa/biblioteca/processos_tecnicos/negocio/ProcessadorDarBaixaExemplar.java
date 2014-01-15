/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on  26/03/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.integracao.interfaces.PatrimonioRemoteService;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MateriaisMarcadosParaGerarEtiquetas;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;


/**
 *
 *   Processador que realiza a operação de dar baixa em um exemplar
 *
 * @author Jadson
 * @since 26/03/2009
 * @version 1.0 criação da classe
 */
public class ProcessadorDarBaixaExemplar extends AbstractProcessador{

	
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		MaterialInformacionalDao dao = null;
		
		/* Guarda as mensagem retornas por esse processador */
		ListaMensagens mensagensRetorno = new ListaMensagens();
		
		try{
			
			dao = getDAO(MaterialInformacionalDao.class, mov);
			
			MovimentoDarBaixaExemplar movimento = (MovimentoDarBaixaExemplar) mov;
			
			Exemplar  exemplar = movimento.getExemplar();
			
			
			SituacaoMaterialInformacional situacaoDeBaixa = dao.findByExactField(
								SituacaoMaterialInformacional.class, "situacaoDeBaixa", true, true);
			
			exemplar.setSituacao(situacaoDeBaixa);
			
			// Guarda as informações sobre o título do material baixado, para poder remover o título posteriormente.
			exemplar.setInformacoesTituloMaterialBaixado(BibliotecaUtil.obtemDadosResumidosTituloDoMaterial(exemplar.getId()) );
			
			ProcessadorAtualizaExemplar processadorAtualizaExemplar = new ProcessadorAtualizaExemplar();
			
			/////////// REALIZA A BAIXA NO ACERVO DA BIBLIOTECA  /////////
			
			MovimentoAtualizaExemplar movAuxiliar = new MovimentoAtualizaExemplar(exemplar, "Exemplar baixado" );
			movAuxiliar.setCodMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR);
			movAuxiliar.setUsuarioLogado(movimento.getUsuarioLogado());
			movAuxiliar.setSistema(movimento.getSistema());
			
			// Utilizado na baixa de materiais perdidos pelo usuário durante o empréstimo //
			if(movimento.isPermiteBaixarMaterialEmprestado()){
				movAuxiliar.setPermiteAtualizacaoDeEmprestados(true);
			}
			
			processadorAtualizaExemplar.execute(movAuxiliar);
			
			BibliotecaUtil.decrementaQuantidadesMateriaisTitulo(dao, exemplar.getTituloCatalografico().getId());
			
			// RETIRA DA LISTA DE MATERIAIS MARCADOS PARA  GERAÇÃO ETIQUETA, SE TIVER //
			Collection<MateriaisMarcadosParaGerarEtiquetas> materiaisMarcados
				= dao.findByExactField(MateriaisMarcadosParaGerarEtiquetas.class, "idExemplar", exemplar.getId());
			
			for (MateriaisMarcadosParaGerarEtiquetas materialMarcado : materiaisMarcados) {
				dao.remove(materialMarcado);
			}
			
			
			////////////// REALIZA A BAIXA DO PATRIMÔNIO NO SIPAC  /////////////
			
			
			try {
				if (exemplar.getNumeroPatrimonio() != null) {
					//PatrimonioRemoteService remoto = BibliotecaDelegate.getInstance().getPatrimonioRemoteService();
					PatrimonioRemoteService remoto = getBean("patrimonioRemoteServiceInvoker", movimento);
					remoto.realizarBaixaLivro( exemplar.getNumeroPatrimonio() );
				}
			} catch(Exception ex) {
				ex.printStackTrace();
				
				String mensagem = "Não foi possível realizar a baixa do patrimônio: "+ exemplar.getNumeroPatrimonio()
				+" no "+ RepositorioDadosInstitucionais.get("siglaSipac");
				
				if(movimento.isBaixaPatrimonioObrigatoria()){
					throw new NegocioException(mensagem);
				}else{
					mensagensRetorno.addErro(mensagem);
				}
			}
			
		}finally{
			if(dao != null ) dao.close();
		}
		
		return mensagensRetorno;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoDarBaixaExemplar movimento = (MovimentoDarBaixaExemplar) mov;
		
		Exemplar exemplar = movimento.getExemplar();
		
		MaterialInformacionalDao dao = null;
		ExemplarDao exemplarDao = null;
		SituacaoMaterialInformacionalDao situacaoDao = null;
		
		try{
			
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			exemplarDao = getDAO(ExemplarDao.class, mov);
			situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, movimento);
		
			
			
			if( StringUtils.isEmpty( exemplar.getMotivoBaixa()))
				lista.addErro("É preciso informar o motivo da baixa.");
			else{
				if(exemplar.getMotivoBaixa().length() > 300)
					lista.addErro("O tamanho máximo para o motivo da baixa é de 300 caracteres");
			}
			
			/* *********************************************************************************************************************************
			* Se o material está emprestado atualmente e o usuário está tentando alterar para uma situação diferente de empretado //
			*  Pode ocorrer caso o usuário abra a tela de alterar o materail antes de realizar o empréstimos, e clicar em atualizar depois de realizar o empréstimo //
			* **********************************************************************************************************************************/
			
			if(! movimento.isPermiteBaixarMaterialEmprestado()){
			
				SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(
						SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
				
				SituacaoMaterialInformacional situacaoAtual =  situacaoDao.findSituacaoAtualMaterial(exemplar.getId());
				
				if(situacaoAtual.isSituacaoEmprestado() && exemplar.getSituacao().getId() != situacaoEmprestado.getId()){
					lista.addErro(" O Exemplar não pode ser baixado, pois ele está "+situacaoEmprestado.getDescricao() );
				}
			
			}
			
			if( dao.materialEstaPendenteDeTransferenciaEntreBibliotecas(exemplar.getId()))
				lista.addErro("O Exemplar não pode ser baixado, pois ele está pendente de transferênica.");
			
			// VERIFICAÇÃO DE MATERIAL POSSUI ANEXOS OU SUPLEMENTOS PARA PODER REMOVER //
			
			Long qtdAnexos = exemplarDao.countAnexosAtivosDoExemplar(exemplar.getId());
			
			if(qtdAnexos > 0)
				lista.addErro("A baixa do exemplar não pôde ser realizada, pois ele possui anexos.");
			
		}finally{
			
			if(dao != null) dao.close();
			if(exemplarDao != null) exemplarDao.close();
			if(situacaoDao != null) situacaoDao.close();
			
			checkValidation(lista);
		}
		
	}

}
