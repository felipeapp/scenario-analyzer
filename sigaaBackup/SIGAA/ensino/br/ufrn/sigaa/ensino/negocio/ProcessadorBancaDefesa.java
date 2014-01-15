/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/02/2011
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.dao.ensino.BancaDefesaDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.dominio.MembroBanca;
import br.ufrn.sigaa.ensino.dominio.StatusBanca;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.NaturezaExame;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Cadastro de banca de defesa e seus membros
 * 
 * @author arlindo
 * 
 */
public class ProcessadorBancaDefesa extends ProcessadorCadastro {
	
	/**
	 * Executar as operações
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException,
			ArqException, RemoteException {
		if (SigaaListaComando.CADASTRAR_BANCA_DEFESA.equals(mov.getCodMovimento()) || 
				SigaaListaComando.ALTERAR_BANCA_DEFESA.equals(mov.getCodMovimento())) {
			return criar((MovimentoCadastro) mov);
		}  else if (SigaaListaComando.REMOVER_BANCA_DEFESA.equals(mov.getCodMovimento())) {
			return desativar((MovimentoCadastro) mov);
		}
		return mov;
	}
	
	/**
	 * Cria/Altera dados da banca de defesa
	 */
	@Override
	protected Object criar(MovimentoCadastro mov) throws DAOException,
			NegocioException, ArqException {

		validate(mov);
		
		BancaDefesa b = mov.getObjMovimentado();
		BancaDefesaDao dao = getDAO(BancaDefesaDao.class, mov);		
				
		try {
			dao.removerMembrosBanca(b);
			
			prepararInsercaoMembrosBanca(mov);
			
			if (b.getId() == 0)
				dao.create(b);
			else
				dao.update(b);
			
			cadastroParticipacaoBanca(mov, b);
			
			dao.getHibernateTemplate().saveOrUpdateAll(b.getMembrosBanca());
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return b;
	}
	
	/**  
	 * Cadastra banca no prodocente para os membros internos da banca (apenas os servidores) 
	 **/
	private void cadastroParticipacaoBanca(MovimentoCadastro mov, BancaDefesa b) throws DAOException {
		
		ProducaoDao producaoDao = getDAO(ProducaoDao.class, mov);
		
		try{
			
			for (MembroBanca membro : b.getMembrosBanca()) {
				if (membro.isInterno()) {
					Banca banca = null;
					
					membro.setServidor( producaoDao.findByPrimaryKey(membro.getServidor().getId(),
							Servidor.class, "id, siape, pessoa.id, pessoa.nome, unidade.nome, unidade.id") );
					banca = producaoDao.findBancaByServidorAndBancaDefesa(membro.getServidor(), b);
					if (banca == null)
						banca = new Banca();
					
					banca.setBancaDefesa(b);
					banca.setAnoReferencia(CalendarUtils.getAno(b.getDataDefesa()));
					banca.setArea(b.getGrandeArea());
					banca.setSubArea(b.getArea());
					banca.setAtivo(true);
					banca.setAutor(b.getDiscente().getNome());
					banca.setData(b.getDataDefesa());
					banca.setDataCadastro(new Date());
					banca.setDataProducao(b.getDataDefesa());
					banca.setTitulo(StringUtils.toAsciiHtml( StringUtils.stripHtmlTags( b.getTitulo() ) ));
					banca.setInstituicao(new InstituicoesEnsino(InstituicoesEnsino.UFRN));
					if (b.getDiscente().getCurso() != null)
						banca.setMunicipio(b.getDiscente().getCurso().getMunicipio());
					else
						banca.setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
					banca.setPais(new Pais(Pais.BRASIL));
					banca.setConsolidado(null);
					banca.setDataConsolidacao(null);
					banca.setDataValidacao(null);
					banca.setValidado(false);
					banca.setUsuarioConsolidador(null);
					banca.setUsuarioValidador(null);
					banca.setSequenciaProducao(null);
					banca.setServidor(membro.getServidor());
					banca.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
					banca.setTipoBanca(new TipoBanca(TipoBanca.CURSO));
					banca.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
					banca.setTipoProducao(TipoProducao.BANCA_CURSO_SELECOES);
					banca.setDepartamento(membro.getServidor().getUnidade());					
					banca.setNaturezaExame(NaturezaExame.MONOGRAFIA_GRADUACAO);
					banca.setCategoriaFuncional(null);
					banca.setInformacao(b.getDescricaoMembros());
					
					if (banca.getId() == 0)
						producaoDao.createNoFlush(banca);
					else
						producaoDao.updateNoFlush(banca);
				}
			}
			
		}finally{
			producaoDao.close();
		}
	}	
	
	/**
	 * Desativa a banca de defesa
	 */
	@Override
	protected Object desativar(MovimentoCadastro mov) throws DAOException,
			NegocioException, ArqException {
		GenericDAO dao = getGenericDAO(mov);
		PersistDB obj = mov.getObjMovimentado();
		try {
			dao.updateField(obj.getClass(), obj.getId(), "status", StatusBanca.CANCELADO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		return mov.getObjMovimentado();
	}
	
	/**
	 * Prepara os objetos para inserir os membros da banca.
	 * @param mov
	 * @throws DAOException
	 */
	private void prepararInsercaoMembrosBanca(MovimentoCadastro mov) throws DAOException {
		
		BancaDefesa b = mov.getObjMovimentado();
		BancaDefesaDao dao = getDAO(BancaDefesaDao.class, mov);
		
		try {
			for (MembroBanca m : b.getMembrosBanca()) {
				m.setId(0);
				m.setBanca(b);
				
				if (m.getMaiorFormacao() != null && m.getMaiorFormacao().getId() == 0)
					m.setMaiorFormacao(null);
				
				if (m.isExternoInstituicao() && m.getPessoaMembroExterno().getId() == 0) {
					int idPessoa = SincronizadorPessoas.getNextIdPessoa();
					m.getPessoaMembroExterno().setId(idPessoa);
					dao.createNoFlush(m.getPessoaMembroExterno());
				} 
			}			
		} finally {
			if (dao != null)
				dao.close();
		}
	}	
	
	/**
	 * Valida os dados cadastrados
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		BancaDefesa banca = ((MovimentoCadastro) mov).getObjMovimentado();
		checkValidation(banca.validate());
	}

}
