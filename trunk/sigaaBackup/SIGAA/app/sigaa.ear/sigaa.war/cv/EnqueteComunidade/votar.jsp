<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>

	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	
	<div class="secaoComunidade">

	${enqueteComunidadeMBean.popularEnquete}
	<c:if test="ajaxRequest">
	</c:if>
	
	<rich:panel header="#{enqueteComunidadeMBean.object.pergunta}">
	<h:form>
		<table class="formulario" width="80%">
		<caption>Votar</caption>

			<c:set var="enqueteAtual" value="#{ enqueteComunidadeMBean.object }" />
			<c:set var="respostaUsuarioEnquete" value="${enqueteComunidadeMBean.respostaUsuarioEnquete}" />
			
			<c:if test="${ enqueteAtual != null }">
				
				<c:if test="${ respostaUsuarioEnquete == null }">

					<c:forEach var="resposta" items="${ enqueteAtual.respostas }">
						<tr>
						<td><input type="radio" name="idEnqueteResposta" id="idEnqueteResposta" value="${resposta.id}" class="noborder" />
						${ resposta.resposta }</td>
						</tr>
					</c:forEach>

				</c:if>
				
				<c:if test="${ respostaUsuarioEnquete != null}">
					<center><b>Você já votou nessa enquete!</b></center>
					<c:forEach var="item" items="${ enqueteComunidadeMBean.estatisticaDeVotos }">
						<tr>
						<td ${ item.id == respostaUsuarioEnquete.id ? 'class="votado"' : '' }>
						${item.resposta} - <fmt:formatNumber pattern="#" value="${item.porcentagemVotos}" />%
						(${item.totalVotos } Voto${item.totalVotos == 1 ? "" : "s" })
						</tr>
					</c:forEach>
				</c:if>
			</c:if>		
			
			<tfoot>
				<tr> 
					<td colspan="2"> 
						<h:commandButton value="Votar" action="#{ enqueteComunidadeMBean.votar }"
							rendered="#{ enqueteComunidadeMBean.respostaUsuarioEnquete == null }" />
						
						&nbsp; <h:commandButton value="Visualizar Votos" action="#{ enqueteComunidadeMBean.mostrar }" 
							rendered="#{ turmaVirtual.docente }" /> &nbsp;
						
						<h:commandButton value="Cancelar" action="#{ enqueteComunidadeMBean.listar }" immediate="true" />
		
						<input type="hidden" name="id" value="${ enqueteAtual.id }" /> 
						<input type="hidden" name="voltarListagem" value="true" />
					</td>
				</tr>
			</tfoot>
						
		</table>
	</h:form>
		</rich:panel>
	</div>
</f:view>

<%@include file="/cv/include/rodape.jsp" %>