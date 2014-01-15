/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 13/10/2010
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
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.dominio.Unidade;

/**
 *
 * <p>Processador quem cont�m as regras para altera��o dos dados de v�rios materiais no acervo </p>
 *
 * <p> <i> (Tem que alterar apenas campos espec�ficos, sen�o a altera��o fica muito pessada, porque a quantidade de materiais pode ser grande ) </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAlteraVariosMateriais extends AbstractProcessador{

	
	
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoAlteraVariosMateriais movi = (MovimentoAlteraVariosMateriais) mov;
		
		MaterialInformacionalDao dao = getDAO(MaterialInformacionalDao.class, movi);
		
		try{
		
			for(MaterialInformacional mat : movi.getMateriaisAlteracao()){
				
				/* ****************************************************************************
				 *                         Dados comuns aos materiais 
				 * ****************************************************************************/
				if(movi.isAlterarNumeroChamada()){
					if( mat.getNumeroChamada() != null && mat.getNumeroChamada().length() > 200){
						throw new NegocioException("O tamanho m�ximo do campo N�mero de Chamada � de 200 caracteres.");
					}
					dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{"numeroChamada"}, new Object[]{mat.getNumeroChamada()});
				}
				
				if(movi.isAlterarSegundaLocalizacao()){
					if( mat.getSegundaLocalizacao() != null && mat.getSegundaLocalizacao().length() > 200){
						throw new NegocioException("O tamanho m�ximo do campo Segunda Localiza��o � de 200 caracteres.");
					}
					dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{"segundaLocalizacao"}, new Object[]{mat.getSegundaLocalizacao()});
				}
				
				if(movi.isAlterarNotaGeral()){
					if( mat.getNotaGeral() != null && mat.getNotaGeral().length() > 300){
						throw new NegocioException("O tamanho m�ximo do campo Nota Geral � de 300 caracteres.");
					}
					dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{"notaGeral"}, new Object[]{mat.getNotaGeral()});
				}
				
				if(movi.isAlterarNotaUsuario()){
					if( mat.getNotaUsuario() != null && mat.getNotaUsuario().length() > 300){
						throw new NegocioException("O tamanho m�ximo do campo Nota ao Usu�rio � de 300 caracteres.");
					}
					dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{"notaUsuario"}, new Object[]{mat.getNotaUsuario()});
				}
				
				if(movi.isAlterarColecao()){
					dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{"colecao.id"}, new Object[]{mat.getColecao().getId()});
				}
				
				if(movi.isAlterarSituacao()){
					dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{"situacao.id"}, new Object[]{mat.getSituacao().getId()});
				}
				
				if(movi.isAlterarStatus()){
					dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{"status.id"}, new Object[]{mat.getStatus().getId()});
				}
				
				if(movi.isAlterarTipoMaterial()){
					dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{"tipoMaterial.id"}, new Object[]{mat.getTipoMaterial().getId()});
				}
				
				
				/* ****************************************************************************
				 *                         Dados de Exemplares
				 * ****************************************************************************/
				
				if(movi.isAlterarNotaTeseDissertacao()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.exemplar SET nota_tese_dissertacao = ? WHERE id_exemplar  = ? ", new Object[]{((Exemplar) mat).getNotaTeseDissertacao(), ((Exemplar) mat).getId() });
				}
				
				if(movi.isAlterarNotaConteudo()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.exemplar SET nota_conteudo = ? WHERE id_exemplar  = ? ", new Object[]{((Exemplar) mat).getNotaConteudo(), ((Exemplar) mat).getId() });
				}
				
				if(movi.isAlterarNumeroVolume()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.exemplar SET numero_volume = ? WHERE id_exemplar  = ? ", new Object[]{((Exemplar) mat).getNumeroVolume(), ((Exemplar) mat).getId() });
				}
				
				
				/* ****************************************************************************
				 *                         Dados de Fasc�culos
				 * ****************************************************************************/
				
				if(movi.isAlterarAnoCronologico()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.fasciculo SET ano_cronologico = ? WHERE id_fasciculo  = ? ", new Object[]{((Fasciculo) mat).getAnoCronologico(), ((Fasciculo) mat).getId() });
				}
				
				if(movi.isAlterarAno()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.fasciculo SET ano = ? WHERE id_fasciculo  = ? ", new Object[]{((Fasciculo) mat).getAno(), ((Fasciculo) mat).getId() });
				}
				
				if(movi.isAlterarVolume()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.fasciculo SET volume = ? WHERE id_fasciculo  = ? ", new Object[]{((Fasciculo) mat).getVolume(), ((Fasciculo) mat).getId() });
				}
				
				if(movi.isAlterarNumero()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.fasciculo SET numero = ? WHERE id_fasciculo  = ? ", new Object[]{((Fasciculo) mat).getNumero(), ((Fasciculo) mat).getId() });
				}
				
				if(movi.isAlterarEdicao()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.fasciculo SET edicao = ? WHERE id_fasciculo  = ? ", new Object[]{((Fasciculo) mat).getEdicao(), ((Fasciculo) mat).getId() });
				}
				
				if(movi.isAlterarDescricaoSuplemento()){
					// N�o � poss�vel usar updateFields aqui porque ele n�o trabalha bem  com heran�a
					dao.update("UPDATE biblioteca.fasciculo SET descricao_suplemento = ? WHERE id_fasciculo  = ? ", new Object[]{((Fasciculo) mat).getDescricaoSuplemento(), ((Fasciculo) mat).getId() });
				}
				
				
				dao.registraAlteracaoMaterial(mat, null, true);
			}
		
		}finally{
			if(dao != null)  dao.close();
		}
		
		return null;
	}

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MaterialInformacionalDao dao = getDAO(MaterialInformacionalDao.class, mov);
		SituacaoMaterialInformacionalDao situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, mov);
		
		try{
		
			MovimentoAlteraVariosMateriais movi = (MovimentoAlteraVariosMateriais) mov;
			
			ListaMensagens lista = new ListaMensagens();
			
			
			SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(
						SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
			
			
			for (MaterialInformacional material : movi.getMateriaisAlteracao()) {	
				
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
					material.getBiblioteca().setUnidade( new Unidade( dao.findByPrimaryKey(material.getBiblioteca().getId(), Biblioteca.class, "unidade.id").getUnidade().getId() ) ); 
					
					try{
						checkRole(material.getBiblioteca().getUnidade(), mov, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
					}catch (SegurancaException se) {
						lista.addErro("O usu�rio(a): "+ mov.getUsuarioLogado().getNome()+ " n�o tem permiss�o para alterar materiais da biblioteca. "+material.getBiblioteca().getDescricao());
					}
				
				}
				
				
				if(movi.isAlterarSituacao()){
					
					SituacaoMaterialInformacional situacaoAtual =  situacaoDao.findSituacaoAtualMaterial(material.getId());
					
					if(situacaoAtual != null && situacaoAtual.getId() == situacaoEmprestado.getId()){
						lista.addErro("A situa��o do material n�o pode ser alterada, pois ele est� "+situacaoEmprestado.getDescricao());
					}
				}
				
				
			}	
					
			checkValidation(lista);
		
		}finally{
			if(dao != null)  dao.close();
			if(situacaoDao != null)  situacaoDao.close();
			
		}
		
	}

}
