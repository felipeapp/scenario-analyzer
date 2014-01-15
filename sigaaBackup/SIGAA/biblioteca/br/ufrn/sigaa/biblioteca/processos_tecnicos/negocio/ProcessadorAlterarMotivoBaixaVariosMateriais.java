/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/10/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;


import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AlterarMotivoBaixaMaterialDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 * <p>Processador quem contém as regras para alteração do motivo de baixa de vários materiais baixados no acervo.</p>
 * 
 * @author felipe
 *
 */
public class ProcessadorAlterarMotivoBaixaVariosMateriais extends AbstractProcessador{

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoAlterarMotivoBaixaVariosMateriais movi = (MovimentoAlterarMotivoBaixaVariosMateriais) mov;
		
		AlterarMotivoBaixaMaterialDao dao = getDAO(AlterarMotivoBaixaMaterialDao.class, movi);
		
		try{
			for(MaterialInformacional mat : movi.getMateriaisAlteracao()){
				dao.atualizaMotivoBaixaMaterial(mat); // atualiza apenas o campo motivo baixa
			}
		}finally{
			if(dao != null)  dao.close();
		}
		
		return null;
	}

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MaterialInformacionalDao dao = getDAO(MaterialInformacionalDao.class, mov);
		SituacaoMaterialInformacionalDao situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, mov);
		AlterarMotivoBaixaMaterialDao alterarMotivoDao = getDAO(AlterarMotivoBaixaMaterialDao.class, mov);
		
		try{
		
			MovimentoAlterarMotivoBaixaVariosMateriais movi = (MovimentoAlterarMotivoBaixaVariosMateriais) mov;
			
			ListaMensagens lista = new ListaMensagens();
			
			for (MaterialInformacional material : movi.getMateriaisAlteracao()) {	
				
				
				if( StringUtils.isEmpty(material.getMotivoBaixa() ) ){
					lista.addErro("Informe o Motivo da Baixa .");
				}else{
					if(material.getMotivoBaixa().length() > 300)
						lista.addErro("O tamanho máximo do campo Motivo da Baixa é de 300 caracteres.");
				}
				
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
					material.getBiblioteca().setUnidade( new Unidade( dao.findByPrimaryKey(material.getBiblioteca().getId(), Biblioteca.class, "unidade.id").getUnidade().getId() ) ); 
					
					try{
						checkRole(material.getBiblioteca().getUnidade(), mov, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
					}catch (SegurancaException se) {
						lista.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()+ " não tem permissão para alterar materiais da biblioteca. "+material.getBiblioteca().getDescricao());
					}
				
					Usuario usuarioRealizouBaixa = alterarMotivoDao.findUsuarioAlterouUltimaVezMaterial(material.getId());
					
					if( usuarioRealizouBaixa == null || ( usuarioRealizouBaixa.getId() != movi.getUsuarioLogado().getId() ) ){
						lista.addErro("Não é possível alterar o motivo da baixa do material: "+material.getCodigoBarras()+" porque a baixa não foi realizada pelo(a) senhor(a). ");
					}
					
				}				
				
			}	
					
			checkValidation(lista);
		
		} finally {
			if(dao != null)  dao.close();
			if(situacaoDao != null)  situacaoDao.close();	
			if(alterarMotivoDao != null)  alterarMotivoDao.close();	
		}	
	}
}
