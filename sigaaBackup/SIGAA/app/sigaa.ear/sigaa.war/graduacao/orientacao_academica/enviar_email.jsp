<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Enviar Mensagem ao Orientando</h2>

<table class="visualizacao">
	<caption>Dados das Orientações do Discente</caption>
	<c:set value="0" var="matriculaAnterior" />
	<tbody>
		<c:forEach items="#{orientacaoAcademica.envio}" var="linha">
			<c:set var="matriculaAtual" value="${linha.matricula}"/>
			<c:if test="${matriculaAnterior != matriculaAtual}">
				<c:set value="${matriculaAtual}" var="matriculaAnterior" />
				<tr>
					<td class="subFormulario" colspan="6">
						Discente: ${linha.matricula} - ${linha.nome} 
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Orientador: </th>
				<td>${linha.orientador}</td>
				<th>Inicio: </th>
				<td><ufrn:format valor="${linha.inicio}" type="data" /></td>
				<th>Fim: </th>
				<td>
					<ufrn:format valor="${linha.fim}" type="data" />
					<h:outputText value="Não finalizada" rendered="#{empty linha.fim}"/>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<br />
<h:form id="form">
	<table class="formulario" width="100%">
		<caption>Informe o conteúdo da mensagem</caption>
				<tr>
					<td class="subFormulario" colspan="2">Conteúdo da Mensagem <h:graphicImage  url="/img/required.gif"/></td>			
				</tr>
				<tr>			
					<td  colspan="2">
						<center>
							<h:inputTextarea id="mensagem" style="width: 90%" rows="9" value="#{orientacaoAcademica.notificacao.mensagem}" />
						</center>
					</td>
				</tr>
			</tbody>
		<tfoot>
			<tr>
				<td  colspan="2">
					<h:commandButton title="Enviar Mensagem"  value="Enviar Mensagem" action="#{orientacaoAcademica.enviarMensagem}" id="envMensagens" />
					<h:commandButton value="<< Escolher Outro Discente" action="#{ orientacaoAcademica.listar }" id="btnescolherOutrosDiscentes"/>
					<h:commandButton value="Cancelar" action="#{ orientacaoAcademica.cancelar }" onclick="#{confirm}" id="cancelarOperacao"/>
				</td>
			</tr>
		</tfoot>	
	</table>
	<br/>
	<center>
	  <h:graphicImage  url="/img/required.gif"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>