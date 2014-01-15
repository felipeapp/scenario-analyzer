<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	.colAssinatura{
		width: 250px;
	}
	.colSiape{
		text-align: right !important;
	}
</</style>
<f:view>
		<h2 class="title"> ${consultaAtividadeAP.operacao.nome}</h2>
		<a4j:keepAlive beanName="geracaoListaPresencaAP"></a4j:keepAlive>	
		<div id="parametrosRelatorio">
			<table >
				<tr>
					<th>Atividade:</th>
					<td>
						<h:outputText value="#{geracaoListaPresencaAP.obj.nome}" ></h:outputText>
						<h:outputText value="Não Informado" rendered="#{empty geracaoListaPresencaAP.obj.nome}"/>	
					</td>
				</tr>
				
				<tr>
					<th>Período:</th>
					<td>
						<h:outputText value="#{geracaoListaPresencaAP.obj.descricaoPeriodo}" ></h:outputText>
						<h:outputText value="Não Informado" rendered="#{empty geracaoListaPresencaAP.obj.descricaoPeriodo}"/>	
					</td>
				</tr>
				
				<tr>
					<th>Horário:</th>
					<td>
						<h:outputText value="#{geracaoListaPresencaAP.obj.horarioInicio} a #{geracaoListaPresencaAP.obj.horarioFim}" rendered="#{not empty geracaoListaPresencaAP.obj.horarioInicio}"/>
						<h:outputText value="Não Informado" rendered="#{empty geracaoListaPresencaAP.obj.horarioInicio}"/>	
					</td>
				</tr>
				
				<tr>
					<th>Carga Horária:</th>
					<td>
						<h:outputText value="#{geracaoListaPresencaAP.obj.ch}h" rendered="#{not empty geracaoListaPresencaAP.obj.ch}"/>
						<h:outputText value="Não Informada" rendered="#{empty geracaoListaPresencaAP.obj.ch}"/>	
					</td>
				</tr>
				
				<tr>
					<th>Nº. de Vagas:</th>
					<td>
						<h:outputText value="#{geracaoListaPresencaAP.obj.numVagas}" rendered="#{not empty geracaoListaPresencaAP.obj.numVagas}"/>
						<h:outputText value="Não Informado" rendered="#{empty geracaoListaPresencaAP.obj.numVagas}"/>	
					</td>
				</tr>
			</table>
		</div>
		<br/>
		<br/>
		
		<t:dataTable value="#{geracaoListaPresencaAP.participantes}" rendered="#{not empty geracaoListaPresencaAP.participantes}"
			 var="_reg" styleClass="tabelaRelatorioBorda"  width="100%" headerClass="colSiape,colDocente,colAssinatura" 
			 columnClasses="colSiape,colDocente,colAssinatura">
					<t:column styleClass="colSiape" headerstyleClass="colSiape">
						<f:facet name="header">
							<f:verbatim>Siape</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.docente.siape}"/>
					</t:column>

					<t:column styleClass="colDocente" headerstyleClass="colDocente">
						<f:facet name="header">
							<f:verbatim>Docente</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.docente.nome}"/>
					</t:column>

					<t:column styleClass="colAssinatura" headerstyleClass="colAssinatura">
						<f:facet name="header">
							<f:verbatim>Assinatura</f:verbatim>
						</f:facet>
						<h:outputText value=" "/>
					</t:column>		

		</t:dataTable>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
