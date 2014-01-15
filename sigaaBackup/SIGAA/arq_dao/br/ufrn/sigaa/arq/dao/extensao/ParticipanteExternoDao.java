package br.ufrn.sigaa.arq.dao.extensao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.ParticipanteExterno;

/**
 * Dao responsável por buscar no banco informações sobre o participante externo
 * 
 * @author julio
 *
 */
public class ParticipanteExternoDao extends GenericSigaaDAO{

	/**
	 * Método que retorna uma lista de participantes externos a partir  que participaram de ações do coordenador dado seu idServidor
	 * 
	 * @param nome
	 * @param idServidor
	 * @return
	 * @throws DAOException 
	 */
	public Collection<ParticipanteExterno> findByNomeAndCoordenador(String nome, int idServidor) throws DAOException{
		try {
			
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT pm.nome, pm.cpf_cnpj, pm.email, pm.sexo, m.participanteExterno.formacao, m.participanteExterno.instituicao ");
			hql.append(" FROM MembroProjeto m ");
			hql.append(" INNER JOIN m.participanteExterno.pessoa pm ");
			hql.append(" INNER JOIN m.projeto.coordenador coord");
			hql.append(" WHERE coord.servidor.id = :idServidor AND coord.ativo = trueValue() AND ");
			hql.append( UFRNUtils.convertUtf8UpperLatin9("m.participanteExterno.pessoa.nome") + " like " + UFRNUtils.toAsciiUpperUTF8("'" + nome.trim() + "%'") );
			hql.append(" ORDER BY m.participanteExterno.pessoa.nome ASC ");
		
			Query q = getSession().createQuery(hql.toString());
			
			q.setInteger("idServidor", idServidor);
			
			@SuppressWarnings("unchecked")
			Collection<ParticipanteExterno> lista = new ArrayList<ParticipanteExterno>();
			
			List<Object[]> l = q.list();
			
			for(Object[] obj : l){
				int col = 0;
				
				ParticipanteExterno p = new ParticipanteExterno();
				p.getPessoa().setNome((String) obj[col++]);
				p.getPessoa().setCpf_cnpj((Long) obj[col++]);
				p.getPessoa().setEmail((String) obj[col++]);
				p.getPessoa().setSexo((Character) obj[col++]);
				p.setFormacao((Formacao) obj[col++]);
				p.setInstituicao((String) obj[col++]);
				
				if(!lista.contains(p)){
					lista.add(p);
				}
			}
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
}
