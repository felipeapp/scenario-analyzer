<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="confirmacaoMatricula" 
	value="return confirm('Deseja realmente matricular-se nessa turma? Esta operação não poderá ser desfeita.');"
	scope="request"/>
<f:view>

	<h2>
		<ufrn:subSistema /> &gt; Matrícula ${matriculaExtraordinaria.ferias ?'em Turma de Férias':''} Extraordinária &gt; Confirmação
	</h2>
	
	<h:form>
	
		<c:if test="${matriculaExtraordinaria.solicitacaoConfirmada}">
			<table  class="subFormulario" align="center">
				<tr>
				<td width="8%" valign="middle" align="center">
					<html:img page="/img/warning.gif"/>
				</td>
				<td valign="middle" style="text-align: justify">
					Por favor imprima o comprovante clicando no ícone ao lado
					para maior segurança dessa operação.
				</td>
				<td>
				<table>
					<tr>
						<td align="center">
							<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{matriculaExtraordinaria.exibirAtestadoMatricula}" id="printComprovante" >
					 			<h:graphicImage url="/img/printer_ok.png" />
					 		</h:commandLink>
					 	</td>
					 </tr>
					 <tr>
					 	<td style="font-size: medium;">
					 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{matriculaExtraordinaria.exibirAtestadoMatricula}"  id="imprimirComprovante"/>
					 	</td>
					 </tr>
				</table>
				</td>
				</tr>
			</table>
		</c:if>	
	<br/>
	<table class="visualizacao" style="width: 100%">
	<tr>
		<th width="25%"> Discente: </th>
		<td> ${matriculaExtraordinaria.discente.matriculaNome} </td>
	</tr>
	<c:if test="${matriculaExtraordinaria.discente.graduacao}">
	<tr>
		<th> Matriz Curricular: </th>
		<td> ${matriculaExtraordinaria.discente.matrizCurricular.descricao} </td>
	</tr>
	</c:if>
	<tr>
		<th> Currículo: </th>
		<td> ${matriculaExtraordinaria.discente.curriculo.codigo} </td>
	</tr>
	</table>
	
	<c:if test="${not empty matriculaExtraordinaria.turmas}">
	<br>
	<table class="listagem" style="width: 100%">
		<caption>Turmas</caption>
		<thead>
			<tr>
			<td>Componente Curricular</td>
			<td width="2%">Turma</td>
			<td width="10%">Local</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{matriculaExtraordinaria.turmas}" var="turma" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td>${turma.disciplina.descricao}</td>
				<td width="12%">Turma ${turma.codigo}</td>
				<td width="12%">${turma.local}</td>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td colspan="3">
					Docente(s): ${turma.docentesNomes }
				</td>
			</tr>
			</c:forEach>
			<c:if test="${!matriculaExtraordinaria.solicitacaoConfirmada}">
			<tr>
				<td colspan="3">
					<div align="center">
						<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
					</div>
				</td>	
			</tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3" align="center">
					<c:if test="${!matriculaExtraordinaria.solicitacaoConfirmada}">
						<h:commandButton id="btnConfirmar" value="Confirmar Matrícula" action="#{matriculaExtraordinaria.confirmar}" onclick="#{confirmacaoMatricula}"/>
					</c:if>
					<c:choose>
					<c:when test="${matriculaExtraordinaria.ferias}">
						<h:commandButton id="btnRealizarNovaMatriculaFerias" value="Realizar Outra Matrícula Extraordinária" action="#{matriculaExtraordinaria.iniciarFerias}"/>
					</c:when>
					<c:otherwise>
						<h:commandButton id="btnRealizarNovaMatricula" value="Realizar Outra Matrícula Extraordinária" action="#{matriculaExtraordinaria.iniciar}"/>
					</c:otherwise>	
					</c:choose>
				</td>
			</tr>
		</tfoot>
	</table>
	</c:if>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>