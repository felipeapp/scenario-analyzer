/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.AlteracaoFotoCandidato;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.StatusFoto;

/**
 * Controller responsável pela validação da foto 3x4 enviada pelo candidato. <br/>
 * A COMPERVE (Comissão Permanente do Vestibular) verifica se o arquivo enviado
 * pelo candidato se encontra no padrão de fotos para documentos, isto é, de
 * busto, sem elementos que impeçam a identificação (óculos, chapéu, boné, etc),
 * plano de fundo neutro, etc.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("validacaoFotoBean")
@Scope("session")
public class ValidacaoFotoCandidatoMBean extends SigaaAbstractController<PessoaVestibular> {
	
	/** Filtra a busca por inscrição restringindo a consulta por nome do candidato. */
	private boolean filtroNome;
	/** Filtra a busca por inscrição restringindo a consulta pelo CPF do candidato. */
	private boolean filtroCPF;
	/** Filtra a busca por inscrição restringindo a consulta pelo status da foto. */
	private boolean filtroStatus;
	/** Arquivo de foto 3x4 do candidato. */
	private UploadedFile foto;
	/** Arquivo enviado pelo usuário, a ser utilizado como foto 3x4. */
	private UploadedFile arquivoUpload;
	/** Mapa de dados da foto */
	private Map<String,Object> dadosFoto;
	/** Coleção de pessoa Vestibular */
	List<PessoaVestibular> pessoaVestibular = new ArrayList<PessoaVestibular>();
	/** Posição que se encontra a foto do candidato */
	private int posicao = 0;
	
	/** Construtor padrão. */
	public ValidacaoFotoCandidatoMBean() {
		init();
	}

	/**
	 * Busca por cadastros de candidatos do vestibular de acordo com os
	 * parâmetros informados pelo usuário. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/StatusFoto/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.BOLSISTA_VESTIBULAR);
		String nome = null;
		Long cpf = null;
		if (!filtroCPF && !filtroStatus && !filtroNome)
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		if (hasOnlyErrors()) return null;
		int idStatus = 0;
		if (filtroCPF) {
			ValidatorUtil.validateCPF_CNPJ(obj.getCpf_cnpj(), "CPF", erros);
			cpf = obj.getCpf_cnpj();
		}
		if (filtroNome) {
			ValidatorUtil.validateRequired(obj.getNome(), "Nome", erros);
			nome = obj.getNome();
		}
		if (filtroStatus) {
			ValidatorUtil.validateRequired(obj.getStatusFoto(), "Status", erros);
			idStatus = obj.getStatusFoto().getId();
		}
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		resultadosBusca = dao.findByNomeCpf(nome, cpf, idStatus);
		getPaginacao().setTotalRegistros(resultadosBusca.size());
		getPaginacao().setPaginaAtual(0);
		if (resultadosBusca.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return forward(getListPage());
	}

	/**
	 * Retorna uma coleção de objetos de acordo com os parâmetros utilizados na
	 * busca.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/ValidacaoFoto/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getResultadosBusca()
	 */
	@Override
	public Collection<PessoaVestibular> getResultadosBusca() {
		Collection<PessoaVestibular> pagina = new ArrayList<PessoaVestibular>();
		if (!ValidatorUtil.isEmpty(resultadosBusca)) {
			Iterator<PessoaVestibular> iterator = resultadosBusca.iterator();
			int k = 0;
			// avança
			while (k++ < getPaginacao().getPaginaAtual() * getTamanhoPagina() && iterator.hasNext()) 
				iterator.next();
			// monta a página
			k = 0;
			while (k++ < getTamanhoPagina() && iterator.hasNext()) {
				PessoaVestibular pessoa = iterator.next();
				pagina.add(pessoa);
			}
		}
		return pagina;
	}
	
	/**
	 * Realiza a atualização da foto do candidato.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/ValidacaoFoto/atualizar_foto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String atualizarFoto() throws Exception{
		enviarArquivo();
		obj.setImagem(foto);
		atualizarFotoCandidato();
		return buscar();
	}

	/**
	 * Realizar a inserção da foto do candidato e atualiza o registro do candidato.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Método não invocado por JSP.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	public void atualizarFotoCandidato() throws Exception{
		PessoaVestibular pessoaOld = getGenericDAO().findByPrimaryKey(obj.getId(), PessoaVestibular.class);
		try {
			if (obj.getImagem() != null ) {
				// remove a foto anterior
				if (obj.getIdFoto() != null) {
					EnvioArquivoHelper.removeArquivo(obj.getIdFoto());
				}
				int idFoto = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idFoto, obj.getImagem().getBytes(),
						obj.getImagem().getContentType(), obj.getImagem().getName());
				obj.setIdFoto( idFoto );
				if (pessoaOld.getNovoStatusFoto().getId() == obj.getNovoStatusFoto().getId()) 
					getGenericDAO().updateField(PessoaVestibular.class, obj.getId(), "idFoto", obj.getIdFoto());
				else {
				getGenericDAO().updateFields(PessoaVestibular.class, obj.getId(),
						new String[] {"statusFoto", "idFoto"}, 
						new Object[] {obj.getNovoStatusFoto().getId(), obj.getIdFoto()});
				}
				AlteracaoFotoCandidato alteracaoFotoCandidato = new AlteracaoFotoCandidato();
				alteracaoFotoCandidato.setPessoaVestibular(obj.getId());
				alteracaoFotoCandidato.setNovoIdFoto(obj.getIdFoto());
				alteracaoFotoCandidato.setIdFoto(pessoaOld.getIdFoto());
				alteracaoFotoCandidato.setDataCadastro(new Date());
				alteracaoFotoCandidato.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
				getGenericDAO().create(alteracaoFotoCandidato);
				getGenericDAO().clearSession();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Serve para o envio em lote das fotos os candidatos do vestibular.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	  <li>/sigaa.war/vestibular/ValidacaoFoto/atualizacao_foto_lote.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws IOException
	 * @throws DAOException
	 */
	public String subAtualizacaoFotoLote() throws IOException, DAOException{
		Long cpf = StringUtils.extractLong(arquivoUpload.getName());
		enviarArquivo();
		if (hasOnlyErrors()) return null;
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		try {
			obj = dao.findByCPF(cpf);
			if (obj == null) {
				addMensagemErro("Candidato não encontrado");
				return null;
			}
			obj.setImagem(foto);
			if (pessoaVestibular.contains(obj)){ 
				addMensagemErro("CPF já presente na lista de alteração da foto.");
				return null;
			}
			pessoaVestibular.add(obj);
			obj = new PessoaVestibular();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dao.close();
		}
		return redirectMesmaPagina();
	}
	
	/**
	 * Atualização das fotos em lote.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	  <li>/sigaa.war/vestibular/ValidacaoFoto/atualizacao_foto_lote.jsp</li>
	 * </ul>
	 */
	public String atualizacaoFotoLote() throws Exception{
		if (pessoaVestibular.size() == 0) {
			addMensagemErro("É necessário informar pelo menos uma foto");
			return redirectMesmaPagina();
		}
		for (PessoaVestibular p : pessoaVestibular) {
			obj = p;
			atualizarFotoCandidato();
		}
		addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "foto(s)");
		init();
		return redirectMesmaPagina();
	}
	
	/** 
	 * Atualização das fotos em lote
	 * 
	 * <ul>
	 * 	<li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 */
	public String iniciarAtualizacaoFotoLote(){
		init();
		posicao = 0;
		return forward("/vestibular/ValidacaoFoto/atualizacao_foto_lote.jsp"); 
	}
	
	/**
	 * Usado para recarregar a listagem.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/vestibular/ValidacaoFoto/atualizar_foto.jsp
	 */
	public String carregarListagem() throws Exception{
		getGenericDAO().clearSession();
		return buscar(); 
	}
	
	/** Atualiza os status das fotos dos candidato do vestibular, de acordo com a avaliação do usuário.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/StatusFoto/lista.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String atualizar() throws ArqException {
		try {
			checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.BOLSISTA_VESTIBULAR);
			prepareMovimento(SigaaListaComando.ATUALIZAR_STATUS_FOTO);
			setOperacaoAtiva(SigaaListaComando.ATUALIZAR_STATUS_FOTO.getId());
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setRegistroEntrada(getRegistroEntrada());
			mov.setCodMovimento(SigaaListaComando.ATUALIZAR_STATUS_FOTO);
			mov.setColObjMovimentado(getResultadosBusca());
			execute(mov);
			atualizaResultadoBusca();
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
			return null;
	}
	
	/** 
	 * Recebe o upload e valida a foto 3x4 do candidato.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Método não invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws IOException
	 */
	public String enviarArquivo() throws IOException {
		int largura = 0;
		int altura = 0;
		double resolucao = 0.0;
		Map<String, Object> dadosFoto = new LinkedHashMap<String, Object>();
		if (arquivoUpload != null) {
			if ("image/jpeg".equalsIgnoreCase(arquivoUpload.getContentType())
					||"image/pjpeg".equalsIgnoreCase(arquivoUpload.getContentType())) {
				BufferedImage imagem = null; 
				try{
					imagem = ImageIO.read(arquivoUpload.getInputStream());
				} catch (Exception e) {
					addMensagemErro("Não foi possível abrir o arquivo enviado");
					return null;
				}
				dadosFoto = new LinkedHashMap<String, Object>();
				largura = imagem.getWidth(null);
				altura = imagem.getHeight(null);
				dadosFoto.put("Lagura", largura);
				dadosFoto.put("Altura", altura);
				// resolução mínima
				resolucao = (double) largura * altura / 1000;
				if (resolucao < 1000)
					dadosFoto.put("Resolução", String.format("%.1f KPixel", resolucao));
				else
					dadosFoto.put("Resolução", String.format("%.1f MPixel", resolucao / 1000));
				// tamanho do arquivo
				double tamanho = arquivoUpload.getBytes().length / 1024;
				if (tamanho < 1024)
					dadosFoto.put("Tamanho do Arquivo", String.format("%.1f KBytes",tamanho));
				else
					dadosFoto.put("Tamanho do Arquivo", String.format("%.1f MBytes", tamanho/1024));
			} else {
				addMensagemErro("Arquivo informado não é arquivo de foto válido. Envie um arquivo no formato JPG.");
			}
		} else {
			addMensagemErro("Por favor, envie um arquivo com a foto.");
		}
		if (!hasOnlyErrors()) {
			foto = arquivoUpload;
			this.dadosFoto = dadosFoto;
		}
		return null;
	}

	/**
	 * Carrega o candidato do vestibular.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/ValidacaoFoto/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String carregarCandidato() throws DAOException {
		obj.setId(getParameterInt("id"));
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), PessoaVestibular.class));
		return forward("/vestibular/ValidacaoFoto/atualizar_foto.jsp");

	}
	
	/** Atualiza o resultado da busca, após a alteração de status dos 
	 * candidatos, atualizando os status das fotos alterados.
	 *  
	 * @throws DAOException 
	 */
	private void atualizaResultadoBusca() throws DAOException {
		GenericDAO dao = getGenericDAO();
		for (PessoaVestibular pessoa : getResultadosBusca()) {
			PessoaVestibular atualizada = dao.refresh(pessoa);
			pessoa.setStatusFoto(atualizada.getStatusFoto());
			pessoa.setNovoStatusFoto(new StatusFoto());
		}
	}

	/**
	 * Redireciona o usuário para o formulário de listagem e busca por fotos não
	 * validadas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		init();
		return super.listar();
	}
	
	/** Inicializa os atributos do controller. */
	private void init() {
		obj = new PessoaVestibular();
		obj.setStatusFoto(new StatusFoto());
		this.filtroCPF = false;
		this.filtroNome = false;
		this.filtroStatus = false;
		resultadosBusca = null;
		pessoaVestibular = new ArrayList<PessoaVestibular>();
	}

	/** 
	 * Retorna o link para a página de listagem de fotos 3x4.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Método não invocado por JSP.</li>
	 * </ul>
	 * 
	 */
	@Override
	public String getListPage() {
		return "/vestibular/ValidacaoFoto/lista.jsp";
	}
	
	/** Retorna a quantidade de fotos pendente de validação.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menu_bolsista.jsp</li>
	 * <li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getQtdPendenteValidacao() {
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		int qtd = dao.countPendenteValidacao();
		return qtd;
	}

	/** Retorna uma coleção de selectItem de possíveis tamanhos de páginas de resultado da busca.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/ValidacaoFoto/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getTamanhoPaginaCombo(){
		Collection<SelectItem> tamanhos = new ArrayList<SelectItem>();
		tamanhos.add(new SelectItem(Integer.valueOf(10), "10"));
		tamanhos.add(new SelectItem(Integer.valueOf(16), "16"));
		tamanhos.add(new SelectItem(Integer.valueOf(20), "20"));
		return tamanhos;
	}
	
	/** 
	 * Retorna o filtro da busca por inscrição restringindo a consulta por nome do candidato. 
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/ValidacaoFoto/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isFiltroNome() {
		return filtroNome;
	}

	/** 
	 * Seta o filtro da busca por inscrição restringindo a consulta por nome do candidato. 
	 * @param filtroNome
	 */
	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	/** Retorna o filtro da busca por inscrição restringindo a consulta pelo CPF do candidato. 
	 * @return
	 */
	public boolean isFiltroCPF() {
		return filtroCPF;
	}
 
	/** Filtra a busca por inscrição restringindo a consulta pelo CPF do candidato. 
	 * @param filtroCPF
	 */
	public void setFiltroCPF(boolean filtroCPF) {
		this.filtroCPF = filtroCPF;
	}

	/** Retorna o filtro da busca por inscrição restringindo a consulta pelo status da foto. 
	 * @return
	 */
	public boolean isFiltroStatus() {
		return filtroStatus;
	}

	/** Filtra a busca por inscrição restringindo a consulta pelo status da foto. 
	 * @param filtroStatus
	 */
	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	public UploadedFile getArquivoUpload() {
		return arquivoUpload;
	}

	public void setArquivoUpload(UploadedFile arquivoUpload) {
		this.arquivoUpload = arquivoUpload;
	}

	public Map<String, Object> getDadosFoto() {
		return dadosFoto;
	}

	public void setDadosFoto(Map<String, Object> dadosFoto) {
		this.dadosFoto = dadosFoto;
	}

	public Collection<PessoaVestibular> getPessoaVestibular() {
		return pessoaVestibular;
	}

	public void setPessoaVestibular(List<PessoaVestibular> pessoaVestibular) {
		this.pessoaVestibular = pessoaVestibular;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

}