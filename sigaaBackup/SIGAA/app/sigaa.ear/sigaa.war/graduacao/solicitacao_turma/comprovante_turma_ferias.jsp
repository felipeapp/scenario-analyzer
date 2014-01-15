<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>

	<div class="naoImprimir">
	<table>
		<tr>
			<td width="8%"><html:img page="/img/warning.gif"/></td>
			<td  valign="middle" style="text-align: justify">
				Por favor imprima o comprovante clicando na ícone da impressora no fim da página
				ou anote o número da sua solicitação para maior segurança dessa operação.	
			</td>
		</tr>
	</table>
	<br />
	</div>

	<h:outputText value="#{confirmacaoMatriculaFeriasBean.create}"/>

	<h2> Comprovante de Matrícula em Turma de Férias <br>
	Número da Solicitação: <big> ${confirmacaoMatriculaFeriasBean.obj.numeroConfirmacao} </big> </h2>

	<c:set var="discente" value="#{confirmacaoMatriculaFeriasBean.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	
	<table class="listagem">
		<caption>Turma de Férias Solicitada</caption>
		<thead>
		<tr>
			<th> Componente </th>
			<th> Turma</th>
			<th> Horário</th>
		</tr>
		</thead>
		<tbody>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style=" font-weight: bold">
				<td> ${confirmacaoMatriculaFeriasBean.obj.turma.disciplina.codigoNome}</td>
				<td align="center">${confirmacaoMatriculaFeriasBean.obj.turma.codigo}</td>
				<td align="center">${confirmacaoMatriculaFeriasBean.obj.turma.descricaoHorario}</td>
			</tr>
		</tbody>
	</table>

	</div> <%-- Fim do div relatorio  --%>
	<div>
		<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
	</div>
</f:view>