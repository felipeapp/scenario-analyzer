/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/07/2009
 *
 */
package br.ufrn.sigaa.ensino.infantil.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.ALTERADO_COM_SUCESSO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.CADASTRADO_COM_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.web.jsf.OrderedSelectItem;
import br.ufrn.rh.dominio.Escolaridade;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.infantil.dominio.DiscenteInfantil;
import br.ufrn.sigaa.ensino.infantil.dominio.RendaFamiliar;
import br.ufrn.sigaa.ensino.infantil.dominio.ResponsavelDiscenteInfantil;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/**
 * Managed Bean para operações de cadastro de alunos do ensino infantil 
 * 
 * @author Leonardo Campos
 *
 */
@Component
@Scope("request")
public class DiscenteInfantilMBean extends SigaaAbstractController<DiscenteInfantil> implements OperadorDiscente {

    // Lista dos municípios da UF atual
    private Collection<SelectItem> municipiosNaturalidade = new ArrayList<SelectItem>(0);
    private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
    
    // Utilizado para a renderização dos campos estado e município para outros países
    private boolean brasil = true;
        
    public DiscenteInfantilMBean() throws DAOException {
        initObj();
    }

    /**
     * Inicializa objetos necessários e define valores padrões para
     * alguns deles
     * @throws DAOException 
     */
    private void initObj() throws DAOException {
    	
    	int cepPadrao =  ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.CEP_PADRAO );
    	int dddPadrao = ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.DDD_PADRAO );
    	
        CalendarioAcademico cal = getCalendarioVigente();
        if(cal == null){
            cal = new CalendarioAcademico();
            cal.setAno( CalendarUtils.getAnoAtual() );
            cal.setPeriodo( getPeriodoAtual() );
        }
        obj = new DiscenteInfantil();
        obj.setAnoIngresso(cal.getAno());
        obj.setPeriodoIngresso(cal.getPeriodo());
        obj.setNivel(NivelEnsino.INFANTIL);
        obj.setRendaFamiliar( new RendaFamiliar());
        
        if (obj.getPessoa().getSexo() != 'M' && obj.getPessoa().getSexo() != 'F')
            obj.getPessoa().setSexo('M');
        if (obj.getPessoa().getUnidadeFederativa() == null || obj.getPessoa().getUnidadeFederativa().getId() == 0)
            obj.getPessoa().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
        if (obj.getPessoa().getMunicipio() == null || obj.getPessoa().getMunicipio().getId() == 0)
            obj.getPessoa().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
        if (obj.getPessoa().getEnderecoContato() == null || obj.getPessoa().getEnderecoContato().getId() == 0) {
            obj.getPessoa().setEnderecoContato(new Endereco());
            obj.getPessoa().getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
            obj.getPessoa().getEnderecoContato().setCep(cepPadrao + "");
            obj.getPessoa().getEnderecoContato().setTipoLogradouro(new TipoLogradouro(TipoLogradouro.RUA));
            obj.getPessoa().setCodigoAreaNacionalTelefoneCelular((short) dddPadrao);
            obj.getPessoa().setCodigoAreaNacionalTelefoneFixo((short) dddPadrao);
            obj.getPessoa().getEnderecoContato().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
        }
        
        if (obj.getPessoa().getEnderecoContato() != null && obj.getPessoa().getEnderecoContato().getUnidadeFederativa() == null) {
            obj.getPessoa().getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
        }
        
        if (obj.getPessoa().getEnderecoContato() != null && obj.getPessoa().getEnderecoContato().getMunicipio() == null) {
            obj.getPessoa().getEnderecoContato().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
        }
        
        if (obj.getPessoa().getPais() == null || obj.getPessoa().getPais().getId() == 0)
            obj.getPessoa().setPais(new Pais(Pais.BRASIL));
        
        if (obj.getResponsavel().getPessoa().getSexo() != 'M' && obj.getResponsavel().getPessoa().getSexo() != 'F')
            obj.getResponsavel().getPessoa().setSexo('M');
        
        if (obj.getOutroResponsavel().getPessoa().getSexo() != 'M' && obj.getOutroResponsavel().getPessoa().getSexo() != 'F')
            obj.getOutroResponsavel().getPessoa().setSexo('M');
    }

    /**
     * Popula campos de municípios que serão utilizados no formulário
     * 
     * @throws DAOException
     */
    private void popularMunicipios() throws DAOException {
        // Popular municípios para campo de naturalidade
        int uf = UnidadeFederativa.ID_UF_PADRAO;
        if (obj.getPessoa().getUnidadeFederativa() != null && obj.getPessoa().getUnidadeFederativa().getId() > 0)
            uf = obj.getPessoa().getUnidadeFederativa().getId();
        carregarMunicipiosNaturalidade(uf); 
        
        //Popular municípios para campo de endereço
        uf = UnidadeFederativa.ID_UF_PADRAO;
        if (obj.getPessoa().getEnderecoContato() != null 
                && obj.getPessoa().getEnderecoContato().getUnidadeFederativa() != null 
                && obj.getPessoa().getEnderecoContato().getUnidadeFederativa().getId() > 0) {
            uf = obj.getPessoa().getEnderecoContato().getUnidadeFederativa().getId();
        }
        carregarMunicipiosEndereco(uf);
    }
    
    /**
     * Inicia o caso de uso. <br /><br />
     * JSP: /infantil/menu.jsp
     * @return
     * @throws ArqException 
     */
    public String iniciar() throws ArqException {
        checkRole(SigaaPapeis.GESTOR_INFANTIL);
        initObj();
        popularMunicipios();
        prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
        setOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId());
        setConfirmButton("Cadastrar");
        return forward(ConstantesNavegacaoInfantil.DISCENTE_INFANTIL_FORM);
    }
    
    /**
     * Persiste um novo aluno no banco. <br />
     * JSP: /infantil/DiscenteInfantil/form.jsp
     */
    public String cadastrar() throws SegurancaException, ArqException,
            NegocioException {
        if( !checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId(),SigaaListaComando.ALTERAR_DISCENTE_INFANTIL.getId()) ) {
            return forward(ConstantesNavegacaoInfantil.MENU_INFANTIL);
        }
        erros = new ListaMensagens();
        ListaMensagens lista = obj.validate();

        if (lista != null && !lista.isEmpty())
            erros.addAll(lista.getMensagens());

        if (!hasErrors()) {

            Comando comando = SigaaListaComando.CADASTRAR_DISCENTE;
            if( obj.getId() > 0 )
                comando = SigaaListaComando.ALTERAR_DISCENTE_INFANTIL;
            DiscenteMov mov = new DiscenteMov(comando, obj);
            try {
                execute(mov);
                if( comando.equals(SigaaListaComando.CADASTRAR_DISCENTE) )
                    addMensagem(CADASTRADO_COM_SUCESSO, "Discente Infantil");
                else
                    addMensagem(ALTERADO_COM_SUCESSO, "Discente Infantil");
            } catch (NegocioException e) {
                addMensagens( e.getListaMensagens() );
                if(obj.getOutroResponsavel() != null && obj.getOutroResponsavel().getEscolaridade() == null)
                	obj.getOutroResponsavel().setEscolaridade(new Escolaridade());
                return null;
            }
            removeOperacaoAtiva();
            return forward(ConstantesNavegacaoInfantil.MENU_INFANTIL);
            
        } else {
            return null;
        }
    }
    
    /**
	 * Retorna uma coleção de itens das Escolaridades
	 * <br><br>
	 * JSP: sigaa.war/infantil/DiscenteInfantil/form.jsp
	 */
    public Collection<SelectItem> getEscolaridadesCombo(){
        return toSelectItems(Escolaridade.getElementosPrincipais(), "id", "descricao");
    }
    
    /**
     * Decide como carregar as opções de municípios no formulário de acordo com os métodos
     * presentes aqui. <br /><br />
     * JSP: /infantil/DiscenteInfantil/form.jsp
     * @param e
     * @throws DAOException
     */
    public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
        String selectId = e.getComponent().getId();
        if (selectId != null && e.getNewValue() != null) {
            Integer ufId = (Integer) e.getNewValue();
            if (selectId.toLowerCase().contains("natur")) {
                carregarMunicipiosNaturalidade(ufId);
            } else if (selectId.toLowerCase().contains("end")) {
                carregarMunicipiosEndereco(ufId);
            }
        }
    }
    
    /**
     * Carrega as opções de municípios no formulário de naturalidade de acordo com a
     * unidade federativa selecionada. <br /><br />
     * O método não é invocado por nenhuma JSP
     * @param idUf
     * @throws DAOException
     */
    public void carregarMunicipiosNaturalidade(Integer idUf) throws DAOException {
        if ( idUf == null ) {
            idUf = obj.getPessoa().getUnidadeFederativa().getId();
        }

        MunicipioDao dao = getDAO(MunicipioDao.class);
        UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
        Collection<Municipio> municipios = dao.findByUF(idUf);
        municipiosNaturalidade = new ArrayList<SelectItem>(0);
        if (uf != null) {
            municipiosNaturalidade.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
            municipiosNaturalidade.addAll(toSelectItems(municipios, "id", "nome"));
        }
    }

    /**
     * Carrega as opções de municípios no formulário de endereço de acordo com a
     * unidade federativa selecionada. <br /><br />
     * O método não é invocado por nenhuma JSP
     * @param idUf
     * @throws DAOException
     */
    public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
        if ( idUf == null ) {
            idUf = obj.getPessoa().getEnderecoContato().getUnidadeFederativa().getId();
        }

        MunicipioDao dao = getDAO(MunicipioDao.class);
        UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
        Collection<Municipio> municipios = dao.findByUF(idUf);
        municipiosEndereco = new ArrayList<SelectItem>(0);
        municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
        municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
    }
    
    /**
     * Carrega as informações do Responsável do Discente Infantil. <br /><br />
     * JSP: /infantil/DiscenteInfantil/form.jsp
     * @throws DAOException
     */
    public void carregarDadosDiscente() throws DAOException{
    	boolean cpf_Cnpj_Validado = ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(obj.getPessoa().getCpf_cnpj());
    	PessoaDao pessoaDao = getDAO(PessoaDao.class);
    	try {
    		Long cpf_cnpj = obj.getPessoa().getCpf_cnpj();
    		if (cpf_Cnpj_Validado && cpf_cnpj != null && cpf_cnpj != 0){
    			Pessoa p = pessoaDao.findByCpf(obj.getPessoa().getCpf_cnpj());
    			if (p != null){
    				pessoaDao.refresh(p.getPais());
    				obj.setPessoa(p);
    			}
    		}
    		else{
    			addMensagemErro("CPF inválido: digite um CPF Válido.");
    		}
    	} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
		} finally {
			pessoaDao.close();
		}
    }

    /**
     * Carrega as informações do Responsável do Discente Infantil. <br /><br />
     * JSP: /infantil/DiscenteInfantil/form.jsp
     * @throws DAOException
     */
    public void carregarDadosResponsavel() throws DAOException{
    	boolean cpf_Cnpj_Validado = ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(obj.getResponsavel().getPessoa().getCpf_cnpj());
    	PessoaDao pessoaDao = getDAO(PessoaDao.class);
    	try {
    		Long cpf_cnpj = obj.getResponsavel().getPessoa().getCpf_cnpj();
    		if (cpf_Cnpj_Validado && cpf_cnpj != null && cpf_cnpj != 0){
    			Pessoa p = pessoaDao.findByCpf(obj.getResponsavel().getPessoa().getCpf_cnpj());
    			if (p != null)
    				obj.getResponsavel().setPessoa(p);
    		}
    		else{
    			addMensagemErro("CPF inválido: digite um CPF Válido.");
    		}
    	} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
		} finally {
			pessoaDao.close();
		}
    }
    
    /**
     * Carrega as informações do Outro Responsável do Discente Infantil. <br /><br />
     * JSP: /infantil/DiscenteInfantil/form.jsp
     * @throws DAOException
     */
    public void carregarDadosOutroResponsavel() throws DAOException{
    	boolean cpf_Cnpj_Validado = ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(obj.getOutroResponsavel().getPessoa().getCpf_cnpj());
    	PessoaDao pessoaDao = getDAO(PessoaDao.class);
    	try {
    		Long cpf_cnpj = obj.getOutroResponsavel().getPessoa().getCpf_cnpj();
    		if (cpf_Cnpj_Validado && cpf_cnpj != null && cpf_cnpj != 0){
    			Pessoa p = pessoaDao.findByCpf(obj.getOutroResponsavel().getPessoa().getCpf_cnpj());
    			if(p != null)
    				obj.getOutroResponsavel().setPessoa(p);
    		}
    		else{
    			addMensagemErro("CPF inválido: digite um CPF Válido.");
    		}
    	} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
		} finally {
			pessoaDao.close();
		}
    }
    
    
    /**
     * Ao se alterar o País no formulário, é verificado se este é Brasil ou não.
     * Caso positivo, é inicializado um Município e uma Unidade Federativa para a Pessoa alterada.
     * JSP: /infantil/DiscenteInfantil/form.jsp
     * @param e
     * @throws DAOException
     */
    public void alterarPais(ValueChangeEvent e) throws DAOException {
        Integer idPais = (Integer) e.getNewValue();
        brasil = (idPais == Pais.BRASIL);
        if (brasil && obj.getPessoa().getMunicipio() == null) {
            obj.getPessoa().setMunicipio(new Municipio());
            obj.getPessoa().setUnidadeFederativa( new UnidadeFederativa() );
        }
    }

    public boolean isBrasil() {
        return brasil;
    }

    public void setBrasil(boolean brasil) {
        this.brasil = brasil;
    }

    public Collection<SelectItem> getMunicipiosNaturalidade() {
        return municipiosNaturalidade;
    }

    public void setMunicipiosNaturalidade(
            Collection<SelectItem> municipiosNaturalidade) {
        this.municipiosNaturalidade = municipiosNaturalidade;
    }

    public Collection<SelectItem> getMunicipiosEndereco() {
        return municipiosEndereco;
    }

    public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
        this.municipiosEndereco = municipiosEndereco;
    }

    /**
     * Redireciona para a busca de discentes, a fim de realizar a operação de atualização de dados. <br /><br />
     * JSP: /infantil/menu.jsp
     */
    @Override
    public String atualizar() throws ArqException {
        checkRole(SigaaPapeis.GESTOR_INFANTIL);
        initObj();
        BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
        buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_INFANTIL);
        return buscaDiscenteMBean.popular();
    }
    
    /**
     * Seleciona o discente e redireciona para o formulário de alteração de discente. <br /><br />
     * Chamado por {@link BuscaDiscenteMBean#redirecionarDiscente(Discente)}
     * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
     * JSP: Não invocado por JSP.
     */
    public String selecionaDiscente() throws ArqException {
        popularMunicipios();
        setConfirmButton("Atualizar");
        prepareMovimento(SigaaListaComando.ALTERAR_DISCENTE_INFANTIL);
        setOperacaoAtiva(SigaaListaComando.ALTERAR_DISCENTE_INFANTIL.getId());
        GenericDAO dao = getGenericDAO();
        obj.setPessoa( dao.refresh(obj.getPessoa()) );
        obj.getPessoa().setPais( dao.refresh(obj.getPessoa().getPais()) );
        if(obj.getResponsavel() != null){
        	obj.getResponsavel().setPessoa( dao.refresh(obj.getResponsavel().getPessoa()) );
        } else {
        	obj.setResponsavel(new ResponsavelDiscenteInfantil());
        	obj.getResponsavel().getPessoa().setSexo('M');
        }
        if(obj.getOutroResponsavel() != null){
        	obj.getOutroResponsavel().setPessoa( dao.refresh(obj.getOutroResponsavel().getPessoa()) );
        	if(obj.getOutroResponsavel().getEscolaridade() == null)
        		obj.getOutroResponsavel().setEscolaridade(new Escolaridade());
        } else {
        	obj.setOutroResponsavel(new ResponsavelDiscenteInfantil());
        	obj.getOutroResponsavel().getPessoa().setSexo('M');
        }

        if ( isEmpty( obj.getPessoa().getIdentidade() ) )
			obj.getPessoa().setIdentidade( new Identidade() );
        
        return forward(ConstantesNavegacaoInfantil.DISCENTE_INFANTIL_FORM);
    }

    public void setDiscente(DiscenteAdapter discente) throws ArqException {
        obj = (DiscenteInfantil) discente;        
    }
    
    /**
     * Retorna as opções de níveis possíveis de associar um aluno do ensino infantil.
     * 
     * @return
     * @throws DAOException
     * @throws ArqException
     */
    public Collection<SelectItem> getNiveisCombo() throws DAOException, ArqException{
    	ArrayList<OrderedSelectItem> itensOrdenaveis = new ArrayList<OrderedSelectItem>();
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
    	Collection<ComponenteCurricular> niveis = getDAO(ComponenteCurricularDao.class).findByUnidadeOtimizado(getUnidadeGestora(), getNivelEnsino(), null);
    	for(ComponenteCurricular cc: niveis){
    		String cod = cc.getCodigo();
			int tam = cod.length();
			OrderedSelectItem item = new OrderedSelectItem(cod.substring(tam-1), cc.getNome());
			itensOrdenaveis.add(item);
    	}
    	Collections.sort(itensOrdenaveis, new Comparator<OrderedSelectItem>() {
    		@Override
    		public int compare(OrderedSelectItem o1, OrderedSelectItem o2) {
    			return ((String) o1.getValue()).compareTo((String) o2.getValue());
    		}
		});
		for ( OrderedSelectItem item : itensOrdenaveis ) {
			itens.add(item.toSelectItem());
		}
		return itens; 
	}
}
