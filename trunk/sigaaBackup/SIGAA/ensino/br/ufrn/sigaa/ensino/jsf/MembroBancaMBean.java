/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2011
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.dominio.MembroBanca;
import br.ufrn.sigaa.ensino.dominio.TipoMembroBanca;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean Responsável por auxiliar no cadastro de membros nas bancas de defesa de TCC.
 * 
 * @author arlindo
 *
 */
@Component @Scope("request")
public class MembroBancaMBean extends SigaaAbstractController<MembroBanca>  {
	
	/** Construtor padrão */
	public MembroBancaMBean() {
		initMembro();
	}
	
	/** Inicializa os atributos de membro da banca. */
	private void initMembro() {
		obj = new MembroBanca();
		obj.setTipo(new TipoMembroBanca(TipoMembroBanca.EXAMINADOR_INTERNO));
		obj.setInstituicao(new InstituicoesEnsino());
		obj.setMaiorFormacao(new Formacao());
		obj.setServidor(new Servidor());
		obj.setDocenteExterno(new DocenteExterno());
		Pessoa p = new Pessoa();
		p.setMunicipio(null);
		p.setTipoEtnia(null);
		p.setPais(null);
		p.setTipoRedeEnsino(null);
		p.setEstadoCivil(null);
		p.setTituloEleitor(null);
		p.setIdentidade(null);
		p.setTipoRaca(null);
		p.setUnidadeFederativa(null);
		p.setEnderecoContato(null);
		p.setSexo('M');
		p.setInternacional(false);
		obj.setPessoaMembroExterno(p);
	}	
	
	/**
	 * Adiciona um membro na banca informada
	 * <br/><br/> 
	 * Método não chamado por JSP.
	 * @see br.ufrn.sigaa.ensino.jsf.BancaDefesaMBean#addMembro();
	 * @param b
	 * @return
	 * @throws DAOException
	 */
	public String addMembroBanca(BancaDefesa b) throws DAOException{
		
		erros = new ListaMensagens();
		
		if ( obj.isExternoInstituicao() && obj.getPessoaMembroExterno() != null ) {			
			PessoaDao daoPessoa = getDAO(PessoaDao.class);
			try{
				Pessoa pessoa = null;
				if (obj.getPessoaMembroExterno().getId() > 0)				
					pessoa = getGenericDAO().findByPrimaryKey(obj.getPessoaMembroExterno().getId(), Pessoa.class, 
							"id", "nome", "cpf_cnpj", "internacional", "passaporte");				
				else if (obj.getPessoaMembroExterno().getCpf_cnpj() > 0){
					pessoa = daoPessoa.findByCpf(obj.getPessoaMembroExterno().getCpf_cnpj());
					
					if (ValidatorUtil.isNotEmpty(pessoa)){
						addMensagemErro("O CPF informado é de uma pessoa já cadastrada, por favor localize-o na opção \"Buscar membro\".");
						return null;
					}
				}
				
				if (pessoa != null)
					obj.setPessoaMembroExterno(pessoa);
				
				if (obj.getPessoaMembroExterno().isInternacional() && ValidatorUtil.isEmpty(obj.getPessoaMembroExterno().getCpf_cnpj()))
					obj.getPessoaMembroExterno().setCpf_cnpj(null);
				
			} finally {
				if (daoPessoa != null){
					daoPessoa.close();
				}
			}							
		}
		
		/* Valida as informações cadastradas do membro */
		erros.addAll(obj.validate());
		
		if (hasErrors())
			return null;
		
		GenericDAO dao = getGenericDAO();
		try {
			if (obj.isInterno()){
				String tipoDocente = getParameter("tipoAjaxDocente_1");
				//verifica se selecionado docente externo
				if( "externo".equals(tipoDocente) ){
					obj.setTipo(new TipoMembroBanca(TipoMembroBanca.EXAMINADOR_EXTERNO));
					obj.setDocenteExterno(dao.findByPrimaryKey(obj.getDocenteExterno().getId(), DocenteExterno.class, "id", "pessoa.id", "pessoa.nome"));				
					obj.setServidor(null);
				}  else {
					obj.setServidor(dao.findByPrimaryKey(obj.getDocenteExterno().getId(), Servidor.class, "id", "siape", "pessoa.id", "pessoa.nome"));
					obj.setDocenteExterno(null);					
				}
				obj.setPessoaMembroExterno(null);
				obj.setInstituicao(null);
				obj.setMaiorFormacao(null);
			} else if (obj.isExternoInstituicao()) {
								
				if (obj.getPessoaMembroExterno().isInternacional()) {
					obj.getPessoaMembroExterno().setCpf_cnpj(null);
					if (isEmpty(obj.getPessoaMembroExterno().getPassaporte())) {
						addMensagemErro("O passaporte do membro é obrigatório.");
						return null;
					}
				} else {
					ValidatorUtil.validateCPF_CNPJ(obj.getPessoaMembroExterno().getCpf_cnpj(), "CPF", erros);
					if (hasErrors()) {
						return null;
					}
				}			
				obj.setDocenteExterno(null);
				obj.setServidor(null);
				obj.setInstituicao(dao.findByPrimaryKey(obj.getInstituicao().getId(), InstituicoesEnsino.class, "id", "sigla"));
				
			}
			
			obj.setTipo(dao.findByPrimaryKey(obj.getTipo().getId(), TipoMembroBanca.class, "id", "descricao"));	
			
			//Verifica se o membro já foi incluído 
			if (CollectionUtils.countMatches(b.getMembrosBanca(), new Predicate() {
				public boolean evaluate(Object m) {
					MembroBanca membro = (MembroBanca) m;
					if (membro.getPessoa().isInternacional() && !isEmpty(membro.getPessoa().getPassaporte()))
						return membro.getPessoa().getPassaporte().equals(obj.getPessoa().getPassaporte());
					else if (membro.getPessoa().getCpf_cnpj() != null && membro.getPessoa().getCpf_cnpj() > 0)
						return membro.getPessoa().getCpf_cnpj().equals(obj.getPessoa().getCpf_cnpj());
					else return false;
				}
			}) > 0) {
				addMensagemErro("A pessoa selecionada já foi adicionada à banca.");
				initMembro();
				return null;
			}
						
			//Realiza uma cópia do objeto para não ficar com referência
			MembroBanca membroBanca = UFRNUtils.deepCopy(obj);
			membroBanca.setBanca(b);
			b.getMembrosBanca().add(membroBanca);					
			
			//Orderna a lista pelo nome do membro
			Collections.sort(b.getMembrosBanca(), new Comparator<MembroBanca>() {
				public int compare(MembroBanca o1, MembroBanca o2) {
					return o1.getPessoa().getNome().compareTo(o2.getPessoa().getNome());
				}
			});		
			
			//inicializa os objetos
			initMembro();
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getFormPage()); 
	}
	
	/**
	 * Remove o membro da lista na posição passada
	 * <br/><br/> 
	 * Método não chamado por JSP.
	 * @see br.ufrn.sigaa.ensino.jsf.BancaDefesaMBean#removerMembro()
	 * @param b
	 * @param indice
	 * @return
	 */
	public String removeMembroBanca(BancaDefesa b, int indice){
		if (b.getMembrosBanca().remove(indice) == null){
			addMensagemErro("Erro ao remover membro de banca");
			return null;			
		}
		return forward(getFormPage()); 
	}
	
	/**
	 * Retorna o caminho do form de cadastro dos membros
	 * <br/><br/> 
	 * Método não chamado por JSP.
	 */
	@Override
	public String getFormPage() {
		return "/ensino/banca_defesa/membros.jsf";
	}

}
