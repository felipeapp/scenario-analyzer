/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/04/2009
 * 
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import static br.ufrn.arq.util.StringUtils.isEmpty;
import static br.ufrn.arq.util.StringUtils.notEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ColecaoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.RegistroConsultasMateriaisDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ClasseMaterialConsultado;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroConsultasDiariasMateriais;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *    MBean responsável por cadastrar os materiais consultados diariamente pelo usuário
 * nas biblioteca do sistema.
 * 
 * @author Agostinho
 * @author Fred Castro
 * @author Bráulio
 */

@Component("registraConsultasDiariasMateriaisMBean")
@Scope("request")
public class RegistraConsultasDiariasMateriaisMBean extends SigaaAbstractController<RegistroConsultasDiariasMateriais> {
	
	/** Lista das classes a serem registradas. */
	private ArrayList <ClasseMaterialConsultado> listaClassesARegistrar = new ArrayList<ClasseMaterialConsultado>();
	
	/** Quantidade de materiais consultados informado pelo usuário */
	private Integer quantidade;
	
	/**  Os dados da 1ª classificação digitada pelo usuário (se o sistema utilizar ela ) */
	private String classificacao1 = "";
	
	/**  Os dados da 2ª classificação digitada pelo usuário (se o sistema utilizar ela ) */
	private String classificacao2 = "";
	
	/** Os dados da 3ª classificação digitada pelo usuário (se o sistema utilizar ela ) */
	private String classificacao3 = "";
	
	/** A classificação utilizada na biblioteca, serve para validar qual classificação o usuário vai poder 
	 * informar também pode ser utilizada para validar as classes principais a serem informadas pelo usuário. */
	private ClassificacaoBibliografica classificacaoDaBiblioteca;
	
	
	/**
	 * Prepara o formulário, indicando o comando correto e exibe o formulário.<br/>
	 * 
	 * Método chamado pela seguinte JSP: /biblioteca/menus/informacao_referencia.jsp
	 */
	public String iniciarRegistroConsultasDiarias() throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		iniciarDadosFormulario();
		prepareMovimento(SigaaListaComando.REGISTRAR_CONSULTAS_DIARIA_MATERIAIS);
		
		return forward(getFormPage());
	}
	
	
	
	/**
	 * Consulta  a quantidade e classes já cadastradas para a biblioteca coleção e tipo de material selecionado <br/>
	 * 
	 * Método chamado pela seguinte JSP: /biblioteca/RegistroConsultasDiariasMateriais/form.jsp
	 */
	public String exibirClassesCadastradas () throws DAOException {
		
		////  Guarda os dados que estavam antes do formulário ////
		int biblioteca = obj.getBiblioteca().getId();
		int tipoMaterial = obj.getTipoMaterial().getId();
		int colecao = obj.getColecao().getId();
		int turno = obj.getTurno();
		
		Date data = obj.getDataConsulta() == null ? new Date() : obj.getDataConsulta();
		RegistroConsultasMateriaisDao dao = null;
		ClassificacaoBibliograficaDao daoClassificacao = null;
		
		try {
			
			dao = getDAO(RegistroConsultasMateriaisDao.class);
			daoClassificacao = getDAO(ClassificacaoBibliograficaDao.class);
			
			if( obj.getBiblioteca().getId() > 0 ){
				classificacaoDaBiblioteca = daoClassificacao.findClassificacaoUtilizadaPelaBiblioteca(obj.getBiblioteca().getId());
			}
		
			if (obj.getBiblioteca().getId() > 0 && obj.getTipoMaterial().getId() > 0 && obj.getDataConsulta() != null && obj.getColecao().getId() > 0){
			
				obj = dao.findRegistroByBibliotecaTipoMaterialColecaoETurno(obj.getBiblioteca().getId(), obj.getTipoMaterial().getId(), obj.getColecao().getId(), obj.getTurno(), obj.getDataConsulta());
				
				if (obj == null){
					obj = new RegistroConsultasDiariasMateriais(new TipoMaterial(tipoMaterial), new Colecao(colecao), new Biblioteca(biblioteca), turno, data);
					
				}else{
					obj.setClassesConsultadas(dao.findRegistrosClassesAtivos(obj.getId()));
				}
			}
		
		} finally {
			if (dao != null) dao.close();
			if (daoClassificacao != null) daoClassificacao.close();
		}
		
		return forward(getFormPage());
	}
	
	
	
	
	
	/**
	 * Adiciona uma classe para ser cadastrada.
	 * Método chamado pela seguinte JSP: /biblioteca/RegistroConsultasDiariasMateriais/form.jsp
	 */
	public String adicionarClasseQuantidade() throws DAOException{
		
		if (validarEntradaParaRegistro(true)) {
			
			ClasseMaterialConsultado cmc = new ClasseMaterialConsultado();
			
			if ( isEmpty(classificacao1) ) classificacao1 = null;
			if ( isEmpty(classificacao2) ) classificacao2 = null;
			if ( isEmpty(classificacao3) ) classificacao3 = null;
			
			
			cmc.setClassificacao1(classificacao1);
			cmc.setClassificacao2(classificacao2);
			cmc.setClassificacao3(classificacao3);
			
			cmc.setQuantidade(quantidade);
			
			if (obj != null){
				
				
				/* **********************************************************************************
				 * Antes de adicionar, faz uma busca para garantir que o registro foi recuperado,
				 * já que não pode ser inseridos dois registros de consultas iguais no banco.
				 * Apenas novos registros de consultas de classes são adicionadas aos registros já existentes
				*************************************************************************************/
				RegistroConsultasDiariasMateriais temp = getDAO(RegistroConsultasMateriaisDao.class)
						.findRegistroByBibliotecaTipoMaterialColecaoETurno( obj.getBiblioteca().getId(), obj.getTipoMaterial().getId()
							, obj.getColecao().getId(), obj.getTurno(), obj.getDataConsulta());
				
				if (temp == null)
					obj.setClassesConsultadas(new ArrayList<ClasseMaterialConsultado>() );
				else{
					List<ClasseMaterialConsultado> classesRegistradas =  getDAO(RegistroConsultasMateriaisDao.class).findRegistrosClassesAtivos(temp.getId());
					
					if (classesRegistradas != null)
						obj.setClassesConsultadas( classesRegistradas );
					else
						obj.setClassesConsultadas(new ArrayList<ClasseMaterialConsultado>() );
				}
			
				if ( ! classePrincipalInformadaValida(classificacaoDaBiblioteca, classificacao1, classificacao2, classificacao3) )
					return null;
				
				if ( ! registroConsultaDuplicado(cmc) ) {
					listaClassesARegistrar.add(cmc);
					addMensagemInformation("Consulta adicionada.");
					
					apagaDadosFormulario();
				}
			}
			
		}
		
		return telaFormRegistraConsultasDiariasMateriaisMean();
		
	}



	/**  Apaga os dados digitados pelo usuário.*/
	private void apagaDadosFormulario() {
		classificacao1 = null;
		classificacao2 = null;
		classificacao3 = null;
		quantidade = null;
	}
	
	
	/**
	 * Verifica se entre os registro já salvos e os registros que estão sendo registrados agora, já existe a quantidade que o usuário está querendo informar.
	 * @return
	 */
	private boolean registroConsultaDuplicado(ClasseMaterialConsultado cmc) {
		
		if(classificacaoDaBiblioteca.isPrimeiraClassificacao() ){
		
			for (ClasseMaterialConsultado c : obj.getClassesConsultadas()){
				if ( notEmpty(c.getClassificacao1()) && c.getClassificacao1().equalsIgnoreCase( cmc.getClassificacao1()) ){
					addMensagemErro("Consulta da classe : \""+classificacao1+"\"  para a data "+new SimpleDateFormat("dd/MM/yyyy").format( obj.getDataConsulta())+" já registrada.");
					return true;
				}
			}
			
			for (ClasseMaterialConsultado c : listaClassesARegistrar){
				if ( notEmpty(c.getClassificacao1()) && c.getClassificacao1().equalsIgnoreCase( cmc.getClassificacao1()) ){
					addMensagemErro("Consulta da classe : \""+classificacao1+"\" para a data "+new SimpleDateFormat("dd/MM/yyyy").format( obj.getDataConsulta())+" já registrada.");
					return true;
				}
			}
		}
		
		if(classificacaoDaBiblioteca.isSegundaClassificacao() ){
			
			for (ClasseMaterialConsultado c : obj.getClassesConsultadas()){
				if ( notEmpty(c.getClassificacao2()) && c.getClassificacao2().equalsIgnoreCase( cmc.getClassificacao2()) ){
					addMensagemErro("Consulta da classe : \""+classificacao2+"\" para a data "+new SimpleDateFormat("dd/MM/yyyy").format( obj.getDataConsulta())+" já registrada.");
					return true;
				}
			}
			
			for (ClasseMaterialConsultado c : listaClassesARegistrar){
				if ( notEmpty(c.getClassificacao2()) && c.getClassificacao2().equalsIgnoreCase( cmc.getClassificacao2()) ){
					addMensagemErro("Consulta da classe : \""+classificacao2+"\" para a data "+new SimpleDateFormat("dd/MM/yyyy").format( obj.getDataConsulta())+" já registrada.");
					return true;
				}
			}
		}
		
		if(classificacaoDaBiblioteca.isTerceiraClassificacao() ){
			
			for (ClasseMaterialConsultado c : obj.getClassesConsultadas()){
				if ( notEmpty(c.getClassificacao3()) && c.getClassificacao3().equalsIgnoreCase( cmc.getClassificacao3()) ){
					addMensagemErro("Consulta da classe : \""+classificacao3+"\" para a data "+new SimpleDateFormat("dd/MM/yyyy").format( obj.getDataConsulta())+" já registrada.");
					return true;
				}
			}
			
			for (ClasseMaterialConsultado c : listaClassesARegistrar){
				if ( notEmpty(c.getClassificacao3()) && c.getClassificacao3().equalsIgnoreCase( cmc.getClassificacao3()) ){
					addMensagemErro("Consulta da classe : \""+classificacao3+"\" para a data "+new SimpleDateFormat("dd/MM/yyyy").format( obj.getDataConsulta())+" já registrada.");
					return true;
				}
			}
		}
		
		return false;
	}



	/**
	 * Verifica se o que foi digita pelo usuário é uma classe principal válida
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param classificacaoDaBiblioteca2
	 * @param classificacao12
	 * @param classificacao22
	 * @param classificacao32
	 */
	private boolean classePrincipalInformadaValida(ClassificacaoBibliografica classificacaoDaBiblioteca, String classificacao1, String classificacao2, String classificacao3) {
		if( classificacaoDaBiblioteca == null){
			addMensagemErro("Não foi possível recuperar a classificação utilizada na biblioteca para validar os dados digitados.");
			return false;
		}
		
		if(classificacaoDaBiblioteca.isPrimeiraClassificacao() 
				&& ! classificacaoDaBiblioteca.getClassesPrincipaisClassificacaoBibliografica().contains(classificacao1)){
			addMensagemErro("A classe principal informada não é válida. <br/><br/> Classes principais válidas: "+classificacaoDaBiblioteca.getClassesPrincipaisClassificacaoBibliografica());
			return false;
		}
		
		if(classificacaoDaBiblioteca.isSegundaClassificacao() 
				&& ! classificacaoDaBiblioteca.getClassesPrincipaisClassificacaoBibliografica().contains(classificacao2)){
			addMensagemErro("A classe principal informada não é válida. <br/><br/> Classes principais válidas: "+classificacaoDaBiblioteca.getClassesPrincipaisClassificacaoBibliografica());
			return false;
		}
		
		if(classificacaoDaBiblioteca.isTerceiraClassificacao() 
				&& ! classificacaoDaBiblioteca.getClassesPrincipaisClassificacaoBibliografica().contains(classificacao3)){
			addMensagemErro("A classe principal informada não é válida. <br/><br/> Classes principais válidas: "+classificacaoDaBiblioteca.getClassesPrincipaisClassificacaoBibliografica());
			return false;
		}
		
		return true;
		
	}



	/**
	 * Remove uma classe a adicionada pelo usuário mas que ainda não foi salva no banco.
	 * 
	 * Método chamado pela seguinte JSP: /biblioteca/RegistroConsultasDiariasMateriais/form.jsp
	 */
	public String removerClasseARegistrar(){
		
		String classificacao1ParaRemocao = getParameter("classificacao1ParaRemocao");
		String classificacao2ParaRemocao = getParameter("classificacao2ParaRemocao");
		String classificacao3ParaRemocao = getParameter("classificacao3ParaRemocao");
		
		boolean removido = false;
		
		
		for (int i = 0; i < listaClassesARegistrar.size(); i++){
			ClasseMaterialConsultado c = listaClassesARegistrar.get(i);
			
			if  ( ( notEmpty(classificacao1ParaRemocao) && c.getClassificacao1() != null&& c.getClassificacao1().equalsIgnoreCase(classificacao1ParaRemocao) ) 
					|| ( notEmpty(classificacao2ParaRemocao) && c.getClassificacao2() != null && c.getClassificacao2().equalsIgnoreCase(classificacao2ParaRemocao ) ) 
					|| ( notEmpty(classificacao3ParaRemocao) && c.getClassificacao3() != null && c.getClassificacao3().equalsIgnoreCase(classificacao3ParaRemocao) ) ){
				
				listaClassesARegistrar.remove(i);
				
				addMensagemInformation("Registro da Consultas removido com sucesso.");
				removido = true;
				break;
			}
		}
		
		if ( !removido )
			addMensagemErro("Este objeto já foi removido.");
		
		return telaFormRegistraConsultasDiariasMateriaisMean();
	}
	
	
	
	
	/**
	 * Remove um registro de consulta que já estava cadastrado no banco.
	 * 
	 * Método chamado pela seguinte JSP: /biblioteca/RegistroConsultasDiariasMateriais/form.jsp
	 */
	public String removerClasseRegistrada() throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			
			int id = getParameterInt("idClasse", 0);
			
			ClasseMaterialConsultado c = dao.findByPrimaryKey(id, ClasseMaterialConsultado.class);
			
			if (c != null){
				
				// desativa o registro feito.
				c.setAtivo(false);
				
				MovimentoCadastro mov = new MovimentoCadastro(c);
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				
				prepareMovimento(ArqListaComando.ALTERAR);
				
				execute(mov);
				
				obj.getClassesConsultadas().remove(c);
				
				addMensagemInformation("Consulta Registrada removida com sucesso.");
				
			} else{
				addMensagemErro("A consulta já foi removida.");
			}
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
		} finally {
			if (dao != null)dao.close();
		}
		
		return telaFormRegistraConsultasDiariasMateriaisMean();
	}

	
	
	/**
	 * Verifica se o usuário preencheu o formulário corretamente.
	 */
	private boolean validarEntradaParaRegistro(boolean validaDadosDigitados){
		
			boolean valido = true;
			
			// Se verdadeiro o usuário está adicionando um nova consulta
            // Senão está penas cadastrando as consulta digitadas anteriormente.
			if ( validaDadosDigitados ) {
				
				if ( isEmpty(classificacao1) && isEmpty(classificacao2) && isEmpty(classificacao3) ) {
					addMensagemErro(" É necessário informar a classe principal do material");
					valido = false;
				}
				
				if (quantidade == null || quantidade <= 0) {
					addMensagemErro("Quantidade consultada: É necessário informar a quantidade. Esta deve ser superior a zero.");
					valido = false;
				}
			}
			
			if (obj.getBiblioteca().getId() == -1) {
				addMensagemErro("Biblioteca: Selecione uma biblioteca. ");
				valido = false;
			}
			
			if (obj.getColecao().getId() == -1) {
				addMensagemErro("Coleção: Selecione uma coleção.");
				valido = false;
			}
			
			if(obj.getTipoMaterial().getId() == -1){
				addMensagemErro("Tipo de Material: Selecione um tipo de material.");
				valido = false;
			}
			
			if(obj.getDataConsulta() == null){
				addMensagemErro("Data da Consulta: Selecione a data da consulta.");
				valido = false;
			}

		return valido;
	}

	/** Inicia os dados do formulário. */
	private void iniciarDadosFormulario(){
		obj = new RegistroConsultasDiariasMateriais (new TipoMaterial(-1), new Colecao(-1),  new Biblioteca(-1), 1, new Date());
		classificacaoDaBiblioteca = null;
	}
	
	/**
	 * Cadastra um registro de consultas a materiais.<br/>
	 * Se o registro já existe, é apenas adicionado uma nova classe de consulta, senão o registro é criado
	 * 
	 * Método chamado pela seguinte JSP: /biblioteca/RegistroConsultasDiariasMateriais/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
		
		if (listaClassesARegistrar.isEmpty()){
			addMensagemErro("Nenhum registro de consulta novo foi adicionado.");
			return telaFormRegistraConsultasDiariasMateriaisMean();
		}
		
		if (validarEntradaParaRegistro(false)){
			
			for (ClasseMaterialConsultado classeAdicionada : listaClassesARegistrar) {
				if (obj.getClassesConsultadas().contains(classeAdicionada)) {
					addMensagemErro ("A consulta da classe "
							+ ( notEmpty(classeAdicionada.getClassificacao1()) ? classeAdicionada.getClassificacao1() 
									: ( notEmpty(classeAdicionada.getClassificacao2()) ? classeAdicionada.getClassificacao2() 
											: classeAdicionada.getClassificacao3() ) )
							+" já foi registrada.");
					return telaFormRegistraConsultasDiariasMateriaisMean();
				}
			}
			
			
			for (ClasseMaterialConsultado classeConsulta : listaClassesARegistrar) {
				classeConsulta.setRegistroConsultaMaterial(obj);
			}
			
			MovimentoCadastro mov = new MovimentoCadastro(obj);
			mov.setObjAuxiliar(classificacaoDaBiblioteca);
			mov.setColObjMovimentado(listaClassesARegistrar);
			mov.setCodMovimento(SigaaListaComando.REGISTRAR_CONSULTAS_DIARIA_MATERIAIS);
			
			try {
				
				execute(mov);
				
				addMensagemInformation("Registro de consulta cadastrado com sucesso!");
				apagaDadosFormulario();
				
				return cancelar();
				
			} catch (NegocioException ne) {
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens());
				return null;
			}
		
			
		}else{
			
			return telaFormRegistraConsultasDiariasMateriaisMean();
		}
				
	}

	
	
	
	//////////////////////// tela de navegação //////////////////////////
	
	
	/**
	 *   Retorna para o formulário de registro da consulta de materiais
	 * 
	 *   Chamado a partir da página: /sigaa.war/biblioteca/RegistroConsultasDiariasMateriais/form.jsp
	 */
	public String telaFormRegistraConsultasDiariasMateriaisMean(){
		return forward(getFormPage());
	}
	
	///////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 * 
	 *    Retorna todas as bibliotecas internas.
	 *    No caso da alteração, aparece todas as bibliotecas porque o usuário pode alterar a biblioteca
	 *    do material.
	 *
	 *   Método chamado pela seguinte JSP: /biblioteca/RegistroConsultasDiariasMateriais/form.jsp
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		
		Collection <Biblioteca> b = new ArrayList<Biblioteca>();
		
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
			List<Integer> idUnidades = new ArrayList<Integer>();
			idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO));
			idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO));
			idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO));
			idUnidades.addAll(BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF));
			
			
			b  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
			
		}else{
			b  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		}
		
		return toSelectItems(b, "id", "descricaoCompleta");
	}
	
	
	/**
	 * Método chamado pela seguinte JSP: /biblioteca/ConsultasDiarioMateriais/form.jsp
	 * Retorna a lista das Coleções ativas, ordenadas pela descrição.
	 */
	public List <SelectItem> getColecoes () throws DAOException{
		Collection <Colecao> cs = getDAO(ColecaoDao.class).findColecoesAtivasParaRegistroConsulta();
		return toSelectItems(cs, "id", "descricao");
	}
	
	
	
	/**
	 * Método chamado pela seguinte JSP: /biblioteca/ConsultasDiarioMateriais/form.jsp
	 * Retorna a lista dos Tipos de Material ativos, ordenados por descrição
	 */
	public List <SelectItem> getTiposMaterial () throws DAOException{
		Collection <TipoMaterial> ts = getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true, "asc", "descricao");
		return toSelectItems(ts, "id", "descricao");
	}

	
	/////////////// sets e gets /////////////////
	
	
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	public String getClassificacao1() {
		return classificacao1;
	}

	public void setClassificacao1(String classificacao1) {
		this.classificacao1 = classificacao1;
	}

	public String getClassificacao2() {
		return classificacao2;
	}

	public void setClassificacao2(String classificacao2) {
		this.classificacao2 = classificacao2;
	}

	public String getClassificacao3() {
		return classificacao3;
	}

	public void setClassificacao3(String classificacao3) {
		this.classificacao3 = classificacao3;
	}

	public ArrayList <ClasseMaterialConsultado> getListaClassesARegistrar() {
		return listaClassesARegistrar;
	}

	public void setListaClassesARegistrar(ArrayList<ClasseMaterialConsultado> listaClassesARegistrar) {
		this.listaClassesARegistrar = listaClassesARegistrar;
	}

	public ClassificacaoBibliografica getClassificacaoDaBiblioteca() {
		return classificacaoDaBiblioteca;
	}

	public void setClassificacaoDaBiblioteca(ClassificacaoBibliografica classificacaoDaBiblioteca) {
		this.classificacaoDaBiblioteca = classificacaoDaBiblioteca;
	}
	
	
	
}