<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

	<style>
	table.formulario th { font-weight: bold; !important}
	table.formulario td { text-align: left; }
	</style>
	
	<f:view>
	<a4j:keepAlive beanName="notificacaoAcademica" />
	<a4j:keepAlive beanName="notificacaoAcademicaDiscente" />
		<h:form id="form">
		<h2 class="title">Administração > Destinatários da Notificação</h2>
		<h3 class="tituloTabela">Lista de Destinatários (${fn:length(notificacaoAcademica.obj.discentes) })</h3>
		<a4j:outputPanel id="discentes" style="margin-top:10px;" >
			<rich:dataTable  rowClasses="linhaPar,linhaImpar" var="d" value="#{ notificacaoAcademica.obj.discentes }" style="width:100%;">								

				<rich:column>
					<f:facet name="header"><f:verbatim><p align="left">
					<h:outputText value="Matrícula" />
					</p></f:verbatim></f:facet>
					<h:outputText value="#{ d.matricula }" />
				</rich:column>
				<rich:column>
					<f:facet name="header"><f:verbatim><p align="left">
					<h:outputText value="Discentes" />
					</p></f:verbatim></f:facet>
					<h:outputText value="#{ d.nome }" />
				</rich:column>
				<rich:column>
					<f:facet name="header"><f:verbatim><p align="left">
					<h:outputText value="Curso / Município" />
					</p></f:verbatim></f:facet>
					<h:outputText value="#{ d.curso.nome } / #{ d.curso.municipio.nome }"  />
				</rich:column>	
			</rich:dataTable>	
		</a4j:outputPanel>
		<br/>
		<div align="center">
			<h:commandButton value="<< Voltar" action="#{notificacaoAcademica.voltarDestinatarios}" />
		</div>				
	</h:form>
	
	</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
