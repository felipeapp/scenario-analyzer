/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/03/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MateriaisMarcadosParaGerarEtiquetas;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *   Processador que cria novos exemplares no acervo do sistema
 *
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorCadastraExemplar extends AbstractProcessador{

	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastraExemplar  movimento = (MovimentoCadastraExemplar) mov;
		
		validate(mov);
		
		MaterialInformacionalDao dao = null;
		
		/* Retorna os exemplares incluídos para realizar outras operações depois da inclusão */
		List<Integer> idsExemplaresIncluidos = new ArrayList<Integer>();
		
		try{
			dao = getDAO(MaterialInformacionalDao.class, mov);
		
			List<CacheEntidadesMarc> cachesAtualizacaoQuantidadeMateriais = new ArrayList<CacheEntidadesMarc>();
			
			for (Exemplar exemplar : movimento.getExemplares()) {	
				
				CacheEntidadesMarc cache = BibliotecaUtil.obtemDadosTituloCache(exemplar.getTituloCatalografico().getId());
				
				if( ! cachesAtualizacaoQuantidadeMateriais.contains(cache) ){
					cache.setQuantidadeMateriaisAtivosTitulo(cache.getQuantidadeMateriaisAtivosTitulo()+1);
					cachesAtualizacaoQuantidadeMateriais.add(cache);
				}else{
					
					CacheEntidadesMarc cacheTemp = cachesAtualizacaoQuantidadeMateriais.get(cachesAtualizacaoQuantidadeMateriais.indexOf(cache));
					cacheTemp.setQuantidadeMateriaisAtivosTitulo(cacheTemp.getQuantidadeMateriaisAtivosTitulo()+1);
				}
				
				
				dao.create(exemplar);
				
				dao.registraAlteracaoMaterial(exemplar, null, false);
				
				idsExemplaresIncluidos.add(exemplar.getId());
				
				 // REGISTRA O EXEMPLAR PARA IMPRESSÃO DA ETIQUETA.
				MateriaisMarcadosParaGerarEtiquetas materialGerarEdiqueta 
					= new MateriaisMarcadosParaGerarEtiquetas(exemplar, (Usuario) movimento.getUsuarioLogado());
				
				dao.create(materialGerarEdiqueta);
				
			}
			
			for (CacheEntidadesMarc cacheEntidadesMarc : cachesAtualizacaoQuantidadeMateriais) {
				dao.update(cacheEntidadesMarc);
			}
			
			
		}finally{
			if(dao != null ) dao.close();
		}
		
		return idsExemplaresIncluidos;
	}

	
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastraExemplar  movimento = (MovimentoCadastraExemplar) mov;
	
		AssinaturaDao dao = null;
		MaterialInformacionalDao materialDao = null;
		ClassificacaoBibliograficaDao daoClassificacao = null;
		
		ListaMensagens lista = new ListaMensagens();
		
		try{
			dao = getDAO(AssinaturaDao.class, movimento);
			
			materialDao = getDAO(MaterialInformacionalDao.class, movimento);
			daoClassificacao = getDAO(ClassificacaoBibliograficaDao.class, movimento);
			
			
			List<Long> numerosPatrimonio = new ArrayList<Long>();
			
			for (Exemplar exemplar : movimento.getExemplares()) {
			
				numerosPatrimonio.add(exemplar.getNumeroPatrimonio());
				
				exemplar.setBiblioteca(dao.refresh(exemplar.getBiblioteca()));
				
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
					try{
						checkRole(exemplar.getBiblioteca().getUnidade() , movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
					}catch (SegurancaException se) {
						lista.addErro(" O usuário(a): "+ mov.getUsuarioLogado().getNome()
								+ " não tem permissão para incluir exemplares na biblioteca "
								+exemplar.getBiblioteca().getDescricao());
						break;
					}
					
				}
				
				
				// exemplar so pode ser colocado na situação de emprestado pelo processador de empréstimo
				
				if(exemplar.getSituacao().isSituacaoEmprestado()){
					
					lista.addErro(" A situação de um exemplar não pode ser alterada manualmente para \""
							+ exemplar.getSituacao().getDescricao()
							+"\". Esta situação é reservada para ser usada durante os empréstimos. ");
				}
				
				
				
				// BLOQUEIO PARA NÃO INSERIR DOIS MATERIAIS COM MESMO CÓDIGO DE BARRAS.
				
				if(materialDao.countMateriaisByCodigosBarras(exemplar.getCodigoBarras()) > 0){
					lista.addErro(" Já existe um outro material com o mesmo código de barras no sistema: "
							+exemplar.getCodigoBarras()+", por isso o exemplar não pôde ser incluído. ");
				}
				
				
				// Bloquea a inclusão de materiais no sistema se o Título estiver sem classificação //
				
				ClassificacaoBibliografica classificacaoUtilizada = daoClassificacao.findClassificacaoUtilizadaPelaBiblioteca(exemplar.getBiblioteca().getId()); 
				
				if(classificacaoUtilizada == null)
					lista.addErro("Para incluir materiais no acervo da biblioteca: "+exemplar.getBiblioteca().getDescricao()+" é preciso primeiro definir qual a classificação bibliográfica que ela utilizará.");
				else{
					if( ! daoClassificacao.isDadosClassificacaoPreenchidos(exemplar.getTituloCatalografico().getId(), classificacaoUtilizada.getOrdem())){
						lista.addErro(" Não é possível incluir o exemplar no sistema porque a sua catalogação não possui as informações da classificação "+classificacaoUtilizada.getDescricao()+" preenchidas. ");
					}
				}
				
			}	
			
		}finally{
			
			if(dao != null) dao.close();
			if(materialDao != null) materialDao.close();
			if(daoClassificacao != null) daoClassificacao.close();
			
			checkValidation(lista);
			
		}
		
	}

}
