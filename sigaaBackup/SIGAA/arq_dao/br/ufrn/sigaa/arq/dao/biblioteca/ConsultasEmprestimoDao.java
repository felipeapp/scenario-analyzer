/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 03/03/2011
 * 
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Convenio;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioExternoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.EmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.MaterialInformacionalDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.TituloCatalograficoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.UsuarioBibliotecaDto;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 *
 * <p>Dao que contém exclusivamente as consultas utilizadas na realização dos empréstimos </p>
 * <p> <i> As consultas foram separadas para fica mais fácil a manutenção e otimização </i> </p>
 * 
 * <p><strong>NÃO ALTERE OS MÉTODOS DESSA CLASSE, A NÃO SER PARA MELHORAR OS SEUS DESEMPENHOS <strong></p>
 * 
 * @author jadson
 *
 */
public class ConsultasEmprestimoDao  extends GenericSigaaDAO {

	/**
	 * Dados retornados por padrão para as consultas dos usuários bibliotecas.  Retorna o extritamente necessário para realizar a maioria dos casos de uso.
	 * Se algum caso de uso quiser algo mais, deve fazer a sua própria consulta, <strong>NÃO</strong> adicione campos a essa projeção.
	 */
	public static final String PROJECAO_PADRAO_INFORMACOES_USUARIO_BIBLIOTECA  = " ub.id, ub.senha, ub.vinculo, ub.identificacaoVinculo, ub.pessoa.id, ub.biblioteca.id, ub.quitado, ub.dataQuitacao ";
	
	/**
	 * Centraliza as restrições na consulta de discentes da biblioteca. Ainda de forma não parametrizada.
	 */
	public static final String RESTRICOES_CONSULTA_DISCENTE_BIBLIOTECA = 
		" AND ( discente.tipo in "+UFRNUtils.gerarStringIn( new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getTiposDiscenteBiblioteca())+" OR forma.permiteEmprestimoBiblioteca = trueValue() ) "
			+" AND discente.status in "+UFRNUtils.gerarStringIn( new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getStatusDiscenteUtilizarBiblioteca());
	
	/**
	 * Centraliza as restrições na consulta de servidores da biblioteca. Ainda de forma não parametrizada. <br/>
	 *  Residentes são considerados alunos de pós agora 13/06/2011
	 */
	public static final String RESTRICOES_CONSULTA_SERVIDOR_BIBLIOTECA = 
	" AND servidor.ativo.id in "+UFRNUtils.gerarStringIn( new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getStatusServidorUtilizarBiblioteca() );
	
	/**
	 * Centraliza as restrições na consulta de usuario externo da biblioteca. Ainda de forma não parametrizada. <br/>
	 */
	public static final String RESTRICOES_CONSULTA_USUARIOEXTERNO_BIBLIOTECA = 
	" AND ube.ativo = trueValue() AND ube.usuarioBiblioteca.ativo = trueValue() AND ube.usuarioBiblioteca.quitado = falseValue() "+
	" AND ube.cancelado = falseValue() AND ube.prazoVinculo >= :hoje ";
	
	/**
	 * Centraliza as restrições na consulta de docente externo da biblioteca. Ainda de forma não parametrizada. <br/>
	 */
	public static final String RESTRICOES_CONSULTA_DOCENTE_EXTERNO_BIBLIOTECA = 
	" AND docente.matricula IS NOT NULL AND  docente.ativo = trueValue() "
	+" AND ( prazoValidade IS NULL or prazoValidade >= :dataAtual ) ";
	
	/**
	 * <p>Retornas as digitais do usuário caso ele tenha, para evitar de realizar uma nova chamada remote e busca para autenticar o usuário </p>
	 * 
	 * digitais[0] = ditital direita
	 * digitais[1] = ditital esqueda
	 * 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public byte[][] findDigitalUsuario(Long cpf) throws DAOException {
		
		GenericDAO dao = null;
		
		try{
		
			dao = DAOFactory.getGeneric(Sistema.COMUM);
		
			Query q = dao.getSession().createSQLQuery(" SELECT dedo_coletado, digital  FROM comum.identificacao_pessoa where cpf =:cpf ");
			
			q.setLong("cpf", cpf);
			
			@SuppressWarnings("unchecked")
			List<Object> dadosDigital = q.list();
			
			byte[][] digitais = new byte[2][]; 
			
			for (Object object : dadosDigital) {
				Object[] temp = (Object[]) object;
				
				if( ((Character)temp[0]).equals('D') )
					digitais[0] = (byte[]) temp[1];
				
				if( ((Character)temp[0]).equals('E') )
					digitais[1] = (byte[]) temp[1];
			}
			
			return digitais; 
			
		}finally{
			if(dao != null ) dao.close();	
		}
		
		
	}
	
	
	/**
	 * <p>Retorna o id usuário da biblioteca dado um cpf.</p>
	 * <p>Este método é utilizado nas cosultas dos usuários no desktop pela digital.</p>
	 * 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	
	public Integer findIdUsuarioBibliotecaAtivoNaoQuitadoByCpf(Long cpf) throws DAOException {
	
		StringBuilder hql = new StringBuilder("SELECT ub.id FROM UsuarioBiblioteca ub INNER JOIN ub.pessoa p WHERE p.cpf_cnpj = :cpf AND ub.quitado = :falso AND ub.ativo = :verdadeiro ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setLong("cpf", cpf);
		q.setBoolean("verdadeiro", true);
		q.setBoolean("falso", false);
		
		return (Integer)  q.uniqueResult();
	}
	
	
	
	
	/**
	 * Retorna somente as informações necessárias de um UsuarioBiblioetca para realizar um empréstimo.
	 *
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public UsuarioBiblioteca findInformacoesUsuarioBibliotecaNaoQuitado(int idUsuarioBiblioteca) throws DAOException{
		
		String hql = " SELECT "+PROJECAO_PADRAO_INFORMACOES_USUARIO_BIBLIOTECA
		+" FROM UsuarioBiblioteca ub " 
		+" LEFT JOIN ub.pessoa pessoa "
		+" LEFT JOIN ub.biblioteca biblioteca "
		+" WHERE ub.id = :idUsuarioBiblioteca and ub.ativo = trueValue() AND ub.quitado = falseValue() ";
		
		Query q = getSession().createQuery(hql);
		q.setParameter("idUsuarioBiblioteca", idUsuarioBiblioteca);
		
		Object[] objetos = (Object[])  q.uniqueResult();

		if(objetos == null)return null;
		
		UsuarioBiblioteca ub = new UsuarioBiblioteca( (Integer) objetos[0]);
		ub.setSenha((String) objetos[1]);
		ub.setVinculo((VinculoUsuarioBiblioteca) objetos[2]);
		ub.setIdentificacaoVinculo( (Integer) objetos[3]);
		
		Integer idPessoa = (Integer) objetos[4];
		if(idPessoa != null)
			ub.setPessoa( new Pessoa(idPessoa));
		
		Integer idBiblioteca = (Integer) objetos[5];
		if(idBiblioteca != null)
			ub.setBiblioteca( new Biblioteca(idBiblioteca));
		
		ub.setQuitado(  (Boolean) objetos[6] );
		ub.setDataQuitacao(  (Date) objetos[7] );
		
		return ub;
	}

	
	
	/**
     * <p>Retorna, em formato Dto, todos os empréstimos ativos do usuário da biblioteca passado.</p>
     *
     * <p><strong>Esse método só deve ser usado para recuperar os empréstimos a partir do módulo remote de circulação.<strong></p>
     *
     * @param usuarioBiblioteca
     * @throws DAOException
     * @return
     */
	public List <EmprestimoDto> findEmprestimosDtoAtivoByUsuarioBiblioteca (UsuarioBiblioteca usuarioBiblioteca) throws DAOException{

		String sql = "select e.id_emprestimo, e.data_emprestimo, e.prazo, " +
				" (select p.data_anterior from biblioteca.prorrogacao_emprestimo p where p.id_emprestimo = e.id_emprestimo and p.tipo = "  // Data da renovação 
					+ TipoProrrogacaoEmprestimo.RENOVACAO + " order by data_emprestimo desc "+BDUtils.limit(1)+") as data_renovacao, " +
				" p.id_tipo_emprestimo, " +
				" (select count (p.id_prorrogacao_emprestimo) from biblioteca.prorrogacao_emprestimo p where p.id_emprestimo = e.id_emprestimo and p.tipo = " + TipoProrrogacaoEmprestimo.RENOVACAO + "), "+ // quantidade de renovações 
				" p.quantidade_renovacoes, p.tipo_prazo, e.id_material, m.codigo_barras, b.descricao as biblioteca, " +
				" s.descricao as situacao, st.descricao as status, co.descricao as colecao, " +
				" c.autor, c.ano, c.edicao, c.titulo, c.id_titulo_catalografico, te.descricao as tipo_emprestimo " +
				" from biblioteca.emprestimo e " +
				" join biblioteca.politica_emprestimo p on p.id_politica_emprestimo = e.id_politica_emprestimo " +
				" join biblioteca.tipo_emprestimo te on te.id_tipo_emprestimo = p.id_tipo_emprestimo " +
				" join biblioteca.material_informacional m on e.id_material = m.id_material_informacional " +
				" join biblioteca.situacao_material_informacional s on s.id_situacao_material_informacional = m.id_situacao_material_informacional " + 
				" join biblioteca.status_material_informacional st on st.id_status_material_informacional = m.id_status_material_informacional " +
				" join biblioteca.colecao co on co.id_colecao = m.id_colecao " +
				" left join biblioteca.exemplar ex on ex.id_exemplar = m.id_material_informacional " +
				" left join biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional " +
				" left join biblioteca.assinatura a on f.id_assinatura = a.id_assinatura " +
				" join biblioteca.biblioteca b on b.id_biblioteca = m.id_biblioteca " +
				" join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = ( "+
				" CASE WHEN (ex.id_titulo_catalografico IS NOT NULL) THEN ex.id_titulo_catalografico WHEN (a.id_titulo_catalografico IS NOT NULL) THEN a.id_titulo_catalografico END ) "+ 
				" where e.id_usuario_biblioteca = " + usuarioBiblioteca.getId() + " " +
				" and e.data_devolucao is null and e.data_estorno is null and e.ativo = trueValue() " +
				" order by e.data_emprestimo";
		
		@SuppressWarnings("unchecked")
		List <Object []> es = getSession().createSQLQuery(sql).list();
		
		List <EmprestimoDto> emprestimos = new ArrayList <EmprestimoDto> ();
		
		UsuarioBibliotecaDto uDto = new UsuarioBibliotecaDto();
		
		for (Object [] e : es){
			EmprestimoDto eDto = new EmprestimoDto();
			SimpleDateFormat sdf = null;
			
			eDto.idEmprestimo = ((Integer)e[0]);
			
			
			eDto.dataEmprestimoFormatado = new SimpleDateFormat("dd/MM/yyyy HH:mm").format( (Date) e[1] );
			
			Date prazo = (Date) e[2];
			eDto.prazoFormatado =  new SimpleDateFormat("dd/MM/yyyy HH:mm").format( prazo );
			
			if (e[3] != null)
				eDto.dataRenovacaoFormatado = new SimpleDateFormat("dd/MM/yyyy HH:mm").format( (Date) e[3] );
			eDto.idTipoEmprestimo = ((Integer)e[4]);
			eDto.podeRenovar = (  (BigInteger)e[5]  ).intValue() >=  (  (Short)e[6]  ).intValue()   ? false : true;
			
			if ( ((Short)e[7]).equals( PoliticaEmprestimo.TIPO_PRAZO_EM_DIAS ) ){
				eDto.status = CalendarUtils.estorouPrazo(prazo , new Date()) ? EmprestimoDto.STATUS_ATRASADO : EmprestimoDto.STATUS_EM_DIA;
				sdf = new SimpleDateFormat("dd/MM/yyyy");
			} else {
				eDto.status = CalendarUtils.estorouPrazoConsiderandoHoras( prazo, new Date()) ? EmprestimoDto.STATUS_ATRASADO : EmprestimoDto.STATUS_EM_DIA;
				sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			}
			
			MaterialInformacionalDto mDto = new MaterialInformacionalDto();
			mDto.idMaterial = ((Integer)e[8]);
			mDto.codigoBarras = (String)e[9];
			mDto.biblioteca = (String)e[10];
			
			
			mDto.situacao = e[11] + " ("+e[19]+") Prazo: " + sdf.format( prazo );
			mDto.status = (String)e[12];
			mDto.estaEmprestado = true;
			mDto.estaDisponivel = false;
			
			if (e[13] != null)
				mDto.colecao = (String)e[13];

			TituloCatalograficoDto tDto = new TituloCatalograficoDto();
			
			if (e[16] != null)
				tDto.autor = (String)e[14];
			if (e[17] != null)
				tDto.ano = (String)e[15];
			if (e[18] != null)
				tDto.edicao = (String)e[16];
			tDto.titulo = (String)e[17];
			
			tDto.idTituloCatalografico = ((Integer)e[18]);

			mDto.tituloDto = tDto;
			eDto.usuario = uDto;
			eDto.materialDto = mDto;
			
			emprestimos.add(eDto);
		}
		
		return emprestimos;
	}
	
	
	/**
	 * A busca do material utilizada no desktop desktop.
	 * 
	 * @param codigoBarras
	 * @return
	 * @throws DAOException
	 */
	public MaterialInformacional findDadosMaterialByCodigoBarras(String codigoBarras) throws DAOException {
		
		long tempo = System.currentTimeMillis();
		
		String sql = " select  case when e.id_exemplar is not null then true else false end as eh_exemplar, " +
				" m.id_material_informacional as id_mi, " +
				" b.id_biblioteca as id_biblioteca, b.descricao as biblioteca, " +
				" c.id_colecao as id_colecao, c.descricao as colecao,  " +
				" s.id_situacao_material_informacional as id_sm,  s.descricao as situacao, " +
				" st.id_status_material_informacional as id_st, st.descricao as status, " +
				" tipo.id_tipo_material id_tipo, tipo.descricao as tipo, "+
				" coalesce(e.id_exemplar_de_quem_sou_anexo, f.id_fasciculo_de_quem_sou_suplemento) as id_anexo_suplemento, " +
				" coalesce(a.id_titulo_catalografico, e.id_titulo_catalografico) as id_titulo " +
				
				" from biblioteca.material_informacional m " +
				" left join biblioteca.exemplar e                   on e.id_exemplar = m.id_material_informacional " +
				" left join biblioteca.fasciculo f                  on f.id_fasciculo = m.id_material_informacional" +
				" left join biblioteca.assinatura a                 on a.id_assinatura = f.id_assinatura  " +
				" join biblioteca.situacao_material_informacional s on s.id_situacao_material_informacional = m.id_situacao_material_informacional " +
				" join biblioteca.status_material_informacional st  on st.id_status_material_informacional = m.id_status_material_informacional " +
				" join biblioteca.tipo_material tipo                on tipo.id_tipo_material = m.id_tipo_material " +
				" join biblioteca.colecao c                         on c.id_colecao = m.id_colecao  " +
				" join biblioteca.biblioteca b                      on b.id_biblioteca = m.id_biblioteca " +
				" where m.codigo_barras = :codigoBarras and m.ativo = trueValue() ";
		
		Query q = getSession().createSQLQuery(sql);
		
		q.setString("codigoBarras", codigoBarras);
		
		q.setMaxResults(1);
		
		Object [] linha = (Object[]) q.uniqueResult();
		
		// Se não encontrou, retorna.
		if (linha == null){
			System.out.println(" ***   Busca material circulação demorou: "+ (System.currentTimeMillis()-tempo)+" ms   *** " );  // temp <= 1s
			return null;
		}
		
		MaterialInformacional material = ((Boolean)linha[0]) ? new Exemplar() : new Fasciculo();
		
		material.setId( (Integer) linha[1]);
		material.setCodigoBarras(codigoBarras);
		
		material.setBiblioteca(new Biblioteca( (Integer)linha[2], (String) linha[3] ));
		
		material.setColecao(new Colecao((Integer)linha[4], (String) linha[5] ));
		
		material.setSituacao(new SituacaoMaterialInformacional( (Integer)linha[6], (String) linha[7] ) );

		material.setStatus(new StatusMaterialInformacional((Integer)linha[8], (String) linha[9]) );
		
		material.setTipoMaterial(new TipoMaterial( (Integer)linha[10], (String) linha[11]));
		
		
		if (material instanceof Exemplar ){
			Exemplar exemplar = (Exemplar) material;
			
			if( linha[12] != null)
				exemplar.setExemplarDeQuemSouAnexo(new Exemplar((Integer)linha[12]));
			
			exemplar.setTituloCatalografico(new TituloCatalografico((Integer)linha[13]));
		
		} else {
			Fasciculo fasciculo = (Fasciculo) material;
			if( linha[12] != null)
				fasciculo.setFasciculoDeQuemSouSuplemento(new Fasciculo((Integer)linha[12]));
			
			fasciculo.setAssinatura(new Assinatura());
			fasciculo.getAssinatura().setTituloCatalografico(new TituloCatalografico((Integer)linha[13]));
		}
		
		System.out.println(" ***   Busca material circulação demorou: "+ (System.currentTimeMillis()-tempo)+" ms   *** " );  // temp <= 1s
		
		return material;
	}
	
	
	
	/**
	 * Método usado para recuperar as informações a serem exibidas na tela de checkout do programa de circulação da biblioteca.
	 * 
	 * 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public MaterialInformacionalDto findInformacoesCheckout(String codigoBarras) throws DAOException {

		String sql = "select m.id_material_informacional, m.codigo_barras,  b.id_biblioteca, b.descricao as biblioteca,  c.titulo, c.autor, c.ano, c.edicao, e.id_emprestimo, e.prazo, s.siape, d.matricula, p.nome, u.id_foto "
			+ " from biblioteca.material_informacional m "
			+ " left join biblioteca.exemplar ex on ex.id_exemplar = m.id_material_informacional "
			+ " left join biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional "
			+ " left join biblioteca.assinatura a on a.id_assinatura = f.id_assinatura "
			+ " join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = coalesce (ex.id_titulo_catalografico, a.id_titulo_catalografico) "
			+ " join biblioteca.biblioteca b on b.id_biblioteca = m.id_biblioteca "
			+ " left join biblioteca.emprestimo e on ( e.id_material = m.id_material_informacional and  e.data_devolucao is null and e.data_estorno is null ) "
			+ " left join biblioteca.usuario_biblioteca ub on ub.id_usuario_biblioteca = e.id_usuario_biblioteca "
			+ " left join comum.pessoa p on p.id_pessoa = ub.id_pessoa "
			+ " left join discente d on d.id_pessoa = p.id_pessoa "
			+ " left join rh.servidor s on s.id_pessoa = p.id_pessoa "
			+ " left join comum.usuario u on u.id_pessoa = p.id_pessoa "
			+ " where m.codigo_barras = :codigoBarras ";
		
		Query q = getSession().createSQLQuery (sql);
		q.setMaxResults(1);
		q.setString("codigoBarras", codigoBarras);
		
		Object[] linha = (Object[]) q.uniqueResult();
		
		MaterialInformacionalDto materialDto = null;
		
		if (linha != null){  // Se o mateiral digitado existe
			
			int contadorIndex = 0;
			
			materialDto =  new MaterialInformacionalDto();
			materialDto.idMaterial = ((Integer)linha[contadorIndex++]);
			materialDto.codigoBarras = (String)linha[contadorIndex++];
			materialDto.idBiblioteca = (Integer)linha[contadorIndex++];
			materialDto.biblioteca = (String)linha[contadorIndex++];
			
			materialDto.tituloDto = new TituloCatalograficoDto();
			if (linha[contadorIndex] != null){
				materialDto.tituloDto.titulo = (String)linha[contadorIndex];
			}
			contadorIndex++;
			if (linha[contadorIndex] != null){
				materialDto.tituloDto.autor = (String)linha[contadorIndex];
			}
			contadorIndex++;
			if (linha[contadorIndex] != null){
				materialDto.tituloDto.ano = (String)linha[contadorIndex];
			}
			contadorIndex++;
			if (linha[contadorIndex] != null){
				materialDto.tituloDto.edicao = (String)linha[contadorIndex];
			}
			contadorIndex++;
			
			// O material está emprestado
			if (linha[contadorIndex] != null && ( (Integer) linha[contadorIndex] > 0 )){
				materialDto.emprestimoAtivoMaterial = new EmprestimoDto ();
				
				
				materialDto.emprestimoAtivoMaterial.idEmprestimo = ((Integer)linha[contadorIndex++]);
				
				Date prazo = (Date) linha[contadorIndex++];
				materialDto.emprestimoAtivoMaterial.prazoFormatado = new SimpleDateFormat("dd/MM/yyyy HH:mm").format( prazo );
				
				materialDto.emprestimoAtivoMaterial.usuario = new UsuarioBibliotecaDto();
				if (linha[contadorIndex] != null){
					materialDto.emprestimoAtivoMaterial.usuario.siape = ((Integer)linha[contadorIndex]);
				}
				contadorIndex++;
				if (linha[contadorIndex] != null){
					materialDto.emprestimoAtivoMaterial.usuario.matricula = ((BigInteger)linha[contadorIndex]).longValue();
				}
				contadorIndex++;
				
				materialDto.emprestimoAtivoMaterial.usuario.nome = (String)linha[contadorIndex++];
				if (linha[contadorIndex] != null){
					materialDto.emprestimoAtivoMaterial.usuario.idFoto = ((Integer)linha[contadorIndex]);
				}
				contadorIndex++;
				
				materialDto.emprestimoAtivoMaterial.status = new Date().after(prazo) ? EmprestimoDto.STATUS_ATRASADO : EmprestimoDto.STATUS_EM_DIA;
			}
			
		}
		
		return materialDto;
	}
	
	
	
	/**
	 * <p>Retorna todos os empréstimos ativos do um usuário da biblioteca  </p>
	 * 
	 * <p>É necessário trazer todos os dados que definem a política de empréstimo</p>
	 * 
	 * @param usuarioBiblioteca
	 * @param materialInformacional
	 * @param biblioteca
	 * @param devolvidos
	 * @param podeRenovar
	 * @param atrasados
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 * 
	 */
	public List <Emprestimo> findEmprestimosAtivosByUsuario (int idUsuarioBiblioteca) throws DAOException{

		if(idUsuarioBiblioteca <= 0)
			throw new IllegalArgumentException("É preciso informar o id do usuário dos empréstimos ");

		String projecao = " e.id, e.prazo, e.dataEmprestimo, e.usuarioBiblioteca.id, "
			+" e.material.id, e.material.codigoBarras, e.material.tipoMaterial.id, " 
			+" e.material.biblioteca.id, e.material.biblioteca.funcionaSabado, e.material.biblioteca.funcionaDomingo,"
			+" politica.id, politica.biblioteca.id, politica.biblioteca.identificador, politica.tipoEmprestimo.id, politica.tipoEmprestimo.descricao, "
			+" politica.vinculo, "
			+" politica.quantidadeMateriais, politica.prazoEmprestimo, politica.tipoPrazo, politica.quantidadeRenovacoes, politica.personalizavel, "
			+" status.id, status.descricao, status.permiteEmprestimo, " 
			+" tiposMateriais.id, tiposMateriais.descricao ";
		
		
		String hql = " SELECT "+projecao+" FROM Emprestimo e "
			+" INNER JOIN e.politicaEmprestimo politica "
			+" LEFT JOIN politica.statusMateriais status "
			+" LEFT JOIN politica.tiposMateriais tiposMateriais "
			+" WHERE e.usuarioBiblioteca.id = :idUsuarioBiblioteca "
			+" AND e.situacao = "+Emprestimo.EMPRESTADO
		    +" AND e.ativo = :true "
		    +" ORDER BY e.dataEmprestimo DESC "; 
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuarioBiblioteca", idUsuarioBiblioteca);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List <Object[]> objetosTemp =  q.list(); // Não dá para usar parseTo por causa da instanciação da classe Material Informacional.
		
		
		List <Emprestimo> emprestimosTemp = new ArrayList<Emprestimo>();
		
		for (Object[] dadosEmprestimos : objetosTemp) {
			Emprestimo e = new Emprestimo();
			e.setId((Integer) dadosEmprestimos[0]);			
			e.setPrazo((Date) dadosEmprestimos[1]);
			e.setDataEmprestimo((Date) dadosEmprestimos[2]);
			e.setUsuarioBiblioteca( new UsuarioBiblioteca( (Integer) dadosEmprestimos[3] ));
			e.setMaterial( new Exemplar( (Integer) dadosEmprestimos[4]) ); // só preciso do id, então tanto faz intanciar exemplar ou fascículo
			e.getMaterial().setCodigoBarras( (String) dadosEmprestimos[5] );
			e.getMaterial().setTipoMaterial( new TipoMaterial ( (Integer) dadosEmprestimos[6] )  );
			e.getMaterial().setBiblioteca( new Biblioteca((Integer) dadosEmprestimos[7]));
			e.getMaterial().getBiblioteca().setFuncionaSabado(  (Boolean) dadosEmprestimos[8]);
			e.getMaterial().getBiblioteca().setFuncionaDomingo(  (Boolean) dadosEmprestimos[9]);
			e.setSituacao(Emprestimo.EMPRESTADO);
			e.setAtivo(true);
			
			if(emprestimosTemp.contains(e))
				e = emprestimosTemp.get(emprestimosTemp.indexOf(e));
			else
				emprestimosTemp.add(e);
			
			if(e.getPoliticaEmprestimo() == null ){
			
				PoliticaEmprestimo politica = new PoliticaEmprestimo();
				politica.setId((Integer) dadosEmprestimos[10]);
				
				politica.setBiblioteca( new Biblioteca ( (Integer) dadosEmprestimos[11]));
				politica.getBiblioteca().setIdentificador( (String) dadosEmprestimos[12]);
				politica.setTipoEmprestimo( new TipoEmprestimo( (Integer) dadosEmprestimos[13],  (String) dadosEmprestimos[14]));
				politica.setPersonalizavel( (Boolean) dadosEmprestimos[20]);
				
				if(! politica.isPersonalizavel()){
					politica.setVinculo( (VinculoUsuarioBiblioteca) dadosEmprestimos[15]);
					politica.setQuantidadeMateriais( (Integer) dadosEmprestimos[16]);
					politica.setPrazoEmprestimo( (Integer) dadosEmprestimos[17]);
					politica.setTipoPrazo( (Short) dadosEmprestimos[18]);
					politica.setQuantidadeRenovacoes( (Integer) dadosEmprestimos[19]);
				
				}
				
				e.setPoliticaEmprestimo(politica);
				
			}
			
			// Retorna a política já existente para adicionar os status e tipos de materiais //
			PoliticaEmprestimo politica = e.getPoliticaEmprestimo();
			
			if(dadosEmprestimos[21] != null)
				politica.adicionaStatusMaterial( new StatusMaterialInformacional(  (Integer) dadosEmprestimos[21],  (String) dadosEmprestimos[22], (Boolean) dadosEmprestimos[23])  ) ;
				
			if(dadosEmprestimos[24] != null)
				politica.adicionaTipoMaterial( new TipoMaterial(  (Integer) dadosEmprestimos[24],  (String) dadosEmprestimos[25])  ) ;
		
			
		}
		
		
		List <Emprestimo> emprestimos = new ArrayList<Emprestimo>();
		
		for (Emprestimo e : emprestimosTemp){
			e.setDataRenovacao(BibliotecaUtil.getDataUltimaRenovacao(e));
			e.setQuantidadeRenovacoes(BibliotecaUtil.getQuantidadeRenovacoesEmprestimo(e));
			emprestimos.add(e);
		}
		
		return emprestimos;
	}
	
	
	
	
	
	
	////////////////////// Método usados nas buscas dos vínculos ativos e para recuperar as suas informações /////////////////////////////
	
	/**
	 * Projeção padrão que recupera os dados de contado de uma pessoa na biblioteca
	 */
	private static final String PROJECAO_DADOS_CONTATO_PESSOA =  " endereco.logradouro, endereco.numero, endereco.complemento, endereco.cep, endereco.bairro, pessoa.email, pessoa.telefone "; 

	/**
	 * Projeção padrão que recupera os dados de contado de uma biblioteca na biblioteca
	 */
	private static final String PROJECAO_DADOS_CONTADO_BIBLIOTECA =  " endereco.logradouro, endereco.numero, endereco.complemento, endereco.cep, endereco.bairro, biblioteca.email, biblioteca.telefone "; 
	
	
	/**
	 *       Método que retorna os servidores do usuário biblioteca que podem fazem empréstimo, retorna também o categoria do servidor 
	 *    para saber se ele é docente ou técnico-administrativo
	 *
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public List<Servidor> findServidoresBibliotecaByPessoa(int idPessoa) throws DAOException {
		String hql = "SELECT servidor.id, servidor.categoria.id from Servidor servidor " +
		" WHERE  servidor.pessoa.id = " + idPessoa + " " +
		RESTRICOES_CONSULTA_SERVIDOR_BIBLIOTECA;
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> objetosServidores = q.list();
		
		List<Servidor> servidores = new ArrayList<Servidor>();
		
		for (Object object : objetosServidores) {
			Object[] temp = (Object[]) object;
			Servidor sTemp = new Servidor( (Integer) temp[0]);
			sTemp.setCategoria( new Categoria((Integer) temp[1]));
			
			servidores.add(sTemp);
		}
		
		return servidores;
	}
	
	
	
	
	/**
	 *       Método que retorna os servidores do usuário biblioteca que podem fazem empréstimo, retorna também o categoria do servidor 
	 *    para saber se ele é docente ou técnico-administrativo
	 *
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Servidor findInfoServidorBiblioteca(int idServidor) throws DAOException {
		String hql = "SELECT servidor.siape, unidade.nome, servidor.cargo.denominacao, pessoa.id, pessoa.nome, "+PROJECAO_DADOS_CONTATO_PESSOA+
		" FROM Servidor servidor " +
		" INNER JOIN servidor.pessoa pessoa "+
		" INNER JOIN servidor.unidade unidade "+
		" LEFT JOIN pessoa.enderecoContato endereco "+
		" WHERE  servidor.id = " + idServidor;
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> objetosServidores = q.list();
		
		Servidor sTemp = new Servidor( );
		
		for (Object object : objetosServidores) {
			Object[] temp = (Object[]) object;
			
			sTemp.setSiape( (Integer) temp[0] );
			sTemp.setUnidade(new Unidade());
			sTemp.getUnidade().setNome((String)temp[1]);
			sTemp.setCargo(new Cargo());
			sTemp.getCargo().setDenominacao((String)temp[2]);
			
			sTemp.setPessoa( new Pessoa((Integer)temp[3], (String)temp[4]));
			
			sTemp.getPessoa().setEnderecoContato( new Endereco(0, null, null, null, (String) temp[5] , (String) temp[6], (String)temp[7], (String)temp[8], "", "", (String)temp[9], "", true));
			
			sTemp.getPessoa().setEmail( (String)temp[10] );
			sTemp.getPessoa().setTelefone( (String)temp[11] );
		}
		
		return sTemp;
	}
	
	
	/**
	 *       <p>Método que retorna os discentes do usuário biblioteca que podem fazer emprestimo, retorna também o nível do discente 
	 *    para saber se ele é gradução, pós, ou nível médio.</p>
	 *
	 *
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 * 
	 * 
	 * Mudar a possibilidade do aluno fazer empréstimo pela forma_ingresso. Colocar um boolean 
	 * (permite_emprestimo_biblioteca) e controlamos assim essa questão. 
	 */
	public List<Discente> findDiscentesBibliotecaByPessoa(int idPessoa) throws DAOException {
		
		String projecao = "  discente.id, discente.nivel  ";
		
		String hql = " SELECT "+projecao+" FROM Discente discente "
			+" LEFT JOIN discente.formaIngresso forma " 
			+" WHERE  discente.pessoa.id = " + idPessoa + " " 
			+ RESTRICOES_CONSULTA_DISCENTE_BIBLIOTECA;
			
			System.out.println(hql);
			
			Query q = getSession().createQuery(hql);		
			
			@SuppressWarnings("unchecked")
			List<Object[]> linhas = q.list();
			
			return new ArrayList<Discente>( HibernateUtils.parseTo(linhas, projecao, Discente.class, "discente"));
	}
	
	
	
	
	/**
	 *       <p>Método que retorna os discentes do usuário biblioteca que podem fazer emprestimo, retorna também o nível do discente 
	 *    para saber se ele é gradução, pós, ou nível médio.</p>
	 *
	 *		<p>Pasando-se os níveis, o método retorna apenas os discentes que possuiem os níveis passados</p>
	 *
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Discente findInfoDiscenteBiblioteca(int idDicente) throws DAOException {
		
		String hql = "SELECT discente.id, discente.nivel, discente.tipo, discente.matricula, curso.nome, gestora.id, gestora.nome, forma.permiteEmprestimoBiblioteca, discente.pessoa.id, discente.pessoa.nome , "+PROJECAO_DADOS_CONTATO_PESSOA
		+" FROM Discente discente "
		+" INNER JOIN discente.pessoa pessoa "
		+" LEFT JOIN discente.curso curso "                // alguns discentes não possuem curso (Especiais)
		+" LEFT JOIN curso.unidade unidadeCurso " 
		+" LEFT JOIN unidadeCurso.gestora gestora " 
		+" LEFT JOIN discente.formaIngresso forma "        // alguns discentes não possuem forma de ingresso
		+" LEFT JOIN pessoa.enderecoContato endereco "
		+" WHERE  discente.id = " + idDicente ;
		
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> objetosServidores = q.list();
		
		Discente discente = new Discente();
		
		for (Object object : objetosServidores) {
			Object[] temp = (Object[]) object;
			
			discente = new Discente();
			discente.setId((Integer) temp[0]);
			discente.setNivel( (Character) temp[1]);
			discente.setTipo( (Integer) temp[2]);
			discente.setMatricula((Long) temp[3]);
			discente.getCurso().setNome((String)temp[4]);
			Unidade u = new Unidade();
			
			// se tiver unidade gestora //
			if((Integer) temp[5] != null){
				u.setGestora(new Unidade());
				u.getGestora().setId((Integer) temp[5]);
				u.getGestora().setNome((String)temp[6]);
			}
			discente.getCurso().setUnidade(u);
			
			 // guarda com o discente a informação se ele é aluno de mobilidade estutantiu  para mostrar ao usuário //
			FormaIngresso fi = new FormaIngresso(); 
			fi.setTipoMobilidadeEstudantil( (Boolean) temp[7]);
			discente.setFormaIngresso( fi);
			
			discente.setPessoa( new Pessoa((Integer)temp[8], (String)temp[9]));
			discente.getPessoa().setEnderecoContato( new Endereco(0, null, null, null, (String)temp[10], (String)temp[11], (String)temp[12], (String)temp[13], "", "", (String)temp[14], "", true));
		
			discente.getPessoa().setEmail( (String)temp[15] );
			discente.getPessoa().setTelefone( (String)temp[16] );
		}
		
		return discente;
		
	}
	
	
	/**
	 * Retorna os docentes externos associados à pessoa informada.
	 * Busca apenas docentes que possuem a matrícula setada.
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public List<Integer> findIdDocenteExternoAtivosByPessoa(Pessoa pessoa) throws DAOException {
		String hql = "SELECT docente.id FROM DocenteExterno docente "
			+" WHERE docente.pessoa.id = :idPessoa "+
			RESTRICOES_CONSULTA_DOCENTE_EXTERNO_BIBLIOTECA;
		
		// Verificar se o prazo adicional de acesso também foi excedido
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		cal.add(Calendar.DAY_OF_MONTH, -ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO)-1);
		Date dataAtual = cal.getTime();
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idPessoa", pessoa.getId());
		q.setTimestamp("dataAtual", dataAtual);

		@SuppressWarnings("unchecked")
		List<Integer> lista = q.list();
		return lista;
	}
	
	
	
	
	/**
	 * Retorna os docentes externos associados à pessoa informada.
	 * Busca apenas docentes que possuem a matrícula setada.
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public DocenteExterno findInfoDocenteExternoBiblioteca(int idDocenteExterno) throws DAOException {
		String hql = "SELECT instituicao.nome as instituicao, pessoa.cpf_cnpj, pessoa.passaporte, pessoa.id, pessoa.nome, "+PROJECAO_DADOS_CONTATO_PESSOA
			+" FROM DocenteExterno docente "
			+" INNER JOIN docente.pessoa pessoa "
			+" LEFT JOIN pessoa.enderecoContato endereco "
			+" LEFT JOIN docente.instituicao instituicao "
			+" WHERE docente.id = :idDocenteExterno ) ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idDocenteExterno", idDocenteExterno);

		@SuppressWarnings("unchecked")
		List<Object> objetosServidores = q.list();
		
		DocenteExterno dTemp = new DocenteExterno( );
		
		for (Object object : objetosServidores) {
			Object[] temp = (Object[]) object;
			
			dTemp.setInstituicao( new InstituicoesEnsino(0, (String)temp[0],false) );
			
			Pessoa p = new Pessoa((Integer)temp[3], (String)temp[4]);
			p.setCpf_cnpj((Long)temp[1]);
			p.setPassaporte((String)temp[2]);
			dTemp.setPessoa( p );
			dTemp.getPessoa().setEnderecoContato( new Endereco(0, null, null, null, (String)temp[2], (String)temp[4], (String)temp[5], (String)temp[6], "", "", (String)temp[7], "", true));
		
			dTemp.getPessoa().setEmail( (String)temp[8] );
			dTemp.getPessoa().setTelefone( (String)temp[9] );
		}
		
		return dTemp;
		
	}

	 
	
	/**
	 * <p>Busca o usuário externo ativo de um UsuarioBiblioteca que não está cancelado, ou seja, não foi cancelado manualmente, nem o prazo vínculo expirou.<p/>
	 * <p><strong>Só era para retornar um usuário externo ativo por usuário biblioteca, caso retorne mais de um é um erro que precisa ser informado ao usuário.</strong> <p/>
	 * 
	 * @param p
	 * @return
	 * @throws DAOException
	 */
	
	public List<UsuarioExternoBiblioteca> findUsuariosExternosBibliotecaByPessoa(int idPessoa) throws DAOException {
		
		String hql = "select ube.id FROM UsuarioExternoBiblioteca ube " +
		" where ube.usuarioBiblioteca.pessoa.id = " + idPessoa + " " +
		RESTRICOES_CONSULTA_USUARIOEXTERNO_BIBLIOTECA;
		
		Query q = getSession().createQuery(hql);
		q.setDate("hoje", new Date());
		
		List<UsuarioExternoBiblioteca> usuarios = new ArrayList<UsuarioExternoBiblioteca>();
		
		
		@SuppressWarnings("unchecked")
		List<Object> dadosUsuarios = q.list();
		
		for (Object object : dadosUsuarios) {
			Integer idUsuarioExterno = (Integer) object;
			
			if(idUsuarioExterno != null){
				UsuarioExternoBiblioteca uex = new UsuarioExternoBiblioteca( idUsuarioExterno );
				usuarios.add(uex);
			}
		}
		// Só é para existir um cadastrado para o usuário externo, se retorna mais de um precisa ser 
		// informado ao usuário para que os outros seja desativados
		return usuarios;
		
	}
	
	
	/**
	 * <p>Busca o usuário externo ativo de um UsuarioBiblioteca que não está cancelado, ou seja, não foi cancelado manualmente, nem o prazo vínculo expirou.<p/>
	 * <p><strong>Só era para retornar um usuário externo ativo por usuário biblioteca, caso retorne mais de um é um erro que precisa ser informado ao usuário.</strong> <p/>
	 * 
	 * @param p
	 * @return
	 * @throws DAOException
	 */
	
	public UsuarioExternoBiblioteca findInfoUsuarioExternoBiblioteca(int idUsuarioExterno) throws DAOException {
		
		String hql = "select ub.id, ub.usuarioBiblioteca, ub.prazoVinculo, ub.documento, ub.cancelado, ub.motivoCancelamento, ub.ativo, pessoa.cpf_cnpj, pessoa.passaporte, pessoa.id, pessoa.nome, "+PROJECAO_DADOS_CONTATO_PESSOA
		+" FROM UsuarioExternoBiblioteca ub " +
		" INNER JOIN ub.usuarioBiblioteca.pessoa pessoa "+
		" LEFT JOIN pessoa.enderecoContato endereco "+
		" WHERE ub.id = " + idUsuarioExterno;
		
		Query c = getSession().createQuery(hql);
		
		UsuarioExternoBiblioteca uex = null;
		
		
		@SuppressWarnings("unchecked")
		List<Object> dadosUsuarios = c.list();
		
		for (Object object : dadosUsuarios) {
			Object[] dados = (Object[]) object;
			uex = new UsuarioExternoBiblioteca( (Integer) dados[0], (UsuarioBiblioteca) dados[1], (Date) dados[2], (String)dados[3],  null, null, (Boolean) dados[4], (String) dados[5], (Boolean) dados[6] );
			
			String hql2 = "select unidade FROM UsuarioExternoBiblioteca ueb WHERE ueb.id = "+uex.getId();
			Query c2 = getSession().createQuery(hql2);
			uex.setUnidade( (Unidade)  c2.uniqueResult());
			
			String hql3 = "select convenio FROM UsuarioExternoBiblioteca ueb WHERE ueb.id = "+uex.getId();
			Query c3 = getSession().createQuery(hql3);
			uex.setConvenio( (Convenio)  c3.uniqueResult());
			
			uex.setUsuarioBiblioteca( new UsuarioBiblioteca());
			
			Pessoa p = new Pessoa();
			p.setCpf_cnpj((Long) dados[7]);
			p.setPassaporte((String) dados[8]);
			p.setId((Integer) dados[9]);
			p.setNome((String) dados[10]);
			
			uex.getUsuarioBiblioteca().setPessoa(p);
			uex.getUsuarioBiblioteca().getPessoa().setEnderecoContato( new Endereco(0, null, null, null, (String)dados[11], (String)dados[12], (String)dados[13], (String)dados[14], "", "", (String)dados[15], "", true));
			
			uex.getUsuarioBiblioteca().getPessoa().setEmail( (String)dados[16] );
			uex.getUsuarioBiblioteca().getPessoa().setTelefone( (String)dados[17] );
			
			
		}
		// Só é para existir um cadastrado para o usuário externo, se retorna mais de um precisa ser 
		// informado ao usuário para que os outros seja desativados
		return uex;
		
	}
	
	/**
	 *  Retorna as informação para visualização do UsuarioBiblioteca que é uma biblioteca.
	 *
	 * @param idBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public Biblioteca findInfoBibliotecaBiblioteca(int idBiblioteca) throws DAOException{
		String hql = "SELECT biblioteca.identificador, biblioteca.descricao, "+PROJECAO_DADOS_CONTADO_BIBLIOTECA
		+" FROM Biblioteca biblioteca "
		+" LEFT JOIN biblioteca.endereco endereco "
		+" WHERE  biblioteca.id = " + idBiblioteca ;
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> objetosServidores = q.list();
		
		Biblioteca biblioteca = new Biblioteca();
		
		for (Object object : objetosServidores) {
			Object[] temp = (Object[]) object;
			
			biblioteca = new Biblioteca((String)temp[0], (String)temp[1]);
			biblioteca.setEndereco( new Endereco(0, null, null, null, (String)temp[2], (String)temp[3], (String)temp[4], (String)temp[5], "", "", (String)temp[6], "", true));
			biblioteca.setEmail( (String)temp[7] );
			biblioteca.setTelefone( (String)temp[8] );
		}
		
		return biblioteca;
	}
	
	
	/**
	 *      <p>Monta as informações para usuário sem vínculos com a biblioteca. Quando o usuário é uma pessoa</p>
	 *
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Pessoa findInfoPessoaSemVinculo(int idPessoa) throws DAOException {

		String hql = "SELECT pessoa.cpf_cnpj, pessoa.nome, "+PROJECAO_DADOS_CONTATO_PESSOA
		+" FROM Pessoa pessoa "
		+" LEFT JOIN pessoa.enderecoContato endereco "
		+" WHERE  pessoa.id = " + idPessoa ;
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> objetosServidores = q.list();
		
		Pessoa pessoa = new Pessoa();
		
		for (Object object : objetosServidores) {
			Object[] temp = (Object[]) object;
			
			pessoa = new Pessoa(idPessoa, (String)temp[1]);
			pessoa.setCpf_cnpj( (Long) temp[0]);
			pessoa.setEnderecoContato( new Endereco(0, null, null, null, (String)temp[2], (String)temp[3], (String)temp[4], (String)temp[5], "", "", (String)temp[6], "", true));
			pessoa.setEmail( (String)temp[7] );
			pessoa.setTelefone( (String)temp[8] );
		}
		
		return pessoa;
		
	}
	
	/**
	 *      <p>Monta as informações para usuário sem vínculos com a biblioteca. Quando o usuário é uma pessoa</p>
	 *
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Biblioteca findInfoBibliotecaSemVinculo(int idBiblioteca) throws DAOException {

		String hql = "SELECT biblioteca.identificador, biblioteca.descricao, "+PROJECAO_DADOS_CONTADO_BIBLIOTECA
		+" FROM Biblioteca biblioteca "
		+" LEFT JOIN biblioteca.endereco endereco "
		+" WHERE  biblioteca.id = " + idBiblioteca ;
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> objetosServidores = q.list();
		
		Biblioteca biblioteca = new Biblioteca();
		
		for (Object object : objetosServidores) {
			Object[] temp = (Object[]) object;
			
			biblioteca = new Biblioteca((String)temp[0], (String)temp[1]);
			biblioteca.setEndereco( new Endereco(0, null, null, null, (String)temp[2], (String)temp[3], (String)temp[4], (String)temp[5], "", "", (String)temp[6], "", true));
			biblioteca.setEmail( (String)temp[7] );
			biblioteca.setTelefone( (String)temp[8] );
		}
		
		return biblioteca;
		
	}
	
	/**
	 * 
	 * Retorna as informações do discente projetadas para verificar se o discente pode realizar emprestimos
	 *
	 * @param idDiscente
	 * @return
	 */
	public Discente findInformacoesDiscenteVerificarOperacoes(int idDiscente) throws DAOException{
		
		String hql = "SELECT curso.id, unidade.id, gestora.id "
		+" FROM Discente discente "
		+" LEFT JOIN discente.curso curso "
		+" LEFT JOIN curso.unidade unidade "
		+" LEFT JOIN unidade.gestora as gestora "
		+" WHERE  discente.id = " + idDiscente ;
		
		Query q = getSession().createQuery(hql);		
		
		Discente discente = new Discente();
		
		Object[] dadosDiscente = (Object[]) q.uniqueResult();
		
		if(dadosDiscente != null){
			discente.setId( idDiscente );
			
			Curso curso = new Curso((Integer)dadosDiscente[0] != null ? (Integer)dadosDiscente[0] : 0);
			Unidade unidade = new Unidade((Integer)dadosDiscente[1] != null  ? (Integer)dadosDiscente[1] : 0 );
			Unidade gestora = new Unidade((Integer)dadosDiscente[2]  != null  ? (Integer)dadosDiscente[2] : 0);
			
			unidade.setGestora(gestora);
			curso.setUnidade(unidade);
			discente.setCurso( curso );
		}
		
		return discente;
	}
	
	
	/**
	 * 
	 * Retorna as informações do discente projetadas para verificar se o discente pode realizar emprestimos
	 *
	 * @param idDiscente
	 * @return
	 */
	public Biblioteca findInformacoesBibliotecaVerificarOperacoes(int idBiblioteca) throws DAOException{
		
		String hql = "SELECT unidade.id, responsavel.id, responsavel.tipoAcademica, gestora.id "
		+" FROM Biblioteca biblioteca "
		+" INNER JOIN biblioteca.unidade unidade "
		+" LEFT JOIN unidade.unidadeResponsavel responsavel "
		+" LEFT JOIN unidade.gestora as gestora "
		+" WHERE  biblioteca.id = :idBiblioteca " ;
		
		Query q = getSession().createQuery(hql);		
		q.setInteger("idBiblioteca", idBiblioteca);
		
		Biblioteca biblioteca = new Biblioteca();
		
		Object[] dadosBiblioteca = (Object[]) q.uniqueResult();
		
		if(dadosBiblioteca != null){
			biblioteca.setId( idBiblioteca );
			
			Unidade unidade = new Unidade((Integer)dadosBiblioteca[0] != null  ? (Integer)dadosBiblioteca[0] : 0 );
			Unidade unidadeResponsavel = new Unidade((Integer)dadosBiblioteca[1] != null  ? (Integer)dadosBiblioteca[1] : 0 );
			unidadeResponsavel.setTipoAcademica( (Integer)dadosBiblioteca[2]  != null  ? (Integer)dadosBiblioteca[2] : null);
			Unidade unidadeGestora = new Unidade((Integer)dadosBiblioteca[3]  != null  ? (Integer)dadosBiblioteca[3] : 0);
			
			unidade.setUnidadeResponsavel(unidadeResponsavel);
			unidade.setGestora(unidadeGestora);
			biblioteca.setUnidade(unidade);
			
		}
		
		return biblioteca;
	}
	
	
	//////////////////////////////////////////////////////////
	
	
	
	
	/**
	 *      <p>Monta as informações para usuário sem vínculos com a biblioteca. Quando o usuário é uma pessoa</p>
	 *
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public boolean pessoaPossuiVinculoInfantil(int idPessoa, boolean somenteAtivos) throws DAOException {

		String hql = "SELECT discente.id "
		+" FROM Discente discente "
		+" LEFT JOIN discente.formaIngresso forma " 
		+" WHERE  discente.pessoa.id = :idPessoa " 
		+" AND discente.nivel = :nivel "
		+ (somenteAtivos ? RESTRICOES_CONSULTA_DISCENTE_BIBLIOTECA: ""); // Na quitação, se tiver qualquer vínculo infantil pode quitar, nos outros casos de uso, apenas ativos.
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idPessoa", idPessoa);
		q.setCharacter("nivel", NivelEnsino.INFANTIL);

		
		@SuppressWarnings("unchecked")
		List<Object> disDiscentesInfantis = q.list();
		
		return disDiscentesInfantis.size() > 0;
	}
	
}


