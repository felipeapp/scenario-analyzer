package br.ufrn.sigaa.ead.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.erros.ArqException;

/**
 * Classe auxiliar para exibir os checkboxes das tabelas.
 */
public class TabelaDadosEadSql {
	private int id;
	private boolean selecionada;
	private String sql;
	private String nome;
	
	public static final int TABELA_ATIVIDADE = 1;
	public static final int TABELA_AVALIACAO_DISCENTE = 2;
	public static final int TABELA_BANCO = 3;
	public static final int TABELA_CARGO_ACADEMICO = 4;
	public static final int TABELA_COMPONENTE_CURRICULAR = 5;
	public static final int TABELA_CONTA_BANCARIA = 6;
	public static final int TABELA_COORDENACAO_CURSO = 7;
	public static final int TABELA_COORDENACAO_POLO = 8;
	public static final int TABELA_CURRICULO = 9;
	public static final int TABELA_CURRICULO_COMPONENTE_CURRICULAR = 10;
	public static final int TABELA_CURSO = 11;
	public static final int TABELA_DISCENTE = 12;
	public static final int TABELA_DISCENTE_TURMA = 13;
	public static final int TABELA_DOCENTE_TURMA = 14;
	public static final int TABELA_ENDERECO = 15;
	public static final int TABELA_FICHA_AVALIACAO = 16;
	public static final int TABELA_FORMACAO = 17;
	public static final int TABELA_HORARIO_TUTOR = 18;
	public static final int TABELA_IDENTIDADE = 19;
	public static final int TABELA_ITEM_AVALIACAO = 20;
	public static final int TABELA_METODOLOGIA_AVALIACAO = 21;
	public static final int TABELA_MUNICIPIO = 22;
	public static final int TABELA_NOTA_ITEM_AVALIACAO = 23;
	public static final int TABELA_NOTA_UNIDADE = 24;
	public static final int TABELA_PESSOA = 25;
	public static final int TABELA_POLO = 26;
	public static final int TABELA_POLO_CURSO = 27;
	public static final int TABELA_SERVIDOR = 28;
	public static final int TABELA_SITUACAO_MATRICULA = 29;
	public static final int TABELA_STATUS_DISCENTE = 30;
	public static final int TABELA_TIPO_COMPONENTE_CURRICULAR = 31;
	public static final int TABELA_TURMA = 32;
	public static final int TABELA_TUTOR_ORIENTADOR = 33;
	public static final int TABELA_TUTORIA_ALUNO = 34;
	public static final int TABELA_USUARIO = 35;
	public static final int TABELA_VINCULO_TUTOR = 36;
	public static final int TABELA_ITEM_PROGRAMA = 37;
	public static final int TABELA_PROGRAMA = 38;
	public static final int TABELA_DOCENTE_EXTERNO = 39;
	public static final int TABELA_ESTRUTURA_CURRICULAR = 40;
	public static final int TABELA_MODULO = 41;
	public static final int TABELA_MODULO_COMPONENTE_CURRICULAR = 42;
	public static final int TABELA_MODULO_CURRICULAR = 43;
	
	public TabelaDadosEadSql (int id, boolean metropoleDigital) throws ArqException {
		this.id = id;
		
		switch (id){
			case TABELA_ATIVIDADE:
				sql = "select a.id_registro_atividade as id_atividade, a.id_coordenador, a.supervisor, o.id_servidor as orientador, m.ano, m.periodo, a.id_matricula_componente as id_discente_turma, m.id_componente_curricular from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)";
				nome = "atividade";
			break;
			case TABELA_AVALIACAO_DISCENTE:
				sql = "select id as id_avaliacao_discente, data_cadastro, id_usuario, id_ficha as id_ficha_avaliacao, observacoes, semana from ead.avaliacao_discente_ead order by id_avaliacao_discente";
				nome = "avaliacao_discente";
			break;
			case TABELA_BANCO:
				sql = "select id_banco, denominacao as nome from banco";
				nome = "banco";
			break;
			case TABELA_CARGO_ACADEMICO:
				sql = "select id_cargo_academico, descricao from ensino.cargo_academico";
				nome = "cargo_academico";
			break;
			case TABELA_COMPONENTE_CURRICULAR:
				if (metropoleDigital)
					sql = "select distinct cc.id_disciplina as id_componente_curricular, cc.codigo, d.nome, d.ementa, d.ch_aula, d.ch_laboratorio, d.ch_estagio, u.nome as unidade_responsavel, cc.id_tipo_componente as id_tipo_componente_curricular from ensino.componente_curricular cc join comum.unidade u on cc.id_unidade = u.id_unidade join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes join tecnico.modulo_disciplina md using (id_disciplina) join tecnico.modulo m using (id_modulo) join tecnico.modulo_curricular using (id_modulo) join tecnico.estrutura_curricular_tecnica ect using (id_estrutura_curricular) where ect.id_curso = 2247254 and ect.ativa = true";
				else
					sql = "select distinct cc.id_disciplina as id_componente_curricular, cc.codigo, d.nome, d.ementa, d.ch_aula, d.ch_laboratorio, d.ch_estagio, u.nome as unidade_responsavel, cc.id_tipo_componente as id_tipo_componente_curricular from ensino.componente_curricular cc join comum.unidade u on cc.id_unidade = u.id_unidade join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = cc.id_disciplina join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso) group by cc.id_disciplina, cc.codigo, d.nome, d.ementa, d.ch_aula, d.ch_laboratorio, d.ch_estagio, u.nome, cc.id_tipo_componente";
				nome = "componente_curricular";
			break;
			case TABELA_CONTA_BANCARIA:
				sql = "select id_conta_bancaria, id_banco, numero, agencia from comum.conta_bancaria where id_conta_bancaria in (select id_conta_bancaria from comum.pessoa where id_pessoa in (select id_pessoa from comum.pessoa p where p.id_pessoa in (select id_pessoa from rh.servidor where id_servidor in (select id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = m.id_componente_curricular join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select a.id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select o.id_servidor from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select id_servidor from ensino.coordenacao_curso cc where cc.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select d.id_docente from ensino.docente_turma d join ensino.turma t on t.id_turma = d.id_turma and t.distancia = trueValue())) or p.id_pessoa in (select id_pessoa from ead.coordenacao_polo) or p.id_pessoa in (select d.id_pessoa from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null) or p.id_pessoa in (select id_pessoa from ead.tutor_orientador)))";
				nome = "conta_bancaria";
			break;
			case TABELA_COORDENACAO_CURSO:
				sql = "select id_coordenacao_curso, id_curso, id_servidor, data_inicio_mandato, data_fim_mandato, id_cargo_academico from ensino.coordenacao_curso cc where cc.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)";
				nome = "coordenacao_curso";
			break;
			case TABELA_COORDENACAO_POLO:
				sql = "select id_coordenacao_polo, id_pessoa, id_polo, inicio, fim from ead.coordenacao_polo";
				nome = "coordeacao_polo";
			break;
			case TABELA_CURRICULO:
				sql = "select distinct c.id_curriculo, c.id_curso from graduacao.curriculo c join curso on curso.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)";
				nome = "curriculo";
			break;
			case TABELA_CURRICULO_COMPONENTE_CURRICULAR:
				sql = "select distinct cc.id_curriculo_componente as id_curriculo_componente_curricular, cc.semestre_oferta, cc.obrigatoria as obrigatoria, cc.id_curriculo, cc.id_componente_curricular from graduacao.curriculo_componente cc join graduacao.curriculo c on cc.id_curriculo = c.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)";
				nome = "curriculo_componente_curricular";
			break;
			case TABELA_CURSO:
				if (metropoleDigital)
					sql = "select c.id_curso, c.nome, c.codigo, u.nome as unidade_responsavel from curso c join comum.unidade u on c.id_unidade = u.id_unidade where c.id_curso = 2247254";
				else
					sql = "select distinct c.id_curso, c.nome, c.codigo, u.nome as unidade_responsavel from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso";
				nome = "curso";
			break;
			case TABELA_DISCENTE:
				if (metropoleDigital)
					sql = "select d.id_discente, d.id_pessoa, d.periodo_ingresso, d.ano_ingresso, d.matricula, d.matricula_antiga, d.id_curso, d.status as id_status from discente d where d.id_curso = 2247254";
				else
					sql = "select d.id_discente, d.id_pessoa, d.periodo_ingresso, d.ano_ingresso, d.matricula, d.matricula_antiga, d.id_curso, g.id_polo, d.status as id_status from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null";
				nome = "discente";
			break;
			case TABELA_DISCENTE_TURMA:
				if (metropoleDigital)
					sql = "select mc.id_matricula_componente as id_discente_turma, mc.media_final, mc.recuperacao, mc.id_discente, mc.id_turma, mc.id_situacao_matricula from ensino.matricula_componente mc where id_discente in (select d.id_discente from discente d where id_curso = 2247254)";
				else
					sql = "select mc.id_matricula_componente as id_discente_turma, mc.media_final, mc.recuperacao, mc.id_discente, mc.id_turma, mc.id_situacao_matricula from ensino.matricula_componente mc where id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)";
				nome = "discente_turma";
			break;
			case TABELA_DOCENTE_EXTERNO:
				if (metropoleDigital)
					sql = "select id_servidor, matricula, id_pessoa from ensino.docente_externo where id_docente_externo in (select dt.id_docente_externo from ensino.docente_turma dt join ensino.turma t using (id_turma) where t.id_curso = 2247254)";
				nome = "docente_externo";
			break;
			case TABELA_DOCENTE_TURMA:
				if (metropoleDigital)
					sql = "select dt.id_docente_turma, t.id_turma, dt.id_docente_externo from ensino.docente_turma dt join ensino.turma t using (id_turma) where t.id_curso = 2247254";
				else
					sql = "select d.id_docente_turma, d.id_docente as id_servidor, d.id_turma from ensino.docente_turma d join ensino.turma t on t.id_turma = d.id_turma and t.distancia = trueValue()";
				nome = "docente_turma";
			break;
			case TABELA_ESTRUTURA_CURRICULAR:
				if (metropoleDigital)
					sql = "select id_estrutura_curricular, ativa, id_curso from tecnico.estrutura_curricular_tecnica where id_curso = 2247254 and ativa = true";
				nome = "estrutura_curricular";
			break;
			case TABELA_ENDERECO:
				if (metropoleDigital)
					sql = "select id_endereco, id_municipio, logradouro, numero, complemento, cep, bairro from comum.endereco where id_endereco in (select id_endereco_contato from comum.pessoa where id_pessoa in (select id_pessoa from ensino.docente_externo where id_docente_externo in (select dt.id_docente_externo from ensino.docente_turma dt join ensino.turma t using (id_turma) where t.id_curso = 2247254) union (select d.id_pessoa from discente d where d.id_curso = 2247254)))";
				else
					sql = "select id_endereco, id_municipio, logradouro, numero, complemento, cep, bairro from comum.endereco where id_endereco in (select id_endereco_contato from comum.pessoa where id_pessoa in (select id_pessoa from comum.pessoa p where p.id_pessoa in (select id_pessoa from rh.servidor where id_servidor in (select id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = m.id_componente_curricular join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select a.id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select o.id_servidor from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select id_servidor from ensino.coordenacao_curso cc where cc.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select d.id_docente from ensino.docente_turma d join ensino.turma t on t.id_turma = d.id_turma and t.distancia = trueValue())) or p.id_pessoa in (select id_pessoa from ead.coordenacao_polo) or p.id_pessoa in (select d.id_pessoa from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null) or p.id_pessoa in (select id_pessoa from ead.tutor_orientador)))";
				nome = "endereco";
			break;
			case TABELA_FICHA_AVALIACAO:
				sql = "select id as id_ficha_avaliacao, id_discente, periodo, ano from ead.ficha_avaliacao_ead";
				nome = "ficha_avaliacao";
			break;
			case TABELA_FORMACAO:
				sql = "select id_formacao, denominacao from rh.formacao";
				nome = "formacao";
			break;
			case TABELA_HORARIO_TUTOR:
				sql = "select id as id_horario_tutor, id_tutor as id_tutor_orientador, dia, hora_inicio, hora_fim from ead.horario_tutor";
				nome = "horario_tutor";
			break;
			case TABELA_IDENTIDADE:
				if (metropoleDigital)
					sql = "select id_identidade, numero from comum.identidade where id_identidade in (select id_identidade from comum.pessoa where id_pessoa in (select id_pessoa from ensino.docente_externo where id_docente_externo in (select dt.id_docente_externo from ensino.docente_turma dt join ensino.turma t using (id_turma) where t.id_curso = 2247254) union (select d.id_pessoa from discente d where d.id_curso = 2247254)))";
				else
					sql = "select id_identidade, numero from identidade where id_identidade in (select id_identidade from comum.pessoa where id_pessoa in (select id_pessoa from comum.pessoa p where p.id_pessoa in (select id_pessoa from rh.servidor where id_servidor in (select id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = m.id_componente_curricular join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select a.id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select o.id_servidor from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select id_servidor from ensino.coordenacao_curso cc where cc.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select d.id_docente from ensino.docente_turma d join ensino.turma t on t.id_turma = d.id_turma and t.distancia = trueValue())) or p.id_pessoa in (select id_pessoa from ead.coordenacao_polo) or p.id_pessoa in (select d.id_pessoa from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null) or p.id_pessoa in (select id_pessoa from ead.tutor_orientador)))";
				nome = "identidade";
			break;
			case TABELA_ITEM_AVALIACAO:
				sql = "select id as id_item_avaliacao, nome, ativo, id_usuario as id_usuario_tutor, data_cadastro, id_metodologia as id_metodologia_avaliacao from ead.item_avaliacao_ead";
				nome = "item_avaliacao";
			break;
			case TABELA_METODOLOGIA_AVALIACAO:
				sql = "select id as id_metodologia_avaliacao, id_curso, porcentagem_tutor, porcentagem_professor, metodo_avaliacao, ativa as ativo, numero_aulas, aulas_ativas, cabecalho_ficha from ead.metodologia_avaliacao";
				nome = "metodologia_avaliacao";
			break;
			case TABELA_MODULO:
				if (metropoleDigital)
					sql = "select m.id_modulo, m.descricao, m.codigo from tecnico.modulo m join tecnico.modulo_curricular using (id_modulo) join tecnico.estrutura_curricular_tecnica using (id_estrutura_curricular) where id_curso = 2247254 and ativa = true";
				nome = "modulo";
			break;
			case TABELA_MODULO_COMPONENTE_CURRICULAR:
				if (metropoleDigital)
					sql = "select md.id_modulo_disciplina, md.id_modulo, id_disciplina from tecnico.modulo_disciplina md join tecnico.modulo m using (id_modulo) join tecnico.modulo_curricular using (id_modulo) join tecnico.estrutura_curricular_tecnica using (id_estrutura_curricular) where id_curso = 2247254 and ativa = true";
				nome = "modulo_componente_curricular";
			break;
			case TABELA_MODULO_CURRICULAR:
				if (metropoleDigital)
					sql = "select id_modulo_curricular, id_estrutura_curricular, id_modulo from tecnico.modulo_curricular join tecnico.estrutura_curricular_tecnica using (id_estrutura_curricular) where id_curso = 2247254 and ativa = true";
				nome = "modulo_curricular";
			break;
			case TABELA_MUNICIPIO:
				if (metropoleDigital)
					sql = "select m.id_municipio, m.nome, uf.sigla as uf from comum.municipio m join comum.unidade_federativa uf on uf.id_unidade_federativa = m.id_unidade_federativa";
				else
					sql = "select m.id_municipio, m.nome, uf.sigla as uf from comum.municipio m join comum.unidade_federativa uf on uf.id_unidade_federativa = m.id_unidade_federativa";
				nome = "municipio";
			break;
			case TABELA_NOTA_ITEM_AVALIACAO:
				sql = "select id as id_nota_item_avaliacao, id_avaliacao as id_avaliacao_discente, id_componente as id_componente_curricular, nota, id_item as id_item_avaliacao from ead.nota_item_avaliacao_ead";
				nome = "nota_item_avaliacao";
			break;
			case TABELA_NOTA_UNIDADE:
				if (metropoleDigital)
					sql = "select id_nota_unidade, faltas, unidade, nota, id_matricula_componente as id_discente_turma, recuperacao from ensino.nota_unidade where id_matricula_componente in (select mc.id_matricula_componente from ensino.matricula_componente mc where id_discente in (select d.id_discente from discente d where d.id_curso = 2247254))";
				else
					sql = "select id_nota_unidade, faltas, unidade, nota, id_matricula_componente as id_discente_turma, recuperacao from ensino.nota_unidade where id_matricula_componente in (select mc.id_matricula_componente from ensino.matricula_componente mc where id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null))";
				nome = "nota_unidade";
			break;
			case TABELA_PESSOA:
				if (metropoleDigital)
					sql = "select distinct id_pessoa, nome, id_identidade, data_nascimento, sexo, id_endereco_contato as id_endereco, cpf_cnpj as cpf, email, telefone_fixo as telefone from comum.pessoa where id_pessoa in (select id_pessoa from ensino.docente_externo where id_docente_externo in (select dt.id_docente_externo from ensino.docente_turma dt join ensino.turma t using (id_turma) where t.id_curso = 2247254) union (select d.id_pessoa from discente d where d.id_curso = 2247254))";
				else
					sql = "select distinct id_pessoa, nome, id_identidade, data_nascimento, sexo, id_endereco_contato as id_endereco, cpf_cnpj as cpf, email, id_conta_bancaria, telefone_fixo as telefone from comum.pessoa p where p.id_pessoa in (select id_pessoa from rh.servidor where id_servidor in (select id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = m.id_componente_curricular join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select a.id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select o.id_servidor from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select id_servidor from ensino.coordenacao_curso cc where cc.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select d.id_docente from ensino.docente_turma d join ensino.turma t on t.id_turma = d.id_turma and t.distancia = trueValue())) or p.id_pessoa in (select id_pessoa from ead.coordenacao_polo) or p.id_pessoa in (select d.id_pessoa from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null) or p.id_pessoa in (select id_pessoa from ead.tutor_orientador)";
				nome = "pessoa";
			break;
			case TABELA_POLO:
				sql = "select id_polo, endereco, telefone, cep, horariofuncionamento as horario_funcionamento, id_cidade as id_municipio, id_curso from ead.polo";
				nome = "polo";
			break;
			case TABELA_POLO_CURSO:
				sql = "select id_polo_curso, id_curso, id_polo from ead.polo_curso";
				nome = "polo_curso";
			break;
			case TABELA_SERVIDOR:
				sql = "select id_servidor, siape, id_pessoa, lotacao, id_formacao, regime_trabalho, dedicacao_exclusiva from rh.servidor where id_servidor in (select id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = m.id_componente_curricular join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select a.id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select o.id_servidor from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select id_servidor from ensino.coordenacao_curso cc where cc.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select d.id_docente from ensino.docente_turma d join ensino.turma t on t.id_turma = d.id_turma and t.distancia = trueValue())";
				nome = "servidor";
			break;
			case TABELA_SITUACAO_MATRICULA:
				sql = "select * from ensino.situacao_matricula";
				nome = "situacao_matricula";
			break;
			case TABELA_STATUS_DISCENTE:
				sql = "select status as id_status, descricao from status_discente";
				nome = "status_discente";
			break;
			case TABELA_TIPO_COMPONENTE_CURRICULAR:
				sql = "select id_tipo_disciplina as id_tipo_componente_curricular, descricao from ensino.tipo_componente_curricular;";
				nome = "tipo_componente_curricular";
			break;
			case TABELA_TURMA:
				if (metropoleDigital)
					sql = "select t.id_turma, t.id_polo, t.id_disciplina as id_componente_curricular, t.codigo as codigo_turma, t.ano, t.periodo, t.data_inicio, t.data_fim from ensino.turma t join ensino.componente_curricular cc using (id_disciplina) join comum.unidade u on cc.id_unidade = u.id_unidade join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes join tecnico.modulo_disciplina md using (id_disciplina) join tecnico.modulo m using (id_modulo) join tecnico.modulo_curricular using (id_modulo) join tecnico.estrutura_curricular_tecnica ect using (id_estrutura_curricular) where ect.id_curso = 2247254 and ect.ativa = true";
				else
					sql = "select id_turma, id_polo, t.id_disciplina as id_componente_curricular, " +
							"('' || ano || periodo || '_' || t.codigo || '_' || cc.codigo) as codigo_turma, " +
							"ano, periodo, data_inicio, data_fim " +
							"from ensino.turma t join ensino.componente_curricular cc on (t.id_disciplina = cc.id_disciplina)" +
							"where distancia = trueValue()";
				nome = "turma";
			break;
			case TABELA_TUTOR_ORIENTADOR:
				sql = "select id_tutor_orientador, id_pessoa, id_polo_curso, id_vinculo from ead.tutor_orientador";
				nome = "tutor_orientador";
				break;
			case TABELA_TUTORIA_ALUNO:
				sql = "select id_tutoria_aluno, id_discente, id_tutor as id_tutor_orientador, inicio, fim, ativo from ead.tutoria_aluno";
				nome = "tutoria_aluno";
				break;
			case TABELA_USUARIO:
				if (metropoleDigital)
					sql = "select id_usuario, login, senha, inativo, id_pessoa from comum.usuario where id_pessoa in (select id_pessoa from ensino.docente_externo where id_docente_externo in (select dt.id_docente_externo from ensino.docente_turma dt join ensino.turma t using (id_turma) where t.id_curso = 2247254) union (select d.id_pessoa from discente d where d.id_curso = 2247254))";
				else
					sql = "select id_usuario, login, senha, inativo, id_pessoa from comum.usuario where id_pessoa in (select id_pessoa from comum.pessoa p where p.id_pessoa in (select id_pessoa from rh.servidor where id_servidor in (select id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = m.id_componente_curricular join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select a.id_coordenador from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select o.id_servidor from ensino.registro_atividade a join ensino.matricula_componente m on m.id_matricula_componente = a.id_matricula_componente join ensino.orientacao_atividade o on o.id_registro_atividade = a.id_registro_atividade and m.id_discente in (select d.id_discente from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null)) or id_servidor in (select id_servidor from ensino.coordenacao_curso cc where cc.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)) or id_servidor in (select d.id_docente from ensino.docente_turma d join ensino.turma t on t.id_turma = d.id_turma and t.distancia = trueValue())) or p.id_pessoa in (select id_pessoa from ead.coordenacao_polo) or p.id_pessoa in (select d.id_pessoa from discente d join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null) or p.id_pessoa in (select id_pessoa from ead.tutor_orientador))";
				nome = "usuario";
			break;
			case TABELA_VINCULO_TUTOR:
				sql = "select id as id_vinculo_tutor, nome from ead.vinculo_tutor";
				nome = "vinculo_tutor";
			break;
			case TABELA_ITEM_PROGRAMA:
				sql = "select i.id as id_item_programa, i.id_componente_curricular, i.aula, i.conteudo from ensino.item_programa i join ensino.componente_curricular cc on cc.id_disciplina = i.id_componente_curricular join comum.unidade u on cc.id_unidade = u.id_unidade join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = cc.id_disciplina join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)";
				nome = "item_programa";
			break;
			case TABELA_PROGRAMA:
				sql = "select p.id_componente_curricular_programa, p.objetivos, p.conteudo, p.competenciashabilidades, p.metodologia, p.procedimentosavaliacao, p.referencias, p.id_componente_curricular, p.periodo, p.ano, p.numunidades, p.caracterizacao from ensino.componente_curricular_programa p join ensino.componente_curricular cc on cc.id_disciplina = p.id_componente_curricular join comum.unidade u on cc.id_unidade = u.id_unidade join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = cc.id_disciplina join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (select c.id_curso from curso c join comum.unidade u on c.id_unidade = u.id_unidade join ead.polo_curso pc on pc.id_curso = c.id_curso group by c.id_curso)";
				nome = "programa";
			break;
			default:
				throw new ArqException ("Nenhuma tabela informada.");
		}
	}
	
	public static List <TabelaDadosEadSql> gerarLista (boolean metropoleDigital) throws ArqException{
		List <TabelaDadosEadSql> tabelas = new ArrayList <TabelaDadosEadSql> ();
		
		if (metropoleDigital){
			tabelas.add(new TabelaDadosEadSql(TABELA_COMPONENTE_CURRICULAR, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_CURSO, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_DISCENTE, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_DISCENTE_TURMA, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_DOCENTE_EXTERNO, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_DOCENTE_TURMA, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_ESTRUTURA_CURRICULAR, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_ENDERECO, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_IDENTIDADE, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_MODULO, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_MODULO_CURRICULAR, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_MODULO_COMPONENTE_CURRICULAR, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_MUNICIPIO, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_NOTA_UNIDADE, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_PESSOA, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_SITUACAO_MATRICULA, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_STATUS_DISCENTE, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_TIPO_COMPONENTE_CURRICULAR, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_TURMA, true));
			tabelas.add(new TabelaDadosEadSql(TABELA_USUARIO, true));
		} else {
			tabelas.add(new TabelaDadosEadSql(TABELA_ATIVIDADE, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_AVALIACAO_DISCENTE, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_BANCO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_CARGO_ACADEMICO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_COMPONENTE_CURRICULAR, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_CONTA_BANCARIA, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_COORDENACAO_CURSO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_COORDENACAO_POLO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_CURRICULO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_CURRICULO_COMPONENTE_CURRICULAR, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_CURSO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_DISCENTE, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_DISCENTE_TURMA, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_DOCENTE_TURMA, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_ENDERECO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_FICHA_AVALIACAO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_FORMACAO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_HORARIO_TUTOR, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_IDENTIDADE, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_ITEM_AVALIACAO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_ITEM_PROGRAMA, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_METODOLOGIA_AVALIACAO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_MUNICIPIO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_NOTA_ITEM_AVALIACAO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_NOTA_UNIDADE, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_PESSOA, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_POLO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_POLO_CURSO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_PROGRAMA, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_SERVIDOR, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_SITUACAO_MATRICULA, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_STATUS_DISCENTE, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_TIPO_COMPONENTE_CURRICULAR, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_TURMA, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_TUTOR_ORIENTADOR, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_TUTORIA_ALUNO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_USUARIO, false));
			tabelas.add(new TabelaDadosEadSql(TABELA_VINCULO_TUTOR, false));
		}
		
		return tabelas;
	}
	
	public int getId() {
		return id;
	}

	public String getSql() {
		return sql;
	}

	public String getNome() {
		return nome;
	}

	public boolean isSelecionada() {
		return selecionada;
	}
	
	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}
}