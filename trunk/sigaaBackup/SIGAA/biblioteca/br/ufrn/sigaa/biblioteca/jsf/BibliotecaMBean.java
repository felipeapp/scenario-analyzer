/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/11/2008
 *
 */	
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoEmprestimoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ServicosEmprestimosBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ServicosInformacaoReferenciaBiblioteca;
import br.ufrn.sigaa.biblioteca.negocio.MovimentoAtualizaBibliotecaInterna;

/**
 * MBean que gerencia a alteração das informações das Bibliotecas.
 * @author Fred_Castro
 * @author jadson - jadson@info.ufrn.br
 */

@Component("bibliotecaMBean")
@Scope("request")
public class BibliotecaMBean extends SigaaAbstractController <Biblioteca>{
	
	/** Guarda as bibliotecas internas ativas do sistema, são aquelas que o usuário pode alterar os seus parâmetros. */
	private List<Biblioteca> bibliotecaInternasAtivas;
	
	/**
	 * Informações do serviço de empréstimos da biblioteca selecionada
	 */
	private ServicosEmprestimosBiblioteca servicoEmprestimoInstitucionalInterna;
	/**
	 * Informações do serviço de empréstimos da biblioteca selecionada
	 */
	private ServicosEmprestimosBiblioteca servicoEmprestimoInstitucionalExterna;
	/**
	 * Informações do serviço de empréstimos da biblioteca selecionada
	 */
	private ServicosEmprestimosBiblioteca servicoEmprestimoAlunoGraduacaoMesmoCentro;
	/**
	 * Informações do serviço de empréstimos da biblioteca selecionada
	 */
	private ServicosEmprestimosBiblioteca servicoEmprestimoAlunoGraduacaoOutroCentro;
	/**
	 * Informações do serviço de empréstimos da biblioteca selecionada
	 */
	private ServicosEmprestimosBiblioteca servicoEmprestimoAlunoPosMesmoCentro;
	/**
	 * Informações do serviço de empréstimos da biblioteca selecionada
	 */
	private ServicosEmprestimosBiblioteca servicoEmprestimoAlunoPosOutroCentro;
	
	/**
	 * <p>Método chamado para visualizar ou alterar os dados de uma biblioteca</p>
	 * 
	 * <p>Redireciona para a o formulário com os dados da biblioteca</p>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/Biblioteca/lista.jsp</li>
	 *   </ul>
	 
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		
		populateObj(true);

		prepareMovimento(SigaaListaComando.ALTERAR_BIBLIOTECA_INTERNA);
		
		TipoEmprestimoDao dao =null;
		
		try{
			
			dao = getDAO(TipoEmprestimoDao.class);

			/*
			 * Os tipos de empréstimo ativos no sistema 
			 */
			List<TipoEmprestimo> tiposEmprestimosAtivos = dao.findTipoEmprestimosAtivosComPoliticaDeEmprestimo(true, false);
			
			
			// Se o objeto foi encontrado, exibe a tela de alterar //
			if (obj != null){
				obj.setUnidade(dao.refresh(obj.getUnidade()));
				obj.setServicos( dao.findByExactField(ServicosInformacaoReferenciaBiblioteca.class, "biblioteca.id", obj.getId(), true));
				
				if (obj.getServicos() == null) {
					ServicosInformacaoReferenciaBiblioteca servicos = new ServicosInformacaoReferenciaBiblioteca();
					
					servicos.setBiblioteca(obj);
					obj.setServicos(servicos);					
				}
				
				montaDadosServicosEmprestimosBiblioteca(tiposEmprestimosAtivos, (List<ServicosEmprestimosBiblioteca> ) 
						dao.findByExactField(ServicosEmprestimosBiblioteca.class, "biblioteca.id", obj.getId() ) );
				
				return super.atualizar();
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		// Se tentando alterar uma biblioteca que não existe //
		obj = new Biblioteca();
		addMensagemErro("Este objeto foi removido.");
		return forward(getListPage());
	}




	/**
	 * <p>Para cada serviço sem ser o institucional, verifica os tipos de empréstimos que ele contém, esses são os tipos de empréstimos
	 * que a biblioteca vai realizar o serviço.</p>
	 * 
	 * <p>Caso a lista não tenha um tipo de empréstimos ativo, significa que o serviço não é aplicado a ele, então ele deve ser adicionado a lista
	 * como não selecionado, caso o usuário o selecione, o sistema vai salva-lo.</p>
	 *
	 * @param tiposEmprestimosAtivos
	 * @param servicosDaBiblioteca
	 */
	private void montaDadosServicosEmprestimosBiblioteca(List<TipoEmprestimo> tiposEmprestimosAtivos, List<ServicosEmprestimosBiblioteca> servicosDaBiblioteca) {
		
		
		for (ServicosEmprestimosBiblioteca servico : servicosDaBiblioteca ) {
			
			if(! servico.isEmprestimoInstitucionalInterno() && ! servico.isEmprestimoInstitucionalExterno() ){
				
			
				Iterator<TipoEmprestimo> iterator = servico.getTiposEmprestimos().iterator();
				while ( iterator.hasNext()){
					TipoEmprestimo tipoEmprestimoUtilizadoServico = iterator.next();
					if( tipoEmprestimoUtilizadoServico.isAtivo()){
						TipoEmprestimo t = new TipoEmprestimo(tipoEmprestimoUtilizadoServico.getId(), tipoEmprestimoUtilizadoServico.getDescricao());
						t.setAtivo(tipoEmprestimoUtilizadoServico.isAtivo());
						t.setSelecionado(true);
						servico.getTiposEmprestimos().set(servico.getTiposEmprestimos().indexOf( tipoEmprestimoUtilizadoServico ), t);
					}else{
						/* Se o tipo de empréstimo foi removido do sistema, ele deve ser removido do relacionamento com o serviço para 
						 * não aparecer mais nesse cadastro.
						 * Se por acaso ele não for removido o usuário não vai conseguir fazer empréstimos pois o tipo de empréstimo renovido não aparece.*/
						iterator.remove();
					}
				}
				
				/* Verifica se o serviço ainda não contém um relacionamento com um tipo de empréstimo ativo do sistema.
				 * Em caso negativo, adiciona o relacionamento para ser salvo agora, quando não tenha não vai conseguir fazer empréstimos 
				 * desse novo tipo ainda. Tem que vim aqui no cadastro e atualizar. */
				for(TipoEmprestimo tipoEmprestimoAtivo : tiposEmprestimosAtivos){
					if( ! servico.getTiposEmprestimos().contains( tipoEmprestimoAtivo) ){  
						TipoEmprestimo t = new TipoEmprestimo(tipoEmprestimoAtivo.getId(), tipoEmprestimoAtivo.getDescricao());
						t.setSelecionado(false);
						servico.getTiposEmprestimos().add(t);
					}
				}
			}
			
			if(servico.isEmprestimoInstitucionalInterno())
				servicoEmprestimoInstitucionalInterna = servico;
			if(servico.isEmprestimoInstitucionalExterno())
				servicoEmprestimoInstitucionalExterna = servico;
			if(servico.isEmprestaAlunosGraduacaoMesmoCentro())
				servicoEmprestimoAlunoGraduacaoMesmoCentro = servico;
			if(servico.isEmprestaAlunosPosMesmoCentro())
				servicoEmprestimoAlunoPosMesmoCentro = servico;
			if(servico.isEmprestaAlunosGraduacaoOutroCentro())
				servicoEmprestimoAlunoGraduacaoOutroCentro = servico;
			if(servico.isEmprestaAlunosPosOutroCentro())
				servicoEmprestimoAlunoPosOutroCentro = servico;
		}
		
		Collections.sort(servicoEmprestimoAlunoGraduacaoMesmoCentro.getTiposEmprestimos());
		Collections.sort(servicoEmprestimoAlunoPosMesmoCentro.getTiposEmprestimos());
		Collections.sort(servicoEmprestimoAlunoGraduacaoOutroCentro.getTiposEmprestimos());
		Collections.sort(servicoEmprestimoAlunoPosOutroCentro.getTiposEmprestimos());
		
	}
	
	
	
	
	/**
	 * Método que verifica se já existe um objeto cadastrado com este nome.
	 * <br><br>
	 * Método não chamado por nenhuma jsp.
	 * 
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
		GenericDAO dao = getGenericDAO();
		Biblioteca s = dao.findByExactField(Biblioteca.class, "descricao", obj.getDescricao(), true);
		
		if ((s != null && obj.getId() == 0) || (s != null && s.getId() != obj.getId()))
			addMensagemErro("Já existe uma biblioteca com este nome.");
		
		s = dao.findByExactField(Biblioteca.class, "identificador", obj.getIdentificador(), true);
		
		if ((s != null && obj.getId() == 0) || (s != null && s.getId() != obj.getId()))
			addMensagemErro("Já existe uma biblioteca com este identificador.");
		
		if (obj.getEndereco() != null && obj.getEndereco().getId() == 0)
			obj.setEndereco(null);
	}

	
	
	/**
	 * Altera as configurações da biblioteca selecionada.
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/Biblioteca/lista.jsp</li>
	 *   </ul>
	 */
	@Override
	public String cadastrar() throws ArqException {

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
		
		// Verifica se o objeto não foi removido
		if (obj == null){
			addMensagemErro("A biblioteca foi removida.");
			return forward(getListPage());
		}

		// Valida o objeto
		addMensagens(obj.validate());
		
		try {
			beforeCadastrarAfterValidate();
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		if (hasErrors())
			return null;
		
		List<ServicosEmprestimosBiblioteca> servicosEmprestimos = new ArrayList<ServicosEmprestimosBiblioteca>();
		servicosEmprestimos.add(servicoEmprestimoInstitucionalInterna);
		servicosEmprestimos.add(servicoEmprestimoInstitucionalExterna);
		servicosEmprestimos.add(servicoEmprestimoAlunoGraduacaoMesmoCentro);
		servicosEmprestimos.add(servicoEmprestimoAlunoGraduacaoOutroCentro);
		servicosEmprestimos.add(servicoEmprestimoAlunoPosMesmoCentro);
		servicosEmprestimos.add(servicoEmprestimoAlunoPosOutroCentro);
		
		// Prepara o movimento, setando o objeto
		MovimentoAtualizaBibliotecaInterna mov = new MovimentoAtualizaBibliotecaInterna(obj, servicosEmprestimos, obj.getServicos());
		mov.setCodMovimento(SigaaListaComando.ALTERAR_BIBLIOTECA_INTERNA);
		
		
		try {
			
			
			execute(mov);

			addMensagemInformation("Operação Realizada com sucesso");
			
			bibliotecaInternasAtivas = null; // Busca novamente as bibliotecas internas no banco

			// Retorna para a página de listagem.
			return forward(getListPage());
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}

		return null;
	}

	public BibliotecaMBean(){
		obj = new Biblioteca();
	}

	
	@Override
	public String forwardCadastrar(){
		return forward( getListPage() );
	}

	
	
	/**
	 * Retorna todas as biblioteca ativas do sistema.
	 * <br><br>
	 * Usado na página /biblioteca/biblioteca/lista.jsp
	 * @throws SegurancaException 
	 */
	public Collection <Biblioteca> getAllBibliotecasInternasAtivas() throws DAOException{
		
		BibliotecaDao dao = null;
		
		try{
		
			if (bibliotecaInternasAtivas == null){
				dao = getDAO(BibliotecaDao.class);
				
				bibliotecaInternasAtivas = dao.findAllBibliotecasInternasAtivas(); // Sempre mostra todas agora só os administrados tem permissão de alterar.
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return bibliotecaInternasAtivas;
	}

	/**
	 *  Retorna todas as bibliotecas internas ativas do sistema. São as únicas para as quais
	 *  pode-se criar inventários.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/InventarioAcervo/lista.jsp</li>
	 *    <li>/sigaa.war/biblioteca/InventarioAcervo/form.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAllCombo() throws DAOException{
		return toSelectItems(getAllBibliotecasInternasAtivas(), "id", "descricaoCompleta");
	}
	
	
	
	/**
	 * Retorna a quantidade de bibliotecas cadastradas e ativas para exibir na listagem.
	 * <br><br>
	 * Chamado na página /biblioteca/biblioteca/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public int getQuantidadeBibliotecasInternasAtivas () throws ArqException{
		return getAllBibliotecasInternasAtivas().size();
	}
	
	
	/**
	 * Ação chamada apenas para atualizar a página por ajax
	 * <br><br>
	 * Método chamado pela seguinte JSP: /biblioteca/Biblioteca/form.jsp
	 * @return
	 */
	public void atualizaPagina(ActionEvent event){
		// Não precisa fazer nada
	}
	
	
	/**
	 * Volta para a página da listagem.
	 * <br><br>
	 * Método chamado pela seguinte JSP: /biblioteca/Biblioteca/form.jsp
	 * @return
	 */
	public String voltar (){
		return forward(getListPage());
	}
	
	
	
	/**
	 *  Verifica se o usuário tem permissão de alteração das configurações da biblioteca.
	 *  Usado para desabilitar as operações na página de alteração da biblioteca.
	 * <br><br>
	 * Chamado a partir da página:  /biblioteca/Biblioteca/form.jsp
	 * @return
	 * @throws DAOException 
	 */
	public boolean isUsuarioTemPermissaoAlteracao() throws DAOException{
		
		if (isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			return true;
		}
			


		if (isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL)){
			
			try{
				if(obj != null){
					
					// VERIFICA SE O USUÁRIO TEM PERMISSÃO DE ADMINISTRADOR DA BIBLIOTECA SELECIONADA
					checkRole(obj.getUnidade(), SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
					
					return true;
				}
			}catch (SegurancaException se) {
				return false;
			}
		}

		return false;
	}


	
	public ServicosEmprestimosBiblioteca getServicoEmprestimoInstitucionalInterna() {
		return servicoEmprestimoInstitucionalInterna;
	}

	public void setServicoEmprestimoInstitucionalInterna(
			ServicosEmprestimosBiblioteca servicoEmprestimoInstitucionalInterna) {
		this.servicoEmprestimoInstitucionalInterna = servicoEmprestimoInstitucionalInterna;
	}

	public ServicosEmprestimosBiblioteca getServicoEmprestimoInstitucionalExterna() {
		return servicoEmprestimoInstitucionalExterna;
	}

	public void setServicoEmprestimoInstitucionalExterna(
			ServicosEmprestimosBiblioteca servicoEmprestimoInstitucionalExterna) {
		this.servicoEmprestimoInstitucionalExterna = servicoEmprestimoInstitucionalExterna;
	}


	public ServicosEmprestimosBiblioteca getServicoEmprestimoAlunoGraduacaoMesmoCentro() {
		return servicoEmprestimoAlunoGraduacaoMesmoCentro;
	}

	public void setServicoEmprestimoAlunoGraduacaoMesmoCentro(ServicosEmprestimosBiblioteca servicoEmprestimoAlunoGraduacaoMesmoCentro) {
		this.servicoEmprestimoAlunoGraduacaoMesmoCentro = servicoEmprestimoAlunoGraduacaoMesmoCentro;
	}

	public ServicosEmprestimosBiblioteca getServicoEmprestimoAlunoGraduacaoOutroCentro() {
		return servicoEmprestimoAlunoGraduacaoOutroCentro;
	}

	public void setServicoEmprestimoAlunoGraduacaoOutroCentro(ServicosEmprestimosBiblioteca servicoEmprestimoAlunoGraduacaoOutroCentro) {
		this.servicoEmprestimoAlunoGraduacaoOutroCentro = servicoEmprestimoAlunoGraduacaoOutroCentro;
	}

	public ServicosEmprestimosBiblioteca getServicoEmprestimoAlunoPosMesmoCentro() {
		return servicoEmprestimoAlunoPosMesmoCentro;
	}


	public void setServicoEmprestimoAlunoPosMesmoCentro(ServicosEmprestimosBiblioteca servicoEmprestimoAlunoPosMesmoCentro) {
		this.servicoEmprestimoAlunoPosMesmoCentro = servicoEmprestimoAlunoPosMesmoCentro;
	}


	public ServicosEmprestimosBiblioteca getServicoEmprestimoAlunoPosOutroCentro() {
		return servicoEmprestimoAlunoPosOutroCentro;
	}


	public void setServicoEmprestimoAlunoPosOutroCentro(ServicosEmprestimosBiblioteca servicoEmprestimoAlunoPosOutroCentro) {
		this.servicoEmprestimoAlunoPosOutroCentro = servicoEmprestimoAlunoPosOutroCentro;
	}
	
	
	
}