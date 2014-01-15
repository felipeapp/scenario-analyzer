<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="ctxadmin" value="/sigaa/administracao/cadastro" />

<div id="cadastro">


<table  class="secao administracao" width="100%" border="0"
	cellspacing="0" cellpadding="0" class="subSistema">
	<tr>
		<th width="33%"><center><h2>Ensino</h2></center></th>
		<th width="33%"><center><h2>Pesquisa</h2></center></th>
		<th width="33%"><center><h2>Outros</h2></center></th>
	</tr>
	<tr>
	</tr>
	<tr valign="top">
		<%-- Ensino --%> 
		<td nowrap="nowrap">
		<ul>
			<li>Area da Sesu
			<ul>
				<li><a href="${ctxadmin}/AreaSesu/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/AreaSesu/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Cargo Acadêmico
			<ul>
				<li><a href="${ctxadmin}/CargoAcademico/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/CargoAcademico/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Estado de Atividade
			<ul>
				<li><a href="${ctxadmin}/EstadoAtividade/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/EstadoAtividade/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Forma de Ingresso


			<ul>
				<li><a href="${ctxadmin}/FormaIngresso/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/FormaIngresso/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Grau Acadêmico

			<ul>
				<li><a href="${ctxadmin}/GrauAcademico/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/GrauAcademico/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Grau de Formação


			<ul>
				<li><a href="${ctxadmin}/GrauFormacao/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/GrauFormacao/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Modalidade de Educação



			<ul>
				<li><a href="${ctxadmin}/ModalidadeEducacao/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/ModalidadeEducacao/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul><ul>
			<li>Natureza do Curso


			<ul>
				<li><a href="${ctxadmin}/NaturezaCurso/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/NaturezaCurso/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul><ul>
			<li>Situação de Curso Hábil


			<ul>
				<li><a href="${ctxadmin}/SituacaoCursoHabil/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/SituacaoCursoHabil/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul><ul>
			<li>Situação de Diploma


			<ul>
				<li><a href="${ctxadmin}/SituacaoDiploma/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/SituacaoDiploma/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul><ul>
			<li>Situação de Matrícula


			<ul>
				<li><a href="${ctxadmin}/SituacaoMatricula/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/SituacaoMatricula/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul><ul>
			<li>Tipo de Atividade


			<ul>
				<li><a href="${ctxadmin}/TipoAtividade/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoAtividade/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul><ul>
			<li>Tipo de Atividade Complementar



			<ul>
				<li><a href="${ctxadmin}/TipoAtividadeComplementar/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoAtividadeComplementar/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Entrada



			<ul>
				<li><a href="${ctxadmin}/TipoEntrada/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoEntrada/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Procedência do Aluno



			<ul>
				<li><a href="${ctxadmin}/TipoProcedenciaAluno/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoProcedenciaAluno/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Rede de Ensino




			<ul>
				<li><a href="${ctxadmin}/TipoRedeEnsino/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoRedeEnsino/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Regime do Aluno




			<ul>
				<li><a href="${ctxadmin}/TipoRegimeAluno/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoRegimeAluno/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Regime Letivo




			<ul>
				<li><a href="${ctxadmin}/TipoRegimeLetivo/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoRegimeLetivo/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		</td>



















		<%-- Pesquisa --%>
		<td nowrap="nowrap">
		<ul>
			<li>Area de Conhecimento da Unesco




			<ul>
				<li><a href="${ctxadmin}/AreaConhecimentoUnesco/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/AreaConhecimentoUnesco/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Classificação da Entidade Financiadora




			<ul>
				<li><a href="${ctxadmin}/ClassificacaoFinanciadora/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/ClassificacaoFinanciadora/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Entidade Financiadora




			<ul>
				<li><a href="${ctxadmin}/EntidadeFinanciadora/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/EntidadeFinanciadora/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Entidade Financiadora de Estágio




			<ul>
				<li><a href="${ctxadmin}/EntidadeFinanciadoraEstagio/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/EntidadeFinanciadoraEstagio/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Entidade Financiadora Outra




			<ul>
				<li><a href="${ctxadmin}/EntidadeFinanciadoraOutra/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/EntidadeFinanciadoraOutra/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Grupo da Entidade Financiadora




			<ul>
				<li><a href="${ctxadmin}/GrupoEntidadeFinanciadora/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/GrupoEntidadeFinanciadora/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Campus da Unidade




			<ul>
				<li><a href="${ctxadmin}/TipoCampusUnidade/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoCampusUnidade/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Orientação do Discente




			<ul>
				<li><a href="${ctxadmin}/TipoOrientacaoDiscente/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoOrientacaoDiscente/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Público Alvo




			<ul>
				<li><a href="${ctxadmin}/TipoPublicoAlvo/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoPublicoAlvo/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		</td>
	




		





		<%-- Outros --%>
		<td nowrap="nowrap">
		<ul>
			<li>Eleição

			<ul>
				<li> <h:commandLink action="#{eleicao.preCadastrar}" value="Cadastrar"/> </li>
				<li><a href="${ctxadmin}/Eleicao/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		
		<ul>
			<li>Candidato

			<ul>
				<li> <h:commandLink action="#{candidato.preCadastrar}" value="Cadastrar"/> </li>
				<li><a href="${ctxadmin}/Candidato/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>

		<ul>
			<li>Estado Civil

			<ul>
				<li><a href="${ctxadmin}/EstadoCivil/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/EstadoCivil/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Instituições de Ensino




			<ul>
				<li><a href="${ctxadmin}/InstituicoesEnsino/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/InstituicoesEnsino/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Município




			<ul>
				<li><a href="${ctxadmin}/Municipio/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/Municipio/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>País

			<ul>
				<li><a href="${ctxadmin}/Pais/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/Pais/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Documento Legal




			<ul>
				<li><a href="${ctxadmin}/TipoDocumentoLegal/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoDocumentoLegal/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Etnia

			<ul>
				<li><a href="${ctxadmin}/TipoEtnia/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoEtnia/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Logradouro
			<ul>
				<li><a href="${ctxadmin}/TipoLogradouro/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoLogradouro/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Necessidade Especial

			<ul>
				<li><a href="${ctxadmin}/TipoNecessidadeEspecial/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoNecessidadeEspecial/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Raça
			<ul>
				<li><a href="${ctxadmin}/TipoRaca/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoRaca/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Tipo de Veiculação de Ensino à Distância

			<ul>
				<li><a href="${ctxadmin}/TipoVeiculacaoEad/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/TipoVeiculacaoEad/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Turno
			<ul>
				<li><a href="${ctxadmin}/Turno/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/Turno/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		<ul>
			<li>Unidade Federativa
			<ul>
				<li><a href="${ctxadmin}/UnidadeFederativa/form.jsf">Cadastrar</a></li>
				<li><a href="${ctxadmin}/UnidadeFederativa/lista.jsf">Alterar/Remover</a></li>
			</ul>
			</li>
		</ul>
		</td>
		</tr>
		
</table>

</div>