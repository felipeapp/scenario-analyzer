/*
 * ProcessadorAlteraAssociacaoAssinaturaTitulo.java
 *
 * Universidade Federal do Rio Grande no Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 25/11/2009
 * Autor: jadson
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;

/**
 *   Processador que altera a associa��o entre a assinatura e o seu t�tulo. Caso a assinatura tenha 
 *   sido atribu�da ao T�tulo errado ou se desejar mudar o T�tulo dos fasc�culos.
 *
 * @author jadson
 * @since 25/11/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorAlteraAssociacaoAssinaturaTitulo extends AbstractProcessador{

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		MovimentoAlteraAssociacaoAssinaturaTitulo m  = (MovimentoAlteraAssociacaoAssinaturaTitulo) mov;
		
		validate(m);
		
		FasciculoDao dao = null;
		
		try{
			dao = getDAO(FasciculoDao.class, m);
			
			Assinatura assinatura = m.getAssinatura();
			CacheEntidadesMarc tituloAntigo = m.getAntigoTituloAssinatura();
			CacheEntidadesMarc tituloNovo = m.getNovoTituloAssinatura();
			
			// se o usu�rio escolheu tranferir para o mesmo T�tulo n�o precisa fazer nada //
			if(! tituloAntigo.getIdTituloCatalografico().equals( tituloNovo.getIdTituloCatalografico())){
			
				long quantidadeFasciculosTransferidos = dao.countFasciculosAtivosNoAcervoDaAssinatura(assinatura.getId());
				
				tituloNovo.setQuantidadeMateriaisAtivosTitulo(tituloNovo.getQuantidadeMateriaisAtivosTitulo()+ (int) quantidadeFasciculosTransferidos);
				tituloAntigo.setQuantidadeMateriaisAtivosTitulo(tituloAntigo.getQuantidadeMateriaisAtivosTitulo() - (int) quantidadeFasciculosTransferidos);
			
				// Altera a assinatura para pertencer ao novo T�tulo //
				dao.updateField(Assinatura.class, assinatura.getId(), "tituloCatalografico.id", tituloNovo.getIdTituloCatalografico());
				
				dao.updateField(CacheEntidadesMarc.class, tituloNovo.getId(), "quantidadeMateriaisAtivosTitulo", tituloNovo.getQuantidadeMateriaisAtivosTitulo());
				dao.updateField(CacheEntidadesMarc.class, tituloAntigo.getId(), "quantidadeMateriaisAtivosTitulo", tituloAntigo.getQuantidadeMateriaisAtivosTitulo());
			
			}
		}finally{
			if(dao != null) dao.close();
		}
		
		
		return null;
	}

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoAlteraAssociacaoAssinaturaTitulo m  = (MovimentoAlteraAssociacaoAssinaturaTitulo) mov;
		
		TituloCatalograficoDao dao = null;
		
		try{
			dao = getDAO(TituloCatalograficoDao.class, m);
			
			Assinatura assinatura = m.getAssinatura();
			CacheEntidadesMarc tituloNovo = m.getNovoTituloAssinatura();
			
			assinatura = dao.refresh(assinatura);
			
			if(assinatura.isAssinaturaDeCompra())
				if(! m.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					erros.addErro(" Usu�rio n�o tem permiss�o para alterar os dados de uma assiantura de compra ");
				}
			
			
			try{
				// Sen�o � administrador geral, checa a unidade de registro da chegado do fasc�culo.
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					checkRole(assinatura.getUnidadeDestino().getUnidade(), m, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				}
			}catch(SegurancaException se){
				erros.addErro("Usu�rio: "+m.getUsuarioLogado().getNome()+" n�o tem permiss�o para alterar assinaturas da biblioteca: "+assinatura.getUnidadeDestino().getDescricao());
			}
			
			FormatoMaterial formato = dao.findFormatoMaterialTitulo(tituloNovo.getIdTituloCatalografico());
			
			if(! formato.isFormatoPeriodico()){
				erros.addErro("A asssinatura n�o pode ser associada com um T�tulo que n�o possui o formato do material de peri�dico.");
			}
			
			
		}finally{
			
			if(dao != null) dao.close();
			
			checkValidation(erros);
		}
	}

}
