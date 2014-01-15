<!-- histórico do projeto -->
<c:if test="${ not empty projeto.projeto.historicoSituacao }">
		<tr> <td colspan="2" style="margin:0; padding: 0;">
	    <table class="subFormulario" width="100%">
		<caption>Histórico do Projeto</caption>
	        <thead>
	        	<tr>
				    <th style="text-align: left"> Data </th>
				    <th style="text-align: center"> Situação </th>
				    <th style="text-align: left"> Usuário </th>
		       </tr>
	        </thead>
	        <tbody>

	        <c:forEach items="${projeto.projeto.historicoSituacao}" var="historico" varStatus="status">
	            <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td width="20%">
						<ufrn:format type="dataHora" name="historico" property="data"/>
					</td>
					<td style="text-align: center">${ historico.projeto.situacaoProjeto.descricao }</td>
					<td>${ historico.registroEntrada.usuario.pessoa.nome }  <i>(${ historico.registroEntrada.usuario.login })</i></td>
	            </tr>
	        </c:forEach>
	    </table>
	    </td></tr>
</c:if>
	<!-- fim do histórico do projeto -->