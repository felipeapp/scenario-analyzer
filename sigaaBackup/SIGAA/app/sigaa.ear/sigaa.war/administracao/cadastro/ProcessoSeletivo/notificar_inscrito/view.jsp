<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="notificarInscritos"></a4j:keepAlive>
	<h:form id="formVisualizarDetalhesNotificacao">

		<h2>
			<ufrn:subSistema /> > 
			<h:commandLink action="#{processoSeletivo.listar}" value="Processos Seletivos"/> > 
			Notificar Inscritos > Visualizar Detalhes
		</h2>
	
		<table class="visualizacao" style="width: 95%;">
			<caption>Dados da Notificação</caption>
			<tbody>
			<tr>
				<th width="18%">Data:</th>
				<td>
					<h:outputText value="#{notificarInscritos.obj.data}"/>
				</td>
			</tr>
			
			<tr>
				<th>Assunto:</th>
				<td><h:outputText value="#{notificarInscritos.obj.titulo}"/></td>				
			</tr>

			<tr>
				<th>Status das Inscrições:</th>
				<td><h:outputText value="#{notificarInscritos.obj.statusInscricao}"/></td>
			</tr>
			
			<tr>
				<th>Mensagem:</th>
				<td><h:outputText escape="false" value="#{notificarInscritos.obj.mensagem}"/></td>				
			</tr>
		</tbody>
		</table>
	</h:form>

<br />
<center>
	<a href="javascript: history.go(-1);"> << Voltar </a>
</center>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>