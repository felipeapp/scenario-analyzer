<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<a4j:keepAlive beanName="notificacaoAcademica" />
	<a4j:keepAlive beanName="notificacaoAcademicaDiscente" />

	<h:form>
	<h2 class="title"><ufrn:subSistema /> 
	 > <h:commandLink value="Acompanhar Notifica��es Acad�micas" action="#{notificacaoAcademica.voltarAcompanhar}" />
	 > <h:commandLink value="Visualizar Discentes Notificados" action="#{notificacaoAcademicaDiscente.voltarListaDiscentes}"/>
	 > Informa��es do Discente
	</h2>
	
		<div class="descricaoOperacao">
		<b>Caro usu�rio,</b> 
		<br/><br/>
		Esta opera��o permite visualizar as informa��es do aluno, assim como a quantidade de vezes em que ele acessou o aviso da notifica��o acad�mica.
		</div>
	
	<table class="visualizacao" style="width: 90%">
		<tr>
			<td width="8%"></td>
			<th width="25%" style="text-align: right;">Matr�cula:</th>
			<td style="text-align: left;">${notificacaoAcademicaDiscente.obj.discente.matricula }</td>
		</tr>
		<tr>
			<td></td>
			<th style="text-align: right;"> Discente: </th>
			<td style="text-align: left;"> ${notificacaoAcademicaDiscente.obj.discente.nome } </td>
		</tr>
		<tr>
			<td></td>
			<th style="text-align: right;"> Curso: </th>
			<td style="text-align: left;"> 	<h:outputText value="#{ notificacaoAcademicaDiscente.obj.discente.curso.nome } / #{ notificacaoAcademicaDiscente.obj.discente.curso.municipio.nome }"  /> </td>
		</tr>
		<tr>
			<td></td>
			<th style="text-align: right;"> Status: </th>
			<td style="text-align: left;"> ${notificacaoAcademicaDiscente.obj.discente.statusString } </td>
		</tr>
		<tr>
			<td></td>
			<th style="text-align: right;"> Data de Confirma��o: </th>
			<c:if test="${!notificacaoAcademicaDiscente.obj.pendente}">
				<td style="text-align: left;color: red;font-weight:bold"> 
					<h:outputText id="data" value="#{notificacaoAcademicaDiscente.obj.dataConfirmacao}">
						<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
					</h:outputText>	
				</td>
			</c:if>
			<c:if test="${notificacaoAcademicaDiscente.obj.pendente}">
				<td style="text-align: left;color: red;font-weight:bold"> <h:outputText value="Ainda n�o foi confirmada"/> </td>
			</c:if>
		</tr>
		<tr>
			<td colspan="3" style="text-align: center"> 
				<h:commandLink action="#{ notificacaoAcademicaDiscente.historico}" value="Visualizar Hist�rico do Aluno">
					<f:param name="idDiscente" value="#{notificacaoAcademicaDiscente.obj.discente.id}" />
				</h:commandLink>
			</td>
		</tr>
	
	</table>
	<br/>
		<c:if test="${empty notificacaoAcademicaDiscente.registroNotificacoes && notificacaoAcademicaDiscente.obj.pendente}">
			<div align="center" style="font-weight:bold;color:red">O discente ainda n�o visualizou esta notifica��o</div>
		</c:if>
		<c:if test="${empty notificacaoAcademicaDiscente.registroNotificacoes && !notificacaoAcademicaDiscente.obj.pendente}">
			<div align="center" style="font-weight:bold;color:red">A notifica��o foi confirmada antes do SIGAA fazer o registro das visualiza��es </div>
		</c:if>
		<c:if test="${not empty notificacaoAcademicaDiscente.registroNotificacoes}">	
			<div align="center">
			<rich:dataTable value="#{notificacaoAcademicaDiscente.registroNotificacoes}" var="r" style="width:40%;" rowKeyVar="row">
					<f:facet name="header">
						<rich:columnGroup style="text-align:center;">
							<rich:column style="background-color:#485688;color:white;font-size:125%">
								<h:outputText value="Datas em que as Notifica��es foram Visualizadas" />
							</rich:column>
							<rich:column breakBefore="true" style="background-color:#ECF4FE">
								<h:outputText value="Datas de Acesso" />
							</rich:column>
						</rich:columnGroup>
					</f:facet>

				<rich:column style="text-align:center;">
					<h:outputText id="data" value="#{r.dataVisualizacao}">
						<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
					</h:outputText>	
				</rich:column>
			</rich:dataTable>
			</div>
		</c:if>
		<br/>
		<div align="center" style=";margin-top:5px;">
			<h:commandButton value="<< Voltar" action="#{notificacaoAcademicaDiscente.voltarListaDiscentes}"/>
			<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{notificacaoAcademicaDiscente.cancelar}"/>
		</div>
			
	</h:form>
	
		
<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
