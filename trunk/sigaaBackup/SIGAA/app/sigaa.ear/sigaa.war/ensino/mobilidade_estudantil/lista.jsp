<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Plano de Docência Assistida </h2>
	<a4j:keepAlive beanName="mobilidadeEstudantil" />
	<h:form>
		<table class="visualizacao" style="width: 100%;">
			<tr>
				<th style="width: 40%;">
					Nome:
				</th>
				<td>
				    ${mobilidadeEstudantil.discente.matricula} - ${mobilidadeEstudantil.discente.pessoa.nome}
				</td>		
			</tr>
			<tr>
				<th>
					Curso:
				</th>
				<td>
					<c:choose>
						<c:when test="${not empty mobilidadeEstudantil.discente.curso}">
							${mobilidadeEstudantil.discente.curso.descricao}										
						</c:when>
						<c:otherwise>
							${mobilidadeEstudantil.discente.matrizCurricular.curso.descricao} <br />				
						</c:otherwise>
					</c:choose>								
				</td>		
			</tr>
			<tr>
				<th>Departamento:</th>
				<td>
					${mobilidadeEstudantil.discente.unidade.nome}
				</td>
			</tr>	
			<tr>
				<th> Status: </th>
				<td> ${mobilidadeEstudantil.discente.statusString } </td>
			</tr>						
			<tr>
				<th>Tipo:</th>
				<td> ${mobilidadeEstudantil.discente.tipoString } </td>						
			</tr>
		</table>	
		<br/>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Mobilidade Estudantil		
			<h:graphicImage value="/img/graduacao/cancelar16.png" style="overflow: visible;"/>: Cancelar Mobilidade Estudantil
		</div>		
		
		<%@include file="historico.jsp"%>
		<table class="formulario" width="100%">
			<tfoot>
			<tr>
				<td>
					<h:commandButton id="btSelecionarDiscente" value="<< Selecionar Outro Discente" action="#{mobilidadeEstudantil.iniciarAteracao}" />				
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>