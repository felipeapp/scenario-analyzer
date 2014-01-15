<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.CategoriaMembro"%>

<h2> Projeto de Pesquisa </h2>

<style>
	table.visualizacao tr td.subFormulario {
		padding: 3px 0 3px 20px;
	}
	p.corpo {
		padding: 2px 8px 10px;
		line-height: 1.2em;
	}
</style>


<c:set var="projeto" value="${ projetoPesquisaForm.obj }" />
<table class="tabelaRelatorioBorda" align="center" style="width: 100%">
<tr><td colspan="2" class="subFormulario" width="45%">Dados do Projeto de Pesquisa</td></tr>
	<tr>
		<th width="25%"> Código: </th>
		<td> ${projeto.codigo.prefixo != null ? projeto.codigo : "<i> A ser gerado após a confirmação </i>"} </td>
	</tr>
	<tr>
		<th> Titulo do Projeto: </th>
		<td> ${projeto.titulo} </td>
	</tr>
	<tr>
		<th> Tipo do Projeto: </th>
		<td> ${ projeto.interno ? "INTERNO" : "EXTERNO" }
			<c:choose>
				<c:when test="${projeto.numeroRenovacoes > 0}">
					(${projeto.numeroRenovacoes}ª Renovação)
				</c:when>
				<c:otherwise>
					(Projeto Novo)
				</c:otherwise>
			</c:choose>
		 </td>
	</tr>
	<tr>
		<th> Categoria do Projeto: </th>
		<td> ${projeto.categoria.denominacao} </td>
	</tr>
	<tr>
		<th> Situação do Projeto: </th>
		<td> ${projeto.situacaoProjeto.descricao} </td>
	</tr>
	<tr>
		<th> Unidade: </th>
		<td> ${ projeto.unidade } </td>
	<tr>
	<tr>
		<th> Centro: </th>
		<td> ${ projeto.centro } </td>
	<tr>
	<tr>
		<th> Palavra-Chave:	</th>
		<td> ${projeto.palavrasChave} </td>
	</tr>

	<tr>
		<th> E-mail:	</th>
		<td> ${projeto.email} </td>
	</tr>

	<c:choose>
		<c:when test="${projeto.interno}">
			<tr>
				<th> Edital:	</th>
				<td> ${projeto.edital.descricao} </td>
			</tr>
			<tr>
				<th> Cota:	</th>
				<td> ${projeto.edital.cota} </td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
				<th> Período do Projeto: </th>
				<td>
					<fmt:formatDate value="${projeto.dataInicio}"pattern="dd/MM/yyyy" />
					a <fmt:formatDate value="${projeto.dataFim}"pattern="dd/MM/yyyy" />
				</td>
			</tr>
		</c:otherwise>
	</c:choose>

	<tr><td colspan="2" class="subFormulario"> Área de Conhecimento, Grupo e Linha de Pesquisa</td></tr>

	<tr>
		<th> Área de Conhecimento: </th>
		<td> ${projeto.areaConhecimentoCnpq.nome} </td>
	</tr>

	<tr>
		<th> Grupo de Pesquisa: </th>
		<td> ${projeto.linhaPesquisa.grupoPesquisa.nomeCompleto} </td>
	</tr>

	<tr>
		<th> Linha de Pesquisa: </th>
		<td> ${projeto.linhaPesquisa.nome} </td>
	</tr>

	<c:if test="${ not projeto.interno and not empty projeto.definicaoPropriedadeIntelectual}">
		<tr><td colspan="2" class="subFormulario"> Definição da Propriedade Intelectual</td></tr>
		<tr>
			<td colspan="2" style="padding: 5px 20px;">
			<ufrn:format type="texto" name="projeto" property="definicaoPropriedadeIntelectual"/>
			</td>
		</tr>
	</c:if>

	<!-- DADOS DO PROJETO -->
	<tr><td colspan="2" class="subFormulario"> Corpo do Projeto </td>

	<tr><th colspan="2" style="text-align: left;"> <b>Resumo</b> </th></tr>
	<tr>
		<td colspan="2"> <p class="corpo"> <ufrn:format type="texto" name="projeto" property="descricao"/> </p> </td>
	</tr>
	<tr> 
		<th colspan="2" style="text-align: left;">  
			<b>Introdução/Justificativa</b> <br /> 
			<small>
				(incluindo os benefícios esperados no processo ensino-aprendizagem 
				e o retorno para os cursos e para os professores da ${ configSistema['siglaInstituicao'] } em geral)
			</small>  
		</th> 
	</tr>
	<tr>
		<td colspan="2"> <p class="corpo"> <ufrn:format type="texto" name="projeto" property="justificativa"/> </p> </td>
	</tr>
	<tr> <th colspan="2" style="text-align: left;">  <b>Objetivos</b> </th> </tr>
	<tr>
		<td colspan="2"> <p class="corpo"> <ufrn:format type="texto" name="projeto" property="objetivos"/> </p> </td>
	</tr>
	
	<tr> <th colspan="2" style="text-align: left;">  <b>Metodologia</b> </th> </tr>
	<tr>
		<td colspan="2"> <p class="corpo"> <ufrn:format type="texto" name="projeto" property="metodologia"/> </p> </td>
	</tr>

	<tr><th colspan="2" style="text-align: left;">  <b>Referências</b> </th></tr>
	<tr>
		<td colspan="2"> <p class="corpo"> <ufrn:format type="texto" name="projeto" property="bibliografia"/> </p> </td>
	</tr>
	

	<!-- Lista de financiamentos do projeto -->
	<c:if test="${not empty projeto.financiamentosProjetoPesq}">
		<tr> <td colspan="2" style="margin:0; padding: 0;">
		    <table class="subFormulario" width="100%">
			<caption class="listagem">Financiamentos</caption>
		        <thead>
		        	<tr>
			        <td>Entidade Financiadora</td>
			        <td>Natureza do Financiamento</td>
			        <td>Data Inicio</td>
			        <td>Data Fim</td>
			        </tr>
		        </thead>
		        <tbody>

		        <c:forEach items="${projeto.financiamentosProjetoPesq}" var="financiamento" varStatus="status">
		            <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		                    <html:hidden property="financiamento.id" value="${financiamento.id}" />
		                    <td>${financiamento.entidadeFinanciadora.nome}</td>
		                    <td>${financiamento.tipoNaturezaFinanciamento.descricao}</td>
		                    <td><fmt:formatDate value="${financiamento.dataInicio}"pattern="dd/MM/yyyy" /></td>
		                    <td><fmt:formatDate value="${financiamento.dataFim}"pattern="dd/MM/yyyy" /></td>
		            </tr>
		        </c:forEach>
		    </table>
	    </td></tr>
	</c:if>

	<!-- lista de membros do projeto -->
		<tr> <td colspan="2" style="margin:0; padding: 0;">
	    <table class="subFormulario" width="100%">
		<caption>Membros do Projeto</caption>
	        <thead>
	        	<tr>
	        		<c:if test="${acesso.pesquisa}">
				    	<td> CPF </td>
				    </c:if>
				    <td> Nome </td>
				    <td> Categoria </td>
				    <td style="text-align: right;"> CH Dedicada </td>
				    <td> Tipo de Participação </td>
		       </tr>
	        </thead>
	        <tbody>

	        <c:forEach items="${projeto.membrosProjeto}" var="membro" varStatus="status">
	            <c:if test="${ membro.categoriaMembro.id != CategoriaMembro.DISCENTE || membro.discente == null || membro.discente.nivel != 'G' }">
		            <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		            	<c:if test="${acesso.pesquisa}">
							<td>
								<ufrn:format type="cpf_cnpj" name="membro" property="pessoa.cpf_cnpj"/>
							</td>
						</c:if>
						<td>${ membro.pessoa.nome }</td>
						<td>${ membro.categoriaMembro.descricao }</td>
						<td style="text-align: right;">
							${ membro.chDedicada != null ? membro.chDedicada : "<i>Não informada</i>" }
						</td>
						<td>${membro.funcaoMembro.descricao}</td>
		            </tr>
	            </c:if>
	        </c:forEach>
	    </table>
	    </td></tr>
	<!-- fim da lista de membros do projeto -->

	<!-- visualização do cronograma -->
		<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano" varStatus="status">
			<c:set var="tamanho" value="${fn:length(projetoPesquisaForm.telaCronograma.mesesAno)}"/>
			<c:choose>
				<c:when test="${status.index == 0}">
					<c:set var="min" value="${status.index}"/>	
					<c:set var="max" value="${status.index + 1}"/>
					<%@include file="include/_view_cronograma.jsp"%>
				</c:when>
				<c:when test="${ min == tamanho || max == tamanho || tamanho == 2 }"></c:when>
				<c:otherwise>
					<c:set var="min" value="${max + 1}"/>	
					<c:set var="max" value="${min + 1}"/>
					<%@include file="include/_view_cronograma.jsp"%>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	<!-- FIM da visualização do cronograma -->

<!-- histórico do projeto -->
		<tr> <td colspan="2" style="margin:0; padding: 0;">
	    <table class="subFormulario" width="100%">
		<caption>Histórico do Projeto</caption>
	        <thead>
	        	<tr>
				    <th style="text-align: center;"> Data </th>
				    <th style="text-align: left;"> Situação </th>
				    <th style="text-align: left"> Usuário </th>
		       </tr>
	        </thead>
	        <tbody>

	        <c:forEach items="${projetoPesquisaForm.obj.historicoSituacao}" var="historico_" varStatus="status">
	            <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td width="20%" style="text-align: center;">
						<ufrn:format type="dataHora" name="historico_" property="data"/>
					</td>
					<td style="text-align: left;">${ historico_.situacaoProjeto.descricao }</td>
					<td>${ historico_.registroEntrada.usuario.pessoa.nome }  <i>(${ historico_.registroEntrada.usuario.login })</i></td>
	            </tr>
	        </c:forEach>
	    </table>
	    </td></tr>
<!-- fim do histórico do projeto -->
</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>