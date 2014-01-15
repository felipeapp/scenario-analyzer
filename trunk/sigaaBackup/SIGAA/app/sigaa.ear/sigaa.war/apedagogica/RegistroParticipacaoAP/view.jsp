<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<a4j:keepAlive beanName="registroParticipacaoAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > Visualização de Participação em Atividades</h2>

	<h:form id="viewRegistroParticipacaoAP">

		<table class="visualizacao" width="90%">
			<caption class="visualizacao">Dados do Registro</caption>

			<tr>
				<th width="50%">Título:</th>
				<td>
					<h:outputText escape="true" id="tituloAtividade" value="#{registroParticipacaoAP.obj.titulo}"></h:outputText>
				</td>
			</tr>
			
			<tr>
				<th>Carga Horária:</th>
				<td>
					<h:outputText id="cargaHoraria" value="#{registroParticipacaoAP.obj.cargaHoraria}"></h:outputText>
				</td>
			</tr>
			
			<tr>
				<th>Período:</th>
				<td>
					<h:outputText value="#{registroParticipacaoAP.obj.dataInicio}" ></h:outputText>	
					a
					<h:outputText value="#{registroParticipacaoAP.obj.dataFim}" ></h:outputText>
				</td>
			</tr>
			
			<tr>
				<th width="20%">Descrição:</th>
				<td>
					<h:outputText escape="true" id="descricaoAtividade" value="#{registroParticipacaoAP.obj.descricaoAtividade}"></h:outputText>
				</td>
			</tr>

		</table>
		<br/>
		<center>
			<h:commandLink value="<< Voltar" action="#{registroParticipacaoAP.listar}" 
			  immediate="true" id="btnvoltar">
			</h:commandLink>	  
		</center>					  
		
	</h:form>


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
