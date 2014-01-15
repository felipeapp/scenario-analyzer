<%@page import="br.ufrn.sigaa.projetos.dominio.CategoriaMembro"%>

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