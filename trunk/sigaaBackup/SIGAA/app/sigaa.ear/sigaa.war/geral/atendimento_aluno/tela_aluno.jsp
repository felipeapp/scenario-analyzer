<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<f:view>
	${atendimentoAluno.create}
	${atendimentoAluno.carregarPergunta}
	
	<c:if test="${not atendimentoAluno.pergunta.respondido}">
		<i><center><strong>Esta pergunta ainda não foi respondida.</strong></center></i>
	</c:if>
	
	<c:if test="${atendimentoAluno.pergunta.respondido}">
		<table class="visualizacao" width="80%">
			<tr>
				<td colspan="2"><h2>Resposta</h2></td>
			</tr>
			<tr>
				<th>Atendente:</th>
				<td>${ atendimentoAluno.pergunta.atendente.nome }</td>
			</tr>
			<tr>
				<th>Titulo:</th>
				<td>${ atendimentoAluno.pergunta.titulo }</td>
			</tr>	
			<tr>	
				<th>Pergunta:</th>
				<td>${ atendimentoAluno.pergunta.pergunta }</td>
			</tr>
			<tr>
				<th>Data/Hora:</th>
				<td>  <fmt:formatDate pattern="dd/MM/yyyy hh:mm" value="${atendimentoAluno.pergunta.dataAtendimento}" /></td>
			</tr>														
		</table>
		
		<br/>
			
			<table class="formulario" width="90%">
				<tr>
					<td>
						<tbody>
							<tr>
								<td>${atendimentoAluno.pergunta.resposta}</td>
							</tr>
						</tbody>
						<tfoot>
							<tr>
								<td>
								</td>
							</tr>
						</tfoot>					
					</td>
				</tr>
			</table>
	</c:if>
	
</f:view>
