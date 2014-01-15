/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '04/10/2006'
 *
 */
package br.ufrn.sigaa.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.GenericTipo;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.TipoEsferaAdministrativa;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.PessoaJuridica;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TipoRedeEnsino;

/**
 * Representa o formulário de dados pessoais, que geralmente é utilizado
 * como primeiro passo para compor o cadastro de outra entidade
 * (por exemplo, Discente)
 * @author Andre M Dantas
 *
 */
public class PessoaForm extends SigaaForm {

	private Pessoa pessoa;

	private PessoaJuridica pessoaJuridica;

	private String tipoBusca;

	public PessoaForm() {
		this.pessoa = new Pessoa();
		this.pessoaJuridica = new PessoaJuridica();
	}

	public void init(ActionMapping mapping, HttpServletRequest request) {
		if (request.getParameter("tipoPessoa") != null) {
			pessoa.setTipo(request.getParameter("tipoPessoa").charAt(0));
		} else if (request.getSession(false).getAttribute("tipoPessoa") != null) {
			char tipo = (Character) request.getSession(false).getAttribute("tipoPessoa");
			pessoa.setTipo(tipo);
		}
		setDefaultProps();
	}

	@Override
	public void setDefaultProps() {
		
		int cepPadrao =  ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.CEP_PADRAO );
		int dddPadrao = ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.DDD_PADRAO );
		
		// campos comuns
		if (pessoa.getUnidadeFederativa() == null || pessoa.getUnidadeFederativa().getId() == 0)
			pessoa.setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		if (pessoa.getMunicipio() == null || pessoa.getMunicipio().getId() == 0)
			pessoa.setMunicipio(new Municipio());
		if (pessoa.getEnderecoContato() == null || pessoa.getEnderecoContato().getId() == 0) {
			pessoa.setEnderecoContato(new Endereco());
			pessoa.getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
			pessoa.getEnderecoContato().setCep(cepPadrao + "");
			pessoa.getEnderecoContato().setTipoLogradouro(
					new TipoLogradouro(TipoLogradouro.RUA));
			pessoa.setCodigoAreaNacionalTelefoneCelular((short) dddPadrao);
			pessoa.setCodigoAreaNacionalTelefoneFixo((short) dddPadrao);
		}
		//pessoa.setEnderecoProfissional(null);
		//pessoa.setEnderecoResidencial(null);
		// campos específicos
		if (pessoa.isPF()) {
			if (pessoa.getPais() == null || pessoa.getPais().getId() == 0)
				pessoa.setPais(new Pais(Pais.BRASIL));
				// comentado: || pessoa.getIdentidade().getId() == 0
			if (pessoa.getIdentidade() == null || pessoa.getIdentidade().getUnidadeFederativa() == null){
				pessoa.setIdentidade(new Identidade());
				pessoa.getIdentidade().setUnidadeFederativa(
					new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
			}
			if (pessoa.getTipoRaca() == null || pessoa.getTipoRaca().getId() == 0)
				pessoa.setTipoRaca(new TipoRaca(GenericTipo.NAO_INFORMADO));
			if (pessoa.getEstadoCivil() == null || pessoa.getEstadoCivil().getId() == 0)
				pessoa.setEstadoCivil(new EstadoCivil(GenericTipo.NAO_INFORMADO));
			if (pessoa.getTipoRedeEnsino() == null || pessoa.getTipoRedeEnsino().getId() == 0)
				pessoa.setTipoRedeEnsino(new TipoRedeEnsino(GenericTipo.NAO_INFORMADO));
		} else {
			pessoaJuridica.setTipoEsferaAdministrativa(new TipoEsferaAdministrativa(GenericTipo.NAO_INFORMADO));
			pessoaJuridica.setEndereco(null);
		}
	}

	@Override
	public void clear() throws Exception {
		this.pessoa = new Pessoa();
		this.pessoaJuridica = new PessoaJuridica();
	}

	public void clear(char tipo) throws Exception {
		clear();
		pessoa.setTipo(tipo);
	}

	/** datas do form */
	private String dataNascimento;

	private String dataExpedicaoIdentidade;

	private String dataExpedicaoCertMilitar;

	/** cpf dos dados pessoais */
	private String cpf_cnpj;

	public String getDataExpedicaoCertMilitar() {
		return dataExpedicaoCertMilitar;
	}

	public void setDataExpedicaoCertMilitar(String dataExpedicaoCertMilitar) {
		this.dataExpedicaoCertMilitar = dataExpedicaoCertMilitar;
	}

	public String getDataExpedicaoIdentidade() {
		return dataExpedicaoIdentidade;
	}

	public void setDataExpedicaoIdentidade(String dataExpedicaoIdentidade) {
		this.dataExpedicaoIdentidade = dataExpedicaoIdentidade;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getCpf_cnpj() {
		return cpf_cnpj;
	}

	public void setCpf_cnpj(String cpf_cnpj) {
		this.cpf_cnpj = cpf_cnpj;
	}

	public PessoaJuridica getPessoaJuridica() {
		return pessoaJuridica;
	}

	public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
		this.pessoaJuridica = pessoaJuridica;
	}

	@Override
	public Collection customSearch(HttpServletRequest req) throws ArqException {
		GenericDAO dao = getGenericDAO(req);
		Collection lista = null;

		try {
			if ("cnpj".equals(getTipoBusca())) {
				long num = Long.parseLong(Formatador.getInstance().parseStringCPFCNPJ(req.getParameter("cpf_cnpj"))) ;
				lista = dao.findByExactField(PessoaJuridica.class, "pessoa.cpf_cpnj", num, getPaging(req));
			} else if ("nome".equals(getTipoBusca())) {
				lista = dao.findByLikeField(PessoaJuridica.class, "pessoa.nome", pessoa.getNome(), getPaging(req));
			} else
				lista = dao.findAll(PessoaJuridica.class, getPaging(req));

		} catch (ArqException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return lista;
	}

	public String getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(String tipoBusca) {
		this.tipoBusca = tipoBusca;
	}


}
