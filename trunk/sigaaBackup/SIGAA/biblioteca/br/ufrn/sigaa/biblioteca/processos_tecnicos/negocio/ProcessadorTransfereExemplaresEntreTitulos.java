/*
 * ProcessadorTransfereExemplaresEntreTitulos.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *    Processador que transfere exemplares de um título para outro.
 *
 *
 * @author jadson
 * @since 01/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorTransfereExemplaresEntreTitulos extends AbstractProcessador{

	
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		ExemplarDao dao = null;
		
		MovimentoTransfereExemplaresEntreTitulos movimento = (MovimentoTransfereExemplaresEntreTitulos) mov;
		
		try{
			
			dao = getDAO( ExemplarDao.class, movimento);
		
			Integer idTituloOrigem = movimento.getIdTituloOriginalExemplares();
			Integer idTituloDestino = movimento.getIdTituloDestinoExemplares();
			
			
		
			
			List<Exemplar> exemplares = movimento.getExemplaresTransferidos();
			
			
			CacheEntidadesMarc cacheOrigem = BibliotecaUtil.obtemDadosTituloCache(idTituloOrigem);
			CacheEntidadesMarc cacheDestino = BibliotecaUtil.obtemDadosTituloCache(idTituloDestino);
			
			if(! cacheDestino.isCatalogado())
				throw new NegocioException("Os exemplares não puderam ser transferidos. O Título para onde os exemplares estão sendo tranferidos não está com a catalogação finalizada no sistema.");
			
			int contador = 0;
			
			for (Exemplar e : exemplares) {
				e.setTituloCatalografico(new TituloCatalografico(idTituloDestino));
				dao.updateField(Exemplar.class, e.getId(), "tituloCatalografico.id", idTituloDestino);
				
				contador ++;
			}
			
			// Atualiza os caches dos título para a nova quantidade //
			dao.updateField(CacheEntidadesMarc.class, cacheOrigem.getId(), "quantidadeMateriaisAtivosTitulo", 
					(cacheOrigem.getQuantidadeMateriaisAtivosTitulo() - contador) >= 0 ? (cacheOrigem.getQuantidadeMateriaisAtivosTitulo() - contador) : 0);
		
			dao.updateField(CacheEntidadesMarc.class, cacheDestino.getId(), "quantidadeMateriaisAtivosTitulo", 
					cacheDestino.getQuantidadeMateriaisAtivosTitulo() + contador);
			
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
		
		ListaMensagens lista = new ListaMensagens();
	
		MaterialInformacionalDao dao = null;
		ExemplarDao daoExemplar = null;
		TituloCatalograficoDao daoTitulo = null;
		
		try{
			
			dao = getDAO(MaterialInformacionalDao.class, mov);
			
			daoTitulo= getDAO(TituloCatalograficoDao.class, mov);
			
			daoExemplar = getDAO(ExemplarDao.class, mov);
			
			MovimentoTransfereExemplaresEntreTitulos movimento = (MovimentoTransfereExemplaresEntreTitulos) mov;
			
			if( movimento.getIdTituloOriginalExemplares().equals(movimento.getIdTituloDestinoExemplares())){
				lista.addErro(" Os exemplares não podem ser transferidos para o mesmo Título ");
			}
			
			
			FormatoMaterial formato =  daoTitulo.findFormatoMaterialTitulo(movimento.getIdTituloDestinoExemplares());
			
			// títulos não finalizados podem não possuir formato
			if(formato != null && formato.isFormatoPeriodico()){
				lista.addErro(" Os exemplares não podem ser tranferidos para um Título de periódico.");
			}
			
				
				
			List<Exemplar> exemplares = movimento.getExemplaresTransferidos();
			
			if(! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				for (Exemplar e : exemplares) {

					Integer idUnidadeMaterial =  dao.findIdUnidadeDoMaterialDaBibliotecaInternaAtiva(e.getId());
					
					try{
						checkRole(new Unidade( idUnidadeMaterial ), movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
					}catch (SegurancaException se) {
						lista.addErro("  O usuário não tem permissão para alterar o exemplar da biblioteca: "+e.getBiblioteca().getDescricao());
						break;
					}
				
				}
				
			}
	
		}finally{
			if(dao != null ) dao.close();
			if(daoExemplar != null) daoExemplar.close();
			if(daoTitulo != null) daoTitulo.close();
		}
		
		
		checkValidation(lista);
	}
	
	

}
