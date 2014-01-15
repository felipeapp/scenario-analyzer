<style>
	table.visualizacao tr td.subFormulario {
		padding: 3px 0 3px 20px;
	}
	p.corpo {
		padding: 2px 8px 10px;
		line-height: 1.2em;
	}
</style>
	<tr>
		<th width="25%"> Código: </th>
		<td> ${projeto.codigo.prefixo != null ? projeto.codigo : "<i> A ser gerado após a confirmação </i>"} </td>
	</tr>
	<tr>
		<th> Título: </th>
		<td> ${projeto.titulo} </td>
	</tr>
	<tr>
		<th> Tipo: </th>
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
		<th> Categoria: </th>
		<td> ${projeto.categoria.denominacao} </td>
	</tr>
	<tr>
		<th> Situação: </th>
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
	<c:if test="${ projeto.projeto.idArquivo != null && projeto.projeto.idArquivo != 0 }">
		<tr>
			<th> Arquivo do Projeto: </th>
			<td>
			<a href="${ctx}/verProducao?idArquivo=${projetoPesquisaForm.obj.projeto.idArquivo}&key=${ sf:generateArquivoKey(projetoPesquisaForm.obj.projeto.idArquivo) }" target="_blank">
				Visualizar arquivo
			</a>
			</td>
		</tr>
		<c:set var="novo" value="Novo "/>
	</c:if>

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
				e o retorno para os cursos e para os professores da instituição em geral)
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
