/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/06/2011
 *
 */
package br.ufrn.sigaa.ava.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.dominio.VideoTurma;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO responsável por consultas específicas aos vídeos da turma virtual. 
 * @author Fred_Castro
 *
 */
public class VideoTurmaDao extends GenericSigaaDAO {

	/**
	 * Atualiza o VideoTurma informando o id do arquivo do vídeo convertido.
	 * 
	 * @param idVideo
	 * @param idArquivoConvertido
	 * @throws DAOException
	 */
	public void atualizaVideoConvertido(int idVideo, int idArquivoConvertido) throws DAOException {
		getSession().createSQLQuery("update ava.video_turma set id_arquivo = null, id_arquivo_convertido = " + idArquivoConvertido + " where id_video_turma = " + idVideo).executeUpdate();
	}
	
	/**
	 * Atualiza o VideoTurma com as informações recebidas do conversor (se houve um erro ou mensagens durante o progresso da conversão).
	 * 
	 * @param idVideo
	 * @param mensagem
	 * @param erro
	 * @throws DAOException
	 */
	public void informaProgressoConversao (int idVideo, String mensagem, boolean erro) throws DAOException {
		System.out.println(mensagem);
		getSession().createSQLQuery("update ava.video_turma set erro = " + ( erro ? "true" : "false" ) + ", mensagem_conversao = '" + mensagem.replaceAll("'", "''") + "' where id_video_turma = " + idVideo).executeUpdate();
	}
	
	/**
	 * Retorna os videos de determinada turma de acordo com o acesso.
	 * @param turma
	 * @param isDocente
	 * @return
	 */
	public List<VideoTurma> findVideosByTurma (Turma turma,boolean isDocente) {
		try {
		
		String projecao = " id, titulo, descricao, telaCheia, link, idArquivo, idArquivoConvertido, converter, contentType, abrirEmNovaJanela , topicoAula.id, material.id, material.ordem, material.nivel ";
		
		String hql = "SELECT " + projecao + " FROM VideoTurma v " +
					 "WHERE v.ativo = trueValue() AND v.turma.id = :idTurma";
					 
		if (!isDocente) {
			hql += " AND v.topicoAula.visivel = trueValue()";  
		}
		
		hql += " ORDER BY v.titulo";
		
		Query q =  getSession().createQuery(hql);
		
		q.setInteger("idTurma", turma.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = q.list();
		
		ArrayList<VideoTurma> lista = new ArrayList<VideoTurma>();
		if ( results != null ) {
			
			for(Object[] linha:results) {
				if(linha != null) {
					Integer i = 0;
					VideoTurma t = new VideoTurma();
					
					t.setId((Integer) linha[i++]);
					t.setTitulo((String) linha[i++]);
					t.setDescricao((String) linha[i++]);
					t.setTelaCheia((Boolean)linha[i++]);
					t.setLink((String)linha[i++]);
					t.setIdArquivo((Integer)linha[i++]);
					t.setIdArquivoConvertido((Integer)linha[i++]);
					t.setConverter((Boolean)linha[i++]);
					t.setContentType((String)linha[i++]);
					t.setAbrirEmNovaJanela((Boolean)linha[i++]);
					
					
					if (linha[i] != null) {
						TopicoAula a = new TopicoAula();
						a.setId((Integer)linha[i++]);
						t.setTopicoAula(a);
					}
					else {
						i++;
					}
										
					MaterialTurma m = new MaterialTurma();
					m.setId((Integer)linha[i++]);
					m.setOrdem((Integer) linha[i++]);
					m.setNivel((Integer) linha[i++]);
					
					t.setMaterial(m);
					lista.add(t);
				}
									
			}
		}
						
		return lista;
		
	} catch (DAOException e) {
		throw new TurmaVirtualException(e);
	}
		
	}

}
