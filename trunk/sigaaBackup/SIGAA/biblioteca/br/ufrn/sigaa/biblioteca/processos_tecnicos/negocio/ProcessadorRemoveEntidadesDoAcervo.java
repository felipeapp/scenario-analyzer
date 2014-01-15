/*
 * ProcessadorRemoveEntidadesDoAcervo.java
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

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EntidadesMarcadasParaExportacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 *     Processador que remove um título do acervo.
 *     
 *     Para remover o título não pode conter materiais não baixados.
 *     
 *     Na remoção coloca-se ativo = false no título e apaga-se o objeto cache passado para o título 
 * não aparecer mais nas pesquisas.
 *
 * @author jadson
 * @since 25/09/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorRemoveEntidadesDoAcervo extends AbstractProcessador{

	
	
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoRemoveEntidadesDoAcervo movimento = (MovimentoRemoveEntidadesDoAcervo) mov;
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movimento);
		
			CacheEntidadesMarc cache = movimento.getEntidade();
			
			// seta ativo para false do título
			if(movimento.isRemovendoTitulo()){
				dao.updateField(TituloCatalografico.class, cache.getIdTituloCatalografico(), "ativo", false);
				
				// tem que remover alguma entidade que existia marcada para exportação.
				List<EntidadesMarcadasParaExportacao> entidades 
					= (List<EntidadesMarcadasParaExportacao>) dao.findByExactField(EntidadesMarcadasParaExportacao.class
							, "idTituloCatalografico", cache.getIdTituloCatalografico());
				
				for (EntidadesMarcadasParaExportacao ent : entidades) {
					dao.remove(ent);
				}
				
			}
				
			if(movimento.isRemovendoAutoridade()){
				dao.updateField(Autoridade.class, cache.getIdAutoridade(), "ativo", false);
			
				// OBS.: atualmente o sistema só desativa a autoridade e não remove apaga nada,
				// se algum dia for remover os subcampos da autoridade para diminuir a 
				//quantidade de dados na tabela de subcampo e tornar o sistema mais rápido, porque 
				// aquela tabela cresce muito, lembrar de remover a referência que os subcampos de 
				// títulos podem ter para esse subcampo de autoridade. 
				
				// tem que remover alguma entidade que existia marcada para exportação.
				List<EntidadesMarcadasParaExportacao> entidades 
					= (List<EntidadesMarcadasParaExportacao>) dao.findByExactField(EntidadesMarcadasParaExportacao.class
							, "idAutoridade", cache.getIdAutoridade());
				
				for (EntidadesMarcadasParaExportacao ent : entidades) {
					dao.remove(ent);
				}
				
			}
			
				
			if(movimento.isRemovendoArtigo())
				dao.updateField(ArtigoDePeriodico.class, cache.getIdArtigoDePeriodico(), "ativo", false);
			
			// remove o cache para não aparecer mais nas pesquisas
			dao.remove(cache);
		
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
		
		MovimentoRemoveEntidadesDoAcervo movimento = (MovimentoRemoveEntidadesDoAcervo) mov;
		
		if(movimento.isRemovendoTitulo()){
			
			AssinaturaDao dao = null;
			
			try{
				
				dao = getDAO(AssinaturaDao.class, movimento);
				
				ListaMensagens lista = new ListaMensagens();
				
				CacheEntidadesMarc cache = movimento.getEntidade();
				
				/*
				 * Se tiver materiais ativos não pode deixar remover o título
				 */
				if(cache.getQuantidadeMateriaisAtivosTitulo() > 0 ){
					lista.addErro("Título com o número do sistema "+cache.getNumeroDoSistema()+" não pôde ser removido, pois ele ainda possui materiais informacionais associados a ele.");
				}
				
				// Para títulos com fascículos registrados, mas com a quantidade de materiais igual a zero porque nenhum foi incluído no acervo ainda. //
				if( dao.countAssinaturasAtivasByTitulo(cache.getIdTituloCatalografico()) > 0 ) {
					lista.addErro("Título com o número do sistema "+cache.getNumeroDoSistema()+" não pôde ser removido, pois ele ainda possui uma assinatura associada a ele.");
				}
				
				checkValidation(lista);
				
				
				
			}finally{
				if(dao != null) dao.close();
			}
		
		}
		
	}

}
